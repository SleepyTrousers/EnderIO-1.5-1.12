package crazypants.enderio.enderface;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class BlockEnderIO extends Block implements ITileEntityProvider {

  public static BlockEnderIO create() {
    BlockEnderIO result = new BlockEnderIO();
    result.init();
    return result;
  }

  Icon frameIcon;

  private BlockEnderIO() {
    super(ModObject.blockEnderIo.id, Material.rock);
    setHardness(0.5F);
    setStepSound(Block.soundStoneFootstep);
    setUnlocalizedName(ModObject.blockEnderIo.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  private void init() {
    LanguageRegistry.addName(this, ModObject.blockEnderIo.name);
    GameRegistry.registerBlock(this, ModObject.blockEnderIo.unlocalisedName);
    GameRegistry.registerTileEntity(TileEnderIO.class, ModObject.blockEnderIo.unlocalisedName + "TileEntity");
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
    if(entityPlayer.isSneaking()) {
      return false;
    }
    if(entityPlayer.getCurrentEquippedItem() != null && entityPlayer.getCurrentEquippedItem().itemID == EnderIO.itemEnderface.itemID) {
      ItemStack enderFaceStack = entityPlayer.getCurrentEquippedItem();
      NBTTagCompound nbttagcompound = enderFaceStack.getTagCompound();
      if(nbttagcompound == null) {
        nbttagcompound = new NBTTagCompound();
      }
      nbttagcompound.setBoolean(ItemEnderface.KEY_IO_SET, true);
      nbttagcompound.setInteger(ItemEnderface.KEY_IO_X, x);
      nbttagcompound.setInteger(ItemEnderface.KEY_IO_Y, y);
      nbttagcompound.setInteger(ItemEnderface.KEY_IO_Z, z);
      nbttagcompound.setInteger(ItemEnderface.KEY_DIMENSION, world.provider.dimensionId);
      enderFaceStack.setTagCompound(nbttagcompound);

      entityPlayer.setCurrentItemOrArmor(0, enderFaceStack);

      if (world.isRemote) {
        ChatMessageComponent c = ChatMessageComponent.createFromText("EnderIO Interface Selected");
        entityPlayer.sendChatToPlayer(c);
      }

    }
    return true;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getRenderType() {
    return -1;
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    return 13;
  }

  @Override
  public int getLightOpacity(World world, int x, int y, int z) {
    return 100;
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:enderIO");
    frameIcon = iconRegister.registerIcon("enderio:enderIOFrame");
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return new TileEnderIO();
  }

}
