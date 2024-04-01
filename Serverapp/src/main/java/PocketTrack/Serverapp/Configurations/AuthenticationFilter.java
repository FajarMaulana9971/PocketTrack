package PocketTrack.Serverapp.Configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import PocketTrack.Serverapp.Domains.Entities.Account;
import PocketTrack.Serverapp.Services.Interfaces.UserService;
import PocketTrack.Serverapp.Utilities.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends BasicAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String jwtToken = getJwtFromRequest(request);

        if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return (StringUtils.hasText(bearerToken) && bearerToken.startsWith("bearer ")) ? bearerToken.substring(7)
                : null;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String tokenHeader = request.getHeader("Authorization");
        String token = tokenHeader.replace("Bearer ", "");
        try {
            String user = jwtUtil.extractUsername(token);
            Account account = userService.getUserByEmailOrUsername(user).getAccount();
            List<String> roles = userService.getAccountRole(account.getId());
            return new UsernamePasswordAuthenticationToken(user, null, getAuthorities(roles));
        } catch (Exception e) {
            return null;
        }
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
