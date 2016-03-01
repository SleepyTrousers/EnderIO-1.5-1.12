package crazypants.enderio.machine.invpanel;

import java.util.Random;

import crazypants.enderio.ClientProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    super(ModObject.blockInventoryPanel, TileInventoryPanel.class, BlockItemInventoryPanel.class);
  }

  @Override
  public int getRenderType() {
    return 0;
  }


  
  @SideOnly(Side.CLIENT)
  @Override
  public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    //TODO: 1.8
    EnumFacing facing = getFacing(worldIn, pos.getX(), pos.getY(), pos.getZ());
    return ClientProxy.sideAndFacingToSpriteOffset[side.ordinal()][facing.ordinal()] == 2;
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
  public void setBlockBoundsForItemRender() {
    setBlockBounds(0.0f, 0.0f, 0.5f - BLOCK_SIZE / 2, 1.0f, 1.0f, 0.5f + BLOCK_SIZE / 2);
  }
  
  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
   return getBoundingBox(worldIn, pos);
  }
  
  @Override
  public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {  
    return getBoundingBox(worldIn, pos);
  }
  
  
  
  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {  
    AxisAlignedBB bb = getBoundingBox(new BlockPos(0, 0, 0), getFacing(worldIn, pos));
    setBlockBounds(bb);
  }

  public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
    EnumFacing facing = getFacing(world, pos);
    return getBoundingBox(pos, facing);
  }

  public AxisAlignedBB getBoundingBox(BlockPos pos, EnumFacing facing) {
    //TODO: 1.8
    return getBoundingBox(pos.getX(), pos.getY(), pos.getZ(), facing.ordinal());
  }
  
  public AxisAlignedBB getBoundingBox(int x, int y, int z, int facing) {
    switch (facing) {
    case 0:
      return new AxisAlignedBB(x, y + (1 - BLOCK_SIZE), z, x + 1, y + 1, z + 1);
    case 1:
      return new AxisAlignedBB(x, y, z, x + 1, y + BLOCK_SIZE, z + 1);
    case 2:
      return new AxisAlignedBB(x, y, z + (1- BLOCK_SIZE), x + 1, y + 1, z + 1);
    case 3:
      return new AxisAlignedBB(x, y, z, x + 1, y + 1, z + BLOCK_SIZE);
    case 4:
      return new AxisAlignedBB(x + (1 - BLOCK_SIZE), y, z, x + 1, y + 1, z + 1);
    case 5:
      return new AxisAlignedBB(x, y, z, x + BLOCK_SIZE, y + 1, z + 1);
    default:
      return new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
    }
  }

  private EnumFacing getFacing(IBlockAccess world, int x, int y, int z) {
    return getFacing(world, new BlockPos(x,y,z));
  }
  
  private EnumFacing getFacing(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileInventoryPanel) {
      return ((TileInventoryPanel) te).getFacing();
    }
    return EnumFacing.NORTH;
  }


  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    //TODO: 1.8
    // this is handled by BlockItemInventoryPanel.placeBlockAt
    return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_INVENTORY_PANEL;
  }

  // @Override
  // protected String getMachineFrontIconKey(boolean active) {
  // if(active) {
  // return "enderio:invPanelFrontOn";
  // }
  // return "enderio:invPanelFrontOff";
  // }

  //  @Override
  //  protected String getTopIconKey(boolean active) {
  //    return "enderio:invPanelSide";
  //  }
  //
  //  @Override
  //  protected String getSideIconKey(boolean active) {
  //    return "enderio:invPanelSide";
  //  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int blockSide, int blockMeta) {
//    // This is used to render the block as an item
//    return iconBuffer[0][blockSide + 6];
//  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileInventoryPanel) {
      return new InventoryPanelContainer(player.inventory, (TileInventoryPanel) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileInventoryPanel te = (TileInventoryPanel) world.getTileEntity(new BlockPos(x, y, z));
    return new GuiInventoryPanel(te, new InventoryPanelContainer(player.inventory, te));
  }
}
