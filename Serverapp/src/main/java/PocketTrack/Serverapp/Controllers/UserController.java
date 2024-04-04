package PocketTrack.Serverapp.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import PocketTrack.Serverapp.Domains.Models.Requests.UserProfileRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.Redis.UserRoleRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.UserResponse;
import PocketTrack.Serverapp.Services.Interfaces.UserService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("admin/users/{email}/email")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email) {
        return userService.findByEmailWithResponse(email);
    }

    @GetMapping("admin/users/{email}/check")
    public String userCheck(@PathVariable("email") String email) {
        return userService.userCheck(email);
    }

    @GetMapping("public/users")
    public Boolean userEmailCheck(@PathVariable("email") String email) {
        return userService.userEmailCheck(email);
    }

    @GetMapping("public/users/{id}/password/{password}/check")
    public Boolean userPasswordCheck(@PathVariable("id") String id, @PathVariable("password") String password) {
        return userService.userPasswordCheck(id, password);
    }

    @GetMapping("admin/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        return userService.getByIdWithResponse(id);
    }

    @GetMapping("admin/users")
    public ObjectResponseData<UserResponse> getUsers(
            @RequestParam(required = false) String keywoard,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return userService.getAllUserWithPagination(keywoard, page, size);
    }

    @PatchMapping("public/user")
    public ResponseEntity<?> updateUserProfile(UserProfileRequest userProfileRequest) {
        return userService.updateUserProfile(userProfileRequest);
    }

    @PatchMapping("public/users/role")
    public void updateUserRole(UserRoleRequest userRoleRequest) {
        userService.updateUserRole(userRoleRequest);
    }

}
