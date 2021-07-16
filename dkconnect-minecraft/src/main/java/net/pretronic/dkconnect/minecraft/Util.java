package net.pretronic.dkconnect.minecraft;

import net.pretronic.dkconnect.api.VoiceAdapterType;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.minecraft.config.RoleAssignment;
import net.pretronic.dkconnect.minecraft.config.discord.DiscordGuildConfig;
import org.mcnative.runtime.api.player.MinecraftPlayer;

public class Util {

    public static void updateDiscordRoles(DKConnectPlugin dkConnectPlugin, Verification verification, MinecraftPlayer minecraftPlayer) {
        if(minecraftPlayer.getPermissionHandler() == null) return;
        if(verification.getVoiceAdapter().getType().equalsIgnoreCase(VoiceAdapterType.DISCORD)) {
            verification.getRoleIds().thenAccept(roleIds -> {
                for (DiscordGuildConfig guildConfig : dkConnectPlugin.getGuildConfigs()) {
                    for (RoleAssignment roleAssignment : guildConfig.getRoleAssignments()) {
                        boolean hasDiscordRole = roleIds.contains(roleAssignment.getRoleId());
                        boolean hasPermission = minecraftPlayer.hasPermission(roleAssignment.getPermission()) || roleAssignment.getPermission().equalsIgnoreCase("default");

                        if(hasPermission && !hasDiscordRole) {
                            verification.assignRole(roleAssignment.getRoleId());
                        } else if(!hasPermission && hasDiscordRole) {
                            verification.removeRole(roleAssignment.getRoleId());
                        }
                    }
                }
            });
        }
    }
}
