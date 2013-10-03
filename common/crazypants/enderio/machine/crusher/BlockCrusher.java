package crazypants.enderio.machine.crusher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockCrusher extends AbstractMachineBlock {

  public static BlockCrusher create() {
    BlockCrusher res = new BlockCrusher();
    res.init();
    return res;
  }

  private BlockCrusher() {
    super(ModObject.blockCrusher, TileCrusher.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileCrusher) {
      return new ContainerCrusher(player.inventory, (TileCrusher) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileCrusher) {
      return new GuiCrusher(player.inventory, (TileCrusher) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_CRUSHER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:crusherFrontOn";
    }
    return "enderio:crusherFront";
  }

}
