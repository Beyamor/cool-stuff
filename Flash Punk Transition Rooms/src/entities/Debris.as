package entities 
{
	import net.flashpunk.Entity;
	import net.flashpunk.graphics.Image;
	import values.Depths;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class Debris extends Entity 
	{
		[Embed (source="/images/debris.png" )]
    	public static const SPRITE:Class;
		
		public function Debris(x:Number, y:Number)
		{
			super(x, y, new Image(SPRITE));
			
			layer = Depths.DEBRIS;
		}
		
	}

}