package appeng.api.implementations.items;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import appeng.api.config.SecurityPermissions;
import appeng.api.features.IPlayerRegistry;
import appeng.api.networking.security.ISecurityRegister;

import com.mojang.authlib.GameProfile;

public interface IBiometricCard
{

	/**
	 * Set the  {@link GameProfile} to null, to clear it.
	 */
	void setProfile(ItemStack itemStack, GameProfile username);

	/**
	 * @return {@link GameProfile} of the player encoded on this card, or a blank string.
	 */
	GameProfile getProfile(ItemStack is);

	/**
	 * @param itemStack
	 * @return the full list of permissions encoded on the card.
	 */
	EnumSet<SecurityPermissions> getPermissions(ItemStack itemStack);

	/**
	 * Check if a permission is encoded on the card.
	 * 
	 * @param permission
	 * @return true if this permission is set on the card.
	 */
	boolean hasPermission(ItemStack is, SecurityPermissions permission);

	/**
	 * remove a permission from the item stack.
	 * 
	 * @param itemStack
	 * @param permission
	 */
	void removePermission(ItemStack itemStack, SecurityPermissions permission);

	/**
	 * add a permission to the item stack.
	 * 
	 * @param itemStack
	 * @param permission
	 */
	void addPermission(ItemStack itemStack, SecurityPermissions permission);

	/**
	 * lets you handle submission of security values on the card for custom behavior.
	 * 
	 * @param register
	 * @param pr
	 * @param is
	 */
	void registerPermissions(ISecurityRegister register, IPlayerRegistry pr, ItemStack is);

}
