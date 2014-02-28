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

  // Power
  itemPowerConduit,

  // Liquid
  itemLiquidConduit,

  // Materials
  itemBasicCapacitor,
  itemAlloy,
  blockFusedQuartz,

  // Stirling Gen
  blockStirlingGenerator,
  blockReservoir,
  blockAlloySmelter,

  // Painter
  blockPainter,
  blockCustomFence,
  blockCustomFenceGate,
  blockCustomWall,
  blockCustomStair,
  itemFusedQuartzFrame,

  // Solar Panel
  blockSolarPanel,

  blockElectricLight,
  blockLightNode,

  blockCapacitorBank,

  itemYetaWrench,

  blockCustomSlab,
  blockCustomDoubleSlab,

  blockCrusher,

  blockHyperCube,

  itemMachinePart,
  itemPowderIngot,

  itemMJReader,
  itemMaterial,

  blockPowerMonitor,

  itemItemConduit,
  itemMeConduit,

  blockTravelAnchor,
  itemTravelStaff;

  public final String unlocalisedName;
  //public final String name;

  public int actualId;

  private ModObject() {
    this.unlocalisedName = name();
    //this.name = Lang.localize(name());
    actualId = -1;
  }

}