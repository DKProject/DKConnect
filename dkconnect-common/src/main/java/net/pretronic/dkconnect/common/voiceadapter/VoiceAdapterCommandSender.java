package net.pretronic.dkconnect.common.voiceadapter;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;

public abstract class VoiceAdapterCommandSender<T extends VoiceAdapter> implements CommandSender {

    protected final T voiceAdapter;

    public VoiceAdapterCommandSender(T voiceAdapter) {
        this.voiceAdapter = voiceAdapter;
    }

    public abstract String getId();

    @Override
    public boolean hasPermission(String permission) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void sendMessage(Textable textable) {
        sendMessage(textable, VariableSet.create());
    }

    public void sendMessageKey(String messageKey, VariableSet variables) {
        sendMessage(this.voiceAdapter.getMessage(messageKey), variables);
    }

    public void sendMessageKey(String messageKey) {
        sendMessage(this.voiceAdapter.getMessage(messageKey), VariableSet.create());
    }

    public DKConnectPlayer getDKConnectPlayer() {
        return this.voiceAdapter.getDKConnect().getPlayerManager().getPlayerByVerificationUsername(voiceAdapter, getName());
    }
}
