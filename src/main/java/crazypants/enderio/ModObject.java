package crazypants.enderio;

import javax.annotation.Nonnull;

import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.block.BlockDarkSteelLadder;
import crazypants.enderio.block.BlockDecoration;
import crazypants.enderio.block.BlockDecorationFacing;
import crazypants.enderio.block.BlockReinforcedObsidian;
import crazypants.enderio.block.BlockSelfResettingLever;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import crazypants.enderio.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.conduit.item.ItemItemConduit;
import crazypants.enderio.conduit.item.filter.ItemBasicItemFilter;
import crazypants.enderio.conduit.item.filter.ItemExistingItemFilter;
import crazypants.enderio.conduit.item.filter.ItemModItemFilter;
import crazypants.enderio.conduit.item.filter.ItemPowerItemFilter;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.oc.ItemOCConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.item.ItemConduitProbe;
import crazypants.enderio.item.ItemEnderFood;
import crazypants.enderio.item.ItemSoulVessel;
import crazypants.enderio.item.ItemYetaWrench;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.buffer.BlockBuffer;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.crafter.BlockCrafter;
import crazypants.enderio.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.gauge.BlockGauge;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator;
import crazypants.enderio.machine.invpanel.BlockInventoryPanel;
import crazypants.enderio.machine.invpanel.remote.ItemRemoteInvAccess;
import crazypants.enderio.machine.invpanel.sensor.BlockInventoryPanelSensor;
import crazypants.enderio.machine.killera.BlockKillerJoe;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.obelisk.attractor.BlockAttractor;
import crazypants.enderio.machine.obelisk.aversion.BlockAversionObelisk;
import crazypants.enderio.machine.obelisk.inhibitor.BlockInhibitorObelisk;
import crazypants.enderio.machine.obelisk.relocator.BlockRelocatorObelisk;
import crazypants.enderio.machine.obelisk.weather.BlockWeatherObelisk;
import crazypants.enderio.machine.obelisk.xp.BlockExperienceObelisk;
import crazypants.enderio.machine.obelisk.xp.ItemXpTransfer;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.painter.blocks.BlockPaintedCarpet;
import crazypants.enderio.machine.painter.blocks.BlockPaintedFence;
import crazypants.enderio.machine.painter.blocks.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.blocks.BlockPaintedGlowstone;
import crazypants.enderio.machine.painter.blocks.BlockPaintedPressurePlate;
import crazypants.enderio.machine.painter.blocks.BlockPaintedRedstone;
import crazypants.enderio.machine.painter.blocks.BlockPaintedSlab;
import crazypants.enderio.machine.painter.blocks.BlockPaintedStairs;
import crazypants.enderio.machine.painter.blocks.BlockPaintedWall;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.sagmill.BlockSagMill;
import crazypants.enderio.machine.slicensplice.BlockSliceAndSplice;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.soul.BlockSoulBinder;
import crazypants.enderio.machine.spawner.BlockPoweredSpawner;
import crazypants.enderio.machine.spawner.ItemBrokenSpawner;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vat.BlockVat;
import crazypants.enderio.machine.wireless.BlockWirelessCharger;
import crazypants.enderio.material.BlockDarkIronBars;
import crazypants.enderio.material.BlockIngotStorage;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFrankenSkull;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemMaterial;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.fusedQuartz.BlockColoredFusedQuartz;
import crazypants.enderio.material.fusedQuartz.BlockFusedQuartz;
import crazypants.enderio.material.fusedQuartz.BlockPaintedFusedQuartz;
import crazypants.enderio.rail.BlockExitRail;
import crazypants.enderio.teleport.ItemTravelStaff;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.telepad.BlockDialingDevice;
import crazypants.enderio.teleport.telepad.BlockTelePad;
import crazypants.enderio.teleport.telepad.ItemCoordSelector;
import crazypants.enderio.teleport.telepad.ItemLocationPrintout;
import crazypants.enderio.teleport.telepad.ItemRodOfReturn;
import crazypants.util.NullHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public enum ModObject implements IModObject {
  // Enderface
  blockEnderIo(BlockEnderIO.class),
  itemEnderface(ItemEnderface.class),

  // Conduits
  blockConduitBundle(BlockConduitBundle.class),
  blockConduitFacade(BlockConduitFacade.class),
  itemConduitFacade,
  itemRedstoneConduit(ItemRedstoneConduit.class),
  itemItemConduit(ItemItemConduit.class),
  itemGasConduit,
  itemMEConduit,
  itemOCConduit(ItemOCConduit.class),
  itemBasicFilterUpgrade(ItemBasicItemFilter.class),
  itemExistingItemFilter(ItemExistingItemFilter.class),
  itemModItemFilter(ItemModItemFilter.class),
  itemPowerItemFilter(ItemPowerItemFilter.class),
  itemExtractSpeedUpgrade(ItemExtractSpeedUpgrade.class),
  itemFunctionUpgrade(ItemFunctionUpgrade.class),

  // Power
  itemPowerConduit(ItemPowerConduit.class),

  // Liquid
  itemLiquidConduit(ItemLiquidConduit.class),

  // Materials
  itemBasicCapacitor(ItemCapacitor.class),
  itemAlloy(ItemAlloy.class),
  itemMaterial(ItemMaterial.class),
  itemMachinePart(ItemMachinePart.class),
  itemPowderIngot(ItemPowderIngot.class),
  blockFusedQuartz {
    @Override
    protected void create() {
      block = BlockFusedQuartz.create();
      BlockColoredFusedQuartz.create();
    }
  },
  blockPaintedFusedQuartz(BlockPaintedFusedQuartz.class),
  blockDarkIronBars(BlockDarkIronBars.class),

  // Machines
  blockStirlingGenerator(BlockStirlingGenerator.class),
  blockCombustionGenerator(BlockCombustionGenerator.class),
  blockZombieGenerator(BlockZombieGenerator.class),
  blockReservoir(BlockReservoir.class),
  blockAlloySmelter(BlockAlloySmelter.class),
  blockSolarPanel(BlockSolarPanel.class),
  blockCapBank(BlockCapBank.class),
  blockSagMill(BlockSagMill.class),
  blockPowerMonitor(BlockPowerMonitor.class, "createPowerMonitor"),
  blockPowerMonitorv2(BlockPowerMonitor.class, "createAdvancedPowerMonitor"),
  blockVat(BlockVat.class),
  blockFarmStation(BlockFarmStation.class),
  blockTank(BlockTank.class),
  blockCrafter(BlockCrafter.class),
  blockVacuumChest(BlockVacuumChest.class),
  blockWirelessCharger(BlockWirelessCharger.class),
  blockEnchanter(BlockEnchanter.class),
  blockSoulBinder(BlockSoulBinder.class),
  blockSliceAndSplice(BlockSliceAndSplice.class),
  blockAttractor(BlockAttractor.class),
  blockSpawnGuard(BlockAversionObelisk.class),
  blockSpawnRelocator(BlockRelocatorObelisk.class),
  blockExperienceObelisk(BlockExperienceObelisk.class),
  blockWeatherObelisk(BlockWeatherObelisk.class),
  blockInhibitorObelisk(BlockInhibitorObelisk.class),
  blockTransceiver(BlockTransceiver.class),
  blockBuffer(BlockBuffer.class),
  blockInventoryPanel(BlockInventoryPanel.class),

  blockPoweredSpawner(BlockPoweredSpawner.class),
  itemBrokenSpawner(ItemBrokenSpawner.class),
  blockKillerJoe(BlockKillerJoe.class),

  blockElectricLight(BlockElectricLight.class),
  blockLightNode(BlockLightNode.class),

  //Blocks
  blockDarkSteelAnvil(BlockDarkSteelAnvil.class),
  blockDarkSteelLadder(BlockDarkSteelLadder.class),
  blockReinforcedObsidian(BlockReinforcedObsidian.class),
  blockIngotStorage(BlockIngotStorage.class),
  blockSelfResettingLever(BlockSelfResettingLever.class),
  blockDecoration1(BlockDecoration.class),
  blockDecoration2(BlockDecorationFacing.class),

  // Painter
  blockPainter(BlockPainter.class),
  blockPaintedFence(BlockPaintedFence.class),
  blockPaintedStoneFence(BlockPaintedFence.class, "create_stone"),
  blockPaintedFenceGate(BlockPaintedFenceGate.class),
  blockPaintedWall(BlockPaintedWall.class),
  blockPaintedStair(BlockPaintedStairs.class),
  blockPaintedStoneStair(BlockPaintedStairs.class, "create_stone"),
  blockPaintedSlab {
    @Override
    protected void create() {
      BlockPaintedSlab[] slabs = BlockPaintedSlab.create();
      blockPaintedSlab.block = slabs[0];
      blockPaintedDoubleSlab.block = slabs[1];
      blockPaintedStoneSlab.block = slabs[2];
      blockPaintedStoneDoubleSlab.block = slabs[3];
    }
  },
  blockPaintedDoubleSlab {
    @Override
    protected void create() {
      // see blockPaintedSlab
    }
  },
  blockPaintedStoneSlab {
    @Override
    protected void create() {
      // see blockPaintedSlab
    }
  },
  blockPaintedStoneDoubleSlab {
    @Override
    protected void create() {
      // see blockPaintedSlab
    }
  },
  blockPaintedGlowstone(BlockPaintedGlowstone.class),
  blockPaintedGlowstoneSolid(BlockPaintedGlowstone.class, "create_solid"),
  blockPaintedCarpet(BlockPaintedCarpet.class),
  blockPaintedPressurePlate(BlockPaintedPressurePlate.class),
  blockPaintedRedstone(BlockPaintedRedstone.class),
  blockPaintedRedstoneSolid,
  blockExitRail(BlockExitRail.class),

  itemConduitProbe(ItemConduitProbe.class),
  itemYetaWrench(ItemYetaWrench.class),
  itemXpTransfer(ItemXpTransfer.class),

  blockTravelAnchor(BlockTravelAnchor.class),
  blockTelePad(BlockTelePad.class, "createTelepad"),
  blockDialingDevice(BlockDialingDevice.class),
  itemCoordSelector(ItemCoordSelector.class),
  itemLocationPrintout(ItemLocationPrintout.class),
  itemTravelStaff(ItemTravelStaff.class),
  itemRodOfReturn(ItemRodOfReturn.class),
  itemMagnet,
  itemGliderWing,
  blockEndermanSkull(BlockEndermanSkull.class),
  itemSoulVessel(ItemSoulVessel.class),
  itemFrankenSkull(ItemFrankenSkull.class),
  
  // blockEnderRail(BlockEnderRail.class),
  
  itemEnderFood(ItemEnderFood.class),
  blockGauge(BlockGauge.class),
  itemRemoteInvAccess(ItemRemoteInvAccess.class),
  blockInventoryPanelSensor(BlockInventoryPanelSensor.class);

  private final @Nonnull String unlocalisedName;


  protected Block block;
  protected Item item;
  
  protected final Class<?> clazz;
  protected final String methodName;
  
  private ModObject() {
    this(null);
  }

  ModObject(Class<?> clazz) {
    this(clazz, "create");
  }
  
  ModObject(Class<?> clazz, String methodName) {
    unlocalisedName = NullHelper.notnullJ(name(), "Enum.name()");
    this.clazz = clazz;
    this.methodName = methodName;
  }
  
  @Override
  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  public Block getBlock() {
    return block;
  }

  public Item getItem() {
    return item;
  }

  protected void create() {
    if(clazz == null) {
      Log.info(this + ".create() missing");
      return;
    }
    Object obj = null;
    try {
      obj = clazz.getDeclaredMethod(methodName, (Class<?>[])null).invoke(null, (Object[])null);
    } catch (Exception e) {
      String str = "ModObject:create: Could not create instance for " + clazz + " using method " + methodName;
      Log.error(str + " Exception: " + e);
      throw new RuntimeException(str, e);
    }
    if(obj instanceof Item) {
      item = (Item)obj;
    } else {
      block = (Block)obj;
      item = Item.getItemFromBlock(block);
    }
  }

  public static void preInit(FMLPreInitializationEvent event) {
    for (ModObject elem : values()) {
      elem.create();
    }
  }

}
