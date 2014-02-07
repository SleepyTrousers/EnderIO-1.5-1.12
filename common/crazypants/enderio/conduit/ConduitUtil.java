package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle.FacadeRenderState;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduit;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduitNetwork;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.liquid.LiquidConduitNetwork;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.RedstoneConduitNetwork;
import crazypants.enderio.conduit.redstone.Signal;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.util.BlockCoord;
import crazypants.util.DyeColor;

public class ConduitUtil {

  public static final Random RANDOM = new Random();

  public static AbstractConduitNetwork<?, ?> createNetworkForType(Class<? extends IConduit> type) {
    if(IRedstoneConduit.class.isAssignableFrom(type)) {
      return new RedstoneConduitNetwork();
    } else if(IPowerConduit.class.isAssignableFrom(type)) {
      return new PowerConduitNetwork();
    } else if(AdvancedLiquidConduit.class.isAssignableFrom(type)) {
      return new AdvancedLiquidConduitNetwork();
    } else if(ILiquidConduit.class.isAssignableFrom(type)) {
      return new LiquidConduitNetwork();
    } else if(IItemConduit.class.isAssignableFrom(type)) {
      return new ItemConduitNetwork();
    }
    FMLCommonHandler.instance().raiseException(new Exception("Could not determine network type for class " + type), "ConduitUtil.createNetworkForType", false);
    return null;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void ensureValidNetwork(IConduit conduit) {
    TileEntity te = conduit.getBundle().getEntity();
    World world = te.worldObj;
    Collection<? extends IConduit> connections = ConduitUtil.getConnectedConduits(world, te.xCoord, te.yCoord, te.zCoord, conduit.getBaseConduitType());

    if(reuseNetwork(conduit, connections, world)) {
      return;
    }

    AbstractConduitNetwork res = createNetworkForType(conduit.getClass());
    res.init(conduit.getBundle(), connections, world);
    return;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static boolean reuseNetwork(IConduit con, Collection<? extends IConduit> connections, World world) {
    AbstractConduitNetwork network = null;
    for (IConduit conduit : connections) {
      if(network == null) {
        network = conduit.getNetwork();
      } else if(network != conduit.getNetwork()) {
        return false;
      }
    }
    if(network == null) {
      return false;
    }
    if(con.setNetwork(network)) {
      network.addConduit(con);
      network.notifyNetworkOfUpdate();
      return true;
    }
    return false;
  }

  public static <T extends IConduit> void disconectConduits(T con, ForgeDirection connDir) {
    con.conduitConnectionRemoved(connDir);
    BlockCoord loc = con.getLocation().getLocation(connDir);
    IConduit neighbour = ConduitUtil.getConduit(con.getBundle().getEntity().worldObj, loc.x, loc.y, loc.z, con.getBaseConduitType());
    if(neighbour != null) {
      neighbour.conduitConnectionRemoved(connDir.getOpposite());
      if(neighbour.getNetwork() != null) {
        neighbour.getNetwork().destroyNetwork();
      }
    }
    if(con.getNetwork() != null) { //this should have been destroyed when destroying the neighbours network but lets just make sure
      con.getNetwork().destroyNetwork();
    }
  }

  public static <T extends IConduit> boolean joinConduits(T con, ForgeDirection faceHit) {
    BlockCoord loc = con.getLocation().getLocation(faceHit);
    IConduit neighbour = ConduitUtil.getConduit(con.getBundle().getEntity().worldObj, loc.x, loc.y, loc.z, con.getBaseConduitType());
    if(neighbour != null && con.canConnectToConduit(faceHit, neighbour) && neighbour.canConnectToConduit(faceHit.getOpposite(), con)) {
      con.conduitConnectionAdded(faceHit);
      neighbour.conduitConnectionAdded(faceHit.getOpposite());
      if(con.getNetwork() != null) {
        con.getNetwork().destroyNetwork();
      }
      if(neighbour.getNetwork() != null) {
        neighbour.getNetwork().destroyNetwork();
      }
      return true;
    }
    return false;
  }

  public static boolean forceSkylightRecalculation(World worldObj, int xCoord, int yCoord, int zCoord) {
    int height = worldObj.getHeightValue(xCoord, zCoord);
    if(height <= yCoord) {
      for (int i = 1; i < 12; i++) {
        if(worldObj.isAirBlock(xCoord, yCoord + i, zCoord)) {
          //We need to force the re-lighting of the column due to a change
          //in the light reaching bellow the block from the sky. To avoid 
          //modifying core classes to expose this functionality I am just placing then breaking
          //a block above this one to force the check
          worldObj.setBlock(xCoord, yCoord + i, zCoord, 1, 0, 3);
          worldObj.setBlockToAir(xCoord, yCoord + i, zCoord);
          return true;
        }
      }
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  public static FacadeRenderState getRequiredFacadeRenderState(IConduitBundle bundle, EntityPlayer player) {
    if(!bundle.hasFacade()) {
      return FacadeRenderState.NONE;
    }
    if(isFacadeHidden(bundle, player)) {
      return FacadeRenderState.WIRE_FRAME;
    }
    return FacadeRenderState.FULL;
  }

  public static boolean isSolidFacadeRendered(IConduitBundle bundle, EntityPlayer player) {
    return bundle.getFacadeId() > 0 && !isFacadeHidden(bundle, player);
  }

  public static boolean isFacadeHidden(IConduitBundle bundle, EntityPlayer player) {
    //ModuleManager.itemHasActiveModule(player.getCurrentEquippedItem, OmniWrenchModule.MODULE_OMNI_WRENCH)
    return bundle.getFacadeId() > 0 && (isToolEquipped(player) || isConduitEquipped(player));
  }

  public static ConduitDisplayMode getDisplayMode(EntityPlayer player) {
    player = player == null ? EnderIO.proxy.getClientPlayer() : player;
    if(player == null) {
      return ConduitDisplayMode.ALL;
    }
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped == null) {
      return ConduitDisplayMode.ALL;
    }
    if(equipped.itemID != ModObject.itemYetaWrench.actualId) {
      return ConduitDisplayMode.ALL;
    }
    ConduitDisplayMode result = ConduitDisplayMode.getDisplayMode(equipped);
    if(result == null) {
      return ConduitDisplayMode.ALL;
    }
    return result;
  }

  public static boolean renderConduit(EntityPlayer player, IConduit con) {
    if(player == null || con == null) {
      return true;
    }
    return renderConduit(player, con.getBaseConduitType());
  }

  public static boolean renderConduit(EntityPlayer player, Class<? extends IConduit> conduitType) {
    if(player == null || conduitType == null) {
      return true;
    }
    ConduitDisplayMode mode = getDisplayMode(player);
    switch (mode) {
    case ALL:
      return true;
    case FLUID:
      return conduitType == ILiquidConduit.class;
    case ITEM:
      return conduitType == IItemConduit.class;
    case POWER:
      return conduitType == IPowerConduit.class;
    case REDSTONE:
      return conduitType == IRedstoneConduit.class || conduitType == IInsulatedRedstoneConduit.class;
    default:
      break;
    }
    return true;
  }

  public static boolean isConduitEquipped(EntityPlayer player) {
    player = player == null ? EnderIO.proxy.getClientPlayer() : player;
    if(player == null) {
      return false;
    }
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped == null) {
      return false;
    }
    return equipped.getItem() instanceof IConduitItem;
  }

  public static boolean isToolEquipped(EntityPlayer player) {
    player = player == null ? EnderIO.proxy.getClientPlayer() : player;
    if(player == null) {
      return false;
    }
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped == null) {
      return false;
    }
    if(MpsUtil.instance.isPowerFistEquiped(equipped)) {
      return MpsUtil.instance.isOmniToolActive(equipped);
    }
    return equipped.getItem() instanceof IToolWrench;
  }

  public static <T extends IConduit> T getConduit(IBlockAccess world, int x, int y, int z, Class<T> type) {
    if(world == null) {
      return null;
    }
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof IConduitBundle) {
      IConduitBundle con = (IConduitBundle) te;
      return con.getConduit(type);
    }
    return null;
  }

