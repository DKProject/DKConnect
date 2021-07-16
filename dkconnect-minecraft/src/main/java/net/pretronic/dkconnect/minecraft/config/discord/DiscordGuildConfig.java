package net.pretronic.dkconnect.minecraft.config.discord;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.minecraft.DKConnectPlugin;
import net.pretronic.dkconnect.minecraft.config.RoleAssignment;
import net.pretronic.libraries.message.StringTextable;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.McNative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DiscordGuildConfig {

    private String voiceAdapterName = "discord-1";
    private long guildId = 1234;
    private Collection<RoleAssignment> roleAssignments = Arrays.asList(
            new RoleAssignment("default", "1234"),
            new RoleAssignment("role.admin", "1234"),
            new RoleAssignment("role.mod", "4321"));
    private Collection<VoiceAdapterPack> packs = Arrays.asList(new VoiceAdapterPack("dkbans-notification-punishment", "1234"),
            new VoiceAdapterPack("join", "1234"),
            new VoiceAdapterPack("leave", "1234"));
    private ChatSync chatSync = new ChatSync(true, "1234", null, "dkconnect.voiceadapter.discord.syncChat", "${dkconnect.chatSync.message}");

    private transient Collection<MinecraftEventMessageTrigger> minecraftEventMessageTriggers;

    public String getVoiceAdapterName() {
        return voiceAdapterName;
    }

    public long getGuildId() {
        return guildId;
    }

    public Collection<RoleAssignment> getRoleAssignments() {
        return roleAssignments;
    }

    public Collection<MinecraftEventMessageTrigger> getMinecraftEventMessageTriggers() {
        return minecraftEventMessageTriggers;
    }

    public ChatSync getChatSync() {
        return chatSync;
    }

    public void init(DKConnectPlugin plugin) {
        minecraftEventMessageTriggers = new ArrayList<>();
        if(packs != null) {
            for (VoiceAdapterPack pack : packs) {
                minecraftEventMessageTriggers.add(pack.toTrigger());
            }
        }
        if(getMinecraftEventMessageTriggers() != null) {
            for (MinecraftEventMessageTrigger messageTrigger : getMinecraftEventMessageTriggers()) {

                McNative.getInstance().getLocal().getEventBus().subscribe(plugin, messageTrigger.getEventClass(), event -> {
                    VoiceAdapter adapter = plugin.getDKConnect().getVoiceAdapter(voiceAdapterName);

                    Textable text = messageTrigger.getEmbedKey() != null ? adapter.getMessage(messageTrigger.getEmbedKey()) : new StringTextable(messageTrigger.getMessage());

                    adapter.sendMessage(messageTrigger.getChannelId(), text, VariableSet.create().addDescribed("event", event));
                });
            }
        }
    }
}
