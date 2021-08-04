package net.pretronic.dkconnect.minecraft.listener;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.minecraft.DKConnectPlugin;
import net.pretronic.dkconnect.minecraft.Util;
import net.pretronic.dkconnect.minecraft.config.discord.ChatSync;
import net.pretronic.dkconnect.minecraft.config.discord.DiscordGuildConfig;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.message.StringTextable;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.event.player.MinecraftPlayerChatEvent;
import org.mcnative.runtime.api.event.player.MinecraftPlayerLogoutEvent;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.runtime.api.player.MinecraftPlayer;

public class PlayerListener {

    private final DKConnectPlugin dkConnectPlugin;

    public PlayerListener(DKConnectPlugin dkConnectPlugin) {
        this.dkConnectPlugin = dkConnectPlugin;
    }

    @Listener
    public void onLogin(MinecraftPlayerPostLoginEvent event) {
        updateDiscordRoles(event.getPlayer());
    }

    @Listener
    public void onLogout(MinecraftPlayerLogoutEvent event) {
        updateDiscordRoles(event.getPlayer());
    }

    private void updateDiscordRoles(MinecraftPlayer minecraftPlayer) {
        DKConnectPlayer player = minecraftPlayer.getAs(DKConnectPlayer.class);

        for (Verification verification : player.getVerifications()) {
            Util.updateDiscordRoles(dkConnectPlugin, verification, minecraftPlayer);
        }
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onChat(MinecraftPlayerChatEvent event) {
        if(event.isCancelled()) return;
        for (DiscordGuildConfig guildConfig : dkConnectPlugin.getGuildConfigs()) {

            VoiceAdapter voiceAdapter = dkConnectPlugin.getDKConnect().getVoiceAdapter(guildConfig.getVoiceAdapterName());
            DKConnectPlayer player = event.getPlayer().getAs(DKConnectPlayer.class);
            ChatSync chatSync = guildConfig.getChatSync();
            if(chatSync.isEnabled()) {
                Textable text = chatSync.getDiscordEmbedKey() != null ? voiceAdapter.getMessage(chatSync.getDiscordEmbedKey()) : new StringTextable(chatSync.getDiscordMessage());
                voiceAdapter.sendMessage(chatSync.getDiscordChannelId(), text, VariableSet.create()
                        .addDescribed("player", player)
                        .add("message", event.getMessage()));
            }

        }
    }

}
