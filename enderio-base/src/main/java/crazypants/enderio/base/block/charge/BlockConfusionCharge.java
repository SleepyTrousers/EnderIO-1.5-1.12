package crazypants.enderio.base.block.charge;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.config.ZooConfig;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockConfusionCharge extends BlockTNT implements ICharge, IDefaultRenderers, IModObject.WithBlockItem {

  public static BlockConfusionCharge create(@Nonnull IModObject modObject) {
    return new BlockConfusionCharge(modObject);
  }

  public BlockConfusionCharge(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIO);
    modObject.apply(this);
    ChargeRegister.instance.registerCharge(this);
  }

  private int chargeId;

  @Override
  public int getID() {
    return chargeId;
  }

  @Override
  public void setID(int id) {
    chargeId = id;
  }

  @Override
  public @Nonnull Block getBlock() {
    return this;
  }

  @Override
  public void explode(@Nonnull EntityPrimedCharge entity) {
    World world = entity.getEntityWorld();

    world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 3F,
        1.4f + ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F));

    PacketHandler.INSTANCE.sendToAllAround(new PacketExplodeEffect(entity, this), new BlockPos(entity), entity.world);
  }

  @Override
  public void explode(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase igniter) {
    if (!world.isRemote) {
      if (state.getValue(EXPLODE).booleanValue()) {
        EntityPrimedCharge entity = new EntityPrimedCharge(this, world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, igniter);
        world.spawnEntity(entity);
        world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1, 1);

        world.updateEntity(entity);
      }
    }
  }

  protected static @Nonnull AxisAlignedBB getBoundsAround(double x, double y, double z, double range) {
    return new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void explodeEffect(@Nonnull World world, double x, double y, double z) {

    List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, getBoundsAround(x, y, z, ZooConfig.confusingChargeRange.get()));

    double maxDistanceSq = ZooConfig.confusingChargeRange.get() * ZooConfig.confusingChargeRange.get();
    for (EntityPlayer player : players) {
      double playerDistSq = player.getDistanceSq(x, y, z);
      if (playerDistSq < maxDistanceSq) {
        double scale = 1 - playerDistSq / maxDistanceSq;
        scale = Math.exp(scale) / Math.E;
        int duration = (int) Math.ceil(ZooConfig.confusingChargeEffectDuration.get() * scale);
        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, false, true));
      }
    }

    // world.spawnParticle("hugeexplosion", x, y, z, 1.0D, 0.0D, 0.0D);
    world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D);

    int col = MobEffects.NAUSEA.getLiquidColor();
    float r = (col >> 16 & 255) / 255.0F;
    float g = (col >> 8 & 255) / 255.0F;
    float b = (col >> 0 & 255) / 255.0F;
    Random random = world.rand;
    for (int i = 0; i < 100; ++i) {
      // double seed = random.nextDouble() * 20.0D;

      double d = random.nextDouble() * 2D;
      double mag = 25;
      double motionX = (0.5 - random.nextDouble()) * mag * d;
      double motionY = (0.5 - random.nextDouble()) * mag;
      double motionZ = (0.5 - random.nextDouble()) * mag * d;

      ParticleSpell entityfx = (ParticleSpell) new ParticleSpell.InstantFactory().createParticle(i, world, x + motionX * 0.1, y + motionY * 0.1,
          z + motionZ * 0.1, motionX, motionY, motionZ);
      float colRan = 0.75F + random.nextFloat() * 0.25F;
      entityfx.setRBGColorF(r * colRan, g * colRan, b * colRan);
      entityfx.multiplyVelocity(0.1f);
      Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);

    }

  }

  @Override
  public void onBlockDestroyedByExplosion(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
    if (!world.isRemote) {
      EntityLivingBase placedBy = explosion.getExplosivePlacedBy();
      onIgnitedByNeighbour(world, pos.getX(), pos.getY(), pos.getZ(), placedBy);
    }
  }

  protected void onIgnitedByNeighbour(World world, int x, int y, int z, EntityLivingBase placedBy) {
    EntityPrimedCharge entity = new EntityPrimedCharge(this, world, x + 0.5F, y + 0.5F, z + 0.5F, placedBy);
    entity.setFuse(world.rand.nextInt(entity.getFuse() / 4) + entity.getFuse() / 8);
    world.spawnEntity(entity);
  }

  @Override
  public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    return true;
  }
}
