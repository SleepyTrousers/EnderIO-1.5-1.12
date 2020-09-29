package crazypants.enderio.machines.machine.crafter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.machines.config.config.CrafterConfig;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrafter<T extends TileCrafter> extends AbstractMachineBlock<T>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockCrafter<TileCrafter> create(@Nonnull IModObject modObject) {
    BlockCrafter<TileCrafter> res = new BlockCrafter<TileCrafter>(modObject);
    res.init();
    return res;
  }

  public static BlockCrafter<TileCrafter.Simple> create_simple(@Nonnull IModObject modObject) {
    BlockCrafter<TileCrafter.Simple> res = new BlockCrafter<TileCrafter.Simple>(modObject) {
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
    res.respectsGravity = CrafterConfig.respectsGravity;
    res.init();
    return res;
  }

  protected BlockCrafter(@Nonnull IModObject modObject) {
    super(modObject);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  @Nullable
  public Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1,
      @Nonnull T te) {
    return ContainerCrafter.create(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1,
      @Nonnull T te) {
    return new GuiCrafter(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nonnull
  public IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileCrafter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

}
