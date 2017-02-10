package crazypants.enderio.machine.invpanel.remote;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.CompoundCapabilityProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.machine.invpanel.TileInventoryPanel;
import crazypants.enderio.power.IInternalPoweredItem;
import crazypants.enderio.power.ItemPowerCapabilityBackend;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import crazypants.util.NbtValue;
import net.minecraft.client.renderer.block.model.ModelBakery;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockInventoryPanel;
import static crazypants.util.NbtValue.ENERGY;
import static crazypants.util.NbtValue.FLUIDAMOUNT;
import static crazypants.util.NbtValue.REMOTE_D;
import static crazypants.util.NbtValue.REMOTE_X;
import static crazypants.util.NbtValue.REMOTE_Y;
import static crazypants.util.NbtValue.REMOTE_Z;

public class ItemRemoteInvAccess extends Item
    implements IAdvancedTooltipProvider, IOverlayRenderAware, IFluidContainerItem, IInternalPoweredItem, IHaveRenderers {

  public static ItemRemoteInvAccess create() {
    ClientRemoteGuiManager.create();
    ItemRemoteInvAccess result = new ItemRemoteInvAccess();
    result.init();
    return result;
  }

  protected ItemRemoteInvAccess() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(ModObject.itemRemoteInvAccess.getUnlocalisedName());
    setRegistryName(ModObject.itemRemoteInvAccess.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(1);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    for (ItemRemoteInvAccessType type : ItemRemoteInvAccessType.values()) {
      ResourceLocation resourceLocation = new ResourceLocation(EnderIO.DOMAIN, type.getUnlocalizedName(ModObject.itemRemoteInvAccess.getUnlocalisedName()));
      ModelBakery.registerItemVariants(this, resourceLocation);
      ClientUtil.regRenderer(this, type.toMetadata(), resourceLocation);
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return ItemRemoteInvAccessType.fromStack(stack).getUnlocalizedName(getUnlocalizedName());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (ItemRemoteInvAccessType type : ItemRemoteInvAccessType.values()) {
      if (type.isVisible()) {
        par3List.add(new ItemStack(par1, 1, type.toMetadata()));
        par3List.add(setFull(new ItemStack(par1, 1, type.toMetadata())));
      }
    }
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return super.hasEffect(stack) || NbtValue.GLINT.hasTag(stack);
  }

  @Override
  public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      EnumHand hand) {

    if (world.isRemote || !player.isSneaking()) {
      return EnumActionResult.PASS;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileInventoryPanel) {
      REMOTE_X.setInt(stack, te.getPos().getX());
      REMOTE_Y.setInt(stack, te.getPos().getY());
      REMOTE_Z.setInt(stack, te.getPos().getZ());
      REMOTE_D.setInt(stack, te.getWorld().provider.getDimension());
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.set")));
      return EnumActionResult.SUCCESS;
    }
    return EnumActionResult.PASS;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack equipped, World world, EntityPlayer player, EnumHand hand) {
    if (!world.isRemote) {
      if (!REMOTE_X.hasTag(equipped) || !REMOTE_Y.hasTag(equipped) || !REMOTE_Z.hasTag(equipped) || !REMOTE_D.hasTag(equipped)) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.notarget")));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      int x = REMOTE_X.getInt(equipped);
      int y = REMOTE_Y.getInt(equipped);
      int z = REMOTE_Z.getInt(equipped);
      int d = REMOTE_D.getInt(equipped);

      ItemRemoteInvAccessType type = ItemRemoteInvAccessType.fromStack(equipped);

      if (!type.inRange(d, x, y, z, world.provider.getDimension(), (int) player.posX, (int) player.posY, (int) player.posZ)) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outofrange")));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      World targetWorld = world;
      if (world.provider.getDimension() != d) {
        targetWorld = DimensionManager.getWorld(d);
        if (targetWorld == null) {
          player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.invalidtargetworld")));
          return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
        }
      }

      final BlockPos pos = new BlockPos(x, y, z);
      if (!targetWorld.isBlockLoaded(pos)) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.notloaded")));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      if (targetWorld.getBlockState(pos).getBlock() != blockInventoryPanel.getBlock()) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.invalidtarget")));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      if (!(world instanceof WorldServer) || !(player instanceof EntityPlayerMP)) {
        Log.warn("Unexpected world or player: " + world + " " + player);
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.error")));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      if (getEnergyStored(equipped) < type.getRfPerTick() * 10) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outofpower")));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      if (!drain(equipped, type.getMbPerOpen())) {
        Fluid fluid = type.getFluidType();
        String fluidname = fluid.getLocalizedName(new FluidStack(fluid, 1));
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outoffluid").replace("{FLUIDNAME}", fluidname)));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
      }

      ServerRemoteGuiManager.openGui((EntityPlayerMP) player, targetWorld, pos);
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }
    return super.onItemRightClick(equipped, world, player, hand);
  }

  public boolean canInteractWith(ItemStack stack, EntityPlayer player) {
    if (getEnergyStored(stack) > 0) {
      return true;
    } else {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outofpower")));
      return false;
    }
  }

  public void tick(ItemStack stack, EntityPlayer player) {
    if (EnderIO.proxy.getServerTickCount() % 10 == 0) {
      ItemRemoteInvAccessType type = ItemRemoteInvAccessType.fromStack(stack);
      extractInternal(stack, type.getRfPerTick() * 10);
    }
  }

  private boolean extractInternal(ItemStack item, int powerUse) {
    int stored = getEnergyStored(item);
    if (stored >= powerUse) {
      setEnergyStored(item, stored - powerUse);
      return true;
    } else if (stored > 0) {
      setEnergyStored(item, 0);
    }
    return false;
  }

 
  public ItemStack setFull(ItemStack container) {
    setEnergyStored(container, getMaxEnergyStored(container));
    FLUIDAMOUNT.setInt(container, getCapacity(container));
    return container;
  }

  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    setEnergyStored(itemStack, 0);
  }

  

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_fluid.render(stack, xPosition, yPosition,
        PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition) ? 1 : 0);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return slotChanged ? super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
        : (oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    list.add(PowerDisplayUtil.formatPower(getEnergyStored(itemStack)) + "/" + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemStack)) + " "
        + PowerDisplayUtil.abrevation());
    list.add(FLUIDAMOUNT.getInt(itemStack, 0) + " " + EnderIO.lang.localize("fluid.millibucket.abr") + " " + PowerDisplayUtil.ofStr() + " "
        + getFluidType(itemStack).getLocalizedName(null));
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
    return ItemRemoteInvAccessType.fromStack(container).getFluidCapacity();
  }

  @Override
  public int fill(ItemStack container, FluidStack resource, boolean doFill) {
    if (container == null || !(container.getItem() == this) || resource == null || resource.amount <= 0 || resource.getFluid() == null
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
  
  public @Nonnull Fluid getFluidType(ItemStack stack) {
    return ItemRemoteInvAccessType.fromStack(stack).getFluidType();
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
  public int getMaxEnergyStored(ItemStack container) {
    return ItemRemoteInvAccessType.fromStack(container).getRfCapacity();
  }
  
  @Override
  public void setEnergyStored(ItemStack container, int energy) {
   ENERGY.setInt(container, energy);
  }
  
  @Override
  public int getEnergyStored(ItemStack stack) {
    return ENERGY.getInt(stack);
  }

  @Override
  public int getMaxInput(ItemStack stack) {
    return ItemRemoteInvAccessType.fromStack(stack).getRfCapacity()/100;
  }

  @Override
  public int getMaxOutput(ItemStack stack) {
    return 0;
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    return new CompoundCapabilityProvider(new CapabilityProvider(stack), new ItemPowerCapabilityBackend(stack));
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
    public int fill(FluidStack resource, boolean doFill) {
      return ItemRemoteInvAccess.this.fill(container, resource, doFill);
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
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, getUnlocalizedName(itemstack));
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addBasicTooltipFromResources(list, getUnlocalizedName(itemstack));
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    List<String> list0 = new ArrayList<String>();
    SpecialTooltipHandler.addDetailedTooltipFromResources(list0, getUnlocalizedName(itemstack));
    Fluid fluid = getFluidType(itemstack);
    String fluidname = fluid.getLocalizedName(new FluidStack(fluid, 1));
    for (String string : list0) {
      list.add(string.replace("{FLUIDNAME}", fluidname));
    }
  }

}
