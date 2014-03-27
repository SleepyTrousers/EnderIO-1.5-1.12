package crazypants.enderio.machine.painter;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.IRecipeOutput;
import crazypants.enderio.crafting.impl.EnderIoRecipe;
import crazypants.enderio.crafting.impl.RecipeInputClass;
import crazypants.enderio.crafting.impl.RecipeOutput;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;

public class BlockPaintedSlab extends BlockSlab implements ITileEntityProvider {

  private IIcon lastRemovedComponetIcon = null;

  private Random rand = new Random();

  private final boolean isDouble;

  public BlockPaintedSlab(boolean isDouble) {
    super(isDouble, new Material(MapColor.stoneColor));
    this.isDouble = isDouble;
    setCreativeTab(null);
    setBlockName(ModObject.blockPaintedSlab.unlocalisedName + (isDouble ? "Double" : ""));
    setHardness(0.5F);
    setResistance(5.0F);
    setLightOpacity(0);
  }

  public void init() {
    // This is required so it is assigned prior to the BlockItem being
    // registered.
    if(isDouble) {
      GameRegistry.registerBlock(this, BlockItemPaintedSlab.class, ModObject.blockPaintedDoubleSlab.unlocalisedName);
      GameRegistry.registerTileEntity(TileEntityPaintedSlab.class, ModObject.blockPaintedDoubleSlab.unlocalisedName + "TileEntity");
    } else {
      GameRegistry.registerBlock(this, BlockItemPaintedSlab.class, ModObject.blockPaintedSlab.unlocalisedName);
      GameRegistry.registerTileEntity(TileEntityPaintedBlock.class, ModObject.blockPaintedSlab.unlocalisedName + "TileEntity");
      MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
    }
  }

