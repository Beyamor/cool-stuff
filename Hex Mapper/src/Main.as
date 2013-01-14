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
			
			FP.screen.color = 0x666666;
		}
		
		override public function init():void 
		{
			super.init();
			
			FP.console.enable();
			FP.world = new Overworld;
		}
		
	}
	
}