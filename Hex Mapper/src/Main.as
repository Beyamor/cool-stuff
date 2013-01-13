package 
{
	import flash.display.Sprite;
	import flash.events.Event;
	import net.flashpunk.Engine;
	import net.flashpunk.FP;
	import values.Game;
	import worlds.Overworld;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class Main extends Engine 
	{
		
		public function Main():void 
		{
			super(Game.WIDTH, Game.HEIGHT);
		}
		
		override public function init():void 
		{
			super.init();
			
			FP.world = new Overworld;
		}
		
	}
	
}