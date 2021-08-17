package net.pretronic.dkconnect.voiceadapter.discord;

import net.pretronic.dkconnect.api.voiceadapter.Emoji;
import net.pretronic.dkconnect.api.voiceadapter.Message;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapterUser;
import net.pretronic.dkconnect.voiceadapter.discord.channel.DiscordTextChannel;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.Validate;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DiscordMessage implements Message {

    private final DiscordVoiceAdapter voiceAdapter;
    private final DiscordTextChannel textChannel;
    private net.dv8tion.jda.api.entities.Message original;

    public DiscordMessage(DiscordVoiceAdapter voiceAdapter, DiscordTextChannel textChannel, net.dv8tion.jda.api.entities.Message original) {
        this.voiceAdapter = voiceAdapter;
        this.textChannel = textChannel;
        this.original = original;
    }

    @Override
    public String getId() {
        return original.getId();
    }

    @Override
    public VoiceAdapterUser getAuthor() {
        return new DiscordVoiceAdapterUser(voiceAdapter, original.getAuthor());
    }

    @Override
    public CompletableFuture<Message> edit(Language language, Textable text, VariableSet variables) {
        Validate.notNull(text);
        CompletableFuture<Message> future = new CompletableFuture<>();
        DiscordBotUtil.editMessage(original.getChannel(), original.getId(), language, text, variables).thenAccept(newMessage -> {
            this.original = newMessage;
            future.complete(this);
        });
        return future;
    }

    @Override
    public CompletableFuture<Message> edit(Textable text, VariableSet variables) {
        return edit(null, text, variables);
    }

    @Override
    public CompletableFuture<Boolean> addReaction(Emoji emoji) {
        Validate.isTrue(emoji instanceof DiscordEmoji);
        return ((DiscordEmoji)emoji).addReaction(this.original);
    }

    @Override
    public void onReactionAdd(Consumer<ReactionAddEvent> listener) {
        Validate.notNull(listener);
        this.textChannel.getMessageReactionAddListeners().put(this.original.getId(), listener);
    }

    @Override
    public CompletableFuture<Boolean> delete() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        original.delete().queue(unused -> future.complete(true), error -> future.complete(false));
        return future;
    }
}
