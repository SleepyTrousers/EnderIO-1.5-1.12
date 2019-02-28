package crazypants.enderio.endergy.init;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.init.IModObjectBase;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelAxe;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelPickaxe;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.endergy.EnderIOEndergy;
import crazypants.enderio.endergy.capacitor.ItemEndergyCapacitor;
import crazypants.enderio.endergy.capacitor.ItemTotemicCapacitor;
import crazypants.enderio.endergy.conduit.ItemEndergyConduit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOEndergy.MODID)
public enum EndergyObject implements IModObjectBase {

  itemCapacitorGrainy(ItemEndergyCapacitor::create_grainy),
  itemCapacitorCrystalline(ItemEndergyCapacitor::create_crystalline),
  itemCapacitorMelodic(ItemEndergyCapacitor::create_melodic),
  itemCapacitorStellar(ItemEndergyCapacitor::create_stellar),
  itemCapacitorTotemic(ItemTotemicCapacitor::create),
  itemEndergyConduit(ItemEndergyConduit::create),
  itemCapacitorSilver(ItemEndergyCapacitor::create_silver),
  itemCapacitorEnergeticSilver(ItemEndergyCapacitor::create_energetic_silver),
  itemCapacitorVivid(ItemEndergyCapacitor::create_vivid),

  // Tools and Armour
  itemStellarAlloySword(ItemDarkSteelSword::createStellarAlloy),
  itemStellarAlloyPickaxe(ItemDarkSteelPickaxe::createStellarAlloy),
  itemStellarAlloyAxe(ItemDarkSteelAxe::createStellarAlloy),
  itemStellarAlloyHelmet(ItemDarkSteelArmor::createStellarAlloyHelmet),
  itemStellarAlloyBoots(ItemDarkSteelArmor::createStellarAlloyBoots),
  itemStellarAlloyChestplate(ItemDarkSteelArmor::createStellarAlloyChestplate),
  itemStellarAlloyLeggings(ItemDarkSteelArmor::createStellarAlloyLeggings),

  ;

  @SubscribeEvent
  public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
    event.register(EndergyObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected @Nullable Block block;
  protected @Nullable Item item;

  protected final @Nullable IModTileEntity modTileEntity;

  protected final @Nullable Function<IModObject, Block> blockMaker;
  protected final @Nullable BiFunction<IModObject, Block, Item> itemMaker;

  private EndergyObject(@Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(null, itemMaker, null);
  }

  private EndergyObject(@Nonnull Function<IModObject, Block> blockMaker) {
    this(blockMaker, null, null);
  }

  private EndergyObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(blockMaker, itemMaker, null);
  }

  private EndergyObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull IModTileEntity modTileEntity) {
    this(blockMaker, null, modTileEntity);
  }

  private EndergyObject(@Nullable Function<IModObject, Block> blockMaker, @Nullable BiFunction<IModObject, Block, Item> itemMaker,
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
  public final @Nullable Block getBlock() {
    return block;
  }

  @Override
  public final @Nullable Item getItem() {
    return item;
  }

  @Override
  public final @Nullable Class<?> getClazz() {
    return null;
  }

  @Override
  public final String getBlockMethodName() {
    return null;
  }

  @Override
  public final String getItemMethodName() {
    return null;
  }

  @Override
  public final void setItem(@Nullable Item obj) {
    item = obj;
  }

  @Override
  public final void setBlock(@Nullable Block obj) {
    block = obj;
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

}
