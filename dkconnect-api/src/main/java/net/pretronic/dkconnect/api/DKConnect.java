package net.pretronic.dkconnect.api;

import net.pretronic.dkconnect.api.player.PlayerManager;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.libraries.event.EventBus;

import java.util.Collection;

public interface DKConnect {

    PlayerManager getPlayerManager();

    EventBus getEventBus();

    Collection<VoiceAdapter> getVoiceAdapters();

    VoiceAdapter getVoiceAdapter(String name);

    void registerVoiceAdapter(VoiceAdapter voiceAdapter);
}
