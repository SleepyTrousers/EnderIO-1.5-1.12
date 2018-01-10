package crazypants.enderio.powertools.machine.capbank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.power.AbstractPoweredBlockItem;
import crazypants.enderio.base.power.ItemPowerCapabilityBackend;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.powertools.init.PowerToolObject;
import crazypants.enderio.util.NbtValue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class BlockItemCapBank extends AbstractPoweredBlockItem implements IOverlayRenderAware {

  public static @Nonnull ItemStack createItemStackWithPower(int meta, int storedEnergy) {
    ItemStack res = new ItemStack(PowerToolObject.block_cap_bank.getBlockNN(), 1, meta);
    if (storedEnergy > 0) {
      setStoredEnergyForItem(res, storedEnergy);
    }
    return res;
  }

  public static int getStoredEnergyForItem(@Nonnull ItemStack item) {
    return NbtValue.ENERGY.getInt(item);
  }

  public static void setStoredEnergyForItem(@Nonnull ItemStack item, int storedEnergy) {
    NbtValue.ENERGY.setInt(item, Math.max(0, storedEnergy));
  }

  public BlockItemCapBank(@Nonnull BlockCapBank blockCapBank, @Nonnull ResourceLocation name) {
    super(blockCapBank, 0, 0, 0);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
    setRegistryName(name);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    return CapBankType.getTypeFromMeta(par1ItemStack.getMetadata()).getUnlocalizedName();
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    if (Config.capacitorBankRenderPowerOverlayOnItem) {
      PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
    }
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return CapBankType.getTypeFromMeta(stack.getMetadata()).isCreative() || super.hasEffect(stack);
  }

  @Override
  public int getMaxEnergyStored(@Nonnull ItemStack stack) {
    return CapBankType.getTypeFromMeta(stack.getMetadata()).getMaxEnergyStored();
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack container) {
    return CapBankType.getTypeFromMeta(container.getMetadata()).getMaxIO();
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack container) {
    return CapBankType.getTypeFromMeta(container.getMetadata()).getMaxIO();
  }

  @Override
  public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return new InnerProv(stack);
  }

  private class InnerProv implements ICapabilityProvider {

    private final ItemStack container;
    private final ItemPowerCapabilityBackend backend;

    public InnerProv(@Nonnull ItemStack container) {
      this.container = container;
      this.backend = new ItemPowerCapabilityBackend(container);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return backend.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (hasCapability(capability, facing) && CapBankType.getTypeFromMeta(container.getMetadata()).isCreative()) {
        return null; // TODO (T)new CreativePowerCap(container);
      }
      return backend.getCapability(capability, facing);
    }
  }

  // private class CreativePowerCap extends InternalPoweredItemWrapper {
  //
  // public CreativePowerCap (ItemStack container) {
  // super(container, BlockItemCapBank.this);
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
