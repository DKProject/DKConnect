package net.pretronic.dkconnect.voiceadapter.discord.channel;

import net.pretronic.dkconnect.api.voiceadapter.Message;
import net.pretronic.dkconnect.api.voiceadapter.channel.TextChannel;
import net.pretronic.dkconnect.voiceadapter.discord.DiscordBotUtil;
import net.pretronic.dkconnect.voiceadapter.discord.DiscordMessage;
import net.pretronic.dkconnect.voiceadapter.discord.DiscordVoiceAdapter;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DiscordTextChannel implements TextChannel {

    private final DiscordVoiceAdapter voiceAdapter;
    private final net.dv8tion.jda.api.entities.MessageChannel original;

    private final Collection<Consumer<Message>> messageReceiveListeners;
    private final Map<String, Consumer<Message.ReactionAddEvent>> messageReactionAddListeners;

    public DiscordTextChannel(DiscordVoiceAdapter voiceAdapter, net.dv8tion.jda.api.entities.MessageChannel original) {
        Validate.notNull(voiceAdapter, original);
        this.voiceAdapter = voiceAdapter;
        this.original = original;
        this.messageReceiveListeners = new ArrayList<>();
        this.messageReactionAddListeners = new ConcurrentHashMap<>();
    }

    @Override
    public String getId() {
        return original.getId();
    }

    @Override
    public CompletableFuture<Message> getMessage(String id) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        DiscordBotUtil.getMessage(this.voiceAdapter, this.original.getId(), id).thenAccept(message -> future.complete(new DiscordMessage(voiceAdapter, this, message)));
        return future;
    }

    @Override
    public CompletableFuture<Message> sendMessage(Language language, Textable text, VariableSet variables) {
        Validate.notNull(text);
        CompletableFuture<Message> future = new CompletableFuture<>();
        DiscordBotUtil.sendMessage(original, null, text, variables).thenAccept(message -> future.complete(new DiscordMessage(voiceAdapter, this, message)));
        return future;
    }

    @Override
    public CompletableFuture<Message> sendMessage(Textable text, VariableSet variables) {
        return sendMessage(null, text, variables);
    }

    @Override
    public void onMessageReceive(Consumer<Message> listener) {
        Validate.notNull(listener);
        this.messageReceiveListeners.add(listener);
    }

    @Override
    public CompletableFuture<Boolean> delete() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(original instanceof net.dv8tion.jda.api.entities.TextChannel) {
            ((net.dv8tion.jda.api.entities.TextChannel) original).delete().queue(unused -> future.complete(true), error -> future.complete(false));
        } else {
            future.complete(false);
        }
        return future;
    }

    public Collection<Consumer<Message>> getMessageReceiveListeners() {
        return messageReceiveListeners;
    }

    public Map<String, Consumer<Message.ReactionAddEvent>> getMessageReactionAddListeners() {
        return messageReactionAddListeners;
    }
}
