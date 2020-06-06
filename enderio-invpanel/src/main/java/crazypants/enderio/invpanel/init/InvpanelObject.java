package crazypants.enderio.invpanel.init;

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
import crazypants.enderio.invpanel.EnderIOInvPanel;
import crazypants.enderio.invpanel.chest.BlockInventoryChest;
import crazypants.enderio.invpanel.conduit.data.ItemDataConduit;
import crazypants.enderio.invpanel.invpanel.BlockInventoryPanel;
import crazypants.enderio.invpanel.remote.ItemRemoteInvAccess;
import crazypants.enderio.invpanel.sensor.BlockInventoryPanelSensor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOInvPanel.MODID)
public enum InvpanelObject implements IModObjectBase {

  blockInventoryPanel(BlockInventoryPanel::create, InvpanelTileEntity.TileInventoryPanel),
  blockInventoryPanelSensor(BlockInventoryPanelSensor::create, InvpanelTileEntity.TileInventoryPanelSensor),

  // Warehouses
  blockInventoryChestTiny(BlockInventoryChest::create_simple, InvpanelTileEntity.TileInventoryChestTiny),
  blockInventoryChestSmall(BlockInventoryChest::create_simple, InvpanelTileEntity.TileInventoryChestSmall),
  blockInventoryChestMedium(BlockInventoryChest::create_simple, InvpanelTileEntity.TileInventoryChestMedium),
  blockInventoryChestBig(BlockInventoryChest::create, InvpanelTileEntity.TileInventoryChestBig),
  blockInventoryChestLarge(BlockInventoryChest::create, InvpanelTileEntity.TileInventoryChestLarge),
  blockInventoryChestHuge(BlockInventoryChest::create, InvpanelTileEntity.TileInventoryChestHuge),
  blockInventoryChestEnormous(BlockInventoryChest::create_enhanced, InvpanelTileEntity.TileInventoryChestEnormous),
  blockInventoryChestWarehouse(BlockInventoryChest::create_enhanced, InvpanelTileEntity.TileInventoryChestWarehouse),
  blockInventoryChestWarehouse13(BlockInventoryChest::create_enhanced, InvpanelTileEntity.TileInventoryChestWarehouse13),

  // Remotes
  itemInventoryRemote(ItemRemoteInvAccess::create),

  // Conduits
  item_data_conduit(ItemDataConduit::create);

  ;

  @SubscribeEvent
  public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
    event.register(InvpanelObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected final @Nullable IModTileEntity modTileEntity;

  protected final @Nullable Function<IModObject, Block> blockMaker;
  protected final @Nullable BiFunction<IModObject, Block, Item> itemMaker;

  private InvpanelObject(@Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(null, itemMaker, null);
  }

  private InvpanelObject(@Nonnull Function<IModObject, Block> blockMaker) {
    this(blockMaker, null, null);
  }

  private InvpanelObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(blockMaker, itemMaker, null);
  }

  private InvpanelObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull IModTileEntity modTileEntity) {
    this(blockMaker, null, modTileEntity);
  }

  private InvpanelObject(@Nullable Function<IModObject, Block> blockMaker, @Nullable BiFunction<IModObject, Block, Item> itemMaker,
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
    blockIn.setCreativeTab(EnderIOTab.tabEnderIOInvpanel);
    return IModObjectBase.super.apply(blockIn);
  }

}
