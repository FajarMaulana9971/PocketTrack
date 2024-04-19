package PocketTrack.Serverapp.Domains.Models.Requests;

import lombok.Data;

@Data
public class VerificationRequest {
    private String accountId;
    private String verificationCode;
    private String budgetId;
}
