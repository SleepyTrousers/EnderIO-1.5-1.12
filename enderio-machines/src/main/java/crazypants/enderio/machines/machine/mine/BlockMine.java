package crazypants.enderio.machines.machine.mine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractCapabilityPoweredMachineBlock;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMine extends AbstractCapabilityPoweredMachineBlock<TileMine> implements ISmartRenderAwareBlock, IResourceTooltipProvider {

  public static BlockMine create(@Nonnull IModObject modObject) {
    BlockMine mine = new BlockMine(modObject);
    mine.init();
    return mine;
  }

  public BlockMine(@Nonnull IModObject mo) {
    super(mo);
    setShape(mkShape(BlockFaceShape.UNDEFINED));
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileMine tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.isActive());
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileMine te) {
    return null; // return new ContainerImpulseHopper(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileMine te) {
    return null; // return new GuiImpulseHopper(player.inventory, te);
  }

}
