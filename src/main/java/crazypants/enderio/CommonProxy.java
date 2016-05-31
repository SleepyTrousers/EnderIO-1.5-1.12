package crazypants.enderio;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import crazypants.enderio.config.Config;
import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.RecipeFactory;
import crazypants.enderio.config.recipes.xml.Recipes;
import crazypants.enderio.sound.SoundRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CommonProxy {

  protected long serverTickCount = 0;
  protected long clientTickCount = 0;
  protected final TickTimer tickTimer = new TickTimer();

  public CommonProxy() {
  }

  public World getClientWorld() {
    return null;
  }

  public EntityPlayer getClientPlayer() {
    return null;
  }

  public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
    return 5;
  }

  public void loadIcons() {
  }
  
  public void preInit() {
    if (Loader.isModLoaded("theoneprobe")) {
      FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "crazypants.enderio.top.TOPCompatibility");
    }
  }
  
  private static final String[] RECIPE_FILES = { "aliases", "materials", "items", "machines" };

  public void init() {
    MinecraftForge.EVENT_BUS.register(tickTimer);
    SoundRegistry.init();

    if (Config.registerRecipes) {
      for (String filename : RECIPE_FILES) {
        try {
          Recipes recipes = RecipeFactory.readFile(new Recipes(), "recipes", "recipe_" + filename);
          if (recipes.isValid()) {
            recipes.enforceValidity();
            recipes.register();
          } else {
            throw new InvalidRecipeConfigException("Recipes config file recipe_" + filename + ".xml is empty or invalid!");
          }
        } catch (InvalidRecipeConfigException e) {
          Log.error("Failed to read recipe config file " + filename + "_core.xml or " + filename + "_user.xml\n\n\n\n"
              + "\n======================================================================="
              + "\n== FATAL ERROR ========================================================"
              + "\n======================================================================="
              + "\n== Cannot register recipes as configured. This means that either     =="
              + "\n== your custom config file has an error or another mod does bad      =="
              + "\n== things to vanilla items or the Ore Dictionary.                    =="
              + "\n=======================================================================" //
              + "\n== Bad file: " + filename + "_core.xml or " + filename + "_user.xml"
              + "\n=======================================================================" //
              + "\n== Error: " + e.getMessage() //
              + "\n======================================================================="
              + "\n======================================================================="
              + "\n== Note: To start the game anyway, you can disable recipe loading in =="
              + "\n======== the Ender IO config file. However, then all of Ender IO's   =="
              + "\n======== crafting recipes will be missing.                           =="
              + "\n=======================================================================" //
              + "\n\n\n");
          throw new RuntimeException("Recipes config file recipe_" + filename + ".xml is invalid: " + e.getMessage());
        } catch (IOException e) {
          throw new RuntimeException("Error while reading recipes config file recipe_" + filename + ".xml: " + e.getMessage());
        } catch (XMLStreamException e) {
          throw new RuntimeException("Recipes config file recipe_" + filename + ".xml is invalid: " + e.getMessage());
        }
      }
    }
  }

  public long getTickCount() {
    return serverTickCount;
  }

  public boolean isAnEiInstalled() {
    return false;
  }

  public void setInstantConfusionOnPlayer(EntityPlayer ent, int duration) {
    ent.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration, 1, true, true));
  }

  protected void onServerTick() {
    ++serverTickCount;
  }

  protected void onClientTick() {
  }

  public final class TickTimer {

    @SubscribeEvent
    public void onTick(ServerTickEvent evt) {
      if(evt.phase == Phase.END) {
        onServerTick();
      }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent evt) {
      if(evt.phase == Phase.END) {
        onClientTick();
      }
    }
  }

  public @Nonnull ResourceLocation getGuiTexture(String name) {
    return new ResourceLocation(EnderIO.DOMAIN + ":unknown");
  }

}
