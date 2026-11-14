package net.timelesswastaken.justputtyexplosive.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(ItemStack.class)
public abstract class PuttyItemStackMixin {
	private static final Map<String, String> DISPLAY_NAMES = Map.of(
			"explosive_putty", "Explosive Putty",
			"explosive_putty_rigged", "Explosive Putty (Rigged)",
			"incendiary_explosive_putty", "Incendiary Explosive Putty",
			"incendiary_explosive_putty_rigged", "Incendiary Explosive Putty (Rigged)",
			"high_explosive_putty", "High Explosive Putty",
			"high_explosive_putty_rigged", "High Explosive Putty (Rigged)");

	@Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
	private void justputtyexlosive$puttyDisplayName(CallbackInfoReturnable<Component> cir) {
		ItemStack self = (ItemStack) (Object) this;
		if (self.hasCustomHoverName()) {
			return;
		}
		var key = ForgeRegistries.ITEMS.getKey(self.getItem());
		String displayName = key == null ? null : DISPLAY_NAMES.get(key.getPath());
		if (displayName != null) {
			cir.setReturnValue(Component.literal(displayName));
		}
	}
}
