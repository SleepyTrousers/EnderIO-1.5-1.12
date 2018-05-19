package crazypants.enderio.zoo.entity;

import crazypants.enderio.zoo.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityFallenMount extends EntityHorse implements IEnderZooMob {

  public static final int EGG_BG_COL = 0x365A25;
  public static final int EGG_FG_COL = 0xA0A0A0;
  public static final String NAME = "fallenmount";

  public static final double MOUNTED_ATTACK_MOVE_SPEED = Config.fallenMountChargeSpeed;;

  private boolean wasRidden = false;
  private final EntityAINearestAttackableTarget<EntityPlayer> findTargetAI;
  private EntityAIAttackMelee attackAI;

  private ItemStack armor = ItemStack.EMPTY;

  public EntityFallenMount(World world) {
    super(world);
    setGrowingAge(0);
    setHorseSaddled(true);

    tasks.taskEntries.clear();
    tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(6, new EntityAIWander(this, 1.2D));
    tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    tasks.addTask(8, new EntityAILookIdle(this));

    findTargetAI = new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true);
    attackAI = new EntityAIAttackMelee(this, MOUNTED_ATTACK_MOVE_SPEED, false);
    updateAttackAI(); 
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    MobInfo.FALLEN_MOUNT.applyAttributes(this);
  }

  @Override
  protected boolean isMovementBlocked() {
    return isRearing();
  }

  @Override
  public boolean processInteract(EntityPlayer player, EnumHand hand) {
    ItemStack itemstack = player.inventory.getCurrentItem();
    if(itemstack.getItem() == Items.SPAWN_EGG) {
      return super.processInteract(player, hand);
    }
    return false;
  }

  @Override
  protected boolean canDespawn() {    
    return true;
  }

  @Override
  public boolean canMateWith(EntityAnimal p_70878_1_) {
    return false;
  }
  
  @Override
  public boolean canBeLeashedTo(EntityPlayer player) {    
    return false;
  }

  @Override
  public boolean isBreedingItem(ItemStack p_70877_1_) {   
    return false;
  }  

  @Override
  public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {    
    if(type == EnumCreatureType.MONSTER) {      
      return true;
    }
    return false;    
  }

  @Override
  public IEntityLivingData onInitialSpawn(DifficultyInstance di, IEntityLivingData data) {  

    setHorseArmorStack(ItemStack.EMPTY);        
    setHorseSaddled(true);    
    setGrowingAge(0);
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.fallenMountHealth);
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
    getAttributeMap().getAttributeInstanceByName("horse.jumpStrength").setBaseValue(0.5);
    setHealth(getMaxHealth());
    
    float chanceOfArmor = world.getDifficulty() == EnumDifficulty.HARD ? Config.fallenMountChanceArmoredHard
        : Config.fallenMountChanceArmored;
    if(rand.nextFloat() <= chanceOfArmor) {

      //Value between 0 and 1 (normal) - 1.5 based on how long a chunk has been occupied and the moon phase
      
      //float occupiedDiffcultyMultiplier = worldObj.func_147462_b(posX, posY, posZ);
      
      float occupiedDiffcultyMultiplier = di.getClampedAdditionalDifficulty();
      //TODO: Do I need this normalised still?
      occupiedDiffcultyMultiplier /= 1.5f; // normalize
      float chanceImprovedArmor = world.getDifficulty() == EnumDifficulty.HARD ? Config.fallenMountChanceArmorUpgradeHard
          : Config.fallenMountChanceArmorUpgrade;
      chanceImprovedArmor *= (1 + occupiedDiffcultyMultiplier); //If we have the max occupied factor, double the chance of improved armor

      int armorLevel = 0;
      for (int i = 0; i < 2; i++) {
        if(rand.nextFloat() <= chanceImprovedArmor) {
          armorLevel++;
        }
      }
      Item armorItem = Items.IRON_HORSE_ARMOR;
      switch (armorLevel) {
      case 1:
        armorItem = Items.GOLDEN_HORSE_ARMOR;
        break;
      case 2:
        armorItem = Items.DIAMOND_HORSE_ARMOR;
        break;
      }
      armor = new ItemStack(armorItem);
      setHorseArmorStack(armor);
    } else {
      armor = ItemStack.EMPTY;
      setHorseArmorStack(armor);
    }
    return data;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    if(!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL) {
      setDead();
    }
  }

  @Override
  public void onLivingUpdate() {

    super.onLivingUpdate();

    if(world.isDaytime() && !world.isRemote) {
      if(burnInSun() && world.getTotalWorldTime() % 20 == 0) {
        float f = getBrightness();
        if(f > 0.5F && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F
            && world.canBlockSeeSky(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ)))) {
          setFire(8);
        }
      }
    }
    setEatingHaystack(false);    

    if(wasRidden != isRidden()) {
      updateAttackAI();
      wasRidden = isRidden();
    }
  }

  
  private boolean burnInSun() {        
    if(!isRidden()) {
      return getTotalArmorValue() == 0;
    }    
    if(Config.fallenMountShadedByRider) {
      return false;
    }
    return getTotalArmorValue() > 0;
  }

  protected boolean isRidden() {    
    return !getPassengers().isEmpty();
  }

  private void updateAttackAI() {
    targetTasks.removeTask(findTargetAI);
    tasks.removeTask(attackAI);
    if(!isRidden()) {
      targetTasks.addTask(2, findTargetAI);
      tasks.addTask(4, attackAI);
    }
  }

  @Override
  public boolean attackEntityAsMob(Entity target) {
    if(isRidden() || isDead) {
      return false;
    }
    super.attackEntityAsMob(target);
    if(!isRearing()) {
      makeMad();
    }
    float damage = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    return target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound root) {
    super.writeEntityToNBT(root);
    NBTTagCompound armTag = new NBTTagCompound();
    armor.writeToNBT(armTag);
    root.setTag("armor", armTag);
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound root) {
    super.readEntityFromNBT(root);
    setHorseSaddled(true);
    if(root.hasKey("armor")) {
      NBTTagCompound armTag = root.getCompoundTag("armor");
      armor = new ItemStack(armTag);
      setHorseArmorStack(armor);
    } else {
      armor = ItemStack.EMPTY;
      setHorseArmorStack(armor);
    }
  }


}
