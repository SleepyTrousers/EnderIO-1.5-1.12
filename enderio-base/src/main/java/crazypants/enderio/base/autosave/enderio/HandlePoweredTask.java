package crazypants.enderio.base.autosave.enderio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.task.PoweredTaskProgress;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import info.loenwind.autosave.util.NullHelper;
import net.minecraft.nbt.NBTTagCompound;

public class HandlePoweredTask implements IHandler<IPoweredTask> {

  public HandlePoweredTask() {
  }

  @Override
  public Class<?> getRootType() {
    return IPoweredTask.class;
  }

  @Override
  public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name, IPoweredTask object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (!(object instanceof PoweredTaskProgress)) {
      NBTTagCompound tag = new NBTTagCompound();
      object.writeToNBT(tag);
      tag.setString("class", NullHelper.notnullJ(object.getClass().getName(), "Class#getName"));
      nbt.setTag(name, tag);
    }
    return true;
  }

  @Override
  @Nullable
  public IPoweredTask read(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name, @Nullable IPoweredTask object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      try {
        NBTTagCompound tag = (NBTTagCompound) nbt.getTag(name);
        String className = tag.getString("class");
        if (!className.isEmpty()) {
          Class<?> clazz = Class.forName(className);
          if (clazz != null) {
            Method method = clazz.getDeclaredMethod("readFromNBT", NBTTagCompound.class);
            if (method != null) {
              Object object2 = method.invoke(null, tag);
              if (object2 instanceof IPoweredTask) {
                return (IPoweredTask) object2;
              }
            }
          }
        }
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(e);
      } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException(e);
      } catch (SecurityException e) {
        throw new IllegalArgumentException(e);
      } catch (InvocationTargetException e) {
        throw new IllegalArgumentException(e);
      }
    }
    return null;
  }

}
