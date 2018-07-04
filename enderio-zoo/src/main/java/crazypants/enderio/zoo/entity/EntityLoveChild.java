package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.render.RenderLoveChild;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
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
public class EntityLoveChild extends EntityZombie implements IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    IEnderZooMob.register(event, NAME, EntityLoveChild.class, EGG_BG_COL, EGG_FG_COL, MobID.LCHILD);
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
    getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0);
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
    return null;
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
      if (rand.nextFloat() < ZooConfig.defendTeleportChance.get()) {
        RandomTeleportUtil.teleportEntity(world, this, false, true, ZooConfig.defendTeleportDistance.get());
        getNavigator().clearPath();
      }
      return true;
    }

    return false;
  }

  @Override
  protected @Nonnull Item getDropItem() {
    return Items.ENDER_PEARL;
  }

  @Override
  protected @Nonnull ItemStack getSkullDrop() {
    return ItemStack.EMPTY;
  }

  @Override
  public @Nonnull ItemStack getHeldItemOffhand() {
    final ItemStack item = super.getHeldItemOffhand();
    return item.isEmpty() ? OFFHAND_STACK : item;
  }

}