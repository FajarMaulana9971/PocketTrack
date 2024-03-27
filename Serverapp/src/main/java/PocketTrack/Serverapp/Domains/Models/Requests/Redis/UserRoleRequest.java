package PocketTrack.Serverapp.Domains.Models.Requests.Redis;

import java.util.List;

import lombok.Data;

@Data
public class UserRoleRequest {
    private String email;
    private List<String> roles;
}
