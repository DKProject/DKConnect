package net.pretronic.dkconnect.minecraft.config.discord;

public class MinecraftEventMessageTrigger {

    private final String eventClass;
    private final String channelId;
    private final String message;
    private final String embedKey;

    public MinecraftEventMessageTrigger(String eventClass, String channelId, String message, String embedKey) {
        this.eventClass = eventClass;
        this.channelId = channelId;
        this.message = message;
        this.embedKey = embedKey;
    }

    public Class<?> getEventClass() {
        try {
            return Class.forName(eventClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
}
