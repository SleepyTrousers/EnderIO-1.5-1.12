package crazypants.enderio.base.render.registry;

import java.util.Map.Entry;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public final class TextureResolver {

  @SubscribeEvent
  public static void onIconLoad(TextureStitchEvent.Pre event) {
    for (Entry<ResourceLocation, Object> entry : TextureRegistry.getSprites().entrySet()) {
      Log.debug("TextureStitchEvent.Pre for ", entry.getKey());
      entry.setValue(event.getMap().registerSprite(NullHelper.notnull(entry.getKey(), "internal data corruption")));
    }
    TextureRegistry.lock(key -> {
      // this fallback should never be triggered as all sprites get assigned a texture and the registry is locked here
      Log.error("Missing texture: " + key);
      TextureRegistry.getSprites().put(key, RenderUtil.getMissingSprite());
      return RenderUtil.getMissingSprite();
    });
  }

}
