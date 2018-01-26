package crazypants.enderio.base.block.painted;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.block.darksteel.door.BlockDarkSteelDoor;
import crazypants.enderio.base.block.darksteel.door.BlockItemDarkSteelDoor;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.paint.render.PaintHelper;
import crazypants.enderio.base.paint.render.PaintRegistry;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.BasicPainterTemplate;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.ICacheKey;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.ISmartRenderAwareBlock;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.property.EnumRenderPart;
import crazypants.enderio.base.render.registry.SmartModelAttacher;
import crazypants.enderio.base.render.util.QuadCollector;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPaintedDoor extends BlockDarkSteelDoor implements ITileEntityProvider, IPaintable.ITexturePaintableBlock, ISmartRenderAwareBlock,
    IRenderMapper.IBlockRenderMapper.IRenderLayerAware, IRenderMapper.IItemRenderMapper.IItemModelMapper, IModObject.WithBlockItem {

  public static BlockPaintedDoor create_wooden(@Nonnull IModObject modObject) {
    BlockPaintedDoor result = new BlockPaintedDoor(modObject, Material.WOOD, false);
    result.init(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new BasicPainterTemplate<BlockPaintedDoor>(result, Blocks.OAK_DOOR) {
      @Override
      public boolean isValidTarget(@Nonnull ItemStack target) {
        return target.getItem() == Items.OAK_DOOR;
      }
    });
    return result;
  }

  public static BlockPaintedDoor create_iron(@Nonnull IModObject modObject) {
    BlockPaintedDoor result = new BlockPaintedDoor(modObject, Material.IRON, false);
    result.init(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new BasicPainterTemplate<BlockPaintedDoor>(result, Blocks.IRON_DOOR) {
      @Override
      public boolean isValidTarget(@Nonnull ItemStack target) {
        return target.getItem() == Items.IRON_DOOR;
      }
    });
    return result;
  }

  public static BlockPaintedDoor create_dark(@Nonnull IModObject modObject) {
    BlockPaintedDoor result = new BlockPaintedDoor(modObject, Material.IRON, true);
    result.init(modObject);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER,
        new BasicPainterTemplate<BlockPaintedDoor>(result, ModObject.blockDarkSteelDoor.getBlockNN()) {
          @Override
          public boolean isValidTarget(@Nonnull ItemStack target) {
            return target.getItem() == ModObject.blockDarkSteelDoor.getItemNN();
          }
        });
    return result;
  }

  protected BlockPaintedDoor(@Nonnull IModObject modObject, Material materialIn, boolean isBlastResistant) {
    super(modObject, materialIn, isBlastResistant);
    Prep.setNoCreativeTab(this);
  }

  protected BlockDarkSteelDoor init(@Nonnull IModObject modObject) {
    SmartModelAttacher.registerNoProps(this);
    PaintRegistry.registerModel("door_bottom", new ResourceLocation("minecraft", "block/iron_door_bottom"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("door_top", new ResourceLocation("minecraft", "block/iron_door_top"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("door_bottom_rh", new ResourceLocation("minecraft", "block/iron_door_bottom_rh"), PaintRegistry.PaintMode.ALL_TEXTURES);
    PaintRegistry.registerModel("door_top_rh", new ResourceLocation("minecraft", "block/iron_door_top_rh"), PaintRegistry.PaintMode.ALL_TEXTURES);
    return this;
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new BlockItemDarkSteelDoor(this, null));
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
    return new TileEntityPaintedBlock();
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer,
      @Nonnull ItemStack stack) {
    setPaintSource(state, world, pos, PaintUtil.getSourceBlock(stack));
    if (!world.isRemote) {
      // BlockPos pos2 = pos.up();
      // world.setBlockState(pos2, this.getDefaultState().withProperty(HALF, EnumDoorHalf.UPPER));
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    }
    return super.removedByPlayer(state, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(@Nonnull World worldIn, @Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te,
      @Nonnull ItemStack stack) {
    super.harvestBlock(worldIn, player, pos, state, te, stack);
    super.removedByPlayer(state, worldIn, pos, player, true);
  }

  @Override
  public @Nonnull List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
    List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
    for (ItemStack drop : drops) {
      PaintUtil.setSourceBlock(NullHelper.notnullM(drop, "null stack from getDrops()"), getPaintSource(state, world, pos));
    }
    return drops;
  }

  @Override
  public @Nonnull ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos,
      @Nonnull EntityPlayer player) {
    final ItemStack pickBlock = super.getPickBlock(state, target, world, pos, player);
    PaintUtil.setSourceBlock(pickBlock, getPaintSource(state, world, pos));
    return pickBlock;
  }

  @Override
  public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    IBlockStateWrapper blockStateWrapper = new BlockStateWrapperBase(state, world, pos, this);
    blockStateWrapper.addCacheKey(getPaintSource(state, world, pos)).addCacheKey(state.getValue(FACING)).addCacheKey(state.getValue(OPEN))
        .addCacheKey(state.getValue(HALF));
    blockStateWrapper.bakeModel();
    return blockStateWrapper;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return this;
  }

  @SideOnly(Side.CLIENT)
  private IBakedModel mapRender(IBlockState state, @Nullable IBlockState paint) {
    EnumFacing facing = state.getValue(BlockDoor.FACING);
    Boolean open = state.getValue(BlockDoor.OPEN);
    EnumHingePosition hinge = state.getValue(BlockDoor.HINGE);
    EnumDoorHalf half = state.getValue(BlockDoor.HALF);

    String model;
    ModelRotation modelState = null;

    final EnumFacing east = EnumFacing.EAST;
    final EnumFacing south = EnumFacing.SOUTH;
    final EnumFacing west = EnumFacing.WEST;
    final EnumFacing north = EnumFacing.NORTH;
    final EnumDoorHalf lower = EnumDoorHalf.LOWER;
    final EnumDoorHalf upper = EnumDoorHalf.UPPER;
    final EnumHingePosition left = EnumHingePosition.LEFT;
    final EnumHingePosition right = EnumHingePosition.RIGHT;

    if (facing == east && half == lower && hinge == left && open == false) {
      model = "iron_door_bottom";
    } else if (facing == south && half == lower && hinge == left && open == false) {
      model = "iron_door_bottom";
      modelState = ModelRotation.X0_Y90;
    } else if (facing == west && half == lower && hinge == left && open == false) {
      model = "iron_door_bottom";
      modelState = ModelRotation.X0_Y180;
    } else if (facing == north && half == lower && hinge == left && open == false) {
      model = "iron_door_bottom";
      modelState = ModelRotation.X0_Y270;
    } else if (facing == east && half == lower && hinge == right && open == false) {
      model = "iron_door_bottom_rh";
    } else if (facing == south && half == lower && hinge == right && open == false) {
      model = "iron_door_bottom_rh";
      modelState = ModelRotation.X0_Y90;
    } else if (facing == west && half == lower && hinge == right && open == false) {
      model = "iron_door_bottom_rh";
      modelState = ModelRotation.X0_Y180;
    } else if (facing == north && half == lower && hinge == right && open == false) {
      model = "iron_door_bottom_rh";
      modelState = ModelRotation.X0_Y270;
    } else if (facing == east && half == lower && hinge == left && open == true) {
      model = "iron_door_bottom_rh";
      modelState = ModelRotation.X0_Y90;
    } else if (facing == south && half == lower && hinge == left && open == true) {
      model = "iron_door_bottom_rh";
      modelState = ModelRotation.X0_Y180;
    } else if (facing == west && half == lower && hinge == left && open == true) {
      model = "iron_door_bottom_rh";
      modelState = ModelRotation.X0_Y270;
    } else if (facing == north && half == lower && hinge == left && open == true) {
      model = "iron_door_bottom_rh";
    } else if (facing == east && half == lower && hinge == right && open == true) {
      model = "iron_door_bottom";
      modelState = ModelRotation.X0_Y270;
    } else if (facing == south && half == lower && hinge == right && open == true) {
      model = "iron_door_bottom";
    } else if (facing == west && half == lower && hinge == right && open == true) {
      model = "iron_door_bottom";
      modelState = ModelRotation.X0_Y90;
    } else if (facing == north && half == lower && hinge == right && open == true) {
      model = "iron_door_bottom";
      modelState = ModelRotation.X0_Y180;
    } else if (facing == east && half == upper && hinge == left && open == false) {
      model = "iron_door_top";
    } else if (facing == south && half == upper && hinge == left && open == false) {
      model = "iron_door_top";
      modelState = ModelRotation.X0_Y90;
    } else if (facing == west && half == upper && hinge == left && open == false) {
      model = "iron_door_top";
      modelState = ModelRotation.X0_Y180;
    } else if (facing == north && half == upper && hinge == left && open == false) {
      model = "iron_door_top";
      modelState = ModelRotation.X0_Y270;
    } else if (facing == east && half == upper && hinge == right && open == false) {
      model = "iron_door_top_rh";
    } else if (facing == south && half == upper && hinge == right && open == false) {
      model = "iron_door_top_rh";
      modelState = ModelRotation.X0_Y90;
    } else if (facing == west && half == upper && hinge == right && open == false) {
      model = "iron_door_top_rh";
      modelState = ModelRotation.X0_Y180;
    } else if (facing == north && half == upper && hinge == right && open == false) {
      model = "iron_door_top_rh";
      modelState = ModelRotation.X0_Y270;
    } else if (facing == east && half == upper && hinge == left && open == true) {
      model = "iron_door_top_rh";
      modelState = ModelRotation.X0_Y90;
    } else if (facing == south && half == upper && hinge == left && open == true) {
      model = "iron_door_top_rh";
      modelState = ModelRotation.X0_Y180;
    } else if (facing == west && half == upper && hinge == left && open == true) {
      model = "iron_door_top_rh";
      modelState = ModelRotation.X0_Y270;
    } else if (facing == north && half == upper && hinge == left && open == true) {
      model = "iron_door_top_rh";
    } else if (facing == east && half == upper && hinge == right && open == true) {
      model = "iron_door_top";
      modelState = ModelRotation.X0_Y270;
    } else if (facing == south && half == upper && hinge == right && open == true) {
      model = "iron_door_top";
    } else if (facing == west && half == upper && hinge == right && open == true) {
      model = "iron_door_top";
      modelState = ModelRotation.X0_Y90;
    } else if (facing == north && half == upper && hinge == right && open == true) {
      model = "iron_door_top";
      modelState = ModelRotation.X0_Y180;
    } else {
      return null;
    }

    return PaintRegistry.getModel(IBakedModel.class, model, paint, modelState);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ICacheKey getCacheKey(@Nonnull Block block, @Nonnull ItemStack stack, @Nonnull ICacheKey cacheKey) {
    return cacheKey.addCacheKey(getPaintSource(block, stack));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBakedModel> mapItemRender(@Nonnull Block block, @Nonnull ItemStack stack) {
    IBlockState paintSource = getPaintSource(block, stack);
    IBlockState stdOverlay = ModObject.block_machine_base.getBlockNN().getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.PAINT_OVERLAY);

    IBakedModel model1 = PaintRegistry.getModel(IBakedModel.class, "door_bottom", paintSource, null);
    IBakedModel model2 = PaintRegistry.getModel(IBakedModel.class, "door_bottom", stdOverlay, PaintRegistry.OVERLAY_TRANSFORMATION3);
    List<IBakedModel> list = new ArrayList<IBakedModel>();
    list.add(model1);
    list.add(model2);
    return list;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (tab == EnderIOTab.tabNoTab) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IBlockState> mapBlockRender(@Nonnull IBlockStateWrapper state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, BlockRenderLayer blockLayer,
      @Nonnull QuadCollector quadCollector) {
    IBlockState paintSource = getPaintSource(state, world, pos);
    if (blockLayer == null || PaintUtil.canRenderInLayer(paintSource, blockLayer)) {
      quadCollector.addFriendlybakedModel(blockLayer, mapRender(state, paintSource), paintSource, MathHelper.getPositionRandom(pos));
    }
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(@Nonnull IBlockState state, @Nonnull World world, @Nonnull RayTraceResult target, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

}
