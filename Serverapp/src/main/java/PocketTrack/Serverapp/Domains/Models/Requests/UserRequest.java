package PocketTrack.Serverapp.Domains.Models.Requests;

import java.time.LocalDate;
import java.time.ZoneId;

import PocketTrack.Serverapp.Domains.Enums.Gender;
import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String email;
    private String numberPhone;
    private LocalDate birthDate;
    private LocalDate joinDate = LocalDate.now(ZoneId.of("Asia/Jakarta"));
    private Gender gender;
    private String username;
    private String password;
}
