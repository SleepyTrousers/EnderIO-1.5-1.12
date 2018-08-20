package crazypants.enderio.base.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ModObjectData {

  private final @Nonnull String unlocalisedName;
  private final @Nonnull Class<?> clazz;
  private final @Nullable String blockMethodName, itemMethodName;
  private final @Nullable IModTileEntity modTileEntity;

  private ModObjectData(@Nonnull String unlocalisedName, @Nonnull Class<?> clazz, String blockMethodName, String itemMethodName, IModTileEntity modTileEntity) {
    this.unlocalisedName = unlocalisedName;
    this.clazz = clazz;
    this.blockMethodName = blockMethodName;
    this.itemMethodName = itemMethodName;
    this.modTileEntity = modTileEntity;
  }

  public static class Builder {
    private String unlocalisedNameB = null;
    private Class<?> clazzB = null;
    private String blockMethodNameB = null, itemMethodNameB = null;
    private IModTileEntity modTileEntityB = null;

    private Builder() {
    }

    public Builder unlocalisedName(@Nonnull String unlocalisedName) {
      assertUnset(unlocalisedName, "unlocalisedName");
      this.unlocalisedNameB = unlocalisedName;
      return this;
    }

    public Builder clazz(@Nonnull Class<?> clazz) {
      assertUnset(clazz, "clazz");
      this.clazzB = clazz;
      return this;
    }

    public Builder method(@Nonnull String methodName) {
      if (Block.class.isAssignableFrom(clazzB)) {
        assertUnset(blockMethodNameB, "blockMethodName");
        blockMethodNameB = methodName;
      }
      if (Item.class.isAssignableFrom(clazzB)) {
        assertUnset(itemMethodNameB, "itemMethodName");
        itemMethodNameB = methodName;
      }
      return this;
    }

    public Builder blockMethod(@Nonnull String blockMethodName) {
      assertUnset(blockMethodName, "blockMethodName");
      this.blockMethodNameB = blockMethodName;
      return this;
    }

    public Builder itemMethod(@Nonnull String itemMethodName) {
      assertUnset(itemMethodName, "itemMethodName");
      this.itemMethodNameB = itemMethodName;
      return this;
    }

    public Builder tileEntity(@Nonnull IModTileEntity modTileEntity) {
      assertUnset(modTileEntity, "modTileEntity");
      this.modTileEntityB = modTileEntity;
      return this;
    }

    public @Nonnull ModObjectData build() {
      return build(null);
    }

    public @Nonnull ModObjectData build(@Nullable String defaultName) {
      if (unlocalisedNameB == null) {
        unlocalisedNameB = defaultName;
      }
      if (blockMethodNameB == null && itemMethodNameB == null) {
        method("create");
      }
      if (blockMethodNameB == null && itemMethodNameB == null) {
        throw new RuntimeException("Internal logic error: Class " + clazzB + " unexpectedly is neither a Block nor an Item.");
      }
      return new ModObjectData(assertSet(unlocalisedNameB, "unlocalisedName"), assertSet(clazzB, "clazz"), blockMethodNameB, itemMethodNameB, modTileEntityB);
    }

  }

  private static void assertUnset(Object value, String name) {
    if (value != null) {
      throw new RuntimeException("Internal logic error: " + name + " is set twice");
    }
  }

  private static @Nonnull <T> T assertSet(T value, String name) {
    if (value == null) {
      throw new RuntimeException("Internal logic error: " + name + " is not set");
    }
    return value;
  }

  public static Builder unlocalizedName(@Nonnull String unlocalisedName) {
    return new Builder().unlocalisedName(unlocalisedName);
  }

  public static Builder clazz(@Nonnull Class<?> clazz) {
    return new Builder().clazz(clazz);
  }

  public static Builder method(@Nonnull String methodName) {
    return new Builder().method(methodName);
  }

  public static Builder blockMethod(@Nonnull String blockMethodName) {
    return new Builder().blockMethod(blockMethodName);
  }

  public static Builder itemMethod(@Nonnull String itemMethodName) {
    return new Builder().itemMethod(itemMethodName);
  }

  public static Builder tileEntity(@Nonnull IModTileEntity modTileEntity) {
    return new Builder().tileEntity(modTileEntity);
  }

  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  public @Nonnull Class<?> getClazz() {
    return clazz;
  }

  public String getBlockMethodName() {
    return blockMethodName;
  }

  public String getItemMethodName() {
    return itemMethodName;
  }

  public IModTileEntity getModTileEntity() {
    return modTileEntity;
  }

}
