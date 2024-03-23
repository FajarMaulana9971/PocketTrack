package PocketTrack.Serverapp.Services.Implementation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Constants.ConstantVariables;
import PocketTrack.Serverapp.Domains.Constants.ExceptionMessage;
import PocketTrack.Serverapp.Domains.Entities.Account;
import PocketTrack.Serverapp.Domains.Entities.AccountRole;
import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.LoginData;
import PocketTrack.Serverapp.Domains.Models.RegisterData;
import PocketTrack.Serverapp.Domains.Models.Requests.EmailRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.PasswordRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.LoginResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.RoleResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.ValidateTokenResponse;
import PocketTrack.Serverapp.Exceptions.AuthorizationException;
import PocketTrack.Serverapp.Exceptions.RequiredFieldIsMissingException;
import PocketTrack.Serverapp.Exceptions.RequiredFieldNotValidException;
import PocketTrack.Serverapp.Repositories.AccountRepository;
import PocketTrack.Serverapp.Repositories.AccountRoleRepository;
import PocketTrack.Serverapp.Repositories.AccountStatusRepository;
import PocketTrack.Serverapp.Repositories.RoleRepository;
import PocketTrack.Serverapp.Repositories.UserRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.UserService;
import PocketTrack.Serverapp.Utilities.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends BaseServicesImpl<User, String> {
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AccountRepository accountRepository;
    private RoleRepository roleRepository;
    private UserService userService;
    private AccountRoleRepository accountRoleRepository;
    private AccountStatusRepository accountStatusRepository;
    private RedisTemplate<String, EmailRequest> sendUserEmail;

    public ResponseEntity<ResponseData<RegisterData>> register(RegisterData registerData) {
        try {
            Optional<User> userCheck = userRepository.findByEmail(registerData.getEmail());
            if (userCheck.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email has already registered!");
            }

            User user = new User();
            user.setName(registerData.getName());
            user.setEmail(registerData.getEmail());

            int atIndex = registerData.getEmail().indexOf("@");

            Account account = new Account();
            account.setUsername(registerData.getEmail().substring(0, atIndex));
            account.setPassword(passwordEncoder.encode(registerData.getPassword()));
            account.setVerificationCode(UUID.randomUUID().toString());
            account.setUser(user);
            accountRepository.save(account);

            user.setAccount(account);
            userRepository.save(user);

            AccountRole accountRole = new AccountRole();
            accountRole.setRole(roleRepository.findByName("ROLE_USER"));
            accountRole.setAccount(account);
            accountRoleRepository.save(accountRole);

            registerData.setPassword(null);

            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setUserid(user.getId());
            emailRequest.setName(user.getName());
            emailRequest.setEmail(user.getEmail());
            emailRequest.setSubject("Registration");
            emailRequest.setCode(account.getVerificationCode());
            sendUserEmail.convertAndSend("email", emailRequest);
            return new ResponseEntity<>(new ResponseData<>(registerData, "Registration successfully"),
                    HttpStatus.CREATED);

        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    public LoginResponse login(LoginData loginData, HttpServletResponse response) {
        try {
            User user = userRepository.findByEmailOrAccount_Username(loginData.getEmail(), loginData.getEmail());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not registered");
            }
            if (!passwordEncoder.matches(loginData.getPassword(), user.getAccount().getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
            }
            if (user.getAccount().getAccountStatus().getId() != 1) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is not active!");
            }

            String accessToken = jwtUtil.generateToken(createPayload(user), user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(createPayload(user), user.getEmail());
            String expiredToken = jwtUtil.extractExpiration(accessToken).toString();
            setTokenCookie("accessToken", accessToken, response);
            setTokenCookie("refreshToken", refreshToken, response);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(accessToken);
            loginResponse.setExpired(expiredToken);
            loginResponse.setStatus(200);
            return loginResponse;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    public Map<String, Object> createPayload(User user) {
        List<String> roles = new ArrayList<>();
        if (user.getAccount().getAccountRoles() != null) {
            for (AccountRole accountRole : user.getAccount().getAccountRoles()) {
                roles.add(accountRole.getRole().getName());
            }
        }

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("username", user.getAccount().getUsername());
        claims.put("role", roles);
        return claims;
    }

    public void setTokenCookie(String type, String token, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(type, token)
                .maxAge(604800)
                .sameSite("none")
                .secure(true)
                .path("/")
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public ResponseEntity<ResponseData<Boolean>> forgotPassword(String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email is not registered");
            }
            Account account = user.getAccount();
            account.setVerificationCode(UUID.randomUUID().toString());
            accountRepository.save(account);

            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setUserid(user.getId());
            emailRequest.setName(user.getName());
            emailRequest.setEmail(user.getEmail());
            emailRequest.setSubject("forgot-password");
            emailRequest.setCode(account.getVerificationCode());
            sendUserEmail.convertAndSend("email", emailRequest);
            return new ResponseEntity<>(new ResponseData<>(Boolean.TRUE, "Email sent successfully"), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    public ResponseEntity<ResponseData<Boolean>> resetPassword(PasswordRequest passwordRequest) {
        try {
            Account account = accountRepository.findByVerificationCode(passwordRequest.getVerificationCode());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email is not registered");
            }
            if (!passwordEncoder.matches(passwordRequest.getOldPassword(), account.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is wrong");
            }
            account.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            account.setVerificationCode(null);
            accountRepository.save(account);
            return new ResponseEntity<>(new ResponseData<>(Boolean.TRUE, "Password changed successfully!"),
                    HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    public LoginResponse refreshToken(String accessToken) {
        try {
            if (Boolean.FALSE.equals(jwtUtil.validateToken(accessToken))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
            }
            if (Boolean.TRUE.equals(jwtUtil.isTokenExpired(accessToken))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is expired");
            }

            String subject = jwtUtil.extractUsername(accessToken);
            String newAccessToken = jwtUtil.generateToken(accessToken, subject);
            String expiredToken = jwtUtil.extractExpiration(newAccessToken).toString();

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(newAccessToken);
            loginResponse.setExpired(expiredToken);
            return loginResponse;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    public ResponseEntity<ResponseData<String>> verification(String verificationCode) {
        try {
            Account account = accountRepository.findByVerificationCode(verificationCode);
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Verification code is not valid !");
            }
            account.setAccountStatus(accountStatusRepository.getReferenceById(1));
            account.setVerificationCode(null);
            accountRepository.save(account);
            return new ResponseEntity<>(new ResponseData<>("Active", "Account verified successfully ! "),
                    HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    public String logout(String accessToken, HttpServletResponse response) {
        if (accessToken != null) {
            ResponseCookie accessCookie = ResponseCookie.fromClientResponse("accessToken", "")
                    .maxAge(0)
                    .sameSite("None")
                    .secure(true)
                    .path("/")
                    .httpOnly(true)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

            ResponseCookie refreshCookie = ResponseCookie.fromClientResponse("refreshToken", "")
                    .maxAge(0)
                    .sameSite("None")
                    .secure(true)
                    .path("/")
                    .httpOnly(true)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return "Logout Success";
    }

    public ResponseEntity<ValidateTokenResponse> validateToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            throw new RequiredFieldIsMissingException(ExceptionMessage.TOKEN_FOR_VALIDATE_IS_MISSING);
        }

        if (authorization.length() < 8) {
            throw new RequiredFieldNotValidException(ExceptionMessage.BEARER_TOKEN_INVALID);
        }

        String token = "";
        if (authorization.startsWith(ConstantVariables.BEARER)) {
            token = authorization.substring(7);
        }

        if (Boolean.TRUE.equals(jwtUtil.isTokenExpired(token))) {
            throw new AuthorizationException(ExceptionMessage.JWT_TOKEN_IS_EXPIRED);
        }

        ValidateTokenResponse validateTokenResponse = new ValidateTokenResponse();
        if (!token.isBlank() && Boolean.TRUE.equals(jwtUtil.validateToken(token))) {
            String username = jwtUtil.extractUsername(token);
            User userDetails = userService.getUserByEmailOrUsername(username);

            if (userDetails != null) {
                List<RoleResponse> listRoles = new ArrayList<>();
                List<AccountRole> userRoles = accountRoleRepository.findByAccountId(userDetails.getId());
                userRoles.forEach(
                        roles -> {
                            RoleResponse roleResponses = new RoleResponse();
                            roleResponses.setName(roles.getRole().getName());
                            listRoles.add(roleResponses);
                        });
                validateTokenResponse.setId(userDetails.getId());
                validateTokenResponse.setName(userDetails.getName());
                validateTokenResponse.setEmail(userDetails.getEmail());
                validateTokenResponse.setRoles(listRoles);
            }
        }
        return new ResponseEntity<>(validateTokenResponse, HttpStatus.OK);
    }
}
