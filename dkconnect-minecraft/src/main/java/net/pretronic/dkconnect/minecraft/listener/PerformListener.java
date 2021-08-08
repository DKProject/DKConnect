package net.pretronic.dkconnect.minecraft.listener;

import net.pretronic.dkconnect.api.VoiceAdapterType;
import net.pretronic.dkconnect.api.event.verification.pending.PendingVerificationValidationCheckEvent;
import net.pretronic.dkconnect.api.event.verification.unverify.UnverifiedEvent;
import net.pretronic.dkconnect.api.event.verification.verify.VerifiedEvent;
import net.pretronic.dkconnect.minecraft.DKConnectPlugin;
import net.pretronic.dkconnect.minecraft.Util;
import net.pretronic.dkconnect.minecraft.config.discord.DiscordSharedConfig;
import net.pretronic.libraries.event.Listener;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.MinecraftPlayer;

public class PerformListener {

    private final DKConnectPlugin dkConnectPlugin;

    public PerformListener(DKConnectPlugin dkConnectPlugin) {
        this.dkConnectPlugin = dkConnectPlugin;
    }

    @Listener
    public void onVerified(VerifiedEvent event) {
        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(event.getPlayer().getId());
        Util.updateDiscordRoles(dkConnectPlugin, event.getVerification(), player);
    }

    @Listener
    public void onUnverified(UnverifiedEvent event) {
        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(event.getPlayer().getId());
        Util.updateDiscordRoles(dkConnectPlugin, event.getVerification(), player);
    }

    @Listener
    public void onPendingVerificationValidationCheck(PendingVerificationValidationCheckEvent event) {
        if(event.getVoiceAdapter().getType().equals(VoiceAdapterType.DISCORD)) {
            long lifetime = DiscordSharedConfig.PENDING_VERIFICATION_LIFETIME_TIME;
            long created = event.getVerification().getTime();
            boolean isValid = (System.currentTimeMillis()-created)<lifetime;
            event.setValid(isValid);
        }
    }
}
