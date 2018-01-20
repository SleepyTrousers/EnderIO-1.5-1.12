package crazypants.enderio.conduit.liquid;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.fluid.IFluidWrapper;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.vecmath.Vector4f;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduit.IConduitComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static crazypants.enderio.conduit.init.ConduitObject.item_liquid_conduit;

public class LiquidConduit extends AbstractTankConduit implements IConduitComponent {

  static final int VOLUME_PER_CONNECTION = Fluid.BUCKET_VOLUME / 4;

  // TODO Lang

  public static final String ICON_KEY = "enderio:blocks/liquidConduit";
  public static final String ICON_KEY_LOCKED = "enderio:blocks/liquidConduitLocked";
  public static final String ICON_CORE_KEY = "enderio:blocks/liquidConduitCore";
  public static final String ICON_EXTRACT_KEY = "enderio:blocks/liquidConduitExtract";
  public static final String ICON_EMPTY_EXTRACT_KEY = "enderio:blocks/emptyLiquidConduitExtract";
  public static final String ICON_INSERT_KEY = "enderio:blocks/liquidConduitInsert";
  public static final String ICON_EMPTY_INSERT_KEY = "enderio:blocks/emptyLiquidConduitInsert";

  static final Map<String, TextureAtlasSprite> ICONS = new HashMap<String, TextureAtlasSprite>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(TextureMap register) {
        ICONS.put(ICON_KEY, register.registerSprite(new ResourceLocation(ICON_KEY)));
        ICONS.put(ICON_CORE_KEY, register.registerSprite(new ResourceLocation(ICON_CORE_KEY)));
        ICONS.put(ICON_EXTRACT_KEY, register.registerSprite(new ResourceLocation(ICON_EXTRACT_KEY)));
        ICONS.put(ICON_EMPTY_EXTRACT_KEY, register.registerSprite(new ResourceLocation(ICON_EMPTY_EXTRACT_KEY)));
        ICONS.put(ICON_EMPTY_INSERT_KEY, register.registerSprite(new ResourceLocation(ICON_EMPTY_INSERT_KEY)));
        ICONS.put(ICON_INSERT_KEY, register.registerSprite(new ResourceLocation(ICON_INSERT_KEY)));
        ICONS.put(ICON_KEY_LOCKED, register.registerSprite(new ResourceLocation(ICON_KEY_LOCKED)));
      }

    });
  }

  private LiquidConduitNetwork network;

  private float lastSyncRatio = -99;

  private int currentPushToken;

  // -----------------------------

  public static final int MAX_EXTRACT_PER_TICK = Config.fluidConduitExtractRate;

  public static final int MAX_IO_PER_TICK = Config.fluidConduitMaxIoRate;

  private EnumFacing startPushDir = EnumFacing.DOWN;

  //  private final Set<BlockPos> filledFromThisTick = new HashSet<BlockPos>();

  private long ticksSinceFailedExtract = 0;

  @Override
  public void updateEntity(@Nonnull World world) {
    super.updateEntity(world);
    if (world.isRemote) {
      return;
    }
    //filledFromThisTick.clear();
    updateStartPushDir();
    doExtract();

    if (stateDirty) {
      getBundle().dirty();
      stateDirty = false;
      lastSyncRatio = tank.getFilledRatio();

    } else if ((lastSyncRatio != tank.getFilledRatio() && world.getTotalWorldTime() % 2 == 0)) {

      //need to send a custom packet as we don't want want to trigger a full chunk update, just
      //need to get the required  values to the entity renderer
      BlockPos pos = getBundle().getLocation();
      PacketHandler.INSTANCE
          .sendToAllAround(new PacketFluidLevel(this), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
      lastSyncRatio = tank.getFilledRatio();
    }
  }

  private void doExtract() {
    if (!hasExtractableMode()) {
      return;
    }

    // assume failure, reset to 0 if we do extract
    ticksSinceFailedExtract++;
    if (ticksSinceFailedExtract > 9 && ticksSinceFailedExtract % 10 != 0) {
      // after 10 ticks of failing, only check every 10 ticks
      return;
    }

    for (EnumFacing dir : externalConnections) {
      if (autoExtractForDir(dir)) {

        IFluidWrapper extTank = getExternalHandler(dir);
        if (extTank != null) {

          FluidStack couldDrain = extTank.getAvailableFluid();
          if (couldDrain != null && couldDrain.amount > 0 && canFill(dir, couldDrain)) {
            couldDrain = couldDrain.copy();
            if (couldDrain.amount > MAX_EXTRACT_PER_TICK) {
              couldDrain.amount = MAX_EXTRACT_PER_TICK;
            }
            int used = pushLiquid(dir, couldDrain, true, network == null ? -1 : network.getNextPushToken());
            if (used > 0) {
              couldDrain.amount = used;
              extTank.drain(couldDrain);
              if (network != null && network.getFluidType() == null) {
                network.setFluidType(couldDrain);
              }
              ticksSinceFailedExtract = 0;
            }
          }
        }
      }
    }

  }

  // --------------- Fluid Capability ------------

  @Override
  public IFluidTankProperties[] getTankProperties() {
    if (network == null) {
      return new FluidTankProperties[0];
    }
    return new FluidTankProperties[] { new FluidTankProperties(tank.getFluid(), tank.getCapacity()) };
  }

  @Override
  public int fill(FluidStack resource, boolean doFill) {
    return 0;
  }

  @Nullable
  @Override
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    return drain(resource.amount, doDrain);
  }

  @Nullable
  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    return tank.drain(maxDrain, doDrain);
  }

  // --------------- End -------------------------

  public int fill(EnumFacing from, FluidStack resource, boolean doFill, boolean doPush, int pushToken) {
    if (network.canAcceptLiquid(resource)) {
      network.setFluidType(resource);
    } else {
      return 0;
    }
    resource = resource.copy();
    resource.amount = Math.min(MAX_IO_PER_TICK, resource.amount);

    if (doPush) {
      return pushLiquid(from, resource, doFill, pushToken);
    } else {
      return tank.fill(resource, doFill);
    }
  }

  private void updateStartPushDir() {

    EnumFacing newVal = getNextDir(startPushDir);
    boolean foundNewStart = false;
    while (newVal != startPushDir && !foundNewStart) {
      foundNewStart = getConduitConnections().contains(newVal) || getExternalConnections().contains(newVal);
      newVal = getNextDir(newVal);
    }
    startPushDir = newVal;
  }

  private EnumFacing getNextDir(@Nonnull EnumFacing dir) {
    if (dir.ordinal() >= EnumFacing.VALUES.length - 1) {
      return EnumFacing.VALUES[0];
    }
    return EnumFacing.VALUES[dir.ordinal() + 1];
  }

  private int pushLiquid(@Nullable EnumFacing from, FluidStack pushStack, boolean doPush, int token) {
    if (token == currentPushToken || pushStack == null || pushStack.amount <= 0 || network == null) {
      return 0;
    }
    currentPushToken = token;
    int pushed = 0;
    int total = pushStack.amount;

    EnumFacing dir = startPushDir;
    FluidStack toPush = pushStack.copy();

    int filledLocal = tank.fill(toPush, doPush);
    toPush.amount -= filledLocal;
    pushed += filledLocal;

    do {
      if (dir != from && canOutputToDir(dir) && !autoExtractForDir(dir)) {
        if (getConduitConnections().contains(dir)) {
          ILiquidConduit conduitCon = getFluidConduit(dir);
          if (conduitCon != null) {
            int toCon = ((LiquidConduit) conduitCon).pushLiquid(dir.getOpposite(), toPush, doPush, token);
            toPush.amount -= toCon;
            pushed += toCon;
          }
        } else if (getExternalConnections().contains(dir)) {
          IFluidWrapper con = getExternalHandler(dir);
          if (con != null) {
            int toExt = doPush ? con.fill(toPush) : con.offer(toPush);
            toPush.amount -= toExt;
            pushed += toExt;
            if (doPush) {
              network.outputedToExternal(toExt);
            }
          }
        }
      }
      dir = getNextDir(dir);
    } while (dir != startPushDir && pushed < total);

    return pushed;
  }

  private ILiquidConduit getFluidConduit(@Nonnull EnumFacing dir) {
    TileEntity ent = getBundle().getEntity();
    return ConduitUtil.getConduit(ent.getWorld(), ent, dir, ILiquidConduit.class);
  }

  @Override
  public boolean canFill(EnumFacing from, FluidStack fluid) {
    if (!getConnectionMode(from).acceptsInput() || network == null || fluid == null || fluid.amount <= 0) {
      return false;
    }
    if (tank.getFluid() == null) {
      return true;
    }
    if (fluid != null && FluidUtil.areFluidsTheSame(fluid.getFluid(), tank.getFluid().getFluid())) {
      return true;
    }
    return false;
  }

  @Override
  public boolean canDrain(EnumFacing from, FluidStack fluid) {
    if (!getConnectionMode(from).acceptsOutput() || tank.getFluid() == null || fluid == null) {
      return false;
    }
    return FluidUtil.areFluidsTheSame(tank.getFluid().getFluid(), fluid.getFluid());
  }

  @Override
  public void connectionsChanged() {
    super.connectionsChanged();
    updateTank();
  }

  @Override
  @Nonnull
  public ItemStack createItem() {
    return new ItemStack(item_liquid_conduit.getItem(), 1, 0);
  }

  @Override
  @Nonnull
  public IConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    if (network == null) {
      this.network = null;
      return true;
    }
    if (!(network instanceof AbstractTankConduitNetwork)) {
      return false;
    }

    AbstractTankConduitNetwork<?> n = (AbstractTankConduitNetwork<?>) network;
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
  public boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit con) {
    if (!super.canConnectToConduit(direction, con)) {
      return false;
    }
    if (!(con instanceof LiquidConduit)) {
      return false;
    }
    if (getFluidType() != null && ((LiquidConduit) con).getFluidType() == null) {
      return false;
    }
    return LiquidConduitNetwork.areFluidsCompatable(getFluidType(), ((LiquidConduit) con).getFluidType());
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return ICONS.get(ICON_CORE_KEY);
    }
    if (getConnectionMode(component.dir) == ConnectionMode.INPUT) {
      return ICONS.get(getFluidType() == null ? ICON_EMPTY_EXTRACT_KEY : ICON_EXTRACT_KEY);
    }
    if (getConnectionMode(component.dir) == ConnectionMode.OUTPUT) {
      return ICONS.get(getFluidType() == null ? ICON_EMPTY_INSERT_KEY : ICON_INSERT_KEY);
    }
    return fluidTypeLocked ? ICONS.get(ICON_KEY_LOCKED) : ICONS.get(ICON_KEY);
  }

  @Override
  public TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    if (tank.getFluid() != null && tank.getFluid().getFluid() != null) {
      return RenderUtil.getStillTexture(tank.getFluid());
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    if (tank.getFluid() != null && tank.getFluid().getFluid() != null) {
      int color = tank.getFluid().getFluid().getColor(tank.getFluid());
      return new Vector4f((color >> 16 & 0xFF) / 255d, (color >> 8 & 0xFF) / 255d, (color & 0xFF) / 255d, 1);
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

  @Override
  public IFluidHandler getFluidDir(EnumFacing dir) {
    if (dir != null) {
      return new ConnectionLiquidConduitSide(dir);
    }
    return this;
  }

  @Override
  @Nonnull
  public LiquidConduitNetwork createNetworkForType() {
    return new LiquidConduitNetwork();
  }

  protected class ConnectionLiquidConduitSide extends ConnectionLiquidSide {

    public ConnectionLiquidConduitSide(EnumFacing side) {
      super(side);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      if (canFill(side, resource) && network.lockNetworkForFill()) {
        try {
          int res = LiquidConduit.this.fill(side, resource, doFill, true, network == null ? -1 : network.getNextPushToken());
          if (doFill && externalConnections.contains(side) && network != null) {
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
  }

}
