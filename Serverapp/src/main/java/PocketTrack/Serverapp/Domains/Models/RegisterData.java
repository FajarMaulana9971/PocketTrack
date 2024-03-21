package PocketTrack.Serverapp.Domains.Models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterData {

    @NotEmpty(message = "name cannot be empty")
    private String name;

    @NotEmpty(message = "email cannot be empty")
    private String email;

    @NotEmpty(message = "password cannot be empty")
    private String password;

}
