package crazypants.enderio.machine.capbank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.power.forge.PowerHandlerItemStack;
import crazypants.util.NbtValue;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

public class BlockItemCapBank extends ItemBlock implements IOverlayRenderAware {

  public static ItemStack createItemStackWithPower(int meta, int storedEnergy) {
    ItemStack res = new ItemStack(EnderIO.blockCapBank, 1, meta);
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

  public BlockItemCapBank(@Nonnull BlockCapBank blockCapBank, @Nonnull String name) {
    super(blockCapBank);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
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

  public int getMaxEnergyStored(ItemStack stack) {
    return CapBankType.getTypeFromMeta(stack.getMetadata()).getMaxEnergyStored();
  }
  
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    return new InnerProv(stack);
  }
  
  
  private class InnerProv implements ICapabilityProvider {

    private final ItemStack container;
    
    public InnerProv(ItemStack container) {
      this.container = container;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityEnergy.ENERGY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      if(capability != CapabilityEnergy.ENERGY) {
        return null;
      }
      CapBankType type = CapBankType.getTypeFromMeta(container.getMetadata());
      if(type.isCreative()) {
        new CreativePowerCap(container);
      }
      return (T)new PowerHandlerItemStack(container, type.getMaxEnergyStored(), type.getMaxIO(), type.getMaxIO());
    }
  }
  
  private class CreativePowerCap extends PowerHandlerItemStack {

    public CreativePowerCap (ItemStack container) {
      super(container, CapBankType.CREATIVE.getMaxEnergyStored(), CapBankType.CREATIVE.getMaxIO(), CapBankType.CREATIVE.getMaxIO());
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return maxReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
      return maxExtract;
    }
  }

  
}
