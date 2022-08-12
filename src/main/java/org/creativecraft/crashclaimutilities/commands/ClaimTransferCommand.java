package org.creativecraft.crashclaimutilities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.claimobjects.PermState;
import net.crashcraft.crashclaim.claimobjects.permission.PlayerPermissionSet;
import net.crashcraft.crashclaim.permissions.PermissionRoute;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.creativecraft.crashclaimutilities.CrashClaimUtilities;

import java.util.HashMap;
import java.util.UUID;

@CommandAlias("%claimtransfer")
@Description("Transfer ownership of a claim to another player.")
public class ClaimTransferCommand extends BaseCommand {
    private final CrashClaimUtilities plugin;

    public ClaimTransferCommand(CrashClaimUtilities plugin) {
        this.plugin = plugin;
    }

    /**
     * Open the player permission menu for the specified player.
     *
     * @param sender The command sender.
     * @param value  The target.
     */
    @Default
    @CommandPermission("crashclaim.user.transfer")
    @Description("Transfer ownership of a claim to another player.")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void onDefault(CommandSender sender, @Optional String value) {
        if (!(sender instanceof Player player)) {
            plugin.sendMessage(sender, plugin.localize("messages.transfer.console"));
            return;
        }

        if (value == null) {
            plugin.sendMessage(player, plugin.localize("messages.transfer.empty"));
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
            plugin.sendMessage(player, plugin.localize("messages.transfer.invalid-player"));
            return;
        }

        Claim claim = plugin.getCrashClaim().getDataManager().getClaim(
            player.getLocation().getBlockX(),
            player.getLocation().getBlockZ(),
            player.getWorld().getUID()
        );

        if (claim == null) {
            plugin.sendMessage(player, plugin.localize("messages.transfer.not-claim"));
            return;
        }

        if (!claim.getOwner().equals(player.getUniqueId())) {
            plugin.sendMessage(player, plugin.localize("messages.transfer.not-owner"));
            return;
        }

        if (claim.getOwner().equals(target)) {
            plugin.sendMessage(player, plugin.localize("messages.transfer.already-owned"));
            return;
        }

        String playerName = plugin.getServer().getOfflinePlayer(target).getName();
        UUID previousOwner = claim.getOwner();

        claim.setOwner(target);

        claim.getPerms().setPlayerPermissionSet(
            previousOwner,
            new PlayerPermissionSet(
                PermState.NEUTRAL,
                PermState.NEUTRAL,
                PermState.NEUTRAL,
                PermState.NEUTRAL,
                PermState.NEUTRAL,
                PermState.NEUTRAL,
                new HashMap<>(),
                PermState.NEUTRAL,
                PermState.NEUTRAL,
                PermState.NEUTRAL
            )
        );

        claim.getPerms().setPlayerPermissionSet(
            target,
            plugin.getCrashClaim().getDataManager().getPermissionSetup().getOwnerPermissionSet().clone()
        );

        if (plugin.getConfig().getBoolean("commands.transfer.keep-as-admin")) {
            claim.getPerms().setPlayerPermission(
                previousOwner,
                PermissionRoute.ADMIN,
                1
            );
        }

        if (playerTarget != null && playerTarget.isOnline()) {
            plugin.sendMessage(
                playerTarget,
                plugin
                    .localize("messages.transfer.success-other")
                    .replace("{player}", player.getName())
                    .replace("{id}", Integer.toString(claim.getId()))
            );
        }

        plugin.sendMessage(
            player,
            plugin
                .localize("messages.transfer.success")
                .replace("{player}", playerName == null ? value : playerName)
                .replace("{id}", Integer.toString(claim.getId()))
        );
    }
}
