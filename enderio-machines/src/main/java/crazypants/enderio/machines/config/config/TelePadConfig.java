package crazypants.enderio.machines.config.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.ValueFactory;
import crazypants.enderio.machines.config.Config;
import net.minecraftforge.fluids.Fluid;

public class TelePadConfig {

  public static final ValueFactory F = Config.F.section("telepad");

  public static final IValue<Integer> telepadPowerCoefficient = F.make("telepadPowerCoefficient", 100000, //
      "Power for a teleport is calculated by the formula:\npower = [this value] * ln(0.005*distance + 1)").setMin(0).sync();
  public static final IValue<Integer> telepadPowerInterdimensional = F.make("telepadPowerInterdimensional", 100000, //
      "The amount of RF required for an interdimensional teleport.").setMin(0).sync();
  public static final IValue<Integer> telepadEnergyBufferRF = F.make("telepadEnergyBufferRF", 100000, //
      "The amount of RF in the internal buffer.").setMin(1).sync();
  public static final IValue<Integer> telepadEnergyUsePerTickRF = F.make("telepadEnergyUsePerTickRF", 4000, //
      "The max amount of RF that can be used per tick. Higher values allow faster teleporting.").setMin(1).sync();
  public static final IValue<Integer> telepadFluidUse = F.make("telepadFluidUse", 50, //
      "The max amount of fluid in mb used per teleport. If set to 0 fluid use will be disabled").setMin(0).sync();

  public static final IValue<Boolean> telepadLockDimension = F.make("telepadLockDimension", true, //
      "If true, the dimension cannot be set via the GUI, the coord selector must be used.").sync();
  public static final IValue<Boolean> telepadLockCoords = F.make("telepadLockCoords", true, //
      "If true, the coordinates cannot be set via the GUI, the coord selector must be used.").sync();
  public static final IValue<Boolean> telepadShrinkEffect = F.make("telepadShrinkEffect", true, //
      "Can be used to disable the 'shrinking' effect of the telepad in case of conflicts with other mods. (client setting)");
  public static final IValue<Boolean> telepadIsTravelAnchor = F.make("telepadIsTravelAnchor", true, //
      "If true, TelePads will also act as normal Travel Anchors.").sync();

  public static final IValue<Fluid> telepadFluidType = F.makeFluid("telepadFluidType", "ender_distillation", //
      "The type of fluid required to teleport entities.").sync();

}
