package net.pretronic.dkconnect.minecraft.config.discord;

public class ChatSync {

    private final boolean enabled;

    private String discordChannelId;
    private final String discordMessage;
    private final String discordEmbedKey;

    private final String minecraftMessage;


    public ChatSync(boolean enabled, String discordChannelId, String discordMessage, String discordEmbedKey, String minecraftMessage) {
        this.enabled = enabled;
        this.discordChannelId = discordChannelId;
        this.discordMessage = discordMessage;
        this.discordEmbedKey = discordEmbedKey;
        this.minecraftMessage = minecraftMessage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDiscordChannelId() {
        return discordChannelId;
    }

    public String getDiscordMessage() {
        return discordMessage;
    }

    public String getDiscordEmbedKey() {
        return discordEmbedKey;
    }

    public String getMinecraftMessage() {
        return minecraftMessage;
    }
}
