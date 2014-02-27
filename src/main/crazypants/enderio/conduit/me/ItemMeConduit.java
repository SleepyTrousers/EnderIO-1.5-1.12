package crazypants.enderio.conduit.me;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;

public class ItemMeConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemMeConduit.name(), "enderio:itemMeConduit")

  };

  public static ItemMeConduit create() {
    ItemMeConduit result = new ItemMeConduit();
    result.init(subtypes);
    return result;
  }

  protected ItemMeConduit() {
    super(ModObject.itemMeConduit);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IMeConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack) {
    return new MeConduit();
  }

}
