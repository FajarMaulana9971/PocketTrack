package PocketTrack.Serverapp.Services.Implementation;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.NOT_FOUND;
import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.SUCCESSFULLY_RETRIEVED;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.UserResponse;
import PocketTrack.Serverapp.Repositories.UserRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.UserService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl extends BaseServicesImpl<User, String> implements UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email : " + email + NOT_FOUND));
    }

    public ResponseEntity<ResponseData<UserResponse>> findByEmailWithResponse(String email) {
        try {
            User user = getUserByEmail(email);
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            return new ResponseEntity<>(new ResponseData<>(userResponse, "User" + SUCCESSFULLY_RETRIEVED),
                    HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }
}
