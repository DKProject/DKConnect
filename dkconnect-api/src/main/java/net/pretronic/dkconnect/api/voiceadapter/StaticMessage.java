package net.pretronic.dkconnect.api.voiceadapter;

import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;

public interface StaticMessage {

    VoiceAdapter getVoiceAdapter();

    String getName();

    String getChannelId();

    String getMessageId();

    void update(Textable text, Language language, VariableSet variables);

    void update(Textable text, VariableSet variables);
}
