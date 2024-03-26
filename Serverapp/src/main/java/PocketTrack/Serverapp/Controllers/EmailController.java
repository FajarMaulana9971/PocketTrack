package PocketTrack.Serverapp.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import PocketTrack.Serverapp.Domains.Models.Requests.EmailRequest;
import PocketTrack.Serverapp.Services.Implementation.EmailService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("test")
    public EmailRequest sendSimpleMessage(@RequestBody EmailRequest emailRequest) {
        return emailService.sendSimpleMessage(emailRequest);
    }
}
