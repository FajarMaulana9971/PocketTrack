package PocketTrack.Serverapp.Services.Interfaces;

import org.springframework.http.ResponseEntity;

import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.UserResponse;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;

public interface UserService extends BaseServices<User, String> {

    User getUserByEmail(String email);

    ResponseEntity<ResponseData<UserResponse>> findByEmailWithResponse(String email);

}
