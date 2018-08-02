package crazypants.enderio.machines.machine.buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.power.PoweredBlockItem;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class BlockItemBuffer extends PoweredBlockItem {

  public BlockItemBuffer(@Nonnull Block block) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getType(stack).getUnlocalizedName();
  }

  private static BufferType getType(ItemStack stack) {
    return BufferType.getTypeFromMeta(stack.getItemDamage());
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return getType(stack).isCreative || super.hasEffect(stack);
  }

  @Override
  public int getMaxEnergyStored(@Nonnull ItemStack stack) {
    return getType(stack).isCreative ? CapacitorKey.CREATIVE_BUFFER_POWER_BUFFER.getDefault() : super.getMaxEnergyStored(stack);
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack stack) {
    return CapacitorKey.BUFFER_POWER_INTAKE.getDefault();
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack container) {
    return getMaxInput(container);
  }

  @Override
  public int getEnergyStored(@Nonnull ItemStack stack) {
    return getType(stack).isCreative ? getMaxEnergyStored(stack) / 2 : super.getEnergyStored(stack);
  }

  @Override
  public void setEnergyStored(@Nonnull ItemStack stack, int energy) {
    if (!getType(stack).isCreative) {
      super.setEnergyStored(stack, energy);
    }
  }

  @Override
  public @Nullable ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return getType(stack).hasPower ? super.initCapabilities(stack, nbt) : null;
  }

}
