package transitions.transitions 
{
	import net.flashpunk.FP;
	import net.flashpunk.utils.Draw;
	import transitions.Transitioner;
	import util.Timer;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class FadeInAndOut extends Transitioner 
	{
		private var _color:uint;		private function get color():uint { return _color; }
		
		public function FadeInAndOut(color:uint = 0xFFFFFF) {
			
			_color = color;
		}
		
		override public function render():void 
		{
			var opacity:Number = 1 - Math.abs(transitionTimer.percentElapsed - 0.5) / 0.5;
			
			Draw.rect(FP.camera.x, FP.camera.y, FP.width, FP.height, color, opacity);
		}
	}
}