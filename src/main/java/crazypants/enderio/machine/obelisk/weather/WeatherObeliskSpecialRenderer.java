package crazypants.enderio.machine.obelisk.weather;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import crazypants.enderio.machine.obelisk.render.ObeliskSpecialRenderer;

public class WeatherObeliskSpecialRenderer extends ObeliskSpecialRenderer<TileWeatherObelisk> {
  
  public WeatherObeliskSpecialRenderer(ItemStack itemStack) {
    super(itemStack);
  }
  
  @Override
  protected void renderItemStack(TileWeatherObelisk te, World world, double x, double y, double z, float tick) {
    if (te == null || te.isActive()) {
      super.renderItemStack(te, world, x, y, z, tick);
    }
  }
}
