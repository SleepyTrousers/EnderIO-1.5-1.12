package cofh.api.tileentity;

/**
 * Implement this interface on Tile Entities which can report information about their energy usage.
 * 
 * @author King Lemming
 * 
 */
public interface IEnergyInfo {

	public int getEnergyPerTick();

	public int getMaxEnergyPerTick();

	public int getEnergy();

	public int getMaxEnergy();

}
