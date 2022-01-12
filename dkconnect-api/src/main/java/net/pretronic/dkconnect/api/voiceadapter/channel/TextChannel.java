package net.pretronic.dkconnect.api.voiceadapter.channel;

import net.pretronic.dkconnect.api.voiceadapter.Message;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface TextChannel extends Channel {

    CompletableFuture<Message> getMessage(String id);

    CompletableFuture<Message> sendMessage(Language language, Textable text, VariableSet variables);

    CompletableFuture<Message> sendMessage(Textable text, VariableSet variables);

    void onMessageReceive(Consumer<Message> listener);

    CompletableFuture<Boolean> delete();
}
