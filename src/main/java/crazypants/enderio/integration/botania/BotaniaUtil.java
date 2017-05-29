package crazypants.enderio.integration.botania;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.farmers.FarmersCommune;
import crazypants.enderio.farming.farmers.PlaceableFarmer;
import crazypants.enderio.farming.fertilizer.Bonemeal;
import crazypants.enderio.farming.fertilizer.Fertilizer;
import crazypants.enderio.farming.fertilizer.Result;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BotaniaUtil {

  public static class MagicalFertilizer extends Bonemeal {
    public MagicalFertilizer(Item item) {
      super(item);
    }

    @Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      BlockPos below = bc.down();
      Block belowBlock = world.getBlockState(below).getBlock();
      if (belowBlock == Blocks.DIRT || belowBlock == Blocks.GRASS) {
        return super.apply(stack, player, world, below);
      }
      return new Result(stack, false);
    }

    @Override
    public boolean applyOnAir() {
      return true;
    }

    @Override
    public boolean applyOnPlant() {
      return false;
    }
  }

  private static final String SOLEGNOLIA = "vazkii.botania.common.block.subtile.functional.SubTileSolegnolia";
  private static final String HAS_SOLEGNOLIA_AROUND = "hasSolegnoliaAround";
  private static Method hasSolegnoliaAround = null;
  private static int errorCount = -1;

  public static boolean hasSolegnoliaAround(Entity entity) {
    if (errorCount < 0) {
      errorCount = 0;
      try {
        Class<?> solegnolia = Class.forName(SOLEGNOLIA);
        hasSolegnoliaAround = solegnolia.getMethod(HAS_SOLEGNOLIA_AROUND, Entity.class);
        Log.debug("Found Botania's Solegnolia class. Magnet will not be greedy.");
      } catch (Throwable t) {
        Log.debug("Didn't find Botania's Solegnolia class. Magnet will be greedy.");
      }
    }
    if (hasSolegnoliaAround != null) {
      try {
        Boolean result = (Boolean) hasSolegnoliaAround.invoke(null, entity);
        if (errorCount > 0) {
          errorCount--;
        }
        return result;
      } catch (Throwable t) {
        if (errorCount++ > 10) {
          Log.warn("Failed to interact with Botania too often. Magnet will ignore Solegnolias from now on. Last error was: " + t);
          hasSolegnoliaAround = null;
        }
      }
    }
    return false;
  }

  public static void addBotania() {
    int count = 0;
    FarmersRegistry.registerFlower("block:botania:flower", "block:botania:doubleflower1", "block:botania:doubleflower2", "block:botania:shinyflower",
        "block:botania:mushroom");
    PlaceableFarmer farmer = new PlaceableFarmer("item:botania:petal");
    farmer.addDirt("block:minecraft:grass");
    if (farmer.isValid()) {
      FarmersCommune.joinCommune(farmer);
      count++;
    }
    final MagicalFertilizer fertilizer = new MagicalFertilizer(FarmersRegistry.findItem("botania", "fertilizer"));
    if (fertilizer.isValid()) {
      Fertilizer.registerFertilizer(fertilizer);
      count++;
    }

    if (count == 2) {
      Log.info("Farming Station: Botania integration fully loaded");
    } else if (count == 0) {
      Log.info("Farming Station: Botania integration not loaded");
    } else {
      Log.info("Farming Station: Botania integration partially loaded (" + count + " of 2)");
    }

  }

}
