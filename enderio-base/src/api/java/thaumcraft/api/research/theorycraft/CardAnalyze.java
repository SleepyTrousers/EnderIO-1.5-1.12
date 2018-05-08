package thaumcraft.api.research.theorycraft;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;

public class CardAnalyze extends TheorycraftCard {
	
	String cat = null;
	
	@Override
	public NBTTagCompound serialize() {
		NBTTagCompound nbt = super.serialize();
		nbt.setString("cat", cat);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		cat = nbt.getString("cat");
	}
	
	@Override
	public String getResearchCategory() {
		return cat;
	}
	
	@Override
	public boolean initialize(EntityPlayer player, ResearchTableData data) { 
		Random r = new Random(this.getSeed());
		ArrayList<String> cats = new ArrayList<>();
		for (ResearchCategory rc:ResearchCategories.researchCategories.values()) {
			if (rc.key=="BASICS") continue;
			if (ThaumcraftCapabilities.getKnowledge(player).getKnowledge(
				EnumKnowledgeType.OBSERVATION, ResearchCategories.researchCategories.get(cat))>0)
				cats.add(rc.key);
		}
		if (cats.size()>0) { 
			cat = cats.get(r.nextInt(cats.size()));
		}		
		return cat!=null;
	}

	@Override
	public int getInspirationCost() {
		return 2;
	}
	
	@Override
	public String getLocalizedName() {
		return new TextComponentTranslation("card.analyze.name", new Object[] {
				TextFormatting.DARK_BLUE+""+TextFormatting.BOLD+new TextComponentTranslation("tc.research_category."+cat).getFormattedText()+TextFormatting.RESET
				}).getUnformattedText();
	}
	
	@Override
	public String getLocalizedText() {
		return new TextComponentTranslation("card.analyze.text", new Object[] {
				TextFormatting.BOLD+new TextComponentTranslation("tc.research_category."+cat).getFormattedText()+TextFormatting.RESET,
				TextFormatting.BOLD+new TextComponentTranslation("tc.research_category.BASICS").getFormattedText()+TextFormatting.RESET
				}).getUnformattedText();
	}
	
	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data) {
		ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
		int k = ThaumcraftCapabilities.getKnowledge(player).getKnowledge(EnumKnowledgeType.OBSERVATION, rc);		
		if (k>=1) {
			data.addTotal("BASICS", 5);
			ThaumcraftCapabilities.getKnowledge(player).addKnowledge(
					EnumKnowledgeType.OBSERVATION, rc, -EnumKnowledgeType.OBSERVATION.getProgression());
			data.addTotal(cat, MathHelper.getInt(player.getRNG(), 25, 50));
			return true;
		}
		return false;
	}
	
	
}
