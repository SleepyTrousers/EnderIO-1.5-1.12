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
  blockFusedQuartz,
  itemFusedQuartzFrame,
  itemMachinePart,
  itemPowderIngot,

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
  blockCustomFence,
  blockCustomFenceGate,
  blockCustomWall,
  blockCustomStair,
  blockCustomSlab,
  blockCustomDoubleSlab,

  itemMJReader,
  itemYetaWrench,

  blockTravelAnchor,
  itemTravelStaff;

  public final String unlocalisedName;

  private ModObject() {
    this.unlocalisedName = name();
  }

}