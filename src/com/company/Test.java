package com.company;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static java.lang.Math.*;

public class Test extends JPanel {
    BufferedImage image;
    BufferedImage coloredImageSmall;
    BufferedImage coloredImageBig;
    BufferedImage grayToneImage;
    int width;
    int height;

    public Test() {
        try {
            File input = new File("image1.jpg");
            image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();

        } catch (Exception e) {
        }
    }


    public void paint(Graphics g) {
        Image img = createImageWithText();
        BufferedImage img2 = GrayImage();
        BufferedImage img3 = ColouredImage();
        Image img4 = Recombination(img2, img3);
        try {
            File input = new File("image1.jpg");
            image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();
        } catch (Exception e) {
        }
        ArrayList<Color> ColoredList = BaseColors(img3, 0);
        ArrayList<Color> ColoredListLast = BaseColors(img3, 1);
        Image he = BaseColorsImage(ColoredList);
        Image ha = BaseColorsImage(ColoredListLast);
        BufferedImage Small = SmallIn3(img3);

        Gene[] genes = BigColoursImage(ColoredList, 50);
        Gene[] bestOf = GeneticColours(Small, ColoredList);
        BufferedImage haha = ImageFromColorGenes(bestOf, ColoredList.get(0));
        BufferedImage coloured = BigImageFromColorGenes(bestOf, ColoredList.get(0));

        g.drawImage(Small, 140, 20, this);
        g.drawImage(he, 140 + (width / 3), 20, this);
        g.drawImage(ha, 140 + 2 * (width / 3), 20, this);
        g.drawImage(haha, 140, 20 + (width / 3), this);
        g.drawImage(img4, 652, 20, this);
        //g.drawImage(img2, 140, 532, this);
        g.drawImage(coloured, 140, 532, this);
        g.drawImage(img3, 652, 532, this);
        //g.drawImage(img, 1164, 20, this);
    }

    private Image createImageWithText() {

        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        g.drawString("ha", 1, 1);
        return bufferedImage;
    }

    private BufferedImage GrayImage() {
        BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try {
            File input = new File("image1.jpg");
            image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();
            for (int i = 0; i < height; i++) {

                for (int j = 0; j < width; j++) {

                    Color c = new Color(image.getRGB(j, i));
                    int red = (int) (c.getRed() * 0.300);
                    int green = (int) (c.getGreen() * 0.587);
                    int blue = (int) (c.getBlue() * 0.113);
                    int Color = red + green + blue;
                    Color newColor = new Color(Color, Color, Color);
                    image1.setRGB(j, i, newColor.getRGB());
                }
            }
            File output = new File("grayscale.jpg");
            ImageIO.write(image1, "jpg", output);
        } catch (Exception e) {
        }
        return image1;
    }

    private BufferedImage ColouredImage() {
        BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try {
            File input = new File("image1.jpg");
            image = ImageIO.read(input);
            width = image.getWidth();
            height = image.getHeight();
            for (int i = 0; i < height; i++) {

                for (int j = 0; j < width; j++) {

                    Color c = new Color(image.getRGB(j, i));
                    int red = (int) (c.getRed());
                    int green = (int) (c.getGreen());
                    int blue = (int) (c.getBlue());
                    float sum = red + green + blue;
                    float red_percent = red / sum;
                    float green_percent = green / sum;
                    float blue_percent = blue / sum;
                    float difference = 255 - sum;
                    int red_plus = (int) (red_percent * difference);
                    int green_plus = (int) (green_percent * difference);
                    int blue_plus = (int) (blue_percent * difference);
                    Color c1 = new Color(red + red_plus, green + green_plus, blue + blue_plus);
                    image1.setRGB(j, i, c1.getRGB());
                }
            }
            File output = new File("ColourScale.jpg");
            ImageIO.write(image1, "jpg", output);
        } catch (Exception e) {
        }
        return image1;
    }

