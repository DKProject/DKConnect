package net.pretronic.dkconnect.api.event.verification.verify;

import net.pretronic.dkconnect.api.event.DKConnectEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.libraries.event.Cancellable;

public interface VerifyEvent extends DKConnectEvent, Cancellable {

    PendingVerification getPendingVerification();

    DKConnectPlayer getPlayer();

    String getUserId();

    String getUsername();
}
