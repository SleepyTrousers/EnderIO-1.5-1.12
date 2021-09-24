package com.tterrag.registrate.builders;

import javax.annotation.Nullable;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.ContainerEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.RegistryObject;

public class ContainerBuilder<T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>,  P> extends AbstractBuilder<MenuType<?>, MenuType<T>, P, ContainerBuilder<T, S, P>> {
    
    public interface ContainerFactory<T extends AbstractContainerMenu> {
        
        T create(MenuType<T> type, int windowId, Inventory inv);
    }

    public interface ForgeContainerFactory<T extends AbstractContainerMenu> {

        T create(MenuType<T> type, int windowId, Inventory inv, @Nullable FriendlyByteBuf buffer);
    }
    
    public interface ScreenFactory<C extends AbstractContainerMenu, T extends Screen & MenuAccess<C>> {
        
        T create(C container, Inventory inv, Component displayName);
    }
    
    private final ForgeContainerFactory<T> factory;
    private final NonNullSupplier<ScreenFactory<T, S>> screenFactory;

    public ContainerBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
        this(owner, parent, name, callback, (type, windowId, inv, $) -> factory.create(type, windowId, inv), screenFactory);
    }

    public ContainerBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ForgeContainerFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
        super(owner, parent, name, callback, MenuType.class);
        this.factory = factory;
        this.screenFactory = screenFactory;
    }

    @Override
    protected @NonnullType MenuType<T> createEntry() {
        ForgeContainerFactory<T> factory = this.factory;
        NonNullSupplier<MenuType<T>> supplier = this.asSupplier();
        MenuType<T> ret = IForgeContainerType.create((windowId, inv, buf) -> factory.create(supplier.get(), windowId, inv, buf));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ScreenFactory<T, S> screenFactory = this.screenFactory.get();
            MenuScreens.<T, S>register(ret, (type, inv, displayName) -> screenFactory.create(type, inv, displayName));
        });
        return ret;
    }

    @Override
    protected RegistryEntry<MenuType<T>> createEntryWrapper(RegistryObject<MenuType<T>> delegate) {
        return new ContainerEntry<>(getOwner(), delegate);
    }

    @Override
    public ContainerEntry<T> register() {
        return (ContainerEntry<T>) super.register();
    }
}
