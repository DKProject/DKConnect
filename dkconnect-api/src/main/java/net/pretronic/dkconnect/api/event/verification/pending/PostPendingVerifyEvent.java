package net.pretronic.dkconnect.api.event.verification.pending;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.DKConnectEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;

public interface PostPendingVerifyEvent extends DKConnectEvent {

    DKConnectPlayer getPlayer();

    VoiceAdapter getVoiceAdapter();
}
