package net.pretronic.dkconnect.voiceadapter.discord;

import net.dv8tion.jda.api.entities.TextChannel;
import net.pretronic.dkconnect.common.voiceadapter.DefaultStaticMessage;
import net.pretronic.dkconnect.voiceadapter.discord.message.DiscordMessage;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;

public class DiscordStaticMessage extends DefaultStaticMessage<DiscordVoiceAdapter> {

    protected DiscordStaticMessage(DiscordVoiceAdapter voiceAdapter, String name, String channelId, String messageId) {
        super(voiceAdapter, name, channelId, messageId);
    }

    @Override
    public void update(Textable text, Language language, VariableSet variables) {
        TextChannel channel = DiscordBotUtil.getTextChannel(getVoiceAdapter(), getChannelId());
        if(text instanceof DiscordMessage) {
            channel.editMessageById(getMessageId(), ((DiscordMessage)text).build(language, variables)).queue();
        } else {
            channel.editMessageById(getMessageId(), text.toText(variables)).queue();
        }
    }
}
