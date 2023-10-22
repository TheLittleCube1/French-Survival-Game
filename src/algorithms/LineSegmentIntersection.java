package algorithms;

public class LineSegmentIntersection {

	// Given three collinear points p, q, r, the function checks if point q lies on
	// line segment 'pr'
	public static boolean onSegment(Point p, Point q, Point r) {
		if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y)
				&& q.y >= Math.min(p.y, r.y)) {
			return true;
		} else {
			return false;
		}
	}

	// To find orientation of ordered triplet (p, q, r).
	// The function returns following values
	// 0 --> p, q and r are collinear
	// 1 --> Clockwise
	// 2 --> Counterclockwise
	public static int orientation(Point p, Point q, Point r) {

		double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

		if (val == 0)
			return 0; // collinear

		return (val > 0) ? 1 : -1; // clock or counterclock wise
	}

	// Returns whether or not p1q1 and p2q2 intersect
	public static boolean intersect(Point playerStart, Point playerFinish, Point boundary1, Point boundary2) {
		// Find the four orientations needed for general and
		// special cases
		int o1 = orientation(playerStart, playerFinish, boundary1);
		int o2 = orientation(playerStart, playerFinish, boundary2);
		int o3 = orientation(boundary1, boundary2, playerStart);
		int o4 = orientation(boundary1, boundary2, playerFinish);

		// General case
		if (o1 != o2 && o3 != o4)
			return true;

		// Special Cases
		// p1, q1 and p2 are collinear and p2 lies on segment p1q1
		if (o1 == 0 && onSegment(playerStart, boundary1, playerFinish))
			return true;

		// p1, q1 and q2 are collinear and q2 lies on segment p1q1
		if (o2 == 0 && onSegment(playerStart, boundary2, playerFinish))
			return true;

		// p2, q2 and p1 are collinear and p1 lies on segment p2q2
		if (o3 == 0 && onSegment(boundary1, playerStart, boundary2))
			return true;

		// p2, q2 and q1 are collinear and q1 lies on segment p2q2
		if (o4 == 0 && onSegment(boundary1, playerFinish, boundary2))
			return true;

		return false; // Doesn't fall in any of the above cases
	}

}
