package 
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.SimpleButton;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TextEvent;
	import flash.events.TimerEvent;
	import flash.media.Camera;
	import flash.media.Video;
	import flash.text.TextField;
	import flash.utils.Timer;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class Main extends Sprite 
	{		
		public static const WIDTH:uint	= 640,
							HEIGHT:uint	= 480;
		
		public function Main():void 
		{
			if (stage) init();
			else addEventListener(Event.ADDED_TO_STAGE, init);
		}
		
		private function init(e:Event = null):void 
		{
			var screens:ScreenList = new ScreenList();
			
			removeEventListener(Event.ADDED_TO_STAGE, init);
			
			var capturedImageData:BitmapData = new BitmapData(WIDTH, HEIGHT);
			
			var capturedImage:Bitmap = new Bitmap(capturedImageData);
			capturedImage.visible = false;
			addChild(capturedImage);
			
			var camera:Camera	= Camera.getCamera(),
				video:Video		= new Video(WIDTH, HEIGHT);
			
			video.attachCamera(camera);
			stage.addChild(video);
			video.visible = false;
			
			var buttonText:TextField = new TextField();
			buttonText.text = "Take picture";
			buttonText.width = buttonText.textWidth + 5;
			buttonText.height = buttonText.textHeight + 5;
			
			var buttonMargin:int = 5;
			buttonText.x = buttonMargin;
			buttonText.y = buttonMargin;
			
			var buttonSprite:Sprite = new Sprite();
			buttonSprite.graphics.beginFill(0xdddddd);
			buttonSprite.graphics.drawRect(0, 0, buttonText.width + buttonMargin * 2, buttonText.height + buttonMargin * 2);
			buttonSprite.graphics.endFill();
			buttonSprite.addChild(buttonText);
			
			var screen:Bitmap = new Bitmap();
			screen.visible = false;
			addChild(screen);
			
			var bullshit:TextField = new TextField();
			bullshit.text =
			"A picture of your face will be compared against a picture of the ideal human face.\n" +
			"Your face will then be rated based on the divergence.";
			bullshit.width = WIDTH;
			bullshit.height = HEIGHT;	
			addChild(bullshit);
			
			var takePicture:SimpleButton = new SimpleButton(buttonSprite, buttonSprite, buttonSprite, buttonSprite);
			takePicture.x = WIDTH - buttonSprite.width;
			takePicture.y = 0;
			takePicture.addEventListener(MouseEvent.CLICK, function():void {
				
				bullshit.visible = false;
				
				capturedImageData.draw(video);
				capturedImage.visible = true;
				
				screen.visible = false;
				var timer:Timer = new Timer(1000, 1);
				timer.addEventListener(TimerEvent.TIMER, function():void {
					
					screen.visible = true;
					screen.bitmapData = screens.next;
				});
				timer.start();
			});
			addChild(takePicture);
		}
		
	}
	
}