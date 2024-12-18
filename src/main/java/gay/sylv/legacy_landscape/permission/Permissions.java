package gay.sylv.legacy_landscape.permission;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import static gay.sylv.legacy_landscape.LegacyLandscape.id;
import static gay.sylv.legacy_landscape.util.Constants.MOD_ID;

@EventBusSubscriber(
	modid = MOD_ID,
	bus = EventBusSubscriber.Bus.GAME
)
public final class Permissions {
	public static final PermissionNode<Boolean> VANISH = new PermissionNode<>(
		id("vanish"),
		PermissionTypes.BOOLEAN,
		Permissions.Resolvers.LEVEL_1
	);
	/**
	 * @see gay.sylv.legacy_landscape.data_attachment.LegacyAttachments#OMNISCIENT
	 */
	public static final PermissionNode<Boolean> OMNISCIENT = new PermissionNode<>(
		id("omniscient"),
		PermissionTypes.BOOLEAN,
		Permissions.Resolvers.LEVEL_1
	);

	private Permissions() {}

	public static void assertPermission(ServerPlayer player, PermissionNode<Boolean> node, PermissionDynamicContext<?>... context) throws CommandSyntaxException {
		boolean permission = PermissionAPI.getPermission(player, node, context);
		if (!permission) throw new PermissionException(node);
	}

	@SubscribeEvent
	public static void registerCommandPermissions(PermissionGatherEvent.Nodes event) {
		event.addNodes(
			Permissions.VANISH,
			Permissions.OMNISCIENT
		);
	}

	public static final class Resolvers {
		public static final PermissionNode.PermissionResolver<Boolean> LEVEL_1 = (player, playerUUID, contexts) -> hasPermissions(player, 1);

		private Resolvers() {}

		private static boolean hasPermissions(Player player, int level) {
			return player != null && player.hasPermissions(level);
		}
	}
}
