package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import crazypants.enderio.conduit.gui.*;
import mods.immibis.core.api.multipart.IMultipartRenderingBlockMarker;
import mods.immibis.core.api.multipart.IMultipartSystem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadeType;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.gui.item.PacketExistingItemFilterSnapshot;
import crazypants.enderio.conduit.gui.item.PacketModItemFilter;
import crazypants.enderio.conduit.liquid.PacketFluidLevel;
import crazypants.enderio.conduit.packet.PacketConnectionMode;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.conduit.packet.PacketOCConduitSignalColor;
import crazypants.enderio.conduit.packet.PacketRedstoneConduitOutputStrength;
import crazypants.enderio.conduit.packet.PacketRedstoneConduitSignalColor;
import crazypants.enderio.conduit.packet.PacketRoundRobinMode;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.item.IRotatableFacade;
import crazypants.enderio.item.ItemConduitProbe;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.ToolUtil;
import crazypants.util.IFacade;

@Optional.InterfaceList({
  @Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetOmniNode", modid = "MineFactoryReloaded"),
  @Interface(iface = "mods.immibis.core.api.multipart.IMultipartRenderingBlockMarker", modid = "ImmibisMicroblocks")
})
public class BlockConduitBundle extends BlockEio implements IGuiHandler, IFacade, IRotatableFacade, IRedNetOmniNode, IMultipartRenderingBlockMarker {

  private static final String KEY_CONNECTOR_ICON = "enderIO:conduitConnector";
  private static final String KEY_CONNECTOR_ICON_EXTERNAL = "enderIO:conduitConnectorExternal";

  public static BlockConduitBundle create() {

    MinecraftForge.EVENT_BUS.register(ConduitNetworkTickHandler.instance);
    FMLCommonHandler.instance().bus().register(ConduitNetworkTickHandler.instance);

    PacketHandler.INSTANCE.registerMessage(PacketFluidLevel.class, PacketFluidLevel.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketExtractMode.class, PacketExtractMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketConnectionMode.class, PacketConnectionMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketItemConduitFilter.class, PacketItemConduitFilter.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketExistingItemFilterSnapshot.class, PacketExistingItemFilterSnapshot.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketModItemFilter.class, PacketModItemFilter.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketFluidFilter.class, PacketFluidFilter.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketFluidChannel.class, PacketFluidChannel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketRedstoneConduitSignalColor.class, PacketRedstoneConduitSignalColor.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketRedstoneConduitOutputStrength.class, PacketRedstoneConduitOutputStrength.class, PacketHandler.nextID(),
        Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketOpenConduitUI.class, PacketOpenConduitUI.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketSlotVisibility.class, PacketSlotVisibility.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketOCConduitSignalColor.class, PacketOCConduitSignalColor.class,
        PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketRoundRobinMode.class, PacketRoundRobinMode.class, PacketHandler.nextID(), Side.SERVER);

    BlockConduitBundle result = new BlockConduitBundle();
    result.init();
    MinecraftForge.EVENT_BUS.register(result);
    return result;
  }

  public static int rendererId = -1;

  private IIcon connectorIcon, connectorIconExternal;

