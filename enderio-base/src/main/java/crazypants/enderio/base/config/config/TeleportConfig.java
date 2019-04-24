package crazypants.enderio.base.config.config;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.factory.IValueFactoryEIO;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class TeleportConfig {

  public static final IValueFactoryEIO F = BaseConfig.F.section("teleport");

  public static final IValue<Integer> rangeBlocks = F.make("defaultTeleportRangeBlocks", 96, //
      "Default range of direct travel between blocks (e.g. Travel Anchors).").setRange(16, 16 * 32).sync();

  public static final IValue<Integer> rangeItem2Block = F.make("defaultTeleportRangeItemToBlock", 256, //
      "Default range of travel using an item to a block (e.g. Staff to Travel Anchors).").setRange(16, 16 * 32).sync();

  public static final IValue<Integer> rangeItem2Blink = F.make("defaultTeleportRangeItem", 16, //
      "Default range of travel using an item (e.g. Staff blinking).").setRange(4, 16 * 32).sync();

  public static final IValue<Float> costItem2Block = F.make("defaultTeleportEnergyCostItemToBlock", 250f, //
      "Default energy cost per block of distance of travel using an item to a block (e.g. Staff to Travel Anchors).").setRange(0, 999999).sync();

  public static final IValue<Float> costItem2Blink = F.make("defaultTeleportEnergyCostItem", 250f, //
      "Default energy cost per block of distance of travel using an item (e.g. Staff blinking).").setRange(0, 999999).sync();

  public static final IValue<Boolean> activateJump = F.make("activateJump", true, //
      "Can direct travel between blocks (e.g. Travel Anchors) be activated by jumping? (at least one of activateSneak/activateJump must be enabled)");

  public static final IValue<Boolean> activateSneak = F.make("activateSneak", true, //
      "Can direct travel between blocks (e.g. Travel Anchors) be activated by sneaking? (at least one of activateSneak/activateJump must be enabled)");

  public static final IValue<Float> visualScale = F.make("visualScale", .2f, //
      "Visual size of possible targets when travelling to blocks.").setRange(0.01, 1);

  public static final IValueFactoryEIO BLINK = F.section("blink");

  public static final IValue<Boolean> enableBlink = BLINK.make("enableBlink", true, //
      "Allow using travel items to 'blink' (teleport without a target)?").sync();

  public static final IValue<Boolean> enableBlinkSolidBlocks = BLINK.make("enableBlinkThroughSolidBlocks", true, //
      "Allow blinking through solid blocks?").sync();

  public static final IValue<Boolean> enableBlinkNonSolidBlocks = BLINK.make("enableBlinkThroughNonSolidBlocks", true, //
      "Allow blinking through non-solid (transparent/partial) blocks?").sync();

  public static final IValue<Boolean> enableBlinkUnbreakableBlocks = BLINK.make("enableBlinkThroughUnbreakableBlocks", false, //
      "Allow blinking through unbreakable (e.g. bedrock) blocks?").sync();

  public static final IValue<Things> blockBlacklist = BLINK.make("blockBlacklist", new Things("Thaumcraft:blockWarded"), //
      "Blocks that cannot be blinked through.").sync();

  public static final IValue<Integer> blinkDelay = BLINK.make("cooldown", 10, //
      "Minimum number of ticks between 'blinks'. Values of 10 or less allow a limited sort of flight.").sync();

  public static final IValueFactory STAFF = F.section("staff");

  public static final IValue<Boolean> enableOffHandBlink = BLINK.make("enableOffHandBlink", true, //
      "Allow using blink when in offhand?").sync();

  public static final IValue<Boolean> enableOffHandTravel = BLINK.make("enableOffHandTravel", true, //
      "Allow travelling to blocks when in offhand?").sync();

}
