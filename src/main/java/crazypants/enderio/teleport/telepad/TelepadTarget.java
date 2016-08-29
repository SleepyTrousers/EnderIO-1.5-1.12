package crazypants.enderio.teleport.telepad;

import java.util.Set;

import com.enderio.core.common.util.BlockCoord;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.java.HandleArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class TelepadTarget implements IHandler<TelepadTarget> {

  static {
    Registry.GLOBAL_REGISTRY.register(new TelepadTarget());
  }
  
  private BlockPos location;
  private int dimension;
  private String name;
  private ItemStack icon;

  public TelepadTarget() {
    this(new BlockPos(0,-1,0), Integer.MIN_VALUE);
  }
  
  public TelepadTarget(BlockPos location, int dimension) {
    this(location, dimension, null, null);
  }

  public TelepadTarget(BlockPos location, int dimension, String name, ItemStack icon) {
    this.location = location;
    this.dimension = dimension;
    this.name = name;
    this.icon = icon;
  }

  public TelepadTarget(TelepadTarget newTarget) {
    this(newTarget.location, newTarget.dimension, newTarget.name, newTarget.icon);
  }

  public BlockPos getLocation() {
    return location;
  }

  public int getDimension() {
    return dimension;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ItemStack getIcon() {
    return icon;
  }

  public void setIcon(ItemStack icon) {
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
  
  public TelepadTarget setLocation(BlockPos pos) {
    if(pos == null) {
      pos = new BlockPos(0,-1,0);
    }
    location = pos;
    return this;
  }
  
  public TelepadTarget setDimension(int dimension) {
    this.dimension = dimension;
    return this;
  }
  
  public String getChatString() {
    String res = "";
    if(name != null) {
      res += name + " ";
    }
    res += BlockCoord.chatString(location) + " ";
    res += getDimenionName(dimension); 
    return res;
  }
  
  public String getDimenionName() {    
    return getDimenionName(dimension);
  }

  public static String getDimenionName(int dim) {
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
  public String toString() {
    String res = "";
    if(name != null) {
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
    result = prime * result + ((icon == null) ? 0 : icon.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    if (icon == null) {
      if (other.icon != null)
        return false;
    } else if (!icon.equals(other.icon))
      return false;
    if (location == null) {
      if (other.location != null)
        return false;
    } else if (!location.equals(other.location))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
    
  //----------------------- I/O -------------- :(

  public void writeToNBT(ItemStack printout) {
    if (printout == null) {
      return;
    }
    if (!printout.hasTagCompound()) {
      printout.setTagCompound(new NBTTagCompound());
    }
    writeToNBT(printout.getTagCompound());
    if (getName() != null) {
      printout.setStackDisplayName(getName());
    } else {
      printout.clearCustomName();
    }
  }

  public void writeToNBT(NBTTagCompound tag) {
    if (location != null) {
      tag.setLong("targetPos", location.toLong());
    }
    tag.setInteger("targetDim", dimension);
    if (name != null) {
      tag.setString("targetName", name);
    }
    if (icon != null) {
      NBTTagCompound iconRoot = new NBTTagCompound();
      icon.writeToNBT(iconRoot);
      tag.setTag("targetIcon", iconRoot);
    }
  }

  public static TelepadTarget readFromNBT(ItemStack stack) {
    if (stack == null || !stack.hasTagCompound()) {
      return null;
    }
    return readFromNBT(stack.getTagCompound());
  }

  public static TelepadTarget readFromNBT(NBTTagCompound tag) {
    BlockPos pos = getTargetPos(tag);
    if (pos == null) {
      return null;
    }
    return new TelepadTarget(pos, getTargetDimension(tag), getName(tag), getIcon(tag));
  }
  
  @Override
  public boolean canHandle(Class<?> clazz) {
    return TelepadTarget.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, TelepadTarget object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {    
    NBTTagCompound root = new NBTTagCompound();
    object.writeToNBT(root);
    nbt.setTag(name, root);       
    return true;
  }

  @Override
  public TelepadTarget read(Registry registry, Set<StoreFor> phase, NBTTagCompound nbt, String name, TelepadTarget object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {    
    if(nbt.hasKey(name)) {
      NBTTagCompound root = nbt.getCompoundTag(name);
      return readFromNBT(root);
    }
    return new TelepadTarget();    
  }

  private static ItemStack getIcon(NBTTagCompound tag) {
    if (tag == null || !tag.hasKey("targetIcon")) {
      return null;
    }
    return ItemStack.loadItemStackFromNBT(tag.getCompoundTag("targetIcon"));
  }

  private static String getName(NBTTagCompound tag) {
    if (tag == null || !tag.hasKey("targetName")) {
      return null;
    }
    return tag.getString("targetName");
  }

  private static BlockPos getTargetPos(NBTTagCompound tag) {
    if (tag == null || !tag.hasKey("targetPos")) {
      return null;
    }
    return BlockPos.fromLong(tag.getLong("targetPos"));
  }

  private static int getTargetDimension(NBTTagCompound tag) {
    if (tag == null || !tag.hasKey("targetDim")) {
      return 0;
    }
    return tag.getInteger("targetDim");
  }
  
  
  public static class TelepadTargetArrayListHandler extends HandleArrayList<TelepadTarget> {

    public TelepadTargetArrayListHandler() {
      super(new TelepadTarget());
    }

  }

}
