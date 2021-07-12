package net.pretronic.dkconnect.api.event.verification.verify;

import net.pretronic.dkconnect.api.event.DKConnectEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.Verification;

public interface VerifiedEvent extends DKConnectEvent {

    DKConnectPlayer getPlayer();

    Verification getVerification();
}
