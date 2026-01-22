package ru.lazer.cas.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.lazer.cas.auth.AccessService;
import ru.lazer.cas.auth.AuditLogRepository;

@RestController
public class ForwardAuthController {

    private final AccessService access;
    private final AuditLogRepository audit;

    public ForwardAuthController(AccessService access, AuditLogRepository audit) {
        this.access = access;
        this.audit = audit;
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<Void> verify(
            @RequestHeader(value = "X-Forwarded-Host", required = false) String host,
            @RequestHeader(value = "X-Forwarded-Uri", required = false) String uri,
            Authentication auth,
            HttpServletRequest req
    ) {
        String remote = req.getRemoteAddr();

        if (auth == null || !auth.isAuthenticated()) {
            audit.log(null, host, uri, "UNAUTH", remote);
            return ResponseEntity.status(401).build();
        }

        String user = auth.getName();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            audit.log(auth.getName(), host, uri, "ALLOW_ADMIN", remote);
            return ResponseEntity.ok()
                    .header("X-User", auth.getName())
                    .header("X-Roles", "ROLE_ADMIN")
                    .build();
        }

        boolean ok = access.isAllowed(user, host);
        audit.log(user, host, uri, ok ? "ALLOW" : "DENY", remote);

        if (!ok) return ResponseEntity.status(403).build();

        return ResponseEntity.ok()
                .header("X-User", user)
                .header("X-Roles", String.join(",", access.roles(user)))
                .build();
    }
}
