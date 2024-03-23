package PocketTrack.Serverapp.Domains.Models.Responses;

import java.util.List;

import lombok.Data;

@Data
public class ValidateTokenResponse {
    private String id;
    private String name;
    private String email;
    private String accountType;
    private List<RoleResponse> roles;
}
