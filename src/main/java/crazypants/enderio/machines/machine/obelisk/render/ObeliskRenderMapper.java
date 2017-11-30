package crazypants.enderio.machines.machine.obelisk.render;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.render.MachineRenderMapper;
import crazypants.enderio.base.paint.render.PaintedBlockAccessWrapper;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import crazypants.enderio.base.render.util.QuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ObeliskRenderMapper extends MachineRenderMapper {

  public static final @Nonnull ObeliskRenderMapper instance = new ObeliskRenderMapper();

  private ObeliskRenderMapper() {
    super(null);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ItemQuadCollector itemQuadCollector) {
    // unused, see ObeliskSpecialRenderer
    itemQuadCollector.addQuads(null, ObeliskBakery.bake(ObeliskRenderManager.INSTANCE.getActiveTextures()));
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    TileEntity tileEntity = getTileEntity(state, pos);

    boolean isActive = tileEntity instanceof AbstractMachineEntity ? ((AbstractMachineEntity) tileEntity).isActive() : true;

    quadCollector.addQuads(null, BlockRenderLayer.CUTOUT,
        ObeliskBakery.bake(isActive ? ObeliskRenderManager.INSTANCE.getActiveTextures() : ObeliskRenderManager.INSTANCE.getTextures()));

    return null;
  }

  private TileEntity getTileEntity(@Nonnull IBlockStateWrapper state, @Nonnull BlockPos pos) {
    IBlockAccess world = state.getWorld();
    if (world instanceof PaintedBlockAccessWrapper) {
      TileEntity te = ((PaintedBlockAccessWrapper) world).getRealTileEntity(pos);
      if (te instanceof AbstractMachineEntity) {
        return te;
      }
    }
    return state.getTileEntity();
  }

}
