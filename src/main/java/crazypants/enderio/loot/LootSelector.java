package crazypants.enderio.loot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.capacitor.CapacitorHelper;
import crazypants.enderio.capacitor.CapacitorHelper.SetType;
import crazypants.util.NbtValue;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootSelector extends LootFunction {
  public LootSelector(LootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
    Map<WeightedUpgrade, Float> keys = new HashMap<WeightedUpgrade, Float>();

    float baselevel = getRandomBaseLevel(rand);

    int no = getRandomCount(rand);

    for (int i = 0; i < no; i++) {
      WeightedUpgrade randomKey = getUpgrade(rand);
      float randomLevel = getRandomLevel(baselevel, rand);
      baselevel = Math.max(baselevel - randomLevel / 10f * rand.nextFloat(), .5f);
      if (keys.containsKey(randomKey)) {
        randomLevel = Math.max(randomLevel, keys.get(randomKey));
      }
      keys.put(randomKey, randomLevel);
    }

    String name = buildBaseName(EnderIO.lang.localize("itemBasicCapacitor.name"), baselevel);
    stack = CapacitorHelper.addCapData(stack, SetType.LEVEL, null, baselevel);

    for (Entry<WeightedUpgrade, Float> entry : keys.entrySet()) {
      stack = CapacitorHelper.addCapData(stack, entry.getKey().setType, entry.getKey().capacitorKey, entry.getValue());
      name = buildName(EnderIO.lang.localize(entry.getKey().langKey, name), entry.getValue());
    }

    NbtValue.CAPNAME.setString(stack, name);

    String count_s = EnderIO.lang.localize("loot.capacitor.entry.count");
    int count = 8;
    try {
      count = Integer.valueOf(count_s);
    } catch (NumberFormatException e) {
      Log.warn("The value of the language key 'enderio.loot.capacitor.entry.count' is not a valid number!");
    }
    NbtValue.CAPNO.setInt(stack, rand.nextInt(count));

    count_s = EnderIO.lang.localize("loot.capacitor.title.count");
    count = 8;
    try {
      count = Integer.valueOf(count_s);
    } catch (NumberFormatException e) {
      Log.warn("The value of the language key 'enderio.loot.capacitor.title.count' is not a valid number!");
    }
    stack.setStackDisplayName(EnderIO.lang.localize("loot.capacitor.title." + rand.nextInt(count)));

    NbtValue.GLINT.setInt(stack, 1);

    return stack;
  }

  private static final List<WeightedInteger> weightedCount = new ArrayList<WeightedInteger>();
  static {
    weightedCount.add(new WeightedInteger(1, 5));
    weightedCount.add(new WeightedInteger(3, 4));
    weightedCount.add(new WeightedInteger(6, 3));
    weightedCount.add(new WeightedInteger(6, 2));
    weightedCount.add(new WeightedInteger(24, 1));
  }

  private int getRandomCount(Random rand) {
    return WeightedRandom.getRandomItem(rand, weightedCount).getInteger();
  }

  private String buildBaseName(String name, float level) {
    if (level < 1f) {
      name = EnderIO.lang.localize("loot.capacitor.baselevel.10", name);
    } else if (level < 1.5f) {
      name = EnderIO.lang.localize("loot.capacitor.baselevel.15", name);
    } else if (level < 2.5f) {
      name = EnderIO.lang.localize("loot.capacitor.baselevel.25", name);
    } else if (level < 3.5f) {
      name = EnderIO.lang.localize("loot.capacitor.baselevel.35", name);
    } else {
      name = EnderIO.lang.localize("loot.capacitor.baselevel.45", name);
    }
    return name;
  }

  private String buildName(String name, float level) {
    if (level < 1f) {
      name = EnderIO.lang.localize("loot.capacitor.level.10", name);
    } else if (level < 1.5f) {
      name = EnderIO.lang.localize("loot.capacitor.level.15", name);
    } else if (level < 2.5f) {
      name = EnderIO.lang.localize("loot.capacitor.level.25", name);
    } else if (level < 3f) {
      name = EnderIO.lang.localize("loot.capacitor.level.30", name);
    } else if (level < 3.5f) {
      name = EnderIO.lang.localize("loot.capacitor.level.35", name);
    } else if (level < 4f) {
      name = EnderIO.lang.localize("loot.capacitor.level.40", name);
    } else if (level < 4.25f) {
      name = EnderIO.lang.localize("loot.capacitor.level.42", name);
    } else {
      name = EnderIO.lang.localize("loot.capacitor.level.45", name);
    }
    return name;
  }

  private float getRandomBaseLevel(Random rand) {
    if (rand.nextFloat() < .3f) {
      return 1f + (rand.nextFloat() - rand.nextFloat()) * .5f;
    } else {
      return 1f + rand.nextFloat() + rand.nextFloat() + rand.nextFloat() + rand.nextFloat();
    }
  }

  /**
   * Gets a random value that is:
   * 
   * <p>
   * For baselevel==1: Centered around 1.8, spread from 0.8 to 2.8
   * <p>
   * For baselevel==2: Centered around 2.7, spread from 1.4 to 3.8
   * <p>
   * For baselevel==3: Centered around 3.6, spread from 2.5 to 4.2
   */
  private static float getRandomLevel(float baseLevel, Random rand) {
    return (getRandomLevel2(baseLevel - .6f, rand) + getRandomLevel2(baseLevel + .5f, rand)) / 2 - .5f;
  }

  private static float getRandomLevel2(float baseLevel, Random rand) {
    float result = baseLevel + rand.nextFloat() * (4 - baseLevel) / 3;
    for (int i = 1; i < 2; i++) {
      result += rand.nextFloat() / i * 2;
      if (result >= baseLevel + 1)
        result -= rand.nextFloat() / (i + 1);
    }
    return Math.min(result, 4.75f);
  }

  private WeightedUpgrade getUpgrade(Random rand) {
    return WeightedRandom.getRandomItem(rand, WeightedUpgrade.getWeightedupgrades()).getUpgrade();
  }

  public static class Serializer extends LootFunction.Serializer<LootSelector> {
    public Serializer() {
      super(new ResourceLocation(EnderIO.DOMAIN, "set_capacitor"), LootSelector.class);
    }

    @Override
    public void serialize(JsonObject object, LootSelector functionClazz, JsonSerializationContext serializationContext) {
    }

    @Override
    public LootSelector deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
      return new LootSelector(conditionsIn);
    }
  }

  // debug only

  static void test_getRandomLevel(float baselevel) {
    Random rand = new Random();
    int runs = 100000;
    int[] a = new int[50];
    for (int i = 0; i < runs; i++) {
      float randomLevel = getRandomLevel(baselevel, rand);
      int idx = (int) (randomLevel * 10);
      a[idx]++;
    }
    int max = 0;
    for (int j = 0; j < 50; j++) {
      if (a[j] >= max) {
        max = a[j];
      }
    }
    for (int i = max; i > 0; i -= max / 20) {
      for (int j = 0; j < 50; j++) {
        if (a[j] >= i) {
          System.out.print("#");
        } else {
          System.out.print(" ");
        }
      }
      System.out.println();
    }
    System.out.println("0....|...1.0...|...2.0...|...3.0...|...4.0...|...5.0");
  }

}