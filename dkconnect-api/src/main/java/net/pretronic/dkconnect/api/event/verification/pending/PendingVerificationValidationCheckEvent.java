package net.pretronic.dkconnect.api.event.verification.pending;

import net.pretronic.dkconnect.api.event.DKConnectEvent;
import net.pretronic.dkconnect.api.player.PendingVerification;

public interface PendingVerificationValidationCheckEvent extends DKConnectEvent {

    PendingVerification getVerification();

    boolean isValid();

    void setValid(boolean valid);
}
