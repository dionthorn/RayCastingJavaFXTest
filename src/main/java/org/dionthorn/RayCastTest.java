package org.dionthorn;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URI;
import java.util.ArrayList;

public class RayCastTest extends Application {

    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;
    private final Camera camera = new Camera(4.5, 4.5, 1, 0, 0, -.66);
    private final ArrayList<Texture> textures = new ArrayList<>();
    private final ArrayList<Map> maps = new ArrayList<>();
    private Map currentMap;
    private GraphicsContext gc;
    private long lastFrame = 0;

    public void render() {
        gc.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gc.setFill(Color.BLACK); // Ceiling
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT/2d);
        gc.setFill(Color.GRAY); // Floor
        gc.fillRect(0, SCREEN_HEIGHT/2d, SCREEN_WIDTH, SCREEN_HEIGHT/2d);

        for(int x=0; x<SCREEN_WIDTH; x=x+1) {
            double cameraX = 2 * x / (double) (SCREEN_WIDTH) - 1;
            double rayDirX = camera.getxDir() + camera.getxPlane() * cameraX;
            double rayDirY = camera.getyDir() + camera.getyPlane() * cameraX;
            //Map position
            int mapX = (int) camera.getxPos();
            int mapY = (int) camera.getyPos();
            //length of ray from current position to next x or y-side
            double sideDistX;
            double sideDistY;
            //Length of ray from one side to next in map
            double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
            double perpWallDist;
            //Direction to go in x and y
            int stepX, stepY;
            boolean hit = false;//was a wall hit
            int side = 0;//was the wall vertical or horizontal
            //Figure out the step direction and initial distance to a side
            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (camera.getxPos() - mapX) * deltaDistX;
            }
            else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - camera.getxPos()) * deltaDistX;
            }
            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (camera.getyPos() - mapY) * deltaDistY;
            }
            else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - camera.getyPos()) * deltaDistY;
            }
            //Loop to find where the ray hits a wall
            while(!hit) {
                //Jump to next square
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                }
                else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                //Check if ray has hit a wall
                if(currentMap.getMap()[mapX][mapY] > 0) hit = true;
            }
            //Calculate distance to the point of impact
            if(side==0)
                perpWallDist = Math.abs((mapX - camera.getxPos() + (1 - stepX) / 2d) / rayDirX);
            else
                perpWallDist = Math.abs((mapY - camera.getyPos() + (1 - stepY) / 2d) / rayDirY);
            //Now calculate the height of the wall based on the distance from the camera
            int lineHeight;
            if(perpWallDist > 0) lineHeight = Math.abs((int)(SCREEN_HEIGHT / perpWallDist));
            else lineHeight = SCREEN_HEIGHT;

            //calculate lowest and highest pixel to fill in current stripe
            int drawStart = -lineHeight/2+ SCREEN_HEIGHT/2;
            if(drawStart < 0)
                drawStart = 0;
            int drawEnd = lineHeight/2 + SCREEN_HEIGHT/2;
            if(drawEnd >= SCREEN_HEIGHT)
                drawEnd = SCREEN_HEIGHT - 1;

            int texNum = currentMap.getMap()[mapX][mapY] - 1;
            double wallX;//Exact position of where wall was hit
            if(side==1) {//If its a y-axis wall
                wallX = (camera.getxPos() + ((mapY - camera.getyPos() + (1 - stepY) / 2d) / rayDirY) * rayDirX);
            } else {//X-axis wall
                wallX = (camera.getyPos() + ((mapX - camera.getxPos() + (1 - stepX) / 2d) / rayDirX) * rayDirY);
            }
            wallX-=Math.floor(wallX);
            //x coordinate on the texture
            int texX = (int)(wallX * (textures.get(texNum).getWidth()));
            if(side == 0 && rayDirX > 0) texX = textures.get(texNum).getWidth() - texX - 1;
            if(side == 1 && rayDirY < 0) texX = textures.get(texNum).getHeight() - texX - 1;

            //calculate y coordinate on texture
            for(int y=drawStart; y<drawEnd; y++) {
                int texY = (((y*2 - SCREEN_HEIGHT + lineHeight) << 6) / lineHeight) / 2;
                Color color;
                if(side==0) color = textures.get(texNum).getPixelColor(texX, texY); //pixels[texX + (texY * textures.get(texNum).SIZE)];
                else color = textures.get(texNum).getPixelColor(texX, texY).darker();//pixels[texX + (texY * textures.get(texNum).SIZE)]>>1) & 8355711;//Make y sides darker
                PixelWriter pw = gc.getPixelWriter();
                pw.setColor(x, y, color);
            }
        }
    }

    public void update() {
        // Update camera data
        camera.update(currentMap);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create Canvas setup
        Group rootGroup = new Group();
        Scene rootScene = new Scene(rootGroup, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
        primaryStage.sizeToScene();
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        rootGroup.getChildren().add(canvas);

        // Keyboard handling
        rootScene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE) {
                primaryStage.close();
            } else if(key.getCode() == KeyCode.LEFT) {
                camera.left = true;
            } else if(key.getCode() == KeyCode.RIGHT) {
                camera.right = true;
            } else if(key.getCode() == KeyCode.DOWN) {
                camera.back = true;
            } else if(key.getCode() == KeyCode.UP) {
                camera.forward = true;
            }
        });

        // Mouse left click handling
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (mouseEvent) -> {
            int mouseX = (int) mouseEvent.getX();
            int mouseY = (int) mouseEvent.getY();
            System.out.println("Mouse Click at: (" + mouseX + ", " + mouseY + ")");
        });

        // initialize textures
        String[] textureNames = FileOpUtils.getFileNamesFromDirectory(URI.create(getClass().getResource("/Textures/").toString()));
        for(String textureName: textureNames) {
            System.out.println(textureName);
            textures.add(new Texture(textureName));
        }

        // initialize maps, obviously if more than one map we just want to loop through the Map folder like above
        maps.add(new Map("0_map.txt"));
        currentMap = maps.get(0);

        // Staging and animator setup
        primaryStage.setScene(rootScene);
        primaryStage.show();
        AnimationTimer animator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastFrame >= 16_000_000) {
                    update();
                    render();
                    lastFrame = now;
                }
            }
        };
        animator.start();
    }

    public static void main(String[] args) {
        launch();
    }

}