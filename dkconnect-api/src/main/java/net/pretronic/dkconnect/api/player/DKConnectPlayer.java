package net.pretronic.dkconnect.api.player;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.language.Language;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DKConnectPlayer {

    UUID getId();

    Language getLanguage();

    Collection<Verification> getVerifications();

    Verification getVerification(VoiceAdapter voiceAdapter);

    boolean isVerified(VoiceAdapter voiceAdapter);


    Collection<PendingVerification> getPendingVerifications();

    PendingVerification getPendingVerification(VoiceAdapter voiceAdapter);


    PendingVerification verify(VoiceAdapter voiceAdapter);


    void sendMessage(VoiceAdapter voiceAdapter, Textable text);

    void assignRole(VoiceAdapter voiceAdapter, String roleId);

    void removeRole(VoiceAdapter voiceAdapter, String roleId);

    CompletableFuture<Boolean> hasRole(VoiceAdapter voiceAdapter, String roleId);

    CompletableFuture<Collection<String>> getRoleIds(VoiceAdapter voiceAdapter);
}
