package colorblind.generators;

/**
 * 
 * Regular renderer that doesn't do anything special.
 * 
 * This is useful if you want to make ColorBlindness render "normally" without
 * commenting out too much of your code.
 * 
 * @author James Schmitz
 *
 */
public class RegularRenderer extends Generator {

    public RegularRenderer() {

    }

    public void transformPixels(int[] pixels) {
        // do nothing
    }

}
