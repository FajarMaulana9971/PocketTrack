package PocketTrack.Serverapp.Domains.Models.Requests;

import java.io.Serializable;

import lombok.Data;

@Data
public class EmailRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userid;
    private String name;
    private String email;
    private String subject;
    private String code;
}
