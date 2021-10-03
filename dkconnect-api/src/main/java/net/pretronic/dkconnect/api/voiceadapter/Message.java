package net.pretronic.dkconnect.api.voiceadapter;

import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Message {

    String getId();

    VoiceAdapterUser getAuthor();

    CompletableFuture<Message> edit(Language language, Textable text, VariableSet variables);

    CompletableFuture<Message> edit(Textable text, VariableSet variables);

    CompletableFuture<Boolean> addReaction(Emoji emoji);

    void onReactionAdd(Consumer<ReactionAddEvent> listener);

    CompletableFuture<Boolean> delete();

    interface ReactionAddEvent {

        VoiceAdapterUser getUser();

        Emoji getEmoji();

        CompletableFuture<Boolean> removeReaction();
    }
}
