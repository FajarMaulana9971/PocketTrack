package PocketTrack.Serverapp.Domains.Models.Requests;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String id;
    private String name;
    private String email;
    private String numberPhone;
    private LocalDate birthDate;
}