    private Image Recombination(BufferedImage GrayImage, BufferedImage ColouredImage) {
        BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try {
            for (int i = 0; i < height; i++) {

                for (int j = 0; j < width; j++) {

                    Color gray = new Color(GrayImage.getRGB(j, i));
                    Color color = new Color(ColouredImage.getRGB(j, i));
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();
                    int depth = gray.getBlue() + gray.getRed() + gray.getGreen();
                    float red_percent = red / (float) (red + blue + green);
                    float green_percent = green / (float) (red + blue + green);
                    float blue_percent = blue / (float) (red + blue + green);
                    float difference = depth - (red + blue + green);
                    int red_plus = (int) (red_percent * difference);
                    int green_plus = (int) (green_percent * difference);
                    int blue_plus = (int) (blue_percent * difference);
                    int new_red = (red + red_plus);
                    if (new_red > 255) {
                        new_red = 255;
                    }
                    if (new_red < 0) {
                        new_red = 0;
                    }
                    int new_green = (green + green_plus);
                    if (new_green > 255) {
                        new_green = 255;
                    }
                    if (new_green < 0) {
                        new_green = 0;
                    }
                    int new_blue = (blue + blue_plus);
                    if (new_blue > 255) {
                        new_blue = 255;
                    }
                    if (new_blue < 0) {
                        new_blue = 0;
                    }
                    Color c1 = new Color(new_red, new_green, new_blue);
                    image1.setRGB(j, i, c1.getRGB());
                    Color now = new Color(image1.getRGB(j, i));
                }
            }
            File output = new File("recombination.jpg");
            ImageIO.write(image1, "jpg", output);
        } catch (Exception e) {
        }
        return image1;
    }

    private float MaxOf3(float a, float b, float c) {
        if ((a > b) & (a > c)) return a;
        else if (b > c) return b;
        else return c;
    }

    private BufferedImage SmallIn3(BufferedImage ColoredImage) {
        BufferedImage image1 = new BufferedImage((int) (width / 3), (int) (width / 3), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height / 3; i++) {
            for (int j = 0; j < width / 3; j++) {
                Color color = new Color(ColoredImage.getRGB(3 * j, 3 * i));
                image1.setRGB(j, i, color.getRGB());
            }
        }
        return image1;
    }

    private Image BaseColorsImage(ArrayList<Color> BestColours) {
        BufferedImage image1 = new BufferedImage((int) (width / 3), (int) (width / 3), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width / 18; i++) {
            for (int j = 0; j < height / 3; j++) {
                Color c1 = BestColours.get(0);
                image1.setRGB(j, i, c1.getRGB());
            }
            if (BestColours.size() >= 2) {
                for (int j = 0; j < height / 3; j++) {
                    Color c2 = BestColours.get(1);
                    image1.setRGB(j, i + 28, c2.getRGB());
                }
            }
            if (BestColours.size() >= 3) {
                for (int j = 0; j < height / 3; j++) {
                    Color c1 = BestColours.get(2);
                    image1.setRGB(j, i + 56, c1.getRGB());
                }
            }
            if (BestColours.size() >= 4) {
                for (int j = 0; j < height / 3; j++) {
                    Color c1 = BestColours.get(3);
                    image1.setRGB(j, i + 84, c1.getRGB());
                }
            }
            if (BestColours.size() >= 5) {
                for (int j = 0; j < height / 3; j++) {
                    Color c1 = BestColours.get(4);
                    image1.setRGB(j, i + 112, c1.getRGB());
                }
            }
            if (BestColours.size() >= 6) {
                for (int j = 0; j < height / 3; j++) {
                    Color c1 = BestColours.get(5);
                    image1.setRGB(j, i + 140, c1.getRGB());
                }
            }
        }
        return image1;
    }

    private float Fitness(BufferedImage orig, Gene[] chromosomes, ArrayList<Color> BaseColors) {
        BufferedImage iteration = ImageFromColorGenes(chromosomes, BaseColors.get(0));
        int width1 = iteration.getWidth();
        int height1 = iteration.getHeight();
        float fit = 0;
        for (int x = 0; x < width1; x += 2) {
            for (int y = 0; y < height1; y += 2) {
                Color origin = new Color(orig.getRGB(x, y));
                Color iter = new Color(iteration.getRGB(x, y));
                if ((abs(origin.getRed() - iter.getRed()) < 15) & (abs(origin.getGreen() - iter.getGreen()) < 15) & (abs(origin.getBlue() - iter.getBlue()) < 15)) {
                    fit++;
                }
            }
        }
        fit = (100 * (fit / (width1 * height1 / 4)));
        return fit;
    }

