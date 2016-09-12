package crazypants.enderio;

import javax.annotation.Nonnull;

import crazypants.util.NullHelper;

public enum ModObject implements IModObject {
  // Enderface
  blockEnderIo,
  itemEnderface,

  // Conduits
  blockConduitBundle,
  blockConduitFacade,
  itemConduitFacade,
  itemRedstoneConduit,
  itemItemConduit,
  itemGasConduit,
  itemMEConduit,
  itemOCConduit,
  itemBasicFilterUpgrade,
  itemExistingItemFilter,
  itemModItemFilter,
  itemPowerItemFilter,
  itemExtractSpeedUpgrade,
  itemFunctionUpgrade,

  // Power
  itemPowerConduit,

  // Liquid
  itemLiquidConduit,

  // Materials
  itemBasicCapacitor,
  itemAlloy,
  itemMaterial,
  itemMachinePart,
  itemPowderIngot,
  blockFusedQuartz,
  blockPaintedFusedQuartz,
  blockDarkIronBars,

  // Machines
  blockStirlingGenerator,
  blockCombustionGenerator,
  blockZombieGenerator,
  blockReservoir,
  blockAlloySmelter,
  blockSolarPanel,
  blockCapacitorBank,
  blockCapBank,
  blockSagMill,
  blockHyperCube,
  blockPowerMonitor,
  blockPowerMonitorv2,
  blockVat,
  blockFarmStation,
  blockTank,
  blockCrafter,
  blockVacuumChest,
  blockWirelessCharger,
  blockEnchanter,
  blockSoulBinder,
  blockSliceAndSplice,
  blockAttractor,
  blockSpawnGuard,
  blockSpawnRelocator,
  blockExperienceObelisk,
  blockWeatherObelisk,
  blockInhibitorObelisk,
  blockTransceiver,
  blockBuffer,
  blockInventoryPanel,

  blockPoweredSpawner,
  itemBrokenSpawner,

  blockKillerJoe,

  blockElectricLight,
  blockLightNode,
  blockLight,

  //Blocks
  blockDarkSteelAnvil,
  blockDarkSteelLadder,
  blockReinforcedObsidian,
  blockIngotStorage,
  blockSelfResettingLever,

  // Painter
  blockPainter,
  blockPaintedFence,
  blockPaintedStoneFence,
  blockPaintedFenceGate,
  blockPaintedWall,
  blockPaintedStair,
  blockPaintedStoneStair,
  blockPaintedSlab,
  blockPaintedStoneSlab,
  blockPaintedDoubleSlab,
  blockPaintedStoneDoubleSlab,
  blockPaintedGlowstone,
  blockPaintedGlowstoneSolid,
  blockPaintedCarpet,
  blockPaintedPressurePlate,
  blockPaintedRedstone,
  blockPaintedRedstoneSolid,
  blockExitRail,

  itemConduitProbe,
  itemYetaWrench,
  itemXpTransfer,

  blockTravelAnchor,
  blockTelePad,
  blockDialingDevice,
  itemCoordSelector,
  itemTravelStaff,
  itemRodOfReturn,
  itemMagnet,
  itemGliderWing,
  blockEndermanSkull,
  itemSoulVessel,
  itemFrankenSkull,
  
  blockEnderRail,
  
  itemEnderFood,
  blockGauge,
  itemRemoteInvAccess,
  blockInventoryPanelSensor;

  private final @Nonnull String unlocalisedName;

  private ModObject() {
    unlocalisedName = NullHelper.notnullJ(name(), "Enum.name()");
  }

  @Override
  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

}
