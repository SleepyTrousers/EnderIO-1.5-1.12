package crazypants.enderio.item.coldfire;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.Lang;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.render.util.PowerBarOverlayRenderHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.util.NbtValue.FLUIDAMOUNT;

public class ItemColdFireIgniter extends Item implements IAdvancedTooltipProvider, IOverlayRenderAware {

  private static final int FLUID_CAPACITY = 1000;

  public static ItemColdFireIgniter create(@Nonnull IModObject modObject) {
    ItemColdFireIgniter result = new ItemColdFireIgniter(modObject);
    result.init();
    return result;
  }

  protected ItemColdFireIgniter(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxDamage(0);
    setMaxStackSize(1);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {

    ItemStack stack = player.getHeldItem(hand);

    pos = pos.offset(side);

    if (!player.canPlayerEdit(pos, side, stack)) {
      return EnumActionResult.FAIL;
    }

    if (world.isAirBlock(pos)) {
      if (Config.coldFireIgniterMbPerUse > 0 && !drain(stack, Config.coldFireIgniterMbPerUse)) {
        Fluid fluid = getFluidType(stack);
        String fluidname = fluid.getLocalizedName(new FluidStack(fluid, 1));
        player.sendMessage(Lang.COLD_FIRE_NO_FLUID.toChat(fluidname));
      } else {
        world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
        world.setBlockState(pos, ModObject.blockColdFire.getBlockNN().getDefaultState(), 11);
      }
    }

    return EnumActionResult.SUCCESS;
  }

  public @Nonnull Fluid getFluidType(@Nonnull ItemStack container) {
    return NullHelper.notnull(FluidRegistry.getFluid(Config.coldFireIgniterFluidType),
        Config.coldFireIgniterFluidType + " is missing (config value 'coldFireIgniterFluidType')");
  }

  private FluidStack getFluid(@Nonnull ItemStack container) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (amount > 0) {
      return new FluidStack(getFluidType(container), amount);
    } else {
      return null;
    }
  }

  private int getCapacity(@Nonnull ItemStack container) {
    return FLUID_CAPACITY;
  }

  private int fill(@Nonnull ItemStack container, FluidStack resource, boolean doFill) {
    if (resource == null || resource.amount <= 0 || resource.getFluid() == null || resource.getFluid() != getFluidType(container)) {
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

  private boolean drain(@Nonnull ItemStack container, int toDrain) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (toDrain > amount) {
      return false;
    } else {
      FLUIDAMOUNT.setInt(container, amount - toDrain);
      return true;
    }
  }

  @Override
  public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return new CapabilityProvider(stack);
  }

  private class CapabilityProvider implements IFluidHandler, ICapabilityProvider {
    protected final @Nonnull ItemStack container;

    private CapabilityProvider(@Nonnull ItemStack container) {
      this.container = container;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
      }
      return null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      return new IFluidTankProperties[] { new IFluidTankProperties() {
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
      } };
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
  public void addInformation(@Nonnull ItemStack itemStack, @Nonnull EntityPlayer par2EntityPlayer, @Nonnull List<String> list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    list.add(Fluids.MB(FLUIDAMOUNT.getInt(itemStack, 0), FLUID_CAPACITY, getFluidType(itemStack)));
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, getUnlocalizedName(itemstack));
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addBasicTooltipFromResources(list, getUnlocalizedName(itemstack));
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, getUnlocalizedName(itemstack));
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_fluid.render(stack, xPosition, yPosition, 0);
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    final ItemStack stack = new ItemStack(itemIn);
    subItems.add(stack.copy());
    FLUIDAMOUNT.setInt(stack, FLUID_CAPACITY);
    subItems.add(stack);
  }

}
