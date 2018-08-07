package crazypants.enderio.conduits.conduit.power;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import cofh.core.util.helpers.MathHelper;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IPowerConduitData {

  public static class Registry {

    private static final @Nonnull List<IPowerConduitData> data = new ArrayList<>();
    private static final @Nonnull IPowerConduitData fallback;

    static {
      register(fallback = new BasePowerConduitData(0));
      register(new BasePowerConduitData(1));
      register(new BasePowerConduitData(2));
    }

    public static @Nonnull IPowerConduitData fromID(int id) {
      return NullHelper.first(data.get(MathHelper.clamp(id, 0, data.size() - 1)), fallback);
    }

    public static void register(@Nonnull IPowerConduitData pcd) {
      while (pcd.getID() >= data.size()) {
        data.add(null);
      }
      if (data.get(pcd.getID()) != null) {
        throw new RuntimeException("Cannot register power conduit with ID " + pcd.getID() + ".");
      }
      data.set(pcd.getID(), pcd);
    }

  }

  int getID();

  int getMaxEnergyIO();

  @Nonnull
  ItemStack createItemStackForSubtype();

  @SideOnly(Side.CLIENT)
  TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component);

  TextureAtlasSprite getTextureForInputMode();

  TextureAtlasSprite getTextureForOutputMode();

}
