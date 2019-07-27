package crazypants.enderio.machines.machine.wired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import crazypants.enderio.base.machine.base.block.BlockMachineExtension;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWiredCharger<T extends TileWiredCharger> extends AbstractPowerConsumerBlock<T>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockWiredCharger<TileWiredCharger> create(@Nonnull IModObject modObject) {
    BlockWiredCharger<TileWiredCharger> res = new BlockWiredCharger<TileWiredCharger>(modObject);
    res.init();
    return res;
  }

  public static BlockWiredCharger<TileWiredCharger.Enhanced> create_enhanced(@Nonnull IModObject modObject) {
    BlockWiredCharger<TileWiredCharger.Enhanced> res = new BlockWiredCharger<TileWiredCharger.Enhanced>(modObject) {
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

      @Override
      @SideOnly(Side.CLIENT)
      public void bindTileEntitySpecialRenderer() {
      }

    };
    res.isEnhanced = true;
    res.init();
    return res;
  }

  public static BlockWiredCharger<TileWiredCharger.Simple> create_simple(@Nonnull IModObject modObject) {
    BlockWiredCharger<TileWiredCharger.Simple> res = new BlockWiredCharger<TileWiredCharger.Simple>(modObject) {
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

      @Override
      @SideOnly(Side.CLIENT)
      public void bindTileEntitySpecialRenderer() {
      }

    };
    res.init();
    return res;
  }

  public static BlockMachineExtension create_extension(@Nonnull IModObject modObject) {
    return new BlockMachineExtension(modObject, MachineObject.block_enhanced_wired_charger, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 11D / 16D, 1.0D));
  }

  private BlockWiredCharger(@Nonnull IModObject modObject) {
    super(modObject);
    setShape(mkShape(BlockFaceShape.SOLID, BlockFaceShape.SOLID, BlockFaceShape.UNDEFINED, BlockFaceShape.SOLID));
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileWiredCharger te) {
    return ContainerWiredCharger.create(player.inventory, te, param1);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileWiredCharger te) {
    return new GuiWiredCharger(player.inventory, te, param1);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileWiredCharger tileEntity) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileWiredCharger.class, new TESRWiredCharger<>(null));
  }

  @Override
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    return openGui(world, pos, entityPlayer, side, baublesToGuiId(BaublesUtil.instance().getBaubles(entityPlayer)));
  }

  private static int baublesToGuiId(IInventory baubles) {
    if (baubles != null) {
      return baubles.getSizeInventory();
    } else {
      return 0;
    }
  }

  @Nullable
  @Override
  public Block getEnhancedExtensionBlock() {
    return MachineObject.block_enhanced_wired_charger_top.getBlockNN();
  }

}
