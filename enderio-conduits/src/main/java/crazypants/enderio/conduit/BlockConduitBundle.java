package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.Util;

import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IConduitBundle.FacadeRenderState;
import crazypants.enderio.base.conduit.IConduitItem;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.facade.EnumFacadeType;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitConnectorType;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.conduitprobe.ItemConduitProbe;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduit.config.ConduitConfig;
import crazypants.enderio.conduit.gui.ExternalConnectionContainer;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.GuiExternalConnectionSelector;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.conduit.render.ConduitRenderMapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

public class BlockConduitBundle extends BlockEio<TileConduitBundle>
    implements IEioGuiHandler.WithPos, IPaintable.IBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockConduitBundle create(@Nonnull IModObject modObject) {
    BlockConduitBundle result = new BlockConduitBundle(modObject);
    result.init();
    return result;
  }

  @SideOnly(Side.CLIENT)
  private TextureAtlasSprite lastHitIcon;

  private final Random rand = new Random();

  private @Nonnull AxisAlignedBB bounds;

  protected BlockConduitBundle(@Nonnull IModObject modObject) {
    super(modObject);
    bounds = setBlockBounds(0.334, 0.334, 0.334, 0.667, 0.667, 0.667);
    setHardness(1.5f);
    setResistance(10.0f);
    setCreativeTab(null);
    setDefaultState(blockState.getBaseState().withProperty(OPAQUE, false));
    setShape(mkShape(BlockFaceShape.UNDEFINED));
  }

  public static final @Nonnull IProperty<Boolean> OPAQUE = PropertyBool.create("opaque");

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { OPAQUE });
  }

  private @Nonnull AxisAlignedBB setBlockBounds(double f, double g, double h, double i, double j, double k) {
    // Return value used to avoid null warnings in constructor
    return (bounds = new AxisAlignedBB(f, g, h, i, j, k));
  }

  @Override
  @Nonnull
  public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return bounds;
  }

  @Override
  protected void init() {
    super.init();
    SmartModelAttacher.registerNoProps(this);
  }

  @Override
  @Nullable
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return null;
  };

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(OPAQUE) ? 1 : 0;
  }

  @Override
  @Nonnull
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(OPAQUE, meta == 1);
  }

  @Override
  @Nonnull
  public IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperConduitBundle(state, world, pos, ConduitRenderMapper.instance);
    TileConduitBundle bundle = getTileEntitySafe(world, pos);
    if (bundle != null) {
      synchronized (bundle) {
        blockStateWrapper.addCacheKey(bundle);
        blockStateWrapper.bakeModel();
      }
    } else {
      blockStateWrapper.bakeModel();
    }
    return blockStateWrapper;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target, @Nonnull ParticleManager effectRenderer) {
    TileConduitBundle cb = getTileEntity(world, target.getBlockPos());
    if (cb == null) {
      return false;
    }

    TextureAtlasSprite tex = null;
    if (YetaUtil.isSolidFacadeRendered(cb, Minecraft.getMinecraft().player)) {
      IBlockState paintSource = cb.getPaintSource();
      if (paintSource != null) {
        tex = RenderUtil.getTexture(paintSource);
      }
    } else if (target.hitInfo instanceof CollidableComponent) {
      CollidableComponent cc = (CollidableComponent) target.hitInfo;
      IConduit con = cb.getConduit(cc.conduitType);
      if (con != null && con instanceof IConduit.WithDefaultRendering) {
        tex = ((IConduit.WithDefaultRendering) con).getTextureForState(cc);
      }
    }
    if (tex == null) {
      tex = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(ModObject.block_machine_base.getBlockNN().getDefaultState());
    }
    lastHitIcon = tex;
    addBlockHitEffects(world, effectRenderer, target.hitVec.x, target.hitVec.y, target.hitVec.z, target.sideHit, tex);
    return true;
  }

  @Override
  public boolean addLandingEffects(@Nonnull IBlockState state, @Nonnull net.minecraft.world.WorldServer world, @Nonnull BlockPos bp,
      @Nonnull IBlockState iblockstate, @Nonnull EntityLivingBase entity, int numberOfParticles) {
    // TODO: Should probably register a dummy state for this, but this is an easy fix for the blockTank that allows testing to begin
    int stateId = Block.getStateId(ModObject.block_machine_base.getBlockNN().getDefaultState());
    TileConduitBundle te = getTileEntity(world, bp);
    if (te != null) {
      IBlockState ps = te.getPaintSource();
      if (ps != null) {
        stateId = Block.getStateId(ps);
      }
    }
    world.spawnParticle(EnumParticleTypes.BLOCK_DUST, bp.getX() + 0.5, bp.getY() + 1, bp.getZ() + 0.5, numberOfParticles, 0.0D, 0.0D, 0.0D,
        0.15000000596046448D, new int[] { stateId });
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {
    if (lastHitIcon == null) {
      lastHitIcon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes()
          .getTexture(ModObject.block_machine_base.getBlockNN().getDefaultState());
    }

    IBlockState state = world.getBlockState(pos);
    TextureAtlasSprite tex = lastHitIcon;
    if (state.getBlock() != this || tex == null) {
      return false;
    }
    state = state.getActualState(world, pos);
    int i = 4;
    for (int j = 0; j < i; ++j) {
      for (int k = 0; k < i; ++k) {
        for (int l = 0; l < i; ++l) {
          double d0 = pos.getX() + (j + 0.5D) / i;
          double d1 = pos.getY() + (k + 0.5D) / i;
          double d2 = pos.getZ() + (l + 0.5D) / i;
          ParticleDigging fx = (ParticleDigging) new ParticleDigging.Factory().createParticle(-1, world, d0, d1, d2, d0 - pos.getX() - 0.5D,
              d1 - pos.getY() - 0.5D, d2 - pos.getZ() - 0.5D, 0);
          fx.setBlockPos(pos);
          fx.setParticleTexture(tex);
          effectRenderer.addEffect(fx);
        }
      }
    }

    return true;
  }

  @SideOnly(Side.CLIENT)
  private void addBlockHitEffects(@Nonnull World world, @Nonnull ParticleManager effectRenderer, double xCoord, double yCoord, double zCoord,
      @Nonnull EnumFacing sideEnum, @Nonnull TextureAtlasSprite tex) {

    double d0 = xCoord;
    double d1 = yCoord;
    double d2 = zCoord;
    if (sideEnum.getAxis() != Axis.X) {
      d0 += rand.nextDouble() * 0.4 - rand.nextDouble() * 0.4;
    }
    if (sideEnum.getAxis() != Axis.Y) {
      d1 += rand.nextDouble() * 0.4 - rand.nextDouble() * 0.4;
    }
    if (sideEnum.getAxis() != Axis.Z) {
      d2 += rand.nextDouble() * 0.4 - rand.nextDouble() * 0.4;
    }

    ParticleDigging digFX = (ParticleDigging) Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), d0, d1,
        d2, 0, 0, 0, 0);
    if (digFX != null) {
      digFX.init().multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
      digFX.setParticleTexture(tex);
    }
  }

  @Override
  @Nonnull
  public ItemStack getPickBlock(@Nonnull IBlockState bs, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    ItemStack ret = ItemStack.EMPTY;

    if (target.hitInfo instanceof CollidableComponent) {
      CollidableComponent cc = (CollidableComponent) target.hitInfo;
      TileConduitBundle bundle = getTileEntity(world, pos);
      if (bundle != null) {
        IConduit conduit = bundle.getConduit(cc.conduitType);
        if (conduit != null) {
          ret = conduit.createItem();
        } else if (cc.conduitType == null && bundle.hasFacade()) {
          bundle.getFacadeType();
          // use the facade
          ret = new ItemStack(ModObject.itemConduitFacade.getItemNN(), 1, EnumFacadeType.getMetaFromType(bundle.getFacadeType()));
          PaintUtil.setSourceBlock(ret, bundle.getPaintSource());
        }
      }
    }
    return ret;
  }

  @Override
  public int quantityDropped(@Nonnull Random r) {
    return 0;
  }

  @Override
  public boolean isSideSolid(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    IConduitBundle te = getTileEntitySafe(world, pos);
    if (te == null) {
      return false;
    }
    if (te.hasFacade()) {
      try {
        return te.getPaintSourceNN().isSideSolid(world, pos, side);
      } catch (Exception e) {
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean canBeReplacedByLeaves(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return bs.getValue(OPAQUE);
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState bs) {
    return bs.getValue(OPAQUE) ? 255 : 0;
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    if (bs.getValue(OPAQUE)) {
      return 255;
    }
    IConduitBundle te = getTileEntitySafe(world, pos);
    if (te == null) {
      return getLightOpacity(bs);
    }
    return te.getLightOpacity();
  }

  @Override
  public int getLightValue(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    TileEntity te = getTileEntitySafe(world, pos);
    if (!(te instanceof IConduitBundle)) {
      return super.getLightValue(bs, world, pos);
    }
    IConduitBundle con = (IConduitBundle) te;
    int result = 0;
    if (con.hasFacade()) {
      IBlockState paintSource = con.getPaintSourceNN();
      result = paintSource.getLightValue();
      if (paintSource.isOpaqueCube()) {
        return result;
      }
    }
    if (ConduitConfig.dynamicLighting.get()) {
      Collection<IConduit> conduits = con.getConduits();
      for (IConduit conduit : conduits) {
        result += conduit.getLightValue();
      }
    }
    return result > 15 ? 15 : result;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Nonnull
  public SoundType getSoundType(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity entity) {
    IConduitBundle te = getTileEntitySafe(world, pos);
    if (te != null && te.hasFacade()) {
      return te.getPaintSourceNN().getBlock().getSoundType();
    }
    return super.getSoundType(state, world, pos, entity);
  }

  @Deprecated
  @Override
  @SideOnly(Side.CLIENT)
  public int getPackedLightmapCoords(@Nonnull IBlockState bs, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    IConduitBundle te = getTileEntitySafe(worldIn, pos);
    if (te != null && te.hasFacade()) {
      if (te.getFacadeRenderedAs() == FacadeRenderState.WIRE_FRAME) {
        return 255;
      } else {
        return getMixedBrightnessForFacade(bs, worldIn, pos, te.getPaintSourceNN().getBlock());
      }
    }
    return super.getPackedLightmapCoords(bs, worldIn, pos);
  }

  @SideOnly(Side.CLIENT)
  public int getMixedBrightnessForFacade(@Nonnull IBlockState bs, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, @Nonnull Block facadeBlock) {
    int i = worldIn.getCombinedLight(pos, getLightValue(bs, worldIn, pos));
    if (i == 0 && facadeBlock instanceof BlockSlab) {
      pos = pos.down();
      Block block = worldIn.getBlockState(pos).getBlock();
      return worldIn.getCombinedLight(pos, block.getLightValue(bs, worldIn, pos));
    } else if (facadeBlock.getUseNeighborBrightness(bs)) {
      return getNeightbourBrightness(worldIn, pos);
    } else {
      return i;
    }
  }

  private int getNeightbourBrightness(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    int result = worldIn.getCombinedLight(pos.up(), 0);
    for (EnumFacing dir : EnumFacing.HORIZONTALS) {
      int val = worldIn.getCombinedLight(pos.offset(NullHelper.notnullM(dir, "EnumFacing iterated with null")), 0);
      if (val > result) {
        result = val;
      }
    }
    return result;
  }

  @Deprecated
  @Override
  public float getBlockHardness(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos) {
    IConduitBundle te = getTileEntity(world, pos);
    if (te == null) {
      return super.getBlockHardness(bs, world, pos);
    }
    return te.getFacadeType().isHardened() ? blockHardness * 10 : blockHardness;
  }

  @Override
  public float getExplosionResistance(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Entity par1Entity, @Nonnull Explosion explosion) {
    float resist = super.getExplosionResistance(world, pos, par1Entity, explosion);
    IConduitBundle te = (IConduitBundle) world.getTileEntity(pos);
    return te != null && te.getFacadeType().isHardened() ? resist * 10 : resist;
  }

  @Override
  public int getStrongPower(@Nonnull IBlockState bs, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    IRedstoneConduit con = getRedstoneConduit(world, pos);
    if (con == null) {
      return 0;
    }
    return con.isProvidingStrongPower(side);
  }

  @Override
  public int getWeakPower(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    IRedstoneConduit con = getRedstoneConduit(world, pos);
    if (con == null) {
      return 0;
    }

    return con.isProvidingWeakPower(side);
  }

  @Override
  public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    IRedstoneConduit con = getRedstoneConduit(world, pos);
    if (con == null) {
      return false;
    }

    return side == null || con.containsExternalConnection(side);
  }

  @Override
  public boolean canProvidePower(@Nonnull IBlockState bs) {
    return true;
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    IConduitBundle te = (IConduitBundle) world.getTileEntity(pos);
    if (te == null) {
      return true;
    }

    boolean breakBlock = true;
    NNList<ItemStack> drop = new NNList<>();
    if (YetaUtil.isSolidFacadeRendered(te, player)) {
      breakBlock = false;
      ItemStack fac = new ItemStack(ModObject.itemConduitFacade.getItemNN(), 1, EnumFacadeType.getMetaFromType(te.getFacadeType()));
      PaintUtil.setSourceBlock(fac, te.getPaintSource());
      drop.add(fac);

      ConduitUtil.playBreakSound(te.getPaintSourceNN().getBlock().getSoundType(), world, pos);
      te.setPaintSource(null);
      te.setFacadeType(EnumFacadeType.BASIC);
    }

    if (breakBlock) {
      List<RaytraceResult> results = doRayTraceAll(world, pos, player);
      RaytraceResult.sort(Util.getEyePosition(player), results);
      for (RaytraceResult rt : results) {
        if (breakConduit(te, drop, rt, player)) {
          break;
        }
      }
    }

    breakBlock = te.getConduits().isEmpty() && !te.hasFacade();

    if (!breakBlock) {
      world.notifyBlockUpdate(pos, bs, bs, 3);
    }

    if (!world.isRemote && !player.capabilities.isCreativeMode) {
      for (ItemStack st : drop) {
        Util.dropItems(world, NullHelper.notnullM(st, "NNList#iterator.next()"), pos, false);
      }
    }

    if (breakBlock) {
      world.setBlockToAir(pos);
      return true;
    }
    return false;
  }

  private boolean breakConduit(IConduitBundle te, List<ItemStack> drop, RaytraceResult rt, EntityPlayer player) {
    if (rt == null) {
      return false;
    }
    CollidableComponent component = rt.component;
    if (component == null) {
      return false;
    }
    Class<? extends IConduit> type = component.conduitType;
    if (!YetaUtil.renderConduit(player, type)) {
      return false;
    }

    if (type == null) {
      // broke a connector so drop any conduits with no connections as there
      // is no other way to remove these
      List<IConduit> cons = new ArrayList<IConduit>(te.getConduits());
      boolean droppedUnconected = false;
      for (IConduit con : cons) {
        if (con.getConduitConnections().isEmpty() && con.getExternalConnections().isEmpty() && YetaUtil.renderConduit(player, con)) {
          te.removeConduit(con);
          drop.addAll(con.getDrops());
          droppedUnconected = true;
        }
      }
      // If there isn't, then drop em all
      if (!droppedUnconected) {
        for (IConduit con : cons) {
          if (con != null && YetaUtil.renderConduit(player, con)) {
            te.removeConduit(con);
            drop.addAll(con.getDrops());
          }
        }
      }
    } else {
      IConduit con = te.getConduit(type);
      if (con != null) {
        te.removeConduit(con);
        drop.addAll(con.getDrops());
      }
    }

    return true;
  }

  @Override
  public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    IConduitBundle te = getTileEntity(world, pos);
    if (te == null) {
      return;
    }
    te.onBlockRemoved();
    world.removeTileEntity(pos);
  }

  @Override
  public void onBlockClicked(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    ItemStack equipped = player.getHeldItemMainhand();
    if (!player.isSneaking() || equipped.isEmpty() || !ToolUtil.isToolEquipped(player, EnumHand.MAIN_HAND)) {
      return;
    }
    ConduitUtil.openConduitGui(world, pos, player);
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {

    IConduitBundle bundle = getTileEntity(world, pos);
    if (bundle == null) {
      return false;
    }

    ItemStack stack = player.getHeldItem(hand);
    if (stack.getItem() == Items.STICK) { // TODO: remove this later!
      player.sendMessage(new TextComponentString("You clicked on " + bundle));
    }
    if (stack.getItem() == ModObject.itemConduitFacade.getItem()) {
      // add or replace facade
      return handleFacadeClick(world, pos, player, side, bundle, stack, hand, hitX, hitY, hitZ);

    } else if (ConduitUtil.isConduitEquipped(player, hand)) {
      // Add conduit
      if (player.isSneaking()) {
        return false;
      }
      if (handleConduitClick(world, pos, player, bundle, stack, hand)) {
        return true;
      }

    } else if (ConduitUtil.isProbeEquipped(player, hand)) {
      // Handle copy / paste of settings
      if (handleConduitProbeClick(world, pos, player, bundle, stack)) {
        return true;
      }
    } else if (ToolUtil.isToolEquipped(player, hand) && player.isSneaking()) {
      // Break conduit with tool
      if (handleWrenchClick(world, pos, player, hand)) {
        return true;
      }
    }

    // Check conduit defined actions
    RaytraceResult closest = doRayTrace(world, pos, player);
    @Nonnull
    List<RaytraceResult> all = NullHelper.notnullJ(Collections.emptyList(), "Collections#emptyList");
    if (closest != null) {
      all = doRayTraceAll(world, pos, player);
    }

    final CollidableComponent closestComponent = closest == null ? null : closest.component;
    if (closestComponent != null && closestComponent.data instanceof ConduitConnectorType) {

      ConduitConnectorType conType = (ConduitConnectorType) closestComponent.data;
      if (conType == ConduitConnectorType.INTERNAL) {
        boolean result = false;
        // if its a connector pass the event on to all conduits
        for (IConduit con : bundle.getConduits()) {
          RaytraceResult res = getHitForConduitType(all, con.getCollidableType());
          if (res != null && YetaUtil.renderConduit(player, con.getCollidableType()) && con.onBlockActivated(player, hand, res, all)) {
            bundle.getEntity().markDirty();
            result = true;
          }

        }
        if (result) {
          return true;
        }
      } else {
        if (!world.isRemote) {
          // GuiID.facing2guiid(closest.component.dir).openGui(world, pos, player, side);
          openGui(world, pos, player, side, closestComponent.dir.ordinal());
        }
        return true;
      }
    }

    if (closestComponent == null || closestComponent.conduitType == null && all.isEmpty()) {
      // Nothing of interest hit
      return false;
    }

    // Conduit specific actions
    if (all.isEmpty()) {
      RaytraceResult.sort(Util.getEyePosition(player), all);
      for (RaytraceResult rr : all) {
        final CollidableComponent component = rr.component;
        if (component != null && YetaUtil.renderConduit(player, component.conduitType) && !(component.data instanceof ConduitConnectorType)) {

          IConduit con = bundle.getConduit(component.conduitType);
          if (con != null && con.onBlockActivated(player, hand, rr, all)) {
            bundle.getEntity().markDirty();
            return true;
          }
        }
      }
    } else {
      IConduit closestConduit = bundle.getConduit(closestComponent.conduitType);
      if (closest != null && closestConduit != null && YetaUtil.renderConduit(player, closestConduit)
          && closestConduit.onBlockActivated(player, hand, closest, all)) {
        bundle.getEntity().markDirty();
        return true;
      }
    }
    return false;

  }

  private boolean handleWrenchClick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ITool tool = ToolUtil.getEquippedTool(player, hand);
    if (tool != null) {
      if (tool.canUse(hand, player, pos)) {
        if (!world.isRemote) {
          IBlockState bs = world.getBlockState(pos);
          if (!PermissionAPI.hasPermission(player.getGameProfile(), permissionNodeWrenching, new BlockPosContext(player, pos, bs, null))) {
            player.sendMessage(new TextComponentString(EnderIO.lang.localize("wrench.permission.denied")));
            return false;
          }
          BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, bs, player);
          event.setExpToDrop(0);
          if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
          }
          removedByPlayer(bs, world, pos, player, true);
          tool.used(hand, player, pos);
        }
        return true;
      }
    }
    return false;
  }

  private boolean handleConduitProbeClick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, @Nonnull IConduitBundle bundle,
      @Nonnull ItemStack stack) {
    if (stack.getItemDamage() != 1) {
      return false; // not in copy paste mode
    }
    RaytraceResult rr = doRayTrace(world, pos, player);
    if (rr == null) {
      return false;
    }
    CollidableComponent component = rr.component;
    if (component == null) {
      return false;
    }
    EnumFacing dir = component.dir;
    if (dir == null) {
      return false;
    }
    return ItemConduitProbe.copyPasteSettings(player, stack, bundle, dir);
  }

  private boolean handleConduitClick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, @Nonnull IConduitBundle bundle,
      @Nonnull ItemStack stack, @Nonnull EnumHand hand) {
    IConduitItem equipped = (IConduitItem) stack.getItem();
    if (!bundle.hasType(equipped.getBaseConduitType())) {
      if (!world.isRemote) {
        bundle.addConduit(equipped.createConduit(stack, player));
        ConduitUtil.playBreakSound(SoundType.METAL, world, pos);
        if (!player.capabilities.isCreativeMode) {
          player.getHeldItem(hand).shrink(1);
        }
      }
      return true;
    }
    return false;
  }

  public boolean handleFacadeClick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, @Nonnull EnumFacing side,
      @Nonnull IConduitBundle bundle, @Nonnull ItemStack stack, @Nonnull EnumHand hand, float hitX, float hitY, float hitZ) {

    // Add facade
    if (player.isSneaking()) {
      return false;
    }

    IBlockState facadeID = PaintUtil.getSourceBlock(player.getHeldItem(hand));
    if (facadeID == null) {
      return false;
    }

    int facadeType = player.getHeldItem(hand).getItemDamage();

    if (bundle.hasFacade()) {
      if (!YetaUtil.isSolidFacadeRendered(bundle, player) || facadeEquals(bundle, facadeID, facadeType)) {
        return false;
      }
      if (!world.isRemote && !player.capabilities.isCreativeMode) {
        ItemStack drop = new ItemStack(ModObject.itemConduitFacade.getItemNN(), 1, EnumFacadeType.getMetaFromType(bundle.getFacadeType()));
        PaintUtil.setSourceBlock(drop, bundle.getPaintSource());
        if (!player.inventory.addItemStackToInventory(drop)) {
          ItemUtil.spawnItemInWorldWithRandomMotion(world, drop, pos, hitX, hitY, hitZ, 1.2f);
        }
      }
    }
    bundle.setFacadeType(EnumFacadeType.getTypeFromMeta(facadeType));
    bundle.setPaintSource(facadeID);
    if (!world.isRemote) {
      ConduitUtil.playPlaceSound(facadeID.getBlock().getSoundType(), world, pos);
    }
    if (!player.capabilities.isCreativeMode) {
      stack.shrink(1);
    }
    IBlockState bs = world.getBlockState(pos);
    world.notifyBlockUpdate(pos, bs, bs, 3);
    bundle.getEntity().markDirty();
    return true;
  }

  private boolean facadeEquals(@Nonnull IConduitBundle bundle, @Nonnull IBlockState b, int facadeType) {
    IBlockState a = bundle.getPaintSource();
    if (a == null) {
      return false;
    }
    if (a.getBlock() != b.getBlock()) {
      return false;
    }
    if (bundle.getFacadeType().ordinal() != facadeType) {
      return false;
    }
    return a.getBlock().getMetaFromState(a) == b.getBlock().getMetaFromState(b);
  }

  @Nullable
  @Override
  public Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    if (facing == null) {
      return null;
    }
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileConduitBundle te = getTileEntity(world, pos);
    if (te != null) {
      return new ExternalConnectionContainer(player.inventory, EnumFacing.getFront(param1), te);
    }
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Nullable
  @Override
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    TileConduitBundle te = getTileEntity(world, pos);
    if (te != null) {
      if (facing == null) {
        return new GuiExternalConnectionSelector(te);
      }
      return new GuiExternalConnection(player.inventory, te, EnumFacing.getFront(param1));
    }
    return null;
  }

  private RaytraceResult getHitForConduitType(List<RaytraceResult> all, Class<? extends IConduit> collidableType) {
    for (RaytraceResult rr : all) {
      CollidableComponent component = rr == null ? null : rr.component;
      if (component != null && component.conduitType == collidableType) {
        return rr;
      }
    }
    return null;
  }

  @Deprecated
  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock,
      @Nonnull BlockPos neighborPos) {
    if (neighborBlock != this) {
      TileConduitBundle conduit = getTileEntity(world, pos);
      if (conduit != null) {
        conduit.onNeighborBlockChange(neighborBlock);
      }
    }
  }

  @Override
  public void onNeighborChange(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull BlockPos neighbor) {
    if (world.getBlockState(neighbor).getBlock() != this) {
      TileConduitBundle conduit = getTileEntity(world, pos);
      if (conduit != null) {
        conduit.onNeighborChange(world, pos, neighbor);
      }
    }
  }

  @Deprecated
  @Override
  public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB axisalignedbb,
      @Nonnull List<AxisAlignedBB> arraylist, @Nullable Entity par7Entity, boolean b) {

    IConduitBundle con = getTileEntity(world, pos);
    if (con == null) {
      return;
    }
    if (con.hasFacade()) {
      setBlockBounds(0, 0, 0, 1, 1, 1);
      super.addCollisionBoxToList(state, world, pos, axisalignedbb, arraylist, par7Entity, b);
    } else {

      Collection<CollidableComponent> collidableComponents = con.getCollidableComponents();
      for (CollidableComponent bnd : collidableComponents) {
        setBlockBounds(bnd.bound.minX, bnd.bound.minY, bnd.bound.minZ, bnd.bound.maxX, bnd.bound.maxY, bnd.bound.maxZ);
        super.addCollisionBoxToList(state, world, pos, axisalignedbb, arraylist, par7Entity, b);
      }

      if (con.getConduits().isEmpty()) { // just in case
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxToList(state, world, pos, axisalignedbb, arraylist, par7Entity, b);
      }
    }

    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nonnull
  public AxisAlignedBB getSelectedBoundingBox(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos) {
    TileConduitBundle te = getTileEntity(world, pos);
    EntityPlayer player = Minecraft.getMinecraft().player;
    if (te == null) {
      // FIXME is this valid?
      return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }
    IConduitBundle con = te;

    BoundingBox minBB = null;

    if (!YetaUtil.isSolidFacadeRendered(con, player)) {

      List<RaytraceResult> results = doRayTraceAll(world, pos, player);
      Iterator<RaytraceResult> iter = results.iterator();
      while (iter.hasNext()) {
        CollidableComponent component = iter.next().component;
        if (component != null && component.conduitType == null && component.data != ConduitConnectorType.EXTERNAL) {
          iter.remove();
        }
      }

      // This is an ugly special case, TODO fix this
      for (RaytraceResult hit : results) {
        IRedstoneConduit cond = con.getConduit(IRedstoneConduit.class);
        CollidableComponent component = hit.component;
        EnumFacing dir = component == null ? null : component.dir;
        if (cond != null && component != null && dir != null && cond.getExternalConnections().contains(dir)
            && component.data == InsulatedRedstoneConduit.COLOR_CONTROLLER_ID) {
          minBB = component.bound;
        }
      }

      if (minBB == null) {
        RaytraceResult hit = RaytraceResult.getClosestHit(Util.getEyePosition(player), results);
        CollidableComponent component = hit == null ? null : hit.component;
        if (component != null) {
          EnumFacing dir = component.dir;
          minBB = component.bound;
          if (dir != null && component.conduitType == null) {
            dir = dir.getOpposite();
            float trans = 0.0125f;
            minBB = minBB.translate(dir.getFrontOffsetX() * trans, dir.getFrontOffsetY() * trans, dir.getFrontOffsetZ() * trans);
            float scale = 0.7f;
            minBB = minBB.scale(1 + Math.abs(dir.getFrontOffsetX()) * scale, 1 + Math.abs(dir.getFrontOffsetY()) * scale,
                1 + Math.abs(dir.getFrontOffsetZ()) * scale);
          } else {
            minBB = minBB.scale(1.09, 1.09, 1.09);
          }
        }
      }
    } else {
      minBB = new BoundingBox(0, 0, 0, 1, 1, 1);
    }

    if (minBB == null) {
      minBB = new BoundingBox(0, 0, 0, 1, 1, 1);
    }

    return new AxisAlignedBB(pos.getX() + minBB.minX, pos.getY() + minBB.minY, pos.getZ() + minBB.minZ, pos.getX() + minBB.maxX, pos.getY() + minBB.maxY,
        pos.getZ() + minBB.maxZ);
  }

  @SuppressWarnings("null")
  @Override
  public RayTraceResult collisionRayTrace(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d origin,
      @Nonnull Vec3d direction) {

    RaytraceResult raytraceResult = doRayTrace(world, pos, origin, direction, null);
    RayTraceResult ret = null;
    if (raytraceResult != null) {
      ret = raytraceResult.movingObjectPosition;
      // FIXME No it's not!!
      ret.hitInfo = raytraceResult.component;
    }

    return ret;
  }

  public RaytraceResult doRayTrace(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer) {
    List<RaytraceResult> allHits = doRayTraceAll(world, pos, entityPlayer);
    Vec3d origin = Util.getEyePosition(entityPlayer);
    return RaytraceResult.getClosestHit(origin, allHits);
  }

  public @Nonnull List<RaytraceResult> doRayTraceAll(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer) {
    double pitch = Math.toRadians(entityPlayer.rotationPitch);
    double yaw = Math.toRadians(entityPlayer.rotationYaw);

    double dirX = -Math.sin(yaw) * Math.cos(pitch);
    double dirY = -Math.sin(pitch);
    double dirZ = Math.cos(yaw) * Math.cos(pitch);

    double reachDistance = EnderIO.proxy.getReachDistanceForPlayer(entityPlayer);

    Vec3d origin = Util.getEyePosition(entityPlayer);
    Vec3d direction = origin.addVector(dirX * reachDistance, dirY * reachDistance, dirZ * reachDistance);
    return doRayTraceAll(world.getBlockState(pos), world, pos, origin, direction, entityPlayer);
  }

  private @Nullable RaytraceResult doRayTrace(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d origin, @Nonnull Vec3d direction,
      EntityPlayer entityPlayer) {
    List<RaytraceResult> allHits = doRayTraceAll(world.getBlockState(pos), world, pos, origin, direction, entityPlayer);
    return RaytraceResult.getClosestHit(origin, allHits);
  }

  protected @Nonnull NNList<RaytraceResult> doRayTraceAll(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d origin,
      @Nonnull Vec3d direction, EntityPlayer player) {

    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IConduitBundle)) {
      return NNList.emptyList();
    }
    IConduitBundle bundle = (IConduitBundle) te;
    NNList<RaytraceResult> hits = new NNList<RaytraceResult>();

    if (YetaUtil.isSolidFacadeRendered(bundle, player)) {
      setBlockBounds(0, 0, 0, 1, 1, 1);
      RayTraceResult hitPos = super.collisionRayTrace(bs, world, pos, origin, direction);
      if (hitPos != null) {
        hits.add(new RaytraceResult(new CollidableComponent(null, BoundingBox.UNIT_CUBE, hitPos.sideHit, null), hitPos));
      }
    } else {
      ConduitDisplayMode mode = YetaUtil.getDisplayMode(player);
      for (CollidableComponent component : new ArrayList<CollidableComponent>(bundle.getCollidableComponents())) {
        if (mode.isAll() || component.conduitType == null || YetaUtil.renderConduit(player, component.conduitType)) {
          setBlockBounds(component.bound.minX, component.bound.minY, component.bound.minZ, component.bound.maxX, component.bound.maxY, component.bound.maxZ);
          RayTraceResult hitPos = super.collisionRayTrace(bs, world, pos, origin, direction);
          if (hitPos != null) {
            hits.add(new RaytraceResult(component, hitPos));
          }
        }
      }

      // safety to prevent unbreakable empty bundles in case of a bug
      if (bundle.getConduits().isEmpty() && !YetaUtil.isFacadeHidden(bundle, player)) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
        RayTraceResult hitPos = super.collisionRayTrace(bs, world, pos, origin, direction);
        if (hitPos != null) {
          hits.add(new RaytraceResult(null, hitPos));
        }
      }
    }

    setBlockBounds(0, 0, 0, 1, 1, 1);

    return hits;
  }

  private IRedstoneConduit getRedstoneConduit(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IConduitBundle te = getTileEntity(world, pos);
    if (te == null) {
      return null;
    }
    return te.getConduit(IRedstoneConduit.class);
  }

  // PAINT

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

}
