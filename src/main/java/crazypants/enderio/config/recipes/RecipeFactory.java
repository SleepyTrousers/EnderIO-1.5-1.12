package crazypants.enderio.config.recipes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config;

public class RecipeFactory {

  private static final String DEFAULT_USER_FILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<enderio:recipes xmlns:enderio=\"http://enderio.com/recipes\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://enderio.com/recipes recipes.xsd \">\n"
      + "\n</enderio:recipes>\n";

  public static <T extends RecipeRoot> T readFile(T target, String rootElement, String fileName) throws IOException, XMLStreamException {
    copyCore("recipes.xsd");
    InputStream userFileStream = null, defaultFileStream = null;
    try {
      T userConfig = null, defaultConfig = null;

      copyCore(fileName + "_core.xml");

      // default first, so the user file has access to the aliases
      defaultFileStream = RecipeFactory.class.getResourceAsStream("/assets/" + EnderIO.DOMAIN + "/config/" + fileName + "_core.xml");
      if (defaultFileStream == null) {
        throw new IOException("Could not get resource /assets/" + EnderIO.DOMAIN + "/config/" + fileName + "_core.xml from classpath. ");
      }
      defaultConfig = readStax(target.copy(target), rootElement, defaultFileStream);

      File configFile = new File(Config.configDirectory, fileName + "_user.xml");
      if (configFile.exists()) {
        userFileStream = new FileInputStream(configFile);
        userConfig = readStax(target, rootElement, userFileStream);
        userConfig.addRecipes(defaultConfig);
        return userConfig;
      } else {
        BufferedWriter writer = null;
        try {
          writer = new BufferedWriter(new FileWriter(configFile, false));
          writer.write(DEFAULT_USER_FILE);
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          IOUtils.closeQuietly(writer);
        }
        return defaultConfig;
      }
    } finally {
      IOUtils.closeQuietly(userFileStream);
      IOUtils.closeQuietly(defaultFileStream);
    }
  }

  private static <T extends RecipeRoot> T readStax(T target, String rootElement, InputStream in) throws XMLStreamException, InvalidRecipeConfigException {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
    StaxFactory factory = new StaxFactory(eventReader);

    while (eventReader.hasNext()) {
      XMLEvent event = eventReader.nextEvent();
      if (event.isStartElement()) {
        StartElement startElement = event.asStartElement();
        if (rootElement.equals(startElement.getName().getLocalPart())) {
          return factory.read(target, startElement);
        } else {
          throw new InvalidRecipeConfigException("Unexpected tag '" + startElement.getName() + "'");
        }
      }
    }

    throw new InvalidRecipeConfigException("Missing recipes tag");
  }

  private static void copyCore(String filename) {
    InputStream schemaIn = null;
    OutputStream schemaOut = null;
    try {
      File file = new File(Config.configDirectory, filename);
      schemaIn = RecipeFactory.class.getResourceAsStream("/assets/" + EnderIO.DOMAIN + "/config/" + filename);
      if (schemaIn != null) {
        schemaOut = new FileOutputStream(file);
        IOUtils.copy(schemaIn, schemaOut);
      }
    } catch (IOException e) {
      Log.error("Copying default recipe file " + filename + " failed. Reason:");
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(schemaIn);
      IOUtils.closeQuietly(schemaOut);
    }
  }

}
