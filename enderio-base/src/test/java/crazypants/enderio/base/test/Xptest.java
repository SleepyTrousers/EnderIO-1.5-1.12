package crazypants.enderio.base.test;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mojang.authlib.GameProfile;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.xp.XpUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("null")
class Xptest {

  private static GameProfile profile;
  private static WorldProvider provider;
  private static World world;

  @BeforeAll
  static void setup() {
    net.minecraft.init.Bootstrap.register();
    profile = new GameProfile(UUID.randomUUID(), "Test");
    provider = new WorldProvider() {

      @Override
      public DimensionType getDimensionType() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public BlockPos getSpawnPoint() {
        return new BlockPos(0, 0, 0);
      }
    };
    world = new World(null, null, provider, null, true) {

      @Override
      protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      protected IChunkProvider createChunkProvider() {
        // TODO Auto-generated method stub
        return null;
      }
    };
    // PlayerXPFixHandler.setErrored(true);
  }

  @Test
  void testNewPlayer() {
    EntityPlayer player = new EntityPlayer(world, profile) {

      @Override
      public boolean isSpectator() {
        return false;
      }

      @Override
      public boolean isCreative() {
        return false;
      }
    };

    assertEquals(0, (int) assertDoesNotThrow(() -> XpUtil.getPlayerXP(player)));
  }

  @Test
  void testRisingPlayer() {
    EntityPlayer player = new EntityPlayer(world, profile) {

      @Override
      public boolean isSpectator() {
        return false;
      }

      @Override
      public boolean isCreative() {
        return false;
      }
    };

    int i = 0;
    int offset = 0;
    while (i++ < 1000) {
      player.addExperience(1);
      if (Log.isInDev()) {
        System.out.println("XP:" + assertDoesNotThrow(() -> XpUtil.getPlayerXP(player)) + " Level:" + player.experienceLevel + " exp:" + player.experience
            + " barCap:" + player.xpBarCap() + " exp*barcap:" + (player.experience * player.xpBarCap()) + " (int)exp*barcap:"
            + ((int) (player.experience * player.xpBarCap())) + " XPtot:" + player.experienceTotal);
      }
      if (i == 91 || i == 178 || i == 233 || i == 520) {
        offset = 1;
      }
      if (i == 159 || i == 232 || i == 478) {
        offset = 0;
      }
      assertEquals(i - offset, (int) assertDoesNotThrow(() -> XpUtil.getPlayerXP(player)));
      assertEquals(i, player.experienceTotal);
    }

  }

  @Test
  void testShootingStarPlayer() {
    EntityPlayer player = new EntityPlayer(world, profile) {

      @Override
      public boolean isSpectator() {
        return false;
      }

      @Override
      public boolean isCreative() {
        return false;
      }
    };

    int i = 0;
    int prev = 0;
    while (i++ < XpUtil.getMaxLevelsStorable()) {
      player.addExperienceLevel(1);

      int xp = assertDoesNotThrow(() -> XpUtil.getPlayerXP(player), "Level: " + i);
      assertTrue(xp > prev);
      prev = xp;
      assertEquals(i, player.experienceLevel);
    }

  }

  // @Test
  void testRisingPlayerWithFix() {
    // fix has been deleted because vanilla xp handling is just a big mess
    EntityPlayer player = new EntityPlayer(world, profile) {

      @Override
      public boolean isSpectator() {
        return false;
      }

      @Override
      public boolean isCreative() {
        return false;
      }
    };

    PlayerTickEvent event = new PlayerTickEvent(Phase.START, player);

    int i = 0;
    while (i++ < 1000) {
      player.addExperience(1);
      if (Log.isInDev()) {
        System.out.println("XP:" + assertDoesNotThrow(() -> XpUtil.getPlayerXP(player)) + " Level:" + player.experienceLevel + " exp:" + player.experience
            + " barCap:" + player.xpBarCap() + " exp*barcap:" + (player.experience * player.xpBarCap()) + " (int)exp*barcap:"
            + ((int) (player.experience * player.xpBarCap())) + " XPtot:" + player.experienceTotal);
      }
      // PlayerXPFixHandler.recalcPlayerXP(event);
      // if (Log.isInDev()) {
      // System.out.println("XP:" + XpUtil.getPlayerXP(player) + " Level:" + player.experienceLevel + " exp:" + player.experience + " barCap:"
      // + player.xpBarCap() + " exp*barcap:" + (player.experience * player.xpBarCap()) + " (int)exp*barcap:"
      // + ((int) (player.experience * player.xpBarCap())) + " XPtot:" + player.experienceTotal);
      // }

      // vanilla method still has rounding error on the remaining XP points
      assertTrue(i == assertDoesNotThrow(() -> XpUtil.getPlayerXP(player)) || (i - 1) == assertDoesNotThrow(() -> XpUtil.getPlayerXP(player)));
      assertEquals(i, player.experienceTotal);
      // PlayerXPFixHandler.setErrored(false); // allow getPlayerXP to use experienceTotal
      // assertEquals(i, XpUtil.getPlayerXP(player));
      // PlayerXPFixHandler.setErrored(true);
    }

  }

  @Test
  void testSeasonedPlayer() {
    EntityPlayer player = new EntityPlayer(world, profile) {

      @Override
      public boolean isSpectator() {
        return false;
      }

      @Override
      public boolean isCreative() {
        return false;
      }
    };
    player.experienceLevel++;

    assertEquals(7, (int) assertDoesNotThrow(() -> XpUtil.getPlayerXP(player)));
  }

}
