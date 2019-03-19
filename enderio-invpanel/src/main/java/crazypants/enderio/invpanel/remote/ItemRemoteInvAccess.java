package crazypants.enderio.invpanel.remote;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.MappedCapabilityProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.power.forge.item.IInternalPoweredItem;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.invpanel.init.InvpanelObject;
import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
import crazypants.enderio.util.ClientUtil;
import crazypants.enderio.util.NbtValue;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.util.NbtValue.ENERGY;
import static crazypants.enderio.util.NbtValue.FLUIDAMOUNT;
import static crazypants.enderio.util.NbtValue.REMOTE_D;
import static crazypants.enderio.util.NbtValue.REMOTE_X;
import static crazypants.enderio.util.NbtValue.REMOTE_Y;
import static crazypants.enderio.util.NbtValue.REMOTE_Z;

public class ItemRemoteInvAccess extends Item implements IAdvancedTooltipProvider, IOverlayRenderAware, IInternalPoweredItem, IHaveRenderers {

  public static ItemRemoteInvAccess create(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemRemoteInvAccess result = new ItemRemoteInvAccess(modObject);
    return result;
  }

  protected ItemRemoteInvAccess(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOInvpanel);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(1);
    modObject.apply(this);
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (ItemRemoteInvAccessType type : ItemRemoteInvAccessType.values()) {
      ResourceLocation resourceLocation = new ResourceLocation(EnderIO.DOMAIN, type.getUnlocalizedName(modObject.getUnlocalisedName()));
      ModelBakery.registerItemVariants(this, resourceLocation);
      ClientUtil.regRenderer(this, type.toMetadata(), resourceLocation);
    }
  }

  @Override
  @Nonnull
  public String getUnlocalizedName(@Nonnull ItemStack stack) {
    return ItemRemoteInvAccessType.fromStack(stack).getUnlocalizedName(getUnlocalizedName());
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs par2CreativeTabs, @Nonnull NonNullList<ItemStack> par3List) {
    if (par2CreativeTabs.equals(EnderIOTab.tabEnderIOInvpanel)) {
      for (ItemRemoteInvAccessType type : ItemRemoteInvAccessType.values()) {
        if (type.isVisible()) {
          par3List.add(new ItemStack(this, 1, type.toMetadata()));
          ItemStack full = new ItemStack(this, 1, type.toMetadata());
          setFull(full);
          par3List.add(full);
        }
      }
    }
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return super.hasEffect(stack) || NbtValue.GLINT.hasTag(stack);
  }

  @Override
  @Nonnull
  public EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, float hitX,
      float hitY, float hitZ, @Nonnull EnumHand hand) {

    if (world.isRemote || !player.isSneaking()) {
      return EnumActionResult.PASS;
    }
    TileEntity te = world.getTileEntity(pos);
    ItemStack stack = player.getHeldItem(hand);
    if (te instanceof TileInventoryPanel) {
      REMOTE_X.setInt(stack, te.getPos().getX());
      REMOTE_Y.setInt(stack, te.getPos().getY());
      REMOTE_Z.setInt(stack, te.getPos().getZ());
      REMOTE_D.setInt(stack, te.getWorld().provider.getDimension());
      player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.set")), true);
      return EnumActionResult.SUCCESS;
    }
    return EnumActionResult.PASS;
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    if (!world.isRemote) {
      ItemStack equipped = player.getHeldItem(hand);
      if (!REMOTE_X.hasTag(equipped) || !REMOTE_Y.hasTag(equipped) || !REMOTE_Z.hasTag(equipped) || !REMOTE_D.hasTag(equipped)) {
        player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.notarget")), true);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      int x = REMOTE_X.getInt(equipped);
      int y = REMOTE_Y.getInt(equipped);
      int z = REMOTE_Z.getInt(equipped);
      int d = REMOTE_D.getInt(equipped);

      ItemRemoteInvAccessType type = ItemRemoteInvAccessType.fromStack(equipped);

      if (!type.inRange(d, x, y, z, world.provider.getDimension(), (int) player.posX, (int) player.posY, (int) player.posZ)) {
        player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outofrange")), true);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      World targetWorld = world;
      if (world.provider.getDimension() != d) {
        targetWorld = DimensionManager.getWorld(d);
        if (targetWorld == null) {
          player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.invalidtargetworld")), true);
          return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
        }
      }

      final BlockPos pos = new BlockPos(x, y, z);
      if (!targetWorld.isBlockLoaded(pos)) {
        player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.notloaded")), true);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      if (targetWorld.getBlockState(pos).getBlock() != InvpanelObject.blockInventoryPanel.getBlock()) {
        player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.invalidtarget")), true);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      if (!(world instanceof WorldServer) || !(player instanceof EntityPlayerMP)) {
        Log.warn("Unexpected world or player: " + world + " " + player);
        player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.error")), true);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      if (getEnergyStored(equipped) < type.getRfPerTick() * 10) {
        player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outofpower")), true);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      if (!drain(equipped, type.getMbPerOpen())) {
        Fluid fluid = type.getFluidType();
        String fluidname = fluid.getLocalizedName(new FluidStack(fluid, 1));
        player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outoffluid").replace("{FLUIDNAME}", fluidname)), true);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      ServerRemoteGuiManager.openGui((EntityPlayerMP) player, targetWorld, pos);
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }
    return super.onItemRightClick(world, player, hand);
  }

  public boolean canInteractWith(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    if (getEnergyStored(stack) > 0) {
      return true;
    } else {
      player.sendStatusMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outofpower")), true);
      return false;
    }
  }

  public void tick(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    if (EnderIO.proxy.getServerTickCount() % 10 == 0) {
      ItemRemoteInvAccessType type = ItemRemoteInvAccessType.fromStack(stack);
      extractInternal(stack, type.getRfPerTick() * 10);
    }
  }

  private boolean extractInternal(@Nonnull ItemStack item, int powerUse) {
    int stored = getEnergyStored(item);
    if (stored >= powerUse) {
      setEnergyStored(item, stored - powerUse);
      return true;
    } else if (stored > 0) {
      setEnergyStored(item, 0);
    }
    return false;
  }

  @Override
  public void setFull(@Nonnull ItemStack container) {
    setEnergyStored(container, getMaxEnergyStored(container));
    FLUIDAMOUNT.setInt(container, getCapacity(container));
  }

  @Override
  public void onCreated(@Nonnull ItemStack itemStack, @Nonnull World world, @Nonnull EntityPlayer entityPlayer) {
    setEnergyStored(itemStack, 0);
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_fluid.render(stack, xPosition, yPosition,
        PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition) ? 1 : 0);
  }

  @Override
  public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
    return slotChanged ? super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
        : (oldStack.isEmpty() || newStack.isEmpty() || !ItemStack.areItemsEqual(oldStack, newStack));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    tooltip.add(LangPower.RF(getEnergyStored(stack), getMaxEnergyStored(stack)));
    tooltip.add(LangFluid.MB(FLUIDAMOUNT.getInt(stack, 0), getFluidType(stack)));
  }

  @Nullable
  private FluidStack getFluid(@Nonnull ItemStack container) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (amount > 0) {
      return new FluidStack(getFluidType(container), amount);
    } else {
      return null;
    }
  }

  private int getCapacity(@Nonnull ItemStack container) {
    return ItemRemoteInvAccessType.fromStack(container).getFluidCapacity();
  }

  @Nonnull
  public Fluid getFluidType(@Nonnull ItemStack stack) {
    return ItemRemoteInvAccessType.fromStack(stack).getFluidType();
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
  public int getMaxEnergyStored(@Nonnull ItemStack container) {
    return ItemRemoteInvAccessType.fromStack(container).getRfCapacity();
  }

  @Override
  public void setEnergyStored(@Nonnull ItemStack container, int energy) {
    ENERGY.setInt(container, energy);
  }

  @Override
  public int getEnergyStored(@Nonnull ItemStack stack) {
    return ENERGY.getInt(stack);
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack stack) {
    return ItemRemoteInvAccessType.fromStack(stack).getRfCapacity() / 100;
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack stack) {
    return 0;
  }

  @Override
  @Nonnull
  public MappedCapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt, @Nonnull MappedCapabilityProvider capProv) {
    return capProv.add(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, new CapabilityProvider(stack));
  }

  private class CapabilityProvider implements IFluidHandlerItem {
    protected final @Nonnull ItemStack container;

    private CapabilityProvider(@Nonnull ItemStack container) {
      this.container = container;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      return new IFluidTankProperties[] { new IFluidTankProperties() {

        @Override
        @Nullable
        public FluidStack getContents() {
          return ItemRemoteInvAccess.this.getFluid(container);
        }

        @Override
        public int getCapacity() {
          return ItemRemoteInvAccess.this.getCapacity(container);
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
    public int fill(@Nullable FluidStack resource, boolean doFill) {
      if (container.isEmpty() || !(container.getItem() == ItemRemoteInvAccess.this) || resource == null || resource.amount <= 0 || resource.getFluid() == null
          || resource.getFluid() != getFluidType(container)) {
        return 0;
      }
      int amount = FLUIDAMOUNT.getInt(container, 0);
      int capacity = getCapacity(container);
      int free = capacity - amount;
      int toFill = Math.min(resource.amount, free);
      if (toFill > 0 && doFill) {
        FLUIDAMOUNT.setInt(container, amount + toFill);
      }
      return toFill;
    }

    @Override
    @Nullable
    public FluidStack drain(@Nullable FluidStack resource, boolean doDrain) {
      return null;
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain) {
      return null;
    }

    @Override
    @Nonnull
    public ItemStack getContainer() {
      return container;
    }
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
    List<String> list0 = new ArrayList<String>();
    SpecialTooltipHandler.addDetailedTooltipFromResources(list0, getUnlocalizedName(itemstack));
    Fluid fluid = getFluidType(itemstack);
    String fluidname = fluid.getLocalizedName(new FluidStack(fluid, 1));
    for (String string : list0) {
      list.add(string.replace("{FLUIDNAME}", fluidname));
    }
  }

}
