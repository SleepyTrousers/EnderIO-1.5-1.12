package crazypants.enderio.base.render.registry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.util.Strings;

import crazypants.enderio.api.IModObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SmartModelRegistry {

  public static class ExtraModel {

    private final @Nonnull ModelResourceLocation mrl;

    public ExtraModel(@Nonnull IModObject mo, @Nonnull String extra) {
      this.mrl = new ModelResourceLocation(mo.getRegistryName(), extra);
      String key = mo.getRegistryName().toString();
      Data data = DATA.computeIfAbsent(key, k -> new Data(mo, false));
      data.extras.put(extra, mrl);
    }

    @SideOnly(Side.CLIENT)
    public @Nonnull IBakedModel get() {
      return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(mrl);
    }

  }

  static class Data {
    @Nonnull
    IModObject mo;
    boolean wrapItem;
    final @Nonnull Map<String, ModelResourceLocation> extras = new HashMap<>();

    Data(@Nonnull IModObject mo, boolean wrapItem) {
      this.mo = mo;
      this.wrapItem = wrapItem;
    }
  }

  private static final Map<String, Data> DATA = new HashMap<>();

  public static void register(@Nonnull IModObject mo, boolean wrapItems, String... extras) {
    final Data data = new Data(mo, wrapItems);
    for (String extra : extras) {
      if (extra != null) {
        data.extras.put(extra, new ModelResourceLocation(mo.getRegistryName(), extra));
      }
    }
    DATA.put(mo.getRegistryName().toString(), data);
  }

  public static Data query(@Nonnull ModelResourceLocation mrl) {
    String key = mrl.getResourceDomain() + ":" + mrl.getResourcePath();
    Data data = DATA.get(key);
    if (data != null) {
      if (Strings.isBlank(mrl.getVariant())) {
        return data;
      }
      if (data.wrapItem && "inventory".equals(mrl.getVariant())) {
        return data;
      }
    }
    return null;
  }

}
