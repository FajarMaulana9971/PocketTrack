package PocketTrack.Serverapp.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import PocketTrack.Serverapp.Services.Interfaces.UserService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("admin/users/{email/email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
        return userService.findByEmailWithResponse(email);
    }

    @GetMapping("admin/users/{email}/check")
    public String userCheck(@PathVariable("email") String email) {
        return userService.userCheck(email);
    }

    @GetMapping("admin/users")
    public ResponseEntity<?> getUsers() {
        return userService.getAllUser();
    }
}
