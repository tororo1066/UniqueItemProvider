package tororo1066.uniqueitemprovider

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.uniqueitemprovider.updateProvider.UpdateProvider
import tororo1066.uniqueitemprovider.updateProvider.UpdateProviderItem

class UniqueItemProvider: SJavaPlugin(UseOption.SInput, UseOption.SConfig), IUniqueItemProvider {

    companion object {
        val UNIQUE_ITEM_KEY by lazy {
            NamespacedKey(plugin, "unique_item")
        }

        val PROVIDERS_KEY by lazy {
            NamespacedKey(plugin, "providers")
        }
        val providers = HashMap<NamespacedKey, Class<out AbstractItemProvider>>()
    }

    override fun onStart() {
        registerProvider(UpdateProvider())
        UpdateProviderItem
        UniqueItemCommand()
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
        providers[provider.namespacedKey] = provider.javaClass
    }
}