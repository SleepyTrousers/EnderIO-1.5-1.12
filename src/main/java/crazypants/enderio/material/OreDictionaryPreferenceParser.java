package crazypants.enderio.material;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.annotation.Nonnull;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.recipe.RecipeConfig;
import crazypants.enderio.recipe.RecipeConfigParser;
import crazypants.enderio.recipe.RecipeInput;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class OreDictionaryPreferenceParser extends DefaultHandler {

  private static final @Nonnull String CORE_FILE_NAME = "OreDictionaryPreferences_Core.xml";
  private static final @Nonnull String CUSTOM_FILE_NAME = "OreDictionaryPreferences_User.xml";
  private static final @Nonnull String DISPLAY_NAME = "OreDictionary preferences";

  public static void loadConfig() {
    File coreFile = new File(Config.configDirectory, CORE_FILE_NAME);

    String defaultVals = null;
    try {
      defaultVals = RecipeConfig.readRecipes(coreFile, CORE_FILE_NAME, true);
    } catch (IOException e) {
      Log.error("Could not load " + DISPLAY_NAME + " " + coreFile + " from EnderIO jar: " + e.getMessage());
      e.printStackTrace();
      return;
    }
    if(!coreFile.exists()) {
      Log.error("Could not load default " + DISPLAY_NAME + " from " + coreFile + " as the file does not exist.");
      return;
    }

    try {
      parse(defaultVals);
    } catch (Exception e) {
      Log.error("Could not parse default " + DISPLAY_NAME + " from " + coreFile + ": " + e);
    }

    File userFile = new File(Config.configDirectory, CUSTOM_FILE_NAME);
    String userConfigStr = null;
    try {
      userConfigStr = RecipeConfig.readRecipes(userFile, CUSTOM_FILE_NAME, false);
      if(userConfigStr == null || userConfigStr.trim().length() == 0) {
        Log.error("Empty user " + DISPLAY_NAME + " file: " + userFile.getAbsolutePath());
      } else {
        parse(userConfigStr);
      }
    } catch (Exception e) {
      Log.error("Could not load user " + DISPLAY_NAME + " from file: " + CUSTOM_FILE_NAME);
      e.printStackTrace();
    }
  }

  public static void parse(String str) throws Exception {
    StringReader reader = new StringReader(str);
    InputSource is = new InputSource(reader);
    try {
      parse(is);
    } finally {
      reader.close();
    }

  }

  private static void parse(InputSource is) throws Exception {
    OreDictionaryPreferenceParser parser = new OreDictionaryPreferenceParser(OreDictionaryPreferences.instance);

    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser saxParser = spf.newSAXParser();
    XMLReader xmlReader = saxParser.getXMLReader();
    xmlReader.setContentHandler(parser);
    xmlReader.parse(is);
  }

  public static final String ELEMENT_PREF = "preference";
  public static final String ELEMENT_STACK = "itemStack";

  private static final String AT_ORE_DICT = "oreDictionary";
  public static final String AT_ITEM_META = "itemMeta";
  public static final String AT_ITEM_NAME = "itemName";
  public static final String AT_MOD_ID = "modID";

  private OreDictionaryPreferences prefs;
  private String oreDictName;
  private @Nonnull ItemStack prefStack = Prep.getEmpty();

  private OreDictionaryPreferenceParser(OreDictionaryPreferences prefs) {
    this.prefs = prefs;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if(ELEMENT_PREF.equals(localName)) {
      oreDictName = RecipeConfigParser.getStringValue(AT_ORE_DICT, attributes, null);
      return;
    }
    if (ELEMENT_STACK.equals(localName) && oreDictName != null && Prep.isInvalid(prefStack)) {
      RecipeInput ri = RecipeConfigParser.getItemStack(attributes);
      if (ri != null && Prep.isValid(ri.getInput())) {
        prefStack = ri.getInput();
      }
      return;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if(ELEMENT_PREF.equals(localName)) {
      final String oreDictName_nullchecked = oreDictName;
      if (oreDictName_nullchecked != null && Prep.isValid(prefStack)) {
        boolean matched = false;
        int[] ids = OreDictionary.getOreIDs(prefStack);
        if(ids != null) {
          for (int i = 0; i < ids.length && !matched; i++) {
            matched = oreDictName_nullchecked.equals(OreDictionary.getOreName(ids[i]));
          }
        }
        if(matched) {
          prefs.setPreference(oreDictName_nullchecked, prefStack);
        } else {
          Log.warn("OreDictionaryPreferenceParser: Attempted to register " + prefStack + " as the preffered output for " + oreDictName_nullchecked
              + " but it is not registered in the OreDictionary as " + oreDictName_nullchecked);
        }
      }
      oreDictName = null;
      prefStack = Prep.getEmpty();
    }
  }

}
