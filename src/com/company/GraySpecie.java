package com.company;

import java.util.ArrayList;

public class GraySpecie {
    int fitness;
    ArrayList<GrayGene> genome;
    public GraySpecie(ArrayList<GrayGene> genome, int fitness){
        this.genome = genome;
        this.fitness = fitness;
    }
}
