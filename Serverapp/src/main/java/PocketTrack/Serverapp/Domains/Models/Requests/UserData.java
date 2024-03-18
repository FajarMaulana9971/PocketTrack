package PocketTrack.Serverapp.Domains.Models.Requests;

import java.time.LocalDate;
import java.util.List;

import PocketTrack.Serverapp.Domains.Enums.Gender;
import lombok.Data;

@Data
public class UserData {
    private String id;
    private String name;
    private String email;
    private String numberPhone;
    private Gender gender;
    private LocalDate birthDate;
    private LocalDate joinDate;
    private List<String> roles;
}
