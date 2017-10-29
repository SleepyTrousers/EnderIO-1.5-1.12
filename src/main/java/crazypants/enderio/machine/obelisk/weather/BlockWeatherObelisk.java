package crazypants.enderio.machine.obelisk.weather;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockWeatherObelisk extends AbstractBlockObelisk<TileWeatherObelisk> {

  public static BlockWeatherObelisk create() {
    BlockWeatherObelisk ret = new BlockWeatherObelisk();
    ret.init();
    PacketHandler.INSTANCE.registerMessage(PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketWeatherTank.class, PacketWeatherTank.class, PacketHandler.nextID(), Side.CLIENT);
    
    EntityRegistry.registerModEntity(EntityWeatherRocket.class, "weather_rocket", 33, EnderIO.instance, 64, 3, false);
    return ret;
  }

  private BlockWeatherObelisk() {
    super(MachineObject.blockWeatherObelisk, TileWeatherObelisk.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new ContainerWeatherObelisk(player.inventory, (TileWeatherObelisk) world.getTileEntity(new BlockPos(x, y, z)));
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiWeatherObelisk(player.inventory, (TileWeatherObelisk) world.getTileEntity(new BlockPos(x, y, z)));
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_WEATHER_OBELISK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
  }
}
