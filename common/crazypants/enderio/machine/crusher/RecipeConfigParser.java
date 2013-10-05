package crazypants.enderio.machine.crusher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Log;
import crazypants.enderio.machine.crusher.RecipeConfig.Recipe;
import crazypants.enderio.machine.crusher.RecipeConfig.RecipeGroup;
import crazypants.util.IOUtils;

public class RecipeConfigParser extends DefaultHandler {

  public static final String ELEMENT_ROOT = "SAGMillRecipes";
  public static final String ELEMENT_RECIPE_GROUP = "recipeGroup";
  public static final String ELEMENT_RECIPE = "recipe";
  public static final String ELEMENT_INPUT = "input";
  public static final String ELEMENT_OUTPUT = "output";
  public static final String ELEMENT_ITEM_STACK = "itemStack";
  public static final String ELEMENT_DUMP_REGISTERY = "dumpRegistery";

  public static final String AT_NAME = "name";
  public static final String AT_ENABLED = "enabled";
  public static final String AT_DUMP_ITEMS = "modObjects";
  public static final String AT_ORE_DICT = "oreDictionary";
  public static final String AT_ENERGY_COST = "energyCost";
  public static final String AT_ITEM_ID = "itemID";
  public static final String AT_ITEM_META = "itemMeta";
  public static final String AT_ITEM_NAME = "itemName";
  public static final String AT_MOD_ID = "modID";
  public static final String AT_NUMBER = "number";
  public static final String AT_CHANCE = "chance";

  // Log prefix
  private static final String LP = "SAGMillRecipeParser: ";

  public static RecipeConfig parse(String str) throws Exception {
    StringReader reader = new StringReader(str);
    InputSource is = new InputSource(reader);
    try {
      return parse(is);
    } finally {
      reader.close();
    }
  }

  public static RecipeConfig parse(File file) throws Exception {
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    InputSource is = new InputSource(bis);
    try {
      return parse(is);
    } finally {
      IOUtils.closeQuietly(bis);
    }
  }

  public static RecipeConfig parse(InputSource is) throws Exception {

    RecipeConfigParser parser = new RecipeConfigParser();

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
  private Recipe recipe = null;

  private boolean outputTagOpen = false;
  private boolean inputTagOpen = false;

  private boolean debug = true;

  RecipeConfigParser() {

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
    if (ELEMENT_ROOT.equals(localName)) {
      result = root;
      root = null;
      if (debug) {
        Log.debug(LP + "Closing root");
      }
      return;
    }
    if (ELEMENT_RECIPE_GROUP.equals(localName)) {

      if (debug) {
        Log.debug(LP + "Closing recipe group");
      }
      if (recipeGroup != null && recipeGroup.isValid() && root != null) {
        root.addRecipeGroup(recipeGroup);
      } else {
        Log.warn(LP + "Could not add recipe group " + recipeGroup + " to root " + root);
      }
      recipeGroup = null;
      return;
    }
    if (ELEMENT_RECIPE.equals(localName)) {
      if (debug) {
        Log.debug(LP + "Closing recipe");
      }
      if (recipe != null && recipeGroup != null) {
        recipeGroup.addRecipe(recipe);
      } else {
        Log.warn(LP + "Could not add recipe " + recipe + " to group " + recipeGroup);
      }

      recipe = null;
      return;
    }
    if (ELEMENT_OUTPUT.equals(localName)) {
      outputTagOpen = false;
      if (debug) {
        Log.debug(LP + "Closing output");
      }
      return;
    }
    if (ELEMENT_INPUT.equals(localName)) {
      inputTagOpen = false;
      if (debug) {
        Log.debug(LP + "Closing input");
      }
      return;
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

    if (debug) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < attributes.getLength(); i++) {
        sb.append("[" + attributes.getQName(i) + "=" + attributes.getValue(i) + "]");
      }
      Log.debug(LP + "RecipeConfigParser.startElement: localName:" + localName + " attrs:" + sb);
    }

    if (ELEMENT_ROOT.equals(localName)) {
      if (root != null) {
        Log.warn(LP + "Multiple " + ELEMENT_ROOT + " elements found.");
      } else {
        root = new RecipeConfig();
      }
      return;
    }

    if (root == null) {
      Log.warn(LP + "<" + ELEMENT_ROOT + "> not specified before element " + localName + ".");
      root = new RecipeConfig();
    }

    if (ELEMENT_DUMP_REGISTERY.equals(localName)) {
      root.setDumpOreDictionary(getBooleanValue(AT_ORE_DICT, attributes, false));
      root.setDumpItemRegistery(getBooleanValue(AT_DUMP_ITEMS, attributes, false));
      return;
    }

    if (ELEMENT_RECIPE_GROUP.equals(localName)) {
      if (recipeGroup != null) {
        Log.warn(LP + "Recipe group " + recipeGroup.getName() + " not closed before encountering a new recipe group.");
      }
      recipeGroup = root.createRecipeGroup(attributes.getValue(AT_NAME));
      recipeGroup.setEnabled(getBooleanValue(AT_ENABLED, attributes, true));
      if (!recipeGroup.isNameValid()) {
        Log.warn(LP + "A recipe group was found with an invalid name: " + attributes.getValue(AT_NAME));
        recipeGroup = null;
      }
      return;
    }

    if (ELEMENT_RECIPE.equals(localName)) {
      if (recipeGroup == null) {
        Log.warn(LP + "A recipe was found outside of a recipe groups tags.");
        return;
      }
      if (recipe != null) {
        Log.warn(LP + "A new recipe was started before the recipe was closed.");
      }
      recipe = recipeGroup.createRecipe();
      recipe.setEnergyRequired(getIntValue(AT_ENERGY_COST, attributes, CrusherRecipeManager.ORE_ENERGY_COST));
      return;
    }

    if (recipe == null) {
      Log.warn(LP + "Found element <" + localName + "> outside of a recipe decleration.");
      return;
    }

    if (ELEMENT_OUTPUT.equals(localName)) {
      if (inputTagOpen) {
        Log.warn(LP + "<output> encounterd before <input> closed.");
        inputTagOpen = false;
      }
      if (outputTagOpen) {
        Log.warn(LP + "<output> encounterd before previous <output> closed.");
      }
      outputTagOpen = true;
      return;
    }

    if (ELEMENT_INPUT.equals(localName)) {
      if (outputTagOpen) {
        Log.warn(LP + "<input> encounterd before <output> closed.");
        outputTagOpen = false;
      }
      if (inputTagOpen) {
        Log.warn(LP + "<input> encounterd before previous <input> closed.");
      }
      inputTagOpen = true;
      return;
    }

    if (ELEMENT_ITEM_STACK.equals(localName)) {
      if (!inputTagOpen && !outputTagOpen) {
        Log.warn(LP + "Encounterd an item stack outside of either an <input> or <output> tag.");
        return;
      }
      if (inputTagOpen && outputTagOpen) {
        Log.warn(LP + "Encounterd an item stack within both an <input> and <output> tag.");
        return;
      }
      if (inputTagOpen) {
        addInputStack(attributes);
      } else {
        addOutputStack(attributes);
      }
    }

  }

