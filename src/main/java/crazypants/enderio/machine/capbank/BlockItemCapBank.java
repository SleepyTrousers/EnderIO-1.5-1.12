package crazypants.enderio.machine.capbank;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.power.AbstractPoweredBlockItem;
import crazypants.enderio.power.ItemPowerCapabilityBackend;
import crazypants.enderio.render.util.PowerBarOverlayRenderHelper;
import crazypants.util.NbtValue;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockItemCapBank extends AbstractPoweredBlockItem implements IOverlayRenderAware {

  public static ItemStack createItemStackWithPower(int meta, int storedEnergy) {
    ItemStack res = new ItemStack(MachineObject.blockCapBank.getBlock(), 1, meta);
    if (storedEnergy > 0) {
      setStoredEnergyForItem(res, storedEnergy);
    }
    return res;
  }
  
  public static int getStoredEnergyForItem(ItemStack item) {
    return NbtValue.ENERGY.getInt(item);
  }
  
  public static void setStoredEnergyForItem(ItemStack item, int storedEnergy) {
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
  public @Nonnull String getUnlocalizedName(ItemStack par1ItemStack) {
    return CapBankType.getTypeFromMeta(par1ItemStack.getMetadata()).getUnlocalizedName();
  }

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    if (Config.capacitorBankRenderPowerOverlayOnItem) {
      PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
    }
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return CapBankType.getTypeFromMeta(stack.getMetadata()).isCreative() || super.hasEffect(stack);
  }

  @Override
  public int getMaxEnergyStored(ItemStack stack) {
    return CapBankType.getTypeFromMeta(stack.getMetadata()).getMaxEnergyStored();
  }

  @Override
  public int getMaxInput(ItemStack container) {
    return CapBankType.getTypeFromMeta(container.getMetadata()).getMaxIO();
  }

  @Override
  public int getMaxOutput(ItemStack container) {
    return CapBankType.getTypeFromMeta(container.getMetadata()).getMaxIO();
  }
  
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    return new InnerProv(stack);
  }

  private class InnerProv implements ICapabilityProvider {

    private final ItemStack container;
    private final ItemPowerCapabilityBackend backend;

    public InnerProv(ItemStack container) {
      this.container = container;
      this.backend = new ItemPowerCapabilityBackend(container);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return backend.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
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
