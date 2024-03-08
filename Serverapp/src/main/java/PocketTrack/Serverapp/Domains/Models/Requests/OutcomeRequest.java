package PocketTrack.Serverapp.Domains.Models.Requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutcomeRequest {
    @NotNull(message = "date must not be empty")
    private LocalDateTime date;

    @NotNull(message = "title must not be empty")
    private String title;

    @NotNull(message = "description must not be empty")
    private String description;

    @NotNull(message = "amount must not be empty")
    private BigDecimal amount;

    @NotNull(message = "status must not be empty")
    private Boolean status;

    @NotNull(message = "isDelete must not be empty")
    private Boolean isDeleted;

    @NotNull(message = "budget id must not be empty")
    private String budget;
}
