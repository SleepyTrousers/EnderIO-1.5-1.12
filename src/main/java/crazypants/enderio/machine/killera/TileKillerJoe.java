package crazypants.enderio.machine.killera;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.fluid.SmartTankFluidHandler;
import crazypants.enderio.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.FakePlayerEIO;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.zombie.IHasNutrientTank;
import crazypants.enderio.machine.generator.zombie.PacketNutrientTank;
import crazypants.enderio.machine.ranged.IRanged;
import crazypants.enderio.machine.ranged.RangeParticle;
import crazypants.enderio.machine.wireless.WirelessChargedLocation;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.SmartTank;
import crazypants.enderio.xp.ExperienceContainer;
import crazypants.enderio.xp.IHaveExperience;
import crazypants.enderio.xp.PacketExperianceContainer;
import crazypants.enderio.xp.XpUtil;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileKillerJoe extends AbstractMachineEntity
    implements IHaveExperience, ITankAccess.IExtendedTankAccess, IHasNutrientTank, Predicate<EntityXPOrb>, IRanged {

  public static class ZombieCache {

    private Set<EntityZombie> cache = Sets.newHashSet();

    @SubscribeEvent
    public void onSummonAid(SummonAidEvent event) {
      if (!cache.isEmpty() && cache.remove(event.getSummoner())) {
        event.setResult(Result.DENY);
      }
    }
  }

  public static ZombieCache zCache;

  private static final int IO_MB_TICK = 250;

  protected BoundingBox killBounds;

  private EnumFacing[] frontFaceAndSides;

  protected AxisAlignedBB hooverBounds;

  protected Attackera attackera;

  protected WirelessChargedLocation chargedLocation;

  @Store
  final SmartTank tank = new SmartTank(Fluids.fluidNutrientDistillation, FluidContainerRegistry.BUCKET_VOLUME * 2);

  int lastFluidLevelUpdate;

  private boolean tanksDirty;

  private boolean isSwingInProgress;

  private int swingProgressInt;

  private float swingProgress;

  private float prevSwingProgress;

  @Store
  private final ExperienceContainer xpCon;

  private boolean hadSword;

  public TileKillerJoe() {
    super(new SlotDefinition(1, 0, 0));
    
    int maxXP;
    if(Config.killerJoeMaxXpLevel <= 0) {
      maxXP = Integer.MAX_VALUE;
    } else {
      maxXP = XpUtil.getExperienceForLevel(Config.killerJoeMaxXpLevel);
    }
    xpCon = new ExperienceContainer(maxXP);
    xpCon.setTileEntity(this);
    xpCon.setCanFill(false);
    if (zCache == null) {
      zCache = new ZombieCache();
      MinecraftForge.EVENT_BUS.register(zCache);
    }
    tank.setTileEntity(this);
    tank.setCanDrain(false);
  }

  @Override
  public @Nonnull String getMachineName() {
    return ModObject.blockKillerJoe.getUnlocalisedName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (itemstack == null) {
      return false;
    }
    return itemstack.getItem() instanceof ItemSword || itemstack.getItem() instanceof ItemAxe || itemstack.getItem() == Items.STICK;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public void doUpdate() {
    updateArmSwingProgress();
    hooverXP();
    if (!worldObj.isRemote) {
      getAttackera().onUpdate();
      if (inventory[0] != null != hadSword) {
        updateBlock();
        hadSword = inventory[0] != null;
      }
    }
    super.doUpdate();
  }

  @Override
  public ExperienceContainer getContainer() {
    return xpCon;
  }

  private static final @Nonnull int[] slots = new int[1];

  @Override
  public @Nonnull int[] getSlotsForFace(EnumFacing var1) {
    return slots;
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {
    if (isSideDisabled(side)) {
      return false;
    }
    if (inventory[slot] == null || inventory[slot].stackSize < itemstack.stackSize) {
      return false;
    }
    return itemstack.getItem() == inventory[slot].getItem();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {

    //send any maintaince packets no more than twice a second
    if (shouldDoWorkThisTick(10)) {
      if (tanksDirty) {
        PacketHandler.sendToAllAround(new PacketNutrientTank(this), this);
        tanksDirty = false;
      }
      if (xpCon.isDirty()) {
        PacketHandler.sendToAllAround(new PacketExperianceContainer(this), this);
        xpCon.setDirty(false);
      }
    }

    if (!redstoneCheck) {
      return false;
    }

    if (tank.getFluidAmount() < getActivationAmount()) {
      return false;
    }

    if (getStackInSlot(0) == null) {
      return false;
    }

    
    Attackera atackera = getAttackera();
    atackera.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getStackInSlot(0));
    if(atackera.getTicksSinceLastSwing() < atackera.getCooldownPeriod()) {
      return false;
    }

    List<EntityLivingBase> entsInBounds = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, getKillBounds());
    if (!entsInBounds.isEmpty()) {

      for (EntityLivingBase ent : entsInBounds) {
        if (!ent.isDead && ent.deathTime <= 0 && !ent.isEntityInvulnerable(DamageSource.generic) && ent.hurtResistantTime == 0) {
          if (ent instanceof EntityPlayer && ((EntityPlayer) ent).capabilities.disableDamage) {
            continue; // Ignore players in creative, can't damage them;
          }
          boolean togglePvp = false;
          if (ent instanceof EntityPlayer && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) {
            if (Config.killerPvPoffDisablesSwing) {
              continue;
            } else if (Config.killerPvPoffIsIgnored) {
              togglePvp = true;
            }
          }
          if (Config.killerJoeMustSee && !canJoeSee(ent)) {
            continue;
          }
          if (ent instanceof EntityZombie) {
            zCache.cache.add((EntityZombie) ent);
          }                      
          try {
            if (togglePvp) {
              FMLCommonHandler.instance().getMinecraftServerInstance().setAllowPvp(true);
            }
            atackera.attackTargetEntityWithCurrentItem(ent);
          } finally {
            if (togglePvp) {
              FMLCommonHandler.instance().getMinecraftServerInstance().setAllowPvp(false);
            }
          }
          atackera.resetCooldown();
          useNutrient();
          swingWeapon();
          if (getStackInSlot(0) == null || getStackInSlot(0).stackSize <= 0 || atackera.getHeldItemMainhand() == null) {
            setInventorySlotContents(0, null);
          }
          return false;
        }
      }
    }
    return false;
  }

  int getActivationAmount() {
    return (int) (tank.getCapacity() * 0.7f);
  }

  private boolean canJoeSee(EntityLivingBase ent) {
    Vec3d entPos = new Vec3d(ent.posX, ent.posY + ent.getEyeHeight(), ent.posZ);
    for (EnumFacing facing1 : frontFaceAndSides) {
      if (this.worldObj.rayTraceBlocks(new Vec3d(getPos().getX() + faceMidPoints[facing1.ordinal()][0], getPos().getY() + faceMidPoints[facing1.ordinal()][1],
          getPos().getZ() + faceMidPoints[facing1.ordinal()][2]), entPos) == null)
        return true;
    }
    return false;
  }

  @Override
  public void setFacing(EnumFacing facing) {
    super.setFacing(facing);
    frontFaceAndSides = new EnumFacing[] { facing, facing.rotateY(), facing.rotateYCCW() };
  }

  private static final double[][] faceMidPoints = new double[][] { { 0.5D, 0.0D, 0.5D }, { 0.5D, 1.0D, 0.5D }, { 0.5D, 0.5D, 0.0D }, { 0.5D, 0.5D, 1.0D },
      { 0.0D, 0.5D, 0.5D }, { 1.0D, 0.5D, 0.5D } };

  // ------------------------------- XP

  public ExperienceContainer getXpContainer() {
    return xpCon;
  }

  private void hooverXP() {

    double maxDist = Config.killerJoeHooverXpLength;

    List<EntityXPOrb> xp = worldObj.getEntitiesWithinAABB(EntityXPOrb.class, getHooverBounds(), this);

    for (EntityXPOrb entity : xp) {
      double xDist = (getPos().getX() + 0.5D - entity.posX);
      double yDist = (getPos().getY() + 0.5D - entity.posY);
      double zDist = (getPos().getZ() + 0.5D - entity.posZ);

      double totalDistance = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);

      if (totalDistance < 1.5) {
        hooverXP(entity);
      } else {
        double d = 1 - (Math.max(0.1, totalDistance) / maxDist);
        double speed = 0.01 + (d * 0.02);

        entity.motionX += xDist / totalDistance * speed;
        entity.motionZ += zDist / totalDistance * speed;
        entity.motionY += yDist / totalDistance * speed;
        if (yDist > 0.5) {
          entity.motionY = 0.12;
        }

      }
    }
  }

  private void hooverXP(EntityXPOrb entity) {
    if (!worldObj.isRemote) {
      if (!entity.isDead) {
        int xpValue = entity.getXpValue();
        if (Config.killerMending && inventory[0] != null && inventory[0].isItemDamaged()
            && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, inventory[0]) > 0) {
          int i = Math.min(xpToDurability(xpValue), inventory[0].getItemDamage());
          xpValue -= durabilityToXp(i);
          inventory[0].setItemDamage(inventory[0].getItemDamage() - i);
          markDirty();
        }
        if (xpValue > 0) {
          xpCon.addExperience(xpValue);
        }
        entity.setDead();
      }
    }
  }
  
  private int durabilityToXp(int durability) {
    return durability / 2;
  }

  private int xpToDurability(int xp) {
    return xp * 2;
  }

  @Override
  public boolean apply(@Nullable EntityXPOrb input) {
    return input != null && !input.isDead;
  }

  // ------------------------------- Weapon stuffs

  void swingWeapon() {
    if (getStackInSlot(0) == null) {
      return;
    }
    if (!isSwingInProgress || swingProgressInt >= getArmSwingAnimationEnd() / 2 || swingProgressInt < 0) {
      swingProgressInt = -1;
      isSwingInProgress = true;
      if (worldObj instanceof WorldServer) {
        PacketHandler.sendToAllAround(new PacketSwing(this), this);
      }
    }
  }

  float getSwingProgress(float p_70678_1_) {
    float f1 = swingProgress - prevSwingProgress;

    if (f1 < 0.0F) {
      ++f1;
    }

    return prevSwingProgress + f1 * p_70678_1_;
  }

  private void updateArmSwingProgress() {

    prevSwingProgress = swingProgress;

    int i = getArmSwingAnimationEnd();
    if (isSwingInProgress) {
      ++swingProgressInt;
      if (swingProgressInt >= i) {
        swingProgressInt = 0;
        isSwingInProgress = false;
      }
    } else {
      swingProgressInt = 0;
    }
    swingProgress = (float) swingProgressInt / (float) i;
  }

  private int getArmSwingAnimationEnd() {
    return 6;
  }

  Attackera getAttackera() {
    if (attackera == null) {
      attackera = new Attackera();
    }
    return attackera;
  }

  WirelessChargedLocation getChargedLocation() {
    if (chargedLocation == null) {
      chargedLocation = new WirelessChargedLocation(this);
    }
    return chargedLocation;
  }

  private BoundingBox getKillBounds() {
    if (killBounds == null) {
      BoundingBox bb = new BoundingBox(getLocation());
      Vector3d min = bb.getMin();
      Vector3d max = bb.getMax();
      max.y += Config.killerJoeAttackHeight;
      min.y -= Config.killerJoeAttackHeight;

      EnumFacing facingDir = facing;
      if (ForgeDirectionOffsets.isPositiveOffset(facingDir)) {
        max.add(ForgeDirectionOffsets.offsetScaled(facingDir, Config.killerJoeAttackLength));
        min.add(ForgeDirectionOffsets.forDir(facingDir));
      } else {
        min.add(ForgeDirectionOffsets.offsetScaled(facingDir, Config.killerJoeAttackLength));
        max.add(ForgeDirectionOffsets.forDir(facingDir));

      }
      if (facingDir.getFrontOffsetX() == 0) {
        min.x -= Config.killerJoeAttackWidth;
        max.x += Config.killerJoeAttackWidth;
      } else {
        min.z -= Config.killerJoeAttackWidth;
        max.z += Config.killerJoeAttackWidth;
      }
      killBounds = new BoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    return killBounds;
  }

  private AxisAlignedBB getHooverBounds() {
    if (hooverBounds == null) {
      BoundingBox bb = new BoundingBox(getLocation());
      Vector3d min = bb.getMin();
      Vector3d max = bb.getMax();
      max.y += Config.killerJoeAttackHeight;
      min.y -= Config.killerJoeAttackHeight;

      EnumFacing facingDir = facing;
      if (ForgeDirectionOffsets.isPositiveOffset(facingDir)) {
        max.add(ForgeDirectionOffsets.offsetScaled(facingDir, Config.killerJoeHooverXpLength));
        min.add(ForgeDirectionOffsets.forDir(facingDir));
      } else {
        min.add(ForgeDirectionOffsets.offsetScaled(facingDir, Config.killerJoeHooverXpLength));
        max.add(ForgeDirectionOffsets.forDir(facingDir));

      }
      if (facingDir.getFrontOffsetX() == 0) {
        min.x -= Config.killerJoeHooverXpWidth * 2;
        max.x += Config.killerJoeHooverXpWidth * 2;
      } else {
        min.z -= Config.killerJoeHooverXpWidth * 2;
        max.z += Config.killerJoeHooverXpWidth * 2;
      }
      hooverBounds = new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    return hooverBounds;
  }

  // ------------------------------- Fluid Stuff

  private void useNutrient() {
    tank.removeFluidAmount(Config.killerJoeNutrientUsePerAttackMb);
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    boolean res = super.doPull(dir);
    if (dir != null && tank.getFluidAmount() < tank.getCapacity()) {
      if (FluidWrapper.transfer(worldObj, getPos().offset(dir), dir.getOpposite(), tank, IO_MB_TICK) > 0) {
        setTanksDirty();
      }
    }
    return res;
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {
    boolean res = super.doPush(dir);
    if (dir != null && xpCon.getFluidAmount() > 0) {
      if (FluidWrapper.transfer(xpCon, worldObj, getPos().offset(dir), dir.getOpposite(), IO_MB_TICK) > 0) {
        setTanksDirty();
      }
    }
    return res;
  }

  private static final UUID uuid = UUID.fromString("3baa66fa-a69a-11e4-89d3-123b93f75cba");
  private static final GameProfile DUMMY_PROFILE = new GameProfile(uuid, "[EioKillera]");

  private class Attackera extends FakePlayerEIO {

    ItemStack prevWeapon;

    public Attackera() {
      super(getWorld(), getLocation(), DUMMY_PROFILE);
    }

    @Override
    public void onUpdate() {

      setHeldItem(EnumHand.MAIN_HAND, getStackInSlot(0));      

      ItemStack prev = prevWeapon;
      ItemStack cur = getHeldItemMainhand();
      if (!ItemStack.areItemStacksEqual(cur, prev)) {
        if (prev != null) {
          getAttributeMap().removeAttributeModifiers(prev.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        }

        if (cur != null) {
          getAttributeMap().applyAttributeModifiers(cur.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        }
        prevWeapon = cur == null ? null : cur.copy();
      }

      getChargedLocation().chargeItems(inventory.mainInventory);
      ticksSinceLastSwing++;
      
    }
    
    public int getTicksSinceLastSwing() {
      return ticksSinceLastSwing;
    }
       
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null && forFluidType.getFluid() == Fluids.fluidNutrientDistillation) {
      return tank;
    }   
    return null;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { xpCon };
  }

  @Override
  public void setTanksDirty() {
    tanksDirty = true;
  }

  @Override
  public SmartTank getNutrientTank() {
    return tank;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    return super.equals(obj);
  }

  @Override
  public boolean shouldRenderInPass(int pass) {
    return true;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  private boolean showingRange = false;
  private final static Vector4f color = new Vector4f(.94f, .11f, .11f, .4f);

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TileKillerJoe>(this, color));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  @Override
  public BoundingBox getBounds() {
    return getKillBounds();
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public List<ITankData> getTankDisplayData() {
    return Collections.<ITankData> singletonList(new ITankData() {

      @Override
      @Nonnull
      public EnumTankType getTankType() {
        return EnumTankType.INPUT;
      }

      @Override
      @Nullable
      public FluidStack getContent() {
        return tank.getFluid();
      }

      @Override
      public int getCapacity() {
        return tank.getCapacity();
      }
    });
  }

  private SmartTankFluidHandler smartTankFluidHandler;

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      if (smartTankFluidHandler == null) {
        smartTankFluidHandler = new SmartTankFluidMachineHandler(this, tank, xpCon);
      }
      return (T) smartTankFluidHandler.get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
