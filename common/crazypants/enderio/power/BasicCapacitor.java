package crazypants.enderio.power;

public class BasicCapacitor implements ICapacitor {

  private int minEnergyReceived = 0;
  private int maxEnergyReceived = 2;
  private int maxEnergyStored = 5000;
  private int minActivationEnergy = 0;

  private int powerLoss = 1;
  private int powerLossRegularity = 200;

  private int maxEnergyExtracted = 2;

  public BasicCapacitor() {
  }

  public BasicCapacitor(int maxEnergyIO, int maxEnergyStored) {
    this(0, maxEnergyIO, maxEnergyStored, 0, 1, 200, maxEnergyIO);
  }

  public BasicCapacitor(int maxEnergyIn, int maxEnergyStored, int maxEnergyOut) {
    this(0, maxEnergyIn, maxEnergyStored, 0, 1, 200, maxEnergyOut);
  }

  public BasicCapacitor(int minEnergyReceived, int maxEnergyReceived, int maxEnergyStored, int minActivationEnergy, int powerLoss, int powerLossRegularity,
      int maxEnergyExtracted) {
    configure(minEnergyReceived, maxEnergyReceived, maxEnergyStored, minActivationEnergy, powerLoss, powerLossRegularity, maxEnergyExtracted);
  }

  protected void configure(int minEnergyReceived, int maxEnergyReceived, int maxEnergyStored, int minActivationEnergy, int powerLoss, int powerLossRegularity,
      int maxEnergyExtracted) {
    this.minEnergyReceived = minEnergyReceived;
    this.maxEnergyReceived = maxEnergyReceived;
    this.maxEnergyStored = maxEnergyStored;
    this.minActivationEnergy = minActivationEnergy;
    this.powerLoss = powerLoss;
    this.powerLossRegularity = powerLossRegularity;
    this.maxEnergyExtracted = maxEnergyExtracted;

  }

  @Override
  public int getMinEnergyReceived() {
    return minEnergyReceived;
  }

  protected void setMinEnergyReceived(int minEnergyReceived) {
    this.minEnergyReceived = minEnergyReceived;
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
  public int getMinActivationEnergy() {
    return minActivationEnergy;
  }

  protected void setMinActivationEnergy(int minActivationEnergy) {
    this.minActivationEnergy = minActivationEnergy;
  }

  @Override
  public int getPowerLoss() {
    return powerLoss;
  }

  protected void setPowerLoss(int powerLoss) {
    this.powerLoss = powerLoss;
  }

  @Override
  public int getPowerLossRegularity() {
    return powerLossRegularity;
  }

  protected void setPowerLossRegularity(int powerLossRegularity) {
    this.powerLossRegularity = powerLossRegularity;
  }

  @Override
  public int getMaxEnergyExtracted() {
    return maxEnergyExtracted;
  }

}
