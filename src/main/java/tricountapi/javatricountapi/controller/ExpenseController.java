package tricountapi.javatricountapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tricountapi.javatricountapi.dto.ExpenseRequest;
import tricountapi.javatricountapi.dto.ExpenseResult;
import tricountapi.javatricountapi.service.ExpenseService;
import tricountapi.javatricountapi.model.ApiResponse;

@RestController
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping("/expenses/add")
    public ApiResponse<ExpenseResult> addExpenseToSettlement(@Valid @RequestBody ExpenseRequest expenseRequest) {
        return new ApiResponse<ExpenseResult>().ok(expenseService.addExpense(expenseRequest));
    }
}
