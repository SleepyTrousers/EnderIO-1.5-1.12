package thaumcraft.api;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import thaumcraft.api.items.ItemsTC;

public class ThaumcraftMaterials {

	public static ToolMaterial TOOLMAT_THAUMIUM = EnumHelper.addToolMaterial("THAUMIUM", 3, 500, 7F, 2.5f, 22).setRepairItem(new ItemStack(ItemsTC.ingots));
	public static ToolMaterial TOOLMAT_VOID = EnumHelper.addToolMaterial("VOID", 4, 150, 8F, 3, 10).setRepairItem(new ItemStack(ItemsTC.ingots,1,1));
	public static ToolMaterial TOOLMAT_ELEMENTAL = EnumHelper.addToolMaterial("THAUMIUM_ELEMENTAL", 3, 1500, 9F, 3, 18).setRepairItem(new ItemStack(ItemsTC.ingots));
	public static ArmorMaterial ARMORMAT_THAUMIUM = EnumHelper.addArmorMaterial("THAUMIUM","THAUMIUM", 25, new int[] { 2, 5, 6, 2 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0F);
	public static ArmorMaterial ARMORMAT_SPECIAL = EnumHelper.addArmorMaterial("SPECIAL","SPECIAL", 25, new int[] { 1, 2, 3, 1 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F);
	public static ArmorMaterial ARMORMAT_VOID = EnumHelper.addArmorMaterial("VOID","VOID", 10, new int[] { 3, 6, 8, 3 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1F);
	public static ArmorMaterial ARMORMAT_VOIDROBE = EnumHelper.addArmorMaterial("VOIDROBE","VOIDROBE", 18, new int[] { 4, 7, 9, 4 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 2f);
	public static ArmorMaterial ARMORMAT_FORTRESS = EnumHelper.addArmorMaterial("FORTRESS","FORTRESS", 40, new int[] { 3, 6, 7, 3 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 3f);
	
	public static final Material MATERIAL_TAINT = new MaterialTaint();
	
	public static class MaterialTaint extends Material
	{
	    public MaterialTaint()
	    {
	        super(MapColor.PURPLE);
	        setNoPushMobility();
	    }	    
	    
	    @Override
	    public boolean blocksMovement()
	    {
	        return true;
	    }
	    	    
	}
	
}
