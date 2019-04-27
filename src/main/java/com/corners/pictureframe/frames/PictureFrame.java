/*
 * The MIT License
 *
 * Copyright 2019 Erick Rincones.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.corners.pictureframe.frames;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * Picture frame class.
 * 
 * <p> This simple component shows an image that can be zoomed with the mouse
 * wheel and moved clicking and dragging with the mouse. Also, the image can be
 * fitted to keeps full visible on the picture frame even when is resized, this
 * and other behavior can be disabled with the provided methods.
 * 
 * @author Erick Rincones
 */
public class PictureFrame extends JComponent {
    
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 3698994423227680539L;
    
    /**
     * The default cursor.
     */
    private static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    
    /**
     * The move cursor.
     */
    private static final Cursor MOVE_CURSOR = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    
    /**
     * Flag to store the dynamic status.
     * 
     * <p> When the status is {@code false} the mouse events are ignored.
     * 
     * @see setDynamic(boolean)
     * @see isDynamic()
     */
    private boolean dynamic;
    
    /**
     * Flag to store the antialiasing status.
     * 
     * <p> When the status is {@code true} and {@link zoom} is less than
     * {@code 1}, the image is drawed with antialiasing.
     * 
     * @see setAntialiasing(boolean)
     * @see isAntialiasing()
     * @see paintComponent(Graphics)
     */
    private boolean antialiasing;
    
    /**
     * Flag to store the fitted image status.
     * 
     * <p> When is {@code true} the image is fitted to the window and cannot be
     * moved over the picture frame, even when the picture frame is resized.
     * This is achived constantly updating {@link zoom} level to
     * {@link zoom_min}.
     * 
     * @see fit()
     * @see setZoom(float, Point)
     * @see zoom_min
     */
    private boolean fitted;
    
    /**
     * The zoom level.
     * 
     * <p> When the {@link zoom} level is updated, the {@link size} and
     * {@link location} should be modified too.
     * 
     * @see setZoom(float, Point)
     * @see getZoom()
     * @see zoom_min
     * @see zoom_max
     * @see size
     */
    private float zoom;
    
    /**
     * The minimum zoom level.
     * 
     * @see getMinZoom()
     * @see zoom
     * @see zoom_max
     */
    private float zoom_min;
    
    /**
     * The maximum zoom level.
     * 
     * <p> By default the maximum zoom level is {@code 20} and cannot be less
     * than {@code 1}.
     * 
     * @see setMaxZoom(float)
     * @see getMinZoom()
     * @see zoom
     * @see zoom_min
     */
    private float zoom_max;
    
    /**
     * The last location where the mouse was pressed.
     */
    private Point mouse;
    
    /**
     * The location of the image into the picture frame.
     * 
     * <p> This location refers to the upper left corner of the image.
     * 
     * @see setImageLocation(Point)
     * @see setImageLocation(int, int)
     * @see setZoom(float, Point)
     * @see moveImage(Point)
     * @see getImageLocation()
     * @see paintComponent(Graphics)
     */
    private final Point location;
    
    /**
     * The size of the image after to apply the {@link zoom}.
     * 
     * @see paintComponent(Graphics)
     * @see setZoom(float, Point)
     * @see zoom
     */
    private final Dimension size;
    
    /**
     * The image to show.
     * 
     * @see setImage(BufferedImage, boolean)
     * @see getImage()
     * @see paintComponent(Graphics)
     */
    private BufferedImage image;
    
    /**
     * Creates a new picture frame without image.
     * 
     * @see setListeners()
     */
    public PictureFrame() {
        // Initialize fields
        dynamic = true;
        fitted = true;
        zoom = 0F;
        zoom_min = 0F;
        zoom_max = 20F;
        location = new Point();
        size = new Dimension();
        image = null;
        
        // Sets the listeners
        setListeners();
    }
    
    /**
     * Creates a new picture frame with the given image.
     * 
     * @param img the image to draw
     * 
     * @see setListeners()
     * @see image
     */
    public PictureFrame(BufferedImage img) {
        // Initialize fields
        dynamic = true;
        fitted = true;
        zoom = 0F;
        zoom_min = 0F;
        zoom_max = 20F;
        location = new Point();
        size = new Dimension();
        image = img;
        
        // Updates the zoom_min, zoom, size and locaion values
        refresh(null);
        
        // Sets the listeners
        setListeners();
    }
    
