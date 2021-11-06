package com.enderio.base.common.item.tool;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.List;

public class ElectromagnetItem extends PoweredToggledItem {


    private static final double COLLISION_DISTANCE_SQ = 1.25 * 1.25;
    private static final double SPEED = 0.035;
    private static final double SPEED_4 = SPEED * 4;

    public ElectromagnetItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected int getEnergyUse() {
        //TODO: Config
        return 1;
    }

    @Override
    protected int getMaxEnergy() {
        //TODO: Config
        return 100000;
    }

    private int getRange() {
        //TODO: Config
        return 5;
    }

    private int getMaxItems() {
        //TODO: Config
        return 20;
    }

    private boolean isBlackListed(ItemEntity entity) {
        //TODO: Config
        return false;
    }

    private boolean isMagnetable(Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            return !isBlackListed(itemEntity);
        }
        return entity instanceof ExperienceOrb;
    }

    @Override
    protected void onTickWhenActive(Player player, @Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull Entity pEntity, int pSlotId, boolean pIsSelected) {

        int range = getRange();
        AABB bounds = new AABB(player.getX() - range, player.getY() - range, player.getZ() - range,
            player.getX() + range, player.getY() + range,player.getZ() + range);

        List<Entity> toMove = pLevel.getEntities(player, bounds, this::isMagnetable);

        int itemsRemaining = getMaxItems();
        if (itemsRemaining <= 0) {
            itemsRemaining = Integer.MAX_VALUE;
        }

        for (Entity entity : toMove) {

            double x = player.getX() - entity.getX();
            //for y value, make attraction point a little bellow eye level for best visual effect
            double y = player.getY() + player.getEyeHeight() * .75f - entity.getY();
            double z = player.getZ() - entity.getZ();

            double distanceSq = x * x + y * y + z * z;

            if (distanceSq < COLLISION_DISTANCE_SQ) {
                entity.playerTouch(player);
            } else {
                double adjustedSpeed = SPEED_4 / distanceSq;
                Vec3 mov = entity.getDeltaMovement();
                double deltaX = mov.x + x * adjustedSpeed;
                double deltaZ = mov.z + z * adjustedSpeed;
                double deltaY;
                if (y > 0) {
                    //if items are below, raise them to player level at a fixed rate
                    deltaY = 0.12;
                } else {
                    //Scaling y speed based on distance works poorly due to 'gravity' so use fixed speed
                    deltaY = mov.y + y * SPEED;
                }
                entity.setDeltaMovement(deltaX, deltaY, deltaZ);
            }

            if (itemsRemaining-- <= 0) {
                return;
            }
        }
    }

}
