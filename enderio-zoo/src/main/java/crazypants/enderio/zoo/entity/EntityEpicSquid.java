package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.ai.EntityAIAttackOnCollideAggressive;
import crazypants.enderio.zoo.entity.render.RenderEpicSquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityEpicSquid extends EntitySquid implements IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    IEnderZooMob.register(event, NAME, EntityEpicSquid.class, EGG_BG_COL, EGG_FG_COL, MobID.ESQUID);
    EntitySpawnPlacementRegistry.setPlacementType(EntityEpicSquid.class, SpawnPlacementType.IN_WATER);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityEpicSquid.class, RenderEpicSquid.FACTORY);
  }

  public static final @Nonnull String NAME = "epicsquid";
  public static final int EGG_BG_COL = 0x9c0001;
  public static final int EGG_FG_COL = 0xec464e;

  public EntityEpicSquid(World worldIn) {
    super(worldIn);
    this.isImmuneToFire = true;
  }

  @Override
  protected void initEntityAI() {
    super.initEntityAI();
    tasks.addTask(1, new EntityAIAttackOnCollideAggressive(this, 1.1D, true).setAttackFrequency(20));
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    applyAttributes(this, ZooConfig.epicSquidHealth, ZooConfig.epicSquidAttackDamage);
  }

  @Override
  public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
    float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);
  }

  @Override
  public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
    super.setRevengeTarget(livingBase);
    if (getAttackTarget() == null) {
      setAttackTarget(livingBase);
    }
  }

  @Override
  public boolean handleWaterMovement() {
    super.handleWaterMovement();
    if (!inWater) {
      if (this.world.handleMaterialAcceleration(this.getEntityBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D), Material.LAVA, this)) {
        this.fallDistance = 0.0F;
        this.inWater = true;
        this.extinguish();
      }
    }
    return this.inWater;
  }

  // TODO: Lava/Nether spawning

}