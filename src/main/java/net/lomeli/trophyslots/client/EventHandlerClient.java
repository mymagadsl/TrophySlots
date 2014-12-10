package net.lomeli.trophyslots.client;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.lomeli.trophyslots.TrophySlots;
import net.lomeli.trophyslots.core.SimpleUtil;

@SideOnly(Side.CLIENT)
public class EventHandlerClient {
    public boolean markContainerUpdate;
    public static ResourceLocation resourceFile = new ResourceLocation(TrophySlots.MOD_ID + ":textures/cross.png");

    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.END && !player.capabilities.isCreativeMode && markContainerUpdate && !SimpleUtil.hasAllSlotsUnlocked(mc.thePlayer)) {
            if (mc.currentScreen != null && mc.currentScreen instanceof GuiContainer) {
                GuiContainer gui = (GuiContainer) mc.currentScreen;
                if (gui instanceof GuiInventory) {
                    ContainerPlayer containerPlayer = new ContainerPlayer(mc.thePlayer.inventory, !mc.theWorld.isRemote, mc.thePlayer);
                    List slotList = containerPlayer.inventorySlots;
                    if (slotList != null) {
                        for (int i = 4; i < slotList.size(); i++) {
                            Slot slot = containerPlayer.getSlot(i);
                            if (slot != null && !(slot instanceof SlotCrafting)) {
                                if (slot.inventory != containerPlayer.craftMatrix && !SimpleUtil.slotUnlocked(slot.getSlotIndex(), mc.thePlayer))
                                    containerPlayer.inventorySlots.set(i, new SlotLocked(mc.thePlayer.inventory, slot.getSlotIndex(), slot.xDisplayPosition, slot.yDisplayPosition));
                            }
                        }
                    }
                    gui.inventorySlots = containerPlayer;
                } else {
                    List slotList = gui.inventorySlots.inventorySlots;
                    if (slotList != null) {
                        for (int i = 0; i < slotList.size(); i++) {
                            Slot slot = gui.inventorySlots.getSlot(i);
                            if (slot != null && slot instanceof SlotLocked) {
                                if (SimpleUtil.slotUnlocked(slot.getSlotIndex(), player))
                                    ((GuiContainer) mc.currentScreen).inventorySlots.inventorySlots.set(i, new Slot(mc.thePlayer.inventory, slot.getSlotIndex(), slot.xDisplayPosition, slot.yDisplayPosition));
                            }
                        }
                    }
                }
            }
            markContainerUpdate = true;
        }
    }

    @SubscribeEvent
    public void postDrawGuiEvent(GuiScreenEvent.DrawScreenEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.gui != null && event.gui instanceof GuiContainer) {
            if (GuiEffectRenderer.validDate())
                GuiEffectRenderer.snowFlakeRenderer(event);
            if (!SimpleUtil.hasAllSlotsUnlocked(mc.thePlayer) && !mc.thePlayer.capabilities.isCreativeMode) {
                GuiContainer guiContainer = (GuiContainer) event.gui;
                int xBase = guiContainer.guiLeft;
                int yBase = guiContainer.guiTop;
                List slotList = guiContainer.inventorySlots.inventorySlots;
                if (slotList != null) {
                    for (int i = 0; i < slotList.size(); i++) {
                        Slot slot = guiContainer.inventorySlots.getSlot(i);
                        if (slot != null && slot.isSlotInInventory(mc.thePlayer.inventory, slot.getSlotIndex())) {
                            if (!SimpleUtil.slotUnlocked(slot.getSlotIndex(), mc.thePlayer)) {
                                GL11.glPushMatrix();
                                mc.renderEngine.bindTexture(resourceFile);
                                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
                                guiContainer.drawTexturedModalRect(xBase + slot.xDisplayPosition, yBase + slot.yDisplayPosition, 0, 0, 16, 16);
                                GL11.glPopMatrix();
                            }
                        }
                    }
                }
                Slot mouseSlot = guiContainer.theSlot;
                int x = Mouse.getEventX() * guiContainer.width / mc.displayWidth;
                int y = guiContainer.height - Mouse.getEventY() * guiContainer.height / mc.displayHeight - 1;


                if (mc.thePlayer.inventory.getItemStack() == null && mouseSlot != null && mouseSlot.getHasStack())
                    renderItemToolTip(mouseSlot.getStack(), x, y, mc);
            }
        }
    }

    @SubscribeEvent
    public void openGuiEvent(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.gui != null && event.gui instanceof GuiContainer) {
            if (GuiEffectRenderer.validDate())
                GuiEffectRenderer.clearPrevList();
            if (!mc.thePlayer.capabilities.isCreativeMode && !SimpleUtil.hasAllSlotsUnlocked(mc.thePlayer)) {
                GuiContainer gui = (GuiContainer) event.gui;
                if (gui instanceof GuiInventory) {
                    ContainerPlayer containerPlayer = new ContainerPlayer(mc.thePlayer.inventory, !mc.theWorld.isRemote, mc.thePlayer);
                    List slotList = containerPlayer.inventorySlots;
                    if (slotList != null) {
                        for (int i = 4; i < slotList.size(); i++) {
                            Slot slot = containerPlayer.getSlot(i);
                            if (slot != null && !(slot instanceof SlotCrafting)) {
                                if (slot.inventory != containerPlayer.craftMatrix && !SimpleUtil.slotUnlocked(slot.getSlotIndex(), mc.thePlayer))
                                    containerPlayer.inventorySlots.set(i, new SlotLocked(mc.thePlayer.inventory, slot.getSlotIndex(), slot.xDisplayPosition, slot.yDisplayPosition));
                            }
                        }
                    }
                    gui.inventorySlots = containerPlayer;
                } else {
                    List slotList = gui.inventorySlots.inventorySlots;
                    if (slotList != null) {
                        for (int i = 0; i < slotList.size(); i++) {
                            Slot slot = gui.inventorySlots.getSlot(i);
                            if (slot != null && slot.isSlotInInventory(mc.thePlayer.inventory, slot.getSlotIndex())) {
                                if (!SimpleUtil.slotUnlocked(slot.getSlotIndex(), mc.thePlayer))
                                    ((GuiContainer) event.gui).inventorySlots.inventorySlots.set(i, new SlotLocked(mc.thePlayer.inventory, slot.getSlotIndex(), slot.xDisplayPosition, slot.yDisplayPosition));
                            }
                        }
                    }
                }
            }
        }
    }

    public void renderItemToolTip(ItemStack stack, int x, int y, Minecraft mc) {
        List list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                String info = "";
                //if (Loader.isModLoaded("NotEnoughItems"))
                //    info = NEIHelper.itemInfoString(stack);
                list.set(i, stack.getRarity().rarityColor + (String) list.get(i) + info);
            } else
                list.set(i, EnumChatFormatting.GRAY + (String) list.get(i));
        }
        list.add(EnumChatFormatting.BLUE + "" + EnumChatFormatting.ITALIC + SimpleUtil.nameFromStack(stack));

        renderTooltip(list, x, y);
    }

    private void renderTooltip(List<String> tooltipData, int x, int y) {
        int color = 0x505000ff;
        int color2 = 0xf0100010;
        boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
        if (lighting)
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        if (!tooltipData.isEmpty()) {
            int var5 = 0;
            int var6;
            int var7;
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            for (var6 = 0; var6 < tooltipData.size(); ++var6) {
                var7 = fontRenderer.getStringWidth(tooltipData.get(var6));
                if (var7 > var5)
                    var5 = var7;
            }
            var6 = x + 12;
            var7 = y - 12;
            int var9 = 8;
            if (tooltipData.size() > 1)
                var9 += 2 + (tooltipData.size() - 1) * 10;
            if (var6 + var5 + 8 > Minecraft.getMinecraft().currentScreen.width)
                var6 -= 24 + var5;
            if (var7 + var9 + 6 > Minecraft.getMinecraft().currentScreen.height)
                var7 = Minecraft.getMinecraft().currentScreen.height - var9 - 6;

            float z = 300F;
            drawGradientRect(var6 - 3, var7 - 4, z, var6 + var5 + 3, var7 - 3, color2, color2);
            drawGradientRect(var6 - 3, var7 + var9 + 3, z, var6 + var5 + 3, var7 + var9 + 4, color2, color2);
            drawGradientRect(var6 - 3, var7 - 3, z, var6 + var5 + 3, var7 + var9 + 3, color2, color2);
            drawGradientRect(var6 - 4, var7 - 3, z, var6 - 3, var7 + var9 + 3, color2, color2);
            drawGradientRect(var6 + var5 + 3, var7 - 3, z, var6 + var5 + 4, var7 + var9 + 3, color2, color2);
            int var12 = (color & 0xFFFFFF) >> 1 | color & -16777216;
            drawGradientRect(var6 - 3, var7 - 3 + 1, z, var6 - 3 + 1, var7 + var9 + 3 - 1, color, var12);
            drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, z, var6 + var5 + 3, var7 + var9 + 3 - 1, color, var12);
            drawGradientRect(var6 - 3, var7 - 3, z, var6 + var5 + 3, var7 - 3 + 1, color, color);
            drawGradientRect(var6 - 3, var7 + var9 + 2, z, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int var13 = 0; var13 < tooltipData.size(); ++var13) {
                String var14 = tooltipData.get(var13);
                fontRenderer.drawStringWithShadow(var14, var6, var7, -1);
                if (var13 == 0)
                    var7 += 2;
                var7 += 10;
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
        if (!lighting)
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    private void drawGradientRect(int par1, int par2, float z, int par3, int par4, int par5, int par6) {
        float var7 = (par5 >> 24 & 255) / 255F;
        float var8 = (par5 >> 16 & 255) / 255F;
        float var9 = (par5 >> 8 & 255) / 255F;
        float var10 = (par5 & 255) / 255F;
        float var11 = (par6 >> 24 & 255) / 255F;
        float var12 = (par6 >> 16 & 255) / 255F;
        float var13 = (par6 >> 8 & 255) / 255F;
        float var14 = (par6 & 255) / 255F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        WorldRenderer var15 = Tessellator.getInstance().getWorldRenderer();
        var15.startDrawingQuads();
        var15.setColorRGBA_F(var8, var9, var10, var7);
        var15.addVertex(par3, par2, z);
        var15.addVertex(par1, par2, z);
        var15.setColorRGBA_F(var12, var13, var14, var11);
        var15.addVertex(par1, par4, z);
        var15.addVertex(par3, par4, z);
        Tessellator.getInstance().draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
