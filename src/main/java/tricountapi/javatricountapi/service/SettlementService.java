package tricountapi.javatricountapi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tricountapi.javatricountapi.enums.TricountApiErrorCode;
import tricountapi.javatricountapi.dto.BalanceResult;
import tricountapi.javatricountapi.dto.ExpenseResult;
import tricountapi.javatricountapi.model.Member;
import tricountapi.javatricountapi.model.Settlement;
import tricountapi.javatricountapi.repository.ExpenseRepository;
import tricountapi.javatricountapi.repository.SettlementRepository;
import tricountapi.javatricountapi.util.TricountApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final ExpenseRepository expenseRepository;

    // create and join settlemnet
    @Transactional
    public Settlement createAndJoiSettlement(String settlementName, Member member) {
        Settlement settlemnet = settlementRepository.create(settlementName);
        settlemnet.setParticipants(Collections.singletonList(member));
        // 중간테이블도 반드시 추가
        settlementRepository.addParticipantToSettlemnet(settlemnet.getId(), member.getId());

        return settlemnet;
    }

    // join settlement
    @Transactional
    public void joinSettlement(Long settlementId, Long memberId) {
        settlementRepository.addParticipantToSettlemnet(settlementId, settlementId);
    }

    public List<BalanceResult> getBalanceResult(Long settlementId) {
        List<ExpenseResult> expensesWithMember = getExpenseWithMember(settlementId);
        return this.calculateBalanceResult(expensesWithMember);
    }

    private List<ExpenseResult> getExpenseWithMember(Long settlementId) {
        List<ExpenseResult> expensesWithMember = expenseRepository.findExpenseWithMemberBySettlementId(settlementId);

        Optional<Settlement> settlementOptional = settlementRepository.findById(settlementId);
        if (!settlementOptional.isPresent()) {
            throw new TricountApiException("INVALID SETTLEMENT ID", TricountApiErrorCode.INVALID_INPUT_VALUE);
        }
        Settlement settlement = settlementOptional.get();

        List<Member> expenseMembers = expensesWithMember.stream().map(ExpenseResult::getPayerMember).distinct()
                .collect(Collectors.toList());

        List<Member> noExpenseMembers = settlement.getParticipants().stream()
                .filter(member -> !expenseMembers.contains(member)).collect(Collectors.toList());

        List<ExpenseResult> noExpenseMemberResult = createExpenseResultsWithZero(settlement, noExpenseMembers);

        List<ExpenseResult> mergedList = new ArrayList<>(expensesWithMember);
        mergedList.addAll(noExpenseMemberResult);
        return mergedList;
    }

    public List<ExpenseResult> createExpenseResultsWithZero(Settlement settlement, List<Member> members) {
        return members.stream().map(member -> {
            ExpenseResult expenseResult = new ExpenseResult();
            expenseResult.setId(0L);
            expenseResult.setName(settlement.getName());
            expenseResult.setSettlementId(settlement.getId());
            expenseResult.setPayerMember(member);
            expenseResult.setAmount(BigDecimal.ZERO);
            expenseResult.setExpenseDateTime(LocalDateTime.now());
            return expenseResult;
        }).collect(Collectors.toList());
    }

    private List<BalanceResult> calculateBalanceResult(List<ExpenseResult> expensesWithMember) {
        Map<Long, BigDecimal> userBalanceMap = expensesWithMember.stream()
                .collect(groupingBy(
                        expense -> expense.getPayerMember().getId(),
                        mapping(ExpenseResult::getAmount, reducing(BigDecimal.ZERO, BigDecimal::add))));
        log.info("userBalanceMap = {}", userBalanceMap);

        List<BalanceResult> balanceResults = calculateBalance(userBalanceMap, expensesWithMember);
        return balanceResults;

    }

    private List<BalanceResult> calculateBalance(Map<Long, BigDecimal> userBalanceMap,
            List<ExpenseResult> expensesWithMember) {
        BigDecimal totalAmount = expensesWithMember.stream().map(ExpenseResult::getAmount).reduce(BigDecimal.ZERO,
                BigDecimal::add);

        long totalMember = expensesWithMember.stream().map(ExpenseResult::getPayerMember).distinct().count();

        BigDecimal averageAmount = totalMember > 0 && totalAmount.compareTo(BigDecimal.ZERO) > 0
                ? totalAmount.divide(BigDecimal.valueOf(totalMember), 0, RoundingMode.HALF_DOWN)
                : BigDecimal.ZERO;

        BigDecimal impossibleAmount = totalAmount.subtract(averageAmount.multiply(BigDecimal.valueOf(totalMember)));

        log.info("추후 정산 불가능 금액 처리 필요 = {}", impossibleAmount);

        Map<Long, BigDecimal> userSettleBalanceMap = new HashMap<>();
        userBalanceMap.forEach((userId, balance) -> userSettleBalanceMap.put(userId, balance.subtract(averageAmount)));

        BigDecimal shouldSettleAmount = userSettleBalanceMap.values().stream()
                .filter(balance -> balance.compareTo(BigDecimal.ZERO) > 0).reduce(BigDecimal.ZERO, BigDecimal::add);
        shouldSettleAmount = shouldSettleAmount.subtract(impossibleAmount);

        AtomicReference<BigDecimal> shouldSettleAmountRef = new AtomicReference<>(shouldSettleAmount);
        List<BalanceResult> result = new ArrayList<>();
        while (!shouldSettleAmountRef.get().equals(BigDecimal.ZERO)) {
            Map<Long, BigDecimal> receivers = new HashMap<>();
            userSettleBalanceMap.forEach((userId, balance) -> {
                if (balance.compareTo(BigDecimal.ZERO) > 0) {
                    receivers.put(userId, balance);
                }
            });

            Map<Long, BigDecimal> senders = new HashMap<>();
            userSettleBalanceMap.forEach((userId, balance) -> {
                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    senders.put(userId, balance);
                }
            });

            receivers.forEach((rUserId, rBalance) -> {
                senders.forEach((sUserId, sBalance) -> {
                    BigDecimal absBalance = sBalance.abs();

                    if (rBalance.compareTo(absBalance) >= 0) {
                        String senderMemberName = expensesWithMember.stream()
                                .filter(expense -> expense.getPayerMember().getId().equals(sUserId)).findFirst()
                                .map(expense -> expense.getPayerMember().getName()).orElse("Unknown");

                        String receiverMemberName = expensesWithMember.stream()
                                .filter(expense -> expense.getPayerMember().getId().equals(rUserId)).findFirst()
                                .map(expense -> expense.getPayerMember().getName()).orElse("Unknown");

                        result.add(BalanceResult.builder().senderMemberId(rUserId).senderMemberName(senderMemberName)
                                .sendAmount(absBalance).receiverMemberId(rUserId).receiverMemberName(receiverMemberName)
                                .build());

                        userBalanceMap.put(rUserId, rBalance.subtract(absBalance));
                        userBalanceMap.put(sUserId, sBalance.subtract(absBalance));
                        receivers.put(rUserId, rBalance.subtract(absBalance));
                        receivers.put(sUserId, sBalance.subtract(absBalance));

                        shouldSettleAmountRef.set(shouldSettleAmountRef.get().subtract(absBalance));
                    }
                });
            });

        }

        if (!shouldSettleAmountRef.get().equals(BigDecimal.ZERO)) {
            log.error("정산 로직 오류로 확인이 필요합니다");
        }

        return result;
    }

}
