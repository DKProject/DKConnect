package net.pretronic.dkconnect.common.player;

import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.PendingVerification;
import net.pretronic.dkconnect.api.player.PlayerManager;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.common.DefaultDKConnect;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.utility.Validate;

import java.util.UUID;

public class DefaultPlayerManager implements PlayerManager {

    private final DefaultDKConnect dkConnect;

    private final Cache<DKConnectPlayer> playerCache;

    public DefaultPlayerManager(DefaultDKConnect dkConnect) {
        this.dkConnect = dkConnect;

        this.playerCache = new ArrayCache<>();
        this.playerCache.registerQuery("byPlayerId", new ByPlayerIdQuery());
        this.playerCache.registerQuery("ByPlayerVerificationUsername", new ByPlayerVerificationUsernameQuery());
        this.playerCache.registerQuery("ByPlayerVerificationUserId", new ByPlayerVerificationUserIdQuery());
        this.playerCache.registerQuery("ByPendingVerificationCode", new ByPendingVerificationCodeQuery());
    }

    @Override
    public DKConnectPlayer getPlayer(UUID playerId) {
        return this.playerCache.get("byPlayerId", playerId);
    }

    @Override
    public DKConnectPlayer getPlayerByVerificationUsername(VoiceAdapter voiceAdapter, String username) {
        return this.playerCache.get("ByPlayerVerificationUsername", voiceAdapter, username);
    }

    @Override
    public DKConnectPlayer getPlayerByVerificationUserId(VoiceAdapter voiceAdapter, String userId) {
        return this.playerCache.get("ByPlayerVerificationUserId", voiceAdapter, userId);
    }

    @Override
    public DKConnectPlayer getPlayerByPendingVerificationCode(VoiceAdapter voiceAdapter, String code) {
        return this.playerCache.get("ByPendingVerificationCode", voiceAdapter, code);
    }

    private class ByPlayerIdQuery implements CacheQuery<DKConnectPlayer> {

        @Override
        public boolean check(DKConnectPlayer player, Object[] identifiers) {
            UUID playerId = (UUID) identifiers[0];
            return player.getId().equals(playerId);
        }

        @Override
        public void validate(Object[] identifiers) {
            Validate.isTrue(identifiers.length == 1 && identifiers[0] instanceof UUID);
        }

        @Override
        public DKConnectPlayer load(Object[] identifiers) {
            return new DefaultDKConnectPlayer(dkConnect, (UUID) identifiers[0]);
        }
    }

    private class ByPlayerVerificationUsernameQuery implements CacheQuery<DKConnectPlayer> {

        @Override
        public boolean check(DKConnectPlayer player, Object[] identifiers) {
            VoiceAdapter voiceAdapter = (VoiceAdapter) identifiers[0];
            String username = (String) identifiers[1];
            Verification verification = player.getVerification(voiceAdapter);
            return verification != null && verification.getUsername().equalsIgnoreCase(username);
        }

        @Override
        public void validate(Object[] identifiers) {
            Validate.isTrue(identifiers.length == 2
                    && identifiers[0] instanceof VoiceAdapter
                    && identifiers[1] instanceof String);
        }

        @Override
        public DKConnectPlayer load(Object[] identifiers) {
            VoiceAdapter voiceAdapter = (VoiceAdapter) identifiers[0];
            String username = (String) identifiers[1];

            QueryResultEntry resultEntry = dkConnect.getStorage().getPlayerVerifications().find()
                    .get("PlayerId")
                    .where("VoiceAdapterName", voiceAdapter.getVerificationSystemName())
                    .where("Username", username)
                    .execute().firstOrNull();
            if(resultEntry == null) return null;
            return new DefaultDKConnectPlayer(dkConnect, resultEntry.getUniqueId("PlayerId"));
        }
    }

    private class ByPlayerVerificationUserIdQuery implements CacheQuery<DKConnectPlayer> {

        @Override
        public boolean check(DKConnectPlayer player, Object[] identifiers) {
            VoiceAdapter voiceAdapter = (VoiceAdapter) identifiers[0];
            String userId = (String) identifiers[1];
            Verification verification = player.getVerification(voiceAdapter);
            return verification != null && verification.getUserId().equalsIgnoreCase(userId);
        }

        @Override
        public void validate(Object[] identifiers) {
            Validate.isTrue(identifiers.length == 2
                    && identifiers[0] instanceof VoiceAdapter
                    && identifiers[1] instanceof String);
        }

        @Override
        public DKConnectPlayer load(Object[] identifiers) {
            VoiceAdapter voiceAdapter = (VoiceAdapter) identifiers[0];
            String userId = (String) identifiers[1];

            QueryResultEntry resultEntry = dkConnect.getStorage().getPlayerVerifications().find()
                    .get("PlayerId")
                    .where("VoiceAdapterName", voiceAdapter.getVerificationSystemName())
                    .where("UserId", userId)
                    .execute().firstOrNull();
            if(resultEntry == null) return null;
            return new DefaultDKConnectPlayer(dkConnect, resultEntry.getUniqueId("PlayerId"));
        }
    }

    private class ByPendingVerificationCodeQuery implements CacheQuery<DKConnectPlayer> {

        @Override
        public boolean check(DKConnectPlayer player, Object[] identifiers) {
            VoiceAdapter voiceAdapter = (VoiceAdapter) identifiers[0];
            String code = (String) identifiers[1];
            PendingVerification verification = player.getPendingVerification(voiceAdapter);
            return verification != null && verification.getCode().equalsIgnoreCase(code);
        }

        @Override
        public void validate(Object[] identifiers) {
            Validate.isTrue(identifiers.length == 2
                    && identifiers[0] instanceof VoiceAdapter
                    && identifiers[1] instanceof String);
        }

        @Override
        public DKConnectPlayer load(Object[] identifiers) {
            VoiceAdapter voiceAdapter = (VoiceAdapter) identifiers[0];
            String code = (String) identifiers[1];

            QueryResultEntry resultEntry = dkConnect.getStorage().getPlayerPendingVerifications().find()
                    .get("PlayerId")
                    .where("VoiceAdapterName", voiceAdapter.getVerificationSystemName())
                    .where("Code", code)
                    .execute().firstOrNull();
            if(resultEntry == null) return null;
            return new DefaultDKConnectPlayer(dkConnect, resultEntry.getUniqueId("PlayerId"));
        }
    }
}
