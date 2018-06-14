package crazypants.enderio.invpanel.init;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.IModTileEntity;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.invpanel.EnderIOInvPanel;
import crazypants.enderio.invpanel.chest.TileInventoryChest;
import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
import crazypants.enderio.invpanel.sensor.TileInventoryPanelSensor;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOInvPanel.MODID)
public enum InvpanelTileEntity implements IModTileEntity {

  TileInventoryPanel(TileInventoryPanel.class),
  TileInventoryPanelSensor(TileInventoryPanelSensor.class),

  // Warehouses
  TileInventoryChestTiny(TileInventoryChest.Tiny.class),
  TileInventoryChestSmall(TileInventoryChest.Small.class),
  TileInventoryChestMedium(TileInventoryChest.Medium.class),
  TileInventoryChestBig(TileInventoryChest.Big.class),
  TileInventoryChestLarge(TileInventoryChest.Large.class),
  TileInventoryChestHuge(TileInventoryChest.Huge.class),
  TileInventoryChestEnormous(TileInventoryChest.Enormous.class),
  TileInventoryChestWarehouse(TileInventoryChest.Warehouse.class),
  TileInventoryChestWarehouse13(TileInventoryChest.Warehouse13.class),

  ;

  private final @Nonnull String unlocalisedName;
  private final @Nonnull Class<? extends TileEntity> teClass;

  private InvpanelTileEntity(@Nonnull Class<? extends TileEntity> teClass) {
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
    ModObjectRegistry.addModTileEntities(InvpanelTileEntity.class);
  }

}
