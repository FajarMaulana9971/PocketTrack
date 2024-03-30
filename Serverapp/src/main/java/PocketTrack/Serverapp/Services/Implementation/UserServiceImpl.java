package PocketTrack.Serverapp.Services.Implementation;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.NOT_FOUND;
import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.SUCCESSFULLY_CREATED;
import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.SUCCESSFULLY_RETRIEVED;
import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.SUCCESSFULLY_UPDATED;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.Account;
import PocketTrack.Serverapp.Domains.Entities.AccountRole;
import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.PageData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserEmailRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserPasswordRequestData;
import PocketTrack.Serverapp.Domains.Models.Requests.UserProfileRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.UserRoleRequestData;
import PocketTrack.Serverapp.Domains.Models.Requests.Redis.UserRoleRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.UserResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.UsersResponseList;
import PocketTrack.Serverapp.Repositories.AccountRepository;
import PocketTrack.Serverapp.Repositories.AccountRoleRepository;
import PocketTrack.Serverapp.Repositories.RoleRepository;
import PocketTrack.Serverapp.Repositories.UserRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.UserService;
import PocketTrack.Serverapp.Utilities.PaginationUtil;
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
    private RedisTemplate<String, UserRoleRequestData> updateUserRole;
    private PaginationUtil paginationUtil;

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
     * This method is used to check user
     * 
     * @param email -Email of user
     * @return String of user account status
     */
    @Override
    public String userCheck(String email) {
        try {
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                return user.get().getAccount().getAccountStatus().getName();
            } else {
                return "Unregistered";
            }
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to check user for public
     * 
     * @param email -Email of user
     * @return String of user account status
     */
    @Override
    public Boolean userEmailCheck(String email) {
        try {
            Optional<User> user = userRepository.findByEmail(email);
            return user.isPresent();
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
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
    public ResponseEntity<UsersResponseList> getAllUserList() {
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
     * This method is used to get all user list for admin page
     * 
     * @param keyword - keyword for search
     * @param page    - page number
     * @param size    - size of page
     * @return List of user with pagination
     */
    @Override
    public ObjectResponseData<UserResponse> getAllUserWithPagination(String keywoard, int page, int size) {
        if (page > 0)
            page = page - 1;

        try {
            UsersResponseList users = getAllUserList().getBody();
            if (users == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registered user is not found");
            }
            List<UserResponse> userResponses = new ArrayList<>();
            users.getUserResponsesList().forEach(user -> {
                UserResponse userResponse = modelMapper.map(user, UserResponse.class);
                List<String> roles = new ArrayList<>();
                user.getRoles().forEach(role -> {
                    role = role.replace("ROLE_", "").replace("_", "");
                    roles.add(role);
                });
                userResponse.setRoles(roles);
                userResponses.add(userResponse);
            });
            Pageable pageable = PageRequest.of(page, size);
            Page<UserResponse> userList = new PageImpl<>(userResponses, pageable, userResponses.size());
            PageData pagination = paginationUtil.setPageData(page, size, (int) userList.getTotalElements());
            return new ObjectResponseData<>(userList.getContent(), pagination);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
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
    public ResponseEntity<ResponseData<UserProfileRequest>> updateUserProfile(UserProfileRequest userProfileRequest) {
        try {
            User user = getById(userProfileRequest.getId());
            user.setName(userProfileRequest.getName());
            user.setEmail(userProfileRequest.getEmail());
            user.setNumberPhone(userProfileRequest.getNumberPhone());
            user.setBirthDate(userProfileRequest.getBirthDate());
            userRepository.save(user);
            return ResponseEntity.ok(new ResponseData<>(userProfileRequest,
                    "User : " + userProfileRequest.getName() + SUCCESSFULLY_UPDATED));
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

    /**
     * This method is used to update user role
     * 
     * @param userRoleRequest - Request body of user role
     */
    @Override
    public void updateUserRole(UserRoleRequest userRoleRequest) {
        try {
            User user = getUserByEmail(userRoleRequest.getEmail());
            Account account = user.getAccount();
            accountRoleRepository.deleteAll(account.getAccountRoles());

            List<AccountRole> accountRoles = new ArrayList<>();
            for (String role : userRoleRequest.getRoles()) {
                AccountRole userAccountRole = accountRoleRepository.findByAccountAndRoleId(account, role);
                if (userAccountRole == null) {
                    AccountRole accountRole = new AccountRole();
                    accountRole.setAccount(account);
                    accountRole.setRole(roleRepository.findByName(role));
                    accountRoleRepository.save(accountRole);
                    accountRoles.add(accountRole);
                }
            }
            account.setAccountRoles(accountRoles);
            accountRepository.save(account);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to update user email
     * 
     * @param userEmailRequest - Request body of user email
     */
    @Override
    public void updateUserEmail(UserEmailRequest userEmailRequest) {
        try {
            User user = getUserByEmail(userEmailRequest.getOldEmail());
            user.setEmail(userEmailRequest.getNewEmail());
            userRepository.save(user);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

}
