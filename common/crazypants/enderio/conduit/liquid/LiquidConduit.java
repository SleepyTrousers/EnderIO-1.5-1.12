package crazypants.enderio.conduit.liquid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  private boolean pushing = false;

  private final Set<ForgeDirection> extractDirs = new HashSet<ForgeDirection>();

  // -----------------------------

  private long lastEmptyTick = 0;
  private int numEmptyEvents = 0;

  private boolean stateDirty = false;

  private int maxDrainPerTick = 50;

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res) {
    if (player.getCurrentEquippedItem() == null) {
      return false;
    }
    if (ConduitUtil.isToolEquipped(player)) {

      if (!getBundle().getEntity().worldObj.isRemote) {
        ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);
        if (res.component != null) {
          ForgeDirection connDir = (ForgeDirection) res.component.dir;

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

      FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(player.getCurrentEquippedItem());
      if (fluid != null) {
        if (!getBundle().getEntity().worldObj.isRemote) {
          if (network != null && (network.getFluidType() == null || network.getTotalVolume() < 500)) {
            network.setFluidType(fluid);
            ChatMessageComponent c = ChatMessageComponent.func_111066_d("Fluid type set to " + FluidRegistry.getFluidName(fluid));
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
    if (world.isRemote) {
      return;
    }
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
    if (!getBundle().getEntity().worldObj.isBlockIndirectlyGettingPowered(loc.x, loc.y, loc.z)) {
      return;
    }

    for (ForgeDirection dir : extractDirs) {
      IFluidHandler extTank = getTankContainer(getLocation().getLocation(dir));

      int maxPush = tank.getFluidAmount();
      int couldPush = pushLiquid(dir, maxPush, false);
      int targetExtract = Math.min(maxDrainPerTick, tank.getAvailableSpace() + couldPush);
      FluidStack couldDrain = extTank.drain(dir.getOpposite(), targetExtract, false);
      if (couldDrain != null && network != null && network.canAcceptLiquid(couldDrain)) {
        couldDrain = extTank.drain(dir.getOpposite(), targetExtract, true);
        if (couldDrain != null) {
          FluidStack drained = couldDrain.copy();

          int totalVolume = tank.getFluidAmount() + drained.amount;
          int pushed = pushLiquid(dir, Math.min(drained.amount, maxPush), true);
          // System.out.println("LiquidConduit.doExtract: Drained: " +
          // drained.amount + " pushed = " + pushed + " contains volume " +
          // tank.getAmount());
          drained.amount = totalVolume - pushed;
          tank.setLiquid(drained);
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
    IFluidHandler ext = getExternalHandler(dir);
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
  public void setFluidType(FluidStack liquidType) {
    if (tank.getFluid() != null && tank.getFluid().isFluidEqual(liquidType)) {
      return;
    }
    if (liquidType != null) {
      liquidType = liquidType.copy();
    }
    tank.setLiquid(liquidType);
  }
  
  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return fill(from, resource, doFill, true);
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill, boolean doPush) {
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
    if(doPush) {
      int maxPush = Math.max(0, recieveAmount + tank.getFluidAmount() - tank.getCapacity());
      pushedVolume = pushLiquid(from, maxPush, doFill);  
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

  private int pushLiquid(ForgeDirection from, int amount, boolean doPush) {
    if (pushing || amount <= 0 || tank.getFluid() == null) { // avoid circular
                                                             // pushing
      return 0;
    }

    pushing = true;
    int pushed = 0;

    try {

      Set<ForgeDirection> cons = new HashSet<ForgeDirection>(getConduitConnections());
      cons.addAll(getExternalConnections());
      cons.remove(from);

      if (cons.size() == 0) {
        return pushed;
      }

      int numToPushTo;
      int amountPerCon;
      List<ForgeDirection> toRemove = new ArrayList<ForgeDirection>(cons.size());
      FluidStack toPush = tank.getFluid().copy();
      do {
        numToPushTo = cons.size();
        amountPerCon = amount / numToPushTo;
        amountPerCon = Math.max(1, amountPerCon);
        toPush.amount = amountPerCon;
        for (ForgeDirection dir : cons) {
          IFluidHandler con = getTankContainer(getLocation().getLocation(dir));
          // NB: Dont push liquid into a conduit that is fuller than yourself
          ILiquidConduit conduitCon = getFluidConduit(dir);
          if (con == null || !canOutputToDir(dir) || (conduitCon != null && conduitCon.getTank().getFilledRatio() > tank.getFilledRatio())) {
            toRemove.add(dir);
          } else {
            int filled = con.fill(dir.getOpposite(), toPush, doPush);
            if (filled < amountPerCon) {
              toRemove.add(dir);
            }
            pushed += filled;

          }
          toPush.amount = amountPerCon;
          if (pushed >= amount) {
            return amount;
          }
        }
        for (ForgeDirection dir : toRemove) {
          cons.remove(dir);
        }

      } while (!cons.isEmpty() && pushed < amount);

    } finally {
      pushing = false;
    }

    return pushed;
  }

  private ILiquidConduit getFluidConduit(ForgeDirection dir) {
    TileEntity ent = getBundle().getEntity();
    return ConduitUtil.getConduit(ent.worldObj, ent, dir, ILiquidConduit.class);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return tank.drain(maxDrain, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if (resource != null && !resource.isFluidEqual(tank.getFluid())) {
      return null;
    }
    return drain(from, resource.amount, doDrain);
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if (tank.getFluid() == null) {
      return true;
    }
    if (fluid != null && fluid.getID() == tank.getFluid().fluidID) {
      return true;
    }
    return false;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if (tank.getFluid() == null || fluid == null) {
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
    if (tank.containsValidLiquid()) {
      nbtRoot.setTag("tank", tank.getFluid().writeToNBT(new NBTTagCompound()));
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
    FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("tank"));
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
    if (tank.getFluid() == null) {
      tank.setLiquid(n.getFluidType() == null ? null : n.getFluidType().copy());
    } else if (n.getFluidType() == null) {
      n.setFluidType(tank.getFluid());
    } else if (!tank.getFluid().isFluidEqual(n.getFluidType())) {
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
    if (!(con instanceof ILiquidConduit)) {
      return false;
    }
    if (getFluidType() != null && ((ILiquidConduit) con).getFluidType() == null) {
      return false;
    }
    return LiquidConduitNetwork.areFluidsCompatable(getFluidType(), ((ILiquidConduit) con).getFluidType());
  }

  @Override
  public net.minecraftforge.fluids.FluidStack getFluidType() {
    FluidStack result = null;
    if (network != null) {
      result = network.getFluidType();
    }
    if (result == null) {
      result = tank.getFluid();
    }
    return result;
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if (component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY);
    }
    if (isExtractingFromDir((ForgeDirection) component.dir)) {
      return ICONS.get(ICON_EXTRACT_KEY);
    }
    return ICONS.get(ICON_KEY);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    if (active && tank.getFluid() != null) {
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
    if (te instanceof IFluidHandler) {
      return (IFluidHandler) te;
    }
    return null;
  }

  private void updateTanksCapacity() {
    int totalConnections = getConduitConnections().size() + getExternalConnections().size();
    tank.setCapacity(totalConnections * VOLUME_PER_CONNECTION);
  }

}
