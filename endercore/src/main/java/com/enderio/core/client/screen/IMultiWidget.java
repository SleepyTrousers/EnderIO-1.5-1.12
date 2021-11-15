package com.enderio.core.client.screen;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.Collection;

public interface IMultiWidget {
    Collection<? extends AbstractWidget> getOtherWidgets();
}
