package gg.galaxygaming.gasconduits.common.conduit;

import com.enderio.core.common.util.DyeColor;
import crazypants.enderio.base.conduit.ConnectionMode;
import gg.galaxygaming.gasconduits.common.conduit.ender.EnderGasConduit;
import javax.annotation.Nonnull;
import mekanism.api.gas.IGasHandler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class NetworkGasTank {

    @Nonnull
    public final EnderGasConduit con;
    @Nonnull
    public final EnumFacing conDir;
    private final IGasHandler externalTank;
    @Nonnull
    private final EnumFacing tankDir;
    @Nonnull
    private final BlockPos conduitLoc;
    private final boolean acceptsOutput;
    private final DyeColor inputColor;
    private final DyeColor outputColor;
    private final int priority;
    private final boolean roundRobin;
    private final boolean selfFeed;

    public NetworkGasTank(@Nonnull EnderGasConduit con, @Nonnull EnumFacing conDir) {
        this.con = con;
        this.conDir = conDir;
        conduitLoc = con.getBundle().getLocation();
        tankDir = conDir.getOpposite();
        externalTank = AbstractGasConduit.getExternalGasHandler(con.getBundle().getBundleworld(), conduitLoc.offset(conDir), tankDir);
        acceptsOutput = con.getConnectionMode(conDir).acceptsOutput();
        inputColor = con.getOutputColor(conDir);
        outputColor = con.getInputColor(conDir);
        priority = con.getOutputPriority(conDir);
        roundRobin = con.isRoundRobinEnabled(conDir);
        selfFeed = con.isSelfFeedEnabled(conDir);
    }

    public boolean isValid() {
        return externalTank != null && con.getConnectionMode(conDir) != ConnectionMode.DISABLED;
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + conDir.hashCode();
        code = 31 * code + conduitLoc.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NetworkGasTank other = (NetworkGasTank) obj;
        if (conDir != other.conDir) {
            return false;
        }
        return conduitLoc.equals(other.conduitLoc);
    }

    public boolean acceptsOutput() {
        return acceptsOutput;
    }

    public DyeColor getInputColor() {
        return inputColor;
    }

    public DyeColor getOutputColor() {
        return outputColor;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isRoundRobin() {
        return roundRobin;
    }

    public boolean isSelfFeed() {
        return selfFeed;
    }

    public IGasHandler getExternalTank() {
        return externalTank;
    }

    public EnumFacing getConduitDir() {
        return conDir;
    }

    @Nonnull
    public EnumFacing getTankDir() {
        return tankDir;
    }

    @Nonnull
    public BlockPos getConduitLocation() {
        return conduitLoc;
    }

    public EnderGasConduit getConduit() {
        return con;
    }
}