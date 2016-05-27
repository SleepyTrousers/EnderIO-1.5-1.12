package crazypants.enderio;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

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
  }
  
  private static final String[] RECIPE_FILES = { "aliases", "machines", "materials" };

  public void init() {
    MinecraftForge.EVENT_BUS.register(tickTimer);
    SoundRegistry.init();

    for (String filename : RECIPE_FILES) {
      try {
        Recipes recipes = RecipeFactory.readFile(new Recipes(), "recipes", "recipe_" + filename);
        if (recipes.isValid()) {
          recipes.register();
        } else {
          Log.warn("Recipes config file recipe_" + filename + ".xml is empty or invalid!");
        }
      } catch (InvalidRecipeConfigException e) {
        Log.warn("Recipes config file recipe_" + filename + ".xml is invalid: " + e.getMessage());
      } catch (IOException e) {
        Log.warn("Error while reading recipes config file recipe_" + filename + ".xml: " + e.getMessage());
      } catch (XMLStreamException e) {
        Log.warn("Recipes config file recipe_" + filename + ".xml is invalid: " + e.getMessage());
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
