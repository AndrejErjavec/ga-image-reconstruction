public class Launcher {
    public static void main(String[] args) {
        int POPULATION_SIZE = 10000;
        float MUTATION_RATE = 0.1f;
        int WIDTH = 800;
        int HEIGHT = 600;

        ImageReconstruction ir = new ImageReconstruction(WIDTH, HEIGHT, POPULATION_SIZE, MUTATION_RATE);
        ir.init();
        ir.draw();
    }
}
