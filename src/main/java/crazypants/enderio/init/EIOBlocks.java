package crazypants.enderio.init;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.block.BlockDarkSteelAnvil;
import crazypants.enderio.block.BlockDarkSteelLadder;
import crazypants.enderio.block.BlockDarkSteelPressurePlate;
import crazypants.enderio.block.BlockReinforcedObsidian;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.facade.BlockConduitFacade;
import crazypants.enderio.config.Config;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.EnderIoRenderer;
import crazypants.enderio.enderface.TileEnderIO;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.item.skull.EndermanSkullRenderer;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineRenderer;
import crazypants.enderio.machine.TechneMachineRenderer;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.attractor.BlockAttractor;
import crazypants.enderio.machine.attractor.ObeliskRenderer;
import crazypants.enderio.machine.attractor.TileAttractor;
import crazypants.enderio.machine.buffer.BlockBuffer;
import crazypants.enderio.machine.capbank.BlockCapBank;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.render.CapBankRenderer;
import crazypants.enderio.machine.crafter.BlockCrafter;
import crazypants.enderio.machine.crusher.BlockCrusher;
import crazypants.enderio.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machine.enchanter.EnchanterModelRenderer;
import crazypants.enderio.machine.enchanter.TileEnchanter;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.farm.FarmingStationRenderer;
import crazypants.enderio.machine.farm.FarmingStationSpecialRenderer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machine.generator.zombie.BlockZombieGenerator;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machine.generator.zombie.ZombieGeneratorRenderer;
import crazypants.enderio.machine.hypercube.BlockHyperCube;
import crazypants.enderio.machine.hypercube.HyperCubeRenderer;
import crazypants.enderio.machine.hypercube.TileHyperCube;
import crazypants.enderio.machine.killera.BlockKillerJoe;
import crazypants.enderio.machine.killera.KillerJoeRenderer;
import crazypants.enderio.machine.killera.TileKillerJoe;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.light.BlockLightNode;
import crazypants.enderio.machine.light.ElectricLightRenderer;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.painter.BlockPaintedCarpet;
import crazypants.enderio.machine.painter.BlockPaintedFence;
import crazypants.enderio.machine.painter.BlockPaintedFenceGate;
import crazypants.enderio.machine.painter.BlockPaintedFenceGateRenderer;
import crazypants.enderio.machine.painter.BlockPaintedGlowstone;
import crazypants.enderio.machine.painter.BlockPaintedSlab;
import crazypants.enderio.machine.painter.BlockPaintedStair;
import crazypants.enderio.machine.painter.BlockPaintedWall;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.painter.PaintedBlockRenderer;
import crazypants.enderio.machine.painter.PaintedItemRenderer;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.power.CapBankRenderer2;
import crazypants.enderio.machine.power.CapacitorBankRenderer;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.reservoir.ReservoirRenderer;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.enderio.machine.slicensplice.BlockSliceAndSplice;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.machine.solar.SolarPanelRenderer;
import crazypants.enderio.machine.soul.BlockSoulBinder;
import crazypants.enderio.machine.soul.SoulBinderRenderer;
import crazypants.enderio.machine.spawner.BlockPoweredSpawner;
import crazypants.enderio.machine.spawnguard.BlockSpawnGuard;
import crazypants.enderio.machine.spawnguard.SpawnGuardRenderer;
import crazypants.enderio.machine.spawnguard.TileSpawnGuard;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.machine.tank.TankFluidRenderer;
import crazypants.enderio.machine.tank.TankItemRenderer;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.enderio.machine.transceiver.render.TransceiverRenderer;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vacuum.VacuumChestRenderer;
import crazypants.enderio.machine.vat.BlockVat;
import crazypants.enderio.machine.vat.TileVat;
import crazypants.enderio.machine.weather.BlockWeatherObelisk;
import crazypants.enderio.machine.weather.TileWeatherObelisk;
import crazypants.enderio.machine.wireless.BlockWirelessCharger;
import crazypants.enderio.machine.xp.BlockExperienceObelisk;
import crazypants.enderio.machine.xp.TileExperienceOblisk;
import crazypants.enderio.material.BlockDarkIronBars;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.material.BlockIngotStorage;
import crazypants.enderio.material.FusedQuartzRenderer;
import crazypants.enderio.material.Material;
import crazypants.enderio.rail.BlockEnderRail;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.anchor.TravelEntitySpecialRenderer;
import crazypants.enderio.teleport.telepad.BlockTelePad;
import crazypants.enderio.teleport.telepad.TelePadRenderer;
import crazypants.enderio.teleport.telepad.TelePadSpecialRenderer;
import crazypants.enderio.teleport.telepad.TileTelePad;

