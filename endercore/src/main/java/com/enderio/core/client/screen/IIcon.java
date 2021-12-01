package com.enderio.core.client.screen;

import com.enderio.core.common.util.Vector2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public interface IIcon {
    Vector2i DEFAULT_TEXTURE_SIZE = new Vector2i(256, 256);

    ResourceLocation getTextureLocation();

    Vector2i getIconSize();

    Vector2i getTexturePosition();

    default Component getTooltip() {
        return TextComponent.EMPTY;
    }

    default Vector2i getTextureSize() {
        return DEFAULT_TEXTURE_SIZE;
    }
}
