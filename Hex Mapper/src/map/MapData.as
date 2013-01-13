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
		private var _width:uint;	public function get width()		{ return _width; }
		private var _height:uint;	public function get height()	{ return _height; }
		
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
		
		public function getPixel(x:uint, y:uint) {
			
			return data.pixels[x][y];
		}
		
		public function getMappedPixel(fromWidth:Number, fromHeight:Number, x:Number, y:Number) {
			
			var mappedX:uint = Math.floor(x * data.width / fromWidth);
			var mappedY:uint = Math.floor(y * data.height / fromHeight);
			
			return getPixel(mappedX, mappedY);
		}
		
	}

}