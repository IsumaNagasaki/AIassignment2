package com.company;

import java.awt.*;

public class GrayGene {
    int x1;
    int y1;
    char symb;
    Color color;
    boolean matched;
    public GrayGene(int x1, int y1, char symb, Color color){
        this.x1 = x1;
        this.y1 = y1;
        this.symb = symb;
        this.color = color;
        matched = false;
    }
    public GrayGene(GrayGene gene){
        this.x1 = gene.x1;
        this.y1 = gene.y1;
        this.symb = gene.symb;
        this.color = gene.color;
        matched = gene.matched;
    }
}
