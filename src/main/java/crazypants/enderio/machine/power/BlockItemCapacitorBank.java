package crazypants.enderio.machine.power;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.Lang;

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
    list.add(Lang.localize("tile.enderio.blockCapacitorBank.tooltipPrefix", false) + " "
        + PowerDisplayUtil.formatPower(PowerHandlerUtil.getStoredEnergyForItem(itemStack)) + " " + PowerDisplayUtil.abrevation());
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
