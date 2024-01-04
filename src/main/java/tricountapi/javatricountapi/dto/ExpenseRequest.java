package tricountapi.javatricountapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpenseRequest {
    @NotNull
    private String name;

    @NotNull
    private Long settlementId;

    @NotNull
    private Long payerMemberId;

    @NotNull
    private BigDecimal amount;

    private LocalDateTime expenseDateTime;
}
