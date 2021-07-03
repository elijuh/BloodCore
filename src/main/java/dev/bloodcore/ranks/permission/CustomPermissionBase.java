package dev.bloodcore.ranks.permission;

import dev.bloodcore.etc.User;
import dev.bloodcore.utils.ReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import java.util.List;

public class CustomPermissionBase extends PermissibleBase {
    @Getter private final PermissibleBase previous;
    private final User user;
    public CustomPermissionBase(User user) {
        super(user.getPlayer());
        this.user = user;
        previous = ReflectionUtil.getPermissibleBase(user.getPlayer());
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return hasPermission(perm.getName());
    }

    @Override
    public boolean hasPermission(String name) {
        name = name.toLowerCase();
        List<String> permissions = user.getPermissions();

        for (String perm : permissions) {
            if (perm.equals("-" + name)) {
                return false;
            } else if (perm.equals("*")) {
                return true;
            } else if (perm.equals(name)) {
                return true;
            }
        }

        Permission perm = Bukkit.getPluginManager().getPermission(name);
        return perm != null ? perm.getDefault().getValue(super.isOp()) : Permission.DEFAULT_PERMISSION.getValue(super.isOp());
    }
}
