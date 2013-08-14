package crazypants.enderio.machine.painter;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.*;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.*;
import crazypants.util.Util;

public class BlockCustomStair extends BlockStairs implements ITileEntityProvider {

  public static BlockCustomStair create() {
    BlockCustomStair result = new BlockCustomStair();
    result.init();
    return result;
  }
  
  protected BlockCustomStair() {
    super(ModObject.blockCustomStair.actualId, Block.brick, 0);
    setCreativeTab(null);
    setUnlocalizedName(ModObject.blockCustomStair.unlocalisedName);
  }

  
  private void init() {
    LanguageRegistry.addName(this, ModObject.blockCustomStair.name);
    GameRegistry.registerBlock(this, BlockItemCustomStair.class, ModObject.blockCustomStair.unlocalisedName);    
    GameRegistry.registerTileEntity(TileEntityCustomBlock.class, ModObject.blockCustomStair.unlocalisedName + "TileEntity");
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
  }
  

  public static ItemStack createItemStackForSourceBlock(int id, int damage) {
    ItemStack result = new ItemStack(ModObject.blockCustomStair.id, 1, damage);
    PainterUtil.setSourceBlock(result, id, damage);
    return result;
  }

  @Override
  public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {    
    int meta = world.getBlockMetadata(x, y, z);
    boolean flipped = ((meta & 4) != 0);
    return ((meta & 3) + side.ordinal() == 5) || (side == ForgeDirection.UP && flipped);
  }

  @Override
  public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int blockSide) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileEntityCustomBlock) {
      TileEntityCustomBlock tef = (TileEntityCustomBlock) te;
      if (tef.getSourceBlockId() > 0 && tef.getSourceBlockId() < Block.blocksList.length) {
        return blocksList[tef.getSourceBlockId()].getIcon(blockSide, tef.getSourceBlockMetadata());
      }
    } 
    return blocksList[Block.anvil.blockID].getBlockTexture(world, x, y, z, blockSide);
  }


  @Override
  public TileEntity createNewTileEntity(World world) {
    return null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileEntityCustomBlock();
  }

  @Override
  public int getLightOpacity(World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileEntityCustomBlock) {
      TileEntityCustomBlock tef = (TileEntityCustomBlock) te;
      if(tef.getSourceBlockId() > 0) {
        return Math.min(super.getLightOpacity(world, x, y, z), Block.lightOpacity[tef.getSourceBlockId()]);
      }
      
    }
    return super.getLightOpacity(world, x, y, z);
  }
 

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
    int id = -1;
    Block b = PainterUtil.getSourceBlock(stack);
    if (b != null) {
      id = b.blockID;
    }

    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileEntityCustomBlock) {
      TileEntityCustomBlock tef = (TileEntityCustomBlock) te;
      tef.setSourceBlockId(id);
      tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));
    }
    world.markBlockForUpdate(x, y, z);
    super.onBlockPlacedBy(world, x, y, z, player, stack);
  }

  /**
   * Remove the tile entity too.
   */
  @Override
  public void breakBlock(World world, int x, int y, int z, int par5, int par6) {

    if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      TileEntity te = world.getBlockTileEntity(x, y, z);

      if (te instanceof TileEntityCustomBlock) {
        TileEntityCustomBlock tef = (TileEntityCustomBlock) te;

        ItemStack itemStack = createItemStackForSourceBlock(tef.getSourceBlockId(), tef.getSourceBlockMetadata());

        float f = 0.7F;
        double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);

      } else {
        System.out.println("dropBlockAsItem_do: No tile entity.");
      }

    }

    world.removeBlockTileEntity(x, y, z);
  }

  @Override
  public int quantityDropped(Random par1Random) {
    return 0; // need to do custom dropping to maintain source metadata
  } 

  public static final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate() {      
    }

    @Override
    public ItemStack[] getCompletedResult(RecipeInput... inputs) {
      ItemStack paintSource = RecipeInput.getInputForSlot(1, inputs);
      return new ItemStack[] {createItemStackForSourceBlock(paintSource.itemID, paintSource.getItemDamage())};
    }  
    
    @Override
    public boolean isValidTarget(ItemStack target) {
      if(target == null) {
        return false;
      }
      Block blk = Util.getBlockFromItemId(target.itemID);
      return blk instanceof BlockStairs;
    }
  }
  
  
}
