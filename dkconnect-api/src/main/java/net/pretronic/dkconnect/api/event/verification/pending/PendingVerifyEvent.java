package net.pretronic.dkconnect.api.event.verification.pending;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.DKConnectEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.libraries.event.Cancellable;

public interface PendingVerifyEvent extends DKConnectEvent, Cancellable {

    DKConnectPlayer getPlayer();

    VoiceAdapter getVoiceAdapter();
}
