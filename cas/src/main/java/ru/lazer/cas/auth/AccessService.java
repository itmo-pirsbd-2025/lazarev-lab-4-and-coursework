package ru.lazer.cas.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazer.cas.db.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccessService {
    private final UserAccountRepository users;
    private final ServiceEntryRepository services;
    private final PermissionRepository permissions;
    private final RoleRepository roles;

    private final Cache<String, Set<String>> rolesByUser = Caffeine.newBuilder()
            .maximumSize(10_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    private final Cache<String, Set<String>> allowedRolesByHost = Caffeine.newBuilder()
            .maximumSize(10_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    private final Cache<Long, String> roleNameById = Caffeine.newBuilder()
            .maximumSize(10_000).expireAfterWrite(Duration.ofMinutes(10)).build();

    public AccessService(UserAccountRepository users, ServiceEntryRepository services,
                         PermissionRepository permissions, RoleRepository roles) {
        this.users = users;
        this.services = services;
        this.permissions = permissions;
        this.roles = roles;
    }

    @Transactional(readOnly = true)
    public Set<String> roles(String username) {
        return rolesByUser.get(username, this::loadRolesForUser);
    }

    @Transactional(readOnly = true)
    public boolean isAllowed(String username, String host) {
        if (host == null || host.isBlank()) return false;
        Set<String> userRoles = roles(username);
        if (userRoles.isEmpty()) return false;
        if (userRoles.contains("ADMIN")) return true;

        Set<String> allowed = allowedRolesByHost.get(host, this::loadAllowedRolesForHost);
        if (allowed.isEmpty()) return false;

        for (String r : userRoles) if (allowed.contains(r)) return true;
        return false;
    }

    @Transactional(readOnly = true)
    public List<ServiceEntry> listAllowedServices(String username) {
        Set<String> userRoles = roles(username);
        List<ServiceEntry> all = services.findAll().stream().filter(ServiceEntry::isEnabled).toList();
        if (userRoles.contains("ADMIN")) return all;

        return all.stream().filter(s -> {
            Set<String> allowed = allowedRolesByHost.get(s.getHost(), this::loadAllowedRolesForHost);
            for (String r : userRoles) if (allowed.contains(r)) return true;
            return false;
        }).toList();
    }

    public void invalidateAll() {
        rolesByUser.invalidateAll();
        allowedRolesByHost.invalidateAll();
        roleNameById.invalidateAll();
    }

    private Set<String> loadRolesForUser(String username) {
        return users.findByUsername(username)
                .map(u -> u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }

    private Set<String> loadAllowedRolesForHost(String host) {
        var sOpt = services.findByHost(host);
        if (sOpt.isEmpty() || !sOpt.get().isEnabled()) return Set.of();

        Long serviceId = sOpt.get().getId();
        var perms = permissions.findByServiceId(serviceId);
        if (perms.isEmpty()) return Set.of();

        Set<String> names = new HashSet<>();
        for (Permission p : perms) {
            if (!p.isAllowAccess()) continue;
            String name = roleNameById.get(p.getRoleId(),
                    rid -> roles.findById(rid).map(Role::getName).orElse(null));
            if (name != null) names.add(name);
        }
        return Collections.unmodifiableSet(names);
    }
}
