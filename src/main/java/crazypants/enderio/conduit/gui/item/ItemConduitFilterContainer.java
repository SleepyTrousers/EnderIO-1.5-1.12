package crazypants.enderio.conduit.gui.item;

import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.util.EnumFacing;

public class ItemConduitFilterContainer implements IItemFilterContainer {

  private IItemConduit itemConduit;
  private EnumFacing dir;
  private boolean isInput;

  public ItemConduitFilterContainer(IItemConduit itemConduit, EnumFacing dir, boolean isInput) {
    this.itemConduit = itemConduit;
    this.dir = dir;
    this.isInput = isInput;
  }

  @Override
  public IItemFilter getItemFilter() {
    if(isInput) {
      return itemConduit.getInputFilter(dir);
    } else {
      return itemConduit.getOutputFilter(dir);
    }
  }

  @Override
  public void onFilterChanged() {
    PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, dir));
  }

}
