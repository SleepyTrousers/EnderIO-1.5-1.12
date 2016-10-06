package crazypants.enderio;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator;
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
import crazypants.enderio.machine.sagmill.BlockSagMill;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.util.NullHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public enum ModObject implements IModObject {
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
  blockReservoir,
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
  blockPowerMonitor,
  blockPowerMonitorv2,
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
  blockSpawnRelocator,
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

  blockTravelAnchor,
  blockTelePad,
  blockDialingDevice,
  itemCoordSelector,
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
  blockInventoryPanelSensor;

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
