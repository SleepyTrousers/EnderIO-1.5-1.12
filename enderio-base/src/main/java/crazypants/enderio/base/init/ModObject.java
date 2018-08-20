package crazypants.enderio.base.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.block.charge.BlockConcussionCharge;
import crazypants.enderio.base.block.charge.BlockConfusionCharge;
import crazypants.enderio.base.block.charge.BlockEnderCharge;
import crazypants.enderio.base.block.coldfire.BlockColdFire;
import crazypants.enderio.base.block.darksteel.anvil.BlockDarkSteelAnvil;
import crazypants.enderio.base.block.darksteel.bars.BlockDarkIronBars;
import crazypants.enderio.base.block.darksteel.bars.BlockEndIronBars;
import crazypants.enderio.base.block.darksteel.door.BlockDarkSteelDoor;
import crazypants.enderio.base.block.darksteel.ladder.BlockDarkSteelLadder;
import crazypants.enderio.base.block.darksteel.obsidian.BlockReinforcedObsidian;
import crazypants.enderio.base.block.darksteel.trapdoor.BlockDarkSteelTrapDoor;
import crazypants.enderio.base.block.decoration.BlockDecoration;
import crazypants.enderio.base.block.decoration.BlockDecorationFacing;
import crazypants.enderio.base.block.detector.BlockDetector;
import crazypants.enderio.base.block.infinity.BlockInfinity;
import crazypants.enderio.base.block.insulation.BlockIndustrialInsulation;
import crazypants.enderio.base.block.lever.BlockSelfResettingLever;
import crazypants.enderio.base.block.painted.BlockPaintedCarpet;
import crazypants.enderio.base.block.painted.BlockPaintedDoor;
import crazypants.enderio.base.block.painted.BlockPaintedFence;
import crazypants.enderio.base.block.painted.BlockPaintedFenceGate;
import crazypants.enderio.base.block.painted.BlockPaintedGlowstone;
import crazypants.enderio.base.block.painted.BlockPaintedPressurePlate;
import crazypants.enderio.base.block.painted.BlockPaintedRedstone;
import crazypants.enderio.base.block.painted.BlockPaintedSlabManager;
import crazypants.enderio.base.block.painted.BlockPaintedStairs;
import crazypants.enderio.base.block.painted.BlockPaintedStone;
import crazypants.enderio.base.block.painted.BlockPaintedTrapDoor;
import crazypants.enderio.base.block.painted.BlockPaintedWall;
import crazypants.enderio.base.block.painted.BlockPaintedWorkbench;
import crazypants.enderio.base.block.rail.BlockExitRail;
import crazypants.enderio.base.block.skull.BlockEndermanSkull;
import crazypants.enderio.base.capacitor.ItemCapacitor;
import crazypants.enderio.base.conduit.facade.ItemConduitFacade;
import crazypants.enderio.base.filter.fluid.items.ItemFluidFilter;
import crazypants.enderio.base.filter.item.items.ItemBasicItemFilter;
import crazypants.enderio.base.filter.item.items.ItemExistingItemFilter;
import crazypants.enderio.base.filter.item.items.ItemModItemFilter;
import crazypants.enderio.base.filter.item.items.ItemPowerItemFilter;
import crazypants.enderio.base.filter.redstone.items.ItemBasicOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.items.ItemComparatorInputSignalFilter;
import crazypants.enderio.base.filter.redstone.items.ItemCountingOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.items.ItemInvertingOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.items.ItemTimerInputSignalFilter;
import crazypants.enderio.base.filter.redstone.items.ItemToggleOutputSignalFilter;
import crazypants.enderio.base.init.ModObjectData.NamedParameter;
import crazypants.enderio.base.item.coldfire.ItemColdFireIgniter;
import crazypants.enderio.base.item.conduitprobe.ItemConduitProbe;
import crazypants.enderio.base.item.coordselector.ItemCoordSelector;
import crazypants.enderio.base.item.coordselector.ItemLocationPrintout;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelAxe;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelBow;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelPickaxe;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelShears;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelTreetap;
import crazypants.enderio.base.item.darksteel.ItemInventoryCharger;
import crazypants.enderio.base.item.eggs.ItemOwlEgg;
import crazypants.enderio.base.item.enderface.ItemEnderface;
import crazypants.enderio.base.item.magnet.ItemMagnet;
import crazypants.enderio.base.item.rodofreturn.ItemRodOfReturn;
import crazypants.enderio.base.item.soulvial.ItemSoulVial;
import crazypants.enderio.base.item.spawner.ItemBrokenSpawner;
import crazypants.enderio.base.item.staffoflevity.ItemStaffOfLevity;
import crazypants.enderio.base.item.travelstaff.ItemTravelStaff;
import crazypants.enderio.base.item.xptransfer.ItemXpTransfer;
import crazypants.enderio.base.item.yetawrench.ItemYetaWrench;
import crazypants.enderio.base.material.alloy.BlockAlloy;
import crazypants.enderio.base.material.alloy.ItemAlloy;
import crazypants.enderio.base.material.food.ItemEnderFood;
import crazypants.enderio.base.material.glass.BlockFusedQuartz;
import crazypants.enderio.base.material.glass.BlockPaintedFusedQuartz;
import crazypants.enderio.base.material.material.ItemMaterial;
import crazypants.enderio.base.render.dummy.BlockMachineBase;
import crazypants.enderio.base.render.dummy.BlockMachineIO;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import static crazypants.enderio.base.init.ModObjectData.blockMethod;
import static crazypants.enderio.base.init.ModObjectData.clazz;
import static crazypants.enderio.base.init.ModObjectData.itemMethod;
import static crazypants.enderio.base.init.ModObjectData.method;
import static crazypants.enderio.base.init.ModObjectData.tileEntity;
import static crazypants.enderio.base.init.ModTileEntity.TileEntityPaintedBlock;
import static crazypants.enderio.base.init.ModTileEntity.TileEntityTwicePaintedBlock;

