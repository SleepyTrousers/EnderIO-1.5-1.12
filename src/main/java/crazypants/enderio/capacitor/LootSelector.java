package crazypants.enderio.capacitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.EnderIO;
import crazypants.enderio.capacitor.CapacitorHelper.SetType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootSelector extends LootFunction {
  public LootSelector(LootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  private static enum Upgrade {
    SMELTING(SetType.NAME, CapacitorKey.ALLOY_SMELTER_POWER_USE, "smelting"),
    INTAKE(SetType.TYPE, CapacitorKey.ALLOY_SMELTER_POWER_INTAKE, "intake"),
    BUFFER(SetType.TYPE, CapacitorKey.ALLOY_SMELTER_POWER_BUFFER, "buffer"),
    CRAFTING(SetType.NAME, CapacitorKey.CRAFTER_TICKS, "crafting"),
    AREA(SetType.TYPE, CapacitorKey.ATTRACTOR_RANGE, "area"),
    GREEN(SetType.NAME, CapacitorKey.FARM_BONUS_SIZE, "green"),
    RED(SetType.NAME, CapacitorKey.STIRLING_POWER_GEN, "red"),

    ;

    final SetType setType;
    final CapacitorKey capacitorKey;
    final String langKey;

    private Upgrade(SetType setType, CapacitorKey capacitorKey, String langKey) {
      this.setType = setType;
      this.capacitorKey = capacitorKey;
      this.langKey = "loot.capacitor." + langKey;
    }
  }

  @Override
  public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
    Map<Upgrade, Float> keys = new HashMap<Upgrade, Float>();

    float baselevel = getRandomBaseLevel(rand);

    int no = getRandomCount(rand);

    for (int i = 0; i < no; i++) {
      Upgrade randomKey = getUpgrade(rand);
      float randomLevel = getRandomLevel(baselevel, rand);
      if (keys.containsKey(randomKey)) {
        keys.put(randomKey, Math.max(randomLevel, keys.get(randomKey)));
      } else {
        keys.put(randomKey, randomLevel);
      }
    }

    String name = buildBaseName(EnderIO.lang.localize("itemBasicCapacitor.name"), baselevel);
    stack = CapacitorHelper.addCapData(stack, SetType.LEVEL, null, baselevel);

    for (Entry<Upgrade, Float> entry : keys.entrySet()) {
      stack = CapacitorHelper.addCapData(stack, entry.getKey().setType, entry.getKey().capacitorKey, entry.getValue());
      name = buildName(EnderIO.lang.localize(entry.getKey().langKey, name), entry.getValue());
    }

    stack.setStackDisplayName(name);

    return stack;
  }

  private int getRandomCount(Random rand) {
    int no = 0;
    switch (rand.nextInt(30)) {
    case 0:
      no++;
    case 1:
    case 2:
    case 3:
      no++;
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
    case 9:
      no++;
    case 10:
    case 11:
    case 12:
    case 13:
    case 14:
    case 15:
    case 16:
      no++;
    default:
      no++;
    }
    return no;
  }

  private String buildBaseName(String name, float level) {
    if (level < 1f) {
      name = EnderIO.lang.localize("loot.capacitor.baselevel.00", name);
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
      name = EnderIO.lang.localize("loot.capacitor.level.00", name);
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
    return 1f + rand.nextFloat() + rand.nextFloat() + rand.nextFloat() + rand.nextFloat();
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

  private Upgrade getUpgrade(Random rand) {
    return Upgrade.values()[rand.nextInt(Upgrade.values().length)];
  }

  public static class Serializer extends LootFunction.Serializer<LootSelector> {
    public Serializer() {
      super(new ResourceLocation("set_capacitor"), LootSelector.class);
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