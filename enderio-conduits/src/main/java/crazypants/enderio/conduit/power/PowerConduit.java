package crazypants.enderio.conduit.power;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import crazypants.enderio.base.conduit.*;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.item.conduitprobe.PacketConduitProbe;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.IConduitComponent;
import crazypants.enderio.conduit.gui.PowerSettings;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

import static crazypants.enderio.base.conduit.ConnectionMode.OUTPUT;
import static crazypants.enderio.conduit.init.ConduitObject.item_power_conduit;

public class PowerConduit extends AbstractConduit implements IPowerConduit, IConduitComponent {

  static final Map<String, TextureAtlasSprite> ICONS = new HashMap<String, TextureAtlasSprite>();

  static final String[] POSTFIX = new String[] { "", "Enhanced", "Ender" };

  @Nonnull
  static ItemStack createItemStackForSubtype(int subtype) {
    ItemStack result = new ItemStack(item_power_conduit.getItem(), 1, subtype);
    return result;
  }

  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(@Nonnull TextureMap register) {

        for (String pf : POSTFIX) {
          ICONS.put(ICON_KEY + pf, register.registerSprite(new ResourceLocation(ICON_KEY + pf)));
          ICONS.put(ICON_KEY_INPUT + pf, register.registerSprite(new ResourceLocation(ICON_KEY_INPUT + pf)));
          ICONS.put(ICON_KEY_OUTPUT + pf, register.registerSprite(new ResourceLocation(ICON_KEY_OUTPUT + pf)));
          ICONS.put(ICON_CORE_KEY + pf, register.registerSprite(new ResourceLocation(ICON_CORE_KEY + pf)));
        }
        ICONS.put(ICON_TRANSMISSION_KEY, register.registerSprite(new ResourceLocation(ICON_TRANSMISSION_KEY)));
      }
    });
  }

  public static final float WIDTH = 0.075f;
  public static final float HEIGHT = 0.075f;

  public static final Vector3d MIN = new Vector3d(0.5f - WIDTH, 0.5 - HEIGHT, 0.5 - WIDTH);
  public static final Vector3d MAX = new Vector3d(MIN.x + WIDTH, MIN.y + HEIGHT, MIN.z + WIDTH);

  public static final BoundingBox BOUNDS = new BoundingBox(MIN, MAX);

  protected PowerConduitNetwork network;

  private int energyStoredRF;

  private int subtype;

  protected final EnumMap<EnumFacing, RedstoneControlMode> rsModes = new EnumMap<EnumFacing, RedstoneControlMode>(EnumFacing.class);
  protected final EnumMap<EnumFacing, DyeColor> rsColors = new EnumMap<EnumFacing, DyeColor>(EnumFacing.class);

  protected EnumMap<EnumFacing, Long> recievedTicks;

  public PowerConduit() {
  }

  public PowerConduit(int meta) {
    this.subtype = meta;
    ;
  }

  @Override
  public boolean getConnectionsDirty() {
    return connectionsDirty;
  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
    DyeColor col = DyeColor.getColorFromDye(player.getHeldItemMainhand());
    if (ConduitUtil.isProbeEquipped(player, hand)) {
      if (!player.world.isRemote) {
        PacketConduitProbe.sendInfoMessage(player, this);
      }
      return true;
    } else if (col != null && res.component != null && isColorBandRendered(res.component.dir)) {
      setExtractionSignalColor(res.component.dir, col);
      return true;
    } else if (ToolUtil.isToolEquipped(player, hand)) {
      if (!getBundle().getEntity().getWorld().isRemote) {
        if (res != null && res.component != null) {
          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;
          if (connDir == null || connDir == faceHit) {
            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }
            // Attempt to join networks
            return ConduitUtil.connectConduits(this, faceHit);
          } else if (externalConnections.contains(connDir)) {
            setConnectionMode(connDir, getNextConnectionMode(connDir));
            return true;
          } else if (containsConduitConnection(connDir)) {
            ConduitUtil.disconnectConduits(this, connDir);
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean isColorBandRendered(@Nonnull EnumFacing dir) {
    return getConnectionMode(dir) != ConnectionMode.DISABLED && getExtractionRedstoneMode(dir) != RedstoneControlMode.IGNORE;
  }

  @Override
  public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing dir) {
    rsModes.put(dir, mode);
    setClientStateDirty();
  }

  @Override
  @Nonnull
  public RedstoneControlMode getExtractionRedstoneMode(@Nonnull EnumFacing dir) {
    RedstoneControlMode res = rsModes.get(dir);
    if (res == null) {
      res = RedstoneControlMode.IGNORE;
    }
    return res;
  }

  @Override
  public void setExtractionSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    rsColors.put(dir, col);
    setClientStateDirty();
  }

  @Override
  @Nonnull
  public DyeColor getExtractionSignalColor(@Nonnull EnumFacing dir) {
    DyeColor res = rsColors.get(dir);
    if (res == null) {
      res = DyeColor.RED;
    }
    return res;
  }

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    setExtractionSignalColor(dir, DyeColor.values()[dataRoot.getShort("extractionSignalColor")]);
    setExtractionRedstoneMode(RedstoneControlMode.values()[dataRoot.getShort("extractionRedstoneMode")], dir);
  }

  @Override
  protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    dataRoot.setShort("extractionSignalColor", (short) getExtractionSignalColor(dir).ordinal());
    dataRoot.setShort("extractionRedstoneMode", (short) getExtractionRedstoneMode(dir).ordinal());
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("subtype", (short) subtype);
    nbtRoot.setInteger("energyStoredRF", energyStoredRF);

    for (Entry<EnumFacing, RedstoneControlMode> entry : rsModes.entrySet()) {
      if (entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("pRsMode." + entry.getKey().name(), ord);
      }
    }

    for (Entry<EnumFacing, DyeColor> entry : rsColors.entrySet()) {
      if (entry.getValue() != null) {
        short ord = (short) entry.getValue().ordinal();
        nbtRoot.setShort("pRsCol." + entry.getKey().name(), ord);
      }
    }
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    subtype = nbtRoot.getShort("subtype");

    if (nbtRoot.hasKey("energyStored")) {
      nbtRoot.setInteger("energyStoredRF", (int) (nbtRoot.getFloat("energyStored") * 10));

    }
    setEnergyStored(nbtRoot.getInteger("energyStoredRF"));

    for (EnumFacing dir : EnumFacing.VALUES) {
      String key = "pRsMode." + dir.name();
      if (nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if (ord >= 0 && ord < RedstoneControlMode.values().length) {
          rsModes.put(dir, RedstoneControlMode.values()[ord]);
        }
      }
      key = "pRsCol." + dir.name();
      if (nbtRoot.hasKey(key)) {
        short ord = nbtRoot.getShort(key);
        if (ord >= 0 && ord < DyeColor.values().length) {
          rsColors.put(dir, DyeColor.values()[ord]);
        }
      }
    }
  }

  @Override
  public void onTick() {
  }

  static int getMaxEnergyIO(int subtype) {
    switch (subtype) {
    case 1:
      return Config.powerConduitTierTwoRF;
    case 2:
      return Config.powerConduitTierThreeRF;
    default:
      return Config.powerConduitTierOneRF;
    }
  }

  // ----------- IEnergyStorage ---------------

  @Override
  public int getMaxEnergyStored() {
    return getMaxEnergyIO(subtype);
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (getMaxEnergyIO(subtype) == 0 || maxReceive <= 0) {
      return 0;
    }
    int freeSpace = getMaxEnergyStored() - getEnergyStored();
    int result = Math.min(maxReceive, freeSpace);
    if (!simulate && result > 0) {
      setEnergyStored(getEnergyStored() + result);
    }
    return result;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (getMaxEnergyIO(subtype) == 0 || maxExtract <= 0) {
      return 0;
    }
    return getMaxEnergyIO(subtype);
  }

  @Override
  public boolean canExtract() {
    return true;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

  @Override
  public int getEnergyStored() {
    return energyStoredRF;
  }

  // ----------- END --------------------------

  private void setEnergyStored(int energyStored) {
    energyStoredRF = MathHelper.clamp(energyStored, 0, getMaxEnergyStored());
  }

  /**
   * Used to get the capability of the conduit for the given direction
   *
   * @param dir side for the capability
   * @return returns the connection with reference to the relevant side
   */
  private IEnergyStorage getEnergyDir(EnumFacing dir) {
    if (dir != null)
      return new ConnectionSide(dir);
    return this;
  }

  private boolean isRedstoneEnabled(@Nonnull EnumFacing dir) {
    RedstoneControlMode mode = getExtractionRedstoneMode(dir);
    return ConduitUtil.isRedstoneControlModeMet(this, mode, getExtractionSignalColor(dir));
  }

  public int getMaxEnergyRecieved(@Nonnull EnumFacing dir) {
    ConnectionMode mode = getConnectionMode(dir);
    if (mode == OUTPUT || mode == ConnectionMode.DISABLED || !isRedstoneEnabled(dir)) {
      return 0;
    }
    return getMaxEnergyIO(subtype);
  }

  @Override
  public boolean onNeighborBlockChange(@Nonnull Block blockId) {
    if (network != null && network.powerManager != null) {
      network.powerManager.receptorsChanged();
    }
    return super.onNeighborBlockChange(blockId);
  }

  @Override
  public void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    recievedTicks = null;
  }

  @Override
  @Nonnull
  public IConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    this.network = (PowerConduitNetwork) network;
    return true;
  }

  @Override
  public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreDisabled) {
    IPowerInterface rec = getExternalPowerReceptor(direction);
    return rec != null;
  }

  @Override
  public boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit conduit) {
    boolean res = super.canConnectToConduit(direction, conduit);
    if (!res) {
      return false;
    }
    if (Config.powerConduitCanDifferentTiersConnect) {
      return res;
    }
    if (!(conduit instanceof IPowerConduit)) {
      return false;
    }
    IPowerConduit pc = (IPowerConduit) conduit;
    return pc.getMaxEnergyStored() == getMaxEnergyStored();
  }

  @Override
  public void externalConnectionAdded(@Nonnull EnumFacing direction) {
    super.externalConnectionAdded(direction);
    if (network != null) {
      TileEntity te = bundle.getEntity();
      BlockPos p = te.getPos().offset(direction);
      network.powerReceptorAdded(this, direction, p.getX(), p.getY(), p.getZ(), getExternalPowerReceptor(direction));
    }
  }

  @Override
  public void externalConnectionRemoved(@Nonnull EnumFacing direction) {
    super.externalConnectionRemoved(direction);
    if (network != null) {
      TileEntity te = bundle.getEntity();
      BlockPos p = te.getPos().offset(direction);
      network.powerReceptorRemoved(p.getX(), p.getY(), p.getZ());
    }
  }

  @Override
  public IPowerInterface getExternalPowerReceptor(@Nonnull EnumFacing direction) {
    TileEntity te = bundle.getEntity();
    World world = te.getWorld();
    if (world == null) {
      return null;
    }
    TileEntity test = world.getTileEntity(te.getPos().offset(direction));
    if (test == null) {
      return null;
    }
    if (test instanceof IConduitBundle) {
      return null;
    }
    return PowerHandlerUtil.getPowerInterface(test, direction.getOpposite());
  }

  @Override
  @Nonnull
  public ItemStack createItem() {
    return createItemStackForSubtype(subtype);
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  // Rendering
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return ICONS.get(ICON_CORE_KEY + POSTFIX[subtype]);
    }
    if (COLOR_CONTROLLER_ID.equals(component.data)) {
      return IconUtil.instance.whiteTexture;
    }
    return ICONS.get(ICON_KEY + POSTFIX[subtype]);
  }

  @Override
  public TextureAtlasSprite getTextureForInputMode() {
    return ICONS.get(ICON_KEY_INPUT + POSTFIX[subtype]);
  }

  @Override
  public TextureAtlasSprite getTextureForOutputMode() {
    return ICONS.get(ICON_KEY_OUTPUT + POSTFIX[subtype]);
  }

  @Override
  public TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  @Nonnull
  public Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key) {
    Collection<CollidableComponent> baseCollidables = super.createCollidables(key);
    if (key.dir == null) {
      return baseCollidables;
    }

    BoundingBox bb = ConduitGeometryUtil.instance.createBoundsForConnectionController(key.dir, key.offset);
    CollidableComponent cc = new CollidableComponent(IPowerConduit.class, bb, key.dir, COLOR_CONTROLLER_ID);

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    result.addAll(baseCollidables);
    result.add(cc);

    return result;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(wrapper, hashCodes);
    if (subtype != 1) {
      hashCodes.add(subtype);
    }
    hashCodes.addEnum(rsModes);
    hashCodes.addEnum(rsColors);
  }

  @Override
  public PowerConduitNetwork createNetworkForType() {
    return new PowerConduitNetwork();
  }

  //  @SideOnly(Side.CLIENT)
  //  @Override
  //  public ITabPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
  //    return new PowerSettings(gui, con);
  //  }

  @SideOnly(Side.CLIENT)
  @Nonnull
  @Override
  public ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IConduit con) {
    return new PowerSettings(gui, con);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int getGuiPanelTabOrder() {
    return 3;
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY)
      return facing == null;
    return false;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityEnergy.ENERGY)
      return (T) getEnergyDir(facing);
    return null;
  }

  /**
   * Class for determining the handling conduit energy in each direction
   */
  public class ConnectionSide implements IEnergyStorage {

    private EnumFacing side;

    public ConnectionSide(EnumFacing side) {
      this.side = side;
    }

    private boolean recievedRfThisTick() {
      if (recievedTicks == null || side == null || recievedTicks.get(side) == null || getBundle() == null || getBundle().getBundleworld() == null) {
        return false;
      }

      long curTick = getBundle().getBundleworld().getTotalWorldTime();
      long recT = recievedTicks.get(side);
      if (curTick - recT <= 5) {
        return true;
      }
      return false;
    }

    private boolean isEnabled() {
      if (getConnectionMode(side) == ConnectionMode.DISABLED || isRedstoneEnabled(side))
        return false;
      return true;
    }

    private boolean isExtract() {
      switch (getConnectionMode(side)) {
      case INPUT:
      case IN_OUT:
        return isRedstoneEnabled(side);
      default:
        return false;
      }
    }

    private boolean isReceive() {
      switch (getConnectionMode(side)) {
      case OUTPUT:
      case IN_OUT:
        return isRedstoneEnabled(side);
      default:
        return false;
      }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
      if (isReceive()) {
        int energyFinal = PowerConduit.this.receiveEnergy(maxReceive, simulate);
        if (getBundle() != null) {
          if (recievedTicks == null) {
            recievedTicks = new EnumMap<EnumFacing, Long>(EnumFacing.class);
          }
          if (side != null) {
            recievedTicks.put(side, getBundle().getBundleworld().getTotalWorldTime());
          }
        }
        return energyFinal;
      }
      return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
      if (isExtract())
        return PowerConduit.this.extractEnergy(maxExtract, simulate);
      return 0;
    }

    @Override
    public int getEnergyStored() {
      if (isEnabled())
        return PowerConduit.this.getEnergyStored();
      return 0;
    }

    @Override
    public int getMaxEnergyStored() {
      if (isEnabled())
        return PowerConduit.this.getMaxEnergyStored();
      return 0;
    }

    @Override
    public boolean canExtract() {
      return isExtract() && PowerConduit.this.canExtract() && !recievedRfThisTick();
    }

    @Override
    public boolean canReceive() {
      return isReceive() && PowerConduit.this.canReceive();
    }
  }
}
