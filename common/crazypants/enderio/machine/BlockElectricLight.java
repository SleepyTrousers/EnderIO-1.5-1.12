package crazypants.enderio.machine;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.*;
import crazypants.enderio.ModObject;

public class BlockElectricLight extends Block {

  
  public static BlockElectricLight create() {
    BlockElectricLight result = new BlockElectricLight();
    result.init();
    return result;
  }
  
  public BlockElectricLight() {
    super(ModObject.blockElectricLight.id, Material.rock);
    setHardness(2.0F);
    setStepSound(soundGlassFootstep);
    setUnlocalizedName(ModObject.blockElectricLight.unlocalisedName);
    //setCreativeTab(EnderIOTab.tabEnderIO);
    setLightOpacity(0);
    setLightValue(1);    
  }
  

  private void init() {
    LanguageRegistry.addName(this, ModObject.blockElectricLight.name);
    GameRegistry.registerBlock(this, ModObject.blockElectricLight.unlocalisedName);
    //GameRegistry.addRecipe(new ItemStack(this), "zxz", "xyx", "zxz", 'x', new ItemStack(Item.eyeOfEnder), 'y', new ItemStack(Block.enderChest),'z', new ItemStack(Item.diamond));
  }  
  
  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:blockElectricLightFace");
  }

  @Override
  public boolean renderAsNormalBlock() {    
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
    // TODO Auto-generated method stub
    return super.getBlockTexture(par1iBlockAccess, par2, par3, par4, par5);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Icon getIcon(int par1, int par2) {
    // TODO Auto-generated method stub
    return super.getIcon(par1, par2);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
    super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
  }

  @Override
  public boolean canPlaceBlockOnSide(World par1World, int x, int y, int z, int side) {
    ForgeDirection dir = ForgeDirection.getOrientation(side);
    ForgeDirection op = dir.getOpposite();
    return par1World.isBlockSolidOnSide(x + op.offsetX, y + op.offsetY, z + op.offsetZ, dir);
//    ForgeDirection dir = ForgeDirection.getOrientation(side);
//    return (dir == DOWN  && par1World.isBlockSolidOnSide(x, y + 1, z, DOWN )) ||
//           (dir == UP    && par1World.isBlockSolidOnSide(x, y - 1, z, UP   )) ||
//           (dir == NORTH && par1World.isBlockSolidOnSide(x, y, z + 1, NORTH)) ||
//           (dir == SOUTH && par1World.isBlockSolidOnSide(x, y, z - 1, SOUTH)) ||
//           (dir == WEST  && par1World.isBlockSolidOnSide(x + 1, y, z, WEST )) ||
//           (dir == EAST  && par1World.isBlockSolidOnSide(x - 1, y, z, EAST ));
  }
 

  @Override
  public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
    return false;
  }
  
  

}
