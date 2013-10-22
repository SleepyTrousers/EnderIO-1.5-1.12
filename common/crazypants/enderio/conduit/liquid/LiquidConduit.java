package crazypants.enderio.conduit.liquid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.RedstoneSwitch;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

public class LiquidConduit extends AbstractConduit implements ILiquidConduit {

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        ICONS.put(ICON_KEY, register.registerIcon(ICON_KEY));
        ICONS.put(ICON_EMPTY_KEY, register.registerIcon(ICON_EMPTY_KEY));
        ICONS.put(ICON_CORE_KEY, register.registerIcon(ICON_CORE_KEY));
        ICONS.put(ICON_EXTRACT_KEY, register.registerIcon(ICON_EXTRACT_KEY));
        ICONS.put(ICON_EMPTY_EXTRACT_KEY, register.registerIcon(ICON_EMPTY_EXTRACT_KEY));
        ICONS.put(ICON_EMPTY_INSERT_KEY, register.registerIcon(ICON_EMPTY_INSERT_KEY));
        ICONS.put(ICON_INSERT_KEY, register.registerIcon(ICON_INSERT_KEY));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  private LiquidConduitNetwork network;

  private ConduitTank tank = new ConduitTank(0);

  private float lastSyncRatio = -99;

  private int currentPushToken;

  // -----------------------------

  private long lastEmptyTick = 0;
  private int numEmptyEvents = 0;

  private boolean stateDirty = false;

  private int maxDrainPerTick = 50;

  private ForgeDirection startPushDir = ForgeDirection.DOWN;

