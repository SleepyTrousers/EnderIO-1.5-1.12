package crazypants.enderio.powertools.machine.monitor;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPowerMonitor extends AbstractMachineBlock<TilePowerMonitor>
    implements IAdvancedTooltipProvider, IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  private final boolean advanced;

  public static BlockPowerMonitor createAdvancedPowerMonitor(@Nonnull IModObject modObject) {
    BlockPowerMonitor result = new BlockPowerMonitor(modObject, true);
    result.init();
    return result;
  }

  public static Block createPowerMonitor(@Nonnull IModObject modObject) {
    BlockPowerMonitor result = new BlockPowerMonitor(modObject, false);
    result.init();
    return result;
  }

  public BlockPowerMonitor(@Nonnull IModObject mo, boolean advanced) {
    super(mo);
    this.advanced = advanced;
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    list.add(new ItemStack(this, 1, 0));
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TilePowerMonitor te) {
    return new ContainerPowerMonitor(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TilePowerMonitor te) {
    return new GuiPowerMonitor(player.inventory, te);
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TilePowerMonitor tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }

  @Override
  public void onBlockPlaced(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull TilePowerMonitor te) {
    super.onBlockPlaced(world, pos, state, player, te);
    te.setAdvanced(advanced);
  }

  @Deprecated
  @Override
  public int getWeakPower(@Nonnull IBlockState blockStateIn, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    TilePowerMonitor te = getTileEntity(blockAccess, pos);
    if (te != null) {
      return te.getRedstoneLevel();
    }
    return super.getWeakPower(blockStateIn, blockAccess, pos, side);
  }

  @Override
  public boolean canProvidePower(@Nonnull IBlockState state) {
    return true;
  }

  @Override
  public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    TilePowerMonitor te = getTileEntitySafe(world, pos);
    if (te != null) {
      return te.isEngineControlEnabled();
    }
    return super.canConnectRedstone(state, world, pos, side);
  }

  @Deprecated
  @Override
  public boolean shouldSideBeRendered(@Nonnull IBlockState bs, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    if (!super.shouldSideBeRendered(bs, worldIn, pos, side)) {
      return false;
    }
    if (!advanced) {
      return true;
    }
    TilePowerMonitor tileEntity = getTileEntitySafe(worldIn, pos);
    if (tileEntity == null) {
      return true;
    }
    if (tileEntity.getFacing() != side) {
      return true;
    }
    if (tileEntity.getPaintSource() == null) {
      return true;
    }
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TilePowerMonitor.class, new TESRPowerMonitor());
  }

}
