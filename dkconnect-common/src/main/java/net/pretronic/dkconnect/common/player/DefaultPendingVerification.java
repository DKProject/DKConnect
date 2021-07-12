package net.pretronic.dkconnect.common.player;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.verification.verify.VerifiedEvent;
import net.pretronic.dkconnect.api.event.verification.verify.VerifyEvent;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.common.DefaultDKConnect;
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
    public Verification complete(String userId, String username) {
        Validate.notNull(username);
        //@Todo Check for other verifications

        VerifyEvent event = new DefaultVerifyEvent(dkConnect, this.player, userId, username, this);
        this.dkConnect.getEventBus().callEvent(VerifyEvent.class, event);
        if(event.isCancelled()) return null;

        long time = System.currentTimeMillis();

        this.dkConnect.getStorage().getPlayerVerifications().insert()
                .set("PlayerId", this.player.getId())
                .set("VoiceAdapterName", getVoiceAdapter().getName())
                .set("Username", username)
                .set("UserId", userId)
                .set("Time", time)
                .execute();

        Verification verification = new DefaultVerification(dkConnect, player, voiceAdapter, userId, username, time);

        this.player.addVerification(verification);

        this.dkConnect.getStorage().getPlayerPendingVerifications().delete()
                .where("PlayerId", player.getId())
                .where("VoiceAdapterName", voiceAdapter.getName())
                .execute();

        this.player.removePendingVerification(this);

        this.dkConnect.getEventBus().callEvent(VerifiedEvent.class, new DefaultVerifiedEvent(dkConnect, player, verification));
        return verification;
    }
}
