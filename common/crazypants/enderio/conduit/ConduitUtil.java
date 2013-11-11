package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduitBundle.FacadeRenderState;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.liquid.LiquidConduitNetwork;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.RedstoneConduitNetwork;

public class ConduitUtil {

  public static final Random RANDOM = new Random();

  public static AbstractConduitNetwork<?> createNetworkForType(Class<? extends IConduit> type) {
    if(IRedstoneConduit.class.isAssignableFrom(type)) {
      return new RedstoneConduitNetwork();
    } else if(IPowerConduit.class.isAssignableFrom(type)) {
      return new PowerConduitNetwork();
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

    AbstractConduitNetwork res = createNetworkForType(conduit.getBaseConduitType());
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
    con.setNetwork(network);
    network.addConduit(con);
    network.notifyNetworkOfUpdate();
    return true;
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
    for (ForgeDirection dir : root.getAllConnections()) {
      T con = getConduit(world, root.getEntity(), dir, type);
      if(con != null) {
        result.add(con);
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

  public static IConduit readConduitFromNBT(NBTTagCompound conduitRoot) {
    String typeName = conduitRoot.getString("conduitType");
    NBTTagCompound conduitBody = conduitRoot.getCompoundTag("conduit");
    if(typeName == null || conduitBody == null) {
      return null;
    }
    IConduit result;
    try {
      result = (IConduit) Class.forName(typeName).newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Could not create an instance of the conduit with name: " + typeName, e);
    }
    result.readFromNBT(conduitBody);
    return result;

  }

}
