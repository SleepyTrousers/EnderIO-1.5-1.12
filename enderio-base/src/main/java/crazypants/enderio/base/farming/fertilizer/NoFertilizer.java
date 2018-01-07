package crazypants.enderio.base.farming.fertilizer;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class NoFertilizer extends AbstractFertilizer {

  /**
   * Not a fertilizer. Using this handler class any item can be "used" as a fertilizer. Meaning, fertilizing will always fail.
   */
  static private IFertilizer NONE;

  public static @Nonnull IFertilizer getNone() {
    return NullHelper.notnull(NONE, "fertilizing before game has started error");
  }

  private NoFertilizer() {
    super(Prep.getEmpty());
    setRegistryName(EnderIO.DOMAIN, "none");
  }

  @Override
  public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
    return new Result(stack, false);
  }

  @Override
  public boolean matches(@Nonnull ItemStack stack) {
    return false;
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    event.getRegistry().register(NONE = new NoFertilizer());
  }

}