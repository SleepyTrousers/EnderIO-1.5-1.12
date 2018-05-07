package thaumcraft.api.aspects;



/**
 * @author Azanor
 * 
 * This interface is implemented by tile entites (or possibly anything else) like jars
 * so that they can act as an essentia source for blocks like the infusion altar.
 *
 */
public interface IAspectSource extends IAspectContainer {
	
	/**
	 * If this returns true then it will not act as an aspect source.
	 * @return
	 */
	public boolean isBlocked();
	
}
