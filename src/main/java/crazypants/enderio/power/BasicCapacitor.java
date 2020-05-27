package crazypants.enderio.power;

public class BasicCapacitor implements ICapacitor {

  private int maxEnergyReceived = 40;
  private int maxEnergyStored = 100000;
  private int maxEnergyExtracted = 20;
  private int tier = 1;

  public BasicCapacitor() {
  }

  public BasicCapacitor(int maxEnergyIO) {
    this(0, maxEnergyIO, maxEnergyIO, maxEnergyIO);
  }

  public BasicCapacitor(int maxEnergyIO, int maxEnergyStored) {
    this(0, maxEnergyIO, maxEnergyStored, maxEnergyIO);
  }

  public BasicCapacitor(int tier, int maxEnergyIO, int maxEnergyStored) {
    this(tier, maxEnergyIO, maxEnergyStored, maxEnergyIO);
  }

  public BasicCapacitor(int tier, int maxEnergyReceived, int maxEnergyStored, int maxEnergyExtracted) {
    configure(tier, maxEnergyReceived, maxEnergyStored, maxEnergyExtracted);
  }




protected void configure(int tier, int maxEnergyReceived, int maxEnergyStored, int maxEnergyExtracted) {
	this.tier = tier;
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

  @Override
  public int getTier() {
	return tier;
  }

}
