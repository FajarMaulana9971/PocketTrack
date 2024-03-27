package PocketTrack.Serverapp.Domains.Models.Requests.Redis;

import lombok.Data;

@Data
public class GenerateParticipantAccountEmailRequest {
    private String userId;
    private String name;
    private String email;
    private String username;
    private String password;
}