public enum ModObject implements IModObjectBase {

  // Dummies
  block_machine_io(BlockMachineIO.class),
  block_machine_base(BlockMachineBase.class),
  itemEnderface(ItemEnderface.class),

  // Conduits
  itemConduitFacade(ItemConduitFacade.class),

  // Materials
  itemBasicCapacitor(ItemCapacitor.class),
  blockAlloy(BlockAlloy.class),
  itemAlloyIngot(ItemAlloy.class),
  itemAlloyNugget(ItemAlloy.class),
  itemAlloyBall(ItemAlloy.class),
  itemMaterial(ItemMaterial.class),

  itemBrokenSpawner(ItemBrokenSpawner.class),
  block_infinity_fog(BlockInfinity.class),

  // Blocks
  blockColdFire(BlockColdFire.class),
  blockDarkSteelAnvil(BlockDarkSteelAnvil.class),
  blockDarkSteelLadder(BlockDarkSteelLadder.class),
  blockDarkIronBars(BlockDarkIronBars.class),
  blockDarkSteelTrapdoor(BlockDarkSteelTrapDoor.class),
  blockDarkSteelDoor(BlockDarkSteelDoor.class),
  blockReinforcedObsidian(BlockReinforcedObsidian.class),
  blockSelfResettingLever5(BlockSelfResettingLever.class, "create5"),
  blockSelfResettingLever10(BlockSelfResettingLever.class, "create10"),
  blockSelfResettingLever30(BlockSelfResettingLever.class, "create30"),
  blockSelfResettingLever60(BlockSelfResettingLever.class, "create60"),
  blockSelfResettingLever300(BlockSelfResettingLever.class, "create300"),
  blockDecoration1(BlockDecoration.class),
  blockDecoration2(BlockDecorationFacing.class),
  blockDecoration3(BlockDecorationFacing.class, "create2"),
  blockIndustrialInsulation(BlockIndustrialInsulation.class),
  blockEndIronBars(BlockEndIronBars.class),

  // Charges
  blockConfusionCharge(BlockConfusionCharge.class),
  blockConcussionCharge(BlockConcussionCharge.class),
  blockEnderCharge(BlockEnderCharge.class),

