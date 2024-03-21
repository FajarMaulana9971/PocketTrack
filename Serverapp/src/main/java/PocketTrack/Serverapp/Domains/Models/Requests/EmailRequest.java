package PocketTrack.Serverapp.Domains.Models.Requests;

import lombok.Data;

@Data
public class EmailRequest {
    private String userid;
    private String name;
    private String email;
    private String subject;
    private String code;
}
