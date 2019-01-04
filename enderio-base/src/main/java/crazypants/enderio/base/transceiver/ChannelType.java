package crazypants.enderio.base.transceiver;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.gui.IconEIO;

public enum ChannelType {

  POWER(IconEIO.WRENCH_OVERLAY_POWER),
  ITEM(IconEIO.WRENCH_OVERLAY_ITEM),
  FLUID(IconEIO.WRENCH_OVERLAY_FLUID);

  private final @Nonnull IWidgetIcon widgetIcon;

  private ChannelType(@Nonnull IWidgetIcon widgetIcon) {
    this.widgetIcon = widgetIcon;
  }

  public static final NNList<ChannelType> VALUES = NNList.of(ChannelType.class);

  public @Nonnull IWidgetIcon getWidgetIcon() {
    return widgetIcon;
  }

}
