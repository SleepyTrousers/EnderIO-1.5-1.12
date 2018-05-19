package crazypants.enderio.base.block.charge;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.config.ZooConfig;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnderCharge extends BlockConfusionCharge {

  public static BlockEnderCharge create(@Nonnull IModObject modObject) {
    return new BlockEnderCharge(modObject);
  }

  public BlockEnderCharge(@Nonnull IModObject modObject) {
    super(modObject);
  }

  @Override
  public void explode(@Nonnull EntityPrimedCharge entity) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketExplodeEffect(entity, this), new BlockPos(entity), entity.world);
    doEntityTeleport(entity);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void explodeEffect(@Nonnull World world, double x, double y, double z) {
    world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D);
    doTeleportEffect(world, x, y, z);
  }

  protected static @Nonnull AxisAlignedBB getBoundsAround(Entity entity, double range) {
    return getBoundsAround(entity.posX, entity.posY, entity.posZ, range);
  }

  public static void doEntityTeleport(EntityPrimedCharge entity) {
    World world = entity.getEntityWorld();
    world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1F,
        1.4f + ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F));
    world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 2F,
        1 + ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F));

    AxisAlignedBB bb = getBoundsAround(entity, ZooConfig.enderChargeRange.get());
    List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
    for (EntityLivingBase ent : ents) {
      if (ent != null) {
        RandomTeleportUtil.teleportEntity(world, ent, false);
      }
    }
  }

  @SideOnly(Side.CLIENT)
  public static void doTeleportEffect(World world, double x, double y, double z) {
    Random random = world.rand;
    for (int i = 0; i < 100; ++i) {
      double d = random.nextDouble() * 2D;
      double mag = 2;
      double motionX = (0.5 - random.nextDouble()) * mag * d;
      double motionY = (0.5 - random.nextDouble()) * mag;
      double motionZ = (0.5 - random.nextDouble()) * mag * d;
      Particle entityfx = new ParticlePortal.Factory().createParticle(i, world, x + motionX * 0.1, y + motionY * 0.1, z + motionZ * 0.1, motionX, motionY,
          motionZ);
      Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
    }
  }

}
