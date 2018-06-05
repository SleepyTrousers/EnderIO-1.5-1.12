package crazypants.enderio.machines.init;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.IModTileEntity;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.machine.alloy.TileAlloySmelter;
import crazypants.enderio.machines.machine.buffer.TileBuffer;
import crazypants.enderio.machines.machine.crafter.TileCrafter;
import crazypants.enderio.machines.machine.enchanter.TileEnchanter;
import crazypants.enderio.machines.machine.farm.TileFarmStation;
import crazypants.enderio.machines.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machines.machine.generator.stirling.TileStirlingGenerator;
import crazypants.enderio.machines.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machines.machine.ihopper.TileImpulseHopper;
import crazypants.enderio.machines.machine.killera.TileKillerJoe;
import crazypants.enderio.machines.machine.light.TileElectricLight;
import crazypants.enderio.machines.machine.light.TileLightNode;
import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import crazypants.enderio.machines.machine.obelisk.aversion.TileAversionObelisk;
import crazypants.enderio.machines.machine.obelisk.inhibitor.TileInhibitorObelisk;
import crazypants.enderio.machines.machine.obelisk.relocator.TileRelocatorObelisk;
import crazypants.enderio.machines.machine.obelisk.weather.TileWeatherObelisk;
import crazypants.enderio.machines.machine.obelisk.xp.TileExperienceObelisk;
import crazypants.enderio.machines.machine.painter.TileEntityPainter;
import crazypants.enderio.machines.machine.reservoir.TileReservoir;
import crazypants.enderio.machines.machine.sagmill.TileSagMill;
import crazypants.enderio.machines.machine.slicensplice.TileSliceAndSplice;
import crazypants.enderio.machines.machine.solar.TileSolarPanel;
import crazypants.enderio.machines.machine.soul.TileSoulBinder;
import crazypants.enderio.machines.machine.spawner.TilePoweredSpawner;
import crazypants.enderio.machines.machine.tank.TileTank;
import crazypants.enderio.machines.machine.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.transceiver.TileTransceiver;
import crazypants.enderio.machines.machine.vacuum.chest.TileVacuumChest;
import crazypants.enderio.machines.machine.vacuum.xp.TileXPVacuum;
import crazypants.enderio.machines.machine.vat.TileVat;
import crazypants.enderio.machines.machine.wired.TileWiredCharger;
import crazypants.enderio.machines.machine.wireless.TileWirelessCharger;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public enum MachineTileEntity implements IModTileEntity {

  TileBufferAbstract(TileBuffer.class),
  TileBufferItem(TileBuffer.TileBufferItem.class),
  TileBufferPower(TileBuffer.TileBufferPower.class),
  TileBufferOmni(TileBuffer.TileBufferOmni.class),
  TileBufferCreative(TileBuffer.TileBufferCreative.class),
  TileZombieGenerator(TileZombieGenerator.class),
  TileExperienceObelisk(TileExperienceObelisk.class),
  TileWeatherObelisk(TileWeatherObelisk.class),
  TileAttractor(TileAttractor.class),
  TileAversionObelisk(TileAversionObelisk.class),
  TileDialingDevice(TileDialingDevice.class),
  TileElectricLight(TileElectricLight.class),
  TileLightNode(TileLightNode.class),
  TileReservoir(TileReservoir.class),
  TileInhibitorObelisk(TileInhibitorObelisk.class),
  TileRelocatorObelisk(TileRelocatorObelisk.class),
  TileKillerJoe(TileKillerJoe.class),
  TileTank(TileTank.class),
  TileTravelAnchor(TileTravelAnchor.class),
  TileEnchanter(TileEnchanter.class),
  TileWiredCharger(TileWiredCharger.class),
  TileAlloySmelter(TileAlloySmelter.class),
  TileAlloySmelterSimple(TileAlloySmelter.Simple.class),
  TileAlloySmelterEnhanced(TileAlloySmelter.Enhanced.class),
  TileSagMill(TileSagMill.Normal.class),
  TileSagMillSimple(TileSagMill.Simple.class),
  TileSagMillEnhanced(TileSagMill.Enhanced.class),
  TileEntityPainter(TileEntityPainter.class),
  TileFarmStation(TileFarmStation.class),
  TilePoweredSpawner(TilePoweredSpawner.class),
  TileSliceAndSplice(TileSliceAndSplice.class),
  TileSoulBinder(TileSoulBinder.class),
  TileTransceiver(TileTransceiver.class),
  TileVat(TileVat.class),
  TileSolarPanel(TileSolarPanel.class),
  TileVacuumChest(TileVacuumChest.class),
  TileWirelessCharger(TileWirelessCharger.class),
  TileXPVacuum(TileXPVacuum.class),
  TileStirlingGenerator(TileStirlingGenerator.class),
  TileStirlingGeneratorSimple(TileStirlingGenerator.Simple.class),
  TileTelePad(TileTelePad.class),
  TileCombustionGenerator(TileCombustionGenerator.class),
  TileCombustionGeneratorEnhanced(TileCombustionGenerator.Enhanced.class),
  TileImpulseHopper(TileImpulseHopper.class),
  TileCrafter(TileCrafter.class),
  TileSimpleCrafter(TileCrafter.Simple.class),

  ;

  private final @Nonnull String unlocalisedName;
  private final @Nonnull Class<? extends TileEntity> teClass;

  private MachineTileEntity(@Nonnull Class<? extends TileEntity> teClass) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.teClass = teClass;
  }

  @Override
  @Nonnull
  public String getUnlocalisedName() {
    return unlocalisedName;
  }

  @Override
  @Nonnull
  public Class<? extends TileEntity> getTileEntityClass() {
    return teClass;
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void registerBlocksEarly(@Nonnull RegistryEvent.Register<Block> event) {
    ModObjectRegistry.addModTileEntities(MachineTileEntity.class);
  }

}
