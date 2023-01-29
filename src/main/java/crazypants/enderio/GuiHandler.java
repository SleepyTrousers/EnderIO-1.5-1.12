package crazypants.enderio;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
    public static final int GUI_ID_STILL = 20;
    public static final int GUI_ID_COMBUSTION_GEN = 21;
    public static final int GUI_ID_FARM_STATATION = 22;
    public static final int GUI_ID_TANK = 86;
    public static final int GUI_ID_CRAFTER = 87;
    public static final int GUI_ID_ZOMBIE_GEN = 71;
    public static final int GUI_ID_POWERED_SPAWNER = 88;
    public static final int GUI_ID_VACUUM_CHEST = 89;
    public static final int GUI_ID_WIRELESS_CHARGER = 90;
    public static final int GUI_ID_ENCHANTER = 91;
    public static final int GUI_ID_KILLER_JOE = 92;
    public static final int GUI_ID_SOUL_BINDER = 93;
    public static final int GUI_ID_SLICE_N_SPLICE = 94;
    public static final int GUI_ID_ATTRACTOR = 95;
    public static final int GUI_ID_SPAWN_GUARD = 96;
    public static final int GUI_ID_TRANSCEIVER = 97;
    public static final int GUI_ID_XP_OBELISK = 98;
    public static final int GUI_ID_ANVIL = 99;
    public static final int GUI_ID_BUFFER = 100;
    public static final int GUI_ID_WEATHER_OBELISK = 101;
    public static final int GUI_ID_TELEPAD = 102;
    public static final int GUI_ID_TELEPAD_TRAVEL = 103;
    public static final int GUI_ID_INHIBITOR = 104;
    public static final int GUI_ID_INVENTORY_PANEL = 105;

    public static final int GUI_ID_CAP_BANK = 142; // leave room for more machines

    protected final Map<Integer, IGuiHandler> guiHandlers = new HashMap<Integer, IGuiHandler>();

    public void registerGuiHandler(int id, IGuiHandler handler) {
        guiHandlers.put(id, handler);
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        IGuiHandler handler = guiHandlers.get(id);
        if (handler != null) {
            return handler.getServerGuiElement(id, player, world, x, y, z);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        IGuiHandler handler = guiHandlers.get(id);
        if (handler != null) {
            return handler.getClientGuiElement(id, player, world, x, y, z);
        }
        return null;
    }
}
