package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.BlockMachineExtension;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAlloySmelter<T extends TileAlloySmelter> extends AbstractPoweredTaskBlock<T>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  private boolean isEnhanced = false;

  public static BlockAlloySmelter<TileAlloySmelter> create(@Nonnull IModObject modObject) {
    BlockAlloySmelter<TileAlloySmelter> res = new BlockAlloySmelter<TileAlloySmelter>(modObject);
    res.init();
    return res;
  }

  public static BlockAlloySmelter<TileAlloySmelter.Simple> create_simple(@Nonnull IModObject modObject) {
    BlockAlloySmelter<TileAlloySmelter.Simple> res = new BlockAlloySmelter<TileAlloySmelter.Simple>(modObject) {
      @Override
      @SideOnly(Side.CLIENT)
      public @Nonnull IRenderMapper.IItemRenderMapper getItemRenderMapper() {
        return RenderMappers.SIMPLE_BODY_MAPPER;
      }

      @Override
      @SideOnly(Side.CLIENT)
      public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
        return RenderMappers.SIMPLE_BODY_MAPPER;
      }
    };
    res.init();
    return res;
  }

  public static BlockAlloySmelter<TileAlloySmelter> create_enhanced(@Nonnull IModObject modObject) {
    BlockAlloySmelter<TileAlloySmelter> res = new BlockAlloySmelter<TileAlloySmelter>(modObject) {
      @Override
      @SideOnly(Side.CLIENT)
      public @Nonnull IRenderMapper.IItemRenderMapper getItemRenderMapper() {
        return RenderMappers.ENHANCED_BODY_MAPPER;
      }

      @Override
      @SideOnly(Side.CLIENT)
      public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
        return RenderMappers.ENHANCED_BODY_MAPPER;
      }
    };
    res.isEnhanced = true;
    res.init();
    return res;
  }

  public static BlockMachineExtension create_extension(@Nonnull IModObject modObject) {
    return new BlockMachineExtension(modObject, MachineObject.block_enhanced_alloy_smelter, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 11D / 16D, 1.0D));
  }

  public static final TextureSupplier vanillaSmeltingOn = TextureRegistry.registerTexture("blocks/furnace_smelting_on");
  public static final TextureSupplier vanillaSmeltingOff = TextureRegistry.registerTexture("blocks/furnace_smelting_off");
  public static final TextureSupplier vanillaSmeltingOnly = TextureRegistry.registerTexture("blocks/furnace_smelting_only");

  protected BlockAlloySmelter(@Nonnull IModObject modObject) {
    super(modObject);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull T te) {
    return ContainerAlloySmelter.create(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull T te) {
    return new GuiAlloySmelter<T>(player.inventory, te);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileAlloySmelter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

  @Override
  public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos) {
    return super.canPlaceBlockAt(world, pos) && (!isEnhanced || (pos.getY() < 255 && super.canPlaceBlockAt(world, pos.up())));
  }

  @Override
  public void onBlockPlaced(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player, @Nonnull T te) {
    super.onBlockPlaced(world, pos, state, player, te);
    if (isEnhanced) {
      world.setBlockState(pos.up(), MachineObject.block_enhanced_alloy_smelter_top.getBlockNN().getDefaultState());
    }
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    if (isEnhanced) {
      if (world.getBlockState(pos.up()).getBlock() != MachineObject.block_enhanced_alloy_smelter_top.getBlockNN()) {
        if (super.canPlaceBlockAt(world, pos.up())) {
          world.setBlockState(pos.up(), MachineObject.block_enhanced_alloy_smelter_top.getBlockNN().getDefaultState());
        } else {
          // impossible error state a.k.a. someone ripped the machine apart. And what do combustion engines that are ripped apart do? They combust. Violently.
          world.createExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 3f, true); // 3 == normal Creeper
        }
      }
    }

    super.neighborChanged(state, world, pos, blockIn, fromPos);
  }

}
