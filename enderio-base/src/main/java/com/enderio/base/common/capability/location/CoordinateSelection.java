package com.enderio.base.common.capability.location;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

/**
 * This class is in this package, because it's not only used by the item, but also by machines
 */

public class CoordinateSelection {

    private BlockPos pos = BlockPos.ZERO;
    private ResourceLocation level = new ResourceLocation("", "");

    public static CoordinateSelection of(Level level, BlockPos pos) {
        CoordinateSelection selection = new CoordinateSelection();
        selection.setLevel(level.dimension().location());
        selection.setPos(pos);
        return selection;
    }

    public static CoordinateSelection of(ResourceLocation level, BlockPos pos) {
        CoordinateSelection selection = new CoordinateSelection();
        selection.level = level;
        selection.setPos(pos);
        return selection;
    }

    public static String getLevelName(ResourceLocation level) {
        return level.getNamespace().equals("minecraft") ? level.getPath() : level.toString();
    }
    public String getLevelName() {
        return getLevelName(getLevel());
    }
    /**
     * Only call on Serverside, the only Level the Client knows about is {@link net.minecraft.client.Minecraft#level}
     * @return the level of this Selection or null if no level is found
     */
    @Nullable
    public Level getLevelInstance() {
        return ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, getLevel()));
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public ResourceLocation getLevel() {
        return level;
    }

    public void setLevel(ResourceLocation level) {
        this.level = level;
    }
}
