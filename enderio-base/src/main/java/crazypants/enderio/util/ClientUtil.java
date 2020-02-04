package crazypants.enderio.util;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class ClientUtil {

  public static void regRenderer(@Nonnull Block block, int meta, @Nonnull String name) {
    Item item = Item.getItemFromBlock(block);
    ResourceLocation resourceLocation = block.getRegistryName();
    if (resourceLocation == null) {
      throw new NullPointerException("Cannot register renderers for block " + block + ": Block is not registered with the block registry");
    }
    ModelResourceLocation modelResourceLocation = new ModelResourceLocation(resourceLocation, name);
    ModelLoader.setCustomModelResourceLocation(item, meta, modelResourceLocation);
  }

  public static void regRenderer(@Nonnull Item item, int meta, @Nonnull String name) {
    regRenderer(item, meta, new ResourceLocation(EnderIO.DOMAIN, name));
  }

  public static void regRenderer(@Nonnull Item item, int meta, @Nonnull ResourceLocation loc) {
    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(loc, "inventory"));
  }

  public static void registerDefaultItemRenderer(@Nonnull IModObject mo) {
    final Item item = mo.getItem();
    if (item != null) {
      regRenderer(item, 0, mo.getUnlocalisedName());
    }
  }

  public static void registerRenderer(@Nonnull Item item, @Nonnull String name) {
    regRenderer(item, 0, name);
  }

  public static void spawnParcticles(double posX, double posY, double posZ, int count, @Nonnull EnumParticleTypes particle) {
    final World world = Minecraft.getMinecraft().world;
    if (NullHelper.untrust(world) == null) {
      // possible race condition during world join and/or disconnect
      return;
    }
    final Random rand = world.rand;
    for (int i = 0; i < count; ++i) {
      double xOff, yOff, zOff, d0, d1, d2;
      if (particle == EnumParticleTypes.PORTAL) {
        xOff = (rand.nextDouble() - 0.5) * 1.1;
        yOff = (rand.nextDouble() - 0.5) * 1.1;
        zOff = (rand.nextDouble() - 0.5) * 1.1;
        d0 = (rand.nextDouble() - 0.5) * 1.5;
        d1 = -rand.nextDouble();
        d2 = (rand.nextDouble() - 0.5) * 1.5;
      } else if (particle == EnumParticleTypes.DAMAGE_INDICATOR) {
        xOff = 0.1D;
        yOff = 0.0D;
        zOff = 0.1D;
        d0 = d1 = d2 = 0.2D;
      } else {
        xOff = yOff = zOff = 0;
        d0 = rand.nextGaussian() * 0.02D;
        d1 = rand.nextGaussian() * 0.02D;
        d2 = rand.nextGaussian() * 0.02D;
      }
      world.spawnParticle(particle, posX + xOff, posY + yOff, posZ + zOff, d0, d1, d2);
    }
  }

}
