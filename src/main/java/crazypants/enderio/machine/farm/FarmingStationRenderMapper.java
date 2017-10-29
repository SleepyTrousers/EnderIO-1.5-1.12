package crazypants.enderio.machine.farm;

import java.util.Collections;
import java.util.List;

import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.machine.render.MachineRenderMapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.property.EnumRenderMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FarmingStationRenderMapper extends MachineRenderMapper implements IRenderMapper.IBlockRenderMapper.IRenderLayerAware {

  public static final FarmingStationRenderMapper instance = new FarmingStationRenderMapper();

  protected FarmingStationRenderMapper() {
    super(null);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, AbstractMachineEntity tileEntity,
                                     AbstractMachineBlock<?> block) {
    if (blockLayer == BlockRenderLayer.TRANSLUCENT) {
      if (tileEntity.isActive()) {
        return Collections.singletonList(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON));
      }
    } else if (blockLayer == BlockRenderLayer.SOLID) {
      return Collections.singletonList(block.getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT));
    }
    return null;
  }

}
