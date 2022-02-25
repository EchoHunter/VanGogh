import processing.core.PApplet;
import processing.core.PImage;

public class Brush {
    PApplet processing;
    PImage painting;
    int paintingIndex;
    int x, y;
    int brushSize, maxBrushThickness, numberOfBristles, brushStagger;
    Bristle[] bristles;

    /*
     * Brush settings
     */
    static int DEFAULT_MAX_BRUSH_THICKNESS = 20;
    static int DEFAULT_NUM_BRISTLES = 500;
    static int DEFAULT_BRUSH_SIZE = 40;
    static int DEFAULT_BRUSH_STAGGER = 6;
    
    public Brush(PApplet processing) {
        this.processing = processing;
        this.paintingIndex = 0;
        this.x = 0;
        this.y = 0;
        this.brushSize = DEFAULT_BRUSH_SIZE;
        this.brushStagger = DEFAULT_BRUSH_STAGGER;
        this.maxBrushThickness = DEFAULT_MAX_BRUSH_THICKNESS;
        this.numberOfBristles = DEFAULT_NUM_BRISTLES;
        this.bristles = new Bristle[this.numberOfBristles];

        initializeBristles();
    }

    public void initializeBristles() {
        for (int i = 0; i < bristles.length; i++) {
            int bristleX = (int) processing.random(-brushSize, brushSize);
            int bristleY = (int) processing.random(-brushSize, brushSize);
            int bristleRadius = (int) processing.random(maxBrushThickness);

            this.bristles[i] = new Bristle(bristleX, bristleY, bristleRadius);
        }
    }

    public void update() {
        x += ((processing.mouseX - x) / brushStagger);
        y += ((processing.mouseY - y) / brushStagger);

        if (x < 0) {
            x = 0;
        } else if (x > processing.width) {
            x = processing.width;
        }

        if (y < 0) {
            y = 0;
        } else if (y > processing.height) {
            y = processing.height;
        }
    }

    public void draw() {
        // Must run whether mouse is pressed or not
        // to keep bristle x/y positions up to date
        for (int i = 0; i < bristles.length; i++) {
            if (processing.mousePressed) {
                bristles[i].draw();
            }
            
            // Must be placed after draw
            bristles[i].update();
        }
    }

    public void setNewPainting(PImage newPainting) {
        this.painting = newPainting;
        processing.getSurface().setSize(newPainting.width, newPainting.height);

        // Sets window to move to the upper left corner
        processing.getSurface().setLocation(10, 10);
    }
    
    class Bristle {
        int startX, startY, endX, endY;
        int radius, startRadius;

        Bristle(int x, int y, int r) {
            startX = x;
            startY = y;
            endX = x;
            endY = y;
            radius = r;
            startRadius = r;
        }

        void update() {
            if (endX == (x + startX) && endY == (y + startY)) {
                radius += 1;
            } else {
                radius = startRadius;
            }

            endX = x + startX;
            endY = y + startY;
        }

        void draw() {
            int pixel = painting.get(endX, endY);
            processing.stroke(pixel);

            if (endX == (x + startX) && endY == (y + startY)) {
                processing.strokeWeight(radius / 50);
                processing.point(endX, endY);
            } else {
                float dist = PApplet.dist(x + startX, y + startY, endX, endY);

                // The bigger the distance, i.e. the faster the mouse is moved,
                // the bigger and more coarse the line width
                processing.strokeWeight((radius * dist) / 50);
                processing.line(x + startX, y + startY, endX, endY);
            }
        }
    }
}
