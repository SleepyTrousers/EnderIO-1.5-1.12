package crazypants.enderio.machine.invpanel.server;

import java.util.ArrayList;

import net.minecraft.inventory.ISidedInventory;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.conduit.item.NetworkedInventory;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

public abstract class InventoryFactory {

    private static final ArrayList<InventoryFactory> factories;

    static {
        factories = new ArrayList<InventoryFactory>();
        factories.add(new DSUFactory());
        if (Loader.isModLoaded("StorageDrawers")) {
            factories.add(new DrawerFactory());
        }
    }

    static AbstractInventory createInventory(NetworkedInventory ni) {
        for (InventoryFactory f : factories) {
            AbstractInventory ai = f.create(ni);
            if (ai != null) {
                return ai;
            }
        }
        return new NormalInventory(ni);
    }

    abstract AbstractInventory create(NetworkedInventory ni);

    static class DSUFactory extends InventoryFactory {

        @Override
        AbstractInventory create(NetworkedInventory ni) {
            ISidedInventory inv = ni.getInventory();
            if (inv instanceof IDeepStorageUnit) {
                return new DSUInventory((IDeepStorageUnit) inv);
            }
            return null;
        }
    }

    static class DrawerFactory extends InventoryFactory {

        @Override
        AbstractInventory create(NetworkedInventory ni) {
            ISidedInventory inv = ni.getInventory();
            if (inv instanceof IDrawerGroup) {
                return new DrawerGroupInventory((IDrawerGroup) inv);
            }
            return null;
        }
    }
}
