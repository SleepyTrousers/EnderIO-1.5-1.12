package crazypants.enderio.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.EnumHelper;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class ItemDarkSteelArmor extends ItemArmor implements IEnergyContainerItem {

  public static final ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("darkSteel", 45, new int[] { 3, 8, 6, 3 }, 25);

  public static final String[] NAMES = new String[] { "helmet", "chestplate", "leggings", "boots" };

  static {
    FMLCommonHandler.instance().bus().register(DarkSteelController.instance);
  }

  public static ItemDarkSteelArmor forArmorType(short armorType) {
    switch (armorType) {
    case 0:
      return EnderIO.itemDarkSteelHelmet;
    case 1:
      return EnderIO.itemDarkSteelLeggings;
    case 2:
      return EnderIO.itemDarkSteelChestplate;
    case 3:
      return EnderIO.itemDarkSteelBoots;
    }
    return null;
  }

  public static ItemDarkSteelArmor create(int armorType) {
    ItemDarkSteelArmor res = new ItemDarkSteelArmor(armorType);
    res.init();
    return res;
  }

  private int capacity;
  private int maxReceive;
  private int maxExtract;

  protected ItemDarkSteelArmor(int armorType) {
    super(MATERIAL, 0, armorType);
    setCreativeTab(EnderIOTab.tabEnderIO);

    String str = "darkSteel_" + NAMES[armorType];
    setUnlocalizedName(str);
    setTextureName("enderIO:" + str);

    capacity = Config.darkSteelPowerStorage;
    maxReceive = capacity / 5;
    maxExtract = maxReceive;
    if(armorType < 2) {
      capacity *= 2;
    }
  }

  protected void init() {
    GameRegistry.registerItem(this, getUnlocalizedName());
  }

  @Override
  public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    list.add("Durability: " + (itemstack.getMaxDamage() - itemstack.getItemDamage()) + "/" + itemstack.getMaxDamage());
    String str = "Power: " + PowerDisplayUtil.formatPower(getEnergyStored(itemstack)) + "/"
        + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemstack)) + " " + PowerDisplayUtil.abrevation();
    list.add(str);
  }

  @Override
  public boolean isDamaged(ItemStack stack) {
    return false;
  }

  @Override
  public String getArmorTexture(ItemStack itemStack, Entity entity, int slot, String layer) {
    if(armorType == 2) {
      return "enderio:textures/models/armor/darkSteel_layer_2.png";
    }
    return "enderio:textures/models/armor/darkSteel_layer_1.png";
  }

  public ItemStack createItemStack() {
    ItemStack res = new ItemStack(this);
    setEnergy(res, 0);
    return res;
  }

  @Override
  public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

    if(container.stackTagCompound == null) {
      container.stackTagCompound = new NBTTagCompound();
    }
    int energy = container.stackTagCompound.getInteger("Energy");
    int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

    if(!simulate) {
      energy += energyReceived;
      container.stackTagCompound.setInteger("Energy", energy);
    }
    return energyReceived;
  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

    if(container == null || container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
      return 0;
    }
    int energy = container.stackTagCompound.getInteger("Energy");
    int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

    if(!simulate) {
      energy -= energyExtracted;
      container.stackTagCompound.setInteger("Energy", energy);
    }
    return energyExtracted;
  }

  @Override
  public int getEnergyStored(ItemStack container) {
    if(container == null || container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
      return 0;
    }
    return container.stackTagCompound.getInteger("Energy");
  }

  @Override
  public int getMaxEnergyStored(ItemStack container) {
    return capacity;
  }

  void setEnergy(ItemStack container, int energy) {
    if(container.stackTagCompound == null) {
      container.stackTagCompound = new NBTTagCompound();
    }
    container.stackTagCompound.setInteger("Energy", energy);
  }

  public void setFull(ItemStack container) {
    setEnergy(container, capacity);
  }

  public boolean isJustCrafted(ItemStack stack) {
    return getEnergyStored(stack) == 0 && getDisplayDamage(stack) == 0;
  }

  //Idea from Mekanism
  //  @ForgeSubscribe
  //  public void onLivingSpecialSpawn(LivingSpawnEvent event)
  //  {
  //    int chance = event.world.rand.nextInt(100);
  //    int armorType = event.world.rand.nextInt(4);
  //    
  //    if(chance < 3)
  //    {
  //      if(event.entityLiving instanceof EntityZombie || event.entityLiving instanceof EntitySkeleton)
  //      {
  //        int sword = event.world.rand.nextInt(100);
  //        int helmet = event.world.rand.nextInt(100);
  //        int chestplate = event.world.rand.nextInt(100);
  //        int leggings = event.world.rand.nextInt(100);
  //        int boots = event.world.rand.nextInt(100);
  //        
  //        if(armorType == 0)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(GlowstoneSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(GlowstoneHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(GlowstoneChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(GlowstoneLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(GlowstoneBoots));
  //        }
  //        else if(armorType == 1)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(LazuliSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(LazuliHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(LazuliChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(LazuliLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(LazuliBoots));
  //        }
  //        else if(armorType == 2)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(OsmiumSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(OsmiumHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(OsmiumChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(OsmiumLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(OsmiumBoots));
  //        }
  //        else if(armorType == 3)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(SteelSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(SteelHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(SteelChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(SteelLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(SteelBoots));
  //        }
  //        else if(armorType == 4)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(BronzeSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(BronzeHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(BronzeChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(BronzeLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(BronzeBoots));
  //        }
  //      }
  //    }
  //  }

}
