package PocketTrack.Serverapp.Domains.Models.Responses;

import java.time.LocalDate;
import java.util.List;

import PocketTrack.Serverapp.Domains.Enums.Gender;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private LocalDate birthDate;
    private Gender gender;
    private LocalDate joinDate;
    private String phoneNumber;
    private String accountStatus;
    private List<String> roles;
}
