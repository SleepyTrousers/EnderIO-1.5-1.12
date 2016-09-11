package mods.immibis.microblocks.api;

public interface PartType2<PartClass extends Part> extends PartType<PartClass> {
	/**
	 * @return The light level emitted by this part, from 0 to 15.
	 */
	public int getLightLevel();
}
