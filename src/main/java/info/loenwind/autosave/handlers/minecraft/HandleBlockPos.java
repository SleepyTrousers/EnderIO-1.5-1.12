package info.loenwind.autosave.handlers.minecraft;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.HandleArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class HandleBlockPos implements IHandler<BlockPos> {

  public HandleBlockPos() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return BlockPos.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull BlockPos object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setLong(name, object.toLong());
    return true;
  }

  @Override
  public BlockPos read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable BlockPos object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      return BlockPos.fromLong(nbt.getLong(name));
    }
    return object;
  }

  public static class HandleBlockPosList extends HandleArrayList<BlockPos> {

    public HandleBlockPosList() {
      super(new HandleBlockPos());
    }

  }

}
