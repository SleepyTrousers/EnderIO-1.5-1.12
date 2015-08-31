package mods.immibis.core.api.multipart;


/** Multipart tiles can also implement IPartContainer4 if they have their own parts (as well as cover system parts) */
public interface ICoverableTile {
	/**
	 * Returns an ICoverSystem object, or null if this tile does not support a cover system
	 * @see mods.immibis.core.api.multipart.ICoverSystem 
	 */
	public ICoverSystem getCoverSystem();
}
