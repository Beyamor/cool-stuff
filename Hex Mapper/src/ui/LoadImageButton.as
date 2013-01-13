package ui 
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Loader;
	import flash.display.LoaderInfo;
	import flash.events.Event;
	import flash.net.FileFilter;
	import flash.net.FileReference;
	import flash.utils.ByteArray;
	import net.flashpunk.graphics.Image;
	/**
	 * ...
	 * @author beyamor
	 */
	public class LoadImageButton extends Button 
	{
		[Embed (source="/images/load-image.png" )]
    	public static const IMAGE:Class;
		
		private var fileReference:FileReference;
		
		public function LoadImageButton(x:Number, y:Number)
		{
			var image:Image = new Image(IMAGE);
			
			super(x, y, image.width, image.height, function():void {
				
				fileReference = new FileReference();
				fileReference.addEventListener(Event.SELECT, imageSelected);
				fileReference.browse([new FileFilter("PNG (*.png)", "*.png")]);
			});
			
			normal = hover = down = inactive = image;
		}
		
		private function imageSelected(event:Event):void {
			
			fileReference.addEventListener(Event.COMPLETE, imageLoaded);
			fileReference.load();
		}
		
		private function imageLoaded(event:Event):void {
			
			var content:ByteArray = fileReference.data;
			
			var loader:Loader = new Loader();
			loader.loadBytes(content);
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, imageBytesRead);
		}
		
		private function imageBytesRead(event:Event):void {
			
			var loaderInfo:LoaderInfo = event.target as LoaderInfo;
			
			var bitmapData:BitmapData = new BitmapData(loaderInfo.width, loaderInfo.height);
			
			trace(bitmapData.width + ", " + bitmapData.height);
		}
	}

}