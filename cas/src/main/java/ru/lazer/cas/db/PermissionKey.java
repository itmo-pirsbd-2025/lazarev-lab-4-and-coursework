package ru.lazer.cas.db;

import java.io.Serializable;
import java.util.Objects;

public class PermissionKey implements Serializable {
    private Long serviceId;
    private Long roleId;

    public PermissionKey() {}
    public PermissionKey(Long serviceId, Long roleId) {
        this.serviceId = serviceId;
        this.roleId = roleId;
    }

    public Long getServiceId() { return serviceId; }
    public Long getRoleId() { return roleId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionKey)) return false;
        PermissionKey other = (PermissionKey) o;
        return Objects.equals(serviceId, other.serviceId) &&
               Objects.equals(roleId, other.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, roleId);
    }
}
