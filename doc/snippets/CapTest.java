package crazypants.enderio.machines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.capacitor.CapacitorHelper.SetType;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.base.init.ModObject.itemBasicCapacitor;

public class CapTest {

  public static void test() {
    for (SetType setType : CapacitorHelper.SetType.values()) {
      Set<String> s = new HashSet<>();
      for (CapacitorKey key : CapacitorKey.values()) {
        if (setType != CapacitorHelper.SetType.LEVEL) {
          ItemStack stack = CapacitorHelper.addCapData(new ItemStack(itemBasicCapacitor.getItemNN(), 1, 3), setType, key, 2.222f);
          s.add(stack.getTagCompound().toString());
          // System.out.println(stack.getTagCompound());
        }
      }
      List<String> l = new ArrayList<>(s);
      Collections.sort(l);
      for (String string : l) {
        System.out.println(string);
      }
      System.out.println("-----");
    }
    ItemStack stack = CapacitorHelper.addCapData(new ItemStack(itemBasicCapacitor.getItemNN(), 1, 3), CapacitorHelper.SetType.LEVEL, null, 1.111f);
    System.out.println(stack.getTagCompound());

    throw new RuntimeException();
  }

}
