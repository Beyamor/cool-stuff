package map 
{
	import flash.display.BitmapData;
	import util.Pixel;
	/**
	 * ...
	 * @author beyamor
	 */
	public class MapData 
	{
		private var _width:uint;	public function get width():uint	{ return _width; }
		private var _height:uint;	public function get height():uint	{ return _height; }
		
		private var _pixels:Vector.<Vector.<Pixel>>;
		public function get pixels():Vector.<Vector.<Pixel>> { return _pixels; }
		
		public function MapData(bitmapData:BitmapData)
		{
			_width	= bitmapData.width;
			_height	= bitmapData.height;
			
			_pixels = new Vector.<Vector.<Pixel>>;
			for (var x:uint = 0; x < _width; ++x) {
				
				_pixels.push(new Vector.<Pixel>);
				for (var y:uint = 0; y < _height; ++y) {
					
					_pixels[x][y] = new Pixel(bitmapData.getPixel32(x, y));
				}
			}
		}
		
		public function getPixel(x:uint, y:uint):Pixel {
			
			return pixels[x][y];
		}
		
		public function getMappedPixel(fromWidth:Number, fromHeight:Number, x:Number, y:Number):Pixel {
			
			var mappedX:uint = Math.max(0, Math.min(width - 1, Math.floor(x * width / fromWidth)));
			var mappedY:uint = Math.max(0, Math.min(height - 1, Math.floor(y * height / fromHeight)));
			
			return getPixel(mappedX, mappedY);
		}
		
	}

}