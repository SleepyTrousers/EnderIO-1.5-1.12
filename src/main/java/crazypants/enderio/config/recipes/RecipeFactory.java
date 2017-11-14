package crazypants.enderio.config.recipes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import crazypants.enderio.Log;

public class RecipeFactory {

  private static final String DEFAULT_USER_FILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<enderio:recipes xmlns:enderio=\"http://enderio.com/recipes\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://enderio.com/recipes recipes.xsd \">\n"
      + "\n</enderio:recipes>\n";

  private final File configDirectory;
  private final Class<RecipeFactory> loaderClazz;
  private final String domain;

  public RecipeFactory(File configDirectory, Class<RecipeFactory> loaderClazz, String domain) {
    this.configDirectory = configDirectory;
    this.loaderClazz = loaderClazz;
    this.domain = domain;
  }

  @SuppressWarnings("resource")
  public <T extends RecipeRoot> T readFile(T target, String rootElement, String fileName) throws IOException, XMLStreamException {
    copyCore("recipes.xsd");
    InputStream userFileStream = null, defaultFileStream = null;
    try {
      T userConfig = null, defaultConfig = null;

      copyCore(fileName + "_core.xml");

      // default first, so the user file has access to the aliases
      final String coreFile = "/assets/" + domain + "/config/" + fileName + "_core.xml";
      defaultFileStream = loaderClazz.getResourceAsStream(coreFile);
      if (defaultFileStream == null) {
        throw new IOException("Could not get resource " + coreFile + " from classpath. ");
      }
      try {
        defaultConfig = readStax(target.copy(target), rootElement, defaultFileStream);
      } catch (XMLStreamException e) {
        printContentsOnError(loaderClazz.getResourceAsStream(coreFile), coreFile);
        throw e;
      }

      File configFile = new File(configDirectory, fileName + "_user.xml");
      if (configFile.exists()) {
        userFileStream = new FileInputStream(configFile);
        try {
          userConfig = readStax(target, rootElement, userFileStream);
        } catch (XMLStreamException e) {
          printContentsOnError(new FileInputStream(configFile), configFile.toString());
          throw e;
        }
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

  protected static void printContentsOnError(InputStream stream, String filename) throws FileNotFoundException, IOException {
    Log.error("Failed to parse xml from file '", filename, "'. Content:");
    try {
      int data = 0;
      while (data != -1) {
        StringBuilder sb1 = new StringBuilder(), sb2 = new StringBuilder();
        for (int i = 0; i < 16; i++) {
          data = stream.read();
          if (data != -1) {
            sb1.append(String.format("%02x ", data));
            if (data > 32 && data < 128) {
              sb2.appendCodePoint(data);
            } else {
              sb2.append(".");
            }
          } else {
            sb1.append("   ");
          }
        }
        Log.error(sb1, sb2);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(stream);
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

  private void copyCore(String filename) {
    InputStream schemaIn = null;
    OutputStream schemaOut = null;
    try {
      File file = new File(configDirectory, filename);
      schemaIn = loaderClazz.getResourceAsStream("/assets/" + domain + "/config/" + filename);
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
