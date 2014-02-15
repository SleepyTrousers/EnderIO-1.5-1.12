package crazypants.enderio.enderface;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class BlockEnderIO extends Block implements ITileEntityProvider {

  public static BlockEnderIO create() {
    BlockEnderIO result = new BlockEnderIO();
    result.init();
    return result;
  }

  Icon frameIcon;
  Icon selectedOverlayIcon;
  Icon highlightOverlayIcon;

  static int pass;

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
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileEnderIO) {
      TileEnderIO eio = (TileEnderIO) te;
      eio.initUiPitch = -player.rotationPitch;
      eio.initUiYaw = -player.rotationYaw + 180;
      eio.lastUiPitch = eio.initUiPitch;
      eio.lastUiYaw = eio.initUiYaw;

      world.markBlockForUpdate(x, y, z);
    }

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
    highlightOverlayIcon = iconRegister.registerIcon("enderio:enderIOHighlight");
    selectedOverlayIcon = iconRegister.registerIcon("enderio:enderIOSelected");
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return new TileEnderIO();
  }

}
