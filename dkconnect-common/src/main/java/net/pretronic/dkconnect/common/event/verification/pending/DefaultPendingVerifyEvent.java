package net.pretronic.dkconnect.common.event.verification.pending;

import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.verification.pending.PendingVerifyEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.libraries.event.injection.annotations.Inject;

import java.util.UUID;

public class DefaultPendingVerifyEvent implements PendingVerifyEvent {

    @Inject
    private final transient DKConnect dkConnect;

    private final UUID playerId;
    private transient DKConnectPlayer player;

    private final String voiceAdapterName;
    private transient VoiceAdapter voiceAdapter;

    private transient boolean cancelled;

    public DefaultPendingVerifyEvent(DKConnect dkConnect, DKConnectPlayer player, VoiceAdapter voiceAdapter) {
        this.dkConnect = dkConnect;
        this.player = player;
        this.voiceAdapter = voiceAdapter;
        this.playerId = player.getId();
        this.voiceAdapterName = voiceAdapter.getName();
    }

    @Override
    public DKConnectPlayer getPlayer() {
        if(this.player == null) {
            this.player = this.dkConnect.getPlayerManager().getPlayer(this.playerId);
        }
        return this.player;
    }

    @Override
    public VoiceAdapter getVoiceAdapter() {
        if(this.voiceAdapter == null) {
            this.voiceAdapter = this.dkConnect.getVoiceAdapter(this.voiceAdapterName);
        }
        return this.voiceAdapter;
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
