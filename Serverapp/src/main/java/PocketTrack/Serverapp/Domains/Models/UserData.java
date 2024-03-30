package PocketTrack.Serverapp.Domains.Models;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserData {
    private String id;
    private String name;
    private String email;
    private LocalDate birthDate;
    private String gender;
    private LocalDate joinDate;
}
