package crazypants.enderio.machines.machine.slicensplice;

import java.awt.Color;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.machines.config.config.ClientConfig;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSliceAndSplice extends AbstractPoweredTaskBlock<TileSliceAndSplice>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockSliceAndSplice create(@Nonnull IModObject modObject) {
    BlockSliceAndSplice result = new BlockSliceAndSplice(modObject);
    result.init();
    return result;
  }

  protected BlockSliceAndSplice(@Nonnull IModObject modObject) {
    super(modObject);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileSliceAndSplice te) {
    return new ContainerSliceAndSplice(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileSliceAndSplice te) {
    return new GuiSliceAndSplice(player.inventory, te);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    TileSliceAndSplice te = getTileEntity(world, pos);
    if (PersonalConfig.machineParticlesEnabled.get() && te != null && isActive(world, pos)) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      EnumFacing front = te.getFacing();

      int count = rand.nextInt(3) + 2;
      for (int i = 0; i < count; i++) {
        double px = x + 0.5 + front.getFrontOffsetX() * 0.5;
        double pz = z + 0.5 + front.getFrontOffsetZ() * 0.5;
        double v = 0.1;
        double vx = 0;
        double vz = 0;

        if (front == EnumFacing.NORTH || front == EnumFacing.SOUTH) {
          px += rand.nextFloat() * 0.6 - 0.275;
          vz += front == EnumFacing.NORTH ? -v : v;
        } else {
          pz += rand.nextFloat() * 0.6 - 0.275;
          vx += front == EnumFacing.WEST ? -v : v;
        }

        if (rand.nextFloat() > 0.3f) {
          Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), px, y + 0.7, pz, vx, 0, vz,
              0);
          if (fx != null) {
            float[] colors = new float[3];
            // Convert a dark unsaturated red to HSB
            Color.RGBtoHSB(80, 20, 20, colors);
            // Randomly lower saturation
            colors[1] = Math.max(0, colors[1] + (rand.nextFloat() * 0.4f) - 0.4f);
            // Randomly perturb the brightness slightly
            colors[2] += (rand.nextFloat() * 0.2f) - 0.1f;
            // Reassemble to RGB
            Color color = Color.getHSBColor(colors[0], colors[1], colors[2]);
            if (ClientConfig.bloodEnabled.get()) {
              fx.setRBGColorF(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
            } else {
              fx.setRBGColorF(color.getGreen() / 255f, color.getRed() / 255f, color.getBlue() / 255f);
            }
            fx.multiplyVelocity(0.25f);
          }
        } else {
          Minecraft.getMinecraft().effectRenderer
              .addEffect(new ParticleBloodDrip(world, px + front.getFrontOffsetX() * 0.1, y + 0.325, pz + front.getFrontOffsetZ() * 0.1, front));
        }
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.SOUL_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.SOUL_MAPPER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileSliceAndSplice tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
