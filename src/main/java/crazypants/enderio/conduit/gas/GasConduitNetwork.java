package crazypants.enderio.conduit.gas;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.conduit.IConduit;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class GasConduitNetwork extends AbstractGasTankConduitNetwork<GasConduit> {

    private final ConduitGasTank tank = new ConduitGasTank(0);

    private final Set<GasOutput> outputs = new HashSet<GasOutput>();

    private Iterator<GasOutput> outputIterator;

    private int ticksActiveUnsynced;

    private boolean lastSyncedActive = false;

    private int lastSyncedVolume = -1;

    public GasConduitNetwork() {
        super(GasConduit.class);
    }

    @Override
    public void addConduit(GasConduit con) {
        tank.setCapacity(tank.getMaxGas() + GasConduit.CONDUIT_VOLUME);
        if (con.getTank().containsValidGas()) {
            tank.addAmount(con.getTank().getStored());
        }
        for (ForgeDirection dir : con.getExternalConnections()) {
            if (con.getConnectionMode(dir).acceptsOutput()) {
                outputs.add(new GasOutput(con.getLocation().getLocation(dir), dir.getOpposite()));
            }
        }
        outputIterator = null;
        super.addConduit(con);
    }

    @Override
    public boolean setGasType(GasStack newType) {
        if (super.setGasType(newType)) {

            GasStack ft = getGasType();
            tank.setGas(ft == null ? null : ft.copy());
            return true;
        }
        return false;
    }

    @Override
    public void destroyNetwork() {
        setConduitVolumes();
        outputs.clear();
        super.destroyNetwork();
    }

    private void setConduitVolumes() {
        if (tank.containsValidGas() && !conduits.isEmpty()) {
            GasStack gasPerConduit = tank.getGas().copy();
            int numCons = conduits.size();
            int leftOvers = gasPerConduit.amount % numCons;
            gasPerConduit.amount = gasPerConduit.amount / numCons;

            for (GasConduit con : conduits) {
                GasStack f = gasPerConduit.copy();
                if (leftOvers > 0) {
                    f.amount += 1;
                    leftOvers--;
                }
                con.getTank().setGas(f);
                BlockCoord bc = con.getLocation();
                con.getBundle()
                        .getEntity()
                        .getWorldObj()
                        .markTileEntityChunkModified(
                                bc.x, bc.y, bc.z, con.getBundle().getEntity());
            }
        }
    }

    @Override
    public void doNetworkTick() {
        if (gasType == null || outputs.isEmpty() || !tank.containsValidGas() || tank.isEmpty()) {
            updateActiveState();
            return;
        }

        if (outputIterator == null || !outputIterator.hasNext()) {
            outputIterator = outputs.iterator();
        }

        updateActiveState();

        int numVisited = 0;
        while (!tank.isEmpty() && numVisited < outputs.size()) {
            if (!outputIterator.hasNext()) {
                outputIterator = outputs.iterator();
            }
            GasOutput output = outputIterator.next();
            if (output != null) {
                IGasHandler cont = getTankContainer(output.location);
                if (cont != null) {
                    GasStack offer = tank.getGas().copy();
                    int filled = cont.receiveGas(output.dir, offer);
                    if (filled > 0) {
                        tank.addAmount(-filled);
                    }
                }
            }
            numVisited++;
        }
    }

    private void updateActiveState() {
        boolean isActive = tank.containsValidGas() && !tank.isEmpty();
        if (lastSyncedActive != isActive) {
            ticksActiveUnsynced++;
        } else {
            ticksActiveUnsynced = 0;
        }
        if (ticksActiveUnsynced >= 10 || ticksActiveUnsynced > 0 && isActive) {
            if (!isActive) {
                setGasType(null);
            }
            for (IConduit con : conduits) {
                con.setActive(isActive);
            }
            lastSyncedActive = isActive;
            ticksActiveUnsynced = 0;
        }
    }

    public int fill(ForgeDirection from, GasStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }
        resource.amount = Math.min(resource.amount, GasConduit.MAX_IO_PER_TICK);
        boolean gasWasValid = tank.containsValidGas();
        int res = tank.receive(resource, doFill);
        if (doFill && res > 0 && gasWasValid) {
            int vol = tank.getStored();
            setGasType(resource);
            tank.setAmount(vol);
        }
        return res;
    }

    public GasStack drain(ForgeDirection from, GasStack resource, boolean doDrain) {
        if (resource == null
                || tank.isEmpty()
                || !tank.containsValidGas()
                || !GasConduitNetwork.areGassCompatable(getGasType(), resource)) {
            return null;
        }
        int amount = Math.min(resource.amount, tank.getStored());
        amount = Math.min(amount, GasConduit.MAX_IO_PER_TICK);
        GasStack result = resource.copy();
        result.amount = amount;
        if (doDrain) {
            tank.addAmount(-amount);
        }
        return result;
    }

    public GasStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (tank.isEmpty() || !tank.containsValidGas()) {
            return null;
        }
        int amount = Math.min(maxDrain, tank.getStored());
        GasStack result = tank.getGas().copy();
        result.amount = amount;
        if (doDrain) {
            tank.addAmount(-amount);
        }
        return result;
    }

    public boolean extractFrom(GasConduit advancedGasConduit, ForgeDirection dir, int maxExtractPerTick) {

        if (tank.isFull()) {
            return false;
        }

        IGasHandler extTank = getTankContainer(advancedGasConduit, dir);
        if (extTank != null) {
            int maxExtract = Math.min(maxExtractPerTick, tank.getAvailableSpace());

            if (gasType == null || !tank.containsValidGas()) {
                GasStack drained = extTank.drawGas(dir.getOpposite(), maxExtract);
                if (drained == null || drained.amount <= 0) {
                    return false;
                }
                setGasType(drained);
                tank.setGas(drained.copy());
                return true;
            }

            GasStack couldDrain = gasType.copy();
            couldDrain.amount = maxExtract;

            //      GasStack drained = extTank.drain(dir.getOpposite(), couldDrain, true);
            //      if(drained == null || drained.amount <= 0) {
            //        return false;
            //      }
            //      tank.addAmount(drained.amount);

            // Have to use this 'double handle' approach to work around an issue with TiC
            GasStack drained = extTank.drawGas(dir.getOpposite(), maxExtract);
            if (drained == null || drained.amount == 0) {
                return false;
            } else {
                if (drained.isGasEqual(getGasType())) {
                    tank.addAmount(drained.amount);
                }
            }
            return true;
        }
        return false;
    }

    public IGasHandler getTankContainer(BlockCoord bc) {
        World w = getWorld();
        if (w == null) {
            return null;
        }
        TileEntity te = w.getTileEntity(bc.x, bc.y, bc.z);
        if (te instanceof IGasHandler) {
            return (IGasHandler) te;
        }
        return null;
    }

    public IGasHandler getTankContainer(GasConduit con, ForgeDirection dir) {
        BlockCoord bc = con.getLocation().getLocation(dir);
        return getTankContainer(bc);
    }

    World getWorld() {
        if (conduits.isEmpty()) {
            return null;
        }
        return conduits.get(0).getBundle().getWorld();
    }

    public void removeInput(GasOutput lo) {
        outputs.remove(lo);
        outputIterator = null;
    }

    public void addInput(GasOutput lo) {
        outputs.add(lo);
        outputIterator = null;
    }

    public void updateConduitVolumes() {
        if (tank.getStored() == lastSyncedVolume) {
            return;
        }
        setConduitVolumes();
        lastSyncedVolume = tank.getStored();
    }
}
