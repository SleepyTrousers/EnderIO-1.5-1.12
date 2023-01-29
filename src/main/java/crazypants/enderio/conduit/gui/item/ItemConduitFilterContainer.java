package crazypants.enderio.conduit.gui.item;

import net.minecraftforge.common.util.ForgeDirection;

import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.network.PacketHandler;

public class ItemConduitFilterContainer implements IItemFilterContainer {

    private IItemConduit itemConduit;
    private ForgeDirection dir;
    private boolean isInput;

    public ItemConduitFilterContainer(IItemConduit itemConduit, ForgeDirection dir, boolean isInput) {
        this.itemConduit = itemConduit;
        this.dir = dir;
        this.isInput = isInput;
    }

    @Override
    public ItemFilter getItemFilter() {
        if (isInput) {
            return (ItemFilter) itemConduit.getInputFilter(dir);
        } else {
            return (ItemFilter) itemConduit.getOutputFilter(dir);
        }
    }

    @Override
    public void onFilterChanged() {
        PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, dir));
    }
}
