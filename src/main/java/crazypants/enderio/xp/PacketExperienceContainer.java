package crazypants.enderio.xp;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExperienceContainer extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketExperienceContainer, IMessage> {
  private ExperienceContainer xpCon;

  public PacketExperienceContainer() {
    xpCon = new ExperienceContainer();
  }

  public PacketExperienceContainer(@Nonnull TileEntity tile) {
    super(tile);
    IHaveExperience xpTile = (IHaveExperience) tile;
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
  public IMessage onMessage(PacketExperienceContainer message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity tile = message.getTileEntity(player.world);
    if (tile instanceof IHaveExperience) {
      IHaveExperience xpTile = (IHaveExperience) tile;
      xpTile.getContainer().set(message.xpCon);
    }
    return null;
  }

}
