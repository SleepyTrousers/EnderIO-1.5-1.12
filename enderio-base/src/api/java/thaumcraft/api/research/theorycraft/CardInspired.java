package thaumcraft.api.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CardInspired extends TheorycraftCard {
	
	String cat = null;
	int amt;
	
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound nbt = super.serialize();
		nbt.setString("cat", cat);
		nbt.setInteger("amt", amt);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		cat = nbt.getString("cat");
		amt = nbt.getInteger("amt");
	}
	
	@Override
	public String getResearchCategory() {
		return cat;
	}
	
	@Override
	public boolean initialize(EntityPlayer player, ResearchTableData data) { 
		if (data.categoryTotals.size()<1) return false;
		int hVal=0;
		String hKey="";
		for (String category:data.categoryTotals.keySet()) {
			int q = data.getTotal(category);
			if (q>hVal) {
				hVal = q;
				hKey = category;
			}
		}
		cat=hKey;
		amt = 10 + (hVal / 2);
		return true;
	}

	@Override
	public int getInspirationCost() {
		return 2;
	}
	
	@Override
	public String getLocalizedName() {
		return new TextComponentTranslation("card.inspired.name").getUnformattedText();
	}
	
	@Override
	public String getLocalizedText() {
		return new TextComponentTranslation("card.inspired.text", new Object[] {
				amt, TextFormatting.BOLD+new TextComponentTranslation("tc.research_category."+cat).getFormattedText()+TextFormatting.RESET}).getUnformattedText();
	}
	
	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {
		data.addTotal(cat, amt);
		return true;
	}
	
	
}
