package crazypants.enderio.base.paint.render;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.paint.IPaintable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PaintHelper {

  @SideOnly(Side.CLIENT)
  private static TextureAtlasSprite lastTexture;

  @SideOnly(Side.CLIENT)
  private static final Random rand = new Random();

  @SideOnly(Side.CLIENT)
  public static boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target,
      @Nonnull ParticleManager effectRenderer) {
    if (state.getBlock() instanceof IPaintable) {
      BlockPos pos = target.getBlockPos();
      IBlockState paintSource = ((IPaintable) state.getBlock()).getPaintSource(state, world, pos);
      if (paintSource != null) {
        addBlockHitEffects(world, state, paintSource, pos, target.sideHit, effectRenderer);
        lastTexture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(paintSource);
        return true;
      }
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  public static void addBlockHitEffects(@Nonnull World world, @Nonnull IBlockState realBlock, @Nonnull IBlockState paintBlock, @Nonnull BlockPos pos,
      @Nonnull EnumFacing side, @Nonnull ParticleManager effectRenderer) {
    int i = pos.getX();
    int j = pos.getY();
    int k = pos.getZ();
    float f = 0.1F;
    AxisAlignedBB axisalignedbb = realBlock.getBoundingBox(world, pos);
    double d0 = i + rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - f * 2.0F) + f + axisalignedbb.minX;
    double d1 = j + rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - f * 2.0F) + f + axisalignedbb.minY;
    double d2 = k + rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - f * 2.0F) + f + axisalignedbb.minZ;

    switch (side) {
    case DOWN:
      d1 = j + axisalignedbb.minY - f;
      break;
    case UP:
      d1 = j + axisalignedbb.maxY + f;
      break;
    case NORTH:
      d2 = k + axisalignedbb.minZ - f;
      break;
    case SOUTH:
      d2 = k + axisalignedbb.maxZ + f;
      break;
    case WEST:
      d0 = i + axisalignedbb.minX - f;
      break;
    case EAST:
      d0 = i + axisalignedbb.maxX + f;
      break;
    }

    // this state sets the gravity and texture. this pos sets the lighting, so we better use our block pos
    Particle digFX = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), i, j, k, 0, 0, 0,
        Block.getStateId(paintBlock));
    if (digFX instanceof ParticleDigging) {
      // this pos sets the tint...wrongly
      ((ParticleDigging) digFX).setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
      // manually fixing the tint
      if (paintBlock.getBlock() == Blocks.GRASS) {
        digFX.setRBGColorF(0.6f, 0.6f, 0.6f);
      } else {
        int tint = Minecraft.getMinecraft().getBlockColors().colorMultiplier(paintBlock, world, pos, 0);
        float particleRed = 0.6f * (tint >> 16 & 255) / 255.0F;
        float particleGreen = 0.6f * (tint >> 8 & 255) / 255.0F;
        float particleBlue = 0.6f * (tint & 255) / 255.0F;
        digFX.setRBGColorF(particleRed, particleGreen, particleBlue);
      }
      /// and this is the pos we actually want the particle to be
      digFX.setPosition(d0, d1, d2);
      // and that's it, spawn my monkeys, spawn!
    }
  }

  @SideOnly(Side.CLIENT)
  public static boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {

    TextureAtlasSprite texture = null;
    IBlockState state = world.getBlockState(pos);
    if (state.getBlock() instanceof IPaintable) {
      IBlockState paintSource = ((IPaintable) state.getBlock()).getPaintSource(state, world, pos);
      if (paintSource != null) {
        texture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(paintSource);
      }
    }
    if (texture == null) {
      texture = lastTexture;
      if (texture == null) {
        return false;
      }
    }

    int i = 4;
    for (int j = 0; j < i; ++j) {
      for (int k = 0; k < i; ++k) {
        for (int l = 0; l < i; ++l) {
          double d0 = pos.getX() + (j + 0.5D) / i;
          double d1 = pos.getY() + (k + 0.5D) / i;
          double d2 = pos.getZ() + (l + 0.5D) / i;
          ParticleDigging fx = (ParticleDigging) new ParticleDigging.Factory().createParticle(-1, world, d0, d1, d2, d0 - pos.getX() - 0.5D,
              d1 - pos.getY() - 0.5D, d2 - pos.getZ() - 0.5D, 0);
          fx.setBlockPos(pos);
          fx.setParticleTexture(texture);
          effectRenderer.addEffect(fx);
        }
      }
    }

    return true;
  }

}
