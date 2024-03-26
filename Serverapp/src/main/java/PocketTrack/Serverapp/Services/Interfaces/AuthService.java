package PocketTrack.Serverapp.Services.Interfaces;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.LoginData;
import PocketTrack.Serverapp.Domains.Models.RegisterData;
import PocketTrack.Serverapp.Domains.Models.Requests.PasswordRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.LoginResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ValidateTokenResponse;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService extends BaseServices<User, String> {
    ResponseEntity<ResponseData<RegisterData>> register(RegisterData registerData);

    LoginResponse login(LoginData loginData, HttpServletResponse response);

    Map<String, Object> createPayload(User user);

    void setTokenToCookie(String type, String token, HttpServletResponse response);

    ResponseEntity<ResponseData<Boolean>> forgotPassword(String email);

    ResponseEntity<ResponseData<Boolean>> resetPassword(PasswordRequest passwordRequest);

    LoginResponse refreshToken(String accessToken);

    ResponseEntity<ResponseData<String>> verification(String verificationCode);

    String logout(String accessToken, HttpServletResponse response);

    ResponseEntity<ValidateTokenResponse> validateToken(String authorization);
}
