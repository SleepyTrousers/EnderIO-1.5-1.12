package info.loenwind.autosave.handlers.minecraft;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.paint.PainterUtil2;

public class HandleIBlockState implements IHandler<IBlockState> {

  public HandleIBlockState() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return IBlockState.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull IBlockState object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    PainterUtil2.writeNbt(tag, object);
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public IBlockState read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nullable IBlockState object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return PainterUtil2.readNbt(nbt.getCompoundTag(name));
  }

}
