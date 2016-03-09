package crazypants.enderio.block;

import java.util.Collections;
import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IHaveRenderers;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.IPaintedBlock;
import crazypants.enderio.machine.painter.PaintSourceValidator;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityPaintedBlock;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDarkSteelPressurePlate extends BlockPressurePlate implements IResourceTooltipProvider, ITileEntityProvider, IPaintedBlock, IHaveRenderers {

  public static BlockDarkSteelPressurePlate create() {
    BlockDarkSteelPressurePlate res = new BlockDarkSteelPressurePlate();
    res.init();
    return res;
  }

  public BlockDarkSteelPressurePlate() {
    super(Material.iron, Sensitivity.MOBS);
    setUnlocalizedName(ModObject.blockDarkSteelPressurePlate.unlocalisedName);
    setStepSound(Block.soundTypeMetal);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(2.0f);
  }

  protected void init() {
    GameRegistry.registerBlock(this, BlockItemDarkSteelPressurePlate.class, ModObject.blockDarkSteelPressurePlate.unlocalisedName);
    GameRegistry.registerTileEntity(TileEntityDarkSteelPressurePlate.class, ModObject.blockDarkSteelPressurePlate.unlocalisedName + "TileEntity");
    // MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate(this));
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    if (itemStack != null && itemStack.getItemDamage() == 1) {
      return getUnlocalizedName() + ".silent";
    }
    return getUnlocalizedName();
  }

  @Override
  public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    super.harvestBlock(worldIn, player, pos, state, te);
    worldIn.setBlockToAir(pos);
  }

  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {    
    TileEntity te = world.getTileEntity(pos);
    if(! (te instanceof TileEntityDarkSteelPressurePlate)) {
      return Collections.emptyList();
    }    
    TileEntityDarkSteelPressurePlate tepb = (TileEntityDarkSteelPressurePlate)te;
    ItemStack stack = new ItemStack(this, 1, tepb.isSilent() ? 1 : 0);
    if (tepb.getSourceBlock() != null) {
      PainterUtil.setSourceBlock(stack, tepb.getSourceBlock());
    }
    return Lists.newArrayList(stack);
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    IBlockState b = PainterUtil.getSourceBlockState(stack);
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEntityDarkSteelPressurePlate) {
      TileEntityDarkSteelPressurePlate tef = (TileEntityDarkSteelPressurePlate) te;
      tef.setSourceBlock(b);      
      tef.setSilent(stack.getItemDamage() == 1);
    }
    world.markBlockForUpdate(pos);
    super.onBlockPlacedBy(world, pos, state, placer, stack);
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityDarkSteelPressurePlate();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      if (tef.getSourceBlock() != null) {
        return tef.getSourceBlock().getBlock().colorMultiplier(world, pos);
      }
    }
    return super.colorMultiplier(world, pos, renderPass);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item arg0, CreativeTabs arg1, List items) {
    items.add(new ItemStack(this, 1, 0));
    items.add(new ItemStack(this, 1, 1));
  }

  @Override
  protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
    AxisAlignedBB axisalignedbb = this.getSensitiveAABB(pos);
    List<? extends Entity> list;

    list = worldIn.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

    if (!list.isEmpty()) {
      for (Entity entity : list) {
        if (!entity.doesEntityNotTriggerPressurePlate()) {
          return 15;
        }
      }
    }

    return 0;
  }

  @Override
  protected void updateState(World worldIn, BlockPos pos, IBlockState state, int oldRedstoneStrength) {
    int i = this.computeRedstoneStrength(worldIn, pos);
    boolean flag = oldRedstoneStrength > 0;
    boolean flag1 = i > 0;

    if (oldRedstoneStrength != i) {
      state = this.setRedstoneStrength(state, i);
      worldIn.setBlockState(pos, state, 2);
      this.updateNeighbors(worldIn, pos);
      worldIn.markBlockRangeForRenderUpdate(pos, pos);
    }

    boolean playSound = true;
    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof TileEntityDarkSteelPressurePlate && ((TileEntityDarkSteelPressurePlate) te).isSilent()) {
      playSound = false;
    }
    if (playSound) {
      if (!flag1 && flag) {
        worldIn.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, "random.click", 0.3F, 0.5F);
      } else if (flag1 && !flag) {
        worldIn.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);
      }
    }

    if (flag1) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
  }

  // public static final class PainterTemplate extends BasicPainterTemplate {
  //
  // public PainterTemplate(Block dspp) {
  // super(dspp);
  // }
  //
  // @Override
  // public boolean isValidPaintSource(ItemStack paintSource) {
  // if (PaintSourceValidator.instance.isValidSourceDefault(paintSource)) {
  // return true;
  // }
  // if (paintSource == null) {
  // return false;
  // }
  // Block block = Util.getBlockFromItemId(paintSource);
  // if (block == null) {
  // return false;
  // }
  // return Block.getBlockFromItem(paintSource.getItem()) == EnderIO.blockFusedQuartz;
  // }
  //
  // @Override
  // public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
  // ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
  // if (paintSource == null) {
  // return new ResultStack[0];
  // }
  // ItemStack target = MachineRecipeInput.getInputForSlot(0, inputs);
  // ItemStack resultStack = createItemStackForSourceBlock(Block.getBlockFromItem(paintSource.getItem()), paintSource.getItemDamage());
  // resultStack.setItemDamage(target.getItemDamage());
  // return new ResultStack[] { new ResultStack(resultStack) };
  // }
  //
  // public static ItemStack createItemStackForSourceBlock(Block block, int damage) {
  // ItemStack result = new ItemStack(EnderIO.blockDarkSteelPressurePlate, 1, damage);
  // PainterUtil.setSourceBlock(result, block, damage);
  // return result;
  // }
  // }

   @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
      ClientUtil.regRenderer(Item.getItemFromBlock(this), 0, ModObject.blockDarkSteelPressurePlate.unlocalisedName);
      ClientUtil.regRenderer(Item.getItemFromBlock(this), 1, ModObject.blockDarkSteelPressurePlate.unlocalisedName); 
  }

}
