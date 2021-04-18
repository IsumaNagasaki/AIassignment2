package com.company;

import org.w3c.dom.ls.LSOutput;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Math.*;

public class Test extends JPanel {
    BufferedImage image;
    BufferedImage colourScale;
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
            System.out.println("Orig reading problems.");
        }
    }


    public void paint(Graphics g) {
        BufferedImage img2 = GrayImage();
        BufferedImage img3 = ColouredImage();
        BufferedImage img4 = Recombination(img2, img3);

        ArrayList<ColorWithTimes> ColoredList = BaseColors(img3, 0);
        ArrayList<ColorWithTimes> AllColors = BaseColors(img3, 1);
        //Image he = BaseColorsImage(ColoredList);
        BufferedImage Small = SmallIn3(img3);

        ArrayList<Color> Base = new ArrayList<>();
        for (int i = 0; i < ColoredList.size(); i++) {
            if (i < 4)
                Base.add(ColoredList.get(i).color);
        }

        Gene[] bestOf = GeneticColours(Small, Base);
        BufferedImage coloredImageBig = BigImageFromColorGenes(bestOf, Base.get(0));


        try {
            File input = new File("GeneticColours.jpg");
            coloredImageBig = ImageIO.read(input);
        } catch (Exception e) {
            System.out.println("Troubles with reading picture.");
        }
        try {
            File input = new File("ColourScale.jpg");
            colourScale = ImageIO.read(input);
        } catch (Exception e) {
            System.out.println("Troubles with reading picture.");
        }
        try {
            File output = new File("ColoursEnhancement.jpg");
            ImageIO.write(coloredImageBig, "jpg", output);
        } catch (Exception e) {
            System.out.println("Troubles with picture saving.");
        }
        try {
            File input = new File("ColoursEnhancement.jpg");
            coloredImageSmall = ImageIO.read(input);
        } catch (Exception e) {
            System.out.println("Troubles with reading picture.");
        }
        for (int i = 0; i < AllColors.size(); i++) {
            if (AllColors.get(i).frequency < 5) {
                AllColors.remove(i);
                i--;
            }
            if (AllColors.get(i).frequency > 1200) {
                AllColors.get(i).frequency = 1000 + AllColors.get(i).frequency / 15;
            } else AllColors.get(i).frequency = AllColors.get(i).frequency + 250;
        }

        Gene[] colorEnhance = ColoursEnhancement(colourScale, ColoredList, coloredImageBig);
        BufferedImage enhanceImage = ImageFromColorEnhancement(colorEnhance, coloredImageSmall);

        System.out.println("Starting...");
        ArrayList<GrayGene> GrayGenome = GeneticShadows(img2, 80);
        BufferedImage GrayOne = GrayImage(GrayGenome);
        BufferedImage result = Recombination(GrayOne, enhanceImage);

        try {
            File output = new File("RESULT.jpg");
            ImageIO.write(result, "jpg", output);
        } catch (Exception e) {
            System.out.println("Troubles with picture saving.");
        }

        //g.drawImage(Small, 140, 20, this);
        //g.drawImage(he, 140 + (width / 3), 20, this);
        g.drawImage(image, 140, 20, this);

        g.drawImage(enhanceImage, 652, 20, this);
        //g.drawImage(GrayImageBig, 652, 20, this);

        //g.drawImage(img4, 652, 20, this);
        //g.drawImage(img2, 140, 532, this);
        //g.drawImage(enhanceImage, 140, 532, this);
        g.drawImage(GrayOne, 140, 532, this);
        g.drawImage(result, 652, 532, this);

        //g.drawImage(img, 1164, 20, this);
    }

    //gray Image from original
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
            System.out.println("Troubles with picture saving.");
        }
        return image1;
    }

    //coloured image from original
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
                    int red = c.getRed();
                    int green = c.getGreen();
                    int blue = c.getBlue();
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
            System.out.println("Troubles with picture saving.");
        }
        return image1;
    }

    //recombination of gray and color images to gain the original or the result
    private BufferedImage Recombination(BufferedImage GrayImage, BufferedImage ColouredImage) {
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
                }
            }
            File output = new File("recombination.jpg");
            ImageIO.write(image1, "jpg", output);
        } catch (Exception e) {
            System.out.println("Troubles with picture saving.");
        }
        return image1;
    }

    //math function
    private float MaxOf3(float a, float b, float c) {
        if ((a > b) & (a > c)) return a;
        else return Math.max(b, c);
    }

    //returns compressed image
    private BufferedImage SmallIn3(BufferedImage ColoredImage) {
        BufferedImage image1 = new BufferedImage((width / 3), (width / 3), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height / 3; i++) {
            for (int j = 0; j < width / 3; j++) {
                Color color = new Color(ColoredImage.getRGB(3 * j, 3 * i));
                image1.setRGB(j, i, color.getRGB());
            }
        }
        return image1;
    }

    //image with base colours, for check
    private Image BaseColorsImage(ArrayList<ColorWithTimes> BestColours) {
        BufferedImage image1 = new BufferedImage((width / 3), (width / 3), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width / 18; i++) {
            for (int j = 0; j < height / 3; j++) {
                Color c1 = BestColours.get(0).color;
                image1.setRGB(j, i, c1.getRGB());
            }
            if (BestColours.size() >= 2) {
                for (int j = 0; j < height / 3; j++) {
                    Color c2 = BestColours.get(1).color;
                    image1.setRGB(j, i + 28, c2.getRGB());
                }
            }
            if (BestColours.size() >= 3) {
                for (int j = 0; j < height / 3; j++) {
                    Color c1 = BestColours.get(2).color;
                    image1.setRGB(j, i + 56, c1.getRGB());
                }
            }
            if (BestColours.size() >= 4) {
                for (int j = 0; j < height / 3; j++) {
                    Color c1 = BestColours.get(3).color;
                    image1.setRGB(j, i + 84, c1.getRGB());
                }
            }
            if (BestColours.size() >= 5) {
                for (int j = 0; j < height / 3; j++) {
                    Color c1 = BestColours.get(4).color;
                    image1.setRGB(j, i + 112, c1.getRGB());
                }
            }
            if (BestColours.size() >= 6) {
                for (int j = 0; j < height / 3; j++) {
                    Color c1 = BestColours.get(5).color;
                    image1.setRGB(j, i + 140, c1.getRGB());
                }
            }
        }
        return image1;
    }

    //fitness for the 1 algorithm (GeneticColours)
    private float Fitness(BufferedImage orig, Gene[] chromosomes) {
        BufferedImage iteration = ImageFromColorGenes(chromosomes);
        int width1 = iteration.getWidth();
        int height1 = iteration.getHeight();
        float fit = 0;
        for (int x = 0; x < width1; x += 2) {
            for (int y = 0; y < height1; y += 2) {
                Color origin = new Color(orig.getRGB(x, y));
                Color iter = new Color(iteration.getRGB(x, y));
                if ((abs(origin.getRed() - iter.getRed()) < 40) & (abs(origin.getGreen() - iter.getGreen()) < 40) & (abs(origin.getBlue() - iter.getBlue()) < 40)) {
                    fit++;
                }
                if ((origin.getRed() == origin.getGreen()) & (origin.getRed() == origin.getBlue())) {
                    fit++;
                }
            }
        }
        fit = (100 * (fit / ((float) (width1 * height1) / 4)));
        return fit;
    }

    //fitness for the 2 algorithm (ColourEnhancement)
    private float Fitness(BufferedImage orig, BufferedImage background, Gene[] genome) {
        BufferedImage iteration = ImageFromColorEnhancement(genome, background);
        int squareSize = genome[0].x4 - genome[0].x1;
        float fitness = 0;
        for (int i = 0; i < genome.length; i++) {
            float fit = 0;
            for (int x1 = genome[i].x1; x1 < genome[i].x4; x1++) {
                for (int y1 = genome[i].y1; y1 < genome[i].y4; y1++) {
                    Color origin = new Color(orig.getRGB(x1, y1));
                    Color back = new Color(iteration.getRGB(x1, y1));
                    float red_or_per = (float) origin.getRed() / 255;
                    float green_or_per = (float) origin.getGreen() / 255;
                    float blue_or_per = (float) origin.getBlue() / 255;
                    float red_back_per = (float) back.getRed() / 255;
                    float green_back_per = (float) back.getGreen() / 255;
                    float blue_back_per = (float) back.getBlue() / 255;
                    float orig_max = Math.max(blue_or_per, Math.max(red_or_per, green_or_per));
                    float back_max = Math.max(blue_back_per, Math.max(red_back_per, green_back_per));
                    if ((red_or_per == orig_max) & (red_back_per == back_max)) {
                        if (abs(origin.getRed() - back.getRed()) < 15) {
                            fit++;
                        }
                    } else if ((green_or_per == orig_max) & (green_back_per == back_max)) {
                        if (abs(origin.getGreen() - back.getGreen()) < 15) {
                            fit++;
                        }
                    } else if ((blue_or_per == orig_max) & (blue_back_per == back_max)) {
                        if (abs(origin.getBlue() - back.getBlue()) < 15) {
                            fit++;
                        }
                    }
                }
            }
            fit = (100 * (fit / (squareSize * squareSize)));
            if (fit > 50) {
                fitness++;
            }
        }
        return fitness;
    }

    //obtains the set of new squares to match for the 2 algorithm: minimization
    private ArrayList<int[]> NewSquares(BufferedImage orig, BufferedImage background, Gene[] genome) {
        BufferedImage iteration = ImageFromColorEnhancement(genome, background);
        int squareSize = genome[0].x4 - genome[0].x1;
        float fitness = 0;
        ArrayList<int[]> squares = new ArrayList<>();
        for (int i = 0; i < genome.length; i++) {
            float fit = 0;
            for (int x1 = genome[i].x1; x1 < genome[i].x4; x1++) {
                for (int y1 = genome[i].y1; y1 < genome[i].y4; y1++) {
                    Color origin = new Color(orig.getRGB(x1, y1));
                    Color back = new Color(iteration.getRGB(x1, y1));
                    float red_or_per = (float) origin.getRed() / 255;
                    float green_or_per = (float) origin.getGreen() / 255;
                    float blue_or_per = (float) origin.getBlue() / 255;
                    float red_back_per = (float) back.getRed() / 255;
                    float green_back_per = (float) back.getGreen() / 255;
                    float blue_back_per = (float) back.getBlue() / 255;
                    float orig_max = Math.max(blue_or_per, Math.max(red_or_per, green_or_per));
                    float back_max = Math.max(blue_back_per, Math.max(red_back_per, green_back_per));
                    if ((red_or_per == orig_max) & (red_back_per == back_max)) {
                        if (abs(origin.getRed() - back.getRed()) < 15) {
                            fit++;
                        }
                    } else if ((green_or_per == orig_max) & (green_back_per == back_max)) {
                        if (abs(origin.getGreen() - back.getGreen()) < 15) {
                            fit++;
                        }
                    } else if ((blue_or_per == orig_max) & (blue_back_per == back_max)) {
                        if (abs(origin.getBlue() - back.getBlue()) < 15) {
                            fit++;
                        }
                    }
                }
            }
            fit = (100 * (fit / (squareSize * squareSize)));
            if (fit > 50) {
                fitness++;
            } else {
                int[] square = {genome[i].x1, genome[i].y1};
                squares.add(square);
            }
        }
        System.out.println("This fit: " + fitness);
        return squares;
    }

    //returns the list of base colours of the image
    private ArrayList<ColorWithTimes> BaseColors(BufferedImage ColoredImage, int flag) {

        ArrayList<ColorWithTimes> ColoredList = new ArrayList<>();
        ArrayList<ColorWithTimes> BestColours = new ArrayList<>();
        for (int i = 0; i < height; i += 3) {
            for (int j = 0; j < width; j += 3) {
                Color color = new Color(ColoredImage.getRGB(j, i));
                float r_percent = 100 * color.getRed() / (float) 255;
                float g_percent = 100 * color.getGreen() / (float) 255;
                float b_percent = 100 * color.getBlue() / (float) 255;
                if ((r_percent < 15) || (g_percent < 15) || (b_percent < 15) || (r_percent > 35) || (g_percent > 35) || (b_percent > 35))
                    ColoredList.add(new ColorWithTimes(color, 1));
            }
        }
        for (int i = 0; i < ColoredList.size(); i++) {
            float r_percent = 100 * (float) ColoredList.get(i).color.getRed() / 255;
            float g_percent = 100 * (float) ColoredList.get(i).color.getGreen() / 255;
            float b_percent = 100 * (float) ColoredList.get(i).color.getBlue() / 255;
            for (int j = i + 1; j < ColoredList.size() - i; j++) {
                float r_percent_comp = 100 * (float) ColoredList.get(j).color.getRed() / 255;
                float g_percent_comp = 100 * (float) ColoredList.get(j).color.getGreen() / 255;
                float b_percent_comp = 100 * (float) ColoredList.get(j).color.getBlue() / 255;
                if ((abs(r_percent - r_percent_comp) < 12) && (abs(g_percent - g_percent_comp) < 12) && (abs(b_percent - b_percent_comp) < 12)) {
                    ColoredList.get(i).frequency = ColoredList.get(i).frequency + 1;
                    float max_percent = MaxOf3(r_percent_comp, g_percent_comp, b_percent_comp);
                    if ((((max_percent == r_percent_comp) & (r_percent_comp > r_percent)) || ((max_percent == g_percent_comp) & (g_percent_comp > g_percent))
                            || ((max_percent == b_percent_comp) & (b_percent_comp > b_percent))) & (max_percent > 45)) {
                        Color color = ColoredList.get(j).color;
                        int frequency = ColoredList.get(i).frequency;
                        ColoredList.set(i, new ColorWithTimes(color, frequency));
                    }
                    ColoredList.remove(j);
                    j--;
                }
            }
        }
        if (flag == 0) {
            BestColours.add(ColoredList.get(0));
            if (ColoredList.size() > 5) {
                for (int i = 1; i < 6; i++) {
                    BestColours.add(ColoredList.get(i));
                }
            }
            for (int k = 0; k < ColoredList.size(); k++) {

                for (int t = 0; t < BestColours.size(); t++) {
                    if (ColoredList.get(k).frequency > BestColours.get(t).frequency) {
                        BestColours.add(t, ColoredList.get(k));
                        break;
                    }
                }
                if (BestColours.size() == 11) {
                    BestColours.remove(10);
                }
            }
        }
        if (flag == 0) {
            return BestColours;
        } else {
            return ColoredList;
        }
    }

    //makes big picture from color1_genome for 1 algorithm
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
            System.out.println("Troubles with picture saving.");
        }
        try {
            File output = new File("ColoursEnhancement.jpg");
            ImageIO.write(image1, "jpg", output);
        } catch (Exception e) {
            System.out.println("Troubles with picture saving.");
        }

        g.dispose();
        return image1;
    }

    //makes Smallin3 picture from color1_genome
    private BufferedImage ImageFromColorGenes(Gene[] chromosomes) {
        BufferedImage image1 = new BufferedImage((int) (width / 3), (int) (width / 3), BufferedImage.TYPE_INT_RGB);
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
            int x1, x2, x3, x4, y1, y2, y3, y4, alpha, randomIndex = rand.nextInt(BaseColors.size());
            if (i == 0) {
                x1 = 0;
                y1 = 0;
                x2 = width / 6;
                y2 = 0;
                x3 = 0;
                y3 = height / 6;
                x4 = width / 6;
                y4 = height / 6;
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
                alpha = 200 + rand.nextInt(55);
            } else if (i < (chromosomes.length - 4) / 8 + 4) {
                //1st square big
                x1 = 0;
                y1 = 0;
                x2 = rand.nextInt(width / 3);
                y2 = rand.nextInt(height / 3 - 1);
                x3 = rand.nextInt(width / 3 - 1);
                y3 = y1 + rand.nextInt(height / 3 - y1 - 1);
                x4 = x3 + rand.nextInt(width / 3 - x3 - 1);
                y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                alpha = 155 + rand.nextInt(100);
            } else if (i < (chromosomes.length - 4) / 4 + 4) {
                //2nd square big
                x1 = rand.nextInt(width / 3 - 1);
                y1 = rand.nextInt(height / 3 - 1);
                x2 = width / 3 - 1;
                y2 = 0;
                x3 = rand.nextInt(width / 3 - 1);
                y3 = y1 + rand.nextInt(height / 3 - y1 - 1);
                x4 = x3 + rand.nextInt(width / 3 - x3 - 1);
                y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                alpha = 155 + rand.nextInt(50);
            } else if (i < (chromosomes.length - 4) / 4 + (chromosomes.length - 4) / 8 + 4) {
                //3d square big
                x1 = rand.nextInt(width / 3 - 1);
                y1 = rand.nextInt(height / 3 - 1);
                x2 = x1 + rand.nextInt(width / 3 - x1 - 1);
                y2 = rand.nextInt(height / 3 - 1);
                x3 = 0;
                y3 = height / 3 - 1;
                x4 = x3 + rand.nextInt(width / 3 - x3 - 1);
                y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                alpha = 155 + rand.nextInt(50);
            } else if (i < (chromosomes.length - 4) / 2 + 4) {
                //4th square big
                x1 = rand.nextInt(width / 3 - 1);
                y1 = rand.nextInt(height / 3 - 1);
                x2 = x1 + rand.nextInt(width / 3 - x1 - 1);
                y2 = rand.nextInt(height / 3 - 1);
                x3 = rand.nextInt(width / 3 - 1);
                y3 = y1 + rand.nextInt(height / 3 - y1 - 1);
                x4 = width / 3 - 1;
                y4 = height / 3 - 1;
                alpha = 155 + rand.nextInt(50);
            } else if (i < (chromosomes.length - 4) / 2 + (chromosomes.length - 4) / 8 + 4) {
                //1st square small
                x1 = 0;
                y1 = 0;
                x2 = x1 + rand.nextInt(width / 6 - x1);
                y2 = rand.nextInt(height / 6);
                x3 = rand.nextInt(width / 6);
                y3 = y1 + rand.nextInt(height / 6 - y1);
                x4 = x3 + rand.nextInt(width / 6 - x3);
                y4 = y2 + rand.nextInt(height / 6 - y2);
                alpha = 155 + rand.nextInt(100);
            } else if (i < (chromosomes.length - 4) / 2 + (chromosomes.length - 4) / 4 + 4) {
                //2nd square small
                x1 = width / 6 + rand.nextInt(width / 6 - 1);
                y1 = rand.nextInt(height / 6);
                x2 = width / 3 - 1;
                y2 = 0;
                x3 = width / 6 + rand.nextInt(width / 6 - 1);
                y3 = y1 + rand.nextInt(height / 6 - y1);
                x4 = x3 + rand.nextInt(width / 3 - x3);
                y4 = y2 + rand.nextInt(height / 6 - y2);
                alpha = 155 + rand.nextInt(50);
            } else if (i < (chromosomes.length - 4) / 2 + (chromosomes.length - 4) / 4 + (chromosomes.length - 4) / 8 + 4) {
                //3d square small
                x1 = rand.nextInt(width / 6);
                y1 = height / 6 + rand.nextInt(height / 6 - 1);
                x2 = x1 + rand.nextInt(width / 6 - x1);
                y2 = height / 6 + rand.nextInt(height / 6 - 1);
                x3 = 0;
                y3 = height / 3 - 1;
                x4 = x3 + rand.nextInt(width / 6 - x3);
                y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                alpha = 155 + rand.nextInt(50);
            } else {
                //4th square small
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
            Color buff = BaseColors.get(randomIndex);
            Color color = new Color(buff.getRed(), buff.getGreen(), buff.getBlue(), alpha);
            chromosomes[i] = new Gene(color, x1, y1, x2, y2, x3, y3, x4, y4);
        }
        return chromosomes;
    }

    //1 algorithm crossover
    private Compliance[] Crossover(Compliance parent1, Compliance parent2, BufferedImage orig) {
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
        children[0].fitness = Fitness(orig, children[0].chromosome);
        children[1].fitness = Fitness(orig, children[1].chromosome);
        return children;
    }
    //2 algorithm crossover
    private Compliance[] Crossover(Compliance parent1, Compliance parent2, BufferedImage orig, BufferedImage Background) {
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
        children[0].fitness = Fitness(orig, Background, children[0].chromosome);
        children[1].fitness = Fitness(orig, Background, children[1].chromosome);
        return children;
    }
    //1 algorithm mutation
    private Compliance Mutation(Compliance orig, ArrayList<Color> BaseColors, BufferedImage original) {
        Random rand = new Random();
        int quarter = rand.nextInt(12);
        int randomIndex = rand.nextInt(BaseColors.size());
        int alpha = 150 + rand.nextInt(105);
        Color buff = BaseColors.get(randomIndex);
        int front = rand.nextInt(2);
        if ((front == 0) & (quarter < 4)) {
            quarter = quarter + 4;
        }
        int index = 4 + rand.nextInt((orig.chromosome.length - 4) / 8) + quarter * (orig.chromosome.length - 4) / 8;
        if (quarter > 7) {
            index = quarter - 8;
        }

        int x1 = orig.chromosome[index].x1;
        int y1 = orig.chromosome[index].y1;
        int x2 = orig.chromosome[index].x2;
        int y2 = orig.chromosome[index].y2;
        int x3 = orig.chromosome[index].x3;
        int y3 = orig.chromosome[index].y3;
        int x4 = orig.chromosome[index].x4;
        int y4 = orig.chromosome[index].y4;
        switch (quarter) {
            case 0:
                x2 = rand.nextInt(width / 3);
                y2 = rand.nextInt(height / 3 - 1);
                x3 = rand.nextInt(width / 3 - 1);
                y3 = y1 + rand.nextInt(height / 3 - y1 - 1);
                x4 = x3 + rand.nextInt(width / 3 - x3 - 1);
                y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                break;
            case 1:
                x1 = rand.nextInt(width / 3 - 1);
                y1 = rand.nextInt(height / 3 - 1);
                x3 = rand.nextInt(width / 3 - 1);
                y3 = y1 + rand.nextInt(height / 3 - y1 - 1);
                x4 = x3 + rand.nextInt(width / 3 - x3 - 1);
                y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                break;
            case 2:
                x1 = rand.nextInt(width / 3 - 1);
                y1 = rand.nextInt(height / 3 - 1);
                x2 = x1 + rand.nextInt(width / 3 - x1 - 1);
                y2 = rand.nextInt(height / 3 - 1);
                x4 = x3 + rand.nextInt(width / 3 - x3 - 1);
                y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                break;
            case 3:
                x1 = rand.nextInt(width / 3 - 1);
                y1 = rand.nextInt(height / 3 - 1);
                x2 = x1 + rand.nextInt(width / 3 - x1 - 1);
                y2 = rand.nextInt(height / 3 - 1);
                x3 = rand.nextInt(width / 3 - 1);
                y3 = y1 + rand.nextInt(height / 3 - y1 - 1);
                break;
            case 4:
                x2 = x1 + rand.nextInt(width / 6 - x1);
                y2 = rand.nextInt(height / 6);
                x3 = rand.nextInt(width / 6);
                y3 = y1 + rand.nextInt(height / 6 - y1);
                x4 = x3 + rand.nextInt(width / 6 - x3);
                y4 = y2 + rand.nextInt(height / 6 - y2);
                break;
            case 5:
                x1 = width / 6 + rand.nextInt(width / 6 - 1);
                y1 = rand.nextInt(height / 6);
                x3 = width / 6 + rand.nextInt(width / 6 - 1);
                y3 = y1 + rand.nextInt(height / 6 - y1);
                x4 = x3 + rand.nextInt(width / 3 - x3);
                y4 = y2 + rand.nextInt(height / 6 - y2);
                break;
            case 6:
                x1 = rand.nextInt(width / 6);
                y1 = height / 6 + rand.nextInt(height / 6 - 1);
                x2 = x1 + rand.nextInt(width / 6 - x1);
                y2 = height / 6 + rand.nextInt(height / 6 - 1);
                x4 = x3 + rand.nextInt(width / 6 - x3);
                y4 = y2 + rand.nextInt(height / 3 - y2 - 1);
                break;
            case 7:
                x1 = width / 6 + rand.nextInt(width / 6 - 1);
                y1 = height / 6 + rand.nextInt(height / 6 - 1);
                x2 = x1 + rand.nextInt(width / 3 - x1);
                y2 = height / 6 + rand.nextInt(height / 6 - 1);
                x3 = width / 6 + rand.nextInt(width / 6 - 1);
                y3 = y1 + rand.nextInt(height / 3 - y1 - 1);
                break;
            default:
                break;
        }
        Color color = new Color(buff.getRed(), buff.getGreen(), buff.getBlue(), alpha);
        Gene[] genome = new Gene[orig.chromosome.length];
        for (int i = 0; i < orig.chromosome.length; i++) {
            if (i == index) {
                genome[i] = new Gene(color, x1, y1, x2, y2, x3, y3, x4, y4);
            } else genome[i] = new Gene(orig.chromosome[i]);
        }
        Compliance result = new Compliance(genome, Fitness(original, orig.chromosome));
        return result;
    }
    //2 algorithm mutation
    private Compliance Mutation(Compliance orig, ArrayList<ColorWithTimes> AllColors, BufferedImage original, BufferedImage background) {
        Random rand = new Random();
        int times = 1 + rand.nextInt(orig.chromosome.length - 1);
        Gene[] genome = new Gene[orig.chromosome.length];
        int frequencySum = 0;
        for (int i = 0; i < AllColors.size(); i++) {
            frequencySum = frequencySum + AllColors.get(i).frequency;
        }
        for (int k = 0; k < times; k++) {
            int alpha = 100 + rand.nextInt(155);
            int index = rand.nextInt(orig.chromosome.length);
            int randomIndex = rand.nextInt(frequencySum);
            Color buff = AllColors.get(0).color;
            frequencySum = 0;
            for (int j = 0; j < AllColors.size(); j++) {
                if (randomIndex >= frequencySum) {
                    frequencySum = frequencySum + AllColors.get(j).frequency;
                    if (randomIndex < frequencySum) {
                        buff = AllColors.get(j).color;
                        break;
                    }
                }
            }
            Color color = new Color(buff.getRed(), buff.getGreen(), buff.getBlue(), alpha);
            for (int i = 0; i < orig.chromosome.length; i++) {
                if (i == index) {
                    genome[i] = new Gene(color, orig.chromosome[i].x1, orig.chromosome[i].y1, orig.chromosome[i].x2, orig.chromosome[i].y2, orig.chromosome[i].x3, orig.chromosome[i].y3, orig.chromosome[i].x4, orig.chromosome[i].y4);
                } else genome[i] = new Gene(orig.chromosome[i]);
            }
        }
        Compliance result = new Compliance(genome, Fitness(original, background, orig.chromosome));
        return result;
    }
    //Sorter for selection, sorts after selection is made
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
    //shuffle for selection, shuffles before selection is done
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
    //1st algorithm selection
    private Compliance[] Selection(Compliance[] population, BufferedImage orig, ArrayList<Color> BaseColors, Executor exec) {
        CompletionService<Compliance[]> completionService = new ExecutorCompletionService<Compliance[]>(exec);
        population = Shuffle(population);
        for (int i = 0; i < population.length; i += 4) {
            Compliance[] selection = {population[i], population[i + 1], population[i + 2], population[i + 3]};
            completionService.submit(new Callable<Compliance[]>() {
                public Compliance[] call() {
                    Compliance[] Sorted = Sorter(selection);
                    Compliance[] Children = Crossover(Sorted[0], Sorted[1], orig);
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
        /*for (int i = 0; i < population.length; i += 4){
            Compliance[] selection = {population[i], population[i + 1], population[i + 2], population[i + 3]};
            Compliance[] Sorted = Sorter(selection);
            Compliance[] Children = Crossover(Sorted[0], Sorted[1], orig, BaseColors);
            Compliance Mutant1 = Mutation(Sorted[2], BaseColors, orig);
            Compliance Mutant2 = Mutation(Sorted[3], BaseColors, orig);
            Compliance[] new_selection = {Sorted[0], Sorted[1], Sorted[2], Sorted[3], Children[0], Children[1], Mutant1, Mutant2};
            Compliance[] Sorted2 = Sorter(new_selection);
            Compliance[] result = {Sorted2[0], Sorted2[1], Sorted2[2], Sorted2[3]};
            new_population[i] = result[0];
            new_population[i + 1] = result[1];
            new_population[i + 2] = result[2];
            new_population[i + 3] = result[3];
        }*/
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
    //2nd algorithm selection
    private Compliance[] Selection(Compliance[] population, BufferedImage orig, ArrayList<ColorWithTimes> AllColors, Executor exec, BufferedImage Background) {
        CompletionService<Compliance[]> completionService = new ExecutorCompletionService<Compliance[]>(exec);
        population = Shuffle(population);
        for (int i = 0; i < population.length; i += 4) {
            Compliance[] selection = {population[i], population[i + 1], population[i + 2], population[i + 3]};
            completionService.submit(new Callable<Compliance[]>() {
                public Compliance[] call() {
                    Compliance[] Sorted = Sorter(selection);
                    Compliance[] Children = Crossover(Sorted[0], Sorted[1], orig, Background);
                    Compliance Mutant1 = Mutation(Sorted[2], AllColors, orig, Background);
                    Compliance Mutant2 = Mutation(Sorted[3], AllColors, orig, Background);
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
    //flood for  1st algorithm
    private Compliance[] Flood(Compliance[] population, ArrayList<Color> BaseColors, BufferedImage origin) {
        Compliance[] new_species = new Compliance[population.length];
        /*for (int i = 0; i < population.length; i++) {
            Gene[] chromosome = BigColoursImage(BaseColors, size);
            new_species[i] = new Compliance(chromosome, Fitness(origin, chromosome, BaseColors));
        }*/
        for (int i = 0; i < population.length / 4; i++) {
            new_species[i] = population[i];
        }
        for (int i = 0; i < population.length / 4; i += 2) {
            Compliance[] Children = Crossover(new_species[i], new_species[i + 1], origin);
            new_species[(population.length / 4) + i] = Children[0];
            new_species[(population.length / 4) + i + 1] = Children[1];
            Children = Crossover(new_species[i], new_species[population.length / 4 - i - 1], origin);
            new_species[(population.length / 2) + i] = Children[0];
            new_species[(population.length / 2) + i + 1] = Children[1];
            new_species[(population.length / 2) + (population.length / 4) + i] = Mutation(new_species[i], BaseColors, origin);
            new_species[(population.length / 2) + (population.length / 4) + i + 1] = Mutation(new_species[i + 1], BaseColors, origin);
        }
        return new_species;
    }
    //decreasing search space for 2nd algorithm
    private Compliance[] MinimizingSquares(Gene[] Best, ArrayList<ColorWithTimes> AllColors, BufferedImage origin, BufferedImage Background, int population_size, int squares, int k) {

        int squareSize = width / squares;
        float fit = Fitness(origin, Background, Best);
        ArrayList<int[]> areas = NewSquares(origin, Background, Best);
        int flag = 0;
        Gene[] whatToDraw = new Gene[Best.length - areas.size()];
        System.out.println(whatToDraw.length);
        int index = 0;
        for (int i = 0; i < Best.length; i++) {
            for (int j = 0; j < areas.size(); j++) {
                if ((Best[i].x1 == areas.get(j)[0]) & (Best[i].y1 == areas.get(j)[1])) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                whatToDraw[index] = Best[i];
                index++;
            }
            flag = 0;
        }

        BufferedImage enhanceImage = ImageFromColorEnhancement(whatToDraw, coloredImageSmall);
        coloredImageSmall = enhanceImage;
        try {
            File output = new File("ColoursEnhancement.jpg");
            ImageIO.write(enhanceImage, "jpg", output);
        } catch (Exception e) {
            System.out.println("Troubles with picture saving.");
        }
        String name = ("Enhancement".concat(Integer.toString(k))).concat(".jpg");
        try {
            File output = new File(name);
            ImageIO.write(enhanceImage, "jpg", output);
        } catch (Exception e) {
            System.out.println("Troubles with picture saving.");
        }

        Compliance[] population = new Compliance[population_size];
        for (int i = 0; i < population_size; i++) {
            Gene[] genome = AllColorsImage(AllColors, squareSize, areas);
            population[i] = new Compliance(genome, Fitness(origin, enhanceImage, genome));
        }
        return population;
    }
    //1st algorithm
    private Gene[] GeneticColours(BufferedImage origin, ArrayList<Color> BaseColors) {
        Executor executor = Executors.newFixedThreadPool(8);
        int size = 84;
        Compliance[] population = new Compliance[56];
        for (int i = 0; i < 56; i++) {
            Gene[] chromosome = BigColoursImage(BaseColors, size);
            population[i] = new Compliance(chromosome, Fitness(origin, chromosome));
        }
        Compliance[] new_population = population;
        new_population = Sorter(new_population);
        Compliance Best = new_population[0];
        int i = 0;
        float fit_prev = Best.fitness;
        int sameFit = 0;
        while (Best.fitness < 95) {
            new_population = Selection(new_population, origin, BaseColors, executor);
            if (new_population[0].fitness > Best.fitness) {
                Best = new_population[0];
                System.out.println(i + " best population number");
            }
            i++;
            if ((i % 50 == 0)&(sameFit<50)) {
                BufferedImage colored = BigImageFromColorGenes(Best.chromosome, BaseColors.get(0));
                String name = ("Color".concat(Integer.toString(i))).concat(".jpg");
                try {
                    File output = new File(name);
                    ImageIO.write(colored, "jpg", output);
                } catch (Exception e) {
                    System.out.println("Troubles with picture saving.");
                }

            }
            if (Best.fitness == fit_prev) {
                sameFit++;
                if (sameFit % 60 == 0) {
                    new_population = Flood(new_population, BaseColors, origin);
                    System.out.println("Flood happened.");
                }
                if (sameFit > 190) {
                    break;
                }
            } else {
                fit_prev = Best.fitness;
                sameFit = 0;

            }
            if (i % 10 == 0)
                System.out.println("Iteration num: " + i + " Fit: " + Best.fitness);
        }


        System.out.println(Best.fitness);
        return Best.chromosome;
    }

    //3rd algorithm start
    //random genes
    private ArrayList<GrayGene> RandomGenes(int squares) {
        Random rand = new Random();
        ArrayList<GrayGene> genome = new ArrayList<>();
        int squareSize = width / squares;
        for (int i = 0; i < width; i += squareSize) {
            for (int j = 0; j < height; j += squareSize) {
                int col = rand.nextInt(256);
                char c = (char) (rand.nextInt(26) + 'a');
                genome.add(new GrayGene(i, j, c, new Color(col, col, col)));
            }
        }
        return genome;
    }
    //makes the image out of genome
    private BufferedImage GrayImage(ArrayList<GrayGene> genome) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result.setRGB(i, j, (new Color(85, 85, 85)).getRGB());
            }
        }
        int squareSize = genome.get(1).y1 - genome.get(0).y1;
        Graphics g = result.getGraphics();
        for (int i = 0; i < genome.size(); i++) {
            g.setColor(genome.get(i).color);
            String str = Character.toString(genome.get(i).symb);
            g.drawString(str, genome.get(i).x1 + squareSize / 2, genome.get(i).y1 + squareSize / 2);
        }
        g.dispose();
        return result;
    }
    //fitness for 3rd alg
    private int GrayFitness(BufferedImage origin, ArrayList<GrayGene> genome, int squares) {
        int squareSize = width / squares;
        int fitness = 0;
        int averageTone = 0;
        int number = 0;
        for (int i = 0; i < genome.size(); i++) {
            for (int x = genome.get(i).x1; x < genome.get(i).x1 + squareSize; x++) {
                for (int y = genome.get(i).y1; y < genome.get(i).y1 + squareSize; y++)
                    if ((x < width) & (y < height)) {
                        number++;
                        averageTone = averageTone + new Color(origin.getRGB(x, y)).getBlue();
                    }
            }
            averageTone = (int) ((float) averageTone / number);
            number = 0;
            if (abs(genome.get(i).color.getRed() - averageTone) < 10) {
                fitness++;
                genome.get(i).matched = true;
            }
            averageTone = 0;
        }
        return fitness;
    }
    //crossover for 3rd alg
    private GraySpecie[] GrayCrossover(GraySpecie parent1, GraySpecie parent2, BufferedImage origin, int squares) {
        GraySpecie[] Children = new GraySpecie[2];
        ArrayList<GrayGene> Child1 = new ArrayList<>();
        ArrayList<GrayGene> Child2 = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < parent1.genome.size(); i++) {
            int Choice = rand.nextInt(2);
            if (Choice == 0) {
                Child1.add(new GrayGene(parent1.genome.get(i)));
                Child2.add(new GrayGene(parent2.genome.get(i)));
            } else {
                Child1.add(new GrayGene(parent2.genome.get(i)));
                Child2.add(new GrayGene(parent1.genome.get(i)));
            }
        }
        Children[0] = new GraySpecie(Child1, GrayFitness(origin, Child1, squares));
        Children[1] = new GraySpecie(Child2, GrayFitness(origin, Child2, squares));

        return Children;
    }
    //mutation for 3rd alg
    private GraySpecie GrayMutation(GraySpecie prototype, BufferedImage origin, int squares) {
        ArrayList<GrayGene> newGenome = new ArrayList<>();
        for (int j = 0; j < prototype.genome.size(); j++) {
            newGenome.add(new GrayGene(prototype.genome.get(j)));
        }
        Random rand = new Random();
        int number = 1 + rand.nextInt(3);
        int index = rand.nextInt(prototype.genome.size());
        for (int i = 0; i < number; i++) {
            while (prototype.genome.get(index).matched) {
                index = rand.nextInt(prototype.genome.size());
            }
            int col = rand.nextInt(256);
            char c = (char) (rand.nextInt(26) + 'a');
            newGenome.set(index, new GrayGene(prototype.genome.get(index).x1, prototype.genome.get(index).y1, c, new Color(col, col, col)));
        }
        GraySpecie Mutant = new GraySpecie(newGenome, GrayFitness(origin, newGenome, squares));
        return Mutant;
    }
    //shuffle for graySelection
    private GraySpecie[] GrayShuffle(GraySpecie[] selection) {
        Random rand = new Random();
        GraySpecie[] shuffle = new GraySpecie[selection.length];
        for (int i = 0; i < selection.length; i++) {
            shuffle[i] = new GraySpecie(selection[i].genome, selection[i].fitness);
        }
        for (int i = 0; i < selection.length; i++) {
            int index = i + rand.nextInt(selection.length - i + 15);
            if (index < selection.length) {
                GraySpecie buff = new GraySpecie(shuffle[i].genome, shuffle[i].fitness);
                shuffle[i] = new GraySpecie(shuffle[index].genome, shuffle[index].fitness);
                shuffle[index] = new GraySpecie(buff.genome, buff.fitness);
            }
        }
        return shuffle;
    }
    //sorter for graySelection
    private GraySpecie[] GraySorter(GraySpecie[] selection) {
        GraySpecie[] Sorted = new GraySpecie[selection.length];
        for (int i = 0; i < selection.length; i++) {
            Sorted[i] = new GraySpecie(selection[i].genome, selection[i].fitness);
        }
        for (int i = 0; i < selection.length; i++) {
            for (int j = i + 1; j < selection.length; j++) {
                if (Sorted[j].fitness > Sorted[i].fitness) {
                    GraySpecie buff = new GraySpecie(Sorted[i].genome, Sorted[i].fitness);
                    Sorted[i] = new GraySpecie(Sorted[j].genome, Sorted[j].fitness);
                    Sorted[j] = new GraySpecie(buff.genome, buff.fitness);
                }
            }
        }
        return Sorted;
    }

    //Selection fr the 3rd alg
    private GraySpecie[] GraySelection(GraySpecie[] population, BufferedImage orig, int squares, Executor exec) {
        CompletionService<GraySpecie[]> completionService = new ExecutorCompletionService<GraySpecie[]>(exec);
        population = GrayShuffle(population);
        for (int i = 0; i < population.length; i += 4) {
            GraySpecie[] selection = {population[i], population[i + 1], population[i + 2], population[i + 3]};
            completionService.submit(new Callable<GraySpecie[]>() {
                public GraySpecie[] call() {
                    GraySpecie[] Sorted = GraySorter(selection);
                    GraySpecie[] Children = GrayCrossover(Sorted[0], Sorted[1], orig, squares);
                    GraySpecie Mutant1 = GrayMutation(Sorted[2], orig, squares);
                    GraySpecie Mutant2 = GrayMutation(Sorted[3], orig, squares);
                    GraySpecie[] new_selection = {Sorted[0], Sorted[1], Sorted[2], Sorted[3], Children[0], Children[1], Mutant1, Mutant2};
                    GraySpecie[] Sorted2 = GraySorter(new_selection);
                    GraySpecie[] result = {Sorted2[0], Sorted2[1], Sorted2[2], Sorted2[3]};
                    return result;
                }
            });
        }

        GraySpecie[] new_population = new GraySpecie[population.length];
        /*for (int i=0; i<population.length; i+=4){
            GraySpecie[] selection = {population[i], population[i + 1], population[i + 2], population[i + 3]};
            GraySpecie[] Sorted = GraySorter(selection);
            GraySpecie[] Children = GrayCrossover(Sorted[0], Sorted[1], orig, squares);
            GraySpecie Mutant1 = GrayMutation(Sorted[2], orig, squares);
            GraySpecie Mutant2 = GrayMutation(Sorted[3], orig, squares);
            GraySpecie[] new_selection = {Sorted[0], Sorted[1], Sorted[2], Sorted[3], Children[0], Children[1], Mutant1, Mutant2};
            GraySpecie[] Sorted2 = GraySorter(new_selection);
            GraySpecie[] result = {Sorted2[0], Sorted2[1], Sorted2[2], Sorted2[3]};
            new_population[i] = result[0];
            new_population[i + 1] = result[1];
            new_population[i + 2] = result[2];
            new_population[i + 3] = result[3];
        }*/
        for (int i = 0; i < population.length; i += 4) {
            try {
                Future<GraySpecie[]> resultFuture = completionService.take(); //blocks if none available
                GraySpecie[] result = resultFuture.get();
                new_population[i] = result[0];
                new_population[i + 1] = result[1];
                new_population[i + 2] = result[2];
                new_population[i + 3] = result[3];
            } catch (Exception e) {
                System.out.println("BIG Problems.");
            }
        }
        new_population = GraySorter(new_population);
        return new_population;
    }
    //third algorithm
    private ArrayList<GrayGene> GeneticShadows(BufferedImage origin, int squares) {
        Executor executor = Executors.newFixedThreadPool(8);
        GraySpecie[] population = new GraySpecie[56];
        for (int i = 0; i < 56; i++) {
            ArrayList<GrayGene> genome = RandomGenes(squares);
            population[i] = new GraySpecie(genome, GrayFitness(origin, genome, squares));
        }
        GraySpecie[] new_population = population;
        new_population = GraySorter(new_population);
        GraySpecie Best = new_population[0];
        int i = 0;
        int fit_prev = Best.fitness;
        int sameFit = 0;
        while (Best.fitness < Best.genome.size()) {
            new_population = GraySelection(new_population, origin, squares, executor);
            if (new_population[0].fitness > Best.fitness) {
                Best = new_population[0];
                System.out.println(i + " best population number");
            }
            i++;
            if (Best.fitness == fit_prev) {
                sameFit++;
                if (sameFit % 20 == 0) {

                    System.out.println("Need to happen.");
                }
                if (sameFit > 100) {
                    break;
                }
            } else {
                fit_prev = Best.fitness;
                sameFit = 0;

            }
            if (i % 500 == 0) {
                BufferedImage gray = GrayImage(Best.genome);
                String name = ("Gray".concat(Integer.toString(i))).concat(".jpg");
                try {
                    File output = new File(name);
                    ImageIO.write(gray, "jpg", output);
                } catch (Exception e) {
                    System.out.println("Troubles with picture saving.");
                }
            }
            if (i % 10 == 0)
                System.out.println("Iteration num: " + i + " Fit: " + Best.fitness + " from " + Best.genome.size());

        }


        System.out.println(Best.fitness);
        BufferedImage gray = GrayImage(Best.genome);
        try {
            File output = new File("GeneticShadows.jpg");
            ImageIO.write(gray, "jpg", output);
        } catch (Exception e) {
            System.out.println("Troubles with picture saving.");
        }
        return Best.genome;
    }

    //black squares image for 2nd alg check
    private BufferedImage Squares(BufferedImage Background, ArrayList<int[]> squares, int squaresInLine) {
        for (int i = 0; i < squares.size(); i++) {
            //System.out.println("x: "+squares.get(i)[0]+" y: "+squares.get(i)[1]);
        }
        int squareSize = width / squaresInLine;
        BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image1.setRGB(i, j, Background.getRGB(i, j));
            }
        }
        Color black = new Color(0, 0, 0);
        for (int i = 0; i < squares.size(); i++) {
            //System.out.println("x: " + squares.get(i)[0] + " y:" + squares.get(i)[1]);
            for (int x = squares.get(i)[0]; x < squares.get(i)[0] + squareSize; x++) {
                for (int y = squares.get(i)[1]; y < squares.get(i)[1] + squareSize; y++) {
                    image1.setRGB(x, y, black.getRGB());
                }
            }
        }
        return image1;
    }
    //determining what squares need to be improved, 2nd alg
    private ArrayList<int[]> AreasToEnhance(BufferedImage origin, BufferedImage Background, int squaresInLine) {
        ArrayList<int[]> result = new ArrayList<>();
        int squareSize = width / squaresInLine;
        for (int x = 0; x < width; x += squareSize) {
            for (int y = 0; y < height; y += squareSize) {
                float fit = 0;
                for (int x1 = x; x1 < x + squareSize; x1++) {
                    for (int y1 = y; y1 < y + squareSize; y1++) {
                        Color orig = new Color(origin.getRGB(x1, y1));
                        Color back = new Color(Background.getRGB(x1, y1));
                        float red_or_per = (float) orig.getRed() / 255;
                        float green_or_per = (float) orig.getGreen() / 255;
                        float blue_or_per = (float) orig.getBlue() / 255;
                        float red_back_per = (float) back.getRed() / 255;
                        float green_back_per = (float) back.getGreen() / 255;
                        float blue_back_per = (float) back.getBlue() / 255;
                        float orig_max = Math.max(blue_or_per, Math.max(red_or_per, green_or_per));
                        float back_max = Math.max(blue_back_per, Math.max(red_back_per, green_back_per));
                        if ((red_or_per == orig_max) & (red_back_per == back_max)) {
                            if (abs(orig.getRed() - back.getRed()) < 15) {
                                fit++;
                            }
                        } else if ((green_or_per == orig_max) & (green_back_per == back_max)) {
                            if (abs(orig.getGreen() - back.getGreen()) < 15) {
                                fit++;
                            }
                        } else if ((blue_or_per == orig_max) & (blue_back_per == back_max)) {
                            if (abs(orig.getBlue() - back.getBlue()) < 15) {
                                fit++;
                            }
                        }
                    }
                }
                fit = (100 * (fit / (squareSize * squareSize)));
                if (fit < 50) {
                    int[] square = {x, y};
                    result.add(square);
                }
            }
        }
        return result;
    }
    //image from the second algorithm
    private BufferedImage ImageFromColorEnhancement(Gene[] genome, BufferedImage background) {
        BufferedImage image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image1.setRGB(i, j, background.getRGB(i, j));
            }
        }
        Graphics g = image1.createGraphics();
        for (int i = 0; i < genome.length; i++) {
            g.setColor(genome[i].color);
            int[] xArr = {genome[i].x1, genome[i].x2, genome[i].x4, genome[i].x3};
            int[] yArr = {genome[i].y1, genome[i].y2, genome[i].y4, genome[i].y3};
            g.drawPolygon(xArr, yArr, 4);
            g.fillPolygon(xArr, yArr, 4);
        }
        g.dispose();
        return image1;
    }
    //random genes for 2nd algorithm
    private Gene[] AllColorsImage(ArrayList<ColorWithTimes> AllColors, int squareSize, ArrayList<int[]> squares) {
        Gene[] genome = new Gene[squares.size()];
        Random rand = new Random();
        int frequencySum = 0;
        for (int i = 0; i < AllColors.size(); i++) {
            frequencySum = frequencySum + AllColors.get(i).frequency;
        }
        for (int i = 0; i < squares.size(); i++) {
            int randomIndex = rand.nextInt(frequencySum);
            int alpha = 100 + rand.nextInt(155);
            Color buff = AllColors.get(0).color;
            int sum = 0;
            for (int j = 0; j < AllColors.size(); j++) {
                sum = sum + AllColors.get(j).frequency;
                if (sum > randomIndex) {
                    buff = AllColors.get(j).color;
                    break;
                }
            }
            Color color = new Color(buff.getRed(), buff.getGreen(), buff.getBlue(), alpha);
            genome[i] = new Gene(color, squares.get(i)[0], squares.get(i)[1], squares.get(i)[0] + squareSize, squares.get(i)[1], squares.get(i)[0], squares.get(i)[1] + squareSize, squares.get(i)[0] + squareSize, squares.get(i)[1] + squareSize);
        }
        return genome;
    }
    //2nd algorithm
    private Gene[] ColoursEnhancement(BufferedImage origin, ArrayList<ColorWithTimes> AllColors, BufferedImage Background) {
        int squaresInLine = 64;
        int squareSize = width / squaresInLine;
        Compliance[] population = new Compliance[56];
        ArrayList<int[]> squares = AreasToEnhance(origin, Background, squaresInLine);
        for (int i = 0; i < 56; i++) {
            Gene[] genome = AllColorsImage(AllColors, squareSize, squares);
            population[i] = new Compliance(genome, Fitness(origin, Background, genome));
        }

        Executor executor = Executors.newFixedThreadPool(8);
        Compliance[] new_population = population;
        new_population = Sorter(new_population);
        Compliance Best = new_population[0];
        int i = 0;
        float fit_prev = Best.fitness;
        int sameFit = 0;
        while (Best.fitness < new_population[0].chromosome.length) {
            new_population = Selection(new_population, origin, AllColors, executor, Background);
            if (new_population[0].fitness > Best.fitness) {
                Best = new_population[0];
                System.out.println(i + " new best specie");
                float fit = Fitness(origin, Background, new_population[0].chromosome);
                System.out.println("Max fit: " + fit);
            }
            i++;
            if (i % 25 == 0) {
                new_population = MinimizingSquares(new_population[0].chromosome, AllColors, origin, Background, 56, squaresInLine, i);
                System.out.println(new_population[0].chromosome.length);
                Best = new_population[0];
                fit_prev = Best.fitness;
                Background = coloredImageSmall;

                System.out.println("Minimization happened.");

            }
            if (Best.fitness == fit_prev) {
                sameFit++;

                if (sameFit % 25 == 0) {
                    break;
                }
            } else {
                fit_prev = Best.fitness;
                sameFit = 0;
            }
            if (i % 10 == 0)
                System.out.println("Iteration num: " + i + " Fit: " + Best.fitness + " of " + new_population[0].chromosome.length);
        }
        ArrayList<int[]> areas = NewSquares(origin, Background, Best.chromosome);
        int flag = 0;
        Gene[] whatToDraw = new Gene[Best.chromosome.length - areas.size()];
        int index = 0;
        for (int f = 0; f < Best.chromosome.length; f++) {
            for (int j = 0; j < areas.size(); j++) {
                if ((Best.chromosome[f].x1 == areas.get(j)[0]) & (Best.chromosome[f].y1 == areas.get(j)[1])) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                whatToDraw[index] = Best.chromosome[f];
                index++;
            }
            flag = 0;
        }
        System.out.println(Best.fitness);
        BufferedImage colourBetter = ImageFromColorEnhancement(whatToDraw, Background);
        try {
            File output = new File("Enhanced.jpg");
            ImageIO.write(colourBetter, "jpg", output);
        } catch (Exception e) {
            System.out.println("Troubles with picture saving.");
        }
        return whatToDraw;
    }

    public static void main(String[] args) throws Exception {
        Test obj = new Test();
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Test());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1100);
        frame.setLocation(300, 0);
        frame.setVisible(true);
    }
}
