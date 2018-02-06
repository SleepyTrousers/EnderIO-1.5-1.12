package crazypants.enderio.base.init;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.block.painted.TileEntityPaintedBlock;
import crazypants.enderio.base.block.painted.TileEntityTwicePaintedBlock;
import crazypants.enderio.base.block.painted.TilePaintedPressurePlate;
import crazypants.enderio.base.block.skull.TileEndermanSkull;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public enum ModTileEntity implements IModTileEntity {
  TileEntityTwicePaintedBlock(TileEntityTwicePaintedBlock.class),
  TileEntityPaintedBlock(TileEntityPaintedBlock.class),
  TilePaintedPressurePlate(TilePaintedPressurePlate.class),
  TileEndermanSkull(TileEndermanSkull.class),

  ;

  private final @Nonnull String unlocalisedName;
  private final @Nonnull Class<? extends TileEntity> teClass;

  private ModTileEntity(@Nonnull Class<? extends TileEntity> teClass) {
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
  public ResourceLocation getRegistryName() {
    return new ResourceLocation(EnderIO.DOMAIN, unlocalisedName);
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