  public static ItemStack createItemStackForSourceBlock(Block source, int damage) {
    ItemStack result = new ItemStack(EnderIO.blockPaintedSlab, 1, 0);
    PainterUtil.setSourceBlock(result, source, damage);
    return result;
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
    return Blocks.anvil.getIcon(world, x, y, z, blockSide);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void registerBlockIcons(IIconRegister IIconRegister) {
    blockIcon = IIconRegister.registerIcon("enderio:conduitConnector");
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(World world, MovingObjectPosition target,
      EffectRenderer effectRenderer) {
    IIcon tex = null;

    TileEntityPaintedBlock cb = (TileEntityPaintedBlock)
        world.getTileEntity(target.blockX, target.blockY, target.blockZ);
    Block b = cb.getSourceBlock();
    if(b != null) {
      tex = b.getIcon(ForgeDirection.NORTH.ordinal(), cb.getSourceBlockMetadata());
    }
    if(tex == null) {
      tex = blockIcon;
    }
    lastRemovedComponetIcon = tex;
    addBlockHitEffects(world, effectRenderer, target.blockX, target.blockY,
        target.blockZ, target.sideHit, tex);
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean addDestroyEffects(World world, int x, int y, int z, int
      meta, EffectRenderer effectRenderer) {
    IIcon tex = lastRemovedComponetIcon;
    byte b0 = 4;
    for (int j1 = 0; j1 < b0; ++j1) {
      for (int k1 = 0; k1 < b0; ++k1) {
        for (int l1 = 0; l1 < b0; ++l1) {
          double d0 = x + (j1 + 0.5D) / b0;
          double d1 = y + (k1 + 0.5D) / b0;
          double d2 = z + (l1 + 0.5D) / b0;
          int i2 = this.rand.nextInt(6);
          EntityDiggingFX fx = new EntityDiggingFX(world, d0, d1, d2, d0 - x - 0.5D,
              d1 - y - 0.5D, d2 - z - 0.5D, this, i2, 0).applyColourMultiplier(x, y, z);
          fx.setParticleIcon(tex);
          effectRenderer.addEffect(fx);
        }
      }
    }
    return true;

  }

  @SideOnly(Side.CLIENT)
  private void addBlockHitEffects(World world, EffectRenderer effectRenderer,
      int x, int y, int z, int side, IIcon tex) {
    float f = 0.1F;
    double d0 = x + rand.nextDouble() * (getBlockBoundsMaxX() -
        getBlockBoundsMinX() - f * 2.0F) + f + getBlockBoundsMinX();
    double d1 = y + rand.nextDouble() * (getBlockBoundsMaxY() -
        getBlockBoundsMinY() - f * 2.0F) + f + getBlockBoundsMinY();
    double d2 = z + rand.nextDouble() * (getBlockBoundsMaxZ() -
        getBlockBoundsMinZ() - f * 2.0F) + f + getBlockBoundsMinZ();
    if(side == 0) {
      d1 = y + getBlockBoundsMinY() - f;
    } else if(side == 1) {
      d1 = y + getBlockBoundsMaxY() + f;
    } else if(side == 2) {
      d2 = z + getBlockBoundsMinZ() - f;
    } else if(side == 3) {
      d2 = z + getBlockBoundsMaxZ() + f;
    } else if(side == 4) {
      d0 = x + getBlockBoundsMinX() - f;
    } else if(side == 5) {
      d0 = x + getBlockBoundsMaxX() + f;
    }
    EntityDiggingFX digFX = new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D,
        0.0D, this, side, 0);
    digFX.applyColourMultiplier(x, y,
        z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
    digFX.setParticleIcon(tex);
    effectRenderer.addEffect(digFX);
  }

  @Override
  public TileEntity createNewTileEntity(World world, int metadata) {
    return new TileEntityPaintedSlab();
  }

  @Override
  public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      if(tef.getSourceBlock() != null) {
        return Math.min(super.getLightOpacity(world, x, y, z), tef.getSourceBlock().getLightOpacity(world, x, y, z));
      }

    }
    return super.getLightOpacity(world, x, y, z);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityPaintedBlock) {
      TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
      Block b = PainterUtil.getSourceBlock(stack);
      tef.setSourceBlock(b);
      tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));
    }
    world.markBlockForUpdate(x, y, z);
    super.onBlockPlacedBy(world, x, y, z, player, stack);
  }

  /**
   * Remove the tile entity too.
   */
  @Override
  public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {

    if(!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      TileEntity te = world.getTileEntity(x, y, z);

      if(te instanceof TileEntityPaintedSlab && !((TileEntityPaintedSlab) te).isConvertingToFullBlock) {
        TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;

        for (int i = 0; i < super.quantityDropped(null); i++) {
          ItemStack itemStack = createItemStackForSourceBlock(tef.getSourceBlock(), tef.getSourceBlockMetadata());

          float f = 0.7F;
          double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
          entityitem.delayBeforeCanPickup = 10;
          world.spawnEntityInWorld(entityitem);
        }
      }
    }
    world.removeTileEntity(x, y, z);
  }

  @Override
  public String func_150002_b(int var1) {
    return getUnlocalizedName();
  }

  @Override
  public int quantityDropped(Random par1Random) {
    return 0; // need to do custom dropping to maintain source metadata
  }

  public static final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate() {
      super(Blocks.wooden_slab, Blocks.stone_slab, EnderIO.blockPaintedDoubleSlab, EnderIO.blockPaintedSlab);
    }

    @Override
    public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
      ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
      if(paintSource == null) {
        return new ResultStack[0];
      }
      return new ResultStack[] { new ResultStack(createItemStackForSourceBlock(Block.getBlockFromItem(paintSource.getItem()), paintSource.getItemDamage())) };
    }

    @Override
    public boolean isValidTarget(ItemStack target) {
      if(target == null) {
        return false;
      }
      Block blk = Block.getBlockFromItem(target.getItem());
      return blk instanceof BlockSlab;
    }

    @Override
    public List<IEnderIoRecipe> getAllRecipes() {
      IRecipeInput input = new RecipeInputClass<BlockSlab>(new ItemStack(Blocks.stone_slab), BlockSlab.class, new ItemStack(Blocks.wooden_slab));
      IRecipeOutput output = new RecipeOutput(new ItemStack(EnderIO.blockPaintedSlab, 1, 0));

      IEnderIoRecipe recipe = new EnderIoRecipe(getMachineName(), DEFAULT_ENERGY_PER_TASK, input, output);
      return Collections.singletonList(recipe);
    }
  }

}
