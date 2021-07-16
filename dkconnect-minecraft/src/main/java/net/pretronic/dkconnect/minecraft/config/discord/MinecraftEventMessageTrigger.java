package net.pretronic.dkconnect.minecraft.config.discord;

public class MinecraftEventMessageTrigger {

    private final String name;
    private final String eventClass;
    private final String channelId;
    private final String message;
    private final String embedKey;

    public MinecraftEventMessageTrigger(String name, String eventClass, String channelId, String message, String embedKey) {
        this.name = name;
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

    public String getEventClassName() {
        return this.eventClass;
    }

    public boolean isAvailable() {
        try {
            Class.forName(eventClass);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
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
}
