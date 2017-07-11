package crazypants.enderio.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIO;
import crazypants.enderio.block.coldfire.BlockColdFire;
import crazypants.enderio.block.darksteel.anvil.BlockDarkSteelAnvil;
import crazypants.enderio.block.darksteel.bars.BlockDarkIronBars;
import crazypants.enderio.block.darksteel.ladder.BlockDarkSteelLadder;
import crazypants.enderio.block.darksteel.obsidian.BlockReinforcedObsidian;
import crazypants.enderio.block.darksteel.trapdoor.BlockDarkSteelTrapDoor;
import crazypants.enderio.block.decoration.BlockDecoration;
import crazypants.enderio.block.decoration.BlockDecorationFacing;
import crazypants.enderio.block.detector.BlockDetector;
import crazypants.enderio.block.lever.BlockSelfResettingLever;
import crazypants.enderio.block.painted.BlockPaintedCarpet;
import crazypants.enderio.block.painted.BlockPaintedFence;
import crazypants.enderio.block.painted.BlockPaintedFenceGate;
import crazypants.enderio.block.painted.BlockPaintedGlowstone;
import crazypants.enderio.block.painted.BlockPaintedPressurePlate;
import crazypants.enderio.block.painted.BlockPaintedRedstone;
import crazypants.enderio.block.painted.BlockPaintedSlabManager;
import crazypants.enderio.block.painted.BlockPaintedStairs;
import crazypants.enderio.block.painted.BlockPaintedStone;
import crazypants.enderio.block.painted.BlockPaintedTrapDoor;
import crazypants.enderio.block.painted.BlockPaintedWall;
import crazypants.enderio.block.painted.TileEntityPaintedBlock;
import crazypants.enderio.block.painted.TileEntityTwicePaintedBlock;
import crazypants.enderio.block.painted.TilePaintedPressurePlate;
import crazypants.enderio.block.rail.BlockExitRail;
import crazypants.enderio.block.skull.BlockEndermanSkull;
import crazypants.enderio.capacitor.ItemCapacitor;
import crazypants.enderio.conduit.facade.ItemConduitFacade;
import crazypants.enderio.filter.items.ItemBasicItemFilter;
import crazypants.enderio.item.coldfire.ItemColdFireIgniter;
import crazypants.enderio.item.conduitprobe.ItemConduitProbe;
import crazypants.enderio.item.coordselector.ItemCoordSelector;
import crazypants.enderio.item.coordselector.ItemLocationPrintout;
import crazypants.enderio.item.darksteel.ItemDarkSteelArmor;
import crazypants.enderio.item.darksteel.ItemDarkSteelAxe;
import crazypants.enderio.item.darksteel.ItemDarkSteelBow;
import crazypants.enderio.item.darksteel.ItemDarkSteelPickaxe;
import crazypants.enderio.item.darksteel.ItemDarkSteelShears;
import crazypants.enderio.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.item.enderface.ItemEnderface;
import crazypants.enderio.item.magnet.ItemMagnet;
import crazypants.enderio.item.rodofreturn.ItemRodOfReturn;
import crazypants.enderio.item.soulvial.ItemSoulVial;
import crazypants.enderio.item.spawner.ItemBrokenSpawner;
import crazypants.enderio.item.travelstaff.ItemTravelStaff;
import crazypants.enderio.item.xptransfer.ItemXpTransfer;
import crazypants.enderio.item.yetawrench.ItemYetaWrench;
import crazypants.enderio.material.alloy.BlockAlloy;
import crazypants.enderio.material.alloy.ItemAlloy;
import crazypants.enderio.material.food.ItemEnderFood;
import crazypants.enderio.material.glass.BlockFusedQuartz;
import crazypants.enderio.material.glass.BlockPaintedFusedQuartz;
import crazypants.enderio.material.material.ItemMaterial;
import crazypants.enderio.render.dummy.BlockMachineBase;
import crazypants.enderio.render.dummy.BlockMachineIO;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public enum ModObject implements IModObject.Registerable {

  block_machine_io(BlockMachineIO.class),
  block_machine_base(BlockMachineBase.class),

  // Enderface
  itemEnderface(ItemEnderface.class),

  // Conduits
  itemConduitFacade(ItemConduitFacade.class),

  // Materials
  itemBasicCapacitor(ItemCapacitor.class),
  blockAlloy(BlockAlloy.class),
  itemAlloyIngot(ItemAlloy.class),
  itemAlloyNugget(ItemAlloy.class),
  itemMaterial(ItemMaterial.class),

  itemBrokenSpawner(ItemBrokenSpawner.class),

  // Blocks
  blockColdFire(BlockColdFire.class),
  blockDarkSteelAnvil(BlockDarkSteelAnvil.class),
  blockDarkSteelLadder(BlockDarkSteelLadder.class),
  blockDarkIronBars(BlockDarkIronBars.class),
  blockDarkSteelTrapdoor(BlockDarkSteelTrapDoor.class),
  blockReinforcedObsidian(BlockReinforcedObsidian.class),
  blockSelfResettingLever5(BlockSelfResettingLever.class, "create5"),
  blockSelfResettingLever10(BlockSelfResettingLever.class, "create10"),
  blockSelfResettingLever30(BlockSelfResettingLever.class, "create30"),
  blockSelfResettingLever60(BlockSelfResettingLever.class, "create60"),
  blockSelfResettingLever300(BlockSelfResettingLever.class, "create300"),
  blockDecoration1(BlockDecoration.class),
  blockDecoration2(BlockDecorationFacing.class),

  // Painter
  blockPaintedFence(BlockPaintedFence.class),
  blockPaintedStoneFence(BlockPaintedFence.class, "create_stone"),
  blockPaintedFenceGate(BlockPaintedFenceGate.class),
  blockPaintedWall(BlockPaintedWall.class),
  blockPaintedStair(BlockPaintedStairs.class),
  blockPaintedStoneStair(BlockPaintedStairs.class, "create_stone"),
  blockPaintedSlab(BlockPaintedSlabManager.class, "create_wood", "create_item"),
  blockPaintedDoubleSlab(BlockPaintedSlabManager.class, "create_wood_double", "", TileEntityTwicePaintedBlock.class),
  blockPaintedStoneSlab(BlockPaintedSlabManager.class, "create_stone", "create_item"),
  blockPaintedStoneDoubleSlab(BlockPaintedSlabManager.class, "create_stone_double", "", TileEntityTwicePaintedBlock.class),
  blockPaintedGlowstone(BlockPaintedGlowstone.class),
  blockPaintedGlowstoneSolid(BlockPaintedGlowstone.class, "create_solid"),
  blockPaintedCarpet(BlockPaintedCarpet.class, TileEntityPaintedBlock.class),
  blockPaintedPressurePlate(BlockPaintedPressurePlate.class, TilePaintedPressurePlate.class),
  blockPaintedRedstone(BlockPaintedRedstone.class),
  blockPaintedRedstoneSolid(BlockPaintedRedstone.class, "create_solid"),
  blockPaintedStone(BlockPaintedStone.class),
  blockPaintedWoodenTrapdoor(BlockPaintedTrapDoor.class, "create_wooden"),
  blockPaintedIronTrapdoor(BlockPaintedTrapDoor.class, "create_iron"),
  blockPaintedDarkSteelTrapdoor(BlockPaintedTrapDoor.class, "create_dark"),

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
  blockEndermanSkull(BlockEndermanSkull.class),
  itemEnderFood(ItemEnderFood.class),
  itemItemFilter(ItemBasicItemFilter.class),

  blockFusedQuartz(BlockFusedQuartz.class, "createFusedQuartz"),
  blockFusedGlass(BlockFusedQuartz.class, "createFusedGlass"),
  blockEnlightenedFusedQuartz(BlockFusedQuartz.class, "createEnlightenedFusedQuartz"),
  blockEnlightenedFusedGlass(BlockFusedQuartz.class, "createEnlightenedFusedGlass"),
  blockDarkFusedQuartz(BlockFusedQuartz.class, "createDarkFusedQuartz"),
  blockDarkFusedGlass(BlockFusedQuartz.class, "createDarkFusedGlass"),
  blockPaintedFusedQuartz(BlockPaintedFusedQuartz.class),

  itemSoulVial(ItemSoulVial.class),

  block_detector_block(BlockDetector.class),
  block_detector_block_silent(BlockDetector.class, "createSilent"),

  itemDarkSteelHelmet(ItemDarkSteelArmor.class, "createDarkSteelHelmet"),
  itemDarkSteelChestplate(ItemDarkSteelArmor.class, "createDarkSteelChestplate"),
  itemDarkSteelLeggings(ItemDarkSteelArmor.class, "createDarkSteelLeggings"),
  itemDarkSteelBoots(ItemDarkSteelArmor.class, "createDarkSteelBoots"),
  itemDarkSteelSword(ItemDarkSteelSword.class),
  itemDarkSteelPickaxe(ItemDarkSteelPickaxe.class),
  itemDarkSteelAxe(ItemDarkSteelAxe.class),
  itemDarkSteelBow(ItemDarkSteelBow.class),
  itemDarkSteelShears(ItemDarkSteelShears.class),

  ;

  final @Nonnull String unlocalisedName;

  protected @Nullable Block block;
  protected @Nullable Item item;

  protected final @Nonnull Class<?> clazz;
  protected final @Nullable String blockMethodName, itemMethodName;
  protected final @Nullable Class<? extends TileEntity> teClazz;

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

  private ModObject(@Nonnull Class<?> clazz) {
    this(clazz, "create", (Class<? extends TileEntity>) null);
  }

  private ModObject(@Nonnull Class<?> clazz, Class<? extends TileEntity> teClazz) {
    this(clazz, "create", teClazz);
  }

  private ModObject(@Nonnull Class<?> clazz, @Nonnull String methodName) {
    this(clazz, methodName, (Class<? extends TileEntity>) null);
  }

  private ModObject(@Nonnull Class<?> clazz, @Nonnull String blockMethodName, @Nonnull String itemMethodName) {
    this(clazz, blockMethodName, itemMethodName, null);
  }

  private ModObject(@Nonnull Class<?> clazz, @Nonnull String methodName, Class<? extends TileEntity> teClazz) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    if (Block.class.isAssignableFrom(clazz)) {
      this.blockMethodName = methodName;
      this.itemMethodName = null;
    } else if (Item.class.isAssignableFrom(clazz)) {
      this.blockMethodName = null;
      this.itemMethodName = methodName;
    } else {
      throw new RuntimeException("Clazz " + clazz + " unexpectedly is neither a Block nor an Item.");
    }
    this.teClazz = teClazz;
  }

  private ModObject(@Nonnull Class<?> clazz, @Nullable String blockMethodName, @Nullable String itemMethodName, Class<? extends TileEntity> teClazz) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    this.blockMethodName = blockMethodName == null || blockMethodName.isEmpty() ? null : blockMethodName;
    this.itemMethodName = itemMethodName == null || itemMethodName.isEmpty() ? null : itemMethodName;
    this.teClazz = teClazz;
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

  @Nullable
  @Override
  public final Class<? extends TileEntity> getTileClass() {
    return teClazz;
  }

  public final @Nonnull Block getBlockNN() {
    return NullHelper.notnull(block, "Block " + this + " is unexpectedly missing");
  }

  public final @Nonnull Item getItemNN() {
    return NullHelper.notnull(item, "Item " + this + " is unexpectedly missing");
  }

  @Override
  public final @Nonnull Class<?> getClazz() {
    return clazz;
  }

  @Override
  public final String getBlockMethodName() {
    return blockMethodName;
  }

  @Override
  public final String getItemMethodName() {
    return itemMethodName;
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
  @Nonnull
  public final ResourceLocation getRegistryName() {
    return new ResourceLocation(EnderIO.DOMAIN, unlocalisedName);
  }

  @Override
  public final @Nonnull <B extends Block> B apply(@Nonnull B blockIn) {
    blockIn.setUnlocalizedName(getUnlocalisedName());
    blockIn.setRegistryName(getRegistryName());
    return blockIn;
  }

  @Override
  public final @Nonnull <I extends Item> I apply(@Nonnull I itemIn) {
    itemIn.setUnlocalizedName(getUnlocalisedName());
    itemIn.setRegistryName(getRegistryName());
    return itemIn;
  }

}
