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
  itemBasicFilterUpgrade,
  itemExistingItemFilter,
  itemModItemFilter,
  itemPowerItemFilter,
  itemExtractSpeedUpgrade,

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
  itemFusedQuartzFrame,
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
  blockTransceiver,
  blockBuffer,

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
  itemTravelStaff,
  itemMagnet,
  itemGliderWing,
  blockEndermanSkull, 
  itemSoulVessel, 
  itemFrankenSkull, 
  
  blockEnderRail;

  public final String unlocalisedName;

  private ModObject() {
    unlocalisedName = name();
  }

}
