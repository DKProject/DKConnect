package net.pretronic.dkconnect.voiceadapter.discord.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.pretronic.libraries.document.annotations.DocumentIgnored;
import net.pretronic.libraries.document.annotations.DocumentKey;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.map.Triple;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.function.Function;

public class DiscordEmbed {

    private static final float MIN_BRIGHTNESS = 0.7f;

    @DocumentKey("author.name")
    private final String authorName;
    @DocumentKey("author.url")
    private final String authorUrl;
    @DocumentKey("author.icon_url")
    private final String authorIconUrl;

    private final String title;
    private final String description;
    private final String url;
    private final String color;

    private final List<DiscordEmbedField> fields;

    @DocumentKey("image.url")
    private final String image;
    @DocumentKey("thumbnail.url")
    private final String thumbnailUrl;

    @DocumentKey("timestamp")
    private final String timestamp0;
    @DocumentIgnored
    private TemporalAccessor timestamp;

    @DocumentKey("footer.text")
    private final String footer;
    @DocumentKey("footer.icon_url")
    private final String footerIconUrl;

    public DiscordEmbed(String authorName, String authorUrl, String authorIconUrl, String title,
                        String description, String url, String color, List<DiscordEmbedField> fields,
                        String image, String thumbnailUrl, String footer, String timestamp, String footerIconUrl) {
        this.authorName = authorName;
        this.authorUrl = authorUrl;
        this.authorIconUrl = authorIconUrl;
        this.title = title;
        this.description = description;
        this.url = url;
        this.color = color;
        this.fields = fields;
        this.image = image;
        this.thumbnailUrl = thumbnailUrl;
        this.footer = footer;
        this.timestamp0 = timestamp;
        this.footerIconUrl = footerIconUrl;
    }

    public MessageEmbed build(Language language, VariableSet variables, Function<Triple<String, Language, VariableSet>, String> messageGetter) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor(convertToStringOrNull(authorName, language, variables, messageGetter),
                convertToStringOrNull(authorUrl, language, variables, messageGetter), convertToStringOrNull(authorIconUrl, language, variables, messageGetter));
        builder.setTitle(convertToStringOrNull(title, language, variables, messageGetter), convertToStringOrNull(url, language, variables, messageGetter));
        builder.setDescription(convertToStringOrNull(description, language, variables, messageGetter));
        builder.setColor(parseColor(convertToStringOrNull(color, language, variables, messageGetter)));

        if(fields != null) {
            for (DiscordEmbedField field : fields) {
                builder.addField(convertToStringOrNull(field.getName(), language, variables, messageGetter),
                        convertToStringOrNull(field.getValue(), language, variables, messageGetter), field.isInline());
            }
        }

        builder.setImage(convertToStringOrNull(image, language, variables, messageGetter));
        builder.setThumbnail(convertToStringOrNull(thumbnailUrl, language, variables, messageGetter));

        builder.setFooter(convertToStringOrNull(footer, language, variables, messageGetter),
                convertToStringOrNull(footerIconUrl, language, variables, messageGetter));
        if(timestamp0 != null) {
            if(timestamp == null) timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse("2021-06-28T22:00:00.000Z");
            builder.setTimestamp(timestamp);
        }

        return builder.build();
    }

    private String convertToStringOrNull(String text, Language language, VariableSet variables, Function<Triple<String, Language, VariableSet>, String> messageGetter) {
        if(text == null) return null;
        return DiscordMessage.replace(text, language, variables, messageGetter);//@Todo replace variables
    }

    private Color parseColor(String rawColor) {
        if(rawColor == null) return null;
        switch (rawColor) {
            case "RANDOM_BRIGHT": {
                float h = GeneralUtil.getDefaultRandom().nextFloat();
                float s = GeneralUtil.getDefaultRandom().nextFloat();
                float b = MIN_BRIGHTNESS + (1f - MIN_BRIGHTNESS) * GeneralUtil.getDefaultRandom().nextFloat();
                return Color.getHSBColor(h, s, b);
            }
            default: {
                return Color.decode(rawColor);
            }
        }
    }
}
