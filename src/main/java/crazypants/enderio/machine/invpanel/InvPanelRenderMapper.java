package crazypants.enderio.machine.invpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.render.EnumRenderMode6;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IOMode.EnumIOMode;
import crazypants.enderio.render.dummy.BlockMachineIO;

public class InvPanelRenderMapper extends MachineRenderMapper {

  public static final MachineRenderMapper instance = new InvPanelRenderMapper();

  public InvPanelRenderMapper() {
    super(null);
  }

  @Override
  protected Pair<List<IBlockState>, List<IBakedModel>> render(IBlockState state, IBlockAccess world, BlockPos pos,
      TileEntity tileEntity, Block block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    EnumFacing facing = ((AbstractMachineEntity) tileEntity).getFacing();
    boolean active = ((AbstractMachineEntity) tileEntity).isActive();

    if (active) {
      states.add(state.withProperty(EnumRenderMode6.RENDER, EnumRenderMode6.FRONT_ON.rotate(facing)));
    } else {
      states.add(state.withProperty(EnumRenderMode6.RENDER, EnumRenderMode6.FRONT.rotate(facing)));
    }

    return Pair.of(states, null);
  }

  @Override
  protected List<IBlockState> renderIO(TileEntity tileEntity, Block block) {
    EnumFacing face = ((AbstractMachineEntity) tileEntity).getFacing().getOpposite();
    IoMode ioMode = ((AbstractMachineEntity) tileEntity).getIoMode(face);
    if (ioMode != IoMode.NONE) {
      @SuppressWarnings("rawtypes")
      EnumIOMode iOMode = ((AbstractMachineBlock) block).mapIOMode(ioMode, face);
      return Collections.singletonList(BlockMachineIO.block.getDefaultState().withProperty(IOMode.IO, IOMode.get(face, iOMode)));
    }
    return null;
  }

  @Override
  public Pair<List<IBlockState>, List<IBakedModel>> mapBlockRender(Block block, ItemStack stack) {
    List<IBlockState> states = new ArrayList<IBlockState>();
    states.add(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode6.RENDER, EnumRenderMode6.FRONT_ON_NORTH));
    return Pair.of(states, null);
  }


}
