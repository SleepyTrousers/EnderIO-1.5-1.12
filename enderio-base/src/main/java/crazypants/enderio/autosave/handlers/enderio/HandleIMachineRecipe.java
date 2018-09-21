package crazypants.enderio.autosave.handlers.enderio;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nullable;

import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class HandleIMachineRecipe implements IHandler<IMachineRecipe> {

  public HandleIMachineRecipe() {
  }

  @Override
  public Class<?> getRootType() {
    return IMachineRecipe.class;
  }

  @Override
  public boolean store(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      IMachineRecipe object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setString(name, object.getUid());
    return true;
  }

  @Override
  @Nullable
  public IMachineRecipe read(Registry registry, Set<NBTAction> phase, NBTTagCompound nbt, Type type, String name,
      @Nullable IMachineRecipe object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      return MachineRecipeRegistry.instance.getRecipeForUid(nbt.getString(name));
    }
    return object;
  }

}
