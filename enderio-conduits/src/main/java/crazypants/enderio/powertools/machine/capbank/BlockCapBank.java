package crazypants.enderio.powertools.machine.capbank;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import crazypants.enderio.base.render.property.IOMode;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.powertools.machine.capbank.network.ICapBankNetwork;
import crazypants.enderio.powertools.machine.capbank.network.NetworkUtil;
import crazypants.enderio.powertools.machine.capbank.render.CapBankBlockRenderMapper;
import crazypants.enderio.powertools.machine.capbank.render.CapBankItemRenderMapper;
import crazypants.enderio.powertools.machine.capbank.render.CapBankRenderer;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCapBank extends BlockEio<TileCapBank>
    implements IEioGuiHandler.WithPos, IAdvancedTooltipProvider, ISmartRenderAwareBlock, IHaveTESR, ICustomSubItems, IPaintable.ISolidBlockPaintableBlock {

  public static BlockCapBank create(@Nonnull IModObject modObject) {
    BlockCapBank res = new BlockCapBank(modObject);
    res.init();
    return res;
  }

  private static final TextureSupplier gaugeIcon = TextureRegistry.registerTexture("blocks/capacitor_bank_overlays");
  private static final TextureSupplier infoPanelIcon = TextureRegistry.registerTexture("blocks/cap_bank_info_panel");

  protected BlockCapBank(@Nonnull IModObject modObject) {
    super(modObject);
    setHardness(2.0F);
    setLightOpacity(255);
    setDefaultState(getBlockState().getBaseState().withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO)
        .withProperty(CapBankType.KIND, CapBankType.NONE));
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  public ItemBlock createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemCapBank(this));
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.register(this, EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.DEFAULTS, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumMergingBlockRenderMode.RENDER, CapBankType.KIND });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(CapBankType.KIND, CapBankType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return CapBankType.getMetaFromType(state.getValue(CapBankType.KIND));
  }

  @Override
  public @Nonnull IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return state.withProperty(EnumMergingBlockRenderMode.RENDER, EnumMergingBlockRenderMode.AUTO);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
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
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  @Nonnull
  public NNList<ItemStack> getSubItems() {
    return getSubItems(this, CapBankType.values().length - 1);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull CreativeTabs p_149666_2_, @Nonnull NonNullList<ItemStack> list) {
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
  public int damageDropped(@Nonnull IBlockState st) {
    return getMetaFromState(st);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    list.add(LangPower.RF(BlockItemCapBank.getStoredEnergyForItem(itemstack), CapBankType.getTypeFromMeta(itemstack.getItemDamage()).getMaxEnergyStored()));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing faceHit, float hitX, float hitY, float hitZ) {
    TileCapBank tcb = getTileEntity(world, pos);
    if (tcb == null) {
      return false;
    }

    if (entityPlayer.isSneaking() && Prep.isInvalid(entityPlayer.getHeldItem(hand)) && faceHit.getFrontOffsetY() == 0) {
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

      if (!world.isRemote) {
        world.notifyNeighborsOfStateChange(pos, this, true);
      }
      world.notifyBlockUpdate(pos, state, state, 3);
      return true;
    }

    return super.onBlockActivated(world, pos, state, entityPlayer, hand, faceHit, hitX, hitY, hitZ);
  }

  @Override
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    return openGui(world, pos, entityPlayer, side, baublesToGuiId(BaublesUtil.instance().getBaubles(entityPlayer)));
  }

  private static int baublesToGuiId(IInventory baubles) {
    if (baubles != null && baubles.getSizeInventory() == 4) {
      return 4;
    } else if (baubles != null && baubles.getSizeInventory() == 7) {
      return 7;
    } else {
      return 0;
    }
  }

  @Override
  @Nullable
  public Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    TileCapBank te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerCapBank(player.inventory, te);
    }
    return null;
  }

  @Override
  @Nullable
  @SideOnly(Side.CLIENT)
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    TileCapBank te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiCapBank(player, player.inventory, te, new ContainerCapBank(player.inventory, te));
    }
    return null;
  }

  @Override
  public boolean isSideSolid(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return true;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Deprecated
  public boolean shouldSideBeRendered(@Nonnull IBlockState bs, @Nonnull IBlockAccess par1IBlockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    Block i1 = par1IBlockAccess.getBlockState(pos.offset(side)).getBlock();
    return i1 == this ? false : super.shouldSideBeRendered(bs, par1IBlockAccess, pos, side);
  }

  @SideOnly(Side.CLIENT)
  public static @Nonnull TextureAtlasSprite getGaugeIcon() {
    return gaugeIcon.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public static @Nonnull TextureAtlasSprite getInfoPanelIcon() {
    return infoPanelIcon.get(TextureAtlasSprite.class);
  }

  @Override
  @Deprecated
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock,
      @Nonnull BlockPos neighborPos) {
    if (world.isRemote) {
      return;
    }
    TileCapBank te = getTileEntity(world, pos);
    if (te != null) {
      te.onNeighborBlockChange(neighborBlock);
    }
  }

  @Override
  public int quantityDropped(@Nonnull Random r) {
    return 0;
  }

  @Override
  public void onBlockPlaced(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull TileCapBank te) {
    super.onBlockPlaced(world, pos, state, player, te);

    Collection<TileCapBank> neigbours = NetworkUtil.getNeigbours(te);
    if (neigbours.isEmpty()) {
      int heading = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
      EnumFacing dir = getDirForHeading(heading);
      te.setDisplayType(dir, InfoDisplayType.LEVEL_BAR);
    } else {
      boolean modifiedDisplayType;
      modifiedDisplayType = setDisplayToVerticalFillBar(te, getTileEntity(world, pos.down()));
      modifiedDisplayType |= setDisplayToVerticalFillBar(te, getTileEntity(world, pos.up()));
      if (modifiedDisplayType) {
        te.validateDisplayTypes();
      }
    }

    if (world.isRemote) {
      return;
    }

    world.notifyBlockUpdate(pos, state, state, 3);
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
  @SideOnly(Side.CLIENT)
  @Deprecated
  public @Nonnull AxisAlignedBB getSelectedBoundingBox(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos) {
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
  public boolean hasComparatorInputOverride(@Nonnull IBlockState bs) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos) {
    TileCapBank te = getTileEntity(world, pos);
    if (te != null) {
      return te.getComparatorOutput();
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull CapBankItemRenderMapper getItemRenderMapper() {
    return CapBankItemRenderMapper.instance;
  }

  @SideOnly(Side.CLIENT)
  public @Nonnull IOMode.EnumIOMode mapIOMode(InfoDisplayType displayType, IoMode mode) {
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
    ClientRegistry.bindTileEntitySpecialRenderer(TileCapBank.class, new CapBankRenderer(this));
  }

  @Override
  public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    return true;
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT END
  // ///////////////////////////////////////////////////////////////////////

}
