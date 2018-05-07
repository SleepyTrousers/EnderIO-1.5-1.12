package thaumcraft.api.research.theorycraft;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CardReject extends TheorycraftCard {
	
	private String cat1;
	
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound nbt = super.serialize();
		nbt.setString("cat", cat1);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		cat1 = nbt.getString("cat");
	}
	
	@Override
	public int getInspirationCost() {
		return 0;
	}
		
	@Override
	public String getLocalizedName() {
		return new TextComponentTranslation("card.reject.name", new Object[] {
				TextFormatting.DARK_BLUE+""+TextFormatting.BOLD+new TextComponentTranslation("tc.research_category."+cat1).getUnformattedText()+TextFormatting.RESET+""+TextFormatting.BOLD
				}).getUnformattedText();
	}
	
	@Override
	public String getLocalizedText() {
		return new TextComponentTranslation("card.reject.text", new Object[] {
				TextFormatting.BOLD+new TextComponentTranslation("tc.research_category."+cat1).getFormattedText()+TextFormatting.RESET
				}).getUnformattedText();
	}

	@Override
	public boolean initialize(EntityPlayer player, ResearchTableData data) {
		ArrayList<String> s = new ArrayList<>();
		for (String c:data.categoryTotals.keySet()) {
			if (!data.categoriesBlocked.contains(c))
				s.add(c);
		}
		if (s.size()<1) return false;
		Random r = new Random(this.getSeed());
		cat1 = s.get(r.nextInt(s.size()));
		return cat1!=null;
	}

	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {		
		if (cat1==null) return false;
		data.addTotal("BASICS", 5);
		data.categoriesBlocked.add(cat1);				
		return true;
	}
	
	
}
