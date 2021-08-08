package net.pretronic.dkconnect.api.player;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;

public interface PendingVerification {

    VoiceAdapter getVoiceAdapter();

    String getCode();

    long getTime();

    boolean isValid();

    Verification complete(String userId, String username);
}
