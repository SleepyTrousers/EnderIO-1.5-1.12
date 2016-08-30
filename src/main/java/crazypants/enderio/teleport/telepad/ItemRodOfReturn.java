package crazypants.enderio.teleport.telepad;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.ClientUtil;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.vecmath.Vector3d;

import cofh.api.energy.ItemEnergyContainer;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.machine.MachineSound;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.teleport.TeleportUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.util.NbtValue.FLUIDAMOUNT;

public class ItemRodOfReturn extends ItemEnergyContainer implements IResourceTooltipProvider, IOverlayRenderAware {

  public static ItemRodOfReturn create() {
    ItemRodOfReturn result = new ItemRodOfReturn();
    result.init();
    return result;
  }
  
  private static final int RF_MAX_INPUT = (int) Math.ceil(Config.rodOfReturnPowerStorage / (double) Config.rodOfReturnMinTicksToRecharge);

  private static final String KEY_LAST_USED_TICK = "lastUsedAt";
  
  @SideOnly(Side.CLIENT)
  private MachineSound activeSound;

  protected ItemRodOfReturn() {
    super(Config.rodOfReturnPowerStorage, RF_MAX_INPUT, 0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemRodOfReturn.getUnlocalisedName());
    setRegistryName(ModObject.itemRodOfReturn.getUnlocalisedName());
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      EnumHand hand) {

    if (world.isRemote) {
      // If we dont return pass on the client this wont get called on the server
      return EnumActionResult.PASS;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ITelePad && player.isSneaking()) {
      ITelePad tp = (ITelePad)te;
      pos = tp.getMaster().getLocation().getBlockPos();
      setTarget(stack, pos, world.provider.getDimension());
      player.addChatMessage(
          new TextComponentString(EnderIO.lang.localize("itemRodOfReturn.chat.sync") + " [" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]"));
      player.stopActiveHand();
      return EnumActionResult.SUCCESS;
    }

    return EnumActionResult.PASS;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
    long lastUsed = getLastUsedTick(stack);
    if ( (lastUsed < 0 || (world.getTotalWorldTime() - lastUsed) > 20) && getEnergyStored(stack) > 0) {
      player.setActiveHand(hand);

    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
  }

  @Override
  public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
    if(player.worldObj.isRemote) {
      onUsingClient(stack, player, count);
    }
    
    int used = (Config.rodOfReturnTicksToActivate - count) * 1000;
    int newVal = getEnergyStored(stack) - used;
    if (newVal < 0) { 
      if (player.worldObj.isRemote) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemRodOfReturn.chat.notEnoughPower", TextFormatting.RED.toString())));
      } 
      player.stopActiveHand();
    }
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
    if (! (player instanceof EntityPlayer) || !((EntityPlayer)player).capabilities.isCreativeMode) {
      updateStackNBT(stack, world, timeLeft);
    }
    if(world.isRemote) {
      stopPlayingSound();
    }
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    boolean hasPower = true;
    boolean hasFluid = true;
    if (! (entityLiving instanceof EntityPlayer) || !((EntityPlayer)entityLiving).capabilities.isCreativeMode) {
      hasPower = updateStackNBT(stack, worldIn, 0);
      hasFluid = hasPower ? useFluid(stack) : true; //don't use fluid if we didn't have enough power        
    }
        
    if (hasPower && hasFluid) {
      TelepadTarget target = TelepadTarget.readFromNBT(stack);
      if(target == null) {
        if (worldIn.isRemote) {
          stopPlayingSound();
          entityLiving.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemRodOfReturn.chat.targetNotSet", TextFormatting.RED.toString())));
        }
        return stack;
      }            
      TeleportUtil.doTeleport(entityLiving, target.getLocation(), target.getDimension(), false, TravelSource.TELEPAD);
    } else if(worldIn.isRemote) {
      if(!hasPower) {
        entityLiving.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemRodOfReturn.chat.notEnoughPower", TextFormatting.RED.toString())));
      } else {
        entityLiving.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemRodOfReturn.chat.notEnoughFluid", TextFormatting.RED.toString())));
      }
    } 
    
    if(worldIn.isRemote) {
      stopPlayingSound();
    }
    
    return stack;
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldS, ItemStack newS, boolean slotChanged) {
    return slotChanged || oldS == null || newS == null || oldS.getItem() != newS.getItem();
  }

