package tricountapi.javatricountapi.repository;

import java.util.List;

import tricountapi.javatricountapi.dto.ExpenseResult;
import tricountapi.javatricountapi.model.Expense;

public interface ExpenseRepository {
    Expense save(Expense expense);

    List<ExpenseResult> findExpenseWithMemberBySettlementId(Long settlementId);
}
