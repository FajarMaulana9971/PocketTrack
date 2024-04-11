package PocketTrack.Serverapp.Domains.Models.Requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import PocketTrack.Serverapp.Domains.Entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BudgetRequest {
    private LocalDateTime date;
    private BigDecimal totalBalance = BigDecimal.ZERO;
    private String title;
    private String description;
}
