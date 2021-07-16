package net.pretronic.dkconnect.voiceadapter.discord;

import net.dv8tion.jda.api.entities.*;
import net.pretronic.dkconnect.voiceadapter.discord.message.DiscordMessage;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;

import java.util.concurrent.CompletableFuture;

public class DiscordBotUtil {

    public static CompletableFuture<Message> sendMessage(MessageChannel channel, Language language, Textable text, VariableSet variables) {
        if(text instanceof DiscordMessage) {
            return ((DiscordMessage)text).send(channel, language, variables);
        } else {
            CompletableFuture<Message> future = new CompletableFuture<>();
            channel.sendMessage(text.toText(variables)).queue(future::complete, Throwable::printStackTrace);
            return future;
        }
    }

    public static CompletableFuture<Member> getMember(DiscordVoiceAdapter voiceAdapter, String userId) {
        CompletableFuture<Member> future = new CompletableFuture<>();
        getGuild(voiceAdapter).retrieveMemberById(userId).queue(future::complete, Throwable::printStackTrace);
        return future;
    }

    public static Role getRole(Guild guild, String roleId) {
        Role role = guild.getRoleById(roleId);
        if(role == null) throw new IllegalArgumentException("Can't retrieve role " + roleId + " from guild " + guild.getId());
        return role;
    }

    public static Guild getGuild(DiscordVoiceAdapter voiceAdapter) {
        Guild guild = voiceAdapter.getJda().getGuildById(voiceAdapter.getGuildId());
        if(guild == null) throw new IllegalArgumentException("Can't retrieve guild " + voiceAdapter.getGuildId());
        return guild;
    }

    public static TextChannel getTextChannel(DiscordVoiceAdapter voiceAdapter, String channelId) {
        TextChannel channel = getGuild(voiceAdapter).getTextChannelById(channelId);
        if(channel == null) throw new IllegalArgumentException("Can't retrieve text channel " + channelId + " frin guild " + voiceAdapter.getGuildId());
        return channel;
    }
}
