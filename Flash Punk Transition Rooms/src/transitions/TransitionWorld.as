package transitions 
{
	import net.flashpunk.FP;
	import net.flashpunk.World;
	import util.Timer;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class TransitionWorld extends World 
	{
		private var _toWorld:World;				private function get toWorld():World { return _toWorld; }
		private var _fromWorld:World;			private function get fromWorld():World { return _fromWorld; }
		
		private var _transitionTimer:Timer;		private function get timer():Timer { return _transitionTimer; }
		
		public function TransitionWorld(from:World, to:World, timeInSeconds:Number)
		{
			_toWorld = to;
			_fromWorld = from;
			_transitionTimer = new Timer(timeInSeconds);
		}
		
		override public function update():void 
		{
			super.update();
			
			trace(timer.percentElapsed());
			
			timer.update();			
			if (timer.hasFired()) FP.world = toWorld;
		}
	}

}