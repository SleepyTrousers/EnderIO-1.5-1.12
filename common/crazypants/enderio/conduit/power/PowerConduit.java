package crazypants.enderio.conduit.power;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.MutablePowerProvider;
import crazypants.render.BoundingBox;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector3d;

public class PowerConduit extends AbstractConduit implements IPowerConduit {

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  static final ICapacitor[] CAPACITORS = new BasicCapacitor[] {
      new BasicCapacitor(250, 1500, 128),
      new BasicCapacitor(350, 3000, 512),
      new BasicCapacitor(500, 5000, 2048)
  };

  static final String[] POSTFIX = new String[] { "", "Enhanced", "Ender" };

  static ItemStack createItemStackForSubtype(int subtype) {
    ItemStack result = new ItemStack(ModObject.itemPowerConduit.actualId, 1, subtype);
    return result;

  }

  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        for (String pf : POSTFIX) {
          ICONS.put(ICON_KEY + pf, register.registerIcon(ICON_KEY + pf));
          ICONS.put(ICON_KEY_INPUT + pf, register.registerIcon(ICON_KEY_INPUT + pf));
          ICONS.put(ICON_KEY_OUTPUT + pf, register.registerIcon(ICON_KEY_OUTPUT + pf));
          ICONS.put(ICON_CORE_KEY + pf, register.registerIcon(ICON_CORE_KEY + pf));
        }
        ICONS.put(ICON_TRANSMISSION_KEY, register.registerIcon(ICON_TRANSMISSION_KEY));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  public static final float WIDTH = 0.075f;
  public static final float HEIGHT = 0.075f;

  public static final Vector3d MIN = new Vector3d(0.5f - WIDTH, 0.5 - HEIGHT, 0.5 - WIDTH);
  public static final Vector3d MAX = new Vector3d(MIN.x + WIDTH, MIN.y + HEIGHT, MIN.z + WIDTH);

  public static final BoundingBox BOUNDS = new BoundingBox(MIN, MAX);

  protected PowerConduitNetwork network;
  private ConduitPowerProvider powerHandler;

  private int subtype;

  public PowerConduit() {
  }

  public PowerConduit(int meta) {
    this.subtype = meta;
    powerHandler = createPowerHandlerForType();
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res) {
    if (ConduitUtil.isToolEquipped(player)) {
      if (!getBundle().getEntity().worldObj.isRemote) {
        if (res.component != null) {
          ForgeDirection connDir = res.component.dir;
          if (externalConnections.contains(connDir)) {
            ConnectionMode curMode = getConectionMode(connDir);
            ConnectionMode newMode = ConnectionMode.getNext(curMode);
            conectionModes.put(connDir, newMode);
            BlockCoord bc = getLocation();
            setClientStateDirty();
          }
        }
      }
      if (network != null) {
        System.out.println("PowerConduit.onBlockActivated: Network contains " + network.getPowerManager().energyStored + " of max energy "
            + network.getPowerManager().maxEnergyStored);
        System.out.println("Conduit contains: " + powerHandler.getEnergyStored() + " of max " + powerHandler.getMaxEnergyStored());
      }
      return true;
    }
    return false;
  }

  @Override
  public ICapacitor getCapacitor() {
    return CAPACITORS[subtype];
  }

  private ConduitPowerProvider createPowerHandlerForType() {
    return ConduitPowerProvider.createHandler(CAPACITORS[subtype], this);
    // return PowerHandlerUtil.createHandler(CAPACITORS[subtype]);
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("subtype", (short) subtype);
    nbtRoot.setFloat("energyStored", powerHandler.getEnergyStored());
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    subtype = nbtRoot.getShort("subtype");
    if (powerHandler == null) {
      powerHandler = createPowerHandlerForType();
    }
    powerHandler.setEnergy(Math.min(powerHandler.getMaxEnergyStored(), nbtRoot.getFloat("energyStored")));
  }

  @Override
  public void applyPerdition() {
  }

  @Override
  public MutablePowerProvider getPowerHandler() {
    return powerHandler;
  }

  @Override
  public void setPowerProvider(IPowerProvider provider) {

  }

  @Override
  public IPowerProvider getPowerProvider() {
    return powerHandler;
  }

  @Override
  public void doWork() {
  }

  @Override
  public int powerRequest(ForgeDirection from) {
    if (getConectionMode(from) == ConnectionMode.OUTPUT) {
      return 0;
    }
    return powerHandler.getMaxEnergyStored() - powerHandler.getMinEnergyReceived();
  }

  @Override
  public float getMaxEnergyExtracted(ForgeDirection dir) {
    ConnectionMode mode = getConectionMode(dir);
    if (mode == ConnectionMode.INPUT) {
      return 0;
    }
    return getCapacitor().getMaxEnergyExtracted();
  }

  @Override
  public float getMaxEnergyRecieved(ForgeDirection dir) {
    ConnectionMode mode = getConectionMode(dir);
    if (mode == ConnectionMode.OUTPUT) {
      return 0;
    }
    return getCapacitor().getMaxEnergyReceived();
  }

  @Override
  public AbstractConduitNetwork<?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?> network) {
    this.network = (PowerConduitNetwork) network;
    return true;
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction) {
    IPowerReceptor rec = getExternalPowerReceptor(direction);
    // if(rec instanceof IPowerEmitter) {
    // return ((IPowerEmitter)rec).canEmitPowerFrom(direction.getOpposite());
    // }
    return rec != null && rec.getPowerProvider() != null;
  }

  @Override
  public void externalConnectionAdded(ForgeDirection direction) {
    super.externalConnectionAdded(direction);
    if (network != null) {
      TileEntity te = bundle.getEntity();
      network.powerReceptorAdded(this, direction, te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ,
          getExternalPowerReceptor(direction));
    }
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection direction) {
    super.externalConnectionRemoved(direction);
    if (network != null) {
      TileEntity te = bundle.getEntity();
      network.powerReceptorRemoved(te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ);
    }
  }

  @Override
  public IPowerReceptor getExternalPowerReceptor(ForgeDirection direction) {
    TileEntity te = bundle.getEntity();
    World world = te.worldObj;
    if (world == null) {
      return null;
    }
    TileEntity test = world.getBlockTileEntity(te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ);
    if (test instanceof IConduitBundle) {
      return null;
    }
    if (test instanceof IPowerReceptor) {
      return (IPowerReceptor) test;
    }
    return null;
  }

  @Override
  public ItemStack createItem() {
    return createItemStackForSubtype(subtype);
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  // Rendering
  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if (component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY + POSTFIX[subtype]);
    }
    return ICONS.get(ICON_KEY + POSTFIX[subtype]);
  }

  @Override
  public Icon getTextureForInputMode() {
    return ICONS.get(ICON_KEY_INPUT + POSTFIX[subtype]);
  }

  @Override
  public Icon getTextureForOutputMode() {
    return ICONS.get(ICON_KEY_OUTPUT + POSTFIX[subtype]);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

}
