package PocketTrack.Serverapp.Services.Implementation;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.Account;
import PocketTrack.Serverapp.Domains.Entities.AccountRole;
import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.RegisterData;
import PocketTrack.Serverapp.Domains.Models.Requests.EmailRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Repositories.AccountRepository;
import PocketTrack.Serverapp.Repositories.AccountRoleRepository;
import PocketTrack.Serverapp.Repositories.RoleRepository;
import PocketTrack.Serverapp.Repositories.UserRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Utilities.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends BaseServicesImpl<User, String> {
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AccountRepository accountRepository;
    private RoleRepository roleRepository;
    private AccountRoleRepository accountRoleRepository;
    private RedisTemplate<String, EmailRequest> sendUserEmail;

    private ResponseEntity<ResponseData<RegisterData>> register(RegisterData registerData) {
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
}
