package gg.galaxygaming.gasconduits.common.conduit.advanced;

import crazypants.enderio.base.diagnostics.Prof;
import gg.galaxygaming.gasconduits.common.conduit.AbstractGasTankConduitNetwork;
import gg.galaxygaming.gasconduits.common.conduit.ConduitGasTank;
import gg.galaxygaming.gasconduits.common.conduit.GasOutput;
import gg.galaxygaming.gasconduits.common.config.GasConduitConfig;
import gg.galaxygaming.gasconduits.common.utils.GasUtil;
import gg.galaxygaming.gasconduits.common.utils.GasWrapper;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class AdvancedGasConduitNetwork extends AbstractGasTankConduitNetwork<AdvancedGasConduit> {

    private final ConduitGasTank tank = new ConduitGasTank(0);
    private final Set<GasOutput> outputs = new HashSet<>();

    private Iterator<GasOutput> outputIterator;
    private boolean lastSyncedActive = false;
    private int lastSyncedVolume = -1;
    private int ticksEmpty;

    public AdvancedGasConduitNetwork() {
        super(AdvancedGasConduit.class);
    }

    @Override
    public void addConduit(@Nonnull AdvancedGasConduit con) {
        tank.setMaxGas(tank.getMaxGas() + AdvancedGasConduit.CONDUIT_VOLUME);
        if (con.getTank().containsValidGas()) {
            tank.addAmount(con.getTank().getStored());
        }
        for (EnumFacing dir : con.getExternalConnections()) {
            if (con.getConnectionMode(dir).acceptsOutput()) {
                outputs.add(new GasOutput(con.getBundle().getLocation().offset(dir), dir.getOpposite()));
            }
        }
        outputIterator = null;
        super.addConduit(con);
    }

    @Override
    public boolean setGasType(GasStack newType) {
        if (super.setGasType(newType)) {
            GasStack gt = getGasType();
            tank.setGas(gt == null ? null : gt.copy());
            return true;
        }
        return false;
    }

    @Override
    public void setGasTypeLocked(boolean gasTypeLocked) {
        super.setGasTypeLocked(gasTypeLocked);
        if (!gasTypeLocked && tank.isEmpty()) {
            setGasType(null);
        }
    }

    @Override
    public void destroyNetwork() {
        setConduitVolumes();
        outputs.clear();
        super.destroyNetwork();
    }

    private void setConduitVolumes() {
        if (tank.getGas() != null && !getConduits().isEmpty()) {
            GasStack gasPerConduit = tank.getGas().copy();
            int numCons = getConduits().size();
            int leftOvers = gasPerConduit.amount % numCons;
            gasPerConduit.amount = gasPerConduit.amount / numCons;

            for (AdvancedGasConduit con : getConduits()) {
                GasStack g = gasPerConduit.copy();
                if (leftOvers > 0) {
                    g.amount += 1;
                    leftOvers--;
                }
                con.getTank().setGas(g);
                BlockPos pos = con.getBundle().getLocation();
                con.getBundle().getEntity().getWorld().markChunkDirty(pos, con.getBundle().getEntity());
            }
        }
    }

    @Override
    public void tickEnd(ServerTickEvent event, @Nullable Profiler profiler) {
        if (gasType == null || outputs.isEmpty() || !tank.containsValidGas() || tank.isEmpty()) {
            Prof.start(profiler, "updateActiveState");
            updateActiveState();
            Prof.stop(profiler);
            return;
        }

        if (outputIterator == null || !outputIterator.hasNext()) {
            outputIterator = outputs.iterator();
        }

        Prof.start(profiler, "updateActiveState");
        updateActiveState();

        Prof.next(profiler, "pushGas");
        int numVisited = 0;
        while (!tank.isEmpty() && numVisited < outputs.size()) {
            if (!outputIterator.hasNext()) {
                outputIterator = outputs.iterator();
            }
            GasOutput output = outputIterator.next();
            if (output != null) {
                Prof.start(profiler, "otherMod_getTankContainer");
                IGasHandler cont = getTankContainer(output);
                Prof.stop(profiler);
                if (cont != null && tank.getGas() != null) {
                    GasStack offer = tank.getGas().copy();
                    Prof.start(profiler, "otherMod_fill");
                    int filled = cont.receiveGas(output.getDir(), offer, true);
                    Prof.stop(profiler);
                    if (filled > 0) {
                        tank.addAmount(-filled);
                    }
                }
            }
            numVisited++;
        }
        Prof.stop(profiler);
    }

    private void updateActiveState() {
        boolean isActive = tank.containsValidGas() && !tank.isEmpty();
        if (!isActive) {
            if (!gasTypeLocked && gasType != null) {
                ticksEmpty++;
                if (ticksEmpty > 40) {
                    setGasType(null);
                    ticksEmpty = 0;
                    getConduits().forEach(con -> con.setActive(false));
                    lastSyncedActive = false;
                }
            }
            return;
        }

        ticksEmpty = 0;
        if (!lastSyncedActive) {
            getConduits().forEach(con -> con.setActive(true));
            lastSyncedActive = true;
        }
    }

    public int receiveGas(GasStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }
        resource.amount = Math.min(resource.amount, GasConduitConfig.tier2_maxIO.get());
        boolean gasWasValid = !tank.containsValidGas();
        int res = tank.receive(resource, doFill);
        if (doFill && res > 0 && !gasWasValid) {
            int vol = tank.getStored();
            setGasType(resource);
            tank.setAmount(vol);
        }
        return res;
    }

    public GasStack drawGas(int maxDrain, boolean doDrain) {
        if (tank.isEmpty() || tank.getGas() == null) {
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

    public boolean extractFrom(@Nonnull AdvancedGasConduit advancedGasConduit, @Nonnull EnumFacing dir,
          int maxExtractPerTick) {
        if (tank.isFull()) {
            return false;
        }

        IGasHandler extTank = getTankContainer(advancedGasConduit, dir);
        if (extTank != null) {
            int maxExtract = Math.min(maxExtractPerTick, tank.getNeeded());

            if (gasType == null || !tank.containsValidGas()) {
                GasStack newGas = GasUtil.getGasStack(extTank, dir.getOpposite());
                if (newGas == null) {
                    return false;
                }
                setGasType(newGas);
            }

            GasStack couldDrain = gasType.copy();
            couldDrain.amount = maxExtract;

            GasStack drained = extTank.drawGas(dir, couldDrain.amount, true);
            if (drained == null || drained.amount == 0) {
                return false;
            } else if (drained.isGasEqual(getGasType())) {
                tank.addAmount(drained.amount);
            } else {
                extTank.receiveGas(dir, drained, true);
            }
            return true;
        }
        return false;
    }

    public IGasHandler getTankContainer(GasOutput output) {
        return GasWrapper.getGasHandler(getWorld(), output.getLocation(), output.getDir());
    }

    public IGasHandler getTankContainer(@Nonnull AdvancedGasConduit con, @Nonnull EnumFacing dir) {
        return GasWrapper.getGasHandler(getWorld(), con.getBundle().getLocation().offset(dir), dir.getOpposite());
    }

    World getWorld() {
        if (getConduits().isEmpty()) {
            return null;
        }
        return getConduits().get(0).getBundle().getBundleworld();
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