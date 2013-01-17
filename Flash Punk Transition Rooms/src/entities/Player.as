package entities 
{
	import net.flashpunk.Entity;
	import net.flashpunk.FP;
	import net.flashpunk.graphics.Image;
	import net.flashpunk.utils.Input;
	import net.flashpunk.utils.Key;
	import values.Depths;
	import values.Game;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class Player extends Entity 
	{
		[Embed (source="/images/player.png" )]
    	public static const SPRITE:Class;
		
		public function Player(x:Number, y:Number)
		{
			super(x, y, new Image(SPRITE));
			
			Input.define("up",		Key.W, Key.UP);
			Input.define("down",	Key.S, Key.DOWN);
			Input.define("left",	Key.A, Key.LEFT);
			Input.define("right",	Key.D, Key.RIGHT);
			
			layer = Depths.PLAYER;
		}
		
		override public function update():void 
		{
			super.update();
			
			// Like you haven't seen this a dozen times
			var dx:Number = 0, dy:Number = 0;
			
			if (Input.check("left"))	dx -= 1;
			if (Input.check("right"))	dx += 1;
			if (Input.check("up"))		dy -= 1;
			if (Input.check("down"))	dy += 1;
			
			if (dx != 0 && dy != 0) {
				
				dx *= Math.SQRT1_2;
				dy *= Math.SQRT1_2;
			}
			
			x += dx * Game.PLAYER_SPEED;
			y += dy * Game.PLAYER_SPEED;
			
			FP.camera.x = x - FP.width/2;
			FP.camera.y = y - FP.height/2;
		}
	}

}