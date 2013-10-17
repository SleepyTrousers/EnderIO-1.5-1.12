package crazypants.enderio.conduit.redstone;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;

public class ItemRedstoneConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemRedstoneConduit.unlocalisedName, ModObject.itemRedstoneConduit.name, "enderio:itemRedstoneConduit"),
      new ItemConduitSubtype(ModObject.itemRedstoneConduit.unlocalisedName + "switch", "Conduit Switch", "enderio:itemRedstoneSwitch"),
      new ItemConduitSubtype(ModObject.itemRedstoneConduit.unlocalisedName + "Insulated", "Insulated " + ModObject.itemRedstoneConduit.name,
          "enderio:itemRedstoneInsulatedConduit")

  };

  public static ItemRedstoneConduit create() {
    ItemRedstoneConduit result = new ItemRedstoneConduit();
    result.init(subtypes);
    return result;
  }

  protected ItemRedstoneConduit() {
    super(ModObject.itemRedstoneConduit);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IRedstoneConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack) {
    if(stack.getItemDamage() == 0) {
      return new RedstoneConduit();
    }
    if(stack.getItemDamage() == 1) {
      return new RedstoneSwitch();
    } else {
      return new InsulatedRedstoneConduit();
    }
  }

}
