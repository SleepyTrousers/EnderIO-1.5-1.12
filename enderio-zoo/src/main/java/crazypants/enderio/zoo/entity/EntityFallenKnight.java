package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.config.Config;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.ai.EntityAIMountedArrowAttack;
import crazypants.enderio.zoo.entity.ai.EntityAIMountedAttackOnCollide;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityFallenKnight extends EntitySkeleton implements IEnderZooMob {

  public static final int EGG_FG_COL = 0x365A25;
  public static final int EGG_BG_COL = 0xA0A0A0;

  public static final String NAME = "fallenknight";

  private final @Nonnull EntityAIMountedArrowAttack aiArrowAttack;
  private final @Nonnull EntityAIMountedAttackOnCollide aiAttackOnCollide;

  private final EntityAIBreakDoor breakDoorAI = new EntityAIBreakDoor(this);
  private boolean canBreakDoors = false;

  private EntityLivingBase lastAttackTarget = null;

  private boolean firstUpdate = true;
  private boolean isMounted = false;

  private boolean spawned = false;

  public EntityFallenKnight(World world) {
    super(world);
    targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, false));
    targetTasks.addTask(2, new EntityAINearestAttackableTarget<AbstractIllager>(this, AbstractIllager.class, false));
    aiArrowAttack = new EntityAIMountedArrowAttack(this, ZooConfig.fallenKnightChargeSpeed, ZooConfig.fallenMountChargeSpeed,
        ZooConfig.fallenKnightRangedMinAttackPause, ZooConfig.fallenKnightRangedMaxAttackPause, ZooConfig.fallenKnightRangedMaxRange,
        ZooConfig.fallKnightMountedArchersMaintainDistance);
    aiAttackOnCollide = new EntityAIMountedAttackOnCollide(this, EntityPlayer.class, ZooConfig.fallenKnightChargeSpeed, ZooConfig.fallenMountChargeSpeed,
        false);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ZooConfig.fallenKnightFollowRange.get());
    MobInfo.FALLEN_KNIGHT.applyAttributes(this);
  }

  // private float getAttackRange() {
  // if(isRiding()) {
  // return 3;
  // }
  // return 2;
  // }

  @Override
  public void setCombatTask() {
    tasks.removeTask(getAiAttackOnCollide());
    tasks.removeTask(getAiArrowAttack());
    if (isRanged()) {
      tasks.addTask(4, getAiArrowAttack());
    } else {
      tasks.addTask(4, getAiAttackOnCollide());
    }
  }

  public @Nonnull EntityAIMountedArrowAttack getAiArrowAttack() {
    return aiArrowAttack;
  }

  public @Nonnull EntityAIMountedAttackOnCollide getAiAttackOnCollide() {
    return aiAttackOnCollide;
  }

  @Override
  protected @Nonnull SoundEvent getAmbientSound() {
    return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
  }

  @Override
  protected @Nonnull SoundEvent getHurtSound(@Nonnull DamageSource source) {
    return SoundEvents.ENTITY_ZOMBIE_HURT;
  }

  @Override
  protected @Nonnull SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_ZOMBIE_DEATH;
  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();

    if (firstUpdate && !world.isRemote) {
      spawnMount();
    }

    if (isRidingMount()) {
      EntityLiving entLiving = ((EntityLiving) getRidingEntity());
      if (lastAttackTarget != getAttackTarget() || firstUpdate) {
        EntityUtil.cancelCurrentTasks(entLiving);
        lastAttackTarget = getAttackTarget();
      }
    }
    firstUpdate = false;

    if (!isMounted == isRidingMount()) {
      getAiAttackOnCollide().resetTask();
      getAiArrowAttack().resetTask();
      getNavigator().clearPath();
      isMounted = isRidingMount();
    }
    if (isBurning() && isRidingMount()) {
      getRidingEntity().setFire(8);
    }
    if (ZooConfig.fallenKnightArchersSwitchToMelee.get() && (!isMounted || !ZooConfig.fallKnightMountedArchersMaintainDistance.get())
        && getAttackTarget() != null && isRanged() && getDistanceSq(getAttackTarget()) < 5) {
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getSwordForLevel(getRandomEquipmentLevel()));
    }
  }

  private boolean isRidingMount() {
    return isRiding() && getRidingEntity().getClass() == EntityFallenMount.class;
  }

  @Override
  protected void despawnEntity() {
    Entity mount = getRidingEntity();
    super.despawnEntity();
    if (isDead && mount != null) {
      mount.setDead();
    }
  }

  private void spawnMount() {

    if (isRiding() || !spawned) {
      return;
    }

    EntityFallenMount mount = null;
    if (Config.fallenMountEnabled && rand.nextFloat() <= Config.fallenKnightChanceMounted) {
      mount = new EntityFallenMount(world);
      mount.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);

      DifficultyInstance di = world.getDifficultyForLocation(new BlockPos(mount));
      mount.onInitialSpawn(di, null);
      // NB: don;t check for entity collisions as we know the knight will collide
      if (!SpawnUtil.isSpaceAvailableForSpawn(world, mount, false)) {
        mount = null;
      }
    }
    if (mount != null) {
      setCanPickUpLoot(false);
      setCanBreakDoors(false);
      world.spawnEntity(mount);
      startRiding(mount);

    }
  }

  private boolean isRanged() {
    ItemStack itemstack = getHeldItem(EnumHand.MAIN_HAND);
    return itemstack != null && itemstack.getItem() == Items.BOW;
  }

  private void addRandomArmor() {

    float occupiedDiffcultyMultiplier = EntityUtil.getDifficultyMultiplierForLocation(world, posX, posY, posZ);

    int equipmentLevel = getRandomEquipmentLevel(occupiedDiffcultyMultiplier);
    int armorLevel = equipmentLevel;
    if (armorLevel == 1) {
      // Skip gold armor, I don't like it
      armorLevel++;
    }
    float chancePerPiece = isHardDifficulty() ? Config.fallenKnightChancePerArmorPieceHard : Config.fallenKnightChancePerArmorPiece;
    chancePerPiece *= (1 + occupiedDiffcultyMultiplier); // If we have the max occupied factor, double the chance of improved armor

    for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
      ItemStack itemStack = getItemStackFromSlot(slot);
      if (itemStack.isEmpty() && rand.nextFloat() <= chancePerPiece) {
        Item item = EntityLiving.getArmorByChance(slot, armorLevel);
        if (item != null) {
          ItemStack stack = new ItemStack(item);
          if (armorLevel == 0) {
            ((ItemArmor) item).setColor(stack, 0);
          }
          setItemStackToSlot(slot, stack);
        }
      }
    }
    if (rand.nextFloat() > Config.fallenKnightRangedRatio) {
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getSwordForLevel(equipmentLevel));
      if (Math.random() <= Config.fallenKnightChanceShield) {
        setItemStackToSlot(EntityEquipmentSlot.OFFHAND, getShieldForLevel(getRandomEquipmentLevel()));
      }
    } else {
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }
  }

  private int getRandomEquipmentLevel() {
    return getRandomEquipmentLevel(EntityUtil.getDifficultyMultiplierForLocation(world, posX, posY, posZ));
  }

  private int getRandomEquipmentLevel(float occupiedDiffcultyMultiplier) {
    float chanceImprovedArmor = isHardDifficulty() ? Config.fallenKnightChanceArmorUpgradeHard : Config.fallenKnightChanceArmorUpgrade;
    chanceImprovedArmor *= (1 + occupiedDiffcultyMultiplier); // If we have the max occupied factor, double the chance of improved armor

    int armorLevel = rand.nextInt(2);
    for (int i = 0; i < 2; i++) {
      if (rand.nextFloat() <= chanceImprovedArmor) {
        armorLevel++;
      }
    }
    return armorLevel;
  }

  protected boolean isHardDifficulty() {
    return EntityUtil.isHardDifficulty(world);
  }

  private ItemStack getSwordForLevel(int swordLevel) {
    //// have a better chance of not getting a wooden or stone sword
    if (swordLevel < 2) {
      swordLevel += rand.nextInt(isHardDifficulty() ? 3 : 2);
      swordLevel = Math.min(swordLevel, 2);
    }
    switch (swordLevel) {
    case 0:
      return new ItemStack(Items.WOODEN_SWORD);
    case 1:
      return new ItemStack(Items.STONE_SWORD);
    case 2:
      return new ItemStack(Items.IRON_SWORD);
    case 4:
      return new ItemStack(Items.DIAMOND_SWORD);
    }
    return new ItemStack(Items.IRON_SWORD);
  }

  private ItemStack getShieldForLevel(int swordLevel) {
    // TODO: 1.9 Can I do something better here?
    return new ItemStack(Items.SHIELD);
  }

  @Override
  public IEntityLivingData onInitialSpawn(@Nonnull DifficultyInstance di, IEntityLivingData livingData) {
    spawned = true;

    // From base entity living class
    getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", rand.nextGaussian() * 0.05D, 1));
    // func_189768_a(SkeletonType.NORMAL);//skeleton types do not exist anymore in 1.11.2. so its always normal.
    addRandomArmor();
    setEnchantmentBasedOnDifficulty(di); // enchantEquipment();

    float f = di.getClampedAdditionalDifficulty();
    this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * f);
    setCanPickUpLoot(rand.nextFloat() < 0.55F * f);
    setCanBreakDoors(rand.nextFloat() < f * 0.1F);

    return livingData;
  }

  @Override
  public void writeEntityToNBT(@Nonnull NBTTagCompound root) {
    super.writeEntityToNBT(root);
    root.setBoolean("canBreakDoors", canBreakDoors);
  }

  @Override
  public void readEntityFromNBT(@Nonnull NBTTagCompound root) {
    super.readEntityFromNBT(root);
    setCanBreakDoors(root.getBoolean("canBreakDoors"));
  }

  private void setCanBreakDoors(boolean val) {
    if (canBreakDoors != val) {
      canBreakDoors = val;
      if (canBreakDoors) {
        tasks.addTask(1, breakDoorAI);
      } else {
        tasks.removeTask(breakDoorAI);
      }
    }
  }

  @Override
  protected void dropFewItems(boolean hitByPlayer, int lootingLevel) {
    int numDrops = rand.nextInt(3 + lootingLevel);
    for (int i = 0; i < numDrops; ++i) {
      if (rand.nextBoolean()) {
        dropItem(Items.BONE, 1);
      } else {
        dropItem(Items.ROTTEN_FLESH, 1);
      }
    }
  }

}
