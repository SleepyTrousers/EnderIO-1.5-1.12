package crazypants.enderio.zoo.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.handler.darksteel.SwordHandler;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.ai.AIFindPlayer;
import crazypants.enderio.zoo.entity.render.RenderEnderminy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityEnderminy extends EntityMob implements IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    IEnderZooMob.register(event, NAME, EntityEnderminy.class, EGG_BG_COL, EGG_FG_COL, MobID.EMINIY);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityEnderminy.class, RenderEnderminy.FACTORY);
  }

  public static final @Nonnull String NAME = "enderminy";
  public static final int EGG_BG_COL = 0x27624D;
  public static final int EGG_FG_COL = 0x212121;

  private static final int MAX_RND_TP_DISTANCE = 32;

  private static final @Nonnull DataParameter<Boolean> SCREAMING_INDEX = EntityDataManager.<Boolean> createKey(EntityEnderminy.class, DataSerializers.BOOLEAN);

  private static final @Nonnull UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291B0");
  private static final @Nonnull AttributeModifier attackingSpeedBoostModifier = (new AttributeModifier(attackingSpeedBoostModifierUUID, "Attacking speed boost",
      6.2, 0)).setSaved(false);

  private boolean isAggressive;

  public EntityEnderminy(World world) {
    super(world);
    setSize(0.6F * 0.5F, 2.9F * 0.25F);
    stepHeight = 1.0F;

    tasks.addTask(0, new EntityAISwimming(this));
    tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
    tasks.addTask(7, new EntityAIWander(this, 1.0D));
    tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    tasks.addTask(8, new EntityAILookIdle(this));
    targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
    targetTasks.addTask(2, new AIFindPlayer(this) {
      @Override
      public boolean shouldExecute() {
        return ZooConfig.attackPlayers.get() ? super.shouldExecute() : false;
      }
    });
    targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityCreeper>(this, EntityCreeper.class, true, true) {
      @Override
      public boolean shouldExecute() {
        return ZooConfig.attackCreepers.get() ? super.shouldExecute() : false;
      }
    });
  }

  @Override
  protected boolean isValidLightLevel() {
    return ZooConfig.spawnInLitAreas.get() ? true : super.isValidLightLevel();
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
    applyAttributes(this, ZooConfig.miniHealth, ZooConfig.miniAttackDamage);
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    dataManager.register(SCREAMING_INDEX, false);
  }

  @Override
  public boolean getCanSpawnHere() {
    boolean passedGrassCheck = true;
    if (ZooConfig.spawnOnlyOnGrass.get()) {
      int i = MathHelper.floor(posX);
      int j = MathHelper.floor(getEntityBoundingBox().minY);
      int k = MathHelper.floor(posZ);
      passedGrassCheck = world.getBlockState(new BlockPos(i, j - 1, k)).getBlock() == Blocks.GRASS;
    }
    return passedGrassCheck && posY >= ZooConfig.spawnMinY.get() && super.getCanSpawnHere();
  }

  /**
   * Checks to see if this enderman should be attacking this player
   */
  public boolean shouldAttackPlayer(EntityPlayer player) {
    ItemStack itemstack = player.inventory.armorInventory.get(3);
    // 3: Helmet, 2: Chestpiece, 1: Legs, 0: Boots
    if (itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN)) {
      return false;
    } else {

      Vec3d relativePlayerEyePos = new Vec3d(posX - player.posX, getEntityBoundingBox().minY + height / 2.0F - (player.posY + player.getEyeHeight()),
          posZ - player.posZ);

      double distance = relativePlayerEyePos.lengthVector();
      relativePlayerEyePos = relativePlayerEyePos.normalize();

      // NB: inverse of normal enderman, attack when this guy looks at the player instead of the other
      // way around
      Vec3d lookVec = getLook(1.0F).normalize();
      double dotTangent = -lookVec.dotProduct(relativePlayerEyePos);

      return dotTangent > 1.0D - 0.025D / distance;
    }
  }

  @Override
  public void onLivingUpdate() {
    if (this.world.isRemote) {
      for (int i = 0; i < 2; ++i) {
        this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * this.width,
            this.posY + this.rand.nextDouble() * this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * this.width,
            (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
      }
    }
    isJumping = false;
    super.onLivingUpdate();
  }

  @Override
  protected void updateAITasks() {
    if (isWet()) {
      attackEntityFrom(DamageSource.DROWN, 1.0F);
    }
    if (isScreaming() && !isAggressive && rand.nextInt(100) == 0) {
      setScreaming(false);
    }
    super.updateAITasks();
  }

  protected boolean teleportRandomly(int distance) {
    double d0 = posX + (rand.nextDouble() - 0.5D) * distance;
    double d1 = posY + rand.nextInt(distance + 1) - distance / 2;
    double d2 = posZ + (rand.nextDouble() - 0.5D) * distance;
    return teleportTo(d0, d1, d2);
  }

  public boolean teleportRandomly() {
    return teleportRandomly(MAX_RND_TP_DISTANCE);
  }

  public boolean teleportToEntity(Entity p_70816_1_) {
    Vec3d vec3 = new Vec3d(posX - p_70816_1_.posX, getEntityBoundingBox().minY + height / 2.0F - p_70816_1_.posY + p_70816_1_.getEyeHeight(),
        posZ - p_70816_1_.posZ);
    vec3 = vec3.normalize();
    double d0 = 16.0D;
    double d1 = posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3.x * d0;
    double d2 = posY + (rand.nextInt(16) - 8) - vec3.y * d0;
    double d3 = posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3.z * d0;
    return teleportTo(d1, d2, d3);
  }

  protected boolean teleportTo(double x, double y, double z) {

    EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
    if (MinecraftForge.EVENT_BUS.post(event)) {
      return false;
    }
    double d3 = posX;
    double d4 = posY;
    double d5 = posZ;
    posX = event.getTargetX();
    posY = event.getTargetY();
    posZ = event.getTargetZ();

    int xInt = MathHelper.floor(posX);
    int yInt = MathHelper.floor(posY);
    int zInt = MathHelper.floor(posZ);

    boolean flag = false;
    if (world.isBlockLoaded(new BlockPos(xInt, yInt, zInt))) {

      boolean foundGround = false;
      while (!foundGround && yInt > 0) {
        IBlockState bs = world.getBlockState(new BlockPos(xInt, yInt - 1, zInt));
        if (bs.getMaterial().blocksMovement()) {
          foundGround = true;
        } else {
          --posY;
          --yInt;
        }
      }

      if (foundGround) {
        setPosition(posX, posY, posZ);
        if (world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(getEntityBoundingBox())) {
          flag = true;
        }
      }
    }

    if (!flag) {
      setPosition(d3, d4, d5);
      return false;
    }

    short short1 = 128;
    for (int l = 0; l < short1; ++l) {
      double d6 = l / (short1 - 1.0D);
      float f = (rand.nextFloat() - 0.5F) * 0.2F;
      float f1 = (rand.nextFloat() - 0.5F) * 0.2F;
      float f2 = (rand.nextFloat() - 0.5F) * 0.2F;
      double d7 = d3 + (posX - d3) * d6 + (rand.nextDouble() - 0.5D) * width * 2.0D;
      double d8 = d4 + (posY - d4) * d6 + rand.nextDouble() * height;
      double d9 = d5 + (posZ - d5) * d6 + (rand.nextDouble() - 0.5D) * width * 2.0D;
      world.spawnParticle(EnumParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
    }

    world.playSound(d3, d4, d5, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
    playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
    return true;

  }

  @Override
  protected SoundEvent getAmbientSound() {
    return isScreaming() ? SoundEvents.ENTITY_ENDERMEN_SCREAM : SoundEvents.ENTITY_ENDERMEN_AMBIENT;
  }

  @Override
  protected @Nonnull SoundEvent getHurtSound(@Nonnull DamageSource source) {
    return SoundEvents.ENTITY_ENDERMEN_HURT;
  }

  @Override
  protected @Nonnull SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_ENDERMEN_DEATH;
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

  /**
   * Called when the entity is attacked.
   */
  @Override
  public boolean attackEntityFrom(@Nonnull DamageSource damageSource, float p_70097_2_) {

    if (isEntityInvulnerable(damageSource)) {
      return false;
    }

    setScreaming(true);

    if (damageSource instanceof EntityDamageSourceIndirect) {
      isAggressive = false;
      for (int i = 0; i < 64; ++i) {
        if (teleportRandomly()) {
          return true;
        }
      }
      return super.attackEntityFrom(damageSource, p_70097_2_);
    }

    boolean res = super.attackEntityFrom(damageSource, p_70097_2_);
    if (damageSource instanceof EntityDamageSource && damageSource.getTrueSource() instanceof EntityPlayer && getHealth() > 0) {
      boolean skipTeleport = SwordHandler.isEquippedAndPowered((EntityPlayer) damageSource.getTrueSource(), DarkSteelConfig.darkSteelSwordPowerUsePerHit);
      isAggressive = true;
      if (rand.nextInt(3) == 0) {
        for (int i = 0; i < 64; ++i) {
          if (skipTeleport || teleportRandomly(16)) {
            setAttackTarget((EntityPlayer) damageSource.getTrueSource());
            doGroupArgo();
            return true;
          }
        }
      }
    }

    if (res) {
      doGroupArgo();
    }
    return res;

  }

  private void doGroupArgo() {
    if (!ZooConfig.miniPackAttackEnabled.get() || !(getAttackTarget() instanceof EntityPlayer)) {
      return;
    }
    int range = 16;
    AxisAlignedBB bb = new AxisAlignedBB(posX - range, posY - range, posZ - range, posX + range, posY + range, posZ + range);
    List<EntityEnderminy> minies = world.getEntitiesWithinAABB(EntityEnderminy.class, bb);
    if (!minies.isEmpty()) {
      for (EntityEnderminy miny : minies) {
        if (miny.getAttackTarget() == null) { // && miny.canEntityBeSeen(this)) {
          miny.setAttackTarget(getAttackTarget());
        }
      }
    }
  }

  public boolean isScreaming() {
    return dataManager.get(SCREAMING_INDEX);
  }

  public void setScreaming(boolean p_70819_1_) {
    dataManager.set(SCREAMING_INDEX, p_70819_1_);
  }

  public static @Nonnull AttributeModifier getAttackingspeedboostmodifier() {
    return attackingSpeedBoostModifier;
  }

  public boolean isAggressive() {
    return isAggressive;
  }

  public void setAggressive(boolean isAggressive) {
    this.isAggressive = isAggressive;
  }

}
