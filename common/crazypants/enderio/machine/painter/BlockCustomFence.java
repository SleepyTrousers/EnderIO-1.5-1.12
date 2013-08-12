package crazypants.enderio.machine.painter;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.*;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.*;

public class BlockCustomFence extends BlockFence implements ITileEntityProvider {

  public static BlockCustomFence create() {
    BlockCustomFence result = new BlockCustomFence();
    result.init();
    return result;
  }

  public BlockCustomFence() {
    super(ModObject.blockCustomFence.id, ModObject.blockCustomFence.unlocalisedName, Material.wood);
    setUnlocalizedName(ModObject.blockCustomFence.unlocalisedName);
    setHardness(2.0F);
    setResistance(5.0F);
    setStepSound(soundWoodFootstep);
    setCreativeTab(null);
  }

  private void init() {
    LanguageRegistry.addName(this, ModObject.blockCustomFence.name);
    GameRegistry.registerBlock(this, BlockItemCustomFence.class, ModObject.blockCustomFence.unlocalisedName);
    GameRegistry.registerTileEntity(TileEntityCustomBlock.class, ModObject.blockCustomFence.unlocalisedName + "TileEntity");    
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
  }

  public static ItemStack createItemStackForSourceBlock(int id, int damage) {
    ItemStack result = new ItemStack(ModObject.blockCustomFence.id, 1, damage);
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
  public boolean canConnectFenceTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
    int l = par1IBlockAccess.getBlockId(par2, par3, par4);
    if(l == ModObject.blockCustomFenceGate.id) {
      return true;
    }
    return super.canConnectFenceTo(par1IBlockAccess, par2, par3, par4);
  }

  @Override
  public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int blockSide) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileEntityCustomBlock) {
      TileEntityCustomBlock tef = (TileEntityCustomBlock) te;
      if (tef.getSourceBlockId() > 0 && tef.getSourceBlockId() < Block.blocksList.length) {
        return blocksList[tef.getSourceBlockId()].getIcon(blockSide, tef.getSourceBlockMetadata());
      }
    } else {
      System.out.println("BlockCustFence: No tile entity.");
    }
    return blocksList[Block.anvil.blockID].getBlockTexture(world, x, y, z, blockSide);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister par1IconRegister) {
    this.blockIcon = par1IconRegister.registerIcon("enderio:conduitConnector");
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
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
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

  /**
   * Called when the block receives a BlockEvent - see World.addBlockEvent. By
   * default, passes it on to the tile entity at this location. Args: world, x,
   * y, z, blockID, EventID, event parameter
   */
  @Override
  public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
    super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
    TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
    return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
  }

  public static final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate() {
      super(Block.fence.blockID/* , Block.netherFence.blockID */);
    }

    @Override
    public ItemStack[] getCompletedResult(RecipeInput... inputs) {
      ItemStack paintSource = RecipeInput.getInputForSlot(1, inputs);
      return new ItemStack[] {createItemStackForSourceBlock(paintSource.itemID, paintSource.getItemDamage())};
    }    

  }

}
