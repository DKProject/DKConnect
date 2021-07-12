package net.pretronic.dkconnect.minecraft.config.discord;

public class DiscordEventMessageTrigger {

    private final String eventClass;
    private final String messageKey;

    public DiscordEventMessageTrigger(String eventClass, String messageKey) {
        this.eventClass = eventClass;
        this.messageKey = messageKey;
    }

    public Class<?> getEventClass() {
        try {
            return Class.forName(eventClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessageKey() {
        return messageKey;
    }
}
