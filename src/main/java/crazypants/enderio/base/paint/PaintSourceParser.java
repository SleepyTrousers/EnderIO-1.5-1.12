package crazypants.enderio.base.paint;

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

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.recipe.RecipeConfig;
import crazypants.enderio.base.recipe.RecipeConfigParser;
import crazypants.enderio.base.recipe.RecipeInput;

public class PaintSourceParser extends DefaultHandler {

  private static final @Nonnull String CORE_FILE_NAME = "painter_paint_sources_core.xml";
  private static final @Nonnull String CUSTOM_FILE_NAME = "painter_paint_sources_user.xml";

  public static void loadConfig() {
    File coreFile = new File(Config.configDirectory, CORE_FILE_NAME);

    String defaultVals = null;
    try {
      defaultVals = RecipeConfig.readRecipes(coreFile, CORE_FILE_NAME, true);
    } catch (IOException e) {
      Log.error("Could not load painter lists " + coreFile + " from EnderIO jar: " + e.getMessage());
      e.printStackTrace();
      return;
    }
    if (!coreFile.exists()) {
      Log.error("Could not load default lists from " + coreFile + " as the file does not exist.");
      return;
    }

    try {
      parse(defaultVals);
    } catch (Exception e) {
      Log.error("Could not parse default lists from " + coreFile + ": " + e);
    }

    File userFile = new File(Config.configDirectory, CUSTOM_FILE_NAME);
    String userConfigStr = null;
    try {
      userConfigStr = RecipeConfig.readRecipes(userFile, CUSTOM_FILE_NAME, false);
      if (userConfigStr.trim().length() == 0) {
        Log.error("Empty user config file: " + userFile.getAbsolutePath());
      } else {
        parse(userConfigStr);
      }
    } catch (Exception e) {
      Log.error("Could not load user defined painter lists from file: " + CUSTOM_FILE_NAME);
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
    PaintSourceParser parser = new PaintSourceParser();

    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser saxParser = spf.newSAXParser();
    XMLReader xmlReader = saxParser.getXMLReader();
    xmlReader.setContentHandler(parser);
    xmlReader.parse(is);
  }

  public static final @Nonnull String ELEMENT_WHITELIST = "whitelist";
  public static final @Nonnull String ELEMENT_BLACKLIST = "blacklist";
  public static final @Nonnull String ELEMENT_ITEM_STACK = "itemStack";

  private static final @Nonnull String AT_REMOVE = "remove";

  private boolean isWhitelist = false;
  private boolean isBlacklist = false;

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (ELEMENT_WHITELIST.equals(localName)) {
      isWhitelist = true;
      isBlacklist = false;
      return;
    }
    if (ELEMENT_BLACKLIST.equals(localName)) {
      isWhitelist = false;
      isBlacklist = true;
      return;
    }
    if (ELEMENT_ITEM_STACK.equals(localName)) {
      if (!isWhitelist && !isBlacklist) {
        Log.warn("PaintSourceParser: Item stack found outside of whitlist/blacklist elements. It will be ignored");
        return;
      }
      RecipeInput stack = RecipeConfigParser.getItemStack(attributes);
      if (stack == null) {
        return;
      }
      boolean isRemove = RecipeConfigParser.getBooleanValue(AT_REMOVE, attributes, false);
      if (isBlacklist) {
        if (isRemove) {
          PaintSourceValidator.instance.removeFromBlackList(stack);
        } else {
          PaintSourceValidator.instance.addToBlacklist(stack);
        }
      } else {
        if (isRemove) {
          PaintSourceValidator.instance.removeFromWhitelist(stack);
        } else {
          PaintSourceValidator.instance.addToWhitelist(stack);
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (ELEMENT_WHITELIST.equals(localName)) {
      isWhitelist = false;
      return;
    }
    if (ELEMENT_BLACKLIST.equals(localName)) {
      isBlacklist = false;
      return;
    }
  }
}
