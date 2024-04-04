package PocketTrack.Serverapp.Domains.Models;

import java.time.LocalDate;

import PocketTrack.Serverapp.Domains.Enums.Gender;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterData {

    @NotEmpty(message = "name cannot be empty")
    private String name;

    // @NotEmpty(message = "gender cannot be empty")
    private Gender gender;

    @NotEmpty(message = "email cannot be empty")
    private String email;

    @NotEmpty(message = "password cannot be empty")
    private String password;

    @NotEmpty(message = "number phone cannot be empty")
    private String numberPhone;

    @NotEmpty(message = "birth date cannot be empty")
    private LocalDate birthDate;

}
