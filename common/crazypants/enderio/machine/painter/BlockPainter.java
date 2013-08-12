package crazypants.enderio.machine.painter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.*;

public class BlockPainter extends AbstractMachineBlock<TileEntityPainter> {

  public static final String KEY_SOURCE_BLOCK_ID = "sourceBlockId";
  public static final String KEY_SOURCE_BLOCK_META = "sourceBlockMeta";

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
    GameRegistry.addRecipe(new ItemStack(this), "bbb", "iri", "bbb", 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Item.redstone), 'b', new ItemStack(
        ModObject.itemIndustrialBinder.id, 1, 0));
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileEntityPainter) {
      return new PainterContainer(player.inventory, (AbstractMachineEntity) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    return new GuiPainter(player.inventory, (AbstractMachineEntity) te);
  }
  
  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_PAINTER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:painterFrontOn";
    }
    return "enderio:painterFrontOff";
  }

}
