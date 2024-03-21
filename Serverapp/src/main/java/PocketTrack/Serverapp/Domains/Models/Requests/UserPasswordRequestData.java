package PocketTrack.Serverapp.Domains.Models.Requests;

import lombok.Data;

@Data
public class UserPasswordRequestData {
    private String id;
    private String oldPassword;
    private String newPassword;
}
