package PocketTrack.Serverapp.Services.Implementation;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.SUCCESSFULLY_DELETED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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
import PocketTrack.Serverapp.Domains.Entities.AccountStatus;
import PocketTrack.Serverapp.Domains.Entities.Budget;
import PocketTrack.Serverapp.Domains.Entities.Income;
import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.LoginData;
import PocketTrack.Serverapp.Domains.Models.RegisterData;
import PocketTrack.Serverapp.Domains.Models.Requests.BudgetRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.EmailRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.PasswordRequest;
import PocketTrack.Serverapp.Domains.Models.Requests.VerificationRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.LoginResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.RegisterResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.RoleResponse;
import PocketTrack.Serverapp.Domains.Models.Responses.ValidateTokenResponse;
import PocketTrack.Serverapp.Exceptions.AuthorizationException;
import PocketTrack.Serverapp.Exceptions.RequiredFieldIsMissingException;
import PocketTrack.Serverapp.Exceptions.RequiredFieldNotValidException;
import PocketTrack.Serverapp.Repositories.AccountRepository;
import PocketTrack.Serverapp.Repositories.AccountRoleRepository;
import PocketTrack.Serverapp.Repositories.AccountStatusRepository;
import PocketTrack.Serverapp.Repositories.BudgetRepository;
import PocketTrack.Serverapp.Repositories.IncomeRepository;
import PocketTrack.Serverapp.Repositories.OutcomeRepository;
import PocketTrack.Serverapp.Repositories.RoleRepository;
import PocketTrack.Serverapp.Repositories.UserRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.AuthService;
import PocketTrack.Serverapp.Services.Interfaces.BudgetService;
import PocketTrack.Serverapp.Services.Interfaces.UserService;
import PocketTrack.Serverapp.Utilities.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl extends BaseServicesImpl<User, String> implements AuthService {
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AccountRepository accountRepository;
    private RoleRepository roleRepository;
    private UserService userService;
    private BudgetService budgetService;
    private BudgetRepository budgetRepository;
    private IncomeRepository incomeRepository;
    private OutcomeRepository outcomeRepository;
    private AccountRoleRepository accountRoleRepository;
    private AccountStatusRepository accountStatusRepository;
    private RedisTemplate<String, EmailRequest> sendUserEmail;

    /**
     * This method is used to register a new user
     * 
     * @param registerData - Request body of register data
     * @return Register with response data
     */
    @Override
    public ResponseEntity<ResponseData<RegisterResponse>> register(RegisterData registerData) {
        try {
            Optional<User> emailUserCheck = userRepository.findByEmail(registerData.getEmail());
            if (emailUserCheck.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email has already registered!");
            }

            Optional<User> numberPhoneCheck = userRepository.findByNumberPhone(registerData.getNumberPhone());
            if (numberPhoneCheck.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number phone is already registered!");
            }

            ZoneId zoneId = ZoneId.of("Asia/Jakarta");
            LocalDate now = LocalDate.now(zoneId);
            User user = new User();
            user.setName(registerData.getName());
            user.setEmail(registerData.getEmail());
            user.setGender(registerData.getGender());
            user.setJoinDate(now);
            user.setNumberPhone(registerData.getNumberPhone());
            user.setBirthDate(registerData.getBirthDate());

            int atIndex = registerData.getEmail().indexOf("@");

            Account account = new Account();
            account.setUsername(registerData.getEmail().substring(0, atIndex));
            account.setPassword(passwordEncoder.encode(registerData.getPassword()));
            account.setVerificationCode(UUID.randomUUID().toString());
            account.setUser(user);
            AccountStatus accountStatus = accountStatusRepository.findById(-1)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            account.setAccountStatus(accountStatus);
            user.setAccount(account);
            userRepository.save(user);

            AccountRole accountRole = new AccountRole();
            accountRole.setRole(roleRepository.findByName("ROLE_USER"));
            accountRole.setAccount(account);
            accountRoleRepository.save(accountRole);

            registerData.setPassword(null);

            RegisterResponse registerResponse = new RegisterResponse();
            registerResponse.setAccountId(user.getId());
            registerResponse.setEmail(user.getEmail());
            registerResponse.setVerificationCode(account.getVerificationCode());

            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setUserid(user.getId());
            emailRequest.setName(user.getName());
            emailRequest.setEmail(user.getEmail());
            emailRequest.setSubject("Registration");
            emailRequest.setCode(account.getVerificationCode());
            sendUserEmail.convertAndSend("email", emailRequest);
            return new ResponseEntity<>(new ResponseData<>(registerResponse, "Registration successfully"),
                    HttpStatus.CREATED);

        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to log in a user
     * 
     * @param loginData - Request body of login data
     * @param response  - Response of http servlet
     * @return Login response with token and expired token
     */
    @Override
    public LoginResponse login(LoginData loginData, HttpServletResponse response) {
        try {
            User user = userRepository.findByEmailOrAccount_Username(loginData.getEmail(), loginData.getEmail());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not registered");
            }
            if (!passwordEncoder.matches(loginData.getPassword(), user.getAccount().getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
            }
            if (user.getAccount().getAccountStatus().getId() == -1) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is not active!");
            }
            if (user.getAccount().getAccountStatus().getId() == -2) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account has been banned");
            }
            if (user.getAccount().getAccountStatus().getId() == -3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account has been deleted");
            }

            String accessToken = jwtUtil.generateToken(createPayload(user), user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(createPayload(user), user.getEmail());
            String expiredToken = jwtUtil.extractExpiration(accessToken).toString();
            setTokenToCookie("accessToken", accessToken, response);
            setTokenToCookie("refreshToken", refreshToken, response);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(accessToken);
            loginResponse.setExpired(expiredToken);
            loginResponse.setStatus(200);
            return loginResponse;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to create payload
     * 
     * @param user - User of user
     * @return Map of string and object
     */
    @Override
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

    /**
     * This method is used to set token to cookie
     * 
     * @param type     - Type of token (access, refresh)
     * @param token    - Token of user
     * @param response - Response of http servlet
     */
    @Override
    public void setTokenToCookie(String type, String token, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(type, token)
                .maxAge(604800)
                .sameSite("None")
                .secure(true)
                .path("/")
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * This method is used to forgot password
     * 
     * @param email - Email of user
     * @return Boolean of forgot password
     */
    @Override
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

    /**
     * This method is used to change password
     * 
     * @param passwordRequest - Request body of password request
     * @return Account with response data
     */
    @Override
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

    /**
     * This method is used to refresh token
     * 
     * @param accessToken - Access token of user
     * @return Login response with token and expired token
     */
    @Override
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

    /**
     * This method is used to verification
     * 
     * @param verificationCode - Verification code of user
     * @return Account with response data
     */
    @Override
    public ResponseEntity<ResponseData<String>> verification(VerificationRequest verificationRequest) {
        try {
            Account account = accountRepository.findByVerificationCode(verificationRequest.getVerificationCode());
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Verification code is not valid !");
            }
            if (account.getAccountStatus().getId() == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Account has already verificated !");
            }

            if (account.getAccountStatus().getId() == -2) {
                throw new ResponseStatusException(HttpStatus.LOCKED,
                        "Account " + verificationRequest.getAccountId() + " has been banned");
            }

            if (account.getAccountStatus().getId() == -3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account has been deleted !");
            }

            if (verificationRequest.getBudgetId() != null && !verificationRequest.getBudgetId().isEmpty()) {
                Budget budget = budgetRepository.findById(verificationRequest.getBudgetId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget id is not found"));
                User user = account.getUser();
                user.setBudget(budget);
                userRepository.save(user);
            } else {
                ZoneId zoneId = ZoneId.of("Asia/Jakarta");
                LocalDateTime now = LocalDateTime.now(zoneId);
                BudgetRequest budgetRequest = new BudgetRequest();
                Budget budget = new Budget();
                budget.setDate(now);
                budget.setTotalBalance(budgetRequest.getTotalBalance());
                budget.setTitle("first default commit by admin ");
                budget.setDescription("First default commit by : " + verificationRequest.getAccountId().toUpperCase());
                Budget savedBudget = budgetRepository.save(budget);

                Income income = new Income();
                income.setDate(now);
                income.setDescription("First commit by budget create".toLowerCase());
                income.setTitle("First Commit".toUpperCase());
                income.setAmount(BigDecimal.ZERO);
                income.setBudget(savedBudget);
                incomeRepository.save(income);

                Outcome outcome = new Outcome();
                outcome.setDate(now);
                outcome.setDescription("first commit by budget create".toLowerCase());
                outcome.setTitle("first commit".toUpperCase());
                outcome.setAmount(BigDecimal.ZERO);
                outcome.setBudget(savedBudget);
                outcome.setIsDeleted(false);
                outcome.setStatus(true);
                outcomeRepository.save(outcome);

                User user = account.getUser();
                user.setBudget(savedBudget);
                userRepository.save(user);
            }
            account.setAccountStatus(accountStatusRepository.getReferenceById(0));
            account.setVerificationCode(null);
            accountRepository.save(account);

            return new ResponseEntity<>(new ResponseData<>("Active", "Account verified successfully ! "),
                    HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to log out a user
     * 
     * @param accessToken - Access token of user
     * @param response    - Response of http servlet
     * @return String of logout
     */
    @Override
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

    /**
     * This method is used to validate token
     * 
     * @param authorization - Authorization of user
     * @return Validate token with response data
     */
    @Override
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

    /**
     * This method is used to soft delete user
     * 
     * @param accountId - Account id of user
     * @return Account with response data
     */
    @Override
    public Account deleteUser(String accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account id is not found"));
            account.setAccountStatus(accountStatusRepository.getReferenceById(-3));
            return accountRepository.save(account);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

}
