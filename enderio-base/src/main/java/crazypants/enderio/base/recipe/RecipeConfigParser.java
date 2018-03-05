package crazypants.enderio.base.recipe;

import java.util.Locale;

import javax.annotation.Nonnull;

import org.xml.sax.Attributes;

import crazypants.enderio.base.Log;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RecipeConfigParser {

  public static final @Nonnull String AT_ORE_DICT = "oreDictionary";
  public static final @Nonnull String AT_ITEM_META = "itemMeta";
  public static final @Nonnull String AT_ITEM_NAME = "itemName";
  public static final @Nonnull String AT_MOD_ID = "modID";
  public static final @Nonnull String AT_NUMBER = "number";
  public static final @Nonnull String AT_MULTIPLIER = "multiplier";
  public static final @Nonnull String AT_SLOT = "slot";

  // Log prefix
  private static final @Nonnull String LP = "RecipeParser: ";

  public static RecipeInput getItemStack(Attributes attributes) {
    int stackSize = getIntValue(AT_NUMBER, attributes, 1);

    boolean useMeta = true;
    int itemMeta = 0;
    String metaString = getStringValue(AT_ITEM_META, attributes, "0");
    if ("*".equals(metaString)) {
      useMeta = false;
    } else {
      itemMeta = getIntValue(AT_ITEM_META, attributes, 0);
    }
    ItemStack res = Prep.getEmpty();

    String modId = getStringValue(AT_MOD_ID, attributes, null);
    String name = getStringValue(AT_ITEM_NAME, attributes, null);

    if (modId != null && name != null) {

      ResourceLocation rl = new ResourceLocation(modId, name);
      Item i = Item.REGISTRY.getObject(rl);
      if (i != null) {
        res = new ItemStack(i, stackSize, useMeta ? itemMeta : 0);
      } else if (Block.REGISTRY.containsKey(rl)) {
        Block b = Block.REGISTRY.getObject(rl);
        res = new ItemStack(b, stackSize, useMeta ? itemMeta : 0);
      }
    }

    if (Prep.isInvalid(res)) {
      Log.debug("Could not create an item stack from the attributes " + toString(attributes));
      return null;
    }
    return new RecipeInput(res, useMeta, getFloatValue(AT_MULTIPLIER, attributes, 1), getIntValue(AT_SLOT, attributes, -1));
  }

  public static boolean getBooleanValue(String qName, Attributes attributes, boolean def) {
    String val = attributes.getValue(qName);
    if (val == null) {
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
    if (val == null) {
      return def;
    }
    val = val.trim();
    if (val.length() <= 0) {
      return null;
    }
    return val;
  }

  public static String toString(Attributes attributes) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < attributes.getLength(); i++) {
      sb.append("[" + attributes.getQName(i) + "=" + attributes.getValue(i) + "]");
    }
    return sb.toString();
  }

}
