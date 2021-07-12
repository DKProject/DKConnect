package net.pretronic.dkconnect.minecraft.config;

public class RoleAssignment {

    private final String permission;
    private final String roleId;

    public RoleAssignment(String permission, String roleId) {
        this.permission = permission;
        this.roleId = roleId;
    }

    public String getPermission() {
        return permission;
    }

    public String getRoleId() {
        return roleId;
    }
}
