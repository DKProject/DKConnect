package net.pretronic.dkconnect.minecraft;

import net.pretronic.libraries.document.Document;
import org.mcnative.runtime.api.network.messaging.MessageReceiver;
import org.mcnative.runtime.api.network.messaging.MessagingChannelListener;

import java.util.UUID;

public class RoutedVoiceAdapterMessagingChannel implements MessagingChannelListener {

    public static final String CHANNEL_NAME = "RoutedVoiceAdapter";

    @Override
    public Document onMessageReceive(MessageReceiver messageReceiver, UUID requestId, Document request) {

        return null;
    }
}
