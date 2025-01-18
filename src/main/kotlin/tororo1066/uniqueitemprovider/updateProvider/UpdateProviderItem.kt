package tororo1066.uniqueitemprovider.updateProvider

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.SJavaPlugin
import java.io.File
import java.util.concurrent.CompletableFuture

class UpdateProviderItem(
    val key: String,
    var itemStack: ItemStack,
    val path: String
) {

//    fun toSection(section: ConfigurationSection): ConfigurationSection {
//        section.set("itemStack", itemStack)
//        return section
//    }

    fun save(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val file = File(path)
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            val yaml = YamlConfiguration.loadConfiguration(file)
            yaml.set("$key.itemStack", itemStack)
            yaml.save(file)
        }
    }

    companion object {
        val items = HashMap<String, UpdateProviderItem>()

        fun load(key: String, config: ConfigurationSection, path: String): UpdateProviderItem? {
            val itemStack = config.getItemStack("itemStack") ?: return null
            return UpdateProviderItem(key, itemStack, path)
        }

        init {
            reload()
        }

        fun reload() {
            items.clear()
            SJavaPlugin.sConfig.mkdirs("updateProvider")
            SJavaPlugin.sConfig.loadAllFiles("updateProvider").forEach {
                if (it.extension != "yml") return@forEach
                val yaml = YamlConfiguration.loadConfiguration(it)
                yaml.getKeys(false).forEach second@ { key ->
                    val config = yaml.getConfigurationSection(key) ?: return@second
                    val item = load(key, config, it.path)
                    if (item != null) {
                        items[key] = item
                    }
                }
            }
        }
    }
}