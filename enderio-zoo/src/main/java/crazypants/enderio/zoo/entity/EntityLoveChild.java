package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.handler.darksteel.SwordHandler;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.render.RenderLoveChild;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityLoveChild extends EntityZombie implements IEnderZooEntity.Aggressive {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    IEnderZooEntity.register(event, NAME, EntityLoveChild.class, EGG_BG_COL, EGG_FG_COL, MobID.LCHILD);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityLoveChild.class, RenderLoveChild.FACTORY);
  }

  public static final @Nonnull String NAME = "lovechild";
  public static final int EGG_BG_COL = 0x132f55;
  public static final int EGG_FG_COL = 0x2b2d1c;

  private static final @Nonnull ItemStack OFFHAND_STACK = new ItemStack(Items.ENDER_PEARL);

  public EntityLoveChild(World worldIn) {
    super(worldIn);
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ZooConfig.loveChildSpeed.get());
    getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(ZooConfig.loveChildArmor.get());
    applyAttributes(this, ZooConfig.loveChildHealth, ZooConfig.loveChildAttackDamage);
  }

  @Override
  public boolean getCanSpawnHere() {
    return super.getCanSpawnHere() && this.world.canSeeSky(new BlockPos(this));
  }

  @Override
  protected boolean shouldBurnInDay() {
    return false;
  }

  @SuppressWarnings("null") // wrong annotation in EntityZombie
  @Override
  protected @Nullable SoundEvent getAmbientSound() {
    return null;
  }

  @Override
  protected @Nonnull SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
    return SoundEvents.ENTITY_ENDERMEN_HURT;
  }

  @Override
  protected @Nonnull SoundEvent getDeathSound() {
    return super.getDeathSound();
  }

  @Override
  protected @Nonnull SoundEvent getStepSound() {
    return super.getStepSound();
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
  public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
    boolean flag = super.attackEntityAsMob(entityIn);

    if (flag && entityIn instanceof EntityLivingBase && rand.nextFloat() < ZooConfig.attackTeleportChance.get()) {
      RandomTeleportUtil.teleportEntity(entityIn.world, entityIn, false, true, ZooConfig.attackTeleportDistance.get());
    }

    return flag;
  }

  @Override
  public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
    if (!isEntityInvulnerable(source) && super.attackEntityFrom(source, amount)) {
      if (source instanceof EntityDamageSource && source.getTrueSource() instanceof EntityPlayer
          && SwordHandler.isEquippedAndPowered((EntityPlayer) source.getTrueSource(), DarkSteelConfig.darkSteelSwordPowerUsePerHit)) {
        return true;
      }
      if (rand.nextFloat() < ZooConfig.defendTeleportChance.get()) {
        RandomTeleportUtil.teleportEntity(world, this, false, true, ZooConfig.defendTeleportDistance.get());
        getNavigator().clearPath();
      }
      return true;
    }

    return false;
  }

  @SubscribeEvent
  public static void onSummonAid(SummonAidEvent event) {
    if (event.getSummoner() instanceof EntityLoveChild) {
      if (ZooConfig.loveSummonAid.get() && //
          event.getResult() != Result.DENY && //
          event.getAttacker() != null && //
          event.getWorld().getDifficulty() == EnumDifficulty.HARD && //
          event.getWorld().rand.nextFloat() < event.getSummoner().getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).getAttributeValue() && //
          event.getWorld().getGameRules().getBoolean("doMobSpawning")) {
        event.setResult(Result.ALLOW);
        event.setCustomSummonedAid(new EntityLoveChild(event.getWorld()));
      } else {
        event.setResult(Result.DENY);
      }
    }
  }

  @Override
  protected @Nonnull ItemStack getSkullDrop() {
    switch (ZooConfig.loveSkullDrop.get()) {
    case ENDERMAN:
      return new ItemStack(ModObject.blockEndermanSkull.getBlockNN());
    case ZOMBIE:
      return super.getSkullDrop();
    case NONE:
    default:
      return ItemStack.EMPTY;
    }
  }

  @Override
  public @Nonnull ItemStack getHeldItemOffhand() {
    final ItemStack item = super.getHeldItemOffhand();
    return item.isEmpty() ? OFFHAND_STACK : item;
  }

}