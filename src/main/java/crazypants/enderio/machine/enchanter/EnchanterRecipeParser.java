package crazypants.enderio.machine.enchanter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.minecraft.enchantment.Enchantment;

import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.recipe.RecipeConfig;
import crazypants.enderio.machine.recipe.RecipeConfigParser;
import crazypants.enderio.machine.recipe.RecipeInput;

public class EnchanterRecipeParser extends DefaultHandler {

    private static final String CORE_FILE_NAME = "EnchanterRecipes_Core.xml";
    private static final String CUSTOM_FILE_NAME = "EnchanterRecipes_User.xml";

    public static List<EnchanterRecipe> loadRecipeConfig() {
        File coreFile = new File(Config.configDirectory, CORE_FILE_NAME);

        String defaultVals = null;
        try {
            defaultVals = readRecipes(coreFile, CORE_FILE_NAME, true);
        } catch (IOException e) {
            Log.error("Could not load default recipes file " + coreFile + " from EnderIO jar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        if (!coreFile.exists()) {
            Log.error("Could not load default recipes from " + coreFile + " as the file does not exist.");
            return null;
        }

        List<EnchanterRecipe> result = null;
        try {
            result = parse(defaultVals);
        } catch (Exception e) {
            Log.error("Error parsing " + CORE_FILE_NAME);
            return null;
        }

        File userFile = new File(Config.configDirectory, CUSTOM_FILE_NAME);
        String userConfigStr = null;
        try {
            userConfigStr = readRecipes(userFile, CUSTOM_FILE_NAME, false);
            if (userConfigStr == null || userConfigStr.trim().length() == 0) {
                Log.error("Empty user config file: " + userFile.getAbsolutePath());
            } else {
                List<EnchanterRecipe> userConfig = parse(userConfigStr);
                merge(result, userConfig);
            }
        } catch (Exception e) {
            Log.error("Could not load user defined recipes from file: " + CUSTOM_FILE_NAME);
            e.printStackTrace();
        }
        return result;
    }

    public static void merge(List<EnchanterRecipe> core, List<EnchanterRecipe> userConfig) {
        for (EnchanterRecipe rec : userConfig) {
            removeFromList(rec.getEnchantment(), core);
        }
        core.addAll(userConfig);
    }

    private static void removeFromList(Enchantment enchantment, List<EnchanterRecipe> recipes) {
        if (enchantment == null) {
            return;
        }
        ListIterator<EnchanterRecipe> iter = recipes.listIterator();
        while (iter.hasNext()) {
            EnchanterRecipe rec = iter.next();
            if (rec != null && rec.getEnchantment() != null
                    && rec.getEnchantment().getName().equals(enchantment.getName())) {
                Log.info("Replacing enchater recipe based on user config for enchantment " + enchantment.getName());
                iter.remove();
            }
        }
    }

    private static String readRecipes(File copyTo, String fileName, boolean replaceIfExists) throws IOException {
        if (!replaceIfExists && copyTo.exists()) {
            return readStream(new FileInputStream(copyTo));
        }

        InputStream in = RecipeConfig.class.getResourceAsStream("/assets/enderio/config/" + fileName);
        if (in == null) {
            Log.error("Could load default Enchanter recipes.");
            throw new IOException("Could not resource /assets/enderio/config/" + fileName + " form classpath. ");
        }
        String output = readStream(in);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(copyTo, false));
            writer.write(output.toString());
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return output.toString();
    }

    private static String readStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder output = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                output.append("\n");
                line = reader.readLine();
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return output.toString();
    }

    public static List<EnchanterRecipe> parse(String str) throws Exception {
        StringReader reader = new StringReader(str);
        InputSource is = new InputSource(reader);
        try {
            return parse(is);
        } finally {
            reader.close();
        }
    }

    public static List<EnchanterRecipe> parse(InputSource is) throws Exception {

        EnchanterRecipeParser parser = new EnchanterRecipeParser();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(parser);
        xmlReader.parse(is);

        return parser.getResult();
    }

    public static final String ELEMENT_ENCHANTMENT = "enchantment";
    public static final String ELEMENT_ITEM_STACK = "itemStack";
    public static final String AT_NAME = "name";
    private static final String AT_LEVEL = "costPerLevel";

    private List<EnchanterRecipe> result = new ArrayList<EnchanterRecipe>();

    private Enchantment curEnchantment = null;
    private int curLevelCost = -1;
    private boolean enchantmentFound = true;
    private RecipeInput curInput = null;

    private List<EnchanterRecipe> getResult() {
        return result;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (ELEMENT_ENCHANTMENT.equals(localName)) {
            curEnchantment = EnchanterRecipe.getEnchantmentFromName(attributes.getValue(AT_NAME));
            curLevelCost = RecipeConfigParser.getIntValue(AT_LEVEL, attributes, -1);
            if (curLevelCost == -1) {
                Log.warn(
                        "Cost per level not found for enchantment with name " + attributes.getValue(AT_NAME)
                                + " when parsing enchanter recipes.");
                curEnchantment = null;
            } else if (curEnchantment == null) {
                Log.warn(
                        "Could not find enchantment with name " + attributes.getValue(AT_NAME)
                                + " when parsing enchanter recipes.");
                enchantmentFound = false;
            } else {
                enchantmentFound = true;
            }
        } else if (ELEMENT_ITEM_STACK.equals(localName)) {
            if (curEnchantment == null) {
                if (enchantmentFound) {
                    Log.error("EnchanterRecipeParser: Encontered an item stack outside an enchantment element.");
                }
            } else if (curInput != null) {
                Log.error(
                        "EnchanterRecipeParser: Multiple input stacks found within the enchantment tag for "
                                + curEnchantment.getName());
            } else {
                curInput = RecipeConfigParser.getItemStack(attributes);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (ELEMENT_ENCHANTMENT.equals(localName)) {
            if (curEnchantment != null) {
                if (curInput == null) {
                    Log.error("Valid input found for enchantment " + curEnchantment.getName() + " not found.");
                } else {
                    EnchanterRecipe rec = new EnchanterRecipe(curInput, curEnchantment, curLevelCost);
                    if (rec.isValid()) {
                        result.add(rec);
                    }
                }
            }
            curInput = null;
            curEnchantment = null;
            enchantmentFound = true;
        }
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        Log.warn("Warning parsing Enchanter config file: " + e.getMessage());
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        Log.error("Error parsing Enchanter config file: " + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        Log.error("Error parsing Enchanter config file: " + e.getMessage());
        e.printStackTrace();
    }
}
