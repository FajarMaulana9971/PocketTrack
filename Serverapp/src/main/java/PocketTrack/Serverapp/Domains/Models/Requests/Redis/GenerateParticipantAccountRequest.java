package PocketTrack.Serverapp.Domains.Models.Requests.Redis;

import lombok.Data;

@Data
public class GenerateParticipantAccountRequest {
    private String name;
    private String email;
    private String company;
}
