package net.pretronic.dkconnect.voiceadapter.discord;


import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.voiceadapter.Emoji;
import net.pretronic.dkconnect.api.voiceadapter.Message;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapterUser;
import net.pretronic.dkconnect.api.voiceadapter.channel.Channel;
import net.pretronic.dkconnect.voiceadapter.discord.channel.DiscordTextChannel;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.utility.Iterators;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class InternalDiscordBotListener {

    private final DKConnect dkConnect;

    public InternalDiscordBotListener(DKConnect dkConnect) {
        this.dkConnect = dkConnect;
    }

    @Listener
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().trim();
        for (VoiceAdapter voiceAdapter0 : dkConnect.getVoiceAdapters()) {
            DiscordVoiceAdapter voiceAdapter = (DiscordVoiceAdapter) voiceAdapter0;
            if(message.startsWith(voiceAdapter.getCommandPrefix())) {
                message = message.substring(1);
                voiceAdapter.getCommandManager().dispatchCommand(new DiscordCommandSender(voiceAdapter, event.getChannel()), message);
            }
            break;
        }
    }

    @Listener
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        DiscordVoiceAdapter voiceAdapter = getVoiceAdapter(event.getGuild().getId());
        if(voiceAdapter != null) {
            Channel channel = Iterators.findOne(voiceAdapter.getChannels(), channel1 -> channel1.getId().equals(event.getChannel().getId()));
            if(channel instanceof DiscordTextChannel) {
                for (Consumer<Message> listener : ((DiscordTextChannel) channel).getMessageReceiveListeners()) {
                    listener.accept(new DiscordMessage(voiceAdapter, (DiscordTextChannel) channel, event.getMessage()));
                }
            }
        }
    }

    @Listener
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        DiscordVoiceAdapter voiceAdapter = getVoiceAdapter(event.getGuild().getId());
        if(voiceAdapter != null) {
            for (Channel channel0 : voiceAdapter.getChannels()) {
               if(channel0 instanceof DiscordTextChannel) {
                   DiscordTextChannel channel = (DiscordTextChannel) channel0;
                   Consumer<Message.ReactionAddEvent> listener = channel.getMessageReactionAddListeners().get(event.getMessageId());
                   if(listener != null) {
                       listener.accept(new Message.ReactionAddEvent() {
                           @Override
                           public VoiceAdapterUser getUser() {
                               return new DiscordVoiceAdapterUser(voiceAdapter, event.getUser());
                           }

                           @Override
                           public Emoji getEmoji() {
                               return new DiscordEmoji(event.getReaction().getReactionEmote());
                           }

                           @Override
                           public CompletableFuture<Boolean> removeReaction() {
                               CompletableFuture<Boolean> future = new CompletableFuture<>();
                               event.getReaction().removeReaction().queue(unused -> future.complete(true), throwable -> future.complete(false));
                               return future;
                           }
                       });
                   }
               }
            }
        }
    }

    private DiscordVoiceAdapter getVoiceAdapter(String guildId) {
        VoiceAdapter voiceAdapter = Iterators.findOne(this.dkConnect.getVoiceAdapters(), voiceAdapter0 -> ((DiscordVoiceAdapter)voiceAdapter0).getGuild().getId().equals(guildId));
        if(voiceAdapter != null) return (DiscordVoiceAdapter) voiceAdapter;
        return null;
    }
}
