package crazypants.enderio.conduit.redstone;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemRedstoneConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemRedstoneConduit.name() + "Insulated", "enderio:itemRedstoneInsulatedConduit")
  };

  public static ItemRedstoneConduit create() {
    ItemRedstoneConduit result = new ItemRedstoneConduit();
    result.init();
    return result;
  }

  protected ItemRedstoneConduit() {
    super(ModObject.itemRedstoneConduit, subtypes);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IRedstoneConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack, EntityPlayer player) {    
    return new InsulatedRedstoneConduit();          
  }

  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }
}