    /**
     * Sets the mouse listeners for the default behavior.
     * 
     * @see dynamic
     */
    private void setListeners() {
        // Adds the component listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refresh(null);
            }
        });
        
        // Adds the mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (dynamic && image != null && e.getButton() == MouseEvent.BUTTON1)
                    mouse = e.getPoint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (dynamic && !fitted) setCursor(MOVE_CURSOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (dynamic && !fitted) setCursor(DEFAULT_CURSOR);
            }
        });
        
        // Adds the mouse motion listener
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dynamic && image != null && e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
                    Point mouse_new = e.getPoint();
                    moveImage(new Point(mouse_new.x - mouse.x, mouse_new.y - mouse.y));

                    mouse = mouse_new;
                }
            }
        });
        
        // Adds the wheel listener
        addMouseWheelListener((MouseWheelEvent e) -> {
            if (dynamic)
                if (e.getPreciseWheelRotation() < 0D) zoomIn(e.getPoint());
                else                                  zoomOut(e.getPoint());
        });
    }
    
    /**
     * Sets the dynamic status.
     * 
     * @param status the dynamic status
     * 
     * @see dynamic
     * @see isDynamic()
     */
    public void setDynamic(boolean status) {
        dynamic = status;
    }
    
    /**
     * Sets the {@link antialiasing} status.
     * 
     * @param status the antialiasing status
     * 
     * @see antialiasing
     * @see isAntialiasing()
     * @see paintComponent(Graphics)
     */
    public void setAntialiasing(boolean status) {
        antialiasing = status;
    }
    
    /**
     * Sets the zoom level.
     * 
     * <p> The {@code zoom_new} is clamped between {@link zoom_min} and
     * {@link zoom_max} before to assign to {@link zoom} and updates the
     * {@link location} and {@link size} attributes.
     * 
     * <p> If the final value of {@link zoom} is equal to {@link zoom_min} the
     * {@link fitted} flag is setted to {@code true}.
     * 
     * <p> If {@code point} is {@code null}, the center of the component is used
     * as the mouse position.
     * 
     * @param zoom_new the zoom level
     * @param point    the mouse position.
     * 
     * @see zoom
     * @see zoom_min
     * @see zoom_max
     * @see fit()
     * @see original()
     * @see getZoom()
     */
    public void setZoom(float zoom_new, Point point) {
        // Check if image is null and border cases
        if (image == null) return;
        
        // Center mouse if is null
        if (point == null)
            point = new Point(getWidth() >> 1, getHeight() >> 1);
        
        // Saves the old zoom
        float zoom_old = zoom;
        
        // Set and apply the new zoom level
        zoom = zoom_new < zoom_max ? (zoom_new > zoom_min ? zoom_new : zoom_min) : zoom_max;
        size.width = (int) (zoom * image.getWidth());
        size.height = (int) (zoom * image.getHeight());
        
        // Updates the fitted flag and the cursor
        fitted = zoom == zoom_min;
        setCursor(fitted ? DEFAULT_CURSOR : MOVE_CURSOR);
        
        // Round fixer and zoom factor
        float round = zoom > zoom_old ? 0.5F : -0.5F;
        float factor = 1F - zoom / zoom_old;
        
        // Realocate horizontally
        int dx = getWidth() - size.width;
        location.x -= (int) ((location.x - point.x) * factor + round);
        
             if (dx >  0)                   location.x = dx >> 1;
        else if (dx == 0 || location.x > 0) location.x = 0;
        else if (dx > location.x)           location.x = dx;
        
        // Realocate vertically
        int dy = getHeight() - size.height;
        location.y -= (int) ((location.y - point.y) * factor + round);
        
             if (dy >  0)                   location.y = dy >> 1;
        else if (dy == 0 || location.y > 0) location.y = 0;
        else if (dy > location.y)           location.y = dy;
        
        // Repaint picture frame
        repaint();
    }
    
    /**
     * Sets the maximum zoom.
     * 
     * <p> If the given value is less than {@code 1}, it will lead to {@code 1}.
     * 
     * @param zoom_max_new the new maximum zoom
     * 
     * @see zoom_max
     * @see getMaxZoom()
     * @see setZoom(float, Point)
     */
    public void setMaxZoom(float zoom_max_new) {
        zoom_max = zoom_max_new < 1F ? 1F : zoom_max_new;
    }
    
    /**
     * Sets the image location.
     * 
     * @param point the new image location
     * 
     * @see location
     * @see setImageLocation(int, int)
     * @see moveImage(Point)
     * @see getLocation()
     */
    public void setImageLocation(Point point) {
        setImageLocation(point.x, point.y);
    }
    
    /**
     * Sets the image location.
     * 
     * The {@code x} and {@code y} values are clamped to ensure that will be
     * valid.
     * 
     * @param x the new image location on the x axis
     * @param y the new image location on the y axis
     * 
     * @see location
     * @see setImageLocation(Point)
     * @see moveImage(Point)
     * @see getImageLocation()
     */
    public void setImageLocation(int x, int y) {
        // Clamp the horizontal location
        int min_x = getWidth() - size.width;
        if (min_x < 0)
            location.x = x < min_x ? min_x : (x > 0 ? 0 : x);
        
        // Clamp the vertical location
        int min_y = getHeight() - size.height;
        if (min_y < 0)
            location.y = y < min_y ? min_y : (y > 0 ? 0 : y);
        
        // Repaint picture frame
        repaint();
    }
    
    /**
     * Sets a new image.
     * 
     * <p> If {@code fit_image} is {@code true}, the image will be fitted into
     * the picture frame.
     * 
     * @param image_new the new image
     * @param fit_image true to fit the image
     * 
     * @see image
     * @see fitted
     * @see getImage()
     * @see paintComponents(Graphics)
     */
    public void setImage(BufferedImage image_new, boolean fit_image) {
        // Set the new image
        image = image_new;
        
        // Repaint and return if the image is null
        if (image == null) {
            repaint();
            return;
        }
        
        // Update zoom variables
        if (fitted |= fit_image)
            zoom = 0F;
        
        // Update canvas
        refresh(null);
    }
    
    /**
     * Gets the {@link zoom} level.
     * 
     * @return the zoom level
     * 
     * @see zoom
     * @see getMinZoom()
     * @see getMaxZoom()
     * @see setZoom(float, Point)
     */
    public float getZoom() {
        return zoom;
    }
    
    /**
     * Gets the current minimum zoom level.
     * 
     * @return the minimum zoom level
     * 
     * @see zoom_min
     * @see getMaxZoom()
     * @see getZoom()
     */
    public float getMinZoom() {
        return zoom_min;
    }
    
    /**
     * Gets the maximum zoom level.
     * 
     * @return the maximum zoom level
     * 
     * @see zoom_max
     * @see setMaxZoom(float)
     * @see getMinZoom()
     * @see getZoom()
     */
    public float getMaxZoom() {
        return zoom_max;
    }
    
    /**
     * Gets the image {@link location} in the picture frame.
     * 
     * @return the image {@link location}
     * 
     * @see location
     * @see setImageLocation(Point)
     * @see setImageLocation(int, int)
     */
    public Point getImageLocation() {
        return new Point(location.x, location.y);
    }
    
    /**
     * Gets the image {@link size} for the current {@link zoom} level.
     * 
     * @return the image {@link size} for the current {@link zoom} level
     * 
     * @see size
     * @see zoom
     * @see getZoom()
     */
    public Dimension getImageSize() {
        return new Dimension(size.width, size.height);
    }
    
    /**
     * Gets the {@link image}.
     * 
     * @return the {@link image}
     * 
     * @see image
     * @see setImage(BufferedImage, boolean)
     */
    public BufferedImage getImage() {
        return image;
    }
    
    /**
     * Gets the {@link dynamic} status.
     * 
     * @return the {@link dynamic} status
     * 
     * @see dynamic
     * @see setDynamic(boolean)
     */
    public boolean isDynamic() {
        return dynamic;
    }
    
    /**
     * Gets the {@link antialiasing} status.
     * 
     * @return the {@link antialiasing} status
     * 
     * @see antialiasing
     * @see setAntialiasing(boolean)
     * @see paintComponent(Graphics)
     */
    public boolean isAntialiasing() {
        return antialiasing;
    }
    
    /**
     * Gets the {@link fitted} status.
     * 
     * @return the {@link fitted} status
     * 
     * @see fitted
     * @see zoom_min
     * @see fit()
     */
    public boolean isFitted() {
        return fitted;
    }
    
    /**
     * Checks if the {@link image} is in the original {@link size}.
     * 
     * @return true if the {@link zoom} level is {@code 1}
     * 
     * @see original()
     * @see zoom
     */
    public boolean isOriginal() {
        return zoom == 1F;
    }
    
    /**
     * Updates the picture frame attributes.
     * 
     * <p> The {@link zoom_min} is updated, and then {@link fit()} is called if
     * the {@link fiteed} flag is {@code true}, else the
     * {@link setZoom(float, Point)} method is called with the current
     * {@link zoom} with the given {@code poin} as mouse poisition.
     * 
     * @param point the mouse position
     * 
     * @see image
     * @see zoom_min
     * @see fit()
     * @see setZoom(float, Point)
     */
    private void refresh(Point point) {
        // Check if image is null
        if (image == null) return;
        
        // Updates the minimum zoom
        float width = (float) getWidth() / (float) image.getWidth();
        float height = (float) getHeight() / (float) image.getHeight();
        zoom_min = width < height ? (width < 1F ? width : 1F) : (height < 1F ? height : 1F);
        
        // Resize and realocate the image
        if (fitted) fit();
        else        setZoom(zoom, point);
    }
    
    /**
     * <p> The {@link image} is drawed at the stored {@link location} and with
     * the stored {@link size}.
     * 
     * <p> Antialiasing is applied if the {@link antialiasing} flag is
     * {@code true} and the {@link zoom} level is less than {@code 1}.
     * 
     * @see image
     * @see zoom
     * @see size
     * @see location
     * @see antialiasing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Antialiasing if the zoom is less than 1
        if (antialiasing && zoom < 1F) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        
        // Draw the image
        g.drawImage(image, location.x, location.y, size.width, size.height, this);
    }
    
    /**
     * Adjust the image to the windows size.
     * 
     * <p> Sets {@link zoom_min} as the {@link zoom} level using the center of
     * the picture frame as the location to apply the {@link zoom}.
     * 
     * @see fitted
     * @see setZoom(float, Point)
     * @see zoom_min
     * @see zoom
     */
    public void fit() {
        setZoom(zoom_min, null);
    }
    
    /**
     * Show the image at the original {@link size}.
     * 
     * <p> Sets {@code 1} as the {@link zoom} level using the center of the
     * picture frame as the location to apply the {@link zoom}.
     * 
     * @see setZoom(float, Point)
     * @see zoom
     */
    public void original() {
        setZoom(1F, null);
    }
    
    /**
     * Zoom in the {@link image} over the mouse position.
     * 
     * <p> If the mouse position is {@code null}, then the center of the picture
     * frame is used as the location to apply the {@link zoom}.
     * 
     * @param point the mouse position
     * 
     * @see setZoom(float, Point)
     * @see zoom
     */
    public void zoomIn(Point point) {
        setZoom(zoom * 1.25F, point);
    }
    
    /**
     * Zoom out the {@link image} over the mouse position.
     * 
     * <p> If the mouse position is {@code null}, then the center of the picture
     * frame is used as the location to apply the {@link zoom}.
     * 
     * @param point the mouse position
     * 
     * @see setZoom(float, Point)
     * @see zoom
     */
    public void zoomOut(Point point) {
        setZoom(zoom / 1.25F, point);
    }
    
    /**
     * Move the {@link image} on the picture frame.
     * 
     * <p> The given {@code distance} is added to the current {@link location}
     * and clamped to correct values.
     * 
     * @param distance the distance to move in both axis
     * 
     * @see setLocation(Point)
     * @see setLocation(int, int)
     * @see location
     */
    public void moveImage(Point distance) {
        // If the image is fitted dont move
        if (fitted) return;
        
        // Clamp the horizontal location
        int min_x = getWidth() - size.width;
        if (min_x < 0) {
            int x = location.x + distance.x;
            location.x = x < min_x ? min_x : (x > 0 ? 0 : x);
        }
        
        // Clamp the vertical location
        int min_y = getHeight() - size.height;
        if (min_y < 0) {
            int y = location.y + distance.y;
            location.y = y < min_y ? min_y : (y > 0 ? 0 : y);
        }
        
        // Repaint picture frame
        repaint();
    }
    
}
