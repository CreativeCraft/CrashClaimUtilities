# CrashClaimUtilities

This is an addon plugin for [CrashClaim](https://www.spigotmc.org/resources/crashclaim-claiming-plugin.94037/) providing some utility commands to assist with administration and claiming.

![Screenshot](https://i.imgur.com/OX1Fwk4.jpg)

## Features

- Paginated list of all claims in the current or specified world.
- Easily list all claims owned by a specific player.
- Teleport to players by hovering and clicking their claim in the claim list.
- Teleport to claims using their ID.
- Delete claims using their ID.
- Easier player trusting using `/trust <name>`.
- Offline player support.
- Console support (where applicable).
- 100% customizable messages & command prefix.

## Commands / Permissions

| **Command**                     | **Description**                                          | **Permission**              |
|---------------------------------|----------------------------------------------------------|-----------------------------|
| /trust \<name\>                 | Open the Claim permission menu for the specified player. | crashclaim.user.trust       |                                                          |                             |
| /claimadmin list [page] [world] | Retrieve a list of claims.                               | crashclaim.admin.claimadmin |
| /claimadmin player \<name\>     | Retrieve a list of the specified player's claims.        | crashclaim.admin.claimadmin |
| /claimadmin teleport \<id\>     | Teleport to the specified claim.                         | crashclaim.admin.claimadmin |
| /claimadmin delete \<id\>       | Delete the specified claim.                              | crashclaim.admin.claimadmin |
| /claimadmin help                | View the help menu.                                      | crashclaim.admin.claimadmin |                          |
| /claimadmin reload              | Reload the plugin configuration.                         | crashclaim.admin.claimadmin |                    |

## Support / Feature Requests

- Add Log1x#0001 on Discord
- Message me on Spigot
