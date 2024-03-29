package imageReconstruction;

import java.io.File;

public class Config {
    static String IMAGE_PATH = new File("images/monalisa.gif").getAbsolutePath();
    static int POPULATION_SIZE;
    static int IMAGE_FRAGMENTS;
    static float MUTATION_RATE;
    static int MAX_GENERATIONS;
    static RunMode runMode;
    static boolean useAlphaColors;
    static int threads;

    public Config() {
        this.threads = Runtime.getRuntime().availableProcessors() - 2;
    }

    public void setConfig(RunMode runMode, boolean useAlphaColors) {
        Config.runMode = runMode;
        Config.useAlphaColors = useAlphaColors;
    }
}
