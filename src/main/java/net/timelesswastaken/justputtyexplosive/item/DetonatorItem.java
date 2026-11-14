package net.timelesswastaken.justputtyexplosive.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.timelesswastaken.justputtyexplosive.init.JustPuttyExplosiveModSounds;
import net.timelesswastaken.justputtyexplosive.util.ExplosivePuttyLinkManager;
import net.timelesswastaken.justputtyexplosive.util.ExplosivePuttyLinkManager.LinkResult;

public class DetonatorItem extends Item {
	public DetonatorItem() {
		super(new Item.Properties().durability(16).rarity(Rarity.UNCOMMON));
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		return useOnBlock(context, stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return useOnBlock(context, context.getItemInHand());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (level.isClientSide) {
			return ExplosivePuttyLinkManager.hasStoredLinks(stack)
					? InteractionResultHolder.success(stack)
					: InteractionResultHolder.pass(stack);
		}

		return ExplosivePuttyLinkManager.detonate(level, stack, player, hand)
				? InteractionResultHolder.success(stack)
				: InteractionResultHolder.pass(stack);
	}

	private InteractionResult useOnBlock(UseOnContext context, ItemStack stack) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (level.isClientSide) {
			return ExplosivePuttyLinkManager.isRiggedPutty(level, pos) || ExplosivePuttyLinkManager.hasStoredLinks(stack)
					? InteractionResult.SUCCESS
					: InteractionResult.PASS;
		}

		if (ExplosivePuttyLinkManager.isRiggedPutty(level, pos)) {
			LinkResult result = ExplosivePuttyLinkManager.toggleLink(level, pos, stack);
			sendFeedback(context.getPlayer(), result, level, stack);
			if (result == LinkResult.LINKED || result == LinkResult.UNLINKED) {
				level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
						result == LinkResult.LINKED ? JustPuttyExplosiveModSounds.DETONATOR_LINK.get() : JustPuttyExplosiveModSounds.DETONATOR_UNLINK.get(),
						SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return InteractionResult.SUCCESS;
		}

		return ExplosivePuttyLinkManager.detonate(level, stack, context.getPlayer(), context.getHand())
				? InteractionResult.SUCCESS
				: InteractionResult.PASS;
	}

	private void sendFeedback(Player player, LinkResult result, Level level, ItemStack stack) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		switch (result) {
			case LINKED -> serverPlayer.sendSystemMessage(feedbackComponent("Explosive linked — " + spacesLeftText(level, stack), 0x55FF55, false));
			case UNLINKED -> serverPlayer.sendSystemMessage(feedbackComponent("Explosive unlinked — " + spacesLeftText(level, stack), 0xFF5555, false));
			case FULL -> serverPlayer.sendSystemMessage(feedbackComponent("Detonator is full — no spaces left", 0xFF5555, true));
			case OTHER_DETONATOR -> serverPlayer.sendSystemMessage(feedbackComponent("Already linked to another detonator", 0xFF5555, true));
			case PRIMED -> serverPlayer.sendSystemMessage(feedbackComponent("Explosive is primed — detonation in progress", 0xFF5555, true));
			case INVALID -> {
			}
		}
	}

	private Component feedbackComponent(String text, int color, boolean bold) {
		Style style = Style.EMPTY.withColor(color);
		if (bold) {
			style = style.withBold(true);
		}
		return Component.literal(text).withStyle(style);
	}

	private String spacesLeftText(Level level, ItemStack stack) {
		int spaces = ExplosivePuttyLinkManager.getRemainingSpaces(level, stack);
		if (spaces <= 0) {
			return "no spaces left";
		}
		return spaces + (spaces == 1 ? " space left" : " spaces left");
	}
}