  // Painter
  blockPaintedFence(BlockPaintedFence.class, ModTileEntity.TileEntityPaintedBlock),
  blockPaintedStoneFence(BlockPaintedFence.class, "create_stone", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedFenceGate(BlockPaintedFenceGate.class, ModTileEntity.TileEntityPaintedBlock),
  blockPaintedWall(BlockPaintedWall.class, ModTileEntity.TileEntityPaintedBlock),
  blockPaintedStair(BlockPaintedStairs.class, ModTileEntity.TileEntityPaintedBlock),
  blockPaintedStoneStair(BlockPaintedStairs.class, "create_stone", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedSlab(clazz(BlockPaintedSlabManager.class), blockMethod("create_wood"), itemMethod("create_item"), tileEntity(TileEntityPaintedBlock)),
  blockPaintedDoubleSlab(clazz(BlockPaintedSlabManager.class), blockMethod("create_wood_double"), tileEntity(TileEntityTwicePaintedBlock)),
  blockPaintedStoneSlab(clazz(BlockPaintedSlabManager.class), blockMethod("create_stone"), itemMethod("create_item"), tileEntity(TileEntityPaintedBlock)),
  blockPaintedStoneDoubleSlab(clazz(BlockPaintedSlabManager.class), blockMethod("create_stone_double"), tileEntity(TileEntityTwicePaintedBlock)),
  blockPaintedGlowstone(BlockPaintedGlowstone.class, ModTileEntity.TileEntityPaintedBlock),
  blockPaintedGlowstoneSolid(BlockPaintedGlowstone.class, "create_solid", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedCarpet(BlockPaintedCarpet.class, ModTileEntity.TileEntityPaintedBlock),
  blockPaintedPressurePlate(BlockPaintedPressurePlate.class, ModTileEntity.TilePaintedPressurePlate),
  blockPaintedRedstone(BlockPaintedRedstone.class, ModTileEntity.TileEntityPaintedBlock),
  blockPaintedRedstoneSolid(BlockPaintedRedstone.class, "create_solid", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedStone(BlockPaintedStone.class, ModTileEntity.TileEntityPaintedBlock),
  blockPaintedWoodenTrapdoor(BlockPaintedTrapDoor.class, "create_wooden", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedIronTrapdoor(BlockPaintedTrapDoor.class, "create_iron", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedDarkSteelTrapdoor(BlockPaintedTrapDoor.class, "create_dark", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedWoodenDoor(BlockPaintedDoor.class, "create_wooden", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedIronDoor(BlockPaintedDoor.class, "create_iron", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedDarkSteelDoor(BlockPaintedDoor.class, "create_dark", ModTileEntity.TileEntityPaintedBlock),
  blockPaintedWorkbench(BlockPaintedWorkbench.class, ModTileEntity.TileEntityPaintedBlock),

  blockExitRail(BlockExitRail.class),

  itemConduitProbe(ItemConduitProbe.class),
  itemYetaWrench(ItemYetaWrench.class),
  itemXpTransfer(ItemXpTransfer.class),
  itemColdFireIgniter(ItemColdFireIgniter.class),

  itemCoordSelector(ItemCoordSelector.class),
  itemLocationPrintout(ItemLocationPrintout.class),
  itemTravelStaff(ItemTravelStaff.class),
  itemRodOfReturn(ItemRodOfReturn.class),
  itemMagnet(ItemMagnet.class),
  blockEndermanSkull(BlockEndermanSkull.class, ModTileEntity.TileEndermanSkull),
  itemEnderFood(ItemEnderFood.class),

  // Filters
  itemBasicItemFilter(ItemBasicItemFilter.class, "createBasicItemFilter"),
  itemAdvancedItemFilter(ItemBasicItemFilter.class, "createAdvancedItemFilter"),
  itemLimitedItemFilter(ItemBasicItemFilter.class, "createLimitedItemFilter"),
  itemBigItemFilter(ItemBasicItemFilter.class, "createBigItemFilter"),
  itemBigAdvancedItemFilter(ItemBasicItemFilter.class, "createBigAdvancedItemFilter"),
  itemExistingItemFilter(ItemExistingItemFilter.class),
  itemModItemFilter(ItemModItemFilter.class),
  itemPowerItemFilter(ItemPowerItemFilter.class),

  itemFluidFilter(ItemFluidFilter.class),

  itemRedstoneNotFilter(ItemInvertingOutputSignalFilter.class),
  itemRedstoneOrFilter(ItemBasicOutputSignalFilter.class, "createOr"),
  itemRedstoneAndFilter(ItemBasicOutputSignalFilter.class, "createAnd"),
  itemRedstoneNorFilter(ItemBasicOutputSignalFilter.class, "createNor"),
  itemRedstoneNandFilter(ItemBasicOutputSignalFilter.class, "createNand"),
  itemRedstoneXorFilter(ItemBasicOutputSignalFilter.class, "createXor"),
  itemRedstoneXnorFilter(ItemBasicOutputSignalFilter.class, "createXnor"),
  itemRedstoneToggleFilter(ItemToggleOutputSignalFilter.class),
  itemRedstoneCountingFilter(ItemCountingOutputSignalFilter.class),

  itemRedstoneSensorFilter(ItemComparatorInputSignalFilter.class),
  itemRedstoneTimerFilter(ItemTimerInputSignalFilter.class),

  blockFusedQuartz(BlockFusedQuartz.class, "createFusedQuartz"),
  blockFusedGlass(BlockFusedQuartz.class, "createFusedGlass"),
  blockEnlightenedFusedQuartz(BlockFusedQuartz.class, "createEnlightenedFusedQuartz"),
  blockEnlightenedFusedGlass(BlockFusedQuartz.class, "createEnlightenedFusedGlass"),
  blockDarkFusedQuartz(BlockFusedQuartz.class, "createDarkFusedQuartz"),
  blockDarkFusedGlass(BlockFusedQuartz.class, "createDarkFusedGlass"),
  blockPaintedFusedQuartz(BlockPaintedFusedQuartz.class, ModTileEntity.TileEntityPaintedBlock),

  itemSoulVial(ItemSoulVial.class),

  block_detector_block(BlockDetector.class, ModTileEntity.TileEntityPaintedBlock),
  block_detector_block_silent(BlockDetector.class, "createSilent", ModTileEntity.TileEntityPaintedBlock),

  itemDarkSteelHelmet(ItemDarkSteelArmor.class, "createDarkSteelHelmet"),
  itemDarkSteelChestplate(ItemDarkSteelArmor.class, "createDarkSteelChestplate"),
  itemDarkSteelLeggings(ItemDarkSteelArmor.class, "createDarkSteelLeggings"),
  itemDarkSteelBoots(ItemDarkSteelArmor.class, "createDarkSteelBoots"),
  itemDarkSteelSword(ItemDarkSteelSword.class, "createDarkSteel"),
  itemDarkSteelPickaxe(ItemDarkSteelPickaxe.class, "createDarkSteel"),
  itemDarkSteelAxe(ItemDarkSteelAxe.class, "createDarkSteel"),
  itemDarkSteelBow(ItemDarkSteelBow.class, "createDarkSteel"),
  itemDarkSteelShears(ItemDarkSteelShears.class),
  itemDarkSteelTreetap(ItemDarkSteelTreetap.class),
  itemInventoryChargerSimple(ItemInventoryCharger.class, "createSimple"),
  itemInventoryChargerBasic(ItemInventoryCharger.class, "createBasic"),
  itemInventoryCharger(ItemInventoryCharger.class),
  itemInventoryChargerVibrant(ItemInventoryCharger.class, "createVibrant"),
  itemEndSteelSword(ItemDarkSteelSword.class, "createEndSteel"),
  itemEndSteelPickaxe(ItemDarkSteelPickaxe.class, "createEndSteel"),
  itemEndSteelAxe(ItemDarkSteelAxe.class, "createEndSteel"),
  itemEndSteelBow(ItemDarkSteelBow.class, "createEndSteel"),

  itemEndSteelHelmet(ItemDarkSteelArmor.class, "createEndSteelHelmet"),
  itemEndSteelChestplate(ItemDarkSteelArmor.class, "createEndSteelChestplate"),
  itemEndSteelLeggings(ItemDarkSteelArmor.class, "createEndSteelLeggings"),
  itemEndSteelBoots(ItemDarkSteelArmor.class, "createEndSteelBoots"),

  itemStaffOfLevity(ItemStaffOfLevity.class),

  item_owl_egg(ItemOwlEgg.class),

  ;

  protected @Nullable Block block;
  protected @Nullable Item item;

  protected final @Nonnull ModObjectData data;

  /*
   * A modObject can be defined in a couple of different ways.
   * 
   * It always needs a class.
   * 
   * If there is no creation method name given, the method name "create" is used and it is auto-detected from the class if it is a Block or an Item.
   * 
   * If only one method name is given, the same auto-detection is used.
   * 
   * If two method names are given, one is used to create a block and the second one to create the blockItem.
   * 
   * If it is a Block and does not have the second method name, but implements IModObject.WithBlockItem, that interface is used to create the blockItem.
   * 
   * It can also have a TileEntity class. It it is given, it is registered automatially. Multiple blocks can use the same class, the registration is
   * automatically de-duped.
   * 
   * 
   * Please note that it is not recommended to override the lifecycle methods to add callbacks to the block/item code. Doing so will classload that Block/Item
   * together with the ModObject enum, which can cause weird errors. Implement the IModObject lifecycle interfaces on the block/item instead.
   */

  private ModObject(NamedParameter<?>... params) {
    data = ModObjectData.create(ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()")), params);
  }

  private ModObject(@Nonnull Class<?> clazz) {
    this(clazz(clazz), method("create"));
  }

  private ModObject(@Nonnull Class<?> clazz, @Nonnull IModTileEntity modTileEntity) {
    this(clazz(clazz), method("create"), tileEntity(modTileEntity));
  }

  private ModObject(@Nonnull Class<?> clazz, @Nonnull String methodName) {
    this(clazz(clazz), method(methodName));
  }

  private ModObject(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nonnull IModTileEntity modTileEntity) {
    this(clazz(clazz), method(methodName), tileEntity(modTileEntity));
  }

  @Override
  public final @Nonnull String getUnlocalisedName() {
    return data.getUnlocalisedName();
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
  public final @Nonnull Class<?> getClazz() {
    return data.getClazz();
  }

  @Override
  public final String getBlockMethodName() {
    return data.getBlockMethodName();
  }

  @Override
  public final String getItemMethodName() {
    return data.getItemMethodName();
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
    return data.getModTileEntity();
  }

}
