package tororo1066.uniqueitemprovider.updateProvider

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.persistence.PersistentDataType
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.uniqueitemprovider.UniqueItem
import tororo1066.uniqueitemprovider.UniqueItemProvider
import java.io.File

class EditUpdateProviderItem(val data: UpdateProviderItem? = null): SInventory(SJavaPlugin.plugin, "Edit Update Provider Item", 5) {

    var itemStack = data?.itemStack ?: ItemStack(Material.STONE)

    init {
        setOnClick {
            it.isCancelled = true
            if (it.clickedInventory !is PlayerInventory) return@setOnClick
            val item = it.currentItem ?: return@setOnClick
            if (item.isSimilar(itemStack)) return@setOnClick
            itemStack = item.clone()
            renderMenu(it.whoClicked as Player)
        }
    }

    override fun renderMenu(p: Player): Boolean {
        fillItem(SInventoryItem(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").setCanClick(false))

        removeItem(13)
        setItem(13, SInventoryItem(itemStack).setCanClick(false))

        setItem(0, createInputItem(
            SItem(Material.NAME_TAG)
                .setDisplayName("§a表示名を設定する"),
            SStr::class.java, "表示名を入力してください(/cancelでキャンセル)", action = { sStr, _ ->
                itemStack = SItem(itemStack.clone()).setDisplayName(sStr.toString()).build()
            }
        ))

        setItem(1, createInputItem(
            SItem(Material.BOOK)
                .setDisplayName("§a説明文を設定する"),
            SStr::class.java, "説明文を入力してください(/cancelでキャンセル)", action = { sStr, _ ->
                itemStack = SItem(itemStack.clone()).setLore(sStr.toString().split("\\n")).build()
            }
        ))

        fun setKeyMeta(itemStack: ItemStack, key: String) {
            UniqueItem.getOrCreate(itemStack).addProvider(UpdateProvider().also { it.key = key })
//            itemStack.editMeta { meta ->
//                meta.persistentDataContainer.set(
//                    NamespacedKey(SJavaPlugin.plugin, "unique_item"),
//                    PersistentDataType.STRING,
//                    ""
//                )
//                meta.persistentDataContainer.set(
//                    UniqueItemProvider.PROVIDERS_KEY,
//                    PersistentDataType.TAG_CONTAINER,
//                    meta.persistentDataContainer.adapterContext.newPersistentDataContainer().also { container ->
//                        container.set(
//                            NamespacedKey(SJavaPlugin.plugin, "update"),
//                            PersistentDataType.TAG_CONTAINER,
//                            container.adapterContext.newPersistentDataContainer().also { updateContainer ->
//                                updateContainer.set(
//                                    NamespacedKey(SJavaPlugin.plugin, "key"),
//                                    PersistentDataType.STRING,
//                                    key
//                                )
//                            }
//                        )
//                    }
//                )
//            }
        }

        setItem(40, SInventoryItem(SItem(Material.LIME_STAINED_GLASS_PANE))
            .setDisplayName("§a保存する")
            .setClickEvent {
                if (data == null) {
                    p.closeInventory()
                    SJavaPlugin.sInput.sendInputCUI(p, String::class.java, "キーを入力してください(/cancelでキャンセル)", action = { key ->
                        if (UpdateProviderItem.items.containsKey(key)) {
                            p.sendMessage("§cそのキーは既に使われています")
                            open(p)
                            return@sendInputCUI
                        }
                        SJavaPlugin.sInput.sendInputCUI(p, String::class.java, "保存先を入力してください((folder/file)のように指定する,/cancelでキャンセル)", action = { path ->
                            UpdateProviderItem.items[key] = UpdateProviderItem(
                                key,
                                itemStack.clone().also { setKeyMeta(it, key) },
                                File(SJavaPlugin.plugin.dataFolder, "updateProvider/$path.yml").path
                            ).also { it.save() }
                            p.sendMessage("§a保存しました")
                        }, onCancel = {
                            open(p)
                        })
                    }, onCancel = {
                        open(p)
                    })
                } else {
                    data.itemStack = itemStack.clone().also { setKeyMeta(it, data.key) }
                    data.save()
                    p.sendMessage("§a保存しました")
                    p.closeInventory()
                }
            })

        return true
    }
}