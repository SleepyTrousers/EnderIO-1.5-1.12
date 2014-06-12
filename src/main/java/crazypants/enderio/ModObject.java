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

  blockElectricLight,
  blockLightNode,
  blockLight,

  // Painter
  blockPainter,
  blockPaintedFence,
  blockPaintedFenceGate,
  blockPaintedWall,
  blockPaintedStair,
  blockPaintedSlab,
  blockPaintedDoubleSlab,

  itemConduitProbe,
  itemYetaWrench,

  blockTravelAnchor,
  itemTravelStaff, 
  itemMagnet;

  public final String unlocalisedName;

  private ModObject() {
    this.unlocalisedName = name();
  }

}