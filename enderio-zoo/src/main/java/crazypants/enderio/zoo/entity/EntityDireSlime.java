package crazypants.enderio.zoo.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autoconfig.factory.IValue;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.render.RenderDireSlime;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityDireSlime extends EntityMagmaCube implements IEnderZooMob {

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    IEnderZooMob.register(event, NAME, EntityDireSlime.class, EGG_BG_COL, EGG_FG_COL, MobID.DLIME);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPreInit(EnderIOLifecycleEvent.PreInit event) {
    RenderingRegistry.registerEntityRenderingHandler(EntityDireSlime.class, RenderDireSlime.FACTORY);
  }

  public static final @Nonnull String NAME = "direslime";
  public static final int EGG_BG_COL = 0xb9855c;
  public static final int EGG_FG_COL = 0x593d29;

  public enum SlimeConf {

    SMALL(1, ZooConfig.direSlime1Health, ZooConfig.direSlime1AttackDamage, ZooConfig.direSlime1Chance),

    MEDIUM(2, ZooConfig.direSlime2Health, ZooConfig.direSlime2AttackDamage, ZooConfig.direSlime2Chance),

    LARGE(4, ZooConfig.direSlime3Health, ZooConfig.direSlime3AttackDamage, ZooConfig.direSlime3Chance);

    public final int size;
    public final @Nonnull IValue<Float> health;
    public final @Nonnull IValue<Float> attackDamage;
    public final @Nonnull IValue<Float> chance;

    private SlimeConf(int size, @Nonnull IValue<Float> health, @Nonnull IValue<Float> attackDamage, @Nonnull IValue<Float> chance) {
      this.size = size;
      this.health = health;
      this.attackDamage = attackDamage;
      this.chance = chance;
    }

    static SlimeConf getConfForSize(int size) {
      for (SlimeConf conf : values()) {
        if (conf.size == size) {
          return conf;
        }
      }
      return SMALL;
    }

    SlimeConf bigger() {
      int index = ordinal() + 1;
      if (index >= values().length) {
        return null;
      }
      return values()[index];
    }

    public float getAttackDamage() {
      return attackDamage.get();
    }

    public float getHealth() {
      return health.get();
    }

    public float getChance() {
      return chance.get();
    }
  }

  public EntityDireSlime(World world) {
    super(world);
    setSlimeSize(1, false);
  }

  @Override
  public void setSlimeSize(int size, boolean doFullHeal) {
    super.setSlimeSize(size, doFullHeal);
    SlimeConf conf = SlimeConf.getConfForSize(size);
    getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(conf.getAttackDamage());
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(conf.getHealth());
    setHealth(getMaxHealth());
  }

  @Override
  public void onDeath(@Nonnull DamageSource damageSource) {
    super.onDeath(damageSource);
    if (!world.isRemote && damageSource.getTrueSource() instanceof EntityPlayer) {
      SlimeConf nextConf = SlimeConf.getConfForSize(getSlimeSize()).bigger();
      if (nextConf != null && world.rand.nextFloat() <= nextConf.getChance()) {
        EntityDireSlime spawn = new EntityDireSlime(world);
        spawn.setSlimeSize(nextConf.size, true);
        spawn.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
        spawn.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(this)), null);
        if (SpawnUtil.isSpaceAvailableForSpawn(world, spawn, false)) {
          world.spawnEntity(spawn);
        }
      }
    }
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
      world.spawnParticle(enumparticletypes, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D, Block.getStateId(Blocks.DIRT.getDefaultState()));
    }
    return true;
  }

  @Override
  protected @Nonnull EntitySlime createInstance() {
    return new EntityDireSlime(this.world);
  }

  @Override
  protected @Nonnull Item getDropItem() {
    return this.getSlimeSize() == 4 ? Item.getItemFromBlock(Blocks.CLAY) : Items.CLAY_BALL;
  }

  @Override
  @Nullable
  protected ResourceLocation getLootTable() {
    return null; // use getDropItem() instead
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

}
