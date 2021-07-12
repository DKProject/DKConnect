package net.pretronic.dkconnect.api.event.verification.unverify;

import net.pretronic.dkconnect.api.event.DKConnectEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.libraries.event.Cancellable;

public interface UnverifiedEvent extends DKConnectEvent {

    DKConnectPlayer getPlayer();

    Verification getVerification();
}
