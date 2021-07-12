package net.pretronic.dkconnect.common.event.verification.unverify;

import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.verification.unverify.UnverifyEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.libraries.event.injection.annotations.Inject;

import java.util.UUID;

public class DefaultUnverifyEvent implements UnverifyEvent {

    @Inject
    private final transient DKConnect dkConnect;

    private final UUID playerId;
    private transient DKConnectPlayer player;

    private transient Verification verification;
    private final String voiceAdapterName;

    private transient boolean cancelled;

    public DefaultUnverifyEvent(DKConnect dkConnect, DKConnectPlayer player, Verification verification) {
        this.dkConnect = dkConnect;
        this.player = player;
        this.playerId = player.getId();
        this.verification = verification;
        this.voiceAdapterName = verification.getVoiceAdapter().getName();
    }

    @Override
    public DKConnectPlayer getPlayer() {
        if(this.player == null) {
            this.player = this.dkConnect.getPlayerManager().getPlayer(this.playerId);
        }
        return this.player;
    }

    @Override
    public Verification getVerification() {
        if(this.verification == null) {
            VoiceAdapter voiceAdapter = this.dkConnect.getVoiceAdapter(voiceAdapterName);
            this.verification = getPlayer().getVerification(voiceAdapter);
        }
        return verification;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