public class EIOBlocks {

  public static BlockFusedQuartz blockFusedQuartz;
  public static BlockIngotStorage blockIngotStorage;
  public static BlockDarkIronBars blockDarkIronBars;

  // Enderface
  public static BlockEnderIO blockEnderIo;

  // Teleporting
  public static BlockTravelAnchor blockTravelPlatform;
  public static BlockTelePad blockTelePad;

  // Painter
  public static BlockPainter blockPainter;
  public static BlockPaintedFence blockPaintedFence;
  public static BlockPaintedFenceGate blockPaintedFenceGate;
  public static BlockPaintedWall blockPaintedWall;
  public static BlockPaintedStair blockPaintedStair;
  public static BlockPaintedSlab blockPaintedSlab;
  public static BlockPaintedSlab blockPaintedDoubleSlab;
  public static BlockPaintedGlowstone blockPaintedGlowstone;
  public static BlockPaintedCarpet blockPaintedCarpet;

  // Conduits
  public static BlockConduitBundle blockConduitBundle;
  public static BlockConduitFacade blockConduitFacade;

  // Machines
  public static BlockStirlingGenerator blockStirlingGenerator;
  public static BlockCombustionGenerator blockCombustionGenerator;
  public static BlockZombieGenerator blockZombieGenerator;
  public static BlockSolarPanel blockSolarPanel;
  public static BlockReservoir blockReservoir;
  public static BlockAlloySmelter blockAlloySmelter;
  public static BlockCapacitorBank blockCapacitorBank;
  public static BlockCapBank blockCapBank;
  public static BlockWirelessCharger blockWirelessCharger;
  public static BlockCrusher blockCrusher;
  public static BlockHyperCube blockHyperCube;
  public static BlockPowerMonitor blockPowerMonitor;
  public static BlockVat blockVat;
  public static BlockFarmStation blockFarmStation;
  public static BlockTank blockTank;
  public static BlockCrafter blockCrafter;
  public static BlockPoweredSpawner blockPoweredSpawner;
  public static BlockSliceAndSplice blockSliceAndSplice;
  public static BlockSoulBinder blockSoulFuser;
  public static BlockAttractor blockAttractor;
  public static BlockSpawnGuard blockSpawnGuard;
  public static BlockExperienceObelisk blockExperianceOblisk;
  public static BlockWeatherObelisk blockWeatherObelisk;
  public static BlockTransceiver blockTransceiver;
  public static BlockBuffer blockBuffer;
  public static BlockKillerJoe blockKillerJoe;
  public static BlockEnchanter blockEnchanter;
  public static BlockElectricLight blockElectricLight;
  public static BlockLightNode blockLightNode;

  // Blocks
  public static BlockDarkSteelPressurePlate blockDarkSteelPressurePlate;
  public static BlockDarkSteelAnvil blockDarkSteelAnvil;
  public static BlockDarkSteelLadder blockDarkSteelLadder;
  public static BlockEndermanSkull blockEndermanSkull;
  public static BlockReinforcedObsidian blockReinforcedObsidian;
  public static BlockEnderRail blockEnderRail;
  public static BlockVacuumChest blockVacuumChest;

