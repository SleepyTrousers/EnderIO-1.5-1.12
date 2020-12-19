package crazypants.enderio.gui.xml.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import crazypants.enderio.gui.xml.IRecipeRoot;
import crazypants.enderio.gui.xml.InvalidRecipeConfigException;
import crazypants.enderio.gui.xml.ResourceLocation;
import crazypants.enderio.gui.xml.StaxFactory;

public class RecipeReader {

  private final List<String> errors = new ArrayList<>();

  public boolean readFile(IRecipeRoot target, String rootElement, File userFL) {
    if (userFL.exists()) {
      try {
        try (InputStream userFileStream = userFL.exists() ? new FileInputStream(userFL) : null;) {
          readStax(target, rootElement, userFileStream, "recipe file '" + userFL.toString() + "'");
          return true;
        }
      } catch (XMLStreamException | IOException e) {
        errors.add("Failed to parse xml from file '" + userFL.toString() + "'. Reason: " + e.getLocalizedMessage());
        try (FileInputStream stream = new FileInputStream(userFL)) {
          printContentsOnError(stream, userFL.toString());
        } catch (IOException e1) {
          errors.add("Cannot preview content. Reason: " + e1.getLocalizedMessage());
        }
        e.printStackTrace();
        return false;
      }
    }
    errors.add("Skipping missing file " + userFL.toString());
    return false;
  }

  public boolean readCoreFile(IRecipeRoot target, String rootElement, ResourceLocation fileName) {
    final ResourceLocation coreRL = new ResourceLocation(fileName.getResourceDomain(), "config/recipes/" + fileName.getResourcePath() + ".xml");

    try {
      try (InputStream coreFileStream = getResource(coreRL)) {
        readStax(target, rootElement, coreFileStream, "core recipe file '" + fileName + "'");
        return true;
      }
    } catch (IOException | XMLStreamException e) {
      errors.add("Failed to parse xml from file '" + fileName + "'. Reason: " + e.getLocalizedMessage());
      e.printStackTrace();
      return false;

    }
  }

  protected static void readStax(IRecipeRoot target, String rootElement, InputStream in, String source)
      throws XMLStreamException, InvalidRecipeConfigException {
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
    StaxFactory factory = new StaxFactory(eventReader, source);

    factory.readRoot(target, rootElement);
  }

  private InputStream getResource(ResourceLocation resourceLocation) throws IOException {
    final String resourcePath = String.format("/%s/%s/%s", "assets", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
    final InputStream resourceAsStream = this.getClass().getResourceAsStream(resourcePath);
    if (resourceAsStream != null) {
      return resourceAsStream;
    } else {
      throw new IOException("Could not find resource " + resourceLocation);
    }
  }

  protected void printContentsOnError(InputStream stream, String filename) throws FileNotFoundException, IOException {
    try {
      errors.add("File content:");
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
        errors.add(sb1.toString() + sb2.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<String> getErrors() {
    return errors;
  }

}
