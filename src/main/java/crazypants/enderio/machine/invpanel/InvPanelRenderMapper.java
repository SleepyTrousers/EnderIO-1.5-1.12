package crazypants.enderio.machine.invpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.render.MachineRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode6;
import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import crazypants.enderio.base.render.util.ItemQuadCollector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InvPanelRenderMapper extends MachineRenderMapper {

  public static final MachineRenderMapper instance = new InvPanelRenderMapper();

  public InvPanelRenderMapper() {
    super(null);
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected List<IBlockState> render(IBlockState state, IBlockAccess world, BlockPos pos, BlockRenderLayer blockLayer, AbstractMachineEntity tileEntity,
                                     AbstractMachineBlock<?> block) {
    List<IBlockState> states = new ArrayList<IBlockState>();

    EnumFacing facing = tileEntity.getFacing();
    boolean active = tileEntity.isActive();

    if (active) {
      states.add(state.withProperty(EnumRenderMode6.RENDER, EnumRenderMode6.FRONT_ON.rotate(facing)));
    } else {
      states.add(state.withProperty(EnumRenderMode6.RENDER, EnumRenderMode6.FRONT.rotate(facing)));
    }

    return states;
  }

  @Override
  @SideOnly(Side.CLIENT)
  protected EnumMap<EnumFacing, EnumIOMode> renderIO(@Nonnull AbstractMachineEntity tileEntity, @Nonnull AbstractMachineBlock<?> block) {
    EnumMap<EnumFacing, EnumIOMode> result = new EnumMap<EnumFacing, EnumIOMode>(EnumFacing.class);
    EnumFacing face = tileEntity.getFacing().getOpposite();
    IoMode ioMode = tileEntity.getIoMode(face);
    if (ioMode != IoMode.NONE) {
      result.put(face, block.mapIOMode(ioMode, face));
      return result;
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<Pair<IBlockState, ItemStack>> mapItemRender(Block block, ItemStack stack, ItemQuadCollector itemQuadCollector) {
    return Collections.singletonList(Pair.of(block.getStateFromMeta(stack.getMetadata()).withProperty(EnumRenderMode6.RENDER, EnumRenderMode6.FRONT_ON_NORTH),
        stack));
  }


}
