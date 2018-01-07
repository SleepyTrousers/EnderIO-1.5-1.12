package crazypants.enderio.base.farming.fertilizer;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFertilizer {

  /**
   * Tries to apply the given item on the given block using the type-specific method. SFX is played on success.
   * 
   * If the item was successfully applied, the stacksize will be decreased if appropriate.
   * 
   * @param stackIn
   * @param player
   * @param world
   * @param bc
   * @return true if the fertilizer was applied
   */
  Result apply(@Nonnull ItemStack stackIn, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc);

  boolean applyOnAir();

  boolean applyOnPlant();

  boolean matches(@Nonnull ItemStack stackIn);

  boolean isValid();

}