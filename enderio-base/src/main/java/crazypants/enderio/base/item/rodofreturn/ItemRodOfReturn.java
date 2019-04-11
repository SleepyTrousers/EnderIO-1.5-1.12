package crazypants.enderio.base.item.rodofreturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.ClientUtil;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.CompoundCapabilityProvider;
import com.enderio.core.common.interfaces.IOverlayRenderAware;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.config.ItemConfig;
import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.sound.MachineSound;
import crazypants.enderio.base.power.forge.item.AbstractPoweredItem;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.base.teleport.TeleportUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.util.NbtValue.FLUIDAMOUNT;
import static crazypants.enderio.util.NbtValue.LAST_USED_TICK;

public class ItemRodOfReturn extends AbstractPoweredItem implements IAdvancedTooltipProvider, IOverlayRenderAware {

  public static ItemRodOfReturn create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemRodOfReturn(modObject);
  }

  private static final int RF_MAX_INPUT = (int) Math.ceil(ItemConfig.rodOfReturnPowerStorage.get() / (double) ItemConfig.rodOfReturnMinTicksToRecharge.get());

  public static final @Nonnull ResourceLocation ACTIVE_RES = new ResourceLocation(EnderIO.DOMAIN, "telepad.active");
  @SideOnly(Side.CLIENT)
  private MachineSound activeSound;

  protected ItemRodOfReturn(@Nonnull IModObject modObject) {
    super(ItemConfig.rodOfReturnPowerStorage.get(), RF_MAX_INPUT, 0);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  @Override
  public @Nonnull EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side,
      float hitX, float hitY, float hitZ, @Nonnull EnumHand hand) {

    if (world.isRemote || !player.isSneaking()) {
      // If we don't return pass on the client this wont get called on the server
      return EnumActionResult.PASS;
    }
    ItemStack stack = player.getHeldItem(hand);
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ITelePad) {
      ITelePad tp = ((ITelePad) te).getMaster();
      if (tp != null) {
        pos = tp.getLocation();
        setTarget(stack, pos, world.provider.getDimension());
        player.sendMessage(Lang.RETURN_ROD_SYNC_TELEPAD.toChat(BlockCoord.chatString(pos, TextFormatting.WHITE)));
        player.stopActiveHand();
        return EnumActionResult.SUCCESS;
      }
    }
    if (ItemConfig.rodOfReturnCanTargetAnywhere.get()) {
      setTarget(stack, pos, world.provider.getDimension());
      player.sendMessage(Lang.RETURN_ROD_SYNC.toChat(BlockCoord.chatString(pos, TextFormatting.WHITE)));
      player.stopActiveHand();
      return EnumActionResult.SUCCESS;
    }
    return EnumActionResult.PASS;
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    long lastUsed = LAST_USED_TICK.getLong(stack);
    if ((lastUsed < 0 || (world.getTotalWorldTime() - lastUsed) > 20) && getEnergyStored(stack) > 0) {
      player.setActiveHand(hand);

    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
  }

  @Override
  public void onUsingTick(@Nonnull ItemStack stack, @Nonnull EntityLivingBase player, int count) {
    if (player.world.isRemote) {
      onUsingClient(stack, player, count);
    }

    int used = (ItemConfig.rodOfReturnTicksToActivate.get() - count) * 1000;
    int newVal = getEnergyStored(stack) - used;
    if (newVal < 0) {
      if (player.world.isRemote) {
        player.sendMessage(Lang.RETURN_ROD_NO_POWER.toChat(TextFormatting.RED));
      }
      player.stopActiveHand();
    }
  }

  @Override
  public void onPlayerStoppedUsing(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase player, int timeLeft) {
    if (!(player instanceof EntityPlayer) || !((EntityPlayer) player).capabilities.isCreativeMode) {
      updateStackNBT(stack, world, timeLeft);
    }
    if (world.isRemote) {
      stopPlayingSound();
    }
  }

  @Override
  public @Nonnull ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull EntityLivingBase entityLiving) {
    boolean hasPower = true;
    boolean hasFluid = true;
    if (!(entityLiving instanceof EntityPlayer) || !((EntityPlayer) entityLiving).capabilities.isCreativeMode) {
      hasPower = updateStackNBT(stack, worldIn, 0);
      hasFluid = hasPower ? useFluid(stack) : true; // don't use fluid if we didn't have enough power
    }

    if (hasPower && hasFluid) {
      TelepadTarget target = TelepadTarget.readFromNBT(stack);
      if (target == null) {
        if (worldIn.isRemote) {
          stopPlayingSound();
          entityLiving.sendMessage(Lang.RETURN_ROD_NO_TARGET.toChat(TextFormatting.RED));
        }
        return stack;
      }
      TeleportUtil.doTeleport(entityLiving, target.getLocation(), target.getDimension(), false, TravelSource.TELEPAD);
    } else if (worldIn.isRemote) {
      if (!hasPower) {
        entityLiving.sendMessage(Lang.RETURN_ROD_NO_POWER.toChat(TextFormatting.RED));
      } else {
        entityLiving.sendMessage(Lang.RETURN_ROD_NO_FLUID.toChat(TextFormatting.RED));
      }
    }

    if (worldIn.isRemote) {
      stopPlayingSound();
    }

    return stack;
  }

  @Override
  public boolean canDestroyBlockInCreative(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return false;
  }

  @Override
  public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldS, @Nonnull ItemStack newS, boolean slotChanged) {
    return slotChanged || oldS.getItem() != newS.getItem();
  }

  @Override
  public boolean shouldCauseBlockBreakReset(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack) {
    return shouldCauseReequipAnimation(oldStack, newStack, false);
  }

  @Override
  public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    return true;
  }

  @Override
  public void onCreated(@Nonnull ItemStack itemStack, @Nonnull World world, @Nonnull EntityPlayer entityPlayer) {
    setEnergyStored(itemStack, 0);
  }

  @Override
  public @Nonnull EnumAction getItemUseAction(@Nonnull ItemStack stack) {
    return EnumAction.BOW;
  }

  @Override
  public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
    return ItemConfig.rodOfReturnTicksToActivate.get();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (usesFluid()) {
      tooltip.add(Lang.RETURN_ROD_FLUID.get(LangFluid.MB(FLUIDAMOUNT.getInt(stack, 0), ItemConfig.rodOfReturnFluidStorage.get())));
    }
    tooltip.add(Lang.RETURN_ROD_POWER.get(LangPower.RF(getEnergyStored(stack), ItemConfig.rodOfReturnPowerStorage.get())));
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      setFull(is);
      list.add(is);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition, true);
    if (usesFluid()) {
      PowerBarOverlayRenderHelper.instance_fluid.render(stack, xPosition, yPosition, 1, true);
    }
  }

  @SideOnly(Side.CLIENT)
  private void onUsingClient(ItemStack stack, EntityLivingBase player, int timeLeft) {

    if (timeLeft > (ItemConfig.rodOfReturnTicksToActivate.get() - 2)) {
      return;
    }

    float progress = 1 - ((float) timeLeft / ItemConfig.rodOfReturnTicksToActivate.get());
    float spinSpeed = progress * 2;
    if (activeSound != null) {
      activeSound.setPitch(MathHelper.clamp(0.5f + (spinSpeed / 1.5f), 0.5f, 2));
    }
    if (activeSound == null) {
      BlockPos p = player.getPosition();
      activeSound = new MachineSound(ACTIVE_RES, p.getX(), p.getY(), p.getZ(), 0.3f, 1);
      playSound();
    }

    double dist = 2 - (progress * 1.5);
    Random rand = player.world.rand;
    for (int i = 0; i < 6; i++) {
      double xo = randomOffset(rand, dist);
      double yo = randomOffset(rand, dist);
      double zo = randomOffset(rand, dist);

      double x = player.posX + xo;
      double y = player.posY + yo + player.height / 2;
      double z = player.posZ + zo;

      Vector3d velocity = new Vector3d(xo, yo, zo);
      velocity.normalize();

      Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.PORTAL.getParticleID(), x, y, z, 0, 0, 0, 0);
      if (fx != null) {
        // if(rand.nextInt(8) == 0) {
        // fx.setRBGColorF((rand.nextFloat() * 0.1f), 0.6f + (rand.nextFloat() * 0.15f), 0.75f + (rand.nextFloat() * 0.2f));
        // }
        ClientUtil.setParticleVelocity(fx, velocity.x, velocity.y, velocity.z);
        fx.setMaxAge(timeLeft + 2);
      }
    }

  }

  private double randomOffset(Random rand, double magnitude) {
    return (rand.nextDouble() - 0.5) * magnitude;
  }

  @SideOnly(Side.CLIENT)
  private void playSound() {
    final MachineSound activeSound_nullchecked = activeSound;
    if (activeSound_nullchecked != null) {
      FMLClientHandler.instance().getClient().getSoundHandler().playSound(activeSound_nullchecked);
    }
  }

  @SideOnly(Side.CLIENT)
  private void stopPlayingSound() {
    if (activeSound != null) {
      activeSound.endPlaying();
      activeSound = null;
    }
  }

  // --------------------- NBT Handling for stacks

  private boolean updateStackNBT(@Nonnull ItemStack stack, @Nonnull World world, int timeLeft) {
    LAST_USED_TICK.setLong(stack, world.getTotalWorldTime());
    // half a second before it costs you
    if (timeLeft > (ItemConfig.rodOfReturnTicksToActivate.get() - 10)) {
      return false;
    }
    return useEnergy(stack, timeLeft);
  }

  private boolean useEnergy(@Nonnull ItemStack stack, int timeLeft) {
    int used = (ItemConfig.rodOfReturnTicksToActivate.get() - timeLeft) * ItemConfig.rodOfReturnRfPerTick.get();
    int newVal = getEnergyStored(stack) - used;
    if (newVal < 0) {
      setEnergyStored(stack, 0);
      return false;
    }
    setEnergyStored(stack, newVal);
    return true;
  }

  @Override
  public void setFull(@Nonnull ItemStack container) {
    super.setFull(container);
    if (usesFluid()) {
      FLUIDAMOUNT.setInt(container, ItemConfig.rodOfReturnFluidStorage.get());
    }
  }

  private void setTarget(@Nonnull ItemStack container, @Nonnull BlockPos pos, int dimension) {
    new TelepadTarget(pos, dimension).writeToNBT(container);
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, getUnlocalizedName());
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addBasicTooltipFromResources(list, getUnlocalizedName());
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    List<String> entries = new ArrayList<String>();
    SpecialTooltipHandler.addDetailedTooltipFromResources(entries, getUnlocalizedName());
    String fluidString = usesFluid() ? ItemConfig.rodOfReturnFluidType.get().getLocalizedName(new FluidStack(ItemConfig.rodOfReturnFluidType.get(), 1000))
        : "---";
    for (int i = 0; i < entries.size(); i++) {
      String str = entries.get(i);
      list.add(String.format(str, fluidString));
    }
  }

  // Fluid handling

  private boolean useFluid(@Nonnull ItemStack container) {
    if (!usesFluid()) {
      return true;
    }
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (ItemConfig.rodOfReturnFluidUsePerTeleport.get() > amount) {
      FLUIDAMOUNT.setInt(container, 0);
      return false;
    } else {
      FLUIDAMOUNT.setInt(container, amount - ItemConfig.rodOfReturnFluidUsePerTeleport.get());
      return true;
    }
  }

  public FluidStack getFluid(@Nonnull ItemStack container) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (amount > 0 && usesFluid()) {
      return new FluidStack(ItemConfig.rodOfReturnFluidType.get(), amount);
    } else {
      return null;
    }
  }

  public int fill(@Nonnull ItemStack container, @Nonnull FluidStack resource, boolean doFill) {
    if (!(container.getItem() == this) || resource.amount <= 0 || resource.getFluid() == null || resource.getFluid() != ItemConfig.rodOfReturnFluidType.get()
        || !usesFluid()) {
      return 0;
    }
    int amount = FLUIDAMOUNT.getInt(container, 0);
    int capacity = ItemConfig.rodOfReturnFluidStorage.get();
    int free = capacity - amount;
    int toFill = Math.min(resource.amount, free);
    if (toFill > 0 && doFill) {
      FLUIDAMOUNT.setInt(container, amount + toFill);
    }
    return toFill;
  }

  private static boolean usesFluid() {
    return ItemConfig.rodOfReturnFluidUsePerTeleport.get() > 0;
  }

  @Override
  public @Nonnull ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return new CompoundCapabilityProvider(new FluidCapabilityProvider(stack), super.initCapabilities(stack, nbt));
  }

  private final class FluidCapabilityProvider implements IFluidHandlerItem, ICapabilityProvider {
    protected final @Nonnull ItemStack container;

    private FluidCapabilityProvider(@Nonnull ItemStack container) {
      this.container = container;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == FluidUtil.getFluidItemCapability();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return capability == FluidUtil.getFluidItemCapability() ? (T) this : null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      return new IFluidTankProperties[] { new IFluidTankProperties() {

        @Override
        @Nullable
        public FluidStack getContents() {
          return getFluid(container);
        }

        @Override
        public int getCapacity() {
          return ItemConfig.rodOfReturnFluidStorage.get();
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
          return fluidStack != null && fluidStack.getFluid() == ItemConfig.rodOfReturnFluidType.get();
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
          return false;
        }
      } };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      return ItemRodOfReturn.this.fill(container, NullHelper.notnull(resource, "Cannot use null as a fluid stack"), doFill);
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

    @Override
    @Nonnull
    public ItemStack getContainer() {
      return container;
    }
  }

}
