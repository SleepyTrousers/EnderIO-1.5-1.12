package com.enderio.machines.common.blockentity.data;

import com.enderio.base.EnderIO;
import com.enderio.base.common.menu.EIOMenus;
import com.enderio.core.client.screen.IIcon;
import com.enderio.machines.EIOMachines;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.UnaryOperator;

public enum RedstoneControl implements IIcon {
    ALWAYS_ACTIVE(bool -> true),
    ACTIVE_WITH_SIGNAL(bool -> bool),
    ACTIVE_WITHOUT_SIGNAL(bool -> !bool),
    NEVER_ACTIVE(bool -> false);
    UnaryOperator<Boolean> isActive;
    private static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/icons/redstone_control.png");
    private static final Pair<Integer, Integer> SIZE = Pair.of(12, 12);
    private final Pair<Integer, Integer> pos;

    RedstoneControl(UnaryOperator<Boolean> isActive) {
        this.isActive = isActive;
        pos = Pair.of(12*ordinal(), 0);
    }

    public boolean isActive(boolean hasRedstone) {
        return isActive.apply(hasRedstone);
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public Pair<Integer, Integer> getIconSize() {
        return SIZE;
    }

    @Override
    public Pair<Integer, Integer> getTexturePosition() {
        return pos;
    }
}
