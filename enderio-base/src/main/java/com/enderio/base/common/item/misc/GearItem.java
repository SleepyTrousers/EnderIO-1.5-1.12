package com.enderio.base.common.item.misc;

import java.util.function.Consumer;

import com.enderio.core.client.render.IRotatingItem;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;

public class GearItem extends MaterialItem implements IRotatingItem {

    private final float tpr;

    public GearItem(Properties props, float tpr) {
        super(props, false);
        this.tpr = tpr;
    }

    // enables the use of a BEWLR
    @Override
    public void initializeClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
        setupBEWLR(consumer);
    }

    @Override
    public float getTicksPerRotation() {
        return tpr;
    }
}
