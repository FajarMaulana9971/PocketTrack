package PocketTrack.Serverapp.Services.Interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;

import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.Requests.UserEmailRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserPasswordRequestData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserProfileRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserRoleRequestData;
import PocketTrack.Serverapp.Domains.Models.Requests.Redis.UserRoleRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.UserResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.UsersResponseList;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;

public interface UserService extends BaseServices<User, String> {

    User getUserByEmail(String email);

    ResponseEntity<ResponseData<UserResponse>> findByEmailWithResponse(String email);

    ObjectResponseData<UserResponse> getAllUserWithPagination(String keywoard, int page, int size);

    String userCheck(String email);

    Boolean userEmailCheck(String email);

    List<String> getAccountRole(String id);

    void updateUserProfile(UserProfileRequest userProfileRequest);

    void updatePasswordUser(UserPasswordRequestData userPasswordRequestData);

    Boolean userPasswordCheck(String id, String password);

    User getUserByEmailOrUsername(String name);

    void updateUserRole(UserRoleRequest userRoleRequest);

    void updateUserEmail(UserEmailRequest userEmailRequest);

}
