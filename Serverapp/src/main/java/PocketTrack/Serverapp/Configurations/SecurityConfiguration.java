package PocketTrack.Serverapp.Configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import PocketTrack.Serverapp.Services.Interfaces.UserService;
import PocketTrack.Serverapp.Utilities.JwtUtil;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    // private final JwtUtil jwtUtil;
    // private final UserService userService;

    // public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws
    // Exception{
    // return httpSecurity.csrf(AbstractHttpConfigurer::disable)
    // .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
    // .requestMatchers(HttpMethod.GET, "/admin/**").hasAnyRole("SUPER_ADMIN")
    // .requestMatchers(HttpMethod.POST, "")
    // )
    // }
}
