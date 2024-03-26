package PocketTrack.Serverapp.Domains.Entities.Redis;

import lombok.Data;

@Data
public class GenerateParticipantAccountRequest {
    private String name;
    private String email;
    private String company;
}
