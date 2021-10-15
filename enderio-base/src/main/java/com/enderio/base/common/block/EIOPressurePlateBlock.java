package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PressurePlateBlock;

import java.util.ArrayList;
import java.util.List;

public class EIOPressurePlateBlock extends PressurePlateBlock {

    @FunctionalInterface
    public interface DetectorType {
        int getSignalStrength(Level pLevel, BlockPos pPos);
    }

    public static DetectorType VANILLA = null;

    public static DetectorType PLAYER = (pLevel, pPos) -> {
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

    public static DetectorType HOSTILE_MOB = (pLevel, pPos) -> {
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
    private final DetectorType type;

    public EIOPressurePlateBlock(Properties pPropertiesn,DetectorType type, boolean silent) {
        this(pPropertiesn, Sensitivity.MOBS, type, silent);
    }

    public EIOPressurePlateBlock(Properties pPropertiesn, Sensitivity sensitivity, DetectorType type, boolean silent) {
        super(sensitivity, pPropertiesn);
        this.silent  =silent;
        this.type = type;
    }

    protected int getSignalStrength(Level pLevel, BlockPos pPos) {
        if(type == null) {
            return super.getSignalStrength(pLevel, pPos);
        }
        return type.getSignalStrength(pLevel, pPos);

    }

    protected void playOnSound(LevelAccessor pLevel, BlockPos pPos) {
        if(silent) {
            return;
        }
        super.playOnSound(pLevel, pPos);
    }

    protected void playOffSound(LevelAccessor pLevel, BlockPos pPos) {
        if(silent) {
            return;
        }
        super.playOffSound(pLevel, pPos);
    }
}
