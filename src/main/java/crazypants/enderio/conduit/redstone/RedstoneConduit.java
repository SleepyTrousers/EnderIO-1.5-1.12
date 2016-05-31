package crazypants.enderio.conduit.redstone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.DyeColor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduit;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class RedstoneConduit extends AbstractConduit implements IRedstoneConduit {

  static final Map<String, TextureAtlasSprite> ICONS = new HashMap<String, TextureAtlasSprite>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(TextureMap register) {        
        ICONS.put(KEY_CONDUIT_ICON, register.registerSprite(new ResourceLocation(KEY_CONDUIT_ICON)));
        ICONS.put(KEY_TRANSMISSION_ICON, register.registerSprite(new ResourceLocation(KEY_TRANSMISSION_ICON)));
      }

    });
  }

  protected RedstoneConduitNetwork network;

  protected final List<Set<Signal>> externalSignals = new ArrayList<Set<Signal>>();

  protected boolean neighbourDirty = true;

  @SuppressWarnings("unused")
  protected RedstoneConduit() {
    for (EnumFacing ignored : EnumFacing.VALUES) {
      externalSignals.add(new HashSet<Signal>());
    }
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IRedstoneConduit.class;
  }

  @Override
  public AbstractConduitNetwork<IRedstoneConduit, IRedstoneConduit> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    this.network = (RedstoneConduitNetwork) network;
    return true;
  }

  @Override
  public void updateNetwork() {
    World world = getBundle().getEntity().getWorld();
    if(world != null) {
      updateNetwork(world);
    }
  }
  
  @Override
  public void onChunkUnload(World worldObj) {
    RedstoneConduitNetwork network = (RedstoneConduitNetwork) getNetwork();
    if (network != null) {
      Set<Signal> oldSignals = Sets.newHashSet(network.getSignals());
      List<IRedstoneConduit> conduits = Lists.newArrayList(network.getConduits());
      super.onChunkUnload(worldObj);
      network.afterChunkUnload(conduits, oldSignals);
    }
  }

  protected boolean acceptSignalsForDir(EnumFacing dir) {
    BlockCoord loc = getLocation().getLocation(dir);
    return ConduitUtil.getConduit(getBundle().getEntity().getWorld(), loc.x, loc.y, loc.z, IRedstoneConduit.class) == null;
  }

  @Override
  public Set<Signal> getNetworkInputs() {
    return getNetworkInputs(null);
  }

  @Override
  public Set<Signal> getNetworkInputs(EnumFacing side) {
    if(network != null) {
      network.setNetworkEnabled(false);
    }

    Set<Signal> res = new HashSet<Signal>();
    for (EnumFacing dir : EnumFacing.VALUES) {
      if((side == null || dir == side) && acceptSignalsForDir(dir)) {
        int input = getExternalPowerLevel(dir);
        if(input > 1) { // need to degrade external signals by one as they
                        // enter
          BlockCoord loc = getLocation().getLocation(dir);
          Signal signal = new Signal(loc.x, loc.y, loc.z, dir, input - 1, getSignalColor(dir));
          res.add(signal);
        }

        if (Loader.isModLoaded("MineFactoryReloaded")) {
          // Add stored RedNet input. See onInputsChanged below for more information.
          res.addAll(externalSignals.get(dir.ordinal()));

          // Manually check if neighbors are outputting bundled redstone signals.
          // This is required to directly support other blocks implementing the
          // RedNet API, without requiring a piece of RedNet cable in-between.
          int[] bundledInput = getExternalBundledPowerLevel(dir);
          if(bundledInput != null) {
            BlockCoord loc = getLocation().getLocation(dir);
            for (int subnet = 0; subnet < bundledInput.length; ++subnet) {
              if(bundledInput[subnet] > 1) { // force signal strength reduction to avoid cycles
                int color = convertColorForRedNet(subnet);
                Signal signal = new Signal(loc.x, loc.y, loc.z, dir, bundledInput[subnet] - 1, DyeColor.fromIndex(color));
                res.add(signal);
              }
            }
          }
        }
        //TODO: 1.9 Computer Craft
//        if (Loader.isModLoaded("ComputerCraft") && canConnectToExternal(dir, false)) {
//          BlockCoord loc = getLocation().getLocation(dir);
//          int bundledInput = getComputerCraftBundledPowerLevel(dir);
//          if(bundledInput >= 0){
//            for(int i = 0; i < 16; i++) {
//              int color = bundledInput >>> i & 1;
//                Signal signal = new Signal(loc.x, loc.y, loc.z, dir, color == 1 ? 16 : 0, DyeColor.fromIndex(Math.max(0, 15 - i)));
//                res.add(signal);
//            }
//          }
//        }
      }
    }

    if(network != null) {
      network.setNetworkEnabled(true);
    }

    return res;
  }

  @Override
  public Set<Signal> getNetworkOutputs(EnumFacing side) {
    if(network == null) {
      return Collections.emptySet();
    }
    return network.getSignals();
  }

  @Override
  public boolean onNeighborBlockChange(Block blockId) {    
    World world = getBundle().getEntity().getWorld();
    if(world.isRemote) {
      return false;
    }
    boolean res = super.onNeighborBlockChange(blockId);
    if(network == null || network.updatingNetwork) {
      return false;
    }
    neighbourDirty |= blockId != EnderIO.blockConduitBundle;
    return res;
  }

  @Override
  public void updateEntity(World world) {
    super.updateEntity(world);
    if(!world.isRemote && neighbourDirty) {
      network.destroyNetwork();
      updateNetwork(world);
      neighbourDirty = false;
    }
  }

  //returns 16 for string power inputs
  protected int getExternalPowerLevel(EnumFacing dir) {
    World world = getBundle().getEntity().getWorld();
    BlockCoord loc = getLocation();
    loc = loc.getLocation(dir);

    //int strong = world.isBlockProvidingPowerTo(loc.x, loc.y, loc.z, dir.ordinal());
    int strong = world.getStrongPower(loc.getBlockPos(), dir);
    if(strong > 0) {
      return 16;
    }

    //int res = world.getIndirectPowerLevelTo(loc.x, loc.y, loc.z, dir.ordinal());
    int res = world.getRedstonePower(loc.getBlockPos(), dir);
    IBlockState bs = world.getBlockState(loc.getBlockPos());
    Block block = bs.getBlock();
    if(res < 15 && block == Blocks.REDSTONE_WIRE) {
      int wireIn = bs.getValue(BlockRedstoneWire.POWER);
      res = Math.max(res, wireIn);
      
      
    }
    return res;
  }

  protected int[] getExternalBundledPowerLevel(EnumFacing dir) {
//    World world = getBundle().getEntity().getWorld();
//    BlockCoord loc = getLocation();
//    loc = loc.getLocation(dir);
//
//    Block block = world.getBlock(loc.x, loc.y, loc.z);
//    if(block instanceof IRedNetOutputNode) {
//      return ((IRedNetOutputNode) block).getOutputValues(world, loc.x, loc.y, loc.z, dir.getOpposite());
//    }

    return null;
  }

  //TODO: 1.9 Computer Craft
//  @Method(modid = "ComputerCraft")
//  protected int getComputerCraftBundledPowerLevel(EnumFacing dir) {
//    BlockCoord loc = getLocation().getLocation(dir);
//    return ComputerCraftAPI.getBundledRedstoneOutput(getBundle().getBundleWorldObj(), loc.getBlockPos(), dir.getOpposite());
//  }
 

  @Override
  public int isProvidingWeakPower(EnumFacing toDirection) {
    if(network == null || !network.isNetworkEnabled()) {
      return 0;
    }
    int result = 0;
    for (Signal signal : getNetworkOutputs(toDirection.getOpposite())) {
      result = Math.max(result, signal.strength);
    }
    return result;
  }

  @Override
  public String toString() {
    return "RedstoneConduit [network=" + network + " connections=" + conduitConnections + " active=" + active + "]";
  }
  
  @Override
  public boolean onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbourPos) {
    return false;
  }

  // RedNet refers to colors in inverse order...
  private static int convertColorForRedNet(int colorOrSubnet) {
    return 15 - colorOrSubnet;
  }
}
