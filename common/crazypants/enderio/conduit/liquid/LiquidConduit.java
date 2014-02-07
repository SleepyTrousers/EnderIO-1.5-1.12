package crazypants.enderio.conduit.liquid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitPacketHandler;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

public class LiquidConduit extends AbstractTankConduit {

  static final int VOLUME_PER_CONNECTION = FluidContainerRegistry.BUCKET_VOLUME / 4;

  public static final String ICON_KEY = "enderio:liquidConduit";
  public static final String ICON_KEY_LOCKED = "enderio:liquidConduitLocked";
  public static final String ICON_CORE_KEY = "enderio:liquidConduitCore";
  public static final String ICON_EXTRACT_KEY = "enderio:liquidConduitExtract";
  public static final String ICON_EMPTY_EXTRACT_KEY = "enderio:emptyLiquidConduitExtract";
  public static final String ICON_INSERT_KEY = "enderio:liquidConduitInsert";
  public static final String ICON_EMPTY_INSERT_KEY = "enderio:emptyLiquidConduitInsert";

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        ICONS.put(ICON_KEY, register.registerIcon(ICON_KEY));
        ICONS.put(ICON_CORE_KEY, register.registerIcon(ICON_CORE_KEY));
        ICONS.put(ICON_EXTRACT_KEY, register.registerIcon(ICON_EXTRACT_KEY));
        ICONS.put(ICON_EMPTY_EXTRACT_KEY, register.registerIcon(ICON_EMPTY_EXTRACT_KEY));
        ICONS.put(ICON_EMPTY_INSERT_KEY, register.registerIcon(ICON_EMPTY_INSERT_KEY));
        ICONS.put(ICON_INSERT_KEY, register.registerIcon(ICON_INSERT_KEY));
        ICONS.put(ICON_KEY_LOCKED, register.registerIcon(ICON_KEY_LOCKED));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  private LiquidConduitNetwork network;

  private float lastSyncRatio = -99;

  private int currentPushToken;

  // -----------------------------

  private long lastEmptyTick = 0;
  private int numEmptyEvents = 0;

  public static final int MAX_EXTRACT_PER_TICK = Config.fluidConduitExtractRate;

  public static final int MAX_IO_PER_TICK = Config.fluidConduitMaxIoRate;

  private ForgeDirection startPushDir = ForgeDirection.DOWN;

  private final Set<BlockCoord> filledFromThisTick = new HashSet<BlockCoord>();

