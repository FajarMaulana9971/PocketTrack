package PocketTrack.Serverapp.Utilities;

import org.springframework.stereotype.Component;

import PocketTrack.Serverapp.Domains.Models.RegisterData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserEmailRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserPasswordRequestData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserProfileRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.Redis.GenerateParticipantAccountRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.Redis.UserRoleRequest;
import PocketTrack.Serverapp.Services.Implementation.AuthServiceImpl;
import PocketTrack.Serverapp.Services.Implementation.UserServiceImpl;
import lombok.RequiredArgsConstructor;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class AuthReceiver {

    private final AuthServiceImpl authService;
    private final UserServiceImpl userService;

    public void handleMessage(RegisterData data) {
        Logger.getLogger("Auth Service").info("Message received from Topic register: " + data);
        authService.register(data);
    }

    public void handleMessage(UserRoleRequest data) {
        Logger.getLogger("Auth Service").info("Message received from Topic update-role: " + data);
        userService.updateUserRole(data);
    }

    public void handleMessage(UserProfileRequest data) {
        Logger.getLogger("Auth Service").info("Message received from Topic update-profile: " + data);
        userService.updateUserProfile(data);
    }

    public void handleMessage(UserEmailRequest data) {
        Logger.getLogger("Auth Service").info("Message received from Topic update-email: " + data);
        userService.updateUserEmail(data);
    }

    public void handleMessage(UserPasswordRequestData data) {
        Logger.getLogger("Auth Service").info("Message received from Topic update-password: " + data);
        userService.updatePasswordUser(data);
    }
}
