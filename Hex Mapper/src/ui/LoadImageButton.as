package ui 
{
	import net.flashpunk.graphics.Image;
	/**
	 * ...
	 * @author beyamor
	 */
	public class LoadImageButton extends Button 
	{
		[Embed (source="/images/load-image.png" )]
    	public static const IMAGE:Class;
		
		public function LoadImageButton(x:Number, y:Number)
		{
			var image:Image = new Image(IMAGE);
			
			super(x, y, image.width, image.height, function():void {
				
				trace("yo, button pressed");
			});
			
			normal = hover = down = inactive = image;
		}
		
	}

}