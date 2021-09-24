package net.pretronic.dkconnect.voiceadapter.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.voiceadapter.*;
import net.pretronic.dkconnect.api.player.Verification;
import net.pretronic.dkconnect.api.voiceadapter.Emoji;
import net.pretronic.dkconnect.api.voiceadapter.Message;
import net.pretronic.dkconnect.api.voiceadapter.channel.Channel;
import net.pretronic.dkconnect.api.voiceadapter.channel.TextChannel;
import net.pretronic.dkconnect.voiceadapter.discord.channel.DiscordTextChannel;
import net.pretronic.dkconnect.voiceadapter.discord.message.DiscordMessage;
import net.pretronic.libraries.command.manager.CommandManager;
import net.pretronic.libraries.command.manager.DefaultCommandManager;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.StringTextable;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.annonations.Nullable;
import net.pretronic.libraries.utility.io.FileUtil;
import net.pretronic.libraries.utility.map.Triple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

public class DiscordVoiceAdapter implements VoiceAdapter {

    private static final File DISCORD_MESSAGES_LOCATION = new File("plugins/DKConnect/discord-messages/");

    private final DKConnect dkConnect;
    private final String name;
    private final JDA jda;
    private final long guildId;
    private final String commandPrefix;

    private final CommandManager commandManager;
    private final EventBus eventBus;
    private final Function<Triple<String, Language, VariableSet>, String> messageGetter;

    private final Map<String, DiscordMessage> messages;

    private final Collection<Channel> channels;

    public DiscordVoiceAdapter(DKConnect dkConnect, String name, JDA jda, long guildId, String commandPrefix,
                               EventBus eventBus, Function<Triple<String, Language, VariableSet>, String> messageGetter) {
        this.dkConnect = dkConnect;
        this.name = name;
        this.guildId = guildId;
        this.commandPrefix = commandPrefix;
        this.eventBus = eventBus;
        this.commandManager = new DefaultCommandManager();
        this.channels = new ArrayList<>();
        this.messages = loadMessages();
        this.messageGetter = messageGetter;
        this.jda = jda;
    }

    @Override
    public String getType() {
        return VoiceAdapterType.DISCORD;
    }

    public DKConnect getDKConnect() {
        return dkConnect;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public Textable getMessage(String rawKey) {
        String key = rawKey.replace("%voiceAdapter%", getVerificationSystemName());
        DiscordMessage message = this.messages.get(key);
        if(message == null) return new StringTextable("The message "+rawKey+" was not found.");
        return message;
    }

    @Override
    public void importMessage(String key, InputStream inputStream) {
        try {
            String fileName = key.replace(".", "-")+".json";
            Files.copy(inputStream, Paths.get(DISCORD_MESSAGES_LOCATION.getPath()+"/"+fileName));

            Document document = DocumentFileType.JSON.getReader().read(inputStream);
            DiscordMessage message = document.getAsObject(DiscordMessage.class);
            message.setVoiceAdapter(this);
            messages.put(key, message);
            this.dkConnect.getLogger().info("("+getName()+") Imported message " + key);
        } catch (IOException e) {
            throw new RuntimeException("Can't extract message file from jar", e);
        }
    }

    @Override
    public Emoji parseEmoji(String value) {
        return new DiscordEmoji(value);
    }

    @Override
    public CompletableFuture<Message> sendPrivateMessage(Verification verification, Textable text, VariableSet variables) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        String userId = verification.getUserId();
        this.jda.retrieveUserById(userId).queue(user -> {
            user.openPrivateChannel().queue(channel -> {
                DiscordBotUtil.sendMessage(channel, verification.getPlayer().getLanguage(), text, variables).thenAccept(message -> {
                    future.complete(new net.pretronic.dkconnect.voiceadapter.discord.DiscordMessage(this, new DiscordTextChannel(this, channel), message));
                });
            }, Throwable::printStackTrace);
        }, Throwable::printStackTrace);

        return future;
    }

    @Override
    public CompletableFuture<Message> sendPrivateMessage(VoiceAdapterUser user, Textable text, VariableSet variables) {
        return null;
    }

