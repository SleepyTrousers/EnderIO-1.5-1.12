package crazypants.enderio.machine.obelisk.weather;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import crazypants.enderio.machine.obelisk.ObeliskRenderer;
import crazypants.enderio.machine.obelisk.ObeliskSpecialRenderer;

public class WeatherObeliskSpecialRenderer extends ObeliskSpecialRenderer<TileWeatherObelisk> {
  
  public WeatherObeliskSpecialRenderer(ItemStack itemStack, ObeliskRenderer renderer) {
    super(itemStack, renderer);
  }

  @Override
  protected void renderItemStack(TileWeatherObelisk te, World world, double x, double y, double z, float tick) {
    if (te == null || te.isActive()) {
      super.renderItemStack(te, world, x, y, z, tick);
    }
  }
}
