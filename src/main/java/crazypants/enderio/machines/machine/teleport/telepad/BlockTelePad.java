package crazypants.enderio.machines.machine.teleport.telepad;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.machines.machine.teleport.ContainerTravelAccessable;
import crazypants.enderio.machines.machine.teleport.ContainerTravelAuth;
import crazypants.enderio.machines.machine.teleport.GuiTravelAuth;
import crazypants.enderio.machines.machine.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.machines.machine.teleport.telepad.gui.ContainerTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.gui.GuiAugmentedTravelAccessible;
import crazypants.enderio.machines.machine.teleport.telepad.gui.GuiTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.render.BlockType;
import crazypants.enderio.machines.machine.teleport.telepad.render.TelePadRenderMapper;
import crazypants.enderio.machines.machine.teleport.telepad.render.TelePadSpecialRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTelePad extends BlockTravelAnchor<TileTelePad> implements IPaintable.ISolidBlockPaintableBlock, /* IWailaInfoProvider, */IHaveTESR {

  public static final int GUI_ID_TELEPAD = 0;
  public static final int GUI_ID_TELEPAD_TRAVEL = 1;

  @SuppressWarnings("rawtypes")
  public static BlockTravelAnchor create(@Nonnull IModObject modObject) {

    // PacketFluidLevel

    BlockTelePad ret = new BlockTelePad(modObject);
    ret.init();
    return ret;
  }

  @Nonnull
  public static final PropertyEnum<BlockType> BLOCK_TYPE = PropertyEnum.<BlockType> create("blocktype", BlockType.class);

  public BlockTelePad(@Nonnull IModObject modObject) {
    super(modObject, TileTelePad.class);
    setLightOpacity(255);
    useNeighborBrightness = true;
  }

  @Override
  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO).withProperty(BLOCK_TYPE, BlockType.SINGLE));
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER, BLOCK_TYPE });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(BLOCK_TYPE, BlockType.getType(meta)).withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO);
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    BlockType type = state.getValue(BLOCK_TYPE);
    return type.ordinal();
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileTelePad tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.inNetwork()).addCacheKey(tileEntity.isMaster());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  /*
   * This makes us "air" for purposes of lighting. Otherwise our model would be much too dark, as it is always surrounded be 8 TelePad blocks.
   */
  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {

    return false;
  }

  @Deprecated
  @Override
  public @Nonnull AxisAlignedBB getSelectedBoundingBox(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos) {
    if (bs.getBlock() == this) {
      BlockType bt = bs.getValue(BLOCK_TYPE);
      if (bt != BlockType.SINGLE) {
        BlockPos masterLoc = bt.getLocationOfMaster(pos);
        AxisAlignedBB res = new AxisAlignedBB(masterLoc.south().south().west(), masterLoc.north().east().east().up());
        return res;
      }
    }
    return super.getSelectedBoundingBox(bs, world, pos);
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block changedTo, @Nonnull BlockPos fromPos) {
    TileTelePad tileEntity = getTileEntity(world, pos);
    if (tileEntity != null) {
      tileEntity.updateRedstoneState();
    }
  }

  @Override
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ITelePad) {
      ITelePad tp = (ITelePad) te;
      if (tp.inNetwork()) {
        if (!tp.isMaster()) {
          ITelePad master = tp.getMaster();
          return openGui(world, master.getLocation(), entityPlayer, side);
        }
      } else {
        return false;
      }

      // from here out we know that we are connected and are the master
      if (tp.canBlockBeAccessed(entityPlayer)) {
        return openGui(world, pos, entityPlayer, side, GUI_ID_TELEPAD);
      } else {
        sendPrivateChatMessage(entityPlayer, tp.getOwner());
      }
    }
    return true;
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int ID) {
    TileTelePad te = getTileEntity(world, pos);
    if (te != null) {
      if (GUI_ID_TELEPAD == ID) {
        return new ContainerTelePad(player.inventory, te);
      } else if (GUI_ID_TELEPAD_TRAVEL == ID) {
        return new ContainerTravelAccessable(player.inventory, te, world);
      } else {
        return new ContainerTravelAuth(player.inventory);
      }
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int ID) {
    TileTelePad te = getTileEntity(world, pos);
    if (te != null) {
      if (GUI_ID_TELEPAD == ID) {
        return new GuiTelePad(player.inventory, te);
      } else if (GUI_ID_TELEPAD_TRAVEL == ID) {
        return new GuiAugmentedTravelAccessible(player.inventory, te, world);
      } else {
        return new GuiTravelAuth(player, te, world);
      }
    }
    return null;
  }

  @Override
  @Deprecated
  public @Nonnull IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY,
      float hitZ, int meta, @Nonnull EntityLivingBase placer) {

    BlockPos swCorner = findSouthWestCorner(worldIn, pos);
    BlockPos masterPos = getMasterPosForNewMB(worldIn, swCorner, pos);
    if (masterPos == null) {
      return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    BlockType myType = null;
    for (BlockType bt : BlockType.values()) {
      BlockPos test = bt.getLocationOfMaster(pos);
      if (test != null && test.equals(masterPos)) {
        myType = bt;
      }
    }
    if (myType == null) {
      return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }
    if (!worldIn.isRemote) {
      updateMultiBlock(worldIn, masterPos, pos, true);
    }
    return getStateFromMeta(myType.ordinal());
  }

  @Override
  public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    super.breakBlock(worldIn, pos, state);
    if (state.getBlock() == this) {
      BlockType type = state.getValue(BLOCK_TYPE);
      if (type != BlockType.SINGLE) {
        BlockPos masterPos = type.getLocationOfMaster(pos);
        updateMultiBlock(worldIn, masterPos, pos, false);
      }
    }
  }

  private void updateMultiBlock(World world, BlockPos masterPos, BlockPos ignorePos, boolean form) {
    for (BlockType type : BlockType.values()) {
      if (type != BlockType.SINGLE) {
        Vec3i offset = type.getOffsetFromMaster();
        BlockPos targetPos = new BlockPos(masterPos.getX() + offset.getX(), masterPos.getY() + offset.getY(), masterPos.getZ() + offset.getZ());
        if (!targetPos.equals(ignorePos)) {
          BlockType setToType = BlockType.SINGLE;
          if (form) {
            setToType = type;
          }
          world.setBlockState(targetPos, getDefaultState().withProperty(BLOCK_TYPE, setToType), 3);
        }
      }
    }
  }

  private BlockPos getMasterPosForNewMB(@Nonnull World worldIn, @Nonnull BlockPos swCorner, @Nonnull BlockPos ignorePos) {
    BlockPos testPos = swCorner;
    for (int i = 0; i < 3; i++) {
      testPos = swCorner.offset(EnumFacing.NORTH, i);
      for (int j = 0; j < 3; j++) {
        if (!testPos.equals(ignorePos) && !isSingle(worldIn, testPos)) {
          return null;
        }
        testPos = testPos.east();
      }
    }
    return swCorner.offset(EnumFacing.NORTH).offset(EnumFacing.EAST);
  }

  private @Nonnull BlockPos findSouthWestCorner(@Nonnull World worldIn, @Nonnull BlockPos pos) {
    BlockPos res = pos;
    int i = 0;
    while (isSingle(worldIn, res.south()) && i < 2) {
      res = res.south();
      i++;
    }
    i = 0;
    while (isSingle(worldIn, res.west()) && i < 2) {
      res = res.west();
      i++;
    }
    return res;
  }

  private boolean isSingle(@Nonnull World worldIn, @Nonnull BlockPos pos) {
    IBlockState bs = worldIn.getBlockState(pos);
    if (bs.getBlock() == this) {
      return bs.getValue(BLOCK_TYPE) == BlockType.SINGLE;
    }
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return TelePadRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return TelePadRenderMapper.instance;
  }

  // @Override
  // public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
  // if (Config.telepadFluidUse <= 0 || world == null) {
  // return;
  // }
  // TileTelePad te = getTileEntity(world, pos);
  // if (te != null && te.inNetwork()) {
  // FluidStack stored = te.getMaster().tank.getFluid();
  // String fluid = stored == null ? EnderIO.lang.localize("tooltip.none") : stored.getFluid().getLocalizedName(stored);
  // int amount = stored == null ? 0 : stored.amount;
  // tooltip.add(String.format("%s%s : %s (%d %s)", TextFormatting.WHITE, EnderIO.lang.localize("tooltip.fluidStored"), fluid, amount,
  // EnderIO.lang.localize("fluid.millibucket.abr")));
  // }
  // }
  //
  // @Override
  // public int getDefaultDisplayMask(World world, int x, int y, int z) {
  // return IWailaInfoProvider.ALL_BITS;
  // }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileTelePad.class, new TelePadSpecialRenderer());
  }

}
