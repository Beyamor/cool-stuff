package map 
{
	import flash.geom.Point;
	import net.flashpunk.Entity;
	import net.flashpunk.utils.Draw;
	import util.Pixel;
	import net.flashpunk.FP;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class HexTile extends Entity 
	{
		private var _radius:Number;
		private function get radius():Number { return _radius; }
		
		private var _color:uint;
		private function get color():uint { return _color; }
		
		private var _points:Vector.<Point>;
		private function get points():Vector.<Point> { return _points; }
		
		public function HexTile(x:Number, y:Number, radius:Number, pixelData:Pixel)
		{
			super(x, y);
			
			_radius = radius;
			_color = pixelData.rgba;
			
			makePointList();
		}
		
		private function makePointList():void {
			
			var theta:Number;
			_points = new Vector.<Point>;
			
			for (var pointIndex:uint = 0; pointIndex < 6; ++pointIndex) {
				
				theta = pointIndex * (Math.PI * 2 / 6);
				
				_points.push(new Point(
							x + radius * Math.cos(theta),
							y + radius * Math.sin(theta)));
			}
		}
		
		override public function render():void 
		{
			if (!collideRect(x, y, FP.camera.x - radius, FP.camera.y - radius, FP.width + 2 * radius, FP.height + 2 * radius)) return;
			
			var firstPoint:Point;
			var secondPoint:Point;
			var pointIndex:uint;
						
			for (pointIndex = 0; pointIndex < points.length; ++pointIndex) {
								
				firstPoint = points[pointIndex];
				secondPoint = points[(pointIndex + 1) % points.length];
				
				Draw.linePlus(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y, 0x000000, 1, 2);
			}
			
			Draw.circlePlus(x, y, radius * 0.8, color);
		}
	}

}