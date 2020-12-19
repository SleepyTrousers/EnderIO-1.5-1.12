package crazypants.enderio.gui.xml;

public class Item extends ItemOptional {

  @Override
  public void validate() throws InvalidRecipeConfigException {
    super.validate();
    if (nullItem) {
      throw new InvalidRecipeConfigException("Missing item name");
    }
  }

}