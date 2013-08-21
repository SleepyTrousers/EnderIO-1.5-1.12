package crazypants.enderio.machine.reservoir;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;

class TankNeighbour {

  final ITankContainer container;
  final ForgeDirection fillFromDir;

  TankNeighbour(ITankContainer container, ForgeDirection fillFromDir) {
    this.container = container;
    this.fillFromDir = fillFromDir;
  }

}
