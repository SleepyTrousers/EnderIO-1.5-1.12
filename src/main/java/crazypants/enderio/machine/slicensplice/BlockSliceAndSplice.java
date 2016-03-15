package crazypants.enderio.machine.slicensplice;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.RenderMappers;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;

public class BlockSliceAndSplice extends AbstractMachineBlock<TileSliceAndSplice> implements IPaintable.ISolidBlockPaintableBlock,
    IPaintable.IWrenchHideablePaint {

  public static BlockSliceAndSplice create() {
    BlockSliceAndSplice result = new BlockSliceAndSplice();
    result.init();
    return result;
  }

  protected BlockSliceAndSplice() {
    super(ModObject.blockSliceAndSplice, TileSliceAndSplice.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileSliceAndSplice) {
      return new ContainerSliceAndSplice(player.inventory, (TileSliceAndSplice) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileSliceAndSplice) {
      return new GuiSliceAndSplice(player.inventory, (TileSliceAndSplice) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_SLICE_N_SPLICE;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    TileSliceAndSplice te = (TileSliceAndSplice) world.getTileEntity(pos);
    if(isActive(world, x, y, z) && te != null) {
      
      EnumFacing front = te.facing;

      for (int i = 0; i < 2; i++) {
        double px = x + 0.5 + front.getFrontOffsetX() * 0.6;
        double pz = z + 0.5 + front.getFrontOffsetZ() * 0.6;
        double v = 0.05;
        double vx = 0;
        double vz = 0;
        
        if(front == EnumFacing.NORTH || front == EnumFacing.SOUTH) {
          px += world.rand.nextFloat() * 0.9 - 0.45;
          vz += front == EnumFacing.NORTH ? -v : v;
        } else {
          pz += world.rand.nextFloat() * 0.9 - 0.45;
          vx += front == EnumFacing.WEST ? -v : v;
        }

        EntityFX fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), px, y + 0.5, pz, vx, 0, vz, 0);        
        if(fx != null) {
          fx.setRBGColorF(0.3f + (rand.nextFloat() * 0.1f), 0.1f + (rand.nextFloat() * 0.1f), 0.1f + (rand.nextFloat() * 0.1f));
          fx.motionX *= 0.25f;
          fx.motionY *= 0.25f;
          fx.motionZ *= 0.25f;
        }

      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper() {    
    return RenderMappers.SOUL_MAPPER;
  }

  @Override
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    BlockStateWrapper extendedState = (BlockStateWrapper) super.getExtendedState(state, world, pos);
    TileEntity tileEntity = extendedState.getTileEntity();
    if (tileEntity instanceof AbstractMachineEntity) {
      extendedState.setCacheKey(((AbstractMachineEntity) tileEntity).getFacing(), ((AbstractMachineEntity) tileEntity).isActive());
    }
    return extendedState;
  }

}
