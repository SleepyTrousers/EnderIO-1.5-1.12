package crazypants.enderio.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;

public class TextureRegistry {

  public static interface TextureSupplier {
    <T extends Object> T get(Class<T> clazz);
  }

  private static final TextureSupplier noSupplier = new TextureSupplier() {
    @Override
    public <T> T get(Class<T> clazz) {
      return null;
    }
  };

  private static class TextureRegistryServer {
    private TextureRegistryServer() {
    }

    protected void init() {
    }

    public TextureSupplier registerTexture(final String location) {
      return noSupplier;
    }
  }

  private static class TextureRegistryClient extends TextureRegistryServer {

    @SideOnly(Side.CLIENT)
    private Map<String, TextureAtlasSprite> sprites;

    private TextureRegistryClient() {
      super();
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void init() {
      sprites = new HashMap<String, TextureAtlasSprite>();
      MinecraftForge.EVENT_BUS.register(instance);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onIconLoad(TextureStitchEvent.Pre event) {
      for (Entry<String, TextureAtlasSprite> entry : sprites.entrySet()) {
        entry.setValue(event.map.registerSprite(new ResourceLocation(EnderIO.DOMAIN, entry.getKey())));
      }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureSupplier registerTexture(final String location) {
      if (!sprites.containsKey(location)) {
        sprites.put(location, null);
      }
      return new TextureSupplier() {
        @Override
        public <T> T get(Class<T> clazz) {
          if (clazz == TextureAtlasSprite.class) {
            return (T) sprites.get(location);
          } else {
            return null;
          }
        }
      };
    }
  }

  private static TextureRegistryServer instance;

  public static TextureSupplier registerTexture(final String location) {
    if (instance == null) {
      instance = new TextureRegistryClient();
      instance.init();
    }
    return instance.registerTexture(location);
  }

  private TextureRegistry() {
  }

}
