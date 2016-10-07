package crazypants.enderio;

import javax.annotation.Nonnull;

import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.buffer.BlockBuffer;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.crafter.BlockCrafter;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator;
import crazypants.enderio.machine.invpanel.BlockInventoryPanel;
import crazypants.enderio.machine.invpanel.sensor.BlockInventoryPanelSensor;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.painter.blocks.BlockPaintedCarpet;
import crazypants.enderio.machine.painter.blocks.BlockPaintedFence;
import crazypants.enderio.machine.painter.blocks.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.blocks.BlockPaintedGlowstone;
import crazypants.enderio.machine.painter.blocks.BlockPaintedPressurePlate;
import crazypants.enderio.machine.painter.blocks.BlockPaintedRedstone;
import crazypants.enderio.machine.painter.blocks.BlockPaintedSlab;
import crazypants.enderio.machine.painter.blocks.BlockPaintedStairs;
import crazypants.enderio.machine.painter.blocks.BlockPaintedWall;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.sagmill.BlockSagMill;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vat.BlockVat;
import crazypants.enderio.machine.wireless.BlockWirelessCharger;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.telepad.BlockDialingDevice;
import crazypants.enderio.teleport.telepad.BlockTelePad;
import crazypants.enderio.teleport.telepad.ItemCoordSelector;
import crazypants.util.NullHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public enum ModObject implements IModObject {
  // Enderface
  blockEnderIo {
    @Override
    protected void create() {
      block = BlockEnderIO.create();
    }
  },
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
  blockStirlingGenerator {
    @Override
    protected void create() {
      block = BlockStirlingGenerator.create();
    }
  },
  blockCombustionGenerator {
    @Override
    protected void create() {
      block = BlockCombustionGenerator.create();
    }
  },
  blockZombieGenerator {
    @Override
    protected void create() {
      block = BlockZombieGenerator.create();
    }
  },
  blockReservoir {
    @Override
    protected void create() {
      block = BlockReservoir.create();
    }
  },
  blockAlloySmelter {
    @Override
    protected void create() {
      block = BlockAlloySmelter.create();
    }
  },
  blockSolarPanel {
    @Override
    protected void create() {
      block = BlockSolarPanel.create();
    }
  },
  blockCapBank {
    @Override
    protected void create() {
      block = BlockCapBank.create();
    }
  },
  blockSagMill {
    @Override
    protected void create() {
      block = BlockSagMill.create();
    }
  },
  blockHyperCube,
  blockPowerMonitor {
    @Override
    protected void create() {
      block = BlockPowerMonitor.createPowerMonitor();
    }
  },
  blockPowerMonitorv2 {
    @Override
    protected void create() {
      block = BlockPowerMonitor.createAdvancedPowerMonitor();
    }
  },
  blockVat {
    @Override
    protected void create() {
      block = BlockVat.create();
    }
  },
  blockFarmStation {
    @Override
    protected void create() {
      block = BlockFarmStation.create();
    }
  },
  blockTank {
    @Override
    protected void create() {
      block = BlockTank.create();
    }
  },
  blockCrafter {
    @Override
    protected void create() {
      block = BlockCrafter.create();
    }
  },
  blockVacuumChest {
    @Override
    protected void create() {
      block = BlockVacuumChest.create();
    }
  },
  blockWirelessCharger {
    @Override
    protected void create() {
      block = BlockWirelessCharger.create();
    }
  },
  blockEnchanter,
  blockSoulBinder,
  blockSliceAndSplice,
  blockAttractor,
  blockSpawnGuard,
  blockSpawnRelocator,
  blockExperienceObelisk,
  blockWeatherObelisk,
  blockInhibitorObelisk,
  blockTransceiver {
    @Override
    protected void create() {
      block = BlockTransceiver.create();
    }
  },
  blockBuffer {
    @Override
    protected void create() {
      block = BlockBuffer.create();
    }
  },
  blockInventoryPanel {
    @Override
    protected void create() {
      block = BlockInventoryPanel.create();
    }
  },

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
  blockDecoration1,
  blockDecoration2,

  // Painter
  blockPainter {
    @Override
    protected void create() {
      block = BlockPainter.create();
    }
  },
  blockPaintedFence {
    @Override
    protected void create() {
      block = BlockPaintedFence.create();
    }
  },
  blockPaintedStoneFence,
  blockPaintedFenceGate {
    @Override
    protected void create() {
      block = BlockPaintedFenceGate.create();
    }
  },
  blockPaintedWall {
    @Override
    protected void create() {
      block = BlockPaintedWall.create();
    }
  },
  blockPaintedStair {
    @Override
    protected void create() {
      block = BlockPaintedStairs.create();
    }
  },
  blockPaintedStoneStair,
  blockPaintedSlab {
    @Override
    protected void create() {
      block = BlockPaintedSlab.create();
    }
  },
  blockPaintedStoneSlab,
  blockPaintedDoubleSlab,
  blockPaintedStoneDoubleSlab,
  blockPaintedGlowstone {
    @Override
    protected void create() {
      block = BlockPaintedGlowstone.create();
    }
  },
  blockPaintedGlowstoneSolid,
  blockPaintedCarpet {
    @Override
    protected void create() {
      block = BlockPaintedCarpet.create();
    }
  },
  blockPaintedPressurePlate {
    @Override
    protected void create() {
      block = BlockPaintedPressurePlate.create();
    }
  },
  blockPaintedRedstone {
    @Override
    protected void create() {
      block = BlockPaintedRedstone.create();
    }
  },
  blockPaintedRedstoneSolid,
  blockExitRail,

  itemConduitProbe,
  itemYetaWrench,
  itemXpTransfer,

  blockTravelAnchor {
    @Override
    protected void create() {
      block = BlockTravelAnchor.create();
    }
  },
  blockTelePad {
    @Override
    protected void create() {
      block = BlockTelePad.createTelepad();
    }
  },
  blockDialingDevice {
    @Override
    protected void create() {
      block = BlockDialingDevice.create();
    }
  },
  itemCoordSelector {
    @Override
    protected void create() {
      item = ItemCoordSelector.create();
    }
  },
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
  blockInventoryPanelSensor {
    @Override
    protected void create() {
      block = BlockInventoryPanelSensor.create();
    }
  };

  private final @Nonnull String unlocalisedName;

  private ModObject() {
    unlocalisedName = NullHelper.notnullJ(name(), "Enum.name()");
  }

  @Override
  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  protected Block block;
  protected Item item;

  public Block getBlock() {
    return block;
  }

  public Item getItem() {
    return item;
  }

  protected void create() {
  }

  public static void preinit() {
    for (ModObject elem : values()) {
      elem.create();
    }
  }

}
