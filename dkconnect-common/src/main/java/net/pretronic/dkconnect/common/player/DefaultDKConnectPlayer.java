package net.pretronic.dkconnect.common.player;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.event.verification.pending.PendingVerifyEvent;
import net.pretronic.dkconnect.api.event.verification.pending.PostPendingVerifyEvent;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.common.DefaultDKConnect;
import net.pretronic.dkconnect.common.event.verification.pending.DefaultPendingVerifyEvent;
import net.pretronic.dkconnect.common.event.verification.pending.DefaultPostPendingVerifyEvent;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultDKConnectPlayer implements DKConnectPlayer {

    private final DefaultDKConnect dkConnect;

    private final UUID id;
    private Collection<Verification> verifications;
    private Collection<PendingVerification> pendingVerifications;

    public DefaultDKConnectPlayer(DefaultDKConnect dkConnect, UUID id) {
        this.dkConnect = dkConnect;
        this.id = id;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public Language getLanguage() {
        return dkConnect.getLanguageGetter().apply(getId());
    }

    @Override
    public Collection<Verification> getVerifications() {
        return Collections.unmodifiableCollection(getOrLoadVerifications());
    }

    @Override
    public Verification getVerification(VoiceAdapter voiceAdapter) {
        Validate.notNull(voiceAdapter);
        return Iterators.findOne(getOrLoadVerifications(), verification -> verification.getVoiceAdapter().equals(voiceAdapter));
    }

    @Override
    public boolean isVerified(VoiceAdapter voiceAdapter) {
        return getVerification(voiceAdapter) != null;
    }

    @Override
    public Collection<PendingVerification> getPendingVerifications() {
        return Collections.unmodifiableCollection(getOrLoadPendingVerifications());
    }

    @Override
    public PendingVerification getPendingVerification(VoiceAdapter voiceAdapter) {
        Validate.notNull(voiceAdapter);
        PendingVerification pendingVerification = Iterators.findOne(getOrLoadPendingVerifications(), pendingVerification0 -> pendingVerification0.getVoiceAdapter().equals(voiceAdapter));
        System.out.println(pendingVerification);
        if(pendingVerification != null) System.out.println(pendingVerification.isValid());
        if(pendingVerification != null && !pendingVerification.isValid()) {

            pendingVerification.delete();
            return null;
        }
        return pendingVerification;
    }

    @Override
    public PendingVerification verify(VoiceAdapter voiceAdapter) {
        Validate.notNull(voiceAdapter);
        if(getPendingVerification(voiceAdapter) != null) throw new IllegalArgumentException("Already open pending verification for user " + getId() + " and voice adapter " + voiceAdapter.getName());

        PendingVerifyEvent event = new DefaultPendingVerifyEvent(dkConnect, this, voiceAdapter);
        this.dkConnect.getEventBus().callEvent(PendingVerifyEvent.class, event);
        if(event.isCancelled()) return null;

        String code =  StringUtil.getRandomString(8);
        long time = System.currentTimeMillis();

        this.dkConnect.getStorage().getPlayerPendingVerifications().insert()
                .set("PlayerId", getId())
                .set("VoiceAdapterName", voiceAdapter.getVerificationSystemName())
                .set("Code", code)
                .set("Time", time)
                .execute();

        DefaultPendingVerification pendingVerification = new DefaultPendingVerification(dkConnect, this, voiceAdapter, code, time);
        getOrLoadPendingVerifications().add(pendingVerification);

        this.dkConnect.getEventBus().callEvent(PostPendingVerifyEvent.class, new DefaultPostPendingVerifyEvent(dkConnect, this, voiceAdapter));
        return pendingVerification;
    }

    @Override
    public void sendMessage(VoiceAdapter voiceAdapter, Textable text) {
        voiceAdapter.sendPrivateMessage(getVerificationInternal(voiceAdapter), text, VariableSet.create());
    }

    @Override
    public void assignRole(VoiceAdapter voiceAdapter, String roleId) {
        voiceAdapter.assignRole(getVerificationInternal(voiceAdapter), roleId);
    }

    @Override
    public void removeRole(VoiceAdapter voiceAdapter, String roleId) {
        voiceAdapter.removeRole(getVerificationInternal(voiceAdapter), roleId);
    }

    @Override
    public CompletableFuture<Boolean> hasRole(VoiceAdapter voiceAdapter, String roleId) {
        return voiceAdapter.hasRole(getVerificationInternal(voiceAdapter), roleId);
    }

    @Override
    public CompletableFuture<Collection<String>> getRoleIds(VoiceAdapter voiceAdapter) {
        return voiceAdapter.getRoleIds(getVerificationInternal(voiceAdapter));
    }

    private Verification getVerificationInternal(VoiceAdapter voiceAdapter) {
        Verification verification = getVerification(voiceAdapter);
        if(verification == null) throw new IllegalArgumentException("Can't execute action for player "+getId()+". No verification available for voice adapter " + voiceAdapter.getName());
        return verification;
    }

    private Collection<Verification> getOrLoadVerifications() {
        if(this.verifications == null) {
            this.verifications = new ArrayList<>();

            this.dkConnect.getStorage().getPlayerVerifications().find().where("PlayerId", getId()).execute()
                    .loadIn(this.verifications, resultEntry -> new DefaultVerification(dkConnect, this,
                            dkConnect.getVoiceAdapterByVerificationSystemName(resultEntry.getString("VoiceAdapterName")),
                            resultEntry.getString("UserId"), resultEntry.getString("Username"),
                            resultEntry.getLong("Time")));
        }
        return this.verifications;
    }

    private Collection<PendingVerification> getOrLoadPendingVerifications() {
        if(this.pendingVerifications == null) {
            this.pendingVerifications = new ArrayList<>();

            this.dkConnect.getStorage().getPlayerPendingVerifications().find().where("PlayerId", getId()).execute()
                    .loadIn(this.pendingVerifications, resultEntry -> new DefaultPendingVerification(dkConnect, this,
                            dkConnect.getVoiceAdapterByVerificationSystemName(resultEntry.getString("VoiceAdapterName")),
                            resultEntry.getString("Code"),
                            resultEntry.getLong("Time")));
        }
        return this.pendingVerifications;
    }

    @Internal
    public void addVerification(Verification verification) {
        if(this.verifications != null) {
            this.verifications.add(verification);
        }
    }

    @Internal
    public void removeVerification(Verification verification) {
        if(this.verifications != null) {
            this.verifications.remove(verification);
        }
    }

    @Internal
    public void removePendingVerification(PendingVerification pendingVerification) {
        if(this.pendingVerifications != null) {
            this.pendingVerifications.remove(pendingVerification);
        }
    }
}
