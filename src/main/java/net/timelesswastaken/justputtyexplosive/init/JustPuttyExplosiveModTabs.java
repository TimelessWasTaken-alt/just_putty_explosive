/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.timelesswastaken.justputtyexplosive.init;

import net.timelesswastaken.justputtyexplosive.JustPuttyExplosiveMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class JustPuttyExplosiveModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JustPuttyExplosiveMod.MODID);
	public static final RegistryObject<CreativeModeTab> PUTTY_EXPLOSIVE = REGISTRY.register("putty_explosive",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.just_putty_explosive.putty_explosive")).icon(() -> new ItemStack(JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY.get())).displayItems((parameters, tabData) -> {
				tabData.accept(JustPuttyExplosiveModBlocks.EXPLOSIVE_PUTTY.get().asItem());
				tabData.accept(JustPuttyExplosiveModItems.BLASTING_CAP.get());
				tabData.accept(JustPuttyExplosiveModItems.DETONATOR.get());
				tabData.accept(JustPuttyExplosiveModBlocks.INCENDIARY_EXPLOSIVE_PUTTY.get().asItem());
				tabData.accept(JustPuttyExplosiveModBlocks.HIGH_EXPLOSIVE_PUTTY.get().asItem());
			}).build());
}