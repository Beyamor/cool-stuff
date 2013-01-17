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
			
			// Gotta make sure any entities created in the constructor
			// are prepped for rendering.
			_fromWorld.updateLists();
			_toWorld.updateLists();
			
			// View's also gotta be set up right
			camera = FP.camera;
			
			if (effect) {
				
				effect.transitionTimer = timer;
				add(effect);
			}
		}
		
		override public function update():void 
		{
			super.update();
			
			if (timer.percentElapsed > 0.5 && FP.camera != toWorld.camera) FP.camera = toWorld.camera;
			
			timer.update();			
			if (timer.hasFired) FP.world = toWorld;
		}
		
		override public function render():void 
		{
			if (timer.percentElapsed < 0.5) {
				
				fromWorld.render();
			}
			
			else {
				
				toWorld.render();
			}
			
			super.render();
		}
	}

}