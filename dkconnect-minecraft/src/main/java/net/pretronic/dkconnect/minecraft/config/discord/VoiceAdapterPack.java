package net.pretronic.dkconnect.minecraft.config.discord;

import org.mcnative.runtime.api.event.player.MinecraftPlayerLogoutEvent;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerPostLoginEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class VoiceAdapterPack {

    public static final Map<String, Function<VoiceAdapterPack, MinecraftEventMessageTrigger>> REGISTRY = new ConcurrentHashMap<>();

    static {
        REGISTRY.put("dkbans-notification-punishment", new DKBansPunishmentNotification());
        REGISTRY.put("join", new JoinNotification());
        REGISTRY.put("leave", new LeaveNotification());
    }

    private final String name;
    private final String channelId;
    private final String message;
    private final String embedKey;

    public VoiceAdapterPack(String name, String channelId, String message, String embedKey) {
        this.name = name;
        this.channelId = channelId;
        this.message = message;
        this.embedKey = embedKey;
    }

    public VoiceAdapterPack(String name, String channelId) {
        this(name, channelId, null, null);
    }

    public String getName() {
        return name;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getMessage() {
        return message;
    }

    public String getEmbedKey() {
        return embedKey;
    }

    public MinecraftEventMessageTrigger toTrigger() {
        Function<VoiceAdapterPack, MinecraftEventMessageTrigger> creator = REGISTRY.get(name);
        if(creator == null) throw new IllegalArgumentException("Can't find pack " + name);
        return creator.apply(this);
    }


    private static class DKBansPunishmentNotification implements Function<VoiceAdapterPack, MinecraftEventMessageTrigger> {

        @Override
        public MinecraftEventMessageTrigger apply(VoiceAdapterPack voiceAdapterPack) {
            String embedKey = null;
            String message = voiceAdapterPack.getMessage();
            if(voiceAdapterPack.getMessage() == null && voiceAdapterPack.getEmbedKey() == null) {
                embedKey = "dkconnect.voiceadapter.discord.notification.punishment";
            }

            String eventClass = "net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishEvent";
            String channelId = voiceAdapterPack.getChannelId();


            return new MinecraftEventMessageTrigger(eventClass, channelId, message, embedKey);
        }
    }

    private static class JoinNotification implements Function<VoiceAdapterPack, MinecraftEventMessageTrigger> {

        @Override
        public MinecraftEventMessageTrigger apply(VoiceAdapterPack voiceAdapterPack) {
            String embedKey = null;
            String message = voiceAdapterPack.getMessage();
            if(voiceAdapterPack.getMessage() == null && voiceAdapterPack.getEmbedKey() == null) {
                embedKey = "dkconnect.voiceadapter.discord.notification.join";
            }

            String eventClass = MinecraftPlayerPostLoginEvent.class.getName();
            String channelId = voiceAdapterPack.getChannelId();


            return new MinecraftEventMessageTrigger(eventClass, channelId, message, embedKey);
        }
    }

    private static class LeaveNotification implements Function<VoiceAdapterPack, MinecraftEventMessageTrigger> {

        @Override
        public MinecraftEventMessageTrigger apply(VoiceAdapterPack voiceAdapterPack) {
            String embedKey = null;
            String message = voiceAdapterPack.getMessage();
            if(voiceAdapterPack.getMessage() == null && voiceAdapterPack.getEmbedKey() == null) {
                embedKey = "dkconnect.voiceadapter.discord.notification.leave";
            }

            String eventClass = MinecraftPlayerLogoutEvent.class.getName();
            String channelId = voiceAdapterPack.getChannelId();


            return new MinecraftEventMessageTrigger(eventClass, channelId, message, embedKey);
        }
    }
}
