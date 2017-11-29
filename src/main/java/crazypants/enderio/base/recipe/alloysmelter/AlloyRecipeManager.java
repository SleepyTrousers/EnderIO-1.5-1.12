package crazypants.enderio.base.recipe.alloysmelter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.CustomTagHandler;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.base.recipe.ManyToOneRecipeManager;
import crazypants.enderio.base.recipe.RecipeConfigParser;
import crazypants.enderio.base.recipe.RecipeInput;

public class AlloyRecipeManager extends ManyToOneRecipeManager {

  private static final @Nonnull String CORE_FILE_NAME = "alloy_smelter_recipes_core.xml";
  private static final @Nonnull String CUSTOM_FILE_NAME = "alloy_smelter_recipes_user.xml";

  static final @Nonnull AlloyRecipeManager instance = new AlloyRecipeManager();

  public static AlloyRecipeManager getInstance() {
    return instance;
  }

  @Nonnull
  VanillaSmeltingRecipe vanillaRecipe = new VanillaSmeltingRecipe();

  public AlloyRecipeManager() {
    super(CORE_FILE_NAME, CUSTOM_FILE_NAME, "Alloy Smelter");
  }

  public @Nonnull VanillaSmeltingRecipe getVanillaRecipe() {
    return vanillaRecipe;
  }

  public void setVanillaRecipe(@Nonnull VanillaSmeltingRecipe vanillaRecipe) {
    this.vanillaRecipe = vanillaRecipe;
  }

  @Override
  protected CustomTagHandler createCustomTagHandler() {
    return new VanillaFurnaceTagHandler();
  }

  @Override
  public void loadRecipesFromConfig() {
    super.loadRecipesFromConfig();
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.ALLOYSMELTER,
        new ManyToOneMachineRecipe("AlloySmelterRecipe", MachineRecipeRegistry.ALLOYSMELTER, this));
    // vanilla alloy furnace recipes
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.ALLOYSMELTER, vanillaRecipe);
  }

  private static final @Nonnull String ELEMENT_ROOT = "vanillaFurnaceRecipes";
  private static final @Nonnull String ELEMENT_EXCLUDE = "exclude";

  private class VanillaFurnaceTagHandler implements CustomTagHandler {

    private boolean inTag = false;

    private boolean inExcludes = false;

    private Boolean enabled = null;

    private final @Nonnull List<RecipeInput> excludes = new ArrayList<RecipeInput>();

    @Override
    public boolean startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_ROOT.equals(localName)) {
        inTag = true;
        if (RecipeConfigParser.hasAttribute(RecipeConfigParser.AT_ENABLED, attributes)) {
          boolean defVal = true;
          if (enabled != null) {
            defVal = enabled;
          }
          enabled = RecipeConfigParser.getBooleanValue(RecipeConfigParser.AT_ENABLED, attributes, defVal);
        }
      } else if (ELEMENT_EXCLUDE.equals(localName)) {
        inExcludes = true;
      } else if (inExcludes && RecipeConfigParser.ELEMENT_ITEM_STACK.equals(localName)) {
        RecipeInput ri = RecipeConfigParser.getItemStack(attributes);
        excludes.add(ri);
      }
      return inTag;
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
      if (ELEMENT_ROOT.equals(localName)) {
        inTag = false;
      } else if (ELEMENT_EXCLUDE.equals(localName)) {
        inExcludes = false;
      }
      return inTag;
    }

    @Override
    public void configProcessed() {
      if (enabled != null) {
        Log.info("AlloyRecipeManager: Vannila smelting in AlloySmelting enabled=" + enabled);
        vanillaRecipe.setEnabled(enabled.booleanValue());
      }
      for (RecipeInput ri : excludes) {
        Log.info("Excluding furnace recipe from Alloy Smelter: " + ri);
        vanillaRecipe.addExclude(ri);
      }
    }

  }

}
