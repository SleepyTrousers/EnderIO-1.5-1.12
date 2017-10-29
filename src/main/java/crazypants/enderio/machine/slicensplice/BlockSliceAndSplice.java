package crazypants.enderio.machine.slicensplice;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.GuiID;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.machine.render.RenderMappers;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
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

public class BlockSliceAndSplice extends AbstractMachineBlock<TileSliceAndSplice> implements IPaintable.ISolidBlockPaintableBlock,
    IPaintable.IWrenchHideablePaint {

  public static BlockSliceAndSplice create() {
    BlockSliceAndSplice result = new BlockSliceAndSplice();
    result.init();
    return result;
  }

  protected BlockSliceAndSplice() {
    super(MachineObject.blockSliceAndSplice, TileSliceAndSplice.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileSliceAndSplice te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerSliceAndSplice(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileSliceAndSplice te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiSliceAndSplice(player.inventory, te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_SLICE_N_SPLICE;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
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

        Particle fx = Minecraft.getMinecraft().effectRenderer
            .spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), px, y + 0.5, pz, vx, 0, vz, 0);
        if (fx != null) {
          fx.setRBGColorF(0.3f + (rand.nextFloat() * 0.1f), 0.1f + (rand.nextFloat() * 0.1f), 0.1f + (rand.nextFloat() * 0.1f));
          fx.multiplyVelocity(0.25f);
        }

      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
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
