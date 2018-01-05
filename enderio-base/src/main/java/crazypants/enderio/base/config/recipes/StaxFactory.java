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

  public StaxFactory(XMLEventReader eventReader) {
    this.eventReader = eventReader;
  }

  public <T extends RecipeConfigElement> T read(T target, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
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

}
