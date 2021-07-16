package net.pretronic.dkconnect.common.player;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.verification.unverify.UnverifiedEvent;
import net.pretronic.dkconnect.api.event.verification.unverify.UnverifyEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.common.DefaultDKConnect;
import net.pretronic.dkconnect.common.event.verification.unverify.DefaultUnverifiedEvent;
import net.pretronic.dkconnect.common.event.verification.unverify.DefaultUnverifyEvent;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Validate;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class DefaultVerification implements Verification {

    private final DefaultDKConnect dkConnect;

    private final DefaultDKConnectPlayer player;
    private final VoiceAdapter voiceAdapter;
    private final String userId;
    private final String username;
    private final long time;

    public DefaultVerification(DefaultDKConnect dkConnect, DefaultDKConnectPlayer player, VoiceAdapter voiceAdapter, String userId, String username, long time) {
        Validate.notNull(voiceAdapter);
        Validate.notNull(userId);
        Validate.notNull(username);
        Validate.notNull(dkConnect, player);
        this.dkConnect = dkConnect;
        this.player = player;
        this.voiceAdapter = voiceAdapter;
        this.userId = userId;
        this.username = username;
        this.time = time;
    }

    @Override
    public DKConnectPlayer getPlayer() {
        return this.player;
    }

    @Override
    public VoiceAdapter getVoiceAdapter() {
        return this.voiceAdapter;
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
    public void setUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public boolean unverify() {
        UnverifyEvent event = new DefaultUnverifyEvent(dkConnect, getPlayer(), this);
        this.dkConnect.getEventBus().callEvent(UnverifyEvent.class, event);
        if(event.isCancelled()) return false;

        this.dkConnect.getStorage().getPlayerVerifications().delete()
                .where("PlayerId", player.getId())
                .where("VoiceAdapterName", voiceAdapter.getVerificationSystemName())
                .where("UserId", userId)
                .execute();
        player.removeVerification(this);

        this.dkConnect.getEventBus().callEvent(UnverifiedEvent.class, new DefaultUnverifiedEvent(dkConnect, getPlayer(), this));
        return true;
    }

    @Override
    public void sendMessage(Textable text) {
        getVoiceAdapter().sendMessage(this, text, VariableSet.create());
    }

    @Override
    public void assignRole(String roleId) {
        getVoiceAdapter().assignRole(this, roleId);
    }

    @Override
    public void removeRole(String roleId) {
        getVoiceAdapter().removeRole(this, roleId);
    }

    @Override
    public CompletableFuture<Boolean> hasRole(String roleId) {
        return getVoiceAdapter().hasRole(this, roleId);
    }

    @Override
    public CompletableFuture<Collection<String>> getRoleIds() {
        return getVoiceAdapter().getRoleIds(this);
    }
}
