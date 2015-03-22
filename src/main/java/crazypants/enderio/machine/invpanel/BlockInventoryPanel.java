package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInventoryPanel extends AbstractMachineBlock<TileInventoryPanel> {

  public static BlockInventoryPanel create() {
    PacketHandler.INSTANCE.registerMessage(PacketItemInfo.class, PacketItemInfo.class, PacketHandler.nextID(), Side.CLIENT);

    BlockInventoryPanel panel = new BlockInventoryPanel();
    panel.init();
    return panel;
  }

  public BlockInventoryPanel() {
    super(ModObject.blockInventoryPanel, TileInventoryPanel.class);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int blockSide) {
    int facing = getFacing(world, x, y, z);
    return ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing] == 2;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean isBlockNormalCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public void setBlockBoundsForItemRender() {
    setBlockBounds(0.0f, 0.0f, 0.4f, 1.0f, 1.0f, 0.6f);
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
    int facing = getFacing(world, x, y, z);
    switch (facing) {
      case 2:
        setBlockBounds(0.0f, 0.0f, 0.8f, 1.0f, 1.0f, 1.0f);
        break;
      case 3:
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.2f);
        break;
      case 4:
        setBlockBounds(0.8f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        break;
      case 5:
        setBlockBounds(0.0f, 0.0f, 0.0f, 0.2f, 1.0f, 1.0f);
        break;
      default:
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        break;
    }
  }

  private int getFacing(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileInventoryPanel) {
      return ((TileInventoryPanel)te).getFacing();
    }
    return 0;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_INVENTORY_PANEL;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:invPanelFrontOn";
    }
    return "enderio:invPanelFrontOff";
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileInventoryPanel) {
      return new InventoryPanelContainer(player.inventory, (TileInventoryPanel) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileInventoryPanel te = (TileInventoryPanel) world.getTileEntity(x, y, z);
    return new GuiInventoryPanel(te, new InventoryPanelContainer(player.inventory, te));
  }
}
