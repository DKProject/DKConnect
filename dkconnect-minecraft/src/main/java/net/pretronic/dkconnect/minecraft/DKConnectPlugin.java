package net.pretronic.dkconnect.minecraft;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.common.DefaultDKConnect;
import net.pretronic.dkconnect.common.voiceadapter.routed.RoutedVoiceAdapter;
import net.pretronic.dkconnect.common.voiceadapter.routed.RoutedVoiceAdapterAction;
import net.pretronic.dkconnect.minecraft.commands.UnverifyCommand;
import net.pretronic.dkconnect.minecraft.commands.VerifyCommand;
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
import net.pretronic.libraries.event.DefaultEventBus;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.MessageProvider;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.io.FileUtil;
import org.mcnative.licensing.context.platform.McNativeLicenseIntegration;
import org.mcnative.licensing.exceptions.CloudNotCheckoutLicenseException;
import org.mcnative.licensing.exceptions.LicenseNotValidException;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.NetworkIdentifier;
import org.mcnative.runtime.api.network.messaging.Messenger;
import org.mcnative.runtime.api.plugin.MinecraftPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DKConnectPlugin extends MinecraftPlugin {

    public static final String RESOURCE_ID = "1e6d4f31-e2e0-11eb-8ba0-0242ac180002";
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoTmvCUx+BvHKWeyCcjpOC8lbe8Pe/1ERyYu5/aSfj1qY2gEa/ie3u3c+ejgiZ5IBckR4qKmRkrBNQCtmd7ojinB7WFqM1xvdyWkK/s3Vv0tzBgZwqXTjX6W07WRFrp/oW1JvA8aShdoVqDVxlWYTkd6mKTTBEs9vKfeTn4GuQExG+qXV7gYm3WAMGehQ3YL3zxGGxjV78HMLyNrK7RbmX0AocDTpDoJeUNT3RDHS9kHQHSUYhHup3XjhymWeiVSh4cb8R5IF61uuxQdpE9KtXuT4qSCgHPUQyoRSuJq6zflCIJngSoTF09eDS0wTeICNvt0WMVpBexDmYJN8owwIgwIDAQAB";

    private DefaultDKConnect dkConnect;
    private Collection<DiscordGuildConfig> guildConfigs;

    @Lifecycle(state = LifecycleState.LOAD)
    public void onLoad(LifecycleState state) {
        getLogger().info("DKConnect is starting, please wait..");
        //SLF4JStaticBridge.setLogger(getLogger());
        try{
            McNativeLicenseIntegration.newContext(this,RESOURCE_ID,PUBLIC_KEY).verifyOrCheckout();
        }catch (LicenseNotValidException | CloudNotCheckoutLicenseException e){
            getLogger().error("--------------------------------");
            getLogger().error("-> Invalid license");
            getLogger().error("-> Error: "+e.getMessage());
            getLogger().error("--------------------------------");
            getLogger().info("DKConnect is shutting down");
            getLoader().shutdown();
            return;
        }

        DefaultDKConnect dkConnect = new DefaultDKConnect(getRuntime().getLocal().getEventBus(), getDatabaseOrCreate(),
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

        getLogger().info("DKConnect started successfully");
    }

    @Lifecycle(state = LifecycleState.BOOTSTRAP)
    public void onBootstrap(LifecycleState state) {
        McNative.getInstance().getScheduler().createTask(this)
                .delay(3, TimeUnit.SECONDS)
                .execute(()-> {
                    for (DiscordGuildConfig guildConfig : getGuildConfigs()) {
                        guildConfig.init(this);
                    }
                });
    }

    private void registerCommands(DefaultDKConnect dkConnect) {
        for (Map.Entry<String, CommandConfiguration> entry : DKConnectConfig.VERIFY_COMMANDS.entrySet()) {
            getRuntime().getLocal().getCommandManager().registerCommand(new VerifyCommand(this, entry.getValue(), dkConnect.getVoiceAdapter(entry.getKey())));
        }
        for (Map.Entry<String, CommandConfiguration> entry : DKConnectConfig.UNVERIFY_COMMANDS.entrySet()) {
            getRuntime().getLocal().getCommandManager().registerCommand(new UnverifyCommand(this, entry.getValue(), dkConnect.getVoiceAdapter(entry.getKey())));
        }
    }

    private void registerVoiceAdapters(DefaultDKConnect dkConnect) {
        if(!McNative.getInstance().isNetworkAvailable() || (McNative.getInstance().getPlatform().isProxy() && !isVoiceAdapterAlreadyHosted("discord"))) {
            JDA jda;
            EventBus eventBus = new DefaultEventBus();
            try {
                jda = JDABuilder.create(DiscordSharedConfig.BOT_TOKEN, Arrays.asList(GatewayIntent.values()))
                        .setAutoReconnect(true)
                        .build();

                jda.setEventManager(new MappedEventManager(this, eventBus));
                jda.addEventListener(new DiscordListener(this));
            } catch (LoginException e) {
                throw new RuntimeException("Can't start discord bot ", e);
            }
            boolean globalListenerRegistered = false;
            this.guildConfigs = loadDiscordGuildConfigs();
            for (DiscordGuildConfig guildConfig : guildConfigs) {
                DiscordVoiceAdapter voiceAdapter = createDiscordVoiceAdapter(dkConnect, jda, eventBus, guildConfig);
                dkConnect.registerVoiceAdapter(voiceAdapter);
                if(!globalListenerRegistered) {
                    globalListenerRegistered = true;
                    jda.addEventListener(new InternalDiscordBotListener(voiceAdapter));
                }
            }

            if(McNative.getInstance().isNetworkAvailable()) {
                Messenger messenger = McNative.getInstance().getNetwork().getMessenger();
                messenger.registerChannel(RoutedVoiceAdapterMessagingChannel.CHANNEL_NAME, this, new RoutedVoiceAdapterMessagingChannel());
            }
        } else {
            dkConnect.registerVoiceAdapter(new RoutedVoiceAdapter(dkConnect, "discord"));
        }
    }

    private DiscordVoiceAdapter createDiscordVoiceAdapter(DKConnect dkConnect, JDA jda, EventBus eventBus,  DiscordGuildConfig guildConfig) {
        DiscordVoiceAdapter discordVoiceAdapter = new DiscordVoiceAdapter(dkConnect, guildConfig.getVoiceAdapterName(),
                jda, guildConfig.getGuildId(), DiscordSharedConfig.COMMAND_PREFIX, new ArrayList<>(), eventBus, (triple) -> {
            MessageProvider messageProvider = McNative.getInstance().getRegistry().getService(MessageProvider.class);
            return messageProvider.buildMessage(triple.getFirst(), triple.getThird(), triple.getSecond());
        });
        discordVoiceAdapter.getCommandManager().registerCommand(new net.pretronic.dkconnect.common.voiceadapter.commands.VerifyCommand(discordVoiceAdapter,
                DiscordSharedConfig.VERIFY_COMMAND));
        discordVoiceAdapter.getCommandManager().registerCommand(new net.pretronic.dkconnect.common.voiceadapter.commands.UnverifyCommand(discordVoiceAdapter,
                DiscordSharedConfig.UNVERIFY_COMMAND));
        return discordVoiceAdapter;
    }

    private boolean isVoiceAdapterAlreadyHosted(String voiceAdapterName) {
        return false;//@Todo check
    }

    @Internal
    public void sendVoiceAdapterNetworkMessage(RoutedVoiceAdapterAction action, Document data) {
        if(!McNative.getInstance().isNetworkAvailable()) throw new UnsupportedOperationException("Can't send messages threw the network. Network not available");
        Messenger messenger = McNative.getInstance().getNetwork().getMessenger();
        messenger.sendMessage(NetworkIdentifier.BROADCAST_PROXY, RoutedVoiceAdapterMessagingChannel.CHANNEL_NAME,
                Document.newDocument().add("action", action).add("data", data));
    }

    @Internal
    public CompletableFuture<Document> sendVoiceAdapterNetworkQueryMessage(RoutedVoiceAdapterAction action, Document data) {
        if(!McNative.getInstance().isNetworkAvailable()) throw new UnsupportedOperationException("Can't send messages threw the network. Network not available");
        Messenger messenger = McNative.getInstance().getNetwork().getMessenger();
        return messenger.sendQueryMessageAsync(NetworkIdentifier.BROADCAST_PROXY, RoutedVoiceAdapterMessagingChannel.CHANNEL_NAME,
                Document.newDocument().add("action", action).add("data", data));
    }

    private Collection<DiscordGuildConfig> loadDiscordGuildConfigs() {
        Collection<DiscordGuildConfig> guildConfigs = new ArrayList<>();

        File location = new File("plugins/DKConnect/discord-guilds/");
        if(!location.exists()) {
            location.mkdirs();
            File defaultGuildConfig = new File(location, "MyGuild.yml");
            Document data = Document.newDocument(new DiscordGuildConfig());
            DocumentFileType.YAML.getWriter().write(defaultGuildConfig, data);
        }
        FileUtil.processFilesHierarchically(location, file -> guildConfigs.add(DocumentFileType.YAML.getReader().read(file).getAsObject(DiscordGuildConfig.class)));
        return guildConfigs;
    }

    public Collection<DiscordGuildConfig> getGuildConfigs() {
        return guildConfigs;
    }

    public DefaultDKConnect getDKConnect() {
        return dkConnect;
    }
}
