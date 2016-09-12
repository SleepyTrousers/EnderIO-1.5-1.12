package crazypants.enderio.machine.obelisk.render;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.util.ItemQuadCollector;
import crazypants.enderio.render.util.QuadCollector;
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

  public static ObeliskRenderMapper instance = new ObeliskRenderMapper();

  private ObeliskRenderMapper() {
    super(null);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack, ItemQuadCollector itemQuadCollector) {
    // unused, see ObeliskSpecialRenderer
    itemQuadCollector.addQuads(null, ObeliskBakery.bake(ObeliskRenderManager.INSTANCE.getActiveTextures()));
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(IBlockStateWrapper state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer,
      QuadCollector quadCollector) {
    TileEntity tileEntity = state.getTileEntity();

    boolean isActive = tileEntity instanceof AbstractMachineEntity ? ((AbstractMachineEntity) tileEntity).isActive() : true;

    quadCollector.addQuads(null, BlockRenderLayer.CUTOUT,
        ObeliskBakery.bake(isActive ? ObeliskRenderManager.INSTANCE.getActiveTextures() : ObeliskRenderManager.INSTANCE.getTextures()));

    return null;
  }

}
