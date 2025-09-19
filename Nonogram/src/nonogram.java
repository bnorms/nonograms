import tester.*;
import javalib.worldimages.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.*;

class Utils {
  
  ArrayList<Cell> generateCellRow(int length) {
    ArrayList<Cell> worklist = new ArrayList<Cell>();
    for (int index = 0; index < length; index += 1) {
      worklist.add(new Cell());
    }
    return worklist;
  }
  
  WorldImage renderRow(ArrayList<Cell> row, int size, boolean marked) {
    WorldImage result = new EmptyImage();
    if (marked) {
      for (int index = 0; index < size; index += 1) {
        result = new BesideImage(result, row.get(index).renderMarked(500 / size));
      }
      return result;
    }
    for (int index = 0; index < size; index += 1) {
      result = new BesideImage(result, row.get(index).renderTrue(500 / size));
    }
    return result;
  }
  
  ArrayList<Integer> getRuns(ArrayList<Cell> list) {
    ArrayList<Integer> worklist = new ArrayList<Integer>();
    int size = list.size();
    int runSoFar = 0;
    for (int index = 0; index < size; index += 1) {
      int currState = list.get(index).trueState;
      if (currState == 1) {
        runSoFar += 1;
      }
      else {
        if (runSoFar > 0) {
          // adds backwards for ease of rendering
          worklist.add(0, runSoFar);
        }
        runSoFar = 0;
      }
    }
    
    if (runSoFar > 0) {
        // adds backwards for ease of rendering
        worklist.add(0, runSoFar);
    }
    
    if (worklist.size() == 0) {
      worklist.add(0);
    }
    
    return worklist;
  }
  
  WorldImage renderColumnRuns(ArrayList<ArrayList<Integer>> list, int renderSize) {
    int size = list.size();
    int fontSize = 38 - renderSize;
    if (fontSize < 12) {
      fontSize = 12;
    }
    WorldImage result = new EmptyImage();
    for (int column = 0; column < size; column += 1) {
      WorldImage currColumn = new EmptyImage();
      int runSize = list.get(column).size();
      for (int index = 0; index < runSize; index += 1) {
        currColumn = new AboveImage(
            new OverlayImage(new TextImage("" + list.get(column).get(index), fontSize, Color.BLACK),
                new RectangleImage(500 / renderSize, 500 / renderSize, OutlineMode.SOLID, Color.WHITE)),
            currColumn);
      }
      result = new BesideAlignImage(AlignModeY.BOTTOM, result, currColumn);
    }
    return result;
  }
  
  WorldImage renderRowRuns(ArrayList<ArrayList<Integer>> list, int renderSize) {
    int size = list.size();
    int fontSize = 38 - renderSize;
    if (fontSize < 12) {
      fontSize = 12;
    }
    WorldImage result = new EmptyImage();
    for (int row = 0; row < size; row += 1) {
      WorldImage currRow = new EmptyImage();
      int runSize = list.get(row).size();
      for (int index = 0; index < runSize; index += 1) {
        currRow = new BesideImage(
            new OverlayImage(new TextImage("" + list.get(row).get(index), fontSize, Color.BLACK),
                new RectangleImage(500 / renderSize, 500 / renderSize, OutlineMode.SOLID, Color.WHITE)),
            currRow);
      }
      result = new AboveAlignImage(AlignModeX.RIGHT, result, currRow);
    }
    return result;
  }
}

class Cell {
  // negative state: marked empty
  // zero state: undetermined
  // positive state: marked full
  int markedState;
  
  int trueState;
  
  // Convenience Constructor for default cell of determined fullness
  Cell(int trueState) {
    this.markedState = 0;
    this.trueState = trueState;
  }
  
  // Constructor
  Cell(int markedState, int trueState) {
    this.markedState = markedState;
    this.trueState = trueState;
  }
  
  // Convenience Constructor for pure random
  Cell() {
    this.markedState = 0;
    if ((int) (Math.random() * 2) < 1) {
      this.trueState = -1;
    }
    else {
      this.trueState = 1;
    }
  }
  
  WorldImage renderMarked(int size) {
    if (markedState < 0) {
      return new OverlayImage(new RectangleImage(size, size, OutlineMode.OUTLINE, Color.GRAY),
          new OverlayImage(new CircleImage((size / 3), OutlineMode.SOLID, Color.RED),
              new RectangleImage(size, size, OutlineMode.SOLID, Color.WHITE)));
    }
    if (markedState > 0) {
      return new OverlayImage( new RectangleImage(size, size, OutlineMode.OUTLINE, Color.GRAY),
          new RectangleImage(size, size, OutlineMode.SOLID, Color.BLACK));
    }
    return new OverlayImage( new RectangleImage(size, size, OutlineMode.OUTLINE, Color.GRAY),
        new RectangleImage(size, size, OutlineMode.SOLID, Color.WHITE));
  }
  
