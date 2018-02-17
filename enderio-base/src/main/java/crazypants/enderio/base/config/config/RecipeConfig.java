package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class RecipeConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "recipe"));
  public static final SectionedValueFactory FA = new SectionedValueFactory(BaseConfig.F, new Section("", "recipe.alloy"));
  public static final SectionedValueFactory FP = new SectionedValueFactory(BaseConfig.F, new Section("", "recipe.painter"));

  public static final IValue<Integer> energyPerTask = FP.make("energyPerTask", 2000, //
      "The total amount of energy required to paint one block.").sync();

  public static final IValue<Boolean> allowTileEntitiesAsPaintSource = FP.make("allowTileEntitiesAsPaintSource", true, //
      "When enabled blocks with tile entities (e.g. machines) can be used as paint targets.").sync();

  public static final IValue<Boolean> createSyntheticRecipes = FA.make("createSyntheticRecipes", true, //
      "Automatically create alloy smelter recipes with double and triple inputs and different slot allocations (1+1+1, 2+1, 1+2, 3 and 2) for single-input recipes.")
      .sync();

}
