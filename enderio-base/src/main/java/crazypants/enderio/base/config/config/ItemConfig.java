package crazypants.enderio.base.config.config;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.factory.IValueFactoryEIO;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;
import net.minecraftforge.fluids.Fluid;

public class ItemConfig {

  public static final IValueFactoryEIO F = BaseConfig.F.section("items");

  public static final IValueFactory FOOD = F.section(".food");

  public static final IValue<Float> enderiosTeleportChance = FOOD.make("enderiosTeleportChance", .3f, //
      "The probability that Enderios do what they promise.").setRange(0, 1).sync();
  public static final IValue<Float> enderiosTeleportRange = FOOD.make("enderiosTeleportRange", 16f, //
      "The maximum range of a cerial-induced location change.").setRange(1.5, 128).sync();

  public static final IValueFactoryEIO MAG = F.section(".magnet");

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

  public static final IValueFactory STAFF = F.section(".staffoflevity");

  public static final IValue<Integer> staffOfLevityFluidUsePerTeleport = STAFF.make("fluidUsePerTeleport", 100, //
      "Amount of fluid in mB used by the Staff of Levity.").setRange(1, 1000).sync();
  public static final IValue<Integer> staffOfLevityFluidStorage = STAFF.make("fluidStorage", 8000, //
      "Amount of fluid stored by the Staff of Levity.").setRange(1, 64000).sync();
  public static final IValue<Integer> staffOfLevityTicksBetweenActivation = STAFF.make("ticksBetweenActivations", 10, //
      "Ticks that must pass between activations of the Staff of Levity.").setRange(1, 20 * 60 * 10).sync();
  public static final IValue<Fluid> staffOfLevityFluidType = STAFF.makeFluid("fluidType", "vapor_of_levity", //
      "Type of fluid used by the Staff of Levity.").sync();
  public static final IValue<Integer> staffOfLevityDurationSeconds = STAFF.make("durationSeconds", 255, //
      "Duration for the levitation effect of the Staff of Levity. This is just a failsafe to prevent players from floating forever.").setRange(1, 60 * 60 * 24)
      .sync();

  public static final IValueFactory ROD = F.section(".rodofreturn");

  public static final IValue<Boolean> rodOfReturnCanTargetAnywhere = ROD.make("canTargetAnywhere", false, //
      "If set to false the Rod of Return can only target a telepad.").sync();
  public static final IValue<Integer> rodOfReturnTicksToActivate = ROD.make("ticksToActivate", 50, //
      "Number of ticks the Rod of Return must be used before teleporting.").setRange(1, 20 * 60).sync();
  public static final IValue<Integer> rodOfReturnPowerStorage = ROD.make("powerStorage", 2000000, //
      "Internal energy buffer for the Rod of Return.").setMin(1).sync();
  public static final IValue<Integer> rodOfReturnMinTicksToRecharge = ROD.make("ticksToRecharge", 100, //
      "Min number of ticks required to recharge the internal energy buffer of the Rod of Return.").setRange(1, 20 * 60).sync();
  public static final IValue<Integer> rodOfReturnFluidStorage = ROD.make("fluidStorage", 200, //
      "Amount of fluid stored by the Rod of Return.").setRange(1, 64000).sync();
  public static final IValue<Integer> rodOfReturnFluidUsePerTeleport = ROD.make("fluidUsePerTeleport", 200, //
      "Amount of fluid in mB used by the Rod of Return. (0 to disable fluid use)").setRange(0, 1000).sync();
  public static final IValue<Fluid> rodOfReturnFluidType = ROD.makeFluid("fluidType", "ender_distillation", //
      "Type of fluid used by the Rod of Return.").sync();
  public static final IValue<Integer> rodOfReturnRfPerTick = ROD.make("energyPerTick", 35000, //
      "Amount of energy used by the Rod of Return each tick it is used.").setRange(1, 1000).sync();

}
