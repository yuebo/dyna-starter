!function ($) {

    $.fn.jsoneditor=function(options){
        this.each(function(){
            $(this).after($("<div class='jsoncontainer'></div>"));
            $this=$(this).siblings("div.jsoncontainer")[0];
//            $($this).css("width",(options.width || '500'))
//            $($this).css("height",(options.height || '500'))
            $($this).css("width","100%");
            $($this).css("height",$(this).css("height"));
            $this.editor=null;
            var $el=$(this);
            $el.hide();
            var _option = {
                mode: 'code',
                modes: ['code', 'form', 'text', 'tree', 'view'], // allowed modes
                error: function (err) {
                    alert(err.toString());
                },
                change: function(){
                    $el.val($this.editor.getText());
                }
            };
            if ($el.val() == "") {
            	 $this.editor = new JSONEditor($this, _option, "");
            } else { 
            	var content = null;
            	 try{
            		 content = JSON.parse($el.val());
            	 } catch (e) {
            		 content = $el.val();
            	 }
            	 $this.editor = new JSONEditor($this, _option, {});
            }
            $this.editor.setText($el.val());
            $this.editor.format();
        })
    }
}(window.jQuery);