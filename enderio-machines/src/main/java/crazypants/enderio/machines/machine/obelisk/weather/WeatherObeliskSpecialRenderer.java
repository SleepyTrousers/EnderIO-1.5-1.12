package crazypants.enderio.machines.machine.obelisk.weather;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.machine.obelisk.render.ObeliskSpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static crazypants.enderio.machines.init.MachineObject.block_weather_obelisk;

public class WeatherObeliskSpecialRenderer extends ObeliskSpecialRenderer<TileWeatherObelisk> {

  public WeatherObeliskSpecialRenderer(@Nonnull ItemStack itemStack) {
    super(itemStack, block_weather_obelisk.getBlock());
  }

  @Override
  protected void renderItemStack(TileWeatherObelisk te, @Nonnull World world, double x, double y, double z, float tick) {
    if (te == null || te.isActive()) {
      super.renderItemStack(te, world, x, y, z, tick);
    }
  }
}
