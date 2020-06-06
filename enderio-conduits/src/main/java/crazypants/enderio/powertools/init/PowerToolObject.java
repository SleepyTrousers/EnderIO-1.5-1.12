package crazypants.enderio.powertools.init;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObjectBase;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.powertools.EnderIOPowerTools;
import crazypants.enderio.powertools.machine.capbank.BlockCapBank;
import crazypants.enderio.powertools.machine.gauge.BlockGauge;
import crazypants.enderio.powertools.machine.monitor.BlockPowerMonitor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOPowerTools.MODID)
public enum PowerToolObject implements IModObjectBase {

  block_cap_bank(BlockCapBank::create, PowerToolTileEntity.TileCapBank),
  block_gauge(BlockGauge::create, PowerToolTileEntity.TileGauge),
  block_power_monitor(BlockPowerMonitor::createPowerMonitor, PowerToolTileEntity.TilePowerMonitor),
  block_advanced_power_monitor(BlockPowerMonitor::createAdvancedPowerMonitor, PowerToolTileEntity.TilePowerMonitor),

  ;

  @SubscribeEvent
  public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
    event.register(PowerToolObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected final @Nullable IModTileEntity modTileEntity;

  protected final @Nullable Function<IModObject, Block> blockMaker;
  protected final @Nullable BiFunction<IModObject, Block, Item> itemMaker;

  private PowerToolObject(@Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(null, itemMaker, null);
  }

  private PowerToolObject(@Nonnull Function<IModObject, Block> blockMaker) {
    this(blockMaker, null, null);
  }

  private PowerToolObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(blockMaker, itemMaker, null);
  }

  private PowerToolObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull IModTileEntity modTileEntity) {
    this(blockMaker, null, modTileEntity);
  }

  private PowerToolObject(@Nullable Function<IModObject, Block> blockMaker, @Nullable BiFunction<IModObject, Block, Item> itemMaker,
      @Nullable IModTileEntity modTileEntity) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.blockMaker = blockMaker;
    this.itemMaker = itemMaker;
    if (blockMaker == null && itemMaker == null) {
      throw new RuntimeException(this + " unexpectedly is neither a Block nor an Item.");
    }
    this.modTileEntity = modTileEntity;
  }

  @Override
  public final @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  @Override
  @Nullable
  public IModTileEntity getTileEntity() {
    return modTileEntity;
  }

  @Override
  public @Nonnull Function<IModObject, Block> getBlockCreator() {
    return blockMaker != null ? blockMaker : mo -> null;
  }

  @Override
  public @Nonnull BiFunction<IModObject, Block, Item> getItemCreator() {
    return NullHelper.first(itemMaker, IModObject.WithBlockItem.itemCreator);
  }

  @Override
  public final @Nonnull <B extends Block> B apply(@Nonnull B blockIn) {
    blockIn.setCreativeTab(EnderIOTab.tabEnderIOConduits);
    return IModObjectBase.super.apply(blockIn);
  }

}
