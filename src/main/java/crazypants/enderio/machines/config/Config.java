package crazypants.enderio.machines.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.ValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.network.PacketHandler;

@ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
public final class Config {

  public static final Section sectionCapacitor = new Section("Capacitor Values", "capacitor");
  public static final Section ENCHANTER = new Section("", "enchanter");
  public static final Section CLIENT = new Section("", "client");
  public static final Section ZOMBIE = new Section("", "zombiegenerator");
  public static final Section KILLERJOE = new Section("", "killerjoe");

  public static final ValueFactory F = new ValueFactory(PacketHandler.INSTANCE);

  public static final IValue<Integer> enchanterBaseLevelCost = F.make(ENCHANTER, "enchanterBaseLevelCost", 2, //
      "Base level cost added to all recipes in the enchanter.").setMin(0).sync();
  public static final IValue<Double> enchanterLevelCostFactor = F.make(ENCHANTER, "enchanterLevelCostFactor", 0.75, //
      "The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.").setMin(0).sync();
  public static final IValue<Double> enchanterLapisCostFactor = F.make(ENCHANTER, "enchanterLapisCostFactor", 3.0, //
      "The lapis cost is enchant level multiplied by this value.").setMin(0).sync();

  public static final IValue<Boolean> jeiUseShortenedPainterRecipes = F.make(CLIENT, "jeiUseShortenedPainterRecipes", true, //
      "If true, only a handful of sample painter recipes will be shown in JEI. Enable this if you have timing problems starting a world or logging into a server.");

  public static final IValue<Boolean> machineSoundsEnabled = new IValue<Boolean>() {
    @Override
    public @Nonnull Boolean get() {
      return crazypants.enderio.base.config.Config.machineSoundsEnabled;
    }
  };

  public static final IValue<Float> machineSoundVolume = new IValue<Float>() {
    @Override
    public @Nonnull Float get() {
      return crazypants.enderio.base.config.Config.machineSoundVolume;
    }
  };

  public static final IValue<Boolean> registerRecipes = new IValue<Boolean>() {
    @Override
    public @Nonnull Boolean get() {
      return crazypants.enderio.base.config.Config.registerRecipes;
    }
  };

  public static final IValue<Integer> ticksPerBucketOfFuel = F.make(ZOMBIE, "ticksPerBucketOfFuel", 10 * 60 * 20, //
      "The number of ticks one bucket of fuel lasts.").setMin(1).sync();
  public static final IValue<Float> minimumTankLevel = F.make(ZOMBIE, "minimumTankLevel", 0.7f, //
      "How full does the tank need to be for the zombie head to produce energy?. (0.0-0.9995)").setRange(0, 0.9995).sync();

  public static final IValue<Double> killerJoeAttackHeight = F.make(KILLERJOE, "killerJoeAttackHeight", 2.0, //
      "The reach of attacks above and below Joe.").setRange(1, 32).sync();
  public static final IValue<Double> killerJoeAttackWidth = F.make(KILLERJOE, "killerJoeAttackWidth", 2.0, //
      "The reach of attacks to each side of Joe.").setRange(1, 32).sync();
  public static final IValue<Double> killerJoeAttackLength = F.make(KILLERJOE, "killerJoeAttackLength", 4.0, //
      "The reach of attacks in front of Joe.").setRange(1, 32).sync();

  public static final IValue<Double> killerJoeHooverXpHeight = F.make(KILLERJOE, "killerJoeHooverXpHeight", 2.0, //
      "The distance from which XP will be gathered above and below Joe.").setRange(1, 32).sync();
  public static final IValue<Double> killerJoeHooverXpWidth = F.make(KILLERJOE, "killerJoeHooverXpWidth", 5.0, //
      "The distance from which XP will be gathered to each side of Joe.").setRange(1, 32).sync();
  public static final IValue<Double> killerJoeHooverXpLength = F.make(KILLERJOE, "killerJoeHooverXpLength", 10.0, //
      "The distance from which XP will be gathered in front of Joe.").setRange(1, 32).sync();

  public static final IValue<Boolean> killerJoeMustSee = F.make(KILLERJOE, "killerJoeMustSee", false, //
      "Set whether the Killer Joe can attack through blocks.").sync();
  public static final IValue<Boolean> killerPvPoffDisablesSwing = F.make(KILLERJOE, "killerPvPoffDisablesSwing", false, //
      "Set whether the Killer Joe swings even if PvP is off (that swing will do nothing unless killerPvPoffIsIgnored is enabled).").sync();
  public static final IValue<Boolean> killerPvPoffIsIgnored = F.make(KILLERJOE, "killerPvPoffIsIgnored", false, //
      "Set whether the Killer Joe ignores PvP settings and always hits players (killerPvPoffDisablesSwing must be off for this to work).").sync();
  public static final IValue<Boolean> killerMendingEnabled = F.make(KILLERJOE, "killerMendingEnabled", true, //
      "If enabled, picked up XP will be used for the enchantement 'Mending' on the weapon.").sync();

  public static final IValue<Integer> killerJoeNutrientUsePerAttackMb = F.make(KILLERJOE, "killerJoeNutrientUsePerAttackMb", 5, //
      "The number of millibuckets of nutrient fluid used per attack.").setMin(1).sync();

  //
}
