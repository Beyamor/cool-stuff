package transitions 
{
	import net.flashpunk.Entity;
	import util.Timer;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class Transitioner extends Entity 
	{
		public var fromEntities:Vector.<Entity>;
		public var toEntities:Vector.<Entity>;
		public var transitionTimer:Timer;
		
		public function init():void {
			
			var entity:Entity;
			
			for each (entity in fromEntities) {
				
				if (entity.layer < layer) layer = entity.layer - 1;
			}
			
			for each (entity in toEntities) {
				
				if (entity.layer < layer) layer = entity.layer - 1;
			}
		}
	}

}