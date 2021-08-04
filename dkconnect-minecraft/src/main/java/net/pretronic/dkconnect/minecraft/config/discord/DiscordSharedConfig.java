package net.pretronic.dkconnect.minecraft.config.discord;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.document.annotations.DocumentIgnored;
import net.pretronic.libraries.document.annotations.OnDocumentConfigurationLoad;
import net.pretronic.libraries.utility.duration.DurationProcessor;

public class DiscordSharedConfig {

    public static String BOT_TOKEN = "********";
    public static String COMMAND_PREFIX = "!";

    public static CommandConfiguration VERIFY_COMMAND = CommandConfiguration.name("verify");
    public static CommandConfiguration UNVERIFY_COMMAND = CommandConfiguration.name("unverify");

    public static String PENDING_VERIFICATION_LIFETIME = "5m";

    @DocumentIgnored
    public static long PENDING_VERIFICATION_LIFETIME_TIME;

    @OnDocumentConfigurationLoad
    public static void onLoad() {
        try {
            PENDING_VERIFICATION_LIFETIME_TIME = DurationProcessor.getStandard().parse(PENDING_VERIFICATION_LIFETIME).toMillis();
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Can't parse pending.verication.lifetime", exception);
        }
    }
}
