package colorblind.generators;

/**
 * 
 * @author James Schmitz
 * 
 *         Base class for all generators.
 *
 */
public abstract class Generator {

    /**
     * Transform the pixels in the current frame.
     * 
     * @param pixels
     */
    abstract public void transformPixels(int[] pixels);

}