  private IIcon lastRemovedComponetIcon = null;

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
      public String getStepResourcePath() {
        return "EnderIO:" + soundName + ".step";
      }
    };
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
    if (MicroblocksUtil.supportMicroblocks() && IM__addHitEffects(world, target, effectRenderer)) {
      return true;
    }
    
    IIcon tex = null;

    TileConduitBundle cb = (TileConduitBundle) world.getTileEntity(target.blockX, target.blockY, target.blockZ);
    if(ConduitUtil.isSolidFacadeRendered(cb, Minecraft.getMinecraft().thePlayer)) {
      if(cb.getFacadeId() != null) {
        tex = cb.getFacadeId().getIcon(target.sideHit, cb.getFacadeMetadata());
      }
    } else if(target.hitInfo instanceof CollidableComponent) {
      CollidableComponent cc = (CollidableComponent) target.hitInfo;
      IConduit con = cb.getConduit(cc.conduitType);
      if(con != null) {
        tex = con.getTextureForState(cc);
      }
    }
    if(tex == null) {
      tex = blockIcon;
    }
    lastRemovedComponetIcon = tex;
    addBlockHitEffects(world, effectRenderer, target.blockX, target.blockY, target.blockZ, target.sideHit, tex);
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
    if (MicroblocksUtil.supportMicroblocks() && IM__addDestroyEffects(world, x, y, z, meta, effectRenderer)) {
      return true;
    }

    IIcon tex = lastRemovedComponetIcon;
    byte b0 = 4;
    for (int j1 = 0; j1 < b0; ++j1) {
      for (int k1 = 0; k1 < b0; ++k1) {
        for (int l1 = 0; l1 < b0; ++l1) {
          double d0 = x + (j1 + 0.5D) / b0;
          double d1 = y + (k1 + 0.5D) / b0;
          double d2 = z + (l1 + 0.5D) / b0;
          int i2 = rand.nextInt(6);
          EntityDiggingFX fx = new EntityDiggingFX(world, d0, d1, d2, d0 - x - 0.5D, d1 - y - 0.5D, d2 - z - 0.5D, this, i2, 0).applyColourMultiplier(x, y, z);
          fx.setParticleIcon(tex);
          effectRenderer.addEffect(fx);
        }
      }
    }
    return true;
  }

  @SideOnly(Side.CLIENT)
  private void addBlockHitEffects(World world, EffectRenderer effectRenderer, int x, int y, int z, int side, IIcon tex) {
    float f = 0.1F;
    double d0 = x + rand.nextDouble() * (getBlockBoundsMaxX() - getBlockBoundsMinX() - f * 2.0F) + f + getBlockBoundsMinX();
    double d1 = y + rand.nextDouble() * (getBlockBoundsMaxY() - getBlockBoundsMinY() - f * 2.0F) + f + getBlockBoundsMinY();
    double d2 = z + rand.nextDouble() * (getBlockBoundsMaxZ() - getBlockBoundsMinZ() - f * 2.0F) + f + getBlockBoundsMinZ();
    if(side == 0) {
      d1 = y + getBlockBoundsMinY() - f;
    } else if(side == 1) {
      d1 = y + getBlockBoundsMaxY() + f;
    } else if(side == 2) {
      d2 = z + getBlockBoundsMinZ() - f;
    } else if(side == 3) {
      d2 = z + getBlockBoundsMaxZ() + f;
    } else if(side == 4) {
      d0 = x + getBlockBoundsMinX() - f;
    } else if(side == 5) {
      d0 = x + getBlockBoundsMaxX() + f;
    }
    EntityDiggingFX digFX = new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, this, side, 0);
    digFX.applyColourMultiplier(x, y, z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
    digFX.setParticleIcon(tex);
    effectRenderer.addEffect(digFX);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onPlaySound(PlaySoundSourceEvent event) {
    String path = event.sound.getPositionedSoundLocation().getResourcePath();
    if ("silence.step".equals(path)) {
      ISound snd = event.sound;
      World world = EnderIO.proxy.getClientWorld();
      BlockCoord bc = new BlockCoord(snd.getXPosF(), snd.getYPosF(), snd.getZPosF());
      TileEntity te = bc.getTileEntity(world);
      if (te != null && te instanceof TileConduitBundle && ((TileConduitBundle) te).hasFacade()) {
        Block facade = getFacade(world, bc.x, bc.y, bc.z, -1);
        ConduitUtil.playHitSound(facade.stepSound, world, bc.x, bc.y, bc.z);
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
        Block facade = getFacade(world, bc.x, bc.y, bc.z, -1);
        ConduitUtil.playStepSound(facade.stepSound, world, bc.x, bc.y, bc.z);
      } else {
        ConduitUtil.playStepSound(Block.soundTypeMetal, world, bc.x, bc.y, bc.z);
      }
    }
  }

  @Override
  protected void init() {
    super.init();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + dir.ordinal(), this);
    }
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR, this);
  }

  @Override
  public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
    return getPickBlock(target, world, x, y, z, null);
  }
  
  @Override
  public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
    ItemStack ret = null;
    if (MicroblocksUtil.supportMicroblocks()) {
      ret = getMicroblockPickBlock(target, world, x, y, z, player);
    }
    if(ret == null && target != null && target.hitInfo instanceof CollidableComponent) {
      CollidableComponent cc = (CollidableComponent) target.hitInfo;
      TileConduitBundle bundle = (TileConduitBundle) world.getTileEntity(x, y, z);
      IConduit conduit = bundle.getConduit(cc.conduitType);
      if(conduit != null) {
        ret = conduit.createItem();
      } else if(cc.conduitType == null && bundle.getFacadeId() != null) {
        // use the facde
        ret = new ItemStack(EnderIO.itemConduitFacade, 1, 0);
        PainterUtil.setSourceBlock(ret, bundle.getFacadeId(), bundle.getFacadeMetadata());
      }
    }
    return ret;
  }

  @Override
  public int getDamageValue(World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return 0;
    }
    IConduitBundle bun = (IConduitBundle) te;
    return bun.getFacadeId() != null ? bun.getFacadeMetadata() : 0;
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  public IIcon getConnectorIcon(Object data) {
    return data == ConduitConnectorType.EXTERNAL ? connectorIconExternal : connectorIcon;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister IIconRegister) {
    connectorIcon = IIconRegister.registerIcon(KEY_CONNECTOR_ICON);
    connectorIconExternal = IIconRegister.registerIcon(KEY_CONNECTOR_ICON_EXTERNAL);
    blockIcon = connectorIcon;
  }

  @Override
  public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    if (MicroblocksUtil.supportMicroblocks() && IM__isSideSolid(world, x, y, z, side)) {
      return true;
    }

    TileEntity te = world.getTileEntity(x, y, z);
    if (!(te instanceof IConduitBundle)) {
      return false;
    }
    IConduitBundle con = (IConduitBundle) te;
    return con.hasFacade();
  }

  @Override
  public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getRenderType() {
    return rendererId;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return super.getLightOpacity(world, x, y, z);
    }
    IConduitBundle con = (IConduitBundle) te;
    return con.getLightOpacity();
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return super.getLightValue(world, x, y, z);
    }
    IConduitBundle con = (IConduitBundle) te;
    if(con.getFacadeId() != null && con.getFacadeId().isOpaqueCube()) {
      return 0;
    }
    Collection<IConduit> conduits = con.getConduits();
    int result = 0;
    for (IConduit conduit : conduits) {
      result += conduit.getLightValue();
    }
    return result;
  }

  @Override
  public float getBlockHardness(World world, int x, int y, int z) {
    IConduitBundle te = (IConduitBundle) world.getTileEntity(x, y, z);
    return te != null && te.getFacadeType() == FacadeType.HARDENED ? blockHardness * 10 : blockHardness;
  }

  @Override
  public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
    float resist = getExplosionResistance(par1Entity);
    IConduitBundle te = (IConduitBundle) world.getTileEntity(x, y, z);
    return te != null && te.getFacadeType() == FacadeType.HARDENED ? resist * 10 : resist;
  }

  @SubscribeEvent
  public void onBreakSpeed(BreakSpeed event) {
    if(event.block == this) {
      ItemStack held = event.entityPlayer.getCurrentEquippedItem();
      if(held == null || held.getItem().getHarvestLevel(held, "pickaxe") == -1) {
        event.newSpeed += 2;
      }
      IConduitBundle te = (IConduitBundle) event.entity.worldObj.getTileEntity(event.x, event.y, event.z);
      if(te != null && te.getFacadeType() == FacadeType.HARDENED) {
        if(!ConduitUtil.isSolidFacadeRendered(te, event.entityPlayer)) {
          event.newSpeed *= 6;
        } else {
          event.newSpeed *= 2;
        }
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getRenderBlockPass() {
    return 1;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean canRenderInPass(int pass) {
    return pass == 0 || pass == 1;
  }

  @Override
  public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int par5) {
    IRedstoneConduit con = getRedstoneConduit(world, x, y, z);
    if(con == null) {
      return 0;
    }
    return con.isProvidingStrongPower(ForgeDirection.getOrientation(par5));
  }

  @Override
  public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int par5) {
    IRedstoneConduit con = getRedstoneConduit(world, x, y, z);
    if(con == null) {
      return 0;
    }

    return con.isProvidingWeakPower(ForgeDirection.getOrientation(par5));
  }

  @Override
  public boolean canProvidePower() {
    return true;
  }

  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
    IConduitBundle te = (IConduitBundle) world.getTileEntity(x, y, z);
    if(te == null) {
      return true;
    }

    boolean breakBlock = true;
    List<ItemStack> drop = new ArrayList<ItemStack>();
    if(ConduitUtil.isSolidFacadeRendered(te, player)) {
      breakBlock = false;
      ItemStack fac = new ItemStack(EnderIO.itemConduitFacade, 1, te.getFacadeType().ordinal());
      PainterUtil.setSourceBlock(fac, te.getFacadeId(), te.getFacadeMetadata());
      drop.add(fac);
      ConduitUtil.playBreakSound(te.getFacadeId().stepSound, world, x, y, z);
      te.setFacadeId(null);
      te.setFacadeMetadata(0);
      te.setFacadeType(FacadeType.BASIC);
    }

    if(breakBlock) {
      List<RaytraceResult> results = doRayTraceAll(world, x, y, z, player);
      RaytraceResult.sort(Util.getEyePosition(player), results);
      for (RaytraceResult rt : results) {
        if(breakConduit(te, drop, rt, player)) {
          break;
        }
      }
    }

    breakBlock = te.getConduits().isEmpty() && !te.hasFacade();

    if(!breakBlock) {
      world.markBlockForUpdate(x, y, z);
    }

    // TODO no microblock sounds...not sure if fixable, need to contact immibis
    if (MicroblocksUtil.supportMicroblocks()) {
      IM__getDrops(drop, world, x, y, z, te.getEntity().getBlockMetadata(), 0);
    }

    if (!world.isRemote && !player.capabilities.isCreativeMode) {
      for (ItemStack st : drop) {
        Util.dropItems(world, st, x, y, z, false);
      }
    }

    if (breakBlock) {
      world.setBlockToAir(x, y, z);
      return true;
    }
    return false;
  }

  private boolean breakConduit(IConduitBundle te, List<ItemStack> drop, RaytraceResult rt, EntityPlayer player) {
    if(rt == null || rt.component == null) {
      return false;
    }
    Class<? extends IConduit> type = rt.component.conduitType;
    if(!ConduitUtil.renderConduit(player, type)) {
      return false;
    }

    if(type == null) {
      // broke a conector so drop any conduits with no connections as there
      // is no other way to remove these
      List<IConduit> cons = new ArrayList<IConduit>(te.getConduits());
      boolean droppedUnconected = false;
      for (IConduit con : cons) {
        if(con.getConduitConnections().isEmpty() && con.getExternalConnections().isEmpty() && ConduitUtil.renderConduit(player, con)) {
          te.removeConduit(con);
          drop.addAll(con.getDrops());
          droppedUnconected = true;
        }
      }
      // If there isn't, then drop em all
      if(!droppedUnconected) {
        for (IConduit con : cons) {
          if(ConduitUtil.renderConduit(player, con)) {
            te.removeConduit(con);
            drop.addAll(con.getDrops());
          }
        }
      }
    } else {
      IConduit con = te.getConduit(type);
      if(con != null) {
        te.removeConduit(con);
        drop.addAll(con.getDrops());
      }
    }
    
    BlockCoord bc = te.getLocation();
    ConduitUtil.playBreakSound(Block.soundTypeMetal, te.getWorld(), bc.x, bc.y, bc.z);

    return true;
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {

    TileEntity tile = world.getTileEntity(x, y, z);
    if(!(tile instanceof IConduitBundle)) {
      return;
    }
    IConduitBundle te = (IConduitBundle) tile;
    te.onBlockRemoved();
    world.removeTileEntity(x, y, z);
  }

  @Override
  public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
    ItemStack equipped = player.getCurrentEquippedItem();
    if(!player.isSneaking() || equipped == null || equipped.getItem() != EnderIO.itemYetaWench) {
      return;
    }
    ConduitUtil.openConduitGui(world, x, y, z, player);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9) {

    IConduitBundle bundle = (IConduitBundle) world.getTileEntity(x, y, z);
    if(bundle == null) {
      return false;
    }

    ItemStack stack = player.getCurrentEquippedItem();
    if(stack != null && stack.getItem() == EnderIO.itemConduitFacade) {
      // add or replace facade
      return handleFacadeClick(world, x, y, z, player, side, bundle, stack);

    } else if(ConduitUtil.isConduitEquipped(player)) {
      // Add conduit
      if(player.isSneaking()) {
        return false;
      }
      if(handleConduitClick(world, x, y, z, player, bundle, stack)) {
        return true;
      }

    } else if(ConduitUtil.isProbeEquipped(player)) {
      //Handle copy / paste of settings
      if(handleConduitProbeClick(world, x, y, z, player, bundle, stack)) {
        return true;
      }
    } else if(ToolUtil.isToolEquipped(player) && player.isSneaking()) {
      // Break conduit with tool
      if(handleWrenchClick(world, x, y, z, player)) {
        return true;
      }
    }

    // Check conduit defined actions
    RaytraceResult closest = doRayTrace(world, x, y, z, player);
    List<RaytraceResult> all = null;
    if(closest != null) {
      all = doRayTraceAll(world, x, y, z, player);
    }

    if(closest != null && closest.component != null && closest.component.data instanceof ConduitConnectorType) {

      ConduitConnectorType conType = (ConduitConnectorType) closest.component.data;
      if(conType == ConduitConnectorType.INTERNAL) {
        boolean result = false;
        // if its a connector pass the event on to all conduits
        for (IConduit con : bundle.getConduits()) {
          if(ConduitUtil.renderConduit(player, con.getCollidableType())
              && con.onBlockActivated(player, getHitForConduitType(all, con.getCollidableType()), all)) {
            bundle.getEntity().markDirty();
            result = true;
          }

        }
        if(result) {
          return true;
        }
      } else {
        if(!world.isRemote) {
          player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + closest.component.dir.ordinal(), world, x, y, z);
        }
        return true;
      }
    }

    if(closest == null || closest.component == null || closest.component.conduitType == null && all == null) {
      // Nothing of interest hit
      return false;
    }

    // Conduit specific actions
    if(all != null) {
      RaytraceResult.sort(Util.getEyePosition(player), all);
      for (RaytraceResult rr : all) {
        if(ConduitUtil.renderConduit(player, rr.component.conduitType) && !(rr.component.data instanceof ConduitConnectorType)) {

          IConduit con = bundle.getConduit(rr.component.conduitType);
          if(con != null && con.onBlockActivated(player, rr, all)) {
            bundle.getEntity().markDirty();
            return true;
          }
        }
      }
    } else {
      IConduit closestConduit = bundle.getConduit(closest.component.conduitType);
      if(closestConduit != null && ConduitUtil.renderConduit(player, closestConduit) && closestConduit.onBlockActivated(player, closest, all)) {
        bundle.getEntity().markDirty();
        return true;
      }
    }
    return false;

  }

  private boolean handleWrenchClick(World world, int x, int y, int z, EntityPlayer player) {
    ITool tool = ToolUtil.getEquippedTool(player);
    if(tool != null) {
      if(tool.canUse(player.getCurrentEquippedItem(), player, x, y, z)) {
        if(!world.isRemote) {
          removedByPlayer(world, player, x, y, z, true);
          tool.used(player.getCurrentEquippedItem(), player, x, y, z);
        }
        return true;
      }
    }
    return false;
  }

  private boolean handleConduitProbeClick(World world, int x, int y, int z, EntityPlayer player, IConduitBundle bundle, ItemStack stack) {
    if(stack.getItemDamage() != 1) {
      return false; //not in copy paste mode
    }
    RaytraceResult rr = doRayTrace(world, x, y, z, player);
    if(rr == null || rr.component == null) {
      return false;
    }
    return ItemConduitProbe.copyPasteSettings(player, stack, bundle, rr.component.dir);
  }

  private boolean handleConduitClick(World world, int x, int y, int z, EntityPlayer player, IConduitBundle bundle, ItemStack stack) {
    IConduitItem equipped = (IConduitItem) stack.getItem();
    if(!bundle.hasType(equipped.getBaseConduitType())) {
      if(!world.isRemote) {
        bundle.addConduit(equipped.createConduit(stack, player));
        ConduitUtil.playBreakSound(soundTypeMetal, world, x, y, z);
        if(!player.capabilities.isCreativeMode) {
          player.getCurrentEquippedItem().stackSize--;
        }
      }
      return true;
    }
    return false;
  }

  public boolean handleFacadeClick(World world, int x, int y, int z, EntityPlayer player, int side, IConduitBundle bundle, ItemStack stack) {
    if (MicroblocksUtil.supportMicroblocks() && hasMicroblocks(bundle)) {
      return false;
    }
    
    // Add facade
    if(player.isSneaking()) {
      return false;
    }

    Block facadeID = PainterUtil.getSourceBlock(player.getCurrentEquippedItem());
    if(facadeID == null) {
      return false;
    }

    int facadeMeta = PainterUtil.getSourceBlockMetadata(player.getCurrentEquippedItem());
    facadeMeta = PainterUtil.adjustFacadeMetadata(facadeID, facadeMeta, side);
    int facadeType = player.getCurrentEquippedItem().getItemDamage();

    if (bundle.hasFacade()) {
      if (!ConduitUtil.isSolidFacadeRendered(bundle, player) || facadeEquals(bundle, facadeID, facadeMeta, facadeType)) {
        return false;
      }
      if (!world.isRemote && !player.capabilities.isCreativeMode) {
        ItemStack fac = new ItemStack(EnderIO.itemConduitFacade, 1, bundle.getFacadeType().ordinal());
        PainterUtil.setSourceBlock(fac, bundle.getFacadeId(), bundle.getFacadeMetadata());
        Util.dropItems(world, fac, x, y, z, false);
      }
    }
    
    bundle.setFacadeId(facadeID);
    bundle.setFacadeMetadata(facadeMeta);
    bundle.setFacadeType(FacadeType.values()[facadeType]);
    if (!world.isRemote) {
      ConduitUtil.playPlaceSound(facadeID.stepSound, world, x, y, z);
    }
    if (!player.capabilities.isCreativeMode) {
      stack.stackSize--;
    }
    world.markBlockForUpdate(x, y, z);
    bundle.getEntity().markDirty();
    return true;
  }

  private boolean facadeEquals(IConduitBundle bundle, Block facadeID, int facadeMeta, int facadeType) {
    return bundle.getFacadeId().equals(facadeID) && bundle.getFacadeMetadata() == facadeMeta
        && bundle.getFacadeType().ordinal() == facadeType;
  }

  @Override
  public boolean tryRotateFacade(World world, int x, int y, int z, ForgeDirection axis) {
    IConduitBundle bundle = (IConduitBundle) world.getTileEntity(x, y, z);
    if(bundle == null) {
      return false;
    }

    int oldMeta = bundle.getFacadeMetadata();
    int newMeta = PainterUtil.rotateFacadeMetadata(bundle.getFacadeId(), oldMeta, axis);
    if(newMeta == oldMeta) {
      return false;
    }

    bundle.setFacadeMetadata(newMeta);
    world.markBlockForUpdate(x, y, z);
    bundle.getEntity().markDirty();
    return true;
  }

  @Override
  public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    if(id == GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR) {
      return null;
    }
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof IConduitBundle) {
      return new ExternalConnectionContainer(player.inventory, (IConduitBundle) te, ForgeDirection.values()[id - GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE]);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof IConduitBundle) {
      if(id == GuiHandler.GUI_ID_EXTERNAL_CONNECTION_SELECTOR) {
        return new GuiExternalConnectionSelector((IConduitBundle) te);
      }
      return new GuiExternalConnection(player.inventory, (IConduitBundle) te, ForgeDirection.values()[id - GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE]);
    }
    return null;
  }

  private RaytraceResult getHitForConduitType(List<RaytraceResult> all, Class<? extends IConduit> collidableType) {
    for (RaytraceResult rr : all) {
      if(rr.component != null && rr.component.conduitType == collidableType) {
        return rr;
      }
    }
    return null;
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
    TileEntity tile = world.getTileEntity(x, y, z);
    if((tile instanceof IConduitBundle)) {
      ((IConduitBundle) tile).onNeighborBlockChange(blockId);
    }
  }

  @Override
  public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
    TileEntity conduit = world.getTileEntity(x, y, z);
    if(conduit instanceof IConduitBundle) {
      ((IConduitBundle) conduit).onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
    }
  }

  @Override
  public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, @SuppressWarnings("rawtypes") List arraylist,
      Entity par7Entity) {
    
    if (MicroblocksUtil.supportMicroblocks()) {
      IM__addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, par7Entity);
    }

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return;
    }
    IConduitBundle con = (IConduitBundle) te;
    if(con.getFacadeId() != null) {
      setBlockBounds(0, 0, 0, 1, 1, 1);
      super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, par7Entity);
    } else {

      Collection<CollidableComponent> bounds = con.getCollidableComponents();
      for (CollidableComponent bnd : bounds) {
        setBlockBounds(bnd.bound.minX, bnd.bound.minY, bnd.bound.minZ, bnd.bound.maxX, bnd.bound.maxY, bnd.bound.maxZ);
        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, par7Entity);
      }

      if(con.getConduits().isEmpty()) { // just in case
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist, par7Entity);
      }
    }

    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {

    TileEntity te = world.getTileEntity(x, y, z);
    EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    if(!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle con = (IConduitBundle) te;

    BoundingBox minBB = new BoundingBox(1, 1, 1, 0, 0, 0);

    if(!ConduitUtil.isSolidFacadeRendered(con, EnderIO.proxy.getClientPlayer())) {

      List<RaytraceResult> results = doRayTraceAll(world, x, y, z, player);
      Iterator<RaytraceResult> iter = results.iterator();
      while (iter.hasNext()) {
        CollidableComponent component = iter.next().component;
        if(component == null || (component.conduitType == null && component.data != ConduitConnectorType.EXTERNAL)) {
          iter.remove();
        }
      }

      // This is an ugly special case, TODO fix this
      for (RaytraceResult hit : results) {
        IInsulatedRedstoneConduit cond = con.getConduit(IInsulatedRedstoneConduit.class);
        if(cond != null && hit.component != null && cond.getExternalConnections().contains(hit.component.dir) && !cond.isSpecialConnection(hit.component.dir)
            && hit.component.data == InsulatedRedstoneConduit.COLOR_CONTROLLER_ID) {
          minBB = hit.component.bound;
        }
      }

      if(!minBB.isValid()) {
        RaytraceResult hit = RaytraceResult.getClosestHit(Util.getEyePosition(player), results);
        if(hit != null && hit.component != null && hit.component.bound != null) {
          minBB = hit.component.bound;
          if(hit.component.conduitType == null) {
            ForgeDirection dir = hit.component.dir.getOpposite();
            float trans = 0.0125f;
            minBB = minBB.translate(dir.offsetX * trans, dir.offsetY * trans, dir.offsetZ * trans);
            float scale = 0.7f;
            minBB = minBB.scale(1 + Math.abs(dir.offsetX) * scale, 1 + Math.abs(dir.offsetY) * scale, 1 + Math.abs(dir.offsetZ) * scale);
          } else {
            minBB = minBB.scale(1.09, 1.09, 1.09);
          }
        }
      }
    } else {
      minBB = new BoundingBox(0, 0, 0, 1, 1, 1);
    }

    if(!minBB.isValid()) {
      minBB = new BoundingBox(0, 0, 0, 1, 1, 1);
    }

    return AxisAlignedBB.getBoundingBox(x + minBB.minX, y + minBB.minY, z + minBB.minZ, x + minBB.maxX, y + minBB.maxY, z + minBB.maxZ);
  }

  @Override
  public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {

    RaytraceResult raytraceResult = doRayTrace(world, x, y, z, origin, direction, null);
    MovingObjectPosition ret = null;
    if (raytraceResult != null) {
      ret = raytraceResult.movingObjectPosition;
      if (ret != null) {
        ret.hitInfo = raytraceResult.component;
      }
    }

    if (MicroblocksUtil.supportMicroblocks()) {
      return IM__collisionRayTrace(ret, world, x, y, z, origin, direction);
    }

    return ret;
  }

  public RaytraceResult doRayTrace(World world, int x, int y, int z, EntityPlayer entityPlayer) {
    List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, entityPlayer);
    if(allHits == null) {
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
    if(allHits == null) {
      return null;
    }
    return RaytraceResult.getClosestHit(origin, allHits);
  }

  protected List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z, Vec3 origin, Vec3 direction, EntityPlayer player) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    List<RaytraceResult> hits = new ArrayList<RaytraceResult>();

    if(player == null) {
      player = EnderIO.proxy.getClientPlayer();
    }

    if(ConduitUtil.isSolidFacadeRendered(bundle, player)) {
      setBlockBounds(0, 0, 0, 1, 1, 1);
      MovingObjectPosition hitPos = super.collisionRayTrace(world, x, y, z, origin, direction);
      if(hitPos != null) {
        hits.add(new RaytraceResult(new CollidableComponent(null, BoundingBox.UNIT_CUBE, ForgeDirection.UNKNOWN, null), hitPos));
      }
    } else {
      ConduitDisplayMode mode = ConduitUtil.getDisplayMode(player);
      Collection<CollidableComponent> components = new ArrayList<CollidableComponent>(bundle.getCollidableComponents());
      for (CollidableComponent component : components) {
        if((component.conduitType != null || mode == ConduitDisplayMode.ALL) && ConduitUtil.renderConduit(player, component.conduitType)) {
          setBlockBounds(component.bound.minX, component.bound.minY, component.bound.minZ, component.bound.maxX, component.bound.maxY, component.bound.maxZ);
          MovingObjectPosition hitPos = super.collisionRayTrace(world, x, y, z, origin, direction);
          if(hitPos != null) {
            hits.add(new RaytraceResult(component, hitPos));
          }
        }
      }

      // safety to prevent unbreakable empty bundles in case of a bug
      if(bundle.getConduits().isEmpty() && !ConduitUtil.isFacadeHidden(bundle, player)) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
        MovingObjectPosition hitPos = super.collisionRayTrace(world, x, y, z, origin, direction);
        if(hitPos != null) {
          hits.add(new RaytraceResult(null, hitPos));
        }
      }
    }

    setBlockBounds(0, 0, 0, 1, 1, 1);

    return hits;
  }

  @Override
  public int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return 0;
    }
    IConduitBundle cb = (IConduitBundle) te;
    return cb.getFacadeMetadata();
  }

  @Override
  public Block getFacade(IBlockAccess world, int x, int y, int z, int side) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return this;
    }
    IConduitBundle cb = (IConduitBundle) te;
    Block res = cb.getFacadeId();
    if(res == null) {
      return this;
    }
    return res;
  }

  @Override
  public Block getVisualBlock(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return getFacade(world, x, y, z, side.ordinal());
  }

  @Override
  public int getVisualMeta(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return getFacadeMetadata(world, x, y, z, side.ordinal());
  }

  @Override
  public boolean supportsVisualConnections() {
    return true;
  }

  @Override
  public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues) {
    IRedstoneConduit conduit = getRedstoneConduit(world, x, y, z);
    if(conduit == null) {
      return;
    }

    conduit.onInputsChanged(world, x, y, z, side, inputValues);
  }

  @Override
  public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue) {
    // Unused because only called in "Single" mode.
  }

  @Override
  public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {
    IRedstoneConduit conduit = getRedstoneConduit(world, x, y, z);
    if(conduit == null) {
      return null;
    }

    return conduit.getOutputValues(world, x, y, z, side);
  }

  @Override
  public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {
    IRedstoneConduit conduit = getRedstoneConduit(world, x, y, z);
    if(conduit == null) {
      return 0;
    }

    return conduit.getOutputValue(world, x, y, z, side, subnet);
  }

  @Override
  @Optional.Method(modid = "MineFactoryReloaded")
  public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side) {
    IRedstoneConduit conduit = getRedstoneConduit(world, x, y, z);
    if(conduit == null) {
      return RedNetConnectionType.None;
    }
    return conduit.canConnectToExternal(side, false) ? RedNetConnectionType.CableAll : RedNetConnectionType.None;
  }

  private static IRedstoneConduit getRedstoneConduit(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    return bundle.getConduit(IRedstoneConduit.class);
  }
  
  public ItemStack getMicroblockPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
    return IMultipartSystem.instance.hook_getPickBlock(target, world, x, y, z, player);
  }

  // IM Hooks

  private boolean IM__isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return IMultipartSystem.instance.hook_isSideSolid(world, x, y, z, side);
  }

  @SideOnly(Side.CLIENT)
  private boolean IM__addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
    return IMultipartSystem.instance.hook_addDestroyEffects(world, x, y, z, meta, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  private boolean IM__addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
    return IMultipartSystem.instance.hook_addHitEffects(worldObj, target, effectRenderer);
  }

  private MovingObjectPosition IM__collisionRayTrace(MovingObjectPosition cur, World world, int x, int y, int z, Vec3 src, Vec3 dst) {
    return IMultipartSystem.instance.hook_collisionRayTrace(cur, world, x, y, z, src, dst);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void IM__addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
    IMultipartSystem.instance.hook_addCollisionBoxesToList(world, x, y, z, mask, list, entity);
  }

  private ArrayList<ItemStack> IM__getDrops(List<ItemStack> cur, World world, int x, int y, int z, int metadata, int fortune) {
    return IMultipartSystem.instance.hook_getDrops(cur, world, x, y, z, metadata, fortune);
  }

  private boolean hasMicroblocks(IConduitBundle bundle) {
    return !bundle.getCoverSystem().getAllParts().isEmpty();
  }
}
