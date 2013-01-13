package map 
{
	import adobe.utils.CustomActions;
	import net.flashpunk.World;
	import ui.Button;
	/**
	 * ...
	 * @author beyamor
	 */
	public class HexGrid 
	{
		private var _tiles:Vector.<Vector.<HexTile>>;
		private function get tiles():Vector.<Vector.<HexTile>> { return _tiles; }
		
		private var _width:uint;	private function get width():uint { return _width; }
		private var _height:uint;	private function get height():uint { return _height; }
		
		public function HexGrid(widthInPixels:uint, heightInPixels:uint, hexRadius:Number)
		{
			var tileX:uint, tileY:uint, tile:HexTile, x:Number, y:Number;
			
			// Okay. Math. Math is hard.
			// We're going to ultimately store everything in a (sparse) 2d array of hexagons.
			// Uh. Like,
			// -----------------
			// | 1 | - | 2 | - |
			// | - | 3 | - | 4 |
			// | 5 | - | 6 | - |
			// | - | 7 | - | 8 |
			// -----------------
			// Hexes are vertically adjacent in column
			// and diagonally adjacent between columns.
			// e.g., with respect to "6":
			//			"2" is above
			//			"3" is to the upper-left
			//			"4" is to the upper-right
			//			"7" is to the lower-left
			//			and "8" is to the lower-right
			
			// Remember this triangle:
			//       |\
			//       | \
			//       |30\
			// ½√3*r |   \  r
			//       |    \
			//       |     \
			//       |      \
			//       |90   60\
			//       ----------
			//          ½r
			
			// Given a width in pixels, we want to fill it with hexes.
			// To do that, we first figure out the cardinality of our array.
			// The width, I think, is given by laying hexes end-to-end and
			// subtracting the intersect. So, with hexes stacked like this:
			//  
			//   -------
			//  /       \         /
			// /         \       /
			//            -------
			// \         /       \
			//  \       /         \
			//   -------   
			//  /       \         /
			// /         \       /
			//
			// We need to add the hex widths, but remove the overlap. Cool?
			// So, we're looking at something like:
			// (widthInPixels / (2r - ½r)) * 2
			var horizontalOverlap:Number = 0.5 * hexRadius;
			
			_width = 2 * Math.floor(widthInPixels / (2 * hexRadius - horizontalOverlap));
			
			// Height is simpler. stack dem hexes.
			// Something like:
			// (heightInPixels / (2 * ½√3*r)) * 2
			_height = 2 * Math.floor(heightInPixels / (Math.sqrt(3) * hexRadius));
			
			// Neato. Start us off with an empty array for simplicity.
			_tiles = new Vector.<Vector.<HexTile>>;
			for (tileX = 0; tileX < width; ++tileX) {
				
				_tiles.push(new Vector.<HexTile>);
				for (tileY = 0; tileY < height; ++tileY) {
					
					_tiles[tileX][tileY] = null;
				}
			}
			
			// Now here's where we're going to introduce some magic.
			// In even columns (0, 2, etc.), we're dealing with even rows (0, 2, etc.)
			// In odd columns (1, 3, etc.), we're dealing with odd columns (1, 3, etc.)
			// So, when we actually add a hex, we need to treat the two column types differently.
			for (tileX = 0; tileX < width; ++tileX) {
				for (tileY = 0; tileY < height; ++tileY) {
					
					if (tileX % 2 != tileY % 2) continue;
					
					// even columns
					if (tileX % 2 == 0) {
						
						x = (tileX/2) * (2 * hexRadius + (2 * hexRadius - 2 * horizontalOverlap));
						y = (tileY/2) * (Math.sqrt(3) * hexRadius);
						_tiles[tileX][tileY] = new HexTile(x, y, hexRadius);
					}
					
					// odd columns
					else {
						
						x = Math.floor(tileX / 2) * (2 * hexRadius + (2 * hexRadius - 2 * horizontalOverlap)) + (2 * hexRadius - horizontalOverlap);
						y = Math.floor(tileY / 2) * (Math.sqrt(3) * hexRadius) + (0.5 * Math.sqrt(3) * hexRadius);
						_tiles[tileX][tileY] = new HexTile(x, y, hexRadius);
					}
				}				
			}
		}
		
		public function addToWorld(world:World):void {
			
			for (var tileX:uint = 0; tileX < width; ++tileX) {
				for (var tileY:uint = 0; tileY < height; ++tileY) {
					
					var tile:HexTile = tiles[tileX][tileY];
					if (tile) world.add(tile);
				}
			}
		}
		
		public function removeFromWorld():void {
			
			for (var tileX:uint = 0; tileX < width; ++tileX) {
				for (var tileY:uint = 0; tileY < height; ++tileY) {
					
					var tile:HexTile = tiles[tileX][tileY];
					if (tile && tile.world) tile.world.remove(tile);
				}
			}
		}
	}

}