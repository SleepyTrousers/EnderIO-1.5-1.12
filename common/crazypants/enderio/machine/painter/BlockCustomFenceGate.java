package crazypants.enderio.machine.painter;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.RecipeInput;

public class BlockCustomFenceGate extends BlockFenceGate implements ITileEntityProvider {

  public static int renderId;

  public static BlockCustomFenceGate create() {
    BlockCustomFenceGate result = new BlockCustomFenceGate();
    result.init();
    return result;
  }

  public BlockCustomFenceGate() {
    super(ModObject.blockCustomFenceGate.id);
    setCreativeTab(null);
    setUnlocalizedName(ModObject.blockCustomFenceGate.unlocalisedName);
    setHardness(2.0F);
    setResistance(5.0F);
    setStepSound(soundWoodFootstep);
  }

  private void init() {
    LanguageRegistry.addName(this, ModObject.blockCustomFenceGate.name);
    GameRegistry.registerBlock(this, BlockItemCustomFenceGate.class, ModObject.blockCustomFenceGate.unlocalisedName);
    GameRegistry.registerTileEntity(TileEntityCustomBlock.class, ModObject.blockCustomFenceGate.unlocalisedName + "TileEntity");
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return null;
  }

  @Override
  public int getLightOpacity(World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileEntityCustomBlock) {
      TileEntityCustomBlock tef = (TileEntityCustomBlock) te;
      if (tef.getSourceBlockId() > 0) {
        return Math.min(super.getLightOpacity(world, x, y, z), Block.lightOpacity[tef.getSourceBlockId()]);
      }

    }
    return super.getLightOpacity(world, x, y, z);
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileEntityCustomBlock();
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
    // world.markBlockForUpdate(x, y, z);

    int l = (MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) % 4;
    world.setBlockMetadataWithNotify(x, y, z, l, 2);
  }

  public static ItemStack createItemStackForSourceBlock(int id, int damage) {
    ItemStack result = new ItemStack(ModObject.blockCustomFenceGate.actualId, 1, damage);
    PainterUtil.setSourceBlock(result, id, damage);
    return result;
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
      super(Block.fenceGate.blockID/* , Block.netherFence.blockID */);
    }

    @Override
    public ItemStack[] getCompletedResult(RecipeInput... inputs) {
      ItemStack paintSource = RecipeInput.getInputForSlot(1, inputs);
      return new ItemStack[] { createItemStackForSourceBlock(paintSource.itemID, paintSource.getItemDamage()) };
    }

  }

}
