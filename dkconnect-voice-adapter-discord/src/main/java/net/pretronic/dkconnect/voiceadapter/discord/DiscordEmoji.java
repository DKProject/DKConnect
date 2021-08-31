package net.pretronic.dkconnect.voiceadapter.discord;

import com.vdurmont.emoji.EmojiManager;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.pretronic.dkconnect.api.voiceadapter.Emoji;
import net.pretronic.libraries.utility.GeneralUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DiscordEmoji implements Emoji {

    private final String unicode;

    private final String emoteName;
    private final long emoteId;

    public DiscordEmoji(String value) {
        if(GeneralUtil.isNaturalNumber(value)) {
            this.emoteId = Long.parseLong(value);

            this.emoteName = null;
            this.unicode = null;
        } else if(EmojiManager.isEmoji(value)) {
            this.unicode = value;

            this.emoteName = null;
            this.emoteId = 0;
        } else {
            this.emoteName = value;

            this.unicode = null;
            this.emoteId = 0;
        }
    }

    public DiscordEmoji(String unicode, String emoteName, long emoteId) {
        this.unicode = unicode;
        this.emoteName = emoteName;
        this.emoteId = emoteId;
    }

    public DiscordEmoji(MessageReaction.ReactionEmote emote) {
        if(emote.isEmoji()) {
            this.unicode = emote.getEmoji();
            this.emoteName = null;
            this.emoteId = 0;
        } else {
            this.emoteId = emote.getEmote().getIdLong();
            this.emoteName = emote.getEmote().getName();
            this.unicode = null;
        }
    }

    @Override
    public String getName() {
        if(this.unicode != null) return unicode;
        else if(emoteId > 0) return String.valueOf(emoteId);
        else if(this.emoteName != null) return emoteName;
        throw new IllegalArgumentException("Emote not found");
    }

    public CompletableFuture<Boolean> addReaction(Message message) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(this.unicode != null) {
            message.addReaction(this.unicode).queue(unused -> future.complete(true), throwable -> {
                throwable.printStackTrace();
                future.complete(false);
            });
        } else if(this.emoteId > 0) {
            Emote emote = message.getGuild().getEmoteById(this.emoteId);
            if(emote == null) {
                System.out.println("Emote by id is null");
                future.complete(false);
            } else {
                message.addReaction(emote).queue(unused -> future.complete(true), throwable -> {
                    throwable.printStackTrace();
                    future.complete(false);
                });
            }
        } else if(this.emoteName != null) {
            List<Emote> emotes = message.getGuild().getEmotesByName(this.emoteName, true);
            if(emotes.isEmpty()) {
                System.out.println("Emotes empty");
                future.complete(false);
            } else {
                for (Emote emote : message.getGuild().getEmotesByName(this.emoteName, true)) {
                    message.addReaction(emote).queue(unused -> future.complete(true), throwable -> {
                        throwable.printStackTrace();
                        future.complete(false);
                    });
                    future.complete(true);
                    break;
                }
            }


        }
        return future;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        System.out.println("Equals check " + emoteName + ":" + emoteId + ":" + unicode);
        if(obj instanceof DiscordEmoji) {
            DiscordEmoji other = (DiscordEmoji) obj;
            if(emoteId > 0 && other.emoteId == emoteId) return true;
            if(emoteName != null && other.emoteName.equalsIgnoreCase(emoteName)) return true;
            return unicode != null && unicode.equals(other.unicode);
        }
        return false;
    }
}
