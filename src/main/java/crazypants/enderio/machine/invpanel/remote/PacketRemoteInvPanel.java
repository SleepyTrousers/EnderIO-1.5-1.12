package crazypants.enderio.machine.invpanel.remote;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.invpanel.TileInventoryPanel;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static crazypants.util.NbtValue.*;

public class PacketRemoteInvPanel implements IMessage, IMessageHandler<PacketRemoteInvPanel, IMessage> {

  EnumHand hand;

  public PacketRemoteInvPanel() {
  }

  public PacketRemoteInvPanel(EnumHand hand) {
    this.hand = hand;
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    buffer.writeByte(hand.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    hand = EnumHand.values()[buffer.readByte()];
  }

  @Override
  public IMessage onMessage(PacketRemoteInvPanel message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;

    ItemStack heldItem = player.getHeldItem(message.hand);
    if (heldItem == null || !(heldItem.getItem() instanceof ItemRemoteInvAccess)) {
      return null;
    }

    if (!REMOTE_X.hasTag(heldItem)) {
      return null;
    }

    int x = REMOTE_X.getInt(heldItem);
    int y = REMOTE_Y.getInt(heldItem);
    int z = REMOTE_Z.getInt(heldItem);
    int d = REMOTE_D.getInt(heldItem);

    ItemRemoteInvAccessType type = ItemRemoteInvAccessType.fromStack(heldItem);

    if (!type.inRange(d, x, y, z, player.getEntityWorld().provider.getDimension(), (int) player.posX, (int) player.posY, (int) player.posZ)) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.outofrange")));
      return null;
    }

    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    if (server == null) {
      return null;
    }

    WorldServer world = server.worldServerForDimension(d);
    if (world == null) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.invalidtarget")));
      return null;
    }

    final BlockPos pos = new BlockPos(x, y, z);
    if (!world.isBlockLoaded(pos)) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.notloaded")));
      return null;
    }
    
    TileEntity tileEntity = player.getEntityWorld().getTileEntity(pos);
    if (!(tileEntity instanceof TileInventoryPanel)) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("remoteinv.chat.invalidtarget")));
      return null;
    }

    Container c = player.openContainer;

    player.interactionManager.processRightClickBlock(player, player.world, null, EnumHand.MAIN_HAND, pos, EnumFacing.UP, 0f, 0f, 0f);

    return null;
  }
}
