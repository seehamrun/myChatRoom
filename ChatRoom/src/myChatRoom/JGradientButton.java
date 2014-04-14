package myChatRoom;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JButton;

public class JGradientButton extends JButton{
    public JGradientButton(String msg){
        super(msg);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setFont(new Font("Tahoma",Font.BOLD,11));
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setPaint(new GradientPaint(
                						new Point(0, 0), 
						                Color.GREEN.brighter(), 
						                new Point(0, getHeight()), 
						                Color.BLUE.brighter()));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();

        super.paintComponent(g);
    }

//    public static final JGradientButton newInstance(){
//        return new JGradientButton();
//    }
}
