package crazypants.enderio.conduit;

import com.enderio.core.common.NBTAction;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.HandleArrayList;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Set;

public class ConduitHandler implements IHandler<IConduit> {

  private IConduit conduit;

  static {
    Registry.GLOBAL_REGISTRY.register(new ConduitHandler());
  }

  public ConduitHandler() {
    this(null);
  }

  public ConduitHandler(IConduit conduit) {
    this.conduit = conduit;
  }

  public IConduit getConduit() {
    return conduit;
  }

  public void setConduit(IConduit conduit) {
    this.conduit = conduit;
  }

  public void writeToNBT(NBTTagCompound nbtRoot) {
    NBTTagCompound conduitRoot = new NBTTagCompound();
    ConduitUtil.writeToNBT(conduit, conduitRoot);
    NbtValue.CONDUIT.setTag(nbtRoot, conduitRoot);
  }

  @Nullable
  public static IConduit readFromNBT(NBTTagCompound nbtRoot) {
    if (!NbtValue.CONDUIT.hasTag(nbtRoot)) {
      return null;
    }
    return ConduitUtil.readConduitFromNBT(nbtRoot);
  }

  @Override
  public boolean canHandle(Class clazz) {
    return ConduitHandler.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull IConduit object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound root = new NBTTagCompound();
    object.writeToNBT(root);
    nbt.setTag(name, root);
    return true;
  }

  @Override
  public IConduit read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field,
      @Nonnull String name, @Nullable IConduit object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      NBTTagCompound root = nbt.getCompoundTag(name);
      return readFromNBT(root);
    }
    return null;
  }

  public static class ConduitArrayListHandler extends HandleArrayList<IConduit>
  {
    public ConduitArrayListHandler() {
      super(new ConduitHandler());
    }
  }
}