  @Override
  public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
    return shouldCauseReequipAnimation(oldStack, newStack, false);
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }

  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    setEnergy(itemStack, 0);
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return Config.rodOfReturnTicksToActivate;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    String str;
        
    str = PowerDisplayUtil.formatPower(FLUIDAMOUNT.getInt(itemStack, 0)) + "/" + PowerDisplayUtil.formatPower(Config.rodOfReturnFluidStorage) + " MB";
    list.add(str);
    
    str = PowerDisplayUtil.formatPower(getEnergyStored(itemStack)) + "/" + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemStack)) + " "
        + PowerDisplayUtil.abrevation();
    list.add(str);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    setFull(is);
    par3List.add(is);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {        
    PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition, true);
    PowerBarOverlayRenderHelper.instance_fluid.render(stack, xPosition, yPosition, 1, true);
  }

  @SideOnly(Side.CLIENT)
  private void onUsingClient(ItemStack stack, EntityLivingBase player, int timeLeft) {
    
    if (timeLeft > (Config.rodOfReturnTicksToActivate - 2)) {       
      return;
    }
    
    float progress = 1 - ((float)timeLeft/Config.rodOfReturnTicksToActivate);
    float spinSpeed = progress * 2;
    if (activeSound != null) {
      activeSound.setPitch(MathHelper.clamp_float(0.5f + (spinSpeed / 1.5f), 0.5f, 2));
    }    
    if (activeSound == null) {
      BlockPos p = player.getPosition();
      activeSound = new MachineSound(TileTelePad.ACTIVE_RES, p.getX(), p.getY(), p.getZ(), 0.3f, 1);
      playSound();
    } 
    
    double dist = 2 - (progress * 1.5);
    Random rand = player.worldObj.rand;
    for(int i=0;i<6;i++) {
      double xo = randomOffset(rand, dist);
      double yo = randomOffset(rand, dist) ;
      double zo = randomOffset(rand, dist);
      
      double x = player.posX + xo;
      double y = player.posY + yo + player.height/2;
      double z = player.posZ + zo;

      Vector3d velocity = new Vector3d(xo,yo,zo);
      velocity.normalize();

      Particle fx = Minecraft.getMinecraft().effectRenderer
          .spawnEffectParticle(EnumParticleTypes.PORTAL.getParticleID(), x, y,z, 0, 0, 0, 0);
      if (fx != null) {
//        if(rand.nextInt(8) == 0) {
//          fx.setRBGColorF((rand.nextFloat() * 0.1f), 0.6f + (rand.nextFloat() * 0.15f), 0.75f + (rand.nextFloat() * 0.2f));
//        }
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
    FMLClientHandler.instance().getClient().getSoundHandler().playSound(activeSound);
  }
  
  @SideOnly(Side.CLIENT)
  private void stopPlayingSound() {
    if (activeSound != null) {
      activeSound.endPlaying();
      activeSound = null;
    }
  }
  
  // --------------------- NBT Handling for stacks

  private boolean updateStackNBT(ItemStack stack, World world, int timeLeft) {
    setLastUsedTick(stack, world.getTotalWorldTime());
     // half a second before it costs you
    if (timeLeft > (Config.rodOfReturnTicksToActivate - 10)) {       
      return false;
    }
    return useEnergy(stack, timeLeft);
  }

  private long getLastUsedTick(ItemStack stack) {
    if (stack == null || !stack.hasTagCompound() || !stack.getTagCompound().hasKey(KEY_LAST_USED_TICK)) {
      return -1;
    }
    return stack.getTagCompound().getLong(KEY_LAST_USED_TICK);
  }

  private void setLastUsedTick(ItemStack stack, long tick) {
    if (!stack.hasTagCompound()) {
      stack.setTagCompound(new NBTTagCompound());
    }
    stack.getTagCompound().setLong(KEY_LAST_USED_TICK, tick);
  }

  private boolean useEnergy(ItemStack stack, int timeLeft) {
    int used = (Config.rodOfReturnTicksToActivate - timeLeft) * Config.rodOfReturnRfPerTick;
    int newVal = getEnergyStored(stack) - used;
    if (newVal < 0) {
      setEnergy(stack, 0);
      return false;
    }
    setEnergy(stack, newVal);
    return true;
  }

  private void setEnergy(ItemStack container, int energy) {
    if (container.getTagCompound() == null) {
      container.setTagCompound(new NBTTagCompound());
    }
    container.getTagCompound().setInteger("Energy", energy);
  }

  private void setFull(ItemStack container) {
    setEnergy(container, Config.rodOfReturnPowerStorage);
    FLUIDAMOUNT.setInt(container, Config.rodOfReturnFluidStorage);
  }

  private void setTarget(ItemStack container, BlockPos pos, int dimension) {
    if (container.getTagCompound() == null) {
      container.setTagCompound(new NBTTagCompound());
    }
    new TelepadTarget(pos, dimension).writeToNBT(container.getTagCompound());    
  }
  
  // Fluid handeling
    
  private boolean useFluid(ItemStack container) {    
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (Config.rodOfReturnFluidUsePerTeleport > amount) {
      FLUIDAMOUNT.setInt(container, 0);
      return false;
    } else {
      FLUIDAMOUNT.setInt(container, amount - Config.rodOfReturnFluidUsePerTeleport);
      return true;
    }
  }  
  
  public FluidStack getFluid(ItemStack container) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (amount > 0) {
      return new FluidStack(Fluids.fluidEnderDistillation, amount);
    } else {
      return null;
    }
  }
 
  public int getCapacity(ItemStack container) {
    return Config.rodOfReturnFluidStorage;
  }

  public int fill(ItemStack container, FluidStack resource, boolean doFill) {
    if (container == null || !(container.getItem() == this) || resource == null || resource.amount <= 0 || resource.getFluid() == null
        || resource.getFluid() != Fluids.fluidEnderDistillation) {
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
      return new IFluidTankProperties[] { new IFluidTankProperties() {

        @Override
        @Nullable
        public FluidStack getContents() {
          return getFluid(container);
        }

        @Override
        public int getCapacity() {
          return ItemRodOfReturn.this.getCapacity(container);
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
          return fluidStack != null && fluidStack.getFluid() == Fluids.fluidEnderDistillation;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
          return false;
        }
      } };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      return ItemRodOfReturn.this.fill(container, resource, doFill);
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

}