    private ArrayList<Color> BaseColors(BufferedImage ColoredImage, int flag) {

        ArrayList<Color> ColoredList = new ArrayList<>();
        ArrayList<Integer> frequency = new ArrayList<>();
        ArrayList<Integer> BestFrequency = new ArrayList<>();
        ArrayList<Integer> LastFrequency = new ArrayList<>();
        ArrayList<Color> BestColours = new ArrayList<>();
        ArrayList<Color> LastColours = new ArrayList<>();
        for (int i = 0; i < height / 3; i++) {
            for (int j = 0; j < width / 3; j++) {
                Color color = new Color(ColoredImage.getRGB(3 * j, 3 * i));
                float r_percent = 100 * color.getRed() / (float) 255;
                float g_percent = 100 * color.getGreen() / (float) 255;
                float b_percent = 100 * color.getBlue() / (float) 255;
                if ((r_percent > 40) || (g_percent > 40) || (b_percent > 40)) {
                    ColoredList.add(color);
                    frequency.add(1);
                }
            }
        }
        for (int i = 0; i < ColoredList.size(); i++) {
            int red = ColoredList.get(i).getRed();
            int green = ColoredList.get(i).getGreen();
            int blue = ColoredList.get(i).getBlue();
            float r_percent = 100 * red / (float) 255;
            float g_percent = 100 * green / (float) 255;
            float b_percent = 100 * blue / (float) 255;
            for (int j = i + 1; j < ColoredList.size() - i; j++) {
                int red_comp = ColoredList.get(j).getRed();
                int green_comp = ColoredList.get(j).getGreen();
                int blue_comp = ColoredList.get(j).getBlue();
                float r_percent_comp = 100 * red_comp / (float) 255;
                float g_percent_comp = 100 * green_comp / (float) 255;
                float b_percent_comp = 100 * blue_comp / (float) 255;
                if ((abs(r_percent - r_percent_comp) < 10) && (abs(g_percent - g_percent_comp) < 10) && (abs(b_percent - b_percent_comp) < 10)) {
                    frequency.set(i, frequency.get(i) + 1);
                    float max_percent = MaxOf3(r_percent_comp, g_percent_comp, b_percent_comp);
                    if (((max_percent == r_percent_comp) & (r_percent_comp > r_percent)) || ((max_percent == g_percent_comp) & (g_percent_comp > g_percent)) || ((max_percent == b_percent_comp) & (b_percent_comp > b_percent)))
                        ColoredList.set(i, ColoredList.get(j));
                    ColoredList.remove(j);
                    frequency.remove(j);
                }
            }
        }
        if (flag == 0) {
            BestFrequency.add(frequency.get(0));
            BestColours.add(ColoredList.get(0));
            if (frequency.size() > 5) {
                for (int i = 1; i < 6; i++) {
                    BestFrequency.add(frequency.get(i));
                    BestColours.add(ColoredList.get(i));
                }
            }
            for (int k = 0; k < frequency.size(); k++) {

                for (int t = 0; t < BestFrequency.size(); t++) {
                    if (frequency.get(k) > BestFrequency.get(t)) {
                        BestFrequency.add(t, frequency.get(k));
                        BestColours.add(t, ColoredList.get(k));
                        break;
                    }
                }
                if (BestFrequency.size() == 11) {
                    BestFrequency.remove(10);
                    BestColours.remove(10);
                }
            }
        } else {
            LastFrequency.add(frequency.get(0));
            LastColours.add(ColoredList.get(0));
            if (frequency.size() > 5) {
                for (int i = 1; i < 6; i++) {
                    LastFrequency.add(frequency.get(i));
                    LastColours.add(ColoredList.get(i));
                }
            }
            for (int k = 0; k < frequency.size(); k++) {

                for (int t = 0; t < LastFrequency.size(); t++) {
                    if (frequency.get(k) < LastFrequency.get(t)) {
                        LastFrequency.add(t, frequency.get(k));
                        LastColours.add(t, ColoredList.get(k));
                        break;
                    }
                }
                if (LastFrequency.size() == 7) {
                    LastFrequency.remove(6);
                    LastColours.remove(6);
                }
            }
        }
        if (flag == 0) {
            return BestColours;
        } else {
            return LastColours;
        }
    }

