package crazypants.enderio.base.material.recipes;

import crazypants.enderio.base.EnderIO;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnderIO.MODID)
public class MaterialRecipes {

  // @SubscribeEvent
  // public static void register(@Nonnull RegistryEvent.Register<IRecipe> event) {
  //
  // for (IAlloy alloy : new NNList<IAlloy>(Alloy.values()).addAll(AlloyEndergy.values())) {
  // abc(String.format("Auto: %s 9 ingots to 1 block", alloy.getBaseName()), false, alloy.getStackBlock(), alloy.getOreIngot(), alloy.getOreIngot(),
  // alloy.getOreIngot(),
  // alloy.getOreIngot(), alloy.getOreIngot(), alloy.getOreIngot(), alloy.getOreIngot(), alloy.getOreIngot(), alloy.getOreIngot());
  //
  // abc(String.format("Auto: %s 1 block to 9 ingots", alloy.getBaseName()), false, alloy.getStackIngot(9), alloy.getOreBlock());
  //
  // abc(String.format("Auto: %s 9 nuggets to 1 ingot", alloy.getBaseName()), false, alloy.getStackIngot(), alloy.getOreNugget(), alloy.getOreNugget(),
  // alloy.getOreNugget(),
  // alloy.getOreNugget(), alloy.getOreNugget(), alloy.getOreNugget(), alloy.getOreNugget(), alloy.getOreNugget(), alloy.getOreNugget());
  //
  // abc(String.format("Auto: %s 1 ingot to 9 nuggets", alloy.getBaseName()), false, alloy.getStackNugget(9), alloy.getStackIngot());
  // }
  //
  // for (EnumDyeColor color : EnumDyeColor.values()) {
  // for (FusedQuartzType type : FusedQuartzType.values()) {
  // abc(String.format("Auto: Coloring %s with %s", type.getName(), color.getUnlocalizedName()), true,
  // new ItemStack(type.getBlock(), 8, color.getMetadata()),
  // type.getOreDictName(), type.getOreDictName(), type.getOreDictName(), "dye" + MaterialOredicts.dyes[color.getDyeDamage()], type.getOreDictName(),
  // type.getOreDictName(), type.getOreDictName(), type.getOreDictName(), type.getOreDictName());
  // if (color != EnumDyeColor.WHITE) {
  // abc(String.format("Auto: Easy Lookup for coloring %s with %s", type.getName(), color.getUnlocalizedName()), true,
  // new ItemStack(type.getBlock(), 8, color.getMetadata()), new ItemStack(type.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()),
  // new ItemStack(type.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()), new ItemStack(type.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()),
  // "dye" + MaterialOredicts.dyes[color.getDyeDamage()], new ItemStack(type.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()),
  // new ItemStack(type.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()), new ItemStack(type.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()),
  // new ItemStack(type.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()), new ItemStack(type.getBlock(), 1, EnumDyeColor.WHITE.getMetadata()));
  // }
  // }
  // }
  //
  // final File userFL = new File(".", "generated.xml");
  //
  // try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFL, false))) {
  // writer.write(s);
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  //
  // // System.out.println(s);
  // }
  //
  // static String s = "\n\n";
  //
  // private static void abc(String name, boolean shaped, ItemStack output, Object... input) {
  // s += " <recipe name=\"" + name + "\" required=\"true\">\n <crafting>\n";
  // s += shaped ? " <grid>\n" : " <shapeless>\n";
  //
  // for (Object object : input) {
  // if (object instanceof String) {
  // s += " <item name=\"oredict:" + object + "\"/>\n";
  // } else {
  // ItemStack stack = (ItemStack) object;
  // ResourceLocation registryName = stack.getItem().getRegistryName();
  // int metadata = stack.getMetadata();
  // s += " <item name=\"" + registryName + ":" + metadata + "\" />\n";
  // }
  // }
  //
  // s += shaped ? " </grid>\n" : " </shapeless>\n";
  // s += " <output name=\"" + output.getItem().getRegistryName() + ":" + output.getMetadata() + "\" amount=\"" + output.getCount() + "\" />\n";
  // s += " </crafting>\n </recipe>\n\n";
  //
  // System.out.println(name);
  // }

}
