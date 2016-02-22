package crazypants.enderio.teleport.telepad;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.ContainerTravelAccessable;
import crazypants.enderio.teleport.ContainerTravelAuth;
import crazypants.enderio.teleport.GuiTravelAuth;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class BlockTelePad extends BlockTravelAnchor<TileTelePad> {

//  @SideOnly(Side.CLIENT)
//  private IIcon[] icons;
//  @SideOnly(Side.CLIENT)
//  private IIcon model;
//  @SideOnly(Side.CLIENT)
//  private IIcon highlightIcon;

  public static BlockTelePad createTelepad() {
    
    PacketHandler.INSTANCE.registerMessage(PacketOpenServerGui.class, PacketOpenServerGui.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketUpdateCoords.class, PacketUpdateCoords.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketUpdateCoords.class, PacketUpdateCoords.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketTeleport.class, PacketTeleport.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketTeleport.class, PacketTeleport.class, PacketHandler.nextID(), Side.CLIENT);
    
    BlockTelePad ret = new BlockTelePad();
    ret.init();
    return ret;
  }

  protected BlockTelePad() {
    super(ModObject.blockTelePad.unlocalisedName, TileTelePad.class);
  }

  @Override
  protected void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TELEPAD, this);
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TELEPAD_TRAVEL, this);
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    icons = new IIcon[3];
//    icons[0] = iIconRegister.registerIcon("enderio:telePadBottom");
//    icons[1] = iIconRegister.registerIcon("enderio:telePadTop");
//    icons[2] = iIconRegister.registerIcon("enderio:telePadSide");
//    model = iIconRegister.registerIcon("enderio:telePadModel");
//    highlightIcon = iIconRegister.registerIcon("enderio:telePadHighlight");
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
//    TileTelePad te = (TileTelePad) world.getTileEntity(x, y, z);
//    if(te != null && te.inNetwork()) {
//      return model;
//    }
//    return getIcon(blockSide, 0);
//  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int side, int meta) {
//    return icons[Math.min(side, 2)];
//  }
//
//  @SideOnly(Side.CLIENT)
//  public IIcon getHighlightIcon() {
//    return highlightIcon;
//  }

  @Override
  public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
    AxisAlignedBB bb = super.getSelectedBoundingBox(world,pos);
    TileTelePad te = (TileTelePad) world.getTileEntity(pos);
    if(!te.inNetwork()) {
      return bb;
    }
    return te.getBoundingBox();
  }

  @Override
  public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block changedTo) {
    super.onNeighborBlockChange(world, pos, state, changedTo);
    getTileEntity(world, pos).updateRedstoneState();
  }

  @Override
  public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
    super.onNeighborChange(world, null, null);
    ((TileTelePad) world.getTileEntity(pos)).updateConnectedState(true);
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileTelePad) {
      TileTelePad tp = (TileTelePad) te;
      if(tp.inNetwork()) {
        if(!tp.isMaster()) {
          TileTelePad master = tp.getMaster();
          return openGui(world, master.getPos(), entityPlayer, side);
        }
      } else {
        return false;
      }

      // from here out we know that we are connected and are the master
      if(tp.canBlockBeAccessed(entityPlayer)) {
        entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TELEPAD, world, pos.getX(), pos.getY(), pos.getZ());
      } else {
        sendPrivateChatMessage(entityPlayer, tp.getOwner());
      }
    }
    return true;
  }

  
  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {  
    super.onBlockPlacedBy(world, pos, state, entity, stack);
    ((TileTelePad) world.getTileEntity(pos)).updateConnectedState(true);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileTelePad) {
      switch (ID) {
      case GuiHandler.GUI_ID_TELEPAD:
        return new ContainerTelePad(player.inventory);
      case GuiHandler.GUI_ID_TELEPAD_TRAVEL:
        return new ContainerTravelAccessable(player.inventory, (TileTelePad) te, world);
      default:
        return new ContainerTravelAuth(player.inventory);
      }
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileTelePad) {
      switch (ID) {
      case GuiHandler.GUI_ID_TELEPAD:
        return new GuiTelePad(player.inventory, (TileTelePad) te, world);
      case GuiHandler.GUI_ID_TELEPAD_TRAVEL:
        return new GuiAugmentedTravelAccessible(player.inventory, (TileTelePad) te, world);
      default:
        return new GuiTravelAuth(player, (ITravelAccessable) te, world);
      }
    }
    return null;
  }

}
