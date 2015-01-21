package crazypants.enderio.machine.weather;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;

public class BlockWeatherObelisk extends AbstractMachineBlock<TileWeatherObelisk> {

  public static int renderId;

  public static BlockWeatherObelisk create() {
    BlockWeatherObelisk ret = new BlockWeatherObelisk();
    ret.init();
    PacketHandler.INSTANCE.registerMessage(PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketFinishWeather.Handler.class, PacketFinishWeather.class, PacketHandler.nextID(), Side.CLIENT);
    return ret;
  }
  
  private BlockWeatherObelisk() {
    super(ModObject.blockWeatherObelisk, TileWeatherObelisk.class);
    setObeliskBounds();
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new ContainerWeatherObelisk(player.inventory, (TileWeatherObelisk) world.getTileEntity(x, y, z));
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiWeatherObelisk(player.inventory, (TileWeatherObelisk) world.getTileEntity(x, y, z));
  }
  
  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }
  
  @Override
  public int getLightOpacity() {
    return 0;
  }
  
  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_WEATHER_OBELISK;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:blockAttractorSideOn";
    }
    return "enderio:blockAttractorSide";
  }

  @Override
  protected String getSideIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }

  @Override
  protected String getBackIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }
  
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    ; // no active smoke
  }
}
