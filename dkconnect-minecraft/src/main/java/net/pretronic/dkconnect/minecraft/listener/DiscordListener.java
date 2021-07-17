package net.pretronic.dkconnect.minecraft.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.minecraft.DKConnectPlugin;
import net.pretronic.dkconnect.minecraft.config.discord.ChatSync;
import net.pretronic.dkconnect.minecraft.config.discord.DiscordGuildConfig;
import net.pretronic.dkconnect.voiceadapter.discord.DiscordBotUtil;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.text.Text;

public class DiscordListener {

    private final DKConnectPlugin plugin;

    public DiscordListener(DKConnectPlugin plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onMessageReceive(GuildMessageReceivedEvent event) {
        if(event.getJDA().getSelfUser().equals(event.getAuthor())) return;
        for (DiscordGuildConfig guildConfig : plugin.getGuildConfigs()) {
            if(guildConfig.getGuildId() == event.getGuild().getIdLong()) {
                ChatSync chatSync = guildConfig.getChatSync();
                if(chatSync.isEnabled() && event.getChannel().getId().equals(chatSync.getDiscordChannelId())) {
                    VoiceAdapter voiceAdapter = plugin.getDKConnect().getVoiceAdapter(guildConfig.getVoiceAdapterName());
                    DKConnectPlayer player = plugin.getDKConnect().getPlayerManager().getPlayerByVerificationUserId(voiceAdapter, event.getAuthor().getId());
                    if(player == null) {
                        event.getMessage().delete().queue();
                        DiscordBotUtil.sendMessage(event.getChannel(), null, voiceAdapter.getMessage("dkconnect.voiceadapter.discord.notVerified"), VariableSet.create());
                        return;
                    }
                    McNative.getInstance().getLocal().broadcast(Text.ofMessageKey(chatSync.getMinecraftMessage()), VariableSet.create()
                            .addDescribed("event", event)
                            .addDescribed("player", player)
                            .add("message", event.getMessage().getContentRaw()));
                }
            }
        }
    }
}