  WorldImage renderTrue(int size) {
    if (trueState <= 0) {
      return new OverlayImage( new RectangleImage(size, size, OutlineMode.OUTLINE, Color.GRAY),
          new RectangleImage(size, size, OutlineMode.SOLID, Color.WHITE));
    }
    return new OverlayImage( new RectangleImage(size, size, OutlineMode.OUTLINE, Color.GRAY),
        new RectangleImage(size, size, OutlineMode.SOLID, Color.BLACK));
  }
  
  boolean isCorrectSoFar() {
    return ((this.markedState >= 0) && (this.trueState == 1))
        || ((this.markedState <= 0) && (this.trueState == -1));
  }
  
  boolean isCorrectFinal() {
    return ((this.markedState == 1) && (this.trueState == 1))
        || ((this.markedState <= 0) && (this.trueState == -1));
  }
  
  void mark(int state) {
    this.markedState = state;
  }
  
  void changeTrueState(int state) {
    this.trueState = state;
  }
  
  void toggleFill() {
    if (this.markedState > 0) {
      this.markedState = 0;
    }
    else {
      this.markedState = 1;
    }

  }
  
  void toggleEmpty() {
    if (this.markedState < 0) {
      this.markedState = 0;
    }
    else {
      this.markedState = -1;
    }
  }
}

class Board {
  ArrayList<ArrayList<Cell>> board;

  // generates a random square board
  Board(int size) {
    ArrayList<ArrayList<Cell>> worklist = new ArrayList<ArrayList<Cell>>();
    for (int index = 0; index < size; index += 1) {
      worklist.add((new Utils()).generateCellRow(size));
    }
    this.board = worklist;
  }
  
  Board(ArrayList<ArrayList<Integer>> columnRuns, ArrayList<ArrayList<Integer>> rowRuns) {
    
  }
  
  int size() {
    return this.board.size();
  }
  
  Cell cellAt(int row, int col) {
    return this.board.get(row).get(col);
  }
  
  WorldImage renderMarked(int size) {
    WorldImage result = new EmptyImage();
    for (int index = 0; index < size; index += 1) {
      result = new AboveImage(result, new Utils().renderRow(this.board.get(index), size, true));
    }
    return result;
  }
  
  WorldImage renderTrue(int size) {
    WorldImage result = new EmptyImage();
    for (int index = 0; index < size; index += 1) {
      result = new AboveImage(result, new Utils().renderRow(this.board.get(index), size, false));
    }
    return result;
  }
  
  ArrayList<ArrayList<Integer>> getColumnRuns() {
    int size = this.size();
    ArrayList<ArrayList<Integer>> worklist = new ArrayList<ArrayList<Integer>>();
    for(int column = 0; column < size; column += 1) {
      ArrayList<Cell> currColumn = new ArrayList<Cell>();
      for (int index = 0; index < size; index += 1) {
        currColumn.add(this.board.get(index).get(column));
      }
      worklist.add(new Utils().getRuns(currColumn));
    }
    return worklist;
  }
  
  ArrayList<ArrayList<Integer>> getRowRuns() {
    int size = this.size();
    ArrayList<ArrayList<Integer>> worklist = new ArrayList<ArrayList<Integer>>();
    for(int row = 0; row < size; row += 1) {
      worklist.add(new Utils().getRuns(this.board.get(row)));
    }
    return worklist;
  }
  
  boolean correctSoFar() {
    int size = this.size();
    for (int row = 0; row < size; row += 1) {
      for (int column = 0; column < size; column += 1) {
        if (!(this.cellAt(row, column).isCorrectSoFar())) {
          return false;
        }
      }
    }
    return true;
  }
  boolean correctFinal() {
    int size = this.size();
    for (int row = 0; row < size; row += 1) {
      for (int column = 0; column < size; column += 1) {
        if (!(this.cellAt(row, column).isCorrectFinal())) {
          return false;
        }
      }
    }
    return true;
  }
  boolean validSoFar(ArrayList<ArrayList<Integer>> columnRuns, ArrayList<ArrayList<Integer>> rowRuns) {
    int size = this.size();
    for (int row = 0; row < size; row += 1) {
      for (int column = 0; row < column; row += 1) {
        // huh
      }
    }
    return true;
  }
}

