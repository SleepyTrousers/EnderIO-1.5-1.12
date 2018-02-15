package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class PersonalConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "personal"));

  public static final IValue<Boolean> recipeButtonDisableAlways = F.make("recipeButtonDisableAlways", false, //
      "Should the annoying recipe button be always disabled?");
  public static final IValue<Boolean> recipeButtonDisableWithJei = F.make("recipeButtonDisableWithJei", false, //
      "Should the annoying recipe button be disabled if JEI is installed? (no effect is recipeButtonReplaceWithJei is set)");
  public static final IValue<Boolean> recipeButtonReplaceWithJei = F.make("recipeButtonReplaceWithJei", true, //
      "Should the annoying recipe button be replaced with a JEI recipe button if JEI is installed?");

}