  private final Set<BlockCoord> filledFromThisTick = new HashSet<BlockCoord>();

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    if(player.getCurrentEquippedItem() == null) {
      return false;
    }
    if(ConduitUtil.isToolEquipped(player)) {

      if(!getBundle().getEntity().worldObj.isRemote) {

        if(res.component != null) {
          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);

          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
            // Attempt to join networks
            ILiquidConduit neighbour = getFluidConduit(faceHit);
            if(neighbour != null && LiquidConduitNetwork.areFluidsCompatable(getFluidType(), neighbour.getFluidType())) {
              // kill the networks so a new one is formed combining then
              if(neighbour.getNetwork() != null) {
                neighbour.getNetwork().destroyNetwork();
              }
              if(getNetwork() != null) {
                getNetwork().destroyNetwork();
              }
              // and join'm'up
              neighbour.conduitConnectionAdded(faceHit.getOpposite());
              conduitConnectionAdded(faceHit);
            }
          } else if(containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setConnectionMode(connDir, getNextConnectionMode(connDir));
          } else if(containsConduitConnection(connDir)) {
            conduitConnectionRemoved(connDir);
            BlockCoord loc = getLocation().getLocation(connDir);
            ILiquidConduit neighbour = ConduitUtil.getConduit(getBundle().getEntity().worldObj, loc.x, loc.y, loc.z, ILiquidConduit.class);
            if(neighbour != null) {
              neighbour.conduitConnectionRemoved(connDir.getOpposite());
            }
          }
        }
      }
      return true;

    } else if(player.getCurrentEquippedItem().itemID == Item.bucketEmpty.itemID) {

      if(!getBundle().getEntity().worldObj.isRemote) {
        long curTick = getBundle().getEntity().worldObj.getWorldTime();
        if(curTick - lastEmptyTick < 20) {
          numEmptyEvents++;
        } else {
          numEmptyEvents = 1;
        }
        lastEmptyTick = curTick;

        if(numEmptyEvents < 2) {
          tank.setAmount(0);
        } else if(network != null) {
          network.setFluidType(null);
          numEmptyEvents = 0;
        }
      }

      return true;
    } else {

      FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(player.getCurrentEquippedItem());
      if(fluid != null) {
        if(!getBundle().getEntity().worldObj.isRemote) {
          if(network != null && (network.getFluidType() == null || network.getTotalVolume() < 500)) {
            network.setFluidType(fluid);
            ChatMessageComponent c = ChatMessageComponent.createFromText("Fluid type set to " + FluidRegistry.getFluidName(fluid));
            player.sendChatToPlayer(c);
          }
        }
        return true;
      }
    }

    return false;
  }

  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);
    if(world.isRemote) {
      return;
    }
    filledFromThisTick.clear();
    updateStartPushDir();
    doExtract();
    // Limit these updates to prevent spamming during flow
    if(stateDirty || (lastSyncRatio != tank.getFilledRatio() && world.getTotalWorldTime() % 2 == 0)) {
      lastSyncRatio = tank.getFilledRatio();
      setActive(lastSyncRatio > 0);
      getBundle().dirty();
      stateDirty = false;
    }
  }

  private void doExtract() {

    BlockCoord loc = getLocation();
    if(!isPowered() || !hasConnectionMode(ConnectionMode.INPUT)) {
      return;
    }

    Fluid f = tank.getFluid() == null ? null : tank.getFluid().getFluid();

    int token = network == null ? -1 : network.getNextPushToken();
    for (ForgeDirection dir : externalConnections) {
      if(isExtractingFromDir(dir)) {
        IFluidHandler extTank = getTankContainer(getLocation().getLocation(dir));

        if(extTank != null) {

          FluidStack couldDrain = extTank.drain(dir.getOpposite(), maxDrainPerTick, false);
          if(couldDrain != null && couldDrain.amount > 0 && canFill(dir, couldDrain.getFluid())) {

            // if we drained all this, how much overflow do we need to push out
            int requiredPush = (tank.getFluidAmount() + couldDrain.amount) - tank.getCapacity();
            if(requiredPush <= 0) {
              FluidStack drained = extTank.drain(dir.getOpposite(), maxDrainPerTick, true);
              if(drained != null) {
                tank.fill(drained, true);
                if(network != null && network.getFluidType() == null) {
                  network.setFluidType(drained);
                }
              }

            } else {

              // push as much as we can, to out target max
              int pushed = pushLiquid(dir, requiredPush, true, token);
              tank.addAmount(-pushed);
              if(tank.getAvailableSpace() > 0) {
                FluidStack drained = extTank.drain(dir.getOpposite(), Math.min(tank.getAvailableSpace(), maxDrainPerTick), true);
                if(drained != null) {
                  tank.addAmount(drained.amount);
                }
              }
            }

          }
        }
      }
    }

  }

  private boolean isPowered() {
    BlockCoord loc = getLocation();
    IRedstoneConduit rsCon = getBundle().getConduit(IRedstoneConduit.class);
    if(rsCon instanceof RedstoneSwitch && ((RedstoneSwitch) rsCon).isActive()) {
      // Need to check for this manually as if we ask the tile if it is being
      // powered it dies not check if it is providing power itself.
      return true;
    }
    return getBundle().getEntity().worldObj.isBlockIndirectlyGettingPowered(loc.x, loc.y, loc.z);
  }

  @Override
  public boolean canOutputToDir(ForgeDirection dir) {
    if(isExtractingFromDir(dir) || getConectionMode(dir) == ConnectionMode.DISABLED) {
      return false;
    }
    if(conduitConnections.contains(dir)) {
      return true;
    }
    if(!externalConnections.contains(dir)) {
      return false;
    }
    IFluidHandler ext = getExternalHandler(dir);
    if(ext instanceof TileReservoir) { // dont push to an auto ejecting
                                       // resevoir or we loop
      TileReservoir tr = (TileReservoir) ext;
      return !tr.isMultiblock() || !tr.isAutoEject();
    }
    return true;
  }

  @Override
  public boolean isExtractingFromDir(ForgeDirection dir) {
    return getConectionMode(dir) == ConnectionMode.INPUT;
  }

  @Override
  public void setFluidType(FluidStack liquidType) {
    if(tank.getFluid() != null && tank.getFluid().isFluidEqual(liquidType)) {
      return;
    }
    if(liquidType != null) {
      liquidType = liquidType.copy();
    }
    tank.setLiquid(liquidType);
    stateDirty = true;
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

    if(!canFill(from, resource.getFluid())) {
      return 0;
    }

    // Note: This is just a guard against mekansims pipes that will continuously
    // call
    // fill on us if we push liquid to them.
    if(filledFromThisTick.contains(getLocation().getLocation(from))) {
      return 0;
    }
    if(doFill) {
      filledFromThisTick.add(getLocation().getLocation(from));
    }

    int res = fill(from, resource, doFill, true, network == null ? -1 : network.getNextPushToken());
    if(doFill && externalConnections.contains(from) && network != null) {
      network.addedFromExternal(res);
    }
    return res;
  }

  @Override
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
    int recieveAmount = resource.amount;

    int pushedVolume = 0;
    if(doPush) {
      int maxPush = Math.max(0, recieveAmount + tank.getFluidAmount() - tank.getCapacity());
      pushedVolume = pushLiquid(from, maxPush, doFill, pushToken);
    }

    if(doFill) {
      tank.drain(pushedVolume, doFill);
      return tank.fill(resource, doFill);
    } else {
      int amount = tank.getFluidAmount();
      tank.drain(pushedVolume, true);
      int res = tank.fill(resource, false);
      tank.setAmount(amount);
      return res;
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

  private int pushLiquid(ForgeDirection from, int amount, boolean doPush, int token) {
    if(token == currentPushToken || amount <= 0 || tank.getFluid() == null || tank.getFluid().getFluid() == null) {
      return 0;
    }
    currentPushToken = token;
    int pushed = 0;

    ForgeDirection dir = startPushDir;
    FluidStack toPush = tank.getFluid().copy();
    toPush.amount = amount;

    do {
      if(dir != from && canOutputToDir(dir)) {
        if(getConduitConnections().contains(dir)) {
          ILiquidConduit conduitCon = getFluidConduit(dir);
          if(conduitCon != null && conduitCon.getTank().getFilledRatio() <= tank.getFilledRatio()) {
            int toCon = conduitCon.fill(dir.getOpposite(), toPush, doPush, true, token);
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
    } while (dir != startPushDir && pushed < amount);

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
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    if(tank.containsValidLiquid()) {
      nbtRoot.setTag("tank", tank.getFluid().writeToNBT(new NBTTagCompound()));
    } else if(getFluidType() != null) {
      FluidStack ft = getFluidType().copy();
      ft.amount = 0;
      nbtRoot.setTag("tank", ft.writeToNBT(new NBTTagCompound()));
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    updateTanksCapacity();
    FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("tank"));
    tank.setLiquid(liquid);
  }

  @Override
  protected void connectionsChanged() {
    super.connectionsChanged();
    updateTanksCapacity();
  }

  @Override
  public ConduitTank getTank() {
    return tank;
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(ModObject.itemLiquidConduit.actualId, 1, 0);
  }

  @Override
  public AbstractConduitNetwork<?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?> network) {
    if(network == null) {
      this.network = null;
      return true;
    }

    LiquidConduitNetwork n = (LiquidConduitNetwork) network;
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
  public boolean canConnectToExternal(ForgeDirection direction) {
    return getExternalHandler(direction) != null;
  }

  @Override
  public IFluidHandler getExternalHandler(ForgeDirection direction) {
    IFluidHandler con = getTankContainer(getLocation().getLocation(direction));
    return (con != null && !(con instanceof IConduitBundle)) ? con : null;
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit con) {
    if(!(con instanceof ILiquidConduit)) {
      return false;
    }
    if(getFluidType() != null && ((ILiquidConduit) con).getFluidType() == null) {
      return false;
    }
    return LiquidConduitNetwork.areFluidsCompatable(getFluidType(), ((ILiquidConduit) con).getFluidType());
  }

  @Override
  public FluidStack getFluidType() {
    FluidStack result = null;
    if(network != null) {
      result = network.getFluidType();
    }
    if(result == null) {
      result = tank.getFluid();
    }
    return result;
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
    if(getFluidType() == null) {
      return ICONS.get(ICON_EMPTY_KEY);
    }
    return ICONS.get(ICON_KEY);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    if(active && tank.getFluid() != null) {
      return tank.getFluid().getFluid().getStillIcon();
    }
    return null;
  }

  @Override
  public float getTransmitionGeometryScale() {
    return tank.getFilledRatio();
  }

  private IFluidHandler getTankContainer(BlockCoord bc) {
    return getTankContainer(bc.x, bc.y, bc.z);
  }

  private IFluidHandler getTankContainer(int x, int y, int z) {
    TileEntity te = getBundle().getEntity().worldObj.getBlockTileEntity(x, y, z);
    if(te instanceof IFluidHandler) {
      return (IFluidHandler) te;
    }
    return null;
  }

  private void updateTanksCapacity() {
    int totalConnections = getConduitConnections().size() + getExternalConnections().size();
    tank.setCapacity(totalConnections * VOLUME_PER_CONNECTION);
  }

}
