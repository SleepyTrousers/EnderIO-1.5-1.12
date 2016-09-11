package crazypants.enderio.machine.painter;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockPainter extends AbstractMachineBlock<TileEntityPainter> {

  public static final String KEY_SOURCE_BLOCK_ID = "sourceBlockId";
  public static final String KEY_SOURCE_BLOCK_META = "sourceBlockMeta";

  public static BlockPainter create() {
    BlockPainter ppainter = new BlockPainter();
    ppainter.init();
    return ppainter;
  }

  private IIcon invisibleIcon;

  private BlockPainter() {
    super(ModObject.blockPainter, TileEntityPainter.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPainter) {
      return new PainterContainer(player.inventory, (TileEntityPainter) te);
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    return new GuiPainter(player.inventory, (TileEntityPainter) te);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_PAINTER;
  }

  public IIcon getInvisibleIcon() {
    return invisibleIcon;
  }

  public void setInvisibleIcon(IIcon invisibleIcon) {
    this.invisibleIcon = invisibleIcon;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);
    invisibleIcon = iIconRegister.registerIcon("enderio:invisblePaint");
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:painterFrontOn";
    }
    return "enderio:painterFrontOff";
  }

}
