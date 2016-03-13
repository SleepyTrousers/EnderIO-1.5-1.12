package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.conduit.IConduitBundle.FacadeRenderState;
import crazypants.enderio.conduit.facade.EnumFacadeType;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.gui.ExternalConnectionContainer;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.GuiExternalConnectionSelector;
import crazypants.enderio.conduit.gui.PacketFluidFilter;
import crazypants.enderio.conduit.gui.PacketOpenConduitUI;
import crazypants.enderio.conduit.gui.PacketSlotVisibility;
import crazypants.enderio.conduit.gui.item.PacketExistingItemFilterSnapshot;
import crazypants.enderio.conduit.gui.item.PacketModItemFilter;
import crazypants.enderio.conduit.liquid.PacketFluidLevel;
import crazypants.enderio.conduit.packet.PacketConnectionMode;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.conduit.packet.PacketRedstoneConduitOutputStrength;
import crazypants.enderio.conduit.packet.PacketRedstoneConduitSignalColor;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.conduit.render.ConduitRenderState;
import crazypants.enderio.item.IRotatableFacade;
import crazypants.enderio.item.ItemConduitProbe;
import crazypants.enderio.machine.painter.PainterUtil2;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.paint.IPaintable;
import crazypants.enderio.tool.ToolUtil;

@Optional.InterfaceList({ @Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = "MineFactoryReloaded"),
    @Interface(iface = "mods.immibis.core.api.multipart.IMultipartRenderingBlockMarker", modid = "ImmibisMicroblocks") })
public class BlockConduitBundle extends BlockEio<TileConduitBundle> implements IGuiHandler, IPaintable.IBlockPaintableBlock, IRotatableFacade {

  public static BlockConduitBundle create() {

    MinecraftForge.EVENT_BUS.register(ConduitNetworkTickHandler.instance);

    PacketHandler.INSTANCE.registerMessage(PacketFluidLevel.class, PacketFluidLevel.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketExtractMode.class, PacketExtractMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketConnectionMode.class, PacketConnectionMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketItemConduitFilter.class, PacketItemConduitFilter.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketExistingItemFilterSnapshot.class, PacketExistingItemFilterSnapshot.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketModItemFilter.class, PacketModItemFilter.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketFluidFilter.class, PacketFluidFilter.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketRedstoneConduitSignalColor.class, PacketRedstoneConduitSignalColor.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketRedstoneConduitOutputStrength.class, PacketRedstoneConduitOutputStrength.class, PacketHandler.nextID(),
        Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketOpenConduitUI.class, PacketOpenConduitUI.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketSlotVisibility.class, PacketSlotVisibility.class, PacketHandler.nextID(), Side.SERVER);

    BlockConduitBundle result = new BlockConduitBundle();
    result.init();
    MinecraftForge.EVENT_BUS.register(result);
    return result;
  }

  @SideOnly(Side.CLIENT)
  private TextureAtlasSprite lastRemovedComponetIcon;

  private final Random rand = new Random();

  protected BlockConduitBundle() {
    super(ModObject.blockConduitBundle.unlocalisedName, TileConduitBundle.class);
    setBlockBounds(0.334f, 0.334f, 0.334f, 0.667f, 0.667f, 0.667f);
    setHardness(1.5f);
    setResistance(10.0f);
    setCreativeTab(null);
    this.stepSound = new SoundType("silence", 0, 0) {
      @Override
      public String getBreakSound() {
        return "EnderIO:" + soundName + ".dig";
      }

      @Override
      public String getStepSound() {
        return "EnderIO:" + soundName + ".step";
      }
    };

    setDefaultState(blockState.getBaseState());
  }

