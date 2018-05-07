package thaumcraft.api.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class CardRethink extends TheorycraftCard {
	
	@Override
	public boolean initialize(EntityPlayer player, ResearchTableData data) {
		int a=0;
		for (String category:data.categoryTotals.keySet()) {
			a+=data.getTotal(category);
		}
		return a>=10;
	}

	@Override
	public int getInspirationCost() {
		return -1;
	}
	
	@Override
	public String getLocalizedName() {
		return new TextComponentTranslation("card.rethink.name").getUnformattedText();
	}
	
	@Override
	public String getLocalizedText() {
		return new TextComponentTranslation("card.rethink.text").getUnformattedText();
	}

	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {
		if (!initialize(player,data)) return false;
		int a=0;
		for (String category:data.categoryTotals.keySet()) {
			a+=data.getTotal(category);
		}
		a = Math.min(a, 10);
		int tries = 0;
		while (a>0 && tries < 1000) {
			tries++;
			for (String category:data.categoryTotals.keySet()) {
				data.addTotal(category, -1);
				a--;
				if (a<=0 || !data.hasTotal(category)) break;
			}
		}
		data.bonusDraws++;
		data.addTotal("BASICS", MathHelper.getInt(player.getRNG(), 1, 10));
		return true;
	}
	
	
}
