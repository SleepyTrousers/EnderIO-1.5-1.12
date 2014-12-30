package crazypants.enderio.machine.weather;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.weather.TileWeatherObelisk.Task;
import crazypants.enderio.network.PacketHandler;

public class GuiWeatherObelisk extends GuiPoweredMachineBase<TileWeatherObelisk> {

  public GuiWeatherObelisk(InventoryPlayer inventory, TileWeatherObelisk tileEntity) {
    super(tileEntity, new ContainerWeatherObelisk(inventory, tileEntity));
  }

  @Override
  public void initGui() {
    super.initGui();
    addButton(new IconButtonEIO(this, 0, 100, 50, IconEIO.SINGLE_PLUS));
    addButton(new IconButtonEIO(this, 1, 130, 50, IconEIO.DOUBLE_PLUS));
    addButton(new IconButtonEIO(this, 2, 160, 50, IconEIO.TRIPLE_PLUS));

  }
  
  @Override
  protected void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if (b.id >= 0 && b.id <= 2) {
      PacketHandler.INSTANCE.sendToServer(new PacketActivateWeather(getTileEntity(), Task.values()[b.id]));
    }
  }
}
