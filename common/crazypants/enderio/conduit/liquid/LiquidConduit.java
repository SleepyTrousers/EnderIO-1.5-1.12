package crazypants.enderio.conduit.liquid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
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
        ICONS.put(ICON_CORE_KEY, register.registerIcon(ICON_CORE_KEY));
        ICONS.put(ICON_EXTRACT_KEY, register.registerIcon(ICON_EXTRACT_KEY));
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

  private final Set<ForgeDirection> extractDirs = new HashSet<ForgeDirection>();

  private int currentPushToken;

  // -----------------------------

  private long lastEmptyTick = 0;
  private int numEmptyEvents = 0;

  private boolean stateDirty = false;

  private int maxDrainPerTick = 50;

  private ForgeDirection startPushDir = ForgeDirection.DOWN;

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res) {
    if (player.getCurrentEquippedItem() == null) {
      return false;
    }
    if (ConduitUtil.isToolEquipped(player)) {

      if (!getBundle().getEntity().worldObj.isRemote) {
        ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);
        if (res.component != null) {
          ForgeDirection connDir = res.component.dir;

          if (connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
            // Attempt to join networls
            ILiquidConduit neighbour = getFluidConduit(faceHit);
            if (neighbour != null && LiquidConduitNetwork.areFluidsCompatable(getFluidType(), neighbour.getFluidType())) {
              // kill the networks so a new one is formed combining then
              if (neighbour.getNetwork() != null) {
                neighbour.getNetwork().destroyNetwork();
              }
              if (getNetwork() != null) {
                getNetwork().destroyNetwork();
              }
              // and join'm'up
              neighbour.conduitConnectionAdded(faceHit.getOpposite());
              conduitConnectionAdded(faceHit);
            }
          } else if (containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setExtractingFromDir(connDir, !isExtractingFromDir(connDir));
          }
        }

      } else {
        String str = player.worldObj.isRemote ? "[Client]" : "[Server]";
        System.out.println(str +
            " LiquidConduit.onBlockActivated: Tank volume is: " +
            tank.getCapacity() + " tank contains: " + tank.getFluidAmount()
            + " filled ratio is: " + tank.getFilledRatio());
      }
      return true;

    } else if (player.getCurrentEquippedItem().itemID == Item.bucketEmpty.itemID) {

      if (!getBundle().getEntity().worldObj.isRemote) {
        long curTick = getBundle().getEntity().worldObj.getWorldTime();
        if (curTick - lastEmptyTick < 20) {
          numEmptyEvents++;
        } else {
          numEmptyEvents = 1;
        }
        lastEmptyTick = curTick;

        if (numEmptyEvents < 2) {
          tank.setAmount(0);
        } else if (network != null) {
          network.setFluidType(null);
          numEmptyEvents = 0;
        }
      }

      return true;
    } else {

      LiquidStack fluid = LiquidContainerRegistry.getLiquidForFilledItem(player.getCurrentEquippedItem());
      if (fluid != null) {
        if (!getBundle().getEntity().worldObj.isRemote) {
          if (network != null && (network.getFluidType() == null || network.getTotalVolume() < 500)) {
            network.setFluidType(fluid);
            // ChatMessageComponent c = ChatMessageComponent.func_111066_d(+
            // FluidRegistry.getFluidName(fluid));
            player.sendChatToPlayer("Fluid type set to " + LiquidDictionary.findLiquidName(fluid));
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
    if (world.isRemote) {
      return;
    }
    updateStartPushDir();
    doExtract();
    // Limit these updates to prevent spamming during flow
    if (stateDirty || (lastSyncRatio != tank.getFilledRatio() && world.getTotalWorldTime() % 2 == 0)) {
      lastSyncRatio = tank.getFilledRatio();
      setActive(lastSyncRatio > 0);
      getBundle().dirty();
      stateDirty = false;
    }
  }

  private void doExtract() {

    BlockCoord loc = getLocation();
    if (!getBundle().getEntity().worldObj.isBlockIndirectlyGettingPowered(loc.x, loc.y, loc.z) || extractDirs.isEmpty()) {
      return;
    }

    int token = network == null ? -1 : network.getNextPushToken();
    for (ForgeDirection dir : extractDirs) {
      ITankContainer extTank = getTankContainer(getLocation().getLocation(dir));
      if (extTank != null) {

        LiquidStack couldDrain = extTank.drain(dir.getOpposite(), maxDrainPerTick, false);
        if (couldDrain != null && couldDrain.amount > 0) {

          // if we drained all this, how much overflow do we need to push out
          int requiredPush = (tank.getFluidAmount() + couldDrain.amount) - tank.getCapacity();
          if (requiredPush <= 0) {
            LiquidStack drained = extTank.drain(dir.getOpposite(), maxDrainPerTick, true);
            if (drained != null) {
              tank.fill(drained, true);
          }
          } else {

            // push as much as we can, to out target max
            int pushed = pushLiquid(dir, requiredPush, true, token);
            tank.addAmount(-pushed);
            if (tank.getAvailableSpace() > 0) {
              LiquidStack drained = extTank.drain(dir.getOpposite(), Math.min(tank.getAvailableSpace(), maxDrainPerTick), true);
              if (drained != null) {
                tank.addAmount(drained.amount);
        }
      }
    }
        }        
      }
    }

  }

  @Override
  public boolean canOutputToDir(ForgeDirection dir) {
    // TODO: In/Out control on externals
    if (conduitConnections.contains(dir)) {
      return true;
    }
    if (!externalConnections.contains(dir)) {
      return false;
    }
    if (isExtractingFromDir(dir)) {
      return false;
    }
    ITankContainer ext = getExternalHandler(dir);
    if (ext instanceof TileReservoir) { // dont push to an auto ejecting
                                        // resevoir or we loop
      TileReservoir tr = (TileReservoir) ext;
      return !tr.isMultiblock() || !tr.isAutoEject();
    }
    return true;
  }

  @Override
  public boolean isExtractingFromDir(ForgeDirection dir) {
    return extractDirs.contains(dir);
  }

  @Override
  public void setExtractingFromDir(ForgeDirection dir, boolean extracting) {
    if (isExtractingFromDir(dir) == extracting) {
      return;
    }
    if (!extracting) {
      extractDirs.remove(dir);
    } else {
      extractDirs.add(dir);
    }
    stateDirty = true;
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection fromDirection) {
    super.externalConnectionRemoved(fromDirection);
    setExtractingFromDir(fromDirection, false);
  }

  @Override
  public void setFluidType(LiquidStack liquidType) {
    if (tank.getLiquid() != null && tank.getLiquid().isLiquidEqual(liquidType)) {
      return;
    }
    if (liquidType != null) {
      liquidType = liquidType.copy();
    }
    tank.setLiquid(liquidType);
  }

  @Override
  public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
    int res = fill(from, resource, doFill, true, network == null ? -1 : network.getNextPushToken());
if (doFill && externalConnections.contains(from) && network != null) {
      network.addedFromExternal(res);
    }
    return res;  }

  @Override
  public int fill(ForgeDirection from, LiquidStack resource, boolean doFill, boolean doPush, int pushToken) {
    if (network == null) {
      return 0;
    }
    if (network.canAcceptLiquid(resource)) {
      network.setFluidType(resource);
    } else {
      return 0;
    }
    int recieveAmount = resource == null ? 0 : resource.amount;
    if (recieveAmount <= 0) {
      return 0;
    }

    int pushedVolume = 0;
    if (doPush) {
      int maxPush = Math.max(0, recieveAmount + tank.getFluidAmount() - tank.getCapacity());
      pushedVolume = pushLiquid(from, maxPush, doFill, pushToken);
    }

    if (doFill) {
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
    if (dir.ordinal() >= ForgeDirection.UNKNOWN.ordinal() - 1) {
      return ForgeDirection.VALID_DIRECTIONS[0];
    }
    return ForgeDirection.VALID_DIRECTIONS[dir.ordinal() + 1];
  }

  private int pushLiquid(ForgeDirection from, int amount, boolean doPush, int token) {
    if (token == currentPushToken || amount <= 0 || tank.getLiquid() == null) {
      return 0;
      }
    currentPushToken = token;
    int pushed = 0;

    ForgeDirection dir = startPushDir;
    LiquidStack toPush = tank.getLiquid().copy();
    toPush.amount = amount;

      do {
      if (dir != from && canOutputToDir(dir)) {
        if (getConduitConnections().contains(dir)) {
          ILiquidConduit conduitCon = getFluidConduit(dir);
          if (conduitCon != null && conduitCon.getTank().getFilledRatio() <= tank.getFilledRatio()) {
            int toCon = conduitCon.fill(dir.getOpposite(), toPush, doPush, true, token);
            toPush.amount -= toCon;
            pushed += toCon;
            }
        } else if (getExternalConnections().contains(dir)) {
          ITankContainer con = getTankContainer(getLocation().getLocation(dir));
          if (con != null) {
            int toExt = con.fill(dir.getOpposite(), toPush, doPush);
            toPush.amount -= toExt;
            pushed += toExt;
            if (doPush) {
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
  public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
    return fill(ForgeDirection.UNKNOWN, resource, doFill);
  }

  @Override
  public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return tank.drain(maxDrain, doDrain);
  }

  @Override
  public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
    return drain(ForgeDirection.UNKNOWN, maxDrain, doDrain);
  }

  @Override
  public ILiquidTank[] getTanks(ForgeDirection direction) {
    return new ILiquidTank[] { tank };
  }

  @Override
  public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
    if (network != null && network.canAcceptLiquid(type)) {
      return tank;
    }
    return null;
  }

  // -----------------------------

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    if (tank.containsValidLiquid()) {
      nbtRoot.setTag("tank", tank.getLiquid().writeToNBT(new NBTTagCompound()));
    }

    int[] dirs = new int[extractDirs.size()];
    Iterator<ForgeDirection> cons = extractDirs.iterator();
    for (int i = 0; i < dirs.length; i++) {
      dirs[i] = cons.next().ordinal();
    }
    nbtRoot.setIntArray("extractDirs", dirs);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    updateTanksCapacity();
    LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbtRoot.getCompoundTag("tank"));
    tank.setLiquid(liquid);

    extractDirs.clear();
    int[] dirs = nbtRoot.getIntArray("extractDirs");
    for (int i = 0; i < dirs.length; i++) {
      extractDirs.add(ForgeDirection.values()[dirs[i]]);
    }
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
    if (network == null) {
      this.network = null;
      return true;
    }

    LiquidConduitNetwork n = (LiquidConduitNetwork) network;
    if (tank.getLiquid() == null) {
      tank.setLiquid(n.getFluidType() == null ? null : n.getFluidType().copy());
    } else if (n.getFluidType() == null) {
      n.setFluidType(tank.getLiquid());
    } else if (!tank.getLiquid().isLiquidEqual(n.getFluidType())) {
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
  public ITankContainer getExternalHandler(ForgeDirection direction) {
    ITankContainer con = getTankContainer(getLocation().getLocation(direction));
    return (con != null && !(con instanceof IConduitBundle)) ? con : null;
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit con) {
    if (!(con instanceof ILiquidConduit)) {
      return false;
    }
    if (getFluidType() != null && ((ILiquidConduit) con).getFluidType() == null) {
      return false;
    }
    return LiquidConduitNetwork.areFluidsCompatable(getFluidType(), ((ILiquidConduit) con).getFluidType());
  }

  @Override
  public LiquidStack getFluidType() {
    LiquidStack result = null;
    if (network != null) {
      result = network.getFluidType();
    }
    if (result == null) {
      result = tank.getLiquid();
    }
    return result;
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if (component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY);
    }
    if (isExtractingFromDir(component.dir)) {
      return ICONS.get(ICON_EXTRACT_KEY);
    }
    return ICONS.get(ICON_KEY);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    if (active && tank.getLiquid() != null) {
      return tank.getLiquid().canonical().getRenderingIcon();
    }
    return null;
  }

  @Override
  public String getTextureSheetForLiquid() {
    if (tank.getLiquid() != null && tank.getLiquid().canonical() != null) {
      return tank.getLiquid().canonical().getTextureSheet();
    }       
    return null;
  }

  @Override
  public float getTransmitionGeometryScale() {
    return tank.getFilledRatio();
  }

  private ITankContainer getTankContainer(BlockCoord bc) {
    return getTankContainer(bc.x, bc.y, bc.z);
  }

  private ITankContainer getTankContainer(int x, int y, int z) {
    TileEntity te = getBundle().getEntity().worldObj.getBlockTileEntity(x, y, z);
    if (te instanceof ITankContainer) {
      return (ITankContainer) te;
    }
    return null;
  }

  private void updateTanksCapacity() {
    int totalConnections = getConduitConnections().size() + getExternalConnections().size();
    tank.setCapacity(totalConnections * VOLUME_PER_CONNECTION);
  }

}
