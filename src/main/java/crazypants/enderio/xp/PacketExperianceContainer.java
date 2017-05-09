package crazypants.enderio.xp;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
import crazypants.enderio.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketExperianceContainer extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketExperianceContainer, IMessage> {

  private static boolean registered = false;
  
  public static void register() {
    if(!registered) {
      PacketHandler.INSTANCE.registerMessage(PacketExperianceContainer.class, PacketExperianceContainer.class, PacketHandler.nextID(), Side.CLIENT);
      registered = true;
    }
  }
  
  private ExperienceContainer xpCon;

  public PacketExperianceContainer() {
    xpCon = new ExperienceContainer();
  }

  public PacketExperianceContainer(TileEntity tile) {
    super(tile);
    IHaveExperience xpTile = (IHaveExperience)tile;
    xpCon = xpTile.getContainer();    
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    xpCon.toBytes(buf);    
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    xpCon.fromBytes(buf);
  }

  @Override
  public IMessage onMessage(PacketExperianceContainer message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity tile = message.getTileEntity(player.world);
    if (tile instanceof IHaveExperience) {      
      IHaveExperience xpTile = (IHaveExperience)tile;
      xpTile.getContainer().set(message.xpCon);
    }
    return null;
  }
}
