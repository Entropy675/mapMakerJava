package application;
	
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

public class Main extends Application 
{
	// mouse contains the x and y coordinates of the mouse on the grid (not in pixels, in grid squares)
	// rmouse contains the x and y corrdinates of the mouse on your screen, in grid squares. 
	// cameraAdj contains the x and y adjustments for the camera. Whenever you move left, in reality, you are moving every object on the screen to the right. This "moving everything" is recorded in camera adjust. 
	Vertex mouse = new Vertex(0,0), rmouse = new Vertex(0,0), cameraAdj = new Vertex(0,0);
	
	// some boolens that are true when certain buttons are held. If you hold up, mUp is true (short for move up).
	boolean mUp = false, mDown = false, mLeft = false, mRight = false;
	int currentEdit = 0; // this is just a counter for which edit you are currently on, we save each edit on an arraylist, and whenever you ctrl-z we delete a position on the array list and go back one on the current edit counter. 
	
	
	// setup for javafx
	public void start(Stage primaryStage) 
	{
		try 
		{
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,1248,768);
			
			Canvas cOpen = new Canvas(scene.getWidth() + 1, scene.getHeight() + 1);
			GraphicsContext g2 = cOpen.getGraphicsContext2D();
			
			
			// Basic variables that might be user-entered in the future. 
			int squareSize = 16; // the size of each square in the grid, so 16 means each square is 16x16
			int squaresInXY = 164; // the number of squares on the grid. 1 means only 1 square on the grid, 2 means 4 squares etc.
			int screenCubeSize = squareSize * squaresInXY; // this is the actual pixel length and width of the grid.
			
			
			// 3 array lists, one is for recording every single placed block, the next one is for recording every single grid line, and the last one is for recording every edit as you do them, so that you can ctrl-z to undo them later.
			ArrayList<Rectangle> placedBlock = new ArrayList<Rectangle>();
			ArrayList<Line> gridLines = new ArrayList<Line>();
			ArrayList<ArrayList<Rectangle>> history = new ArrayList<ArrayList<Rectangle>>();
			history.add(new ArrayList<Rectangle>());
			
			
			// this is just a simple truth table for the grid, if any position on it is 1 that means that there is something there, and you cant put anything on top of that. 
			Boolean[][] occupationTable = new Boolean[squaresInXY][squaresInXY];
			
			
			// filling it with 0s to start off
			for(int i = 0; i < occupationTable.length; i++)
			{
				for(int x = 0; x < occupationTable[i].length; x++)
				{
					occupationTable[i][x] = false;
				}
			}
			
			// adding cOpen to the screen, so basically it is just the canvas we draw on. basically just javafx setup stuff
			root.getChildren().add(cOpen);
			
			
			// setting the background color to gray, then filling the screen with gray.
			g2.setFill(Color.GREY);
			g2.fillRect(0, 0, scene.getWidth() + 1, scene.getHeight() + 1);
			
			// setting the next thing drawn to be black
			g2.setFill(Color.BLACK);
			
			// making all the lines for the grid, and adding them to the screen. 
			for(int i = 0; i < screenCubeSize/squareSize + 1; i++)
			{
				gridLines.add(new Line(i*squareSize, 0, i*squareSize, screenCubeSize));
				gridLines.add(new Line(0, i*squareSize, screenCubeSize, i*squareSize));
				root.getChildren().addAll(gridLines.get(gridLines.size() - 2), gridLines.get(gridLines.size() - 1));
			}
			
			
			// just some setup variables, self explanatory. 
			primaryStage.setResizable(false);
			primaryStage.setTitle("MAP TOOL? ");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// this rectangle highlights the current block the mouse is hovering over. 
			Rectangle mouseHoveredBlock = new Rectangle(0, 0, squareSize, squareSize);
			root.getChildren().add(mouseHoveredBlock); // add it to the window
			
			// this is gonna be the main timer for the game, everything inside of the internal handle() function runs every frame.
			AnimationTimer timer = new AnimationTimer() 
			{
				public void handle(long now)
				{
					// move the highlighted square to the right place
					mouseHoveredBlock.setX(rmouse.getX()*squareSize);
					mouseHoveredBlock.setY(rmouse.getY()*squareSize);
					
					// check if any of the movement keys are pressed, if they are move the camera accordingly.
					if(mUp)
					{
						cameraAdj.setY(cameraAdj.getY() + 1);
					}
					else if(mDown)
					{
						cameraAdj.setY(cameraAdj.getY() - 1);
					}
					
					if(mRight)
					{
						cameraAdj.setX(cameraAdj.getX() - 1);
					} 
					else if(mLeft)
					{
						cameraAdj.setX(cameraAdj.getX() + 1);
					}
					
					
					// move all the gridlines and placed blocks around based on where the camera has moved 
					for (Shape i : gridLines)
					{
						i.setTranslateX(cameraAdj.getX()*squareSize);
						i.setTranslateY(cameraAdj.getY()*squareSize);
					}
					for (Rectangle i : placedBlock)
					{
						i.setTranslateX(cameraAdj.getX()*squareSize);
						i.setTranslateY(cameraAdj.getY()*squareSize);
						//System.out.println(i.getX() + " " + i.getY() + " " + occupationTable[(int) (i.getX()/squareSize)][(int) (i.getY()/squareSize)]);
					}
					
					
					mouse.sub(rmouse, cameraAdj); // change the mouse coordinates every time the camera is moved. 
					
					//System.out.println("X " + (mouse.getX()) + " Y " + (mouse.getY()) + ", RAW: X " + rmouse.getX() + " Y " + rmouse.getY());
					
				}
			};
			
			timer.start(); // start the game timer
			
			
			
			// everytihng inside the handle function continously happens whenever the mouse is moved.
			scene.setOnMouseMoved(new EventHandler<MouseEvent>() 
			{
				public void handle(MouseEvent e)
				{
					// just changing the coordinates of the mouse that is saved in the rmouse and mouse vertices. 
					rmouse.setX((int)e.getX()/squareSize);
					rmouse.setY((int)e.getY()/squareSize);
					mouse.setX((int)e.getX()/squareSize - cameraAdj.getX());
					mouse.setY((int)e.getY()/squareSize - cameraAdj.getY());
				}
			});

			// everytihng inside the handle function happens right when the mouse click and dragged.
			scene.setOnMouseDragEntered(new EventHandler<MouseEvent>() {
			
				public void handle(MouseEvent e)
				{
					// whenever the mouse is dragged, we add a new entry into the history. Then all the squares added are added to this history position.
					history.add(new ArrayList<Rectangle>());
					currentEdit++;
				}
			});
			
			// everytihng inside the handle function continuously happens whenever the mouse is click and dragged.
			scene.setOnMouseDragged(new EventHandler<MouseEvent>() 
			{
				public void handle(MouseEvent e)
				{
					// while the mouse is being dragged, since its location is still changing we need to do all the normal moved code
					rmouse.setX((int)e.getX()/squareSize);
					rmouse.setY((int)e.getY()/squareSize);
					mouse.setX((int)e.getX()/squareSize - cameraAdj.getX());
					mouse.setY((int)e.getY()/squareSize - cameraAdj.getY());
					
					// then we basically place a block everywhere the mouse is dragged over, that already doesn't have a block on it.
					if(mouse.getX() < screenCubeSize/squareSize && mouse.getY() < screenCubeSize/squareSize && mouse.getX() > 0 && mouse.getY() > 0 && !occupationTable[(int) mouse.getX()][(int) mouse.getY()])
					{
						placeBlock(root, placedBlock, history, currentEdit, occupationTable, mouse, squareSize);
					}
				}
			});
			
			// whenever any key is pressed the code inside handle happens.
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() 
			{
				public void handle(KeyEvent e)
				{
					//System.out.println(e.getCode());
					
					// this is pretty self explanatory.
					if(e.getCode() == KeyCode.A)
					{
						mLeft = true;
					}
					if(e.getCode() == KeyCode.D)
					{
						mRight = true;
					}
					if(e.getCode() == KeyCode.W)
					{
						mUp = true;
					}
					if(e.getCode() == KeyCode.S)
					{
						mDown = true;
					}
					
					// this is the ctrl+z stuff
					if(e.isControlDown() && e.getCode() == KeyCode.Z && currentEdit != 0)
					{
						// iterates through the current position in the history, removes all the squares in there from existance, and then removes that position itself from history.
						System.out.println("UNDO");
						for (int i = 0; i < history.get(currentEdit).size(); i++)
						{
							occupationTable[(int) history.get(currentEdit).get(i).getX()/squareSize][(int) history.get(currentEdit).get(i).getY()/squareSize] = false;
							root.getChildren().remove(history.get(currentEdit).get(i));
						}
						history.remove(currentEdit);
						currentEdit--;
					}
				}
			});
			
