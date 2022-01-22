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
	vertex mouse = new vertex(0,0), rmouse = new vertex(0,0), cameraAdj = new vertex(0,0);
	boolean mUp = false, mDown = false, mLeft = false, mRight = false;
	int currentEdit = 0;
	
	public void start(Stage primaryStage) 
	{
		try 
		{
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,1248,768);
			
			Canvas cOpen = new Canvas(scene.getWidth() + 1, scene.getHeight() + 1);
			GraphicsContext g2 = cOpen.getGraphicsContext2D();
			
			
			// VARS
			int squareSize = 16;
			int squaresInXY = 164;
			int screenCubeSize = squareSize * squaresInXY; // must be multple of square size	
			
			
			ArrayList<Rectangle> placedBlock = new ArrayList<Rectangle>();
			ArrayList<Line> gridLines = new ArrayList<Line>();
			ArrayList<ArrayList<Rectangle>> history = new ArrayList<ArrayList<Rectangle>>();
			history.add(new ArrayList<Rectangle>());

			Boolean[][] occupationTable = new Boolean[squaresInXY][squaresInXY];
			for(int i = 0; i < occupationTable.length; i++)
			{
				for(int x = 0; x < occupationTable[i].length; x++)
				{
					occupationTable[i][x] = false;
				}
			}
			
			
			
			g2.setFill(Color.GREY);
			
			g2.fillRect(0, 0, scene.getWidth() + 1, scene.getHeight() + 1);
			
			g2.setFill(Color.BLACK);
			root.getChildren().add(cOpen);
			
			
			for(int i = 0; i < screenCubeSize/squareSize + 1; i++)
			{
				gridLines.add(new Line(i*squareSize, 0, i*squareSize, screenCubeSize));
				gridLines.add(new Line(0, i*squareSize, screenCubeSize, i*squareSize));
				root.getChildren().addAll(gridLines.get(gridLines.size() - 2), gridLines.get(gridLines.size() - 1));
			}
			
			
			
			primaryStage.setResizable(false);
			primaryStage.setTitle("MAP TOOL? ");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			Rectangle a = new Rectangle(0, 0, squareSize, squareSize);
			
			AnimationTimer timer = new AnimationTimer() 
			{
				public void handle(long now)
				{
					a.setX(rmouse.getX()*squareSize);
					a.setY(rmouse.getY()*squareSize);
					
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
					
					//mouse.setX(rmouse.getX() - cameraAdj.getX());
					//mouse.setY(rmouse.getY() - cameraAdj.getY());
					mouse.sub(rmouse, cameraAdj);
					
					//System.out.println("X " + (mouse.getX()) + " Y " + (mouse.getY()) + ", RAW: X " + rmouse.getX() + " Y " + rmouse.getY());
					
				}
			};
			timer.start();
			
			
			root.getChildren().add(a);
			
			scene.setOnMouseMoved(new EventHandler<MouseEvent>() 
			{
				public void handle(MouseEvent e)
				{
					rmouse.setX((int)e.getX()/squareSize);
					rmouse.setY((int)e.getY()/squareSize);
					mouse.setX((int)e.getX()/squareSize - cameraAdj.getX());
					mouse.setY((int)e.getY()/squareSize - cameraAdj.getY());
				}
			});
			
			scene.setOnMouseDragEntered(new EventHandler<MouseEvent>() {
			
				public void handle(MouseEvent e)
				{
					history.add(new ArrayList<Rectangle>());
					currentEdit++;
				}
			});
			
			scene.setOnMouseDragged(new EventHandler<MouseEvent>() 
			{
				public void handle(MouseEvent e)
				{
					rmouse.setX((int)e.getX()/squareSize);
					rmouse.setY((int)e.getY()/squareSize);
					mouse.setX((int)e.getX()/squareSize - cameraAdj.getX());
					mouse.setY((int)e.getY()/squareSize - cameraAdj.getY());
					
					if(mouse.getX() < screenCubeSize/squareSize && mouse.getY() < screenCubeSize/squareSize && mouse.getX() > 0 && mouse.getY() > 0 && !occupationTable[(int) mouse.getX()][(int) mouse.getY()])
					{
						placeBlock(root, placedBlock, history, currentEdit, occupationTable, mouse, squareSize);
					}
				}
			});
			
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() 
			{
				public void handle(KeyEvent e)
				{
					//System.out.println(e.getCode());
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
					
					if(e.isControlDown() && e.getCode() == KeyCode.Z && currentEdit != 0)
					{
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
			
			scene.setOnKeyReleased(new EventHandler<KeyEvent>() 
			{
				public void handle(KeyEvent e)
				{
					//System.out.println(e.getCode());
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
			
			scene.setOnMousePressed(new EventHandler<MouseEvent>() 
			{
				public void handle(MouseEvent e)
				{
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
					if(!plr.keyboard)
					{
						plr.setUp(false);
					}
				}
			});
			*/
			
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void placeBlock(BorderPane root, ArrayList<Rectangle> a, ArrayList<ArrayList<Rectangle>> h, int currentEdit, Boolean[][] t, vertex m, int blockSize)
	{
		Rectangle b = new Rectangle(m.getX()*blockSize, m.getY()*blockSize, blockSize, blockSize);
		a.add(b);
		h.get(currentEdit).add(b);
		
		root.getChildren().add(a.get(a.size() - 1));
		
		t[(int) m.getX()][(int) m.getY()] = true;
	}
	
	
	public static void main(String[] args) 
	{
		launch(args);
	}
}
