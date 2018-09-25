package crazypants.enderio.autosave.handlers.endercore;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

import com.enderio.core.common.fluid.SmartTank;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class HandleSmartTank implements IHandler<SmartTank> {

  public HandleSmartTank() {
  }

  @Override
  public Class<?> getRootType() {
    return SmartTank.class;
  }

  @Override
  public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name, SmartTank object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    object.writeCommon(name, nbt);
    return true;
  }

  @Override
  @Nullable
  public SmartTank read(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      @Nullable SmartTank object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      if (object != null) {
        object.readCommon(name, nbt);
      } else {
        object = SmartTank.createFromNBT(name, nbt);
      }
    }
    return object;
  }

}
