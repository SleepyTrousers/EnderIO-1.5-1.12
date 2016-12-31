package crazypants.enderio.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.power.PowerDisplayUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.util.NbtValue.FLUIDAMOUNT;

public class ItemColdFireIgniter extends Item implements IAdvancedTooltipProvider, IOverlayRenderAware, IFluidContainerItem {

  private static final int FLUID_CAPACITY = 1000;

  public static ItemColdFireIgniter create() {
    ItemColdFireIgniter result = new ItemColdFireIgniter();
    result.init();
    return result;
  }

  protected ItemColdFireIgniter() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(ModObject.itemColdFireIgniter.getUnlocalisedName());
    setRegistryName(ModObject.itemColdFireIgniter.getUnlocalisedName());
    setMaxDamage(0);
    setMaxStackSize(1);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    pos = pos.offset(side);

    if (!player.canPlayerEdit(pos, side, stack))
    {
      return EnumActionResult.FAIL;
    }

    if (world.isAirBlock(pos))
    {
      if (Config.coldFireIgniterMbPerUse > 0 && !drain(stack, Config.coldFireIgniterMbPerUse)) {
        Fluid fluid = getFluidType(stack);
        String fluidname = fluid.getLocalizedName(new FluidStack(fluid, 1));
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("coldfireigniter.chat.outoffluid").replace("{FLUIDNAME}", fluidname)));
      } else {
	world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
	world.setBlockState(pos, ModObject.blockColdFire.getBlock().getDefaultState(), 11);
      }
    }

    return EnumActionResult.SUCCESS;
  }

  public @Nonnull Fluid getFluidType(ItemStack container) {
    return FluidRegistry.getFluid(Config.coldFireIgniterFluidType);
  }

  @Override
  public FluidStack getFluid(ItemStack container) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (amount > 0) {
      return new FluidStack(getFluidType(container), amount);
    } else {
      return null;
    }
  }

  @Override
  public int getCapacity(ItemStack container) {
    return FLUID_CAPACITY;
  }

  @Override
  public int fill(ItemStack container, FluidStack resource, boolean doFill) {
    if (container == null || resource == null || resource.amount <= 0 || resource.getFluid() == null
        || resource.getFluid() != getFluidType(container)) {
      return 0;
    }
    int amount = FLUIDAMOUNT.getInt(container, 0);
    int free = FLUID_CAPACITY - amount;
    int toFill = Math.min(resource.amount, free);
    if (toFill > 0 && doFill) {
      FLUIDAMOUNT.setInt(container, amount + toFill);
    }
    return toFill;
  }

  @Override
  public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
    return null;
  }

  private boolean drain(ItemStack container, int toDrain) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (toDrain > amount) {
      return false;
    } else {
      FLUIDAMOUNT.setInt(container, amount - toDrain);
      return true;
    }
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
      if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
        return (T) this;
      }
      return null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      return new IFluidTankProperties[] {
	new IFluidTankProperties() {
	  @Override
	  @Nullable
	  public FluidStack getContents() {
	    return ItemColdFireIgniter.this.getFluid(container);
	  }

	  @Override
	  public int getCapacity() {
	    return ItemColdFireIgniter.this.getCapacity(container);
	  }

	  @Override
	  public boolean canFill() {
	    return true;
	  }

	  @Override
	  public boolean canDrain() {
	    return false;
	  }

	  @Override
	  public boolean canFillFluidType(FluidStack fluidStack) {
	    return fluidStack != null && fluidStack.getFluid() == getFluidType(container);
	  }

	  @Override
	  public boolean canDrainFluidType(FluidStack fluidStack) {
	    return false;
	  }
	}
      };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      return ItemColdFireIgniter.this.fill(container, resource, doFill);
    }

    @Override
    @Nullable
    public FluidStack drain(FluidStack resource, boolean doDrain) {
      return null;
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain) {
      return null;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    list.add(FLUIDAMOUNT.getInt(itemStack, 0) + " " + EnderIO.lang.localize("fluid.millibucket.abr") + " " + PowerDisplayUtil.ofStr() + " "
        + getFluidType(itemStack).getLocalizedName(null));
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, getUnlocalizedName(itemstack));
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addBasicTooltipFromResources(list, getUnlocalizedName(itemstack));
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, getUnlocalizedName(itemstack));
  }

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_fluid.render(stack, xPosition, yPosition, 0);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    final ItemStack stack = new ItemStack(itemIn);
    subItems.add(stack.copy());
    FLUIDAMOUNT.setInt(stack, FLUID_CAPACITY);
    subItems.add(stack);
  }

}
