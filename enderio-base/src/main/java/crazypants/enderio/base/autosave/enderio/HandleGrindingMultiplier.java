package crazypants.enderio.base.autosave.enderio;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.recipe.sagmill.GrindingMultiplierNBT;
import crazypants.enderio.base.recipe.sagmill.IGrindingMultiplier;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class HandleGrindingMultiplier implements IHandler<IGrindingMultiplier> {

  private static String CM = "grindBall.chanceMultiplier";
  private static String PM = "grindBall.powerMultiplier";
  private static String GM = "grindBall.grindingMultiplier";
  private static String DMJ = "grindBall.durationMJ";
  
  @Override
  public @Nonnull Class<?> getRootType() {
    return IGrindingMultiplier.class;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type, @Nonnull String name,
      @Nonnull IGrindingMultiplier object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setFloat(CM, object.getChanceMultiplier());
    tag.setFloat(PM, object.getPowerMultiplier());
    tag.setFloat(GM, object.getGrindingMultiplier());
    tag.setInteger(DMJ, object.getDurability());
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  @Nullable
  public IGrindingMultiplier read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type,
      @Nonnull String name, @Nullable IGrindingMultiplier object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      NBTTagCompound tag = (NBTTagCompound) nbt.getTag(name);
      if (tag.hasKey(CM) && tag.hasKey(PM) && tag.hasKey(GM) && tag.hasKey(DMJ)) {
        return new GrindingMultiplierNBT(tag.getFloat(CM), tag.getFloat(PM), tag.getFloat(GM), tag.getInteger(DMJ));
      }
    }
    return null;
  }

}
