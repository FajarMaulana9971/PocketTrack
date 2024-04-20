package PocketTrack.Serverapp.Controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import PocketTrack.Serverapp.Domains.Entities.Budget;
import PocketTrack.Serverapp.Domains.Models.ErrorData;
import PocketTrack.Serverapp.Domains.Models.Requests.BudgetRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Services.Interfaces.BudgetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("budget")
public class BudgetController {
    private BudgetService budgetService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetById(@PathVariable String id) {
        return budgetService.getByIdWithResponse(id);
    }

    @GetMapping("totalBalance/{id}")
    public ResponseEntity<?> getTotalBalanceWithResponse(@PathVariable BigDecimal totalBalance) {
        return budgetService.getTotalBalanceWithResponse(totalBalance);
    }

    @GetMapping("admin/take-all")
    public ObjectResponseData<Budget> getAllBudget(@RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return budgetService.getAllBudget(keyword, page, size);
    }

    @PostMapping("admin/insert")
    public ResponseEntity<?> insertBudget(BudgetRequest budgetRequest, Errors error) {
        if (error.hasErrors()) {
            List<ErrorData> errorDataList = new ArrayList<>();
            error.getAllErrors().forEach(errorr -> errorDataList.add(new ErrorData(errorr.getDefaultMessage())));
        }
        return budgetService.insertBudget(budgetRequest);
    }

    @PatchMapping("admin/update/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable String id, @Valid @RequestBody BudgetRequest budgetRequest,
            Errors errors) {
        if (errors.hasErrors()) {
            List<ErrorData> errorDataList = new ArrayList<>();
            errors.getAllErrors().forEach(error -> errorDataList.add(new ErrorData(error.getDefaultMessage())));
        }
        return budgetService.updateBudget(id, budgetRequest);
    }

    @DeleteMapping("admin/delete/{id}")
    public ResponseEntity<?> deleteBudgetById(@PathVariable String id) {
        return budgetService.deleteWithResponse(id);
    }
}
