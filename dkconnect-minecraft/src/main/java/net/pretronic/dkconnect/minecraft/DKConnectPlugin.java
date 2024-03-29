package net.pretronic.dkconnect.minecraft;

import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.common.DefaultDKConnect;
import net.pretronic.dkconnect.minecraft.commands.DKConnectCommand;
import net.pretronic.dkconnect.minecraft.commands.UnverifyCommand;
import net.pretronic.dkconnect.minecraft.commands.VerifyCommand;
import net.pretronic.dkconnect.minecraft.commands.VerifyInfoCommand;
import net.pretronic.dkconnect.minecraft.config.DKConnectConfig;
import net.pretronic.dkconnect.minecraft.config.discord.DiscordGuildConfig;
import net.pretronic.dkconnect.minecraft.config.discord.DiscordSharedConfig;
import net.pretronic.dkconnect.minecraft.listener.DiscordListener;
import net.pretronic.dkconnect.minecraft.listener.PerformListener;
import net.pretronic.dkconnect.minecraft.listener.PlayerListener;
import net.pretronic.dkconnect.voiceadapter.discord.InternalDiscordBotListener;
import net.pretronic.dkconnect.voiceadapter.discord.DiscordVoiceAdapter;
import net.pretronic.dkconnect.voiceadapter.discord.MappedEventManager;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.MessageProvider;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import net.pretronic.libraries.utility.io.FileUtil;
import org.mcnative.licensing.context.platform.McNativeLicenseIntegration;
import org.mcnative.licensing.exceptions.CloudNotCheckoutLicenseException;
import org.mcnative.licensing.exceptions.LicenseNotValidException;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.plugin.MinecraftPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DKConnectPlugin extends MinecraftPlugin {

    private DefaultDKConnect dkConnect;
    private Collection<DiscordGuildConfig> guildConfigs;

    @Lifecycle(state = LifecycleState.LOAD)
    public void onLoad(LifecycleState state) {
        getLogger().info("DKConnect is starting, please wait..");
        //SLF4JStaticBridge.setLogger(getLogger());

        DefaultDKConnect dkConnect = new DefaultDKConnect(getRuntime().getLocal().getEventBus(), getLogger(), getDatabaseOrCreate(),
                playerId -> McNative.getInstance().getPlayerManager().getPlayer(playerId).getLanguage());

        this.dkConnect = dkConnect;

        getRuntime().getRegistry().registerService(this, DKConnect.class, dkConnect);

        DescriberRegistrar.register();

        getConfiguration().load(DKConnectConfig.class);
        getConfiguration("discord").load(DiscordSharedConfig.class);

        getRuntime().getPlayerManager().registerPlayerAdapter(DKConnectPlayer.class, player -> dkConnect.getPlayerManager().getPlayer(player.getUniqueId()));

        getRuntime().getLocal().getEventBus().subscribe(this, new PlayerListener(this));
        getRuntime().getLocal().getEventBus().subscribe(this, new PerformListener(this));

        registerVoiceAdapters(dkConnect);
        registerCommands(dkConnect);

        McNative.getInstance().getScheduler().createTask(this)
                .delay(3, TimeUnit.SECONDS)
                .execute(()-> {
                    for (DiscordGuildConfig guildConfig : getGuildConfigs()) {
                        guildConfig.init(this);
                    }
                });

        getLogger().info("DKConnect started successfully");
    }

    @Lifecycle(state = LifecycleState.BOOTSTRAP)
    public void onBootstrap(LifecycleState state) {

    }

    private void registerCommands(DefaultDKConnect dkConnect) {
        for (Map.Entry<String, CommandConfiguration> entry : DKConnectConfig.VERIFY_COMMANDS.entrySet()) {
            getRuntime().getLocal().getCommandManager().registerCommand(new VerifyCommand(this, entry.getValue(), dkConnect.getVoiceAdapterByVerificationSystemName(entry.getKey())));
        }
        for (Map.Entry<String, CommandConfiguration> entry : DKConnectConfig.UNVERIFY_COMMANDS.entrySet()) {
            getRuntime().getLocal().getCommandManager().registerCommand(new UnverifyCommand(this, entry.getValue(), dkConnect.getVoiceAdapterByVerificationSystemName(entry.getKey())));
        }
        getRuntime().getLocal().getCommandManager().registerCommand(new DKConnectCommand(this));
        getRuntime().getLocal().getCommandManager().registerCommand(new VerifyInfoCommand(this, DKConnectConfig.COMMAND_VERIFYINFO));
    }

    private void registerVoiceAdapters(DefaultDKConnect dkConnect) {
        if(!McNative.getInstance().isNetworkAvailable() || (McNative.getInstance().getPlatform().isProxy())) {
            net.dv8tion.jda.api.JDA jda;
            EventBus eventBus = McNative.getInstance().getLocal().getEventBus();
            try {
                getLogger().info("Starting jda bot instance");
                jda = net.dv8tion.jda.api.JDABuilder.create(DiscordSharedConfig.BOT_TOKEN, Arrays.asList(net.dv8tion.jda.api.requests.GatewayIntent.values()))
                        .setAutoReconnect(true)
                        .build();

                jda.setEventManager(new MappedEventManager(this, eventBus));
                jda.addEventListener(new DiscordListener(this));
            } catch (LoginException e) {
                throw new RuntimeException("Can't start discord bot", e);
            }
            boolean globalListenerRegistered = false;
            this.guildConfigs = loadDiscordGuildConfigs();
            for (DiscordGuildConfig guildConfig : guildConfigs) {
                DiscordVoiceAdapter voiceAdapter = createDiscordVoiceAdapter(dkConnect, jda, eventBus, guildConfig);
                dkConnect.registerVoiceAdapter(voiceAdapter);
                if(!globalListenerRegistered) {
                    globalListenerRegistered = true;
                    jda.addEventListener(new InternalDiscordBotListener(dkConnect));
                }
            }
        }
    }

    private DiscordVoiceAdapter createDiscordVoiceAdapter(DKConnect dkConnect, net.dv8tion.jda.api.JDA jda, EventBus eventBus,  DiscordGuildConfig guildConfig) {
        DiscordVoiceAdapter discordVoiceAdapter = new DiscordVoiceAdapter(dkConnect, guildConfig.getVoiceAdapterName(),
                jda, guildConfig.getGuildId(), DiscordSharedConfig.COMMAND_PREFIX, eventBus, (triple) -> {
            MessageProvider messageProvider = McNative.getInstance().getRegistry().getService(MessageProvider.class);
            return messageProvider.buildMessage(triple.getFirst(), triple.getThird(), triple.getSecond());
        });
        discordVoiceAdapter.getCommandManager().registerCommand(new net.pretronic.dkconnect.common.voiceadapter.commands.VerifyCommand(discordVoiceAdapter,
                DiscordSharedConfig.VERIFY_COMMAND));
        discordVoiceAdapter.getCommandManager().registerCommand(new net.pretronic.dkconnect.common.voiceadapter.commands.UnverifyCommand(discordVoiceAdapter,
                DiscordSharedConfig.UNVERIFY_COMMAND));
        return discordVoiceAdapter;
    }

    private Collection<DiscordGuildConfig> loadDiscordGuildConfigs() {
        getLogger().info("Loading discord guild configs");
        Collection<DiscordGuildConfig> guildConfigs = new ArrayList<>();

        File location = new File("plugins/DKConnect/discord-guilds/");
        if(!location.exists()) {
            location.mkdirs();
            File defaultGuildConfig = new File(location, "MyGuild.yml");
            Document data = Document.newDocument(new DiscordGuildConfig());
            DocumentFileType.YAML.getWriter().write(defaultGuildConfig, data);
        }
        FileUtil.processFilesHierarchically(location, file -> guildConfigs.add(DocumentFileType.YAML.getReader().read(file).getAsObject(DiscordGuildConfig.class)));
        getLogger().info("Found " + guildConfigs.size() + " discord guild configs");
        return guildConfigs;
    }

    public Collection<DiscordGuildConfig> getGuildConfigs() {
        return guildConfigs;
    }

    public DefaultDKConnect getDKConnect() {
        return dkConnect;
    }
}
