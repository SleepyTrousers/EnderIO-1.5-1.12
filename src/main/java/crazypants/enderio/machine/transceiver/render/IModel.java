package crazypants.enderio.machine.transceiver.render;

import crazypants.enderio.machine.transceiver.TileTransceiver;

public interface IModel {

    void render(TileTransceiver cube, double x, double y, double z);

    void render();
}
