package crazypants.enderio.base.config.config;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.factory.IValueFactoryEIO;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueBool;

public final class InfinityConfig {

  public static final IValueFactoryEIO F = BaseConfig.F.section("items.infinityPowder");

  public static final IValue<Float> dropChance = F.make("dropChance", .5f, //
      "Chance that Infinity Powder will drop from fire on bedrock.").setRange(0, 1).sync();
  public static final IValue<Float> dropChanceFirewater = F.make("dropChanceFirewater", .025f, //
      "Chance that Infinity Powder will drop from firewater on bedrock per growth tick.").setRange(0, 1).sync();

  public static final IValue<Float> dropChanceFogCreatures = F.make("dropChanceFogCreatures", .314f, //
      "Chance that Infinity Powder will drop from creatures that were spawned by Infinity Fog. (0 to disable)").setRange(0, 1).sync();

  public static final IValue<Integer> dropStackSize = F.make("dropStackSize", 1, //
      "Stack size when dropped from fire.").setRange(1, 64).sync();
  public static final IValue<Boolean> makesSound = F.make("makesSound", true, //
      "Should it make a sound when Infinity Powder drops from fire?");
  public static final IValue<Integer> fireMinAge = F.make("fireMinAge", 260, //
      "How old (in ticks) does a dying fire have to be to spawn Infinity Powder? (average fire age at death is 11.5s, default is 13s").setRange(1, 1000).sync();

  public static final IValue<Boolean> enableInAllDimensions = F.make("enableInAllDimensions", false, //
      "Should making Infinity Powder be allowed in all dimensions? If not, it'll only work in the worlds configured in enableInDimensions.").sync();

  public static final IValue<int[]> enableInDimensions = F.make("enableInDimensions", new int[] { 0 }, //
      "In which demensions (numeric IDs) should Infinity Powder making be allowed? This is ignored if enableInAllDimensions is enabled.").sync();

  public static boolean isEnabledInDimension(int dim) {
    if (enableInAllDimensions.get()) {
      return true;
    }
    for (int i : enableInDimensions.get()) {
      if (dim == i) {
        return true;
      }
    }
    return false;
  }

  public static boolean isEnabledInMultipleDimensions() {
    return enableInAllDimensions.get() || enableInDimensions.get().length > 1;
  }

  public static final IValue<Boolean> inWorldCraftingEnabled = F.make("inWorldCraftingEnabled", true, //
      "Should making Infinity Powder be enabled? Please note that you need to provide an alternative way of crafting it if you disable this.").sync();

  public static final IValue<Boolean> inWorldCraftingFireEnabled = new IValueBool.And(inWorldCraftingEnabled, F.make("inWorldCraftingFireEnabled", true, //
      "Should making Infinity Powder by lighting bedrock on fire be enabled? (no effect if inWorldCraftingEnabled is off)").sync());

  public static final IValue<Boolean> inWorldCraftingFireWaterEnabled = new IValueBool.And(inWorldCraftingEnabled,
      F.make("inWorldCraftingFireWaterEnabled", true, //
          "Should making Infinity Powder by putting Firewater on bedrock be enabled? (no effect if inWorldCraftingEnabled is off)").sync());

  public static final IValue<Things> bedrock = F.make("infiniteBlocks", new Things("minecraft:bedrock"), //
      "Blocks that should be considered infinite and can be used to split off Infinity Powder from.").sync();

}
