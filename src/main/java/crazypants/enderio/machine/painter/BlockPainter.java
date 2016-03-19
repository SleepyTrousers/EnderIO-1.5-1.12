package crazypants.enderio.machine.painter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.recipe.EveryPaintableRecipe;
import crazypants.enderio.paint.IPaintable;

public class BlockPainter extends AbstractMachineBlock<TileEntityPainter> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockPainter create() {
    BlockPainter ppainter = new BlockPainter();
    ppainter.init();
    return ppainter;
  }

  private BlockPainter() {
    super(ModObject.blockPainter, TileEntityPainter.class);
  }

  @Override
  protected void init() {
    super.init();
    MachineRecipeRegistry.instance.enableRecipeSorting(ModObject.blockPainter.unlocalisedName);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new EveryPaintableRecipe());
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileEntityPainter) {
      return new PainterContainer(player.inventory, (TileEntityPainter) te);
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    return new GuiPainter(player.inventory, (TileEntityPainter) te);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_PAINTER;
  }

//  public IIcon getInvisibleIcon() {
//    return invisibleIcon;
//  }
//
//  public void setInvisibleIcon(IIcon invisibleIcon) {
//    this.invisibleIcon = invisibleIcon;
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    super.registerBlockIcons(iIconRegister);
//    invisibleIcon = iIconRegister.registerIcon("enderio:invisblePaint");
//  }

}
