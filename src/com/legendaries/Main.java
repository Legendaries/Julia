package com.legendaries;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame{

    public static final int WIDTH = 1024, HEIGHT = 720;
    
    public Main(){
        add(new JuliaPanel(WIDTH, HEIGHT));
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new Main();
    }
    
    private class JuliaPanel extends JPanel{
        
        private BufferedImage img;
        
        public JuliaPanel(int width, int height){
            img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent me){
                    zoom -= 1/zoom;
                    generateImage();
                }
            });
            generateImage();
            javax.swing.Timer timer = new javax.swing.Timer(1, new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent ae){
//                    zoom -= 20*Math.pow(0.5f, 10-zoom);
                    generateImage();
                }
            });
            timer.start();
        }
        
        public float zoom = 8f;
        
        public void generateImage(){
            int[] colors = new int[1000];
            int black = (int)(System.nanoTime()/100000f);
            System.out.println(black);
            for (int i = 0; i < 1000; i++) {
                colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 4f)) + black;
            }
            for(int i = 0; i < Main.WIDTH; i++)
                for(int j = 0; j < Main.HEIGHT; j++){
                    int iterations = 100;
                    ComplexNumber julia = new ComplexNumber(zoom*((float)i/Main.WIDTH - 0.5f), zoom*((float)Main.HEIGHT/Main.WIDTH)*((float)j/Main.HEIGHT - 0.5f));
                    while(--iterations > 1 && Math.abs(julia.real) < 1000)
                        julia = juliaFunc(julia, new ComplexNumber(0, 0.75f));
//                        julia = juliaFunc(julia, new ComplexNumber(4*((float)i/Main.WIDTH - 0.5f), 4*((float)j/Main.HEIGHT - 0.5f)));
                    if(iterations == 1)
                        img.setRGB(i, j, Color.black.getRGB());
                    else{
                        float f = (100f-iterations)/100f;
                        Color c = new Color((int)(f * (float)colors[iterations]));
                        img.setRGB(i, j, c.getRGB());
                    }
                }
            repaint();
        }
        
        public ComplexNumber juliaFunc(ComplexNumber init, ComplexNumber c){
            return init.add(c).squared();
        }
        
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(img, 0, 0, this);
        }
        
        private class ComplexNumber {
            
            public float real, i;
            
            public ComplexNumber(float real, float i){
                this.real = real;
                this.i = i;
            }
            
            public ComplexNumber squared(){
                return new ComplexNumber(real * real - i * i, 2 * (real * i));
            }
            
            public ComplexNumber add(ComplexNumber c){
                return new ComplexNumber(real + c.real, i + c.i);
            }
            
        }
        
    }
    
}
