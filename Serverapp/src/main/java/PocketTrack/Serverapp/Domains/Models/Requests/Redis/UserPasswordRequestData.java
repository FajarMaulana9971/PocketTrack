package PocketTrack.Serverapp.Domains.Models.Requests.Redis;

import lombok.Data;

@Data
public class UserPasswordRequestData {
    private String id;
    private String password;
    private String newPassword;
}
