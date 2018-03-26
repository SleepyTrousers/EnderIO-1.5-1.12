package crazypants.enderio.conduits.conduit.liquid;

import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduits.conduit.IConduitComponent;
import crazypants.enderio.util.Prep;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.conduits.init.ConduitObject.item_liquid_conduit;

public class EnderLiquidConduit extends AbstractLiquidConduit implements IConduitComponent {

  public static final TextureSupplier ICON_KEY = TextureRegistry.registerTexture("blocks/liquid_conduit_ender");
  public static final TextureSupplier ICON_CORE_KEY = TextureRegistry.registerTexture("blocks/liquid_conduit_core_ender");
  public static final TextureSupplier ICON_IN_OUT_KEY = TextureRegistry.registerTexture("blocks/liquid_conduit_advanced_in_out");

  private EnderLiquidConduitNetwork network;
  private int ticksSinceFailedExtract;

  private final EnumMap<EnumFacing, FluidFilter> outputFilters = new EnumMap<EnumFacing, FluidFilter>(EnumFacing.class);
  private final EnumMap<EnumFacing, FluidFilter> inputFilters = new EnumMap<EnumFacing, FluidFilter>(EnumFacing.class);

  @Override
  @Nonnull
  public ItemStack createItem() {
    return new ItemStack(item_liquid_conduit.getItemNN(), 1, 2);
  }

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
    if (Prep.isInvalid(player.getHeldItem(hand))) {
      return false;
    }

    if (ToolUtil.isToolEquipped(player, hand)) {

      if (!getBundle().getEntity().getWorld().isRemote) {

        final CollidableComponent component = res.component;
        if (component != null) {

          EnumFacing connDir = component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;

          if (connDir == null || connDir == faceHit) {

            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }

            BlockPos pos = getBundle().getLocation().offset(faceHit);
            ILiquidConduit n = ConduitUtil.getConduit(getBundle().getEntity().getWorld(), pos.getX(), pos.getY(), pos.getZ(), ILiquidConduit.class);
            if (n == null) {
              return false;
            }
            if (!(n instanceof EnderLiquidConduit)) {
              return false;
            }
            return ConduitUtil.connectConduits(this, faceHit);
          } else if (containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setConnectionMode(connDir, getNextConnectionMode(connDir));
          } else if (containsConduitConnection(connDir)) {
            ConduitUtil.disconnectConduits(this, connDir);

          }
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public @Nullable IConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  public FluidFilter getFilter(@Nonnull EnumFacing dir, boolean isInput) {
    if (isInput) {
      return inputFilters.get(dir);
    }
    return outputFilters.get(dir);
  }

  public void setFilter(@Nonnull EnumFacing dir, @Nonnull FluidFilter filter, boolean isInput) {
    if (isInput) {
      inputFilters.put(dir, filter);
    } else {
      outputFilters.put(dir, filter);
    }
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
    if (!(network instanceof EnderLiquidConduitNetwork)) {
      return false;
    }
    this.network = (EnderLiquidConduitNetwork) network;
    for (EnumFacing dir : externalConnections) {
      this.network.connectionChanged(this, dir);
    }

    return true;
  }

  @Override
  public void clearNetwork() {
    this.network = null;
  }

  // --------------------------------
  // TEXTURES
  // --------------------------------

  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return ICON_CORE_KEY.get(TextureAtlasSprite.class);
    }
    return ICON_KEY.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInputMode() {
    return AdvancedLiquidConduit.ICON_EXTRACT_KEY.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForOutputMode() {
    return AdvancedLiquidConduit.ICON_INSERT_KEY.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInOutMode() {
    return ICON_IN_OUT_KEY.get(TextureAtlasSprite.class);
  }

  @Override
  public @Nonnull TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    return null;
  }

  @Override
  public boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit con) {
    if (!super.canConnectToConduit(direction, con)) {
      return false;
    }
    if (!(con instanceof EnderLiquidConduit)) {
      return false;
    }
    return true;
  }

  @Override
  public void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    refreshConnections(dir);
  }

