package net.pretronic.dkconnect.api.voiceadapter;

import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.libraries.command.manager.CommandManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

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


    void sendMessage(Verification verification, Textable text, VariableSet variables);

    void sendMessage(String channelId, Language language, Textable text, VariableSet variables);

    void sendMessage(String channelId, Textable text, VariableSet variables);


    StaticMessage getStaticMessage(String name);

    CompletableFuture<StaticMessage> sendStaticMessage(String name, String channelId, Language language, Textable text, VariableSet variables);

    CompletableFuture<StaticMessage> sendStaticMessage(String name, String channelId, Textable text, VariableSet variables);


    CompletableFuture<String> createTextChannel(String categoryId, String name, String[] allowedRoles, String[] allowedUserIds);

    void deleteTextChannel(String channelId);


    void assignRole(Verification verification, String roleId);

    void removeRole(Verification verification, String roleId);

    CompletableFuture<Boolean> hasRole(Verification verification, String roleId);

    CompletableFuture<Collection<String>> getRoleIds(Verification verification);
}
