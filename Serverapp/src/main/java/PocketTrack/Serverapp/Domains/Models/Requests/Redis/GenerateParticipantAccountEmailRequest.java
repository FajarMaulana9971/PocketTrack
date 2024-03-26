package PocketTrack.Serverapp.Domains.Entities.Redis;

import lombok.Data;

@Data
public class GenerateParticipantAccountEmailRequest {
    private String userId;
    private String name;
    private String email;
    private String username;
    private String password;
}
