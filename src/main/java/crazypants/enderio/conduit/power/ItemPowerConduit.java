package crazypants.enderio.conduit.power;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.power.PowerDisplayUtil;

public class ItemPowerConduit extends AbstractItemConduit {

  private static String PREFIX;
  private static String POSTFIX;

  static ItemConduitSubtype[] SUBTYPES = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemPowerConduit.name(), "enderio:itemPowerConduit"),
      new ItemConduitSubtype(ModObject.itemPowerConduit.name() + "Enhanced", "enderio:itemPowerConduitEnhanced"),
      new ItemConduitSubtype(ModObject.itemPowerConduit.name() + "Ender", "enderio:itemPowerConduitEnder")
  };

  public static ItemPowerConduit create() {
    ItemPowerConduit result = new ItemPowerConduit();
    result.init();
    return result;
  }

  protected ItemPowerConduit() {
    super(ModObject.itemPowerConduit, SUBTYPES);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack, EntityPlayer player) {
    return new PowerConduit(stack.getItemDamage());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    if(PREFIX == null) {
      POSTFIX = " " + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr();
      PREFIX = EnderIO.lang.localize("power.maxOutput") + " ";
    }
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    int cap = PowerConduit.getMaxEnergyIO(itemStack.getMetadata());
    list.add(PREFIX + PowerDisplayUtil.formatPower(cap) + POSTFIX);
  }
  
  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }
}