  public static <T extends IConduit> T getConduit(IBlockAccess world, TileEntity te, ForgeDirection dir, Class<T> type) {
    return ConduitUtil.getConduit(world, te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ, type);
  }

  public static <T extends IConduit> Collection<T> getConnectedConduits(IBlockAccess world, int x, int y, int z, Class<T> type) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      return Collections.emptyList();
    }
    List<T> result = new ArrayList<T>();
    IConduitBundle root = (IConduitBundle) te;
    T con = root.getConduit(type);
    if(con != null) {
      for (ForgeDirection dir : con.getConduitConnections()) {
        T connected = getConduit(world, root.getEntity(), dir, type);
        if(connected != null) {
          result.add(connected);
        }

      }
    }
    return result;
  }

  public static void writeToNBT(IConduit conduit, NBTTagCompound conduitRoot) {
    if(conduit == null) {
      return;
    }

    NBTTagCompound conduitBody = new NBTTagCompound();
    conduit.writeToNBT(conduitBody);

    conduitRoot.setString("conduitType", conduit.getClass().getCanonicalName());
    conduitRoot.setCompoundTag("conduit", conduitBody);
  }

  public static IConduit readConduitFromNBT(NBTTagCompound conduitRoot, short nbtVersion) {
    String typeName = conduitRoot.getString("conduitType");
    NBTTagCompound conduitBody = conduitRoot.getCompoundTag("conduit");
    if(typeName == null || conduitBody == null) {
      return null;
    }
    if(nbtVersion == 0 && "crazypants.enderio.conduit.liquid.LiquidConduit".equals(typeName)) {
      Log.debug("ConduitUtil.readConduitFromNBT: Converted pre 0.7.3 fluid conduit to advanced fluid conduit.");
      typeName = "crazypants.enderio.conduit.liquid.AdvancedLiquidConduit";
    }
    IConduit result;
    try {
      result = (IConduit) Class.forName(typeName).newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Could not create an instance of the conduit with name: " + typeName, e);
    }
    result.readFromNBT(conduitBody, nbtVersion);
    return result;

  }

  public static boolean isRedstoneControlModeMet(IConduitBundle bundle, RedstoneControlMode mode, DyeColor col) {

    if(mode == RedstoneControlMode.IGNORE) {
      return true;
    } else if(mode == RedstoneControlMode.NEVER) {
      return false;
    }

    int signalStrength = getInternalSignalForColor(bundle, col);
    if(signalStrength < 15 && DyeColor.RED == col && bundle != null && bundle.getEntity() != null) {
      TileEntity te = bundle.getEntity();
      signalStrength = Math.max(signalStrength, te.worldObj.getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord));
    }
    return mode.isConditionMet(mode, signalStrength);
  }

  public static int getInternalSignalForColor(IConduitBundle bundle, DyeColor col) {
    int signalStrength = 0;
    IRedstoneConduit rsCon = bundle.getConduit(IRedstoneConduit.class);
    if(rsCon != null) {
      Set<Signal> signals = rsCon.getNetworkOutputs(ForgeDirection.UNKNOWN);
      for (Signal sig : signals) {
        if(sig.color == col) {
          if(sig.strength > signalStrength) {
            signalStrength = sig.strength;
          }
        }
      }
    }
    return signalStrength;
  }

  public static boolean isFluidValid(FluidStack fluidStack) {
    if(fluidStack != null) {
      String name = FluidRegistry.getFluidName(fluidStack);
      if(name != null && !name.trim().isEmpty()) {
        return true;
      }
    }
    return false;
  }

}
