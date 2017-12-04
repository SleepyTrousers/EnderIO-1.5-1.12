package crazypants.enderio.machines.machine.killera;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.fluid.SmartTankFluidHandler;
import com.enderio.core.common.transform.EnderCoreMethods.ICreeperTarget;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.util.MagnetUtil;
import com.enderio.core.common.util.UserIdent;
import com.enderio.core.common.util.stackable.Things;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.fluid.SmartTankFluidMachineHandler;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.config.config.KillerJoeConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.generator.zombie.IHasNutrientTank;
import crazypants.enderio.machines.machine.generator.zombie.PacketNutrientTank;
import crazypants.enderio.machines.machine.wireless.WirelessChargedLocation;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.TargetContext;

import static crazypants.enderio.base.config.Config.killerProvokesCreeperExpolosions;

@Storable
public class TileKillerJoe extends AbstractInventoryMachineEntity implements ITankAccess.IExtendedTankAccess, IHasNutrientTank, IRanged {

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

  private EnumFacing[] frontFaceAndSides;

  protected final static @Nonnull BoundingBox NULL_AABB = new BoundingBox(new BlockPos(0, 0, 0));

  protected @Nonnull AxisAlignedBB hooverBounds = NULL_AABB;

  protected @Nonnull BoundingBox killBounds = NULL_AABB;

  protected Attackera attackera;

  protected WirelessChargedLocation chargedLocation;

  @Store
  final SmartTank tank = new SmartTank(Fluids.NUTRIENT_DISTILLATION.getFluid(), Fluid.BUCKET_VOLUME * 2);

  int lastFluidLevelUpdate;

  private boolean tanksDirty;

  private boolean isSwingInProgress;

  private int swingProgressInt;

  private float swingProgress;

  private float prevSwingProgress;

  private boolean hadSword;

  private boolean isMending = false;

  public TileKillerJoe() {
    super(new SlotDefinition(1, 0, 0));

    if (zCache == null) {
      zCache = new ZombieCache();
      MinecraftForge.EVENT_BUS.register(zCache);
    }
    tank.setTileEntity(this);
    tank.setCanDrain(false);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_killer_joe.getUnlocalisedName();
  }

  // These weapons are tested to work and render correctly in the Killer Joe. That's why this is not in the config file.
  public static final Things WEAPONS = new Things("tconstruct:hatchet", "tconstruct:mattock", "tconstruct:hammer", "tconstruct:lumberaxe", "tconstruct:scythe",
      "tconstruct:broadsword", "tconstruct:longsword", "tconstruct:rapier", "tconstruct:frypan", "tconstruct:cleaver", "minecraft:stick")
          // for the ghost slot:
          .add(Items.WOODEN_SWORD).add(Items.STONE_SWORD).add(Items.IRON_SWORD).add(Items.GOLDEN_SWORD).add(Items.DIAMOND_SWORD)
          .add(ModObject.itemDarkSteelSword.getItem()).add(Items.WOODEN_AXE).add(Items.IRON_AXE).add(Items.GOLDEN_AXE).add(Items.DIAMOND_AXE)
          .add(ModObject.itemDarkSteelAxe.getItem());

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    if (itemstack.isEmpty()) {
      return false;
    }
    return itemstack.getItem() instanceof ItemSword || itemstack.getItem() instanceof ItemAxe || WEAPONS.contains(itemstack);
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public void doUpdate() {
    updateArmSwingProgress();
    if (!world.isRemote) {
      if (doMending()) {
        hooverXP();
        if (!needsMending()) {
          endMending();
        }
      }
      getAttackera().onUpdate();
      if (inventory[0] != null != hadSword) {
        updateBlock();
        hadSword = inventory[0] != null;
      }
    }
    super.doUpdate();
  }

  private void endMending() {
    isMending = false;
  }

