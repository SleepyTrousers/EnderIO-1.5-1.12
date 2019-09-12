package crazypants.enderio.machines.machine.farm;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.blockiterators.PlanarBlockIterator;
import com.enderio.core.common.util.blockiterators.PlanarBlockIterator.Orientation;

import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.SimpleTicket;

import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_BASE_SIZE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.FARM_BONUS_SIZE;

public class TicketFarmingStation extends SimpleTicket<Vec3d> {

  private final @Nonnull TileFarmStation farm;

  public TicketFarmingStation(@Nonnull TileFarmStation farm) {
    this.farm = farm;
  }

  @Override
  public boolean matches(Vec3d toMatch) {
    return toMatch != null && this.farm.canWater(toMatch);
  }

  public void prepare() {
    if (getMasterManager() == null && !farm.getWorld().isRemote) {
      final int maxSize = (int) (FARM_BASE_SIZE.getFloat(DefaultCapacitorData.LVL10) + FARM_BONUS_SIZE.getFloat(DefaultCapacitorData.LVL10));
      final PlanarBlockIterator blockIterator = new PlanarBlockIterator(farm.getPos(), Orientation.HORIZONTAL, maxSize);
      final Set<ChunkPos> chunks = new HashSet<>();
      while (blockIterator.hasNext()) {
        // wasteful? maybe, but this only runs once per TileEntity
        chunks.add(new ChunkPos(blockIterator.next()));
      }
      final ChunkPos farmChunk = new ChunkPos(farm.getPos());
      chunks.remove(farmChunk);
      FarmlandWaterManager.addCustomTicket(farm.getWorld(), this, farmChunk, chunks.toArray(new ChunkPos[0]));
    } else if (!isValid()) {
      validate();
    }
  }

}
