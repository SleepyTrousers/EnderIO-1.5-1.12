package mods.immibis.microblocks.api;

public interface IMicroblockSystem2 extends IMicroblockSystem {
	/**
	 * Adds a microblock permission handler.
	 * 
	 * You cannot register two permission handlers with the same name.
	 * You are recommended to make the name start with your mod ID.
	 * You are recommended to make the name human readable.
	 * 
	 * This method may be called from any thread at any time.
	 * 
	 * @param name A name identifying this permission handler.
	 * @param handler The permission handler object.
	 * 
	 * @throws IllegalArgumentException If <var>name</var> is null, <var>handler</var> is null, or
	 *                                  a permission handler is already registered with the same name, or
	 *                                  the permission handler is already registered. 									
	 */
	public void addPermissionHandler(String name, IMicroblockPermissionHandler handler);
	
	/**
	 * Removes a permission handler.
	 * 
	 * This method may be called from any thread at any time.
	 * 
	 * @param handler The permission handler object.
	 * 
	 * @throws IllegalArgumentException If <var>handler</var> is null, or the permission handler is not registered.
	 */
	public void removePermissionHandler(IMicroblockPermissionHandler handler);
}