  @Override
  public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, @Nonnull EnumFacing side) {
    if (isSideDisabled(side)) {
      return false;
    }
    if (inventory[slot] == null || inventory[slot].getCount() < itemstack.getCount()) {
      return false;
    }
    return itemstack.getItem() == inventory[slot].getItem();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {

    // send any maintaince packets no more than twice a second
    if (shouldDoWorkThisTick(10)) {
      if (tanksDirty) {
        PacketHandler.sendToAllAround(new PacketNutrientTank(this), this);
        tanksDirty = false;
      }
    }

    if (!redstoneCheck) {
      return false;
    }

    if (tank.getFluidAmount() < getActivationAmount()) {
      return false;
    }

    if (getStackInSlot(0).isEmpty()) {
      return false;
    }

    Attackera atackera = getAttackera();
    atackera.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getStackInSlot(0));
    if (atackera.getTicksSinceLastSwing() < atackera.getCooldownPeriod()) {
      return false;
    }

    List<EntityLivingBase> entsInBounds = world.getEntitiesWithinAABB(EntityLivingBase.class, getKillBounds());
    if (!entsInBounds.isEmpty()) {

      for (EntityLivingBase ent : entsInBounds) {
        if (!ent.isDead && ent.deathTime <= 0 && !ent.isEntityInvulnerable(DamageSource.GENERIC) && ent.hurtResistantTime == 0) {
          if (ent instanceof EntityPlayer && ((EntityPlayer) ent).capabilities.disableDamage) {
            continue; // Ignore players in creative, can't damage them;
          }
          boolean togglePvp = false;
          if (ent instanceof EntityPlayer && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled()) {
            if (KillerJoeConfig.killerPvPoffDisablesSwing.get()) {
              continue;
            } else if (KillerJoeConfig.killerPvPoffIsIgnored.get()) {
              togglePvp = true;
            }
          }
          if (KillerJoeConfig.killerJoeMustSee.get() && !canJoeSee(ent)) {
            continue;
          }
          if (!PermissionAPI.hasPermission(getOwner().getAsGameProfile(), BlockKillerJoe.permissionAttacking, new TargetContext(atackera, ent))) {
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
          if (getStackInSlot(0).isEmpty() || getStackInSlot(0).getCount() <= 0 || atackera.getHeldItemMainhand().isEmpty()) {
            setInventorySlotContents(0, ItemStack.EMPTY);
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
      if (this.world.rayTraceBlocks(new Vec3d(getPos().getX() + faceMidPoints[facing1.ordinal()][0], getPos().getY() + faceMidPoints[facing1.ordinal()][1],
          getPos().getZ() + faceMidPoints[facing1.ordinal()][2]), entPos) == null)
        return true;
    }
    return false;
  }

  @Override
  public void setFacing(@Nonnull EnumFacing facing) {
    super.setFacing(facing);
    frontFaceAndSides = new EnumFacing[] { facing, facing.rotateY(), facing.rotateYCCW() };
  }

  private static final double[][] faceMidPoints = new double[][] { { 0.5D, 0.0D, 0.5D }, { 0.5D, 1.0D, 0.5D }, { 0.5D, 0.5D, 0.0D }, { 0.5D, 0.5D, 1.0D },
      { 0.0D, 0.5D, 0.5D }, { 1.0D, 0.5D, 0.5D } };

  // ------------------------------- XP

  private void hooverXP() {

    double maxDist = Math.max(KillerJoeConfig.killerJoeHooverXpHeight.get(), Math.max(KillerJoeConfig.killerJoeHooverXpLength.get(), KillerJoeConfig.killerJoeHooverXpWidth.get()));

    List<EntityXPOrb> xp = world.getEntitiesWithinAABB(EntityXPOrb.class, getHooverBounds(), null);

    for (EntityXPOrb entity : xp) {
      double xDist = (getPos().getX() + 0.5D - entity.posX);
      double yDist = (getPos().getY() + 0.5D - entity.posY);
      double zDist = (getPos().getZ() + 0.5D - entity.posZ);

      double totalDistance = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);

      if (totalDistance < 1.5) {
        hooverXP(entity);
        if (!needsMending()) {
          return;
        }
      } else if (MagnetUtil.shouldAttract(getPos(), entity)) {
        double d = 1 - (Math.max(0.1, totalDistance) / maxDist);
        double speed = 0.01 + (d * 0.02);

        entity.motionX += xDist / totalDistance * speed;
        entity.motionZ += zDist / totalDistance * speed;
        entity.motionY += yDist / totalDistance * speed;
        if (yDist > 0.5) {
          entity.motionY = 0.12;
        }

        // force client sync because this movement is server-side only
        boolean silent = entity.isSilent();
        entity.setSilent(!silent);
        entity.setSilent(silent);
      }
    }
  }

  private void hooverXP(EntityXPOrb entity) {
    if (!world.isRemote && !entity.isDead && needsMending()) {
      int xpValue = entity.getXpValue();
      int i = Math.min(xpToDurability(xpValue), inventory[0].getItemDamage());
      xpValue -= durabilityToXp(i);
      inventory[0].setItemDamage(inventory[0].getItemDamage() - i);
      markDirty();
      if (xpValue > 0) {
        entity.xpValue = xpValue;
        MagnetUtil.release(entity);
      } else {
        entity.setDead();
      }
    }
  }

  private boolean doMending() {
    if (!needsMending()) {
      endMending();
      return false;
    } else if (isMending) {
      return true;
    } else {
      return isMending = inventory[0].getItem().getDurabilityForDisplay(inventory[0]) > .1;
    }
  }

  private boolean needsMending() {
    return KillerJoeConfig.killerMendingEnabled.get() && inventory[0] != null && inventory[0].isItemDamaged()
        && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, inventory[0]) > 0;
  }

  private int durabilityToXp(int durability) {
    return durability / 2;
  }

  private int xpToDurability(int xp) {
    return xp * 2;
  }

  // ------------------------------- Weapon stuffs

  void swingWeapon() {
    if (getStackInSlot(0).isEmpty()) {
      return;
    }
    if (!isSwingInProgress || swingProgressInt >= getArmSwingAnimationEnd() / 2 || swingProgressInt < 0) {
      swingProgressInt = -1;
      isSwingInProgress = true;
      if (world instanceof WorldServer) {
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
      attackera = new Attackera(getOwner());
    }
    return attackera;
  }

  WirelessChargedLocation getChargedLocation() {
    if (chargedLocation == null) {
      chargedLocation = new WirelessChargedLocation(this);
    }
    return chargedLocation;
  }

  private @Nonnull BoundingBox getKillBounds() {
    if (killBounds == NULL_AABB) {
      double killerJoeAttackHeight = KillerJoeConfig.killerJoeAttackHeight.get();
      double killerJoeAttackLength = KillerJoeConfig.killerJoeAttackLength.get();
      double killerJoeAttackWidth = KillerJoeConfig.killerJoeAttackWidth.get();

      BoundingBox bb = new BoundingBox(getLocation());
      Vector3d min = bb.getMin();
      Vector3d max = bb.getMax();
      max.y += killerJoeAttackHeight;
      min.y -= killerJoeAttackHeight;

      EnumFacing facingDir = facing;
      if (ForgeDirectionOffsets.isPositiveOffset(facingDir)) {
        max.add(ForgeDirectionOffsets.offsetScaled(facingDir, killerJoeAttackLength));
        min.add(ForgeDirectionOffsets.forDir(facingDir));
      } else {
        min.add(ForgeDirectionOffsets.offsetScaled(facingDir, killerJoeAttackLength));
        max.add(ForgeDirectionOffsets.forDir(facingDir));

      }
      if (facingDir.getFrontOffsetX() == 0) {
        min.x -= killerJoeAttackWidth;
        max.x += killerJoeAttackWidth;
      } else {
        min.z -= killerJoeAttackWidth;
        max.z += killerJoeAttackWidth;
      }
      killBounds = new BoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    return killBounds;
  }

  private @Nonnull AxisAlignedBB getHooverBounds() {
    if (hooverBounds == NULL_AABB) {
      double killerJoeHooverXpHeight = KillerJoeConfig.killerJoeHooverXpHeight.get();
      double killerJoeHooverXpLength = KillerJoeConfig.killerJoeHooverXpLength.get();
      double killerJoeHooverXpWidth = KillerJoeConfig.killerJoeHooverXpWidth.get();

      BoundingBox bb = new BoundingBox(getLocation());
      Vector3d min = bb.getMin();
      Vector3d max = bb.getMax();
      max.y += killerJoeHooverXpHeight;
      min.y -= killerJoeHooverXpHeight;

      EnumFacing facingDir = facing;
      if (ForgeDirectionOffsets.isPositiveOffset(facingDir)) {
        max.add(ForgeDirectionOffsets.offsetScaled(facingDir, killerJoeHooverXpLength));
        min.add(ForgeDirectionOffsets.forDir(facingDir));
      } else {
        min.add(ForgeDirectionOffsets.offsetScaled(facingDir, killerJoeHooverXpLength));
        max.add(ForgeDirectionOffsets.forDir(facingDir));

      }
      if (facingDir.getFrontOffsetX() == 0) {
        min.x -= killerJoeHooverXpWidth * 2;
        max.x += killerJoeHooverXpWidth * 2;
      } else {
        min.z -= killerJoeHooverXpWidth * 2;
        max.z += killerJoeHooverXpWidth * 2;
      }
      hooverBounds = new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    return hooverBounds;
  }

  // ------------------------------- Fluid Stuff

  private void useNutrient() {
    tank.removeFluidAmount(KillerJoeConfig.killerJoeNutrientUsePerAttackMb.get());
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    boolean res = super.doPull(dir);
    if (dir != null && tank.getFluidAmount() < tank.getCapacity()) {
      if (FluidWrapper.transfer(world, getPos().offset(dir), dir.getOpposite(), tank, IO_MB_TICK) > 0) {
        setTanksDirty();
      }
    }
    return res;
  }

  private static final UUID uuid = UUID.fromString("3baa66fa-a69a-11e4-89d3-123b93f75cba");
  private static final GameProfile DUMMY_PROFILE = new GameProfile(uuid, "[Killer Joe]");

  private class Attackera extends FakePlayerEIO implements ICreeperTarget {

    @Nonnull
    ItemStack prevWeapon = ItemStack.EMPTY;

    public Attackera(UserIdent owner) {
      super(getWorld(), getLocation(), (owner == null || owner == UserIdent.NOBODY || StringUtils.isBlank(owner.getPlayerName())) ? DUMMY_PROFILE
          : new GameProfile(uuid, "[" + owner.getPlayerName() + "'s Killer Joe]"));
      setOwner(owner);
    }

    @Override
    public void onUpdate() {

      setHeldItem(EnumHand.MAIN_HAND, getStackInSlot(0));

      ItemStack prev = prevWeapon;
      ItemStack cur = getHeldItemMainhand();
      if (!ItemStack.areItemStacksEqual(cur, prev)) {
        if (!prev.isEmpty()) {
          getAttributeMap().removeAttributeModifiers(prev.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        }

        if (!cur.isEmpty()) {
          getAttributeMap().applyAttributeModifiers(cur.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        }
        prevWeapon = cur.isEmpty() ? ItemStack.EMPTY : cur.copy();
      }

      if (getChargedLocation().chargeItems(inventory.mainInventory)) {
        markDirty();
      }
      ticksSinceLastSwing++;

    }

    public int getTicksSinceLastSwing() {
      return ticksSinceLastSwing;
    }

    @Override
    public boolean isCreeperTarget(@Nonnull EntityCreeper swellingCreeper) {
      return killerProvokesCreeperExpolosions;
    }

  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null && forFluidType.getFluid() == Fluids.NUTRIENT_DISTILLATION.getFluid()) {
      return tank;
    }
    return null;
  }

  @Override
  public @Nonnull FluidTank[] getOutputTanks() {
    return new FluidTank[] {};
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
  public boolean shouldRenderInPass(int pass) {
    return true;
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

  protected SmartTankFluidHandler getSmartTankFluidHandler() {
    if (smartTankFluidHandler == null) {
      smartTankFluidHandler = new SmartTankFluidMachineHandler(this, tank);
    }
    return smartTankFluidHandler;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) getSmartTankFluidHandler().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
