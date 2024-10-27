package tororo1066.uniqueitemprovider

import org.bukkit.inventory.ItemStack

interface IUniqueItemProvider {

    fun getUniqueItem(itemStack: ItemStack): IUniqueItem?

    fun getOrCreateUniqueItem(itemStack: ItemStack): IUniqueItem

    fun getProviders(itemStack: ItemStack): List<AbstractItemProvider>

    fun registerProvider(provider: AbstractItemProvider)
}