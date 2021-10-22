package com.enderio.decoration.common.blockentity;

import com.enderio.decoration.common.util.PaintUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class SinglePaintedBlockEntity extends BlockEntity implements IPaintableBlockEntity {

    private Block paint;

    public Block getPaint() {
        return paint;
    }

    public static final ModelProperty<Block> PAINT = new ModelProperty<>();

    public SinglePaintedBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(PAINT, paint).build();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        writePaint(tag);
        return new ClientboundBlockEntityDataPacket(worldPosition, -1, tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        Block oldPaint = paint;
        CompoundTag tag = pkt.getTag();
        handleUpdateTag(tag);
        if (oldPaint != paint) {
            ModelDataManager.requestModelDataRefresh(this);
            if (level != null) {
                level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()), 9);
            }
        }
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        readPaint(tag);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        writePaint(nbt);
        return nbt;
    }

    // TODO: This should probably be converted to a capability.
    protected void readPaint(CompoundTag tag) {
        if (tag.contains("paint")) {
            paint = PaintUtils.getBlockFromRL(tag.getString("paint"));
            if (level != null) {
                if (level.isClientSide) {
                    ModelDataManager.requestModelDataRefresh(this);
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                        Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag tag) {
        writePaint(tag);
        return super.save(tag);
    }

    protected void writePaint(CompoundTag tag) {
        if (paint != null) {
            tag.putString("paint", Objects.requireNonNull(paint.getRegistryName()).toString());
        }
    }
}
