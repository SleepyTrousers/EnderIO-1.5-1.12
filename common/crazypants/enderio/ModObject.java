package crazypants.enderio;

import static net.minecraftforge.common.Configuration.CATEGORY_BLOCK;
import static net.minecraftforge.common.Configuration.CATEGORY_ITEM;
import crazypants.util.Lang;
import net.minecraftforge.common.Configuration;

public enum ModObject {
  // Enderface
  blockEnderIo(++Config.BID, CATEGORY_BLOCK),
  itemEnderface(++Config.IID, CATEGORY_ITEM),

  // Conduits
  blockConduitBundle(++Config.BID, CATEGORY_BLOCK),
  blockConduitFacade(++Config.BID, CATEGORY_BLOCK),
  itemConduitFacade(++Config.IID, CATEGORY_ITEM),
  itemRedstoneConduit(++Config.IID, CATEGORY_ITEM),

  // Power
  itemPowerConduit(++Config.IID, CATEGORY_ITEM),

  // Liquid
  itemLiquidConduit(++Config.IID, CATEGORY_ITEM),

  // Materials
  itemBasicCapacitor(++Config.IID, CATEGORY_ITEM),
  itemAlloy(++Config.IID, CATEGORY_ITEM),
  blockFusedQuartz(++Config.BID, CATEGORY_BLOCK),

  // Stirling Gen
  blockStirlingGenerator(++Config.BID, CATEGORY_BLOCK),
  blockReservoir(++Config.BID, CATEGORY_BLOCK),
  blockAlloySmelter(++Config.BID, CATEGORY_BLOCK),

  // Painter
  blockPainter(++Config.BID, CATEGORY_BLOCK),
  blockCustomFence(++Config.BID, CATEGORY_BLOCK),
  blockCustomFenceGate(++Config.BID, CATEGORY_BLOCK),
  blockCustomWall(++Config.BID, CATEGORY_BLOCK),
  blockCustomStair(++Config.BID, CATEGORY_BLOCK),
  itemFusedQuartzFrame(++Config.IID, CATEGORY_ITEM),

  // Solar Panel
  blockSolarPanel(++Config.BID, CATEGORY_BLOCK),

  blockElectricLight(++Config.BID, CATEGORY_BLOCK),
  blockLightNode(++Config.BID, CATEGORY_BLOCK),

  blockCapacitorBank(++Config.BID, CATEGORY_BLOCK),

  itemYetaWrench(++Config.IID, CATEGORY_ITEM),

  blockCustomSlab(++Config.BID, CATEGORY_BLOCK),
  blockCustomDoubleSlab(++Config.BID, CATEGORY_BLOCK),

  blockCrusher(++Config.BID, CATEGORY_BLOCK),

  blockHyperCube(++Config.BID, CATEGORY_BLOCK),

  itemMachinePart(++Config.IID, CATEGORY_ITEM),
  itemPowderIngot(++Config.IID, CATEGORY_ITEM),

  itemMJReader(++Config.IID, CATEGORY_ITEM),
  itemMaterial(++Config.IID, CATEGORY_ITEM),

  blockPowerMonitor(++Config.BID, CATEGORY_BLOCK),

  itemItemConduit(++Config.IID, CATEGORY_ITEM);

  public final String unlocalisedName;
  public final String name;
  public final int defaultId;
  public final String category;
  public int id;
  public int actualId;

  private ModObject(int defaultId, String category) {
    this.unlocalisedName = "enderIO:" + toString();
    this.defaultId = defaultId;
    this.name = Lang.localize(name());
    this.category = category;
    id = -1;
    actualId = -1;
  }

  void load(Configuration config) {
    if(CATEGORY_ITEM.equals(category)) {
      id = config.getItem(unlocalisedName, defaultId).getInt();
    } else if(CATEGORY_BLOCK.equals(category)) {
      id = config.getBlock(unlocalisedName, defaultId).getInt();
    } else {
      throw new RuntimeException("Unknown category " + category);
    }
    actualId = id;
    if(CATEGORY_ITEM.equals(category)) {
      actualId += 256;
    }
  }

}