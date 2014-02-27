package crazypants.enderio.conduit.liquid;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;

public class ItemLiquidConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemLiquidConduit.name(), "enderio:itemLiquidConduit"),
      new ItemConduitSubtype(ModObject.itemLiquidConduit.name() + "Advanced", "enderio:itemLiquidConduitAdvanced")

  };

  public static ItemLiquidConduit create() {
    ItemLiquidConduit result = new ItemLiquidConduit();
    result.init(subtypes);
    return result;
  }

  protected ItemLiquidConduit() {
    super(ModObject.itemLiquidConduit);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack) {
    if(stack.getItemDamage() == 1) {
      return new AdvancedLiquidConduit();
    }
    return new LiquidConduit();
  }

  //TODO
  //  @Override
  //  @SideOnly(Side.CLIENT)
  //  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
  //    if(PREFIX == null) {
  //      POSTFIX = " " + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTick();
  //      PREFIX = Lang.localize("power.maxOutput") + " ";
  //    }
  //    super.addInformation(itemStack, par2EntityPlayer, list, par4);
  //    ICapacitor cap = PowerConduit.CAPACITORS[itemStack.getItemDamage()];
  //    list.add(PREFIX + PowerDisplayUtil.formatPower(cap.getMaxEnergyExtracted()) + POSTFIX);
  //  }

}
