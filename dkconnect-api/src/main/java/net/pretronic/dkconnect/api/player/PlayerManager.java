package net.pretronic.dkconnect.api.player;

import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;

import java.util.UUID;

public interface PlayerManager {

    DKConnectPlayer getPlayer(UUID playerId);

    DKConnectPlayer getPlayerByVerificationUsername(VoiceAdapter voiceAdapter, String username);

    DKConnectPlayer getPlayerByVerificationUserId(VoiceAdapter voiceAdapter, String userId);

    DKConnectPlayer getPlayerByPendingVerificationCode(VoiceAdapter voiceAdapter, String code);
}