  private long ticksSinceFailedExtract = 0;

  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);
    if(world.isRemote) {
      return;
    }
    filledFromThisTick.clear();
    updateStartPushDir();
    doExtract();

    if(stateDirty) {
      getBundle().dirty();
      stateDirty = false;
      lastSyncRatio = tank.getFilledRatio();

    } else if((lastSyncRatio != tank.getFilledRatio() && world.getTotalWorldTime() % 2 == 0)) {

      //need to send a custom packet as we don't want want to trigger a full chunk update, just
      //need to get the required  values to the entity renderer        
      BlockCoord loc = getLocation();
      Packet packet = ConduitPacketHandler.createFluidConduitLevelPacket(this);
      PacketDispatcher.sendPacketToAllAround(loc.x, loc.y, loc.z, 64, world.provider.dimensionId, packet);

      lastSyncRatio = tank.getFilledRatio();
    }
  }

  private void doExtract() {

    BlockCoord loc = getLocation();
    if(!hasConnectionMode(ConnectionMode.INPUT)) {
      return;
    }

    // assume failure, reset to 0 if we do extract
    ticksSinceFailedExtract++;
    if(ticksSinceFailedExtract > 9 && ticksSinceFailedExtract % 10 != 0) {
      // after 10 ticks of failing, only check every 10 ticks
      return;
    }

    Fluid f = tank.getFluid() == null ? null : tank.getFluid().getFluid();
    int token = network == null ? -1 : network.getNextPushToken();
    for (ForgeDirection dir : externalConnections) {
      if(autoExtractForDir(dir)) {

        IFluidHandler extTank = getTankContainer(getLocation().getLocation(dir));
        if(extTank != null) {
          FluidStack couldDrain = extTank.drain(dir.getOpposite(), MAX_EXTRACT_PER_TICK, false);
          if(couldDrain != null && couldDrain.amount > 0 && canFill(dir, couldDrain.getFluid())) {
            int used = pushLiquid(dir, couldDrain, true, network == null ? -1 : network.getNextPushToken());
            extTank.drain(dir.getOpposite(), used, true);
            if(used > 0 && network != null && network.getFluidType() == null) {
              network.setFluidType(couldDrain);
            }
            if(used > 0) {
              ticksSinceFailedExtract = 0;
            }
          }
        }
      }
    }

  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

    if(network == null || resource == null) {
      return 0;
    }
    if(!canFill(from, resource.getFluid())) {
      return 0;
    }

    // Note: This is just a guard against mekansims pipes that will continuously
    // call
    // fill on us if we push liquid to them.
    if(filledFromThisTick.contains(getLocation().getLocation(from))) {
      return 0;
    }

    if(network.lockNetworkForFill()) {
      if(doFill) {
        filledFromThisTick.add(getLocation().getLocation(from));
      }
      try {
        int res = fill(from, resource, doFill, true, network == null ? -1 : network.getNextPushToken());
        if(doFill && externalConnections.contains(from) && network != null) {
          network.addedFromExternal(res);
        }
        return res;
      } finally {
        network.unlockNetworkFromFill();

      }
    } else {
      return 0;
    }

  }

  public int fill(ForgeDirection from, FluidStack resource, boolean doFill, boolean doPush, int pushToken) {
    if(resource == null || resource.amount <= 0) {
      return 0;
    }

    if(!canFill(from, resource.getFluid())) {
      return 0;
    }

    if(network == null) {
      return 0;
    }
    if(network.canAcceptLiquid(resource)) {
      network.setFluidType(resource);
    } else {
      return 0;
    }
    //int recieveAmount = resource.amount;
    resource.amount = Math.min(MAX_IO_PER_TICK, resource.amount);

    if(doPush) {
      return pushLiquid(from, resource, doFill, pushToken);
    } else {
      return tank.fill(resource, doFill);
    }
  }

  private void updateStartPushDir() {

    ForgeDirection newVal = getNextDir(startPushDir);
    boolean foundNewStart = false;
    while (newVal != startPushDir && !foundNewStart) {
      foundNewStart = getConduitConnections().contains(newVal) || getExternalConnections().contains(newVal);
      newVal = getNextDir(newVal);
    }
    startPushDir = newVal;
  }

  private ForgeDirection getNextDir(ForgeDirection dir) {
    if(dir.ordinal() >= ForgeDirection.UNKNOWN.ordinal() - 1) {
      return ForgeDirection.VALID_DIRECTIONS[0];
    }
    return ForgeDirection.VALID_DIRECTIONS[dir.ordinal() + 1];
  }

  private int pushLiquid(ForgeDirection from, FluidStack pushStack, boolean doPush, int token) {
    if(token == currentPushToken || pushStack == null || pushStack.amount <= 0 || network == null) {
      return 0;
    }
    currentPushToken = token;
    int pushed = 0;
    int total = pushStack.amount;

    ForgeDirection dir = startPushDir;
    FluidStack toPush = pushStack.copy();

    int filledLocal = tank.fill(toPush, doPush);
    toPush.amount -= filledLocal;
    pushed += filledLocal;

    do {
      if(dir != from && canOutputToDir(dir)) {
        if(getConduitConnections().contains(dir)) {
          ILiquidConduit conduitCon = getFluidConduit(dir);
          if(conduitCon != null) {
            int toCon = ((LiquidConduit) conduitCon).pushLiquid(dir.getOpposite(), toPush, doPush, token);
            toPush.amount -= toCon;
            pushed += toCon;
          }
        } else if(getExternalConnections().contains(dir)) {
          IFluidHandler con = getTankContainer(getLocation().getLocation(dir));
          if(con != null) {
            int toExt = con.fill(dir.getOpposite(), toPush, doPush);
            toPush.amount -= toExt;
            pushed += toExt;
            if(doPush) {
              network.outputedToExternal(toExt);
            }
          }
        }
      }
      dir = getNextDir(dir);
    } while (dir != startPushDir && pushed < total);

    return pushed;
  }

  private ILiquidConduit getFluidConduit(ForgeDirection dir) {
    TileEntity ent = getBundle().getEntity();
    return ConduitUtil.getConduit(ent.worldObj, ent, dir, ILiquidConduit.class);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    if(getConectionMode(from) == ConnectionMode.INPUT || getConectionMode(from) == ConnectionMode.DISABLED) {
      return null;
    }
    return tank.drain(maxDrain, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(resource != null && !resource.isFluidEqual(tank.getFluid())) {
      return null;
    }
    return drain(from, resource.amount, doDrain);
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if(getConectionMode(from) == ConnectionMode.OUTPUT || getConectionMode(from) == ConnectionMode.DISABLED) {
      return false;
    }
    if(tank.getFluid() == null) {
      return true;
    }
    if(fluid != null && fluid.getID() == tank.getFluid().fluidID) {
      return true;
    }
    return false;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if(getConectionMode(from) == ConnectionMode.INPUT || getConectionMode(from) == ConnectionMode.DISABLED
        || tank.getFluid() == null || fluid == null) {
      return false;
    }
    return tank.getFluid().getFluid().getID() == fluid.getID();
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return new FluidTankInfo[] { tank.getInfo() };
  }

  // -----------------------------

  @Override
  protected void connectionsChanged() {
    super.connectionsChanged();
    updateTank();
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(ModObject.itemLiquidConduit.actualId, 1, 0);
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    if(network == null) {
      this.network = null;
      return true;
    }
    if(!(network instanceof AbstractTankConduitNetwork)) {
      return false;
    }

    AbstractTankConduitNetwork n = (AbstractTankConduitNetwork) network;
    if(tank.getFluid() == null) {
      tank.setLiquid(n.getFluidType() == null ? null : n.getFluidType().copy());
    } else if(n.getFluidType() == null) {
      n.setFluidType(tank.getFluid());
    } else if(!tank.getFluid().isFluidEqual(n.getFluidType())) {
      return false;
    }
    this.network = (LiquidConduitNetwork) network;
    return true;
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit con) {
    if(!super.canConnectToConduit(direction, con)) {
      return false;
    }
    if(!(con instanceof LiquidConduit)) {
      return false;
    }
    if(getFluidType() != null && ((LiquidConduit) con).getFluidType() == null) {
      return false;
    }
    return LiquidConduitNetwork.areFluidsCompatable(getFluidType(), ((LiquidConduit) con).getFluidType());
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY);
    }
    if(isExtractingFromDir(component.dir)) {
      return ICONS.get(getFluidType() == null ? ICON_EMPTY_EXTRACT_KEY : ICON_EXTRACT_KEY);
    }
    if(getConectionMode(component.dir) == ConnectionMode.OUTPUT) {
      return ICONS.get(getFluidType() == null ? ICON_EMPTY_INSERT_KEY : ICON_INSERT_KEY);
    }
    //    if(getFluidType() == null) {
    //      return ICONS.get(ICON_EMPTY_KEY);
    //    }
    return fluidTypeLocked ? ICONS.get(ICON_KEY_LOCKED) : ICONS.get(ICON_KEY);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    if(tank.getFluid() != null && tank.getFluid().getFluid() != null) {
      return tank.getFluid().getFluid().getStillIcon();
    }
    return null;
  }

  @Override
  public float getTransmitionGeometryScale() {
    return tank.getFilledRatio();
  }

  @Override
  protected void updateTank() {
    int totalConnections = getConduitConnections().size() + getExternalConnections().size();
    tank.setCapacity(totalConnections * VOLUME_PER_CONNECTION);
  }

  @Override
  protected boolean canJoinNeighbour(ILiquidConduit n) {
    return n instanceof LiquidConduit;
  }

  @Override
  public AbstractTankConduitNetwork<? extends AbstractTankConduit> getTankNetwork() {
    return network;
  }

}
