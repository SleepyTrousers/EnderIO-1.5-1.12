package com.enderio.decoration.common.entity;

import com.enderio.decoration.common.util.PaintUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PaintedSandEntity extends FallingBlockEntity implements IEntityAdditionalSpawnData {

    public PaintedSandEntity(EntityType<? extends FallingBlockEntity> p_31950_, Level level) {
        super(p_31950_, level);
    }

    public PaintedSandEntity(Level p_31953_, double p_31954_, double p_31955_, double p_31956_, BlockState p_31957_) {
        super(p_31953_, p_31954_, p_31955_, p_31956_, p_31957_);
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nonnull
    @Override
    public EntityType<?> getType() {
        return DecorEntities.PAINTED_SAND.get();
    }

    public Block getPaint() {
        if (blockData != null) {
            return PaintUtils.getBlockFromRL(blockData.getString("paint"));
        }
        return null;
    }

    public void setPaint(Block block) {
        if (blockData == null)
            blockData = new CompoundTag();
        blockData.putString("paint", Objects.requireNonNull(block.getRegistryName()).toString());
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        Block block = getPaint();
        buffer.writeResourceLocation(block != null ? Objects.requireNonNull(block.getRegistryName()) : new ResourceLocation(""));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        ResourceLocation rl = additionalData.readResourceLocation();
        Block block = ForgeRegistries.BLOCKS.getValue(rl);
        if (block != Blocks.AIR)
            setPaint(block);
    }
}
