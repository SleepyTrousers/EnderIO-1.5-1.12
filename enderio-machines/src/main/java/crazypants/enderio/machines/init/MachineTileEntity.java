package crazypants.enderio.machines.init;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.IModTileEntity;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.init.ModTileEntity;
import crazypants.enderio.machines.machine.buffer.TileBuffer;
import crazypants.enderio.machines.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machines.machine.light.TileElectricLight;
import crazypants.enderio.machines.machine.light.TileLightNode;
import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import crazypants.enderio.machines.machine.obelisk.aversion.TileAversionObelisk;
import crazypants.enderio.machines.machine.obelisk.inhibitor.TileInhibitorObelisk;
import crazypants.enderio.machines.machine.obelisk.weather.TileWeatherObelisk;
import crazypants.enderio.machines.machine.obelisk.xp.TileExperienceObelisk;
import crazypants.enderio.machines.machine.reservoir.TileReservoir;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    ModObjectRegistry.addModTileEntities(ModTileEntity.class);
  }

}
