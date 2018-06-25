package crazypants.enderio.zoo.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.ai.EntityAIAttackOnCollideAggressive;
import crazypants.enderio.zoo.entity.ai.EntityAINearestAttackableTargetBounded;
import crazypants.enderio.zoo.entity.render.RenderDirewolf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityDireWolf extends EntityMob implements IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    IEnderZooMob.register(event, NAME, EntityDireWolf.class, EGG_BG_COL, EGG_FG_COL, MobID.DWOLF);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityDireWolf.class, RenderDirewolf.FACTORY);
  }

  public static final @Nonnull String NAME = "direwolf";
  public static final int EGG_BG_COL = 0x606060;
  public static final int EGG_FG_COL = 0xA0A0A0;

  private static final @Nonnull SoundEvent SND_HURT = new SoundEvent(new ResourceLocation("enderzoo:direwolf.hurt"));
  private static final @Nonnull SoundEvent SND_HOWL = new SoundEvent(new ResourceLocation("enderzoo:direwolf.howl"));
  private static final @Nonnull SoundEvent SND_GROWL = new SoundEvent(new ResourceLocation("enderzoo:direwolf.growl"));
  private static final @Nonnull SoundEvent SND_DEATH = new SoundEvent(new ResourceLocation("enderzoo:direwolf.death"));

  private static final @Nonnull DataParameter<Boolean> ANGRY_INDEX = EntityDataManager.<Boolean> createKey(EntityDireWolf.class, DataSerializers.BOOLEAN);

  private EntityLivingBase previsousAttackTarget;

  private static int packHowl = 0;
  private static long lastHowl = 0;

  public EntityDireWolf(World world) {
    super(world);
    setSize(0.8F, 1.2F);
    tasks.addTask(1, new EntityAISwimming(this));
    tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
    tasks.addTask(4, new EntityAIAttackOnCollideAggressive(this, 1.1D, true).setAttackFrequency(20));
    tasks.addTask(7, new EntityAIWanderAvoidWater(this, 0.5D));
    tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    tasks.addTask(9, new EntityAILookIdle(this));
    targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
    targetTasks.addTask(2,
        new EntityAINearestAttackableTargetBounded<EntityPlayer>(this, EntityPlayer.class, true).setMaxDistanceToTarget(ZooConfig.wolfAggressionRange));
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    dataManager.register(ANGRY_INDEX, false);
    updateAngry();
  }

  public boolean isAngry() {
    return dataManager.get(ANGRY_INDEX);
  }

  @Override
  public boolean getCanSpawnHere() {
    return world.canBlockSeeSky(BlockCoord.get(this)) && super.getCanSpawnHere();
  }

  @Override
  protected boolean isValidLightLevel() {
    return true;
  }

  @Override
  public int getMaxSpawnedInChunk() {
    return 6;
  }

  private void updateAngry() {
    dataManager.set(ANGRY_INDEX, getAttackTarget() != null);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
    applyAttributes(this, ZooConfig.wolfHealth, ZooConfig.wolfAttackDamage);
  }

  @Override
  protected void playStepSound(@Nonnull BlockPos bp, @Nonnull Block p_145780_4_) {
    playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
  }

  @Override
  protected SoundEvent getAmbientSound() {
    if (!world.isRemote) {
      // sounds are played client-side only anyway, so why waste cpu cycles selecting them?
      return null;
    }
    if (isAngry()) {
      return SND_GROWL;
    }
    if (EntityUtil.isPlayerWithinRange(this, 12)) {
      return SND_GROWL;
    }
    boolean howl = (packHowl > 0 || rand.nextFloat() <= ZooConfig.howlChance.get()) && world.getTotalWorldTime() > (lastHowl + 10);
    if (howl) {
      if (packHowl <= 0 && rand.nextFloat() <= ZooConfig.howlPackChance.get()) {
        packHowl = ZooConfig.howlPackSize.get();
      }
      lastHowl = world.getTotalWorldTime();
      packHowl = Math.max(packHowl - 1, 0);
      return SND_HOWL;
    } else {
      return SND_GROWL;
    }
  }

  @Override
  public void playSound(@Nonnull SoundEvent sound, float volume, float pitch) {
    if (SND_HOWL.equals(sound)) {
      volume *= ZooConfig.howlVolume.get();
      pitch *= 0.8f;
    }
    world.playSound(posX, posY, posZ, sound, SoundCategory.NEUTRAL, volume, pitch, true); // NOP on server
  }

  @Override
  protected @Nonnull SoundEvent getHurtSound(@Nonnull DamageSource source) {
    return SND_HURT;
  }

  @Override
  protected @Nonnull SoundEvent getDeathSound() {
    return SND_DEATH;
  }

  @Override
  public float getEyeHeight() {
    return height * 0.8F;
  }

  @Override
  protected float getSoundVolume() {
    return 0.4F;
  }

  @Override
  protected Item getDropItem() {
    return Item.getItemById(-1);
  }

  @Override
  @Nullable
  protected ResourceLocation getLootTable() {
    return null; // use getDropItem() instead
  }

  public float getTailRotation() {
    if (isAngry()) {
      return (float) Math.PI / 2;
    }
    return (float) Math.PI / 4;
  }

  @Override
  public void setPosition(double x, double y, double z) {
    posX = x;
    posY = y;
    posZ = z;
    // Correct misalignment of bounding box
    double hw = width / 2.0F;
    double hd = hw * 2.25;
    float f1 = height;

    setEntityBoundingBox(new AxisAlignedBB(x - hw, y, z - hd, x + hw, y + f1, z + hd));
  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    EntityLivingBase curTarget = getAttackTarget();
    if (curTarget != previsousAttackTarget) {
      if (curTarget != null) {
        doGroupAgro(curTarget);
      }
      previsousAttackTarget = getAttackTarget();
      updateAngry();
    }
  }

  private void doGroupAgro(EntityLivingBase curTarget) {
    if (ZooConfig.packAttackEnabled.get()) {
      int range = ZooConfig.packAttackRange.get();
      AxisAlignedBB bb = new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
      List<EntityDireWolf> pack = world.getEntitiesWithinAABB(EntityDireWolf.class, bb);
      if (!pack.isEmpty()) {
        for (EntityDireWolf wolf : pack) {
          if (wolf.getAttackTarget() == null) {
            EntityUtil.cancelCurrentTasks(wolf);
            wolf.setAttackTarget(curTarget);
          }
        }
      }
    }
  }

}
