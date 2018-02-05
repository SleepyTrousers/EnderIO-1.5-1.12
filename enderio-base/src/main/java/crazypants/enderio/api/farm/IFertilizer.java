package crazypants.enderio.api.farm;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * This class is a handler for handling fertilizing in the Farming Station.
 * <p>
 * In order for an item to be usable as fertilizer, it must have a handler. The handler needs to be registered in the
 * {@link net.minecraftforge.event.RegistryEvent.Register}&lt;IFertilizer&gt; event.
 * <p>
 * The handler's {@link #matches(ItemStack)} method will be called on two different occasions. First to check if an item can be inserted into the fertilizer
 * slot (on both client and server), second to select the correct handler when trying to apply the fertilizer (server only).
 * <p>
 * The {@link #apply(ItemStack, EntityPlayer, World, BlockPos)} method will then be called to execute applying the fertilizer. It is responsible for checking if
 * the target block can be fertilized and then doing the actual fertilizing. If it wants to play sounds or spawn FX (green particles), is has to do so, too.
 * (Note that the purple Farming Station particles will be added by the Farming Station.) It then has to return the changed itemStack.
 * <p>
 * If the fertilizer item is some kind of container item (e.g. a bucket, or an RF powered item) and got useless for fertilizing after being used, you can put it
 * into any inventory slot of the {@link FakePlayer} to be dumped into the Farming Station's output slots.
 * 
 * @author Henry Loenwind
 *
 */
public interface IFertilizer extends IForgeRegistryEntry<IFertilizer> {

  /**
   * Tries to apply the given item on the given block using the type-specific method. SFX is played on success.
   * 
   * @param stack
   *          The itemStack that should be used. May be modified, but changes are ignored if the action was successful. (Please don't modify it otherwise.)
   * @param player
   *          A suitable FakePlayer. Extra results from applying the fertilizer can be put into its inventory. Those will go into the farm's output.
   * @param world
   *          The real world in which to apply the fertilizer.
   * @param pos
   *          The location on which to apply the fertilizer.
   * @return A {@link IFertilizerResult}.
   */
  IFertilizerResult apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos);

  /**
   * @return True if this fertilizer must be applied on air (not on plants or replaceable blocks).
   */
  boolean applyOnAir();

  /**
   * @return True if this fertilizer must be applied on a plant (not on air or replaceable blocks).
   */
  boolean applyOnPlant();

  /**
   * Check if this IFertilizer handles the given stack.
   * 
   * @param stack
   *          An itemStack to be tested.
   * @return True if the stack is a fertilizer.
   */
  boolean matches(@Nonnull ItemStack stack);

}