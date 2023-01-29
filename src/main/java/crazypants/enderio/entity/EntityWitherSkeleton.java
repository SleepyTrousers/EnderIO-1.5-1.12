package crazypants.enderio.entity;

import java.util.Calendar;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.enderio.core.common.util.EntityUtil;
import cpw.mods.fml.common.registry.GameRegistry;

public class EntityWitherSkeleton extends EntitySkeleton {

    public EntityWitherSkeleton(World world) {
        super(world);
    }

    public EntityWitherSkeleton(EntitySkeleton entity) {
        this(entity.worldObj);
        this.copyLocationAndAnglesFrom(entity);
        for (int i = 0; i < entity.getLastActiveItems().length; i++) {
            this.setCurrentItemOrArmor(i, entity.getEquipmentInSlot(i));
        }
    }

    @Override
    public void setFire(int p_70015_1_) {}

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityData) {
        this.getEntityAttribute(SharedMonsterAttributes.followRange)
                .applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));
        this.setSkeletonType(1);
        this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
        this.setCombatTask();
        this.setCanPickUpLoot(
                this.rand.nextFloat() < 0.55F * this.worldObj.func_147462_b(this.posX, this.posY, this.posZ));

        if (this.getEquipmentInSlot(4) == null) {
            Calendar calendar = this.worldObj.getCurrentDate();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.rand.nextFloat() < 0.25F) {
                this.setCurrentItemOrArmor(
                        4,
                        new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                this.equipmentDropChances[4] = 0.0F;
            }
        }

        return entityData;
    }

    @Override
    public int getSkeletonType() {
        return 1;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (source.damageType.equals("player")) {
            EntityPlayer player = (EntityPlayer) source.getEntity();
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.hasTagCompound()) {
                Item cleaver = GameRegistry.findItem("TConstruct", "cleaver");
                int beheading = stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Beheading");
                if (stack.getItem() == cleaver) {
                    beheading += 2;
                }
                if (beheading > 0 && worldObj.rand.nextInt(100) < beheading * 10) {
                    EntityUtil.spawnItemInWorldWithRandomMotion(
                            worldObj,
                            new ItemStack(Items.skull, 1, 1),
                            posX,
                            posY,
                            posZ);
                }
            }
        }
        if (source.getEntity() instanceof EntityLivingBase) {
            int lootingLevel = EnchantmentHelper.getLootingModifier((EntityLivingBase) source.getEntity());
            if (worldObj.rand.nextInt(Math.max(1, 1 - lootingLevel)) == 0) {
                Item necroticBone = GameRegistry.findItem("TConstruct", "materials");
                if (necroticBone != null) {
                    EntityUtil.spawnItemInWorldWithRandomMotion(
                            worldObj,
                            new ItemStack(necroticBone, 1, 8),
                            posX,
                            posY,
                            posZ);
                }
            }
        }
    }
}
