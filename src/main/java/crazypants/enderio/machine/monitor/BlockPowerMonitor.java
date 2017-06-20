package crazypants.enderio.machine.monitor;


import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import crazypants.enderio.GuiID;
import crazypants.enderio.IModObject;
import crazypants.enderio.ModObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockPowerMonitor extends AbstractMachineBlock<TilePowerMonitor> implements IAdvancedTooltipProvider, IPaintable.ISolidBlockPaintableBlock,
    IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockPowerMonitor advancedInstance;

  public static BlockPowerMonitor createAdvancedPowerMonitor() {
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorGraph.ClientHandler.class, PacketPowerMonitorGraph.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorGraph.ServerHandler.class, PacketPowerMonitorGraph.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorStatData.ClientHandler.class, PacketPowerMonitorStatData.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorStatData.ServerHandler.class, PacketPowerMonitorStatData.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerMonitorConfig.ServerHandler.class, PacketPowerMonitorConfig.class, PacketHandler.nextID(), Side.SERVER);
    advancedInstance = new BlockPowerMonitor(ModObject.blockPowerMonitorv2) {
      @Override
      protected GuiID getGuiId() {
        return GuiID.GUI_ID_POWER_MONITOR_ADVANCED;
      }
    };
    advancedInstance.init();
    return advancedInstance;
  }

  public static Block createPowerMonitor() {
    BlockPowerMonitor result = new BlockPowerMonitor(ModObject.blockPowerMonitor);
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
    list.add(new ItemStack(this, 1, 0));
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (world == null) {
      return null;
    }
    TileEntity te = getTileEntity(world, new BlockPos(x, y, z));
    if (!(te instanceof TilePowerMonitor)) {
      return null;
    }
    return new ContainerPowerMonitor(player.inventory, (TilePowerMonitor) te);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (world != null) {
      TileEntity te = getTileEntity(world, new BlockPos(x, y, z));
      final InventoryPlayer inventory = player.inventory;
      if (te instanceof TilePowerMonitor && inventory != null) {
        return new GuiPowerMonitor(inventory, (TilePowerMonitor) te);
      }
    }
    return null;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_POWER_MONITOR;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TilePowerMonitor tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, player, stack);
    if (world != null && pos != null) {
      TilePowerMonitor te = getTileEntity(world, pos);
      if (te != null) {
        te.setAdvanced(this == advancedInstance);
      }
    }
  }

  @Deprecated
  @Override
  public int getWeakPower(IBlockState blockStateIn, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    if (blockAccess != null && pos != null) {
      TilePowerMonitor te = getTileEntity(blockAccess, pos);
      if (te != null) {
        return te.getRedstoneLevel();
      }
    }
    return super.getWeakPower(blockStateIn, blockAccess, pos, side);
  }

  @Override
  public boolean canProvidePower(IBlockState state) {
    return true;
  }

  @Override
  public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    if (world != null && pos != null) {
      TilePowerMonitor te = getTileEntitySafe(world, pos);
      if (te != null) {
        return te.isEngineControlEnabled();
      }
    }
    return super.canConnectRedstone(state, world, pos, side);
  }

  @Deprecated
  @Override
  public boolean shouldSideBeRendered(IBlockState bs, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    if (!super.shouldSideBeRendered(bs, worldIn, pos, side)) {
      return false;
    }
    if (advancedInstance != this || worldIn == null || pos == null) {
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
