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
  itemMeConduit,
  itemBasicFilterUpgrade,
  itemExistingItemFilter,
  itemModItemFilter,
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
  
  blockPoweredSpawner,
  itemBrokenSpawner,
  
  blockKillerJoe,

  blockElectricLight,
  blockLightNode,
  blockLight,
  
  //Blocks
  blockDarkSteelPressurePlate,

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

  blockTravelAnchor,
  itemTravelStaff, 
  itemMagnet, 
  itemGliderWing, 
  blockEndermanSkull;

  public final String unlocalisedName;

  private ModObject() {
    this.unlocalisedName = name();
  }

}