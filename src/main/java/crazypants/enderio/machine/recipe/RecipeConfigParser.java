package crazypants.enderio.machine.recipe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.enderio.core.common.util.OreDictionaryHelper;

import crazypants.enderio.Log;
import crazypants.enderio.machine.recipe.RecipeConfig.RecipeElement;
import crazypants.enderio.machine.recipe.RecipeConfig.RecipeGroup;
import crazypants.enderio.machine.sagmill.SagMillRecipeManager;
import crazypants.enderio.material.OreDictionaryPreferences;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeConfigParser extends DefaultHandler {

  public static final String ELEMENT_RECIPE_GROUP = "recipeGroup";
  public static final String ELEMENT_RECIPE = "recipe";
  public static final String ELEMENT_INPUT = "input";
  public static final String ELEMENT_OUTPUT = "output";
  public static final String ELEMENT_ITEM_STACK = "itemStack";
  public static final String ELEMENT_FLUID_STACK = "fluidStack";
  public static final String ELEMENT_DUMP_REGISTERY = "dumpRegistery"; // compat
  public static final String ELEMENT_DUMP_REGISTRY = "dumpRegistry";

  public static final String AT_NAME = "name";
  public static final String AT_ENABLED = "enabled";
  public static final String AT_DUMP_ITEMS = "modObjects";
  public static final String AT_ORE_DICT = "oreDictionary";
  public static final String AT_ENERGY_COST = "energyCost";
  public static final String AT_BONUS_TYPE = "bonusType";
  public static final String AT_ITEM_META = "itemMeta";
  public static final String AT_ITEM_NAME = "itemName";
  public static final String AT_MOD_ID = "modID";
  public static final String AT_NUMBER = "number";
  public static final String AT_AMOUNT = "amount";
  public static final String AT_MULTIPLIER = "multiplier";
  public static final String AT_SLOT = "slot";
  public static final String AT_CHANCE = "chance";
  public static final String AT_EXP = "exp";
  public static final String AT_ALLOW_MISSING = "allowMissing";

  // Log prefix
  private static final String LP = "RecipeParser: ";

  public static RecipeConfig parse(String str, CustomTagHandler customHandler) throws Exception {
    StringReader reader = new StringReader(str);
    InputSource is = new InputSource(reader);
    try {
      return parse(is, customHandler);
    } finally {
      reader.close();
    }
  }

  public static RecipeConfig parse(File file, CustomTagHandler customHandler) throws Exception {
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    InputSource is = new InputSource(bis);
    try {
      return parse(is, customHandler);
    } finally {
      IOUtils.closeQuietly(bis);
    }
  }

  public static RecipeConfig parse(InputSource is, CustomTagHandler customHandler) throws Exception {

    RecipeConfigParser parser = new RecipeConfigParser(customHandler);

    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser saxParser = spf.newSAXParser();
    XMLReader xmlReader = saxParser.getXMLReader();
    xmlReader.setContentHandler(parser);
    xmlReader.parse(is);

    return parser.getResult();
  }

  private RecipeConfig result = null;
  private RecipeConfig root = null;
  private RecipeGroup recipeGroup = null;
  private RecipeElement recipe = null;

  private boolean outputTagOpen = false;
  private boolean inputTagOpen = false;

  private boolean debug = false;

  private boolean inCustomHandler = false;

  private CustomTagHandler customHandler = null;

  RecipeConfigParser(CustomTagHandler customHandler) {
    this.customHandler = customHandler;
  }

  RecipeConfig getResult() {
    return result != null ? result : root;
  }

  @Override
  public void warning(SAXParseException e) throws SAXException {
    Log.warn("Warning parsing SAG Mill config file: " + e.getMessage());
  }

  @Override
  public void error(SAXParseException e) throws SAXException {
    Log.error("Error parsing SAG Mill config file: " + e.getMessage());
    e.printStackTrace();
  }

  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    Log.error("Error parsing SAG Mill config file: " + e.getMessage());
    e.printStackTrace();
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if(isElementRoot(localName)) {
      result = root;
      root = null;
      if(debug) {
        Log.debug(LP + "Closing root");
      }
      return;
    }
    if(ELEMENT_RECIPE_GROUP.equals(localName)) {

      if(debug) {
        Log.debug(LP + "Closing recipe group");
      }
      if(recipeGroup != null && root != null) {
        root.addRecipeGroup(recipeGroup);
      }
      recipeGroup = null;
      return;
    }
    if(ELEMENT_RECIPE.equals(localName)) {
      if(debug) {
        Log.debug(LP + "Closing recipe");
      }
      if(recipe != null && recipeGroup != null) {
        recipeGroup.addRecipe(recipe);
      } else {
        Log.warn(LP + "Could not add recipe " + recipe + " to group " + recipeGroup);
      }

      recipe = null;
      return;
    }
    if(ELEMENT_OUTPUT.equals(localName)) {
      outputTagOpen = false;
      if(debug) {
        Log.debug(LP + "Closing output");
      }
      return;
    }
    if(ELEMENT_INPUT.equals(localName)) {
      inputTagOpen = false;
      if(debug) {
        Log.debug(LP + "Closing input");
      }
      return;
    }
    // Custom tag handling
    if(customHandler != null) {
      if(customHandler.endElement(uri, localName, qName)) {
        inCustomHandler = false;
        return;
      }
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

    if(debug) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < attributes.getLength(); i++) {
        sb.append("[" + attributes.getQName(i) + "=" + attributes.getValue(i) + "]");
      }
      Log.debug(LP + "RecipeConfigParser.startElement: localName:" + localName + " attrs:" + sb);
    }

    if(isElementRoot(localName)) {
      if(root != null) {
        Log.warn(LP + "Multiple root elements found.");
      } else {
        root = new RecipeConfig();
      }
      return;
    }

    if(root == null) {
      Log.warn(LP + " Root element not specified before element " + localName + ".");
      root = new RecipeConfig();
    }

    if (ELEMENT_DUMP_REGISTERY.equals(localName) || ELEMENT_DUMP_REGISTRY.equals(localName)) {
      root.setDumpOreDictionary(getBooleanValue(AT_ORE_DICT, attributes, false));
      root.setDumpItemRegistery(getBooleanValue(AT_DUMP_ITEMS, attributes, false));
      return;
    }

    if(ELEMENT_RECIPE_GROUP.equals(localName)) {
      if(recipeGroup != null) {
        Log.warn(LP + "Recipe group " + recipeGroup.getName() + " not closed before encountering a new recipe group.");
      }
      recipeGroup = root.createRecipeGroup(attributes.getValue(AT_NAME));
      recipeGroup.setEnabled(getBooleanValue(AT_ENABLED, attributes, true));
      if(!recipeGroup.isNameValid()) {
        Log.warn(LP + "A recipe group was found with an invalid name: " + attributes.getValue(AT_NAME));
        recipeGroup = null;
      }
      return;
    }

    if(ELEMENT_RECIPE.equals(localName)) {
      if(recipeGroup == null) {
        Log.warn(LP + "A recipe was found outside of a recipe groups tags.");
        return;
      }
      if(recipe != null) {
        Log.warn(LP + "A new recipe was started before the recipe was closed.");
      }
      String name = getStringValue(AT_NAME, attributes, null);
      if(name == null) {
        Log.warn(LP + "An unnamed recipe was found.");
        return;
      }
      recipe = recipeGroup.createRecipe(name);
      recipe.setEnergyRequired(getIntValue(AT_ENERGY_COST, attributes, SagMillRecipeManager.ORE_ENERGY_COST));
      recipe.setBonusType(getEnumValue(AT_BONUS_TYPE, attributes, RecipeBonusType.class, RecipeBonusType.MULTIPLY_OUTPUT));
      recipe.setAllowMissing(getBooleanValue(AT_ALLOW_MISSING, attributes, false));
      return;
    }

    // Custom tag handling
    if(customHandler != null) {
      if(customHandler.startElement(uri, localName, qName, attributes)) {
        inCustomHandler = true;
        return;
      }
    }

    if(recipe == null) {
      if(!inCustomHandler) {
        Log.warn(LP + "Found element <" + localName + "> with no recipe decleration.");
      }
      return;
    }

    if(ELEMENT_OUTPUT.equals(localName)) {
      if(inputTagOpen) {
        Log.warn(LP + "<output> encounterd before <input> closed.");
        inputTagOpen = false;
      }
      if(outputTagOpen) {
        Log.warn(LP + "<output> encounterd before previous <output> closed.");
      }
      outputTagOpen = true;
      return;
    }

    if(ELEMENT_INPUT.equals(localName)) {
      if(outputTagOpen) {
        Log.warn(LP + "<input> encounterd before <output> closed.");
        outputTagOpen = false;
      }
      if(inputTagOpen) {
        Log.warn(LP + "<input> encounterd before previous <input> closed.");
      }
      inputTagOpen = true;
      return;
    }

    boolean isFluidStack = ELEMENT_FLUID_STACK.equals(localName);
    if(ELEMENT_ITEM_STACK.equals(localName) || isFluidStack) {
      if(!inputTagOpen && !outputTagOpen) {
        Log.warn(LP + "Encounterd an item stack outside of either an <input> or <output> tag.");
        return;
      }
      if(inputTagOpen && outputTagOpen) {
        Log.warn(LP + "Encounterd an item stack within both an <input> and <output> tag.");
        return;
      }

      if(inputTagOpen) {
        if(isFluidStack) {
          addInputFluidStack(attributes);
        } else {
          addInputStack(attributes);
        }
      } else {
        if(isFluidStack) {
          addOutputFluidStack(attributes);
        } else {
          addOutputStack(attributes);
        }
      }
    }

  }

  //What a hack!
  private boolean isElementRoot(String str) {
    return "AlloySmelterRecipes".equals(str) || "SAGMillRecipes".equals(str) || "VatRecipes".equals(str) || "SliceAndSpliceRecipes".equals(str);
  }

  private void addOutputStack(Attributes attributes) {
    RecipeInput stack = getItemStack(attributes);
    if(stack == null) {
      return;
    }
    float exp = getFloatValue(AT_EXP, attributes, 0f);
    recipe.addOutput(new RecipeOutput(stack.getInput(), getFloatValue(AT_CHANCE, attributes, 1f), exp));
  }

  private void addInputStack(Attributes attributes) {
    RecipeInput stack = getItemStack(attributes);
    if (stack == null) {
      if (!recipe.allowMissing()) {
        recipe.invalidate();
      }
      return;
    }
    recipe.addInput(stack);
  }

  private void addOutputFluidStack(Attributes attributes) {
    RecipeInput stack = getFluidStack(attributes);
    if(stack == null) {
      return;
    }
    recipe.addOutput(new RecipeOutput(stack.getFluidInput()));
  }

  private void addInputFluidStack(Attributes attributes) {
    RecipeInput stack = getFluidStack(attributes);
    if(stack == null) {
      return;
    }
    recipe.addInput(stack);
  }

  private RecipeInput getFluidStack(Attributes attributes) {
    int amount = getIntValue(AT_AMOUNT, attributes, Fluid.BUCKET_VOLUME);
    String name = getStringValue(AT_NAME, attributes, null);
    if(name == null) {
      return null;
    }
    Fluid fluid = FluidRegistry.getFluid(name);
    if(fluid == null) {
      Log.warn("When parsing recipes could not find fluid with name: " + name);
      return null;
    }
    return new RecipeInput(new FluidStack(fluid, amount), getFloatValue(AT_MULTIPLIER, attributes, 1));
  }

  public static RecipeInput getItemStack(Attributes attributes) {

    int stackSize = getIntValue(AT_NUMBER, attributes, 1);
    String oreDict = getStringValue(AT_ORE_DICT, attributes, null);
    if(oreDict != null) {
      if(!OreDictionaryHelper.isRegistered(oreDict)) {
        Log.debug(LP + "Could not find an entry in the ore dictionary for " + oreDict);
        return null;
      }
      ItemStack stack = OreDictionaryPreferences.instance.getPreferred(oreDict, true);
      if(stack == null || stack.getItem() == null) {
        Log.debug(LP + "Could not find a prefered item  in the ore dictionary for " + oreDict);
        return null;
      }
      stack = stack.copy();
      stack.stackSize = stackSize;
      if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
        stack.setItemDamage(0);
        Log.warn(LP + "Could not find a specific item in the ore dictionary for " + oreDict + ". Assuming " + stack.getUnlocalizedName() + " with meta=0.");
      }
      return new OreDictionaryRecipeInput(stack, oreDict, getFloatValue(AT_MULTIPLIER, attributes, 1), getIntValue(AT_SLOT, attributes, -1));
    }

    boolean useMeta = true;
    int itemMeta = 0;
    String metaString = getStringValue(AT_ITEM_META, attributes, "0");
    if("*".equals(metaString)) {
      useMeta = false;
    } else {
      itemMeta = getIntValue(AT_ITEM_META, attributes, 0);
    }
    ItemStack res = null;

    String modId = getStringValue(AT_MOD_ID, attributes, null);
    String name = getStringValue(AT_ITEM_NAME, attributes, null);

    if(modId != null && name != null) {

      ResourceLocation rl = new ResourceLocation(modId, name);
      Item i = Item.REGISTRY.getObject(rl);
      if(i != null) {
        res = new ItemStack(i, stackSize, useMeta ? itemMeta : 0);
      } else if (Block.REGISTRY.containsKey(rl)) {
        Block b = Block.REGISTRY.getObject(rl);
        res = new ItemStack(b, stackSize, useMeta ? itemMeta : 0);
      }
    }

    if(res == null || res.getItem() == null) {
      Log.debug("Could not create an item stack from the attributes " + toString(attributes));
      return null;
    }
    return new RecipeInput(res, useMeta, getFloatValue(AT_MULTIPLIER, attributes, 1), getIntValue(AT_SLOT, attributes, -1));
  }

  public static boolean getBooleanValue(String qName, Attributes attributes, boolean def) {
    String val = attributes.getValue(qName);
    if(val == null) {
      return def;
    }
    val = val.toLowerCase(Locale.US).trim();
    return val.equals("false") ? false : val.equals("true") ? true : def;
  }

  public static int getIntValue(String qName, Attributes attributes, int def) {
    try {
      return Integer.parseInt(getStringValue(qName, attributes, def + ""));
    } catch (Exception e) {
      Log.warn(LP + "Could not parse a valid int for attribute " + qName + " with value " + getStringValue(qName, attributes, null));
      return def;
    }
  }

  public static float getFloatValue(String qName, Attributes attributes, float def) {
    try {
      return Float.parseFloat(getStringValue(qName, attributes, def + ""));
    } catch (Exception e) {
      Log.warn(LP + "Could not parse a valid float for attribute " + qName + " with value " + getStringValue(qName, attributes, null));
      return def;
    }
  }

  public static String getStringValue(String qName, Attributes attributes, String def) {
    String val = attributes.getValue(qName);
    if(val == null) {
      return def;
    }
    val = val.trim();
    if(val.length() <= 0) {
      return null;
    }
    return val;
  }

  public static <E extends Enum<E>> E getEnumValue(String qName, Attributes attributes, Class<E> clazz, E def) {
    String val = attributes.getValue(qName);
    if(val == null) {
      return def;
    }
    val = val.trim();
    if(val.length() <= 0) {
      return def;
    }
    val = val.toUpperCase(Locale.US);
    try {
      return Enum.valueOf(clazz, val);
    } catch(IllegalArgumentException ex) {
      return def;
    }
  }

  public static boolean hasAttribute(String att, Attributes attributes) {
    return attributes.getValue(att) != null;
  }

  public static String toString(Attributes attributes) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < attributes.getLength(); i++) {
      sb.append("[" + attributes.getQName(i) + "=" + attributes.getValue(i) + "]");
    }
    return sb.toString();
  }

}
