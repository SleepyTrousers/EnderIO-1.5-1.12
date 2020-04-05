package crazypants.enderio;

public enum ModObject {
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
  itemBigFilterUpgrade,
  itemExistingItemFilter,
  itemModItemFilter,
  itemPowerItemFilter,
  itemExtractSpeedUpgrade,
  itemFunctionUpgrade,

  // Power
  itemPowerConduit,
  itemPowerConduitEndergy,

  // Liquid
  itemLiquidConduit,

  // Materials
  itemBasicCapacitor,
  itemAlloy,
  itemAlloyEndergy,
  itemGrindingBall,
  itemGrindingBallEndergy,
  itemMaterial,
  itemMachinePart,
  itemPowderIngot,
  blockFusedQuartz,
  itemFusedQuartzFrame,
  blockDarkIronBars,
  blockSoulariumBars,
  blockEndSteelBars,

  // Machines
  blockStirlingGenerator,
  blockCombustionGenerator,
  blockZombieGenerator,
  blockFrankenzombieGenerator,
  blockEnderGenerator,
  blockReservoir,
  blockAlloySmelter,
  blockSolarPanel,
  blockCapacitorBank,
  blockCapBank,
  blockSagMill,
  blockHyperCube,
  blockPowerMonitor,
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
  blockDarkSteelPressurePlate,
  blockDarkSteelAnvil,
  blockDarkSteelLadder,
  blockReinforcedObsidian,
  blockIngotStorage,
  blockIngotStorageEndergy,

  // Painter
  blockPainter,
  blockPaintedFence,
  blockPaintedFenceGate,
  blockPaintedWall,
  blockPaintedStair,
  blockPaintedSlab,
  blockPaintedDoubleSlab,
  blockPaintedGlowstone,
  blockPaintedCarpet,

  itemConduitProbe,
  itemYetaWrench,
  itemXpTransfer,

  blockTravelAnchor,
  blockTelePad,
  itemCoordSelector,
  itemTravelStaff,
  itemMagnet,
  itemGliderWing,
  blockEndermanSkull,
  itemSoulVessel,
  itemFrankenSkull,

  blockEnderRail,

  itemEnderFood;

  public final String unlocalisedName;

  private ModObject() {
    unlocalisedName = name();
  }

}
