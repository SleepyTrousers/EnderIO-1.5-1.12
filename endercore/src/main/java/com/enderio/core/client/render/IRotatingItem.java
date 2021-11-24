package com.enderio.core.client.render;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;

import java.util.function.Consumer;

public interface IRotatingItem {
    float getTicksPerRotation();

    default void setupBEWLR(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {

            // Minecraft can be null during datagen
            final NonNullLazy<BlockEntityWithoutLevelRenderer> renderer = NonNullLazy.of(() -> RotatingItemBEWLR.INSTANCE);

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer.get();
            }
        });
    }
}
