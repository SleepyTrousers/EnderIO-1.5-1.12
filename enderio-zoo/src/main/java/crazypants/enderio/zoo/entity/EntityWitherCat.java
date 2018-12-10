package crazypants.enderio.zoo.entity;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.ai.EntityAIAttackOnCollideOwned;
import crazypants.enderio.zoo.entity.ai.EntityAIFollowOwner;
import crazypants.enderio.zoo.entity.render.RenderWitherCat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
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
public class EntityWitherCat extends EntityMob implements IOwnable<EntityWitherCat, EntityWitherWitch>, IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    IEnderZooMob.register(event, NAME, EntityWitherCat.class, EGG_BG_COL, EGG_FG_COL, MobID.WCAT);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    RenderingRegistry.registerEntityRenderingHandler(EntityWitherCat.class, RenderWitherCat.FACTORY);
  }

  public enum GrowthMode {
    NONE,
    GROW,
    SHRINK
  };

  public static final @Nonnull String NAME = "withercat";
  public static final int EGG_BG_COL = 0x303030;
  public static final int EGG_FG_COL = 0xFFFFFF;

  private static final float DEF_HEIGHT = 0.8F;
  private static final float DEF_WIDTH = 0.6F;

  private static final @Nonnull DataParameter<Float> SCALE_INDEX = EntityDataManager.<Float> createKey(EntityWitherCat.class, DataSerializers.FLOAT);
  private static final @Nonnull DataParameter<Integer> GROWTH_MODE_INDEX = EntityDataManager.<Integer> createKey(EntityWitherCat.class, DataSerializers.VARINT);

  private static final float ANGRY_SCALE = 2;
  private static final float SCALE_INC = 0.05f;

  private static final @Nonnull UUID ATTACK_BOOST_MOD_UID = UUID.fromString("B9662B59-9566-4402-BC1F-2ED2B276D846");
  private static final @Nonnull UUID HEALTH_BOOST_MOD_UID = UUID.fromString("B9662B29-9467-3302-1D1A-2ED2B276D846");

  private float lastScale = 1f;
  private EntityWitherWitch owner;
  private final @Nonnull EntityAIFollowOwner followTask = new EntityAIFollowOwner(this, 2.5, 5, 1);

  private boolean attackTargetChanged = false;

  public EntityWitherCat(World world) {
    super(world);

    EntityAIFollowOwner retreatTask = new EntityAIFollowOwner(this, 2.5, 5, 2.5);

    tasks.addTask(1, new EntityAISwimming(this));
    tasks.addTask(2, new EntityAIAttackOnCollideOwned(this, EntityPlayer.class, 2.5, false, retreatTask));
    tasks.addTask(3, followTask);
    tasks.addTask(4, new EntityAIWander(this, 1.0D));
    tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    tasks.addTask(6, new EntityAILookIdle(this));
    tasks.addTask(7, new EntityAIAttackMelee(this, 1.0D, true));

    setSize(DEF_WIDTH, DEF_HEIGHT);
  }

  @Override
  protected void entityInit() {
    super.entityInit();

    dataManager.register(SCALE_INDEX, 1f);
    dataManager.register(GROWTH_MODE_INDEX, GrowthMode.NONE.ordinal());
  }

  @Override
  public EntityWitherWitch getOwner() {
    return owner;
  }

  @Override
  public void setOwner(EntityWitherWitch owner) {
    this.owner = owner;
  }

  @Override
  public @Nonnull EntityWitherCat asEntity() {
    return this;
  }

  public void setScale(float scale) {
    dataManager.set(SCALE_INDEX, scale);
  }

  public float getScale() {
    return dataManager.get(SCALE_INDEX);
  }

  public void setGrowthMode(GrowthMode mode) {
    setGrowthMode(mode.ordinal());
  }

  private void setGrowthMode(int ordinal) {
    dataManager.set(GROWTH_MODE_INDEX, ordinal);
  }

  public GrowthMode getGrowthMode() {
    return GrowthMode.values()[dataManager.get(GROWTH_MODE_INDEX)];
  }

  public float getAngryScale() {
    return ANGRY_SCALE;
  }

  public float getScaleInc() {
    return SCALE_INC;
  }

  public boolean isAngry() {
    return getScale() >= ANGRY_SCALE;
  }

  @Override
  public void setAttackTarget(@Nullable EntityLivingBase target) {
    if (getAttackTarget() != target) {
      attackTargetChanged = true;
    }
    super.setAttackTarget(target);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    applyAttributes(this, ZooConfig.witherCatHealth, ZooConfig.witherCatAttackDamage);
  }

  @Override
  public boolean isPotionApplicable(@Nonnull PotionEffect potion) {
    return potion.getPotion() != MobEffects.WITHER && super.isPotionApplicable(potion);
  }

  @Override
  public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
    if (owner != null && source.getTrueSource() == owner) {
      return false;
    }
    boolean res = super.attackEntityFrom(source, amount);
    if (!world.isRemote) {
      if (source.getTrueSource() instanceof EntityLivingBase) {
        if (owner != null) {
          EntityLivingBase ownerHitBy = owner.getRevengeTarget();
          if (ownerHitBy == null) {
            owner.setRevengeTarget((EntityLivingBase) source.getTrueSource());
          }
        } else if (owner == null) {
          setAttackTarget((EntityLivingBase) source.getTrueSource());
        }
      }
    }
    return res;
  }

  @Override
  public void setDead() {
    super.setDead();
    if (owner != null) {
      owner.catDied(this);
    }
  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    if (world.isRemote) {
      float scale = getScale();
      if (lastScale != scale) {
        spawnParticles();
        lastScale = scale;
        setSize(DEF_WIDTH * scale, DEF_HEIGHT * scale);
      }
      return;
    }

    if (!world.isRemote && attackTargetChanged) {
      EntityUtil.cancelCurrentTasks(this);
      tasks.removeTask(followTask);
      if (getAttackTarget() == null) {
        tasks.addTask(3, followTask);
      }
      attackTargetChanged = false;
    }

    if (owner != null && owner.isDead) {
      setOwner(null);
    }
    if (/* getOwner() != null && */getAttackTarget() != null && !isAngry() && getGrowthMode() != GrowthMode.GROW) {
      setGrowthMode(GrowthMode.GROW);
    }

    updateScale();

    float scale = getScale();
    if (lastScale != scale) {
      lastScale = scale;
      setSize(DEF_WIDTH * scale, DEF_HEIGHT * scale);
      float growthRatio = (lastScale - 1) / (ANGRY_SCALE - 1);
      updateAttackDamage(growthRatio);
      updateHealth(growthRatio);
    }
  }

  public void updateScale() {
    GrowthMode curMode = getGrowthMode();
    if (curMode == GrowthMode.NONE) {
      return;
    }

    float scale = getScale();
    if (curMode == GrowthMode.GROW) {
      if (scale < ANGRY_SCALE) {
        setScale(Math.min(scale + SCALE_INC, ANGRY_SCALE));
      } else {
        setScale(ANGRY_SCALE);
        setGrowthMode(GrowthMode.NONE);
      }
    } else {
      if (scale > 1) {
        setScale(Math.max(1, scale - SCALE_INC));
      } else {
        setScale(1);
        setGrowthMode(GrowthMode.NONE);
      }
    }
  }

  protected void updateAttackDamage(float growthRatio) {
    IAttributeInstance att = EntityUtil.removeModifier(this, SharedMonsterAttributes.ATTACK_DAMAGE, ATTACK_BOOST_MOD_UID);
    if (growthRatio == 0 || att == null) {
      return;
    }
    double damageInc = EntityUtil.isHardDifficulty(world) ? ZooConfig.witherCatAttackDamageBonusHard.get() : 0;
    double attackDif = damageInc + ZooConfig.witherCatAttackDamageBonusAngry.get();
    double toAdd = attackDif * growthRatio;
    AttributeModifier mod = new AttributeModifier(ATTACK_BOOST_MOD_UID, "Transformed Attack Modifier", toAdd, 0);
    att.applyModifier(mod);
  }

  protected void updateHealth(float growthRatio) {
    IAttributeInstance att = EntityUtil.removeModifier(this, SharedMonsterAttributes.MAX_HEALTH, HEALTH_BOOST_MOD_UID);
    if (growthRatio == 0 || att == null) {
      return;
    }
    double currentRatio = getHealth() / getMaxHealth();
    double healthDif = ZooConfig.witherCatHealthBonusAngry.get();
    double toAdd = healthDif * growthRatio;
    AttributeModifier mod = new AttributeModifier(HEALTH_BOOST_MOD_UID, "Transformed Attack Modifier", toAdd, 0);
    att.applyModifier(mod);

    double newHealth = currentRatio * getMaxHealth();
    setHealth((float) newHealth);

  }

  private void spawnParticles() {
    double startX = posX;
    double startY = posY;
    double startZ = posZ;
    double offsetScale = 0.8 * getScale();
    for (int i = 0; i < 2; i++) {
      double xOffset = offsetScale - rand.nextFloat() * offsetScale * 2;
      double yOffset = offsetScale / 3 + rand.nextFloat() * offsetScale / 3 * 2F;
      double zOffset = offsetScale - rand.nextFloat() * offsetScale * 2;
      Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), startX + xOffset, startY + yOffset,
          startZ + zOffset, 0.0D, 0.0D, 0.0D);
      if (fx != null) {
        fx.setRBGColorF(0.8f, 0.2f, 0.2f);
      }
    }
  }

  @Override
  public void setPosition(double x, double y, double z) {
    posX = x;
    posY = y;
    posZ = z;
    updateBounds();
  }

  @Override
  protected boolean isValidLightLevel() {
    return true;
  }

  @Override
  protected void setSize(float width, float height) {
    if (width != this.width || height != this.height) {
      this.width = width;
      this.height = height;
      updateBounds();
    }
  }

  private void updateBounds() {
    double hw = width / 2.0F;
    double hd = hw * 2.75;
    float f1 = height;
    setEntityBoundingBox(new AxisAlignedBB(posX - hw, posY, posZ - hd, posX + hw, posY + f1, posZ + hd));
  }

  // TODO: New sounds
  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.ENTITY_CAT_AMBIENT;
  }

  @Override
  protected @Nonnull SoundEvent getHurtSound(@Nonnull DamageSource source) {
    return SoundEvents.ENTITY_CAT_HURT;
  }

  @Override
  protected @Nonnull SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_CAT_DEATH;
  }

  @Override
  public boolean writeToNBTOptional(@Nonnull NBTTagCompound root) {
    if (getOwner() == null) {
      return super.writeToNBTOptional(root);
    }
    return false;
  }

  @Override
  public void writeEntityToNBT(@Nonnull NBTTagCompound root) {
    super.writeEntityToNBT(root);
    root.setFloat("scale", getScale());
    root.setByte("growthMode", (byte) getGrowthMode().ordinal());
  }

  @Override
  public void readEntityFromNBT(@Nonnull NBTTagCompound root) {
    super.readEntityFromNBT(root);
    if (root.hasKey("scale")) {
      setScale(root.getFloat("scale"));
    }
    if (root.hasKey("growthMode")) {
      setGrowthMode(root.getByte("growthMode"));
    }
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

}
