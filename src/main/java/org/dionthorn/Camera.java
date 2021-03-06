package org.dionthorn;

public class Camera {

    private double xPos, yPos; // Camera position in 2D space { 11.5 , 10 } both positive values
    private double xDir, yDir; // Camera direction vector
    private double xPlane, yPlane; // Camera 2.5D plane position
    private final double MOVE_SPEED = 1;
    private final double ROTATION_SPEED = 0.45;

    public boolean left, right, forward, back;

    public Camera(double xPos, double yPos, double xDir, double yDir, double xPlane, double yPlane) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xDir = xDir;
        this.yDir = yDir;
        this.xPlane = xPlane;
        this.yPlane = yPlane;
    }

    public void update(Map map) {
        if(forward) {
            if(map.getMap()[(int)(xPos + xDir * MOVE_SPEED)][(int)yPos] == 0) {
                xPos+=xDir*MOVE_SPEED;
            }
            if(map.getMap()[(int)xPos][(int)(yPos + yDir * MOVE_SPEED)] == 0)
                yPos+=yDir*MOVE_SPEED;
            forward = false;
        }
        if(back) {
            if(map.getMap()[(int)(xPos - xDir * MOVE_SPEED)][(int)yPos] == 0)
                xPos-=xDir*MOVE_SPEED;
            if(map.getMap()[(int)xPos][(int)(yPos - yDir * MOVE_SPEED)] == 0)
                yPos-=yDir*MOVE_SPEED;
            back = false;
        }
        if(right) {
            double oldxDir=xDir;
            xDir=xDir*Math.cos(-ROTATION_SPEED) - yDir*Math.sin(-ROTATION_SPEED);
            yDir=oldxDir*Math.sin(-ROTATION_SPEED) + yDir*Math.cos(-ROTATION_SPEED);
            double oldxPlane = xPlane;
            xPlane=xPlane*Math.cos(-ROTATION_SPEED) - yPlane*Math.sin(-ROTATION_SPEED);
            yPlane=oldxPlane*Math.sin(-ROTATION_SPEED) + yPlane*Math.cos(-ROTATION_SPEED);
            right = false;
        }
        if(left) {
            double oldxDir=xDir;
            xDir=xDir*Math.cos(ROTATION_SPEED) - yDir*Math.sin(ROTATION_SPEED);
            yDir=oldxDir*Math.sin(ROTATION_SPEED) + yDir*Math.cos(ROTATION_SPEED);
            double oldxPlane = xPlane;
            xPlane=xPlane*Math.cos(ROTATION_SPEED) - yPlane*Math.sin(ROTATION_SPEED);
            yPlane=oldxPlane*Math.sin(ROTATION_SPEED) + yPlane*Math.cos(ROTATION_SPEED);
            left = false;
        }
    }

    public double getxPlane() {
        return xPlane;
    }

    public double getxPos() {
        return xPos;
    }

    public double getyPlane() {
        return yPlane;
    }

    public double getyPos() {
        return yPos;
    }

    public double getxDir() {
        return xDir;
    }

    public double getyDir() {
        return yDir;
    }


}
