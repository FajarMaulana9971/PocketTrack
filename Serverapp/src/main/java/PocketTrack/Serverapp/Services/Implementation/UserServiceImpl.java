package PocketTrack.Serverapp.Services.Implementation;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.NOT_FOUND;
import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.SUCCESSFULLY_CREATED;
import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.SUCCESSFULLY_RETRIEVED;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.Account;
import PocketTrack.Serverapp.Domains.Entities.AccountRole;
import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.Requests.UserPasswordRequestData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserProfileRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.UserResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.UsersResponseList;
import PocketTrack.Serverapp.Repositories.AccountRepository;
import PocketTrack.Serverapp.Repositories.AccountRoleRepository;
import PocketTrack.Serverapp.Repositories.RoleRepository;
import PocketTrack.Serverapp.Repositories.UserRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseServicesImpl<User, String> implements UserService {
    private UserRepository userRepository;
    private AccountRoleRepository accountRoleRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private AccountRepository accountRepository;

    /**
     * This method is used to get user by email
     * 
     * @param email -Email of user
     * @return User
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email : " + email + NOT_FOUND));
    }

    /**
     * This method is used to get user by email or username
     * 
     * @param name -Email or username of user
     * @return User
     */
    @Override
    public User getUserByEmailOrUsername(String name) {
        return userRepository.findByEmailOrAccount_Username(name, name);
    }

    /**
     * This method is used to get user by email
     * 
     * @param email -Email of user
     * @return User response with response data
     */

    @Override
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

    /**
     * This method is used to get users
     * 
     * @return Users Response with list of user data
     */
    @Override
    public ResponseEntity<UsersResponseList> getAllUser() {
        UsersResponseList usersResponseList = new UsersResponseList();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setName(user.getName());
            userResponse.setEmail(user.getEmail());
            userResponse.setBirthDate(user.getBirthDate());
            userResponse.setGender(user.getGender());
            userResponse.setJoinDate(user.getJoinDate());
            userResponse.setRoles(user.getAccount().getAccountRoles().stream().collect(ArrayList::new,
                    (list, accountRole) -> list.add(accountRole.getRole().getName()), ArrayList::addAll));
            userResponses.add(userResponse);
        }
        usersResponseList.setUserResponsesList(userResponses);
        return new ResponseEntity<>(usersResponseList, HttpStatus.OK);
    }

    /**
     * This method is used to get account role
     * 
     * @param id -ID of user
     * @return List of account role
     */
    @Override
    public List<String> getAccountRole(String id) {
        List<AccountRole> accountRoles = accountRoleRepository.findByAccountId(id);
        return accountRoles.stream().collect(ArrayList::new,
                (list, accountRole) -> list.add(accountRole.getRole().getName()), ArrayList::addAll);
    }

    /**
     * This method is used to upload user data
     * 
     * @param UserProfileRequest -userRequest of user
     * @return User Response
     */
    @Override
    public void updateUserProfile(UserProfileRequest userProfileRequest) {
        try {
            User user = getById(userProfileRequest.getId());
            user.setName(userProfileRequest.getName());
            user.setEmail(userProfileRequest.getEmail());
            user.setNumberPhone(userProfileRequest.getNumberPhone());
            user.setBirthDate(userProfileRequest.getBirthDate());

            userRepository.save(user);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to update password user
     * 
     * @param UserPasswordRequestData -userPasswordRequestData of user
     * @return User Response
     */
    @Override
    public void updatePasswordUser(UserPasswordRequestData userPasswordRequestData) {
        try {
            User user = getById(userPasswordRequestData.getId());
            Account account = user.getAccount();
            if (userPasswordRequestData.getNewPassword() == userPasswordRequestData.getOldPassword()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "New password and old password cannot be same");
            }
            account.setPassword(passwordEncoder.encode(userPasswordRequestData.getNewPassword()));
            accountRepository.save(account);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to check password user is match or not
     * 
     * @param id       -id of user
     * @param password -password of user
     * @return Boolean true or false
     */
    @Override
    public Boolean userPasswordCheck(String id, String password) {
        try {
            User user = getById(id);
            return passwordEncoder.matches(password, user.getAccount().getPassword());
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

}
