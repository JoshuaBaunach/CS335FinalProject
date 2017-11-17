/*
This class will store the data necessary to render the "tweens" for a particular point.
It will hold the point itself as well as its position on the coordinate frame.
 */

public class TweenDataPoint {

    public GridPoint sourcePoint, destPoint;
    public int gridX, gridY;

    public TweenDataPoint(GridPoint sourcePoint, GridPoint destPoint, int gridX, int gridY)
    {
        this.sourcePoint = sourcePoint;
        this.destPoint = destPoint;
        this.gridX = gridX;
        this.gridY = gridY;
    }
}