  @Override
  protected void init() {
    super.init();
    for (EnumFacing dir : EnumFacing.VALUES) {
      EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + dir.ordinal(), this);
    }
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR, this);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return new ConduitRenderState(state, world, pos, getTileEntity(world, pos));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.CUTOUT;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {

    TextureAtlasSprite tex = null;

    TileConduitBundle cb = (TileConduitBundle) world.getTileEntity(target.getBlockPos());
    if (ConduitUtil.isSolidFacadeRendered(cb, Minecraft.getMinecraft().thePlayer)) {
      IBlockState paintSource = cb.getPaintSource();
      if (paintSource != null) {
        tex = RenderUtil.getTexture(paintSource);
      }
    } else if (target.hitInfo instanceof CollidableComponent) {
      CollidableComponent cc = (CollidableComponent) target.hitInfo;
      IConduit con = cb.getConduit(cc.conduitType);
      if (con != null) {
        tex = con.getTextureForState(cc);
      }
    }
    if (tex == null) {
      tex = IconUtil.instance.whiteTexture;
    }
    lastRemovedComponetIcon = tex;
    BlockPos p = target.getBlockPos();
    addBlockHitEffects(world, effectRenderer, p.getX(), p.getY(), p.getZ(), target.sideHit, tex);
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    TextureAtlasSprite tex = lastRemovedComponetIcon;
    byte b0 = 4;
    for (int j1 = 0; j1 < b0; ++j1) {
      for (int k1 = 0; k1 < b0; ++k1) {
        for (int l1 = 0; l1 < b0; ++l1) {
          double d0 = x + (j1 + 0.5D) / b0;
          double d1 = y + (k1 + 0.5D) / b0;
          double d2 = z + (l1 + 0.5D) / b0;
          EntityDiggingFX fx = (EntityDiggingFX) Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), d0,
              d1, d2, d0 - x - 0.5D, d1 - y - 0.5D, d2 - z - 0.5D, 0);
          fx.func_174845_l();
          fx.setParticleIcon(tex);
          effectRenderer.addEffect(fx);
        }
      }
    }
    return true;
  }

  @SideOnly(Side.CLIENT)
  private void addBlockHitEffects(World world, EffectRenderer effectRenderer, int x, int y, int z, EnumFacing sideEnum, TextureAtlasSprite tex) {
    float f = 0.1F;
    double d0 = x + rand.nextDouble() * (getBlockBoundsMaxX() - getBlockBoundsMinX() - f * 2.0F) + f + getBlockBoundsMinX();
    double d1 = y + rand.nextDouble() * (getBlockBoundsMaxY() - getBlockBoundsMinY() - f * 2.0F) + f + getBlockBoundsMinY();
    double d2 = z + rand.nextDouble() * (getBlockBoundsMaxZ() - getBlockBoundsMinZ() - f * 2.0F) + f + getBlockBoundsMinZ();
    int side = sideEnum.ordinal();
    if (side == 0) {
      d1 = y + getBlockBoundsMinY() - f;
    } else if (side == 1) {
      d1 = y + getBlockBoundsMaxY() + f;
    } else if (side == 2) {
      d2 = z + getBlockBoundsMinZ() - f;
    } else if (side == 3) {
      d2 = z + getBlockBoundsMaxZ() + f;
    } else if (side == 4) {
      d0 = x + getBlockBoundsMinX() - f;
    } else if (side == 5) {
      d0 = x + getBlockBoundsMaxX() + f;
    }

    EntityDiggingFX digFX = (EntityDiggingFX) Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), d0, d1,
        d2, 0, 0, 0, 0);
    digFX.func_174845_l().multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
    digFX.setParticleIcon(tex);
    effectRenderer.addEffect(digFX);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onPlaySound(PlaySoundSourceEvent event) {
    String path = event.sound.getSoundLocation().toString();
    if (path != null && path.contains("silence.step")) {
      ISound snd = event.sound;
      World world = EnderIO.proxy.getClientWorld();
      BlockCoord bc = new BlockCoord(snd.getXPosF(), snd.getYPosF(), snd.getZPosF());
      TileEntity te = bc.getTileEntity(world);
      if (te != null && te instanceof TileConduitBundle && ((TileConduitBundle) te).hasFacade()) {
        IBlockState facade = ((TileConduitBundle) te).getPaintSource();
        ConduitUtil.playHitSound(facade.getBlock().stepSound, world, bc.x, bc.y, bc.z);
      } else {
        ConduitUtil.playHitSound(Block.soundTypeMetal, world, bc.x, bc.y, bc.z);
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
    String path = event.name;
    World world = event.entity.worldObj;
    if ("EnderIO:silence.step".equals(path) && world.isRemote) {
      BlockCoord bc = new BlockCoord(event.entity.posX, event.entity.posY - 2, event.entity.posZ);
      TileEntity te = bc.getTileEntity(world);
      if (te != null && te instanceof TileConduitBundle && ((TileConduitBundle) te).hasFacade()) {
        IBlockState facade = ((TileConduitBundle) te).getPaintSource();
        ConduitUtil.playStepSound(facade.getBlock().stepSound, world, bc.x, bc.y, bc.z);
      } else {
        ConduitUtil.playStepSound(Block.soundTypeMetal, world, bc.x, bc.y, bc.z);
      }
    }
  }

  @Override
  public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
    ItemStack ret = null;

    if (target != null && target.hitInfo instanceof CollidableComponent) {
      CollidableComponent cc = (CollidableComponent) target.hitInfo;
      TileConduitBundle bundle = (TileConduitBundle) world.getTileEntity(pos);
      IConduit conduit = bundle.getConduit(cc.conduitType);
      if (conduit != null) {
        ret = conduit.createItem();
      } else if (cc.conduitType == null && bundle.hasFacade()) {
        // use the facade
        ret = new ItemStack(EnderIO.blockConduitFacade, 1, 0);
        PainterUtil2.setSourceBlock(ret, bundle.getPaintSource());
      }
    }
    return ret;
  }

  @Override
  public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
    return getPickBlock(target, world, pos, null);
  }

  @Override
  public int getDamageValue(World world, BlockPos pos) {
    IBlockState f = getPaintSource(null, world, pos);
    return f == null ? 0 : f.getBlock().getMetaFromState(f);
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  @Override
  public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IConduitBundle)) {
      return false;
    }
    IConduitBundle con = (IConduitBundle) te;
    return con.hasFacade();
  }

  @Override
  public boolean canReplace(World worldIn, BlockPos pos, EnumFacing side, ItemStack stack) {
    return super.canReplace(worldIn, pos, side, stack);
  }

  @Override
  public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean isFullCube() {  
    return false;
  }

  @Override
  public int getLightOpacity() {
    return 0;
  }
  
  @Override
  public int getLightOpacity(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IConduitBundle)) {
      return super.getLightOpacity(world, pos);
    }
    IConduitBundle con = (IConduitBundle) te;
    return con.getLightOpacity();    
  }

  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IConduitBundle)) {
      return super.getLightValue(world, pos);
    }
    IConduitBundle con = (IConduitBundle) te;
    int result = 0;
    if (con.hasFacade()) {
      IBlockState paintSource = con.getPaintSource();
      result = paintSource.getBlock().getLightValue();
      if (paintSource.getBlock().isOpaqueCube()) {
        return result;
      }
    }
    Collection<IConduit> conduits = con.getConduits();
    for (IConduit conduit : conduits) {
      result += conduit.getLightValue();
    }
    return result > 15 ? 15 : result;
  }

  @Override
  public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
    IConduitBundle te = getTileEntity(worldIn, pos);
    if(te != null &&  te.hasFacade() && te.getFacadeRenderedAs() == FacadeRenderState.WIRE_FRAME) {
      return 255;
    }
    return super.getMixedBrightnessForBlock(worldIn, pos);
  }

  @Override
  public float getBlockHardness(World world, BlockPos pos) {
    IConduitBundle te = getTileEntity(world, pos);
    if(te == null) {
      return super.getBlockHardness(world, pos);
    }    
    return te.getFacadeType() == EnumFacadeType.HARDENED ? blockHardness * 10 : blockHardness;
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity par1Entity, Explosion explosion) {
    float resist = getExplosionResistance(par1Entity);
    IConduitBundle te = (IConduitBundle) world.getTileEntity(pos);
    return te != null && te.getFacadeType() == EnumFacadeType.HARDENED ? resist * 10 : resist;
  }

  @SubscribeEvent
  public void onBreakSpeed(BreakSpeed event) {
    if (event.state.getBlock() == this) {
      ItemStack held = event.entityPlayer.getCurrentEquippedItem();
      if (held == null || held.getItem().getHarvestLevel(held, "pickaxe") == -1) {
        event.newSpeed += 2;
      }
      IConduitBundle te = (IConduitBundle) event.entity.worldObj.getTileEntity(event.pos);
      if (te != null && te.getFacadeType() == EnumFacadeType.HARDENED) {
        if (!ConduitUtil.isSolidFacadeRendered(te, event.entityPlayer)) {
          event.newSpeed *= 6;
        } else {
          event.newSpeed *= 2;
        }
      }
    }
  }

  @Override
  public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
    IRedstoneConduit con = getRedstoneConduit(world, pos);
    if (con == null) {
      return 0;
    }
    return con.isProvidingStrongPower(side);
  }

  @Override
  public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
    IRedstoneConduit con = getRedstoneConduit(world, pos);
    if (con == null) {
      return 0;
    }

    return con.isProvidingWeakPower(side);
  }

  @Override
  public boolean canProvidePower() {
    return true;
  }

  @Override
  public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    IConduitBundle te = (IConduitBundle) world.getTileEntity(pos);
    if (te == null) {
      return true;
    }

    boolean breakBlock = true;
    List<ItemStack> drop = new ArrayList<ItemStack>();
    if (ConduitUtil.isSolidFacadeRendered(te, player)) {
      breakBlock = false;
      ItemStack fac = new ItemStack(EnderIO.blockConduitFacade, 1, te.getFacadeType().ordinal());
      PainterUtil2.setSourceBlock(fac, te.getPaintSource());
      drop.add(fac);

      ConduitUtil.playBreakSound(te.getPaintSource().getBlock().stepSound, world, pos.getX(), pos.getY(), pos.getZ());
      te.setPaintSource(null);
      te.setFacadeType(EnumFacadeType.BASIC);
    }

    if (breakBlock) {
      List<RaytraceResult> results = doRayTraceAll(world, pos.getX(), pos.getY(), pos.getZ(), player);
      RaytraceResult.sort(Util.getEyePosition(player), results);
      for (RaytraceResult rt : results) {
        if (breakConduit(te, drop, rt, player)) {
          break;
        }
      }
    }

    breakBlock = te.getConduits().isEmpty() && !te.hasFacade();

    if (!breakBlock) {
      world.markBlockForUpdate(pos);
    }

    if (!world.isRemote && !player.capabilities.isCreativeMode) {
      for (ItemStack st : drop) {
        Util.dropItems(world, st, pos, false);
      }
    }

    if (breakBlock) {
      world.setBlockToAir(pos);
      return true;
    }
    return false;
  }

  private boolean breakConduit(IConduitBundle te, List<ItemStack> drop, RaytraceResult rt, EntityPlayer player) {
    if (rt == null || rt.component == null) {
      return false;
    }
    Class<? extends IConduit> type = rt.component.conduitType;
    if (!ConduitUtil.renderConduit(player, type)) {
      return false;
    }

    if (type == null) {
      // broke a connector so drop any conduits with no connections as there
      // is no other way to remove these
      List<IConduit> cons = new ArrayList<IConduit>(te.getConduits());
      boolean droppedUnconected = false;
      for (IConduit con : cons) {
        if (con.getConduitConnections().isEmpty() && con.getExternalConnections().isEmpty() && ConduitUtil.renderConduit(player, con)) {
          te.removeConduit(con);
          drop.addAll(con.getDrops());
          droppedUnconected = true;
        }
      }
      // If there isn't, then drop em all
      if (!droppedUnconected) {
        for (IConduit con : cons) {
          if (ConduitUtil.renderConduit(player, con)) {
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

    BlockCoord bc = te.getLocation();
    ConduitUtil.playBreakSound(Block.soundTypeMetal, te.getBundleWorldObj(), bc.x, bc.y, bc.z);

    return true;
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {

    TileEntity tile = world.getTileEntity(pos);
    if (!(tile instanceof IConduitBundle)) {
      return;
    }
    IConduitBundle te = (IConduitBundle) tile;
    te.onBlockRemoved();
    world.removeTileEntity(pos);
  }

  @Override
  public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
    ItemStack equipped = player.getCurrentEquippedItem();
    if (!player.isSneaking() || equipped == null || equipped.getItem() != EnderIO.itemYetaWench) {
      return;
    }
    ConduitUtil.openConduitGui(world, pos, player);
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {

    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    IConduitBundle bundle = (IConduitBundle) world.getTileEntity(pos);
    if (bundle == null) {
      return false;
    }

    ItemStack stack = player.getCurrentEquippedItem();
    if (stack != null && Block.getBlockFromItem(stack.getItem()) == EnderIO.blockConduitFacade) {
      // add or replace facade
      return handleFacadeClick(world, pos, player, side, bundle, stack);

    } else if (ConduitUtil.isConduitEquipped(player)) {
      // Add conduit
      if (player.isSneaking()) {
        return false;
      }
      if (handleConduitClick(world, x, y, z, player, bundle, stack)) {
        return true;
      }

    } else if (ConduitUtil.isProbeEquipped(player)) {
      // Handle copy / paste of settings
      if (handleConduitProbeClick(world, x, y, z, player, bundle, stack)) {
        return true;
      }
    } else if (ToolUtil.isToolEquipped(player) && player.isSneaking()) {
      // Break conduit with tool
      if (handleWrenchClick(world, x, y, z, player)) {
        return true;
      }
    }

    // Check conduit defined actions
    RaytraceResult closest = doRayTrace(world, x, y, z, player);
    List<RaytraceResult> all = null;
    if (closest != null) {
      all = doRayTraceAll(world, x, y, z, player);
    }

    if (closest != null && closest.component != null && closest.component.data instanceof ConduitConnectorType) {

      ConduitConnectorType conType = (ConduitConnectorType) closest.component.data;
      if (conType == ConduitConnectorType.INTERNAL) {
        boolean result = false;
        // if its a connector pass the event on to all conduits
        for (IConduit con : bundle.getConduits()) {
          if (ConduitUtil.renderConduit(player, con.getCollidableType())
              && con.onBlockActivated(player, getHitForConduitType(all, con.getCollidableType()), all)) {
            bundle.getEntity().markDirty();
            result = true;
          }

        }
        if (result) {
          return true;
        }
      } else {
        if (!world.isRemote) {
          player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + closest.component.dir.ordinal(), world, x, y, z);
        }
        return true;
      }
    }

    if (closest == null || closest.component == null || closest.component.conduitType == null && all == null) {
      // Nothing of interest hit
      return false;
    }

    // Conduit specific actions
    if (all != null) {
      RaytraceResult.sort(Util.getEyePosition(player), all);
      for (RaytraceResult rr : all) {
        if (ConduitUtil.renderConduit(player, rr.component.conduitType) && !(rr.component.data instanceof ConduitConnectorType)) {

          IConduit con = bundle.getConduit(rr.component.conduitType);
          if (con != null && con.onBlockActivated(player, rr, all)) {
            bundle.getEntity().markDirty();
            return true;
          }
        }
      }
    } else {
      IConduit closestConduit = bundle.getConduit(closest.component.conduitType);
      if (closestConduit != null && ConduitUtil.renderConduit(player, closestConduit) && closestConduit.onBlockActivated(player, closest, all)) {
        bundle.getEntity().markDirty();
        return true;
      }
    }
    return false;

  }

  private boolean handleWrenchClick(World world, int x, int y, int z, EntityPlayer player) {
    ITool tool = ToolUtil.getEquippedTool(player);
    if (tool != null) {
      if (tool.canUse(player.getCurrentEquippedItem(), player, new BlockPos(x, y, z))) {
        if (!world.isRemote) {
          removedByPlayer(world, new BlockPos(x, y, z), player, true);
          tool.used(player.getCurrentEquippedItem(), player, new BlockPos(x, y, z));
        }
        return true;
      }
    }
    return false;
  }

  private boolean handleConduitProbeClick(World world, int x, int y, int z, EntityPlayer player, IConduitBundle bundle, ItemStack stack) {
    if (stack.getItemDamage() != 1) {
      return false; // not in copy paste mode
    }
    RaytraceResult rr = doRayTrace(world, x, y, z, player);
    if (rr == null || rr.component == null) {
      return false;
    }
    return ItemConduitProbe.copyPasteSettings(player, stack, bundle, rr.component.dir);
  }

  private boolean handleConduitClick(World world, int x, int y, int z, EntityPlayer player, IConduitBundle bundle, ItemStack stack) {
    IConduitItem equipped = (IConduitItem) stack.getItem();
    if (!bundle.hasType(equipped.getBaseConduitType())) {
      if (!world.isRemote) {
        bundle.addConduit(equipped.createConduit(stack, player));
        ConduitUtil.playBreakSound(soundTypeMetal, world, x, y, z);
        if (!player.capabilities.isCreativeMode) {
          player.getCurrentEquippedItem().stackSize--;
        }
      }
      return true;
    }
    return false;
  }

  public boolean handleFacadeClick(World world, BlockPos pos, EntityPlayer player, EnumFacing side, IConduitBundle bundle, ItemStack stack) {

    // Add facade
    if (player.isSneaking()) {
      return false;
    }

    IBlockState facadeID = PainterUtil2.getSourceBlock(player.getCurrentEquippedItem());
    if (facadeID == null) {
      return false;
    }

    int facadeType = player.getCurrentEquippedItem().getItemDamage();

    if (bundle.hasFacade()) {
      if (!ConduitUtil.isSolidFacadeRendered(bundle, player) || facadeEquals(bundle, facadeID, facadeType)) {
        return false;
      }
      if (!world.isRemote && !player.capabilities.isCreativeMode) {
        ItemStack fac = new ItemStack(EnderIO.blockConduitFacade, 1, bundle.getFacadeType().ordinal());
        PainterUtil2.setSourceBlock(fac, bundle.getPaintSource());
        Util.dropItems(world, fac, pos, false);
      }
    }
    bundle.setPaintSource(facadeID);
    bundle.setFacadeType(EnumFacadeType.getTypeFromMeta(facadeType));
    if (!world.isRemote) {
      ConduitUtil.playPlaceSound(facadeID.getBlock().stepSound, world, pos.getX(), pos.getY(), pos.getZ());
    }
    if (!player.capabilities.isCreativeMode) {
      stack.stackSize--;
    }
    world.markBlockForUpdate(pos);
    bundle.getEntity().markDirty();
    return true;
  }

  private boolean facadeEquals(IConduitBundle bundle, IBlockState b, int facadeType) {
    IBlockState a = bundle.getPaintSource();
    if(a == null) {
      return false;
    }
    if(a.getBlock() != b.getBlock()) {
      return false;
    }
    return a.getBlock().getMetaFromState(a) == b.getBlock().getMetaFromState(b);
  }

  @Override
  public boolean tryRotateFacade(World world, int x, int y, int z, EnumFacing side) {
    IConduitBundle bundle = (IConduitBundle) world.getTileEntity(new BlockPos(x, y, z));
    if (bundle == null || !bundle.hasFacade()) {
      return false;
    }
    bundle.setFacing(side);
    world.markBlockForUpdate(new BlockPos(x, y, z));
    return true;
  }

  @Override
  public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    if (id == GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR) {
      return null;
    }
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof IConduitBundle) {
      return new ExternalConnectionContainer(player.inventory, (IConduitBundle) te, EnumFacing.values()[id - GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE]);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof IConduitBundle) {
      if (id == GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR) {
        return new GuiExternalConnectionSelector((IConduitBundle) te);
      }
      return new GuiExternalConnection(player.inventory, (IConduitBundle) te, EnumFacing.values()[id - GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE]);
    }
    return null;
  }

  private RaytraceResult getHitForConduitType(List<RaytraceResult> all, Class<? extends IConduit> collidableType) {
    for (RaytraceResult rr : all) {
      if (rr.component != null && rr.component.conduitType == collidableType) {
        return rr;
      }
    }
    return null;
  }

  @Override

  public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
    TileEntity tile = world.getTileEntity(pos);
    if ((tile instanceof IConduitBundle)) {
      ((IConduitBundle) tile).onNeighborBlockChange(neighborBlock);
    }
  }

  @Override
  public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
    TileEntity conduit = world.getTileEntity(pos);
    if (conduit instanceof IConduitBundle) {
      ((IConduitBundle) conduit).onNeighborChange(world, null, null);
    }
  }

  @Override
  public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> arraylist,
      Entity par7Entity) {

    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IConduitBundle)) {
      return;
    }
    IConduitBundle con = (IConduitBundle) te;
    if (con.hasFacade()) {
      setBlockBounds(0, 0, 0, 1, 1, 1);
      super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
    } else {

      Collection<CollidableComponent> bounds = con.getCollidableComponents();
      for (CollidableComponent bnd : bounds) {
        setBlockBounds(bnd.bound.minX, bnd.bound.minY, bnd.bound.minZ, bnd.bound.maxX, bnd.bound.maxY, bnd.bound.maxZ);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
      }

      if (con.getConduits().isEmpty()) { // just in case
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, pos, state, axisalignedbb, arraylist, par7Entity);
      }
    }

    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {

    TileEntity te = world.getTileEntity(pos);
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    if (!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle con = (IConduitBundle) te;

    BoundingBox minBB = new BoundingBox(1, 1, 1, 0, 0, 0);

    if (!ConduitUtil.isSolidFacadeRendered(con, EnderIO.proxy.getClientPlayer())) {

      List<RaytraceResult> results = doRayTraceAll(world, pos.getX(), pos.getY(), pos.getZ(), player);
      Iterator<RaytraceResult> iter = results.iterator();
      while (iter.hasNext()) {
        CollidableComponent component = iter.next().component;
        if (component == null || (component.conduitType == null && component.data != ConduitConnectorType.EXTERNAL)) {
          iter.remove();
        }
      }

      // This is an ugly special case, TODO fix this
      for (RaytraceResult hit : results) {
        IInsulatedRedstoneConduit cond = con.getConduit(IInsulatedRedstoneConduit.class);
        if (cond != null && hit.component != null && cond.getExternalConnections().contains(hit.component.dir) && !cond.isSpecialConnection(hit.component.dir)
            && hit.component.data == InsulatedRedstoneConduit.COLOR_CONTROLLER_ID) {
          minBB = hit.component.bound;
        }
      }

      if (!minBB.isValid()) {
        RaytraceResult hit = RaytraceResult.getClosestHit(Util.getEyePosition(player), results);
        if (hit != null && hit.component != null && hit.component.bound != null) {
          minBB = hit.component.bound;
          if (hit.component.conduitType == null) {
            EnumFacing dir = hit.component.dir.getOpposite();
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

    if (!minBB.isValid()) {
      minBB = new BoundingBox(0, 0, 0, 1, 1, 1);
    }

    return new AxisAlignedBB(pos.getX() + minBB.minX, pos.getY() + minBB.minY, pos.getZ() + minBB.minZ, pos.getX() + minBB.maxX, pos.getY() + minBB.maxY,
        pos.getZ() + minBB.maxZ);
  }

  @Override
  public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 origin, Vec3 direction) {

    RaytraceResult raytraceResult = doRayTrace(world, pos.getX(), pos.getY(), pos.getZ(), origin, direction, null);
    MovingObjectPosition ret = null;
    if (raytraceResult != null) {
      ret = raytraceResult.movingObjectPosition;
      if (ret != null) {
        ret.hitInfo = raytraceResult.component;
      }
    }

    return ret;
  }

  public RaytraceResult doRayTrace(World world, int x, int y, int z, EntityPlayer entityPlayer) {
    List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, entityPlayer);
    if (allHits == null) {
      return null;
    }
    Vec3 origin = Util.getEyePosition(entityPlayer);
    return RaytraceResult.getClosestHit(origin, allHits);
  }

  public List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z, EntityPlayer entityPlayer) {
    double pitch = Math.toRadians(entityPlayer.rotationPitch);
    double yaw = Math.toRadians(entityPlayer.rotationYaw);

    double dirX = -Math.sin(yaw) * Math.cos(pitch);
    double dirY = -Math.sin(pitch);
    double dirZ = Math.cos(yaw) * Math.cos(pitch);

    double reachDistance = EnderIO.proxy.getReachDistanceForPlayer(entityPlayer);

    Vec3 origin = Util.getEyePosition(entityPlayer);
    Vec3 direction = origin.addVector(dirX * reachDistance, dirY * reachDistance, dirZ * reachDistance);
    return doRayTraceAll(world, x, y, z, origin, direction, entityPlayer);
  }

  private RaytraceResult doRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction, EntityPlayer entityPlayer) {
    List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, origin, direction, entityPlayer);
    if (allHits == null) {
      return null;
    }
    return RaytraceResult.getClosestHit(origin, allHits);
  }

  protected List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z, Vec3 origin, Vec3 direction, EntityPlayer player) {

    BlockPos pos = new BlockPos(x, y, z);
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    List<RaytraceResult> hits = new ArrayList<RaytraceResult>();

    if (player == null) {
      player = EnderIO.proxy.getClientPlayer();
    }

    if (ConduitUtil.isSolidFacadeRendered(bundle, player)) {
      setBlockBounds(0, 0, 0, 1, 1, 1);
      MovingObjectPosition hitPos = super.collisionRayTrace(world, pos, origin, direction);
      if (hitPos != null) {
        hits.add(new RaytraceResult(new CollidableComponent(null, BoundingBox.UNIT_CUBE, null, null), hitPos));
      }
    } else {
      ConduitDisplayMode mode = ConduitUtil.getDisplayMode(player);
      Collection<CollidableComponent> components = new ArrayList<CollidableComponent>(bundle.getCollidableComponents());
      for (CollidableComponent component : components) {
        if ((component.conduitType != null || mode == ConduitDisplayMode.ALL) && ConduitUtil.renderConduit(player, component.conduitType)) {
          setBlockBounds(component.bound.minX, component.bound.minY, component.bound.minZ, component.bound.maxX, component.bound.maxY, component.bound.maxZ);
          MovingObjectPosition hitPos = super.collisionRayTrace(world, pos, origin, direction);
          if (hitPos != null) {
            hits.add(new RaytraceResult(component, hitPos));
          }
        }
      }

      // safety to prevent unbreakable empty bundles in case of a bug
      if (bundle.getConduits().isEmpty() && !ConduitUtil.isFacadeHidden(bundle, player)) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
        MovingObjectPosition hitPos = super.collisionRayTrace(world, pos, origin, direction);
        if (hitPos != null) {
          hits.add(new RaytraceResult(null, hitPos));
        }
      }
    }

    setBlockBounds(0, 0, 0, 1, 1, 1);

    return hits;
  }

  private static IRedstoneConduit getRedstoneConduit(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    return bundle.getConduit(IRedstoneConduit.class);
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, IBlockState paintSource) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      ((IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintable.IPaintableTileEntity) {
      return ((IPaintableTileEntity) te).getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(Block block, ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    return getPaintSource(getDefaultState(), world, pos);
  }

  @Override
  public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
    return true;
  }
}
