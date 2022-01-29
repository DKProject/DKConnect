package net.pretronic.dkconnect.voiceadapter.discord;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.ErrorResponse;
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

    public static CompletableFuture<Message> editMessage(MessageChannel channel, String messageId, Language language, Textable text, VariableSet variables) {
        if(text instanceof DiscordMessage) {
            return ((DiscordMessage)text).edit(channel, messageId, language, variables);
        } else {
            CompletableFuture<Message> future = new CompletableFuture<>();
            channel.editMessageById(messageId, text.toText(variables)).queue(future::complete, Throwable::printStackTrace);
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

    public static Category getCategory(Guild guild, String categoryId) {
        Category category = guild.getCategoryById(categoryId);
        if(category == null) throw new IllegalArgumentException("Can't retrieve category " + categoryId + " from guild " + guild.getId());
        return category;
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

    public static CompletableFuture<Message> getMessage(DiscordVoiceAdapter voiceAdapter, String channelId, String messageId) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        getGuild(voiceAdapter).getTextChannelById(channelId).retrieveMessageById(messageId).queue(future::complete, exception -> {
            if(ErrorResponse.UNKNOWN_MESSAGE.test(exception)) future.complete(null);/*Message does not exists*/
            else exception.printStackTrace();
        });
        return future;
    }
}
