package com.enderio.base.common.blockentity;

import com.enderio.base.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.screen.IIcon;
import com.enderio.core.common.util.Vector2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public enum RedstoneControl implements IIcon {
    ALWAYS_ACTIVE(bool -> true, EIOLang.REDSTONE_ALWAYS_ACTIVE),
    ACTIVE_WITH_SIGNAL(bool -> bool, EIOLang.REDSTONE_ACTIVE_WITH_SIGNAL),
    ACTIVE_WITHOUT_SIGNAL(bool -> !bool, EIOLang.REDSTONE_ACTIVE_WITHOUT_SIGNAL),
    NEVER_ACTIVE(bool -> false, EIOLang.REDSTONE_NEVER_ACTIVE);
    UnaryOperator<Boolean> isActive;
    private static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/icons/redstone_control.png");
    private static final Vector2i SIZE = new Vector2i(12, 12);
    private final Vector2i pos;
    private final Component tooltip;

    RedstoneControl(UnaryOperator<Boolean> isActive, Component tooltip) {
        this.isActive = isActive;
        pos = new Vector2i(12*ordinal(), 0);
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
    public Vector2i getIconSize() {
        return SIZE;
    }

    @Override
    public Vector2i getTexturePosition() {
        return pos;
    }

    @Override
    public Component getTooltip() {
        return tooltip;
    }
}
