package net.pretronic.dkconnect.api.voiceadapter;

import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.Verification;

public interface VoiceAdapterUser {

    String getId();

    String getName();

    VoiceAdapter getVoiceAdapter();

    DKConnectPlayer getPlayer();

    Verification getVerification();

    boolean isBot();

    boolean isSystem();
}
