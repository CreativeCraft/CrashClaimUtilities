package org.creativecraft.crashclaimutilities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.menus.permissions.SimplePermissionMenu;
import net.crashcraft.crashclaim.permissions.PermissionHelper;
import net.crashcraft.crashclaim.permissions.PermissionRoute;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.creativecraft.crashclaimutilities.CrashClaimUtilities;

import java.util.UUID;

@CommandAlias("%claimtrust")
@Description("Trust a player to your claim.")
public class ClaimTrustCommand extends BaseCommand {
    private final CrashClaimUtilities plugin;

    public ClaimTrustCommand(CrashClaimUtilities plugin) {
        this.plugin = plugin;
    }

    /**
     * Open the player permission menu for the specified player.
     *
     * @param sender The command sender.
     * @param value  The target.
     */
    @Default
    @CommandPermission("crashclaim.user.trust")
    @Description("Open the claim permission menu for the specified player.")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onDefault(CommandSender sender, @Optional String value) {
        if (!(sender instanceof Player player)) {
            plugin.sendMessage(sender, plugin.localize("messages.trust.console"));
            return;
        }

        if (value == null) {
            plugin.sendMessage(player, plugin.localize("messages.trust.empty"));
            return;
        }

        UUID target = null;
        Player playerTarget = plugin.getServer().getPlayer(value);
        OfflinePlayer offlineTarget = plugin.getServer().getOfflinePlayer(value);

        if (playerTarget != null) {
            target = playerTarget.getUniqueId();
        }

        if (playerTarget == null && offlineTarget.hasPlayedBefore()) {
            target = offlineTarget.getUniqueId();
        }

        if (target == null) {
            plugin.sendMessage(player, plugin.localize("messages.trust.invalid-player"));
            return;
        }

        Claim claim = plugin.getCrashClaim().getDataManager().getClaim(
            player.getLocation().getBlockX(),
            player.getLocation().getBlockZ(),
            player.getWorld().getUID()
        );

        if (claim == null) {
            plugin.sendMessage(player, plugin.localize("messages.trust.not-claim"));
            return;
        }

        if (player.getUniqueId() == target) {
            plugin.sendMessage(player, plugin.localize("messages.trust.self"));
            return;
        }

        if (claim.getOwner() == target) {
            plugin.sendMessage(player, plugin.localize("messages.trust.owner"));
            return;
        }

        if (!PermissionHelper.getPermissionHelper().hasPermission(
            claim,
            player.getUniqueId(),
            PermissionRoute.MODIFY_PERMISSIONS
        )) {
            plugin.sendMessage(player, plugin.localize("messages.trust.no-permission"));
            return;
        }

        new SimplePermissionMenu(player, claim, target, null).open();
    }
}
