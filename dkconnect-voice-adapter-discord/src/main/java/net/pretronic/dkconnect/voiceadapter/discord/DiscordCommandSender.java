package net.pretronic.dkconnect.voiceadapter.discord;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.pretronic.dkconnect.api.player.DKConnectPlayer;
import net.pretronic.dkconnect.common.voiceadapter.VoiceAdapterCommandSender;
import net.pretronic.dkconnect.voiceadapter.discord.message.DiscordMessage;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;

public class DiscordCommandSender extends VoiceAdapterCommandSender<DiscordVoiceAdapter> {

    private final PrivateChannel privateChannel;

    public DiscordCommandSender(DiscordVoiceAdapter voiceAdapter, PrivateChannel privateChannel) {
        super(voiceAdapter);
        this.privateChannel = privateChannel;
    }

    @Override
    public String getName() {
        return this.privateChannel.getUser().getName()+"#"+this.privateChannel.getUser().getDiscriminator();
    }

    @Override
    public String getId() {
        return this.privateChannel.getUser().getId();
    }

    @Override
    public void sendMessage(String message) {
        this.privateChannel.sendMessage(message).queue();
    }

    @Override
    public void sendMessage(Textable textable, VariableSet variables) {
        variables.add("commandPrefix", voiceAdapter.getCommandPrefix());
        if(textable instanceof DiscordMessage) {
            DKConnectPlayer player = getDKConnectPlayer();
            ((DiscordMessage)textable).send(this.privateChannel, player == null ? null : player.getLanguage(), variables);
        } else {
            sendMessage(textable.toText(variables));
        }
    }

    public PrivateChannel getPrivateChannel() {
        return privateChannel;
    }
}
