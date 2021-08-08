package net.pretronic.dkconnect.common.event.verification.pending;

import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.event.verification.pending.PendingVerificationValidationCheckEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.libraries.event.injection.annotations.Inject;

import java.util.UUID;

public class DefaultPendingVerificationValidationCheckEvent implements PendingVerificationValidationCheckEvent {

    @Inject
    private final transient DKConnect dkConnect;

    private final UUID playerId;
    private transient DKConnectPlayer player;

    private final String voiceAdapterName;
    private transient VoiceAdapter voiceAdapter;

    private transient PendingVerification pendingVerification;

    private boolean valid;

    public DefaultPendingVerificationValidationCheckEvent(DKConnect dkConnect, DKConnectPlayer player, VoiceAdapter voiceAdapter, PendingVerification pendingVerification) {
        this.dkConnect = dkConnect;
        this.player = player;
        this.pendingVerification = pendingVerification;
        this.voiceAdapter = voiceAdapter;

        this.valid = true;
        this.playerId = player.getId();
        this.voiceAdapterName = voiceAdapter.getName();
    }

    @Override
    public PendingVerification getVerification() {
        if(this.pendingVerification == null) {
            VoiceAdapter voiceAdapter = getVoiceAdapter();
            DKConnectPlayer player = getPlayer();
            this.pendingVerification = player.getPendingVerification(voiceAdapter);
        }
        return this.pendingVerification;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public VoiceAdapter getVoiceAdapter() {
        if(this.voiceAdapter == null) {
            this.voiceAdapter = this.dkConnect.getVoiceAdapter(voiceAdapterName);
        }
        return voiceAdapter;
    }

    private DKConnectPlayer getPlayer() {
        if(this.player == null) {
            this.player = this.dkConnect.getPlayerManager().getPlayer(this.playerId);
        }
        return this.player;
    }
}
