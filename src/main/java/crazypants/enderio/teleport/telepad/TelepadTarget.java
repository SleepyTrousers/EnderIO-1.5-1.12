package crazypants.enderio.teleport.telepad;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public class TelepadTarget {

  private final BlockPos location;
  private final int dimension;
  private String name;
  private ItemStack icon;
  
  public TelepadTarget(BlockPos location, int dimension) {
    this(location, dimension, null, null);
  }
  
  public TelepadTarget(BlockPos location, int dimension, String name, ItemStack icon) {  
    this.location = location;
    this.dimension = dimension;
    this.name = name;
    this.icon = icon;
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
  
  public void writeToNBT(ItemStack printout) {
    if(printout == null) {
      return;
    }
    if(!printout.hasTagCompound()) {
      printout.setTagCompound(new NBTTagCompound());
    }
    writeToNBT(printout.getTagCompound());
    if(getName() != null) {
      printout.setStackDisplayName(getName());
    } else {
      printout.clearCustomName();
    }
    
  }
  
  public void writeToNBT(NBTTagCompound tag) {
    if(location != null) {
      tag.setLong("targetPos", location.toLong());
    }
    tag.setInteger("targetDim", dimension);
    if(name != null) {
      tag.setString("targetName", name);
    }
    if(icon != null) {
      NBTTagCompound iconRoot = new NBTTagCompound();
      icon.writeToNBT(iconRoot);
      tag.setTag("targetIcon", iconRoot);
    }
  }
  
  public static TelepadTarget readFromNBT(ItemStack stack) {
    if(stack == null || !stack.hasTagCompound()) {
      return null;
    }    
    return readFromNBT(stack.getTagCompound());
  }
  
  public static TelepadTarget readFromNBT(NBTTagCompound tag) {
    BlockPos pos = getTargetPos(tag);
    if(pos == null) {
      return null;
    }
    return new TelepadTarget(pos, getTargetDimension(tag), getName(tag), getIcon(tag));
  }
  
  public static ItemStack getIcon(NBTTagCompound tag) {
    if (tag == null|| !tag.hasKey("targetIcon")) {
      return null;
    }
    return ItemStack.loadItemStackFromNBT(tag.getCompoundTag("targetIcon"));    
  }

  public static String getName(NBTTagCompound tag) {
    if (tag == null|| !tag.hasKey("targetName")) {
      return null;
    }
    return tag.getString("targetName");
  }

  public static BlockPos getTargetPos(NBTTagCompound tag) {
    if (tag == null|| !tag.hasKey("targetPos")) {
      return null;
    }
    return BlockPos.fromLong(tag.getLong("targetPos"));
  }
  
  public static int getTargetDimension(NBTTagCompound tag) {
    if (tag == null|| !tag.hasKey("targetDim")) {
      return 0;
    }
    return tag.getInteger("targetDim");
  }

  public static String getDimenionName(int dim) {
    WorldProvider prov = DimensionManager.getProvider(dim);    
    if(prov == null) {
      return null;
    }
    DimensionType type = prov.getDimensionType();
    if(type == null) {
      return null;
    }
    String name = type.getName();
    int[] dims = DimensionManager.getDimensions(type);
    if(dims != null && dims.length > 1) {
      name += " " + prov.getDimension();
    }
    return name;
  }

  

}
