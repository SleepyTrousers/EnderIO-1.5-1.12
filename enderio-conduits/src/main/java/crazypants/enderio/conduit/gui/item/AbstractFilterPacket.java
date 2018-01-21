package crazypants.enderio.conduit.gui.item;

import crazypants.enderio.base.filter.IItemFilter;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class AbstractFilterPacket<T extends IItemFilter> implements IMessage {

  protected IItemFilterContainer filterContainer;
  protected T filter;

  public AbstractFilterPacket(IItemFilterContainer filterContainer, T filter) {
    this.filterContainer = filterContainer;
    this.filter = filter;
  }

  @Override
  public void fromBytes(ByteBuf buf) {

  }

  @Override
  public void toBytes(ByteBuf buf) {

  }
}
