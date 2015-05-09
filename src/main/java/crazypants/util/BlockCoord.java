package crazypants.util;

import com.google.common.base.Strings;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class BlockCoord {

  public final int x;
  public final int y;
  public final int z;
  
  public BlockCoord(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public BlockCoord() {
    this(0, 0, 0);
  }

  public BlockCoord(double x, double y, double z) {
    this(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
  }

  public BlockCoord(TileEntity tile) {
    this(tile.xCoord, tile.yCoord, tile.zCoord);
  }
  
  public BlockCoord(Entity e) {
    this(e.posX, e.posY, e.posZ);
  }

  public BlockCoord(BlockCoord bc) {
    this(bc.x, bc.y, bc.z);
  }

  public BlockCoord getLocation(ForgeDirection dir) {
    return new BlockCoord(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
  }
  
  public BlockCoord(String x, String y, String z) {
    this(Strings.isNullOrEmpty(x) ? 0 : Integer.parseInt(x),
         Strings.isNullOrEmpty(y) ? 0 : Integer.parseInt(y),
         Strings.isNullOrEmpty(z) ? 0 : Integer.parseInt(z));
  }

  public BlockCoord(MovingObjectPosition mop) {
    this(mop.blockX, mop.blockY, mop.blockZ);
  }

  public int distanceSquared(BlockCoord other) {
    int dx, dy, dz;
    dx = x - other.x;
    dy = y - other.y;
    dz = z - other.z;
    return (dx * dx + dy * dy + dz * dz);
  }
  
  public int distance(BlockCoord other) {
    double dsq = distanceSquared(other);    
    return (int)Math.ceil(Math.sqrt(dsq)); 
  }
  
  public Block getBlock(IBlockAccess world) {
    return world.getBlock(x, y, z);
  }

  public TileEntity getTileEntity(IBlockAccess world) {
    return world.getTileEntity(x, y, z);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    result = prime * result + z;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(getClass() != obj.getClass()) {
      return false;
    }
    BlockCoord other = (BlockCoord) obj;
    if(x != other.x) {
      return false;
    }
    if(y != other.y) {
      return false;
    }
    if(z != other.z) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "BlockCoord [x=" + x + ", y=" + y + ", z=" + z + "]";
  }

  public String chatString() {
    return chatString(EnumChatFormatting.WHITE);
  }

  public String chatString(EnumChatFormatting defaultColor) {
    return String.format(
        "x%s%d%s y%s%d%s z%s%d",
        EnumChatFormatting.GREEN, x, defaultColor,
        EnumChatFormatting.GREEN, y, defaultColor,
        EnumChatFormatting.GREEN, z
        );
  }

  public boolean equals(int xCoord, int yCoord, int zCoord) {
    return x == xCoord && y == yCoord && z == zCoord;
  }

  public void writeToBuf(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
  }

  public static BlockCoord readFromBuf(ByteBuf buf) {
    return new BlockCoord(buf.readInt(), buf.readInt(), buf.readInt());
  }
  
  public void writeToNBT(NBTTagCompound tag) {
    tag.setInteger("bc:x", x);
    tag.setInteger("bc:y", y);
    tag.setInteger("bc:z", z);
  }

  public static BlockCoord readFromNBT(NBTTagCompound tag) {
    return new BlockCoord(tag.getInteger("bc:x"), tag.getInteger("bc:y"), tag.getInteger("bc:z"));
  }
}
