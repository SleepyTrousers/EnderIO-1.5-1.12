package info.loenwind.autosave.handlers.enderio;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.machine.monitor.StatCollector;

public class HandleStatCollector implements IHandler<StatCollector> {

  public HandleStatCollector() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return StatCollector.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull StatCollector object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setByteArray("data", object.getData());
    tag.setInteger("collectCount", object.getCollectCount());
    tag.setInteger("pos", object.getPos());
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public StatCollector read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nullable StatCollector object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (object != null && nbt.hasKey(name)) {
      NBTTagCompound tag = nbt.getCompoundTag(name);
      object.setData(tag.getByteArray("data"));
      object.setCollectCount(tag.getInteger("collectCount"));
      object.setPos(tag.getInteger("pos"));
    }
    return object;
  }

}
