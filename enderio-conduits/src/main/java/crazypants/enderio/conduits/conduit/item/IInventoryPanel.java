package crazypants.enderio.conduits.conduit.item;

public interface IInventoryPanel {

  float getAvailablePower();
  
  void refuelPower(IInventoryDatabaseServer db);

}
