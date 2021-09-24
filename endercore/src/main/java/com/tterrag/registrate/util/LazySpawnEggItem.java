package com.tterrag.registrate.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class LazySpawnEggItem<T extends Entity> extends SpawnEggItem {

    private final NonNullSupplier<EntityType<T>> typeIn;

    public LazySpawnEggItem(final NonNullSupplier<EntityType<T>> type, int primaryColor, int secondaryColor, Properties properties) {
        super(null, primaryColor, secondaryColor, properties);
        this.typeIn = type;
    }
    
    private static final Field _EGGS = ObfuscationReflectionHelper.findField(SpawnEggItem.class, "BY_ID");
    
    @SuppressWarnings("unchecked")
    public void injectType() {
        try {
            Map<EntityType<?>, SpawnEggItem> EGGS = (Map<EntityType<?>, SpawnEggItem>) _EGGS.get(this);
            EGGS.put(typeIn.get(), this);
            EGGS.remove(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public EntityType<?> getType(@Nullable CompoundTag p_208076_1_) {
        if (p_208076_1_ != null && p_208076_1_.contains("EntityTag", 10)) {
            return super.getType(p_208076_1_);
        }

        return this.typeIn.get();
    }

    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (block == Blocks.SPAWNER) {
                BlockEntity BlockEntity = world.getBlockEntity(blockpos);
                if (BlockEntity instanceof SpawnerBlockEntity) {
                    BaseSpawner abstractspawner = ((SpawnerBlockEntity) BlockEntity).getSpawner();
                    EntityType<?> entitytype1 = this.getType(itemstack.getTag());
                    abstractspawner.setEntityId(entitytype1);
                    BlockEntity.setChanged();
                    world.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                    itemstack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            EntityType<?> entitytype = this.getType(itemstack.getTag());
            if (entitytype.spawn((ServerLevel) world, itemstack, context.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
                itemstack.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide) {
            return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
        } else {
            HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
            if (raytraceresult.getType() != HitResult.Type.BLOCK) {
                return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
            } else {
                BlockHitResult blockraytraceresult = (BlockHitResult) raytraceresult;
                BlockPos blockpos = blockraytraceresult.getBlockPos();
                if (!(worldIn.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                    return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
                } else if (worldIn.mayInteract(playerIn, blockpos) && playerIn.mayUseItemAt(blockpos, blockraytraceresult.getDirection(), itemstack)) {
                    EntityType<?> entitytype = this.getType(itemstack.getTag());
                    if (entitytype.spawn((ServerLevel) worldIn, itemstack, playerIn, blockpos, MobSpawnType.SPAWN_EGG, false, false) == null) {
                        return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
                    } else {
                        if (!playerIn.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }

                        playerIn.awardStat(Stats.ITEM_USED.get(this));
                        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
                    }
                } else {
                    return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
                }
            }
        }
    }
}
