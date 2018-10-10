package crazypants.enderio.util;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.paint.PaintUtil;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Legacy handling for PaintUtil methods to save/load IBlockState. Prevents
 * breaking change after extracting AutoSave to a library.
 */
public class HandlePaintSource implements IHandler<IBlockState> {

  @Override
  public @Nonnull Class<?> getRootType() {
    return IBlockState.class;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type, @Nonnull String name, @Nonnull IBlockState object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    PaintUtil.writeNbt(tag, object);
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public IBlockState read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type, @Nonnull String name,
      @Nullable IBlockState object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return PaintUtil.readNbt(nbt.getCompoundTag(name));
  }
}