  public static void registerBlocks() {
    blockStirlingGenerator = BlockStirlingGenerator.create();
    blockCombustionGenerator = BlockCombustionGenerator.create();
    blockZombieGenerator = BlockZombieGenerator.create();
    blockSolarPanel = BlockSolarPanel.create();
    blockCrusher = BlockCrusher.create();
    blockAlloySmelter = BlockAlloySmelter.create();
    blockCapacitorBank = BlockCapacitorBank.create();
    blockCapBank = BlockCapBank.create();
    blockPainter = BlockPainter.create();
    blockPaintedFence = BlockPaintedFence.create();
    blockPaintedFenceGate = BlockPaintedFenceGate.create();
    blockPaintedWall = BlockPaintedWall.create();
    blockPaintedStair = BlockPaintedStair.create();
    blockPaintedSlab = new BlockPaintedSlab(false);
    blockPaintedSlab.init();
    blockPaintedDoubleSlab = new BlockPaintedSlab(true);
    blockPaintedDoubleSlab.init();
    blockCrafter = BlockCrafter.create();
    blockPaintedGlowstone = BlockPaintedGlowstone.create();
    blockPaintedCarpet = BlockPaintedCarpet.create();
    blockVat = BlockVat.create();
    blockPowerMonitor = BlockPowerMonitor.create();
    blockFarmStation = BlockFarmStation.create();
    blockWirelessCharger = BlockWirelessCharger.create();
    blockHyperCube = BlockHyperCube.create();
    blockTank = BlockTank.create();
    blockReservoir = BlockReservoir.create();
    blockVacuumChest = BlockVacuumChest.create();
    blockTransceiver = BlockTransceiver.create();
    blockBuffer = BlockBuffer.create();
    blockEnderIo = BlockEnderIO.create();
    blockTravelPlatform = BlockTravelAnchor.create();
    blockTelePad = BlockTelePad.create();
    blockSliceAndSplice = BlockSliceAndSplice.create();
    blockSoulFuser = BlockSoulBinder.create();
    blockPoweredSpawner = BlockPoweredSpawner.create();
    blockKillerJoe = BlockKillerJoe.create();
    blockAttractor = BlockAttractor.create();
    blockSpawnGuard = BlockSpawnGuard.create();
    blockExperianceOblisk = BlockExperienceObelisk.create();
    blockWeatherObelisk = BlockWeatherObelisk.create();
    blockEnchanter = BlockEnchanter.create();
    blockDarkSteelPressurePlate = BlockDarkSteelPressurePlate.create();
    blockDarkSteelAnvil = BlockDarkSteelAnvil.create();
    blockDarkSteelLadder = BlockDarkSteelLadder.create();
    blockElectricLight = BlockElectricLight.create();
    blockLightNode = BlockLightNode.create();
    blockReinforcedObsidian = BlockReinforcedObsidian.create();
    blockFusedQuartz = BlockFusedQuartz.create();
    blockEnderRail = BlockEnderRail.create();
    blockConduitBundle = BlockConduitBundle.create();
    blockConduitFacade = BlockConduitFacade.create();
    blockEndermanSkull = BlockEndermanSkull.create();
    blockIngotStorage = BlockIngotStorage.create();
    blockDarkIronBars = BlockDarkIronBars.create();
  }

