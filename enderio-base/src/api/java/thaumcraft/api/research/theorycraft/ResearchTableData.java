package thaumcraft.api.research.theorycraft;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta; 

public class ResearchTableData 
{
	public TileEntity table;
	public String player;
	public int inspiration;
	public int inspirationStart;
	public int bonusDraws;
	public int placedCards;	
	public int aidsChosen;
	public int penaltyStart;
	public ArrayList<Long> savedCards = new ArrayList<Long>();
	public ArrayList<String> aidCards = new ArrayList<String>();
	/*
	 * categoryTotals stores the amount of progress per research category. Each point = 1% of progress towards a full theory.  
	 */
	public TreeMap<String,Integer> categoryTotals = new TreeMap<>();
	public ArrayList<String> categoriesBlocked = new ArrayList<String>();
	public ArrayList<CardChoice> cardChoices = new ArrayList<>();
	
	public CardChoice lastDraw;
//	public CardChoice savedCard;
	
	public class CardChoice {
		public TheorycraftCard card;		
		public String key;
		public boolean fromAid;
		public boolean selected;
		public CardChoice(String key, TheorycraftCard card, boolean aid, boolean selected) {
			this.key = key;
			this.card = card;
			this.fromAid = aid;
			this.selected = selected;
		}
		
		@Override
		public String toString() {
			return "key:"+key+
					" card:"+card.getSeed()+
					" fromAid:"+fromAid+
					" selected:"+selected;
		}				
	}
	
	public ResearchTableData(TileEntity tileResearchTable) {
		table = tileResearchTable;
	}
		
	public ResearchTableData(EntityPlayer player2, TileEntity tileResearchTable) {
		player = player2.getName();
		table = tileResearchTable;
	}

	public boolean isComplete() {
		return inspiration<=0;
	}
	
	public boolean hasTotal(String cat) {
		return categoryTotals.containsKey(cat);
	}
	
	public int getTotal(String cat) {
		return categoryTotals.containsKey(cat)?categoryTotals.get(cat):0;
	}
	
	public void addTotal(String cat, int amt) {
		int current = categoryTotals.containsKey(cat)?categoryTotals.get(cat):0;
		current+=amt;
		if (current<=0)
			categoryTotals.remove(cat);
		else
			categoryTotals.put(cat,current);
	}	
	
	public void addInspiration(int amt) {
		inspiration += amt;
		if (inspiration>inspirationStart) inspirationStart = inspiration;
	}	
	
	public NBTTagCompound serialize() {
		NBTTagCompound nbt = new NBTTagCompound();
		
		nbt.setString("player", player);
		nbt.setInteger("inspiration", inspiration);
		nbt.setInteger("inspirationStart", inspirationStart);
		nbt.setInteger("placedCards", placedCards);
		nbt.setInteger("bonusDraws", bonusDraws);		
		nbt.setInteger("aidsChosen", aidsChosen);		
		nbt.setInteger("penaltyStart", penaltyStart);
		
		//
		NBTTagList savedTag = new NBTTagList();
		for (Long card:savedCards) {
			NBTTagCompound gt = new NBTTagCompound();
			gt.setLong("card", card);
			savedTag.appendTag(gt);
		}		
		nbt.setTag("savedCards", savedTag);		 		
				
		//
		NBTTagList categoriesBlockedTag = new NBTTagList();
		for (String category:categoriesBlocked) {
			NBTTagCompound gt = new NBTTagCompound();
			gt.setString("category", category);
			categoriesBlockedTag.appendTag(gt);
		}		
		nbt.setTag("categoriesBlocked", categoriesBlockedTag);		 
		
		//
		NBTTagList categoryTotalsTag = new NBTTagList();
		for (String category:categoryTotals.keySet()) {
			NBTTagCompound gt = new NBTTagCompound();
			gt.setString("category", category);
			gt.setInteger("total", categoryTotals.get(category));
			categoryTotalsTag.appendTag(gt);
		}		
		nbt.setTag("categoryTotals", categoryTotalsTag);		
		
		//
		NBTTagList aidCardsTag = new NBTTagList();
		for (String mc:aidCards) {
			NBTTagCompound gt = new NBTTagCompound();
			gt.setString("aidCard", mc);
			aidCardsTag.appendTag(gt);
		}		
		nbt.setTag("aidCards", aidCardsTag);	
		
		//
		NBTTagList cardChoicesTag = new NBTTagList();
		for (CardChoice mc:cardChoices) {
			NBTTagCompound gt = serializeCardChoice(mc);
			cardChoicesTag.appendTag(gt);
		}		
		nbt.setTag("cardChoices", cardChoicesTag);	
		
		if (lastDraw!=null) nbt.setTag("lastDraw", serializeCardChoice(lastDraw));
//		if (savedCard!=null) nbt.setTag("savedCard", serializeCardChoice(savedCard));
				
		return nbt;
	}
	
