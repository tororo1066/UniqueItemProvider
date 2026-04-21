package tororo1066.uniqueitemprovider

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.opentelemetry.SOpenTelemetry
import tororo1066.uniqueitemprovider.inject.ItemInjector
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

        val legacyComponentSerializer = LegacyComponentSerializer.builder().hexColors().build()
        val miniMessage = MiniMessage.miniMessage()

        var telemetry: SOpenTelemetry? = null
    }

    override fun onStart() {
        telemetry = try {
            SOpenTelemetry(this).also { it.logger }
        } catch (e: Exception) {
            logger.warning("Failed to initialize OpenTelemetry: ${e.message}")
            null
        }

        registerProvider(UpdateProvider())
        UpdateProviderItem
        ItemInjector.loadAll()
        UniqueItemCommand()
    }

    override fun onEnd() {
    }

    override fun getUniqueItem(itemStack: ItemStack): IUniqueItem? {
        return UniqueItem.get(itemStack)
    }

    override fun getOrCreateUniqueItem(itemStack: ItemStack): IUniqueItem {
        return UniqueItem.getOrCreate(itemStack)
    }

    override fun clearUniqueItem(itemStack: ItemStack) {
        UniqueItem.clear(itemStack)
    }

    override fun getProviders(itemStack: ItemStack): List<AbstractItemProvider> {
        return UniqueItem.getProviders(itemStack)
    }

    override fun registerProvider(provider: AbstractItemProvider) {
        providers[provider.namespacedKey] = provider.javaClass
    }
}