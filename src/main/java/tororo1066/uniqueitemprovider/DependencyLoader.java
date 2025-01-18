package tororo1066.uniqueitemprovider;

import tororo1066.tororopluginapi.AbstractDependencyLoader;
import tororo1066.tororopluginapi.Library;
import tororo1066.tororopluginapi.LibraryType;
import tororo1066.tororopluginapi.Repository;

public class DependencyLoader extends AbstractDependencyLoader {
    public DependencyLoader() {}

    @Override
    public Library[] getDependencies() {
        return new Library[]{
                LibraryType.KOTLIN.createLibrary()
        };
    }
}
