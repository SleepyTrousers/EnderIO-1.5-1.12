package crazypants.enderio.conduits.init;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.base.init.IModObjectBase;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.base.registry.Registry;
import crazypants.enderio.conduits.EnderIOConduits;
import crazypants.enderio.conduits.conduit.BlockConduitBundle;
import crazypants.enderio.conduits.conduit.item.ItemItemConduit;
import crazypants.enderio.conduits.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduits.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduits.conduit.redstone.ItemRedstoneConduit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODID)
public enum ConduitObject implements IModObjectBase {

  // Conduits
  block_conduit_bundle(BlockConduitBundle::create, ConduitTileEntity.TileConduitBundle),

  item_item_conduit(ItemItemConduit::create),
  item_liquid_conduit(ItemLiquidConduit::create),
  item_power_conduit(ItemPowerConduit::create),
  item_redstone_conduit(ItemRedstoneConduit::create),

  // Function Upgrades
  item_extract_speed_upgrade(ItemFunctionUpgrade::createUpgrade),
  item_extract_speed_downgrade(ItemFunctionUpgrade::createDowngrade),

  ;

  @SubscribeEvent
  public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
    Registry.registerConduitBlock(block_conduit_bundle);
    event.register(ConduitObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected final @Nullable IModTileEntity modTileEntity;

  protected final @Nullable Function<IModObject, Block> blockMaker;
  protected final @Nullable BiFunction<IModObject, Block, Item> itemMaker;

  private ConduitObject(@Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(null, itemMaker, null);
  }

  private ConduitObject(@Nonnull Function<IModObject, Block> blockMaker) {
    this(blockMaker, null, null);
  }

  private ConduitObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(blockMaker, itemMaker, null);
  }

  private ConduitObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull IModTileEntity modTileEntity) {
    this(blockMaker, null, modTileEntity);
  }

  private ConduitObject(@Nullable Function<IModObject, Block> blockMaker, @Nullable BiFunction<IModObject, Block, Item> itemMaker,
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
