package tororo1066.uniqueitemprovider

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.SJavaPlugin

class UniqueItemProvider: SJavaPlugin(), IUniqueItemProvider {

    companion object {
        val UNIQUE_ITEM_KEY by lazy {
            NamespacedKey(plugin, "unique_item")
        }

        val PROVIDERS_KEY by lazy {
            NamespacedKey(plugin, "providers")
        }
        val providers = HashMap<NamespacedKey, AbstractItemProvider>()
    }

    override fun onStart() {

    }

    override fun getUniqueItem(itemStack: ItemStack): IUniqueItem? {
        return UniqueItem.get(itemStack)
    }

    override fun getOrCreateUniqueItem(itemStack: ItemStack): IUniqueItem {
        return UniqueItem.getOrCreate(itemStack)
    }

    override fun getProviders(itemStack: ItemStack): List<AbstractItemProvider> {
        return UniqueItem.getProviders(itemStack)
    }

    override fun registerProvider(provider: AbstractItemProvider) {
        providers[provider.namespacedKey] = provider
    }
}