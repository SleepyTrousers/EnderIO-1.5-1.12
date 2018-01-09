package crazypants.enderio.conduit.liquid;

import static crazypants.enderio.conduit.init.ConduitObject.item_liquid_conduit;

import java.util.HashMap;
import java.util.Map;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduitComponent;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class AdvancedLiquidConduit extends AbstractTankConduit implements IConduitComponent {

  public static final int CONDUIT_VOLUME = Fluid.BUCKET_VOLUME;

  // TODO Lang
  public static final String ICON_KEY = "enderio:blocks/liquidConduitAdvanced";
  public static final String ICON_KEY_LOCKED = "enderio:blocks/liquidConduitAdvancedLocked";
  public static final String ICON_CORE_KEY = "enderio:blocks/liquidConduitCoreAdvanced";
  public static final String ICON_EXTRACT_KEY = "enderio:blocks/liquidConduitAdvancedInput";
  public static final String ICON_INSERT_KEY = "enderio:blocks/liquidConduitAdvancedOutput";

  
  public static final String ICON_EMPTY_EDGE = "enderio:blocks/liquidConduitAdvancedEdge";

  static final Map<String, TextureAtlasSprite> ICONS = new HashMap<String, TextureAtlasSprite>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(TextureMap register) {
        ICONS.put(ICON_KEY, register.registerSprite(new ResourceLocation(ICON_KEY)));
        ICONS.put(ICON_CORE_KEY, register.registerSprite(new ResourceLocation(ICON_CORE_KEY)));
        ICONS.put(ICON_EMPTY_EDGE, register.registerSprite(new ResourceLocation(ICON_EMPTY_EDGE)));
        ICONS.put(ICON_KEY_LOCKED, register.registerSprite(new ResourceLocation(ICON_KEY_LOCKED)));
        ICONS.put(ICON_INSERT_KEY, register.registerSprite(new ResourceLocation(ICON_INSERT_KEY)));        
        ICONS.put(ICON_EXTRACT_KEY, register.registerSprite(new ResourceLocation(ICON_EXTRACT_KEY)));        
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
  public void updateEntity(@Nonnull World world) {
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
    // Extraction can happen on extract mode or in/out mode
    if(!hasExtractableMode()) {
      return;
    }
    if(network == null) {
      return;
    }

    // assume failure, reset to 0 if we do extract
    ticksSinceFailedExtract++;
    if(ticksSinceFailedExtract > 25 && ticksSinceFailedExtract % 10 != 0) {
      // after 25 ticks of failing, only check every 10 ticks
      return;
    }
  
    for (EnumFacing dir : externalConnections) {
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
  @Nonnull
  public ItemStack createItem() {
    return new ItemStack(item_liquid_conduit.getItem(), 1, 1);
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network) {
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
  public boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit con) {
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
  public void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    refreshInputs(dir);
  }
  
  @Override
  public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing dir) {
    super.setExtractionRedstoneMode(mode, dir);
    refreshInputs(dir);
  }

  private void refreshInputs(@Nonnull EnumFacing dir) {
    if(network == null) {
      return;
    }
    LiquidOutput lo = new LiquidOutput(getBundle().getLocation().offset(dir), dir.getOpposite());
    network.removeInput(lo);
    if(canInputToDir(dir) && containsExternalConnection(dir)) {
      network.addInput(lo);
    }
  }

  @Override
  public void externalConnectionAdded(@Nonnull EnumFacing fromDirection) {
    super.externalConnectionAdded(fromDirection);
    refreshInputs(fromDirection);
  }

  @Override
  public void externalConnectionRemoved(@Nonnull EnumFacing fromDirection) {
    super.externalConnectionRemoved(fromDirection);
    refreshInputs(fromDirection);
  }

  //-------------------------------------
  // TEXTURES
  //-------------------------------------


  @SideOnly(Side.CLIENT)
  @Override
  @Nonnull
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if(component.dir == null) {
      return ICONS.get(ICON_CORE_KEY);
    }
    return fluidTypeLocked ? ICONS.get(ICON_KEY_LOCKED) : ICONS.get(ICON_KEY);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInputMode() {
    return ICONS.get(ICON_EXTRACT_KEY);       
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForOutputMode() {    
    return ICONS.get(ICON_INSERT_KEY);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getNotSetEdgeTexture() {
    return ICONS.get(ICON_EMPTY_EDGE);
  }

  @Override
  public TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component) {
    if(isActive() && tank.containsValidLiquid()) {
      return RenderUtil.getStillTexture(tank.getFluid());
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component) {
    if (isActive() && tank.containsValidLiquid()) {
      int color = tank.getFluid().getFluid().getColor(tank.getFluid());
      return new Vector4f((color >> 16 & 0xFF) / 255d, (color >> 8 & 0xFF) / 255d, (color & 0xFF) / 255d, 1);
    }
    return null;
  }

  // ------------------------------------------- Fluid API

  // TODO Confirm if the "from" direction in previous fluid API versions is needed still

  @Override
  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    if(network == null || !getConnectionMode(from).acceptsInput()) {
      return 0;
    }
    return network.fill(from, resource, doFill);
  }

  @Override
  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    if(network == null || !getConnectionMode(from).acceptsOutput()) {
      return null;
    }
    return network.drain(from, resource, doDrain);
  }

  @Override
  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    if(network == null || !getConnectionMode(from).acceptsOutput()) {
      return null;
    }
    return network.drain(from, maxDrain, doDrain);
  }

  @Override
  public boolean canFill(EnumFacing from, Fluid fluid) {
    if(network == null) {
      return false;
    }
    return canExtractFromDir(from) && LiquidConduitNetwork.areFluidsCompatable(getFluidType(), new FluidStack(fluid, 0));
  }

  @Override
  public boolean canDrain(EnumFacing from, Fluid fluid) {
    if(network == null) {
      return false;
    }
    return canInputToDir(from) && LiquidConduitNetwork.areFluidsCompatable(getFluidType(), new FluidStack(fluid, 0));
  }

  @Override
  public FluidTankInfo[] getTankInfo(EnumFacing from) {
    if(network == null) {
      return new FluidTankInfo[0];
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

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(wrapper, hashCodes);
    FluidStack fluidType = getFluidType();
    if (fluidType != null && fluidType.getFluid() != null) {
      hashCodes.add(fluidType.getFluid());
    }
  }

  @Override
  @Nonnull
  public AdvancedLiquidConduitNetwork createNetworkForType() {
    return new AdvancedLiquidConduitNetwork();
  }

}
