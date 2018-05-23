package crazypants.enderio.invpanel.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateExtractionDisabled extends MessageTileEntity<TileInventoryPanel> {

  private boolean extractionDisabled;

  public PacketUpdateExtractionDisabled() {
  }

  public PacketUpdateExtractionDisabled(@Nonnull TileInventoryPanel tile, boolean extractionDisabled) {
    super(tile);
    this.extractionDisabled = extractionDisabled;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    extractionDisabled = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(extractionDisabled);
  }

  public static class Handler implements IMessageHandler<PacketUpdateExtractionDisabled, IMessage> {

    @Override
    public IMessage onMessage(PacketUpdateExtractionDisabled message, MessageContext ctx) {
      EntityPlayer player = EnderIO.proxy.getClientPlayer();
      TileEntity te = player.world.getTileEntity(message.getPos());
      if (te instanceof TileInventoryPanel) {
        TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
        teInvPanel.updateExtractionDisabled(message.extractionDisabled);
      }
      return null;
    }
  }
}
