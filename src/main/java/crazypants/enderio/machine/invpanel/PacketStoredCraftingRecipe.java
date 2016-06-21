package crazypants.enderio.machine.invpanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketStoredCraftingRecipe implements IMessage, IMessageHandler<PacketStoredCraftingRecipe, IMessage> {

  public static final int ACTION_ADD = 0;
  public static final int ACTION_DELETE = 1;

  private int action;
  private int index;
  private StoredCraftingRecipe recipe;

  public PacketStoredCraftingRecipe() {
  }

  public PacketStoredCraftingRecipe(int action, int index, StoredCraftingRecipe recipe) {
    this.action = action;
    this.index = index;
    this.recipe = recipe;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    action = buf.readUnsignedByte();
    index = buf.readUnsignedByte();
    NBTTagCompound nbt = NetworkUtil.readNBTTagCompound(buf);
    if(nbt != null) {
      recipe = new StoredCraftingRecipe();
      if(!recipe.readFromNBT(nbt)) {
        recipe = null;
      }
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeByte(action);
    buf.writeByte(index);
    NBTTagCompound nbt = null;
    if(recipe != null) {
      nbt = new NBTTagCompound();
      recipe.writeToNBT(nbt);
    }
    NetworkUtil.writeNBTTagCompound(nbt, buf);
  }

  @Override
  public IMessage onMessage(PacketStoredCraftingRecipe message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    if(player.openContainer instanceof InventoryPanelContainer) {
      InventoryPanelContainer ipc = (InventoryPanelContainer) player.openContainer;
      switch (message.action) {
        case ACTION_ADD:
          if(message.recipe != null) {
            ipc.getInventoryPanel().addStoredCraftingRecipe(message.recipe);
          }
          break;
        case ACTION_DELETE:
          ipc.getInventoryPanel().removeStoredCraftingRecipe(message.index);
          break;
      }
    }
    return null;
  }
}
