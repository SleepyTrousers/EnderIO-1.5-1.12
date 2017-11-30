package crazypants.enderio.machines.machine.slicensplice;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSliceAndSplice extends AbstractMachineBlock<TileSliceAndSplice>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockSliceAndSplice create(@Nonnull IModObject modObject) {
    BlockSliceAndSplice result = new BlockSliceAndSplice(modObject);
    result.init();
    return result;
  }

  protected BlockSliceAndSplice(@Nonnull IModObject modObject) {
    super(modObject, TileSliceAndSplice.class);
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileSliceAndSplice te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerSliceAndSplice(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileSliceAndSplice te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiSliceAndSplice(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_SLICE_N_SPLICE;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    TileSliceAndSplice te = getTileEntity(world, pos);
    if (te != null && isActive(world, pos)) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      EnumFacing front = te.getFacing();

      for (int i = 0; i < 2; i++) {
        double px = x + 0.5 + front.getFrontOffsetX() * 0.6;
        double pz = z + 0.5 + front.getFrontOffsetZ() * 0.6;
        double v = 0.05;
        double vx = 0;
        double vz = 0;

        if (front == EnumFacing.NORTH || front == EnumFacing.SOUTH) {
          px += world.rand.nextFloat() * 0.9 - 0.45;
          vz += front == EnumFacing.NORTH ? -v : v;
        } else {
          pz += world.rand.nextFloat() * 0.9 - 0.45;
          vx += front == EnumFacing.WEST ? -v : v;
        }

        Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), px, y + 0.5, pz, vx, 0, vz,
            0);
        if (fx != null) {
          fx.setRBGColorF(0.3f + (rand.nextFloat() * 0.1f), 0.1f + (rand.nextFloat() * 0.1f), 0.1f + (rand.nextFloat() * 0.1f));
          fx.multiplyVelocity(0.25f);
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
