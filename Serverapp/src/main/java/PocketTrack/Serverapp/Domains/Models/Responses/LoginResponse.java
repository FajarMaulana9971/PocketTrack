package PocketTrack.Serverapp.Domains.Models.Responses;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String expired;
    private Integer status;
}
