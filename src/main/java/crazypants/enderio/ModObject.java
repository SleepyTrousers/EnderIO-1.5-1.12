package crazypants.enderio;

import javax.annotation.Nonnull;

import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.block.BlockDarkSteelLadder;
import crazypants.enderio.block.BlockDecoration;
import crazypants.enderio.block.BlockDecorationFacing;
import crazypants.enderio.block.BlockReinforcedObsidian;
import crazypants.enderio.block.BlockSelfResettingLever;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.buffer.BlockBuffer;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.crafter.BlockCrafter;
import crazypants.enderio.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.gauge.BlockGauge;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator;
import crazypants.enderio.machine.invpanel.BlockInventoryPanel;
import crazypants.enderio.machine.invpanel.sensor.BlockInventoryPanelSensor;
import crazypants.enderio.machine.killera.BlockKillerJoe;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.obelisk.attractor.BlockAttractor;
import crazypants.enderio.machine.obelisk.aversion.BlockAversionObelisk;
import crazypants.enderio.machine.obelisk.inhibitor.BlockInhibitorObelisk;
import crazypants.enderio.machine.obelisk.relocator.BlockRelocatorObelisk;
import crazypants.enderio.machine.obelisk.weather.BlockWeatherObelisk;
import crazypants.enderio.machine.obelisk.xp.BlockExperienceObelisk;
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
import crazypants.enderio.machine.slicensplice.BlockSliceAndSplice;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.soul.BlockSoulBinder;
import crazypants.enderio.machine.spawner.BlockPoweredSpawner;
import crazypants.enderio.machine.spawner.ItemBrokenSpawner;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vat.BlockVat;
import crazypants.enderio.machine.wireless.BlockWirelessCharger;
import crazypants.enderio.material.BlockDarkIronBars;
import crazypants.enderio.material.BlockIngotStorage;
import crazypants.enderio.material.ItemFrankenSkull;
import crazypants.enderio.material.fusedQuartz.BlockColoredFusedQuartz;
import crazypants.enderio.material.fusedQuartz.BlockFusedQuartz;
import crazypants.enderio.material.fusedQuartz.BlockPaintedFusedQuartz;
import crazypants.enderio.rail.BlockExitRail;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.telepad.BlockDialingDevice;
import crazypants.enderio.teleport.telepad.BlockTelePad;
import crazypants.enderio.teleport.telepad.ItemCoordSelector;
import crazypants.util.NullHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
  blockConduitBundle {
    @Override
    protected void create() {
      block = BlockConduitBundle.create();
    }
  },
  blockConduitFacade {
    @Override
    protected void create() {
      block = BlockConduitFacade.create();
    }
  },
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
  blockFusedQuartz {
    @Override
    protected void create() {
      block = BlockFusedQuartz.create();
      BlockColoredFusedQuartz.create();
    }
  },
  blockPaintedFusedQuartz {
    @Override
    protected void create() {
      block = BlockPaintedFusedQuartz.create();
    }
  },
  blockDarkIronBars {
    @Override
    protected void create() {
      block = BlockDarkIronBars.create();
    }
  },

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
  blockEnchanter {
    @Override
    protected void create() {
      block = BlockEnchanter.create();
    }
  },
  blockSoulBinder {
    @Override
    protected void create() {
      block = BlockSoulBinder.create();
    }
  },
  blockSliceAndSplice {
    @Override
    protected void create() {
      block = BlockSliceAndSplice.create();
    }
  },
  blockAttractor {
    @Override
    protected void create() {
      block = BlockAttractor.create();
    }
  },
  blockSpawnGuard {
    @Override
    protected void create() {
      block = BlockAversionObelisk.create();
    }
  },
  blockSpawnRelocator {
    @Override
    protected void create() {
      block = BlockRelocatorObelisk.create();
    }
  },
  blockExperienceObelisk {
    @Override
    protected void create() {
      block = BlockExperienceObelisk.create();
    }
  },
  blockWeatherObelisk {
    @Override
    protected void create() {
      block = BlockWeatherObelisk.create();
    }
  },
  blockInhibitorObelisk {
    @Override
    protected void create() {
      block = BlockInhibitorObelisk.create();
    }
  },
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

  blockPoweredSpawner {
    @Override
    protected void create() {
      block = BlockPoweredSpawner.create();
    }
  },
  itemBrokenSpawner {
    @Override
    protected void create() {
      item = ItemBrokenSpawner.create();
    }
  },

  blockKillerJoe {
    @Override
    protected void create() {
      block = BlockKillerJoe.create();
    }
  },

  blockElectricLight {
    @Override
    protected void create() {
      block = BlockElectricLight.create();
    }
  },
  blockLightNode {
    @Override
    protected void create() {
      block = BlockLightNode.create();
    }
  },

  //Blocks
  blockDarkSteelAnvil {
    @Override
    protected void create() {
      block = BlockDarkSteelAnvil.create();
    }
  },
  blockDarkSteelLadder {
    @Override
    protected void create() {
      block = BlockDarkSteelLadder.create();
    }
  },
  blockReinforcedObsidian {
    @Override
    protected void create() {
      block = BlockReinforcedObsidian.create();
    }
  },
  blockIngotStorage {
    @Override
    protected void create() {
      block = BlockIngotStorage.create();
    }
  },
  blockSelfResettingLever {
    @Override
    protected void create() {
      BlockSelfResettingLever.create();
    }
  },
  blockDecoration1 {
    @Override
    protected void create() {
      block = BlockDecoration.create();
    }
  },
  blockDecoration2 {
    @Override
    protected void create() {
      block = BlockDecorationFacing.create();
    }
  },

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
  blockPaintedStoneFence {
    @Override
    protected void create() {
      block = BlockPaintedFence.create_stone();
    }
  },
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
  blockPaintedStoneStair {
    @Override
    protected void create() {
      block = BlockPaintedStairs.create_stone();
    }
  },
  blockPaintedSlab {
    @Override
    protected void create() {
      BlockPaintedSlab[] slabs = BlockPaintedSlab.create();
      blockPaintedSlab.block = slabs[0];
      blockPaintedDoubleSlab.block = slabs[1];
      blockPaintedStoneSlab.block = slabs[2];
      blockPaintedStoneDoubleSlab.block = slabs[3];
    }
  },
  blockPaintedDoubleSlab {
    @Override
    protected void create() {
      // see blockPaintedSlab
    }
  },
  blockPaintedStoneSlab {
    @Override
    protected void create() {
      // see blockPaintedSlab
    }
  },
  blockPaintedStoneDoubleSlab {
    @Override
    protected void create() {
      // see blockPaintedSlab
    }
  },
  blockPaintedGlowstone {
    @Override
    protected void create() {
      block = BlockPaintedGlowstone.create();
    }
  },
  blockPaintedGlowstoneSolid {
    @Override
    protected void create() {
      block = BlockPaintedGlowstone.create_solid();
    }
  },
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
  blockExitRail {
    @Override
    protected void create() {
      block = BlockExitRail.create();
    }
  },

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
  blockEndermanSkull {
    @Override
    protected void create() {
      block = BlockEndermanSkull.create();
    }
  },
  itemSoulVessel,
  itemFrankenSkull {
    @Override
    protected void create() {
      item = ItemFrankenSkull.create();
    }
  },
  
  // blockEnderRail {
  // @Override
  // protected void create() {
  // block = BlockEnderRail.create();
  // }
  // },
  
  itemEnderFood,
  blockGauge {
    @Override
    protected void create() {
      block = BlockGauge.create();
    }
  },
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
    Log.info(this + ".create() missing");
  }

  public static void preInit(FMLPreInitializationEvent event) {
    for (ModObject elem : values()) {
      elem.create();
    }
  }

}
