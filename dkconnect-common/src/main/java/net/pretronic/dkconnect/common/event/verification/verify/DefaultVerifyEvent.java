package net.pretronic.dkconnect.common.event.verification.verify;

import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.verification.verify.VerifyEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.libraries.event.injection.annotations.Inject;

import java.util.UUID;

public class DefaultVerifyEvent implements VerifyEvent {

    @Inject
    private final transient DKConnect dkConnect;

    private final UUID playerId;
    private transient DKConnectPlayer player;

    private final String userId;
    private final String username;

    private transient PendingVerification pendingVerification;
    private final String voiceAdapterName;

    private transient boolean cancelled;

    public DefaultVerifyEvent(DKConnect dkConnect, DKConnectPlayer player, String userId, String username, PendingVerification pendingVerification) {
        this.dkConnect = dkConnect;
        this.player = player;
        this.playerId = player.getId();
        this.userId = userId;
        this.username = username;
        this.pendingVerification = pendingVerification;
        this.voiceAdapterName = pendingVerification.getVoiceAdapter().getName();
    }

    @Override
    public PendingVerification getPendingVerification() {
        if(this.pendingVerification == null) {
            VoiceAdapter voiceAdapter = this.dkConnect.getVoiceAdapter(this.voiceAdapterName);
            this.pendingVerification = getPlayer().getPendingVerification(voiceAdapter);
        }
        return pendingVerification;
    }

    @Override
    public DKConnectPlayer getPlayer() {
        if(this.player == null) {
            this.player = this.dkConnect.getPlayerManager().getPlayer(this.playerId);
        }
        return this.player;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }

    @Override
    public String getUsername() {
        return this.username;
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
