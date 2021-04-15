package com.company;
import java.awt.*;

public class Gene {
    Color color;
    int x1, y1, x2, y2, x3, y3, x4, y4;
    public Gene(Color color, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
        this.color = color;
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
        this.x4 = x4;
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
        this.y4 = y4;
    }
    public Gene(Gene gene){
        this.color = new Color(gene.color.getRed(), gene.color.getGreen(), gene.color.getBlue(), gene.color.getAlpha());
        this.x1 = gene.x1;
        this.x2 = gene.x2;
        this.x3 = gene.x3;
        this.x4 = gene.x4;
        this.y1 = gene.y1;
        this.y2 = gene.y2;
        this.y3 = gene.y3;
        this.y4 = gene.y4;
    }
}
