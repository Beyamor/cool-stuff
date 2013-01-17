package worlds 
{
	import entities.Debris;
	import entities.Player;
	import net.flashpunk.Entity;
	import net.flashpunk.FP;
	import net.flashpunk.graphics.Text;
	import net.flashpunk.World;
	import net.flashpunk.utils.Input;
	import net.flashpunk.utils.Key;
	import transitions.transitions.FadeInAndOut;
	import transitions.TransitionWorld;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class GameWorld extends World 
	{
		
		public function GameWorld() 
		{
			super();
			
			Input.define("transition", Key.ENTER);
		}

		override public function begin():void 
		{
			super.begin();
			
			add(new Entity(0, 0, new Text("Press enter to go to the next world", 0, 20)));
			
			add(new Player(FP.width/2, FP.height/2));
			
			for (var i:int = 0; i < 3 + Math.random() * 10; ++i) {
				
				var x:Number = Math.random() * FP.width;
				var y:Number = Math.random() * FP.height;
				
				add(new Debris(x, y));
			}
		}
		
		override public function update():void 
		{
			super.update();
			
			if (Input.pressed("transition")) FP.world = new TransitionWorld(this, new GameWorld(), 1, new FadeInAndOut);
		}
	}

}