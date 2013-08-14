package crazypants.enderio.machine.painter;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.*;
import cpw.mods.fml.common.registry.*;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.*;

public class BlockCustomWall extends BlockWall implements ITileEntityProvider {

  
  public static BlockCustomWall create() {
    BlockCustomWall result = new BlockCustomWall();
    result.init();
    return result;
  }
  
  public BlockCustomWall() {
    super(ModObject.blockCustomWall.id, Block.cobblestone);
    setCreativeTab(null);
    setUnlocalizedName(ModObject.blockCustomWall.unlocalisedName);
  }

  
  private void init() {
    LanguageRegistry.addName(this, ModObject.blockCustomWall.name);
    GameRegistry.registerBlock(this, BlockItemCustomWall.class, ModObject.blockCustomWall.unlocalisedName);
    GameRegistry.registerTileEntity(TileEntityCustomBlock.class, ModObject.blockCustomWall.unlocalisedName + "TileEntity");
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
  }
  

  public static ItemStack createItemStackForSourceBlock(int id, int damage) {
    ItemStack result = new ItemStack(ModObject.blockCustomWall.id, 1, damage);
    PainterUtil.setSourceBlock(result, id, damage);
    return result;
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
  public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
    int id = world.getBlockId(x, y, z);
    if (id == this.blockID) {
      return true;
    }
    return super.canPlaceTorchOnTop(world, x, y, z);
  }

  
  @Override
  public boolean canConnectWallTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
    int l = par1IBlockAccess.getBlockId(par2, par3, par4);
    if(l == ModObject.blockCustomFenceGate.id) {
      return true;
    }
    return super.canConnectWallTo(par1IBlockAccess, par2, par3, par4);
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
      super(Block.cobblestoneWall.blockID);
    }

    @Override
    public ItemStack[] getCompletedResult(RecipeInput... inputs) {
      ItemStack paintSource = RecipeInput.getInputForSlot(1, inputs);
      return new ItemStack[] {createItemStackForSourceBlock(paintSource.itemID, paintSource.getItemDamage())};
    }  
  }

  
}