			// whenever a key is released this code happens.
			scene.setOnKeyReleased(new EventHandler<KeyEvent>() 
			{
				public void handle(KeyEvent e)
				{
					
					// pretty self explanatory. 
					if(e.getCode() == KeyCode.A)
					{
						mLeft = false;
					}
					if(e.getCode() == KeyCode.D)
					{
						mRight = false;
					}
					if(e.getCode() == KeyCode.W)
					{
						mUp = false;
					}
					if(e.getCode() == KeyCode.S)
					{
						mDown = false;
					}
				}
			});
			
			// whenever you click normally the stuff in handle happens.
			scene.setOnMousePressed(new EventHandler<MouseEvent>() 
			{
				public void handle(MouseEvent e)
				{
					// if there is nothing where you have clicked, and its inside the grid, place a block there (and add it to history)
					if(mouse.getX() < screenCubeSize/squareSize && mouse.getY() < screenCubeSize/squareSize && mouse.getX() > 0 && mouse.getY() > 0 && !occupationTable[(int) mouse.getX()][(int) mouse.getY()])
					{
						history.add(new ArrayList<Rectangle>());
						currentEdit++;
						placeBlock(root, placedBlock, history, currentEdit, occupationTable, mouse, squareSize);
					}
				}
			});
			
			/*
			scene.setOnMouseReleased(new EventHandler<MouseEvent>() 
			{
				public void handle(MouseEvent e)
				{
					
				}
			});
			*/
			
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	// function for placing a block. This is totally gonna have to change for when we have pictures, because right now it only works with rectangles. Maybe we can just have an override method?
	public void placeBlock(BorderPane root, ArrayList<Rectangle> a, ArrayList<ArrayList<Rectangle>> h, int currentEdit, Boolean[][] t, Vertex m, int blockSize)
	{
		Rectangle b = new Rectangle(m.getX()*blockSize, m.getY()*blockSize, blockSize, blockSize);
		a.add(b);
		h.get(currentEdit).add(b);
		
		root.getChildren().add(a.get(a.size() - 1));
		
		t[(int) m.getX()][(int) m.getY()] = true;
	}
	
	
	// setup stuff for javafx
	public static void main(String[] args) 
	{
		launch(args);
	}
}
