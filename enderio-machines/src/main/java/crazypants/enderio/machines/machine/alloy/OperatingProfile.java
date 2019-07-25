package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.machines.lang.Lang;

public enum OperatingProfile {
  SIMPLE_ALLOY(OperatingMode.ALLOY, "simple_alloy_smelter"),
  SIMPLE_FURNACE(OperatingMode.FURNACE, "simple_furnace"),
  ALLOY(OperatingMode.ALLOY, "alloy_smelter_alloy") {
    @Override
    @Nonnull
    IWidgetIcon getIcon() {
      return IconEIO.ALLOY_MODE_ALLOY;
    }

    @Override
    @Nonnull
    Lang getLang() {
      return Lang.GUI_ALLOY_MODE_ALLOY;
    }
  },
  FURNACE(OperatingMode.FURNACE, "alloy_smelter_furnace") {
    @Override
    @Nonnull
    IWidgetIcon getIcon() {
      return IconEIO.ALLOY_MODE_FURNACE;
    }

    @Override
    @Nonnull
    Lang getLang() {
      return Lang.GUI_ALLOY_MODE_FURNACE;
    }
  },
  AUTO(OperatingMode.ALL, "alloy_smelter_auto"),
  ALLOY_ONLY(OperatingMode.ALLOY, "alloy_smelter_alloy"),
  FURNACE_ONLY(OperatingMode.FURNACE, "alloy_smelter_furnace");

  private final @Nonnull String guiTexture;
  private final @Nonnull OperatingMode operatingMode;

  private OperatingProfile(@Nonnull OperatingMode operatingMode, @Nonnull String guiTexture) {
    this.operatingMode = operatingMode;
    this.guiTexture = guiTexture;
  }

  static @Nonnull String[] getAllGuiTextures() {
    String[] result = new String[values().length];
    for (int i = 0; i < result.length; i++) {
      result[i] = values()[i].guiTexture;
    }
    return result;
  }

  int getGuiTextureID() {
    return ordinal();
  }

  boolean canSwitchProfiles() {
    return this == AUTO || this == ALLOY || this == FURNACE;
  }

  boolean hasRedstoneControl() {
    return this != SIMPLE_ALLOY && this != SIMPLE_FURNACE;
  }

  @Nonnull
  IWidgetIcon getIcon() {
    return IconEIO.ALLOY_MODE_BOTH;
  }

  @Nonnull
  Lang getLang() {
    return Lang.GUI_ALLOY_MODE_ALL;
  }

  @Nonnull
  OperatingProfile fromMode(@Nonnull OperatingMode mode) {
    if (canSwitchProfiles()) {
      switch (mode) {
      case ALL:
        return OperatingProfile.AUTO;
      case ALLOY:
        return OperatingProfile.ALLOY;
      case FURNACE:
        return OperatingProfile.FURNACE;
      default:
        throw new RuntimeException("Just found out that black is smellier than the sound of hot!");
      }
    }
    return this;
  }

  @Nonnull
  public OperatingMode getOperatingMode() {
    return operatingMode;
  }

}