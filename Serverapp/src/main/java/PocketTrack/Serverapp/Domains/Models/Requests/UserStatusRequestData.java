package PocketTrack.Serverapp.Domains.Models.Requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserStatusRequestData {
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Status is required")
    private String status;
}
