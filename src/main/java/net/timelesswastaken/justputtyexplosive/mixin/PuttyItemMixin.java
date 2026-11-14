package net.timelesswastaken.justputtyexplosive.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(Item.class)
public abstract class PuttyItemMixin {
	private static final Map<String, Integer> STACK_SIZES = Map.of(
			"explosive_putty", 8,
			"explosive_putty_rigged", 1,
			"incendiary_explosive_putty", 4,
			"incendiary_explosive_putty_rigged", 1,
			"high_explosive_putty", 2,
			"high_explosive_putty_rigged", 1);

	@Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
	private void justputtyexlosive$puttyStackSize(CallbackInfoReturnable<Integer> cir) {
		Integer stackSize = STACK_SIZES.get(registryPath());
		if (stackSize != null) {
			cir.setReturnValue(stackSize);
		}
	}

	protected String registryPath() {
		var key = ForgeRegistries.ITEMS.getKey((Item) (Object) this);
		return key == null ? "" : key.getPath();
	}
}
