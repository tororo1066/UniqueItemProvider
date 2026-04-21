package tororo1066.uniqueitemprovider.inject

import com.google.gson.Gson
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.uniqueitemprovider.UniqueItemProvider
import tororo1066.uniqueitemprovider.inject.inject.AbstractInject
import tororo1066.uniqueitemprovider.inject.modification.AbstractModification
import java.io.File
import java.util.concurrent.CompletableFuture

class ItemInjector: Cloneable {

    companion object {
        val injectors = mutableMapOf<String, ItemInjector>()
        private val gson = Gson()

        fun load(key: String, configuration: ConfigurationSection, path: String): ItemInjector {
            val injector = ItemInjector()

            injector.key = key
            injector.path = path

            val injectsSection = configuration.getConfigurationSection("injects")
            injectsSection?.getKeys(false)?.forEach { injectName ->
                val inject = AbstractInject.injects[injectName] ?: return@forEach
                val dataSection = injectsSection.getConfigurationSection(injectName) ?: return@forEach
                val data = inject.createData()
                data.load(dataSection)
                injector.injects[inject] = data
            }

            val modificationName = configuration.getString("modification")
            if (modificationName != null) {
                val modification = AbstractModification.modifications[modificationName]
                if (modification != null) {
                    injector.modification = modification
                    val modificationDataSection = configuration.getConfigurationSection("modificationData")
                    if (modificationDataSection != null) {
                        val modificationData = modification.createData()
                        modificationData.load(modificationDataSection)
                        injector.modificationData = modificationData
                    }
                }
            }

            return injector
        }

        fun loadAll() {
            injectors.clear()
            SJavaPlugin.sConfig.mkdirs("injectors")
            SJavaPlugin.sConfig.loadAllFiles("injectors").forEach {
                if (it.extension != "yml") return@forEach
                val yaml = YamlConfiguration.loadConfiguration(it)
                yaml.getKeys(false).forEach second@ { key ->
                    val config = yaml.getConfigurationSection(key) ?: return@second
                    val injector = load(key, config, it.path)
                    injectors[key] = injector
                }
            }
        }
    }

    var key: String = ""
    var path: String = ""

    val injects = mutableMapOf<AbstractInject<*>, AbstractInjectorData>()

    var modification: AbstractModification<*>? = null
    var modificationData: AbstractInjectorData? = null

    fun tryInject(itemStack: ItemStack, player: Player): ItemStack? {
        val modification = modification ?: return null
        val modificationData = modificationData ?: return null

        if (injects.isEmpty()) return null

        if (injects.all { (inject, data) -> canInject(inject, itemStack, data) }) {
            val result = modify(modification, itemStack, modificationData)
            val log = mapOf(
                "injectorKey" to key,
                "player" to player.name,
                "uuid" to player.uniqueId.toString(),
                "itemStack" to SItem(itemStack).toByteArrayBase64(),
                "resultItemStack" to SItem(result).toByteArrayBase64()
            )

            val jsonLog = gson.toJson(log)

            val telemetryLogger = UniqueItemProvider.telemetry?.logger
            if (telemetryLogger != null) {
                telemetryLogger.logRecordBuilder()
                    .setBody(jsonLog)
                    .emit()
            } else {
                SJavaPlugin.plugin.logger.warning("Telemetry is not enabled. Enable telemetry to see injector logs.")
                SJavaPlugin.plugin.logger.info("Injector log: $jsonLog")
            }
            return result
        }

        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: AbstractInjectorData> canInject(inject: AbstractInject<T>, itemStack: ItemStack, data: AbstractInjectorData): Boolean {
        return inject.canInject(itemStack, data as T)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: AbstractInjectorData> modify(modification: AbstractModification<T>, itemStack: ItemStack, modificationData: AbstractInjectorData): ItemStack {
        return modification.modify(itemStack, modificationData as T)
    }

//    fun save() {
//        val yaml = YamlConfiguration()
//        injects.forEach { (inject, data) ->
//            val section = yaml.createSection("injects.${inject.name}")
//            data.save(section)
//        }
//        modification?.let { modification ->
//            yaml.set("modification", modification.name)
//            modificationData?.let { modificationData ->
//                val section = yaml.createSection("modificationData")
//                modificationData.save(section)
//            }
//        }
//    }

    fun save(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val file = File(path)
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
            val yaml = YamlConfiguration.loadConfiguration(file)
            yaml.set(key, null)
            val injectorSection = yaml.createSection(key)
            injects.forEach { (inject, data) ->
                val section = injectorSection.createSection("injects.${inject.name}")
                data.save(section)
            }
            modification?.let { modification ->
                injectorSection.set("modification", modification.name)
                modificationData?.let { modificationData ->
                    val section = injectorSection.createSection("modificationData")
                    modificationData.save(section)
                }
            }
            yaml.save(file)
        }
    }

    public override fun clone(): ItemInjector {
        val clone = ItemInjector()
        clone.key = this.key
        clone.path = this.path

        clone.injects.putAll(this.injects.mapValues { (_, data) -> data.clone() })
        clone.modification = this.modification
        clone.modificationData = this.modificationData?.clone()
        return clone
    }
}