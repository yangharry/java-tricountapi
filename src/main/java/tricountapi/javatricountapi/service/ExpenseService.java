package tricountapi.javatricountapi.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tricountapi.javatricountapi.enums.TricountApiErrorCode;
import tricountapi.javatricountapi.dto.ExpenseRequest;
import tricountapi.javatricountapi.dto.ExpenseResult;
import tricountapi.javatricountapi.repository.ExpenseRepository;
import tricountapi.javatricountapi.repository.MemberRepository;
import tricountapi.javatricountapi.repository.SettlementRepository;
import tricountapi.javatricountapi.util.TricountApiException;
import tricountapi.javatricountapi.model.Settlement;
import tricountapi.javatricountapi.model.Expense;
import tricountapi.javatricountapi.model.Member;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final SettlementRepository settlementRpository;

    @Transactional
    public ExpenseResult addExpense(ExpenseRequest expenseRequest) {
        Optional<Member> payer = memberRepository.findMyId(expenseRequest.getPayerMemberId());
        if (!payer.isPresent()) {
            throw new TricountApiException("INVALID MEMBER ID! (Payer)", TricountApiErrorCode.INVALID_INPUT_VALUE);
        }

        Optional<Settlement> settlement = settlementRpository.findById(expenseRequest.getSettlementId());
        if (!settlement.isPresent()) {
            throw new TricountApiException("INVALID SETTLEMENT ID", TricountApiErrorCode.INVALID_INPUT_VALUE);
        }
        Expense expense = Expense.builder().name(expenseRequest.getName())
                .settlementId(expenseRequest.getSettlementId()).payerMemberId(expenseRequest.getPayerMemberId())
                .amount(expenseRequest.getAmount())
                .expenseDateTime(
                        Objects.nonNull(expenseRequest.getExpenseDateTime()) ? expenseRequest.getExpenseDateTime()
                                : LocalDateTime.now())
                .build();
        expenseRepository.save(expense);

        return new ExpenseResult(expense, payer.get());
    }
}
