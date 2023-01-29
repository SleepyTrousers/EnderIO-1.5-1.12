package crazypants.enderio.item.darksteel;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ChestGenHooks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.ItemMagnet;

public class DarkSteelItems {

    public static ItemGliderWing itemGliderWing;

    public static ItemDarkSteelArmor itemDarkSteelHelmet;
    public static ItemDarkSteelArmor itemDarkSteelChestplate;
    public static ItemDarkSteelArmor itemDarkSteelLeggings;
    public static ItemDarkSteelArmor itemDarkSteelBoots;
    public static ItemDarkSteelSword itemDarkSteelSword;
    public static ItemDarkSteelPickaxe itemDarkSteelPickaxe;
    public static ItemDarkSteelAxe itemDarkSteelAxe;
    public static ItemDarkSteelShears itemDarkSteelShears;

    public static ItemEndSteelArmor itemEndSteelHelmet;
    public static ItemEndSteelArmor itemEndSteelChestplate;
    public static ItemEndSteelArmor itemEndSteelLeggings;
    public static ItemEndSteelArmor itemEndSteelBoots;
    public static ItemEndSteelSword itemEndSteelSword;
    public static ItemEndSteelPickaxe itemEndSteelPickaxe;
    public static ItemEndSteelAxe itemEndSteelAxe;

    public static ItemStellarArmor itemStellarHelmet;
    public static ItemStellarArmor itemStellarChestplate;
    public static ItemStellarArmor itemStellarLeggings;
    public static ItemStellarArmor itemStellarBoots;
    public static ItemStellarSword itemStellarSword;
    public static ItemStellarPickaxe itemStellarPickaxe;
    public static ItemStellarAxe itemStellarAxe;

    public static ItemMagnet itemMagnet;

    private DarkSteelItems() {}

    public static void createDarkSteelArmorItems() {
        itemGliderWing = ItemGliderWing.create();

        itemDarkSteelHelmet = ItemDarkSteelArmor.create(0);
        itemDarkSteelChestplate = ItemDarkSteelArmor.create(1);
        itemDarkSteelLeggings = ItemDarkSteelArmor.create(2);
        itemDarkSteelBoots = ItemDarkSteelArmor.create(3);

        itemDarkSteelSword = ItemDarkSteelSword.create();
        itemDarkSteelPickaxe = ItemDarkSteelPickaxe.create();
        itemDarkSteelAxe = ItemDarkSteelAxe.create();
        itemDarkSteelShears = ItemDarkSteelShears.create();

        itemEndSteelHelmet = ItemEndSteelArmor.create(0);
        itemEndSteelChestplate = ItemEndSteelArmor.create(1);
        itemEndSteelLeggings = ItemEndSteelArmor.create(2);
        itemEndSteelBoots = ItemEndSteelArmor.create(3);

        itemEndSteelSword = ItemEndSteelSword.create();
        itemEndSteelPickaxe = ItemEndSteelPickaxe.create();
        itemEndSteelAxe = ItemEndSteelAxe.create();

        itemStellarHelmet = ItemStellarArmor.create(0);
        itemStellarChestplate = ItemStellarArmor.create(1);
        itemStellarLeggings = ItemStellarArmor.create(2);
        itemStellarBoots = ItemStellarArmor.create(3);

        itemStellarSword = ItemStellarSword.create();
        itemStellarPickaxe = ItemStellarPickaxe.create();
        itemStellarAxe = ItemStellarAxe.create();

        itemMagnet = ItemMagnet.create();
    }

    public static boolean isArmorPart(Item item, int type) {
        switch (type) {
            case 0:
                return item == itemDarkSteelHelmet || item == itemEndSteelHelmet || item == itemStellarHelmet;
            case 1:
                return item == itemDarkSteelChestplate || item == itemEndSteelChestplate
                        || item == itemStellarChestplate;
            case 2:
                return item == itemDarkSteelLeggings || item == itemEndSteelLeggings || item == itemStellarLeggings;
            case 3:
                return item == itemDarkSteelBoots || item == itemEndSteelBoots || item == itemStellarBoots;
            default:
                return false;
        }
    }

    public static void addLoot() {
        if (Config.lootTheEnder) {
            ItemStack sword = new ItemStack(itemDarkSteelSword, 1, 0);
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(sword, 1, 1, 5));
            ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH)
                    .addItem(new WeightedRandomChestContent(sword, 1, 1, 5));
            ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST)
                    .addItem(new WeightedRandomChestContent(sword, 1, 1, 4));
            ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST)
                    .addItem(new WeightedRandomChestContent(sword, 1, 1, 4));
        }

        if (Config.lootDarkSteelBoots) {
            ItemStack boots = new ItemStack(itemDarkSteelBoots, 1, 0);
            ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(boots, 1, 1, 5));
            ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH)
                    .addItem(new WeightedRandomChestContent(boots, 1, 1, 5));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemRenderer() {
        PoweredItemRenderer dsr = new PoweredItemRenderer();
        MinecraftForgeClient.registerItemRenderer(itemDarkSteelBoots, dsr);
        MinecraftForgeClient.registerItemRenderer(itemDarkSteelLeggings, dsr);
        MinecraftForgeClient.registerItemRenderer(itemDarkSteelChestplate, dsr);
        MinecraftForgeClient.registerItemRenderer(itemDarkSteelHelmet, dsr);
        MinecraftForgeClient.registerItemRenderer(itemDarkSteelSword, dsr);
        MinecraftForgeClient.registerItemRenderer(itemDarkSteelPickaxe, dsr);
        MinecraftForgeClient.registerItemRenderer(itemDarkSteelAxe, dsr);
        MinecraftForgeClient.registerItemRenderer(itemDarkSteelShears, dsr);

        MinecraftForgeClient.registerItemRenderer(itemEndSteelBoots, dsr);
        MinecraftForgeClient.registerItemRenderer(itemEndSteelLeggings, dsr);
        MinecraftForgeClient.registerItemRenderer(itemEndSteelChestplate, dsr);
        MinecraftForgeClient.registerItemRenderer(itemEndSteelHelmet, dsr);
        MinecraftForgeClient.registerItemRenderer(itemEndSteelSword, dsr);
        MinecraftForgeClient.registerItemRenderer(itemEndSteelPickaxe, dsr);
        MinecraftForgeClient.registerItemRenderer(itemEndSteelAxe, dsr);

        MinecraftForgeClient.registerItemRenderer(itemStellarBoots, dsr);
        MinecraftForgeClient.registerItemRenderer(itemStellarLeggings, dsr);
        MinecraftForgeClient.registerItemRenderer(itemStellarChestplate, dsr);
        MinecraftForgeClient.registerItemRenderer(itemStellarHelmet, dsr);
        MinecraftForgeClient.registerItemRenderer(itemStellarSword, dsr);
        MinecraftForgeClient.registerItemRenderer(itemStellarPickaxe, dsr);
        MinecraftForgeClient.registerItemRenderer(itemStellarAxe, dsr);
    }
}
