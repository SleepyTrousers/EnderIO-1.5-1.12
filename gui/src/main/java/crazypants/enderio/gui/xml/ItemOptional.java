package crazypants.enderio.gui.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.gamedata.ValueRepository;
import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class ItemOptional implements IRecipeConfigElement {

  protected transient boolean allowDelaying = false;
  protected @Nonnull Optional<String> potion = empty();
  protected transient boolean nullItem;
  protected transient final @Nonnull NameField name = new NameField();

  @Override
  public void validate() throws InvalidRecipeConfigException {
    if (potion.isPresent()) {
      if (name.hasNbt()) {
        throw new InvalidRecipeConfigException("Cannot have nbt on a potion");
      }
      if (!ValueRepository.POTIONS.isValid(get(potion))) {
        throw new InvalidRecipeConfigException("'" + get(potion) + "' is not a valid potion name");
      }
      return;
    }

    if (name.isEmpty()) {
      if (name.hasNbt()) {
        throw new InvalidRecipeConfigException("Cannot have nbt on an empty item");
      }
      nullItem = true;
      return;
    }
  }

  public void setName(@Nonnull String name) {
    this.name.add(name);
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name.add(value);
      return true;
    }
    if ("nbt".equals(name)) {
      this.name.setNbt(value);
      return true;
    }
    if ("potion".equals(name)) {
      this.potion = ofString(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public boolean isSame(ItemOptional other) {
    return name.equals(other.name);
  }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    parent.child("item").attribute("potion", potion, true).attribute("name", name.getName(), true).attribute("nbt", name.getNbt(), true);
  }

}
