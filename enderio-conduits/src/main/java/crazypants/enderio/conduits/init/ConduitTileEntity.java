package crazypants.enderio.conduits.init;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.IModTileEntity;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.conduits.EnderIOConduits;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODID)
public enum ConduitTileEntity implements IModTileEntity {

  TileConduitBundle(TileConduitBundle.class),
  ;

  private final @Nonnull String unlocalisedName;
  private final @Nonnull Class<? extends TileEntity> teClass;

  private ConduitTileEntity(@Nonnull Class<? extends TileEntity> teClass) {
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
    ModObjectRegistry.addModTileEntities(ConduitTileEntity.class);
  }

}
