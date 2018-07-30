package crazypants.enderio.powertools.init;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.IModTileEntityBase;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.conduits.EnderIOConduits;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import crazypants.enderio.powertools.machine.gauge.TileGauge;
import crazypants.enderio.powertools.machine.monitor.TilePowerMonitor;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODID)
public enum PowerToolTileEntity implements IModTileEntityBase {

  TileCapBank(TileCapBank.class),
  TileGauge(TileGauge.class),
  TilePowerMonitor(TilePowerMonitor.class),
  
  ;

  private final @Nonnull String unlocalisedName;
  private final @Nonnull Class<? extends TileEntity> teClass;

  private PowerToolTileEntity(@Nonnull Class<? extends TileEntity> teClass) {
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
    ModObjectRegistry.addModTileEntities(PowerToolTileEntity.class);
  }

}
