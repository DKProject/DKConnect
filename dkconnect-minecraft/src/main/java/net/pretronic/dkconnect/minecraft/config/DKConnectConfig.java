package net.pretronic.dkconnect.minecraft.config;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.utility.map.Maps;

import java.util.Map;

public class DKConnectConfig {

    public static Map<String, CommandConfiguration> VERIFY_COMMANDS = Maps.of("discord", CommandConfiguration.name("verify"));
    public static Map<String, CommandConfiguration> UNVERIFY_COMMANDS = Maps.of("discord", CommandConfiguration.name("unverify"));
}