  @SideOnly(Side.CLIENT)
  public static void registerBlockRenderers() {
    AbstractMachineBlock.renderId = RenderingRegistry.getNextAvailableRenderId();
    AbstractMachineRenderer machRen = new AbstractMachineRenderer();
    RenderingRegistry.registerBlockHandler(machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockStirlingGenerator), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockCrusher), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockAlloySmelter), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPowerMonitor), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPainter), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockCrafter), machRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockBuffer), machRen);
  
    BlockSolarPanel.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new SolarPanelRenderer());
  
    EnchanterModelRenderer emr = new EnchanterModelRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnchanter.class, emr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockEnchanter), emr);
  
    BlockFusedQuartz.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new FusedQuartzRenderer());
  
    BlockFarmStation.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new FarmingStationRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileFarmStation.class, new FarmingStationSpecialRenderer());
  
    BlockSoulBinder.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new SoulBinderRenderer());
  
    BlockAttractor.renderId = RenderingRegistry.getNextAvailableRenderId();
    ObeliskRenderer<TileAttractor> attRen = new ObeliskRenderer<TileAttractor>(new ItemStack(EIOItems.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal()));
    RenderingRegistry.registerBlockHandler(attRen);
    ClientRegistry.bindTileEntitySpecialRenderer(TileAttractor.class, attRen);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockAttractor), attRen);
  
    SpawnGuardRenderer sgr = new SpawnGuardRenderer();
    BlockSpawnGuard.renderId = BlockAttractor.renderId;
    ClientRegistry.bindTileEntitySpecialRenderer(TileSpawnGuard.class, sgr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockSpawnGuard), sgr);
  
    ObeliskRenderer<TileExperienceOblisk> eor = new ObeliskRenderer<TileExperienceOblisk>(new ItemStack(EIOItems.itemXpTransfer));
    BlockExperienceObelisk.renderId = BlockAttractor.renderId;
    ClientRegistry.bindTileEntitySpecialRenderer(TileExperienceOblisk.class, eor);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockExperianceOblisk), eor);
  
    ObeliskRenderer<TileWeatherObelisk> twr = new ObeliskRenderer<TileWeatherObelisk>(
        new ItemStack(EIOItems.itemMaterial, 1, Material.WEATHER_CRYSTAL.ordinal()));
    BlockWeatherObelisk.renderId = BlockAttractor.renderId;
    ClientRegistry.bindTileEntitySpecialRenderer(TileWeatherObelisk.class, twr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockWeatherObelisk), twr);
  
    BlockCombustionGenerator.renderId = RenderingRegistry.getNextAvailableRenderId();
    TechneMachineRenderer<TileCombustionGenerator> cr = new TechneMachineRenderer<TileCombustionGenerator>(
        blockCombustionGenerator, "models/combustionGen");
    RenderingRegistry.registerBlockHandler(cr);
  
    ZombieGeneratorRenderer zgr = new ZombieGeneratorRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, zgr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockZombieGenerator), zgr);
  
    KillerJoeRenderer kjr = new KillerJoeRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileKillerJoe.class, kjr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockKillerJoe), kjr);
  
    BlockVat.renderId = RenderingRegistry.getNextAvailableRenderId();
    TechneMachineRenderer<TileVat> vr = new TechneMachineRenderer<TileVat>(blockVat, "models/vat");
    RenderingRegistry.registerBlockHandler(vr);
  
    BlockElectricLight.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new ElectricLightRenderer());
  
    if (blockCapBank != null) {
      BlockCapBank.renderId = RenderingRegistry.getNextAvailableRenderId();
      CapBankRenderer newCbr = new CapBankRenderer();
      RenderingRegistry.registerBlockHandler(newCbr);
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockCapBank), newCbr);
      ClientRegistry.bindTileEntitySpecialRenderer(TileCapBank.class, newCbr);
    }
  
    CapacitorBankRenderer capr = new CapacitorBankRenderer();
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockCapacitorBank), capr);
  
    BlockCapacitorBank.renderId = RenderingRegistry.getNextAvailableRenderId();
    CapBankRenderer2 cbr2 = new CapBankRenderer2();
    RenderingRegistry.registerBlockHandler(cbr2);
  
    BlockVacuumChest.renderId = RenderingRegistry.getNextAvailableRenderId();
    VacuumChestRenderer vcr = new VacuumChestRenderer();
    RenderingRegistry.registerBlockHandler(vcr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockVacuumChest), vcr);
  
    BlockPaintedFenceGateRenderer bcfgr = new BlockPaintedFenceGateRenderer();
    BlockPaintedFenceGate.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(bcfgr);
  
    PaintedItemRenderer pir = new PaintedItemRenderer();
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPaintedFence), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPaintedFenceGate), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPaintedWall), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPaintedStair), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPaintedSlab), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPaintedGlowstone), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockPaintedCarpet), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockDarkSteelPressurePlate), pir);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockTravelPlatform), pir);
  
    BlockPaintedGlowstone.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new PaintedBlockRenderer(BlockPaintedGlowstone.renderId, Blocks.glowstone));
  
    BlockTravelAnchor.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new PaintedBlockRenderer(BlockTravelAnchor.renderId, blockTravelPlatform));
  
    BlockTelePad.renderId = RenderingRegistry.getNextAvailableRenderId();
    RenderingRegistry.registerBlockHandler(new TelePadRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileTelePad.class, new TelePadSpecialRenderer());
  
    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer());
  
    BlockEndermanSkull.renderId = RenderingRegistry.getNextAvailableRenderId();
    EndermanSkullRenderer esk = new EndermanSkullRenderer();
    RenderingRegistry.registerBlockHandler(esk);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockEndermanSkull), esk);
  
    EnderIoRenderer eior = new EnderIoRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnderIO.class, eior);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockEnderIo), eior);
  
    ClientRegistry.bindTileEntitySpecialRenderer(TileReservoir.class, new ReservoirRenderer(blockReservoir));
    ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankFluidRenderer());
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockTank), new TankItemRenderer());
  
    HyperCubeRenderer hcr = new HyperCubeRenderer();
    ClientRegistry.bindTileEntitySpecialRenderer(TileHyperCube.class, hcr);
    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockHyperCube), hcr);
  
    if(Config.transceiverEnabled) {
      TransceiverRenderer tr = new TransceiverRenderer();
      ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, tr);
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockTransceiver), tr);
    }
  }

}
