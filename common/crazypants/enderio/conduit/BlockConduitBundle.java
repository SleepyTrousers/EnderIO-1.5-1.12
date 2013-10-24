package crazypants.enderio.conduit;

import static net.minecraftforge.common.ForgeDirection.getOrientation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.render.BoundingBox;
import crazypants.util.Util;

public class BlockConduitBundle extends Block implements ITileEntityProvider, IConnectableRedNet {

  private static final String KEY_CONNECTOR_ICON = "enderIO:conduitConnector";

  public static BlockConduitBundle create() {
    BlockConduitBundle result = new BlockConduitBundle();
    result.init();
    return result;
  }

  public static int rendererId = -1;

  private Icon connectorIcon;

  private Icon lastRemovedComponetIcon = null;

  private Random rand = new Random();

  protected BlockConduitBundle() {
    super(ModObject.blockConduitBundle.id, Material.ground);
    setHardness(0.5F);
    setBlockBounds(0.334f, 0.334f, 0.334f, 0.667f, 0.667f, 0.667f);
    setStepSound(Block.soundMetalFootstep);
    setUnlocalizedName(ModObject.blockConduitBundle.unlocalisedName);
    setCreativeTab(null);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addBlockHitEffects(World world, MovingObjectPosition target,
      EffectRenderer effectRenderer) {
    Icon tex = null;

    TileConduitBundle cb = (TileConduitBundle)
        world.getBlockTileEntity(target.blockX, target.blockY, target.blockZ);
    if(ConduitUtil.isSolidFacadeRendered(cb, Minecraft.getMinecraft().thePlayer)) {
      if(cb.getFacadeId() > 0) {
        tex = Block.blocksList[cb.getFacadeId()].getIcon(target.sideHit,
            cb.getFacadeMetadata());
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
    addBlockHitEffects(world, effectRenderer, target.blockX, target.blockY,
        target.blockZ, target.sideHit, tex);
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean addBlockDestroyEffects(World world, int x, int y, int z, int
      meta, EffectRenderer effectRenderer) {
    Icon tex = lastRemovedComponetIcon;
    byte b0 = 4;
    for (int j1 = 0; j1 < b0; ++j1) {
      for (int k1 = 0; k1 < b0; ++k1) {
        for (int l1 = 0; l1 < b0; ++l1) {
          double d0 = x + (j1 + 0.5D) / b0;
          double d1 = y + (k1 + 0.5D) / b0;
          double d2 = z + (l1 + 0.5D) / b0;
          int i2 = this.rand.nextInt(6);
          EntityDiggingFX fx = new EntityDiggingFX(world, d0, d1, d2, d0 - x - 0.5D,
              d1 - y - 0.5D, d2 - z - 0.5D, this, i2, 0).applyColourMultiplier(x, y, z);
          fx.setParticleIcon(tex);
          effectRenderer.addEffect(fx);
        }
      }
    }
    return true;

  }

  @SideOnly(Side.CLIENT)
  private void addBlockHitEffects(World world, EffectRenderer effectRenderer,
      int x, int y, int z, int side, Icon tex) {
    float f = 0.1F;
    double d0 = x + rand.nextDouble() * (getBlockBoundsMaxX() -
        getBlockBoundsMinX() - f * 2.0F) + f + getBlockBoundsMinX();
    double d1 = y + rand.nextDouble() * (getBlockBoundsMaxY() -
        getBlockBoundsMinY() - f * 2.0F) + f + getBlockBoundsMinY();
    double d2 = z + rand.nextDouble() * (getBlockBoundsMaxZ() -
        getBlockBoundsMinZ() - f * 2.0F) + f + getBlockBoundsMinZ();
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
    EntityDiggingFX digFX = new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D,
        0.0D, this, side, 0);
    digFX.applyColourMultiplier(x, y,
        z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
    digFX.setParticleIcon(tex);
    effectRenderer.addEffect(digFX);
  }

  private void init() {
    LanguageRegistry.addName(this, ModObject.blockConduitBundle.name);
    GameRegistry.registerBlock(this, ModObject.blockConduitBundle.unlocalisedName);
    GameRegistry.registerTileEntity(TileConduitBundle.class, ModObject.blockConduitBundle.unlocalisedName + "TileEntity");
  }

  @Override
  public ItemStack getPickBlock(MovingObjectPosition target, World world, int
      x, int y, int z) {
    if(target != null && target.hitInfo instanceof CollidableComponent) {
      CollidableComponent cc = (CollidableComponent) target.hitInfo;
      TileConduitBundle bundle = (TileConduitBundle) world.getBlockTileEntity(x,
          y, z);
      IConduit conduit = bundle.getConduit(cc.conduitType);
      if(conduit != null) {
        return conduit.createItem();
      } else if(cc.conduitType == null && bundle.getFacadeId() > 0) {
        // use the facde
        ItemStack fac = new ItemStack(ModObject.itemConduitFacade.actualId, 1, 0);
        PainterUtil.setSourceBlock(fac, bundle.getFacadeId(),
            bundle.getFacadeMetadata());
        return fac;
      }
    }
    return null;
  }

  @Override
  public int getDamageValue(World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return 0;
    }
    IConduitBundle bun = (IConduitBundle) te;
    return bun.getFacadeId() > 0 ? bun.getFacadeMetadata() : 0;
  }

  @Override
  public int idDropped(int par1, Random par2Random, int par3) {
    return 0;
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  public Icon getConnectorIcon() {
    return connectorIcon;
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    connectorIcon = iconRegister.registerIcon(KEY_CONNECTOR_ICON);
    blockIcon = connectorIcon;
  }

  @Override
  public boolean isBlockSolidOnSide(World world, int x, int y, int z,
      ForgeDirection side) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return false;
    }
    IConduitBundle con = (IConduitBundle) te;
    if(con.getFacadeId() > 0) {
      return true;
    }
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
  public TileEntity createNewTileEntity(World world) {
    return null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileConduitBundle();
  }

  @Override
  public int getLightOpacity(World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return super.getLightOpacity(world, x, y, z);
    }
    IConduitBundle con = (IConduitBundle) te;
    return con.getLightOpacity();
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return super.getLightValue(world, x, y, z);
    }
    IConduitBundle con = (IConduitBundle) te;
    if(con.getFacadeId() > 0) {
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
  public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z,
      int par5) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return 0;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    IRedstoneConduit con = bundle.getConduit(IRedstoneConduit.class);
    if(con == null) {
      return 0;
    }
    return con.isProvidingStrongPower(getOrientation(par5));
  }

  @Override
  public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
      int par5) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return 0;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    IRedstoneConduit con = bundle.getConduit(IRedstoneConduit.class);
    if(con == null) {
      return 0;
    }
    return con.isProvidingWeakPower(getOrientation(par5));
  }

  @Override
  public boolean canProvidePower() {
    return true;
  }

  @Override
  public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side) {
    return RedNetConnectionType.CableAll;
  }

  @Override
  public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return new int[16];
    }
    IConduitBundle bundle = (IConduitBundle) te;
    IRedstoneConduit con = bundle.getConduit(IRedstoneConduit.class);
    if(con == null) {
      return new int[16];
    }
    return con.getOutputValues(world, x, y, z, side);
  }

  @Override
  public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return 0;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    IRedstoneConduit con = bundle.getConduit(IRedstoneConduit.class);
    if(con == null) {
      return 0;
    }
    return con.getOutputValue(world, x, y, z, side, subnet);
  }

  @Override
  public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof IConduitBundle) {
      IConduitBundle bundle = (IConduitBundle) te;
      IRedstoneConduit con = bundle.getConduit(IRedstoneConduit.class);
      if(con != null) {
        con.onNeighborBlockChange(world.getBlockId(x, y, z));
      }
    }

  }

  @Override
  public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof IConduitBundle) {
      IConduitBundle bundle = (IConduitBundle) te;
      IRedstoneConduit con = bundle.getConduit(IRedstoneConduit.class);
      if(con != null) {
        con.onNeighborBlockChange(world.getBlockId(x, y, z));
      }
    }

  }

  @Override
  public boolean removeBlockByPlayer(World world, EntityPlayer player, int x,
      int y, int z) {
    IConduitBundle te = (IConduitBundle) world.getBlockTileEntity(x, y, z);
    if(te == null) {
      return true;
    }

    boolean breakBlock = true;
    List<ItemStack> drop = new ArrayList<ItemStack>();
    if(ConduitUtil.isSolidFacadeRendered(te, player)) {
      breakBlock = false;
      ItemStack fac = new ItemStack(ModObject.itemConduitFacade.actualId, 1, 0);
      PainterUtil.setSourceBlock(fac, te.getFacadeId(), te.getFacadeMetadata());
      drop.add(fac);
      te.setFacadeId(-1);
      te.setFacadeMetadata(0);
    }

    if(breakBlock) {
      RaytraceResult rt = doRayTrace(world, x, y, z, player);
      if(rt != null && rt.component != null) {
        Class<? extends IConduit> type = rt.component.conduitType;
        if(type == null) {
          // broke a conector so drop any conduits with no connections as there
          // is no other way to remove these
          List<IConduit> cons = new ArrayList<IConduit>(te.getConduits());
          boolean droppedUnconected = false;
          for (IConduit con : cons) {
            if(con.getConduitConnections().isEmpty() &&
                con.getExternalConnections().isEmpty()) {
              te.removeConduit(con);
              drop.add(con.createItem());
              droppedUnconected = true;
            }
          }
          // If there isn't, then drop em all
          if(!droppedUnconected) {
            for (IConduit con : cons) {
              te.removeConduit(con);
              drop.add(con.createItem());
            }
          }
        } else {
          IConduit con = te.getConduit(type);
          if(con != null) {
            te.removeConduit(con);
            drop.add(con.createItem());
          }

        }
      }
    }

    breakBlock = te.getConduits().isEmpty() && !te.hasFacade();

    if(!world.isRemote && !player.capabilities.isCreativeMode) {
      for (ItemStack st : drop) {
        Util.dropItems(world, st, x, y, z, false);
      }
    }

    if(!breakBlock) {
      world.markBlockForUpdate(x, y, z);
      return false;
    }
    world.setBlockToAir(x, y, z);
    return true;
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, int par5, int
      par6) {
    IConduitBundle te = (IConduitBundle) world.getBlockTileEntity(x, y, z);
    if(te != null) {
      te.onBlockRemoved();
    }
    world.removeBlockTileEntity(x, y, z);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z,
      EntityPlayer player, int par6, float par7, float par8, float par9) {

    IConduitBundle bundle = (IConduitBundle) world.getBlockTileEntity(x, y, z);
    if(bundle == null) {
      return false;
    }

    ItemStack stack = player.getCurrentEquippedItem();
    if(stack != null && stack.itemID == ModObject.itemConduitFacade.actualId
        && !bundle.hasFacade()) {
      // Add facade
      if(player.isSneaking()) {
        return false;
      }

      bundle.setFacadeId(PainterUtil.getSourceBlockId(player.getCurrentEquippedItem()));
      bundle.setFacadeMetadata(PainterUtil.getSourceBlockMetadata(player.getCurrentEquippedItem()));
      if(!player.capabilities.isCreativeMode) {
        stack.stackSize--;
      }
      world.markBlockForUpdate(x, y, z);
      bundle.getEntity().onInventoryChanged();
      return true;

    } else if(ConduitUtil.isConduitEquipped(player)) {
      // Add conduit
      if(player.isSneaking()) {
        return false;
      }

      IConduitItem equipped = (IConduitItem) stack.getItem();
      if(!bundle.hasType(equipped.getBaseConduitType())) {
        bundle.addConduit(equipped.createConduit(stack));
        if(!player.capabilities.isCreativeMode) {
          world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, stepSound.getPlaceSound(),
              (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F);
          player.getCurrentEquippedItem().stackSize--;
        }
        return true;
      }

    }

    // Break conduit with tool
    if(ConduitUtil.isToolEquipped(player) && player.isSneaking()) {
      if(player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) player.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(player, x, y, z)) {
          if(!world.isRemote) {
            removeBlockByPlayer(world, player, x, y, z);
            if(player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
              ((IToolWrench) player.getCurrentEquippedItem().getItem()).wrenchUsed(player, x, y, z);
            }
          }
          return true;
        }
      }
    }

    // Check conduit defined actions
    RaytraceResult closest = doRayTrace(world, x, y, z, player);
    List<RaytraceResult> all = null;
    if(closest != null) {
      all = doRayTraceAll(world, x, y, z, player);
    }

    if(closest != null && closest.component != null && closest.component.data instanceof
        ConduitConnectorType) {
      boolean result = false;
      // if its a connector pass the event on to all conduits
      for (IConduit con : bundle.getConduits()) {
        if(con.onBlockActivated(player, closest, all)) {
          bundle.getEntity().onInventoryChanged();
          result = true;
        }

      }
      return result;
    }

    if(closest == null || closest.component == null || closest.component.conduitType ==
        null) {
      // Nothing of interest hit
      return false;
    }

    // Conduit specific actions
    if(bundle.getConduit(closest.component.conduitType) != null && bundle.getConduit(closest.component.conduitType).onBlockActivated(player,
        closest, all)) {
      bundle.getEntity().onInventoryChanged();
      return true;
    }
    return false;

  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int
      blockId) {
    TileEntity tile = world.getBlockTileEntity(x, y, z);
    if((tile instanceof IConduitBundle)) {
      ((IConduitBundle) tile).onNeighborBlockChange(blockId);
    }
  }

  @Override
  public void addCollisionBoxesToList(World world, int x, int y, int z,
      AxisAlignedBB axisalignedbb, @SuppressWarnings("rawtypes") List arraylist,
      Entity par7Entity) {

    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return;
    }
    IConduitBundle con = (IConduitBundle) te;
    if(con.getFacadeId() > 0) {
      setBlockBounds(0, 0, 0, 1, 1, 1);
      super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist,
          par7Entity);
    } else {

      Collection<CollidableComponent> bounds = con.getCollidableComponents();
      for (CollidableComponent bnd : bounds) {
        setBlockBounds(bnd.bound.minX, bnd.bound.minY, bnd.bound.minZ,
            bnd.bound.maxX, bnd.bound.maxY, bnd.bound.maxZ);
        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist,
            par7Entity);
      }

      if(con.getConduits().isEmpty()) { // just in case
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, arraylist,
            par7Entity);
      }
    }

    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

  }

  @Override
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int
      y, int z) {

    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle con = (IConduitBundle) te;

    BoundingBox minBB = new BoundingBox(1, 1, 1, 0, 0, 0);
    if(!ConduitUtil.isSolidFacadeRendered(con, EnderIO.proxy.getClientPlayer())) {

      Collection<CollidableComponent> bounds = con.getCollidableComponents();
      for (CollidableComponent bnd : bounds) {
        minBB = minBB.expandBy(bnd.bound);
      }

    } else {
      minBB = new BoundingBox(0, 0, 0, 1, 1, 1);
    }

    if(!minBB.isValid()) {
      minBB = new BoundingBox(0, 0, 0, 1, 1, 1);
    }

    return AxisAlignedBB.getBoundingBox(x + minBB.minX, y + minBB.minY,
        z + minBB.minZ, x + minBB.maxX, y + minBB.maxY, z +
            minBB.maxZ);
  }

  @Override
  public MovingObjectPosition collisionRayTrace(World world, int x, int y,
      int z, Vec3 origin, Vec3 direction) {
    RaytraceResult raytraceResult = doRayTrace(world, x, y, z, origin,
        direction, null);
    if(raytraceResult == null) {
      return null;
    }

    if(raytraceResult.movingObjectPosition != null) {
      raytraceResult.movingObjectPosition.hitInfo = raytraceResult.component;

    }
    return raytraceResult.movingObjectPosition;
  }

  public RaytraceResult doRayTrace(World world, int x, int y, int z,
      EntityPlayer entityPlayer) {
    List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, entityPlayer);
    if(allHits == null) {
      return null;
    }
    double posY = entityPlayer.posY + 1.62 - entityPlayer.yOffset;
    if(!world.isRemote && entityPlayer.isSneaking()) {
      posY -= 0.08;
    }
    Vec3 origin = Vec3.fakePool.getVecFromPool(entityPlayer.posX, posY,
        entityPlayer.posZ);
    return RaytraceResult.getClosestHit(origin, allHits);
  }

  public List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z,
      EntityPlayer entityPlayer) {
    double pitch = Math.toRadians(entityPlayer.rotationPitch);
    double yaw = Math.toRadians(entityPlayer.rotationYaw);

    double dirX = -Math.sin(yaw) * Math.cos(pitch);
    double dirY = -Math.sin(pitch);
    double dirZ = Math.cos(yaw) * Math.cos(pitch);

    double reachDistance = EnderIO.proxy.getReachDistanceForPlayer(entityPlayer);

    double posY = entityPlayer.posY + 1.62 - entityPlayer.yOffset;
    if(!world.isRemote && entityPlayer.isSneaking()) {
      posY -= 0.08;
    }
    Vec3 origin = Vec3.fakePool.getVecFromPool(entityPlayer.posX, posY,
        entityPlayer.posZ);
    Vec3 direction = origin.addVector(dirX * reachDistance, dirY *
        reachDistance, dirZ * reachDistance);
    return doRayTraceAll(world, x, y, z, origin, direction,
        entityPlayer);
  }

  private RaytraceResult doRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction, EntityPlayer entityPlayer) {
    List<RaytraceResult> allHits = doRayTraceAll(world, x, y, z, origin, direction, entityPlayer);
    if(allHits == null) {
      return null;
    }
    return RaytraceResult.getClosestHit(origin, allHits);
  }

  protected List<RaytraceResult> doRayTraceAll(World world, int x, int y, int z, Vec3
      origin, Vec3 direction, EntityPlayer player) {

    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    List<RaytraceResult> hits = new ArrayList<RaytraceResult>();

    if(ConduitUtil.isSolidFacadeRendered(bundle, player)) {
      setBlockBounds(0, 0, 0, 1, 1, 1);
      MovingObjectPosition hitPos = super.collisionRayTrace(world, x, y, z,
          origin, direction);
      if(hitPos != null) {
        hits.add(new RaytraceResult(new CollidableComponent(null,
            BoundingBox.UNIT_CUBE, ForgeDirection.UNKNOWN, null), hitPos));
      }
    } else {

      Collection<CollidableComponent> components =
          new ArrayList<CollidableComponent>(bundle.getCollidableComponents());
      for (CollidableComponent component : components) {
        setBlockBounds(component.bound.minX, component.bound.minY,
            component.bound.minZ, component.bound.maxX, component.bound.maxY,
            component.bound.maxZ);
        MovingObjectPosition hitPos = super.collisionRayTrace(world, x, y, z,
            origin, direction);
        if(hitPos != null) {
          hits.add(new RaytraceResult(component, hitPos));
        }
      }

      // safety to prevent unbreakable empty bundles in case of a bug
      if(bundle.getConduits().isEmpty() && !ConduitUtil.isFacadeHidden(bundle,
          player)) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
        MovingObjectPosition hitPos = super.collisionRayTrace(world, x, y, z,
            origin, direction);
        if(hitPos != null) {
          hits.add(new RaytraceResult(null, hitPos));
        }
      }
    }

    setBlockBounds(0, 0, 0, 1, 1, 1);

    return hits;
  }

}
