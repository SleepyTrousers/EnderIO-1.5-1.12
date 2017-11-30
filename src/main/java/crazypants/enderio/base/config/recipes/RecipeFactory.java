package crazypants.enderio.base.config.recipes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

import crazypants.enderio.base.Log;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class RecipeFactory {

  private static final String DEFAULT_USER_FILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<enderio:recipes xmlns:enderio=\"http://enderio.com/recipes\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://enderio.com/recipes recipes.xsd \">\n"
      + "\n</enderio:recipes>\n";

  private final @Nonnull File configDirectory;
  private final @Nonnull String domain;

  public RecipeFactory(@Nonnull File configDirectory, @Nonnull String domain) {
    this.configDirectory = configDirectory;
    this.domain = domain;
  }

  private InputStream getResource(ResourceLocation resourceLocation) {
    final ModContainer container = Loader.instance().activeModContainer();
    if (container != null) {
      final String resourcePath = String.format("/%s/%s/%s", "assets", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
      final InputStream resourceAsStream = container.getMod().getClass().getResourceAsStream(resourcePath);
      if (resourceAsStream != null) {
        return resourceAsStream;
      } else {
        throw new RuntimeException("Could not find resource " + resourceLocation);
      }
    } else {
      throw new RuntimeException("Failed to find current mod while looking for resource " + resourceLocation);
    }
  }

  @SuppressWarnings("resource")
  public <T extends RecipeRoot> T readFile(T target, String rootElement, String fileName) throws IOException, XMLStreamException {
    final ResourceLocation xsdRL = new ResourceLocation(domain, "config/recipes.xsd");
    final File xsdFL = new File(configDirectory, "recipes.xsd");
    copyCore(xsdRL, xsdFL);

    final ResourceLocation coreRL = new ResourceLocation(domain, "config/" + fileName + "_core.xml");
    final File coreFL = new File(configDirectory, fileName + "_core.xml");
    copyCore(coreRL, coreFL);

    final File userFL = new File(configDirectory, fileName + "_user.xml");

    T config;
    try (InputStream coreFileStream = getResource(coreRL)) {
      try {
        config = readStax(target.copy(target), rootElement, coreFileStream);
      } catch (XMLStreamException e) {
        printContentsOnError(getResource(coreRL), coreRL.toString());
        throw e;
      }

    }

    if (userFL.exists()) {
      try (InputStream userFileStream = userFL.exists() ? new FileInputStream(userFL) : null;) {
        try {
          config = readStax(target, rootElement, userFileStream).addRecipes(config);
        } catch (XMLStreamException e) {
          printContentsOnError(new FileInputStream(userFL), userFL.toString());
          throw e;
        }
      }
    } else {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFL, false))) {
        writer.write(DEFAULT_USER_FILE);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return config;
  }

  protected static void printContentsOnError(InputStream stream, String filename) throws FileNotFoundException, IOException {
    try {
      Log.error("Failed to parse xml from file '", filename, "'. Content:");
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

  private void copyCore(ResourceLocation resourceLocation, File file) {
    try (InputStream schemaIn = getResource(resourceLocation)) {
      try (OutputStream schemaOut = new FileOutputStream(file)) {
        IOUtils.copy(schemaIn, schemaOut);
      }
    } catch (IOException e) {
      Log.error("Copying default recipe file from " + resourceLocation + " to " + file + " failed. Reason:");
      e.printStackTrace();
    }
  }

}
