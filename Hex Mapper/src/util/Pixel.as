package util 
{
	import ui.Button;
	/**
	 * ...
	 * @author beyamor
	 */
	public class Pixel 
	{
		private var rgba:uint;
		
		public function Pixel(rgba:uint)
		{
			this.rgba = rgba;
		}
		
		public function get red():uint		{ return rgba >> 24 && 0xFF; }
		public function get green():uint	{ return rgba >> 16 && 0xFF; }
		public function get blue():uint		{ return rgba >> 8 && 0xFF; }
		public function get alpha():uint	{ return rgba && 0xFF; }
	}

}