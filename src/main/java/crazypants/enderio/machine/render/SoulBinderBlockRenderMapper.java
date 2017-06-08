package crazypants.enderio.machine.render;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.init.ModObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.machine.interfaces.ISoulBinder;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.property.EnumRenderPart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SoulBinderBlockRenderMapper extends MachineRenderMapper {

  public static final SoulBinderBlockRenderMapper instance = new SoulBinderBlockRenderMapper();

  private SoulBinderBlockRenderMapper() {
    super(EnumRenderPart.SOUL_FRAME);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, AbstractMachineEntity tileEntity,
                                     AbstractMachineBlock<?> block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    EnumFacing facing = tileEntity.getFacing();
    boolean active = (tileEntity instanceof ISoulBinder) ? ((ISoulBinder) tileEntity).isWorking() : tileEntity.isActive();

    states.add(ModObject.block_machine_base.getBlockNN().getDefaultState().withProperty(EnumRenderPart.SUB, body.rotate(facing)));

    if (!active) {
      states.add(state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT.rotate(facing)));
    }

    return states;
  }

}
