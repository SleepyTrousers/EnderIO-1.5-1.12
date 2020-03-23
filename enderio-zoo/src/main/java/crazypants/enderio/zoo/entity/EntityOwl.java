package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.ai.EntityAIFlyingAttackOnCollide;
import crazypants.enderio.zoo.entity.ai.EntityAIFlyingFindPerch;
import crazypants.enderio.zoo.entity.ai.EntityAIFlyingLand;
import crazypants.enderio.zoo.entity.ai.EntityAIFlyingPanic;
import crazypants.enderio.zoo.entity.ai.EntityAIFlyingShortWander;
import crazypants.enderio.zoo.entity.ai.EntityAINearestAttackableTargetBounded;
import crazypants.enderio.zoo.entity.navigate.FlyingMoveHelper;
import crazypants.enderio.zoo.entity.navigate.FlyingPathNavigate;
import crazypants.enderio.zoo.entity.render.RenderOwl;
import crazypants.enderio.zoo.sound.SoundRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityOwl extends EntityAnimal implements IEnderZooEntity.Flying {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    IEnderZooEntity.register(event, NAME, EntityOwl.class, EGG_BG_COL, EGG_FG_COL, MobID.OWL);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityOwl.class, RenderOwl.FACTORY);
  }

  public static final @Nonnull String NAME = "owl";
  public static final int EGG_BG_COL = 0xC17949;
  public static final int EGG_FG_COL = 0xFFDDC6;

  private float wingRotation;
  private float prevWingRotation;
  private float wingRotDelta = 1.0F;

  private float destPos;
  private float prevDestPos;

  private float bodyAngle = 5;
  private float targetBodyAngle = 0;
  private float wingAngle;

  private double groundSpeedRatio = 0.25;
  private float climbRate = 0.25f;
  private float turnRate = 30;

  public int timeUntilNextEgg;

  public EntityOwl(World worldIn) {
    super(worldIn);
    setSize(0.4F, 0.85F);
    stepHeight = 1.0F;

    int pri = 0;
    tasks.addTask(++pri, new EntityAIFlyingPanic(this, 2));
    tasks.addTask(++pri, new EntityAIFlyingAttackOnCollide(this, 2.5, false));
    tasks.addTask(++pri, new EntityAIMate(this, 1.0));
    tasks.addTask(++pri, new EntityAITempt(this, 1.0D, Items.SPIDER_EYE, false));
    tasks.addTask(++pri, new EntityAIFollowParent(this, 1.5));
    tasks.addTask(++pri, new EntityAIFlyingLand(this, 2));
    tasks.addTask(++pri, new EntityAIFlyingFindPerch(this, 2, 80));
    tasks.addTask(++pri, new EntityAIFlyingShortWander(this, 2, 150));

    tasks.addTask(++pri, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    tasks.addTask(++pri, new EntityAILookIdle(this));

    EntityAINearestAttackableTargetBounded<EntitySpider> targetSpiders = new EntityAINearestAttackableTargetBounded<EntitySpider>(this, EntitySpider.class,
        true, true);
    targetSpiders.setMaxDistanceToTarget(ZooConfig.owlAggressionRange);
    targetSpiders.setMaxVerticalDistanceToTarget(ZooConfig.owlAggressionRangeVertical);
    targetTasks.addTask(0, targetSpiders);

    moveHelper = new FlyingMoveHelper(this);

    timeUntilNextEgg = getNextLayingTime();
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    applyAttributes(this, ZooConfig.owlHealth, ZooConfig.OwlDamage);
  }

  @Override
  protected @Nonnull PathNavigate createNavigator(@Nonnull World worldIn) {
    return new FlyingPathNavigate(this, worldIn);
  }

  @Override
  public FlyingPathNavigate getFlyingNavigator() {
    return (FlyingPathNavigate) getNavigator();
  }

  @Override
  public float getBlockPathWeight(@Nonnull BlockPos pos) {
    IBlockState bs = world.getBlockState(pos.down());
    return bs.getMaterial() == Material.LEAVES ? 10.0F : 0;
  }

  @Override
  public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
    super.attackEntityAsMob(entityIn);
    float attackDamage = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    if (entityIn instanceof EntitySpider) {
      attackDamage *= ZooConfig.owlSpiderDamageMultiplier.get();
    }
    return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
  }

  @Override
  public void onLivingUpdate() {

    // setDead();
    super.onLivingUpdate();
    prevWingRotation = wingRotation;
    prevDestPos = destPos;
    destPos = (float) (destPos + (onGround ? -1 : 4) * 0.3D);
    destPos = MathHelper.clamp(destPos, 0.0F, 1.0F);
    if (!onGround && wingRotDelta < 1.0F) {
      wingRotDelta = 1.0F;
    }
    wingRotDelta = (float) (wingRotDelta * 0.9D);
    float flapSpeed = 2f;
    float yDelta = (float) Math.abs(posY - prevPosY);
    if (yDelta != 0) {
      // normalise between 0 and 0.02
      flapSpeed *= MathHelper.clamp(yDelta / 0.02f, 0.75f, 1f);
    }
    wingRotation += wingRotDelta * flapSpeed;

    if (!world.isRemote && !isChild() && --timeUntilNextEgg <= 0) {
      if (isOnLeaves()) {
        playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        dropItem(ModObject.item_owl_egg.getItemNN(), 1);
      }
      timeUntilNextEgg = getNextLayingTime();
    }

    if (onGround) {
      motionX *= groundSpeedRatio;
      motionZ *= groundSpeedRatio;
    }
  }

  private boolean isOnLeaves() {
    IBlockState bs = world.getBlockState(getPosition().down());
    return bs.getMaterial() == Material.LEAVES;
  }

  private void calculateWingAngle(float partialTicks) {
    float flapComletion = prevWingRotation + (wingRotation - prevWingRotation) * partialTicks;
    float onGroundTimerThing = prevDestPos + (destPos - prevDestPos) * partialTicks;
    wingAngle = (MathHelper.sin(flapComletion) + 1.0F) * onGroundTimerThing;

    if (onGround) {
      wingAngle = (float) Math.toRadians(3);
    }
  }

  private void calculateBodyAngle(float partialTicks) {

    if (onGround) {
      bodyAngle = 7;
      targetBodyAngle = 7;
      return;
    }

    // ignore y as we want no tilt going straight up or down
    Vec3d motionVec = new Vec3d(motionX, 0, motionZ);
    double speed = motionVec.lengthVector();
    // normalise between 0 - 0.1
    speed = Math.min(1, speed * 10);
    targetBodyAngle = 20 + ((float) speed * 30);

    if (targetBodyAngle == bodyAngle) {
      return;
    }
    if (targetBodyAngle > bodyAngle) {
      bodyAngle += (2 * partialTicks);
      if (bodyAngle > targetBodyAngle) {
        bodyAngle = targetBodyAngle;
      }
    } else {
      bodyAngle -= (1 * partialTicks);
      if (bodyAngle < targetBodyAngle) {
        bodyAngle = targetBodyAngle;
      }
    }
  }

  public void calculateAngles(float partialTicks) {
    calculateBodyAngle(partialTicks);
    calculateWingAngle(partialTicks);
  }

  public float getBodyAngle() {
    return (float) Math.toRadians(bodyAngle);
  }

  public float getWingAngle() {
    return wingAngle;
  }

  @Override
  public float getEyeHeight() {
    return height - 0.1F;
  }

  @Override
  protected void updateFallState(double y, boolean onGroundIn, @Nonnull IBlockState blockIn, @Nonnull BlockPos pos) {
  }

  @Override
  public int getTalkInterval() {
    return ZooConfig.owlHootInterval.get();
  }

  @Override
  public void playLivingSound() {
    if (world.isDaytime() || getAttackTarget() != null) {
      return;
    }

    playSound(getAmbientSound(), getSoundVolume() * ZooConfig.owlHootVolumeMultiplier.get(), 0.8f * getSoundPitch());
  }

  @Override
  protected @Nonnull SoundEvent getAmbientSound() {
    if (world.rand.nextBoolean()) {
      return SoundRegistry.OWL_HOOT2.getSoundEvent();
    } else {
      return SoundRegistry.OWL_HOOT.getSoundEvent();
    }
  }

  @Override
  protected SoundEvent getHurtSound(@Nonnull DamageSource source) {
    return SoundRegistry.OWL_HURT.getSoundEvent();
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundRegistry.OWL_HURT.getSoundEvent();
  }

  @Override
  public EntityOwl createChild(@Nonnull EntityAgeable ageable) {
    return new EntityOwl(world);
  }

  @Override
  public boolean isBreedingItem(@Nonnull ItemStack stack) {
    return stack.getItem() == Items.SPIDER_EYE;
  }

  @Override
  protected void playStepSound(@Nonnull BlockPos pos, @Nonnull Block blockIn) {
    playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
  }

  @Override
  @Nullable
  protected ResourceLocation getLootTable() {
    return new ResourceLocation(EnderIOZoo.DOMAIN, NAME);
  }

  @Override
  protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
    EntityLootHelper.dropLoot(this, getLootTable(), source);
    dropEquipment(wasRecentlyHit, lootingModifier);
  }

  @Override
  public float getMaxTurnRate() {
    return turnRate;
  }

  @Override
  public float getMaxClimbRate() {
    return climbRate;
  }

  @Override
  public EntityCreature asEntityCreature() {
    return this;
  }

  private int getNextLayingTime() {
    int dif = ZooConfig.owlTimeBetweenEggsMax.get() - ZooConfig.owlTimeBetweenEggsMin.get();
    return ZooConfig.owlTimeBetweenEggsMin.get() + rand.nextInt(dif);
  }

  @Override
  public void readEntityFromNBT(@Nonnull NBTTagCompound tagCompund) {
    super.readEntityFromNBT(tagCompund);
    if (tagCompund.hasKey("EggLayTime")) {
      this.timeUntilNextEgg = tagCompund.getInteger("EggLayTime");
    }
  }

  @Override
  public void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {
    super.writeEntityToNBT(tagCompound);
    tagCompound.setInteger("EggLayTime", this.timeUntilNextEgg);
  }

  @Override
  public boolean canBeLeashedTo(@Nonnull EntityPlayer player) {
    return !this.getLeashed();
  }
}
