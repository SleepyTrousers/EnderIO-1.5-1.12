package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.ai.EntityAIAttackOnCollideAggressive;
import crazypants.enderio.zoo.entity.render.RenderEpicSquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityEpicSquid extends EntitySquid implements IEnderZooEntity {

  private static final @Nonnull EntityLiving.SpawnPlacementType IN_LAVA = NullHelper.first(
      EnumHelper.addSpawnPlacementType("IN_LAVA",
          (world, pos) -> pos != null && world.getBlockState(pos).getMaterial() == Material.LAVA
              && world.getBlockState(pos.down()).getMaterial() == Material.LAVA && !world.getBlockState(pos.up()).isNormalCube()),
      EntityLiving.SpawnPlacementType.IN_WATER);

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    IEnderZooEntity.register(event, NAME, EntityEpicSquid.class, EGG_BG_COL, EGG_FG_COL, MobID.ESQUID);
    EntitySpawnPlacementRegistry.setPlacementType(EntityEpicSquid.class, IN_LAVA);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityEpicSquid.class, renderManager -> new RenderEpicSquid(renderManager));
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
    tasks.addTask(0, new EntityEpicSquid.AIMoveRandom(this));
    tasks.addTask(1, new EntityAIAttackOnCollideAggressive(this, 1.1D, true).setAttackFrequency(20));
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    applyAttributes(this, ZooConfig.epicSquidHealth, ZooConfig.epicSquidAttackDamage);
  }

  @Override
  public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
    float f = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
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
      if (world.handleMaterialAcceleration(this.getEntityBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D), Material.LAVA, this)) {
        fallDistance = 0.0F;
        inWater = true;
        extinguish();
      }
    }
    return inWater;
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

  /*
   * copy of the squid's MoveRandom with some tweaks (marked)
   */
  static class AIMoveRandom extends EntityAIBase {
    private final @Nonnull EntityEpicSquid squid;

    public AIMoveRandom(@Nonnull EntityEpicSquid p_i45859_1_) {
      this.squid = p_i45859_1_;
    }

    @Override
    public boolean shouldExecute() {
      return true;
    }

    @Override
    public void updateTask() {
      int i = this.squid.getIdleTime();

      if (i > 100) {
        squid.setMovementVector(0.0F, 0.0F, 0.0F);
      } else if (squid.getRNG().nextInt(50) == 0 || !squid.inWater || !squid.hasMovementVector()) {
        float f = squid.getRNG().nextFloat() * ((float) Math.PI * 2F);
        float f1 = MathHelper.cos(f) * 0.2F;
        float f2 = -0.1F + squid.getRNG().nextFloat() * 0.2F;
        if (squid.inWater && squid.getRNG().nextBoolean()) { // +
          // prefer to move up because lava is not transparent like water
          f2 = Math.abs(f2); // +
        } // +
        if (squid.world.getBlockState(squid.getPosition()).getMaterial() == Material.LAVA // +
            && squid.world.getBlockState(squid.getPosition().up()).getMaterial() == Material.LAVA) { // +
          // these are not deep sea kraken
          f2 += 0.2f; // +
        } // +
        float f3 = MathHelper.sin(f) * 0.2F;
        squid.setMovementVector(f1, f2, f3);
      }
    }
  }
}