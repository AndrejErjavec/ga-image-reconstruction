package imageReconstruction;

import mpi.MPI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Population {
    public int size;
    public ArrayList<Image> population;
    public float mutationRate;
    public BufferedImage targetImage;
    public int imageFragments;

    public Image bestFittingImage;
    public float averageFitness;

    public ArrayList<Image> selectionPool;
    private int totalScore;


    public Population(int size, int imageFragments, float mutationRate, BufferedImage targetImage) {
        this.size = size;
        this.imageFragments = imageFragments;
        this.mutationRate = mutationRate;
        this.targetImage = targetImage;
        this.population = new ArrayList<>();
        this.selectionPool = new ArrayList<>();
        this.averageFitness = 0;
        this.totalScore = 0;
    }

    public void initialize() {
        for (int i = 0; i < this.size; i++) {
            Image image = new Image(targetImage.getWidth(), targetImage.getHeight(), imageFragments);
            population.add(image);
        }
        this.bestFittingImage = population.get(0);
    }

    public void naturalSelection() {
        // System.out.println("Performing natural selection...");
        selectionPool.clear();
        switch (Config.runMode) {
            case SEQUENTIAL:
                calculateFitnessSequential();
                break;
            case PARALLEL:
                calculateFitnessParallel();
                selectionPool.addAll(population);
                break;
            case DISTRIBUTED:
                selectionPool.addAll(population);
                break;
        }

        sortSelectionPool();
        updateFitnessData();
    }

    private void calculateFitnessSequential() {
        for (int i = 0; i < population.size(); i++) {
            population.get(i).calculateFitnessSequential(targetImage);
        }
        selectionPool.addAll(population);
    }

    private void calculateFitnessParallel() {
        int threads = Config.threads;
        int chunkHeight = population.size() / threads;

        MSEWorker[] workers = new MSEWorker[threads];
        for (int i = 0; i < threads; i++) {
            if (i == threads - 1) {
                workers[i] = new MSEWorker(i*chunkHeight, population.size(), population, targetImage);
            }
            else {
                workers[i] = new MSEWorker(i*chunkHeight, i*chunkHeight + chunkHeight, population, targetImage);
            }
            workers[i].start();
        }

        for (int i = 0; i < threads; i++) {
            try {
                workers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

            Image child = parentA.onePointCrossover(parentB);
            child.mutateOriginal(mutationRate);
            child.generateImage();
            this.population.set(i, child);
        }
    }

    // not used
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
        selectionPool.sort(new Comparator<Image>() {
            public int compare(Image img1, Image img2) {
                return Float.compare(img2.fitness, img1.fitness);
            }
        });
    }

    private void updateFitnessData() {
        // get best fitting image
        if (selectionPool.get(0).fitness > bestFittingImage.fitness) {
            this.bestFittingImage = selectionPool.get(0);
        }
        // get average fitness
        float total = 0.0f;
        for (int i = 0; i < population.size(); i++) {
            total += population.get(i).fitness;
        }
        this.averageFitness = (total / population.size());
    }
    
    public void draw(Graphics g) {
        // System.out.println("Drawing best image...");
        bestFittingImage.draw(g);
    }

    public void exportEndResult() {
        bestFittingImage.export();
    }
}
