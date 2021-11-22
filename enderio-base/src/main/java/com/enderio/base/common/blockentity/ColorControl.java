package com.enderio.base.common.blockentity;

import com.enderio.base.EnderIO;
import com.enderio.core.client.screen.IIcon;
import com.enderio.core.common.util.Vector2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum ColorControl implements IIcon {
    GREEN(),
    BROWN(),
    BLUE(),
    PURPLE(),
    CYAN(),
    LIGHT_GRAY(),
    GRAY(),
    PINK(),
    LIME(),
    YELLOW(),
    LIGHT_BLUE(),
    MAGENTA(),
    ORANGE(),
    WHITE(),
    BLACK(),
    RED();

    private static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/icons/color_control.png");
    private static final Vector2i SIZE = new Vector2i(12, 12);
    private final Vector2i pos;

    ColorControl() {
        pos = new Vector2i(12 * ordinal(), 0);
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public Component getTooltip() {
        return new TextComponent(name().toLowerCase(Locale.ROOT).replace('_', ' '));
    }

    @Override
    public Vector2i getIconSize() {
        return SIZE;
    }

    @Override
    public Vector2i getTexturePosition() {
        return pos;
    }
}
