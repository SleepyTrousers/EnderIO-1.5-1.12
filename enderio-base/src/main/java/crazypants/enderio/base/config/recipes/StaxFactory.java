package crazypants.enderio.base.config.recipes;

import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StaxFactory {

  private final XMLEventReader eventReader;
  private final String source;

  public StaxFactory(XMLEventReader eventReader, String source) {
    this.eventReader = eventReader;
    this.source = source;
  }

  public <T extends RecipeRoot> T readRoot(T target, String rootElement) throws XMLStreamException, InvalidRecipeConfigException {
    while (eventReader.hasNext()) {
      XMLEvent event = eventReader.nextEvent();
      if (event.isStartElement()) {
        StartElement startElement = event.asStartElement();
        if (rootElement.equals(startElement.getName().getLocalPart())) {
          return read(target, startElement);
        } else {
          throw new InvalidRecipeConfigException("Unexpected tag '" + startElement.getName() + "'");
        }
      }
    }

    throw new InvalidRecipeConfigException("Missing top-level tag '" + rootElement + "'");
  }

  public <T extends RecipeConfigElement> T read(T target, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    target.setSource(source != null ? source : "unkown");

    try {
      Iterator<Attribute> attributes = startElement.getAttributes();
      while (attributes.hasNext()) {
        Attribute attribute = attributes.next();
        if (!target.setAttribute(this, attribute.getName().getLocalPart().toString(), attribute.getValue())) {
          throw new InvalidRecipeConfigException("Unexpected attribute '" + attribute.getName() + "' inside " + startElement.getName());
        }
      }
    } catch (InvalidRecipeConfigException e) {
      if (e.getMessage().contains("[row,col]")) {
        throw e;
      } else {
        throw new InvalidRecipeConfigException(e,
            "at [row,col]:[" + startElement.getLocation().getLineNumber() + "," + startElement.getLocation().getColumnNumber() + "]");
      }
    }

    while (eventReader.hasNext()) {
      XMLEvent event = eventReader.nextEvent();

      try {
        if (event.isStartElement()) {
          if (!target.setElement(this, event.asStartElement().getName().getLocalPart(), event.asStartElement())) {
            throw new InvalidRecipeConfigException("Unexpected tag '" + event.asStartElement().getName() + "' inside " + startElement.getName());
          }
        } else if (event.isEndElement()) {
          target.readResolve();
          return target;
        } else if (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
          // NOP
        } else if (event.getEventType() == XMLStreamConstants.COMMENT) {
          // NOP
        } else {
          throw new InvalidRecipeConfigException("Unexpected '" + event.getEventType() + "' inside " + startElement.getName());
        }
      } catch (InvalidRecipeConfigException e) {
        if (e.getMessage().contains("[row,col]")) {
          throw e;
        } else {
          throw new InvalidRecipeConfigException(e, "at [row,col]:[" + event.getLocation().getLineNumber() + "," + event.getLocation().getColumnNumber() + "]");
        }
      }
    }
    throw new InvalidRecipeConfigException("Unexpected end of document inside " + startElement.getName());
  }

  public void skip(StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    while (eventReader.hasNext()) {
      XMLEvent event = eventReader.nextEvent();
      if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(startElement.getName().getLocalPart())) {
        return;
      }
    }
    throw new InvalidRecipeConfigException("Unexpected end of document inside " + startElement.getName());
  }

}
