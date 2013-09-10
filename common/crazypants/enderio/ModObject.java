package crazypants.enderio;

import static net.minecraftforge.common.Configuration.CATEGORY_BLOCK;
import static net.minecraftforge.common.Configuration.CATEGORY_ITEM;
import net.minecraftforge.common.Configuration;

public enum ModObject {
  // Enderface
  blockEnderIo(++Config.BID, CATEGORY_BLOCK, "Ender IO"),
  itemEnderface(++Config.IID, CATEGORY_ITEM, "Enderface"),

  // Conduits
  blockConduitBundle(++Config.BID, CATEGORY_BLOCK, "Conduit Bundle"),
  blockConduitFacade(++Config.BID, CATEGORY_BLOCK, "Conduit Facade"),
  itemConduitFacade(++Config.IID, CATEGORY_ITEM, "Conduit Facade"),
  itemRedstoneConduit(++Config.IID, CATEGORY_ITEM, "Redstone Conduit"),

  // Power
  itemPowerConduit(++Config.IID, CATEGORY_ITEM, "Power Conduit"),

  // Liquid
  itemLiquidConduit(++Config.IID, CATEGORY_ITEM, "Liquid Conduit"),

  // Materials
  itemIndustrialBinder(++Config.IID, CATEGORY_ITEM, "Industrial Binder"),
  itemBasicCapacitor(++Config.IID, CATEGORY_ITEM, "Basic Capacitor"),
  itemAlloy(++Config.IID, CATEGORY_ITEM, "Alloy"),
  itemRedstoneInductor(++Config.IID, CATEGORY_ITEM, "Redstone Inductor"),
  itemBasicGear(++Config.IID, CATEGORY_ITEM, "Basic Gear"),
  blockFusedQuartz(++Config.BID, CATEGORY_BLOCK, "Fused Quartz"),
  blockMachineChassi(++Config.BID, CATEGORY_BLOCK, "Machine Chassi"),

  // Stirling Gen
  blockStirlingGenerator(++Config.BID, CATEGORY_BLOCK, "Stirling Generator"),
  blockReservoir(++Config.BID, CATEGORY_BLOCK, "Reservoir"),
  blockAlloySmelter(++Config.BID, CATEGORY_BLOCK, "Alloy Smelter"),

  // Painter
  blockPainter(++Config.BID, CATEGORY_BLOCK, "Painter"),
  blockCustomFence(++Config.BID, CATEGORY_BLOCK, "Painted Fence"),
  blockCustomFenceGate(++Config.BID, CATEGORY_BLOCK, "Painted Gate"),
  blockCustomWall(++Config.BID, CATEGORY_BLOCK, "Painted Wall"),
  blockCustomStair(++Config.BID, CATEGORY_BLOCK, "Painted Stair"),  
  itemFusedQuartzFrame(++Config.IID, CATEGORY_ITEM, "Fused Quartz Frame"),

  // Solar Panel
  blockSolarPanel(++Config.BID, CATEGORY_BLOCK, "Photovoltaic Panel"),

  blockElectricLight(++Config.BID, CATEGORY_BLOCK, "Electric Light"),
  blockLightNode(++Config.BID, CATEGORY_BLOCK, "Light Node (Internal Only)"),
  
  blockCapacitorBank(++Config.BID, CATEGORY_BLOCK, "Capacitor Bank"),

  itemYetaWrench(++Config.IID, CATEGORY_ITEM, "Yeta Wrench"),
  itemMJReader(++Config.IID, CATEGORY_ITEM, "MJ Reader (WIP)"),
  
  blockCustomSlab(++Config.BID, CATEGORY_BLOCK, "Painted Slab"),
  blockCustomDoubleSlab(++Config.BID, CATEGORY_BLOCK, "Painted Slab"),
  
  blockCrusher(++Config.BID, CATEGORY_BLOCK,"SAG Mill");

  public final String unlocalisedName;
  public final String name;
  public final int defaultId;
  public final String category;
  public int id;
  public int actualId;

  private ModObject(int defaultId, String category, String name) {
    this.unlocalisedName = "enderIO:" + toString();
    this.defaultId = defaultId;
    this.name = name;
    this.category = category;
    id = -1;
    actualId = -1;
  }

  void load(Configuration config) {
    if (CATEGORY_ITEM.equals(category)) {
      id = config.getItem(unlocalisedName, defaultId).getInt();
    } else if (CATEGORY_BLOCK.equals(category)) {
      id = config.getBlock(unlocalisedName, defaultId).getInt();
    } else {
      throw new RuntimeException("Unknown category " + category);
    }
    actualId = id;
    if (CATEGORY_ITEM.equals(category)) {
      actualId += 256;
    }
  }

}