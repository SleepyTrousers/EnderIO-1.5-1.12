package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;

import java.util.List;

public class EIOPressurePlateBlock extends PressurePlateBlock {

    @FunctionalInterface
    public interface Detector {
        int getSignalStrength(Level pLevel, BlockPos pPos);
    }

    public static Detector PLAYER = (pLevel, pPos) -> {
        net.minecraft.world.phys.AABB aabb = TOUCH_AABB.move(pPos);
        List<? extends Entity> list;
        list = pLevel.getEntitiesOfClass(Player.class, aabb);
        for (Entity entity : list) {
            if (!entity.isIgnoringBlockTriggers()) {
                return 15;
            }
        }
        return 0;
    };

    public static Detector HOSTILE_MOB = (pLevel, pPos) -> {
        net.minecraft.world.phys.AABB aabb = TOUCH_AABB.move(pPos);
        List<LivingEntity> list;
        list = pLevel.getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity entity : list) {
            if (entity instanceof Enemy && !entity.isIgnoringBlockTriggers()) {
                return 15;
            }
        }
        return 0;
    };

    private final boolean silent;
    private final Detector detector;

    public EIOPressurePlateBlock(Properties props, Detector detector, boolean silent) {
        super(Sensitivity.MOBS, props.sound(SoundType.METAL));
        this.detector = detector;
        this.silent = silent;
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected int getSignalStrength(Level pLevel, BlockPos pPos) {
        return detector.getSignalStrength(pLevel, pPos);
    }


    @Override
    protected void playOnSound(LevelAccessor pLevel, BlockPos pPos) {
        if (!silent) {
            pLevel.playSound(null, pPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.90000004F);
        }
    }

    @Override
    protected void playOffSound(LevelAccessor pLevel, BlockPos pPos) {
        if (!silent) {
            pLevel.playSound(null, pPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.75F);
        }
    }
}
