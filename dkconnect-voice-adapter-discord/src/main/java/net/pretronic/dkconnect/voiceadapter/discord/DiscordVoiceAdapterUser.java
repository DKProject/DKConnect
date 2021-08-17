package net.pretronic.dkconnect.voiceadapter.discord;

import net.dv8tion.jda.api.entities.User;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapterUser;

public class DiscordVoiceAdapterUser implements VoiceAdapterUser {

    private final DiscordVoiceAdapter voiceAdapter;
    private final User user;

    public DiscordVoiceAdapterUser(DiscordVoiceAdapter voiceAdapter, User user) {
        this.voiceAdapter = voiceAdapter;
        this.user = user;
    }

    @Override
    public String getId() {
        return user.getId();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public VoiceAdapter getVoiceAdapter() {
        return voiceAdapter;
    }

    @Override
    public DKConnectPlayer getPlayer() {
        return voiceAdapter.getDKConnect().getPlayerManager().getPlayerByVerificationUserId(voiceAdapter, user.getId());
    }

    @Override
    public Verification getVerification() {
        DKConnectPlayer player = getPlayer();
        if(player == null) return null;
        return player.getVerification(voiceAdapter);
    }

    @Override
    public boolean isBot() {
        return user.isBot();
    }

    @Override
    public boolean isSystem() {
        return user.getId().equals(voiceAdapter.getJda().getSelfUser().getId());
    }
}
