package net.pretronic.dkconnect.api.voiceadapter;

import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.api.voiceadapter.channel.TextChannel;
import net.pretronic.libraries.command.manager.CommandManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.annonations.Nullable;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface VoiceAdapter extends ObjectOwner {

    @Override
    String getName();

    String getVerificationSystemName();

    String getType();

    DKConnect getDKConnect();

    CommandManager getCommandManager();

    EventBus getEventBus();

    Textable getMessage(String key);

    void importMessage(String key, InputStream inputStream);

    Emoji parseEmoji(String value);


    CompletableFuture<Message> sendPrivateMessage(Verification verification, Textable text, VariableSet variables);


    CompletableFuture<TextChannel> createTextChannel(@Nullable String categoryId, String name, String[] allowedRoles, String[] allowedUserIds);

    TextChannel getTextChannel(String id);


    void assignRole(Verification verification, String roleId);

    void removeRole(Verification verification, String roleId);

    CompletableFuture<Boolean> hasRole(Verification verification, String roleId);

    CompletableFuture<Collection<String>> getRoleIds(Verification verification);
}
