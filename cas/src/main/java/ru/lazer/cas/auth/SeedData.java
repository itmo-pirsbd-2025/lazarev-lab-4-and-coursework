package ru.lazer.cas.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.lazer.cas.db.*;

@Configuration
public class SeedData {

    @Bean
    CommandLineRunner seed(UserAccountRepository users,
                           RoleRepository roles,
                           ServiceEntryRepository services,
                           PermissionRepository permissions,
                           PasswordEncoder enc,
                           AccessService access) {
        return args -> seedOnce(users, roles, services, permissions, enc, access);
    }

    @Transactional
    void seedOnce(UserAccountRepository users,
                  RoleRepository roles,
                  ServiceEntryRepository services,
                  PermissionRepository permissions,
                  PasswordEncoder enc,
                  AccessService access) {

        Role adminRole = roles.findByName("ADMIN").orElseGet(() -> roles.save(new Role("ADMIN")));
        Role userRole  = roles.findByName("USER").orElseGet(() -> roles.save(new Role("USER")));

        users.findByUsername("admin").orElseGet(() -> {
            UserAccount u = new UserAccount();
            u.setUsername("admin");
            u.setPasswordHash(enc.encode("admin"));
            u.getRoles().add(adminRole);
            return users.save(u);
        });

        users.findByUsername("user").orElseGet(() -> {
            UserAccount u = new UserAccount();
            u.setUsername("user");
            u.setPasswordHash(enc.encode("user"));
            u.getRoles().add(userRole);
            return users.save(u);
        });

        ServiceEntry graf = services.findByHost("grafana.localtest.me").orElseGet(() -> {
            ServiceEntry s = new ServiceEntry();
            s.setName("Grafana");
            s.setHost("grafana.localtest.me");
            return services.save(s);
        });

        ServiceEntry whoami = services.findByHost("whoami.localtest.me").orElseGet(() -> {
            ServiceEntry s = new ServiceEntry();
            s.setName("WhoAmI");
            s.setHost("whoami.localtest.me");
            return services.save(s);
        });

        ServiceEntry prom = services.findByHost("prom.localtest.me").orElseGet(() -> {
            ServiceEntry s = new ServiceEntry();
            s.setName("Prometheus");
            s.setHost("prom.localtest.me");
            return services.save(s);
        });

        ensurePermission(permissions, graf.getId(), userRole.getId());
        ensurePermission(permissions, whoami.getId(), userRole.getId());
        ensurePermission(permissions, prom.getId(), userRole.getId());

        access.invalidateAll();
    }

    private static void ensurePermission(PermissionRepository repo, Long serviceId, Long roleId) {
        PermissionKey k = new PermissionKey(serviceId, roleId);
        repo.findById(k).orElseGet(() -> repo.save(new Permission(serviceId, roleId, true)));
    }
}