  private void addOutputStack(Attributes attributes) {
    ItemStack stack = getItemStack(attributes);
    if (stack == null) {
      return;
    }
    recipe.addOutput(new CrusherOutput(stack, getFloatValue(AT_CHANCE, attributes, 1f)));
  }

  private void addInputStack(Attributes attributes) {

    String oreDict = getStringValue(AT_ORE_DICT, attributes, null);
    if (oreDict != null) {
      ArrayList<ItemStack> ores = OreDictionary.getOres(oreDict);
      if (ores == null) {
        return;
      }
      int stackSize = getIntValue(AT_NUMBER, attributes, 1);
      for (ItemStack st : ores) {
        if (st != null) {
          ItemStack stack = st.copy();
          stack.stackSize = stackSize;
          if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            for (int i = 0; i < 16; i++) {
              stack = stack.copy();
              stack.setItemDamage(i);
              recipe.addInput(stack);
            }
          } else {
            recipe.addInput(stack);
          }
        }
      }

    } else {
      ItemStack stack = getItemStack(attributes);
      if (stack == null) {
        return;
      }
      stack.stackSize = 1;
      recipe.addInput(stack);
    }
  }

  private ItemStack getItemStack(Attributes attributes) {
    String oreDict = getStringValue(AT_ORE_DICT, attributes, null);
    if (oreDict != null) {
      ArrayList<ItemStack> ores = OreDictionary.getOres(oreDict);
      if (ores == null || ores.isEmpty() || ores.get(0) == null) {
        Log.warn(LP + "Could not find an entry in the ore dictionary for " + oreDict);
        return null;
      }
      ItemStack stack = ores.get(0).copy();
      stack.stackSize = getIntValue(AT_NUMBER, attributes, 1);
      if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
        stack.setItemDamage(0);
      }
      return stack;
    }

    int itemID = getIntValue(AT_ITEM_ID, attributes, -1);
    if (itemID <= 0) {

      String modId = getStringValue(AT_MOD_ID, attributes, null);
      String name = getStringValue(AT_ITEM_NAME, attributes, null);

      if (modId != null && name != null) {

        Item i = GameRegistry.findItem(modId, name);
        if (i != null) {
          itemID = i.itemID;
        } else {
          Block b = GameRegistry.findBlock(modId, name);
          if (b != null) {
            itemID = b.blockID;
          }
        }
      }
    }

    if (itemID <= 0) {
      Log.warn("Could not create an item stack from the attributes " + toString(attributes));
      return null;
    }

    int itemMeta = getIntValue(AT_ITEM_META, attributes, 0);
    int stackSize = getIntValue(AT_NUMBER, attributes, 1);

    return new ItemStack(itemID, stackSize, itemMeta);
  }

  private boolean getBooleanValue(String qName, Attributes attributes, boolean def) {
    String val = attributes.getValue(qName);
    if (val == null) {
      return def;
    }
    val = val.toLowerCase().trim();
    return val.equals("false") ? false : val.equals("true") ? true : def;
  }

  private int getIntValue(String qName, Attributes attributes, int def) {
    try {
      return Integer.parseInt(getStringValue(qName, attributes, def + ""));
    } catch (Exception e) {
      Log.warn(LP + "Could not parse a valid int for attribute " + qName + " with value " + getStringValue(qName, attributes, null));
      return def;
    }
  }

  private float getFloatValue(String qName, Attributes attributes, float def) {
    try {
      return Float.parseFloat(getStringValue(qName, attributes, def + ""));
    } catch (Exception e) {
      Log.warn(LP + "Could not parse a valid float for attribute " + qName + " with value " + getStringValue(qName, attributes, null));
      return def;
    }
  }

  private String getStringValue(String qName, Attributes attributes, String def) {
    String val = attributes.getValue(qName);
    if (val == null) {
      return def;
    }
    val = val.trim();
    if (val.length() <= 0) {
      return null;
    }
    return val;
  }

  private String toString(Attributes attributes) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < attributes.getLength(); i++) {
      sb.append("[" + attributes.getQName(i) + "=" + attributes.getValue(i) + "]");
    }
    return sb.toString();
  }

}
