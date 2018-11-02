package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class TeleportConfig {

  public static final IValueFactory F = BaseConfig.F.section("teleport");

  public static final IValue<Integer> rangeBlocks = F.make("defaultTeleportRangeBlocks", 96, //
      "Default range of direct travel between blocks (e.g. Travel Anchors).").setRange(16, 16 * 32).sync();

  public static final IValue<Integer> rangeItems = F.make("defaultTeleportRangeItems", 256, //
      "Default range of travel using an item to a block (e.g. Staff to Travel Anchors).").setRange(16, 16 * 32).sync();

  public static final IValue<Float> costItem2Block = F.make("defaultTeleportEnergyCostItemToBlock", 250f, //
      "Default energy cost per block of distance of travel using an item to a block (e.g. Staff to Travel Anchors).").setRange(0, 999999).sync();

  public static final IValue<Float> costItem2Blink = F.make("defaultTeleportEnergyCostItem", 250f, //
      "Default energy cost per block of distance of travel using an item (e.g. Staff blinking).").setRange(0, 999999).sync();

  public static final IValue<Boolean> activateJump = F.make("activateJump", true, //
      "Can direct travel between blocks (e.g. Travel Anchors) be activated by jumping? (at least one of activateSneak/activateJump must be enabled)");

  public static final IValue<Boolean> activateSneak = F.make("activateSneak", true, //
      "Can direct travel between blocks (e.g. Travel Anchors) be activated by sneaking? (at least one of activateSneak/activateJump must be enabled)");

}
