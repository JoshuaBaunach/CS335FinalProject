/*
This class will store the data necessary to render the "tweens" for a particular point.
It will hold the point itself as well as its position on the coordinate frame.
 */

public class TweenDataPoint {

    public GridPoint point;
    public int gridX, gridY;

    public TweenDataPoint(GridPoint point, int gridX, int gridY)
    {
        this.point = point;
        this.gridX = gridX;
        this.gridY = gridY;
    }
}
