package crazypants.enderio.teleport.telepad.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.teleport.telepad.TileTelePad;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTeleportTrigger extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketTeleportTrigger, IMessage> {

  public PacketTeleportTrigger() {
    super();
  }

  public PacketTeleportTrigger(TileTelePad te) {
    super(te.getTileEntity());
  }

  @Override
  public IMessage onMessage(PacketTeleportTrigger message, MessageContext ctx) {
    World world = message.getWorld(ctx);
    TileEntity te = message.getTileEntity(world);
    if (te instanceof TileTelePad) {
      ((TileTelePad) te).teleportAll();
    }
    return null;
  }

}
