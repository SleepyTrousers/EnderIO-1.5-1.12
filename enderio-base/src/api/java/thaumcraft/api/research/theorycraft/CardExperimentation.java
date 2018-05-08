package thaumcraft.api.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import thaumcraft.api.research.ResearchCategories;

public class CardExperimentation extends TheorycraftCard {

	@Override
	public int getInspirationCost() {
		return 1;
	}
	
	@Override
	public String getLocalizedName() {
		return new TextComponentTranslation("card.experimentation.name").getUnformattedText();
	}
	
	@Override
	public String getLocalizedText() {
		return new TextComponentTranslation("card.experimentation.text").getUnformattedText();
	}
	
	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {		
		try {
			String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[] {});
			String cat = s[ player.getRNG().nextInt(s.length) ];
			data.addTotal(cat, MathHelper.getInt(player.getRNG(), 10, 25));
			data.addTotal("BASICS", MathHelper.getInt(player.getRNG(), 1, 10));
		} catch (Exception e) {
			return false;
		}		
		return true;
	}
	
	
}
