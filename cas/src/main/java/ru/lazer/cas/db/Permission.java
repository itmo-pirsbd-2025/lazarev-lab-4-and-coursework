package ru.lazer.cas.db;

import jakarta.persistence.*;

@Entity
@Table(name = "permissions")
@IdClass(PermissionKey.class)
public class Permission {
    @Id
    @Column(name = "service_id")
    private Long serviceId;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "allow_access", nullable = false)
    private boolean allowAccess = true;

    public Permission() {}

    public Permission(Long serviceId, Long roleId, boolean allowAccess) {
        this.serviceId = serviceId;
        this.roleId = roleId;
        this.allowAccess = allowAccess;
    }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public boolean isAllowAccess() { return allowAccess; }
    public void setAllowAccess(boolean allowAccess) { this.allowAccess = allowAccess; }
}
