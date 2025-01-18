package tororo1066.uniqueitemprovider

import org.bukkit.NamespacedKey
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.sCommand.*
import tororo1066.uniqueitemprovider.updateProvider.EditUpdateProviderItem
import tororo1066.uniqueitemprovider.updateProvider.UpdateProviderItem

class UniqueItemCommand: SCommand(
    "uniqueitemprovider",
    perm = "uniqueitemprovider.op",
    alias = listOf("uip")
) {

    @SCommandBody
    val setUniqueItem = command().addArg(SCommandArg("create"))
        .setPlayerFunction { sender, _, _, _ ->
            val item = sender.inventory.itemInMainHand
            UniqueItem.getOrCreate(item)
            sender.sendMessage("UniqueItem created.")
        }

    @SCommandBody
    val addProvider = command().addArg(SCommandArg("addProvider"))
        .addArg(SCommandArg(SCommandArgType.STRING).addAlias("namespace"))
        .addArg(SCommandArg(SCommandArgType.STRING).addAlias("key"))
        .setPlayerFunction { sender, _, _, args ->
            val item = UniqueItem.get(sender.inventory.itemInMainHand) ?: run {
                sender.sendMessage("UniqueItem not found.")
                return@setPlayerFunction
            }
            val provider = UniqueItemProvider.providers[NamespacedKey(args[0], args[1])] ?: return@setPlayerFunction
            item.addProvider(provider.getConstructor().newInstance())
            sender.sendMessage("Provider added.")
        }

    @SCommandBody
    val createUpdateProviderItem = command().addArg(SCommandArg("createUpdateProviderItem"))
        .setPlayerFunction { sender, _, _, _ ->
            EditUpdateProviderItem().open(sender)
        }

    @SCommandBody
    val editUpdateProviderItem = command().addArg(SCommandArg("createUpdateProviderItem"))
        .addArg(SCommandArg(SCommandArgType.STRING).addChangeableAllowString(object : ChangeableAllowString() {
            override fun getAllowString(data: SCommandData): Collection<String> {
                return UpdateProviderItem.items.keys
            }
        }))
        .setPlayerFunction { sender, _, _, args ->
            val item = UpdateProviderItem.items[args[1]] ?: return@setPlayerFunction
            EditUpdateProviderItem(item).open(sender)
        }

    @SCommandBody
    val getUpdateProviderItem = command().addArg(SCommandArg("getUpdateProviderItem"))
        .addArg(SCommandArg(SCommandArgType.STRING).addChangeableAllowString(object : ChangeableAllowString() {
            override fun getAllowString(data: SCommandData): Collection<String> {
                return UpdateProviderItem.items.keys
            }
        }))
        .setPlayerFunction { sender, _, _, args ->
            val item = UpdateProviderItem.items[args[1]] ?: return@setPlayerFunction
            sender.inventory.addItem(item.itemStack)
            sender.sendMessage("Item added.")
        }

    @SCommandBody
    val reload = command().addArg(SCommandArg("reload"))
        .setPlayerFunction { sender, _, _, _ ->
            UpdateProviderItem.reload()
            sender.sendMessage("Reloaded.")
        }
}