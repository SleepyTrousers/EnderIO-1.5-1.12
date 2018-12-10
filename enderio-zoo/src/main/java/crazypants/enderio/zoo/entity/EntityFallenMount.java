package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.render.RenderFallenMount;
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
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
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
public class EntityFallenMount extends EntityHorse implements IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    IEnderZooMob.register(event, NAME, EntityFallenMount.class, EGG_BG_COL, EGG_FG_COL, MobID.FMOUNT);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    RenderingRegistry.registerEntityRenderingHandler(EntityFallenMount.class, RenderFallenMount.FACTORY);
  }

  public static final @Nonnull String NAME = "fallenmount";
  public static final int EGG_BG_COL = 0x365A25;
  public static final int EGG_FG_COL = 0xA0A0A0;

  private boolean wasRidden = false;
  private final @Nonnull EntityAINearestAttackableTarget<EntityPlayer> findTargetAI;
  private final @Nonnull EntityAIAttackMelee attackAI;

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
    attackAI = new EntityAIAttackMelee(this, ZooConfig.fallenMountChargeSpeed.get(), false);
    updateAttackAI();
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    applyAttributes(this, ZooConfig.fallenMountHealth, ZooConfig.fallenMountAttackDamage);
  }

  @Override
  protected boolean isMovementBlocked() {
    return isRearing();
  }

  @Override
  public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack itemstack = player.inventory.getCurrentItem();
    if (itemstack.getItem() == Items.SPAWN_EGG) {
      return super.processInteract(player, hand);
    }
    return false;
  }

  @Override
  protected boolean canDespawn() {
    return true;
  }

  @Override
  public boolean canMateWith(@Nonnull EntityAnimal p_70878_1_) {
    return false;
  }

  @Override
  public boolean canBeLeashedTo(@Nonnull EntityPlayer player) {
    return false;
  }

  @Override
  public boolean isBreedingItem(@Nonnull ItemStack p_70877_1_) {
    return false;
  }

  @Override
  public boolean isCreatureType(@Nonnull EnumCreatureType type, boolean forSpawnCount) {
    if (type == EnumCreatureType.MONSTER) {
      return true;
    }
    return false;
  }

  @Override
  public IEntityLivingData onInitialSpawn(@Nonnull DifficultyInstance di, @Nullable IEntityLivingData data) {

    setHorseArmorStack(ItemStack.EMPTY);
    setHorseSaddled(true);
    setGrowingAge(0);
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ZooConfig.fallenMountHealth.get());
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
    final IAttributeInstance jumpStrength = getAttributeMap().getAttributeInstanceByName("horse.jumpStrength");
    if (jumpStrength != null) {
      jumpStrength.setBaseValue(0.5);
    }
    setHealth(getMaxHealth());

    float chanceOfArmor = (world.getDifficulty() == EnumDifficulty.HARD ? ZooConfig.fallenMountChanceArmoredHard : ZooConfig.fallenMountChanceArmored).get();
    if (rand.nextFloat() <= chanceOfArmor) {

      // Value between 0 and 1 (normal) - 1.5 based on how long a chunk has been occupied and the moon phase

      // float occupiedDiffcultyMultiplier = worldObj.func_147462_b(posX, posY, posZ);

      float occupiedDiffcultyMultiplier = di.getClampedAdditionalDifficulty();
      // TODO: Do I need this normalised still?
      occupiedDiffcultyMultiplier /= 1.5f; // normalize
      float chanceImprovedArmor = (world.getDifficulty() == EnumDifficulty.HARD ? ZooConfig.fallenMountChanceArmorUpgradeHard
          : ZooConfig.fallenMountChanceArmorUpgrade).get();
      chanceImprovedArmor *= (1 + occupiedDiffcultyMultiplier); // If we have the max occupied factor, double the chance of improved armor

      int armorLevel = 0;
      for (int i = 0; i < 2; i++) {
        if (rand.nextFloat() <= chanceImprovedArmor) {
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
    if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL) {
      setDead();
    }
  }

  @Override
  public void onLivingUpdate() {

    super.onLivingUpdate();

    if (world.isDaytime() && !world.isRemote) {
      if (burnInSun() && world.getTotalWorldTime() % 20 == 0) {
        float f = getBrightness();
        if (f > 0.5F && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F
            && world.canBlockSeeSky(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ)))) {
          setFire(8);
        }
      }
    }
    setEatingHaystack(false);

    if (wasRidden != isRidden()) {
      updateAttackAI();
      wasRidden = isRidden();
    }
  }

  private boolean burnInSun() {
    if (isRidden() && ZooConfig.fallenMountShadedByRider.get()) {
      return false;
    }
    return getTotalArmorValue() == 0;
  }

  protected boolean isRidden() {
    return !getPassengers().isEmpty();
  }

  private void updateAttackAI() {
    targetTasks.removeTask(findTargetAI);
    tasks.removeTask(attackAI);
    if (!isRidden()) {
      targetTasks.addTask(2, findTargetAI);
      tasks.addTask(4, attackAI);
    }
  }

  @Override
  public boolean attackEntityAsMob(@Nonnull Entity target) {
    if (isRidden() || isDead) {
      return false;
    }
    super.attackEntityAsMob(target);
    if (!isRearing()) {
      makeMad();
    }
    float damage = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    return target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
  }

  @Override
  public void writeEntityToNBT(@Nonnull NBTTagCompound root) {
    super.writeEntityToNBT(root);
    NBTTagCompound armTag = new NBTTagCompound();
    armor.writeToNBT(armTag);
    root.setTag("armor", armTag);
  }

  @Override
  public void readEntityFromNBT(@Nonnull NBTTagCompound root) {
    super.readEntityFromNBT(root);
    setHorseSaddled(true);
    setHorseArmorStack(armor = new ItemStack(root.getCompoundTag("armor")));
  }

  @Override
  protected @Nonnull ResourceLocation getLootTable() {
    return new ResourceLocation(EnderIOZoo.DOMAIN, NAME);
  }

  @Override
  protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
    EntityLootHelper.dropLoot(this, getLootTable(), source);
    dropEquipment(wasRecentlyHit, lootingModifier);
  }

}
