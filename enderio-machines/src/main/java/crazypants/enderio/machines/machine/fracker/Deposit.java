package crazypants.enderio.machines.machine.fracker;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

class Deposit {
  private final @Nonnull BlockPos pos;
  private final @Nonnull ChunkPos chunkPos;
  private long size;
  private int radius, r2;

  public @Nonnull NBTTagCompound save() {
    NBTTagCompound result = new NBTTagCompound();
    result.setLong("pos", pos.toLong());
    result.setLong("size", size);
    return result;
  }

  public Deposit(@Nonnull NBTTagCompound data) {
    this(BlockPos.fromLong(data.getLong("pos")));
    setSize(data.getLong("size"));
  }

  public Deposit(@Nonnull BlockPos pos) {
    this.pos = pos.toImmutable();
    this.chunkPos = new ChunkPos(pos);
    this.size = 0L;
    this.radius = 0;
    this.r2 = 0;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
    this.radius = Long.SIZE - Long.numberOfLeadingZeros(size >>> 4);
    this.r2 = radius * radius;
  }

  public void add(long amount) {
    setSize(size + amount);
  }

  public void remove(long amount) {
    setSize(size - amount);
  }

  public boolean isEmpty() {
    return size <= 0;
  }

  public @Nonnull BlockPos getPos() {
    return pos;
  }

  public @Nonnull ChunkPos getChunkPos() {
    return chunkPos;
  }

  public int getRadius() {
    return Math.max(radius, 1);
  }

  public boolean inRange(@Nonnull Deposit other) {
    int r = this.radius + other.radius;
    return this.pos.distanceSq(other.pos) <= r * r;
  }

  public boolean inRange(@Nonnull BlockPos other) {
    return this.pos.distanceSq(other) <= r2;
  }

  public @Nonnull Deposit combine(@Nonnull Deposit other) {
    Deposit result = new Deposit(
        new BlockPos((this.pos.getX() + other.pos.getX()) / 2, (this.pos.getY() + other.pos.getY()) / 2, (this.pos.getZ() + other.pos.getZ()) / 2));
    result.setSize(this.size + other.size);
    return result;
  }
}
