package colorblind;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import colorblind.generators.ColorDeficiencySimulator;
import colorblind.generators.ColorTranformGenerator;
import colorblind.generators.DaltonizeGenerator;
import colorblind.generators.Generator;
import colorblind.generators.RegularRenderer;

public class ColorBlindness implements PConstants {

    public final static String VERSION = "##library.prettyVersion##";

    private PApplet parent;

    private int pixelCount;
    float avgGeneratorTimeMillis;
    float avgDrawTimeMillis;

    private String saveFrameLocation;
    private boolean enableSaveFrame;
    private boolean saveNextFrame;
    private char saveFrameKey;
    private int saveFrameNum;
    private boolean reportStats;
    private String parentClassName;

    private Generator generator;
    private boolean callPostDraw;
    private int[] cachedPixels;
    private boolean active;

    public ColorBlindness(PApplet parent) {
        this.parent = parent;

        String[] tokens = parent.getClass().getName().split("\\.");
        parentClassName = tokens[tokens.length - 1].toLowerCase();

        this.callPostDraw = checkForMethod("postDraw");

        parent.registerMethod("pre", this);
        parent.registerMethod("draw", this);
        parent.registerMethod("keyEvent", this);

        avgGeneratorTimeMillis = 1;
        pixelCount = parent.width * parent.height;
        cachedPixels = null;
        active = true;

        enableSaveFrame = false;
        saveNextFrame = false;
        reportStats = false;

        renderRegular();

        welcome();
    }

    private void welcome() {
        System.out
                .println("##library.name## ##library.prettyVersion## by ##author##");
    }

    public static String version() {
        return VERSION;
    }

    /*
     * User Settings
     */
    public ColorTranformGenerator simulate(Deficiency colorBlindness) {
        ColorTranformGenerator generator = ColorDeficiencySimulator
                .createSimulator(colorBlindness);

        setGenerator(generator);

        return generator;
    }

    public ColorTranformGenerator simulateProtanopia() {
        return simulate(Deficiency.PROTANOPIA);
    }

    public ColorTranformGenerator simulateDeuteranopia() {
        return simulate(Deficiency.DEUTERANOPIA);
    }

    public ColorTranformGenerator simulateTritanopia() {
        return simulate(Deficiency.TRITANOPIA);
    }

    public ColorTranformGenerator simulateAchromatopsia() {
        return simulate(Deficiency.ACHROMATOPSIA);
    }

    public ColorTranformGenerator simulateBlueConeMonochromacy() {
        return simulate(Deficiency.BLUE_CONE_MONOCHROMACY);
    }

    public ColorTranformGenerator daltonize(Deficiency colorBlindness) {
        ColorTranformGenerator generator = DaltonizeGenerator
                .createDaltonizer(colorBlindness);

        setGenerator(generator);

        return generator;
    }

    public ColorTranformGenerator daltonizeProtanopia() {
        return daltonize(Deficiency.PROTANOPIA);
    }

    public ColorTranformGenerator daltonizeDeuteranopia() {
        return daltonize(Deficiency.DEUTERANOPIA);
    }

    public ColorTranformGenerator daltonizeTritanopia() {
        return daltonize(Deficiency.TRITANOPIA);
    }

    public ColorTranformGenerator daltonizeAchromatopsia() {
        return daltonize(Deficiency.ACHROMATOPSIA);
    }

    public ColorTranformGenerator daltonizeBlueConeMonochromacy() {
        return daltonize(Deficiency.BLUE_CONE_MONOCHROMACY);
    }

    public RegularRenderer renderRegular() {
        RegularRenderer generator = new RegularRenderer();

        setGenerator(generator);

        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;

        avgGeneratorTimeMillis = 1;
    }

    public Generator getGenerator() {
        return generator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void enableSaveFrame(char key, String saveFrameLocation) {
        saveFrameKey = key;

        if (!saveFrameLocation.endsWith(File.separator)) {
            saveFrameLocation += File.separator;
        }
        this.saveFrameLocation = saveFrameLocation;

        enableSaveFrame = true;
    }

    public void enableSaveFrame(String saveFrameLocation) {
        enableSaveFrame('s', saveFrameLocation);
    }

    public void enableSaveFrame(char key) {
        enableSaveFrame(key, "");
    }

    public void enableSaveFrame() {
        enableSaveFrame('s');
    }

    public float getGeneratorTime() {
        return avgGeneratorTimeMillis;
    }

    public float getDrawTime() {
        return avgDrawTimeMillis;
    }

    public void reportStats() {
        reportStats = true;
    }

    /*
     * Drawing functions, called by Processing framework
     * 
     * The pre() and draw() methods are where all the action is in this class.
     * The rest is mainly configuration code.
     */
    public void pre() {
        if (cachedPixels == null) {
            cachedPixels = new int[pixelCount];
        } else {
            // replace frame with the un-transformed pixels. this prevents an
            // infinite cycle of re-simulating the same pixel, which leads to
            // blackness.
            parent.loadPixels();
            System.arraycopy(cachedPixels, 0, parent.pixels, 0, pixelCount);
            parent.updatePixels();
        }
    }

    public void draw() {
        // retrieve and cache what was just drawn
        parent.loadPixels();
        System.arraycopy(parent.pixels, 0, cachedPixels, 0, pixelCount);

        if (saveNextFrame)
            parent.saveFrame(saveFrameLocation + "####-" + parentClassName
                    + "-pre-transformation.png");

        // create transformed frame
        long generateStartTime = System.nanoTime();

        if (active)
            generator.transformPixels(parent.pixels);

        avgGeneratorTimeMillis = 0.9f * avgGeneratorTimeMillis + 0.1f
                * (System.nanoTime() - generateStartTime) / 1000000f;

        parent.updatePixels();

        if (saveNextFrame && active)
            parent.saveFrame(saveFrameLocation + "####-" + parentClassName
                    + "-post-transformation.png");

        if (callPostDraw) {
            callMethod("postDraw");
        }

        if (saveNextFrame) {
            parent.saveFrame(saveFrameLocation + "####-" + parentClassName
                    + "-final.png");
            saveNextFrame = false;
        }

        if (reportStats) {
            System.out
                    .printf("Frame Rate: %.2f frames/sec | Generator Render Time: %.3f ms\n",
                            parent.frameRate, avgGeneratorTimeMillis);
        }
    }

    public void keyEvent(KeyEvent e) {
        // the saveFrameNum thing below is to keep the program from saving many
        // frames in a row
        // if the user is too slow to lift their finger off the keyboard.
        if (e.getKey() == saveFrameKey && enableSaveFrame
                && parent.frameCount > saveFrameNum + 10) {
            saveNextFrame = true;
            saveFrameNum = parent.frameCount;
        }
    }

    /*
     * Internal reflective methods for examining sketch.
     */
    private boolean checkForMethod(String method) {
        try {
            parent.getClass().getMethod(method);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    private void callMethod(String method) {
        try {
            Method m = parent.getClass().getMethod(method);
            m.invoke(parent, new Object[] {});
        } catch (NoSuchMethodException e) {
            System.err.println("Unexpected exception calling " + method
                    + ". Please report.");
            e.printStackTrace();
        } catch (SecurityException e) {
            System.err.println("Unexpected exception calling " + method
                    + ". Please report.");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Unexpected exception calling " + method
                    + ". Please report.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Unexpected exception calling " + method
                    + ". Please report.");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("Exception thrown in function " + method
                    + ". Please fix.");
            e.printStackTrace();
            parent.exit();
        }
    }
}