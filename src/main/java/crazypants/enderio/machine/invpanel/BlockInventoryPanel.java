package crazypants.enderio.machine.invpanel;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;

public class BlockInventoryPanel extends AbstractMachineBlock<TileInventoryPanel> {

  private static final float BLOCK_SIZE = 3f / 16f;

  public static BlockInventoryPanel create() {
    PacketHandler.INSTANCE.registerMessage(PacketItemInfo.class, PacketItemInfo.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketItemList.class, PacketItemList.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketRequestMissingItems.class, PacketRequestMissingItems.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketFetchItem.class, PacketFetchItem.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketMoveItems.class, PacketMoveItems.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketDatabaseReset.class, PacketDatabaseReset.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketGuiSettings.class, PacketGuiSettings.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketStoredCraftingRecipe.class, PacketStoredCraftingRecipe.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketSetExtractionDisabled.class, PacketSetExtractionDisabled.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketUpdateExtractionDisabled.class, PacketUpdateExtractionDisabled.class, PacketHandler.nextID(), Side.CLIENT);

    BlockInventoryPanel panel = new BlockInventoryPanel();
    panel.init();
    return panel;
  }

  public BlockInventoryPanel() {
    super(ModObject.blockInventoryPanel, TileInventoryPanel.class);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemInventoryPanel.class, modObject.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, modObject.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
  }

  @Override
  public int getRenderType() {
    return 0;
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
    setBlockBounds(0.0f, 0.0f, 0.5f - BLOCK_SIZE / 2, 1.0f, 1.0f, 0.5f + BLOCK_SIZE / 2);
  }
  
  @Override
  public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
   return getBoundingBox(world, x, y, z);
  }
  
  @Override
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
    return getBoundingBox(world, x, y, z);
  }
  
  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
    AxisAlignedBB bb = getBoundingBox(0, 0, 0, getFacing(world, x, y, z));
    setBlockBounds(bb);
  }

  public AxisAlignedBB getBoundingBox(IBlockAccess world, int x, int y, int z) {
    int facing = getFacing(world, x, y, z);
    return getBoundingBox(x, y, z, facing);
  }

  public AxisAlignedBB getBoundingBox(int x, int y, int z, int facing) {
    switch (facing) {
    case 0:
      return AxisAlignedBB.getBoundingBox(x, y + (1 - BLOCK_SIZE), z, x + 1, y + 1, z + 1);
    case 1:
      return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + BLOCK_SIZE, z + 1);
    case 2:
      return AxisAlignedBB.getBoundingBox(x, y, z + (1- BLOCK_SIZE), x + 1, y + 1, z + 1);
    case 3:
      return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + BLOCK_SIZE);
    case 4:
      return AxisAlignedBB.getBoundingBox(x + (1 - BLOCK_SIZE), y, z, x + 1, y + 1, z + 1);
    case 5:
      return AxisAlignedBB.getBoundingBox(x, y, z, x + BLOCK_SIZE, y + 1, z + 1);
    default:
      return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }
  }

  private int getFacing(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileInventoryPanel) {
      return ((TileInventoryPanel) te).getFacing();
    }
    return 0;
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    // this is handled by BlockItemInventoryPanel.placeBlockAt
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

  //  @Override
  //  protected String getTopIconKey(boolean active) {
  //    return "enderio:invPanelSide";
  //  }
  //
  //  @Override
  //  protected String getSideIconKey(boolean active) {
  //    return "enderio:invPanelSide";
  //  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int blockSide, int blockMeta) {
    // This is used to render the block as an item
    return iconBuffer[0][blockSide + 6];
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
