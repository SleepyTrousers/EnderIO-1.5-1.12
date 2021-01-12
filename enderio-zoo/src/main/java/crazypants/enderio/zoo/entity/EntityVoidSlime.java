package crazypants.enderio.zoo.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.base.Predicate;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.loot.EntityLootHelper;
import crazypants.enderio.base.render.ranged.InfinityParticle;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.render.RenderVoidSlime;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class EntityVoidSlime extends EntityMagmaCube implements IEnderZooEntity.Aggressive {

  private static final @Nonnull EntityLiving.SpawnPlacementType ON_BEDROCK = NullHelper.first(EnumHelper.addSpawnPlacementType("ON_BEDROCK",
      (world, pos) -> pos != null && pos.getY() < 32 && world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK
          && WorldEntitySpawner.isValidEmptySpawnBlock(world.getBlockState(pos)) && WorldEntitySpawner.isValidEmptySpawnBlock(world.getBlockState(pos.up()))
          && !(world instanceof World && ((World) world).canBlockSeeSky(pos))),
      EntityLiving.SpawnPlacementType.ON_GROUND);

  @SubscribeEvent
  public static void onEntityRegister(@Nonnull Register<EntityEntry> event) {
    LootTableList.register(new ResourceLocation(EnderIOZoo.DOMAIN, NAME));
    IEnderZooEntity.register(event, NAME, EntityVoidSlime.class, EGG_BG_COL, EGG_FG_COL, MobID.VSLIME);
    EntitySpawnPlacementRegistry.setPlacementType(EntityVoidSlime.class, ON_BEDROCK);
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
    super.setSlimeSize(1, doFullHeal);
    getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ZooConfig.voidSlimeAttackDamage.get());
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(ZooConfig.voidSlimeHealth.get());
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
    return (int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
  }

  // This is called every tick on onUpdate(), so avoid moving the slime around twice per tick.
  @Override
  protected void setSize(float p_70105_1_, float p_70105_2_) {
    super.setSize(1, 1);
  }

  @Override
  public void onCollideWithPlayer(@Nonnull EntityPlayer player) {
    if (canEntityBeSeen(player) && player.attackEntityFrom(DamageSource.causeMobDamage(this), getAttackStrength())) {
      playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
    }
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
      for (EntityPlayer player : getClosestPlayers(getMaxDistSq())) {
        player.addPotionEffect(new BlindEffect(distanceSq2Byte(player.getDistanceSq(posX, posY, posZ))));
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
        float maxSize = rand.nextFloat() * (rand.nextBoolean() ? (2 * ZooConfig.voidSlimeRange.get() + 0.1f) : 3.9f);
        float color = rand.nextBoolean() ? 0 : rand.nextFloat() / 10;
        final InfinityParticle particle = new InfinityParticle(world, BlockCoord.get(this), new Vector4f(color, color, color, 1f),
            new Vector4f(offsetX, offsetY, offsetZ, maxSize));
        particle.setMaxAge(20 * 10);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        particles.add(particle);
        actionDelay = (int) (20 * (.5f + .5f * rand.nextFloat()));
      }

    }
  }

  /**
   * Get a list of all players in range. Derived from {@link World#getClosestPlayer(double, double, double, double, Predicate)}.
   */
  public List<EntityPlayer> getClosestPlayers(double distanceSq) {
    List<EntityPlayer> entityplayers = null;

    for (EntityPlayer entityplayer : world.playerEntities) {
      if (EntitySelectors.NOT_SPECTATING.apply(entityplayer)) {
        if (entityplayer.getDistanceSq(posX, posY, posZ) < distanceSq) {
          if (entityplayers == null) {
            entityplayers = new ArrayList<>();
          }
          entityplayers.add(entityplayer);
        }
      }
    }

    return entityplayers != null ? entityplayers : Collections.emptyList();
  }

  /**
   * Server-side (logical side) subclass that keeps track of the originating mob and suicides when that mob is too far away or dies.
   */
  private class BlindEffect extends PotionEffect {

    private final Set<EntityVoidSlime> tracking = new HashSet<>();

    private int distance;

    public BlindEffect(int distance) {
      super(BLIND_POTION, 100, 0, true, false);
      this.distance = distance;
      tracking.add(EntityVoidSlime.this);
    }

    @Override
    public int getAmplifier() {
      // Note: This is what will be but into the SPacketEntityEffect so the client will get a nice normal PotionEffect with the distance as amplifier
      return distance;
    }

    @Override
    public boolean onUpdate(@Nonnull EntityLivingBase player) {
      if (!(player instanceof EntityPlayerMP)) {
        // should be impossible
        return false;
      }
      // find the distance to the closest slime
      int dist = Integer.MAX_VALUE;
      for (Iterator<EntityVoidSlime> itr = tracking.iterator(); itr.hasNext();) {
        EntityVoidSlime next = itr.next();
        if (next.isDead || next.dead) {
          // and remove dead ones
          itr.remove();
        } else {
          int d = distanceSq2Byte(player.getDistanceSq(posX, posY, posZ));
          if (d < 0) {
            // and those that are too far away
            itr.remove();
          } else {
            dist = Math.min(dist, d);
          }
        }
      }
      if (dist >= DISTS.length) {
        // no more slimes alive or in range
        return false;
      }
      if (dist != distance) {
        if (dist < distance) {
          // sending a potion update packet with a lower amplifier to the client has no effect (see super.combine()), so we first need to
          // tell the client to delete its current poten effect, then send a new one
          ((EntityPlayerMP) player).connection.sendPacket(new SPacketRemoveEntityEffect(((EntityPlayerMP) player).getEntityId(), getPotion()));
        }
        distance = dist;
        // force an update to be sent to the client, that doesn't happen by itself
        ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityEffect(((EntityPlayerMP) player).getEntityId(), this));
      }
      return true;
    }

    @Override
    public void combine(@Nonnull PotionEffect other) {
      if (other instanceof BlindEffect) {
        // in theory we could calculate distances in here...but we have no way of contacting the player from here and we need to do that to lower the
        // distance (see above). So we just put the additional tracked slimes into our list and wait for onUpdate() to find them
        tracking.addAll(((BlindEffect) other).tracking);
      }
    }

  }

  @SubscribeEvent
  public static void registerPotions(Register<Potion> event) {
    event.getRegistry().register(BLIND_POTION);
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (applied >= 0 && event.phase == Phase.END && event.player.world.isRemote && event.player == Minecraft.getMinecraft().player
        && event.player.getActivePotionEffect(BLIND_POTION) == null && Minecraft.getMinecraft().entityRenderer.isShaderActive()) {
      applied = -1;
      removeShader();
    }
  }

  @SideOnly(Side.CLIENT)
  private static void removeShader() {
    try {
      Object shaderGroup = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, "field_147707_d");
      if (shaderGroup instanceof ShaderGroup) {
        Object shaderGroupName = ObfuscationReflectionHelper.getPrivateValue(ShaderGroup.class, (ShaderGroup) shaderGroup, "field_148034_c");
        for (ResourceLocation rl : SHADER_RL) { // don't trust 'applied'
          if (rl.toString().equals(shaderGroupName)) {
            Minecraft.getMinecraft().entityRenderer.stopUseShader();
            return;
          }
        }
      }
    } catch (UnableToAccessFieldException e) {
      e.printStackTrace();
    }
  }

  private static double[] DISTS = { 0.25, 0.5, 0.75, 1, 2, 4 };
  private static final @Nonnull NNList<ResourceLocation> SHADER_RL = new NNList<>( //
      new ResourceLocation("minecraft", "shaders/post/invert.json"), new ResourceLocation("minecraft", "shaders/post/spider.json"),
      new ResourceLocation("minecraft", "shaders/post/creeper.json"), new ResourceLocation("minecraft", "shaders/post/invert.json"),
      new ResourceLocation("minecraft", "shaders/post/spider.json"), new ResourceLocation("minecraft", "shaders/post/creeper.json"));
  private static final @Nonnull BlindPotion BLIND_POTION = new BlindPotion();
  private static int applied = -1; // array index of the currently applied shader

  /**
   * @return The radius (squared) of the effect volume around a void slime
   */
  private static double getMaxDistSq() {
    double d = ZooConfig.voidSlimeRange.get() * DISTS[DISTS.length - 1];
    return d * d * 3;
  }

  /**
   * Converts a distance (squared) into an index in the shader list. -1 if the distance is bigger than the effect sphere.
   */
  private static int distanceSq2Byte(double d) {
    int ref = ZooConfig.voidSlimeRange.get();
    for (int i = 0; i < DISTS.length; i++) {
      double f = DISTS[i] * ref;
      double refd = f * f * 3;
      if (d < refd) {
        return i;
      }
    }
    return -1;
  }

  private static class BlindPotion extends Potion {

    protected BlindPotion() {
      super(true, 0);
      setPotionName("potion.enderio.void_blindness");
      setRegistryName(EnderIO.DOMAIN, "void_blindness");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
      return !EnderIO.proxy.isDedicatedServer(); // integrated server goes to isRemote check in performEffect()
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {
      if (entityLivingBaseIn.world.isRemote && entityLivingBaseIn == Minecraft.getMinecraft().player) {
        EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
        if (applied != amplifier && renderer.isShaderActive()) {
          removeShader();
        }
        if (!renderer.isShaderActive()) {
          renderer.loadShader(SHADER_RL.get(amplifier));
          applied = amplifier;
        }
      }
    }

    @Override
    public boolean shouldRender(@Nonnull PotionEffect effect) {
      return false;
    }

    @Override
    public boolean shouldRenderHUD(@Nonnull PotionEffect effect) {
      return false;
    }

    @Override
    public boolean shouldRenderInvText(@Nonnull PotionEffect effect) {
      return false;
    }

  }

}
