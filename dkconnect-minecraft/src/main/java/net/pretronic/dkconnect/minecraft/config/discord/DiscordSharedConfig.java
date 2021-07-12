package net.pretronic.dkconnect.minecraft.config.discord;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;

public class DiscordSharedConfig {

    public static String BOT_TOKEN = "********";
    public static String COMMAND_PREFIX = "!";

    public static CommandConfiguration VERIFY_COMMAND = CommandConfiguration.name("verify");
    public static CommandConfiguration UNVERIFY_COMMAND = CommandConfiguration.name("unverify");
}
