package PocketTrack.Serverapp.Domains.Models.Requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PasswordRequest {
    @NotEmpty(message = "Code must not be empty")
    private String verificationCode;

    @NotEmpty(message = "old password must not be empty")
    private String oldPassword;

    @NotEmpty(message = "new password must not be empty")
    private String newPassword;
}
