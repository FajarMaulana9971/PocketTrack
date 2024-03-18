package PocketTrack.Serverapp.Domains.Models.Responses;

import java.time.LocalDate;

import PocketTrack.Serverapp.Domains.Enums.Gender;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Gender gender;
    private LocalDate birthDate;
    private LocalDate joinDate;
}
