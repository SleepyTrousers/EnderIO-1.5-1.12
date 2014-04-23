package crazypants.enderio.machine.power;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.power.PowerHandlerUtil;

public class BlockItemCapacitorBank extends ItemBlock {

  public static ItemStack createItemStackWithPower(float storedEnergy) {
    ItemStack res = new ItemStack(EnderIO.blockCapacitorBank);
    PowerHandlerUtil.setStoredEnergyForItem(res, storedEnergy);
    return res;
  }

  public BlockItemCapacitorBank() {
    super(EnderIO.blockCapacitorBank);
    setHasSubtypes(true);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack stack = createItemStackWithPower(0);
    stack.setItemDamage(0);
    par3List.add(stack);

    stack = createItemStackWithPower(TileCapacitorBank.BASE_CAP.getMaxEnergyStored());
    stack.setItemDamage(1);
    par3List.add(stack);

  }

}
