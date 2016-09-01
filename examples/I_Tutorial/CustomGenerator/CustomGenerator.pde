/*
 Simple example demonstrating how to make a custom generator.
 
 The ColorBlindness library can do a lot more than just simulate color
 blindness. Think of it as a post-processor for your Processing sketch.
 
 In this example, the ColorBlindness library is applying a custom
 generator that converts all RGB colors to a grayscale color. The
 generator class must implement this method:
 
 void transformPixels(int[] pixels)
 
 The pixels argument is the pixel array after your draw() method is run. What
 you do with those pixels is up to you! You don't even have to do a color
 transformation. How about distorting everything with a fish eye lens? How about
 simulating cataracts? Or macular degeneration? This library can do those things
 too.
 */

import colorblind.ColorBlindness;
import colorblind.generators.Generator;

void setup() {
  size(500, 500);

  ColorBlindness colorBlindness = new ColorBlindness(this);

  /* 
   configure library to use an instance of the custom generator.
   
   try commenting this out to see what the sketch normally does.
   */
  colorBlindness.setGenerator(new RGBtoGrayscale());

  /*
   when building your own generators, it's a good idea to
   performance tune your code. Pre-compute as much as possible
   to keep the generator render time as low as possible.
   */
  colorBlindness.reportStats();

  background(255);
}

void draw() {
  // simple sketch to draw random colored lines.
  // old lines fade out.
  fill(color(255, 255, 255, 1));
  noStroke();
  rect(0, 0, width, height);

  stroke(color(random(255), random(255), random(255)));
  line(random(0, width), random(0, height), random(0, width), 
    random(0, height));
}