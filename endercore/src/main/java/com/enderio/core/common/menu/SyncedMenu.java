package com.enderio.core.common.menu;

import com.enderio.core.common.blockentity.SyncedBlockEntity;
import com.enderio.core.common.blockentity.sync.SyncMode;
import lombok.Getter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

public abstract class SyncedMenu<T extends SyncedBlockEntity> extends AbstractContainerMenu {

    @Getter
    private T blockEntity;
    private final Inventory inventory;

    protected SyncedMenu(T blockEntity, Inventory inventory, @Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
        this.blockEntity = blockEntity;
        this.inventory = inventory;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
       sync(false);
    }

    @Override
    public void broadcastFullState() {
        super.broadcastFullState();
        sync(true);
    }

    private void sync(boolean fullSync) {
        if (inventory.player instanceof ServerPlayer player) {
            blockEntity.sendPacket(player, blockEntity.createUpdatePacket(fullSync, SyncMode.GUI));
        }
    }
}
