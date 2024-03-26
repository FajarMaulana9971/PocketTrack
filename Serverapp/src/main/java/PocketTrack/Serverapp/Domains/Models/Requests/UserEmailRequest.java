package PocketTrack.Serverapp.Domains.Models.Requests;

import lombok.Data;

@Data
public class UserEmailRequest {
    private String oldEmail;
    private String newEmail;
}
