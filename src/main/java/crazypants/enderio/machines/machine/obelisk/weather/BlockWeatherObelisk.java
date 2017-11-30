package crazypants.enderio.machines.machine.obelisk.weather;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.machines.machine.obelisk.AbstractBlockObelisk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWeatherObelisk extends AbstractBlockObelisk<TileWeatherObelisk> {

  public static BlockWeatherObelisk create(@Nonnull IModObject modObject) {
    BlockWeatherObelisk ret = new BlockWeatherObelisk(modObject);
    ret.init();
    PacketHandler.INSTANCE.registerMessage(PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketWeatherTank.class, PacketWeatherTank.class, PacketHandler.nextID(), Side.CLIENT);

    EntityRegistry.registerModEntity(new ResourceLocation(EnderIO.DOMAIN, "weather_rocket"), EntityWeatherRocket.class, "weather_rocket", 33, EnderIO.instance,
        64, 3, false); // TODO Check if Forge has a registry for this
    return ret;
  }

  private BlockWeatherObelisk(@Nonnull IModObject modObject) {
    super(modObject, TileWeatherObelisk.class);
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileWeatherObelisk te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerWeatherObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileWeatherObelisk te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiWeatherObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_WEATHER_OBELISK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }
}
