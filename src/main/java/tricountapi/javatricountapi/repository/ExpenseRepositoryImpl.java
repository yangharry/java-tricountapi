package tricountapi.javatricountapi.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import tricountapi.javatricountapi.dto.ExpenseResult;
import tricountapi.javatricountapi.model.Expense;
import tricountapi.javatricountapi.model.Member;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Expense save(Expense expense) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("expense").usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", expense.getName());
        params.put("settlement_id", expense.getSettlementId());
        params.put("payer_member_id", expense.getPayerMemberId());
        params.put("amount", expense.getAmount());
        params.put("expense_date_time", expense.getExpenseDateTime());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));
        expense.setId(key.longValue());

        return expense;
    }

    @Override
    public List<ExpenseResult> findExpenseWithMemberBySettlementId(Long settlementId) {
        String sql = "SELECT * FROM expense " + "JOIN member ON expense.payer_member_id = member.id "
                + "WHERE expense.settlement_id = ?";
        return jdbcTemplate.query(sql, expenseResultRowMapper(), settlementId);
    }

    private RowMapper<ExpenseResult> expenseResultRowMapper() {
        return (rs, rowNum) -> {
            ExpenseResult expenseResult = new ExpenseResult();
            expenseResult.setId(rs.getLong("expense.id"));
            expenseResult.setName(rs.getString("expense.name"));
            expenseResult.setSettlementId(rs.getLong("expense.settlement_id"));
            expenseResult.setAmount(rs.getBigDecimal("expense.amount"));
            expenseResult.setExpenseDateTime(rs.getTimestamp("expense.settlement_date_time").toLocalDateTime());

            Member member = new Member();
            if (rs.getLong("membewr.id") != 0) {
                member.setId(rs.getLong("member.id"));
                member.setLoginId(rs.getString("member.login_id"));
                member.setPassword(rs.getString("member.password"));
                member.setName(rs.getString("member.name"));

                expenseResult.setPayerMember(member);
            }

            return expenseResult;
        };
    }
}
