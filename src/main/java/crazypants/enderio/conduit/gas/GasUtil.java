package crazypants.enderio.conduit.gas;

import com.enderio.core.common.util.BlockCoord;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.Optional.Method;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.config.Config;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public final class GasUtil {

    private static boolean useCheckPerformed = false;
    private static boolean isGasConduitEnabled = false;

    public static final String API_NAME = "MekanismAPI|gas";

    public static boolean isGasConduitEnabled() {
        if (!useCheckPerformed) {
            if (Config.isGasConduitEnabled) {
                isGasConduitEnabled = ModAPIManager.INSTANCE.hasAPI(API_NAME);
            } else {
                isGasConduitEnabled = false;
            }
            useCheckPerformed = true;
        }
        return isGasConduitEnabled;
    }

    @Method(modid = API_NAME)
    public static IGasHandler getExternalGasHandler(IBlockAccess world, BlockCoord bc) {
        IGasHandler con = getGasHandler(world, bc);
        return (con != null && !(con instanceof IConduitBundle)) ? con : null;
    }

    @Method(modid = API_NAME)
    public static IGasHandler getGasHandler(IBlockAccess world, BlockCoord bc) {
        return getGasHandler(world, bc.x, bc.y, bc.z);
    }

    @Method(modid = API_NAME)
    public static IGasHandler getGasHandler(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return getGasHandler(te);
    }

    @Method(modid = API_NAME)
    public static IGasHandler getGasHandler(TileEntity te) {
        if (te instanceof IGasHandler) {
            return (IGasHandler) te;
        }
        return null;
    }

    @Method(modid = API_NAME)
    public static boolean isGasValid(GasStack gas) {
        if (gas != null) {
            String name = gas.getGas().getLocalizedName();
            if (name != null && !name.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private GasUtil() {}
}
