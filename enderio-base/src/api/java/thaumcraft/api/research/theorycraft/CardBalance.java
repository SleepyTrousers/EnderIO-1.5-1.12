package thaumcraft.api.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

public class CardBalance extends TheorycraftCard {

	@Override
	public int getInspirationCost() {
		return 1;
	}
		
	@Override
	public String getLocalizedName() {
		return new TextComponentTranslation("card.balance.name").getUnformattedText();
	}
	
	@Override
	public String getLocalizedText() {
		return new TextComponentTranslation("card.balance.text").getUnformattedText();
	}
	
	@Override
	public boolean initialize(EntityPlayer player, ResearchTableData data) {
		int total=0;
		int size=0;
		for (String c:data.categoryTotals.keySet()) {
			if (data.categoriesBlocked.contains(c)) continue;
			total+=data.categoryTotals.get(c);
			size++;
		}
		return data.categoriesBlocked.size()<data.categoryTotals.size()-1 && total>=size;
	}

	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {
		int total=0;
		int size=0;
		for (String c:data.categoryTotals.keySet()) {
			if (data.categoriesBlocked.contains(c)) continue;
			total+=data.categoryTotals.get(c);
			size++;
		}
		if (data.categoriesBlocked.size()>=data.categoryTotals.size()-1 || total<size) return false;
		for (String category:data.categoryTotals.keySet()) {
			if (data.categoriesBlocked.contains(category)) continue;
			data.categoryTotals.put(category, total/size);
		}
		data.addTotal("BASICS", 5);
		data.penaltyStart++;
		return true;
	}
	
	
}
