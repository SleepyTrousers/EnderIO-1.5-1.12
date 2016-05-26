package crazypants.enderio.teleport.telepad;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.teleport.ContainerTravelAccessable;
import crazypants.enderio.teleport.ContainerTravelAuth;
import crazypants.enderio.teleport.GuiTravelAuth;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTelePad extends BlockTravelAnchor<TileTelePad> implements IPaintable.ISolidBlockPaintableBlock {

  @SideOnly(Side.CLIENT)
  private static IRenderMapper RENDER_MAPPER;

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
    super(ModObject.blockTelePad.getUnlocalisedName(), TileTelePad.class);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
    setLightOpacity(255);
    useNeighborBrightness = true;
  }

  @Override
  protected void registerGuiHandlers() {
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TELEPAD, this);
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TELEPAD_TRAVEL, this);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return getDefaultState();
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileTelePad tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.inNetwork()).addCacheKey(tileEntity.isMaster());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  /*
   * This makes us "air" for purposes of lighting. Otherwise our model would be much too dark, as it is always surrounded be 8 TelePad blocks.
   */
  @Override
  public boolean isFullCube(IBlockState bs) {
    
    return false;
  }

  @Deprecated
  @Override
  public AxisAlignedBB getSelectedBoundingBox(IBlockState bs, World world, BlockPos pos) {
    if (world != null && pos != null) {
      TileTelePad tileEntity = getTileEntity(world, pos);
      if (tileEntity != null && tileEntity.inNetwork()) {
        return tileEntity.getBoundingBox();
      }
    }
    return super.getSelectedBoundingBox(bs, world, pos);
  }

  @Deprecated
  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block changedTo) {    
    if (world != null && pos != null) {
      TileTelePad tileEntity = getTileEntity(world, pos);
      if (tileEntity != null) {
        tileEntity.updateRedstoneState();
      }
    }
  }

  @Override
  public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
    if (world != null && pos != null) {
      TileTelePad tileEntity = getTileEntity(world, pos);
      if (tileEntity != null) {
        tileEntity.updateConnectedState(true);
      }
    }
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
    if (world != null && pos != null) {
      TileTelePad tileEntity = getTileEntity(world, pos);
      if (tileEntity != null) {
        tileEntity.updateConnectedState(true);
      }
    }
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

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return TelepadRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return TelepadRenderMapper.instance;
  }

}
