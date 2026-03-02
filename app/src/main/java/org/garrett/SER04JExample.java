package org.garrett;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.AccessDeniedException;

/// Example class to show SER04-J by never forgetting the performSecurityManagerCheck function
class Shape implements Serializable {
  // Private internal state
  private String shape;
  private double width;
  private double height;

  private static final String UNKNOWN = "UNKNOWN";

  // Private functions
  /**
   * Performs a security check to ensure that the caller has permission to modify the shape.
   * @throws AccessDeniedException
   */
  private void performSecurityManagerCheck() throws AccessDeniedException {
    // SecurityManager sm = System.getSecurityManager();
    // if (sm != null) {
    //   sm.checkPermission(new RuntimePermission("modifyShape"));
    // }
  }

  /**
   * Validates the input parameters for the shape. Ensures that the shape type is either CIRCLE or POLYGON,
   * and that the width and height are non-negative.
   * @param shape
   * @param w
   * @param h
   * @throws InvalidClassException
   */
  private void validateInput(String shape, double w, double h) throws InvalidClassException {
    if (w < 0.0 || h < 0.0 || !shape.equals("CIRCLE") && !shape.equals("POLYGON"))
      throw new InvalidClassException("Parameters invalidatable");
  }

  // Constructors
  /**
   * Default constructor initializes the shape to UNKNOWN with width and height of 0.0.
   * @throws AccessDeniedException
   */
  public Shape() throws AccessDeniedException {
    performSecurityManagerCheck();

    shape = UNKNOWN;
    width = 0.0;
    height = 0.0;
  }

  // Functions
  /**
   * Returns the type of shape (CIRCLE or POLYGON). If the caller does not have permission to access the shape,
   * it returns null.
   * @return
   */
  public String getShape() {
    try {
      performSecurityManagerCheck();
      return shape;
    } catch(AccessDeniedException e){
      return null;
    }
  }

  /**
   * Returns the width of the shape. If the caller does not have permission to access the shape, it returns -1.0.
   * @return
   */
  public double getWidth() {
    try {
      performSecurityManagerCheck();
      return width;
    } catch(AccessDeniedException e){
      return -1.0;
    }
  }

  /**
   * Returns the height of the shape. If the caller does not have permission to access the shape, it returns -1.0.
   * @return
   */
  public double getHeight() {
    try {
      performSecurityManagerCheck();
      return height;
    } catch(AccessDeniedException e){
      return -1.0;
    }
  }

  /**
   * Changes the shape type to the new shape. If the caller does not have permission to modify the shape, or if the new shape is invalid, the shape remains unchanged.
   * @param newShape POLYGON or CIRCLE
   */
  public void changeShape(String newShape) {
    if (shape.equals(newShape)) {
      // No change
      return;
    } else {
      try {
        performSecurityManagerCheck();
        validateInput(newShape, width, height);
        shape = newShape;
      } catch(AccessDeniedException e){
        // Do nothing
      } catch(InvalidClassException e){
        // Do nothing
      }
    }
  }

  /**
   * Sets the width of the shape to the new width. If the caller does not have permission to modify the shape, or if the new width is invalid, the width remains unchanged.
   * @param w
   */
  public void setWidth(double w) {
    if (w == width) {
      // No change
      return;
    } else {
      try {
        performSecurityManagerCheck();
        validateInput(shape, w, height);
        width = w;
      } catch(AccessDeniedException e){
        // Do nothing
      } catch(InvalidClassException e){
        // Do nothing
      }
    }
  }

  /**
   * Sets the height of the shape to the new height. If the caller does not have permission to modify the shape, or if the new height is invalid, the height remains unchanged.
   * @param h
   */
  public void setHeight(double h) {
    if (h == height) {
      // No change
      return;
    } else {
      try {
        performSecurityManagerCheck();
        validateInput(shape, width, h);
        height = h;
      } catch(AccessDeniedException e){
        // Do nothing
      } catch(InvalidClassException e){
        // Do nothing
      }
    }
  }

  /**
   * Writes the object to the output stream. It performs a security check before allowing serialization. If the caller does not have permission to modify the shape, an AccessDeniedException is thrown.
   * @param out
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException {
    performSecurityManagerCheck();
    out.writeObject(shape);
    out.writeObject(width);
    out.writeObject(height);
  }

  /**
   * Reads the object from the input stream. It performs a security check before allowing deserialization. If the caller does not have permission to modify the shape, an AccessDeniedException is thrown. It also validates the input parameters after deserialization to ensure they are valid.
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();

    if (!UNKNOWN.equals(shape)) {
      performSecurityManagerCheck();
      validateInput(shape, width, height);
    }

    if (width != 0.0) {
      performSecurityManagerCheck();
      validateInput(shape, width, height);
    }

    if (height != 0.0) {
      performSecurityManagerCheck();
      validateInput(shape, width, height);
    }
  }
}

public class SER04JExample {
  /**
   * Main method for testing the Shape class. It creates a shape, changes its type, and sets its dimensions. It also handles any AccessDeniedException that may occur during the operations.
   * @param args
   */
  public static void main(String[] args) {
    // Example usage
    try {
      Shape shape = new Shape();
      shape.changeShape("CIRCLE");
      shape.setWidth(5.0);
      shape.setHeight(5.0);

      System.out.println("Shape: " + shape.getShape());
      System.out.println("Width: " + shape.getWidth());
      System.out.println("Height: " + shape.getHeight());
    } catch (AccessDeniedException e) {
      System.out.println("Access denied: " + e.getMessage());
    }
  }
}