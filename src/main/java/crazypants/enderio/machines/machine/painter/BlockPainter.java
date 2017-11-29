package crazypants.enderio.machines.machine.painter;

import javax.annotation.Nonnull;

import crazypants.enderio.GuiID;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.painter.EveryPaintableRecipe;
import crazypants.enderio.render.IBlockStateWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPainter extends AbstractMachineBlock<TileEntityPainter> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockPainter create(@Nonnull IModObject modObject) {
    BlockPainter painter = new BlockPainter(modObject);
    painter.init();
    return painter;
  }

  private BlockPainter(@Nonnull IModObject modObject) {
    super(modObject, TileEntityPainter.class);
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected void init() {
    super.init();
    MachineRecipeRegistry.instance.enableRecipeSorting(MachineObject.block_painter.getUnlocalisedName());
    MachineRecipeRegistry.instance.registerRecipe(MachineObject.block_painter.getUnlocalisedName(), new EveryPaintableRecipe());
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileEntityPainter) {
      return new ContainerPainter(player.inventory, (TileEntityPainter) te);
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileEntityPainter) {
      return new GuiPainter(player.inventory, (TileEntityPainter) te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_PAINTER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileEntityPainter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
