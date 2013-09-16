package crazypants.enderio.machine.power;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.conduit.power.PowerConduit;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BlockItemCapacitorBank extends ItemBlock {

  public static ItemStack createItemStackWithPower(float storedEnergy) {        
    ItemStack res = new ItemStack(EnderIO.blockCapacitorBank);
    PowerHandlerUtil.setStoredEnergyForItem(res, storedEnergy);    
    return res;
  }

  public BlockItemCapacitorBank(int id) {
    super(id);
    setHasSubtypes(true);
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    list.add("Contains " + BlockCapacitorBank.NF.format(PowerHandlerUtil.getStoredEnergyForItem(itemStack)) + " MJ");
    super.addInformation(itemStack, par2EntityPlayer, list, par4);  
  }
  
  @Override
  public int getMetadata(int par1) {
    return par1;
  }
  
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack stack = createItemStackWithPower(0);
    stack.setItemDamage(0);
    par3List.add(stack);
    
    stack = createItemStackWithPower(TileCapacitorBank.BASE_CAP.getMaxEnergyStored());
    stack.setItemDamage(1);
    par3List.add(stack);
    
  }

}
