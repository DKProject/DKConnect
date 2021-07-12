package net.pretronic.dkconnect.api.player;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;

public interface PendingVerification {

    VoiceAdapter getVoiceAdapter();

    String getCode();

    long getTime();

    Verification complete(String userId, String username);
}
