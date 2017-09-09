package model;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import model.math.Expression;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Danilo on 21/09/2016.
 */
public class MathElement implements Comparable<MathElement>{
    private static int counter = 0;
    private int mId;
    private int mXStart;
    private int mYStart;
    private int mWidth;
    private int mHeight;
    private int mXEnd;
    private int mYEnd;
    private int mXCenter;
    private int mYCenter;
    private Expression mExpression;
    private TeXFormula mTeXFormula;
    private TeXIcon mIcon;  // to draw element
    private Color mColor;
    private final ImageView mImageView;

    public MathElement(Expression expression) {
        mId = counter++;
        mXStart = 0;
        mYStart = 0;
        mExpression = expression;
        mTeXFormula = new TeXFormula(mExpression.getLatexExpression());
        mIcon = mTeXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
        mWidth = mIcon.getIconWidth();
        mHeight = mIcon.getIconHeight();
        mXEnd = mXStart + mWidth - 1;
        mYEnd = mYStart + mHeight - 1;
        mXCenter = (mXStart + mXEnd)/2;
        mYCenter = (mYStart + mYEnd)/2;
        mColor = Color.black;
        mImageView = new ImageView();
    }

    public MathElement(MathElement mathElement, Expression expression) {
        mId = counter++;
        mXStart = mathElement.getXStart();
        mYStart = mathElement.getYStart();
        mExpression = expression;           // Careful here, is a reference instead of new expression
        mXEnd = mathElement.getXEnd();
        mYEnd = mathElement.getYEnd();
        mXCenter = mathElement.getXCenter();
        mYCenter = mathElement.getYCenter();
        mColor = Color.black;
        updateIcon(mYCenter);
        mImageView = new ImageView();
    }

    public int getId() {
        return mId;
    }

    public int getXStart() {
        return mXStart;
    }

    public void setXStart(int XStart) {
        mXStart = XStart;
        mXEnd = mXStart + mWidth -1;
        mXCenter = (mXStart + mXEnd)/2;
    }

    public int getYStart() {
        return mYStart;
    }

    public void setYStart(int YStart) {
        mYStart = YStart;
        mYEnd = mYStart + mHeight;
        mYCenter = (mYStart + mYEnd)/2;
    }

    public int getXEnd() {
        return mXEnd;
    }

    public int getYEnd() {
        return mYEnd;
    }

    public int getXCenter() {
        return mXCenter;
    }

    public int getYCenter() {
        return mYCenter;
    }

    public TeXIcon getIcon() {
        return mIcon;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public Expression getExpression() {
        return mExpression;
    }

    public void setExpression(Expression expression) {
        mExpression = expression;
    }

    public Color getColor() {
        return mColor;
    }

    public void setColor(Color color) {
        mColor = color;
    }

    public void updateIcon(int alignment) {
        mTeXFormula = new TeXFormula(mExpression.getLatexExpression());
        mIcon = mTeXFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
        mWidth = mIcon.getIconWidth();
        mHeight = mIcon.getIconHeight();
        mYStart = alignment - (mHeight/2);
        mYEnd = mYStart + mHeight - 1;
        mYCenter = (mYStart + mYEnd)/2;
//        If X position changes then all elements should do too
//        mXEnd = mXStart + mWidth - 1;
//        mXCenter = (mXStart + mXEnd)/2;
    }

    public ImageView getImageView() {
        BufferedImage image = prepareToDrawME();
        // writing image buffer to image
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        // setting a imageView element
        mImageView.setImage(wr);
        return mImageView;
    }

    public BufferedImage prepareToDrawME() {
        // now create an actual image of the rendered equation
        BufferedImage image = new BufferedImage(this.getWidth(),
                this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gg = image.createGraphics();
        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, this.getWidth(), this.getHeight());
        JLabel jl = new JLabel();
        jl.setForeground(this.getColor());
        this.getIcon().paintIcon(jl, gg, 0, 0);
        return image;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("MathElement:")
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mId = " + mId)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mXStart = " + mXStart)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mXEnd = " + mXEnd)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("myStart = " + mYStart)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("myEnd = " + mYEnd)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mWidth = " + mWidth)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mHeight = " + mHeight)
                .append(System.getProperty("line.separator"));
        stringBuilder.append("mExpression = " + mExpression.getLatexExpression())
                .append(System.getProperty("line.separator"));

        return stringBuilder.toString();
    }

    @Override
    public int compareTo(MathElement o) {
        return mXStart - o.getXStart();   // sort by x position
    }
}