class NonogramWorld extends World {
  Board nonogramBoard;
  ArrayList<ArrayList<Integer>> columnRuns;
  ArrayList<ArrayList<Integer>> rowRuns;
  
  int size;
  Posn boardStart;
  boolean foundStart;
  boolean renderSolution;
  String showCorrect;
  
  NonogramWorld(Board nonogramBoard) {
    this.nonogramBoard = nonogramBoard;
    this.size = nonogramBoard.size();
    this.columnRuns = this.nonogramBoard.getColumnRuns();
    this.rowRuns = this.nonogramBoard.getRowRuns();
    this.boardStart = new Posn(0, 0);
    this.foundStart = false;
    this.renderSolution = false;
    this.showCorrect = "none";
  }
  
  NonogramWorld(int size) {
    this.nonogramBoard = new Board(size);
    this.size = size;
    this.columnRuns = this.nonogramBoard.getColumnRuns();
    this.rowRuns = this.nonogramBoard.getRowRuns();
    this.boardStart = new Posn(0, 0);
    this.foundStart = false;
    this.renderSolution = false;
    this.showCorrect = "none";
  }
  
  WorldImage renderBoardWithRuns(boolean marked) {
    WorldImage columnRunImage = new Utils().renderColumnRuns(this.columnRuns, this.size);
    WorldImage rowRunImage = new Utils().renderRowRuns(this.rowRuns, this.size);
    
    if (!foundStart) {
      this.boardStart = new Posn((int) rowRunImage.getWidth(), (int) columnRunImage.getHeight());
      this.foundStart = true;
    }
    
    WorldImage result = new EmptyImage();
    if (marked) {
      result = this.nonogramBoard.renderMarked(this.size);
    }
    else {
      result = this.nonogramBoard.renderTrue(this.size);
    }
    
    result = new AboveImage(columnRunImage, result);
    result = new BesideAlignImage(AlignModeY.BOTTOM, rowRunImage, result);
    return result;
  }
  
  public WorldScene makeScene() {
    WorldImage levelImage = this.renderBoardWithRuns(!this.renderSolution);
    if (this.showCorrect.equals("correctFinal")) {
      levelImage = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          new TextImage("YOU WON! :D", 16, Color.GREEN), 0, 0, levelImage);
    }
    else if (this.showCorrect.equals("correct")) {
      levelImage = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          new TextImage("Correct so far!", 16, Color.GREEN), 0, 0, levelImage);
    }
    else if (this.showCorrect.equals("incorrect")) {
      levelImage = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          new TextImage("There are mistakes :(", 16, Color.RED), 0, 0, levelImage);
    }
    int sideLen1 = (int) levelImage.getWidth();
    int sideLen2 = (int) levelImage.getHeight();
    WorldScene levelScene = new WorldScene(sideLen1, sideLen2);
    levelScene.placeImageXY(levelImage, (sideLen1 / 2), (sideLen2 / 2));
    return levelScene;
  }
  
  public void onMousePressed(Posn pos, String buttonName) {
    Posn normalizedPos = new Posn(((pos.x - boardStart.x) * this.size) / 500, ((pos.y - boardStart.y) * size) / 500);
    if (normalizedPos.x >= 0 && normalizedPos.x < this.size && normalizedPos.y >= 0 && normalizedPos.y < this.size) {
      if (buttonName == "LeftButton") {
        this.nonogramBoard.cellAt(normalizedPos.y, normalizedPos.x).toggleFill();
        this.showCorrect = "none";
      }
      if (buttonName == "RightButton") {
        this.nonogramBoard.cellAt(normalizedPos.y, normalizedPos.x).toggleEmpty();
        this.showCorrect = "none";
      }
    }
  }
  
  public void onKeyEvent(String key) {
    if (key.equals("s")) {
      this.renderSolution = !this.renderSolution;
      this.showCorrect = "none";
    }
    else if (key.equals(" ")) {
      this.checkSoFar();
    }
    
  }
  
  void checkSoFar() {
    if (this.nonogramBoard.correctFinal()) {
      this.showCorrect = "correctFinal";
    }
    else if (this.nonogramBoard.correctSoFar()) {
      this.showCorrect = "correct";
    }
    else {
      this.showCorrect = "incorrect";
    }
  }
  
  
}

class ExamplesNonogram {
  void testBigBang(Tester t) {
    NonogramWorld w = new NonogramWorld(15);
    t.checkExpect(w.columnRuns, 5);
    t.checkExpect(w.rowRuns, 5);
    double tickRate = 0.02;
    w.bigBang(1000, 1000, tickRate);
  }
}