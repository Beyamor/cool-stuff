package transitions 
{
	import net.flashpunk.Entity;
	
	/**
	 * ...
	 * @author beyamor
	 */
	public class Transitioner extends Entity 
	{
		
		public function Transitioner(fromEntities:Vector.<Entity>, toEntities:Vector.<Entity>)
		{
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