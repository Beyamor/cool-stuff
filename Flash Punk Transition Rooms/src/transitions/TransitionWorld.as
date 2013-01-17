package transitions 
{
	import net.flashpunk.Entity;
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
		
		public function TransitionWorld(from:World, to:World, timeInSeconds:Number, effect:Transitioner=null)
		{
			_toWorld = to;
			_fromWorld = from;
			_transitionTimer = new Timer(timeInSeconds);
			
			if (effect) {
				
				effect.transitionTimer = timer;
				add(effect);
			}
		}
		
		override public function update():void 
		{
			super.update();
			
			trace(timer.percentElapsed());
			
			timer.update();			
			if (timer.hasFired()) FP.world = toWorld;
		}
		
		override public function render():void 
		{
			if (timer.percentElapsed() < 0.5) {
				
				fromWorld.render();
			}
			
			else {
				
				toWorld.render();
			}
			
			super.render();
		}
	}

}