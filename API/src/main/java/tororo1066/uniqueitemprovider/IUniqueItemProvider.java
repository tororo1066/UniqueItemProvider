package tororo1066.uniqueitemprovider;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IUniqueItemProvider {

    IUniqueItem getUniqueItem(@NotNull ItemStack itemStack);

    @NotNull IUniqueItem getOrCreateUniqueItem(@NotNull ItemStack itemStack);

    @NotNull List<AbstractItemProvider> getProviders(@NotNull ItemStack itemStack);

    void registerProvider(@NotNull AbstractItemProvider provider);
}