package worlds 
{
	import flash.display.BitmapData;
	import map.HexGrid;
	import map.HexTile;
	import map.MapData;
	import net.flashpunk.World;
	import ui.LoadImageButton;
	import values.Game;
	import net.flashpunk.utils.Input;
	import net.flashpunk.FP;
	import net.flashpunk.utils.Key;
	
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
			
			Input.define("up", Key.W, Key.UP);
			Input.define("down", Key.S, Key.DOWN);
			Input.define("right", Key.D, Key.RIGHT);
			Input.define("left", Key.A, Key.LEFT);
			
			var loadButton:LoadImageButton = new LoadImageButton(0, 0);
			loadButton.x = Game.WIDTH - loadButton.width - 10;
			loadButton.y = Game.HEIGHT - loadButton.height - 10;
			add(loadButton);
		}
		
		override public function update():void 
		{
			 super.update();
			 
			 if (Input.check("up"))		FP.camera.y -= Game.CAMERA_SPEED;
			 if (Input.check("down"))	FP.camera.y += Game.CAMERA_SPEED;
			 if (Input.check("left"))	FP.camera.x -= Game.CAMERA_SPEED;
			 if (Input.check("right"))	FP.camera.x += Game.CAMERA_SPEED;
		}
		
		public function loadMap(mapBitmapData:BitmapData):void {
			
			var grid:HexGrid = new HexGrid(Game.WORLD_WIDTH, Game.WORLD_HEIGHT, Game.TILE_RADIUS, new MapData(mapBitmapData));
			
			grid.addToWorld(this);
		}
	}

}