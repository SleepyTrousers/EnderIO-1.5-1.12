package crazypants.enderio.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
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
import crazypants.enderio.machine.painter.IPaintedBlock;
import crazypants.enderio.machine.painter.PaintSourceValidator;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityPaintedBlock;
import crazypants.util.Util;

public class BlockDarkSteelPressurePlate extends BlockPressurePlate implements IResourceTooltipProvider, ITileEntityProvider, IPaintedBlock {

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
    GameRegistry.registerTileEntity(TileEntityDarkSteelPressurePlate.class, ModObject.blockDarkSteelPressurePlate.unlocalisedName + "TileEntity");
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate(this));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon("enderio:" + ModObject.blockDarkSteelPressurePlate.unlocalisedName);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    if(itemStack != null && itemStack.getItemDamage() == 1) {
      return getUnlocalizedName() + ".silent";
    }
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
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
    if(te instanceof TileEntityDarkSteelPressurePlate) {
      TileEntityDarkSteelPressurePlate tepb = (TileEntityDarkSteelPressurePlate) te;
      ItemStack stack = new ItemStack(this, 1, tepb.isSilent() ? 1 : 0);
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
    if(te instanceof TileEntityDarkSteelPressurePlate) {
      TileEntityDarkSteelPressurePlate tef = (TileEntityDarkSteelPressurePlate) te;
      tef.setSourceBlock(b);
      tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));
      tef.setSilent(stack.getItemDamage() == 1);
    }
    world.markBlockForUpdate(x, y, z);
    super.onBlockPlacedBy(world, x, y, z, player, stack);
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityDarkSteelPressurePlate();
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item arg0, CreativeTabs arg1, List items) {
    items.add(new ItemStack(this, 1, 0));
    items.add(new ItemStack(this, 1, 1));
  }

  @Override
  protected void func_150062_a(World world, int x, int y, int z, int p_150062_5_) {
    int i1 = func_150065_e(world, x, y, z);
    boolean flag = p_150062_5_ > 0;
    boolean flag1 = i1 > 0;

    if(p_150062_5_ != i1) {
      world.setBlockMetadataWithNotify(x, y, z, func_150066_d(i1), 2);
      func_150064_a_(world, x, y, z);
      world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
    }

    boolean playSound = true;
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityDarkSteelPressurePlate && ((TileEntityDarkSteelPressurePlate)te).isSilent()) {
      playSound = false;
    }
    if(playSound) {
      if(!flag1 && flag) {
        world.playSoundEffect(x + 0.5D, y + 0.1D, z + 0.5D, "random.click", 0.3F, 0.5F);
      } else if(flag1 && !flag) {
        world.playSoundEffect(x + 0.5D, y + 0.1D, z + 0.5D, "random.click", 0.3F, 0.6F);
      }
    }

    if(flag1) {
      world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
    }
  }

  public static final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate(Block dspp) {
      super(dspp);
    }

    @Override
    public boolean isValidPaintSource(ItemStack paintSource) {
      if(PaintSourceValidator.instance.isValidSourceDefault(paintSource)) {
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
      ItemStack target = MachineRecipeInput.getInputForSlot(0, inputs);
      ItemStack resultStack = createItemStackForSourceBlock(Block.getBlockFromItem(paintSource.getItem()), paintSource.getItemDamage());
      resultStack.setItemDamage(target.getItemDamage());
      return new ResultStack[] { new ResultStack(resultStack) };
    }

    public static ItemStack createItemStackForSourceBlock(Block block, int damage) {
      ItemStack result = new ItemStack(EnderIO.blockDarkSteelPressurePlate, 1, damage);
      PainterUtil.setSourceBlock(result, block, damage);
      return result;
    }
  }

}
