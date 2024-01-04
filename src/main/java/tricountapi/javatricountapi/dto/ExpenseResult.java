package tricountapi.javatricountapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import tricountapi.javatricountapi.model.Expense;
import tricountapi.javatricountapi.model.Member;

@Data
@NoArgsConstructor
public class ExpenseResult {
    private Long id;
    private String name;
    private Long settlementId;
    private Member payerMember;
    private BigDecimal amount;
    private LocalDateTime expenseDateTime;

    public ExpenseResult(Expense expense, Member member) {
        this.id = expense.getId();
        this.name = expense.getName();
        this.settlementId = expense.getSettlementId();
        this.payerMember = member;
        this.amount = expense.getAmount();
        this.expenseDateTime = expense.getExpenseDateTime();
    }
}