    @Override
    public CompletableFuture<net.pretronic.dkconnect.api.voiceadapter.channel.TextChannel> createTextChannel(@Nullable String categoryId, String name, String[] allowedRoles, String[] allowedUserIds) {
        CompletableFuture<net.pretronic.dkconnect.api.voiceadapter.channel.TextChannel> future = new CompletableFuture<>();

        Category category = DiscordBotUtil.getCategory(getGuild(), categoryId);
        getGuild().createTextChannel(name, category).queue(channel -> {
            DiscordTextChannel discordTextChannel = new DiscordTextChannel(this, channel);
            this.channels.add(discordTextChannel);
            future.complete(discordTextChannel);

            if(allowedRoles != null) {
                for (String roleId : allowedRoles) {
                    Role role = DiscordBotUtil.getRole(getGuild(), roleId);
                    channel.upsertPermissionOverride(role).setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ).queue(ignored -> {}, Throwable::printStackTrace);
                }
            }

            if(allowedUserIds != null) {
                for (String userId : allowedUserIds) {
                    getGuild().retrieveMemberById(userId).queue(member -> {
                        channel.upsertPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_READ).queue(ignored -> {}, Throwable::printStackTrace);
                    }, Throwable::printStackTrace);
                }
            }
        }, Throwable::printStackTrace);
        return future;
    }

    @Override
    public TextChannel getTextChannel(String id) {
        Validate.notNull(id);
        Channel channel = Iterators.findOne(this.channels, channel0 -> channel0.getId().equals(id) && channel0 instanceof TextChannel);
        if(channel == null) {
            net.dv8tion.jda.api.entities.TextChannel discordChannel = getGuild().getTextChannelById(id);
            if(discordChannel != null) {
                channel = new DiscordTextChannel(this, discordChannel);
                this.channels.add(channel);
            }
        }
        if(channel == null) {
            throw new IllegalArgumentException("Can't retrieve text channel " + id);
        }
        return (TextChannel) channel;
    }

    @Override
    public void assignRole(Verification verification, String roleId) {
        String userId = verification.getUserId();

        DiscordBotUtil.getMember(this, userId).thenAccept(member -> {
            member.getGuild().addRoleToMember(member, DiscordBotUtil.getRole(member.getGuild(), roleId)).queue(unused -> {}, Throwable::printStackTrace);
        });
    }

    @Override
    public void removeRole(Verification verification, String roleId) {
        String userId = verification.getUserId();

        DiscordBotUtil.getMember(this, userId).thenAccept(member -> {
            member.getGuild().removeRoleFromMember(member, DiscordBotUtil.getRole(member.getGuild(), roleId)).queue(unused -> {}, Throwable::printStackTrace);
        });
    }

    @Override
    public CompletableFuture<Boolean> hasRole(Verification verification, String roleId) {
        String userId = verification.getUserId();
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        DiscordBotUtil.getMember(this, userId).thenAccept(member -> {
            boolean hasRole = Iterators.findOne(member.getRoles(), role -> role.getId().equals(roleId)) != null;
            future.complete(hasRole);
        });
        return future;
    }

    @Override
    public CompletableFuture<Collection<String>> getRoleIds(Verification verification) {
        String userId = verification.getUserId();
        CompletableFuture<Collection<String>> future = new CompletableFuture<>();

        DiscordBotUtil.getMember(this, userId).thenAccept(member -> {
            Collection<String> roles = Iterators.map(member.getRoles(), ISnowflake::getId);
            future.complete(roles);
        });
        return future;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getVerificationSystemName() {
        return "discord";
    }

    @Internal
    public JDA getJda() {
        return jda;
    }

    private Map<String, DiscordMessage> loadMessages() {
        Map<String, DiscordMessage> messages = new HashMap<>();

        if(!DISCORD_MESSAGES_LOCATION.exists()) {
            this.dkConnect.getLogger().info("("+getName()+") Extracting default message");
            DISCORD_MESSAGES_LOCATION.mkdirs();
            for (Iterator<Path> iterator = getDirectoryFiles("/discord-messages").iterator(); iterator.hasNext();){
                Path child = iterator.next();
                if(Files.isRegularFile(child)) {
                    String key = StringUtil.split(child.getFileName().toString(), '.')[0];

                    String fileName = key.replace(".", "-")+".json";
                    try {
                        Files.copy(DiscordVoiceAdapter.class.getResourceAsStream(child.toString()), Paths.get(DISCORD_MESSAGES_LOCATION.getPath()+"/"+fileName));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        this.dkConnect.getLogger().info("("+getName()+") Importing messages");
        AtomicInteger messagesCount = new AtomicInteger();
        FileUtil.processFilesHierarchically(DISCORD_MESSAGES_LOCATION, file -> {
            try {
                String key = StringUtil.split(file.getName(), '.')[0].replace("-", ".");
                Document document = DocumentFileType.JSON.getReader().read(file);
                DiscordMessage message = document.getAsObject(DiscordMessage.class);
                message.setVoiceAdapter(this);
                messages.put(key, message);
                this.dkConnect.getLogger().info("("+getName()+") Loaded message " + key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        this.dkConnect.getLogger().info("("+getName()+") "+ messagesCount.get() + " messages loaded");
        return messages;
    }

    private Stream<Path> getDirectoryFiles(String folder) {
        URI uri = null;
        try {
            uri = DiscordVoiceAdapter.class.getResource(folder).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Path path;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem;
            try {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            } catch (IOException e) {
                throw new RuntimeException("Can't get file system for folder " + folder, e);
            }
            path = fileSystem.getPath("/discord-messages");
        } else {
            path = Paths.get(uri);
        }
        Stream<Path> walk;
        try {
            walk = Files.walk(path, 1);
        } catch (IOException e) {
            throw new RuntimeException("Can't get stream for folder " + folder, e);
        }
        return walk;
    }

    public Function<Triple<String, Language, VariableSet>, String> getMessageGetter() {
        return messageGetter;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public long getGuildId() {
        return guildId;
    }

    public Guild getGuild() {
        return DiscordBotUtil.getGuild(this);
    }

    public Collection<Channel> getChannels() {
        return channels;
    }
}
