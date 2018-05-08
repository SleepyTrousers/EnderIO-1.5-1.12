package thaumcraft.api.research.theorycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CardNotation extends TheorycraftCard {
	
	private String cat1, cat2;
	
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound nbt = super.serialize();
		nbt.setString("cat1", cat1);
		nbt.setString("cat2", cat2);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		cat1 = nbt.getString("cat1");
		cat2 = nbt.getString("cat2");
	}
	
	@Override
	public boolean isAidOnly() {
		return true;
	}

	@Override
	public int getInspirationCost() {
		return 1;
	}
		
	@Override
	public String getLocalizedName() {
		return new TextComponentTranslation("card.notation.name").getUnformattedText();
	}
	
	@Override
	public String getLocalizedText() {
		return new TextComponentTranslation("card.notation.text", new Object[] {
				TextFormatting.BOLD+new TextComponentTranslation("tc.research_category."+cat1).getFormattedText()+TextFormatting.RESET,
				TextFormatting.BOLD+new TextComponentTranslation("tc.research_category."+cat2).getFormattedText()+TextFormatting.RESET
				}).getUnformattedText();
	}

	@Override
	public boolean initialize(EntityPlayer player, ResearchTableData data) {
		if (data.categoryTotals.size()<2) return false;
		int lVal=Integer.MAX_VALUE;
		String lKey="";
		int hVal=0;
		String hKey="";
		for (String category:data.categoryTotals.keySet()) {
			int q = data.getTotal(category);
			if (q<lVal) {
				lVal = q;
				lKey = category;
			}
			if (q>hVal) {
				hVal = q;
				hKey = category;
			}
		}
		if (hKey.equals(lKey) || lVal<=0) return false;
		cat1=lKey;
		cat2=hKey;
		return true;
	}

	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {		
		if (cat1==null || cat2==null) return false;
		int lVal = data.getTotal(cat1);
		data.addTotal(cat1, -lVal);
		data.addTotal(cat2, lVal/2 + MathHelper.getInt(player.getRNG(), 0, lVal/2));		
		return true;
	}
	
	
}
