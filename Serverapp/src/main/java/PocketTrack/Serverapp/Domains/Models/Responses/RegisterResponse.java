package PocketTrack.Serverapp.Domains.Models.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterResponse {
    private String accountId;
    private String email;
    private String verificationCode;
}