	public NBTTagCompound serializeCardChoice(CardChoice mc) {
		NBTTagCompound nbt = new NBTTagCompound();	
		nbt.setString("cardChoice", mc.key);
		nbt.setBoolean("aid", mc.fromAid); 
		nbt.setBoolean("select", mc.selected);
		try {
			nbt.setTag("cardNBT", mc.card.serialize());
		} catch (Exception e) {	}
		return nbt;
	}
	
	public void deserialize(NBTTagCompound nbt) {	
		if (nbt == null) return;	
		inspiration = nbt.getInteger("inspiration");
		inspirationStart = nbt.getInteger("inspirationStart");
		placedCards = nbt.getInteger("placedCards");
		bonusDraws = nbt.getInteger("bonusDraws");
		aidsChosen = nbt.getInteger("aidsChosen");
		penaltyStart = nbt.getInteger("penaltyStart");
		player=nbt.getString("player");
				
		//
		NBTTagList savedTag = nbt.getTagList("savedCards", (byte)10);
		savedCards = new ArrayList<Long>();
		for (int x=0;x<savedTag.tagCount();x++) {
			NBTTagCompound nbtdata = (NBTTagCompound) savedTag.getCompoundTagAt(x);
			savedCards.add(nbtdata.getLong("card"));
		}
		
		//
		NBTTagList categoriesBlockedTag = nbt.getTagList("categoriesBlocked", (byte)10);
		categoriesBlocked = new ArrayList<String>();
		for (int x=0;x<categoriesBlockedTag.tagCount();x++) {
			NBTTagCompound nbtdata = (NBTTagCompound) categoriesBlockedTag.getCompoundTagAt(x);
			categoriesBlocked.add(nbtdata.getString("category"));
		}
		
		//
		NBTTagList categoryTotalsTag = nbt.getTagList("categoryTotals", (byte)10);
		categoryTotals = new TreeMap<String,Integer>();
		for (int x=0;x<categoryTotalsTag.tagCount();x++) {
			NBTTagCompound nbtdata = (NBTTagCompound) categoryTotalsTag.getCompoundTagAt(x);
			categoryTotals.put(nbtdata.getString("category"), nbtdata.getInteger("total"));
		}
		
		//
		NBTTagList aidCardsTag = nbt.getTagList("aidCards", (byte)10);
		aidCards = new ArrayList<String>();
		for (int x=0;x<aidCardsTag.tagCount();x++) {
			NBTTagCompound nbtdata = (NBTTagCompound) aidCardsTag.getCompoundTagAt(x);
			aidCards.add(nbtdata.getString("aidCard"));
		}
		
		//
		
		EntityPlayer pe = null;		
		if (this.table!=null && table.getWorld()!=null && !table.getWorld().isRemote) 
			pe = this.table.getWorld().getPlayerEntityByName(player);
			
		NBTTagList cardChoicesTag = nbt.getTagList("cardChoices", (byte)10);
		cardChoices = new ArrayList<CardChoice>();
		for (int x=0;x<cardChoicesTag.tagCount();x++) {			
			NBTTagCompound nbtdata = (NBTTagCompound) cardChoicesTag.getCompoundTagAt(x);			
			CardChoice cc = deserializeCardChoice(nbtdata);
			if (cc!=null) cardChoices.add(cc);
		}
		
		lastDraw = deserializeCardChoice(nbt.getCompoundTag("lastDraw"));
//		savedCard = deserializeCardChoice(nbt.getCompoundTag("savedCard"));
		
	}
	
	public CardChoice deserializeCardChoice(NBTTagCompound nbt) {	
		if (nbt == null) return null;	
		String key = nbt.getString("cardChoice");			
		TheorycraftCard tc=generateCardWithNBT(nbt.getString("cardChoice"), nbt.getCompoundTag("cardNBT"));				
		if (tc==null) return null;
		return new CardChoice(key,tc,nbt.getBoolean("aid"),nbt.getBoolean("select"));
	}
	
	private boolean isCategoryBlocked(String cat) {
		return this.categoriesBlocked.contains(cat);
	}
	
