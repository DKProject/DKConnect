package net.pretronic.dkconnect.minecraft.config.discord;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.minecraft.DKConnectPlugin;
import net.pretronic.dkconnect.minecraft.config.RoleAssignment;
import net.pretronic.libraries.message.StringTextable;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerPostLoginEvent;

import java.util.Arrays;
import java.util.Collection;

public class DiscordGuildConfig {

    /*
    voiceAdapterName: 'discord'
guildId: 771103131777892353
roleAssignments:
  - permission: 'role.admin'
    roleId: 861021664514539530
  - permission: 'role.mod'
    roleId: 861021736097808394
messageTriggers:
  - eventClass: 'org.mcnative.runtime.api.event.player.login.MinecraftPlayerPostLoginEvent'
    channelId: 861354080693125171
    message: '{event.player.name} has joined the server'
     */

    private String voiceAdapterName = "discord";
    private long guildId = 1234;
    private Collection<RoleAssignment> roleAssignments = Arrays.asList(new RoleAssignment("role.admin", "1234"),
            new RoleAssignment("role.mod", "4321"));
    private Collection<MinecraftEventMessageTrigger> minecraftEventMessageTriggers = Arrays.asList(
            new MinecraftEventMessageTrigger(MinecraftPlayerPostLoginEvent.class.getName(), "1234", "{event.player.name} has joined the server", null),
            new MinecraftEventMessageTrigger("net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishEvent", "1234", null, "dkconnect.voiceadapter.discord.notification.ban"));
    private ChatSync chatSync = new ChatSync(true, "1234", null, "dkconnect.voiceadapter.discord.syncChat", "${dkconnect.chatSync.message}");


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
        if(getMinecraftEventMessageTriggers() != null) {
            for (MinecraftEventMessageTrigger messageTrigger : getMinecraftEventMessageTriggers()) {

                McNative.getInstance().getLocal().getEventBus().subscribe(plugin, messageTrigger.getEventClass(), event -> {
                    if(VariableDescriberRegistry.getDescriber(event.getClass()) == null) {
                        VariableDescriberRegistry.registerDescriber(event.getClass());//@Todo optimize
                    }
                    VoiceAdapter adapter = plugin.getDKConnect().getVoiceAdapter(voiceAdapterName);

                    Textable text = messageTrigger.getEmbedKey() != null ? adapter.getMessage(messageTrigger.getEmbedKey()) : new StringTextable(messageTrigger.getMessage());

                    adapter.sendMessage(messageTrigger.getChannelId(), text, VariableSet.create().addDescribed("event", event));
                });
            }
        }
    }
}
