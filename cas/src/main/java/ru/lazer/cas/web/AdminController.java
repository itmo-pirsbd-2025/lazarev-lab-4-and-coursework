package ru.lazer.cas.web;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.lazer.cas.auth.AccessService;
import ru.lazer.cas.db.*;

@Controller
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final ServiceEntryRepository services;
    private final RoleRepository roles;
    private final PermissionRepository permissions;

    private final AccessService access;
    public AdminController(ServiceEntryRepository services, RoleRepository roles, PermissionRepository permissions, UserAccountRepository userAccounts, AccessService access) {

        this.services = services;
        this.roles = roles;
        this.permissions = permissions;
        this.access = access;
    }


    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", services.findAll());
        return "admin_services";
    }

    @PostMapping("/services")
    @Transactional
    public String addService(@RequestParam @NotBlank String name,
                             @RequestParam @NotBlank String host) {
        ServiceEntry s = new ServiceEntry();
        s.setName(name);
        s.setHost(host);
        s.setEnabled(true);
        services.save(s);
        access.invalidateAll();
        return "redirect:/admin/services";
    }

    @PostMapping("/services/{id}/toggle")
    @Transactional
    public String toggle(@PathVariable Long id) {
        ServiceEntry s = services.findById(id).orElseThrow();
        s.setEnabled(!s.isEnabled());
        services.save(s);
        access.invalidateAll();
        return "redirect:/admin/services";
    }

    @GetMapping("/permissions")
    public String permissions(Model model) {
        model.addAttribute("services", services.findAll());
        model.addAttribute("roles", roles.findAll());
        model.addAttribute("perms", permissions.findAll());
        return "admin_permissions";
    }

    @PostMapping("/permissions")
    @Transactional
    public String grant(@RequestParam Long serviceId,
                        @RequestParam Long roleId) {
        PermissionKey k = new PermissionKey(serviceId, roleId);
        permissions.findById(k).orElseGet(() -> permissions.save(new Permission(serviceId, roleId, true)));
        access.invalidateAll();
        return "redirect:/admin/permissions";
    }


    @GetMapping("/roles")
    public String roles(Model model) {
        model.addAttribute("roles", roles.findAll());
        return "admin_roles";
    }

    @PostMapping("/roles")
    public String addRole(@RequestParam("name") String name) {
        roles.save(new Role(name.trim()));
        access.invalidateAll();
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/{id}/delete")
    public String deleteRole(@PathVariable("id") Long id) {
        roles.deleteById(id);
        access.invalidateAll();
        return "redirect:/admin/roles";
    }

}
