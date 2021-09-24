package com.tterrag.registrate.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.tterrag.registrate.util.nullness.NonnullType;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@RequiredArgsConstructor
@Log4j2
public class OneTimeEventReceiver<T extends Event> implements Consumer<@NonnullType T> {
    
    public static <T extends Event & IModBusEvent> void addModListener(Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addModListener(EventPriority.NORMAL, evtClass, listener);
    }
    
    public static <T extends Event & IModBusEvent> void addModListener(EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addListener(FMLJavaModLoadingContext.get().getModEventBus(), priority, evtClass, listener);
    }
    
    public static <T extends Event> void addForgeListener(Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addForgeListener(EventPriority.NORMAL, evtClass, listener);
    }
    
    public static <T extends Event> void addForgeListener(EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addListener(MinecraftForge.EVENT_BUS, priority, evtClass, listener);
    }
    
    public static <T extends Event> void addListener(IEventBus bus, Class<? super T> evtClass, Consumer<? super T> listener) {
        OneTimeEventReceiver.<T>addListener(bus, EventPriority.NORMAL, evtClass, listener);
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Event> void addListener(IEventBus bus, EventPriority priority, Class<? super T> evtClass, Consumer<? super T> listener) {
        bus.addListener(priority, false, (Class<T>) evtClass, new OneTimeEventReceiver<>(bus, listener));
    }

    private static final @Nullable MethodHandle getBusId;
    static {
        MethodHandle ret;
        try {
            ret = MethodHandles.lookup().unreflectGetter(ObfuscationReflectionHelper.findField(EventBus.class, "busID"));
        } catch (IllegalAccessException e) {
            log.warn("Failed to set up EventBus reflection to release one-time event listeners, leaks will occur. This is not a big deal.");
            ret = null;
        }
        getBusId = ret;

        addModListener(FMLLoadCompleteEvent.class, OneTimeEventReceiver::onLoadComplete);
    }

    private final IEventBus bus;
    private final Consumer<? super T> listener;
    private final AtomicBoolean consumed = new AtomicBoolean();

    @Override
    public void accept(T event) {
        if (consumed.compareAndSet(false, true)) {
            listener.accept(event);
            unregister(bus, this, event);
        }
    }

    private static final List<Triple<IEventBus, Object, Event>> toUnregister = new ArrayList<>();

    public static synchronized void unregister(IEventBus bus, Object listener, Event event) {
        toUnregister.add(Triple.of(bus, listener, event));
    }

    @SuppressWarnings("deprecation")
    private static void onLoadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            toUnregister.forEach(t -> {
                t.getLeft().unregister(t.getMiddle());
                try {
                    final MethodHandle mh = getBusId;
                    if (mh != null) {
                        t.getRight().getListenerList().getListeners((int) mh.invokeExact((EventBus) t.getLeft()));
                    }
                } catch (Throwable ex) {
                    log.warn("Failed to clear listener list of one-time event receiver, so the receiver has leaked. This is not a big deal.", ex);
                }
            });
            toUnregister.clear();
        });
    }
}
