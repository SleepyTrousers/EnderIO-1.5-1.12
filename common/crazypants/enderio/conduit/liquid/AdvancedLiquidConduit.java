package crazypants.enderio.conduit.liquid;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

public class AdvancedLiquidConduit extends AbstractTankConduit {

  public static final int CONDUIT_VOLUME = FluidContainerRegistry.BUCKET_VOLUME;

  public static final String ICON_KEY = "enderio:liquidConduitAdvanced";
  public static final String ICON_KEY_LOCKED = "enderio:liquidConduitAdvancedLocked";
  public static final String ICON_CORE_KEY = "enderio:liquidConduitCoreAdvanced";
  public static final String ICON_EXTRACT_KEY = "enderio:liquidConduitAdvancedInput";
  public static final String ICON_INSERT_KEY = "enderio:liquidConduitAdvancedOutput";
  public static final String ICON_EMPTY_EDGE = "enderio:liquidConduitAdvancedEdge";

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        ICONS.put(ICON_KEY, register.registerIcon(ICON_KEY));
        ICONS.put(ICON_CORE_KEY, register.registerIcon(ICON_CORE_KEY));
        ICONS.put(ICON_EXTRACT_KEY, register.registerIcon(ICON_EXTRACT_KEY));
        ICONS.put(ICON_INSERT_KEY, register.registerIcon(ICON_INSERT_KEY));
        ICONS.put(ICON_EMPTY_EDGE, register.registerIcon(ICON_EMPTY_EDGE));
        ICONS.put(ICON_KEY_LOCKED, register.registerIcon(ICON_KEY_LOCKED));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  private AdvancedLiquidConduitNetwork network;

  private long ticksSinceFailedExtract = 0;

  public static final int MAX_EXTRACT_PER_TICK = Config.advancedFluidConduitExtractRate;

  public static final int MAX_IO_PER_TICK = Config.advancedFluidConduitMaxIoRate;

  public AdvancedLiquidConduit() {
    updateTank();
  }

  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);
    if(world.isRemote) {
      return;
    }
    doExtract();
    if(stateDirty) {
      getBundle().dirty();
      stateDirty = false;
    }
  }

  private void doExtract() {
    BlockCoord loc = getLocation();
    if(!hasConnectionMode(ConnectionMode.INPUT)) {
      return;
    }
    if(network == null) {
      return;
    }

    // assume failure, reset to 0 if we do extract
    ticksSinceFailedExtract++;
    if(ticksSinceFailedExtract > 9 && ticksSinceFailedExtract % 10 != 0) {
      // after 10 ticks of failing, only check every 10 ticks
      return;
    }

    Fluid f = tank.getFluid() == null ? null : tank.getFluid().getFluid();
    for (ForgeDirection dir : externalConnections) {
      if(autoExtractForDir(dir)) {
        if(network.extractFrom(this, dir, MAX_EXTRACT_PER_TICK)) {
          ticksSinceFailedExtract = 0;
        }
      }
    }

  }

  @Override
  protected void updateTank() {
    tank.setCapacity(CONDUIT_VOLUME);
    if(network != null) {
      network.updateConduitVolumes();
    }
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(ModObject.itemLiquidConduit.actualId, 1, 1);
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
    if(!(network instanceof AdvancedLiquidConduitNetwork)) {
      return false;
    }

    AdvancedLiquidConduitNetwork n = (AdvancedLiquidConduitNetwork) network;
    if(tank.getFluid() == null) {
      tank.setLiquid(n.getFluidType() == null ? null : n.getFluidType().copy());
    } else if(n.getFluidType() == null) {
      n.setFluidType(tank.getFluid());
    } else if(!tank.getFluid().isFluidEqual(n.getFluidType())) {
      return false;
    }
    this.network = n;
    return true;

  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit con) {
    if(!super.canConnectToConduit(direction, con)) {
      return false;
    }
    if(!(con instanceof AdvancedLiquidConduit)) {
      return false;
    }
    if(getFluidType() != null && ((AdvancedLiquidConduit) con).getFluidType() == null) {
      return false;
    }
    return LiquidConduitNetwork.areFluidsCompatable(getFluidType(), ((AdvancedLiquidConduit) con).getFluidType());
  }

  @Override
  public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    refreshInputs(dir);
  }

  private void refreshInputs(ForgeDirection dir) {
    if(network == null) {
      return;
    }
    LiquidOutput lo = new LiquidOutput(getLocation().getLocation(dir), dir.getOpposite());
    network.removeInput(lo);
    if(getConectionMode(dir).acceptsOutput() && containsExternalConnection(dir)) {
      network.addInput(lo);
    }
  }

  @Override
  public void externalConnectionAdded(ForgeDirection fromDirection) {
    super.externalConnectionAdded(fromDirection);
    refreshInputs(fromDirection);
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection fromDirection) {
    super.externalConnectionRemoved(fromDirection);
    refreshInputs(fromDirection);
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY);
    }
    return fluidTypeLocked ? ICONS.get(ICON_KEY_LOCKED) : ICONS.get(ICON_KEY);
  }

  public Icon getTextureForInputMode() {
    return ICONS.get(ICON_EXTRACT_KEY);
  }

  public Icon getTextureForOutputMode() {
    return ICONS.get(ICON_INSERT_KEY);
  }

  public Icon getNotSetEdgeTexture() {
    return ICONS.get(ICON_EMPTY_EDGE);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    if(isActive() && tank.containsValidLiquid()) {
      return tank.getFluid().getFluid().getStillIcon();
    }
    return null;
  }

  // ------------------------------------------- Fluid API

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(network == null) {
      return 0;
    }
    return network.fill(from, resource, doFill);
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(network == null) {
      return null;
    }
    return network.drain(from, resource, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    if(network == null) {
      return null;
    }
    return network.drain(from, maxDrain, doDrain);
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if(network == null) {
      return false;
    }
    return getConectionMode(from).acceptsInput() && LiquidConduitNetwork.areFluidsCompatable(getFluidType(), new FluidStack(fluid, 0));
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if(network == null) {
      return false;
    }
    return getConectionMode(from).acceptsOutput() && LiquidConduitNetwork.areFluidsCompatable(getFluidType(), new FluidStack(fluid, 0));
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    if(network == null) {
      return null;
    }
    return new FluidTankInfo[] { new FluidTankInfo(tank) };
  }

  @Override
  protected boolean canJoinNeighbour(ILiquidConduit n) {
    return n instanceof AdvancedLiquidConduit;
  }

  @Override
  public AbstractTankConduitNetwork<? extends AbstractTankConduit> getTankNetwork() {
    return network;
  }

}
