package crazypants.enderio.machine.power;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.power.PowerHandlerUtil;

public class BlockItemCapacitorBank extends ItemBlock implements IEnergyContainerItem {

  public static ItemStack createItemStackWithPower(int storedEnergy) {
    ItemStack res = new ItemStack(EnderIO.blockCapacitorBank);
    PowerHandlerUtil.setStoredEnergyForItem(res, storedEnergy);
    return res;
  }

  public BlockItemCapacitorBank() {
    super(EnderIO.blockCapacitorBank);
    setHasSubtypes(true);
  }

  public BlockItemCapacitorBank(Block block) {
    super(block);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    String result = super.getUnlocalizedName(par1ItemStack);
    if(meta == 1) {
      result += ".creative";
    }
    return result;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack stack = createItemStackWithPower(0);
    par3List.add(stack);

    stack = createItemStackWithPower(TileCapacitorBank.BASE_CAP.getMaxEnergyStored());
    par3List.add(stack);

    stack = createItemStackWithPower(TileCapacitorBank.BASE_CAP.getMaxEnergyStored() / 2);
    stack.setItemDamage(1);
    par3List.add(stack);

  }

  @Override
  public boolean showDurabilityBar(ItemStack itemStack) {
    return true;
  }

  @Override
  public double getDurabilityForDisplay(ItemStack itemStack) {
    double stored = this.getMaxEnergyStored(itemStack) - this.getEnergyStored(itemStack) + 1;
    double max = this.getMaxEnergyStored(itemStack) + 1;
    return stored / max;
  }

  @Override
  public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    int energy = this.getEnergyStored(container);
    int maxInput = TileCapacitorBank.BASE_CAP.getMaxEnergyReceived();
    int energyReceived = Math.min(this.getMaxEnergyStored(container) - energy, Math.min(maxReceive, maxInput));

    if(!simulate) {
      energy += energyReceived;
      PowerHandlerUtil.setStoredEnergyForItem(container, energy);
    }
    return energyReceived;
  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    int energy = this.getEnergyStored(container);
    int maxOutput = TileCapacitorBank.BASE_CAP.getMaxEnergyExtracted();
    int energyExtracted = Math.min(energy, Math.min(maxExtract, maxOutput));

    if(!simulate) {
      energy -= energyExtracted;
      PowerHandlerUtil.setStoredEnergyForItem(container, energy);
    }
    return energyExtracted;
  }

  @Override
  public int getEnergyStored(ItemStack container) {
    return PowerHandlerUtil.getStoredEnergyForItem(container);
  }

  @Override
  public int getMaxEnergyStored(ItemStack container) {
    return TileCapacitorBank.BASE_CAP.getMaxEnergyStored();
  }

}
