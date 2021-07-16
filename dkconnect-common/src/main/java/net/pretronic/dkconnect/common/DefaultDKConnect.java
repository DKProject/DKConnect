package net.pretronic.dkconnect.common;

import net.pretronic.databasequery.api.Database;
import net.pretronic.dkconnect.api.DKConnect;
import net.pretronic.dkconnect.api.voiceadapter.VoiceAdapter;
import net.pretronic.dkconnect.api.player.PlayerManager;
import net.pretronic.dkconnect.common.player.DefaultPlayerManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.message.language.Language;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public class DefaultDKConnect implements DKConnect {

    private final PlayerManager playerManager;
    private final EventBus eventBus;
    private final Storage storage;
    private final Collection<VoiceAdapter> voiceAdapters;
    private final Function<UUID, Language> languageGetter;

    public DefaultDKConnect(EventBus eventBus, Database database, Function<UUID, Language> languageGetter) {
        this.eventBus = eventBus;
        this.languageGetter = languageGetter;
        this.playerManager = new DefaultPlayerManager(this);
        this.storage = new Storage(database);
        this.voiceAdapters = new ArrayList<>();
    }

    @Override
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public Collection<VoiceAdapter> getVoiceAdapters() {
        return voiceAdapters;
    }

    @Override
    public VoiceAdapter getVoiceAdapter(String name) {
        VoiceAdapter voiceAdapter = Iterators.findOne(voiceAdapters, adapter -> adapter.getName().equalsIgnoreCase(name));
        if(voiceAdapter == null) throw new IllegalArgumentException("Can't find voice adapter " + name);
        return voiceAdapter;
    }

    @Override
    public VoiceAdapter getVoiceAdapterByVerificationSystemName(String name) {
        VoiceAdapter voiceAdapter = Iterators.findOne(voiceAdapters, adapter -> adapter.getVerificationSystemName().equalsIgnoreCase(name));
        if(voiceAdapter == null) throw new IllegalArgumentException("Can't find voice adapter for verification system name " + name);
        return voiceAdapter;
    }

    @Override
    public void registerVoiceAdapter(VoiceAdapter voiceAdapter) {
        this.voiceAdapters.add(voiceAdapter);
    }

    public Storage getStorage() {
        return storage;
    }

    public Function<UUID, Language> getLanguageGetter() {
        return languageGetter;
    }
}
