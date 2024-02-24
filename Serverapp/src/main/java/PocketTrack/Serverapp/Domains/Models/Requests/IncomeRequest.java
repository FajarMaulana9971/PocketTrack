package PocketTrack.Serverapp.Domains.Models.Requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IncomeRequest {
    @NotEmpty(message = "date must not be empty")
    private LocalDateTime date;

    @NotEmpty(message = "title must not be empty")
    private String title;

    @NotEmpty(message = "description must not be empty")
    private String description;

    @NotEmpty(message = "amount must not be empty")
    private BigDecimal amount;

    @NotEmpty(message = "budget id must not be empty")
    private String budget;
}
