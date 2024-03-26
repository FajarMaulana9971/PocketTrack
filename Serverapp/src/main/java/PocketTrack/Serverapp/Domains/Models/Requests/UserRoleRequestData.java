package PocketTrack.Serverapp.Domains.Models.Requests;

import java.util.List;

import lombok.Data;

@Data
public class UserRoleRequestData {
    private String email;
    private List<String> roles;
}
