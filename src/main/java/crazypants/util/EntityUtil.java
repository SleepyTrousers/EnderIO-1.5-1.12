package crazypants.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.StatCollector;

public class EntityUtil {

  public static String getDisplayNameForEntity(String mobName) {
    return StatCollector.translateToLocal("entity." + mobName + ".name");
  }
  
  public static List<String> getAllRegisteredMobNames(boolean excludeBosses) {
    List<String> result = new ArrayList<String>();    
    Set<Map.Entry<Class, String>> entries = EntityList.classToStringMapping.entrySet();
    for(Map.Entry<Class, String> entry : entries) {
      if(EntityLiving.class.isAssignableFrom(entry.getKey()) ) {
        if(!excludeBosses || !IBossDisplayData.class.isAssignableFrom(entry.getKey())) {
          result.add(entry.getValue());
        }
      }
    }    
    return result;
  }
  
  private EntityUtil() {    
  }
  
}
