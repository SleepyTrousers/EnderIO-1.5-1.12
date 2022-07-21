package crazypants.enderio.machine.capbank.network;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.machine.capbank.TileCapBank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class NetworkUtil {

    private static AtomicInteger nextID = new AtomicInteger(0);

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void ensureValidNetwork(TileCapBank cap) {
        World world = cap.getWorldObj();
        Collection<TileCapBank> neighbours = getNeigbours(cap);
        if (reuseNetwork(cap, neighbours, world)) {
            return;
        }
        CapBankNetwork network = new CapBankNetwork(nextID.getAndIncrement());
        network.init(cap, neighbours, world);
        return;
    }

    public static Collection<TileCapBank> getNeigbours(TileCapBank cap) {
        if (!cap.getType().isMultiblock()) {
            return Collections.emptyList();
        }
        Collection<TileCapBank> res = new ArrayList<TileCapBank>();
        getNeigbours(cap, res);
        return res;
    }

    public static void getNeigbours(TileCapBank cap, Collection<TileCapBank> res) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            BlockCoord bc = cap.getLocation().getLocation(dir);
            TileEntity te = cap.getWorldObj().getTileEntity(bc.x, bc.y, bc.z);
            if (te instanceof TileCapBank) {
                TileCapBank neighbour = (TileCapBank) te;
                if (neighbour.canConnectTo(cap)) {
                    res.add(neighbour);
                }
            }
        }
    }

    private static boolean reuseNetwork(TileCapBank cap, Collection<TileCapBank> neighbours, World world) {
        ICapBankNetwork network = null;
        for (TileCapBank conduit : neighbours) {
            if (network == null) {
                network = conduit.getNetwork();
            } else if (network != conduit.getNetwork()) {
                return false;
            }
        }
        if (network == null) {
            return false;
        }
        if (cap.setNetwork(network)) {
            network.addMember(cap);
            // network.notifyNetworkOfUpdate();
            return true;
        }
        return false;
    }
}
