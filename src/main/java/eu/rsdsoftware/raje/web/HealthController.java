package eu.rsdsoftware.raje.web;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "service", "rsds-ai-jobs-extractor");
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<Map<String, String>> currentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oauthUser) {
            String name = oauthUser.getAttribute("name");
            String email = oauthUser.getAttribute("email");
            return ResponseEntity.ok(Map.of(
                    "name", name == null ? authentication.getName() : name,
                    "email", email == null ? "" : email));
        }

        return ResponseEntity.ok(Map.of("name", authentication.getName(), "email", ""));
    }
}
