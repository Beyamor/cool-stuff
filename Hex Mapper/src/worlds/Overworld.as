package worlds 
{
	import flash.display.BitmapData;
	import map.HexGrid;
	import map.HexTile;
	import net.flashpunk.World;
	import ui.LoadImageButton;
	import values.Game;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class Overworld extends World 
	{
		
		public function Overworld() 
		{
			super();
		}
		
		override public function begin():void 
		{
			super.begin();
			
			var loadButton:LoadImageButton = new LoadImageButton(0, 0);
			loadButton.x = Game.WIDTH - loadButton.width - 10;
			loadButton.y = Game.HEIGHT - loadButton.height - 10;
			add(loadButton);
		}
		
		public function loadMap(mapBitmapData:BitmapData):void {
			
			var grid:HexGrid = new HexGrid(800, 600, 32);
			
			grid.addToWorld(this);
		}
	}

}