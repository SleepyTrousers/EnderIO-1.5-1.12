package crazypants.enderio.conduit.packet;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.IConduit;

public class AbstractConduitPacket<T extends IConduit> extends AbstractConduitBundlePacket {

  protected ConTypeEnum conType;
  
  public AbstractConduitPacket() {
  }

  public AbstractConduitPacket(TileEntity tile, ConTypeEnum conType) {
    super(tile);
    this.conType = conType;
  }
  
  protected Class<? extends IConduit> getConType()
  {
      return conType.getBaseType();
  }
  
  @SuppressWarnings("unchecked")
  protected T getTileCasted(MessageContext ctx)
  {
      return (T) getTileEntity(getWorld(ctx));
  }
}
