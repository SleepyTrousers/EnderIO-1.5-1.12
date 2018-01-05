package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class InfinityConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "infinityPowder"));

  public static final IValue<Float> infinityDropChance = F.make("infinityDropChance", .5f, //
      "Chance that Infinity Powder will drop from fire on bedrock.").setRange(0, 1).sync();
  public static final IValue<Integer> infinityStackSize = F.make("infinityStackSize", 1, //
      "Stack size when dropped from fire.").setRange(1, 64).sync();
  public static final IValue<Boolean> infinityMakesSound = F.make("infinityMakesSound", true, //
      "Should it make a sound when Infinity Powder drops from fire?");
  public static final IValue<Integer> infinityMinAge = F
      .make("infinityMinAge", 260, //
          "How old (in ticks) does a dying fire have to be to spawn Infinity Powder? (average fire age at death is 11.5s, defauilt is 13s")
      .setRange(1, 1000).sync();
  public static final IValue<Boolean> infinityInAllDimensions = F.make("infinityInAllDimensions", false, //
      "Should making Infinity Powder be allowed in all dimensions? If not, it'll only work in the overworld.");
  public static final IValue<Boolean> infinityCraftingEnabled = F.make("infinityCraftingEnabled", true, //
          "Should making Infinity Powder by lighting bedrock on fire be enabled? Please note that you need to provide an alternative way of crafting it if you disabled this.")
      .sync();

}
