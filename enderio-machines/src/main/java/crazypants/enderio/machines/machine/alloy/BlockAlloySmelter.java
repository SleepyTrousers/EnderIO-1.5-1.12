package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.base.block.BlockMachineExtension;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.machines.config.config.AlloySmelterConfig;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.gui.GuiScreen;
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
    res.respectsGravity = AlloySmelterConfig.respectsGravity;
    res.init();
    return res;
  }

  public static BlockAlloySmelter<TileAlloySmelter.Furnace> create_furnace(@Nonnull IModObject modObject) {
    BlockAlloySmelter<TileAlloySmelter.Furnace> res = new BlockAlloySmelter<TileAlloySmelter.Furnace>(modObject) {
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
    return GuiAlloySmelter.create(player.inventory, te);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileAlloySmelter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

  @Nullable
  @Override
  public Block getEnhancedExtensionBlock() {
    return MachineObject.block_enhanced_alloy_smelter_top.getBlockNN();
  }
}
