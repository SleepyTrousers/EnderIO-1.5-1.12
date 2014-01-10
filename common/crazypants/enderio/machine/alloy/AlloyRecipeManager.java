package crazypants.enderio.machine.alloy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import crazypants.enderio.Config;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.recipe.CustomTagHandler;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeConfig;
import crazypants.enderio.machine.recipe.RecipeConfigParser;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.util.Util;

public class AlloyRecipeManager {

  private static final String CORE_FILE_NAME = "AlloySmelterRecipes_Core.xml";
  private static final String CUSTOM_FILE_NAME = "AlloySmelterRecipes_User.xml";

  static final AlloyRecipeManager instance = new AlloyRecipeManager();

  public static AlloyRecipeManager getInstance() {
    return instance;
  }

  private final List<IAlloyRecipe> recipes = new ArrayList<IAlloyRecipe>();

  private VanillaSmeltingRecipe vanillaRecipe = new VanillaSmeltingRecipe();

  public AlloyRecipeManager() {
  }

  public void loadRecipesFromConfig() {
    VanillaFurnaceTagHandler tagHandler = new VanillaFurnaceTagHandler();
    RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME, tagHandler);

    if(config != null) {
      processConfig(config, tagHandler);
    } else {
      Log.error("Could not load recipes for Alloy Smelter.");
    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, new AlloyMachineRecipe());
    //vanilla alloy furnace recipes    
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, vanillaRecipe);
  }

  public void addCustumRecipes(String xmlDef) {
    RecipeConfig config;
    VanillaFurnaceTagHandler tagHandler = new VanillaFurnaceTagHandler();
    try {
      config = RecipeConfigParser.parse(xmlDef, tagHandler);
    } catch (Exception e) {
      Log.error("Error parsing custom xml");
      return;
    }

    if(config == null) {
      Log.error("Could process custom XML");
      return;
    }
    processConfig(config, tagHandler);
  }

  public List<IAlloyRecipe> getRecipes() {
    return recipes;
  }

  private void processConfig(RecipeConfig config, VanillaFurnaceTagHandler tagHandler) {

    if(config.isDumpItemRegistery()) {
      Util.dumpModObjects(new File(Config.configDirectory, "modObjectsRegistery.txt"));
    }
    if(config.isDumpOreDictionary()) {
      Util.dumpOreNames(new File(Config.configDirectory, "oreDictionaryRegistery.txt"));
    }

    List<Recipe> newRecipes = config.getRecipes(false);
    Log.info("Added " + newRecipes.size() + " Alloy Smelter recipes from config.");
    for (Recipe rec : newRecipes) {
      addRecipe(new BasicAlloyRecipe(rec));
    }

    tagHandler.apply();

  }

  public void addRecipe(IAlloyRecipe recipe) {
    if(recipe == null) {
      Log.debug("Could not add invalid recipe: " + recipe);
      return;
    }
    IRecipe rec = getRecipeForInputs(recipe.getInputStacks());
    if(rec != null) {
      Log.warn("Not adding supplied recipe as a recipe already exists for the input: " + recipe);
      return;
    }
    recipes.add(recipe);
  }

  IRecipe getRecipeForInputs(ItemStack[] inputs) {
    for (IAlloyRecipe rec : recipes) {
      if(rec.isInputForRecipe(inputs)) {
        return rec;
      }
    }
    return null;
  }

  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null || input.item == null) {
      return false;
    }
    for (IAlloyRecipe recipe : recipes) {
      for (RecipeInput ri : recipe.getInputs()) {
        if(ri.isInput(input.item)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isValidRecipeComponents(ItemStack[] inputs) {
    if(inputs == null || inputs.length == 0) {
      return false;
    }
    for (IAlloyRecipe recipe : recipes) {
      if(recipe.isValidRecipeComponents(inputs)) {
        return true;
      }
    }
    return false;
  }

  public float getExperianceForOutput(ItemStack output) {
    for (IAlloyRecipe recipe : recipes) {
      if(recipe.getOutput().itemID == output.itemID && recipe.getOutput().getItemDamage() == output.getItemDamage()) {
        return recipe.getOutputs()[0].getExperiance();
      }
    }
    return 0;
  }

  private static final String ELEMENT_ROOT = "vanillaFurnaceRecipes";

  private static final String ELEMENT_EXCLUDE = "exclude";

  //private static final String AT_EXCLUDE = "exclude";

  private class VanillaFurnaceTagHandler implements CustomTagHandler {

    private boolean inTag = false;

    private boolean inExcludes = false;

    private Boolean enabled = null;

    private List<RecipeInput> excludes = new ArrayList<RecipeInput>();

    @Override
    public boolean startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if(ELEMENT_ROOT.equals(localName)) {
        inTag = true;
        if(RecipeConfigParser.hasAttribute(RecipeConfigParser.AT_ENABLED, attributes)) {
          boolean defVal = true;
          if(enabled != null) {
            defVal = enabled;
          }
          enabled = RecipeConfigParser.getBooleanValue(RecipeConfigParser.AT_ENABLED, attributes, defVal);
        }
      } else if(ELEMENT_EXCLUDE.equals(localName)) {
        inExcludes = true;
      } else if(inExcludes && RecipeConfigParser.ELEMENT_ITEM_STACK.equals(localName)) {
        RecipeInput ri = RecipeConfigParser.getItemStack(attributes);
        excludes.add(ri);
      }
      return inTag;
    }

    @Override
    public boolean endElement(String uri, String localName, String qName) throws SAXException {
      if(ELEMENT_ROOT.equals(localName)) {
        inTag = false;
      } else if(ELEMENT_EXCLUDE.equals(localName)) {
        inExcludes = false;
      }
      return inTag;
    }

    public void apply() {

      if(enabled != null) {
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
