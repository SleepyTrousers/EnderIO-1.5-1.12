package com.enderio.base.common.blockentity;

import com.enderio.base.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.screen.IIcon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.UnaryOperator;

public enum RedstoneControl implements IIcon {
    ALWAYS_ACTIVE(bool -> true, EIOLang.REDSTONE_ALWAYS_ACTIVE),
    ACTIVE_WITH_SIGNAL(bool -> bool, EIOLang.REDSTONE_ACTIVE_WITH_SIGNAL),
    ACTIVE_WITHOUT_SIGNAL(bool -> !bool, EIOLang.REDSTONE_ACTIVE_WITHOUT_SIGNAL),
    NEVER_ACTIVE(bool -> false, EIOLang.REDSTONE_NEVER_ACTIVE);
    UnaryOperator<Boolean> isActive;
    private static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/icons/redstone_control.png");
    private static final Pair<Integer, Integer> SIZE = Pair.of(12, 12);
    private final Pair<Integer, Integer> pos;
    private final Component tooltip;

    RedstoneControl(UnaryOperator<Boolean> isActive, Component tooltip) {
        this.isActive = isActive;
        pos = Pair.of(12*ordinal(), 0);
        this.tooltip = tooltip;
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

    @Override
    public Component getTooltip() {
        return tooltip;
    }
}
