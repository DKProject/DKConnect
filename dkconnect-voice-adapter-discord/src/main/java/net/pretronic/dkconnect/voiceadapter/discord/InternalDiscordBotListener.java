package net.pretronic.dkconnect.voiceadapter.discord;


import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.pretronic.libraries.event.Listener;
import org.jetbrains.annotations.NotNull;

public class InternalDiscordBotListener {

    private final DiscordVoiceAdapter voiceAdapter;

    public InternalDiscordBotListener(DiscordVoiceAdapter voiceAdapter) {
        this.voiceAdapter = voiceAdapter;
    }

    @Listener
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if(message.startsWith(voiceAdapter.getCommandPrefix())) {
            message = message.substring(1);
            this.voiceAdapter.getCommandManager().dispatchCommand(new DiscordCommandSender(this.voiceAdapter, event.getChannel()), message);
        }
    }
}
