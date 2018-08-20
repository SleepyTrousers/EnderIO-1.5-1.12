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

  public static @Nonnull ModObjectData create(@Nullable String defaultName, NamedParameter<?>... params) {
    String unlocalisedName = null, blockMethodName = null, itemMethodName = null;
    Class<?> clazz = null;
    IModTileEntity modTileEntity = null;
    for (NamedParameter<?> param : params) {
      if (param instanceof NamedParameter.UnlocName) {
        assertUnset(unlocalisedName, "unlocalisedName");
        unlocalisedName = ((NamedParameter.UnlocName) param).getValue();
      } else if (param instanceof NamedParameter.Clazz) {
        assertUnset(clazz, "clazz");
        clazz = ((NamedParameter.Clazz) param).getValue();
      } else if (param instanceof NamedParameter.BlockName) {
        assertUnset(blockMethodName, "blockMethodName");
        blockMethodName = ((NamedParameter.BlockName) param).getValue();
      } else if (param instanceof NamedParameter.ItemName) {
        assertUnset(itemMethodName, "itemMethodName");
        itemMethodName = ((NamedParameter.ItemName) param).getValue();
      } else if (param instanceof NamedParameter.TeObject) {
        assertUnset(modTileEntity, "modTileEntity");
        modTileEntity = ((NamedParameter.TeObject) param).getValue();
      } else if (param instanceof NamedParameter.AnyName) {
        if (Block.class.isAssignableFrom(clazz)) {
          assertUnset(blockMethodName, "blockMethodName");
          blockMethodName = ((NamedParameter.BlockName) param).getValue();
        }
        if (Item.class.isAssignableFrom(clazz)) {
          assertUnset(itemMethodName, "itemMethodName");
          itemMethodName = ((NamedParameter.AnyName) param).getValue();
        }
      } else {
        throw new RuntimeException("Internal logic error: " + param + " is not a valid parameter");
      }
    }
    if (unlocalisedName == null) {
      unlocalisedName = defaultName;
    }
    if (blockMethodName == null && itemMethodName == null) {
      throw new RuntimeException("Internal logic error: Class " + clazz + " unexpectedly is neither a Block nor an Item.");
    }
    return new ModObjectData(assertSet(unlocalisedName, "unlocalisedName"), assertSet(clazz, "clazz"), blockMethodName, itemMethodName, modTileEntity);
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

  public static class NamedParameter<T> {
    private final @Nonnull T value;

    @Nonnull
    T getValue() {
      return value;
    }

    protected NamedParameter(@Nonnull T value) {
      this.value = value;
    }

    public static class UnlocName extends NamedParameter<String> {
      protected UnlocName(@Nonnull String value) {
        super(value);
      }
    }

    public static class Clazz extends NamedParameter<Class<?>> {
      protected Clazz(@Nonnull Class<?> value) {
        super(value);
      }
    }

    public static class AnyName extends NamedParameter<String> {
      protected AnyName(@Nonnull String value) {
        super(value);
      }
    }

    public static class BlockName extends NamedParameter<String> {
      protected BlockName(@Nonnull String value) {
        super(value);
      }
    }

    public static class ItemName extends NamedParameter<String> {
      protected ItemName(@Nonnull String value) {
        super(value);
      }
    }

    public static class TeObject extends NamedParameter<IModTileEntity> {
      protected TeObject(@Nonnull IModTileEntity value) {
        super(value);
      }
    }

  }

  static NamedParameter<?> unlocalizedName(@Nonnull String unlocalisedName) {
    return new NamedParameter.UnlocName(unlocalisedName);
  }

  static NamedParameter<?> clazz(@Nonnull Class<?> clazz) {
    return new NamedParameter.Clazz(clazz);
  }

  static NamedParameter<?> method(@Nonnull String methodName) {
    return new NamedParameter.AnyName(methodName);
  }

  static NamedParameter<?> blockMethod(@Nonnull String blockMethodName) {
    return new NamedParameter.BlockName(blockMethodName);
  }

  static NamedParameter<?> itemMethod(@Nonnull String itemMethodName) {
    return new NamedParameter.ItemName(itemMethodName);
  }

  static NamedParameter<?> tileEntity(@Nonnull IModTileEntity modTileEntity) {
    return new NamedParameter.TeObject(modTileEntity);
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
