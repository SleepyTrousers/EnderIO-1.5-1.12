package com.enderio.core.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

public interface IIcon {

    ResourceLocation getTextureLocation();

    Pair<Integer, Integer> getIconSize();

    Pair<Integer, Integer> getTexturePosition();

    default Component getTooltip() {
        return TextComponent.EMPTY;
    }

    default Pair<Integer, Integer> getTextureSize() {
        return Pair.of(256, 256);
    }
}