	public void drawCards(int draw, EntityPlayer pe) {
		
		if (draw==3) {
			if (bonusDraws>0) {
				this.bonusDraws--;
			} else {
				draw=2;
			}
		}
		cardChoices.clear();
		this.player=pe.getName();
		ArrayList<String> availCats = getAvailableCategories(pe);
		ArrayList<String> drawnCards = new ArrayList<>();
		boolean aidDrawn=false;
		int failsafe=0;
		while (draw>0 && failsafe<10000) {
			failsafe++;
			if (!aidDrawn && !aidCards.isEmpty() && pe.getRNG().nextFloat()<=.25) {
				int idx = pe.getRNG().nextInt(aidCards.size());
				String key = aidCards.get(idx);				
				TheorycraftCard card = generateCard(key,-1,pe);
				if (card==null || card.getInspirationCost()>inspiration || isCategoryBlocked(card.getResearchCategory())) continue;		
				
				if (drawnCards.contains(key)) continue;
				drawnCards.add(key);
				cardChoices.add(new CardChoice(key,card,true,false));				
				aidCards.remove(idx);
			} else {
				try {
					String[] cards = TheorycraftManager.cards.keySet().toArray(new String[]{});
					int idx = pe.getRNG().nextInt(cards.length);
					TheorycraftCard card = generateCard(cards[idx],-1,pe);
					if (card==null || card.isAidOnly() || card.getInspirationCost()>inspiration) continue;
					if (card.getResearchCategory()!=null) {
						boolean found=false;
						for (String cn:availCats) {
							if (cn.equals(card.getResearchCategory())) {
								found=true;
								break;
							}
						}
						if (!found) continue;
					}
					
					if (drawnCards.contains(cards[idx])) continue;
					drawnCards.add(cards[idx]);
					cardChoices.add(new CardChoice(cards[idx],card,false,false));
				} catch (Exception e) {
//					e.printStackTrace();
					continue;
				}
			}		
			draw--;
		}
	}
	
	private TheorycraftCard generateCard(String key, long seed, EntityPlayer pe) {
		if (key==null) return null;
		Class<TheorycraftCard> tcc = TheorycraftManager.cards.get(key);
		if (tcc==null) return null;
		TheorycraftCard tc=null;
		try {
			tc = tcc.newInstance();
			if (seed<0)
				if (pe!=null)
					tc.setSeed(pe.getRNG().nextLong());
				else
					tc.setSeed(System.nanoTime());
			else
				tc.setSeed(seed);
			if (pe!=null && !tc.initialize(pe, this)) return null;
		} catch (Exception e) {  }
		return tc;
	}
	
	private TheorycraftCard generateCardWithNBT(String key, NBTTagCompound nbt) {
		if (key==null) return null;
		Class<TheorycraftCard> tcc = TheorycraftManager.cards.get(key);
		if (tcc==null) return null;
		TheorycraftCard tc=null;
		try {
			tc = tcc.newInstance();
			tc.deserialize(nbt);
		} catch (Exception e) {  }
		return tc;
	}
	
	public void initialize(EntityPlayer player1, Set<String> aids) {
		inspirationStart=getAvailableInspiration(player1);
		inspiration= inspirationStart - aids.size();
		
		for (String muk:aids) {
			ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
			if (mu!=null) {
				for (Class clazz:mu.getCards()) {
					aidCards.add(clazz.getName());
				}
			}
		}
	}
	
	
	public ArrayList<String> getAvailableCategories(EntityPlayer player) {
		ArrayList<String> cats = new ArrayList<String>();
		for(String rck: ResearchCategories.researchCategories.keySet()) {
			ResearchCategory rc = ResearchCategories.getResearchCategory(rck);
			if (rc==null || isCategoryBlocked(rck)) continue;
			if (rc.researchKey==null || ThaumcraftCapabilities.knowsResearchStrict(player, rc.researchKey)) {
				cats.add(rck);
			}
		}
		return cats;
	}
	
	public static int getAvailableInspiration(EntityPlayer player) {
		float tot = 5;
		IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
		for (String s:knowledge.getResearchList()) {
			if (ThaumcraftCapabilities.knowsResearchStrict(player, s)) {
				ResearchEntry re = ResearchCategories.getResearch(s);
				if (re==null) continue;
				if (re.hasMeta(EnumResearchMeta.SPIKY)) 				
					tot+=.5f;
				if (re.hasMeta(EnumResearchMeta.HIDDEN)) 
					tot+=.1f;
			}
		}
		return Math.min(15, Math.round(tot));
	}
	
	
}

