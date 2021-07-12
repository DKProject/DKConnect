package net.pretronic.dkconnect.api.player;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.libraries.message.Textable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Verification {

    DKConnectPlayer getPlayer();


    VoiceAdapter getVoiceAdapter();

    String getUserId();

    String getUsername();

    void setUsername(String username);

    long getTime();

    boolean unverify();


    void sendMessage(Textable text);

    void assignRole(String roleId);

    void removeRole(String roleId);

    CompletableFuture<Boolean> hasRole(String roleId);

    CompletableFuture<Collection<String>> getRoleIds();
}
