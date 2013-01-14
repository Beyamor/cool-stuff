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
			
			// Remember this triangle becase it's going to come in handy.
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
			
			//Okay. Let's name a few values here.
			
			// An important propery when looking at horizontal texes is the overlap.
			// So, with hexes stacked like this:
			//  
			//   -------
			//  /       \         /
			// /         \       /
			//   |- 2r -| -------
			// \         /       \
			//  \       /         \
			//   -------  |- 2r -|
			//  /       \         /
			// /         \       /
			//
			// The hexes have overlapping x values.
			// We can get this value from our handy triangle.
			const horizontalOverlap:Number = 0.5 * hexRadius;
			
			// We can also look at horizontal distance between the centers
			// of hexes. This has two meanings. The first is the "interleaved"
			// distance - the horizontal distance btween two diagonally adjacent
			// hexes.
			const interleavedHorizontalDistance:Number = 2 * hexRadius - horizontalOverlap;
			
			// The second is the horizontal distance between two hexes in the same row.
			// e.g, the distance between A and B
			//   -------           -------
			//  /       \         /        \
			// /         \       /          \
			//      A     -------      B
			// \         /       \          /
			//  \       /         \        /
			//   -------            -------
			const horizontalDistance:Number = 2 * hexRadius + (2 * hexRadius - 2 * horizontalOverlap);
			
			// Also nice to have is the vertical height of a hex.
			// i.e., ½√3*r * 2
			const verticalHeight:Number = Math.sqrt(3) * hexRadius;
			
			// The width then is how many interleaved distances can fit in the space
			// times two for the sparse array
			_width = Math.ceil(widthInPixels / interleavedHorizontalDistance) * 2;
			
			// The height is how many heights can fit in the space,
			// again times two for the sparse array
			_height = Math.ceil(heightInPixels / verticalHeight) * 2;
			
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
			// I ain't even gunna justify the math tho. Draw it out.
			for (tileX = 0; tileX < width; ++tileX) {
				for (tileY = 0; tileY < height; ++tileY) {
					
					if (tileX % 2 != tileY % 2) continue;
					
					// even columns
					if (tileX % 2 == 0) {
						
						x = (tileX/2) * horizontalDistance;
						y = (tileY/2) * verticalHeight;
						_tiles[tileX][tileY] = new HexTile(x, y, hexRadius);
					}
					
					// odd columns
					else {
						
						x = Math.floor(tileX / 2) * horizontalDistance + interleavedHorizontalDistance;
						y = Math.floor(tileY / 2) * verticalHeight + verticalHeight/2;
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