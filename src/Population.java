import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Population {
    public int size;
    public ArrayList<Image> population;
    public float mutationRate;
    public BufferedImage target_image;
    public int image_fragments;
    public ArrayList<Image> selectionPool;

    public float maxFitness;
    public float minFitness;
    public float averageFitness;
    public Image bestFittingImage;

    private int totalScore;

    private int image_width;
    private int image_height;


    public Population(int size, int image_fragments, float mutationRate, BufferedImage target_image) {
        this.size = size;
        this.image_fragments = image_fragments;
        this.mutationRate = mutationRate;
        this.target_image = target_image;
        this.population = new ArrayList<>();
        this.selectionPool = new ArrayList<>();
        this.maxFitness = 0.0f;
        this.minFitness = Float.MAX_VALUE;
        this.averageFitness = 0.0f;

        this.totalScore = 0;

        this.image_width = target_image.getWidth();
        this.image_height = target_image.getHeight();
    }

    public void initialize() {
        for (int i = 0; i < this.size; i++) {
            Image image = new Image(image_width, image_height, image_fragments);
            population.add(image);
        }
        this.bestFittingImage = population.get(0);
    }

    public void naturalSelection() {
        // System.out.println("Performing natural selection...");
        selectionPool.clear();
        for (int i = 0; i < population.size(); i++) {
            population.get(i).calculateFitness(target_image);
            selectionPool.add(population.get(i));
        }

        sortSelectionPool();

        int score = 0;
        for (int i = selectionPool.size() - 1; i > 0; i--) {
            selectionPool.get(i).fitnessScore = score;
            score += 1;
        }

        totalScore = 0;
        for (int i = 0; i < population.size(); i++) {
            totalScore += population.get(i).fitnessScore;
        }
        updateFitnessData();
    }

    public void generateNewPopulation() {
        // System.out.println("Generating new population...");

        ArrayList<Image> best = new ArrayList<>();
        for (int i = 0; i < population.size() / 10; i++) {
            best.add(selectionPool.get(i));
        }
        for (int i = 0; i < population.size(); i++) {
            int a = (int)(Math.random() * best.size());
            int b = (int)(Math.random() * best.size());

            Image parentA = best.get(a);
            Image parentB = best.get(b);

            // Image parentA = pickParent();
            // Image parentB = pickParent();

            Image child = parentA.uniformCrossover(parentB);
            child.mutateOriginal(mutationRate);
            child.generateImage();
            this.population.set(i, child);
        }
    }

    private Image pickParent() {
        int i = 0;
        double ts = Math.random() * totalScore;
        while (ts > 0) {
            ts -=selectionPool.get(i).fitnessScore;
            i++;
        }
        i--;
        return selectionPool.get(i);
    }

    private void sortSelectionPool() {
        Collections.sort(selectionPool, new Comparator<Image>() {
            public int compare(Image img1, Image img2) {
                return Float.valueOf(img2.fitness).compareTo(Float.valueOf(img1.fitness));
            }
        });
    }

    public void updateFitnessData() {
        // reset max and min fitness values for current generation
        maxFitness = 0.0f;
        minFitness = Float.MAX_VALUE;
        // get best fitting image
        if (selectionPool.get(0).fitness > bestFittingImage.fitness) {
            this.bestFittingImage = selectionPool.get(0);
        }
        // get max fitness value
        if (selectionPool.get(0).fitness > maxFitness) {
            this.maxFitness = bestFittingImage.fitness;
        }
        // get min fitness value
        if (selectionPool.get(selectionPool.size() - 1).fitness < minFitness) {
            this.minFitness = selectionPool.get(selectionPool.size() - 1).fitness;
        }
        // get average fitness
        getAverageFitness();
    }

    private void getAverageFitness() {
        float total = 0.0f;
        for (int i = 0; i < population.size(); i++) {
            total += population.get(i).fitness;
        }
        //System.out.println(total);
        this.averageFitness = (total / population.size());
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing best image...");
        Image image = bestFittingImage;
        image.draw(g);
    }

    public void exportEndResult() {
        bestFittingImage.export();
    }
}
