package crazypants.enderio.machines.machine.wired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerBlock;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWiredCharger extends AbstractPowerConsumerBlock<TileWiredCharger>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockWiredCharger create(@Nonnull IModObject modObject) {
    BlockWiredCharger res = new BlockWiredCharger(modObject);
    res.init();
    return res;
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
    ClientRegistry.bindTileEntitySpecialRenderer(TileWiredCharger.class, new TESRWiredCharger<>(this));
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

}
