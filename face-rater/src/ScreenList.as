package  
{
	import adobe.utils.CustomActions;
	import flash.display.BitmapData;
	/**
	 * ...
	 * @author beyamor
	 */
	public class ScreenList 
	{
		[Embed (source="/img/screen1.png" )]
    	public static const SCREEN1:Class;
		
		[Embed (source="/img/screen2.png" )]
    	public static const SCREEN2:Class;
		
		[Embed (source="/img/screen3.png" )]
    	public static const SCREEN3:Class;
		
		[Embed (source="/img/screen4.png" )]
    	public static const SCREEN4:Class;
		
		[Embed (source="/img/screen5.png" )]
    	public static const SCREEN5:Class;
		
		[Embed (source="/img/screen6.png" )]
    	public static const SCREEN6:Class;
		
		[Embed (source="/img/screen7.png" )]
    	public static const SCREEN7:Class;
		
		private var unused:Vector.<BitmapData>,
					used:Vector.<BitmapData>;
		
		public function ScreenList() 
		{
			unused = new <BitmapData>[
				(new SCREEN1).bitmapData,
				(new SCREEN2).bitmapData,
				(new SCREEN3).bitmapData,
				(new SCREEN4).bitmapData,
				(new SCREEN5).bitmapData,
				(new SCREEN6).bitmapData,
				(new SCREEN7).bitmapData
			];
			
			used = new Vector.<BitmapData>;
		}
		
		public function get next():BitmapData {
			
			if (unused.length == 0) {
				
				unused = used;
				used = new Vector.<BitmapData>;
			}
			
			var index:int = Math.floor(Math.random() * unused.length);
			var screen:BitmapData = unused[index];
			
			used.push(screen);
			unused.splice(index, 1);
			
			return screen;
		}
	}

}