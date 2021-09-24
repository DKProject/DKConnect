package net.pretronic.dkconnect.common.player;

import net.pretronic.dkconnect.api.event.verification.pending.PendingVerificationValidationCheckEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.verification.verify.VerifiedEvent;
import net.pretronic.dkconnect.api.event.verification.verify.VerifyEvent;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.common.DefaultDKConnect;
import net.pretronic.dkconnect.common.event.verification.pending.DefaultPendingVerificationValidationCheckEvent;
import net.pretronic.dkconnect.common.event.verification.verify.DefaultVerifiedEvent;
import net.pretronic.dkconnect.common.event.verification.verify.DefaultVerifyEvent;
import net.pretronic.libraries.utility.Validate;

public class DefaultPendingVerification implements PendingVerification {

    private final DefaultDKConnect dkConnect;

    private final DefaultDKConnectPlayer player;
    private final VoiceAdapter voiceAdapter;
    private final String code;
    private final long time;

    public DefaultPendingVerification(DefaultDKConnect dkConnect, DefaultDKConnectPlayer player, VoiceAdapter voiceAdapter, String code, long time) {
        Validate.notNull(dkConnect, player, voiceAdapter, code);
        this.player = player;
        this.dkConnect = dkConnect;
        this.time = time;
        this.voiceAdapter = voiceAdapter;
        this.code = code;
    }


    @Override
    public VoiceAdapter getVoiceAdapter() {
        return this.voiceAdapter;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public boolean isValid() {
        PendingVerificationValidationCheckEvent event = new DefaultPendingVerificationValidationCheckEvent(this.dkConnect, player, voiceAdapter, this);
        this.dkConnect.getEventBus().callEvent(PendingVerificationValidationCheckEvent.class, event);
        return event.isValid();
    }

    @Override
    public Verification complete(String userId, String username) {
        checkValid();
        Validate.notNull(username);

        DKConnectPlayer alreadyVerifiedPlayer = this.dkConnect.getPlayerManager().getPlayerByVerificationUserId(voiceAdapter, userId);

        VerifyEvent event = new DefaultVerifyEvent(dkConnect, this.player, userId, username, this);
        this.dkConnect.getEventBus().callEvent(VerifyEvent.class, event);
        if(event.isCancelled()) return null;

        long time = System.currentTimeMillis();

        this.dkConnect.getStorage().getPlayerVerifications().insert()
                .set("PlayerId", this.player.getId())
                .set("VoiceAdapterName", getVoiceAdapter().getVerificationSystemName())
                .set("Username", username)
                .set("UserId", userId)
                .set("Time", time)
                .execute();

        Verification verification = new DefaultVerification(dkConnect, player, voiceAdapter, userId, username, time);

        this.player.addVerification(verification);

        delete();

        if(alreadyVerifiedPlayer != null) {
            alreadyVerifiedPlayer.getVerification(this.voiceAdapter).unverify();
        }

        this.dkConnect.getEventBus().callEvent(VerifiedEvent.class, new DefaultVerifiedEvent(dkConnect, player, verification));
        return verification;
    }

    @Override
    public void delete() {
        this.dkConnect.getStorage().getPlayerPendingVerifications().delete()
                .where("PlayerId", player.getId())
                .where("VoiceAdapterName", voiceAdapter.getVerificationSystemName())
                .execute();
        this.player.removePendingVerification(this);
    }

    private void checkValid() {
        if(!isValid()) throw new IllegalArgumentException("PendingVerification for player " + player.getId() + " for voice adapter " + voiceAdapter.getName() + " is invalid");
    }
}
