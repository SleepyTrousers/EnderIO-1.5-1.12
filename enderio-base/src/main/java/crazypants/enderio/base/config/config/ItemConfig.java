package crazypants.enderio.base.config.config;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public class ItemConfig {

  public static final IValueFactory F = BaseConfig.F.section("items");

  public static final IValueFactory FOOD = F.section(".food");

  public static final IValue<Float> enderiosTeleportChance = FOOD.make("enderiosTeleportChance", .3f, //
      "The probability that Enderios do what they promise.").setRange(0, 1).sync();
  public static final IValue<Float> enderiosTeleportRange = FOOD.make("enderiosTeleportRange", 16f, //
      "The maximum range of a cerial-induced location change.").setRange(1, 128).sync();

  public static final IValueFactory MAG = F.section(".magnet");

  public static final IValue<Integer> magnetPowerUsePerSecond = MAG.make("energyUsePerSecond", 1, //
      "The amount of energy used per tick when the magnet is active.").setRange(0, 1000).sync();
  public static final IValue<Integer> magnetPowerCapacity = MAG.make("energyCapacity", 100000, //
      "Amount of energy stored in a fully charged magnet.").setRange(1, 100000000).sync();
  public static final IValue<Integer> magnetRange = MAG.make("range", 5, //
      "Range of the magnet in blocks.").setRange(1, 128).sync();
  public static final IValue<Integer> magnetMaxItems = MAG.make("itemLimit", 20, //
      "Maximum number of items the magnet can effect at a time. (-1 for unlimited)").setRange(-1, 512).sync();
  public static final IValue<Things> magnetBlacklist = MAG.make("blacklist", //
      new Things("appliedenergistics2:crystal_seed", "botania:livingrock", "botania:manatablet"), //
      "These items will not be picked up by the magnet.").sync();

  public static final IValue<Boolean> magnetAllowInMainInventory = MAG.make("allowInMainInventory", false, //
      "If true the magnet will also work in the main inventory, not just the hotbar.").sync();

  public static final IValueFactory MAGB = MAG.section(".baubles");

  public static final IValue<Boolean> magnetAllowInBaublesSlot = MAG.make("allowInBaublesSlot", true, //
      "If true the magnet can be put into a Baubles slot (requires Baubles to be installed).").sync();
  public static final IValue<Boolean> magnetAllowDeactivatedInBaublesSlot = MAG.make("allowDeactivatedInBaublesSlot", false, //
      "If true the magnet can be put into a Baubles slot even if switched off (requires Baubles to be installed and allowInBaublesSlot to be on).").sync();
  public static final IValue<String> magnetBaublesType = MAG.make("baublesType", "AMULET", //
      new String[] { "AMULET", "RING", "BELT", "TRINKET", "HEAD", "BODY", "CHARM" },
      "The BaublesType the magnet should be, 'AMULET', 'RING' or 'BELT' (requires Baubles to be installed and allowInBaublesSlot to be on).").sync();

}
