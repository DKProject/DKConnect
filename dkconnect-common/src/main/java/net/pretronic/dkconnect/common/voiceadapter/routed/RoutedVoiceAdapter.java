package net.pretronic.dkconnect.common.voiceadapter.routed;

import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.voiceadapter.StaticMessage;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.common.DefaultDKConnect;
import net.pretronic.libraries.command.manager.CommandManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;

import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RoutedVoiceAdapter implements VoiceAdapter {

    private final DefaultDKConnect dkConnect;
    private final String type;

    public RoutedVoiceAdapter(DefaultDKConnect dkConnect, String type) {
        this.dkConnect = dkConnect;
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public DKConnect getDKConnect() {
        return this.dkConnect;
    }

    @Override
    public CommandManager getCommandManager() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public EventBus getEventBus() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Textable getMessage(String key) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void importMessage(String key, InputStream inputStream) {

    }

    @Override
    public void sendMessage(Verification verification, Textable text, VariableSet variables) {

    }

    @Override
    public void sendMessage(String channelId, Language language, Textable text, VariableSet variables) {

    }

    @Override
    public void sendMessage(String channelId, Textable text, VariableSet variables) {

    }

    @Override
    public StaticMessage getStaticMessage(String name) {
        return null;
    }

    @Override
    public CompletableFuture<StaticMessage> sendStaticMessage(String name, String channelId, Language language, Textable text, VariableSet variables) {
        return null;
    }

    @Override
    public CompletableFuture<StaticMessage> sendStaticMessage(String name, String channelId, Textable text, VariableSet variables) {
        return null;
    }

    @Override
    public CompletableFuture<String> createTextChannel(String categoryId, String name, String[] allowedRoles, String[] allowedUserIds) {
        return null;
    }

    @Override
    public void deleteTextChannel(String channelId) {

    }

    @Override
    public void assignRole(Verification verification, String roleId) {

    }

    @Override
    public void removeRole(Verification verification, String roleId) {

    }

    @Override
    public CompletableFuture<Boolean> hasRole(Verification verification, String roleId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getRoleIds(verification).thenAccept(roleIds -> future.complete(roleIds.contains(roleId)));
        return future;
    }

    @Override
    public CompletableFuture<Collection<String>> getRoleIds(Verification verification) {
        return null;
    }

    @Override
    public String getName() {
        return this.type;
    }

    @Override
    public String getVerificationSystemName() {
        return null;
    }


}
