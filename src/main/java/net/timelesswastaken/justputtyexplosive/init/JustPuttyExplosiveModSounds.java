/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.timelesswastaken.justputtyexplosive.init;

import net.timelesswastaken.justputtyexplosive.JustPuttyExplosiveMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

public class JustPuttyExplosiveModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JustPuttyExplosiveMod.MODID);
	public static final RegistryObject<SoundEvent> DETONATOR_EQUIP = REGISTRY.register("detonator_equip", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("just_putty_explosive", "detonator_equip")));
	public static final RegistryObject<SoundEvent> DETONATOR_LINK = REGISTRY.register("detonator_link", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("just_putty_explosive", "detonator_link")));
	public static final RegistryObject<SoundEvent> DETONATOR_ACTIVATE = REGISTRY.register("detonator_activate", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("just_putty_explosive", "detonator_activate")));
	public static final RegistryObject<SoundEvent> DETONATOR_UNLINK = REGISTRY.register("detonator_unlink", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("just_putty_explosive", "detonator_unlink")));
	public static final RegistryObject<SoundEvent> BLASTING_CAP_PLACE = REGISTRY.register("blasting_cap_place", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("just_putty_explosive", "blasting_cap_place")));
}