    //makes big picture from color1_genome
    private BufferedImage BigImageFromColorGenes(Gene[] chromosomes, Color FirstColor) {
        BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image1.setRGB(i, j, FirstColor.getRGB());
            }
        }
        Graphics g = image1.createGraphics();
        for (int i = 0; i < chromosomes.length; i++) {
            g.setColor(chromosomes[i].color);
            int[] xArr = {chromosomes[i].x1 * 3, chromosomes[i].x2 * 3, chromosomes[i].x4 * 3, chromosomes[i].x3 * 3};
            int[] yArr = {chromosomes[i].y1 * 3, chromosomes[i].y2 * 3, chromosomes[i].y4 * 3, chromosomes[i].y3 * 3};
            g.drawPolygon(xArr, yArr, 4);
            g.fillPolygon(xArr, yArr, 4);
        }
        try {
            File output = new File("GeneticColours.jpg");
            ImageIO.write(image1, "jpg", output);
        } catch (Exception e) {
        }
        g.dispose();
        return image1;
    }

    //makes Smallin3 picture from color1_genome
    private BufferedImage ImageFromColorGenes(Gene[] chromosomes, Color FirstColor) {
        BufferedImage image1 = new BufferedImage((int) (width / 3), (int) (width / 3), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width / 3; i++) {
            for (int j = 0; j < height / 3; j++) {
                image1.setRGB(i, j, FirstColor.getRGB());
            }
        }
        Graphics g = image1.createGraphics();
        for (int i = 0; i < chromosomes.length; i++) {
            g.setColor(chromosomes[i].color);
            int[] xArr = {chromosomes[i].x1, chromosomes[i].x2, chromosomes[i].x4, chromosomes[i].x3};
            int[] yArr = {chromosomes[i].y1, chromosomes[i].y2, chromosomes[i].y4, chromosomes[i].y3};
            g.drawPolygon(xArr, yArr, 4);
            g.fillPolygon(xArr, yArr, 4);
        }
        g.dispose();
        return image1;
    }

    //makes random gene combination of size=size
    private Gene[] BigColoursImage(ArrayList<Color> BaseColors, int size) {
        Gene[] chromosomes = new Gene[size];
        Random rand = new Random();
        for (int i = 0; i < chromosomes.length; i++) {
            int x1, x2, x3, x4, y1, y2, y3, y4, alpha, randomIndex;
            if (i == 0) {
                x1 = 0;
                y1 = 0;
                x2 = width / 6;
                y2 = 0;
                x3 = 0;
                y3 = height / 6;
                x4 = width / 6;
                y4 = height / 6;
                randomIndex = 1 + rand.nextInt(BaseColors.size() - 1);
                alpha = 200 + rand.nextInt(55);
            } else if (i == 1) {
                x1 = width / 6;
                y1 = 0;
                x2 = width / 3 - 1;
                y2 = 0;
                x3 = width / 6;
                y3 = height / 6;
                x4 = width / 3 - 1;
                y4 = height / 6;
                randomIndex = 1 + rand.nextInt(BaseColors.size() - 1);
                alpha = 200 + rand.nextInt(55);
            } else if (i == 2) {
                x1 = 0;
                y1 = height / 6;
                x2 = width / 6;
                y2 = height / 6;
                x3 = 0;
                y3 = height / 3 - 1;
                x4 = width / 6;
                y4 = height / 3 - 1;
                randomIndex = 1 + rand.nextInt(BaseColors.size() - 1);
                alpha = 200 + rand.nextInt(55);
            } else if (i == 3) {
                x1 = width / 6;
                y1 = height / 6;
                x2 = width / 3 - 1;
                y2 = height / 6;
                x3 = width / 6;
                y3 = height / 3 - 1;
                x4 = width / 3;
                y4 = height / 3 - 1;
                randomIndex = 1 + rand.nextInt(BaseColors.size() - 1);
                alpha = 200 + rand.nextInt(55);
            } else if (i < chromosomes.length / 4) {
                if (i < chromosomes.length / 8) {
                    randomIndex = rand.nextInt(BaseColors.size());
                    x1 = 0;
                    y1 = 0;
                    x2 = x1 + rand.nextInt(width / 3 - x1);
                    y2 = rand.nextInt(height / 3 - height / 30);
                    x3 = rand.nextInt(width / 3 - width / 30);
                    y3 = y1 + rand.nextInt(height / 3 - y1);
                    x4 = x3 + rand.nextInt(width / 3 - x3);
                    y4 = y2 + rand.nextInt(height / 3 - y2);
                    alpha = 155 + rand.nextInt(100);
                } else {
                    randomIndex = rand.nextInt(BaseColors.size());
                    x1 = 0;
                    y1 = 0;
                    x2 = x1 + rand.nextInt(width / 6 - x1);
                    y2 = rand.nextInt(height / 6);
                    x3 = rand.nextInt(width / 6);
                    y3 = y1 + rand.nextInt(height / 6 - y1);
                    x4 = x3 + rand.nextInt(width / 6 - x3);
                    y4 = y2 + rand.nextInt(height / 6 - y2);
                    alpha = 155 + rand.nextInt(100);
                }
            } else if (i < chromosomes.length / 2) {
                if (i < chromosomes.length / 2 - chromosomes.length / 8) {
                    randomIndex = rand.nextInt(BaseColors.size());
                    x1 = rand.nextInt(width / 3 - width / 30);
                    y1 = rand.nextInt(height / 3 - height / 30);
                    x2 = width / 3 - 1;
                    y2 = 0;
                    x3 = rand.nextInt(width / 3 - width / 30);
                    y3 = y1 + rand.nextInt(height / 3 - y1);
                    x4 = x3 + rand.nextInt(width / 3 - x3);
                    y4 = y2 + rand.nextInt(height / 3 - y2);
                    alpha = 155 + rand.nextInt(50);
                } else {
                    randomIndex = rand.nextInt(BaseColors.size());
                    x1 = width / 6 + rand.nextInt(width / 6 - 1);
                    y1 = rand.nextInt(height / 6);
                    x2 = width / 3 - 1;
                    y2 = 0;
                    x3 = width / 6 + rand.nextInt(width / 6 - 1);
                    y3 = y1 + rand.nextInt(height / 6 - y1);
                    x4 = x3 + rand.nextInt(width / 3 - x3);
                    y4 = y2 + rand.nextInt(height / 6 - y2);
                    alpha = 155 + rand.nextInt(50);
                }
            } else if (i < chromosomes.length / 2 + chromosomes.length / 4) {
                if (i < chromosomes.length / 2 + chromosomes.length / 8) {
                    randomIndex = rand.nextInt(BaseColors.size());
                    x1 = rand.nextInt(width / 3 - width / 30);
                    y1 = rand.nextInt(height / 3 - height / 30);
                    x2 = x1 + rand.nextInt(width / 3 - x1);
                    y2 = rand.nextInt(height / 3 - height / 30);
                    x3 = 0;
                    y3 = height / 3 - 1;
                    x4 = x3 + rand.nextInt(width / 3 - x3);
                    y4 = y2 + rand.nextInt(height / 3 - y2);
                    alpha = 155 + rand.nextInt(50);
                } else {
                    randomIndex = rand.nextInt(BaseColors.size());
                    x1 = rand.nextInt(width / 6);
                    y1 = height / 6 + rand.nextInt(height / 6 - 1);
                    x2 = x1 + rand.nextInt(width / 6 - x1);
                    y2 = height / 6 + rand.nextInt(height / 6 - 1);
                    x3 = 0;
                    y3 = height / 3 - 1;
                    x4 = x3 + rand.nextInt(width / 6 - x3);
                    y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                    alpha = 155 + rand.nextInt(50);
                }
            } else {
                if (i < chromosomes.length - chromosomes.length / 8) {
                    randomIndex = rand.nextInt(BaseColors.size());
                    x1 = rand.nextInt(width / 3 - width / 30);
                    y1 = rand.nextInt(height / 3 - height / 30);
                    x2 = x1 + rand.nextInt(width / 3 - x1);
                    y2 = rand.nextInt(height / 3 - height / 30);
                    x3 = rand.nextInt(width / 3 - width / 30);
                    y3 = y1 + rand.nextInt(height / 3 - y1);
                    x4 = width / 3 - 1;
                    y4 = height / 3 - 1;
                    alpha = 155 + rand.nextInt(50);
                } else {
                    randomIndex = rand.nextInt(BaseColors.size());
                    x1 = width / 6 + rand.nextInt(width / 6 - 1);
                    y1 = height / 6 + rand.nextInt(height / 6 - 1);
                    x2 = x1 + rand.nextInt(width / 3 - x1);
                    y2 = height / 6 + rand.nextInt(height / 6 - 1);
                    x3 = width / 6 + rand.nextInt(width / 6 - 1);
                    y3 = y1 + rand.nextInt(height / 3 - y1 - 1);
                    x4 = width / 3 - 1;
                    y4 = height / 3 - 1;
                    alpha = 155 + rand.nextInt(50);
                }
            }
            Color buff = BaseColors.get(randomIndex);
            Color color = new Color(buff.getRed(), buff.getGreen(), buff.getBlue(), alpha);
            chromosomes[i] = new Gene(color, x1, y1, x2, y2, x3, y3, x4, y4);
        }
        return chromosomes;
    }

    private int Sorter(float[] array) {
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > array[index]) {
                index = i;
            }
        }
        return index;
    }

    private Compliance[] Crossover(Compliance parent1, Compliance parent2, BufferedImage orig, ArrayList<Color> BaseColors) {
        Compliance[] children = new Compliance[2];
        children[0] = new Compliance(new Gene[parent1.chromosome.length], 0);
        children[1] = new Compliance(new Gene[parent1.chromosome.length], 0);
        for (int i = 0; i < parent1.chromosome.length; i++) {
            Random rand = new Random();
            int random = rand.nextInt(3);
            if (random % 2 == 0) {
                children[0].chromosome[i] = new Gene(parent1.chromosome[i]);
                children[1].chromosome[i] = new Gene(parent2.chromosome[i]);
            } else {
                children[0].chromosome[i] = new Gene(parent2.chromosome[i]);
                children[1].chromosome[i] = new Gene(parent1.chromosome[i]);
            }
        }
        children[0].fitness = Fitness(orig, children[0].chromosome, BaseColors);
        children[1].fitness = Fitness(orig, children[1].chromosome, BaseColors);
        return children;
    }

    private Compliance Mutation(Compliance orig, ArrayList<Color> BaseColors, BufferedImage original) {
        Random rand = new Random();
        int index = rand.nextInt(orig.chromosome.length - 1 + 100);
        int randomIndex = 1 + rand.nextInt(BaseColors.size() - 1);
        int alpha = 150 + rand.nextInt(105);
        Color buff = BaseColors.get(randomIndex);
        if (index < 25) index = 0;
        else if (index < 50) index = 1;
        else if (index < 75) index = 2;
        else if (index < 100) index = 3;
        else index = index - 100;

        int x1 = orig.chromosome[index].x1;
        int y1 = orig.chromosome[index].y1;
        int x2 = orig.chromosome[index].x2;
        int y2 = orig.chromosome[index].y2;
        int x3 = orig.chromosome[index].x3;
        int y3 = orig.chromosome[index].y3;
        int x4 = orig.chromosome[index].x4;
        int y4 = orig.chromosome[index].y4;
        if (index > 3) {
            if (index < orig.chromosome.length / 4) {
                int coordinate = rand.nextInt(8);
                switch (coordinate) {
                    case 0:
                        x2 = rand.nextInt(width / 3 - 1);
                        break;
                    case 1:
                        y2 = rand.nextInt(height / 3 - y4);
                        break;
                    case 2:
                        x3 = rand.nextInt(width / 3 - x4);
                        break;
                    case 3:
                        y3 = rand.nextInt(height / 3 - 1);
                        break;
                    case 4:
                        x4 = x3 + rand.nextInt(width / 3 - x3);
                        break;
                    case 5:
                        y4 = y2 + rand.nextInt(height / 3 - y2);
                        break;
                    default:
                        break;
                }
            } else if (index < orig.chromosome.length / 2) {
                int coordinate = rand.nextInt(8);
                switch (coordinate) {
                    case 0:
                        x1 = rand.nextInt(width / 3 - 1);
                        break;
                    case 1:
                        y1 = rand.nextInt(height / 3 - 1);
                        break;
                    case 2:
                        x3 = rand.nextInt(width / 3 - x4);
                        break;
                    case 3:
                        y3 = y1 + rand.nextInt(height / 3 - y1);
                        break;
                    case 4:
                        x4 = x3 + rand.nextInt(width / 3 - x3);
                        break;
                    case 5:
                        y4 = rand.nextInt(height / 3);
                        break;
                    default:
                        break;
                }

            } else if (index < orig.chromosome.length / 2 + orig.chromosome.length / 4) {
                int coordinate = rand.nextInt(8);
                switch (coordinate) {
                    case 0:
                        x2 = rand.nextInt(width / 3 - 1);
                        break;
                    case 1:
                        y2 = rand.nextInt(height / 3 - y4);
                        break;
                    case 2:
                        x1 = rand.nextInt(width / 3 - x2);
                        break;
                    case 3:
                        y1 = rand.nextInt(height / 3 - 1);
                        break;
                    case 4:
                        x4 = rand.nextInt(width / 3);
                        break;
                    case 5:
                        y4 = y2 + rand.nextInt(height / 3 - y2);
                        break;
                    default:
                        break;
                }
            } else {
                int coordinate = rand.nextInt(8);
                switch (coordinate) {
                    case 0:
                        x2 = x1 + rand.nextInt(width / 3 - x1);
                        break;
                    case 1:
                        y2 = rand.nextInt(height / 3 - 1);
                        break;
                    case 2:
                        x3 = rand.nextInt(width / 3 - 1);
                        break;
                    case 3:
                        y3 = y1 + rand.nextInt(height / 3 - y1);
                        break;
                    case 4:
                        x1 = rand.nextInt(width / 3 - x2);
                        break;
                    case 5:
                        y1 = rand.nextInt(height / 3 - y3);
                        break;
                    default:
                        break;
                }

            }
        }
        Color color = new Color(buff.getRed(), buff.getGreen(), buff.getBlue(), alpha);
        Gene[] genome = new Gene[orig.chromosome.length];
        for (int i = 0; i < orig.chromosome.length; i++) {
            if (i == index) {
                genome[i] = new Gene(color, x1, y1, x2, y2, x3, y3, x4, y4);
            } else genome[i] = new Gene(orig.chromosome[i]);
        }
        Compliance result = new Compliance(genome, Fitness(original, orig.chromosome, BaseColors));
        return result;
    }

    private Compliance[] Sorter(Compliance[] selection) {
        Compliance[] Unsorted = new Compliance[selection.length];
        for (int i = 0; i < selection.length; i++) {
            Unsorted[i] = new Compliance(selection[i].chromosome, selection[i].fitness);
        }
        for (int i = 0; i < selection.length; i++) {
            for (int j = i + 1; j < selection.length; j++) {
                if (Unsorted[j].fitness > Unsorted[i].fitness) {
                    Compliance buff = new Compliance(Unsorted[i].chromosome, Unsorted[i].fitness);
                    Unsorted[i] = new Compliance(Unsorted[j].chromosome, Unsorted[j].fitness);
                    Unsorted[j] = new Compliance(buff.chromosome, buff.fitness);
                }
            }
        }
        return Unsorted;
    }

    private Compliance[] Shuffle(Compliance[] selection) {
        Random rand = new Random();
        Compliance[] shuffle = new Compliance[selection.length];
        for (int i = 0; i < selection.length; i++) {
            shuffle[i] = new Compliance(selection[i].chromosome, selection[i].fitness);
        }
        for (int i = 0; i < selection.length; i++) {
            int index = i + rand.nextInt(selection.length - i + 15);
            if (index < selection.length) {
                Compliance buff = new Compliance(shuffle[i].chromosome, shuffle[i].fitness);
                shuffle[i] = new Compliance(shuffle[index].chromosome, shuffle[index].fitness);
                shuffle[index] = new Compliance(buff.chromosome, buff.fitness);
            }
        }
        return shuffle;
    }

    private Compliance[] Selection(Compliance[] population, BufferedImage orig, ArrayList<Color> BaseColors, Executor exec) {
        CompletionService<Compliance[]> completionService = new ExecutorCompletionService<Compliance[]>(exec);
        population = Shuffle(population);
        for (int i = 0; i < population.length; i += 4) {
            Compliance[] selection = {population[i], population[i + 1], population[i + 2], population[i + 3]};
            completionService.submit(new Callable<Compliance[]>() {
                public Compliance[] call() {
                    Compliance[] Sorted = Sorter(selection);
                    Compliance[] Children = Crossover(Sorted[0], Sorted[1], orig, BaseColors);
                    Compliance Mutant1 = Mutation(Sorted[2], BaseColors, orig);
                    Compliance Mutant2 = Mutation(Sorted[3], BaseColors, orig);
                    Compliance[] new_selection = {Sorted[0], Sorted[1], Sorted[2], Sorted[3], Children[0], Children[1], Mutant1, Mutant2};
                    Compliance[] Sorted2 = Sorter(new_selection);
                    Compliance[] result = {Sorted2[0], Sorted2[1], Sorted2[2], Sorted2[3]};
                    return result;
                }
            });
        }
        Compliance[] new_population = new Compliance[population.length];
        for (int i = 0; i < population.length; i += 4) {
            try {
                Future<Compliance[]> resultFuture = completionService.take(); //blocks if none available
                Compliance[] result = resultFuture.get();
                new_population[i] = result[0];
                new_population[i + 1] = result[1];
                new_population[i + 2] = result[2];
                new_population[i + 3] = result[3];
            } catch (Exception e) {
                System.out.println("BIGGEST Problems.");
            }
        }
        new_population = Sorter(new_population);
        return new_population;
    }

    private Compliance[] Flood(Compliance[] population, ArrayList<Color> BaseColors, int size, BufferedImage origin) {
        Compliance[] new_species = new Compliance[population.length];
        for (int i = 0; i < population.length; i++) {
            Gene[] chromosome = BigColoursImage(BaseColors, size);
            new_species[i] = new Compliance(chromosome, Fitness(origin, chromosome, BaseColors));
        }
        for (int i = 0; i < population.length / 4; i++) {
            new_species[i] = population[i];
        }
        return new_species;
    }

    private Gene[] GeneticColours(BufferedImage origin, ArrayList<Color> BaseColors) {
        Executor executor = Executors.newFixedThreadPool(4);
        int size = 75;
        Compliance[] population = new Compliance[64];
        for (int i = 0; i < 64; i++) {
            Gene[] chromosome = BigColoursImage(BaseColors, size);
            population[i] = new Compliance(chromosome, Fitness(origin, chromosome, BaseColors));
        }
        Compliance[] new_population = population;
        new_population = Sorter(new_population);
        Compliance Best = new_population[0];
        int i = 0;
        float fit_prev = Best.fitness;
        int sameFit = 0;
        while (Best.fitness < 50) {
            new_population = Selection(new_population, origin, BaseColors, executor);
            for (int j = 0; j < 56; j++) {
                if (new_population[j].fitness > Best.fitness) {
                    Best = new_population[j];
                    System.out.println(j + " best population number");
                }
            }
            i++;
            if (Best.fitness == fit_prev) {
                sameFit++;
                if (sameFit == 150) {
                    new_population = Flood(new_population, BaseColors, size, origin);
                    System.out.println("Flood happened.");
                }
                if (sameFit > 300) {
                    break;
                }
            } else {
                fit_prev = Best.fitness;
                sameFit = 0;
            }
            System.out.println("Iteration num: " + i + " Fit: " + Best.fitness);
        }


        System.out.println(Best.fitness);
        return Best.chromosome;
    }

    public static void main(String[] args) throws Exception {
        Test obj = new Test();
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Test());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1100);
        frame.setLocation(500, 100);
        frame.setVisible(true);
    }
}
