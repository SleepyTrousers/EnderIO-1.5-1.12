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

  // Machines
  blockStirlingGenerator,
  blockReservoir,
  blockAlloySmelter,
  blockSolarPanel,
  blockCapacitorBank,
  blockSagMill,
  blockHyperCube,
  blockPowerMonitor,

  blockElectricLight,
  blockLightNode,

  // Painter
  blockPainter,
  blockPaintedFence,
  blockPaintedFenceGate,
  blockPaintedWall,
  blockPaintedStair,
  blockPaintedSlab,
  blockPaintedDoubleSlab,

  itemMJReader,
  itemYetaWrench,

  blockTravelAnchor,
  itemTravelStaff;

  public final String unlocalisedName;

  private ModObject() {
    this.unlocalisedName = name();
  }

}