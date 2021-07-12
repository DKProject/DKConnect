package net.pretronic.dkconnect.voiceadapter.discord.message;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.pretronic.dkconnect.common.DefaultDKConnect;
import net.pretronic.dkconnect.voiceadapter.discord.DiscordVoiceAdapter;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.event.injection.annotations.Inject;
import net.pretronic.libraries.message.Textable;
import net.pretronic.libraries.message.bml.variable.Variable;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DiscordMessage implements Textable {

    private transient DiscordVoiceAdapter voiceAdapter;
    private final String content;
    private final List<DiscordEmbed> embeds;

    public DiscordMessage(String content, List<DiscordEmbed> embeds) {
        this.content = content;
        this.embeds = embeds;
    }

    @Override
    public String toText(VariableSet variables) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Document toDocument() {
        throw new UnsupportedOperationException();
    }

    public CompletableFuture<Message> send(MessageChannel channel, Language language, VariableSet variables) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        channel.sendMessage(build(language, variables)).queue(future::complete, Throwable::printStackTrace);
        return future;
    }

    public Message build(Language language, VariableSet variables) {
        MessageBuilder builder = new MessageBuilder();
        if(this.content != null) {
            builder.setContent(replace(this.content, language, variables, voiceAdapter.getMessageGetter()));//@Todo replace variables
        }
        if(this.embeds != null) {
            Collection<MessageEmbed> embeds = new ArrayList<>(this.embeds.size());
            for (DiscordEmbed embed : this.embeds) {
                embeds.add(embed.build(language, variables, voiceAdapter.getMessageGetter()));
            }
            builder.setEmbeds(embeds);
        }
        return builder.build();
    }

    protected static String replace(String text, Language language, VariableSet variables, Function<Triple<String, Language, VariableSet>, String> messageGetter){
        char[] content = text.toCharArray();
        StringBuilder builder = new StringBuilder(content.length);
        int start = -1;
        int startMessage = -1;
        for (int i = 0; i < content.length; i++) {
            if(content[i] == '$') {
                if(i != 0 && content[i-1] == '\\'){
                    builder.setCharAt(builder.length()-1,content[i]);
                }else{
                    startMessage = i;
                }
            } else if(content[i] == '{'){
                if(i != 0 && content[i-1] == '\\'){
                    builder.setCharAt(builder.length()-1,content[i]);
                }else{
                    if(startMessage != -1) startMessage = i;
                    else start = i;
                }
            }else if(content[i] == '}'){
                if(start != -1) {
                    String key = text.substring(start+1,i);
                    Variable variable = variables.get(key);
                    if(variable != null){
                        builder.append(variable.getObject(key));
                    }else builder.append("NULL");
                    start = -1;
                } else if(startMessage != -1) {
                    String key = text.substring(startMessage+1,i);
                    builder.append(messageGetter.apply(new Triple<>(key, language, variables)));
                    startMessage = -1;
                }

            }else if(start == -1 && startMessage == -1){
                builder.append(content[i]);
            }
        }
        return builder.toString();
    }

    @Internal
    public void setVoiceAdapter(DiscordVoiceAdapter voiceAdapter) {
        this.voiceAdapter = voiceAdapter;
    }
}
