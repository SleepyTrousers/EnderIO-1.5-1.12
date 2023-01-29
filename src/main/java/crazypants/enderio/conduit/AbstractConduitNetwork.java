package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

// I=base type, I is the base class of the implementations accepted by the network
public abstract class AbstractConduitNetwork<T extends IConduit, I extends T> {

    protected final List<I> conduits = new ArrayList<I>();

    protected final Class<I> implClass;
    protected final Class<T> baseConduitClass;

    protected AbstractConduitNetwork(Class<I> implClass, Class<T> baseConduitClass) {
        this.implClass = implClass;
        this.baseConduitClass = baseConduitClass;
    }

    public void init(IConduitBundle tile, Collection<I> connections, World world) {

        if (world.isRemote) {
            throw new UnsupportedOperationException();
        }

        // Destroy all existing redstone networks around this block
        for (I con : connections) {
            AbstractConduitNetwork<?, ?> network = con.getNetwork();
            if (network != null) {
                network.destroyNetwork();
            }
        }
        setNetwork(world, tile);
        notifyNetworkOfUpdate();
    }

    public final Class<T> getBaseConduitType() {
        return baseConduitClass;
    }

    protected void setNetwork(World world, IConduitBundle tile) {

        T conduit = tile.getConduit(getBaseConduitType());

        if (conduit != null && implClass.isAssignableFrom(conduit.getClass()) && conduit.setNetwork(this)) {
            addConduit(implClass.cast(conduit));
            TileEntity te = tile.getEntity();
            Collection<T> connections = ConduitUtil
                    .getConnectedConduits(world, te.xCoord, te.yCoord, te.zCoord, getBaseConduitType());
            for (T con : connections) {
                if (con.getNetwork() == null) {
                    setNetwork(world, con.getBundle());
                } else if (con.getNetwork() != this) {
                    con.getNetwork().destroyNetwork();
                    setNetwork(world, con.getBundle());
                }
            }
        }
    }

    public void addConduit(I con) {
        if (!conduits.contains(con)) {
            if (conduits.isEmpty()) {
                ConduitNetworkTickHandler.instance.registerNetwork(this);
            }
            conduits.add(con);
        }
    }

    public void destroyNetwork() {
        for (I con : conduits) {
            con.setNetwork(null);
        }
        conduits.clear();
        ConduitNetworkTickHandler.instance.unregisterNetwork(this);
    }

    public List<I> getConduits() {
        return conduits;
    }

    public void notifyNetworkOfUpdate() {
        for (I con : conduits) {
            TileEntity te = con.getBundle().getEntity();
            te.getWorldObj().markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IConduit con : conduits) {
            sb.append(con.getLocation());
            sb.append(", ");
        }
        return "AbstractConduitNetwork [conduits=" + sb.toString() + "]";
    }

    public void doNetworkTick() {}
}
