package thaumcraft.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

public class OreDictionaryEntries {

	/**
	 * I included this in the API to make it simpler to see what items and blocks are ore dictionaried 
	 */
	public static void initializeOreDictionary() {
		OreDictionary.registerOre("oreAmber", new ItemStack(BlocksTC.oreAmber));
		OreDictionary.registerOre("oreCinnabar", new ItemStack(BlocksTC.oreCinnabar));
		OreDictionary.registerOre("oreQuartz", new ItemStack(BlocksTC.oreQuartz));
		
		OreDictionary.registerOre("oreCrystalAir", new ItemStack(BlocksTC.crystalAir,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalEarth", new ItemStack(BlocksTC.crystalEarth,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalWater", new ItemStack(BlocksTC.crystalWater,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalFire", new ItemStack(BlocksTC.crystalFire,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalOrder", new ItemStack(BlocksTC.crystalOrder,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalEntropy", new ItemStack(BlocksTC.crystalEntropy,1,OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("oreCrystalTaint", new ItemStack(BlocksTC.crystalTaint,1,OreDictionary.WILDCARD_VALUE));
		
		OreDictionary.registerOre("logWood", new ItemStack(BlocksTC.logGreatwood));
		OreDictionary.registerOre("logWood", new ItemStack(BlocksTC.logSilverwood));
		OreDictionary.registerOre("plankWood", new ItemStack(BlocksTC.plankGreatwood));
		OreDictionary.registerOre("plankWood", new ItemStack(BlocksTC.plankSilverwood));
		OreDictionary.registerOre("slabWood", new ItemStack(BlocksTC.slabGreatwood));
		OreDictionary.registerOre("slabWood", new ItemStack(BlocksTC.slabSilverwood));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlocksTC.saplingGreatwood));
		OreDictionary.registerOre("treeSapling", new ItemStack(BlocksTC.saplingSilverwood));
				
		for (Block b:BlocksTC.nitor.values())
			OreDictionary.registerOre("nitor", new ItemStack(b));
		
		OreDictionary.registerOre("gemAmber", new ItemStack(ItemsTC.amber));
		OreDictionary.registerOre("quicksilver", new ItemStack(ItemsTC.quicksilver));
		
		OreDictionary.registerOre("nuggetIron", new ItemStack(ItemsTC.nuggets,1,0));
		OreDictionary.registerOre("nuggetCopper", new ItemStack(ItemsTC.nuggets,1,1));
		OreDictionary.registerOre("nuggetTin", new ItemStack(ItemsTC.nuggets,1,2));
		OreDictionary.registerOre("nuggetSilver", new ItemStack(ItemsTC.nuggets,1,3));
		OreDictionary.registerOre("nuggetLead", new ItemStack(ItemsTC.nuggets,1,4));
		OreDictionary.registerOre("nuggetQuicksilver", new ItemStack(ItemsTC.nuggets,1,5));
		OreDictionary.registerOre("nuggetThaumium", new ItemStack(ItemsTC.nuggets,1,6));
		OreDictionary.registerOre("nuggetVoid", new ItemStack(ItemsTC.nuggets,1,7));
		OreDictionary.registerOre("nuggetBrass", new ItemStack(ItemsTC.nuggets,1,8));
		OreDictionary.registerOre("nuggetQuartz", new ItemStack(ItemsTC.nuggets,1,9));		
		
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,0));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,1));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,2));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,3));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,4));
		OreDictionary.registerOre("nuggetMeat", new ItemStack(ItemsTC.chunks,1,5));
		
		OreDictionary.registerOre("ingotThaumium", new ItemStack(ItemsTC.ingots,1,0));
		OreDictionary.registerOre("ingotVoid", new ItemStack(ItemsTC.ingots,1,1));
		OreDictionary.registerOre("ingotBrass", new ItemStack(ItemsTC.ingots,1,2));
		
		OreDictionary.registerOre("blockThaumium", new ItemStack(BlocksTC.metalBlockThaumium,1,0));
		OreDictionary.registerOre("blockVoid", new ItemStack(BlocksTC.metalBlockVoid,1,1));
		OreDictionary.registerOre("blockBrass", new ItemStack(BlocksTC.metalBlockBrass,1,4));
		
		OreDictionary.registerOre("plateIron", new ItemStack(ItemsTC.plate,1,1));
		OreDictionary.registerOre("plateBrass", new ItemStack(ItemsTC.plate,1,0));
		OreDictionary.registerOre("plateThaumium", new ItemStack(ItemsTC.plate,1,2));
		OreDictionary.registerOre("plateVoid", new ItemStack(ItemsTC.plate,1,3));
				
		OreDictionary.registerOre("clusterIron", new ItemStack(ItemsTC.clusters,1,0));
		OreDictionary.registerOre("clusterGold", new ItemStack(ItemsTC.clusters,1,1));	
		OreDictionary.registerOre("clusterCopper", new ItemStack(ItemsTC.clusters,1,2));
		OreDictionary.registerOre("clusterTin", new ItemStack(ItemsTC.clusters,1,3));
		OreDictionary.registerOre("clusterSilver", new ItemStack(ItemsTC.clusters,1,4));
		OreDictionary.registerOre("clusterLead", new ItemStack(ItemsTC.clusters,1,5));
		OreDictionary.registerOre("clusterCinnabar", new ItemStack(ItemsTC.clusters,1,6));
		OreDictionary.registerOre("clusterQuartz", new ItemStack(ItemsTC.clusters,1,7));
	
	}

}
