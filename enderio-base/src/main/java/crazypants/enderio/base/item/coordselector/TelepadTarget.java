package crazypants.enderio.base.item.coordselector;

import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.autosave.handlers.EIOHandlers;
import crazypants.enderio.util.NbtValue;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class TelepadTarget implements IHandler<TelepadTarget> {

  static {
    EIOHandlers.REGISTRY.register(new TelepadTarget());
  }

  private @Nonnull BlockPos location;
  private int dimension;
  private @Nonnull String name;
  private @Nonnull ItemStack icon;

  public TelepadTarget() {
    this(new BlockPos(0, -1, 0), Integer.MIN_VALUE);
  }

  public TelepadTarget(@Nonnull BlockPos location, int dimension) {
    this(location, dimension, "", Prep.getEmpty());
  }

  public TelepadTarget(@Nonnull BlockPos location, int dimension, @Nonnull String name, @Nonnull ItemStack icon) {
    this.location = location;
    this.dimension = dimension;
    this.name = name;
    this.icon = icon;
  }

  public TelepadTarget(@Nonnull TelepadTarget newTarget) {
    this(newTarget.location, newTarget.dimension, newTarget.name, newTarget.icon);
  }

  public @Nonnull BlockPos getLocation() {
    return location;
  }

  public int getDimension() {
    return dimension;
  }

  public @Nonnull String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  public @Nonnull ItemStack getIcon() {
    return icon;
  }

  public void setIcon(@Nonnull ItemStack icon) {
    this.icon = icon;
  }

  public int getX() {
    return location.getX();
  }

  public TelepadTarget setX(int x) {
    location = new BlockPos(x, getY(), getZ());
    return this;
  }

  public int getY() {
    return location.getY();
  }

  public TelepadTarget setY(int y) {
    location = new BlockPos(getX(), y, getZ());
    return this;
  }

  public int getZ() {
    return location.getZ();
  }

  public TelepadTarget setZ(int z) {
    location = new BlockPos(getX(), getY(), z);
    return this;
  }

  public TelepadTarget setLocation(@Nonnull BlockPos pos) {
    location = pos;
    return this;
  }

  public TelepadTarget setDimension(int dimension) {
    this.dimension = dimension;
    return this;
  }

  public @Nonnull String getChatString() {
    String res = "";
    if (!name.isEmpty()) {
      res += name + " ";
    }
    res += BlockCoord.chatString(location, TextFormatting.WHITE) + " ";
    res += getDimenionName(dimension);
    return res;
  }

  public @Nonnull String getDimenionName() {
    return getDimenionName(dimension);
  }

  public static @Nonnull String getDimenionName(int dim) {
    if (!DimensionManager.isDimensionRegistered(dim)) {
      return Integer.toString(dim);
    }
    DimensionType type = DimensionManager.getProviderType(dim);
    if (type == null) {
      return Integer.toString(dim);
    }
    String name = type.getName();
    int[] dims = DimensionManager.getDimensions(type);
    if (dims != null && dims.length > 1) {
      name += " " + dim;
    }
    return name;
  }

  public boolean isValid() {
    return location.getY() >= 0;
  }

  @Override
  public @Nonnull String toString() {
    String res = "";
    if (!name.isEmpty()) {
      res += " " + name + " ";
    }
    res += location + " " + dimension;
    return res;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + dimension;
    result = prime * result + icon.hashCode();
    result = prime * result + location.hashCode();
    result = prime * result + name.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TelepadTarget other = (TelepadTarget) obj;
    if (dimension != other.dimension)
      return false;
    if (!ItemStack.areItemStacksEqual(icon, other.icon))
      return false;
    if (!location.equals(other.location))
      return false;
    if (!name.equals(other.name))
      return false;
    return true;
  }

  // ----------------------- I/O -------------- :(

  public void writeToNBT(@Nonnull ItemStack stack) {
    writeToNBT(NbtValue.getOrCreateRoot(stack));
    if (!getName().isEmpty()) {
      stack.setStackDisplayName(getName());
    } else {
      stack.clearCustomName();
    }
  }

  public void writeToNBT(@Nonnull NBTTagCompound tag) {
    NbtValue.REMOTE_POS.setBlockPos(tag, location);
    NbtValue.REMOTE_D.setInt(tag, dimension);
    NbtValue.REMOTE_NAME.setString(tag, name);
    NbtValue.REMOTE_ICON.setStack(tag, icon);
  }

  public static @Nullable TelepadTarget readFromNBT(@Nonnull ItemStack stack) {
    return readFromNBT(NbtValue.getReadOnlyRoot(stack));
  }

  public static @Nullable TelepadTarget readFromNBT(@Nonnull NBTTagCompound tag) {
    if (!NbtValue.REMOTE_POS.hasTag(tag)) {
      return null;
    }
    
    return new TelepadTarget(NbtValue.REMOTE_POS.getBlockPos(tag), NbtValue.REMOTE_D.getInt(tag), NbtValue.REMOTE_NAME.getString(tag, ""),
        NbtValue.REMOTE_ICON.getStack(tag));
  }

  @Override
  public @Nonnull Class<?> getRootType() {
    return TelepadTarget.class;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type, @Nonnull String name1,
      @Nonnull TelepadTarget object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound root = new NBTTagCompound();
    object.writeToNBT(root);
    nbt.setTag(name1, root);
    return true;
  }

  @Override
  public TelepadTarget read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull Type type,
      @Nonnull String name1, @Nullable TelepadTarget object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name1)) {
      NBTTagCompound root = nbt.getCompoundTag(name1);
      return readFromNBT(root);
    }
    return new TelepadTarget();
  }
}
