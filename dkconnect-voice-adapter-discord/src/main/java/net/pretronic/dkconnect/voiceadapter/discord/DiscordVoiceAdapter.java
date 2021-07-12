package net.pretronic.dkconnect.voiceadapter.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.TextChannel;
import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.voiceadapter.StaticMessage;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.VoiceAdapterType;
import net.pretronic.dkconnect.api.player.Verification;
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
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.io.FileUtil;
import net.pretronic.libraries.utility.map.Triple;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class DiscordVoiceAdapter implements VoiceAdapter {

    private final DKConnect dkConnect;
    private final String name;
    private final JDA jda;
    private final long guildId;
    private final String commandPrefix;

    private final CommandManager commandManager;
    private final EventBus eventBus;
    private final Function<Triple<String, Language, VariableSet>, String> messageGetter;

    private final Map<String, DiscordMessage> messages;
    private final Collection<StaticMessage> staticMessages;

    public DiscordVoiceAdapter(DKConnect dkConnect, String name, JDA jda, long guildId, String commandPrefix,
                               Collection<StaticMessage> staticMessages,
                               EventBus eventBus, Function<Triple<String, Language, VariableSet>, String> messageGetter) {
        this.dkConnect = dkConnect;
        this.name = name;
        this.guildId = guildId;
        this.commandPrefix = commandPrefix;
        this.eventBus = eventBus;
        this.commandManager = new DefaultCommandManager();
        this.messages = loadMessages();
        this.messageGetter = messageGetter;
        this.jda = jda;
        this.staticMessages = staticMessages;
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
        String key = rawKey.replace("%voiceAdapter%", getName());
        DiscordMessage message = this.messages.get(key);
        if(message == null) return new StringTextable("The message "+rawKey+" was not found.");
        return message;
    }

    @Override
    public void sendMessage(Verification verification, Textable text, VariableSet variables) {
        String userId = verification.getUserId();
        this.jda.retrieveUserById(userId).queue(user -> {
            user.openPrivateChannel().queue(channel -> {
                DiscordBotUtil.sendMessage(channel, verification.getPlayer().getLanguage(), text, variables);
            }, Throwable::printStackTrace);
        }, Throwable::printStackTrace);
    }

    @Override
    public void sendMessage(String channelId, Language language, Textable text, VariableSet variables) {
        DiscordBotUtil.sendMessage(DiscordBotUtil.getTextChannel(this, channelId), language, text, variables);
    }

    @Override
    public void sendMessage(String channelId, Textable text, VariableSet variables) {
        sendMessage(channelId, null, text, variables);
    }

    @Override
    public StaticMessage getStaticMessage(String name) {
        return Iterators.findOne(this.staticMessages, message -> message.getName().equalsIgnoreCase(name));
    }

    @Override
    public CompletableFuture<StaticMessage> sendStaticMessage(String name, String channelId, Language language, Textable text, VariableSet variables) {
        Validate.notNull(language, text, variables, name, channelId);
        TextChannel channel = DiscordBotUtil.getTextChannel(this, channelId);
        CompletableFuture<StaticMessage> future = new CompletableFuture<>();
        DiscordBotUtil.sendMessage(channel, language, text, variables).thenAccept(message -> {
            DiscordStaticMessage staticMessage = new DiscordStaticMessage(this, name, channelId, message.getId());
            future.complete(staticMessage);
        });
        return future;
    }

    @Override
    public CompletableFuture<StaticMessage> sendStaticMessage(String name, String channelId, Textable text, VariableSet variables) {
        return sendStaticMessage(name, channelId, null, text, variables);
    }

    @Override
    public void assignRole(Verification verification, String roleId) {
        String userId = verification.getUserId();

        DiscordBotUtil.getMember(this, userId).thenAccept(member -> {
            member.getGuild().addRoleToMember(member, DiscordBotUtil.getRole(member.getGuild(), roleId)).queue(unused -> System.out.println("success"), Throwable::printStackTrace);
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

    @Internal
    public JDA getJda() {
        return jda;
    }

    private Map<String, DiscordMessage> loadMessages() {
        Map<String, DiscordMessage> messages = new HashMap<>();
        File location = new File("plugins/DKConnect/discord-messages/");
        if(!location.exists()) {
            location.mkdirs();
            for (Iterator<Path> iterator = getDirectoryFiles("/discord-messages").iterator(); iterator.hasNext();){
                Path child = iterator.next();
                if(Files.isRegularFile(child)) {
                    try {
                        Files.copy(DiscordVoiceAdapter.class.getResourceAsStream(child.toString()), Paths.get(location.getPath()+"/"+child.getFileName()));
                    } catch (IOException e) {
                        throw new RuntimeException("Can't extract message file from jar", e);
                    }
                }
            }
        }

        FileUtil.processFilesHierarchically(location, file -> {
            try {
                System.out.println("load message:"+file.getName());
                String key = file.getName().split("\\.")[0].replace("-", ".");
                Document document = DocumentFileType.JSON.getReader().read(file);
                System.out.println("before");
                DiscordMessage message = document.getAsObject(DiscordMessage.class);
                System.out.println("after");
                message.setVoiceAdapter(this);
                messages.put(key, message);
            } catch (Exception e) {
                System.out.println("Exception");
                throw new RuntimeException(e);
            }
        });

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
}
