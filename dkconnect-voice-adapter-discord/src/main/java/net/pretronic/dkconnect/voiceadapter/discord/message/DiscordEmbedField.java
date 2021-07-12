package net.pretronic.dkconnect.voiceadapter.discord.message;

import net.pretronic.libraries.message.Textable;

public class DiscordEmbedField {

    private final String name;
    private final String value;
    private final boolean inline;

    public DiscordEmbedField(String name, String value, boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public boolean isInline() {
        return inline;
    }
}
