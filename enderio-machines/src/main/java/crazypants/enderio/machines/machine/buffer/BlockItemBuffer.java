package crazypants.enderio.machines.machine.buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.power.AbstractPoweredBlockItem;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import static crazypants.enderio.machines.capacitor.CapacitorKey.BUFFER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.BUFFER_POWER_INTAKE;

public class BlockItemBuffer extends AbstractPoweredBlockItem implements IOverlayRenderAware {

  public BlockItemBuffer(@Nonnull Block block) {
    super(block, 0, 0, 0);
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
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    if (stack.getCount() == 1 && getType(stack).hasPower) {
      PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
    }
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return getType(stack).isCreative || super.hasEffect(stack);
  }

  @Override
  public int getMaxEnergyStored(@Nonnull ItemStack stack) {
    return getType(stack).hasPower ? BUFFER_POWER_BUFFER.get(DefaultCapacitorData.BASIC_CAPACITOR) : 0;
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack stack) {
    return getType(stack).hasPower ? BUFFER_POWER_INTAKE.get(DefaultCapacitorData.BASIC_CAPACITOR) : 0;
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack container) {
    return getMaxInput(container);
  }

  @Override
  public @Nonnull ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return new InnerProv(stack, super.initCapabilities(stack, nbt));
  }

  private class InnerProv implements ICapabilityProvider {

    private final @Nonnull ItemStack container;
    private final @Nonnull ICapabilityProvider backend;

    public InnerProv(@Nonnull ItemStack container, @Nonnull ICapabilityProvider backend) {
      this.container = container;
      this.backend = backend;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return backend.hasCapability(capability, facing) && getType(container).hasPower;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (!hasCapability(capability, facing)) {
        return null;
      }
      BufferType type = getType(container);
      if (!type.hasPower || container.getCount() > 1) {
        return null;
      }
      if (type.isCreative) {
        return null; // TODO (T)new CreativePowerCap(container);
      }
      return backend.getCapability(capability, facing);
    }
  }

  // private class CreativePowerCap extends InternalPoweredItemWrapper {
  //
  // public CreativePowerCap (ItemStack container) {
  // super(container, BlockItemBuffer.this);
  // }
  //
  // @Override
  // public int receiveEnergy(int maxReceive, boolean simulate) {
  // return maxReceive;
  // }
  //
  // @Override
  // public int extractEnergy(int maxExtract, boolean simulate) {
  // return maxExtract;
  // }
  // }

}
