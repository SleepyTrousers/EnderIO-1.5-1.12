package crazypants.enderio.powertools.machine.monitor;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.powertools.init.PowerToolObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
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

  public static BlockPowerMonitor advancedInstance;

  public static BlockPowerMonitor createAdvancedPowerMonitor() {
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorGraph.ClientHandler.class, PacketPowerMonitorGraph.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorGraph.ServerHandler.class, PacketPowerMonitorGraph.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorStatData.ClientHandler.class, PacketPowerMonitorStatData.class, PacketHandler.nextID(),
        Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorStatData.ServerHandler.class, PacketPowerMonitorStatData.class, PacketHandler.nextID(),
        Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorConfig.ServerHandler.class, PacketPowerMonitorConfig.class, PacketHandler.nextID(), Side.SERVER);
    advancedInstance = new BlockPowerMonitor(PowerToolObject.block_advanced_power_monitor);
    advancedInstance.init();
    return advancedInstance;
  }

  public static Block createPowerMonitor() {
    BlockPowerMonitor result = new BlockPowerMonitor(PowerToolObject.block_power_monitor);
    result.init();
    return result;
  }

  public BlockPowerMonitor(@Nonnull IModObject mo) {
    super(mo, TilePowerMonitor.class);
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public void getSubBlocks(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    list.add(new ItemStack(this, 1, 0));
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TilePowerMonitor te) {
    return new ContainerPowerMonitor(player.inventory, te);
  }

  @Override
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
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, player, stack);
    TilePowerMonitor te = getTileEntity(world, pos);
    if (te != null) {
      te.setAdvanced(this == advancedInstance);
    }
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
    if (advancedInstance != this) {
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
