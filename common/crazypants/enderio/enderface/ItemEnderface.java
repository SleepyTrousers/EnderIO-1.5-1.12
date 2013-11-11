package crazypants.enderio.enderface;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;

public class ItemEnderface extends Item implements IGuiHandler {

  public static final String KEY_IO_SET = "enderFaceIoSet";
  public static final String KEY_IO_X = "enderFaceIoX";
  public static final String KEY_IO_Y = "enderFaceIoY";
  public static final String KEY_IO_Z = "enderFaceIoZ";
  public static final String KEY_DIMENSION = "enderFaceDimension";

  public static ItemEnderface create() {
    ItemEnderface result = new ItemEnderface();
    result.init();
    return result;
  }

  protected ItemEnderface() {
    super(ModObject.itemEnderface.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemEnderface.unlocalisedName);
    setMaxStackSize(1);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.itemEnderface.name);
    GameRegistry.registerItem(this, ModObject.itemEnderface.unlocalisedName);
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_ENDERFACE, this);
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:enderface");
  }

  @Override
  public boolean hasEffect(ItemStack par1ItemStack, int pass) {
    return true;
  }

  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    super.onCreated(itemStack, world, entityPlayer);
    NBTTagCompound nbttagcompound = new NBTTagCompound();
    nbttagcompound.setBoolean(KEY_IO_SET, false);
    nbttagcompound.setInteger(KEY_IO_X, -1);
    nbttagcompound.setInteger(KEY_IO_Y, -1);
    nbttagcompound.setInteger(KEY_IO_Z, -1);
    nbttagcompound.setInteger(KEY_DIMENSION, -1);
    itemStack.setTagCompound(nbttagcompound);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiEnderface(player, world, x, y, z);
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemStack, World world, final EntityPlayer entityPlayer) {

    if(!world.isRemote) {
      return itemStack;
    }

    NBTTagCompound tag = itemStack.getTagCompound();
    boolean tagsSet = tag != null && tag.getBoolean(KEY_IO_SET);

    if(tag != null && tag.getBoolean(KEY_IO_SET)) {

      int x = tag.getInteger(KEY_IO_X);
      int y = tag.getInteger(KEY_IO_Y);
      int z = tag.getInteger(KEY_IO_Z);
      int dimension = tag.getInteger(KEY_DIMENSION);

      if (world.provider.dimensionId != dimension) {
        ChatMessageComponent c = ChatMessageComponent.createFromText("EnderIO block is in a different dimension.");
        entityPlayer.sendChatToPlayer(c);
        return itemStack;
      }

      Chunk c = world.getChunkFromBlockCoords(x, z);

      if (c == null || !c.isChunkLoaded) {
        ChatMessageComponent cm = ChatMessageComponent.createFromText("EnderIO block's chunk is not loaded.");
        entityPlayer.sendChatToPlayer(cm);
        return itemStack;
      }
      int blockId = world.getBlockId(x, y, z);

      if (blockId != EnderIO.blockEnderIo.blockID) {
        ChatMessageComponent cm = ChatMessageComponent.createFromText("EnderIO block has been destroyed.");
        entityPlayer.sendChatToPlayer(cm);
        return itemStack;
      }
      entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_ENDERFACE, world, x, y, z);
      return itemStack;
    }
    ChatMessageComponent cm = ChatMessageComponent.createFromText(" Enderface not synchronized with EnderIO.");
    entityPlayer.sendChatToPlayer(cm);
    return itemStack;
  }

}
