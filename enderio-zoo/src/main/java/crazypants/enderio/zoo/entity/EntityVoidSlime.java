package crazypants.enderio.zoo.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.base.Predicate;

import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.base.render.ranged.InfinityParticle;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.render.RenderVoidSlime;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
public class EntityVoidSlime extends EntityMagmaCube implements IEnderZooEntity.Aggressive {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    IEnderZooEntity.register(event, NAME, EntityVoidSlime.class, EGG_BG_COL, EGG_FG_COL, MobID.VSLIME);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityVoidSlime.class, RenderVoidSlime.FACTORY);
  }

  public static final @Nonnull String NAME = "voidslime";
  public static final int EGG_BG_COL = 0x000000;
  public static final int EGG_FG_COL = 0xaaaaaa;

  public EntityVoidSlime(World world) {
    super(world);
    setSlimeSize(1, false);
  }

  @Override
  public void setSlimeSize(int size, boolean doFullHeal) {
    super.setSlimeSize(size, doFullHeal);
    getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ZooConfig.voidSlime1AttackDamage.get());
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ZooConfig.voidSlime1Health.get());
    setHealth(getMaxHealth());
  }

  @Override
  public void setDead() {
    // Override to prevent smaller slimes spawning
    isDead = true;
  }

  @Override
  protected @Nonnull EnumParticleTypes getParticleType() {
    return EnumParticleTypes.BLOCK_CRACK;
  }

  @Override
  protected boolean spawnCustomParticles() {
    int i = this.getSlimeSize();
    for (int j = 0; j < i * 8; ++j) {
      float f = this.rand.nextFloat() * ((float) Math.PI * 2F);
      float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
      float f2 = MathHelper.sin(f) * i * 0.5F * f1;
      float f3 = MathHelper.cos(f) * i * 0.5F * f1;
      EnumParticleTypes enumparticletypes = this.getParticleType();
      double d0 = this.posX + f2;
      double d1 = this.posZ + f3;
      world.spawnParticle(enumparticletypes, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D, Block.getStateId(Blocks.BEDROCK.getDefaultState()));
    }
    return true;
  }

  @Override
  protected @Nonnull EntitySlime createInstance() {
    return new EntityVoidSlime(this.world);
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
  @SideOnly(Side.CLIENT)
  public int getBrightnessForRender() {
    int i = MathHelper.floor(this.posX);
    int j = MathHelper.floor(this.posZ);

    if (!world.isAirBlock(new BlockPos(i, 0, j))) {
      double d0 = (getEntityBoundingBox().maxY - getEntityBoundingBox().minY) * 0.66D;
      int k = MathHelper.floor(this.posY - getYOffset() + d0);
      return world.getCombinedLight(new BlockPos(i, k, j), 0);
    } else {
      return 0;
    }
  }

  @Override
  public float getBrightness() {
    int i = MathHelper.floor(this.posX);
    int j = MathHelper.floor(this.posZ);

    if (!world.isAirBlock(new BlockPos(i, 0, j))) {
      double d0 = (getEntityBoundingBox().maxY - getEntityBoundingBox().minY) * 0.66D;
      int k = MathHelper.floor(this.posY - getYOffset() + d0);
      return world.getLightBrightness(new BlockPos(i, k, j));
    } else {
      return 0.0F;
    }
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
  }

  @Override
  protected int getAttackStrength() {
    int res = (int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
    return res;
  }

  // This is called every tick on onUpdate(), so avoid moving the slime around twice per tick.
  @Override
  protected void setSize(float p_70105_1_, float p_70105_2_) {
    int i = this.getSlimeSize();
    super.setSize(i, i);
  }

  @Override
  public void onCollideWithPlayer(@Nonnull EntityPlayer p_70100_1_) {
    int i = getSlimeSize();
    if (canEntityBeSeen(p_70100_1_) && this.getDistanceSq(p_70100_1_) < (double) i * (double) i
        && p_70100_1_.attackEntityFrom(DamageSource.causeMobDamage(this), getAttackStrength())) {
      playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
    }
  }

  @Override
  protected float applyArmorCalculations(@Nonnull DamageSource p_70655_1_, float p_70655_2_) {
    if (!p_70655_1_.isUnblockable()) {
      return Math.min(Math.max(p_70655_2_ - 3 - this.getSlimeSize(), this.getSlimeSize()) / 2, p_70655_2_);
    }
    return p_70655_2_;
  }

  // Object because of sidedness
  private final @Nonnull List<Object> particles = new ArrayList<>();
  private int actionDelay = 0;

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    if (!dead) {
      if (world.isRemote) {
        onLivingUpdateClient();
      } else {
        onLivingUpdateServer();
      }
    }
  }

  private void onLivingUpdateServer() {
    if (actionDelay-- <= 0) {
      for (EntityPlayer player : getClosestPlayers(8)) {
        player.addPotionEffect(new BlindEffect());
        // System.out.println(player);
      }
      actionDelay = (int) (20 * (.5f + .5f * rand.nextFloat()));
    }
  }

  @SideOnly(Side.CLIENT)
  private void onLivingUpdateClient() {
    if (actionDelay-- <= 0) {
      for (Iterator<Object> i = particles.iterator(); i.hasNext();) {
        if (!((InfinityParticle) i.next()).isAlive()) {
          i.remove();
        }
      }
      if (particles.size() < 10) {
        float offsetX = (-5f + 10f * rand.nextFloat());
        float offsetY = (-5f + 10f * rand.nextFloat());
        float offsetZ = (-5f + 10f * rand.nextFloat());
        float maxSize = rand.nextFloat() * (rand.nextBoolean() ? 16.1f : 3.9f);// 8 * (.25f + .75f * rand.nextFloat()) * 2;
        float color = rand.nextBoolean() ? 0 : rand.nextFloat() / 10;
        final InfinityParticle particle = new InfinityParticle(world, BlockCoord.get(this), new Vector4f(color, color, color, 1f),
            new Vector4f(offsetX, offsetY, offsetZ, maxSize));
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        particles.add(particle);
        particle.setMaxAge(20 * 10);
        actionDelay = (int) (20 * (.5f + .5f * rand.nextFloat()));
      }

    }
  }

  public List<EntityPlayer> getClosestPlayers(double distance) {
    Predicate<Entity> predicate = EntitySelectors.NOT_SPECTATING;
    List<EntityPlayer> entityplayers = null;
    final double distanceSq = distance * distance;

    for (EntityPlayer entityplayer : world.playerEntities) {
      if (predicate.apply(entityplayer)) {
        if ((distance < 0.0D || entityplayer.getDistanceSq(posX, posY, posZ) < distanceSq)) {
          if (entityplayers == null) {
            entityplayers = new ArrayList<>();
          }
          entityplayers.add(entityplayer);
        }
      }
    }

    return entityplayers != null ? entityplayers : Collections.emptyList();
  }

  private class BlindEffect extends PotionEffect {

    private boolean combined = false;

    public BlindEffect() {
      super(MobEffects.BLINDNESS, 100, 0, true, false);
    }

    @Override
    public boolean onUpdate(@Nonnull EntityLivingBase entityplayer) {
      if (combined || entityplayer.getDistanceSq(posX, posY, posZ) < 8 * 8) {
        return super.onUpdate(entityplayer);
      } else {
        return false;
      }
    }

    @Override
    public void combine(@Nonnull PotionEffect other) {
      if (!(other instanceof BlindEffect)) {
        // we got combined with a normal blindness effect. this means we should no longer vanish when the player gets out of range.
        combined = true;
      }
      super.combine(other);
    }

  }

}
