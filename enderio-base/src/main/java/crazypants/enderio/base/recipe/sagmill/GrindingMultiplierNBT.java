package crazypants.enderio.base.recipe.sagmill;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class GrindingMultiplierNBT implements IGrindingMultiplier, IHandler<IGrindingMultiplier> {

  private float chanceMultiplier = 1;

  private float powerMultiplier = 1;

  private float grindingMultiplier = 1;

  private int durationMJ;

  private static @Nonnull String CM = "grindBall.chanceMultiplier";
  private static @Nonnull String PM = "grindBall.powerMultiplier";
  private static @Nonnull String GM = "grindBall.grindingMultiplier";
  private static @Nonnull String DMJ = "grindBall.durationMJ";

  protected GrindingMultiplierNBT(float chanceMultiplier, float powerMultiplier, float grindingMultiplier, int durationMJ) {
    this.chanceMultiplier = chanceMultiplier;
    this.powerMultiplier = powerMultiplier;
    this.grindingMultiplier = grindingMultiplier;
    this.durationMJ = durationMJ;
  }

  @Override
  public float getGrindingMultiplier() {
    return grindingMultiplier;
  }

  @Override
  public float getChanceMultiplier() {
    return chanceMultiplier;
  }

  @Override
  public float getPowerMultiplier() {
    return powerMultiplier;
  }

  @Override
  public void setChanceMultiplier(float chanceMultiplier) {
    this.chanceMultiplier = chanceMultiplier;
  }

  @Override
  public void setPowerMultiplier(float powerMultiplier) {
    this.powerMultiplier = powerMultiplier;
  }

  @Override
  public void setGrindingMultiplier(float grindingMultiplier) {
    this.grindingMultiplier = grindingMultiplier;
  }

  @Override
  public int getDurability() {
    return durationMJ;
  }

  @Override
  public void setDurability(int durationMJ) {
    this.durationMJ = durationMJ;
  }

  public GrindingMultiplierNBT() {
  }

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
