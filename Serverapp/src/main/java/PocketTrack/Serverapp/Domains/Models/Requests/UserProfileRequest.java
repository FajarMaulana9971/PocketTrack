package PocketTrack.Serverapp.Domains.Models.Requests;

import java.time.LocalDate;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserProfileRequest {
    @NotEmpty(message = "id cannot be empty")
    private String id;
    private String name;
    private String email;
    private String numberPhone;
    private LocalDate birthDate;
}
