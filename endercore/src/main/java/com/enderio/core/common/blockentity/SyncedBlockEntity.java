package com.enderio.core.common.blockentity;

import com.enderio.core.common.blockentity.sync.EnderDataSlot;
import com.enderio.core.common.blockentity.sync.SyncMode;
import com.enderio.core.common.util.CallOnly;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class SyncedBlockEntity extends BlockEntity {

    /**
     * This list is needed to send a full update packet to Players who started tracking this BlockEntity.
     * The Clients only receive changed data to reduce the amount of Data sent to the client.
     */
    private final List<UUID> lastSyncedToPlayers = new ArrayList<>();

    @Getter
    private final List<EnderDataSlot<?>> dataSlots = new ArrayList<>();

    @Getter
    private final List<EnderDataSlot<?>> clientDecidingDataSlots = new ArrayList<>();

    public SyncedBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    /**
     * call this ticker method to sync all renderData to the Client
     */
    public void tick() {
        if (!level.isClientSide) {
            sync();
        }
        setChanged();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return createUpdatePacket(false, SyncMode.RENDER);
    }

    /**
     * create the ClientBoundBlockEntityDataPacket for this BlockEntity
     * @param fullUpdate if this packet should send all information (this is used for players who started tracking this BlockEntity)
     * @return the UpdatePacket
     */
    @Nullable
    public ClientboundBlockEntityDataPacket createUpdatePacket(boolean fullUpdate, SyncMode mode) {
        CompoundTag nbt = new CompoundTag();
        ListTag listNBT = new ListTag();
        for (int i = 0; i < dataSlots.size(); i++) {
            EnderDataSlot<?> dataSlot = dataSlots.get(i);
            if (dataSlot.getSyncMode() == mode) {
                Optional<CompoundTag> optionalNBT = fullUpdate ? Optional.of(dataSlot.toFullNBT()) : dataSlot.toOptionalNBT();

                if (optionalNBT.isPresent()) {
                    CompoundTag elementNBT = optionalNBT.get();
                    elementNBT.putInt("dataSlotIndex", i);
                    listNBT.add(elementNBT);
                }
            }
        }
        if (listNBT.isEmpty())
            return null;
        nbt.put("data", listNBT);
        return new ClientboundBlockEntityDataPacket(this.getBlockPos(), -1, nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();

        ListTag listNBT = nbt.getList("data", Constants.NBT.TAG_COMPOUND);
        for (Tag tag : listNBT) {
            CompoundTag elementNBT = (CompoundTag) tag;
            int dataSlotIndex = elementNBT.getInt("dataSlotIndex");
            dataSlots.get(dataSlotIndex).handleNBT(elementNBT);
        }
    }

    public void addDataSlot(EnderDataSlot<?> slot) {
        dataSlots.add(slot);
    }

    public void addClientDecidingDataSlot(EnderDataSlot<?> slot) {
        clientDecidingDataSlots.add(slot);
    }

    public void add2WayDataSlot(EnderDataSlot<?> slot) {
        addDataSlot(slot);
        addClientDecidingDataSlot(slot);
    }

    /**
     * Sync the BlockEntity to all tracking players.
     */
    @CallOnly(LogicalSide.SERVER)
    private void sync() {
        ClientboundBlockEntityDataPacket fullUpdate = createUpdatePacket(true, SyncMode.RENDER);
        ClientboundBlockEntityDataPacket partialUpdate = getUpdatePacket();

        List<UUID> currentlyTracking = new ArrayList<>();

        getTrackingPlayers().forEach(serverPlayer -> {
            currentlyTracking.add(serverPlayer.getUUID());
            if (lastSyncedToPlayers.contains(serverPlayer.getUUID())) {
                sendPacket(serverPlayer, partialUpdate);
            } else {
                sendPacket(serverPlayer, fullUpdate);
            }
        });
        lastSyncedToPlayers.clear();
        lastSyncedToPlayers.addAll(currentlyTracking);
    }

    public void sendPacket(ServerPlayer player, @Nullable Packet<?> packet) {
        if (packet != null)
            player.connection.send(packet);
    }

    /**
     * never call this on client
     * @return all ServerPlayers tracking this BlockEntity
     */
    @CallOnly(LogicalSide.SERVER)
    private Stream<ServerPlayer> getTrackingPlayers() {
        return ((ServerChunkCache)level.getChunkSource()).chunkMap.getPlayers(new ChunkPos(worldPosition), false);
    }
}
