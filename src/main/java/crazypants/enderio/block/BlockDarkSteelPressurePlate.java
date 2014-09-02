package crazypants.enderio.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityPaintedBlock;
import crazypants.util.Util;

public class BlockDarkSteelPressurePlate extends BlockPressurePlate implements IResourceTooltipProvider, ITileEntityProvider {

  public static BlockDarkSteelPressurePlate create() {
    BlockDarkSteelPressurePlate res = new BlockDarkSteelPressurePlate();
    res.init();
    return res;
  }

  public BlockDarkSteelPressurePlate() {
    super(ModObject.blockDarkSteelPressurePlate.unlocalisedName, Material.iron, Sensitivity.players);
    setBlockName(ModObject.blockDarkSteelPressurePlate.unlocalisedName);
    setStepSound(Block.soundTypeMetal);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(2.0f);
  }

  protected void init() {
    GameRegistry.registerBlock(this, ModObject.blockDarkSteelPressurePlate.unlocalisedName);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate(this));
  }

  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon("enderio:" + ModObject.blockDarkSteelPressurePlate.unlocalisedName);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      if(tef.getSourceBlock() != null) {
        return tef.getSourceBlock().getIcon(blockSide, tef.getSourceBlockMetadata());
      }
    }
    return super.getIcon(world, x, y, z, blockSide);
  }
  
  @Override
  public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tepb = (TileEntityPaintedBlock) te;
      ItemStack stack = new ItemStack(this);
      if(tepb.getSourceBlock() != null) {
        PainterUtil.setSourceBlock(stack, tepb.getSourceBlock(), tepb.getSourceBlockMetadata());
      }

      float f = 0.7F;
      double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
      double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
      double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
      EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, stack);
      entityitem.delayBeforeCanPickup = 10;
      world.spawnEntityInWorld(entityitem);
    }
  }
  
  @Override
  public int quantityDropped(int meta, int fortune, Random random) {
    return 0; // for custom drops
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    Block b = PainterUtil.getSourceBlock(stack);
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      tef.setSourceBlock(b);
      tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));
    }
    world.markBlockForUpdate(x, y, z);
    super.onBlockPlacedBy(world, x, y, z, player, stack);
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      if(tef.getSourceBlock() != null) {
        return tef.getSourceBlock().colorMultiplier(world, x, y, z);
      }
    }
    return super.colorMultiplier(world, x, y, z);
  }

  public static final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate(Block dspp) {
      super(dspp);
    }

    public boolean isValidPaintSource(ItemStack paintSource) {
      if(BasicPainterTemplate.isValidSourceDefault(paintSource)) {
        return true;
      }
      if(paintSource == null) {
        return false;
      }
      Block block = Util.getBlockFromItemId(paintSource);
      if(block == null) {
        return false;
      }
      return Block.getBlockFromItem(paintSource.getItem()) == EnderIO.blockFusedQuartz;
    }

    @Override
    public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
      ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
      if(paintSource == null) {
        return new ResultStack[0];
      }
      return new ResultStack[] { new ResultStack(createItemStackForSourceBlock(Block.getBlockFromItem(paintSource.getItem()), paintSource.getItemDamage())) };
    }

    public static ItemStack createItemStackForSourceBlock(Block block, int damage) {
      ItemStack result = new ItemStack(EnderIO.blockDarkSteelPressurePlate, 1, damage);
      PainterUtil.setSourceBlock(result, block, damage);
      return result;
    }
  }

}
