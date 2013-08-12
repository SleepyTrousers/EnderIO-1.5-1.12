package crazypants.enderio.conduit.power;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.power.ICapacitor;

public class ItemPowerConduit extends AbstractItemConduit {

  static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
    new ItemConduitSubtype(ModObject.itemPowerConduit.unlocalisedName, ModObject.itemPowerConduit.name, "enderio:itemPowerConduit"),        
    new ItemConduitSubtype(ModObject.itemPowerConduit.unlocalisedName + "Enhanced", "Enhanced " + ModObject.itemPowerConduit.name, "enderio:itemPowerConduitEnhanced"),
    new ItemConduitSubtype(ModObject.itemPowerConduit.unlocalisedName + "Ender", "Ender " + ModObject.itemPowerConduit.name, "enderio:itemPowerConduitEnder")
  };
  
  public static ItemPowerConduit create() {
    ItemPowerConduit result = new ItemPowerConduit();
    result.init(subtypes);
    return result;
  }

  protected ItemPowerConduit() {
    super(ModObject.itemPowerConduit);    
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack) {
    return new PowerConduit(stack.getItemDamage());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    ICapacitor cap = PowerConduit.CAPACITORS[itemStack.getItemDamage()];
    list.add(String.format("I/O %d MJ/t", cap.getMaxEnergyExtracted()));
    list.add(String.format("Storage %d MJ", cap.getMaxEnergyStored()));     
  }
  
  
}
