package crazypants.enderio.zoo.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.base.potion.PotionUtil;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.EntityWitherCat.GrowthMode;
import crazypants.enderio.zoo.entity.ai.EntityAIRangedAttack;
import crazypants.enderio.zoo.entity.render.RenderWitherWitch;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityWitherWitch extends EntityMob implements IRangedAttackMob, IEnderZooEntity.Aggressive {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    IEnderZooEntity.register(event, NAME, EntityWitherWitch.class, EGG_BG_COL, EGG_FG_COL, MobID.WWITCH);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityWitherWitch.class, RenderWitherWitch.FACTORY);
  }

  public static final @Nonnull String NAME = "witherwitch";
  public static final int EGG_BG_COL = 0x26520D;
  public static final int EGG_FG_COL = 0x905E43;

  private int attackTimer;
  private EntityLivingBase attackedWithPotion;

  private int healTimer;
  private boolean isHealing;

  private boolean spawned;
  private boolean firstUpdateWitch = true;

  private final @Nonnull List<EntityWitherCat> cats = new ArrayList<>();
  private final @Nonnull List<NBTTagCompound> loadedCats = new ArrayList<>();
  private final @Nonnull EntityAIRangedAttack rangedAttackAI;
  private int noActiveTargetTime;

  public EntityWitherWitch(World world) {
    super(world);
    rangedAttackAI = new EntityAIRangedAttack(this, 1, 60, 10);
    tasks.addTask(1, new EntityAISwimming(this));
    tasks.addTask(2, rangedAttackAI);
    tasks.addTask(2, new EntityAIWander(this, 1.0D));
    tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    tasks.addTask(3, new EntityAILookIdle(this));
    targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    applyAttributes(this, ZooConfig.witherWitchHealth, NO_ATTACK);
  }

  @Override
  protected float applyPotionDamageCalculations(@Nonnull DamageSource damageSource, float damage) {
    // same as a vanilla witch
    damage = super.applyPotionDamageCalculations(damageSource, damage);
    if (damageSource.getTrueSource() == this) {
      damage = 0.0F;
    }
    if (damageSource.isMagicDamage()) {
      damage = (float) (damage * 0.15D);
    }
    return damage;
  }

  @Override
  public boolean isPotionApplicable(@Nonnull PotionEffect potion) {
    return potion.getPotion() != MobEffects.WITHER && super.isPotionApplicable(potion);
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
  public void setRevengeTarget(@Nullable EntityLivingBase target) {
    EntityLivingBase curTarget = getRevengeTarget();
    super.setRevengeTarget(target);
    if (curTarget == target || world.isRemote || target == null) {
      return;
    }
    float distToSrc = getDistance(target);
    if (distToSrc > getNavigator().getPathSearchRange() && distToSrc < 50) {
      getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(distToSrc + 2);
    }
  }

  @Override
  public IEntityLivingData onInitialSpawn(@Nonnull DifficultyInstance di, @Nullable IEntityLivingData livingData) {
    spawned = true;
    return super.onInitialSpawn(di, livingData);
  }

  @Override
  public void onLivingUpdate() {
    if (world.isRemote) {
      super.onLivingUpdate();
      return;
    }

    if (firstUpdateWitch) {
      if (spawned) {
        spawnCats();
      } else {
        loadCats();
      }
      firstUpdateWitch = false;
    }
    doAttackActions();
    manageCats();

    super.onLivingUpdate();
  }

  protected void doAttackActions() {
    attackTimer--;
    healTimer--;

    EntityLivingBase target = getActiveTarget();
    if (target == null) {
      noActiveTargetTime++;
    } else {
      noActiveTargetTime = 0;
    }

    if (shouldStartHeal()) {
      ItemStack potion;
      if (rand.nextFloat() > 0.75) {
        potion = PotionUtil.createRegenerationPotion(false, true, true);
      } else {
        potion = PotionUtil.createHealthPotion(false, true);
      }
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, potion);
      healTimer = 10;
      isHealing = true;
    } else if (target != null && getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
      ItemStack potion;
      if (getActiveTarget().isPotionActive(MobEffects.WITHER)) {
        if (rand.nextFloat() < (EntityUtil.isHardDifficulty(world) ? .05f : .005f)) {
          potion = PotionUtil.createFloatingPotion(false, false, true);
        } else {
          potion = PotionUtil.createHarmingPotion(EntityUtil.isHardDifficulty(world), true);
        }
      } else {
        potion = PotionUtil.createWitherPotion(false, true);
      }
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, potion);
      attackTimer = 10;
      healTimer = 40;
    } else if (noActiveTargetTime > 40 && !isHealing && getHeldItem(EnumHand.MAIN_HAND).isEmpty() == false) {
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
      attackedWithPotion = null;
    }
    // the EntityPotion class validates if this potion is throwable, and if not it logs error "ThrownPotion entity {} has no item?!
    if (isHealing && healTimer <= 0 && getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.SPLASH_POTION) {
      throwHealthPotion();
      isHealing = false;
    }
  }

  protected EntityLivingBase getActiveTarget() {
    EntityLivingBase res = getAttackTarget();
    if (res == null) {
      res = rangedAttackAI.getAttackTarget();
    }
    return res;
  }

  protected boolean shouldStartHeal() {
    if (isPotionActive(MobEffects.REGENERATION)) {
      return false;
    }
    return getHealth() < getMaxHealth() * 0.75 && rand.nextFloat() > 0.5 && healTimer <= 0;
  }

  @Override
  public void attackEntityWithRangedAttack(@Nonnull EntityLivingBase entity, float rangeRatio) {
    // the EntityPotion class validates if this potion is throwable, and if not it logs error "ThrownPotion entity {} has no item?!
    if (attackTimer <= 0 && getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.SPLASH_POTION && !isHealing) {

      attackedWithPotion = entity;

      double x = entity.posX + entity.motionX - posX;
      double y = entity.posY + entity.getEyeHeight() - 1.100000023841858D - posY;
      double z = entity.posZ + entity.motionZ - posZ;
      float groundDistance = MathHelper.sqrt(x * x + z * z);

      ItemStack potion = getHeldItem(EnumHand.MAIN_HAND);
      attackTimer = getHeldItem(EnumHand.MAIN_HAND).getMaxItemUseDuration();

      EntityPotion entitypotion = new EntityPotion(world, this, potion);
      entitypotion.rotationPitch -= -20.0F;
      entitypotion.shoot(x, y + groundDistance * 0.2F, z, 0.75F, 8.0F);
      world.spawnEntity(entitypotion);

      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }
  }

  protected void throwHealthPotion() {
    ItemStack potion = getHeldItem(EnumHand.MAIN_HAND);
    // if its not a splash or lingering potion it will be an error
    EntityPotion entitypotion = new EntityPotion(world, this, potion);
    Vec3d lookVec = getLookVec();

    entitypotion.shoot(lookVec.x * 0.5, -1, lookVec.z * 0.5, 0.75F, 1.0F);
    world.spawnEntity(entitypotion);
    setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
    healTimer = 80;
  }

  public void catDied(EntityWitherCat cat) {
    cats.remove(cat);
  }

  private void spawnCats() {
    if (!ZooConfig.witherCatEnabled.get()) {
      return;
    }
    int numCats = rand.nextInt(ZooConfig.witherCatMaximum.get() + 1);
    numCats = Math.max(numCats, ZooConfig.witherCatMinimum.get());
    for (int i = 0; i < numCats; i++) {
      BlockPos clearGround = SpawnUtil.findClearGround(world, BlockCoord.get(this).add(4 - rand.nextInt(9), 0, 4 - rand.nextInt(9)), 2, 10, true);
      if (clearGround != null) {
        spawnCat(clearGround);
      } else {
        return;
      }
    }
  }

  private void spawnCat(BlockPos spawnLoc) {
    EntityWitherCat cat = new EntityWitherCat(world);
    cat.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(this)), null);
    cat.setOwner(this);
    cat.setPositionAndRotation(spawnLoc.getX() + 0.5, spawnLoc.getY() + 0.5, spawnLoc.getZ() + 0.5, rotationYaw, 0);
    if (MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.CheckSpawn(cat, world, (float) cat.posX, (float) cat.posY, (float) cat.posZ, false))) {
      return;
    }
    if (!cat.getCanSpawnHere()) {
      return;
    }
    cats.add(cat);
    world.spawnEntity(cat);
  }

  @Override
  public void writeEntityToNBT(@Nonnull NBTTagCompound root) {
    super.writeEntityToNBT(root);
    if (cats.isEmpty()) {
      return;
    }
    NBTTagList catsList = new NBTTagList();
    for (EntityWitherCat cat : cats) {
      if (!cat.isDead) {
        NBTTagCompound catRoot = new NBTTagCompound();
        cat.writeToNBT(catRoot);
        catsList.appendTag(catRoot);
      }
    }
    if (catsList.tagCount() > 0) {
      root.setTag("cats", catsList);
    }
  }

  @Override
  public void readEntityFromNBT(@Nonnull NBTTagCompound root) {
    super.readEntityFromNBT(root);
    loadedCats.clear();
    if (root.hasKey("cats")) {
      NBTTagList catsList = (NBTTagList) root.getTag("cats");
      for (int i = 0; i < catsList.tagCount(); i++) {
        loadedCats.add(catsList.getCompoundTagAt(i));
      }
    }
  }

  private void loadCats() {
    if (loadedCats.isEmpty()) {
      return;
    }
    for (NBTTagCompound catRoot : loadedCats) {
      if (catRoot != null) {
        EntityWitherCat cat = new EntityWitherCat(world);
        cat.readFromNBT(catRoot);
        cat.setOwner(this);
        cats.add(cat);
        world.spawnEntity(cat);
      }
    }
  }

  protected void manageCats() {
    if (cats.isEmpty()) {
      return;
    }
    if (noActiveTargetTime > 40) {
      pacifyCats();
      return;
    }
    EntityLivingBase currentTarget = getActiveTarget();
    EntityLivingBase hitBy = getRevengeTarget();
    if (hitBy == null) {
      // agro the cats if we have been hit or we have actually thrown a potion
      hitBy = attackedWithPotion;
    }
    angerCats(currentTarget, hitBy);
  }

  private void angerCats(EntityLivingBase targ, EntityLivingBase hitBy) {
    for (EntityWitherCat cat : cats) {
      if (cat.isAngry()) {
        if (cat.getAttackTarget() != targ) {
          cat.setAttackTarget(targ);
        }
      } else if (cat.getGrowthMode() != GrowthMode.GROW && hitBy != null) {
        cat.setGrowthMode(GrowthMode.GROW);
      }
    }
  }

  private void pacifyCats() {
    for (EntityWitherCat cat : cats) {
      if (cat.isAngry()) {
        cat.setGrowthMode(GrowthMode.SHRINK);
        if (cat.getAttackTarget() != null) {
          cat.setAttackTarget(null);
        }
      }
    }
  }

  @Override
  public void setSwingingArms(boolean swingingArms) {
  }

}
