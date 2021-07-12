package net.pretronic.dkconnect.voiceadapter.discord;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import net.pretronic.libraries.utility.reflect.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MappedEventManager implements IEventManager {

    private final ObjectOwner objectOwner;
    private final EventBus original;

    public MappedEventManager(ObjectOwner objectOwner, EventBus original) {
        this.objectOwner = objectOwner;
        this.original = original;
    }

    @Override
    public void register(@NotNull Object listener) {
        this.original.subscribe(this.objectOwner, listener);
    }

    @Override
    public void unregister(@NotNull Object listener) {
        this.original.unsubscribe(listener);
    }

    @Override
    public void handle(@NotNull GenericEvent event) {
        this.original.callEvent(event);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return ReflectionUtil.getFieldValue(this.original, "executors", List.class);
    }
}
