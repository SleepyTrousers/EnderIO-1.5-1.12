package crazypants.enderio.machines.machine.painter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskBlock;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.EveryPaintableRecipe;
import crazypants.enderio.base.render.IBlockStateWrapper;
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

public class BlockPainter extends AbstractPoweredTaskBlock<TileEntityPainter> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockPainter create(@Nonnull IModObject modObject) {
    PersonalConfig.TooltipPaintEnum.setPainterAvailable();
    BlockPainter painter = new BlockPainter(modObject);
    painter.init();
    return painter;
  }

  private BlockPainter(@Nonnull IModObject modObject) {
    super(modObject);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected void init() {
    super.init();
    MachineRecipeRegistry.instance.enableRecipeSorting(MachineRecipeRegistry.PAINTER);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new EveryPaintableRecipe());
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileEntityPainter te) {
    return new ContainerPainter(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileEntityPainter te) {
    return new GuiPainter(player.inventory, te);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileEntityPainter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
