package crazypants.enderio.machines.machine.obelisk.weather;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.machine.obelisk.base.AbstractBlockObelisk;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
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

    // FIXME: this is wrong! "Detected an attempt by a mod %s to perform game activity during mod construction. This is a serious programming error."
    EntityRegistry.registerModEntity(new ResourceLocation(EnderIO.DOMAIN, "weather_rocket"), EntityWeatherRocket.class, "weather_rocket", 33, EnderIO.instance,
        64, 3, false); // TODO Check if Forge has a registry for this
    return ret;
  }

  private BlockWeatherObelisk(@Nonnull IModObject modObject) {
    super(modObject, TileWeatherObelisk.class);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileWeatherObelisk te) {
    return new ContainerWeatherObelisk(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileWeatherObelisk te) {
    return new GuiWeatherObelisk(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block changedTo, @Nonnull BlockPos fromPos) {
    TileWeatherObelisk tileEntity = getTileEntity(world, pos);
    if (tileEntity != null) {
      tileEntity.updateRedstoneState();
    }
  }

}
