package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.ai.EntityAIMountedArrowAttack;
import crazypants.enderio.zoo.entity.ai.EntityAIMountedAttackOnCollide;
import crazypants.enderio.zoo.entity.render.RenderFallenKnight;
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
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
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
public class EntityFallenKnight extends EntitySkeleton implements IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    IEnderZooMob.register(event, NAME, EntityFallenKnight.class, EGG_BG_COL, EGG_FG_COL, MobID.FKNIGHT);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    RenderingRegistry.registerEntityRenderingHandler(EntityFallenKnight.class, RenderFallenKnight.FACTORY);
  }

  public static final @Nonnull String NAME = "fallenknight";
  public static final int EGG_FG_COL = 0x365A25;
  public static final int EGG_BG_COL = 0xA0A0A0;

  private final @Nonnull EntityAIMountedArrowAttack aiArrowAttack = new EntityAIMountedArrowAttack(this, ZooConfig.fallenKnightChargeSpeed,
      ZooConfig.fallenMountChargeSpeed, ZooConfig.fallenKnightRangedMinAttackPause, ZooConfig.fallenKnightRangedMaxAttackPause,
      ZooConfig.fallenKnightRangedMaxRange, ZooConfig.fallKnightMountedArchersMaintainDistance);
  private final @Nonnull EntityAIMountedAttackOnCollide aiAttackOnCollide = new EntityAIMountedAttackOnCollide(this, EntityPlayer.class,
      ZooConfig.fallenKnightChargeSpeed, ZooConfig.fallenMountChargeSpeed, false);

  private final @Nonnull EntityAIBreakDoor breakDoorAI = new EntityAIBreakDoor(this);
  private boolean canBreakDoors = false;

  private EntityLivingBase lastAttackTarget = null;

  private boolean knightFirstUpdate = true;
  private boolean isMounted = false;
  private boolean spawned = false;

  public EntityFallenKnight(World world) {
    super(world);
    setCombatTaskReal();
    targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, false));
    targetTasks.addTask(2, new EntityAINearestAttackableTarget<AbstractIllager>(this, AbstractIllager.class, false));
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ZooConfig.fallenKnightFollowRange.get());
    applyAttributes(this, ZooConfig.fallenKnightHealth, ZooConfig.fallenKnightAttackDamage);
  }

  // This is called from the super constructor and so is completely useless
  @Override
  public void setCombatTask() {
  }

  public void setCombatTaskReal() {
    tasks.removeTask(getAiAttackOnCollide());
    tasks.removeTask(getAiArrowAttack());
    if (isRanged()) {
      tasks.addTask(4, getAiArrowAttack());
    } else {
      tasks.addTask(4, getAiAttackOnCollide());
    }
  }

  @Override
  public void setItemStackToSlot(@Nonnull EntityEquipmentSlot slotIn, @Nonnull ItemStack stack) {
    super.setItemStackToSlot(slotIn, stack);
    setCombatTaskReal();
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

    if (knightFirstUpdate && !world.isRemote) {
      spawnMount();
    }

    final EntityLivingBase attackTarget = getAttackTarget();
    if (isRidingMount()) {
      EntityLiving entLiving = ((EntityLiving) getRidingEntity());
      if (entLiving != null && (lastAttackTarget != attackTarget || knightFirstUpdate)) {
        EntityUtil.cancelCurrentTasks(entLiving);
        lastAttackTarget = attackTarget;
      }
    }
    knightFirstUpdate = false;

    if (!isMounted == isRidingMount()) {
      getAiAttackOnCollide().resetTask();
      getAiArrowAttack().resetTask();
      getNavigator().clearPath();
      isMounted = isRidingMount();
    }
    if (isBurning() && isRidingMount()) {
      getRidingEntityNN().setFire(8);
    }
    if (ZooConfig.fallenKnightArchersSwitchToMelee.get() && (!isMounted || !ZooConfig.fallKnightMountedArchersMaintainDistance.get()) && attackTarget != null
        && isRanged() && getDistanceSq(attackTarget) < 5) {
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getSwordForLevel(getRandomEquipmentLevel()));
    }
  }

  private boolean isRidingMount() {
    return isRiding() && getRidingEntityNN().getClass() == EntityFallenMount.class;
  }

  public @Nonnull Entity getRidingEntityNN() {
    return NullHelper.notnullM(getRidingEntity(), "getRidingEntity()");
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
    if (rand.nextFloat() < ZooConfig.fallenKnightChanceMounted.get()) {
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
    return getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow;
  }

  private void addRandomArmor() {
    float occupiedDiffcultyMultiplier = EntityUtil.getDifficultyMultiplierForLocation(world, posX, posY, posZ);

    int equipmentLevel = getRandomEquipmentLevel(occupiedDiffcultyMultiplier);
    int armorLevel = equipmentLevel;
    if (armorLevel == 1) {
      // Skip gold armor, I don't like it
      armorLevel++;
    }
    float chancePerPiece = (isHardDifficulty() ? ZooConfig.fallenKnightChancePerArmorPiece : ZooConfig.fallenKnightChancePerArmorPieceHard).get();
    chancePerPiece *= (1 + occupiedDiffcultyMultiplier); // If we have the max occupied factor, double the chance of improved armor

    for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
      if (slot != null) {
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
    }
    if (rand.nextFloat() >= ZooConfig.fallenKnightRangedRatio.get()) {
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getSwordForLevel(equipmentLevel));
      if (rand.nextFloat() < ZooConfig.fallenKnightChanceAgentOfShield.get()) {
        setItemStackToSlot(EntityEquipmentSlot.OFFHAND, getShieldForLevel(getRandomEquipmentLevel()));
      }
    } else {
      setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getBowForLevel(equipmentLevel));
    }
  }

  private int getRandomEquipmentLevel() {
    return getRandomEquipmentLevel(EntityUtil.getDifficultyMultiplierForLocation(world, posX, posY, posZ));
  }

  private int getRandomEquipmentLevel(float occupiedDiffcultyMultiplier) {
    float chanceImprovedArmor = (isHardDifficulty() ? ZooConfig.fallenKnightChanceArmorUpgradeHard : ZooConfig.fallenKnightChanceArmorUpgrade).get();
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

  private @Nonnull ItemStack getSwordForLevel(int swordLevel) {
    //// have a better chance of not getting a wooden or stone sword
    if (swordLevel < 2) {
      swordLevel += rand.nextInt(isHardDifficulty() ? 3 : 2);
      swordLevel = Math.min(swordLevel, 2);
    }
    if (rand.nextFloat() < .80f) {
      switch (swordLevel) {
      case 0:
        return new ItemStack(Items.WOODEN_SWORD);
      case 1:
        return new ItemStack(Items.STONE_SWORD);
      case 2:
        return new ItemStack(Items.IRON_SWORD);
      case 4:
        return new ItemStack(Items.DIAMOND_SWORD);
      default:
        return rand.nextBoolean() ? new ItemStack(Items.IRON_SWORD) : new ItemStack(ModObject.itemDarkSteelSword.getItemNN());
      }
    } else {
      switch (swordLevel) {
      case 0:
        return new ItemStack(Items.WOODEN_AXE);
      case 1:
        return new ItemStack(Items.STONE_AXE);
      case 2:
        return new ItemStack(Items.IRON_AXE);
      case 4:
        return new ItemStack(Items.DIAMOND_AXE);
      default:
        return rand.nextBoolean() ? new ItemStack(Items.IRON_AXE) : new ItemStack(ModObject.itemDarkSteelAxe.getItemNN());
      }
    }
  }

  private @Nonnull ItemStack getBowForLevel(int bowLevel) {
    switch (bowLevel) {
    case 0:
    case 1:
      return new ItemStack(Items.BOW);
    default:
      return new ItemStack(ModObject.itemDarkSteelBow.getItemNN());
    }
  }

  private @Nonnull ItemStack getShieldForLevel(int swordLevel) {
    // TODO: 1.9 Can I do something better here?
    return new ItemStack(Items.SHIELD);
  }

  @Override
  public IEntityLivingData onInitialSpawn(@Nonnull DifficultyInstance di, @Nullable IEntityLivingData livingData) {
    spawned = true;

    // From base entity living class
    getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", rand.nextGaussian() * 0.05D, 1));
    setCombatTaskReal();
    addRandomArmor();
    setEnchantmentBasedOnDifficulty(di);

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
    setCombatTaskReal();
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
