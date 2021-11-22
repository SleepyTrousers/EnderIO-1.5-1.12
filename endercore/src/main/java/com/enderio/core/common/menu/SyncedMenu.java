package com.enderio.core.common.menu;

import com.enderio.core.common.blockentity.SyncedBlockEntity;
import com.enderio.core.common.blockentity.sync.EnderDataSlot;
import com.enderio.core.common.blockentity.sync.SyncMode;
import com.enderio.core.common.network.EnderNetwork;
import com.enderio.core.common.network.packet.SyncClientToServerMenuPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SyncedMenu<T extends SyncedBlockEntity> extends AbstractContainerMenu {

    @Nullable
    private final T blockEntity;
    private final Inventory inventory;

    private final List<EnderDataSlot<?>> clientToServerSlots = new ArrayList<>();

    protected SyncedMenu(@Nullable T blockEntity, Inventory inventory, @Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
        this.blockEntity = blockEntity;
        this.inventory = inventory;
        if (blockEntity != null) {
            clientToServerSlots.addAll(blockEntity.getClientDecidingDataSlots());
        }
    }

    protected void addClientDecidingDataSlot(EnderDataSlot<?> dataSlot) {
        clientToServerSlots.add(dataSlot);
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
        if (inventory.player instanceof ServerPlayer player && blockEntity != null) {
            blockEntity.sendPacket(player, blockEntity.createUpdatePacket(fullSync, SyncMode.GUI));
        }
    }

    public void clientTick() {
        ListTag listNBT = new ListTag();
        for (int i = 0; i < clientToServerSlots.size(); i++) {
            Optional<CompoundTag> optionalNBT = clientToServerSlots.get(0).toOptionalNBT();

            if (optionalNBT.isPresent()) {
                CompoundTag elementNBT = optionalNBT.get();
                elementNBT.putInt("dataSlotIndex", i);
                listNBT.add(elementNBT);
            }
        }
        if (!listNBT.isEmpty()) {
            EnderNetwork.getNetwork().getNetworkChannel().sendToServer(new SyncClientToServerMenuPacket(containerId, listNBT));
        }
    }

    @Nullable
    public T getBlockEntity() {
        return blockEntity;
    }

    public List<EnderDataSlot<?>> getClientToServerSlots() {
        return clientToServerSlots;
    }
}
