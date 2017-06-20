package crazypants.enderio.machine.capbank;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.integration.baubles.BaublesUtil;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import crazypants.enderio.machine.capbank.network.NetworkUtil;
import crazypants.enderio.machine.capbank.packet.*;
import crazypants.enderio.machine.capbank.render.CapBankBlockRenderMapper;
import crazypants.enderio.machine.capbank.render.CapBankItemRenderMapper;
import crazypants.enderio.machine.capbank.render.CapBankRenderer;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.render.property.IOMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.render.registry.TextureRegistry;
import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.tool.ToolUtil;
import info.loenwind.autosave.Reader;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class BlockCapBank extends BlockEio<TileCapBank> implements IGuiHandler, IAdvancedTooltipProvider, IRedstoneConnectable,
    ISmartRenderAwareBlock, IHaveTESR {

  @SideOnly(Side.CLIENT)
  private static CapBankItemRenderMapper CAPBANK_RENDER_MAPPER;

  public static BlockCapBank create() {
    PacketHandler.INSTANCE.registerMessage(PacketNetworkStateResponse.class, PacketNetworkStateResponse.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkStateRequest.class, PacketNetworkStateRequest.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkIdRequest.class, PacketNetworkIdRequest.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkIdResponse.class, PacketNetworkIdResponse.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkEnergyRequest.class, PacketNetworkEnergyRequest.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkEnergyResponse.class, PacketNetworkEnergyResponse.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketGuiChange.class, PacketGuiChange.class, PacketHandler.nextID(), Side.SERVER);

    BlockCapBank res = new BlockCapBank();
    res.init();
    return res;
  }

  public static final TextureSupplier gaugeIcon = TextureRegistry.registerTexture("blocks/capacitorBankOverlays");
  public static final TextureSupplier infoPanelIcon = TextureRegistry.registerTexture("blocks/capBankInfoPanel");

  protected BlockCapBank() {
    super(MachineObject.blockCapBank, TileCapBank.class);
    setHardness(2.0F);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO)
        .withProperty(CapBankType.KIND, CapBankType.NONE));
  }

  @Override
  protected ItemBlock createItemBlock() {
    return new BlockItemCapBank(this, getRegistryName());
  }

  @Override
  protected void init() {
    super.init();
    GuiID.registerGuiHandler(GuiID.GUI_ID_CAP_BANK, this);
    GuiID.registerGuiHandler(GuiID.GUI_ID_CAP_BANK_WITH_BAUBLES4, this);
    GuiID.registerGuiHandler(GuiID.GUI_ID_CAP_BANK_WITH_BAUBLES7, this);
    setLightOpacity(255);
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, CapBankType.KIND });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(CapBankType.KIND, CapBankType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return CapBankType.getMetaFromType(state.getValue(CapBankType.KIND));
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      CapBankBlockRenderMapper renderMapper = new CapBankBlockRenderMapper(state, world, pos);
      IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, renderMapper);
      blockStateWrapper.addCacheKey(state.getValue(CapBankType.KIND));
      blockStateWrapper.addCacheKey(renderMapper);
      TileCapBank tileEntity = getTileEntitySafe(world, pos);
      if (tileEntity != null) {
        for (EnumFacing face : EnumFacing.values()) {
          blockStateWrapper.addCacheKey(tileEntity.getIoMode(NullHelper.notnullJ(face, "Enum.values()")));
          blockStateWrapper.addCacheKey(tileEntity.getDisplayType(face));
        }
      }
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, NonNullList<ItemStack> list) {
    for (CapBankType type : CapBankType.types()) {
      if (type.isCreative()) {
        list.add(BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(type), type.getMaxEnergyStored() / 2));
      } else {
        list.add(BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(type), 0));
        list.add(BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(type), type.getMaxEnergyStored()));
      }
    }
  }

  @Override
  public int damageDropped(IBlockState st) {
    return getMetaFromState(st);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    list.add(PowerDisplayUtil.formatStoredPower(BlockItemCapBank.getStoredEnergyForItem(itemstack), CapBankType.getTypeFromMeta(itemstack.getItemDamage())
        .getMaxEnergyStored()));
    final @Nullable NBTTagCompound tagCompound = itemstack.getTagCompound();
    if (tagCompound != null) {
      ItemStack[] stacks = Reader.readField(tagCompound, ItemStack[].class, "inventory", new ItemStack[4]);
      if (stacks != null) {
        int count = 0;
        for (ItemStack stack : stacks) {
          if (stack != null) {
            count++;
          }
        }
        if (count > 0) {
          String msg = EnderIO.lang.localizeExact("tile.blockCapBank.tooltip.hasItems");
          Object[] objects = { count };
          list.add(TextFormatting.GOLD + MessageFormat.format(msg, objects));
        }
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand,
      EnumFacing faceHit, float hitX, float hitY, float hitZ) {
    if (world != null && pos != null && state != null && entityPlayer != null && hand != null && faceHit != null) {
      TileCapBank tcb = getTileEntity(world, pos);
      if (tcb == null) {
        return false;
      }

      if (entityPlayer.isSneaking() && entityPlayer.getHeldItem(hand) == null && faceHit.getFrontOffsetY() == 0) {
        InfoDisplayType newDisplayType = tcb.getDisplayType(faceHit).next();
        if (newDisplayType == InfoDisplayType.NONE) {
          tcb.setDefaultIoMode(faceHit);
        } else {
          tcb.setIoMode(faceHit, IoMode.DISABLED);
        }
        tcb.setDisplayType(faceHit, newDisplayType);
        return true;
      }

      if (!entityPlayer.isSneaking() && ToolUtil.isToolEquipped(entityPlayer, hand)) {
        IoMode ioMode = tcb.getIoMode(faceHit);
        if (faceHit.getFrontOffsetY() == 0) {
          if (ioMode == IoMode.DISABLED) {
            InfoDisplayType newDisplayType = tcb.getDisplayType(faceHit).next();
            tcb.setDisplayType(faceHit, newDisplayType);
            if (newDisplayType == InfoDisplayType.NONE) {
              tcb.toggleIoModeForFace(faceHit);
            }
          } else {
            tcb.toggleIoModeForFace(faceHit);
          }
        } else {
          tcb.toggleIoModeForFace(faceHit);
        }

        IBlockState bs = world.getBlockState(pos);
        if (world.isRemote) {
          world.notifyBlockUpdate(pos, bs, bs, 3);
        } else {
          final Block blockCapBank2 = MachineObject.blockCapBank.getBlock();
          if (blockCapBank2 != null) {
            world.notifyNeighborsOfStateChange(pos, this, true);
          }
          world.notifyBlockUpdate(pos, bs, bs, 3);
        }

        return true;
      }
    }

    return super.onBlockActivated(world, pos, state, entityPlayer, hand, faceHit, hitX, hitY, hitZ);
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    baublesToGuiId(BaublesUtil.instance().getBaubles(entityPlayer)).openGui(world, pos, entityPlayer, side);
    return true;
  }

  private static GuiID baublesToGuiId(IInventory baubles) {
    if (baubles != null && baubles.getSizeInventory() == 4) {
      return GuiID.GUI_ID_CAP_BANK_WITH_BAUBLES4;
    } else if (baubles != null && baubles.getSizeInventory() == 7) {
      return GuiID.GUI_ID_CAP_BANK_WITH_BAUBLES7;
    } else {
      return GuiID.GUI_ID_CAP_BANK;
    }
  }

  private static int guiIdToBaublesSize(GuiID ID) {
    if (ID == GuiID.GUI_ID_CAP_BANK_WITH_BAUBLES4) {
      return 4;
    } else if (ID == GuiID.GUI_ID_CAP_BANK_WITH_BAUBLES7) {
      return 7;
    } else {
      return 0;
    }
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileCapBank te = getTileEntity(NullHelper.notnullF(world, "getServerGuiElement() was called without a world"), new BlockPos(x, y, z));
    if (te != null) {
      return ContainerCapBank.create(player.inventory, te, guiIdToBaublesSize(GuiID.byID(ID)));
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileCapBank te = getTileEntity(NullHelper.notnullF(world, "getClientGuiElement() was called without a world"), new BlockPos(x, y, z));
    if (te != null) {
      return new GuiCapBank(player, player.inventory, te, ContainerCapBank.create(player.inventory, te, guiIdToBaublesSize(GuiID.byID(ID))));
    }
    return null;
  }

  @Override
  public boolean isSideSolid(IBlockState bs, IBlockAccess world, BlockPos pos, EnumFacing side) {
    return true;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Deprecated
  public boolean shouldSideBeRendered(IBlockState bs, IBlockAccess par1IBlockAccess, BlockPos pos, EnumFacing side) {
    Block i1 = par1IBlockAccess.getBlockState(pos.offset(side)).getBlock();
    return i1 == this ? false : super.shouldSideBeRendered(bs, par1IBlockAccess, pos, side);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getGaugeIcon() {
    return gaugeIcon.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getInfoPanelIcon() {
    return infoPanelIcon.get(TextureAtlasSprite.class);
  }

  @Override
  @Deprecated
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
    if (world.isRemote) {
      return;
    }
    TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof TileCapBank) {
      TileCapBank te = (TileCapBank) tile;
      te.onNeighborBlockChange(neighborBlock);
    }
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, player, stack);

    if (world != null && pos != null && stack != null) {
    TileCapBank cb = getTileEntity(world, pos);
    if (cb == null) {
      return;
    }
    if (stack.getTagCompound() != null) {
      cb.readContentsFromNBT(stack.getTagCompound());
    }

    Collection<TileCapBank> neigbours = NetworkUtil.getNeigbours(cb);
    if (neigbours.isEmpty()) {
        if (player != null) {
          int heading = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
          EnumFacing dir = getDirForHeading(heading);
          cb.setDisplayType(dir, InfoDisplayType.LEVEL_BAR);
        }
    } else {
      boolean modifiedDisplayType;
        modifiedDisplayType = setDisplayToVerticalFillBar(cb, getTileEntity(world, NullHelper.notnullM(pos.down(), "EnumFacing.down()")));
        modifiedDisplayType |= setDisplayToVerticalFillBar(cb, getTileEntity(world, NullHelper.notnullM(pos.up(), "EnumFacing.up()")));
      if (modifiedDisplayType) {
        cb.validateDisplayTypes();
      }
    }

    if (world.isRemote) {
      return;
    }
    
    IBlockState bs = world.getBlockState(pos);
    world.notifyBlockUpdate(pos, bs, bs, 3);
    }
  }

  protected boolean setDisplayToVerticalFillBar(TileCapBank cb, TileCapBank capBank) {
    boolean modifiedDisplayType = false;
    if (capBank != null) {
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (dir.getFrontOffsetY() == 0 && capBank.getDisplayType(dir) == InfoDisplayType.LEVEL_BAR && capBank.getType() == cb.getType()) {
          cb.setDisplayType(dir, InfoDisplayType.LEVEL_BAR);
          modifiedDisplayType = true;
        }
      }
    }
    return modifiedDisplayType;
  }

  protected EnumFacing getDirForHeading(int heading) {
    switch (heading) {
    case 0:
      return EnumFacing.values()[2];
    case 1:
      return EnumFacing.values()[5];
    case 2:
      return EnumFacing.values()[3];
    case 3:
    default:
      return EnumFacing.values()[4];
    }
  }

  @Override
  public boolean removedByPlayer(IBlockState bs, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if (world != null && pos != null && player != null && !world.isRemote && (!player.capabilities.isCreativeMode)) {
      TileCapBank te = getTileEntity(world, pos);
      if (te != null) {
        te.moveInventoryToNetwork();
      }
    }
    return super.removedByPlayer(bs, world, pos, player, willHarvest);
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, @Nullable TileCapBank te, ItemStack drop) {
    drop.setTagCompound(new NBTTagCompound());
    if (te != null) {
      te.writeContentsToNBT(drop.getTagCompound());
    }
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {
    if (world != null && pos != null && !world.isRemote) {
      TileCapBank te = getTileEntity(world, pos);
      if (te != null) {
        te.onBreakBlock();
      }
    }
    super.breakBlock(world, pos, state);
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Deprecated
  public AxisAlignedBB getSelectedBoundingBox(IBlockState bs, World world, BlockPos pos) {
    if (world == null || pos == null) {
      return super.getSelectedBoundingBox(bs, world, pos);
    }
    TileCapBank tr = getTileEntity(world, pos);
    if (tr == null) {
      return super.getSelectedBoundingBox(bs, world, pos);
    }
    ICapBankNetwork network = tr.getNetwork();
    if (!tr.getType().isMultiblock() || network == null) {
      return super.getSelectedBoundingBox(bs, world, pos);
    }

    Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    for (TileCapBank bc : network.getMembers()) {
      int x = bc.getPos().getX();
      int y = bc.getPos().getY();
      int z = bc.getPos().getZ();
      min.x = Math.min(min.x, x);
      max.x = Math.max(max.x, x + 1);
      min.y = Math.min(min.y, y);
      max.y = Math.max(max.y, y + 1);
      min.z = Math.min(min.z, z);
      max.z = Math.max(max.z, z + 1);
    }
    return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
  }

  @Override
  public boolean hasComparatorInputOverride(IBlockState bs) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(IBlockState bs, World world, BlockPos pos) {
    if (world != null && pos != null) {
      TileCapBank te = getTileEntity(world, pos);
      if (te != null) {
        return te.getComparatorOutput();
      }
    }
    return 0;
  }

//  @Override
//  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
//    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
//    if (te instanceof TileCapBank) {
//      TileCapBank cap = (TileCapBank) te;
//      if (cap.getNetwork() != null) {
//        if (world.isRemote && shouldDoWorkThisTick(world, new BlockPos(x, y, z), 20)) {
//          PacketHandler.INSTANCE.sendToServer(new PacketNetworkStateRequest(cap));
//        }
//        ICapBankNetwork nw = cap.getNetwork();
//        if (world.isRemote) {
//          ((CapBankClientNetwork) nw).requestPowerUpdate(cap, 2);
//        }
//
//        if (SpecialTooltipHandler.showAdvancedTooltips()) {
//          String format = Util.TAB + Util.ALIGNRIGHT + TextFormatting.WHITE;
//          String suffix = Util.TAB + Util.ALIGNRIGHT + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr();
//          tooltip.add(String.format("%s : %s%s%s", EnderIO.lang.localize("capbank.maxIO"), format, PowerDisplayUtil.formatPower(nw.getMaxIO()), suffix));
//          tooltip.add(String.format("%s : %s%s%s", EnderIO.lang.localize("capbank.maxIn"), format, PowerDisplayUtil.formatPower(nw.getMaxInput()), suffix));
//          tooltip.add(String.format("%s : %s%s%s", EnderIO.lang.localize("capbank.maxOut"), format, PowerDisplayUtil.formatPower(nw.getMaxOutput()), suffix));
//          tooltip.add("");
//        }
//
//        long stored = nw.getEnergyStoredL();
//        long max = nw.getMaxEnergyStoredL();
//        tooltip.add(String.format("%s%s%s / %s%s%s %s", TextFormatting.WHITE, PowerDisplayUtil.formatPower(stored), TextFormatting.RESET,
//            TextFormatting.WHITE, PowerDisplayUtil.formatPower(max), TextFormatting.RESET, PowerDisplayUtil.abrevation()));
//
//        int change = Math.round(nw.getAverageChangePerTick());
//        String color = TextFormatting.WHITE.toString();
//        if (change > 0) {
//          color = TextFormatting.GREEN.toString() + "+";
//        } else if (change < 0) {
//          color = TextFormatting.RED.toString();
//        }
//        tooltip.add(String.format("%s%s%s", color, PowerDisplayUtil.formatPowerPerTick(change), " " + TextFormatting.RESET.toString()));
//      }
//    }
//  }
//
//  @Override
//  public int getDefaultDisplayMask(World world, int x, int y, int z) {
//    return IWailaInfoProvider.BIT_DETAILED;
//  }

  /* IRedstoneConnectable */

  @Override
  public boolean shouldRedstoneConduitConnect(World world, BlockPos pos, EnumFacing from) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public CapBankItemRenderMapper getItemRenderMapper() {
    if (CAPBANK_RENDER_MAPPER == null) {
      CAPBANK_RENDER_MAPPER = new CapBankItemRenderMapper();
    }
    return CAPBANK_RENDER_MAPPER;
  }

  @SideOnly(Side.CLIENT)
  public IOMode.EnumIOMode mapIOMode(InfoDisplayType displayType, IoMode mode) {
    switch (displayType) {
    case IO:
      return IOMode.EnumIOMode.CAPACITORBANK;
    case LEVEL_BAR:
      switch (mode) {
      case NONE:
        return IOMode.EnumIOMode.CAPACITORBANK;
      case PULL:
        return IOMode.EnumIOMode.CAPACITORBANKINPUTSMALL;
      case PUSH:
        return IOMode.EnumIOMode.CAPACITORBANKOUTPUTSMALL;
      case PUSH_PULL:
        return IOMode.EnumIOMode.CAPACITORBANK;
      case DISABLED:
        return IOMode.EnumIOMode.CAPACITORBANKLOCKEDSMALL;
      }
    case NONE:
      switch (mode) {
      case NONE:
        return IOMode.EnumIOMode.CAPACITORBANK;
      case PULL:
        return IOMode.EnumIOMode.CAPACITORBANKINPUT;
      case PUSH:
        return IOMode.EnumIOMode.CAPACITORBANKOUTPUT;
      case PUSH_PULL:
        return IOMode.EnumIOMode.CAPACITORBANK;
      case DISABLED:
        return IOMode.EnumIOMode.CAPACITORBANKLOCKED;
      }
    }
    throw new RuntimeException("Hey, leave our enums alone!");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileCapBank.class, new CapBankRenderer());
  }

}
