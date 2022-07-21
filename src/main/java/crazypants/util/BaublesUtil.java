package crazypants.util;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.Log;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class BaublesUtil {

    public static enum WhoAmI {
        SPCLIENT,
        MPCLIENT,
        SPSERVER,
        MPSERVER,
        OTHER;

        public static BaublesUtil.WhoAmI whoAmI(World world) {
            Side side = FMLCommonHandler.instance().getSide();
            if (side == Side.CLIENT) {
                if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
                    if (world.isRemote) {
                        return SPCLIENT;
                    } else {
                        return SPSERVER;
                    }
                } else {
                    return MPCLIENT;
                }
            } else if (side == Side.SERVER) {
                if (MinecraftServer.getServer().isDedicatedServer()) {
                    return MPSERVER;
                } else if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
                    return SPSERVER;
                }
            }
            return OTHER;
        }
    }

    private static final BaublesUtil instance = new BaublesUtil();
    private static final boolean baublesLoaded;

    static {
        baublesLoaded = Loader.isModLoaded("Baubles");
    }

    private BaublesUtil() {}

    public static BaublesUtil instance() {
        return instance;
    }

    public boolean hasBaubles() {
        return baublesLoaded;
    }

    /**
     * Do NOT modify this inventory on the client side of a singleplayer game!
     *
     * Wrap it in a ShadowInventory if you need to.
     */
    public IInventory getBaubles(EntityPlayer player) {
        return hasBaubles() ? getBaublesInvUnsafe(player) : null;
    }

    private IInventory getBaublesInvUnsafe(EntityPlayer player) {
        return BaublesApi.getBaubles(player);
    }

    private static boolean failedDirectAccess = false;

    public void disableCallbacks(IInventory baubles, boolean b) {
        if (!failedDirectAccess) {
            try {
                Class<?> inventoryBaubles = Class.forName("baubles.common.container.InventoryBaubles");
                Field blockEvents = inventoryBaubles.getDeclaredField("blockEvents");
                blockEvents.set(baubles, b);
            } catch (Throwable t) {
                Log.info("Failed to access Baubles internals: " + t);
                failedDirectAccess = true;
            }
        }
    }
}
