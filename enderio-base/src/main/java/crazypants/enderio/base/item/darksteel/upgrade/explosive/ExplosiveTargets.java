package crazypants.enderio.base.item.darksteel.upgrade.explosive;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.config.DarkSteelConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public enum ExplosiveTargets {
  DEFAULT {
    @Override
    public boolean matches(@Nonnull Block block, boolean hasSpoon) {
      return STONES.contains(block) || (hasSpoon && DIRTS.contains(block));
    }
  },
  NO_INVENTORY {
    @Override
    public boolean matches(@Nonnull Block block, boolean hasSpoon) {
      return !block.hasTileEntity();
    }
  },
  CUSTOM {
    @Override
    public boolean matches(@Nonnull Block block, boolean hasSpoon) {
      return DarkSteelConfig.explosiveUpgradeCustomStone.get().contains(block)
          || (hasSpoon && DarkSteelConfig.explosiveUpgradeCustomDirt.get().contains(block));
    }
  },
  DEFAULT_AND_CUSTOM {
    @Override
    public boolean matches(@Nonnull Block block, boolean hasSpoon) {
      return DEFAULT.matches(block, hasSpoon) || CUSTOM.matches(block, hasSpoon);
    }
  },
  ALL {
    @Override
    public boolean matches(@Nonnull Block block, boolean hasSpoon) {
      return true;
    }
  };

  private static final Things STONES = new Things().add(Blocks.STONE).add(Blocks.COBBLESTONE).add(Blocks.NETHERRACK).add(Blocks.SANDSTONE)
      .add(Blocks.BRICK_BLOCK).add(Blocks.BRICK_STAIRS).add(Blocks.COBBLESTONE_WALL).add(Blocks.END_BRICKS).add(Blocks.END_STONE)
      .add(Blocks.MOSSY_COBBLESTONE).add(Blocks.MONSTER_EGG).add(Blocks.HARDENED_CLAY).add(Blocks.NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE)
      .add(Blocks.NETHER_BRICK_STAIRS).add(Blocks.SANDSTONE_STAIRS).add(Blocks.STAINED_HARDENED_CLAY).add(Blocks.STONE_BRICK_STAIRS).add(Blocks.STONE_STAIRS)
      .add(Blocks.STONEBRICK).add(Blocks.STONE_SLAB).add(Blocks.STONE_SLAB2).add(Blocks.DOUBLE_STONE_SLAB).add(Blocks.DOUBLE_STONE_SLAB2);
  private static final Things DIRTS = new Things().add(Blocks.DIRT).add(Blocks.GRAVEL).add(Blocks.GRASS).add(Blocks.SOUL_SAND).add(Blocks.MYCELIUM)
      .add(Blocks.GRASS_PATH).add(Blocks.FARMLAND).add(Blocks.CLAY).add(Blocks.SAND);

  public abstract boolean matches(@Nonnull Block block, boolean hasSpoon);
}