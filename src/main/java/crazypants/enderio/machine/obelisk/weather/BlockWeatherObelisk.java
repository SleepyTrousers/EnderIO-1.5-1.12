package crazypants.enderio.machine.obelisk.weather;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import crazypants.enderio.network.PacketHandler;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockWeatherObelisk extends BlockObeliskAbstract<TileWeatherObelisk> {

    public static BlockWeatherObelisk create() {
        BlockWeatherObelisk ret = new BlockWeatherObelisk();
        ret.init();
        PacketHandler.INSTANCE.registerMessage(
                PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
                PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
                PacketWeatherTank.class, PacketWeatherTank.class, PacketHandler.nextID(), Side.CLIENT);

        EntityRegistry.registerModEntity(
                EntityWeatherRocket.class, "weather_rocket", 33, EnderIO.instance, 64, 3, false);
        return ret;
    }

    private BlockWeatherObelisk() {
        super(ModObject.blockWeatherObelisk, TileWeatherObelisk.class);
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
    protected int getGuiId() {
        return GuiHandler.GUI_ID_WEATHER_OBELISK;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        ; // no active particles
    }
}
