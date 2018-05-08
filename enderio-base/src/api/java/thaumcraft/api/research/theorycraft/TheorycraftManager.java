package thaumcraft.api.research.theorycraft;

import java.util.HashMap;

public class TheorycraftManager {
	
	//
	
	public static HashMap<String,ITheorycraftAid> aids = new HashMap<>();
	
	public static void registerAid(ITheorycraftAid aid) {
		String key = aid.getClass().getName();
		if (!aids.containsKey(key))
			aids.put(key, aid);
	}
	
	//
	
	public static HashMap<String,Class<TheorycraftCard>> cards = new HashMap<>();	
	
	public static void registerCard(Class cardClass) {
		String key = cardClass.getName();
		if (!cards.containsKey(key))
			cards.put(key, cardClass);
	}
	

}
