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
import crazypants.enderio.capacitor.ItemCapacitor;
import crazypants.enderio.conduit.facade.ItemConduitFacade;
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
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.item.soulvial.ItemSoulVessel;
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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
  itemAlloyNugget(ItemAlloy.class, "createNuggets"),
  itemMaterial(ItemMaterial.class),

  itemBrokenSpawner(ItemBrokenSpawner.class),

  //Blocks
  blockColdFire(BlockColdFire.class),
  blockDarkSteelAnvil(BlockDarkSteelAnvil.class),
  blockDarkSteelLadder(BlockDarkSteelLadder.class),
  blockDarkIronBars(BlockDarkIronBars.class),
  blockDarkSteelTrapdoor(BlockDarkSteelTrapDoor.class),
  blockReinforcedObsidian(BlockReinforcedObsidian.class),
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
  blockPaintedSlab(BlockPaintedSlabManager.class, "create_wood"),
  blockPaintedDoubleSlab(BlockPaintedSlabManager.class, "create_wood_double", TileEntityTwicePaintedBlock.class),
  blockPaintedStoneSlab(BlockPaintedSlabManager.class, "create_stone"),
  blockPaintedStoneDoubleSlab(BlockPaintedSlabManager.class, "create_stone_double", TileEntityTwicePaintedBlock.class),
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

  blockFusedQuartz(BlockFusedQuartz.class, "createFusedQuartz"),
  blockFusedGlass(BlockFusedQuartz.class, "createFusedGlass"),
  blockEnlightenedFusedQuartz(BlockFusedQuartz.class, "createEnlightenedFusedQuartz"),
  blockEnlightenedFusedGlass(BlockFusedQuartz.class, "createEnlightenedFusedGlass"),
  blockDarkFusedQuartz(BlockFusedQuartz.class, "createDarkFusedQuartz"),
  blockDarkFusedGlass(BlockFusedQuartz.class, "createDarkFusedGlass"),
  blockPaintedFusedQuartz(BlockPaintedFusedQuartz.class),

  itemSoulVessel(ItemSoulVessel.class) {
    @Override
    protected void initElem(@Nonnull FMLInitializationEvent event) {
      ItemSoulVessel.initPhase();
    }
  },

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
  
  protected final @Nullable Class<?> clazz;
  protected final @Nonnull String methodName;
  protected final @Nullable Class<? extends TileEntity> teClazz;
  
  private ModObject() {
    this(null);
  }

  private ModObject(@Nullable Class<?> clazz) {
    this(clazz, "create", null);
  }

  private ModObject(@Nullable Class<?> clazz, Class<? extends TileEntity> teClazz) {
    this(clazz, "create", teClazz);
  }
  
  private ModObject(@Nullable Class<?> clazz, @Nonnull String methodName) {
    this(clazz, methodName, null);
  }

  private ModObject(@Nullable Class<?> clazz, @Nonnull String methodName, Class<? extends TileEntity> teClazz) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    this.methodName = methodName;
    this.teClazz = teClazz;
  }

  @Override
  public @Nonnull String getUnlocalisedName() {
    return EnderIO.DOMAIN + "_" + unlocalisedName;
  }

  @Override
  public @Nullable Block getBlock() {
    return block;
  }

  @Override
  public @Nullable Item getItem() {
    return item;
  }

  public @Nonnull Block getBlockNN() {
    return NullHelper.notnull(block, "Block " + this + " is unexpectedly missing");
  }

  public @Nonnull Item getItemNN() {
    return NullHelper.notnull(item, "Item " + this + " is unexpectedly missing");
  }

  protected void preInitElem(@Nonnull FMLPreInitializationEvent event) {
    ModObjectRegistry.preInit(this, event);
  }

  protected void initElem(@Nonnull FMLInitializationEvent event) {
    ModObjectRegistry.initElem(this, event);
  }

  @Override
  public Class<?> getClazz() {
    return clazz;
  }

  @Override
  public String getMethodName() {
    return methodName;
  }

  @Override
  public void setItem(Item obj) {
    item = obj;
  }

  @Override
  public void setBlock(Block obj) {
    block = obj;
  }

  @Override
  @Nonnull
  public ResourceLocation getRegistryName() {
    return new ResourceLocation(EnderIO.DOMAIN, unlocalisedName);
  }

  @Override
  public @Nonnull <B extends Block> B apply(@Nonnull B blockIn) {
    blockIn.setUnlocalizedName(getUnlocalisedName());
    blockIn.setRegistryName(getRegistryName());
    return blockIn;
  }

  @Override
  public @Nonnull <I extends Item> I apply(@Nonnull I itemIn) {
    itemIn.setUnlocalizedName(getUnlocalisedName());
    itemIn.setRegistryName(getRegistryName());
    return itemIn;
  }

}
