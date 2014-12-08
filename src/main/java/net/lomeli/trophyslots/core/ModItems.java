package net.lomeli.trophyslots.core;

import org.lwjgl.input.Keyboard;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.lomeli.trophyslots.TrophySlots;

public class ModItems {
    public static Item trophy;

    public static void loadItems() {
        trophy = new ItemTrophy();
        GameRegistry.registerItem(trophy, "trophy");
    }

    public static class ItemTrophy extends Item {
        public ItemTrophy() {
            super();
            this.setCreativeTab(CreativeTabs.tabMisc);
            this.setMaxStackSize(1);
            this.setUnlocalizedName(TrophySlots.MOD_ID + ".trophy");
            this.setTextureName(TrophySlots.MOD_ID + ":trophy");
        }

        public static boolean fromVillager(ItemStack stack) {
            if (stack.hasTagCompound())
                return stack.getTagCompound().getBoolean("fromVillager");
            return false;
        }

        @Override
        public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
            if (!world.isRemote) {
                if (!TrophySlots.canBuyTrophy && fromVillager(stack))
                    player.addChatComponentMessage(new ChatComponentText(SimpleUtil.translate("msg.trophyslots.villager")));
                else if (!TrophySlots.canUseTrophy)
                    player.addChatComponentMessage(new ChatComponentText(SimpleUtil.translate("msg.trophyslots.trophy")));
                else {
                    if (SimpleUtil.unlockSlot(player))
                        stack.stackSize--;
                }
            }
            return stack;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean var) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                if (fromVillager(stack) && !TrophySlots.canBuyTrophy)
                    list.add(SimpleUtil.translate("subtext.torphyslots.trophy.villager"));
                list.add(SimpleUtil.translate("subtext.trophyslots.trophy"));
                list.add(SimpleUtil.translate(TrophySlots.canUseTrophy ? "subtext.trophyslots.trophy.canUse" : "subtext.trophyslots.trophy.cannotUse"));
            } else {
                list.add(SimpleUtil.translate("subtext.trophyslots.info"));
                if (fromVillager(stack) && !TrophySlots.canBuyTrophy)
                    list.add(SimpleUtil.translate("subtext.torphyslots.trophy.villager"));
            }
        }

        @Override
        public EnumRarity getRarity(ItemStack p_77613_1_) {
            return EnumRarity.epic;
        }
    }
}
