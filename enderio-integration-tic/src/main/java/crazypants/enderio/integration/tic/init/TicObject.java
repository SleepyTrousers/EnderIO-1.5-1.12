package crazypants.enderio.integration.tic.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.IModTileEntity;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.integration.tic.book.ItemEioBook;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public enum TicObject implements IModObject.Registerable {

  item_eio_book(ItemEioBook.class),

  ;

  final @Nonnull String unlocalisedName;

  protected @Nullable Item item;

  protected final @Nonnull Class<?> clazz;

  private TicObject(@Nonnull Class<?> clazz) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
  }

  @Override
  public final @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  @Override
  public final @Nullable Item getItem() {
    return item;
  }

  @Override
  public final @Nonnull Class<?> getClazz() {
    return clazz;
  }

  @Override
  public final String getBlockMethodName() {
    return null;
  }

  @Override
  public final String getItemMethodName() {
    return "create";
  }

  @Override
  public final void setItem(@Nullable Item obj) {
    item = obj;
  }

  @Override
  public final void setBlock(@Nullable Block obj) {
  }

  @Override
  @Nullable
  public IModTileEntity getTileEntity() {
    return null;
  }

}
