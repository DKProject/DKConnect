package net.pretronic.dkconnect.minecraft.config.discord;

import net.pretronic.dkconnect.api.voiceadapter.channel.TextChannel;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;

public class ChatSync {

    private transient VoiceAdapter voiceAdapter;

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

    public TextChannel getDiscordTextChannel() {
        return getVoiceAdapter().getTextChannel(getDiscordChannelId());
    }

    public void sendMessage(Textable text, VariableSet variables) {
        getDiscordTextChannel().sendMessage(text, variables);
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

    public VoiceAdapter getVoiceAdapter() {
        if(voiceAdapter == null) throw new IllegalArgumentException("VoiceAdapter not initialized yet");
        return voiceAdapter;
    }

    public void setVoiceAdapter(VoiceAdapter voiceAdapter) {
        this.voiceAdapter = voiceAdapter;
    }
}
