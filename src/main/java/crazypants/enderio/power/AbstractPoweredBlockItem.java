package crazypants.enderio.power;

import crazypants.util.NbtValue;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class AbstractPoweredBlockItem extends ItemBlock implements IInternalPoweredItem {

  protected int maxEneryStored;
  protected int maxInput;
  protected int maxOutput;

  public AbstractPoweredBlockItem(Block block, int maxEneryStored, int maxInput, int maxOutput) {
    super(block);
    this.maxEneryStored = maxEneryStored;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
  }

  @Override
  public int getMaxEnergyStored(ItemStack container) {
    return maxEneryStored;
  }
  
  @Override
  public int getEnergyStored(ItemStack container) {
    return NbtValue.ENERGY.getInt(container);
  }
    
  @Override
  public void setEnergyStored(ItemStack container, int energy) {
    NbtValue.ENERGY.setInt(container, MathHelper.clamp_int(energy, 0, getMaxEnergyStored(container)));
  }

  @Override
  public int getMaxInput(ItemStack container) {
    return maxInput;
  }

  @Override
  public int getMaxOutput(ItemStack container) {
    return maxOutput;
  }
  
  public void setFull(ItemStack container) {
    setEnergyStored(container, getMaxEnergyStored(container));
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    return new ItemPowerCapabilityBackend(stack);
  }

}
