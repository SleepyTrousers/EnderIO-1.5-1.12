package crazypants.enderio.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
  public int getMaxEnergyStored(@Nonnull ItemStack container) {
    return maxEneryStored;
  }

  @Override
  public int getEnergyStored(@Nonnull ItemStack container) {
    return NbtValue.ENERGY.getInt(container);
  }

  @Override
  public void setEnergyStored(@Nonnull ItemStack container, int energy) {
    NbtValue.ENERGY.setInt(container, MathHelper.clamp(energy, 0, getMaxEnergyStored(container)));
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack container) {
    return maxInput;
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack container) {
    return maxOutput;
  }

  public void setFull(@Nonnull ItemStack container) {
    setEnergyStored(container, getMaxEnergyStored(container));
  }

  @Override
  public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return new ItemPowerCapabilityBackend(stack);
  }

}
