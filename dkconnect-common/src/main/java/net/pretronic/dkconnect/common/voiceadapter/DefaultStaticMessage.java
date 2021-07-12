package net.pretronic.dkconnect.common.voiceadapter;

import net.pretronic.dkconnect.api.voiceadapter.StaticMessage;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;

public abstract class DefaultStaticMessage<T extends VoiceAdapter> implements StaticMessage {

    private final T voiceAdapter;
    private final String name;
    private final String channelId;
    private final String messageId;

    protected DefaultStaticMessage(T voiceAdapter, String name, String channelId, String messageId) {
        this.voiceAdapter = voiceAdapter;
        this.name = name;
        this.channelId = channelId;
        this.messageId = messageId;
    }

    @Override
    public T getVoiceAdapter() {
        return this.voiceAdapter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getChannelId() {
        return this.channelId;
    }

    @Override
    public String getMessageId() {
        return this.messageId;
    }

    @Override
    public void update(Textable text, VariableSet variables) {
        update(text, null, variables);
    }
}
