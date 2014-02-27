package crazypants.enderio.conduit.item;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;

public class ItemItemConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemItemConduit.name(), "enderio:itemItemConduit"),
      new ItemConduitSubtype(ModObject.itemItemConduit.name() + "Empowered", "enderio:itemItemConduitAdvanced")
  };

  public static ItemItemConduit create() {
    ItemItemConduit result = new ItemItemConduit();
    result.init(subtypes);
    return result;
  }

  protected ItemItemConduit() {
    super(ModObject.itemItemConduit);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack item) {
    return new ItemConduit(item.getItemDamage());
  }

}
