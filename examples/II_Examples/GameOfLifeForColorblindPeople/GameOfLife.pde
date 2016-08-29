/*
 Basic Game of Life Implementation
 */

class GameOfLife {

  private boolean[] state;
  public int rows;
  public int cols;

  private final int[][] NEIGHBORS = { { -1, -1 }, { -1, 0 }, { -1, +1 }, 
    { 0, -1 }, { 0, +1 }, { +1, -1 }, { +1, 0 }, { +1, +1 } };

  public GameOfLife(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;

    state = new boolean[rows * cols];
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < cols; ++j) {
        state[i * cols + j] = random(1) > 0.8;
      }
    }
  }

  public boolean getState(int row, int col) {
    return state[row * cols + col];
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return cols;
  }

  public void update() {
    boolean[] newState = new boolean[rows * cols];

    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < cols; ++j) {
        newState[i * cols + j] = newState(i, j);
      }
    }
    state = newState;
  }

  private boolean newState(int row, int col) {
    int neighborCount = 0;

    for (int[] offset : NEIGHBORS) {
      if (isAlive(row + offset[0], col + offset[1])) {
        neighborCount++;
      }
    }

    if (isAlive(row, col)) {
      return (neighborCount == 2) || (neighborCount == 3);
    } else {
      return neighborCount == 3;
    }
  }

  private boolean isAlive(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      return false;
    }
    return state[row * cols + col];
  }
}