  @Override
  public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing dir) {
    super.setExtractionRedstoneMode(mode, dir);
    refreshConnections(dir);
  }

  private void refreshConnections(@Nonnull EnumFacing dir) {
    if (network == null) {
      return;
    }
    network.connectionChanged(this, dir);
  }

  @Override
  public void externalConnectionAdded(@Nonnull EnumFacing fromDirection) {
    super.externalConnectionAdded(fromDirection);
    refreshConnections(fromDirection);
  }

  @Override
  public void externalConnectionRemoved(@Nonnull EnumFacing fromDirection) {
    super.externalConnectionRemoved(fromDirection);
    refreshConnections(fromDirection);
  }

  @Override
  public void updateEntity(@Nonnull World world) {
    super.updateEntity(world);
    if (world.isRemote) {
      return;
    }
    doExtract();
  }

  private void doExtract() {
    if (!hasExtractableMode()) {
      return;
    }
    if (network == null) {
      return;
    }

    // assume failure, reset to 0 if we do extract
    ticksSinceFailedExtract++;
    if (ticksSinceFailedExtract > 25 && ticksSinceFailedExtract % 10 != 0) {
      // after 25 ticks of failing, only check every 10 ticks
      return;
    }

    for (EnumFacing dir : externalConnections) {
      if (autoExtractForDir(dir)) {
        if (network.extractFrom(this, dir)) {
          ticksSinceFailedExtract = 0;
        }
      }
    }

  }

  // ---------- Fluid Capability -----------------

  // Fill and Tank properties are both sided, and are handled below
  @Override
  public int fill(FluidStack resource, boolean doFill) {
    return 0;
  }

  @Override
  public IFluidTankProperties[] getTankProperties() {
    return new IFluidTankProperties[0];
  }

  @Nullable
  @Override
  public FluidStack drain(FluidStack resource, boolean doDrain) {
    return null;
  }

  @Nullable
  @Override
  public FluidStack drain(int maxDrain, boolean doDrain) {
    return null;
  }

  // ---------- End ------------------------------

  // Fluid API

  @Override
  public boolean canFill(EnumFacing from, FluidStack fluid) {
    if (network == null) {
      return false;
    }
    return getConnectionMode(from).acceptsInput();
  }

  @Override
  public boolean canDrain(EnumFacing from, FluidStack fluid) {
    return false;
  }

  @Override
  protected void readTypeSettings(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    super.readTypeSettings(dir, dataRoot);
    if (dataRoot.hasKey("outputFilters")) {
      FluidFilter out = new FluidFilter();
      out.readFromNBT(dataRoot.getCompoundTag("outputFilters"));
      outputFilters.put(dir, out);
    }
    if (dataRoot.hasKey("inputFilters")) {
      FluidFilter in = new FluidFilter();
      in.readFromNBT(dataRoot.getCompoundTag("inputFilters"));
      inputFilters.put(dir, in);
    }
  }

  @Override
  protected void writeTypeSettingsToNbt(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound dataRoot) {
    super.writeTypeSettingsToNbt(dir, dataRoot);
    FluidFilter out = outputFilters.get(dir);
    if (out != null) {
      NBTTagCompound outTag = new NBTTagCompound();
      out.writeToNBT(outTag);
      dataRoot.setTag("outputFilters", outTag);
    }
    FluidFilter in = inputFilters.get(dir);
    if (in != null) {
      NBTTagCompound inTag = new NBTTagCompound();
      in.writeToNBT(inTag);
      dataRoot.setTag("inputFilters", inTag);
    }
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    for (Entry<EnumFacing, FluidFilter> entry : inputFilters.entrySet()) {
      if (entry.getValue() != null) {
        FluidFilter f = entry.getValue();
        if (f != null && !f.isDefault()) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          f.writeToNBT(itemRoot);
          nbtRoot.setTag("inFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }
    for (Entry<EnumFacing, FluidFilter> entry : outputFilters.entrySet()) {
      if (entry.getValue() != null) {
        FluidFilter f = entry.getValue();
        if (f != null && !f.isDefault()) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          f.writeToNBT(itemRoot);
          nbtRoot.setTag("outFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    for (EnumFacing dir : EnumFacing.VALUES) {
      String key = "inFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        FluidFilter f = new FluidFilter();
        f.readFromNBT(filterTag);
        if (!f.isEmpty()) {
          inputFilters.put(dir, f);
        }
      }

      key = "outFilts." + dir.name();
      if (nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        FluidFilter f = new FluidFilter();
        f.readFromNBT(filterTag);
        if (!f.isEmpty()) {
          outputFilters.put(dir, f);
        }
      }
    }

  }

  @Override
  @Nonnull
  public EnderLiquidConduitNetwork createNetworkForType() {
    return new EnderLiquidConduitNetwork();
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return (T) new ConnectionEnderLiquidSide(facing);
    }
    return null;
  }

  protected class ConnectionEnderLiquidSide extends ConnectionLiquidSide {
    public ConnectionEnderLiquidSide(EnumFacing side) {
      super(side);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      if (canFill(side, resource)) {
        return network.fillFrom(EnderLiquidConduit.this, side, resource, doFill);
      }
      return 0;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      if (network == null) {
        return new FluidTankProperties[0];
      }
      return network.getTankProperties(EnderLiquidConduit.this, side);
    }
  }
}
