package net.pretronic.dkconnect.api.event.verification.pending;

import net.pretronic.dkconnect.api.event.DKConnectEvent;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;

public interface PendingVerificationValidationCheckEvent extends DKConnectEvent {

    PendingVerification getVerification();

    VoiceAdapter getVoiceAdapter();

    boolean isValid();

    void setValid(boolean valid);
}
