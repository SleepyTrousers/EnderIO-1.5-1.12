package crazypants.enderio.conduit.liquid;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitComponent;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.itemLiquidConduit;

public class EnderLiquidConduit extends AbstractLiquidConduit implements IConduitComponent {

  public static final String ICON_KEY = "enderio:blocks/liquidConduitEnder";
  public static final String ICON_CORE_KEY = "enderio:blocks/liquidConduitCoreEnder"; 
  public static final String ICON_IN_OUT_KEY = "enderio:blocks/liquidConduitAdvancedInOut";

  static final Map<String, TextureAtlasSprite> ICONS = new HashMap<String, TextureAtlasSprite>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(TextureMap register) {
        ICONS.put(ICON_KEY, register.registerSprite(new ResourceLocation(ICON_KEY)));
        ICONS.put(ICON_CORE_KEY, register.registerSprite(new ResourceLocation(ICON_CORE_KEY)));
        ICONS.put(ICON_IN_OUT_KEY, register.registerSprite(new ResourceLocation(ICON_IN_OUT_KEY)));
      }

    });
  }

  private EnderLiquidConduitNetwork network;
  private int ticksSinceFailedExtract;

  private final EnumMap<EnumFacing, FluidFilter> outputFilters = new EnumMap<EnumFacing, FluidFilter>(EnumFacing.class);
  private final EnumMap<EnumFacing, FluidFilter> inputFilters = new EnumMap<EnumFacing, FluidFilter>(EnumFacing.class);

  @Override
  public ItemStack createItem() {
    return new ItemStack(itemLiquidConduit.getItem(), 1, 2);
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, EnumHand hand, RaytraceResult res, List<RaytraceResult> all) {
    if(player.getHeldItem(hand) == null) {
      return false;
    }

    if(ToolUtil.isToolEquipped(player, hand)) {

      if(!getBundle().getEntity().getWorld().isRemote) {

        if(res != null && res.component != null) {

          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;

          if(connDir == null || connDir == faceHit) {

            if(getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }

            BlockCoord loc = getLocation().getLocation(faceHit);
            ILiquidConduit n = ConduitUtil.getConduit(getBundle().getEntity().getWorld(), loc.x, loc.y, loc.z, ILiquidConduit.class);
            if(n == null) {
              return false;
            }
            if(!(n instanceof EnderLiquidConduit)) {
              return false;
            }
            return ConduitUtil.joinConduits(this, faceHit);
          } else if(containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setConnectionMode(connDir, getNextConnectionMode(connDir));
          } else if(containsConduitConnection(connDir)) {
            ConduitUtil.disconectConduits(this, connDir);

          }
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  public FluidFilter getFilter(EnumFacing dir, boolean isInput) {
    if(isInput) {
      return inputFilters.get(dir);
    }
    return outputFilters.get(dir);
  }

  public void setFilter(EnumFacing dir, FluidFilter filter, boolean isInput) {
    if(isInput) {
      inputFilters.put(dir, filter);
    } else {
      outputFilters.put(dir, filter);
    }
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    if(network == null) {
      this.network = null;
      return true;
    }
    if(!(network instanceof EnderLiquidConduitNetwork)) {
      return false;
    }
    this.network = (EnderLiquidConduitNetwork) network;
    for (EnumFacing dir : externalConnections) {
      this.network.connectionChanged(this, dir);
    }

    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public TextureAtlasSprite getTextureForState(CollidableComponent component) {
    if(component.dir ==null) {
      return ICONS.get(ICON_CORE_KEY);
    }
    return ICONS.get(ICON_KEY);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInputMode() {
    return AdvancedLiquidConduit.ICONS.get(AdvancedLiquidConduit.ICON_EXTRACT_KEY);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForOutputMode() {
    return AdvancedLiquidConduit.ICONS.get(AdvancedLiquidConduit.ICON_INSERT_KEY);    
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForInOutMode() {
    return ICONS.get(ICON_IN_OUT_KEY);
  }

  @Override
  public TextureAtlasSprite getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Vector4f getTransmitionTextureColorForState(CollidableComponent component) {
    return null;
  }

  @Override
  public boolean canConnectToConduit(EnumFacing direction, IConduit con) {
    if(!super.canConnectToConduit(direction, con)) {
      return false;
    }
    if(!(con instanceof EnderLiquidConduit)) {
      return false;
    }
    return true;
  }

  @Override
  public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    refreshConnections(dir);
  }
  
  @Override
  public void setExtractionRedstoneMode(RedstoneControlMode mode, EnumFacing dir) {
    super.setExtractionRedstoneMode(mode, dir);
    refreshConnections(dir);
  }

  private void refreshConnections(EnumFacing dir) {
    if(network == null) {
      return;
    }
    network.connectionChanged(this, dir);
  }

  @Override
  public void externalConnectionAdded(EnumFacing fromDirection) {
    super.externalConnectionAdded(fromDirection);
    refreshConnections(fromDirection);
  }

  @Override
  public void externalConnectionRemoved(EnumFacing fromDirection) {
    super.externalConnectionRemoved(fromDirection);
    refreshConnections(fromDirection);
  }

  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);
    if(world.isRemote) {
      return;
    }
    doExtract();
  }

  private void doExtract() {    
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
        if(network.extractFrom(this, dir)) {
          ticksSinceFailedExtract = 0;
        }
      }
    }

  }

  //Fluid API

  @Override
  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    if(network == null || !getConnectionMode(from).acceptsInput()) {
      return 0;
    }
    return network.fillFrom(this, from, resource, doFill);
  }

  @Override
  public boolean canFill(EnumFacing from, Fluid fluid) {
    if(network == null) {
      return false;
    }
    return getConnectionMode(from).acceptsInput();
  }

  @Override
  public boolean canDrain(EnumFacing from, Fluid fluid) {
    return false;
  }

  @Override
  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    return null;
  }

  @Override
  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    return null;
  }

  @Override
  public FluidTankInfo[] getTankInfo(EnumFacing from) {
    if(network == null) {
      return new FluidTankInfo[0];
    }
    return network.getTankInfo(this, from);
  }
  
  @Override
  protected void readTypeSettings(EnumFacing dir, NBTTagCompound dataRoot) {
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
  protected void writeTypeSettingsToNbt(EnumFacing dir, NBTTagCompound dataRoot) {
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
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    for (Entry<EnumFacing, FluidFilter> entry : inputFilters.entrySet()) {
      if(entry.getValue() != null) {
        FluidFilter f = entry.getValue();
        if(f != null && !f.isDefault()) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          f.writeToNBT(itemRoot);
          nbtRoot.setTag("inFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }
    for (Entry<EnumFacing, FluidFilter> entry : outputFilters.entrySet()) {
      if(entry.getValue() != null) {
        FluidFilter f = entry.getValue();
        if(f != null && !f.isDefault()) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          f.writeToNBT(itemRoot);
          nbtRoot.setTag("outFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);
    for (EnumFacing dir : EnumFacing.VALUES) {
      String key = "inFilts." + dir.name();
      if(nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        FluidFilter f = new FluidFilter();
        f.readFromNBT(filterTag);
        if(!f.isEmpty()) {
          inputFilters.put(dir, f);
        }
      }
      
      key = "outFilts." + dir.name();
      if(nbtRoot.hasKey(key)) {
        NBTTagCompound filterTag = (NBTTagCompound) nbtRoot.getTag(key);
        FluidFilter f = new FluidFilter();
        f.readFromNBT(filterTag);
        if(!f.isEmpty()) {
          outputFilters.put(dir, f);
        }
      }
    }

  }

}
