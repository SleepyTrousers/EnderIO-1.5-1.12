package crazypants.enderio;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

  public static final int GUI_ID_ENDERFACE = 1;
  public static final int GUI_ID_PAINTER = 2;
  public static final int GUI_ID_STIRLING_GEN = 3;
  public static final int GUI_ID_ALLOY_SMELTER = 4;
  public static final int GUI_ID_CAPACITOR_BANK = 5;
  public static final int GUI_ID_CRUSHER = 6;
  public static final int GUI_ID_HYPER_CUBE = 7;
  public static final int GUI_ID_POWER_MONITOR = 8;

  public static final int GUI_ID_EXTERNAL_CONNECTION_BASE = 9;
  public static final int GUI_ID_EXTERNAL_CONNECTION_UP = 9 + ForgeDirection.UP.ordinal();
  public static final int GUI_ID_EXTERNAL_CONNECTION_DOWN = 9 + ForgeDirection.DOWN.ordinal();
  public static final int GUI_ID_EXTERNAL_CONNECTION_NORTH = 9 + ForgeDirection.NORTH.ordinal();
  public static final int GUI_ID_EXTERNAL_CONNECTION_SOUTH = 9 + ForgeDirection.SOUTH.ordinal();
  public static final int GUI_ID_EXTERNAL_CONNECTION_EAST = 9 + ForgeDirection.EAST.ordinal();
  public static final int GUI_ID_EXTERNAL_CONNECTION_WEST = 9 + ForgeDirection.WEST.ordinal();
  public static final int GUI_ID_EXTERNAL_CONNECTION_SELECTOR = 16;

  public static final int GUI_ID_ME_ACCESS_TERMINAL = 17;

  public static final int GUI_ID_TRAVEL_ACCESSABLE = 18;
  public static final int GUI_ID_TRAVEL_AUTH = 19;

  protected final Map<Integer, IGuiHandler> guiHandlers = new HashMap<Integer, IGuiHandler>();

  public void registerGuiHandler(int id, IGuiHandler handler) {
    guiHandlers.put(id, handler);
  }

  @Override
  public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    IGuiHandler handler = guiHandlers.get(id);
    if(handler != null) {
      return handler.getServerGuiElement(id, player, world, x, y, z);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    IGuiHandler handler = guiHandlers.get(id);
    if(handler != null) {
      return handler.getClientGuiElement(id, player, world, x, y, z);
    }
    return null;
  }

}
