package crazypants.enderio.power;

public class BasicCapacitor implements ICapacitor {
  
  private int maxEnergyReceived = 40;
  private int maxEnergyStored = 100000;  
  private int maxEnergyExtracted = 20;

  public BasicCapacitor() {
  }

  public BasicCapacitor(int maxEnergyIO, int maxEnergyStored) {
    this(maxEnergyIO, maxEnergyStored, maxEnergyIO);
  }

  public BasicCapacitor(int maxEnergyIn, int maxEnergyStored, int maxEnergyOut) {
    configure(maxEnergyIn, maxEnergyStored, maxEnergyOut);
  }

  
  protected void configure(int maxEnergyReceived, int maxEnergyStored, int maxEnergyExtracted) {
    this.maxEnergyReceived = maxEnergyReceived;
    this.maxEnergyStored = maxEnergyStored;
    this.maxEnergyExtracted = maxEnergyExtracted;

  }

  @Override
  public int getMaxEnergyReceived() {
    return maxEnergyReceived;
  }

  protected void setMaxEnergyReceived(int maxEnergyReceived) {
    this.maxEnergyReceived = maxEnergyReceived;
  }

  @Override
  public int getMaxEnergyStored() {
    return maxEnergyStored;
  }

  protected void setMaxEnergyStored(int maxEnergyStored) {
    this.maxEnergyStored = maxEnergyStored;
  }
  
  @Override
  public int getMaxEnergyExtracted() {
    return maxEnergyExtracted;
  }

}
