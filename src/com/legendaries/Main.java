package com.legendaries;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
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
        private boolean mousePressed;
        private int mouseX = 0, mouseY = 0;
        
        public JuliaPanel(int width, int height){
            img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent me){
//                    mousePressed = true;
                    mouseX = me.getX();
                    mouseY = me.getY();
                    if(me.getButton() == MouseEvent.BUTTON3){
                        zoom /= 5;
                        generateImage();
                    }
                }
                @Override
                public void mouseReleased(MouseEvent me){
                    mousePressed = false;
                }
            });
            addMouseMotionListener(new MouseAdapter(){
                @Override
                public void mouseDragged(MouseEvent me){
//                    mousePressed = true;
                    startX = startX - (me.getX() - mouseX)*zoom/100f;
                    startY = startY - (me.getY() - mouseY)*zoom/100f;
                    mouseX = me.getX();
                    mouseY = me.getY();
                    repaint();
                }
            });
            generateImage();
            javax.swing.Timer timer = new javax.swing.Timer(10, new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent ae){
//                    zoom -= 0.1f;
                    generateImage();
                }
            });
            timer.start();
        }
        
        public float zoom = 5f;
        private boolean f = false;
        int[] colors = new int[1000];
        
        private float startX = 0, startY = 0;
        public int i;
        public void generateImage(){
            if(!f){
                f = true;
                for (int i = 0; i < 1000; i++)
                    colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 4f));// + black;
            }else 
                for (int i = 0; i < 100; i++)
                    colors[i] = colors[i == colors.length-1 ? 0 : i+1];
            
//            int black = (int)(System.nanoTime()/100000L);
            for(int i = 0; i < Main.WIDTH; i++)
                for(int j = 0; j < Main.HEIGHT; j++){
                    int iterations = 100;
                    ComplexNumber zero = new ComplexNumber(0, 0);
                    ComplexNumber julia = new ComplexNumber(zoom*((float)i/Main.WIDTH - 0.5f), zoom*((float)Main.HEIGHT/Main.WIDTH)*((float)j/Main.HEIGHT - 0.5f));
                    ComplexNumber viewPortLoc = new ComplexNumber(startX + zoom*((float)i/Main.WIDTH-0.5f), startY + zoom*((float)Main.HEIGHT/Main.WIDTH)*((float)j/Main.HEIGHT - 0.5f));
                    while(--iterations > 1 && Math.abs(zero.real) < 1000)
                        zero = juliaFunc(zero, viewPortLoc);
//                        julia = juliaFunc(julia, new ComplexNumber(4*((float)i/Main.WIDTH - 0.5f), 4*((float)j/Main.HEIGHT - 0.5f)));
                    if(iterations == 1)
                        img.setRGB(i, j, Color.black.getRGB());
                    else{
                        float f = (100f-iterations)/100f;
                        Color c = new Color((int)(f * (float)colors[iterations]));
                        img.setRGB(i, j, c.getRGB());
                    }
                    if(mousePressed)
                        break;
                }
//            try{
//                ImageIO.write(img, "png", new File(i++ + ".png"));
//            }catch(Exception e){
//                
//            }
            repaint();
        }
        
        public ComplexNumber juliaFunc(ComplexNumber init, ComplexNumber c){
            return init.squared().add(c);
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
