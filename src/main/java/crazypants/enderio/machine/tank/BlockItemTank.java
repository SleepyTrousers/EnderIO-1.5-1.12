package crazypants.enderio.machine.tank;

import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.fluid.SmartTank;
import crazypants.enderio.machine.ItemTankHelper;
import crazypants.enderio.power.PowerDisplayUtil;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockTank;

public class BlockItemTank extends ItemBlock implements IAdvancedTooltipProvider, IFluidContainerItem {

  public BlockItemTank() {
    super(blockTank.getBlock());
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  public BlockItemTank(Block block, String name) {
    super(block);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
    setRegistryName(name);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    String result = super.getUnlocalizedName(par1ItemStack);
    if(meta == 1) {
      result += ".advanced";
    }
    return result;
  }

  @Override  
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    ItemStack stack = new ItemStack(this, 1,0);
    par3List.add(stack);
    stack = new ItemStack(this, 1,1);
    par3List.add(stack);
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    ((IAdvancedTooltipProvider) blockTank.getBlock()).addCommonEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SmartTank tank = loadTank(itemstack);
    if (!tank.isEmpty()) {
      String str = tank.getFluidAmount() + " " + EnderIO.lang.localize("fluid.millibucket.abr") + " " + PowerDisplayUtil.ofStr() + " "
          + tank.getFluid().getLocalizedName();
      list.add(str);
    }
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    ((IAdvancedTooltipProvider) blockTank.getBlock()).addDetailedEntries(itemstack, entityplayer, list, flag);
  }
  
  private SmartTank loadTank(ItemStack stack) {
    if (stack.hasTagCompound()) {
      SmartTank tank = ItemTankHelper.getTank(stack);
      if (tank != null) {
        return tank;
      }
    }
    return stack.getMetadata() == 0 ? new SmartTank(16000) : new SmartTank(32000);
  }
  
  private void saveTank(ItemStack stack, SmartTank tank) {
    if (!stack.hasTagCompound()) {
      stack.setTagCompound(new NBTTagCompound());
    }
    ItemTankHelper.setTank(stack, tank);
  }

  @Override
  public FluidStack getFluid(ItemStack container) {
    return loadTank(container).getFluid();
  }

  @Override
  public int getCapacity(ItemStack container) {
    return loadTank(container).getCapacity();
  }

  @Override
  public int fill(ItemStack container, FluidStack resource, boolean doFill) {
    // has to be exactly 1, must be handled from the caller
    if (container.stackSize != 1) {
      return 0;
    }
    SmartTank tank = loadTank(container);
    int ret = tank.fill(resource, doFill);
    saveTank(container, tank);
    return ret;
  }

  @Override
  public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
    // has to be exactly 1, must be handled from the caller
    if (container.stackSize != 1) {
      return null;
    }
    SmartTank tank = loadTank(container);
    FluidStack ret = tank.drain(maxDrain, doDrain);
    saveTank(container, tank);
    return ret;
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    return new CapabilityProvider(stack);
  }

  private class CapabilityProvider implements IFluidHandler, ICapabilityProvider {
    protected final ItemStack container;

    private CapabilityProvider(ItemStack container) {
      this.container = container;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      return loadTank(container).getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      if (container.stackSize != 1) {
        return 0;
      }
      SmartTank tank = loadTank(container);
      int ret = tank.fill(resource, doFill);
      saveTank(container, tank);
      return ret;
    }

    @Override
    @Nullable
    public FluidStack drain(FluidStack resource, boolean doDrain) {
      if (container.stackSize != 1) {
        return null;
      }
      SmartTank tank = loadTank(container);
      FluidStack ret = tank.drain(resource, doDrain);
      saveTank(container, tank);
      return ret;
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain) {
      if (container.stackSize != 1) {
        return null;
      }
      SmartTank tank = loadTank(container);
      FluidStack ret = tank.drain(maxDrain, doDrain);
      saveTank(container, tank);
      return ret;
    }

  }
}
