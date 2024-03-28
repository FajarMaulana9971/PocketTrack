package PocketTrack.Serverapp.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import PocketTrack.Serverapp.Domains.Models.ErrorData;
import PocketTrack.Serverapp.Domains.Models.LoginData;
import PocketTrack.Serverapp.Domains.Models.RegisterData;
import PocketTrack.Serverapp.Domains.Models.Responses.LoginResponse;
import PocketTrack.Serverapp.Services.Interfaces.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    // @Operation(summary = "This endpoint is used to register a new user")
    @PostMapping("register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterData registerData, Errors errors) {
        if (errors.hasErrors()) {
            List<ErrorData> errorDataList = new ArrayList<>();
            errors.getAllErrors().forEach(error -> errorDataList.add(new ErrorData(error.getDefaultMessage())));
            return ResponseEntity.badRequest().body(errorDataList);
        }
        return authService.register(registerData);
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginData loginData, HttpServletResponse response) {
        return authService.login(loginData, response);
    }

    @PostMapping("refresh-token")
    public LoginResponse refreshToken(@CookieValue(value = "refreshToken") String accessToken) {
        return authService.refreshToken(accessToken);
    }

    @PostMapping("logging-out")
    public String logout(@CookieValue(value = "accessToken") String accessToken, HttpServletResponse response) {
        return authService.logout(accessToken, response);
    }

    @PostMapping("forgot-password/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable String email) {
        return authService.forgotPassword(email);
    }

}
