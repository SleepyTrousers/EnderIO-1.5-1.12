package crazypants.enderio.conduit.liquid;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.*;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EnderLiquidConduit extends AbstractLiquidConduit {

  public static final String ICON_KEY = "enderio:liquidConduitEnder";
  public static final String ICON_CORE_KEY = "enderio:liquidConduitCoreEnder";
  public static final String ICON_EXTRACT_KEY = "enderio:liquidConduitAdvancedInput";
  public static final String ICON_INSERT_KEY = "enderio:liquidConduitAdvancedOutput";
  public static final String ICON_IN_OUT_KEY = "enderio:liquidConduitAdvancedInOut";

  static final Map<String, IIcon> ICONS = new HashMap<String, IIcon>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        ICONS.put(ICON_KEY, register.registerIcon(ICON_KEY));
        ICONS.put(ICON_CORE_KEY, register.registerIcon(ICON_CORE_KEY));
        ICONS.put(ICON_EXTRACT_KEY, register.registerIcon(ICON_EXTRACT_KEY));
        ICONS.put(ICON_INSERT_KEY, register.registerIcon(ICON_INSERT_KEY));
        ICONS.put(ICON_IN_OUT_KEY, register.registerIcon(ICON_IN_OUT_KEY));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  private EnderLiquidConduitNetwork network;
  private int ticksSinceFailedExtract;

  private final EnumMap<ForgeDirection, FluidFilter> outputFilters = new EnumMap<ForgeDirection, FluidFilter>(ForgeDirection.class);
  private final EnumMap<ForgeDirection, FluidFilter> inputFilters = new EnumMap<ForgeDirection, FluidFilter>(ForgeDirection.class);
  private int roundRobin = 0;
  @Override
  public ItemStack createItem() {
    return new ItemStack(EnderIO.itemLiquidConduit, 1, 2);
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    if(player.getCurrentEquippedItem() == null) {
      return false;
    }

    if(ToolUtil.isToolEquipped(player)) {

      if(!getBundle().getEntity().getWorldObj().isRemote) {

        if(res != null && res.component != null) {

          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);

          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {

            if(getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }

            BlockCoord loc = getLocation().getLocation(faceHit);
            ILiquidConduit n = ConduitUtil.getConduit(getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, ILiquidConduit.class);
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

  public FluidFilter getFilter(ForgeDirection dir, boolean isInput) {
    if(isInput) {
      return inputFilters.get(dir);
    }
    return outputFilters.get(dir);
  }

  public void setFilter(ForgeDirection dir, FluidFilter filter, boolean isInput) {
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
    for (ForgeDirection dir : externalConnections) {
      this.network.connectionChanged(this, dir);
    }

    return true;
  }

  @Override
  public IIcon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY);
    }
    return ICONS.get(ICON_KEY);
  }

  public IIcon getTextureForInputMode() {
    return ICONS.get(ICON_EXTRACT_KEY);
  }

  public IIcon getTextureForOutputMode() {
    return ICONS.get(ICON_INSERT_KEY);
  }

  public IIcon getTextureForInOutMode() {
    return ICONS.get(ICON_IN_OUT_KEY);
  }

  @Override
  public IIcon getTransmitionTextureForState(CollidableComponent component) {
    return null;
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit con) {
    if(!super.canConnectToConduit(direction, con)) {
      return false;
    }
    if(!(con instanceof EnderLiquidConduit)) {
      return false;
    }
    return true;
  }

  @Override
  public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
    super.setConnectionMode(dir, mode);
    refreshConnections(dir);
  }
  
  @Override
  public void setExtractionRedstoneMode(RedstoneControlMode mode, ForgeDirection dir) {
    super.setExtractionRedstoneMode(mode, dir);
    refreshConnections(dir);
  }

  private void refreshConnections(ForgeDirection dir) {
    if(network == null) {
      return;
    }
    network.connectionChanged(this, dir);
  }

  @Override
  public void externalConnectionAdded(ForgeDirection fromDirection) {
    super.externalConnectionAdded(fromDirection);
    refreshConnections(fromDirection);
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection fromDirection) {
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
    BlockCoord loc = getLocation();
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

    for (ForgeDirection dir : externalConnections) {
      if(autoExtractForDir(dir)) {
        if(network.extractFrom(this, dir)) {
          ticksSinceFailedExtract = 0;
        }
      }
    }

  }

  //Fluid API

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(network == null || !getConnectionMode(from).acceptsInput()) {
      return 0;
    }
    return network.fillFrom(this, from, resource, doFill);
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if(network == null) {
      return false;
    }
    return getConnectionMode(from).acceptsInput();
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return false;
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    return null;
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return null;
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    if(network == null) {
      return new FluidTankInfo[0];
    }
    return network.getTankInfo(this, from);
  }
  
  @Override
  protected void readTypeSettings(ForgeDirection dir, NBTTagCompound dataRoot) {
    super.readTypeSettings(dir, dataRoot);
    if(dataRoot.hasKey("roundRobin")) {
      setRoundRobin(dir, dataRoot.getBoolean("roundRobin"));
    } else {
      setRoundRobin(dir, true);
    }
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
  protected void writeTypeSettingsToNbt(ForgeDirection dir, NBTTagCompound dataRoot) {
    super.writeTypeSettingsToNbt(dir, dataRoot);
    dataRoot.setBoolean("roundRobin", isRoundRobin(dir));
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
    nbtRoot.setInteger("roundRobin", roundRobin);
    for (Entry<ForgeDirection, FluidFilter> entry : inputFilters.entrySet()) {
      if(entry.getValue() != null) {
        FluidFilter f = entry.getValue();
        if(f != null && !f.isDefault()) {
          NBTTagCompound itemRoot = new NBTTagCompound();
          f.writeToNBT(itemRoot);
          nbtRoot.setTag("inFilts." + entry.getKey().name(), itemRoot);
        }
      }
    }
    for (Entry<ForgeDirection, FluidFilter> entry : outputFilters.entrySet()) {
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
    if(nbtRoot.hasKey("roundRobin")) {
      roundRobin = nbtRoot.getInteger("roundRobin");
    } else {
      roundRobin = 0b111111;
    }
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
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

  public boolean isRoundRobin(ForgeDirection dir) {
    return (roundRobin & dir.flag) != 0;
  }

  public void setRoundRobin(ForgeDirection dir, boolean roundRobin) {
    if (roundRobin)
      this.roundRobin |= dir.flag;
    else
      this.roundRobin &= ~dir.flag;
  }
}
