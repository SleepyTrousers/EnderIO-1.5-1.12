package crazypants.enderio.invpanel.network;

import javax.annotation.Nullable;

import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.invpanel.invpanel.InventoryPanelContainer;
import crazypants.enderio.invpanel.util.StoredCraftingRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketStoredCraftingRecipe implements IMessage {

  public static final int ACTION_ADD = 0;
  public static final int ACTION_DELETE = 1;

  private int action;
  private int index;
  private @Nullable StoredCraftingRecipe recipe;

  public PacketStoredCraftingRecipe() {
  }

  public PacketStoredCraftingRecipe(int action, int index, @Nullable StoredCraftingRecipe recipe) {
    this.action = action;
    this.index = index;
    this.recipe = recipe;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    action = buf.readUnsignedByte();
    index = buf.readUnsignedByte();
    NBTTagCompound nbt = NetworkUtil.readNBTTagCompound(buf);
    recipe = new StoredCraftingRecipe();
    if (!recipe.readFromNBT(nbt)) {
      recipe = null;
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeByte(action);
    buf.writeByte(index);
    NBTTagCompound nbt = null;
    if (recipe != null) {
      nbt = new NBTTagCompound();
      recipe.writeToNBT(nbt);
    }
    NetworkUtil.writeNBTTagCompound(nbt, buf);
  }

  public static class Handler implements IMessageHandler<PacketStoredCraftingRecipe, IMessage> {

    @Override
    public IMessage onMessage(PacketStoredCraftingRecipe message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      if (player.openContainer instanceof InventoryPanelContainer) {
        InventoryPanelContainer ipc = (InventoryPanelContainer) player.openContainer;
        switch (message.action) {
        case ACTION_ADD:
          if (message.recipe != null) {
            ipc.getTe().addStoredCraftingRecipe(message.recipe);
          }
          break;
        case ACTION_DELETE:
          ipc.getTe().removeStoredCraftingRecipe(message.index);
          break;
        }
      }
      return null;
    }
  }
}
