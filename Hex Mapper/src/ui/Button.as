// Yoink: http://flashpunk.net/forums/index.php?topic=1209.0

package ui
{
	import flash.events.MouseEvent;
	
	import net.flashpunk.Entity;
	import net.flashpunk.FP;
	import net.flashpunk.Graphic;
	import net.flashpunk.Mask;
	import net.flashpunk.graphics.Graphiclist;
	import net.flashpunk.utils.Input;
	
	/**
	 * A button class that allows to insert an entity which will call a function when pressed.
	 *  
	 * @author Rolpege
	 */	
	public class Button extends Entity
	{
		/**
		 * This function will be called when the button is pressed. 
		 */		
		public var callback:Function = null;
		/**
		 * This function will be called when the mouse overs the button. 
		 */		
		public var overCall:Function = null;
		private var overCalled:Boolean = false;
			
		private var initialized:Boolean = false;
				
		private var _normal:Graphic = new Graphic;
		private var _hover:Graphic = new Graphic;
		private var _down:Graphic = new Graphic;
		private var _inactive:Graphic = new Graphic;
		
		private var _normalChanged:Boolean = false;
		private var _hoverChanged:Boolean = false;
		private var _downChanged:Boolean = false;
		private var _inactiveChanged:Boolean = false;
		
		/**
		 * This var manages if the button is inactive or not. 
		 */		
		public var shouldCall:Boolean = true;
		
		/**
		 * Constructor.
		 *  
		 * @param x			X coordinate of the button.
		 * @param y			Y coordinate of the button.
		 * @param width		With of the button's hitbox.
		 * @param height	Height of the button's hitbox.
		 * @param callback	The function that will be called when the button is pressed.
		 * 
		 */		
		public function Button(x:Number=0, y:Number=0, width:int=0, height:int=0, callback:Function=null)
		{
			super(x, y);
			
			setHitbox(width, height);
			
			this.callback = callback;
			graphic = normal;
		}
		
		/**
		 * @private 
		 */		
		override public function update():void
		{
			if(!initialized)
			{
				if(FP.stage != null)
				{
					FP.stage.addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
					initialized = true;
				}
			}
			
			super.update();
			
			if(!shouldCall)
			{
				if(graphic != _inactive || _inactiveChanged)
				{
					graphic = _inactive;
					_inactiveChanged = false;
				}
			}
			else if(collidePoint(x, y, Input.mouseX, Input.mouseY))
			{
				if(Input.mouseDown)
				{
					if(graphic != _down || _downChanged)
					{
						graphic = _down;
						_downChanged = false;
					}
				}
				else if(graphic != _hover || _hoverChanged)
				{
					graphic = _hover;
					_hoverChanged = false;
					
					if(!overCalled)
					{
						if(overCall != null) overCall();
						overCalled = true;
					}
				}
			}
			else if(graphic != _normal || _normalChanged)
			{
				graphic = _normal;
				_normalChanged = false;
				overCalled = false;
			}
		}
		
		private function onMouseUp(e:MouseEvent=null):void
		{
			if(!shouldCall || !Input.mouseReleased || (callback == null)) return;
			if(collidePoint(x - FP.camera.x, y - FP.camera.y, Input.mouseX, Input.mouseY)) callback();
		}
		
		/** 
		 * @private
		 */		
		override public function removed():void
		{
			super.removed();
			
			if(FP.stage != null)
				FP.stage.removeEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}
		
		public function set normal(normal:Graphic):void
		{
			_normal = normal;
			_normalChanged = true;
		}
		
		public function set hover(hover:Graphic):void
		{
			_hover = hover;
			_hoverChanged = true;
		}
		
		public function set down(down:Graphic):void
		{
			_down = down;
			_downChanged = true;
		}
		
		public function set inactive(inactive:Graphic):void
		{
			_inactive = inactive;
			_inactiveChanged = true;
		}
		
		/**
		 * Graphic of the button when active and not pressed nor overed.
		 */		
		public function get normal():Graphic{ return _normal; }
		/**
		 * Graphic of the button when the mouse overs it and it's active.
		 */		
		public function get hover():Graphic{ return _hover; }
		/**
		 * Graphic of the button when the mouse is pressing it and it's active.
		 */		
		public function get down():Graphic{ return _down; }
		/**
		 * Graphic of the button when inactive.
		 * 
		 * @see #shouldCall
		 */		
		public function get inactive():Graphic{ return _inactive; }
	}
}