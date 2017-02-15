/*packaged at 2016-08-25 14:21:18*/
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\bootstrap\js\bs_tooltip.js
*/
/* ========================================================================
 * Bootstrap: tooltip.js v3.2.0
 * http://getbootstrap.com/javascript/#tooltip
 * Inspired by the original jQuery.tipsy by Jason Frame
 * ========================================================================
 * Copyright 2011-2014 Twitter, Inc.
 * Licensed under MIT (https://github.com/twbs/bootstrap/blob/master/LICENSE)
 * ======================================================================== */


+function ($) {
  'use strict';

  // TOOLTIP PUBLIC CLASS DEFINITION
  // ===============================

  var Tooltip = function (element, options) {
    this.type       =
    this.options    =
    this.enabled    =
    this.timeout    =
    this.hoverState =
    this.$element   = null

    this.init('tooltip', element, options)
  }

  Tooltip.VERSION  = '3.2.0'

  Tooltip.DEFAULTS = {
    animation: true,
    placement: 'top',
    selector: false,
    template: '<div class="tooltip" role="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>',
    trigger: 'hover focus',
    title: '',
    delay: 0,
    html: false,
    container: false,
    viewport: {
      selector: 'body',
      padding: 0
    }
  }

  Tooltip.prototype.init = function (type, element, options) {
    this.enabled   = true
    this.type      = type
    this.$element  = $(element)
    this.options   = this.getOptions(options)
    this.$viewport = this.options.viewport && $(this.options.viewport.selector || this.options.viewport)

    var triggers = this.options.trigger.split(' ')

    for (var i = triggers.length; i--;) {
      var trigger = triggers[i]

      if (trigger == 'click') {
        this.$element.on('click.' + this.type, this.options.selector, $.proxy(this.toggle, this))
      } else if (trigger != 'manual') {
        var eventIn  = trigger == 'hover' ? 'mouseenter' : 'focusin'
        var eventOut = trigger == 'hover' ? 'mouseleave' : 'focusout'

        this.$element.on(eventIn  + '.' + this.type, this.options.selector, $.proxy(this.enter, this))
        this.$element.on(eventOut + '.' + this.type, this.options.selector, $.proxy(this.leave, this))
      }
    }

    this.options.selector ?
      (this._options = $.extend({}, this.options, { trigger: 'manual', selector: '' })) :
      this.fixTitle()
  }

  Tooltip.prototype.getDefaults = function () {
    return Tooltip.DEFAULTS
  }

  Tooltip.prototype.getOptions = function (options) {
    options = $.extend({}, this.getDefaults(), this.$element.data(), options)

    if (options.delay && typeof options.delay == 'number') {
      options.delay = {
        show: options.delay,
        hide: options.delay
      }
    }

    return options
  }

  Tooltip.prototype.getDelegateOptions = function () {
    var options  = {}
    var defaults = this.getDefaults()

    this._options && $.each(this._options, function (key, value) {
      if (defaults[key] != value) options[key] = value
    })

    return options
  }

  Tooltip.prototype.enter = function (obj) {
    var self = obj instanceof this.constructor ?
      obj : $(obj.currentTarget).data('bs.' + this.type)

    if (!self) {
      self = new this.constructor(obj.currentTarget, this.getDelegateOptions())
      $(obj.currentTarget).data('bs.' + this.type, self)
    }

    clearTimeout(self.timeout)

    self.hoverState = 'in'

    if (!self.options.delay || !self.options.delay.show) return self.show()

    self.timeout = setTimeout(function () {
      if (self.hoverState == 'in') self.show()
    }, self.options.delay.show)
  }

  Tooltip.prototype.leave = function (obj) {
    var self = obj instanceof this.constructor ?
      obj : $(obj.currentTarget).data('bs.' + this.type)

    if (!self) {
      self = new this.constructor(obj.currentTarget, this.getDelegateOptions())
      $(obj.currentTarget).data('bs.' + this.type, self)
    }

    clearTimeout(self.timeout)

    self.hoverState = 'out'

    if (!self.options.delay || !self.options.delay.hide) return self.hide()

    self.timeout = setTimeout(function () {
      if (self.hoverState == 'out') self.hide()
    }, self.options.delay.hide)
  }

  Tooltip.prototype.show = function () {
    var e = $.Event('show.bs.' + this.type)

    if (this.hasContent() && this.enabled) {
      this.$element.trigger(e)

      var inDom = $.contains(document.documentElement, this.$element[0])
      if (e.isDefaultPrevented() || !inDom) return
      var that = this

      var $tip = this.tip()

      var tipId = this.getUID(this.type)

      this.setContent()
      $tip.attr('id', tipId)
      this.$element.attr('aria-describedby', tipId)

      if (this.options.animation) $tip.addClass('fade')

      var placement = typeof this.options.placement == 'function' ?
        this.options.placement.call(this, $tip[0], this.$element[0]) :
        this.options.placement

      var autoToken = /\s?auto?\s?/i
      var autoPlace = autoToken.test(placement)
      if (autoPlace) placement = placement.replace(autoToken, '') || 'top'

      $tip
        .detach()
        .css({ top: 0, left: 0, display: 'block' })
        .addClass(placement)
        .data('bs.' + this.type, this)

      this.options.container ? $tip.appendTo(this.options.container) : $tip.insertAfter(this.$element)

      var pos          = this.getPosition()
      var actualWidth  = $tip[0].offsetWidth
      var actualHeight = $tip[0].offsetHeight

      if (autoPlace) {
        var orgPlacement = placement
        var $parent      = this.$element.parent()
        var parentDim    = this.getPosition($parent)

        placement = placement == 'bottom' && pos.top   + pos.height       + actualHeight - parentDim.scroll > parentDim.height ? 'top'    :
                    placement == 'top'    && pos.top   - parentDim.scroll - actualHeight < 0                                   ? 'bottom' :
                    placement == 'right'  && pos.right + actualWidth      > parentDim.width                                    ? 'left'   :
                    placement == 'left'   && pos.left  - actualWidth      < parentDim.left                                     ? 'right'  :
                    placement

        $tip
          .removeClass(orgPlacement)
          .addClass(placement)
      }

      var calculatedOffset = this.getCalculatedOffset(placement, pos, actualWidth, actualHeight)

      this.applyPlacement(calculatedOffset, placement)

      var complete = function () {
        that.$element.trigger('shown.bs.' + that.type)
        that.hoverState = null
      }

      $.support.transition && this.$tip.hasClass('fade') ?
        $tip
          .one('bsTransitionEnd', complete)
          .emulateTransitionEnd(150) :
        complete()
    }
  }

  Tooltip.prototype.applyPlacement = function (offset, placement) {
    var $tip   = this.tip()
    var width  = $tip[0].offsetWidth
    var height = $tip[0].offsetHeight

    // manually read margins because getBoundingClientRect includes difference
    var marginTop = parseInt($tip.css('margin-top'), 10)
    var marginLeft = parseInt($tip.css('margin-left'), 10)

    // we must check for NaN for ie 8/9
    if (isNaN(marginTop))  marginTop  = 0
    if (isNaN(marginLeft)) marginLeft = 0

    offset.top  = offset.top  + marginTop
    offset.left = offset.left + marginLeft

    // $.fn.offset doesn't round pixel values
    // so we use setOffset directly with our own function B-0
    $.offset.setOffset($tip[0], $.extend({
      using: function (props) {
        $tip.css({
          top: Math.round(props.top),
          left: Math.round(props.left)
        })
      }
    }, offset), 0)

    $tip.addClass('in')

    // check to see if placing tip in new offset caused the tip to resize itself
    var actualWidth  = $tip[0].offsetWidth
    var actualHeight = $tip[0].offsetHeight

    if (placement == 'top' && actualHeight != height) {
      offset.top = offset.top + height - actualHeight
    }

    var delta = this.getViewportAdjustedDelta(placement, offset, actualWidth, actualHeight)

    if (delta.left) offset.left += delta.left
    else offset.top += delta.top

    var arrowDelta          = delta.left ? delta.left * 2 - width + actualWidth : delta.top * 2 - height + actualHeight
    var arrowPosition       = delta.left ? 'left'        : 'top'
    var arrowOffsetPosition = delta.left ? 'offsetWidth' : 'offsetHeight'

    $tip.offset(offset)
    this.replaceArrow(arrowDelta, $tip[0][arrowOffsetPosition], arrowPosition)
  }

  Tooltip.prototype.replaceArrow = function (delta, dimension, position) {
    this.arrow().css(position, delta ? (50 * (1 - delta / dimension) + '%') : '')
  }

  Tooltip.prototype.setContent = function () {
    var $tip  = this.tip()
    var title = this.getTitle()

    $tip.find('.tooltip-inner')[this.options.html ? 'html' : 'text'](title)
    $tip.removeClass('fade in top bottom left right')
  }

  Tooltip.prototype.hide = function () {
    var that = this
    var $tip = this.tip()
    var e    = $.Event('hide.bs.' + this.type)

    this.$element.removeAttr('aria-describedby')

    function complete() {
      if (that.hoverState != 'in') $tip.detach()
      that.$element.trigger('hidden.bs.' + that.type)
    }

    this.$element.trigger(e)

    if (e.isDefaultPrevented()) return

    $tip.removeClass('in')

    $.support.transition && this.$tip.hasClass('fade') ?
      $tip
        .one('bsTransitionEnd', complete)
        .emulateTransitionEnd(150) :
      complete()

    this.hoverState = null

    return this
  }

  Tooltip.prototype.fixTitle = function () {
    var $e = this.$element
    if ($e.attr('title') || typeof ($e.attr('data-original-title')) != 'string') {
      $e.attr('data-original-title', $e.attr('title') || '').attr('title', '')
    }
  }

  Tooltip.prototype.hasContent = function () {
    return this.getTitle()
  }

  Tooltip.prototype.getPosition = function ($element) {
    $element   = $element || this.$element
    var el     = $element[0]
    var isBody = el.tagName == 'BODY'
    return $.extend({}, (typeof el.getBoundingClientRect == 'function') ? el.getBoundingClientRect() : null, {
      scroll: isBody ? document.documentElement.scrollTop || document.body.scrollTop : $element.scrollTop(),
      width:  isBody ? $(window).width()  : $element.outerWidth(),
      height: isBody ? $(window).height() : $element.outerHeight()
    }, isBody ? { top: 0, left: 0 } : $element.offset())
  }

  Tooltip.prototype.getCalculatedOffset = function (placement, pos, actualWidth, actualHeight) {
    return placement == 'bottom' ? { top: pos.top + pos.height,   left: pos.left + pos.width / 2 - actualWidth / 2  } :
           placement == 'top'    ? { top: pos.top - actualHeight, left: pos.left + pos.width / 2 - actualWidth / 2  } :
           placement == 'left'   ? { top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left - actualWidth } :
        /* placement == 'right' */ { top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left + pos.width   }

  }

  Tooltip.prototype.getViewportAdjustedDelta = function (placement, pos, actualWidth, actualHeight) {
    var delta = { top: 0, left: 0 }
    if (!this.$viewport) return delta

    var viewportPadding = this.options.viewport && this.options.viewport.padding || 0
    var viewportDimensions = this.getPosition(this.$viewport)

    if (/right|left/.test(placement)) {
      var topEdgeOffset    = pos.top - viewportPadding - viewportDimensions.scroll
      var bottomEdgeOffset = pos.top + viewportPadding - viewportDimensions.scroll + actualHeight
      if (topEdgeOffset < viewportDimensions.top) { // top overflow
        delta.top = viewportDimensions.top - topEdgeOffset
      } else if (bottomEdgeOffset > viewportDimensions.top + viewportDimensions.height) { // bottom overflow
        delta.top = viewportDimensions.top + viewportDimensions.height - bottomEdgeOffset
      }
    } else {
      var leftEdgeOffset  = pos.left - viewportPadding
      var rightEdgeOffset = pos.left + viewportPadding + actualWidth
      if (leftEdgeOffset < viewportDimensions.left) { // left overflow
        delta.left = viewportDimensions.left - leftEdgeOffset
      } else if (rightEdgeOffset > viewportDimensions.width) { // right overflow
        delta.left = viewportDimensions.left + viewportDimensions.width - rightEdgeOffset
      }
    }

    return delta
  }

  Tooltip.prototype.getTitle = function () {
    var title
    var $e = this.$element
    var o  = this.options

    title = $e.attr('data-original-title')
      || (typeof o.title == 'function' ? o.title.call($e[0]) :  o.title)

    return title
  }

  Tooltip.prototype.getUID = function (prefix) {
    do prefix += ~~(Math.random() * 1000000)
    while (document.getElementById(prefix))
    return prefix
  }

  Tooltip.prototype.tip = function () {
    return (this.$tip = this.$tip || $(this.options.template))
  }

  Tooltip.prototype.arrow = function () {
    return (this.$arrow = this.$arrow || this.tip().find('.tooltip-arrow'))
  }

  Tooltip.prototype.validate = function () {
    if (!this.$element[0].parentNode) {
      this.hide()
      this.$element = null
      this.options  = null
    }
  }

  Tooltip.prototype.enable = function () {
    this.enabled = true
  }

  Tooltip.prototype.disable = function () {
    this.enabled = false
  }

  Tooltip.prototype.toggleEnabled = function () {
    this.enabled = !this.enabled
  }

  Tooltip.prototype.toggle = function (e) {
    var self = this
    if (e) {
      self = $(e.currentTarget).data('bs.' + this.type)
      if (!self) {
        self = new this.constructor(e.currentTarget, this.getDelegateOptions())
        $(e.currentTarget).data('bs.' + this.type, self)
      }
    }

    self.tip().hasClass('in') ? self.leave(self) : self.enter(self)
  }

  Tooltip.prototype.destroy = function () {
    clearTimeout(this.timeout)
    this.hide().$element.off('.' + this.type).removeData('bs.' + this.type)
  }


  // TOOLTIP PLUGIN DEFINITION
  // =========================

  function Plugin(option) {
    return this.each(function () {
      var $this   = $(this)
      var data    = $this.data('bs.tooltip')
      var options = typeof option == 'object' && option

      if (!data && option == 'destroy') return
      if (!data) $this.data('bs.tooltip', (data = new Tooltip(this, options)))
      if (typeof option == 'string') data[option]()
    })
  }

  var old = $.fn.tooltip

  $.fn.tooltip             = Plugin
  $.fn.tooltip.Constructor = Tooltip


  // TOOLTIP NO CONFLICT
  // ===================

  $.fn.tooltip.noConflict = function () {
    $.fn.tooltip = old
    return this
  }

}(jQuery);

/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\thirdparty\js\template-debug.js
*/
/*!
 * artTemplate - Template Engine
 * https://github.com/aui/artTemplate
 * Released under the MIT, BSD, and GPL Licenses
 */
 
!(function () {


/**
 * 模板引擎
 * @name    template
 * @param   {String}            模板名
 * @param   {Object, String}    数据。如果为字符串则编译并缓存编译结果
 * @return  {String, Function}  渲染好的HTML字符串或者渲染方法
 */
var template = function (filename, content) {
    return typeof content === 'string'
    ?   compile(content, {
            filename: filename
        })
    :   renderFile(filename, content);
};


template.version = '3.0.0';


/**
 * 设置全局配置
 * @name    template.config
 * @param   {String}    名称
 * @param   {Any}       值
 */
template.config = function (name, value) {
    defaults[name] = value;
};



var defaults = template.defaults = {
    openTag: '<%',    // 逻辑语法开始标签
    closeTag: '%>',   // 逻辑语法结束标签
    escape: true,     // 是否编码输出变量的 HTML 字符
    cache: true,      // 是否开启缓存（依赖 options 的 filename 字段）
    compress: false,  // 是否压缩输出
    parser: null      // 自定义语法格式器 @see: template-syntax.js
};


var cacheStore = template.cache = {};


/**
 * 渲染模板
 * @name    template.render
 * @param   {String}    模板
 * @param   {Object}    数据
 * @return  {String}    渲染好的字符串
 */
template.render = function (source, options) {
    return compile(source, options);
};


/**
 * 渲染模板(根据模板名)
 * @name    template.render
 * @param   {String}    模板名
 * @param   {Object}    数据
 * @return  {String}    渲染好的字符串
 */
var renderFile = template.renderFile = function (filename, data) {
    var fn = template.get(filename) || showDebugInfo({
        filename: filename,
        name: 'Render Error',
        message: 'Template not found'
    });
    return data ? fn(data) : fn;
};


/**
 * 获取编译缓存（可由外部重写此方法）
 * @param   {String}    模板名
 * @param   {Function}  编译好的函数
 */
template.get = function (filename) {

    var cache;
    
    if (cacheStore[filename]) {
        // 使用内存缓存
        cache = cacheStore[filename];
    } else if (typeof document === 'object') {
        // 加载模板并编译
        var elem = document.getElementById(filename);
        
        if (elem) {
            var source = (elem.value || elem.innerHTML)
            .replace(/^\s*|\s*$/g, '');
            cache = compile(source, {
                filename: filename
            });
        }
    }

    return cache;
};


var toString = function (value, type) {

    if (typeof value !== 'string') {

        type = typeof value;
        if (type === 'number') {
            value += '';
        } else if (type === 'function') {
            value = toString(value.call(value));
        } else {
            value = '';
        }
    }

    return value;

};


var escapeMap = {
    "<": "&#60;",
    ">": "&#62;",
    '"': "&#34;",
    "'": "&#39;",
    "&": "&#38;"
};


var escapeFn = function (s) {
    return escapeMap[s];
};

var escapeHTML = function (content) {
    return toString(content)
    .replace(/&(?![\w#]+;)|[<>"']/g, escapeFn);
};


var isArray = Array.isArray || function (obj) {
    return ({}).toString.call(obj) === '[object Array]';
};


var each = function (data, callback) {
    var i, len;        
    if (isArray(data)) {
        for (i = 0, len = data.length; i < len; i++) {
            callback.call(data, data[i], i, data);
        }
    } else {
        for (i in data) {
            callback.call(data, data[i], i);
        }
    }
};


var utils = template.utils = {

	$helpers: {},

    $include: renderFile,

    $string: toString,

    $escape: escapeHTML,

    $each: each
    
};/**
 * 添加模板辅助方法
 * @name    template.helper
 * @param   {String}    名称
 * @param   {Function}  方法
 */
template.helper = function (name, helper) {
    helpers[name] = helper;
};

var helpers = template.helpers = utils.$helpers;




/**
 * 模板错误事件（可由外部重写此方法）
 * @name    template.onerror
 * @event
 */
template.onerror = function (e) {
    var message = 'Template Error\n\n';
    for (var name in e) {
        message += '<' + name + '>\n' + e[name] + '\n\n';
    }
    
    if (typeof console === 'object') {
        console.error(message);
    }
};


// 模板调试器
var showDebugInfo = function (e) {

    template.onerror(e);
    
    return function () {
        return '{Template Error}';
    };
};


/**
 * 编译模板
 * 2012-6-6 @TooBug: define 方法名改为 compile，与 Node Express 保持一致
 * @name    template.compile
 * @param   {String}    模板字符串
 * @param   {Object}    编译选项
 *
 *      - openTag       {String}
 *      - closeTag      {String}
 *      - filename      {String}
 *      - escape        {Boolean}
 *      - compress      {Boolean}
 *      - debug         {Boolean}
 *      - cache         {Boolean}
 *      - parser        {Function}
 *
 * @return  {Function}  渲染方法
 */
var compile = template.compile = function (source, options) {
    
    // 合并默认配置
    options = options || {};
    for (var name in defaults) {
        if (options[name] === undefined) {
            options[name] = defaults[name];
        }
    }


    var filename = options.filename;


    try {
        
        var Render = compiler(source, options);
        
    } catch (e) {
    
        e.filename = filename || 'anonymous';
        e.name = 'Syntax Error';

        return showDebugInfo(e);
        
    }
    
    
    // 对编译结果进行一次包装

    function render (data) {
        
        try {
            
            return new Render(data, filename) + '';
            
        } catch (e) {
            
            // 运行时出错后自动开启调试模式重新编译
            if (!options.debug) {
                options.debug = true;
                return compile(source, options)(data);
            }
            
            return showDebugInfo(e)();
            
        }
        
    }
    

    render.prototype = Render.prototype;
    render.toString = function () {
        return Render.toString();
    };


    if (filename && options.cache) {
        cacheStore[filename] = render;
    }

    
    return render;

};




// 数组迭代
var forEach = utils.$each;


// 静态分析模板变量
var KEYWORDS =
    // 关键字
    'break,case,catch,continue,debugger,default,delete,do,else,false'
    + ',finally,for,function,if,in,instanceof,new,null,return,switch,this'
    + ',throw,true,try,typeof,var,void,while,with'

    // 保留字
    + ',abstract,boolean,byte,char,class,const,double,enum,export,extends'
    + ',final,float,goto,implements,import,int,interface,long,native'
    + ',package,private,protected,public,short,static,super,synchronized'
    + ',throws,transient,volatile'

    // ECMA 5 - use strict
    + ',arguments,let,yield'

    + ',undefined';

var REMOVE_RE = /\/\*[\w\W]*?\*\/|\/\/[^\n]*\n|\/\/[^\n]*$|"(?:[^"\\]|\\[\w\W])*"|'(?:[^'\\]|\\[\w\W])*'|\s*\.\s*[$\w\.]+/g;
var SPLIT_RE = /[^\w$]+/g;
var KEYWORDS_RE = new RegExp(["\\b" + KEYWORDS.replace(/,/g, '\\b|\\b') + "\\b"].join('|'), 'g');
var NUMBER_RE = /^\d[^,]*|,\d[^,]*/g;
var BOUNDARY_RE = /^,+|,+$/g;
var SPLIT2_RE = /^$|,+/;


// 获取变量
function getVariable (code) {
    return code
    .replace(REMOVE_RE, '')
    .replace(SPLIT_RE, ',')
    .replace(KEYWORDS_RE, '')
    .replace(NUMBER_RE, '')
    .replace(BOUNDARY_RE, '')
    .split(SPLIT2_RE);
};


// 字符串转义
function stringify (code) {
    return "'" + code
    // 单引号与反斜杠转义
    .replace(/('|\\)/g, '\\$1')
    // 换行符转义(windows + linux)
    .replace(/\r/g, '\\r')
    .replace(/\n/g, '\\n') + "'";
}


function compiler (source, options) {
    
    var debug = options.debug;
    var openTag = options.openTag;
    var closeTag = options.closeTag;
    var parser = options.parser;
    var compress = options.compress;
    var escape = options.escape;
    

    
    var line = 1;
    var uniq = {$data:1,$filename:1,$utils:1,$helpers:1,$out:1,$line:1};
    


    var isNewEngine = ''.trim;// '__proto__' in {}
    var replaces = isNewEngine
    ? ["$out='';", "$out+=", ";", "$out"]
    : ["$out=[];", "$out.push(", ");", "$out.join('')"];

    var concat = isNewEngine
        ? "$out+=text;return $out;"
        : "$out.push(text);";
          
    var print = "function(){"
    +      "var text=''.concat.apply('',arguments);"
    +       concat
    +  "}";

    var include = "function(filename,data){"
    +      "data=data||$data;"
    +      "var text=$utils.$include(filename,data,$filename);"
    +       concat
    +   "}";

    var headerCode = "'use strict';"
    + "var $utils=this,$helpers=$utils.$helpers,"
    + (debug ? "$line=0," : "");
    
    var mainCode = replaces[0];

    var footerCode = "return new String(" + replaces[3] + ");"
    
    // html与逻辑语法分离
    forEach(source.split(openTag), function (code) {
        code = code.split(closeTag);
        
        var $0 = code[0];
        var $1 = code[1];
        
        // code: [html]
        if (code.length === 1) {
            
            mainCode += html($0);
         
        // code: [logic, html]
        } else {
            
            mainCode += logic($0);
            
            if ($1) {
                mainCode += html($1);
            }
        }
        

    });
    
    var code = headerCode + mainCode + footerCode;
    
    // 调试语句
    if (debug) {
        code = "try{" + code + "}catch(e){"
        +       "throw {"
        +           "filename:$filename,"
        +           "name:'Render Error',"
        +           "message:e.message,"
        +           "line:$line,"
        +           "source:" + stringify(source)
        +           ".split(/\\n/)[$line-1].replace(/^\\s+/,'')"
        +       "};"
        + "}";
    }
    
    
    
    try {
        
        
        var Render = new Function("$data", "$filename", code);
        Render.prototype = utils;

        return Render;
        
    } catch (e) {
        e.temp = "function anonymous($data,$filename) {" + code + "}";
        throw e;
    }



    
    // 处理 HTML 语句
    function html (code) {
        
        // 记录行号
        line += code.split(/\n/).length - 1;

        // 压缩多余空白与注释
        if (compress) {
            code = code
            .replace(/\s+/g, ' ')
            .replace(/<!--[\w\W]*?-->/g, '');
        }
        
        if (code) {
            code = replaces[1] + stringify(code) + replaces[2] + "\n";
        }

        return code;
    }
    
    
    // 处理逻辑语句
    function logic (code) {

        var thisLine = line;
       
        if (parser) {
        
             // 语法转换插件钩子
            code = parser(code, options);
            
        } else if (debug) {
        
            // 记录行号
            code = code.replace(/\n/g, function () {
                line ++;
                return "$line=" + line +  ";";
            });
            
        }
        
        
        // 输出语句. 编码: <%=value%> 不编码:<%=#value%>
        // <%=#value%> 等同 v2.0.3 之前的 <%==value%>
        if (code.indexOf('=') === 0) {

            var escapeSyntax = escape && !/^=[=#]/.test(code);

            code = code.replace(/^=[=#]?|[\s;]*$/g, '');

            // 对内容编码
            if (escapeSyntax) {

                var name = code.replace(/\s*\([^\)]+\)/, '');

                // 排除 utils.* | include | print
                
                if (!utils[name] && !/^(include|print)$/.test(name)) {
                    code = "$escape(" + code + ")";
                }

            // 不编码
            } else {
                code = "$string(" + code + ")";
            }
            

            code = replaces[1] + code + replaces[2];

        }
        
        if (debug) {
            code = "$line=" + thisLine + ";" + code;
        }
        
        // 提取模板中的变量名
        forEach(getVariable(code), function (name) {
            
            // name 值可能为空，在安卓低版本浏览器下
            if (!name || uniq[name]) {
                return;
            }

            var value;

            // 声明模板变量
            // 赋值优先级:
            // [include, print] > utils > helpers > data
            if (name === 'print') {

                value = print;

            } else if (name === 'include') {
                
                value = include;
                
            } else if (utils[name]) {

                value = "$utils." + name;

            } else if (helpers[name]) {

                value = "$helpers." + name;

            } else {

                value = "$data." + name;
            }
            
            headerCode += name + "=" + value + ",";
            uniq[name] = true;
            
            
        });
        
        return code + "\n";
    }
    
    
};



// 定义模板引擎的语法


defaults.openTag = '{{';
defaults.closeTag = '}}';


var filtered = function (js, filter) {
    var parts = filter.split(':');
    var name = parts.shift();
    var args = parts.join(':') || '';

    if (args) {
        args = ', ' + args;
    }

    return '$helpers.' + name + '(' + js + args + ')';
}


defaults.parser = function (code, options) {

    // var match = code.match(/([\w\$]*)(\b.*)/);
    // var key = match[1];
    // var args = match[2];
    // var split = args.split(' ');
    // split.shift();

    code = code.replace(/^\s/, '');

    var split = code.split(' ');
    var key = split.shift();
    var args = split.join(' ');

    

    switch (key) {

        case 'if':

            code = 'if(' + args + '){';
            break;

        case 'else':
            
            if (split.shift() === 'if') {
                split = ' if(' + split.join(' ') + ')';
            } else {
                split = '';
            }

            code = '}else' + split + '{';
            break;

        case '/if':

            code = '}';
            break;

        case 'each':
            
            var object = split[0] || '$data';
            var as     = split[1] || 'as';
            var value  = split[2] || '$value';
            var index  = split[3] || '$index';
            
            var param   = value + ',' + index;
            
            if (as !== 'as') {
                object = '[]';
            }
            
            code =  '$each(' + object + ',function(' + param + '){';
            break;

        case '/each':

            code = '});';
            break;

        case 'echo':

            code = 'print(' + args + ');';
            break;

        case 'print':
        case 'include':

            code = key + '(' + split.join(',') + ');';
            break;

        default:

            // 过滤器（辅助方法）
            // {{value | filterA:'abcd' | filterB}}
            // >>> $helpers.filterB($helpers.filterA(value, 'abcd'))
            // TODO: {{ddd||aaa}} 不包含空格
            if (/^\s*\|\s*[\w\$]/.test(args)) {

                var escape = true;

                // {{#value | link}}
                if (code.indexOf('#') === 0) {
                    code = code.substr(1);
                    escape = false;
                }

                var i = 0;
                var array = code.split('|');
                var len = array.length;
                var val = array[i++];

                for (; i < len; i ++) {
                    val = filtered(val, array[i]);
                }

                code = (escape ? '=' : '=#') + val;

            // 即将弃用 {{helperName value}}
            } else if (template.helpers[key]) {
                
                code = '=#' + key + '(' + split.join(',') + ');';
            
            // 内容直接输出 {{value}}
            } else {

                code = '=' + code;
            }

            break;
    }
    
    
    return code;
};



// RequireJS && SeaJS
if (typeof define === 'function') {
    define(function() {
        return template;
    });

// NodeJS
} else if (typeof exports !== 'undefined') {
    module.exports = template;
} else {
    this.template = template;
}

})();
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\thirdparty\js\respond.min.js
*/
/*! matchMedia() polyfill - Test a CSS media type/query in JS. Authors & copyright (c) 2012: Scott Jehl, Paul Irish, Nicholas Zakas. Dual MIT/BSD license */
/*! NOTE: If you're already including a window.matchMedia polyfill via Modernizr or otherwise, you don't need this part */
(function(w) {
  "use strict";
  w.matchMedia = w.matchMedia || function(doc, undefined) {
    var bool, docElem = doc.documentElement, refNode = docElem.firstElementChild || docElem.firstChild, fakeBody = doc.createElement("body"), div = doc.createElement("div");
    div.id = "mq-test-1";
    div.style.cssText = "position:absolute;top:-100em";
    fakeBody.style.background = "none";
    fakeBody.appendChild(div);
    return function(q) {
      div.innerHTML = '&shy;<style media="' + q + '"> #mq-test-1 { width: 42px; }</style>';
      docElem.insertBefore(fakeBody, refNode);
      bool = div.offsetWidth === 42;
      docElem.removeChild(fakeBody);
      return {
        matches: bool,
        media: q
      };
    };
  }(w.document);
})(this);

/*! Respond.js v1.4.0: min/max-width media query polyfill. (c) Scott Jehl. MIT Lic. j.mp/respondjs  */
(function(w) {
  "use strict";
  var respond = {};
  w.respond = respond;
  respond.update = function() {};
  var requestQueue = [], xmlHttp = function() {
    var xmlhttpmethod = false;
    try {
      xmlhttpmethod = new w.XMLHttpRequest();
    } catch (e) {
      xmlhttpmethod = new w.ActiveXObject("Microsoft.XMLHTTP");
    }
    return function() {
      return xmlhttpmethod;
    };
  }(), ajax = function(url, callback) {
    var req = xmlHttp();
    if (!req) {
      return;
    }
    req.open("GET", url, true);
    req.onreadystatechange = function() {
      if (req.readyState !== 4 || req.status !== 200 && req.status !== 304) {
        return;
      }
      callback(req.responseText);
    };
    if (req.readyState === 4) {
      return;
    }
    req.send(null);
  }, isUnsupportedMediaQuery = function(query) {
    return query.replace(respond.regex.minmaxwh, "").match(respond.regex.other);
  };
  respond.ajax = ajax;
  respond.queue = requestQueue;
  respond.unsupportedmq = isUnsupportedMediaQuery;
  respond.regex = {
    media: /@media[^\{]+\{([^\{\}]*\{[^\}\{]*\})+/gi,
    keyframes: /@(?:\-(?:o|moz|webkit)\-)?keyframes[^\{]+\{(?:[^\{\}]*\{[^\}\{]*\})+[^\}]*\}/gi,
    comments: /\/\*[^*]*\*+([^/][^*]*\*+)*\//gi,
    urls: /(url\()['"]?([^\/\)'"][^:\)'"]+)['"]?(\))/g,
    findStyles: /@media *([^\{]+)\{([\S\s]+?)$/,
    only: /(only\s+)?([a-zA-Z]+)\s?/,
    minw: /\(\s*min\-width\s*:\s*(\s*[0-9\.]+)(px|em)\s*\)/,
    maxw: /\(\s*max\-width\s*:\s*(\s*[0-9\.]+)(px|em)\s*\)/,
    minmaxwh: /\(\s*m(in|ax)\-(height|width)\s*:\s*(\s*[0-9\.]+)(px|em)\s*\)/gi,
    other: /\([^\)]*\)/g
  };
  respond.mediaQueriesSupported = w.matchMedia && w.matchMedia("only all") !== null && w.matchMedia("only all").matches;
  if (respond.mediaQueriesSupported) {
    return;
  }
  var doc = w.document, docElem = doc.documentElement, mediastyles = [], rules = [], appendedEls = [], parsedSheets = {}, resizeThrottle = 30, head = doc.getElementsByTagName("head")[0] || docElem, base = doc.getElementsByTagName("base")[0], links = head.getElementsByTagName("link"), lastCall, resizeDefer, eminpx, getEmValue = function() {
    var ret, div = doc.createElement("div"), body = doc.body, originalHTMLFontSize = docElem.style.fontSize, originalBodyFontSize = body && body.style.fontSize, fakeUsed = false;
    div.style.cssText = "position:absolute;font-size:1em;width:1em";
    if (!body) {
      body = fakeUsed = doc.createElement("body");
      body.style.background = "none";
    }
    docElem.style.fontSize = "100%";
    body.style.fontSize = "100%";
    body.appendChild(div);
    if (fakeUsed) {
      docElem.insertBefore(body, docElem.firstChild);
    }
    ret = div.offsetWidth;
    if (fakeUsed) {
      docElem.removeChild(body);
    } else {
      body.removeChild(div);
    }
    docElem.style.fontSize = originalHTMLFontSize;
    if (originalBodyFontSize) {
      body.style.fontSize = originalBodyFontSize;
    }
    ret = eminpx = parseFloat(ret);
    return ret;
  }, applyMedia = function(fromResize) {
    var name = "clientWidth", docElemProp = docElem[name], currWidth = doc.compatMode === "CSS1Compat" && docElemProp || doc.body[name] || docElemProp, styleBlocks = {}, lastLink = links[links.length - 1], now = new Date().getTime();
    if (fromResize && lastCall && now - lastCall < resizeThrottle) {
      w.clearTimeout(resizeDefer);
      resizeDefer = w.setTimeout(applyMedia, resizeThrottle);
      return;
    } else {
      lastCall = now;
    }
    for (var i in mediastyles) {
      if (mediastyles.hasOwnProperty(i)) {
        var thisstyle = mediastyles[i], min = thisstyle.minw, max = thisstyle.maxw, minnull = min === null, maxnull = max === null, em = "em";
        if (!!min) {
          min = parseFloat(min) * (min.indexOf(em) > -1 ? eminpx || getEmValue() : 1);
        }
        if (!!max) {
          max = parseFloat(max) * (max.indexOf(em) > -1 ? eminpx || getEmValue() : 1);
        }
        if (!thisstyle.hasquery || (!minnull || !maxnull) && (minnull || currWidth >= min) && (maxnull || currWidth <= max)) {
          if (!styleBlocks[thisstyle.media]) {
            styleBlocks[thisstyle.media] = [];
          }
          styleBlocks[thisstyle.media].push(rules[thisstyle.rules]);
        }
      }
    }
    for (var j in appendedEls) {
      if (appendedEls.hasOwnProperty(j)) {
        if (appendedEls[j] && appendedEls[j].parentNode === head) {
          head.removeChild(appendedEls[j]);
        }
      }
    }
    appendedEls.length = 0;
    for (var k in styleBlocks) {
      if (styleBlocks.hasOwnProperty(k)) {
        var ss = doc.createElement("style"), css = styleBlocks[k].join("\n");
        ss.type = "text/css";
        ss.media = k;
        head.insertBefore(ss, lastLink.nextSibling);
        if (ss.styleSheet) {
          ss.styleSheet.cssText = css;
        } else {
          ss.appendChild(doc.createTextNode(css));
        }
        appendedEls.push(ss);
      }
    }
  }, translate = function(styles, href, media) {
    var qs = styles.replace(respond.regex.comments, "").replace(respond.regex.keyframes, "").match(respond.regex.media), ql = qs && qs.length || 0;
    href = href.substring(0, href.lastIndexOf("/"));
    var repUrls = function(css) {
      return css.replace(respond.regex.urls, "$1" + href + "$2$3");
    }, useMedia = !ql && media;
    if (href.length) {
      href += "/";
    }
    if (useMedia) {
      ql = 1;
    }
    for (var i = 0; i < ql; i++) {
      var fullq, thisq, eachq, eql;
      if (useMedia) {
        fullq = media;
        rules.push(repUrls(styles));
      } else {
        fullq = qs[i].match(respond.regex.findStyles) && RegExp.$1;
        rules.push(RegExp.$2 && repUrls(RegExp.$2));
      }
      eachq = fullq.split(",");
      eql = eachq.length;
      for (var j = 0; j < eql; j++) {
        thisq = eachq[j];
        if (isUnsupportedMediaQuery(thisq)) {
          continue;
        }
        mediastyles.push({
          media: thisq.split("(")[0].match(respond.regex.only) && RegExp.$2 || "all",
          rules: rules.length - 1,
          hasquery: thisq.indexOf("(") > -1,
          minw: thisq.match(respond.regex.minw) && parseFloat(RegExp.$1) + (RegExp.$2 || ""),
          maxw: thisq.match(respond.regex.maxw) && parseFloat(RegExp.$1) + (RegExp.$2 || "")
        });
      }
    }
    applyMedia();
  }, makeRequests = function() {
    if (requestQueue.length) {
      var thisRequest = requestQueue.shift();
      ajax(thisRequest.href, function(styles) {
        translate(styles, thisRequest.href, thisRequest.media);
        parsedSheets[thisRequest.href] = true;
        w.setTimeout(function() {
          makeRequests();
        }, 0);
      });
    }
  }, ripCSS = function() {
    for (var i = 0; i < links.length; i++) {
      var sheet = links[i], href = sheet.href, media = sheet.media, isCSS = sheet.rel && sheet.rel.toLowerCase() === "stylesheet";
      if (!!href && isCSS && !parsedSheets[href]) {
        if (sheet.styleSheet && sheet.styleSheet.rawCssText) {
          translate(sheet.styleSheet.rawCssText, href, media);
          parsedSheets[href] = true;
        } else {
          if (!/^([a-zA-Z:]*\/\/)/.test(href) && !base || href.replace(RegExp.$1, "").split("/")[0] === w.location.host) {
            if (href.substring(0, 2) === "//") {
              href = w.location.protocol + href;
            }
            requestQueue.push({
              href: href,
              media: media
            });
          }
        }
      }
    }
    makeRequests();
  };
  ripCSS();
  respond.update = ripCSS;
  respond.getEmValue = getEmValue;
  function callMedia() {
    applyMedia(true);
  }
  if (w.addEventListener) {
    w.addEventListener("resize", callMedia, false);
  } else if (w.attachEvent) {
    w.attachEvent("onresize", callMedia);
  }
})(this);

/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\bootstrap\js\bs_button.js
*/
/* ========================================================================
 * Bootstrap: button.js v3.0.3
 * http://getbootstrap.com/javascript/#buttons
 * ========================================================================
 * Copyright 2013 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ======================================================================== */
$(document).ready(function(){
	$("button").on("mouseup",function(){
		$(this).blur();
	});
});

+function ($) { "use strict";

  // BUTTON PUBLIC CLASS DEFINITION
  // ==============================

  var Button = function (element, options) {
    this.$element = $(element)
    this.options  = $.extend({}, Button.DEFAULTS, options)
  }

  Button.DEFAULTS = {
    loadingText: 'loading...'
  }

  Button.prototype.setState = function (state) {
    var d    = 'disabled'
    var $el  = this.$element
    var val  = $el.is('input') ? 'val' : 'html'
    var data = $el.data()

    state = state + 'Text'

    if (!data.resetText) $el.data('resetText', $el[val]())

    $el[val](data[state] || this.options[state])

    // push to event loop to allow forms to submit
    setTimeout(function () {
      state == 'loadingText' ?
        $el.addClass(d).attr(d, d) :
        $el.removeClass(d).removeAttr(d);
    }, 0)
  }

  Button.prototype.toggle = function () {
    var $parent = this.$element.closest('[data-toggle="buttons"]')
    var changed = true

    if ($parent.length) {
      var $input = this.$element.find('input')
      if ($input.prop('type') === 'radio') {
        // see if clicking on current one
        if ($input.prop('checked') && this.$element.hasClass('active'))
          changed = false
        else
          $parent.find('.active').removeClass('active')
      }
      if (changed) $input.prop('checked', !this.$element.hasClass('active')).trigger('change')
    }

    if (changed) this.$element.toggleClass('active')
  }


  // BUTTON PLUGIN DEFINITION
  // ========================

  var old = $.fn.button

  $.fn.button = function (option) {
    return this.each(function () {
      var $this   = $(this)
      var data    = $this.data('bs.button')
      var options = typeof option == 'object' && option

      if (!data) $this.data('bs.button', (data = new Button(this, options)))

      if (option == 'toggle') data.toggle()
      else if (option) data.setState(option)

    })
  }

  $.fn.button.Constructor = Button


  // BUTTON NO CONFLICT
  // ==================

  $.fn.button.noConflict = function () {
    $.fn.button = old
    return this
  }


  // BUTTON DATA-API
  // ===============

  $(document).on('click.bs.button.data-api', '[data-toggle^=button]', function (e) {
    var $btn = $(e.target)
    if (!$btn.hasClass('btn')) $btn = $btn.closest('.btn')
    $btn.button('toggle')
    e.preventDefault()
  })

}(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\bootstrap\js\bs_menu.js
*/
/* ========================================================================
 * Bootstrap: dropdown.js v3.0.3
 * http://getbootstrap.com/javascript/#dropdowns
 * ========================================================================
 * Copyright 2013 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ======================================================================== */


+function ($) { "use strict";

  // DROPDOWN CLASS DEFINITION
  // =========================

  var backdrop = '.dropdown-backdrop'
  var toggle   = '[data-toggle=dropdown]'
  var Dropdown = function (element) {
    $(element).on('click.bs.dropdown', this.toggle)
  }

  Dropdown.prototype.toggle = function (e) {
    var $this = $(this)

    if ($this.is('.disabled, :disabled')) return

    var $parent  = getParent($this)
    var isActive = $parent.hasClass('open')

    clearMenus()

    if (!isActive) {
      if ('ontouchstart' in document.documentElement && !$parent.closest('.navbar-nav').length) {
        // if mobile we use a backdrop because click events don't delegate
        $('<div class="dropdown-backdrop"/>').insertAfter($(this)).on('click', clearMenus)
      }

      $parent.trigger(e = $.Event('show.bs.dropdown'))

      if (e.isDefaultPrevented()) return

      $parent
        .toggleClass('open')
        .trigger('shown.bs.dropdown')
      $this.focus()
    }

    return false
  }

  Dropdown.prototype.keydown = function (e) {
    if (!/(38|40|27)/.test(e.keyCode)) return

    var $this = $(this)

    e.preventDefault()
    e.stopPropagation()

    if ($this.is('.disabled, :disabled')) return

    var $parent  = getParent($this)
    var isActive = $parent.hasClass('open')

    if (!isActive || (isActive && e.keyCode == 27)) {
      if (e.which == 27) $parent.find(toggle).focus()
      return $this.click()
    }

    var $items = $('[role=menu] li:not(.divider):visible a', $parent)

    if (!$items.length) return

    var index = $items.index($items.filter(':focus'))

    if (e.keyCode == 38 && index > 0)                 index--                        // up
    if (e.keyCode == 40 && index < $items.length - 1) index++                        // down
    if (!~index)                                      index=0

    $items.eq(index).focus()
  }

  function clearMenus() {
    $(backdrop).remove()
    $(toggle).each(function (e) {
      var $parent = getParent($(this))
      if (!$parent.hasClass('open')) return
      $parent.trigger(e = $.Event('hide.bs.dropdown'))
      if (e.isDefaultPrevented()) return
      $parent.removeClass('open').trigger('hidden.bs.dropdown')
    })
  }

  function getParent($this) {
    var selector = $this.attr('data-target')

    if (!selector) {
      selector = $this.attr('href')
      selector = selector && /#/.test(selector) && selector.replace(/.*(?=#[^\s]*$)/, '') //strip for ie7
    }

    var $parent = selector && $(selector)

    return $parent && $parent.length ? $parent : $this.parent()
  }


  // DROPDOWN PLUGIN DEFINITION
  // ==========================

  var old = $.fn.dropdown

  $.fn.dropdown = function (option) {
    return this.each(function () {
      var $this = $(this)
      var data  = $this.data('bs.dropdown')

      if (!data) $this.data('bs.dropdown', (data = new Dropdown(this)))
      if (typeof option == 'string') data[option].call($this)
    })
  }

  $.fn.dropdown.Constructor = Dropdown


  // DROPDOWN NO CONFLICT
  // ====================

  $.fn.dropdown.noConflict = function () {
    $.fn.dropdown = old
    return this
  }


  // APPLY TO STANDARD DROPDOWN ELEMENTS
  // ===================================

  $(document)
    .on('click.bs.dropdown.data-api', clearMenus)
    .on('click.bs.dropdown.data-api', '.dropdown form', function (e) { e.stopPropagation() })
    .on('click.bs.dropdown.data-api'  , toggle, Dropdown.prototype.toggle)
    .on('keydown.bs.dropdown.data-api', toggle + ', [role=menu]' , Dropdown.prototype.keydown)

}(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_core.js
*/
var privMapping = window.privMapping || _parent().privMapping || null;
var itcui = itcui || {};
itcui.combo_displayed = false;
function ITC_GetAbsPos(obj)
{
	var o = $(obj);
	var obj_x = o.offset().left;
	var obj_y = o.offset().top;
	while(o.attr("parentNode"))
	{
		o = o.parent();
		obj_x += o.offset().left;
		obj_y += o.offset().top;
	}
	var p = {"left":obj_x,"top":obj_y};
	return p;
}

function isNull(val){
    return typeof(val)=="undefined" || val===null;
}

function _parent(){
	if(window.top == self){
		return window;
	}
	else{
		return window.parent;
	}
}

function _topmost(){
    if(window._xssEmbbed){
        return window;
    }
    var w = window;
    while(w.top != w){
        w = w.top;
    }
    return w;
}

function ITC_Len(str){
	var len = str.length;
	var reLen = 0; 
	    for (var i = 0; i < len; i++) {        
	        if (str.charCodeAt(i) < 27 || str.charCodeAt(i) > 126) { 
	            // 全角    
	            reLen += 2; 
	        } else { 
	            reLen++; 
	        } 
	    } 
	return reLen;  
}

Number.prototype.toFixed=function (d) { 
    var s=this+""; 
    if(!d)d=0; 
    if(s.indexOf(".")==-1)s+="."; 
    s+=new Array(d+1).join("0"); 
    if(new RegExp("^(-|\\+)?(\\d+(\\.\\d{0,"+(d+1)+"})?)\\d*$").test(s)){
        var s="0"+RegExp.$2,pm=RegExp.$1,a=RegExp.$3.length,b=true;
        if(a==d+2){
            a=s.match(/\d/g); 
            if(parseInt(a[a.length-1])>4){
                for(var i=a.length-2;i>=0;i--){
                    a[i]=parseInt(a[i])+1;
                    if(a[i]==10){
                        a[i]=0;
                        b=i!=1;
                    }else break;
                }
            }
            s=a.join("").replace(new RegExp("(\\d+)(\\d{"+d+"})\\d$"),"$1.$2");

        }if(b)s=s.substr(1); 
        return (pm+s).replace(/\.$/,"");
    }return this+"";

};

function ITC_Substr(str, startp, endp) {
    var i=0; c = 0; unicode=0; rstr = '';
    var len = str.length;
    var sblen = ITC_Len(str);

    if (startp < 0) {
        startp = sblen + startp;
    }

    if (endp < 1) {
        endp = sblen + endp;// - ((str.charCodeAt(len-1) < 127) ? 1 : 2);
    }
    // 寻找起点
    for(i = 0; i < len; i++) {
        if (c >= startp) {
            break;
        }
	    var unicode = str.charCodeAt(i);
		if (unicode < 127) {
			c += 1;
		} else {
			c += 2;
		}
	}
	// 开始取
	for(i = i; i < len; i++) {
	    var unicode = str.charCodeAt(i);
		if (unicode < 127) {
			c += 1;
		} else {
			c += 2;
		}
		rstr += str.charAt(i);
		if (c >= endp) {
		    break;
		}
	}
	return rstr;
}

function isArray(o) {
  return Object.prototype.toString.call(o) === '[object Array]'; 
}

Date.prototype.format =function(format){
	var o = {
	"M+" : this.getMonth()+1, //month
	"d+" : this.getDate(), //day
	"h+" : this.getHours(), //hour
	"m+" : this.getMinutes(), //minute
	"s+" : this.getSeconds(), //second
	"q+" : Math.floor((this.getMonth()+3)/3), //quarter
	"S" : this.getMilliseconds() //millisecond
	}
	if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
	(this.getFullYear()+"").substr(4- RegExp.$1.length));
	for(var k in o)if(new RegExp("("+ k +")").test(format))
	format = format.replace(RegExp.$1,
	RegExp.$1.length==1? o[k] :
	("00"+ o[k]).substr((""+ o[k]).length));
	return format;
}

//禁止退格键返回
function _banbackspace(e){   
    var ev = e || window.event;
    var obj = ev.target || ev.srcElement;
    var jqObj = $(obj);
    var t = obj.type || obj.getAttribute('type');
    var vReadOnly = obj.getAttribute('readonly');
    var vEnabled = obj.getAttribute('enabled');
    vReadOnly = (vReadOnly == null) ? false : vReadOnly;
    vEnabled = (vEnabled == null) ? true : vEnabled;    
    var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea") 
                && (vReadOnly==true || vEnabled!=true))?true:false;
   
    var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")
                ?true:false;        
    
    if((t=="text" || t=="password") && !jqObj.hasClass("enter-allowed") && ev.keyCode == 13){
        //防止表单中的回车键触发表单提交
        if(jqObj.parents("form").length > 0){
            e.preventDefault();
        }
        else{
            jqObj.addClass("enter-allowed");
        }
    }
    if(flag1 || flag2){
        return false;
    }    
}

document.onkeypress=_banbackspace;
document.onkeydown=_banbackspace; 

var formToken = new Date().getTime() + "" + Math.floor(Math.random()*100);

String.prototype.replaceAll = function(reallyDo, replaceWith, ignoreCase) {  
    if (!RegExp.prototype.isPrototypeOf(reallyDo)) {  
        return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi": "g")), replaceWith);  
    } else {  
        return this.replace(reallyDo, replaceWith);  
    }  
}

//解决flash析构的问题
_itc_unloadcallback = function(){
	document.getElementsByTagName("body")[0].innerHTML="";
};

/*window.onbeforeunload = _itc_unloadcallback;
window.onunload = _itc_unloadcallback;*/

/*文件上传公用函数*/
function _itc_getextname(fileName){
    var ext = fileName.toLowerCase().split(".");
    ext = ext[ext.length-1];
    var exts = ["jpg","png","gif","doc","docx","exe","dll","xls","xlsx","ppt","pptx","txt","zip","rar","7z","mp3","wav","pdf","tif","tiff","bmp"];
    var flag = false;
    for(var i=0;i<exts.length;i++){
        if(ext == exts[i]){
            flag = true;
            break;
        }
    }
    ext = flag?ext:"unknown";
    return ext;
}

function _itc_getfilesize(fileSize){
    fileSize = Math.round(fileSize / 1024);
    var suffix   = 'KB';
    if (fileSize > 1000) {
        fileSize = Math.round(fileSize / 1000);
        suffix   = 'MB';
    }
    var fileSizeParts = fileSize.toString().split('.');
    fileSize = fileSizeParts[0];
    if (fileSizeParts.length > 1) {
        fileSize += '.' + fileSizeParts[1].substr(0,2);
    }
    fileSize += suffix;
    return fileSize;
}

function _itc_binddownload(o){
    $(o).on("click",function(){
        var that = $(this);
        var fileWrap = that.parents(".itc_upload_wrap");
        var id = fileWrap.attr("fileID");
        var q = fileWrap.parent();
        var opts = q.data("options");
        var downUrl = opts.downloadFileUrl;
        if(downUrl.indexOf("?")>0){
            downUrl += "&"
        }
        //使用iframe方式下载文件 防止触发flash回调清理
        _itc_download(downUrl + "id=" + id)
    });
}

function _itc_binddel(o){
    $(o).on("click",function(){
        var that = $(this);
        var fileWrap = that.parents(".itc_upload_wrap");
        var msg = fileWrap.data("ajaxMsg") || fileWrap.attr("fileID");
        if(!msg){
            return;
        }
        var q = fileWrap.parent();
        var opts = q.data("options");
        if(opts.delFileAfterPost && fileWrap.attr("init")){
            fileWrap.remove();
            if(opts.singleFile){
                q.show();
            }
        }
        else{
            Notice.confirm("确认删除|是否删除该文件？该操作无法恢复。",function(){
                var ajaxOpts = {
                    "type" : "GET",
                    "dataType" : "json",
                    "error" : function(){
                        FW.error("文件删除失败");
                    },
                    "success" : function(data){
                        if(!data || data.status==1){
                            FW.success("文件删除成功");
                            fileWrap.remove();
                            if(opts.singleFile){
                                q.show();
                            }
                        }
                        else{
                            FW.error(data.msg || "文件删除失败");
                        }
                    }
                };
                if(opts.delFileUrl){
                    ajaxOpts.url = opts.delFileUrl;
                    ajaxOpts.data = {"id":msg};
                }
                else{
                    ajaxOpts.url = msg;
                }
                $.ajax(ajaxOpts);
            });
        }
    });
}
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_eventhandler.js
*/
ITCUI_EventHandler  = function(){
	var events = {};
	var invMapping = {};
	var data = {};
	var frameMapping = {};
	
	this.get = function(key){
		return data[key];
	}

	this.set = function(key,val,deepcopy){
		if(deepcopy){
			data[key] = JSON.parse(JSON.stringify(val));
		}
		else{
			data[key] = val;
		}
	}

	this.registerEvent = function(eventId,f,isFromTab){
		var evtList = events[eventId];
		if(!evtList){
			events[eventId] = [];
			evtList = events[eventId];
		}
		var evtNo = (new Date().getTime()%100000) + "" + Math.abs(Math.round(Math.random()*1000));
		evtList.push([f,evtNo])
		invMapping[evtNo] = eventId;
		if(isFromTab){
			var tabId = FW.getCurrentTabId();
			if(!frameMapping[tabId]){
				frameMapping[tabId] = [];
			}
			frameMapping[tabId].push(evtNo);
		}
		return evtNo;
	};

	this.unregisterEvent = function(evtNo){
		var eventId = invMapping[evtNo];
		if(!eventId){
			return;
		}
		var evtList = events[eventId];
		if(!evtList){
			return;
		}		
		var i = 0;	
		for(i=0;i<evtList.length;i++){
			var evt = evtList[i];
			if(evt[1]==evtNo){
				break;
			}
		}
		var newEvents = [];
		for(var j=0;j<evtList.length;j++){
			if(j!=i){
				newEvents.push(evtList[i]);
			}
		}
		evtList = newEvents;
		delete(invMapping[evtNo]);
	};

	this.triggerEvent = function(eventId,args){
		var evtList = events[eventId];
		var rtnList = [];
		if(evtList){
			for(var i=0;i<evtList.length;i++){
				var evt = evtList[i];
				if(evt && evt[0]){
					rtnList.push(evt[0](args));
				}
			}
		}
		if(rtnList.length>0){
			return rtnList;
		}
		return -1;
	};
}

var _event_handler = window._event_handler || _parent()._event_handler || new ITCUI_EventHandler();
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_docready.js
*/
$(document).ready(function(){
	$("body").on("click", function(){
		UiCommon.removeComboWrap();
        FW.triggerEvent("onClickBlank");
	});
    
    //自动令牌
    $.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
		if(originalOptions && originalOptions.type && originalOptions.type.toUpperCase()=="POST"){
            if(typeof(originalOptions.data) === "object"){
			    options.data = $.param($.extend(originalOptions.data, { "_token": window._token || formToken}));
            }
		}
	});
    
    $(window).resize(function(){
        FW.refreshGrids();
        //调整表头搜索框的大小 因为没有onresize的div支持 只能用延迟函数
        setTimeout(function(){
            for(var k in _itc_gridsearches){
                var searchBoxes = $("#" + k).children(".itc-gs-wrap");
                for(var i = 0;i<searchBoxes.length;i++){
                    var sb = $(searchBoxes[i]);
                    var field = $("#" + sb.attr("fieldid"));
                    var width = field.css("width").replace("px","");
                    sb.css("width",(i==searchBoxes.length?width-18:width-8) + "px");
                }
            } 
        },200);
    });
    
    $("body").on("mouseenter", ".pl-ellipsis", function(e){
        var $this = $(this);
        if(!$this.attr('title') && this.offsetWidth < this.scrollWidth){
            $this.attr('title', $this.text());
        } 
    });
    
    $("body").on("loadingMaskRemoved", function(){
        $(".datagrid-cell").each(function(){
            var $this = $(this);
            if(!$this.attr('title') && this.offsetWidth < this.scrollWidth){
                $this.attr('title', $this.text());
            } 
        });
    });
});
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_input.js
*/
var hintTimerId = null;
(function($){
	$.fn.extend({ 
		/*
		 * 扩展输入框
		 */
		ITCUI_Input : function(opt){
			var _this = $(this);			
			var options = opt || {};
			var _parent = _this.parent();
			var t = this;
			
			init = function(){
				var icon = options["icon"] || _this.attr("icon") || null;
				var placeholder = options["placeholder"] || _this.attr("placeholder") || null;
				var onlyLabel = options["onlylabel"] || _this.attr("onlylabel") || null;
				var onPressEnter = options["onPressEnter"];
				var onClickIcon = options["onClickIcon"];
				var onSearch = options["onSearch"];
				if(onSearch){
					onPressEnter = onSearch;
					onClickIcon = onSearch;
				}
				var inputWidth = "100%";
				if(icon){				
					$("<span class='itcui_input_icon'></span>").appendTo(_parent).addClass(icon).css({
						"position":"absolute",
						"top" : "6px",
						"right" : "6px"
					});
					_parent.css("padding-right","20px");
				}
				if(placeholder||icon){
					_this.css({
						"float":"left",
						"border-width" : "0px",
						"outline":"none",
						"height":"24px",
						"display":"block",
						"width":"95%"
					});
					_parent.addClass("form-control-style");
				}else{
					//这里要考虑iInput初始化的情况 iInput如果是会移除form-control样式的
					_this.addClass("form-control");
				}
				if(placeholder){
					//ie10以下的浏览器降级实现placeholder
					if(!window.requestAnimationFrame){
						$("<span class='itcui_placeholder' style='float:left'>" + placeholder +  "</span>").prependTo(_parent).addClass("itcui_placeholder").css({
							"width" : inputWidth
						});
						_parent.children("input").hide().blur(function(){
							var _this = $(this);
							if(!_this.val()){
								_this.hide();
								_this.prev("span").css("display","block");
							}
						});
						_parent.children(".itcui_placeholder").click(function(){
							var _this = $(this);
							_this.hide();
							_this.next("input").css("display","block").focus();
						});
					}
				}				
				if(onlyLabel){
					addLabel();
				}
				if(onPressEnter){
					_this.data("onPressEnter",onPressEnter);
					_this.on("keypress",function(e){
						if(e.which == 13) {
					        $(this).data("onPressEnter")($(this).val());
					    }
					});
				}
				if(onClickIcon){
					_this.data("onClickIcon",onClickIcon);
					_this.parent().children(".itcui_input_icon").on("click",function(){
						var ipt = $(this).parent().children("input");
						ipt.data("onClickIcon")(ipt.val());
					});
				}
			};
			
			addLabel = function(){
				_parent.addClass("borderless").find(".itcui_input_icon").css("display","none");
				_parent.find("input").css("visibility","hidden");
				_parent.css("height",_parent.css("height")).css("overflow","hidden");
				var text = _this.val();
				$("<span class='itcui_onlylabel'>" + text + "</span>").prependTo(_parent);
			};

			
			if(!opt || typeof(opt)=="object"){
				init();
			}
			else if(typeof(opt)=="string"){
				if(opt=="label"){
					addLabel();
				}
			}
		}
	});
})(jQuery);

var hintTimer = null;
var lastHintVal = null;
var currHintObj = null;
var hintRequestId = null;

//处理hintList的blur事件 不想用闭包 这里把input的id当参数传进来
function _itc_hintListBlur(inputId){
	if(hintTimer){
		clearInterval(hintTimer);
	}
	var inputObj = $("#" + inputId);
	var wrapObj = inputObj.next(".hintlist-wrap");
	if(wrapObj.length>0 && wrapObj.css("display")!="none"){
		if(!inputObj.data("hintObjSelected")){
			if(inputObj.val().length!=0){
				var opt = inputObj.data("opt");
				var forceParse = opt.forceParse;
				if(inputObj.val() != inputObj.data("lastInputVal")){
					if(typeof(forceParse)=="string"){
						if(forceParse=="clear"){
							inputObj.val("");
						}
					}
					else if(typeof(forceParse)=="function"){
						forceParse(inputObj,wrapObj.data("listData"));
					}
				}
			}
		}
		else{
			inputObj.data("hintObjSelected",false);
		}
	}
	wrapObj.remove();
}
(function($){
	$.fn.extend({ 
		ITCUI_HintList : function(opt){
			var _this = $(this);
			var _t = this;
			opt = opt || {};			
			if(!opt.datasource && !opt.datafunc){
				return;
			}
			opt.maxItemCount = opt.maxItemCount || 10; //最多显示10项
			opt.filterHere = opt.filterHere || false;
			opt.getDataOnKeyPress = opt.getDataOnKeyPress || false;//是否在每次按键都重新获取数据
			opt.extArgName = opt.extArgName || null;//在每次显示外框之前执行的动作，结果作为补充参数传入函数或者post内
			opt.extArgFunc = opt.extArgFunc || function(){};
			opt.highlight = opt.highlight===false ? false : true;//是否需要高亮关键字
			opt.clickEvent = opt.clickEvent || function(){};//点击提示项的动作，参数依次为id,name
			opt.formatter = opt.formatter || function(id,name){return name};
			opt.highlighter = opt.highlighter || "<span style='color:red'>#</span>";
			opt.showOn = opt.showOn || "click";
			opt.forceParse = opt.forceParse || "clear";//取值可以为clear,first
			_this.data("opt",opt);
			_this.addClass("enter-allowed").addClass("hintlist-input");

			_t.createWrap = function(target){
				var wrapWidth = target.css("width");
				var hlHtml = '<ul class="dropdown-menu hintlist-wrap" role="menu" style="overflow-x:hidden;position:absolute;min-width:200px;width:' + wrapWidth + '">';
				hlHtml += '<li><span class="menuitem noclick">正在加载，请稍后</span></li>';
				hlHtml += '</ul>';
				target.after(hlHtml);
				var wrapObj = target.next(".hintlist-wrap");
				target.data("hintList",target.next())
				var opt = target.data("opt");				
				var pos = (opt.getAbsPosFunc || ITC_GetAbsPos)(target);
				wrapObj.css({
					"left" : pos.left,
					"top" : pos.top + parseInt(target.css("height") + 1),
					"display" : "block"
				});
				if(opt.showOn=="input"){
					wrapObj.hide();
				}
				//IE8兼容版本
				if(hintTimer!=null){
					clearInterval(hintTimer);
				}
				lastHintVal = null;
				currHintObj = this;
				hintTimer = setInterval(function(){
					var that = $(currHintObj);
					var val = that.val();
					var wrapObj = that.next(".hintlist-wrap");
					if(val.length>0 && wrapObj.css("display")=="none"){
						wrapObj.show();
					}
					if(val!=lastHintVal){
						lastHintVal = val;
						changeData(that);
					}
				},100);
			};

			_this.on("keydown",function(e){
				var me = $(this);
				e = e || window.event;
				var kc = e.which || e.keyCode;
				var lst = me.next(".hintlist-wrap");
				if(lst.length==0 || lst.css("display")=="none"){
					return;
				}
				if(kc==38 || kc==40){
					e.preventDefault();
					var currItem = lst.children(".active");
					currItem.removeClass("active");
					if(kc==38){//上
						if(currItem.length==0){
							lst.children("li").last().addClass("active");
						}
						else{
							if(currItem.prev("li").length == 0){
								var li = lst.children("li").last();
								if(li.children(".noclick").length==0){
									li.addClass("active");
								}
								else{
									li.prev("li").addClass("active");
								}
							}
							else{
								currItem.prev("li").addClass("active");
							}
						}
					}
					else if(kc==40){//下
						if(currItem.length==0){
							lst.children("li").first().addClass("active");
						}
						else{							
							if(currItem.next("li").children(".noclick").length>0 || currItem.next("li").length==0){	
								lst.children("li").first().addClass("active");
							}
							else{
								currItem.next("li").addClass("active");								
							}
						}
					}
				}
				else if(kc==13){//回车
					if(lst.find(".active")){
						lst.find(".active").children("a").mousedown();
						me.blur();
					}
				}
			});
			
			_this.on("click",function(e){
				e.stopPropagation();
			});
			
			_this.on("focus",function(e){
				var target = $(this);
				var wrapObj = target.next(".hintlist-wrap");
				//现在使用blur事件清除了 这里要检测防止点击列表时再次触发focus事件
				if(wrapObj.length>0){
					return;
				}
				_t.createWrap(target);										
			});
			
			_this.on("blur",function(){
				setTimeout("_itc_hintListBlur('" + $(this).attr("id") + "')",200);				
			});
			
			getRequestId = function(){
				hintRequestId = Math.random();
				return hintRequestId;
			};

			changeData = function(target){
				var kw = target.val();
				var opt = target.data("opt");
				if(opt.datafunc){
					var data = opt.datafunc(kw);
					if(isArray(data)){
						genList(kw,data,target);
					}
				}
				else if(opt.datasource){
					var form = {
						"kw" : kw 
					};
					if(opt.extArgName){
						form[opt.extArgName] = opt.extArgFunc();
					}
					//使用闭包为每次ajax请求关联一个ID
					(function() {
						var reqId = getRequestId();	
						$.ajax({
							url : opt.datasource,
							type : "POST",
							data : form,
							success : function(result){
								if(!result){
									result = [];
								}
								if(!isArray(result)){
									result = eval("(" + result + ")");
								}
								if(isArray(result)){
									if(reqId === hintRequestId){
										genList(kw,result,target);
									}
									else{
										//FW.debug("[iHint deny]reqId=" + reqId + ",hintReqId=" + hintRequestId);
									}
								}
							}
						});
					})();									
				}
			};

			genList = function(kw,data,target){
				target.data("hintObjSelected",false);
				var opt = target.data("opt");
				var formatter = opt.formatter;
				var lHtml = "";
				var l = data.length>opt.maxItemCount?opt.maxItemCount:data.length;
				for(var i=0;i<l;i++){
					var o = data[i];
					var dspName = formatter(o.id,FW.specialchars(o.name),o);
					if(opt.highlight){
						var _kw = FW.specialchars(kw);
						var t = opt.highlighter.replace("#",_kw);
						dspName = dspName.replace(_kw,t);
					}
					lHtml += '<li><a class="menuitem" seq=' + i + ' hintid="' + o.id + '" _name=\'' + FW.specialchars(o.name) + '\'>' + dspName + '</a></li>';
				}
				if(data.length==0){
					lHtml += '<li><span class="noclick">没有符合条件的结果</span></li>';
				}
				if(data.length>opt.maxItemCount){
					lHtml += '<li><span class="noclick">结果多于' + opt.maxItemCount + '条，请补充关键字</span></li>';
				}
				target.next(".hintlist-wrap").data("listData",data).html(lHtml).find(".menuitem").mousedown(function(){
					if(hintTimer){
						clearInterval(hintTimer);
					}
					var __this = $(this);
					var p = __this.parents(".hintlist-wrap");
					var id = __this.attr("hintid");
					var seq = parseInt(__this.attr("seq"),10);
					var name = FW.unspecialchars(__this.attr("_name"));
					var target = p.prev("input");
					var opt = target.data("opt");
					target.data("hintObjSelected",true);
					opt.clickEvent(id,name,p.data("listData")[seq]);
					//保存单击后的文本框内容 如果用户再次点击文本框但是不选择也不会清除
					target.data("lastInputVal",target.val());
				});	
			};
		}
	});
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.panel.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
$.fn._remove=function(){
return this.each(function(){
$(this).remove();
try{
this.outerHTML="";
}
catch(err){
}
});
};
function _1(_2){
_2._remove();
};
function _3(_4,_5){
var _6=$.data(_4,"panel").options;
var _7=$.data(_4,"panel").panel;
var _8=_7.children("div.panel-header");
var _9=_7.children("div.panel-body");
if(_5){
$.extend(_6,{width:_5.width,height:_5.height,left:_5.left,top:_5.top});
}
_6.fit?$.extend(_6,_7._fit()):_7._fit(false);
_7.css({left:_6.left,top:_6.top});
if(!isNaN(_6.width)){
_7._outerWidth(_6.width);
}else{
_7.width("auto");
}
_8.add(_9)._outerWidth(_7.width());
if(!isNaN(_6.height)){
_7._outerHeight(_6.height);
_9._outerHeight(_7.height()-_8._outerHeight());
}else{
_9.height("auto");
}
_7.css("height","");
_6.onResize.apply(_4,[_6.width,_6.height]);
$(_4).find(">div,>form>div").triggerHandler("_resize");
};
function _a(_b,_c){
var _d=$.data(_b,"panel").options;
var _e=$.data(_b,"panel").panel;
if(_c){
if(_c.left!=null){
_d.left=_c.left;
}
if(_c.top!=null){
_d.top=_c.top;
}
}
_e.css({left:_d.left,top:_d.top});
_d.onMove.apply(_b,[_d.left,_d.top]);
};
function _f(_10){
$(_10).addClass("panel-body");
var _11=$("<div class=\"panel\"></div>").insertBefore(_10);
_11[0].appendChild(_10);
_11.bind("_resize",function(){
var _12=$.data(_10,"panel").options;
if(_12.fit==true){
_3(_10);
}
return false;
});
return _11;
};
function _13(_14){
var _15=$.data(_14,"panel").options;
var _16=$.data(_14,"panel").panel;
if(_15.tools&&typeof _15.tools=="string"){
_16.find(">div.panel-header>div.panel-tool .panel-tool-a").appendTo(_15.tools);
}
_1(_16.children("div.panel-header"));
if(_15.title&&!_15.noheader){
var _17=$("<div class=\"panel-header\"><div class=\"panel-title\">"+_15.title+"</div></div>").prependTo(_16);
if(_15.iconCls){
_17.find(".panel-title").addClass("panel-with-icon");
$("<div class=\"panel-icon\"></div>").addClass(_15.iconCls).appendTo(_17);
}
var _18=$("<div class=\"panel-tool\"></div>").appendTo(_17);
_18.bind("click",function(e){
e.stopPropagation();
});
if(_15.tools){
if($.isArray(_15.tools)){
for(var i=0;i<_15.tools.length;i++){
var t=$("<a href=\"javascript:void(0)\"></a>").addClass(_15.tools[i].iconCls).appendTo(_18);
if(_15.tools[i].handler){
t.bind("click",eval(_15.tools[i].handler));
}
}
}else{
$(_15.tools).children().each(function(){
$(this).addClass($(this).attr("iconCls")).addClass("panel-tool-a").appendTo(_18);
});
}
}
if(_15.collapsible){
$("<a class=\"panel-tool-collapse\" href=\"javascript:void(0)\"></a>").appendTo(_18).bind("click",function(){
if(_15.collapsed==true){
_3c(_14,true);
}else{
_2c(_14,true);
}
return false;
});
}
if(_15.minimizable){
$("<a class=\"panel-tool-min\" href=\"javascript:void(0)\"></a>").appendTo(_18).bind("click",function(){
_47(_14);
return false;
});
}
if(_15.maximizable){
$("<a class=\"panel-tool-max\" href=\"javascript:void(0)\"></a>").appendTo(_18).bind("click",function(){
if(_15.maximized==true){
_4b(_14);
}else{
_2b(_14);
}
return false;
});
}
if(_15.closable){
$("<a class=\"panel-tool-close\" href=\"javascript:void(0)\"></a>").appendTo(_18).bind("click",function(){
_19(_14);
return false;
});
}
_16.children("div.panel-body").removeClass("panel-body-noheader");
}else{
_16.children("div.panel-body").addClass("panel-body-noheader");
}
};
function _1a(_1b){
var _1c=$.data(_1b,"panel");
var _1d=_1c.options;
if(_1d.href){
if(!_1c.isLoaded||!_1d.cache){
if(_1d.onBeforeLoad.call(_1b)==false){
return;
}
_1c.isLoaded=false;
_1e(_1b);
if(_1d.loadingMessage){
$(_1b).html($("<div class=\"panel-loading\"></div>").html(_1d.loadingMessage));
}
$.ajax({url:_1d.href,cache:false,dataType:"html",success:function(_1f){
_20(_1d.extractor.call(_1b,_1f));
_1d.onLoad.apply(_1b,arguments);
_1c.isLoaded=true;
}});
}
}else{
if(_1d.content){
if(!_1c.isLoaded){
_1e(_1b);
_20(_1d.content);
_1c.isLoaded=true;
}
}
}
function _20(_21){
$(_1b).html(_21);
if($.parser){
$.parser.parse($(_1b));
}
};
};
function _1e(_22){
var t=$(_22);
t.find(".combo-f").each(function(){
$(this).combo("destroy");
});
t.find(".m-btn").each(function(){
$(this).menubutton("destroy");
});
t.find(".s-btn").each(function(){
$(this).splitbutton("destroy");
});
t.find(".tooltip-f").each(function(){
$(this).tooltip("destroy");
});
};
function _23(_24){
$(_24).find("div.panel:visible,div.accordion:visible,div.tabs-container:visible,div.layout:visible").each(function(){
$(this).triggerHandler("_resize",[true]);
});
};
function _25(_26,_27){
var _28=$.data(_26,"panel").options;
var _29=$.data(_26,"panel").panel;
if(_27!=true){
if(_28.onBeforeOpen.call(_26)==false){
return;
}
}
_29.show();
_28.closed=false;
_28.minimized=false;
var _2a=_29.children("div.panel-header").find("a.panel-tool-restore");
if(_2a.length){
_28.maximized=true;
}
_28.onOpen.call(_26);
if(_28.maximized==true){
_28.maximized=false;
_2b(_26);
}
if(_28.collapsed==true){
_28.collapsed=false;
_2c(_26);
}
if(!_28.collapsed){
_1a(_26);
_23(_26);
}
};
function _19(_2d,_2e){
var _2f=$.data(_2d,"panel").options;
var _30=$.data(_2d,"panel").panel;
if(_2e!=true){
if(_2f.onBeforeClose.call(_2d)==false){
return;
}
}
_30._fit(false);
_30.hide();
_2f.closed=true;
_2f.onClose.call(_2d);
};
function _31(_32,_33){
var _34=$.data(_32,"panel").options;
var _35=$.data(_32,"panel").panel;
if(_33!=true){
if(_34.onBeforeDestroy.call(_32)==false){
return;
}
}
_1e(_32);
_1(_35);
_34.onDestroy.call(_32);
};
function _2c(_36,_37){
var _38=$.data(_36,"panel").options;
var _39=$.data(_36,"panel").panel;
var _3a=_39.children("div.panel-body");
var _3b=_39.children("div.panel-header").find("a.panel-tool-collapse");
if(_38.collapsed==true){
return;
}
_3a.stop(true,true);
if(_38.onBeforeCollapse.call(_36)==false){
return;
}
_3b.addClass("panel-tool-expand");
if(_37==true){
_3a.slideUp("normal",function(){
_38.collapsed=true;
_38.onCollapse.call(_36);
});
}else{
_3a.hide();
_38.collapsed=true;
_38.onCollapse.call(_36);
}
};
function _3c(_3d,_3e){
var _3f=$.data(_3d,"panel").options;
var _40=$.data(_3d,"panel").panel;
var _41=_40.children("div.panel-body");
var _42=_40.children("div.panel-header").find("a.panel-tool-collapse");
if(_3f.collapsed==false){
return;
}
_41.stop(true,true);
if(_3f.onBeforeExpand.call(_3d)==false){
return;
}
_42.removeClass("panel-tool-expand");
if(_3e==true){
_41.slideDown("normal",function(){
_3f.collapsed=false;
_3f.onExpand.call(_3d);
_1a(_3d);
_23(_3d);
});
}else{
_41.show();
_3f.collapsed=false;
_3f.onExpand.call(_3d);
_1a(_3d);
_23(_3d);
}
};
function _2b(_43){
var _44=$.data(_43,"panel").options;
var _45=$.data(_43,"panel").panel;
var _46=_45.children("div.panel-header").find("a.panel-tool-max");
if(_44.maximized==true){
return;
}
_46.addClass("panel-tool-restore");
if(!$.data(_43,"panel").original){
$.data(_43,"panel").original={width:_44.width,height:_44.height,left:_44.left,top:_44.top,fit:_44.fit};
}
_44.left=0;
_44.top=0;
_44.fit=true;
_3(_43);
_44.minimized=false;
_44.maximized=true;
_44.onMaximize.call(_43);
};
function _47(_48){
var _49=$.data(_48,"panel").options;
var _4a=$.data(_48,"panel").panel;
_4a._fit(false);
_4a.hide();
_49.minimized=true;
_49.maximized=false;
_49.onMinimize.call(_48);
};
function _4b(_4c){
var _4d=$.data(_4c,"panel").options;
var _4e=$.data(_4c,"panel").panel;
var _4f=_4e.children("div.panel-header").find("a.panel-tool-max");
if(_4d.maximized==false){
return;
}
_4e.show();
_4f.removeClass("panel-tool-restore");
$.extend(_4d,$.data(_4c,"panel").original);
_3(_4c);
_4d.minimized=false;
_4d.maximized=false;
$.data(_4c,"panel").original=null;
_4d.onRestore.call(_4c);
};
function _50(_51){
var _52=$.data(_51,"panel").options;
var _53=$.data(_51,"panel").panel;
var _54=$(_51).panel("header");
var _55=$(_51).panel("body");
_53.css(_52.style);
_53.addClass(_52.cls);
if(_52.border){
_54.removeClass("panel-header-noborder");
_55.removeClass("panel-body-noborder");
}else{
_54.addClass("panel-header-noborder");
_55.addClass("panel-body-noborder");
}
_54.addClass(_52.headerCls);
_55.addClass(_52.bodyCls);
if(_52.id){
$(_51).attr("id",_52.id);
}else{
$(_51).attr("id","");
}
};
function _56(_57,_58){
$.data(_57,"panel").options.title=_58;
$(_57).panel("header").find("div.panel-title").html(_58);
};
var TO=false;
var _59=true;
$(window).unbind(".panel").bind("resize.panel",function(){
if(!_59){
return;
}
if(TO!==false){
clearTimeout(TO);
}
TO=setTimeout(function(){
_59=false;
var _5a=$("body.layout");
if(_5a.length){
_5a.layout("resize");
}else{
$("body").children("div.panel,div.accordion,div.tabs-container,div.layout").triggerHandler("_resize");
}
_59=true;
TO=false;
},200);
});
$.fn.panel=function(_5b,_5c){
if(typeof _5b=="string"){
return $.fn.panel.methods[_5b](this,_5c);
}
_5b=_5b||{};
return this.each(function(){
var _5d=$.data(this,"panel");
var _5e;
if(_5d){
_5e=$.extend(_5d.options,_5b);
_5d.isLoaded=false;
}else{
_5e=$.extend({},$.fn.panel.defaults,$.fn.panel.parseOptions(this),_5b);
$(this).attr("title","");
_5d=$.data(this,"panel",{options:_5e,panel:_f(this),isLoaded:false});
}
_13(this);
_50(this);
if(_5e.doSize==true){
_5d.panel.css("display","block");
_3(this);
}
if(_5e.closed==true||_5e.minimized==true){
_5d.panel.hide();
}else{
_25(this);
}
});
};
$.fn.panel.methods={options:function(jq){
return $.data(jq[0],"panel").options;
},panel:function(jq){
return $.data(jq[0],"panel").panel;
},header:function(jq){
return $.data(jq[0],"panel").panel.find(">div.panel-header");
},body:function(jq){
return $.data(jq[0],"panel").panel.find(">div.panel-body");
},setTitle:function(jq,_5f){
return jq.each(function(){
_56(this,_5f);
});
},open:function(jq,_60){
return jq.each(function(){
_25(this,_60);
});
},close:function(jq,_61){
return jq.each(function(){
_19(this,_61);
});
},destroy:function(jq,_62){
return jq.each(function(){
_31(this,_62);
});
},refresh:function(jq,_63){
return jq.each(function(){
$.data(this,"panel").isLoaded=false;
if(_63){
$.data(this,"panel").options.href=_63;
}
_1a(this);
});
},resize:function(jq,_64){
return jq.each(function(){
_3(this,_64);
});
},move:function(jq,_65){
return jq.each(function(){
_a(this,_65);
});
},maximize:function(jq){
return jq.each(function(){
_2b(this);
});
},minimize:function(jq){
return jq.each(function(){
_47(this);
});
},restore:function(jq){
return jq.each(function(){
_4b(this);
});
},collapse:function(jq,_66){
return jq.each(function(){
_2c(this,_66);
});
},expand:function(jq,_67){
return jq.each(function(){
_3c(this,_67);
});
}};
$.fn.panel.parseOptions=function(_68){
var t=$(_68);
return $.extend({},$.parser.parseOptions(_68,["id","width","height","left","top","title","iconCls","cls","headerCls","bodyCls","tools","href",{cache:"boolean",fit:"boolean",border:"boolean",noheader:"boolean"},{collapsible:"boolean",minimizable:"boolean",maximizable:"boolean"},{closable:"boolean",collapsed:"boolean",minimized:"boolean",maximized:"boolean",closed:"boolean"}]),{loadingMessage:(t.attr("loadingMessage")!=undefined?t.attr("loadingMessage"):undefined)});
};
$.fn.panel.defaults={id:null,title:null,iconCls:null,width:"auto",height:"auto",left:null,top:null,cls:null,headerCls:null,bodyCls:null,style:{},href:null,cache:true,fit:false,border:true,doSize:true,noheader:false,content:null,collapsible:false,minimizable:false,maximizable:false,closable:false,collapsed:false,minimized:false,maximized:false,closed:false,tools:null,href:null,loadingMessage:"Loading...",extractor:function(_69){
var _6a=/<body[^>]*>((.|[\n\r])*)<\/body>/im;
var _6b=_6a.exec(_69);
if(_6b){
return _6b[1];
}else{
return _69;
}
},onBeforeLoad:function(){
},onLoad:function(){
},onBeforeOpen:function(){
},onOpen:function(){
},onBeforeClose:function(){
},onClose:function(){
},onBeforeDestroy:function(){
},onDestroy:function(){
},onResize:function(_6c,_6d){
},onMove:function(_6e,top){
},onMaximize:function(){
},onRestore:function(){
},onMinimize:function(){
},onBeforeCollapse:function(){
},onBeforeExpand:function(){
},onCollapse:function(){
},onExpand:function(){
}};
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.resizable.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
$.fn.resizable=function(_1,_2){
if(typeof _1=="string"){
return $.fn.resizable.methods[_1](this,_2);
}
function _3(e){
var _4=e.data;
var _5=$.data(_4.target,"resizable").options;
if(_4.dir.indexOf("e")!=-1){
var _6=_4.startWidth+e.pageX-_4.startX;
_6=Math.min(Math.max(_6,_5.minWidth),_5.maxWidth);
_4.width=_6;
}
if(_4.dir.indexOf("s")!=-1){
var _7=_4.startHeight+e.pageY-_4.startY;
_7=Math.min(Math.max(_7,_5.minHeight),_5.maxHeight);
_4.height=_7;
}
if(_4.dir.indexOf("w")!=-1){
var _6=_4.startWidth-e.pageX+_4.startX;
_6=Math.min(Math.max(_6,_5.minWidth),_5.maxWidth);
_4.width=_6;
_4.left=_4.startLeft+_4.startWidth-_4.width;
}
if(_4.dir.indexOf("n")!=-1){
var _7=_4.startHeight-e.pageY+_4.startY;
_7=Math.min(Math.max(_7,_5.minHeight),_5.maxHeight);
_4.height=_7;
_4.top=_4.startTop+_4.startHeight-_4.height;
}
};
function _8(e){
var _9=e.data;
var t=$(_9.target);
t.css({left:_9.left,top:_9.top});
if(t.outerWidth()!=_9.width){
t._outerWidth(_9.width);
}
if(t.outerHeight()!=_9.height){
t._outerHeight(_9.height);
}
};
function _a(e){
$.fn.resizable.isResizing=true;
$.data(e.data.target,"resizable").options.onStartResize.call(e.data.target,e);
return false;
};
function _b(e){
_3(e);
if($.data(e.data.target,"resizable").options.onResize.call(e.data.target,e)!=false){
_8(e);
}
return false;
};
function _c(e){
$.fn.resizable.isResizing=false;
_3(e,true);
_8(e);
$.data(e.data.target,"resizable").options.onStopResize.call(e.data.target,e);
$(document).unbind(".resizable");
$("body").css("cursor","");
return false;
};
return this.each(function(){
var _d=null;
var _e=$.data(this,"resizable");
if(_e){
$(this).unbind(".resizable");
_d=$.extend(_e.options,_1||{});
}else{
_d=$.extend({},$.fn.resizable.defaults,$.fn.resizable.parseOptions(this),_1||{});
$.data(this,"resizable",{options:_d});
}
if(_d.disabled==true){
return;
}
$(this).bind("mousemove.resizable",{target:this},function(e){
if($.fn.resizable.isResizing){
return;
}
var _f=_10(e);
if(_f==""){
$(e.data.target).css("cursor","");
}else{
$(e.data.target).css("cursor",_f+"-resize");
}
}).bind("mouseleave.resizable",{target:this},function(e){
$(e.data.target).css("cursor","");
}).bind("mousedown.resizable",{target:this},function(e){
var dir=_10(e);
if(dir==""){
return;
}
function _11(css){
var val=parseInt($(e.data.target).css(css));
if(isNaN(val)){
return 0;
}else{
return val;
}
};
var _12={target:e.data.target,dir:dir,startLeft:_11("left"),startTop:_11("top"),left:_11("left"),top:_11("top"),startX:e.pageX,startY:e.pageY,startWidth:$(e.data.target).outerWidth(),startHeight:$(e.data.target).outerHeight(),width:$(e.data.target).outerWidth(),height:$(e.data.target).outerHeight(),deltaWidth:$(e.data.target).outerWidth()-$(e.data.target).width(),deltaHeight:$(e.data.target).outerHeight()-$(e.data.target).height()};
$(document).bind("mousedown.resizable",_12,_a);
$(document).bind("mousemove.resizable",_12,_b);
$(document).bind("mouseup.resizable",_12,_c);
$("body").css("cursor",dir+"-resize");
});
function _10(e){
var tt=$(e.data.target);
var dir="";
var _13=tt.offset();
var _14=tt.outerWidth();
var _15=tt.outerHeight();
var _16=_d.edge;
if(e.pageY>_13.top&&e.pageY<_13.top+_16){
dir+="n";
}else{
if(e.pageY<_13.top+_15&&e.pageY>_13.top+_15-_16){
dir+="s";
}
}
if(e.pageX>_13.left&&e.pageX<_13.left+_16){
dir+="w";
}else{
if(e.pageX<_13.left+_14&&e.pageX>_13.left+_14-_16){
dir+="e";
}
}
var _17=_d.handles.split(",");
for(var i=0;i<_17.length;i++){
var _18=_17[i].replace(/(^\s*)|(\s*$)/g,"");
if(_18=="all"||_18==dir){
return dir;
}
}
return "";
};
});
};
$.fn.resizable.methods={options:function(jq){
return $.data(jq[0],"resizable").options;
},enable:function(jq){
return jq.each(function(){
$(this).resizable({disabled:false});
});
},disable:function(jq){
return jq.each(function(){
$(this).resizable({disabled:true});
});
}};
$.fn.resizable.parseOptions=function(_19){
var t=$(_19);
return $.extend({},$.parser.parseOptions(_19,["handles",{minWidth:"number",minHeight:"number",maxWidth:"number",maxHeight:"number",edge:"number"}]),{disabled:(t.attr("disabled")?true:undefined)});
};
$.fn.resizable.defaults={disabled:false,handles:"n, e, s, w, ne, se, sw, nw, all",minWidth:10,minHeight:10,maxWidth:10000,maxHeight:10000,edge:5,onStartResize:function(e){
},onResize:function(e){
},onStopResize:function(e){
}};
$.fn.resizable.isResizing=false;
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.linkbutton.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
function _1(_2){
var _3=$.data(_2,"linkbutton").options;
var t=$(_2);
t.addClass("l-btn").removeClass("l-btn-plain l-btn-selected l-btn-plain-selected");
if(_3.plain){
t.addClass("l-btn-plain");
}
if(_3.selected){
t.addClass(_3.plain?"l-btn-selected l-btn-plain-selected":"l-btn-selected");
}
t.attr("group",_3.group||"");
t.attr("id",_3.id||"");
t.html("<span class=\"l-btn-left\">"+"<span class=\"l-btn-text\"></span>"+"</span>");
if(_3.text){
t.find(".l-btn-text").html(_3.text);
if(_3.iconCls){
t.find(".l-btn-text").addClass(_3.iconCls).addClass(_3.iconAlign=="left"?"l-btn-icon-left":"l-btn-icon-right");
}
}else{
t.find(".l-btn-text").html("<span class=\"l-btn-empty\">&nbsp;</span>");
if(_3.iconCls){
t.find(".l-btn-empty").addClass(_3.iconCls);
}
}
t.unbind(".linkbutton").bind("focus.linkbutton",function(){
if(!_3.disabled){
$(this).find(".l-btn-text").addClass("l-btn-focus");
}
}).bind("blur.linkbutton",function(){
$(this).find(".l-btn-text").removeClass("l-btn-focus");
});
if(_3.toggle&&!_3.disabled){
t.bind("click.linkbutton",function(){
if(_3.selected){
$(this).linkbutton("unselect");
}else{
$(this).linkbutton("select");
}
});
}
_4(_2,_3.selected);
_5(_2,_3.disabled);
};
function _4(_6,_7){
var _8=$.data(_6,"linkbutton").options;
if(_7){
if(_8.group){
$("a.l-btn[group=\""+_8.group+"\"]").each(function(){
var o=$(this).linkbutton("options");
if(o.toggle){
$(this).removeClass("l-btn-selected l-btn-plain-selected");
o.selected=false;
}
});
}
$(_6).addClass(_8.plain?"l-btn-selected l-btn-plain-selected":"l-btn-selected");
_8.selected=true;
}else{
if(!_8.group){
$(_6).removeClass("l-btn-selected l-btn-plain-selected");
_8.selected=false;
}
}
};
function _5(_9,_a){
var _b=$.data(_9,"linkbutton");
var _c=_b.options;
$(_9).removeClass("l-btn-disabled l-btn-plain-disabled");
if(_a){
_c.disabled=true;
var _d=$(_9).attr("href");
if(_d){
_b.href=_d;
$(_9).attr("href","javascript:void(0)");
}
if(_9.onclick){
_b.onclick=_9.onclick;
_9.onclick=null;
}
_c.plain?$(_9).addClass("l-btn-disabled l-btn-plain-disabled"):$(_9).addClass("l-btn-disabled");
}else{
_c.disabled=false;
if(_b.href){
$(_9).attr("href",_b.href);
}
if(_b.onclick){
_9.onclick=_b.onclick;
}
}
};
$.fn.linkbutton=function(_e,_f){
if(typeof _e=="string"){
return $.fn.linkbutton.methods[_e](this,_f);
}
_e=_e||{};
return this.each(function(){
var _10=$.data(this,"linkbutton");
if(_10){
$.extend(_10.options,_e);
}else{
$.data(this,"linkbutton",{options:$.extend({},$.fn.linkbutton.defaults,$.fn.linkbutton.parseOptions(this),_e)});
$(this).removeAttr("disabled");
}
_1(this);
});
};
$.fn.linkbutton.methods={options:function(jq){
return $.data(jq[0],"linkbutton").options;
},enable:function(jq){
return jq.each(function(){
_5(this,false);
});
},disable:function(jq){
return jq.each(function(){
_5(this,true);
});
},select:function(jq){
return jq.each(function(){
_4(this,true);
});
},unselect:function(jq){
return jq.each(function(){
_4(this,false);
});
}};
$.fn.linkbutton.parseOptions=function(_11){
var t=$(_11);
return $.extend({},$.parser.parseOptions(_11,["id","iconCls","iconAlign","group",{plain:"boolean",toggle:"boolean",selected:"boolean"}]),{disabled:(t.attr("disabled")?true:undefined),text:$.trim(t.html()),iconCls:(t.attr("icon")||t.attr("iconCls"))});
};
$.fn.linkbutton.defaults={id:null,disabled:false,toggle:false,selected:false,group:null,plain:false,text:"",iconCls:null,iconAlign:"left"};
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.pagination.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($) {
    function _1(_2) {
        var _3 = $.data(_2, "pagination");
        var _4 = _3.options;
        var bb = _3.bb = {};
        var _5 = $(_2).addClass("pagination").html("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr></tr></table>");
        var tr = _5.find("tr");
        var aa = $.extend([], _4.layout);
        if (!_4.showPageList) {
            _6(aa, "list");
        }
        if (!_4.showRefresh) {
            _6(aa, "refresh");
        }
        if (aa[0] == "sep") {
            aa.shift();
        }
        if (aa[aa.length - 1] == "sep") {
            aa.pop();
        }
        for (var _7 = 0; _7 < aa.length; _7++) {
            var _8 = aa[_7];
            if (_8 == "list") {
                var ps = $("<select class=\"pagination-page-list\"></select>");
                ps.bind("change",
                function() {
                    _4.pageSize = parseInt($(this).val());
                    _4.onChangePageSize.call(_2, _4.pageSize);
                    _10(_2, _4.pageNumber);
                });
                for (var i = 0; i < _4.pageList.length; i++) {
                    $("<option></option>").text(_4.pageList[i]).appendTo(ps);
                }
                $("<td></td>").append(ps).appendTo(tr);
            } else {
                if (_8 == "sep") {
                    $("<td><div class=\"pagination-btn-separator\"></div></td>").appendTo(tr);
                } else {
                    if (_8 == "first") {
                        bb.first = _9("first");
                    } else {
                        if (_8 == "prev") {
                            bb.prev = _9("prev");
                        } else {
                            if (_8 == "next") {
                                bb.next = _9("next");
                            } else {
                                if (_8 == "last") {
                                    bb.last = _9("last");
                                } else {
                                    if (_8 == "manual") {
                                        $("<span style=\"padding-left:6px;\"></span>").html(_4.beforePageText).appendTo(tr).wrap("<td></td>");
                                        bb.num = $("<input class=\"pagination-num\" type=\"text\" value=\"1\" size=\"2\">").appendTo(tr).wrap("<td></td>");
                                        bb.num.unbind(".pagination").bind("keydown.pagination",
                                        function(e) {
                                            if (e.keyCode == 13) {
                                                var _a = parseInt($(this).val()) || 1;
                                                _10(_2, _a);
                                                return false;
                                            }
                                        });
                                        bb.after = $("<span style=\"padding-right:6px;\"></span>").appendTo(tr).wrap("<td></td>");
                                    } else {
                                        if (_8 == "refresh") {
                                            bb.refresh = _9("refresh");
                                        } else {
                                            if (_8 == "links") {
                                                $("<td class=\"pagination-links\"></td>").appendTo(tr);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (_4.buttons) {
            $("<td><div class=\"pagination-btn-separator\"></div></td>").appendTo(tr);
            if ($.isArray(_4.buttons)) {
                for (var i = 0; i < _4.buttons.length; i++) {
                    var _b = _4.buttons[i];
                    if (_b == "-") {
                        $("<td><div class=\"pagination-btn-separator\"></div></td>").appendTo(tr);
                    } else {
                        var td = $("<td></td>").appendTo(tr);
                        var a = $("<a href=\"javascript:void(0)\"></a>").appendTo(td);
                        a[0].onclick = eval(_b.handler ||
                        function() {});
                    }
                }
            } else {
                var td = $("<td></td>").appendTo(tr);
                $(_4.buttons).appendTo(td).show();
            }
        }
        $("<div class=\"pagination-info\"></div>").appendTo(_5);
        $("<div style=\"clear:both;\"></div>").appendTo(_5);
        function _9(_c) {
            var _d = _4.nav[_c];
            var a = $("<a href=\"javascript:void(0)\"></a>").appendTo(tr);
            a.wrap("<td></td>");
            return a;
        };
        function _6(aa, _e) {
            var _f = $.inArray(_e, aa);
            if (_f >= 0) {
                aa.splice(_f, 1);
            }
            return aa;
        };
    };
    function _10(_11, _12) {
        var _13 = $.data(_11, "pagination").options;
        _14(_11, {
            pageNumber: _12
        });
        _13.onSelectPage.call(_11, _13.pageNumber, _13.pageSize);
    };
    function _14(_15, _16) {
        var _17 = $.data(_15, "pagination");
        var _18 = _17.options;
        var bb = _17.bb;
        $.extend(_18, _16 || {});
        var ps = $(_15).find("select.pagination-page-list");
        if (ps.length) {
            ps.val(_18.pageSize + "");
            _18.pageSize = parseInt(ps.val());
        }
        var _19 = Math.ceil(_18.total / _18.pageSize) || 1;
        if (_18.pageNumber < 1) {
            _18.pageNumber = 1;
        }
        if (_18.pageNumber > _19) {
            _18.pageNumber = _19;
        }
        if (bb.num) {
            bb.num.val(_18.pageNumber);
        }
        if (bb.after) {
            bb.after.html(_18.afterPageText.replace(/{pages}/, _19));
        }
        var td = $(_15).find("td.pagination-links");
        if (td.length) {
            td.empty();
            var _1a = _18.pageNumber - Math.floor(_18.links / 2);
            if (_1a < 1) {
                _1a = 1;
            }
            var _1b = _1a + _18.links - 1;
            if (_1b > _19) {
                _1b = _19;
            }
            _1a = _1b - _18.links + 1;
            if (_1a < 1) {
                _1a = 1;
            }
            for (var i = _1a; i <= _1b; i++) {
                var a = $("<a class=\"pagination-link\" href=\"javascript:void(0)\"></a>").appendTo(td);
            }
        }
        var _1c = _18.displayMsg;
        _1c = _1c.replace(/{from}/, _18.total == 0 ? 0 : _18.pageSize * (_18.pageNumber - 1) + 1);
        _1c = _1c.replace(/{to}/, Math.min(_18.pageSize * (_18.pageNumber), _18.total));
        _1c = _1c.replace(/{total}/, _18.total);
        $(_15).find("div.pagination-info").html(_1c);
        _1d(_15, _18.loading);
    };
    function _1d(_1e, _1f) {
        var _20 = $.data(_1e, "pagination");
        var _21 = _20.options;
        _21.loading = _1f;
    };
    $.fn.pagination = function(_22, _23) {
        if (typeof _22 == "string") {
            return $.fn.pagination.methods[_22](this, _23);
        }
        _22 = _22 || {};
        return this.each(function() {
            var _24;
            var _25 = $.data(this, "pagination");
            if (_25) {
                _24 = $.extend(_25.options, _22);
            } else {
                _24 = $.extend({},
                $.fn.pagination.defaults, $.fn.pagination.parseOptions(this), _22);
                $.data(this, "pagination", {
                    options: _24
                });
            }
            _1(this);
            _14(this);
        });
    };
    $.fn.pagination.methods = {
        options: function(jq) {
            return $.data(jq[0], "pagination").options;
        },
        loading: function(jq) {
            return jq.each(function() {
                _1d(this, true);
            });
        },
        loaded: function(jq) {
            return jq.each(function() {
                _1d(this, false);
            });
        },
        refresh: function(jq, _26) {
            return jq.each(function() {
                _14(this, _26);
            });
        },
        select: function(jq, _27) {
            return jq.each(function() {
                _10(this, _27);
            });
        }
    };
    $.fn.pagination.parseOptions = function(_28) {
        var t = $(_28);
        return $.extend({},
        $.parser.parseOptions(_28, [{
            total: "number",
            pageSize: "number",
            pageNumber: "number",
            links: "number"
        },
        {
            loading: "boolean",
            showPageList: "boolean",
            showRefresh: "boolean"
        }]), {
            pageList: (t.attr("pageList") ? eval(t.attr("pageList")) : undefined)
        });
    };
    $.fn.pagination.defaults = {
        total: 1,
        pageSize: 10,
        pageNumber: 1,
        pageList: [10, 20, 30, 50],
        loading: false,
        buttons: null,
        showPageList: true,
        showRefresh: true,
        links: 10,
        layout: ["list", "sep", "first", "prev", "sep", "manual", "sep", "next", "last", "sep", "refresh"],
        onSelectPage: function(_29, _2a) {},
        onBeforeRefresh: function(_2b, _2c) {},
        onRefresh: function(_2d, _2e) {},
        onChangePageSize: function(_2f) {},
        beforePageText: "Page",
        afterPageText: "of {pages}",
        displayMsg: "Displaying {from} to {to} of {total} items",
        nav: {
            first: {
                iconCls: "pagination-first",
                handler: function() {
                    var _30 = $(this).pagination("options");
                    if (_30.pageNumber > 1) {
                        $(this).pagination("select", 1);
                    }
                }
            },
            prev: {
                iconCls: "pagination-prev",
                handler: function() {
                    var _31 = $(this).pagination("options");
                    if (_31.pageNumber > 1) {
                        $(this).pagination("select", _31.pageNumber - 1);
                    }
                }
            },
            next: {
                iconCls: "pagination-next",
                handler: function() {
                    var _32 = $(this).pagination("options");
                    var _33 = Math.ceil(_32.total / _32.pageSize);
                    if (_32.pageNumber < _33) {
                        $(this).pagination("select", _32.pageNumber + 1);
                    }
                }
            },
            last: {
                iconCls: "pagination-last",
                handler: function() {
                    var _34 = $(this).pagination("options");
                    var _35 = Math.ceil(_34.total / _34.pageSize);
                    if (_34.pageNumber < _35) {
                        $(this).pagination("select", _35);
                    }
                }
            },
            refresh: {
                iconCls: "pagination-refresh",
                handler: function() {
                    var _36 = $(this).pagination("options");
                    if (_36.onBeforeRefresh.call(this, _36.pageNumber, _36.pageSize) != false) {
                        $(this).pagination("select", _36.pageNumber);
                        _36.onRefresh.call(this, _36.pageNumber, _36.pageSize);
                    }
                }
            }
        }
    };
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.parser.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
$.parser={auto:true,onComplete:function(_1){
},plugins:["draggable","droppable","resizable","pagination","tooltip","linkbutton","menu","menubutton","splitbutton","progressbar","tree","combobox","combotree","combogrid","numberbox","validatebox","searchbox","numberspinner","timespinner","calendar","datebox","datetimebox","slider","layout","panel","datagrid","propertygrid","treegrid","tabs","accordion","window","dialog"],parse:function(_2){
var aa=[];
for(var i=0;i<$.parser.plugins.length;i++){
var _3=$.parser.plugins[i];
var r=$(".easyui-"+_3,_2);
if(r.length){
if(r[_3]){
r[_3]();
}else{
aa.push({name:_3,jq:r});
}
}
}
if(aa.length&&window.easyloader){
var _4=[];
for(var i=0;i<aa.length;i++){
_4.push(aa[i].name);
}
easyloader.load(_4,function(){
for(var i=0;i<aa.length;i++){
var _5=aa[i].name;
var jq=aa[i].jq;
jq[_5]();
}
$.parser.onComplete.call($.parser,_2);
});
}else{
$.parser.onComplete.call($.parser,_2);
}
},parseOptions:function(_6,_7){
var t=$(_6);
var _8={};
var s=$.trim(t.attr("data-options"));
if(s){
if(s.substring(0,1)!="{"){
s="{"+s+"}";
}
_8=(new Function("return "+s))();
}
if(_7){
var _9={};
for(var i=0;i<_7.length;i++){
var pp=_7[i];
if(typeof pp=="string"){
if(pp=="width"||pp=="height"||pp=="left"||pp=="top"){
_9[pp]=parseInt(_6.style[pp])||undefined;
}else{
_9[pp]=t.attr(pp);
}
}else{
for(var _a in pp){
var _b=pp[_a];
if(_b=="boolean"){
_9[_a]=t.attr(_a)?(t.attr(_a)=="true"):undefined;
}else{
if(_b=="number"){
_9[_a]=t.attr(_a)=="0"?0:parseFloat(t.attr(_a))||undefined;
}
}
}
}
}
$.extend(_8,_9);
}
return _8;
}};
$(function(){
var d=$("<div style=\"position:absolute;top:-1000px;width:100px;height:100px;padding:5px\"></div>").appendTo("body");
d.width(100);
$._boxModel=parseInt(d.width())==100;
d.remove();
if(!window.easyloader&&$.parser.auto){
$.parser.parse();
}
});
$.fn._outerWidth=function(_c){
if(_c==undefined){
if(this[0]==window){
return this.width()||document.body.clientWidth;
}
return this.outerWidth()||0;
}
return this.each(function(){
if($._boxModel){
$(this).width(_c-($(this).outerWidth()-$(this).width()));
}else{
$(this).width(_c);
}
});
};
$.fn._outerHeight=function(_d){
if(_d==undefined){
if(this[0]==window){
return this.height()||document.body.clientHeight;
}
return this.outerHeight()||0;
}
return this.each(function(){
if($._boxModel){
$(this).height(_d-($(this).outerHeight()-$(this).height()));
}else{
$(this).height(_d);
}
});
};
$.fn._scrollLeft=function(_e){
if(_e==undefined){
return this.scrollLeft();
}else{
return this.each(function(){
$(this).scrollLeft(_e);
});
}
};
$.fn._propAttr=$.fn.prop||$.fn.attr;
$.fn._fit=function(_f){
_f=_f==undefined?true:_f;
var t=this[0];
var p=(t.tagName=="BODY"?t:this.parent()[0]);
var _10=p.fcount||0;
if(_f){
if(!t.fitted){
t.fitted=true;
p.fcount=_10+1;
$(p).addClass("panel-noscroll");
if(p.tagName=="BODY"){
$("html").addClass("panel-fit");
}
}
}else{
if(t.fitted){
t.fitted=false;
p.fcount=_10-1;
if(p.fcount==0){
$(p).removeClass("panel-noscroll");
if(p.tagName=="BODY"){
$("html").removeClass("panel-fit");
}
}
}
}
return {width:$(p).width(),height:$(p).height()};
};
})(jQuery);
(function($){
var _11=null;
var _12=null;
var _13=false;
function _14(e){
if(e.touches.length!=1){
return;
}
if(!_13){
_13=true;
dblClickTimer=setTimeout(function(){
_13=false;
},500);
}else{
clearTimeout(dblClickTimer);
_13=false;
_15(e,"dblclick");
}
_11=setTimeout(function(){
_15(e,"contextmenu",3);
},1000);
_15(e,"mousedown");
if($.fn.draggable.isDragging||$.fn.resizable.isResizing){
e.preventDefault();
}
};
function _16(e){
if(e.touches.length!=1){
return;
}
if(_11){
clearTimeout(_11);
}
_15(e,"mousemove");
if($.fn.draggable.isDragging||$.fn.resizable.isResizing){
e.preventDefault();
}
};
function _17(e){
if(_11){
clearTimeout(_11);
}
_15(e,"mouseup");
if($.fn.draggable.isDragging||$.fn.resizable.isResizing){
e.preventDefault();
}
};
function _15(e,_18,_19){
var _1a=new $.Event(_18);
_1a.pageX=e.changedTouches[0].pageX;
_1a.pageY=e.changedTouches[0].pageY;
_1a.which=_19||1;
$(e.target).trigger(_1a);
};
if(document.addEventListener){
document.addEventListener("touchstart",_14,true);
document.addEventListener("touchmove",_16,true);
document.addEventListener("touchend",_17,true);
}
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.datagrid.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($) {
    var _1 = 0;
    function _2(a, o) {
        for (var i = 0,
        _3 = a.length; i < _3; i++) {
            if (a[i] == o) {
                return i;
            }
        }
        return - 1;
    };
    function _4(a, o, id) {
        if (typeof o == "string") {
            for (var i = 0,
            _5 = a.length; i < _5; i++) {
                if (a[i][o] == id) {
                    a.splice(i, 1);
                    return;
                }
            }
        } else {
            var _6 = _2(a, o);
            if (_6 != -1) {
                a.splice(_6, 1);
            }
        }
    };
    function _7(a, o, r) {
        for (var i = 0,
        _8 = a.length; i < _8; i++) {
            if (a[i][o] == r[o]) {
                return;
            }
        }
        a.push(r);
    };
    function _9(_a) {
        var cc = _a || $("head");
        var _b = $.data(cc[0], "ss");
        if (!_b) {
            _b = $.data(cc[0], "ss", {
                cache: {},
                dirty: []
            });
        }
        return {
            add: function(_c) {
                var ss = ["<style type=\"text/css\">"];
                for (var i = 0; i < _c.length; i++) {
                    _b.cache[_c[i][0]] = {
                        width: _c[i][1]
                    };
                }
                var _d = 0;
                for (var s in _b.cache) {
                    var _e = _b.cache[s];
                    _e.index = _d++;
                    ss.push(s + "{width:" + _e.width + "}");
                }
                ss.push("</style>");
                $(ss.join("\n")).appendTo(cc);
                setTimeout(function() {
                    cc.children("style:not(:last)").remove();
                },
                0);
            },
            getRule: function(_f) {
                var _10 = cc.children("style:last")[0];
                var _11 = _10.styleSheet ? _10.styleSheet: (_10.sheet || document.styleSheets[document.styleSheets.length - 1]);
                var _12 = _11.cssRules || _11.rules;
                return _12[_f];
            },
            set: function(_13, _14) {
                var _15 = _b.cache[_13];
                if (_15) {
                    _15.width = _14;
                    var _16 = this.getRule(_15.index);
                    if (_16) {
                        _16.style["width"] = _14;
                    }
                }
            },
            remove: function(_17) {
                var tmp = [];
                for (var s in _b.cache) {
                    if (s.indexOf(_17) == -1) {
                        tmp.push([s, _b.cache[s].width]);
                    }
                }
                _b.cache = {};
                this.add(tmp);
            },
            dirty: function(_18) {
                if (_18) {
                    _b.dirty.push(_18);
                }
            },
            clean: function() {
                for (var i = 0; i < _b.dirty.length; i++) {
                    this.remove(_b.dirty[i]);
                }
                _b.dirty = [];
            }
        };
    };
    function _19(_1a, _1b) {
        var _1c = $.data(_1a, "datagrid").options;
        var _1d = $.data(_1a, "datagrid").panel;
        if (_1b) {
            if (_1b.width) {
                _1c.width = _1b.width;
            }
            if (_1b.height) {
                _1c.height = _1b.height;
            }
        }
        if (_1c.fit == true) {
            var p = _1d.panel("panel").parent();
            _1c.width = p.width();
            _1c.height = p.height();
        }
        _1d.panel("resize", {
            width: _1c.width,
            height: _1c.height
        });
    };
    function _1e(_1f) {
        var _20 = $.data(_1f, "datagrid").options;
        var dc = $.data(_1f, "datagrid").dc;
        var _21 = $.data(_1f, "datagrid").panel;
        var _22 = _21.width();
        var _23 = _21.height();
        var _24 = dc.view;
        var _25 = dc.view1;
        var _26 = dc.view2;
        var _27 = _25.children("div.datagrid-header");
        var _28 = _26.children("div.datagrid-header");
        var _29 = _27.find("table");
        var _2a = _28.find("table");
        _24.width(_22);
        var _2b = _27.children("div.datagrid-header-inner").show();
        _25.width(_2b.find("table").width());
        if (!_20.showHeader) {
            _2b.hide();
        }
        _26.width(_22 - _25._outerWidth());
        _25.children("div.datagrid-header,div.datagrid-body,div.datagrid-footer").width(_25.width());
        _26.children("div.datagrid-header,div.datagrid-body,div.datagrid-footer").width(_26.width());
        var hh;
        _27.css("height", "");
        _28.css("height", "");
        _29.css("height", "");
        _2a.css("height", "");
        hh = Math.max(_29.height(), _2a.height());
        _29.height(hh);
        _2a.height(hh);
        _27.add(_28)._outerHeight(hh);
        if (_20.height != "auto") {
            var _2c = _23 - _26.children("div.datagrid-header")._outerHeight() - _26.children("div.datagrid-footer")._outerHeight() - _21.children("div.datagrid-toolbar")._outerHeight();
            _21.children("div.datagrid-pager").each(function() {
                //_2c-=$(this)._outerHeight();
            });
            dc.body1.add(dc.body2).children("table.datagrid-btable-frozen").css({
                position: "absolute",
                top: dc.header2._outerHeight()
            });
            var _2d = dc.body2.children("table.datagrid-btable-frozen")._outerHeight();
            _25.add(_26).children("div.datagrid-body").css({
                marginTop: _2d,
                height: (_2c - _2d)
            });
        }
        _24.height(_26.height());
    };
    function _2e(_2f, _30, _31) {
        var _32 = $.data(_2f, "datagrid").data.rows;
        var _33 = $.data(_2f, "datagrid").options;
        var dc = $.data(_2f, "datagrid").dc;
        if (!dc.body1.is(":empty") && (!_33.nowrap || _33.autoRowHeight || _31)) {
            if (_30 != undefined) {
                var tr1 = _33.finder.getTr(_2f, _30, "body", 1);
                var tr2 = _33.finder.getTr(_2f, _30, "body", 2);
                _34(tr1, tr2);
            } else {
                var tr1 = _33.finder.getTr(_2f, 0, "allbody", 1);
                var tr2 = _33.finder.getTr(_2f, 0, "allbody", 2);
                _34(tr1, tr2);
                if (_33.showFooter) {
                    var tr1 = _33.finder.getTr(_2f, 0, "allfooter", 1);
                    var tr2 = _33.finder.getTr(_2f, 0, "allfooter", 2);
                    _34(tr1, tr2);
                }
            }
        }
        _1e(_2f);
        if (_33.height == "auto") {
            var _35 = dc.body1.parent();
            var _36 = dc.body2;
            var _37 = _38(_36);
            var _39 = _37.height;
            if (_37.width > _36.width()) {
                _39 += 18;
            }
            _35.height(_39);
            _36.height(_39);
            dc.view.height(dc.view2.height());
        }
        dc.body2.triggerHandler("scroll");
        function _34(_3a, _3b) {
            for (var i = 0; i < _3b.length; i++) {
                var tr1 = $(_3a[i]);
                var tr2 = $(_3b[i]);
                tr1.css("height", "");
                tr2.css("height", "");
                var _3c = Math.max(tr1.height(), tr2.height());
                tr1.css("height", _3c);
                tr2.css("height", _3c);
            }
        };
        function _38(cc) {
            var _3d = 0;
            var _3e = 0;
            $(cc).children().each(function() {
                var c = $(this);
                if (c.is(":visible")) {
                    _3e += c._outerHeight();
                    if (_3d < c._outerWidth()) {
                        _3d = c._outerWidth();
                    }
                }
            });
            return {
                width: _3d,
                height: _3e
            };
        };
    };
    function _3f(_40, _41) {
        var _42 = $.data(_40, "datagrid");
        var _43 = _42.options;
        var dc = _42.dc;
        if (!dc.body2.children("table.datagrid-btable-frozen").length) {
            dc.body1.add(dc.body2).prepend("<table class=\"datagrid-btable datagrid-btable-frozen\" cellspacing=\"0\" cellpadding=\"0\"></table>");
        }
        _44(true);
        _44(false);
        _1e(_40);
        function _44(_45) {
            var _46 = _45 ? 1 : 2;
            var tr = _43.finder.getTr(_40, _41, "body", _46); (_45 ? dc.body1: dc.body2).children("table.datagrid-btable-frozen").append(tr);
        };
    };
    function _47(_48, _49) {
        function _4a() {
            var _4b = [];
            var _4c = [];
            $(_48).children("thead").each(function() {
                var opt = $.parser.parseOptions(this, [{
                    frozen: "boolean"
                }]);
                $(this).find("tr").each(function() {
                    var _4d = [];
                    $(this).find("th").each(function() {
                        var th = $(this);
                        var col = $.extend({},
                        $.parser.parseOptions(this, ["field", "align", "halign", "order", {
                            sortable: "boolean",
                            checkbox: "boolean",
                            resizable: "boolean",
                            fixed: "boolean"
                        },
                        {
                            rowspan: "number",
                            colspan: "number",
                            width: "number"
                        }]), {
                            title: (th.html() || undefined),
                            hidden: (th.attr("hidden") ? true: undefined),
                            formatter: (th.attr("formatter") ? eval(th.attr("formatter")) : undefined),
                            styler: (th.attr("styler") ? eval(th.attr("styler")) : undefined),
                            sorter: (th.attr("sorter") ? eval(th.attr("sorter")) : undefined)
                        });
                        if (th.attr("editor")) {
                            var s = $.trim(th.attr("editor"));
                            if (s.substr(0, 1) == "{") {
                                col.editor = eval("(" + s + ")");
                            } else {
                                col.editor = s;
                            }
                        }
                        _4d.push(col);
                    });
                    opt.frozen ? _4b.push(_4d) : _4c.push(_4d);
                });
            });
            return [_4b, _4c];
        };
        var _4e = $("<div class=\"datagrid-wrap\">" + "<div class=\"datagrid-view\">" + "<div class=\"datagrid-view1\">" + "<div class=\"datagrid-header\">" + "<div class=\"datagrid-header-inner\"></div>" + "</div>" + "<div class=\"datagrid-body\">" + "<div class=\"datagrid-body-inner\"></div>" + "</div>" + "<div class=\"datagrid-footer\">" + "<div class=\"datagrid-footer-inner\"></div>" + "</div>" + "</div>" + "<div class=\"datagrid-view2\">" + "<div class=\"datagrid-header\">" + "<div class=\"datagrid-header-inner\"></div>" + "</div>" + "<div class=\"datagrid-body\"></div>" + "<div class=\"datagrid-footer\">" + "<div class=\"datagrid-footer-inner\"></div>" + "</div>" + "</div>" + "</div>" + "</div>").insertAfter(_48);
        _4e.panel({
            doSize: false
        });
        _4e.panel("panel").addClass("datagrid").bind("_resize",
        function(e, _4f) {
            var _50 = $.data(_48, "datagrid").options;
            if (_50.fit == true || _4f) {
                _19(_48);
                setTimeout(function() {
                    if ($.data(_48, "datagrid")) {
                        _51(_48);
                    }
                },
                0);
            }
            return false;
        });
        $(_48).hide().appendTo(_4e.children("div.datagrid-view"));
        var cc = _4a();
        var _52 = _4e.children("div.datagrid-view");
        var _53 = _52.children("div.datagrid-view1");
        var _54 = _52.children("div.datagrid-view2");
        var _55 = _4e.closest("div.datagrid-view");
        if (!_55.length) {
            _55 = _52;
        }
        var ss = _9(_55);
        return {
            panel: _4e,
            frozenColumns: cc[0],
            columns: cc[1],
            dc: {
                view: _52,
                view1: _53,
                view2: _54,
                header1: _53.children("div.datagrid-header").children("div.datagrid-header-inner"),
                header2: _54.children("div.datagrid-header").children("div.datagrid-header-inner"),
                body1: _53.children("div.datagrid-body").children("div.datagrid-body-inner"),
                body2: _54.children("div.datagrid-body"),
                footer1: _53.children("div.datagrid-footer").children("div.datagrid-footer-inner"),
                footer2: _54.children("div.datagrid-footer").children("div.datagrid-footer-inner")
            },
            ss: ss
        };
    };
    function _56(_57) {
        var _58 = $.data(_57, "datagrid");
        var _59 = _58.options;
        var dc = _58.dc;
        var _5a = _58.panel;
        _5a.panel($.extend({},
        _59, {
            id: null,
            doSize: false,
            onResize: function(_5b, _5c) {
                setTimeout(function() {
                    if ($.data(_57, "datagrid")) {
                        _1e(_57);
                        _8d(_57);
                        _59.onResize.call(_5a, _5b, _5c);
                    }
                },
                0);
            },
            onExpand: function() {
                _2e(_57);
                _59.onExpand.call(_5a);
            }
        }));
        _58.rowIdPrefix = "datagrid-row-r" + (++_1);
        _58.cellClassPrefix = "datagrid-cell-c" + _1;
        _5d(dc.header1, _59.frozenColumns, true);
        _5d(dc.header2, _59.columns, false);
        _5e();
        dc.header1.add(dc.header2).css("display", _59.showHeader ? "block": "none");
        dc.footer1.add(dc.footer2).css("display", _59.showFooter ? "block": "none");
        if (_59.toolbar) {
            //removed toolbar support 2015/11/11
        } else {
            $("div.datagrid-toolbar", _5a).remove();
        }
        $("div.datagrid-pager", _5a).remove();
        if (_59.pagination) {
            var _60 = $("<div class=\"datagrid-pager\"></div>");
            if (_59.pagePosition == "bottom") {
                _60.appendTo(_5a);
            } else {
                if (_59.pagePosition == "top") {
                    _60.addClass("datagrid-pager-top").prependTo(_5a);
                } else {
                    var _61 = $("<div class=\"datagrid-pager datagrid-pager-top\"></div>").prependTo(_5a);
                    _60.appendTo(_5a);
                    _60 = _60.add(_61);
                }
            }
            _60.pagination({
                total: (_59.pageNumber * _59.pageSize),
                pageNumber: _59.pageNumber,
                pageSize: _59.pageSize,
                pageList: _59.pageList,
                onSelectPage: function(_62, _63) {
                    _59.pageNumber = _62;
                    _59.pageSize = _63;
                    _60.pagination("refresh", {
                        pageNumber: _62,
                        pageSize: _63
                    });
                    _16b(_57);
                }
            });
            _59.pageSize = _60.pagination("options").pageSize;
        }
        function _5d(_64, _65, _66) {
            if (!_65) {
                return;
            }
            $(_64).show();
            $(_64).empty();
            var _67 = [];
            var _68 = [];
            if (_59.sortName) {
                _67 = _59.sortName.split(",");
                _68 = _59.sortOrder.split(",");
            }
            var t = $("<table class=\"datagrid-htable\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tbody></tbody></table>").appendTo(_64);
            for (var i = 0; i < _65.length; i++) {
                var tr = $("<tr class=\"datagrid-header-row\"></tr>").appendTo($("tbody", t));
                var _69 = _65[i];
                for (var j = 0; j < _69.length; j++) {
                    var col = _69[j];
                    //by murmur
                    if(/\(元\)/.test(col.title) && typeof(col.precision)=="undefined"){
                        col.precision = 2;
                    }   
                    var _6a = "";
                    if (col.rowspan) {
                        _6a += "rowspan=\"" + col.rowspan + "\" ";
                    }
                    if (col.colspan) {
                        _6a += "colspan=\"" + col.colspan + "\" ";
                    }
                    var td = $("<td " + _6a + "></td>").appendTo(tr);
                    if (col.checkbox) {
                        td.attr("field", col.field);
                        $("<div class=\"datagrid-header-check\"></div>").html("<input type=\"checkbox\"/>").appendTo(td);
                    } else {
                        if (col.field) {
                            td.attr("field", col.field);
                            td.append("<div class=\"datagrid-cell\"><span></span><span class=\"datagrid-sort-icon\"></span></div>");
                            $("span", td).html(col.title);
                            $("span.datagrid-sort-icon", td).html("&nbsp;");
                            var _6b = td.find("div.datagrid-cell");
                            var pos = _2(_67, col.field);
                            if (pos >= 0) {
                                _6b.addClass("datagrid-sort-" + _68[pos]);
                            }
                            if (col.resizable == false) {
                                _6b.attr("resizable", "false");
                            }
                            if (col.width) {
                                _6b._outerWidth(col.width);
                                col.boxWidth = parseInt(_6b[0].style.width);                                
                            } else {
                                col.auto = true;
                            }
                            _6b.css("text-align", (col.halign || col.align || ""));
                            col.cellClass = _58.cellClassPrefix + "-" + col.field.replace(/[\.|\s]/g, "-");
                            _6b.addClass(col.cellClass).css("width", "");
                        } else {
                            $("<div class=\"datagrid-cell-group\"></div>").html(col.title).appendTo(td);
                        }
                    }
                    if (col.hidden) {
                        td.hide();
                    }
                }
            }
            if (_66 && _59.rownumbers) {
                var td = $("<td rowspan=\"" + _59.frozenColumns.length + "\"><div class=\"datagrid-header-rownumber\"></div></td>");
                if ($("tr", t).length == 0) {
                    td.wrap("<tr class=\"datagrid-header-row\"></tr>").parent().appendTo($("tbody", t));
                } else {
                    td.prependTo($("tr:first", t));
                }
            }
        };
        function _5e() {
            var _6c = [];
            var _6d = _6e(_57, true).concat(_6e(_57));
            for (var i = 0; i < _6d.length; i++) {
                var col = _6f(_57, _6d[i]);
                if (col && !col.checkbox) {
                    _6c.push(["." + col.cellClass, col.boxWidth ? col.boxWidth + "px": "auto"]);
                }
            }
            _58.ss.add(_6c);
            _58.ss.dirty(_58.cellSelectorPrefix);
            _58.cellSelectorPrefix = "." + _58.cellClassPrefix;
        };
    };
    function _70(_71) {
        var _72 = $.data(_71, "datagrid");
        var _73 = _72.panel;
        var _74 = _72.options;
        var dc = _72.dc;
        var _75 = dc.header1.add(dc.header2);
        _75.find("input[type=checkbox]").unbind(".datagrid").bind("click.datagrid",
        function(e) {
            if (_74.singleSelect && _74.selectOnCheck) {
                return false;
            }
            if ($(this).is(":checked")) {
                _106(_71);
            } else {
                _10c(_71);
            }
            e.stopPropagation();
        });
        var _76 = _75.find("div.datagrid-cell");
        _76.closest("td").unbind(".datagrid").bind("mouseenter.datagrid",
        function() {
            if (_72.resizing) {
                return;
            }
            $(this).addClass("datagrid-header-over");
        }).bind("mouseleave.datagrid",
        function() {
            $(this).removeClass("datagrid-header-over");
        }).bind("contextmenu.datagrid",
        function(e) {
            var _77 = $(this).attr("field");
            _74.onHeaderContextMenu.call(_71, e, _77);
        });
        _76.unbind(".datagrid").bind("click.datagrid",
        function(e) {
            var p1 = $(this).offset().left + 5;
            var p2 = $(this).offset().left + $(this)._outerWidth() - 5;
            if (e.pageX < p2 && e.pageX > p1) {
                var _78 = $(this).parent().attr("field");
                var col = _6f(_71, _78);
                if (!col.sortable || _72.resizing) {
                    return;
                }
                var _79 = [];
                var _7a = [];
                if (_74.sortName) {
                    _79 = _74.sortName.split(",");
                    _7a = _74.sortOrder.split(",");
                }
                var pos = _2(_79, _78);
                var _7b = col.order || "asc";
                if (pos >= 0) {
                    $(this).removeClass("datagrid-sort-asc datagrid-sort-desc");
                    var _7c = _7a[pos] == "asc" ? "desc": "asc";
                    if (_74.multiSort && _7c == _7b) {
                        _79.splice(pos, 1);
                        _7a.splice(pos, 1);
                    } else {
                        _7a[pos] = _7c;
                        $(this).addClass("datagrid-sort-" + _7c);
                    }
                } else {
                    if (_74.multiSort) {
                        _79.push(_78);
                        _7a.push(_7b);
                    } else {
                        _79 = [_78];
                        _7a = [_7b];
                        _76.removeClass("datagrid-sort-asc datagrid-sort-desc");
                    }
                    $(this).addClass("datagrid-sort-" + _7b);
                }
                _74.sortName = _79.join(",");
                _74.sortOrder = _7a.join(",");
                if (_74.remoteSort) {
                    _16b(_71);
                } else {
                    var _7d = $.data(_71, "datagrid").data;
                    _c6(_71, _7d);
                }
                _74.onSortColumn.call(_71, _74.sortName, _74.sortOrder);
            }
        }).bind("dblclick.datagrid",
        function(e) {
            var p1 = $(this).offset().left + 5;
            var p2 = $(this).offset().left + $(this)._outerWidth() - 5;
            var _7e = _74.resizeHandle == "right" ? (e.pageX > p2) : (_74.resizeHandle == "left" ? (e.pageX < p1) : (e.pageX < p1 || e.pageX > p2));
            if (_7e) {
                var _7f = $(this).parent().attr("field");
                var col = _6f(_71, _7f);
                if (col.resizable == false) {
                    return;
                }
                $(_71).datagrid("autoSizeColumn", _7f);
                col.auto = false;
            }
        });
        var _80 = _74.resizeHandle == "right" ? "e": (_74.resizeHandle == "left" ? "w": "e,w");
        _76.each(function() {
            $(this).resizable({
                handles: _80,
                disabled: ($(this).attr("resizable") ? $(this).attr("resizable") == "false": false),
                minWidth: 25,
                onStartResize: function(e) {
                    _72.resizing = true;
                    _75.css("cursor", $("body").css("cursor"));
                    if (!_72.proxy) {
                        _72.proxy = $("<div class=\"datagrid-resize-proxy\"></div>").appendTo(dc.view);
                    }
                    _72.proxy.css({
                        left: e.pageX - $(_73).offset().left - 1,
                        display: "none"
                    });
                    setTimeout(function() {
                        if (_72.proxy) {
                            _72.proxy.show();
                        }
                    },
                    500);
                },
                onResize: function(e) {
                    _72.proxy.css({
                        left: e.pageX - $(_73).offset().left - 1,
                        display: "block"
                    });
                    return false;
                },
                onStopResize: function(e) {
                    _75.css("cursor", "");
                    $(this).css("height", "");
                    $(this)._outerWidth($(this)._outerWidth());
                    var _81 = $(this).parent().attr("field");
                    var col = _6f(_71, _81);
                    col.width = $(this)._outerWidth();
                    col.boxWidth = parseInt(this.style.width);
                    col.auto = undefined;
                    $(this).css("width", "");
                    _51(_71, _81);
                    _72.proxy.remove();
                    _72.proxy = null;
                    if ($(this).parents("div:first.datagrid-header").parent().hasClass("datagrid-view1")) {
                        _1e(_71);
                    }
                    _8d(_71);
                    _74.onResizeColumn.call(_71, _81, col.width);
                    setTimeout(function() {
                        _72.resizing = false;
                    },
                    0);
                }
            });
        });
        dc.body1.add(dc.body2).unbind().bind("mouseover",
        function(e) {
            if (_72.resizing) {
                return;
            }
            var tr = $(e.target).closest("tr.datagrid-row");
            if (!_82(tr)) {
                return;
            }
            var _83 = _84(tr);
            _eb(_71, _83);
            e.stopPropagation();
        }).bind("mouseout",
        function(e) {
            var tr = $(e.target).closest("tr.datagrid-row");
            if (!_82(tr)) {
                return;
            }
            var _85 = _84(tr);
            _74.finder.getTr(_71, _85).removeClass("datagrid-row-over");
            e.stopPropagation();
        }).bind("click",
        function(e) {
            var tt = $(e.target);
            var tr = tt.closest("tr.datagrid-row");
            if (!_82(tr)) {
                return;
            }
            var _86 = _84(tr);
            if (tt.parent().hasClass("datagrid-cell-check")) {
                if (_74.singleSelect && _74.selectOnCheck) {
                    if (!_74.checkOnSelect) {
                        _10c(_71, true);
                    }
                    _f8(_71, _86);
                } else {
                    if (tt.is(":checked")) {
                        _f8(_71, _86);
                    } else {
                        _100(_71, _86);
                    }
                }
            } else {
                var row = _74.finder.getRow(_71, _86);
                var td = tt.closest("td[field]", tr);
                if (td.length) {
                    var _87 = td.attr("field");
                    _74.onClickCell.call(_71, _86, _87, row[_87]);
                }
                if (_74.singleSelect == true) {
                    _f0(_71, _86);
                } else {
                    if (tr.hasClass("datagrid-row-selected")) {
                        _f9(_71, _86);
                    } else {
                        _f0(_71, _86);
                    }
                }
                _74.onClickRow.call(_71, _86, row);
            }
            e.stopPropagation();
        }).bind("dblclick",
        function(e) {
            var tt = $(e.target);
            var tr = tt.closest("tr.datagrid-row");
            if (!_82(tr)) {
                return;
            }
            var _88 = _84(tr);
            var row = _74.finder.getRow(_71, _88);
            var td = tt.closest("td[field]", tr);
            if (td.length) {
                var _89 = td.attr("field");
                _74.onDblClickCell.call(_71, _88, _89, row[_89]);
            }
            FW.pushEvent("双击表格行","id=" + $(_71).attr("id"),row);
            _74.onDblClickRow.call(_71, _88, row);
            e.stopPropagation();
        }).bind("contextmenu",
        function(e) {
            var tr = $(e.target).closest("tr.datagrid-row");
            if (!_82(tr)) {
                return;
            }
            var _8a = _84(tr);
            var row = _74.finder.getRow(_71, _8a);
            _74.onRowContextMenu.call(_71, e, _8a, row);
            e.stopPropagation();
        });
        dc.body2.bind("scroll",
        function() {
            var b1 = dc.view1.children("div.datagrid-body");
            b1.scrollTop($(this).scrollTop());
            var c1 = dc.body1.children(":first");
            var c2 = dc.body2.children(":first");
            if (c1.length && c2.length) {
                var _8b = c1.offset().top;
                var _8c = c2.offset().top;
                if (_8b != _8c) {
                    b1.scrollTop(b1.scrollTop() + _8b - _8c);
                }
            }
            dc.view2.children("div.datagrid-header,div.datagrid-footer")._scrollLeft($(this)._scrollLeft());
            dc.body2.children("table.datagrid-btable-frozen").css("left", -$(this)._scrollLeft());
        });
        function _84(tr) {
            if (tr.attr("datagrid-row-index")) {
                return parseInt(tr.attr("datagrid-row-index"));
            } else {
                return tr.attr("node-id");
            }
        };
        function _82(tr) {
            return tr.length && tr.parent().length;
        };
    };
    function _8d(_8e) {
        var _8f = $.data(_8e, "datagrid");
        var _90 = _8f.options;
        var dc = _8f.dc;
        dc.body2.css("overflow-x", _90.fitColumns ? "hidden": "");
        if (!_90.fitColumns) {
            return;
        }
        if (!_8f.leftWidth) {
            _8f.leftWidth = 0;
        }
        var _91 = dc.view2.children("div.datagrid-header");
        var _92 = 0;
        var _93;
        var _94 = _6e(_8e, false);
        for (var i = 0; i < _94.length; i++) {
            var col = _6f(_8e, _94[i]);
            if (_95(col)) {
                _92 += col.width;
                _93 = col;
            }
        }
        if (!_92) {
            return;
        }
        if (_93) {
            _96(_93, -_8f.leftWidth);
        }
        var _97 = _91.children("div.datagrid-header-inner").show();
        var _98 = _91.width() - _91.find("table").width() - _90.scrollbarSize + _8f.leftWidth;
        var _99 = _98 / _92;
        if (!_90.showHeader) {
            _97.hide();
        }
        for (var i = 0; i < _94.length; i++) {
            var col = _6f(_8e, _94[i]);
            if (_95(col)) {
                var _9a = parseInt(col.width * _99);
                _96(col, _9a);
                _98 -= _9a;
            }
        }
        _8f.leftWidth = _98;
        if (_93) {
            _96(_93, _8f.leftWidth);
        }
        _51(_8e);
        function _96(col, _9b) {
            col.width += _9b;
            col.boxWidth += _9b;
        };
        function _95(col) {
            if (!col.hidden && !col.checkbox && !col.auto && !col.fixed) {
                return true;
            }
        };
    };
    function _9c(_9d, _9e) {
        var _9f = $.data(_9d, "datagrid");
        var _a0 = _9f.options;
        var dc = _9f.dc;
        var tmp = $("<div class=\"datagrid-cell\" style=\"position:absolute;left:-9999px\"></div>").appendTo("body");
        if (_9e) {
            _19(_9e);
            if (_a0.fitColumns) {
                _1e(_9d);
                _8d(_9d);
            }
        } else {
            var _a1 = false;
            var _a2 = _6e(_9d, true).concat(_6e(_9d, false));
            for (var i = 0; i < _a2.length; i++) {
                var _9e = _a2[i];
                var col = _6f(_9d, _9e);
                if (col.auto) {
                    _19(_9e);
                    _a1 = true;
                }
            }
            if (_a1 && _a0.fitColumns) {
                _1e(_9d);
                _8d(_9d);
            }
        }
        tmp.remove();
        function _19(_a3) {
            var _a4 = dc.view.find("div.datagrid-header td[field=\"" + _a3 + "\"] div.datagrid-cell");
            _a4.css("width", "");
            var col = $(_9d).datagrid("getColumnOption", _a3);
            col.width = undefined;
            col.boxWidth = undefined;
            col.auto = true;
            $(_9d).datagrid("fixColumnSize", _a3);
            var _a5 = Math.max(_a6("header"), _a6("allbody"), _a6("allfooter"));
            _a4._outerWidth(_a5);
            col.width = _a5;
            col.boxWidth = parseInt(_a4[0].style.width);
            _a4.css("width", "");
            $(_9d).datagrid("fixColumnSize", _a3);
            _a0.onResizeColumn.call(_9d, _a3, col.width);
            function _a6(_a7) {
                var _a8 = 0;
                if (_a7 == "header") {
                    _a8 = _a9(_a4);
                } else {
                    _a0.finder.getTr(_9d, 0, _a7).find("td[field=\"" + _a3 + "\"] div.datagrid-cell").each(function() {
                        var w = _a9($(this));
                        if (_a8 < w) {
                            _a8 = w;
                        }
                    });
                }
                return _a8;
                function _a9(_aa) {
                    return _aa.is(":visible") ? _aa._outerWidth() : tmp.html(_aa.html())._outerWidth();
                };
            };
        };
    };
    function _51(_ab, _ac) {
        var _ad = $.data(_ab, "datagrid");
        var _ae = _ad.options;
        var dc = _ad.dc;
        var _af = dc.view.find("table.datagrid-btable,table.datagrid-ftable");
        _af.css("table-layout", "fixed");
        if (_ac) {
            fix(_ac);
        } else {
            var ff = _6e(_ab, true).concat(_6e(_ab, false));
            for (var i = 0; i < ff.length; i++) {
                fix(ff[i]);
            }
        }
        _af.css("table-layout", "auto");
        _b0(_ab);
        setTimeout(function() {
            _2e(_ab);
            _b5(_ab);
        },
        0);
        function fix(_b1) {
            var col = _6f(_ab, _b1);
            if (!col.checkbox) {            	
                _ad.ss.set("." + col.cellClass, col.boxWidth ? col.boxWidth + "px": "auto");
            }
        };
    };
    function _b0(_b2) {
        var dc = $.data(_b2, "datagrid").dc;
        dc.body1.add(dc.body2).find("td.datagrid-td-merged").each(function() {
            var td = $(this);
            var _b3 = td.attr("colspan") || 1;
            var _b4 = _6f(_b2, td.attr("field")).width;
            for (var i = 1; i < _b3; i++) {
                td = td.next();
                _b4 += _6f(_b2, td.attr("field")).width + 1;
            }
            $(this).children("div.datagrid-cell")._outerWidth(_b4);
        });
    };
    function _b5(_b6) {
        var dc = $.data(_b6, "datagrid").dc;
        dc.view.find("div.datagrid-editable").each(function() {
            var _b7 = $(this);
            var _b8 = _b7.parent().attr("field");
            var col = $(_b6).datagrid("getColumnOption", _b8);
            _b7._outerWidth(col.width);
            var ed = $.data(this, "datagrid.editor");
            if (ed.actions.resize) {
                ed.actions.resize(ed.target, _b7.width());
            }
        });
    };
    function _6f(_b9, _ba) {
        function _bb(_bc) {
            if (_bc) {
                for (var i = 0; i < _bc.length; i++) {
                    var cc = _bc[i];
                    for (var j = 0; j < cc.length; j++) {
                        var c = cc[j];
                        if (c.field == _ba) {
                            return c;
                        }
                    }
                }
            }
            return null;
        };
        var _bd = $.data(_b9, "datagrid").options;
        var col = _bb(_bd.columns);
        if (!col) {
            col = _bb(_bd.frozenColumns);
        }
        return col;
    };
    function _6e(_be, _bf) {
        var _c0 = $.data(_be, "datagrid").options;
        var _c1 = (_bf == true) ? (_c0.frozenColumns || [[]]) : _c0.columns;
        if (_c1.length == 0) {
            return [];
        }
        var _c2 = [];
        function _c3(_c4) {
            var c = 0;
            var i = 0;
            while (true) {
                if (_c2[i] == undefined) {
                    if (c == _c4) {
                        return i;
                    }
                    c++;
                }
                i++;
            }
        };
        function _c5(r) {
            var ff = [];
            var c = 0;
            for (var i = 0; i < _c1[r].length; i++) {
                var col = _c1[r][i];
                if (col.field) {
                    ff.push([c, col.field]);
                }
                c += parseInt(col.colspan || "1");
            }
            for (var i = 0; i < ff.length; i++) {
                ff[i][0] = _c3(ff[i][0]);
            }
            for (var i = 0; i < ff.length; i++) {
                var f = ff[i];
                _c2[f[0]] = f[1];
            }
        };
        for (var i = 0; i < _c1.length; i++) {
            _c5(i);
        }
        return _c2;
    };
    function _c6(_c7, _c8) {
        var _c9 = $.data(_c7, "datagrid");
        var _ca = _c9.options;
        var dc = _c9.dc;
        _c8 = _ca.loadFilter.call(_c7, _c8);
        _c8.total = parseInt(_c8.total);
        _c9.data = _c8;
        if (_c8.footer) {
            _c9.footer = _c8.footer;
        }
        if (!_ca.remoteSort && _ca.sortName) {
            var _cb = _ca.sortName.split(",");
            var _cc = _ca.sortOrder.split(",");
            _c8.rows.sort(function(r1, r2) {
                var r = 0;
                for (var i = 0; i < _cb.length; i++) {
                    var sn = _cb[i];
                    var so = _cc[i];
                    var col = _6f(_c7, sn);
                    var _cd = col.sorter ||
                    function(a, b) {
                        return a == b ? 0 : (a > b ? 1 : -1);
                    };
                    r = _cd(r1[sn], r2[sn]) * (so == "asc" ? 1 : -1);
                    if (r != 0) {
                        return r;
                    }
                }
                return r;
            });
        }
        if (_ca.view.onBeforeRender) {
            _ca.view.onBeforeRender.call(_ca.view, _c7, _c8.rows);
        }
        _ca.view.render.call(_ca.view, _c7, dc.body2, false);
        _ca.view.render.call(_ca.view, _c7, dc.body1, true);
        if (_ca.showFooter) {
            _ca.view.renderFooter.call(_ca.view, _c7, dc.footer2, false);
            _ca.view.renderFooter.call(_ca.view, _c7, dc.footer1, true);
        }
        if (_ca.view.onAfterRender) {
            _ca.view.onAfterRender.call(_ca.view, _c7);
        }
        _c9.ss.clean();
        _ca.onLoadSuccess.call(_c7, _c8);
        var _ce = $(_c7).datagrid("getPager");
        if (_ce.length) {
            var _cf = _ce.pagination("options");
            if (_cf.total != _c8.total) {
                _ce.pagination("refresh", {
                    total: _c8.total
                });
                if (_ca.pageNumber != _cf.pageNumber) {
                    _ca.pageNumber = _cf.pageNumber;
                    _16b(_c7);
                }
            }
        }
        _2e(_c7);
        dc.body2.triggerHandler("scroll");
        _d0();
        $(_c7).datagrid("autoSizeColumn");
        _ca.onRenderFinish.call(_c7,_c8);
        function _d0() {
            if (_ca.idField) {
                for (var i = 0; i < _c8.rows.length; i++) {
                    var row = _c8.rows[i];
                    if (_d1(_c9.selectedRows, row)) {
                        _ca.finder.getTr(_c7, i).addClass("datagrid-row-selected");
                    }
                    if (_d1(_c9.checkedRows, row)) {
                        var p = _ca.finder.getTr(_c7, i).find("div.datagrid-cell-check input[type=checkbox]");
                        p._propAttr("checked", true);
                        _itc_togglecheck(p,true);
                    }
                }
            }
            function _d1(a, r) {
                for (var i = 0; i < a.length; i++) {
                    if (a[i][_ca.idField] == r[_ca.idField]) {
                        a[i] = r;
                        return true;
                    }
                }
                return false;
            };
        };
    };
    function _d2(_d3, row) {
        var _d4 = $.data(_d3, "datagrid");
        var _d5 = _d4.options;
        var _d6 = _d4.data.rows;
        if (typeof row == "object") {
            return _2(_d6, row);
        } else {
            for (var i = 0; i < _d6.length; i++) {
                if (_d6[i][_d5.idField] == row) {
                    return i;
                }
            }
            return - 1;
        }
    };
    function _d7(_d8) {
        var _d9 = $.data(_d8, "datagrid");
        var _da = _d9.options;
        var _db = _d9.data;
        if (_da.idField) {
            return _d9.selectedRows;
        } else {
            var _dc = [];
            _da.finder.getTr(_d8, "", "selected", 2).each(function() {
                var _dd = parseInt($(this).attr("datagrid-row-index"));
                _dc.push(_db.rows[_dd]);
            });
            return _dc;
        }
    };
    function _de(_df) {
        var _e0 = $.data(_df, "datagrid");
        var _e1 = _e0.options;
        if (_e1.idField) {
            return _e0.checkedRows;
        } else {
            var _e2 = [];
            _e1.finder.getTr(_df, "", "checked", 2).each(function() {
                _e2.push(_e1.finder.getRow(_df, $(this)));
            });
            return _e2;
        }
    };
    function _e3(_e4, _e5) {
        var _e6 = $.data(_e4, "datagrid");
        var dc = _e6.dc;
        var _e7 = _e6.options;
        var tr = _e7.finder.getTr(_e4, _e5);
        if (tr.length) {
            if (tr.closest("table").hasClass("datagrid-btable-frozen")) {
                return;
            }
            var _e8 = dc.view2.children("div.datagrid-header")._outerHeight();
            var _e9 = dc.body2;
            var _ea = _e9.outerHeight(true) - _e9.outerHeight();
            var top = tr.position().top - _e8 - _ea;
            if (top < 0) {
                _e9.scrollTop(_e9.scrollTop() + top);
            } else {
                if (top + tr._outerHeight() > _e9.height() - 18) {
                    _e9.scrollTop(_e9.scrollTop() + top + tr._outerHeight() - _e9.height() + 18);
                }
            }
        }
    };
    function _eb(_ec, _ed) {
        var _ee = $.data(_ec, "datagrid");
        var _ef = _ee.options;
        _ef.finder.getTr(_ec, _ee.highlightIndex).removeClass("datagrid-row-over");
        _ef.finder.getTr(_ec, _ed).addClass("datagrid-row-over");
        _ee.highlightIndex = _ed;
    };
    function _f0(_f1, _f2, _f3) {
        var _f4 = $.data(_f1, "datagrid");
        var dc = _f4.dc;
        var _f5 = _f4.options;
        var _f6 = _f4.selectedRows;
        if (_f5.singleSelect) {
            _f7(_f1);
            _f6.splice(0, _f6.length);
        }
        if (!_f3 && _f5.checkOnSelect) {
            _f8(_f1, _f2, true);
        }
        var row = _f5.finder.getRow(_f1, _f2);
        if (_f5.idField) {
            _7(_f6, _f5.idField, row);
        }
        _f5.finder.getTr(_f1, _f2).addClass("datagrid-row-selected");
        _f5.onSelect.call(_f1, _f2, row);
        _e3(_f1, _f2);
    };
    function _f9(_fa, _fb, _fc) {
        var _fd = $.data(_fa, "datagrid");
        var dc = _fd.dc;
        var _fe = _fd.options;
        var _ff = $.data(_fa, "datagrid").selectedRows;
        if (!_fc && _fe.checkOnSelect) {
            _100(_fa, _fb, true);
        }
        _fe.finder.getTr(_fa, _fb).removeClass("datagrid-row-selected");
        var row = _fe.finder.getRow(_fa, _fb);
        if (_fe.idField) {
            _4(_ff, _fe.idField, row[_fe.idField]);
        }
        _fe.onUnselect.call(_fa, _fb, row);
    };
    function _101(_102, _103) {
        var _104 = $.data(_102, "datagrid");
        var opts = _104.options;
        var rows = _104.data.rows;
        var _105 = $.data(_102, "datagrid").selectedRows;
        if (!_103 && opts.checkOnSelect) {
            _106(_102, true);
        }
        opts.finder.getTr(_102, "", "allbody").addClass("datagrid-row-selected");
        if (opts.idField) {
            for (var _107 = 0; _107 < rows.length; _107++) {
                _7(_105, opts.idField, rows[_107]);
            }
        }
        opts.onSelectAll.call(_102, rows);
    };
    function _f7(_108, _109) {
        var _10a = $.data(_108, "datagrid");
        var opts = _10a.options;
        var rows = _10a.data.rows;
        var _10b = $.data(_108, "datagrid").selectedRows;
        if (!_109 && opts.checkOnSelect) {
            _10c(_108, true);
        }
        opts.finder.getTr(_108, "", "selected").removeClass("datagrid-row-selected");
        if (opts.idField) {
            for (var _10d = 0; _10d < rows.length; _10d++) {
                _4(_10b, opts.idField, rows[_10d][opts.idField]);
            }
        }
        opts.onUnselectAll.call(_108, rows);
    };
    function _f8(_10e, _10f, _110) {
        var _111 = $.data(_10e, "datagrid");
        var opts = _111.options;
        if (!_110 && opts.selectOnCheck) {
            _f0(_10e, _10f, true);
        }
        var tr = opts.finder.getTr(_10e, _10f).addClass("datagrid-row-checked");
        var ck = tr.find("div.datagrid-cell-check input[type=checkbox]");
        ck._propAttr("checked", true);
        _itc_togglecheck(ck,true);
        tr = opts.finder.getTr(_10e, "", "checked", 2);
        if (tr.length == _111.data.rows.length) {
            var dc = _111.dc;
            var _112 = dc.header1.add(dc.header2);
            var p = _112.find("input[type=checkbox]");
            p._propAttr("checked", true);
            _itc_togglecheck(p,true);
        }
        var row = opts.finder.getRow(_10e, _10f);
        if (opts.idField) {
            _7(_111.checkedRows, opts.idField, row);
        }
        opts.onCheck.call(_10e, _10f, row);
    };
    function _100(_113, _114, _115) {
        var _116 = $.data(_113, "datagrid");
        var opts = _116.options;
        if (!_115 && opts.selectOnCheck) {
            _f9(_113, _114, true);
        }
        var tr = opts.finder.getTr(_113, _114).removeClass("datagrid-row-checked");
        var ck = tr.find("div.datagrid-cell-check input[type=checkbox]");
        //对应行的选项框
        ck._propAttr("checked", false);
        _itc_togglecheck(ck,false);
        var dc = _116.dc;
        var _117 = dc.header1.add(dc.header2);
        var p =_117.find("input[type=checkbox]");
        //表头选项框
        p._propAttr("checked", false);
        _itc_togglecheck(p,false);
        var row = opts.finder.getRow(_113, _114);
        if (opts.idField) {
            _4(_116.checkedRows, opts.idField, row[opts.idField]);
        }
        opts.onUncheck.call(_113, _114, row);
    };
    function _106(_118, _119) {
        var _11a = $.data(_118, "datagrid");
        var opts = _11a.options;
        var rows = _11a.data.rows;
        if (!_119 && opts.selectOnCheck) {
            _101(_118, true);
        }
        var dc = _11a.dc;
        var hck = dc.header1.add(dc.header2).find("input[type=checkbox]");
        var bck = opts.finder.getTr(_118, "", "allbody").addClass("datagrid-row-checked").find("div.datagrid-cell-check input[type=checkbox]");
        hck.add(bck)._propAttr("checked", true);
        _itc_togglecheck(bck,true);
        if (opts.idField) {
            for (var i = 0; i < rows.length; i++) {
                _7(_11a.checkedRows, opts.idField, rows[i]);
            }
        }
        opts.onCheckAll.call(_118, rows);
    };
    function _10c(_11b, _11c) {
        var _11d = $.data(_11b, "datagrid");
        var opts = _11d.options;
        var rows = _11d.data.rows;
        if (!_11c && opts.selectOnCheck) {
            _f7(_11b, true);
        }
        var dc = _11d.dc;
        var hck = dc.header1.add(dc.header2).find("input[type=checkbox]");
        var bck = opts.finder.getTr(_11b, "", "checked").removeClass("datagrid-row-checked").find("div.datagrid-cell-check input[type=checkbox]");
        hck.add(bck)._propAttr("checked", false);
        _itc_togglecheck(bck,false);
        if (opts.idField) {
            for (var i = 0; i < rows.length; i++) {
                _4(_11d.checkedRows, opts.idField, rows[i][opts.idField]);
            }
        }
        opts.onUncheckAll.call(_11b, rows);
    };
    function _11e(_11f, _120) {
        var opts = $.data(_11f, "datagrid").options;
        var tr = opts.finder.getTr(_11f, _120);
        var row = opts.finder.getRow(_11f, _120);
        if (tr.hasClass("datagrid-row-editing")) {
            return;
        }
        if (opts.onBeforeEdit.call(_11f, _120, row) == false) {
            return;
        }
        tr.addClass("datagrid-row-editing");
        _121(_11f, _120);
        _b5(_11f);
        tr.find("div.datagrid-editable").each(function() {
            var _122 = $(this).parent().attr("field");
            var ed = $.data(this, "datagrid.editor");
            ed.actions.setValue(ed.target, row[_122]);
        });
        _123(_11f, _120);
    };
    function _124(_125, _126, _127) {
        var opts = $.data(_125, "datagrid").options;
        var _128 = $.data(_125, "datagrid").updatedRows;
        var _129 = $.data(_125, "datagrid").insertedRows;
        var tr = opts.finder.getTr(_125, _126);
        var row = opts.finder.getRow(_125, _126);
        if (!tr.hasClass("datagrid-row-editing")) {
            return;
        }
        if (!_127) {
            if (!_123(_125, _126)) {
                return;
            }
            var _12a = false;
            var _12b = {};
            tr.find("div.datagrid-editable").each(function() {
                var _12c = $(this).parent().attr("field");
                var ed = $.data(this, "datagrid.editor");
                var _12d = ed.actions.getValue(ed.target);
                if (row[_12c] != _12d) {
                    row[_12c] = _12d;
                    _12a = true;
                    _12b[_12c] = _12d;
                }
            });
            if (_12a) {
                if (_2(_129, row) == -1) {
                    if (_2(_128, row) == -1) {
                        _128.push(row);
                    }
                }
            }
        }
        tr.removeClass("datagrid-row-editing");
        _12e(_125, _126);
        $(_125).datagrid("refreshRow", _126);
        if (!_127) {
            opts.onAfterEdit.call(_125, _126, row, _12b);
        } else {
            opts.onCancelEdit.call(_125, _126, row);
        }
    };
    function _12f(_130, _131) {
        var opts = $.data(_130, "datagrid").options;
        var tr = opts.finder.getTr(_130, _131);
        var _132 = [];
        tr.children("td").each(function() {
            var cell = $(this).find("div.datagrid-editable");
            if (cell.length) {
                var ed = $.data(cell[0], "datagrid.editor");
                _132.push(ed);
            }
        });
        return _132;
    };
    function _133(_134, _135) {
        var _136 = _12f(_134, _135.index != undefined ? _135.index: _135.id);
        for (var i = 0; i < _136.length; i++) {
            if (_136[i].field == _135.field) {
                return _136[i];
            }
        }
        return null;
    };
    function _121(_137, _138) {
        var opts = $.data(_137, "datagrid").options;
        var tr = opts.finder.getTr(_137, _138);
        var lineNum = tr.attr("datagrid-row-index");
        tr.children("td").each(function() {
            var cell = $(this).find("div.datagrid-cell");
            var _139 = $(this).attr("field");
            var col = _6f(_137, _139);
            if (col && col.editor) {
                var _13a, _13b;
                if (typeof col.editor == "string") {
                    _13a = col.editor;
                } else {
                    _13a = col.editor.type;
                    _13b = col.editor.options;
                }
                var _13c = opts.editors[_13a];
                if (_13c) {
                    var _13d = cell.html();
                    var _13e = cell._outerWidth();
                    cell.addClass("datagrid-editable");
                    cell._outerWidth(_13e);
                    cell.html("<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\"><tr><td></td></tr></table>");
                    cell.children("table").bind("click dblclick contextmenu",
                    function(e) {
                        e.stopPropagation();
                    });
                    $.data(cell[0], "datagrid.editor", {
                        actions: _13c,
                        target: _13c.init(cell.find("td"), _13b,_139,lineNum),//by murmur
                        field: _139,
                        type: _13a,
                        oldHtml: _13d
                    });
                }
            }
        });
        _2e(_137, _138, true);
    };
    function _12e(_13f, _140) {
        var opts = $.data(_13f, "datagrid").options;
        var tr = opts.finder.getTr(_13f, _140);
        tr.children("td").each(function() {
            var cell = $(this).find("div.datagrid-editable");
            if (cell.length) {
                var ed = $.data(cell[0], "datagrid.editor");
                if (ed.actions.destroy) {
                    ed.actions.destroy(ed.target);
                }
                cell.html(ed.oldHtml);
                $.removeData(cell[0], "datagrid.editor");
                cell.removeClass("datagrid-editable");
                cell.css("width", "");
            }
        });
    };
    function _123(_141, _142) {
        var tr = $.data(_141, "datagrid").options.finder.getTr(_141, _142);
        if (!tr.hasClass("datagrid-row-editing")) {
            return true;
        }
        var vbox = tr.find(".validatebox-text");
        vbox.validatebox("validate");
        vbox.trigger("mouseleave");
        var _143 = tr.find(".validatebox-invalid");
        return _143.length == 0;
    };
    function _144(_145, _146) {
        var _147 = $.data(_145, "datagrid").insertedRows;
        var _148 = $.data(_145, "datagrid").deletedRows;
        var _149 = $.data(_145, "datagrid").updatedRows;
        if (!_146) {
            var rows = [];
            rows = rows.concat(_147);
            rows = rows.concat(_148);
            rows = rows.concat(_149);
            return rows;
        } else {
            if (_146 == "inserted") {
                return _147;
            } else {
                if (_146 == "deleted") {
                    return _148;
                } else {
                    if (_146 == "updated") {
                        return _149;
                    }
                }
            }
        }
        return [];
    };
    function _14a(_14b, _14c) {
        var _14d = $.data(_14b, "datagrid");
        var opts = _14d.options;
        var data = _14d.data;
        var _14e = _14d.insertedRows;
        var _14f = _14d.deletedRows;
        $(_14b).datagrid("cancelEdit", _14c);
        var row = data.rows[_14c];
        if (_2(_14e, row) >= 0) {
            _4(_14e, row);
        } else {
            _14f.push(row);
        }
        _4(_14d.selectedRows, opts.idField, data.rows[_14c][opts.idField]);
        _4(_14d.checkedRows, opts.idField, data.rows[_14c][opts.idField]);
        opts.view.deleteRow.call(opts.view, _14b, _14c);
        if (opts.height == "auto") {
            _2e(_14b);
        }
        $(_14b).datagrid("getPager").pagination("refresh", {
            total: data.total
        });
    };
    function _150(_151, _152) {
        var data = $.data(_151, "datagrid").data;
        var view = $.data(_151, "datagrid").options.view;
        var _153 = $.data(_151, "datagrid").insertedRows;
        view.insertRow.call(view, _151, _152.index, _152.row);
        _153.push(_152.row);
        $(_151).datagrid("getPager").pagination("refresh", {
            total: data.total
        });
    };
    function _154(_155, row) {
        var data = $.data(_155, "datagrid").data;
        var view = $.data(_155, "datagrid").options.view;
        var _156 = $.data(_155, "datagrid").insertedRows;
        view.insertRow.call(view, _155, null, row);
        _156.push(row);
        $(_155).datagrid("getPager").pagination("refresh", {
            total: data.total
        });
    };
    function _157(_158) {
        var _159 = $.data(_158, "datagrid");
        var data = _159.data;
        var rows = data.rows;
        var _15a = [];
        for (var i = 0; i < rows.length; i++) {
            _15a.push($.extend({},
            rows[i]));
        }
        _159.originalRows = _15a;
        _159.updatedRows = [];
        _159.insertedRows = [];
        _159.deletedRows = [];
    };
    function _15b(_15c) {
        var data = $.data(_15c, "datagrid").data;
        var ok = true;
        for (var i = 0,
        len = data.rows.length; i < len; i++) {
            if (_123(_15c, i)) {
                _124(_15c, i, false);
            } else {
                ok = false;
            }
        }
        if (ok) {
            _157(_15c);
        }
    };
    function _15d(_15e) {
        var _15f = $.data(_15e, "datagrid");
        var opts = _15f.options;
        var _160 = _15f.originalRows;
        var _161 = _15f.insertedRows;
        var _162 = _15f.deletedRows;
        var _163 = _15f.selectedRows;
        var _164 = _15f.checkedRows;
        var data = _15f.data;
        function _165(a) {
            var ids = [];
            for (var i = 0; i < a.length; i++) {
                ids.push(a[i][opts.idField]);
            }
            return ids;
        };
        function _166(ids, _167) {
            for (var i = 0; i < ids.length; i++) {
                var _168 = _d2(_15e, ids[i]);
                if (_168 >= 0) { (_167 == "s" ? _f0: _f8)(_15e, _168, true);
                }
            }
        };
        for (var i = 0; i < data.rows.length; i++) {
            _124(_15e, i, true);
        }
        var _169 = _165(_163);
        var _16a = _165(_164);
        _163.splice(0, _163.length);
        _164.splice(0, _164.length);
        data.total += _162.length - _161.length;
        data.rows = _160;
        _c6(_15e, data);
        _166(_169, "s");
        _166(_16a, "c");
        _157(_15e);
    };
    function _16b(_16c, _16d) {
        var opts = $.data(_16c, "datagrid").options;
        if (_16d) {
            opts.queryParams = _16d;
        }
        var _16e = $.extend({},
        opts.queryParams);
        if (opts.pagination) {
            $.extend(_16e, {
                page: opts.pageNumber,
                rows: opts.pageSize
            });
        }
        if (opts.sortName) {
            $.extend(_16e, {
                sort: opts.sortName,
                order: opts.sortOrder
            });
        }
        if (opts.onBeforeLoad.call(_16c, _16e) == false) {
            return;
        }
        $(_16c).datagrid("loading");
        setTimeout(function() {
            _16f();
        },
        0);
        function _16f() {
            var _170 = opts.loader.call(_16c, _16e,
            function(data) {
                setTimeout(function() {
                    $(_16c).datagrid("loaded");
                },
                0);
                _c6(_16c, data);
                setTimeout(function() {
                    _157(_16c);
                },
                0);
            },
            function() {
                setTimeout(function() {
                    $(_16c).datagrid("loaded");
                },
                0);
                opts.onLoadError.apply(_16c, arguments);
            });
            if (_170 == false) {
                $(_16c).datagrid("loaded");
            }
        };
    };
    function _171(_172, _173) {
        var opts = $.data(_172, "datagrid").options;
        _173.rowspan = _173.rowspan || 1;
        _173.colspan = _173.colspan || 1;
        if (_173.rowspan == 1 && _173.colspan == 1) {
            return;
        }
        var tr = opts.finder.getTr(_172, (_173.index != undefined ? _173.index: _173.id));
        if (!tr.length) {
            return;
        }
        var row = opts.finder.getRow(_172, tr);
        var _174 = row[_173.field];
        var td = tr.find("td[field=\"" + _173.field + "\"]");
        td.attr("rowspan", _173.rowspan).attr("colspan", _173.colspan);
        td.addClass("datagrid-td-merged");
        for (var i = 1; i < _173.colspan; i++) {
            td = td.next();
            td.hide();
            row[td.attr("field")] = _174;
        }
        for (var i = 1; i < _173.rowspan; i++) {
            tr = tr.next();
            if (!tr.length) {
                break;
            }
            var row = opts.finder.getRow(_172, tr);
            var td = tr.find("td[field=\"" + _173.field + "\"]").hide();
            row[td.attr("field")] = _174;
            for (var j = 1; j < _173.colspan; j++) {
                td = td.next();
                td.hide();
                row[td.attr("field")] = _174;
            }
        }
        _b0(_172);
    };
    $.fn.datagrid = function(_175, _176) {
        if (typeof _175 == "string") {
            return $.fn.datagrid.methods[_175](this, _176);
        }
        _175 = _175 || {};
        return this.each(function() {
            var _177 = $.data(this, "datagrid");
            var opts;
            if (_177) {
            	//如果datagrid已经初始化 则扩展原始选项
                opts = $.extend(_177.options, _175);
                _177.options = opts;
            } else {
                opts = $.extend({},
                $.extend({},
                $.fn.datagrid.defaults, {
                    queryParams: {}
                }), $.fn.datagrid.parseOptions(this), _175);
                $(this).css("width", "").css("height", "");
                var _178 = _47(this, opts.rownumbers);
                if (!opts.columns) {
                    opts.columns = _178.columns;
                }
                if (!opts.frozenColumns) {
                    opts.frozenColumns = _178.frozenColumns;
                }
                opts.columns = $.extend(true, [], opts.columns);
                //根据当前页面动态调整表格宽度
                /*
                var tmp = opts.columns[0];
                for(var i=0;i<tmp.length;i++){
                	var col = tmp[i];
                	//还原真实宽度
                	if(col._width){
                		col.width = col._width;
                	}
                	else{
                		col._width = col.width;
                	}
                	if(!col.checkbox && col.fixed){
                		if(styleOpt.defStyle=="bigTheme"){
                			col.width = col.width * 1.15;
                		}
                	}
                }
                */
                opts.frozenColumns = $.extend(true, [], opts.frozenColumns);
                opts.view = $.extend({},
                opts.view);
                $.data(this, "datagrid", {
                    options: opts,
                    panel: _178.panel,
                    dc: _178.dc,
                    ss: _178.ss,
                    selectedRows: [],
                    checkedRows: [],
                    data: {
                        total: 0,
                        rows: []
                    },
                    originalRows: [],
                    updatedRows: [],
                    insertedRows: [],
                    deletedRows: []
                });
            }
            _56(this);
            if (opts.data) {
                _c6(this, opts.data);
                _157(this);
            } else {
                var data = $.fn.datagrid.parseData(this);
                if (data.total > 0) {
                    _c6(this, data);
                    _157(this);
                }
            }
            _19(this);
            _16b(this);
            _70(this);
        });
    };
    var _179 = {
        text: {
            init: function(_17a, _17b) {
                var _17c = $("<input type=\"text\" class=\"datagrid-editable-input\">").appendTo(_17a);
                return _17c;
            },
            getValue: function(_17d) {
                return $(_17d).val();
            },
            setValue: function(_17e, _17f) {
                $(_17e).val(_17f);
            },
            resize: function(_180, _181) {
                $(_180)._outerWidth(_181)._outerHeight(22);
            }
        },
        textarea: {
            init: function(_182, _183) {
                var _184 = $("<textarea class=\"datagrid-editable-input\"></textarea>").appendTo(_182);
                return _184;
            },
            getValue: function(_185) {
                return $(_185).val();
            },
            setValue: function(_186, _187) {
                $(_186).val(_187);
            },
            resize: function(_188, _189) {
                $(_188)._outerWidth(_189);
            }
        },
        checkbox: {
            init: function(_18a, _18b) {
                var _18c = $("<input type=\"checkbox\">").appendTo(_18a);
                _18c.val(_18b.on);
                _18c.attr("offval", _18b.off);
                return _18c;
            },
            getValue: function(_18d) {
                if ($(_18d).is(":checked")) {
                    return $(_18d).val();
                } else {
                    return $(_18d).attr("offval");
                }
            },
            setValue: function(_18e, _18f) {
                var _190 = false;
                if ($(_18e).val() == _18f) {
                    _190 = true;
                }
                $(_18e)._propAttr("checked", _190);
            }
        },
        numberbox: {
            init: function(_191, _192) {
                var _193 = $("<input type=\"text\" class=\"datagrid-editable-input\">").appendTo(_191);
                _193.numberbox(_192);
                return _193;
            },
            destroy: function(_194) {
                $(_194).numberbox("destroy");
            },
            getValue: function(_195) {
                $(_195).blur();
                return $(_195).numberbox("getValue");
            },
            setValue: function(_196, _197) {
                $(_196).numberbox("setValue", _197);
            },
            resize: function(_198, _199) {
                $(_198)._outerWidth(_199)._outerHeight(22);
            }
        },
        validatebox: {
            init: function(_19a, _19b) {
                var _19c = $("<input type=\"text\" class=\"datagrid-editable-input\">").appendTo(_19a);
                _19c.validatebox(_19b);
                return _19c;
            },
            destroy: function(_19d) {
                $(_19d).validatebox("destroy");
            },
            getValue: function(_19e) {
                return $(_19e).val();
            },
            setValue: function(_19f, _1a0) {
                $(_19f).val(_1a0);
            },
            resize: function(_1a1, _1a2) {
                $(_1a1)._outerWidth(_1a2)._outerHeight(22);
            }
        },
        datebox: {
            init: function(_1a3, _1a4) {
                var _1a5 = $("<input type=\"text\">").appendTo(_1a3);
                _1a5.datebox(_1a4);
                return _1a5;
            },
            destroy: function(_1a6) {
                $(_1a6).datebox("destroy");
            },
            getValue: function(_1a7) {
                return $(_1a7).datebox("getValue");
            },
            setValue: function(_1a8, _1a9) {
                $(_1a8).datebox("setValue", _1a9);
            },
            resize: function(_1aa, _1ab) {
                $(_1aa).datebox("resize", _1ab);
            }
        },
        combobox: {
            init: function(_1ac, _1ad) {
                var _1ae = $("<input type=\"text\">").appendTo(_1ac);
                _1ae.combobox(_1ad || {});
                return _1ae;
            },
            destroy: function(_1af) {
                $(_1af).combobox("destroy");
            },
            getValue: function(_1b0) {
                var opts = $(_1b0).combobox("options");
                if (opts.multiple) {
                    return $(_1b0).combobox("getValues").join(opts.separator);
                } else {
                    return $(_1b0).combobox("getValue");
                }
            },
            setValue: function(_1b1, _1b2) {
                var opts = $(_1b1).combobox("options");
                if (opts.multiple) {
                    if (_1b2) {
                        $(_1b1).combobox("setValues", _1b2.split(opts.separator));
                    } else {
                        $(_1b1).combobox("clear");
                    }
                } else {
                    $(_1b1).combobox("setValue", _1b2);
                }
            },
            resize: function(_1b3, _1b4) {
                $(_1b3).combobox("resize", _1b4);
            }
        },
        combotree: {
            init: function(_1b5, _1b6) {
                var _1b7 = $("<input type=\"text\">").appendTo(_1b5);
                _1b7.combotree(_1b6);
                return _1b7;
            },
            destroy: function(_1b8) {
                $(_1b8).combotree("destroy");
            },
            getValue: function(_1b9) {
                return $(_1b9).combotree("getValue");
            },
            setValue: function(_1ba, _1bb) {
                $(_1ba).combotree("setValue", _1bb);
            },
            resize: function(_1bc, _1bd) {
                $(_1bc).combotree("resize", _1bd);
            }
        }
    };
    $.fn.datagrid.methods = {
        options: function(jq) {
            var _1be = $.data(jq[0], "datagrid").options;
            var _1bf = $.data(jq[0], "datagrid").panel.panel("options");
            var opts = $.extend(_1be, {
                width: _1bf.width,
                height: _1bf.height,
                closed: _1bf.closed,
                collapsed: _1bf.collapsed,
                minimized: _1bf.minimized,
                maximized: _1bf.maximized
            });
            return opts;
        },
        getPanel: function(jq) {
            return $.data(jq[0], "datagrid").panel;
        },
        getPager: function(jq) {
            return $.data(jq[0], "datagrid").panel.children("div.datagrid-pager");
        },
        getColumnFields: function(jq, _1c0) {
            return _6e(jq[0], _1c0);
        },
        getColumnOption: function(jq, _1c1) {
            return _6f(jq[0], _1c1);
        },
        resize: function(jq, _1c2) {
            var result = jq.each(function() {
                _19(this, _1c2);
            });
            return result;
        },
        load: function(jq, _1c3) {
            return jq.each(function() {
                var opts = $(this).datagrid("options");
                opts.pageNumber = 1;
                var _1c4 = $(this).datagrid("getPager");
                _1c4.pagination("refresh", {
                    pageNumber: 1
                });
                _16b(this, _1c3);
            });
        },
        reload: function(jq, _1c5) {
            return jq.each(function() {
                _16b(this, _1c5);
            });
        },
        reloadFooter: function(jq, _1c6) {
            return jq.each(function() {
                var opts = $.data(this, "datagrid").options;
                var dc = $.data(this, "datagrid").dc;
                if (_1c6) {
                    $.data(this, "datagrid").footer = _1c6;
                }
                if (opts.showFooter) {
                    opts.view.renderFooter.call(opts.view, this, dc.footer2, false);
                    opts.view.renderFooter.call(opts.view, this, dc.footer1, true);
                    if (opts.view.onAfterRender) {
                        opts.view.onAfterRender.call(opts.view, this);
                    }
                    $(this).datagrid("fixRowHeight");
                }
            });
        },
        loading: function(jq) {
            return true;
        },
        loaded: function(jq) {
            return true;
        },
        fitColumns: function(jq) {
            return jq.each(function() {
                _8d(this);
            });
        },
        fixColumnSize: function(jq, _1c9) {
            return jq.each(function() {
                _51(this, _1c9);
            });
        },
        fixRowHeight: function(jq, _1ca) {
            return jq.each(function() {
                _2e(this, _1ca);
            });
        },
        freezeRow: function(jq, _1cb) {
            return jq.each(function() {
                _3f(this, _1cb);
            });
        },
        autoSizeColumn: function(jq, _1cc) {
            return jq.each(function() {
                _9c(this, _1cc);
            });
        },
        loadData: function(jq, data) {
            return jq.each(function() {
                _c6(this, data);
                _157(this);
            });
        },
        getData: function(jq) {
            return $.data(jq[0], "datagrid").data;
        },
        getRows: function(jq) {
            return $.data(jq[0], "datagrid").data.rows;
        },
        getFooterRows: function(jq) {
            return $.data(jq[0], "datagrid").footer;
        },
        getRowIndex: function(jq, id) {
            return _d2(jq[0], id);
        },
        getChecked: function(jq) {
            return _de(jq[0]);
        },
        getSelected: function(jq) {
            var rows = _d7(jq[0]);
            return rows.length > 0 ? rows[0] : null;
        },
        getSelections: function(jq) {
            return _d7(jq[0]);
        },
        clearSelections: function(jq) {
            return jq.each(function() {
                var _1cd = $.data(this, "datagrid").selectedRows;
                _1cd.splice(0, _1cd.length);
                _f7(this);
            });
        },
        clearChecked: function(jq) {
            return jq.each(function() {
                var _1ce = $.data(this, "datagrid").checkedRows;
                _1ce.splice(0, _1ce.length);
                _10c(this);
            });
        },
        scrollTo: function(jq, _1cf) {
            return jq.each(function() {
                _e3(this, _1cf);
            });
        },
        highlightRow: function(jq, _1d0) {
            return jq.each(function() {
                _eb(this, _1d0);
                _e3(this, _1d0);
            });
        },
        selectAll: function(jq) {
            return jq.each(function() {
                _101(this);
            });
        },
        unselectAll: function(jq) {
            return jq.each(function() {
                _f7(this);
            });
        },
        selectRow: function(jq, _1d1) {
            return jq.each(function() {
                _f0(this, _1d1);
            });
        },
        selectRecord: function(jq, id) {
            return jq.each(function() {
                var opts = $.data(this, "datagrid").options;
                if (opts.idField) {
                    var _1d2 = _d2(this, id);
                    if (_1d2 >= 0) {
                        $(this).datagrid("selectRow", _1d2);
                    }
                }
            });
        },
        unselectRow: function(jq, _1d3) {
            return jq.each(function() {
                _f9(this, _1d3);
            });
        },
        checkRow: function(jq, _1d4) {
            return jq.each(function() {
                _f8(this, _1d4);
            });
        },
        uncheckRow: function(jq, _1d5) {
            return jq.each(function() {
                _100(this, _1d5);
            });
        },
        checkAll: function(jq) {
            return jq.each(function() {
                _106(this);
            });
        },
        uncheckAll: function(jq) {
            return jq.each(function() {
                _10c(this);
            });
        },
        beginEdit: function(jq, _1d6) {
            return jq.each(function() {
                _11e(this, _1d6);
            });
        },
        endEdit: function(jq, _1d7) {
            return jq.each(function() {
                _124(this, _1d7, false);
            });
        },
        cancelEdit: function(jq, _1d8) {
            return jq.each(function() {
                _124(this, _1d8, true);
            });
        },
        getEditors: function(jq, _1d9) {
            return _12f(jq[0], _1d9);
        },
        getEditor: function(jq, _1da) {
            return _133(jq[0], _1da);
        },
        refreshRow: function(jq, _1db) {
            return jq.each(function() {
                var opts = $.data(this, "datagrid").options;
                opts.view.refreshRow.call(opts.view, this, _1db);
            });
        },
        validateRow: function(jq, _1dc) {
            return _123(jq[0], _1dc);
        },
        updateRow: function(jq, _1dd) {
            return jq.each(function() {
                var opts = $.data(this, "datagrid").options;
                opts.view.updateRow.call(opts.view, this, _1dd.index, _1dd.row);
            });
        },
        appendRow: function(jq, row) {
            return jq.each(function() {
                _154(this, row);
            });
        },
        insertRow: function(jq, _1de) {
            return jq.each(function() {
                _150(this, _1de);
            });
        },
        deleteRow: function(jq, _1df) {
            return jq.each(function() {
                _14a(this, _1df);
            });
        },
        getChanges: function(jq, _1e0) {
            return _144(jq[0], _1e0);
        },
        acceptChanges: function(jq) {
            return jq.each(function() {
                _15b(this);
            });
        },
        rejectChanges: function(jq) {
            return jq.each(function() {
                _15d(this);
            });
        },
        mergeCells: function(jq, _1e1) {
            return jq.each(function() {
                _171(this, _1e1);
            });
        },
        showColumn: function(jq, _1e2) {
            return jq.each(function() {
                var _1e3 = $(this).datagrid("getPanel");
                _1e3.find("td[field=\"" + _1e2 + "\"]").show();
                $(this).datagrid("getColumnOption", _1e2).hidden = false;
                $(this).datagrid("fitColumns");
            });
        },
        hideColumn: function(jq, _1e4) {
            return jq.each(function() {
                var _1e5 = $(this).datagrid("getPanel");
                _1e5.find("td[field=\"" + _1e4 + "\"]").hide();
                $(this).datagrid("getColumnOption", _1e4).hidden = true;
                $(this).datagrid("fitColumns");
            });
        }
    };
    $.fn.datagrid.parseOptions = function(_1e6) {
        var t = $(_1e6);
        return $.extend({},
        $.fn.panel.parseOptions(_1e6), $.parser.parseOptions(_1e6, ["url", "toolbar", "idField", "sortName", "sortOrder", "pagePosition", "resizeHandle", {
            fitColumns: "boolean",
            autoRowHeight: "boolean",
            striped: "boolean",
            nowrap: "boolean"
        },
        {
            rownumbers: "boolean",
            singleSelect: "boolean",
            checkOnSelect: "boolean",
            selectOnCheck: "boolean"
        },
        {
            pagination: "boolean",
            pageSize: "number",
            pageNumber: "number"
        },
        {
            multiSort: "boolean",
            remoteSort: "boolean",
            showHeader: "boolean",
            showFooter: "boolean"
        },
        {
            scrollbarSize: "number"
        }]), {
            pageList: (t.attr("pageList") ? eval(t.attr("pageList")) : undefined),
            loadMsg: (t.attr("loadMsg") != undefined ? t.attr("loadMsg") : undefined),
            rowStyler: (t.attr("rowStyler") ? eval(t.attr("rowStyler")) : undefined)
        });
    };
    $.fn.datagrid.parseData = function(_1e7) {
        var t = $(_1e7);
        var data = {
            total: 0,
            rows: []
        };
        var _1e8 = t.datagrid("getColumnFields", true).concat(t.datagrid("getColumnFields", false));
        t.find("tbody tr").each(function() {
            data.total++;
            var row = {};
            $.extend(row, $.parser.parseOptions(this, ["iconCls", "state"]));
            for (var i = 0; i < _1e8.length; i++) {
                row[_1e8[i]] = $(this).find("td:eq(" + i + ")").html();
            }
            data.rows.push(row);
        });
        return data;
    };
    var _1e9 = {
        render: function(_1ea, _1eb, _1ec) {
            var _1ed = $.data(_1ea, "datagrid");
            var opts = _1ed.options;
            var rows = _1ed.data.rows;
            var _1ee = $(_1ea).datagrid("getColumnFields", _1ec);
            if (_1ec) {
                if (! (opts.rownumbers || (opts.frozenColumns && opts.frozenColumns.length))) {
                    return;
                }
            }
            var _1ef = ["<table class=\"datagrid-btable\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>"];
            for (var i = 0; i < rows.length; i++) {
                var css = opts.rowStyler ? opts.rowStyler.call(_1ea, i, rows[i]) : "";
                var _1f0 = "";
                var _1f1 = "";
                if (typeof css == "string") {
                    _1f1 = css;
                } else {
                    if (css) {
                        _1f0 = css["class"] || "";
                        _1f1 = css["style"] || "";
                    }
                }
                var cls = "class=\"datagrid-row " + (i % 2 && opts.striped ? "datagrid-row-alt ": " ") + _1f0 + "\"";
                var _1f2 = _1f1 ? "style=\"" + _1f1 + "\"": "";
                var _1f3 = _1ed.rowIdPrefix + "-" + (_1ec ? 1 : 2) + "-" + i;
                _1ef.push("<tr id=\"" + _1f3 + "\" datagrid-row-index=\"" + i + "\" " + cls + " " + _1f2 + ">");
                _1ef.push(this.renderRow.call(this, _1ea, _1ee, _1ec, i, rows[i]));
                _1ef.push("</tr>");
            }
            _1ef.push("</tbody></table>");
            $(_1eb).html(_1ef.join(""));
        },
        renderFooter: function(_1f4, _1f5, _1f6) {
            var opts = $.data(_1f4, "datagrid").options;
            var rows = $.data(_1f4, "datagrid").footer || [];
            var _1f7 = $(_1f4).datagrid("getColumnFields", _1f6);
            var _1f8 = ["<table class=\"datagrid-ftable\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>"];
            for (var i = 0; i < rows.length; i++) {
                _1f8.push("<tr class=\"datagrid-row\" datagrid-row-index=\"" + i + "\">");
                _1f8.push(this.renderRow.call(this, _1f4, _1f7, _1f6, i, rows[i]));
                _1f8.push("</tr>");
            }
            _1f8.push("</tbody></table>");
            $(_1f5).html(_1f8.join(""));
        },
        renderRow: function(_1f9, _1fa, _1fb, _1fc, _1fd) {
            var opts = $.data(_1f9, "datagrid").options;
            var cc = [];
            if (_1fb && opts.rownumbers) {
                var _1fe = _1fc + 1;
                if (opts.pagination) {
                    _1fe += (opts.pageNumber - 1) * opts.pageSize;
                }
                cc.push("<td class=\"datagrid-td-rownumber\"><div class=\"datagrid-cell-rownumber\">" + _1fe + "</div></td>");
            }
            for (var i = 0; i < _1fa.length; i++) {
                var _1ff = _1fa[i];
                var col = $(_1f9).datagrid("getColumnOption", _1ff);
                if (col) {
                    var _200 = _1fd[_1ff];
                    var css = col.styler ? (col.styler(_200, _1fd, _1fc) || "") : "";
                    var _201 = "";
                    var _202 = "";
                    if (typeof css == "string") {
                        _202 = css;
                    } else {
                        if (cc) {
                            _201 = css["class"] || "";
                            _202 = css["style"] || "";
                        }
                    }
                    var cls = _201 ? "class=\"" + _201 + "\"": "";
                    var _203 = col.hidden ? "style=\"display:none;" + _202 + "\"": (_202 ? "style=\"" + _202 + "\"": "");
                    cc.push("<td field=\"" + _1ff + "\" " + cls + " " + _203 + ">");
                    if (col.checkbox) {
                        var _203 = "";
                    } else {
                        var _203 = _202;
                        if (col.align) {
                            _203 += ";text-align:" + col.align + ";";
                        }
                        if (!opts.nowrap) {
                            _203 += ";white-space:normal;height:auto;";
                        } else {
                            if (opts.autoRowHeight) {
                                _203 += ";height:auto;";
                            }
                        }
                    }
                    cc.push("<div style=\"" + _203 + "\" ");
                    cc.push(col.checkbox ? "class=\"datagrid-cell-check\"": "class=\"datagrid-cell " + col.cellClass + "\"");
                    cc.push(">");
                    if (col.checkbox) {
                        cc.push("<input type=\"checkbox\" name=\"" + _1ff + "\" value=\"" + (_200 != undefined ? _200: "") + "\">");
                    } else {
                        //by murmur 自动格式化金额
                        if(col.precision!==null && typeof(col.precision)!="undefined"){
                            _200 = _toFixed(_200,2);
                        }
                        if(typeof(_200) == "string"){
                            _200 = FW.specialchars(_200);
                        }
                        if (col.formatter) {
                            cc.push(col.formatter(_200, _1fd, _1fc));
                        } else {
                            //将BRBR字符串转为空格，不是空字符串，方便区分分段信息；放在这里避免与用户自定义formatter冲突（避免与日期处理formatter冲突）
                            _200=FW.convertBRBRToBlankSpace(_200);
                            cc.push(_200);
                        }
                    }
                    cc.push("</div>");
                    cc.push("</td>");
                }
            }
            return cc.join("");
        },
        refreshRow: function(_204, _205) {
            this.updateRow.call(this, _204, _205, {});
        },
        updateRow: function(_206, _207, row) {
            var opts = $.data(_206, "datagrid").options;
            var rows = $(_206).datagrid("getRows");
            $.extend(rows[_207], row);
            var css = opts.rowStyler ? opts.rowStyler.call(_206, _207, rows[_207]) : "";
            var _208 = "";
            var _209 = "";
            if (typeof css == "string") {
                _209 = css;
            } else {
                if (css) {
                    _208 = css["class"] || "";
                    _209 = css["style"] || "";
                }
            }
            var _208 = "datagrid-row " + (_207 % 2 && opts.striped ? "datagrid-row-alt ": " ") + _208;
            function _20a(_20b) {
                var _20c = $(_206).datagrid("getColumnFields", _20b);
                var tr = opts.finder.getTr(_206, _207, "body", (_20b ? 1 : 2));
                var _20d = tr.find("div.datagrid-cell-check input[type=checkbox]").is(":checked");
                tr.html(this.renderRow.call(this, _206, _20c, _20b, _207, rows[_207]));
                tr.attr("style", _209).attr("class", tr.hasClass("datagrid-row-selected") ? _208 + " datagrid-row-selected": _208);
                if (_20d) {
                    tr.find("div.datagrid-cell-check input[type=checkbox]")._propAttr("checked", true);
                }
            };
            _20a.call(this, true);
            _20a.call(this, false);
            $(_206).datagrid("fixRowHeight", _207);
        },
        insertRow: function(_20e, _20f, row) {
            var _210 = $.data(_20e, "datagrid");
            var opts = _210.options;
            var dc = _210.dc;
            var data = _210.data;
            if (_20f == undefined || _20f == null) {
                _20f = data.rows.length;
            }
            if (_20f > data.rows.length) {
                _20f = data.rows.length;
            }
            function _211(_212) {
                var _213 = _212 ? 1 : 2;
                for (var i = data.rows.length - 1; i >= _20f; i--) {
                    var tr = opts.finder.getTr(_20e, i, "body", _213);
                    tr.attr("datagrid-row-index", i + 1);
                    tr.attr("id", _210.rowIdPrefix + "-" + _213 + "-" + (i + 1));
                    if (_212 && opts.rownumbers) {
                        var _214 = i + 2;
                        if (opts.pagination) {
                            _214 += (opts.pageNumber - 1) * opts.pageSize;
                        }
                        tr.find("div.datagrid-cell-rownumber").html(_214);
                    }
                    if (opts.striped) {
                        tr.removeClass("datagrid-row-alt").addClass((i + 1) % 2 ? "datagrid-row-alt": "");
                    }
                }
            };
            function _215(_216) {
                var _217 = _216 ? 1 : 2;
                var _218 = $(_20e).datagrid("getColumnFields", _216);
                var _219 = _210.rowIdPrefix + "-" + _217 + "-" + _20f;
                var tr = "<tr id=\"" + _219 + "\" class=\"datagrid-row\" datagrid-row-index=\"" + _20f + "\"></tr>";
                if (_20f >= data.rows.length) {
                    if (data.rows.length) {
                        opts.finder.getTr(_20e, "", "last", _217).after(tr);
                    } else {
                        var cc = _216 ? dc.body1: dc.body2;
                        cc.html("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>" + tr + "</tbody></table>");
                    }
                } else {
                    opts.finder.getTr(_20e, _20f + 1, "body", _217).before(tr);
                }
            };
            _211.call(this, true);
            _211.call(this, false);
            _215.call(this, true);
            _215.call(this, false);
            data.total += 1;
            data.rows.splice(_20f, 0, row);
            this.refreshRow.call(this, _20e, _20f);
        },
        deleteRow: function(_21a, _21b) {
            var _21c = $.data(_21a, "datagrid");
            var opts = _21c.options;
            var data = _21c.data;
            function _21d(_21e) {
                var _21f = _21e ? 1 : 2;
                for (var i = _21b + 1; i < data.rows.length; i++) {
                    var tr = opts.finder.getTr(_21a, i, "body", _21f);
                    tr.attr("datagrid-row-index", i - 1);
                    tr.attr("id", _21c.rowIdPrefix + "-" + _21f + "-" + (i - 1));
                    if (_21e && opts.rownumbers) {
                        var _220 = i;
                        if (opts.pagination) {
                            _220 += (opts.pageNumber - 1) * opts.pageSize;
                        }
                        tr.find("div.datagrid-cell-rownumber").html(_220);
                    }
                    if (opts.striped) {
                        tr.removeClass("datagrid-row-alt").addClass((i - 1) % 2 ? "datagrid-row-alt": "");
                    }
                }
            };
            opts.finder.getTr(_21a, _21b).remove();
            _21d.call(this, true);
            _21d.call(this, false);
            data.total -= 1;
            data.rows.splice(_21b, 1);
        },
        onBeforeRender: function(_221, rows) {},
        onAfterRender: function(_222) {
            var opts = $.data(_222, "datagrid").options;
            if (opts.showFooter) {
                var _223 = $(_222).datagrid("getPanel").find("div.datagrid-footer");
                _223.find("div.datagrid-cell-rownumber,div.datagrid-cell-check").css("visibility", "hidden");
            }
        }
    };
    $.fn.datagrid.defaults = $.extend({},
    $.fn.panel.defaults, {
        frozenColumns: undefined,
        columns: undefined,
        fitColumns: false,
        resizeHandle: "right",
        autoRowHeight: true,
        toolbar: null,
        striped: false,
        method: "post",
        nowrap: true,
        idField: null,
        url: null,
        data: null,
        loadMsg: "正在加载中，请稍候 ...",
        rownumbers: false,
        singleSelect: false,
        selectOnCheck: true,
        checkOnSelect: true,
        pagination: false,
        pagePosition: "bottom",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 20, 30, 40, 50],
        queryParams: {},
        sortName: null,
        sortOrder: "asc",
        multiSort: false,
        remoteSort: true,
        showHeader: true,
        showFooter: false,
        scrollbarSize: 0/*18*/,
        rowStyler: function(_224, _225) {},
        loader: function(_226, _227, _228) {
            var opts = $(this).datagrid("options");
            if (!opts.url) {
                return false;
            }
            $.ajax({
                type: opts.method,
                url: opts.url,
                data: _226,
                dataType: "json",
                success: function(data) {
                    _227(data);
                },
                error: function() {
                    _228.apply(this, arguments);
                }
            });
        },
        loadFilter: function(data) {
            if (typeof data.length == "number" && typeof data.splice == "function") {
                return {
                    total: data.length,
                    rows: data
                };
            } else {
                return data;
            }
        },
        editors: _179,
        finder: {
            getTr: function(_229, _22a, type, _22b) {
                type = type || "body";
                _22b = _22b || 0;
                var _22c = $.data(_229, "datagrid");
                var dc = _22c.dc;
                var opts = _22c.options;
                if (_22b == 0) {
                    var tr1 = opts.finder.getTr(_229, _22a, type, 1);
                    var tr2 = opts.finder.getTr(_229, _22a, type, 2);
                    return tr1.add(tr2);
                } else {
                    if (type == "body") {
                        var tr = $("#" + _22c.rowIdPrefix + "-" + _22b + "-" + _22a);
                        if (!tr.length) {
                            tr = (_22b == 1 ? dc.body1: dc.body2).find(">table>tbody>tr[datagrid-row-index=" + _22a + "]");
                        }
                        return tr;
                    } else {
                        if (type == "footer") {
                            return (_22b == 1 ? dc.footer1: dc.footer2).find(">table>tbody>tr[datagrid-row-index=" + _22a + "]");
                        } else {
                            if (type == "selected") {
                                return (_22b == 1 ? dc.body1: dc.body2).find(">table>tbody>tr.datagrid-row-selected");
                            } else {
                                if (type == "highlight") {
                                    return (_22b == 1 ? dc.body1: dc.body2).find(">table>tbody>tr.datagrid-row-over");
                                } else {
                                    if (type == "checked") {
                                        return (_22b == 1 ? dc.body1: dc.body2).find(">table>tbody>tr.datagrid-row-checked");
                                    } else {
                                        if (type == "last") {
                                            return (_22b == 1 ? dc.body1: dc.body2).find(">table>tbody>tr[datagrid-row-index]:last");
                                        } else {
                                            if (type == "allbody") {
                                                return (_22b == 1 ? dc.body1: dc.body2).find(">table>tbody>tr[datagrid-row-index]");
                                            } else {
                                                if (type == "allfooter") {
                                                    return (_22b == 1 ? dc.footer1: dc.footer2).find(">table>tbody>tr[datagrid-row-index]");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            getRow: function(_22d, p) {
                var _22e = (typeof p == "object") ? p.attr("datagrid-row-index") : p;
                return $.data(_22d, "datagrid").data.rows[parseInt(_22e)];
            }
        },
        view: _1e9,
        onBeforeLoad: function(_22f) {},
        onLoadSuccess: function() {},
        onRenderFinish: function() {},
        onLoadError: function() {},
        onClickRow: function(_230, _231) {},
        onDblClickRow: function(_232, _233) {},
        onClickCell: function(_234, _235, _236) {},
        onDblClickCell: function(_237, _238, _239) {},
        onSortColumn: function(sort, _23a) {},
        onResizeColumn: function(_23b, _23c) {},
        onSelect: function(_23d, _23e) {},
        onUnselect: function(_23f, _240) {},
        onSelectAll: function(rows) {},
        onUnselectAll: function(rows) {},
        onCheck: function(_241, _242) {},
        onUncheck: function(_243, _244) {},
        onCheckAll: function(rows) {},
        onUncheckAll: function(rows) {},
        onBeforeEdit: function(_245, _246) {},
        onAfterEdit: function(_247, _248, _249) {},
        onCancelEdit: function(_24a, _24b) {},
        onHeaderContextMenu: function(e, _24c) {},
        onRowContextMenu: function(e, _24d, _24e) {}
    });
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_pagination.js
*/
//分页器基础API
(function($){
	$.fn.extend({
		/*
			此翻页器依赖EasyUi的Pagination，仅提供样式
		*/
		ITCUI_Pagination:function(action,arg,opts){
			var _this = $(this);
			var _t = this;
			var _pagination = $(_this.datagrid('getPager'));
			var options = _pagination.pagination("options")
			var PG_STEP = 2;
			var NO_SEL_TEXT = "";//可以写"请选择xxxx"

			_t.bindMenuPagerEvents = function(){
				var p = $(arg);
				//绑定菜单翻页器的事件
				p.find(".itc_pagination_menu li").click(function(e){
					var __this = $(this);
					var num = __this.attr("turnto");
					_t.turnTo(__this.parents(".itc_pagination_container"),num);
					
				});
				p.find(".itc_pagination_psize li").click(function(e){
					var pgSize = $(this).children("a").html().replace(/<.*>/,"").replace("每页","").replace("条","");
					var container = $(this).parents(".itc_pagination_container");
					var targetPager = container.data("targetPager");
					if(!targetPager){
						return;
					}
					targetPager.pagination({"pageSize":pgSize}).pagination("select",1);
					_this = container.data('ptrGrid');
					targetDiv = container.data('targetDiv');
					_this.ITCUI_Pagination("create",targetDiv,container.data("opts"));
					container.data("onChangePageSize")(pgSize);
					//设置其他选项卡的表格页条目数
					if(typeof(FW.setAllPageSize)!="undefined"){
						FW.setAllPageSize(pgSize);
					}
				});
				p.find(".itc_pagination_style li").click(function(e){
					var __this = $(this);
					var style = __this.attr("tblStyle");
					var container = __this.parents(".itc_pagination_container");
					__this.parent().find(".dropdown-selected").removeClass("dropdown-selected").addClass("dropdown-unselected");
					__this.find(".dropdown-unselected").removeClass("dropdown-unselected").addClass("dropdown-selected");
					container.data("onChangeStyle")(style);
				});
			}

			_t.turnTo = function(itcPagination,pageNumber){
				FW.pushEvent("翻页","页数=" + pageNumber,"");
				var style = "TIMSS";
				var pagination = itcPagination.data("targetPager");
				pagination.pagination("select",pageNumber);
				_t.adjustButtons(itcPagination);
				//刷新当前页数
				var wrap = itcPagination.find(".itc_pg_selector_wrap");
				wrap.find(".currpage").html(pageNumber);
				//刷新翻页菜单
				var opts = pagination.pagination("options");
				var currPage = parseInt(opts.pageNumber);
				var maxPage = Math.ceil(opts.total/opts.pageSize) || 1;//没有任何数据时也显示1页 2014.3.4
				if(style!="TIMSS"){
					//标准翻页风格 只显示几页加首位链接
					var pStart = -1;
					var pEnd = -1;
					if(maxPage<=1 + 2*PG_STEP){
						pStart = 1;
						pEnd = maxPage;
					}
					else if(currPage-PG_STEP>1&&currPage+PG_STEP<maxPage){
						//两头都不着边界 取p-2 p-1 p p+1 p+2
						pStart = currPage - PG_STEP;
						pEnd = currPage + PG_STEP;
					}
					else if(currPage-PG_STEP<=1){
						//左边不够长
						pStart = 1;
						pEnd = 1 + 2*PG_STEP;
					}
					else if(currPage+PG_STEP>=maxPage){
						//右边不够长
						pEnd = maxPage;
						pStart = maxPage - 1 - 2*PG_STEP;
					}
					var liHtml = "";
					for(var i=pStart;i<=pEnd;i++){
						liHtml += "<li turnto=" + i + "><a class='menuitem'>" + i +"/" + maxPage + "</a></li>";
					}
					liHtml += "<li class='divider'></li>";
					liHtml += "<li turnto=1><a class='menuitem'>首页</a></li>";
					liHtml += "<li turnto=" + maxPage + "><a class='menuitem'>末页</a></li>";
					setTimeout('$("div .itc_pagination_menu").html(\"' + liHtml + '\");_t.bindMenuPagerEvents();',200);				
				}				
			};



			_t.createPagination = function(){
				//保存分页器的参数
				_t.data("target",arg);
				_t.data("styleOpt",opts);
				//为本地翻页增加相关的回调函数
				var dgOpts = _this.datagrid("options");
				if(!dgOpts.url && dgOpts._data){
					if(action!="createPg"){
						dgOpts.__data = dgOpts._data;//这里还要备份_data数据 因为搜索时_data都要被重置
					}
					options.datagrid = _this;
					options.onSelectPage = function(pageNumber, pageSize, filter){
						var pgOpts = $(this).data("pagination")["options"];
						var data = pgOpts.datagrid.datagrid("options")._data.rows;
						var dCnt = 0;
						var dspRows = [];
						while(dCnt<pageSize){
							var num = (pageNumber-1)*pageSize+dCnt;
							if(num<data.length){
								dspRows.push(data[num]);	
							}
							else{
								break;
							}							
							dCnt++;
						}
						pgOpts.datagrid.datagrid("loadData",{"total":data.length,"rows":dspRows});
					}
				}
				//本地排序相关的回调函数
				if(!dgOpts.remoteSort){
					dgOpts.onSortColumn = function(sort, order){
						var _opts = $(this).datagrid("options")
						var data = _opts._data;
						_arr_sort(data.rows,sort,order);
						var pager = $(this).datagrid("getPager");
						$(pager).pagination("select");
					};
				}
				$(arg).addClass("itc_pagination_container");
				var btnHtml = "";
				var pgSizeList = options.pageList;
				//设置菜单
				btnHtml += "<div class='dropdown bbox' style='float:right'><span data-toggle='dropdown' class='itc_pg_btn itc_pg_setting'></span>";
				btnHtml += "<ul class='dropdown-menu pull-right mnu-multilevel' role='menu'>";
				if(opts&&opts.defStyle&&opts.styles){
					//表格样式部分
					btnHtml += "<li class='dropdown-submenu pull-left'><a>显示设置</a><ul class='dropdown-menu itc_pagination_style'>";
					for(var i=0;i<opts.styles.length;i++){
						var style = opts.styles[i];
						btnHtml += "<li tblStyle='" + style.id + "'><a class='menuitem'>" 
						if(style.id==opts.defStyle){
							btnHtml += "<span class='dropdown-selected'></span>";
						}
						else{
							btnHtml += "<span class='dropdown-unselected'></span>";	
						}
						btnHtml += style.title + "</a></li>";
					}
					$(arg).data("onChangeStyle",opts.onChangeStyle || function(){});
					$(arg).data("onChangePageSize",opts.onChangePageSize || function(){});
					$(arg).data("opts",opts);
					btnHtml += "</ul></li>"	
				}
				btnHtml += "<li class='dropdown-submenu pull-left'><a>分页设置</a><ul class='dropdown-menu itc_pagination_psize'>";
				for(var i=0;i<pgSizeList.length;i++){
					btnHtml += "<li><a class='menuitem'>";
					var stlStr = pgSizeList[i]==options.pageSize?"selected":"unselected";
					btnHtml += "<span class='dropdown-" + stlStr + "' pagesize=" + pgSizeList[i] + "></span>";
					btnHtml += "每页" + pgSizeList[i] + "条</a></li>";
				}
				btnHtml += "</ul></li>"
				
				btnHtml += "</ul></div>";
				//前后翻页按钮
				btnHtml += "<span class='itc_pg_btn itc_pg_next'></span>"
				btnHtml += "<span class='itc_pg_btn itc_pg_prev'></span>";				
				//菜单式翻页器
				var totalPage = Math.ceil(options.total/options.pageSize) || 1;
				btnHtml += "<div class='dropdown bbox' style='float:right'><div class='itc_pg_selector_wrap' data-toggle='dropdown'><span class='currpage'>";
				btnHtml += options.pageNumber + "</span><span class='itc_pagination_divider'>/</span><span>" + totalPage;
				btnHtml += "</span><span class='itc_pg_selector_icon'></span></div>";
				btnHtml += "<ul class='itc_pagination_menu dropdown-menu' role='menu' style='overflow-y:auto;overflow-x:hidden;max-height:208px'>";
				var pEnd = totalPage>8?8:totalPage;
					for(var i=1;i<=totalPage;i++){
					btnHtml += "<li turnto=" + i + "><a class='menuitem'>" + i +"<span class='itc_pagination_divider'>/</span>" + totalPage + "</a></li>";
				}
				//btnHtml += "<li class='divider'></li>";
				//btnHtml += "<li turnto=1><a class='menuitem'>首页</a></li>";
				//btnHtml += "<li turnto=" + totalPage + "><a class='menuitem'>末页</a></li>";
				btnHtml += "</ul></div>";
				$(arg).html(btnHtml);
				//表格下部分页器
				var btmPager = $(arg).attr("bottompager");
				if(btmPager && !window._dialogEmmbed){
					var bHtml = '<div class="btn-group btn-group-sm">';
					bHtml += '<button type="button" class="btn btn-default itc_pg_round_l btn_sm_w88"><b class="itc_pgprev_nr"></b><span>上一页</span></button>';
					bHtml += '<button type="button" class="btn btn-default itc_pg_round_r btn_sm_w88"><span>下一页</span><b class="itc_pgnext_nr"></b></button>';
					bHtml += '</div>';
				}
				$(btmPager).addClass("bbox").html(bHtml).css({"text-align":"center","margin-top":"20px"});
				$(btmPager).data("targetITCPager",$(arg));
				$(btmPager).data("targetEUPager",_this);
				_pagination.css("display","none");
				_t.bindMenuPagerEvents();				
			};

			_t.adjustButtons = function(tgtITCPager){	
				tgt = tgtITCPager==null?$(arg):tgtITCPager;
				var opt = tgtITCPager==null?options:tgt.data("targetPager").pagination("options");
				var bottomPager = tgt.attr("bottompager");
				var bottomNoPrev = false;
				var bottomNoNext = false;
				if(opt.pageNumber>1){
					tgt.find(".itc_pg_prev_disable").removeClass("itc_pg_prev_disable").addClass("itc_pg_prev");
				}
				else{
					tgt.find(".itc_pg_prev").addClass("itc_pg_prev_disable").removeClass("itc_pg_prev");
					bottomNoPrev = true;
				}

				if(opt.pageNumber*opt.pageSize<opt.total){
					tgt.find(".itc_pg_next_disable").removeClass("itc_pg_next_disable").addClass("itc_pg_next");
				}
				else{
					tgt.find(".itc_pg_next").addClass("itc_pg_next_disable").removeClass("itc_pg_next");
					bottomNoNext = true;
				}

				if(bottomPager && !window._dialogEmmbed){
					var bPager = $(bottomPager);
					if(bottomNoNext && bottomNoPrev){
						bPager.hide();
					}
					else {
						bPager.show();
						var d = bPager.children("div");
						if(bottomNoPrev){
							d.children(".itc_pg_round_l").attr("disabled","disabled");
						}
						else{
							d.children(".itc_pg_round_l").removeAttr("disabled");
						}

						if(bottomNoNext){
							d.children(".itc_pg_round_r").attr("disabled","disabled");
						}
						else{
							d.children(".itc_pg_round_r").removeAttr("disabled");
						}
					}
				}
			};

			_t.addEvents = function(){				
				var pgBtn = $(arg).find(".itc_pg_btn");
				$(arg).data("targetPager",_pagination);
				$(arg).data("targetDiv",arg);
				$(arg).data('ptrGrid',_this);
				/*## 2014-06-03底部分页器事件*/
				var bottomPager = $(arg).attr("bottompager");
				if(bottomPager){
					var bWrap = $(bottomPager).children("div");
					bWrap.children(".itc_pg_round_l").click(function(){
						var pp = $(this).parent().parent();
						var itcPager = $(this).parent().parent().data("targetITCPager");
						var opt= pp.data("targetEUPager").datagrid("getPager").pagination("options");
						_t.turnTo(itcPager,parseInt(opt.pageNumber) - 1);
						$(this).blur();
					});

					bWrap.children(".itc_pg_round_r").click(function(){
						var pp = $(this).parent().parent();
						var itcPager = $(this).parent().parent().data("targetITCPager");
						var opt= pp.data("targetEUPager").datagrid("getPager").pagination("options");
						_t.turnTo(itcPager,parseInt(opt.pageNumber) + 1);
						$(this).blur();
					});
				}
				//顶部分页器事件
				pgBtn.click(function(e){
					var __this = $(this);
					if(!__this.hasClass("itc_pg_setting")){						
						var targetPager = __this.parent().data("targetPager");
						var targetITCPager = $(__this.parent().data("targetDiv"));
						var opt = targetPager.pagination("options");
						if(__this.hasClass("itc_pg_prev")){
							_t.turnTo(targetITCPager,parseInt(opt.pageNumber) - 1);
						}
						if(__this.hasClass("itc_pg_next")){
							_t.turnTo(targetITCPager,parseInt(opt.pageNumber) + 1);
						}
					}
				});
			};

			_t.search = function(opts){
				var dgOpts = _this.datagrid("options");
				if(!dgOpts.url && dgOpts.__data){
					_t.localSearch(opts);
				}
			};

			_t.localSearch = function(opts){
				var dgOpts = _this.datagrid("options");
				var newData = [];
				var oldData = dgOpts.__data.rows;
				for(var i=0;i<oldData.length;i++){
					var canAdd = true;
					var row = oldData[i];
					for(var field in row){
						var fldStr = ("" + row[field]).toLowerCase();
						
						if(opts[field]){
							var searchStr = ("" + opts[field]).toLowerCase();
							if(searchStr=="null" || !searchStr){
								searchStr = "";
							}
							if(fldStr.indexOf(searchStr)<0){
								canAdd=false;
								break;
							}
						}
						if(opts["_" + field]){//等值combo搜索
							var searchStr = ("" + opts["_" + field]).toLowerCase();
							if(fldStr!=searchStr && searchStr){
								canAdd = false;
								break;
							}
						}
					}
					if(canAdd){
						newData.push(row);
					}
				}
				//重建datagrid和分页器
				dgOpts["_data"] = {"rows":newData,"total":newData.length};
				dgOpts["data"] = {"rows":newData.slice(0,dgOpts.pageSize),"total":newData.length};
				_t.datagrid(dgOpts);
				_t.ITCUI_Pagination("createPg",_t.data("target"),_t.data("styleOpt"));
			};

			if(action=="create"||action=="createPg"){
				_t.createPagination();
				_t.adjustButtons();
				_t.addEvents();
			}
			else if(action=="search"){
				_t.search(arg);
			}
		}
	});
})(jQuery);

function _swap(arr,i, j){ 
    var temp = arr[i]; 
    arr[i] = arr[j]; 
    arr[j] = temp; 
}
 
function _arr_sort(arr,field,sort){
    for (var i = arr.length - 1; i > 0; --i) { 
        for (var j = 0; j < i; ++j) { 
            if(sort=="desc"){
                if (arr[j][field] < arr[j + 1][field]) {
                    _swap(arr,j, j + 1); 
                }
            }
            else{
                if (arr[j][field] > arr[j + 1][field]) {
                    _swap(arr,j, j + 1); 
                }
            }
        } 
    } 
}
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\thirdparty\js\icheck.js
*/
/*!
 * iCheck v1.0.1, http://git.io/arlzeA
 * ===================================
 * Powerful jQuery and Zepto plugin for checkboxes and radio buttons customization
 *
 * (c) 2013 Damir Sultanov, http://fronteed.com
 * MIT Licensed
 */

(function($) {

  // Cached vars
  var _iCheck = 'iCheck',
    _iCheckHelper = _iCheck + '-helper',
    _checkbox = 'checkbox',
    _radio = 'radio',
    _checked = 'checked',
    _unchecked = 'un' + _checked,
    _disabled = 'disabled',
    _determinate = 'determinate',
    _indeterminate = 'in' + _determinate,
    _update = 'update',
    _type = 'type',
    _click = 'click',
    _touch = 'touchbegin.i touchend.i',
    _add = 'addClass',
    _remove = 'removeClass',
    _callback = 'trigger',
    _label = 'label',
    _cursor = 'cursor',
    _mobile = /ipad|iphone|ipod|android|blackberry|windows phone|opera mini|silk/i.test(navigator.userAgent);

  // Plugin init
  $.fn[_iCheck] = function(options, fire) {

    // Walker
    var handle = 'input[type="' + _checkbox + '"], input[type="' + _radio + '"]',
      stack = $(),
      walker = function(object) {
        object.each(function() {
          var self = $(this);

          if (self.is(handle)) {
            stack = stack.add(self);
          } else {
            stack = stack.add(self.find(handle));
          };
        });
      };

    // Check if we should operate with some method
    if (/^(check|uncheck|toggle|indeterminate|determinate|disable|enable|update|destroy)$/i.test(options)) {

      // Normalize method's name
      options = options.toLowerCase();

      // Find checkboxes and radio buttons
      walker(this);

      return stack.each(function() {
        var self = $(this);

        if (options == 'destroy') {
          tidy(self, 'ifDestroyed');
        } else {
          operate(self, true, options);
        };

        // Fire method's callback
        if ($.isFunction(fire)) {
          fire();
        };
      });

    // Customization
    } else if (typeof options == 'object' || !options) {

      // Check if any options were passed
      var settings = $.extend({
          checkedClass: _checked,
          disabledClass: _disabled,
          indeterminateClass: _indeterminate,
          labelHover: true,
          aria: false
        }, options),

        selector = settings.handle,
        hoverClass = settings.hoverClass || 'hover',
        focusClass = settings.focusClass || 'focus',
        activeClass = settings.activeClass || 'active',
        labelHover = !!settings.labelHover,
        labelHoverClass = settings.labelHoverClass || 'hover',

        // Setup clickable area
        area = ('' + settings.increaseArea).replace('%', '') | 0;

      // Selector limit
      if (selector == _checkbox || selector == _radio) {
        handle = 'input[type="' + selector + '"]';
      };

      // Clickable area limit
      if (area < -50) {
        area = -50;
      };

      // Walk around the selector
      walker(this);

      return stack.each(function() {
        var self = $(this);

        // If already customized
        tidy(self);

        var node = this,
          id = node.id,

          // Layer styles
          offset = -area + '%',
          size = 100 + (area * 2) + '%',
          layer = {
            position: 'absolute',
            top: offset,
            left: offset,
            display: 'block',
            width: size,
            height: size,
            margin: 0,
            padding: 0,
            background: '#fff',
            border: 0,
            opacity: 0
          },

          // Choose how to hide input
          hide = _mobile ? {
            position: 'absolute',
            visibility: 'hidden'
          } : area ? layer : {
            position: 'absolute',
            opacity: 0
          },

          // Get proper class
          className = node[_type] == _checkbox ? settings.checkboxClass || 'i' + _checkbox : settings.radioClass || 'i' + _radio,

          // Find assigned labels
          label = $(_label + '[for="' + id + '"]').add(self.closest(_label)),

          // Check ARIA option
          aria = !!settings.aria,

          // Set ARIA placeholder
          ariaID = _iCheck + '-' + Math.random().toString(36).substr(2,6),

          // Parent & helper
          parent = '<div class="' + className + '" ' + (aria ? 'role="' + node[_type] + '" ' : ''),
          helper;

        // Set ARIA "labelledby"
        if (aria) {
          label.each(function() {
            parent += 'aria-labelledby="';

            if (this.id) {
              parent += this.id;
            } else {
              this.id = ariaID;
              parent += ariaID;
            }

            parent += '"';
          });
        };

        // Wrap input
        parent = self.wrap(parent + '/>')[_callback]('ifCreated').parent().append(settings.insert);

        // Layer addition
        helper = $('<ins class="' + _iCheckHelper + '"/>').css(layer).appendTo(parent);

        // Finalize customization
        self.data(_iCheck, {o: settings, s: self.attr('style')}).css(hide);
        !!settings.inheritClass && parent[_add](node.className || '');
        !!settings.inheritID && id && parent.attr('id', _iCheck + '-' + id);
        parent.css('position') == 'static' && parent.css('position', 'relative');
        operate(self, true, _update);

        // Label events
        if (label.length) {
          label.on(_click + '.i mouseover.i mouseout.i ' + _touch, function(event) {
            var type = event[_type],
              item = $(this);

            // Do nothing if input is disabled
            if (!node[_disabled]) {

              // Click
              if (type == _click) {
                if ($(event.target).is('a')) {
                  return;
                }
                operate(self, false, true);

              // Hover state
              } else if (labelHover) {

                // mouseout|touchend
                if (/ut|nd/.test(type)) {
                  parent[_remove](hoverClass);
                  item[_remove](labelHoverClass);
                } else {
                  parent[_add](hoverClass);
                  item[_add](labelHoverClass);
                };
              };

              if (_mobile) {
                event.stopPropagation();
              } else {
                return false;
              };
            };
          });
        };

        // Input events
        self.on(_click + '.i focus.i blur.i keyup.i keydown.i keypress.i', function(event) {
          var type = event[_type],
            key = event.keyCode;

          // Click
          if (type == _click) {
            return false;

          // Keydown
          } else if (type == 'keydown' && key == 32) {
            if (!(node[_type] == _radio && node[_checked])) {
              if (node[_checked]) {
                off(self, _checked);
              } else {
                on(self, _checked);
              };
            };

            return false;

          // Keyup
          } else if (type == 'keyup' && node[_type] == _radio) {
            !node[_checked] && on(self, _checked);

          // Focus/blur
          } else if (/us|ur/.test(type)) {
            parent[type == 'blur' ? _remove : _add](focusClass);
          };
        });

        // Helper events
        helper.on(_click + ' mousedown mouseup mouseover mouseout ' + _touch, function(event) {
          var type = event[_type],

            // mousedown|mouseup
            toggle = /wn|up/.test(type) ? activeClass : hoverClass;

          // Do nothing if input is disabled
          if (!node[_disabled]) {

            // Click
            if (type == _click) {
              operate(self, false, true);

            // Active and hover states
            } else {

              // State is on
              if (/wn|er|in/.test(type)) {

                // mousedown|mouseover|touchbegin
                parent[_add](toggle);

              // State is off
              } else {
                parent[_remove](toggle + ' ' + activeClass);
              };

              // Label hover
              if (label.length && labelHover && toggle == hoverClass) {

                // mouseout|touchend
                label[/ut|nd/.test(type) ? _remove : _add](labelHoverClass);
              };
            };

            if (_mobile) {
              event.stopPropagation();
            } else {
              return false;
            };
          };
        });
      });
    } else {
      return this;
    };
  };

  // Do something with inputs
  function operate(input, direct, method) {
    var node = input[0],
      state = /er/.test(method) ? _indeterminate : /bl/.test(method) ? _disabled : _checked,
      active = method == _update ? {
        checked: node[_checked],
        disabled: node[_disabled],
        indeterminate: input.attr(_indeterminate) == 'true' || input.attr(_determinate) == 'false'
      } : node[state];

    // Check, disable or indeterminate
    if (/^(ch|di|in)/.test(method) && !active) {
      on(input, state);

    // Uncheck, enable or determinate
    } else if (/^(un|en|de)/.test(method) && active) {
      off(input, state);

    // Update
    } else if (method == _update) {

      // Handle states
      for (var state in active) {
        if (active[state]) {
          on(input, state, true);
        } else {
          off(input, state, true);
        };
      };

    } else if (!direct || method == 'toggle') {

      // Helper or label was clicked
      if (!direct) {
        input[_callback]('ifClicked');
      };

      // Toggle checked state
      if (active) {
        if (node[_type] !== _radio) {
          off(input, state);
        };
      } else {
        on(input, state);
      };
    };
  };

  // Add checked, disabled or indeterminate state
  function on(input, state, keep) {
    var node = input[0],
      parent = input.parent(),
      checked = state == _checked,
      indeterminate = state == _indeterminate,
      disabled = state == _disabled,
      callback = indeterminate ? _determinate : checked ? _unchecked : 'enabled',
      regular = option(input, callback + capitalize(node[_type])),
      specific = option(input, state + capitalize(node[_type]));

    // Prevent unnecessary actions
    if (node[state] !== true) {

      // Toggle assigned radio buttons
      if (!keep && state == _checked && node[_type] == _radio && node.name) {
        var form = input.closest('form'),
          inputs = 'input[name="' + node.name + '"]';

        inputs = form.length ? form.find(inputs) : $(inputs);

        inputs.each(function() {
          if (this !== node && $(this).data(_iCheck)) {
            off($(this), state);
          };
        });
      };

      // Indeterminate state
      if (indeterminate) {

        // Add indeterminate state
        node[state] = true;

        // Remove checked state
        if (node[_checked]) {
          off(input, _checked, 'force');
        };

      // Checked or disabled state
      } else {

        // Add checked or disabled state
        if (!keep) {
          node[state] = true;
        };

        // Remove indeterminate state
        if (checked && node[_indeterminate]) {
          off(input, _indeterminate, false);
        };
      };

      // Trigger callbacks
      callbacks(input, checked, state, keep);
    };

    // Add proper cursor
    if (node[_disabled] && !!option(input, _cursor, true)) {
      parent.find('.' + _iCheckHelper).css(_cursor, 'default');
    };

    // Add state class
    parent[_add](specific || option(input, state) || '');

    // Set ARIA attribute
    disabled ? parent.attr('aria-disabled', 'true') : parent.attr('aria-checked', indeterminate ? 'mixed' : 'true');

    // Remove regular state class
    parent[_remove](regular || option(input, callback) || '');
  };

  // Remove checked, disabled or indeterminate state
  function off(input, state, keep) {
    var node = input[0],
      parent = input.parent(),
      checked = state == _checked,
      indeterminate = state == _indeterminate,
      disabled = state == _disabled,
      callback = indeterminate ? _determinate : checked ? _unchecked : 'enabled',
      regular = option(input, callback + capitalize(node[_type])),
      specific = option(input, state + capitalize(node[_type]));

    // Prevent unnecessary actions
    if (node[state] !== false) {

      // Toggle state
      if (indeterminate || !keep || keep == 'force') {
        node[state] = false;
      };

      // Trigger callbacks
      callbacks(input, checked, callback, keep);
    };

    // Add proper cursor
    if (!node[_disabled] && !!option(input, _cursor, true)) {
      parent.find('.' + _iCheckHelper).css(_cursor, 'pointer');
    };

    // Remove state class
    parent[_remove](specific || option(input, state) || '');

    // Set ARIA attribute
    disabled ? parent.attr('aria-disabled', 'false') : parent.attr('aria-checked', 'false');

    // Add regular state class
    parent[_add](regular || option(input, callback) || '');
  };

  // Remove all traces
  function tidy(input, callback) {
    if (input.data(_iCheck)) {

      // Remove everything except input
      input.parent().html(input.attr('style', input.data(_iCheck).s || ''));

      // Callback
      if (callback) {
        input[_callback](callback);
      };

      // Unbind events
      input.off('.i').unwrap();
      $(_label + '[for="' + input[0].id + '"]').add(input.closest(_label)).off('.i');
    };
  };

  // Get some option
  function option(input, state, regular) {
    if (input.data(_iCheck)) {
      return input.data(_iCheck).o[state + (regular ? '' : 'Class')];
    };
  };

  // Capitalize some string
  function capitalize(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  };

  // Executable handlers
  function callbacks(input, checked, callback, keep) {
    if (!keep) {
      if (checked) {
        input[_callback]('ifToggled');
      };

      input[_callback]('ifChanged')[_callback]('if' + capitalize(callback));
    };
  };
})(window.jQuery || window.Zepto);

/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_combobox_v2.js
*/
(function($){
	$.fn.extend({
		iCombo: function(act, options){
			var self = $(this);
			var _t = this;			
			
			_t.init = function(opts){
				opts = opts || {};
				var defaultOptions = {
					fixWrapWidth : !!opts.wrapWidth,
					wrapWidth: 150,
					onChange: function(){},
					url: null,
					onRemoteData: function(v){return v;},
					remoteLoadOn: "click",
					displayItemCount: 6,
					allowEmpty:  (opts.url && opts.remoteLoadOn=="click") || false,
					initOnChange: true,
					firstItemEmpty: false,
					allowSearch: false,
					sbPlaceHolder: "输入关键字搜索",
					delimiter: "##",
					highlight: "#aaa",
					multiselect: false,
					onRenderFinish: function(){},
					syncOnChange : false,
				};
				var comboOpts = $.extend(defaultOptions, opts);
				self.data("options", comboOpts);
				self.data("dataMapping", []);
				self.data("firstOnChange", true);//是否第一次触发onChange事件
				self.data("selectedValues", {});
				if(!comboOpts.data){
					_t.initDataFromHtml(comboOpts);
				}
				self.data("ptrInput", self.prev(".itcui_combo").children("input"));
				if(comboOpts.data){
					_t.updateListData(comboOpts.data);
					_t.setInitValue();
					if(_t.getVal()){
						_t.triggerOnChange();
					}
				}				
				_t.initUI();
				if(comboOpts.url){
					_t.doRemoteLoad(comboOpts.url);
				}
				return self;
			};
			
			_t.initDataFromHtml = function(opts){
				var children = self.children();
				if(children.length){
					var options = self.data("options");
					var data = [];
					for(var i=0;i<children.length;i++){
						var item = $(children[i]);
						data.push([item.attr("value"),item.text(),!!item.attr("selected")]);
					}
					options.data = data;	
				}
			};
			
			function hasDataInitValue(data){
				if(isArray(data)){
					return false;
				}
				for(var i=0;i<data.length;i++){
					var obj = data[i];
					if(obj[2]){
						return true;
					}
				}
				return false;
			}
			
			_t.doDataLoad = function(data){
				_t.updateListData(data);
				if(!isValueIntersect(data, self.data("selectedValues"))){
					var options = self.data("options");
					if(!_t.getVal() && options.allowEmpty && !hasDataInitValue(data)){
						//如果没有初值 而且允许为空 则直接跳过加载过程
						return;
					}
					_t.setInitValue();
				}
				_t.updateText();
			};
			
			_t.doRemoteLoad = function(url){
				$.ajax({
					url : url,
					dataType: "json",
					error : function(req,errStr,errObj){
						FW.error("从服务器获取数据失败（" + errStr + "）");						
					},
					success: function(data){
						var func = self.data("options").onRemoteData;
						data = func(data);
						_t.doDataLoad(data);
					}
				});
			};
				
			_t.setInitValue = function(){
				var dataMapping = self.data("dataMapping");
				var opts = self.data("options");
				var valSet = false;
				var selectedValues = {};
				var item;
				if(dataMapping.length){
					for(var i=0;i<dataMapping.length;i++){
						item = dataMapping[i];
						if(item.initSelected){
							selectedValues[item.value] = true;
							valSet = true;
						}
					}
					//如果不允许为空，则自动把第一个值作为默认值
					if(!valSet && !opts.allowEmpty){
						item = dataMapping[0];
						selectedValues[item.value] = true;
					}
				}
				self.data("selectedValues", selectedValues);
				self.prev(".itcui_combo").find(".valid-field").val(_t.getVal());			
			};
			
			_t.updateListData = function(data){
				var jqData = [];
				var opts = self.data("options");
				if(opts.firstItemEmpty){
					jqData.push({
						"value": "",
						"title": "请选择",
						"clearSelect": true
					});
				}
				if(isArray(data)){
					for(var i=0;i<data.length;i++){
						var item = data[i];
						jqData.push({
							"value": item[0],
							"title": item[1] + "",
							initSelected:!!item[2]
						});
					}
				}else{
					for(var k in data){
						jqData.push({"value": k,"title": data[k] + ""});
					}
				}
				self.data("dataMapping", jqData);
			};
			
			_t.triggerOnChange = function(){
				var opts = self.data("options");
				if(!opts.initOnChange){
					if(self.data("firstOnChange")){
						self.data("firstOnChange",false);
						return;
					}
				}
				var val = _t.getVal();
				self.trigger("onComboChange", {
					value : val,
					text: _t.getTxt()
				});
				if(opts.syncOnChange){
					opts.onChange(val, self);
				}else{
					setTimeout(function(){
						opts.onChange(val, self);	
					}, 0);
				}					
			};
			
			_t.initUI = function(){
				//删除之前生成的combobox
				var prevDiv = self.prev("div");
				UiCommon.makeWrap(self);
				if(prevDiv.hasClass("itcui_combo")){
					prevDiv.remove();
				}
				var name = self.attr("_name");
				var valueStr = _t.getVal() ? (" value='" + _t.getVal() + "' ") : "";
				var comboHtml = "<div class='itcui_combo remove-on-scroll bbox' style='position:relative;width:100%;'>";
				comboHtml += "<input class='valid-field' type='hidden' name='" + name + "' " + valueStr + "/>";
				comboHtml += "<span class='cb_text'>" + _t.getTxt() + "</span><span class='arrow_wrap'><b class='itcui_combo_arrow'></b></span>";
				comboHtml += "</div>";
				self.css("display", "none").before(comboHtml);
				self.prev(".itcui_combo").click(function(e){
					e.stopPropagation();
					_t.displayDropdown();
					if(window._dialogEmmbed){
						_parent().$(".window-mask").click(function(){
							UiCommon.removeComboWrap();
						});
					}	
				});
			};
			
			_t.displayDropdown = function(){
				UiCommon.removeComboWrap();
				var opts = self.data("options");
				var cbWidth = opts.fixWrapWidth?parseInt(opts.wrapWidth):self.parent().width();
				cbWidth = Math.max(cbWidth, 150);
				//处理选项高亮 用_title存储高亮后的文字
				var highlight = opts.highlight;
				var delimiter = opts.delimiter;
				var dataMapping = self.data("dataMapping");
				if(highlight){
					var regex = new RegExp(delimiter + ".*?" + delimiter);
					var regex2 = new RegExp(delimiter, "g");
					for(var i=0;i<dataMapping.length;i++){
						var item = dataMapping[i];
						if(item._title){
							break;
						}
						var title = item.title;
						var fstr = title.match(regex);
						if(fstr){
							if(typeof(fstr) != "string"){
								fstr = fstr[0];
							}
							var tmp = fstr.replace(regex2, "");
							tmp = '<span style="color:' + highlight + '">' + tmp + '</span>';
							item._title = title.replace(fstr, tmp);
						}
					}
				}
				var selectedValues = self.data("selectedValues");
				var tplData = {
					cbWidth : cbWidth,
					dialogEmmbed: window._dialogEmmbed,
					opts: opts,
					dataMapping: dataMapping,
					selectedValues: selectedValues
				};
				//将初始选择状态写到dataMapping中 因为在模板中没法双变量嵌套
				for(var i=0;i<dataMapping.length;i++){
					if(opts.multiselect){
						for(var i=0;i<dataMapping.length;i++){
							var item = dataMapping[i];
							item.checked = !!selectedValues[dataMapping[i].value]; 
						}
					}
				}
				var render = _templates._comboDropDown;
				if(!render){
					render = template.compile(_templates.comboDropDown);
					_templates._comboDropDown = render;
				}				
				var listHtml = render(tplData);
				var wrap = $(listHtml);				
				if(self.attr("fixposition")){
					//某些特殊情况下，当外框容器有display:none时需要把wrap放到body里
					$("body").append(wrap);
					var offset = self.prev().offset();
					wrap.css({
						top : parseInt(offset.top) + self.prev().height() + 2 + "px",
						left : offset.left
					});
				}				
				else{
					self.after(wrap);
				}
				wrap.data("targetSelect", self);
				if(opts.allowSearch){
					_t.bindSearchEvent();
				}
				_t.bindDropDownEvents(wrap);
				UiCommon.moveToParent(wrap, self.prev(".itcui_combo"));
			};
			
			_t.bindSearchEvent = function(){
				var jqObj = $("#itc_combo_search"); 
				jqObj.iInput("init");
				jqObj.parent().click(function(e){
					e.stopPropagation();
				})
				jqObj.keyup(function(e){
					var me = $(this);
					var itemWrap = me.parents(".itcui_dropdown_menu");
					var kw = me.val().toLowerCase();
					itemWrap.children(".itcui_dropdown_item").each(function(){
						var item = $(this);
						var txt = item.text().toLowerCase();
						if(txt.indexOf(kw)>=0){
							item.show();
							//颜色高亮
							coloredTxt = txt.replace(kw,"<span style='color:#f00'>" + kw + "</span>");
							var txtSpan = item.find(".itcui_dropdown_text");
							if(txtSpan.length === 0){
								//多选用label
								txtSpan = item.find("label");
							}
							txtSpan.html(coloredTxt);
						}
						else{
							item.hide();
						}
					});
				}).focus();
			};
			
			_t.bindDropDownEvents = function(wrap){
				var opts = self.data("options");
				if(opts.multiselect){
					wrap.find("input").iCheck({
						checkboxClass: 'icheckbox_flat-blue',
						radioClass: 'iradio_flat-blue'
					});
					wrap.on("ifChanged", ".itcui_dropdown_checkbox", function(e){
						var item = $(this).parents(".itcui_dropdown_item");
						var wrap = item.parents("#itc_combo_wrap");
						_t.doSelect(wrap.data("targetSelect"), +item.attr("data-index"));
					});
				}else{
					wrap.on("click", ".itcui_dropdown_item", function(e){
						var me = $(this);
						var wrap = me.parents("#itc_combo_wrap");
						var target = wrap.data("targetSelect");
						var index = +me.attr("data-index");
						if(!opts.multiselect){
							wrap.remove();
						}
						_t.doSelect(target, index);
					});	
				}				
			};
			
			_t.doSelect = function(target, index){
				var opts = target.data("options");				
				var mapping = target.data("dataMapping");
				var item = mapping[index];
				var selectedValues = self.data("selectedValues");
				//手动选择一定无视firstOnChange
				self.data("firstOnChange", false);			
				if(!item.clearSelect){
					if(!opts.multiselect){
						if(selectedValues[item.value]){
							//两次选择同一个选项不会触发onChange
							return;
						}
						selectedValues = {};
						selectedValues[item.value] = true;	
					}else{
						selectedValues[item.value] = !!!selectedValues[item.value];
					}
				}else{
					selectedValues = {};
				}
				self.data("selectedValues", selectedValues);
				_t.updateText();
			};
			
			_t.setVal = function(val){
				var v = {};
				var opts = self.data("options");
				if(opts.multiselect && typeof(val) == "string"){
					var tmp = {};
					var arr = val.split(",");
					for(var i=0;i<arr.length;i++){
						tmp[arr[i]] = true;
					}
					val = tmp;
				}
				if(val === null || val === undefined){
					
				}else if(typeof(val) == "object"){
					v = val;
					for(var k in val){
						if(val[k]){
							v[k] = true;
						}
					}
				}else{
					if(val || val === 0){
						v[val + ""] = true;
					}
				}
				self.data("selectedValues", v);
				_t.updateText();				
				return self;
			};
			
			_t.getVal = function(){
				var selectedValues = self.data("selectedValues");
				return selectedValues ? UiCommon.joinKeys(selectedValues) : "";
			};
			
			_t.setTxt = function(txt){
				self.prev("div").children(".cb_text").html(txt);
				return self;
			};
			
			_t.updateText = function(){
				self.prev("div").children(".cb_text").html(_t.getTxt());
				self.prev(".itcui_combo").find(".valid-field").val(_t.getVal());
				_t.triggerOnChange();
			};
			
			_t.getTxt = function(){
				var selectedTitles = [];
				var dataMapping = self.data("dataMapping") || {};
				var selectedValues = self.data("selectedValues");
				var options = self.data("options");
				var reg = new RegExp(options.delimiter, "g");
				for(var i=0;i<dataMapping.length;i++){
					var item = dataMapping[i];
					if(selectedValues[item.value]){
						var title = item.title;
						if(typeof(title) == "string"){
							title = title.replace(reg, "");
						}
						selectedTitles.push(title);
					}
				}
				return selectedTitles.join(",");
			};
			
			function isValueIntersect(data, selected){
				var tmpMap = data;
				if(isArray(data)){
					tmpMap = {};
					for(var i=0;i<data.length;i++){
						tmpMap[data[i][0]] = true;
					}
				}
				for(var k in tmpMap){
					if(selected[k]){
						return true;
					}
				}
				return false;
			}
			
			_t.loadData = function(data){
				if(!data){
					return;
				}
				//判断combo之前的值是否有效 如果之前赋的值在data中存在，则继续用这个做初值，否则清空重新选一个初值
				_t.doDataLoad(data);
				return self;
			};
			
			_t.getData = function(){
				return self.data("dataMapping");
			};
					
			/*loader部分*/
			var finalAct = null;
			var mapping = {
				getSelected: "getVal",
				getSelectedText: "getTxt",
				select: "setVal",
				setValue: "setVal",
				setText: "setText"
			};
			if(act == null){
				finalAct = "init";
			}else if(mapping[act]){
				finalAct = mapping[act];
			}else{
				finalAct = act;
			}
			
			if(finalAct){
				return _t[finalAct](options);
			}
		}
	});
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.draggable.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
function _1(e){
var _2=$.data(e.data.target,"draggable");
var _3=_2.options;
var _4=_2.proxy;
var _5=e.data;
var _6=_5.startLeft+e.pageX-_5.startX;
var _7=_5.startTop+e.pageY-_5.startY;
if(_4){
if(_4.parent()[0]==document.body){
if(_3.deltaX!=null&&_3.deltaX!=undefined){
_6=e.pageX+_3.deltaX;
}else{
_6=e.pageX-e.data.offsetWidth;
}
if(_3.deltaY!=null&&_3.deltaY!=undefined){
_7=e.pageY+_3.deltaY;
}else{
_7=e.pageY-e.data.offsetHeight;
}
}else{
if(_3.deltaX!=null&&_3.deltaX!=undefined){
_6+=e.data.offsetWidth+_3.deltaX;
}
if(_3.deltaY!=null&&_3.deltaY!=undefined){
_7+=e.data.offsetHeight+_3.deltaY;
}
}
}
if(e.data.parent!=document.body){
_6+=$(e.data.parent).scrollLeft();
_7+=$(e.data.parent).scrollTop();
}
if(_3.axis=="h"){
_5.left=_6;
}else{
if(_3.axis=="v"){
_5.top=_7;
}else{
_5.left=_6;
_5.top=_7;
}
}
};
function _8(e){
var _9=$.data(e.data.target,"draggable");
var _a=_9.options;
var _b=_9.proxy;
if(!_b){
_b=$(e.data.target);
}
_b.css({left:e.data.left,top:e.data.top});
$("body").css("cursor",_a.cursor);
};
function _c(e){
$.fn.draggable.isDragging=true;
var _d=$.data(e.data.target,"draggable");
var _e=_d.options;
var _f=$(".droppable").filter(function(){
return e.data.target!=this;
}).filter(function(){
var _10=$.data(this,"droppable").options.accept;
if(_10){
return $(_10).filter(function(){
return this==e.data.target;
}).length>0;
}else{
return true;
}
});
_d.droppables=_f;
var _11=_d.proxy;
if(!_11){
if(_e.proxy){
if(_e.proxy=="clone"){
_11=$(e.data.target).clone().insertAfter(e.data.target);
}else{
_11=_e.proxy.call(e.data.target,e.data.target);
}
_d.proxy=_11;
}else{
_11=$(e.data.target);
}
}
_11.css("position","absolute");
_1(e);
_8(e);
_e.onStartDrag.call(e.data.target,e);
return false;
};
function _12(e){
var _13=$.data(e.data.target,"draggable");
_1(e);
if(_13.options.onDrag.call(e.data.target,e)!=false){
_8(e);
}
var _14=e.data.target;
_13.droppables.each(function(){
var _15=$(this);
if(_15.droppable("options").disabled){
return;
}
var p2=_15.offset();
if(e.pageX>p2.left&&e.pageX<p2.left+_15.outerWidth()&&e.pageY>p2.top&&e.pageY<p2.top+_15.outerHeight()){
if(!this.entered){
$(this).trigger("_dragenter",[_14]);
this.entered=true;
}
$(this).trigger("_dragover",[_14]);
}else{
if(this.entered){
$(this).trigger("_dragleave",[_14]);
this.entered=false;
}
}
});
return false;
};
function _16(e){
$.fn.draggable.isDragging=false;
_12(e);
var _17=$.data(e.data.target,"draggable");
var _18=_17.proxy;
var _19=_17.options;
if(_19.revert){
if(_1a()==true){
$(e.data.target).css({position:e.data.startPosition,left:e.data.startLeft,top:e.data.startTop});
}else{
if(_18){
var _1b,top;
if(_18.parent()[0]==document.body){
_1b=e.data.startX-e.data.offsetWidth;
top=e.data.startY-e.data.offsetHeight;
}else{
_1b=e.data.startLeft;
top=e.data.startTop;
}
_18.animate({left:_1b,top:top},function(){
_1c();
});
}else{
$(e.data.target).animate({left:e.data.startLeft,top:e.data.startTop},function(){
$(e.data.target).css("position",e.data.startPosition);
});
}
}
}else{
$(e.data.target).css({position:"absolute",left:e.data.left,top:e.data.top});
_1a();
}
_19.onStopDrag.call(e.data.target,e);
$(document).unbind(".draggable");
setTimeout(function(){
$("body").css("cursor","");
},100);
function _1c(){
if(_18){
_18.remove();
}
_17.proxy=null;
};
function _1a(){
var _1d=false;
_17.droppables.each(function(){
var _1e=$(this);
if(_1e.droppable("options").disabled){
return;
}
var p2=_1e.offset();
if(e.pageX>p2.left&&e.pageX<p2.left+_1e.outerWidth()&&e.pageY>p2.top&&e.pageY<p2.top+_1e.outerHeight()){
if(_19.revert){
$(e.data.target).css({position:e.data.startPosition,left:e.data.startLeft,top:e.data.startTop});
}
$(this).trigger("_drop",[e.data.target]);
_1c();
_1d=true;
this.entered=false;
return false;
}
});
if(!_1d&&!_19.revert){
_1c();
}
return _1d;
};
return false;
};
$.fn.draggable=function(_1f,_20){
if(typeof _1f=="string"){
return $.fn.draggable.methods[_1f](this,_20);
}
return this.each(function(){
var _21;
var _22=$.data(this,"draggable");
if(_22){
_22.handle.unbind(".draggable");
_21=$.extend(_22.options,_1f);
}else{
_21=$.extend({},$.fn.draggable.defaults,$.fn.draggable.parseOptions(this),_1f||{});
}
var _23=_21.handle?(typeof _21.handle=="string"?$(_21.handle,this):_21.handle):$(this);
$.data(this,"draggable",{options:_21,handle:_23});
if(_21.disabled){
$(this).css("cursor","");
return;
}
_23.unbind(".draggable").bind("mousemove.draggable",{target:this},function(e){
if($.fn.draggable.isDragging){
return;
}
var _24=$.data(e.data.target,"draggable").options;
if(_25(e)){
$(this).css("cursor",_24.cursor);
}else{
$(this).css("cursor","");
}
}).bind("mouseleave.draggable",{target:this},function(e){
$(this).css("cursor","");
}).bind("mousedown.draggable",{target:this},function(e){
if(_25(e)==false){
return;
}
$(this).css("cursor","");
var _26=$(e.data.target).position();
var _27=$(e.data.target).offset();
var _28={startPosition:$(e.data.target).css("position"),startLeft:_26.left,startTop:_26.top,left:_26.left,top:_26.top,startX:e.pageX,startY:e.pageY,offsetWidth:(e.pageX-_27.left),offsetHeight:(e.pageY-_27.top),target:e.data.target,parent:$(e.data.target).parent()[0]};
$.extend(e.data,_28);
var _29=$.data(e.data.target,"draggable").options;
if(_29.onBeforeDrag.call(e.data.target,e)==false){
return;
}
$(document).bind("mousedown.draggable",e.data,_c);
$(document).bind("mousemove.draggable",e.data,_12);
$(document).bind("mouseup.draggable",e.data,_16);
});
function _25(e){
var _2a=$.data(e.data.target,"draggable");
var _2b=_2a.handle;
var _2c=$(_2b).offset();
var _2d=$(_2b).outerWidth();
var _2e=$(_2b).outerHeight();
var t=e.pageY-_2c.top;
var r=_2c.left+_2d-e.pageX;
var b=_2c.top+_2e-e.pageY;
var l=e.pageX-_2c.left;
return Math.min(t,r,b,l)>_2a.options.edge;
};
});
};
$.fn.draggable.methods={options:function(jq){
return $.data(jq[0],"draggable").options;
},proxy:function(jq){
return $.data(jq[0],"draggable").proxy;
},enable:function(jq){
return jq.each(function(){
$(this).draggable({disabled:false});
});
},disable:function(jq){
return jq.each(function(){
$(this).draggable({disabled:true});
});
}};
$.fn.draggable.parseOptions=function(_2f){
var t=$(_2f);
return $.extend({},$.parser.parseOptions(_2f,["cursor","handle","axis",{"revert":"boolean","deltaX":"number","deltaY":"number","edge":"number"}]),{disabled:(t.attr("disabled")?true:undefined)});
};
$.fn.draggable.defaults={proxy:null,revert:false,cursor:"move",deltaX:null,deltaY:null,handle:null,disabled:false,edge:0,axis:null,onBeforeDrag:function(e){
},onStartDrag:function(e){
},onDrag:function(e){
},onStopDrag:function(e){
}};
$.fn.draggable.isDragging=false;
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.droppable.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
function _1(_2){
$(_2).addClass("droppable");
$(_2).bind("_dragenter",function(e,_3){
$.data(_2,"droppable").options.onDragEnter.apply(_2,[e,_3]);
});
$(_2).bind("_dragleave",function(e,_4){
$.data(_2,"droppable").options.onDragLeave.apply(_2,[e,_4]);
});
$(_2).bind("_dragover",function(e,_5){
$.data(_2,"droppable").options.onDragOver.apply(_2,[e,_5]);
});
$(_2).bind("_drop",function(e,_6){
$.data(_2,"droppable").options.onDrop.apply(_2,[e,_6]);
});
};
$.fn.droppable=function(_7,_8){
if(typeof _7=="string"){
return $.fn.droppable.methods[_7](this,_8);
}
_7=_7||{};
return this.each(function(){
var _9=$.data(this,"droppable");
if(_9){
$.extend(_9.options,_7);
}else{
_1(this);
$.data(this,"droppable",{options:$.extend({},$.fn.droppable.defaults,$.fn.droppable.parseOptions(this),_7)});
}
});
};
$.fn.droppable.methods={options:function(jq){
return $.data(jq[0],"droppable").options;
},enable:function(jq){
return jq.each(function(){
$(this).droppable({disabled:false});
});
},disable:function(jq){
return jq.each(function(){
$(this).droppable({disabled:true});
});
}};
$.fn.droppable.parseOptions=function(_a){
var t=$(_a);
return $.extend({},$.parser.parseOptions(_a,["accept"]),{disabled:(t.attr("disabled")?true:undefined)});
};
$.fn.droppable.defaults={accept:null,disabled:false,onDragEnter:function(e,_b){
},onDragOver:function(e,_c){
},onDragLeave:function(e,_d){
},onDrop:function(e,_e){
}};
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.tree.js
*/
﻿/**
 * jQuery EasyUI 1.3.6
 * 
 * Copyright (c) 2009-2014 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL license: http://www.gnu.org/licenses/gpl.txt
 * To use it on other terms please contact us at info@jeasyui.com
 *
 */
 var _itc_treedblclick = false;
 var _itc_tlstclknode = null;//上一次点的节点
 var _itc_tlstclktime = null;//上一次点的时间
(function($) {
    function _1(_2) {
        var _3 = $(_2);
        _3.addClass("tree");
        return _3;
    };
    function _4(_5) {
        var _6 = $.data(_5, "tree").options;
        $(_5).unbind().bind("mouseover",
        function(e) {
            var tt = $(e.target);
            var _7 = tt.closest("div.tree-node");
            if (!_7.length) {
                return;
            }
            _7.addClass("tree-node-hover");
            if (tt.hasClass("tree-hit")) {
                if (tt.hasClass("tree-expanded")) {
                    tt.addClass("tree-expanded-hover");
                } else {
                    tt.addClass("tree-collapsed-hover");
                }
            }
            e.stopPropagation();
        }).bind("mouseout",
        function(e) {
            var tt = $(e.target);
            var _8 = tt.closest("div.tree-node");
            if (!_8.length) {
                return;
            }
            _8.removeClass("tree-node-hover");
            if (tt.hasClass("tree-hit")) {
                if (tt.hasClass("tree-expanded")) {
                    tt.removeClass("tree-expanded-hover");
                } else {
                    tt.removeClass("tree-collapsed-hover");
                }
            }
            _6.onMouseOut.call(_5, e, _c(_5, _8[0]));
            e.stopPropagation();
        }).bind("mouseenter",function(e){
            var tt = $(e.target);
            var _9 = tt.closest("div.tree-node");
            _6.onMouseEnter.call(_5, e, _c(_5, _9[0]));
            e.stopPropagation();
        }).bind("click",
        function(e) {
            var tt = $(e.target);
            var _9 = tt.closest("div.tree-node");
            if (!_9.length) {
                return;
            }
            if (tt.hasClass("tree-hit")) {
                _81(_5, _9[0]);
                return false;
            } else {
                if (tt.hasClass("tree-checkbox")) {
                    _34(_5, _9[0], !tt.hasClass("tree-checkbox1"));
                    return false;
                } else {
                    _da(_5, _9[0]);
                    _6.onClick.call(_5, _c(_5, _9[0]));
                }
            }
            e.stopPropagation();
        }).bind("dblclick",
        function(e) {
        	_itc_treedblclick = true;
            var _a = $(e.target).closest("div.tree-node");
            if (!_a.length) {
                return;
            }
            _da(_5, _a[0]);
            //_5是this指针 _a[0]是节点的target by murmur
            var hit = $(_a[0]).children(".tree-hit");
            if(hit.hasClass("tree-expanded")){
            	_7c(_5,_a[0]);
            }
            else{
            	_75(_5,_a[0]);
            }
            _6.onDblClick.call(_5, _c(_5, _a[0]));
            e.stopPropagation();
        }).bind("contextmenu",
        function(e) {
            var _b = $(e.target).closest("div.tree-node");
            if (!_b.length) {
                return;
            }
            _6.onContextMenu.call(_5, e, _c(_5, _b[0]));
            e.stopPropagation();
        });
    };
    function _d(_e) {
        var _f = $.data(_e, "tree").options;
        _f.dnd = false;
        var _10 = $(_e).find("div.tree-node");
        _10.draggable("disable");
        _10.css("cursor", "pointer");
    };
    function _11(_12) {
        var _13 = $.data(_12, "tree");
        var _14 = _13.options;
        var _15 = _13.tree;
        _13.disabledNodes = [];
        _14.dnd = true;
        _15.find("div.tree-node").draggable({
            disabled: false,
            revert: true,
            cursor: "pointer",
            proxy: function(_16) {
                var p = $("<div class=\"tree-node-proxy\"></div>").appendTo("body");
                p.html("<span class=\"tree-dnd-icon tree-dnd-no\">&nbsp;</span>" + $(_16).find(".tree-title").html());
                p.hide();
                return p;
            },
            deltaX: 15,
            deltaY: 15,
            onBeforeDrag: function(e) {
                if (_14.onBeforeDrag.call(_12, _c(_12, this)) == false) {
                    return false;
                }
                if ($(e.target).hasClass("tree-hit") || $(e.target).hasClass("tree-checkbox")) {
                    return false;
                }
                if (e.which != 1) {
                    return false;
                }
                $(this).next("ul").find("div.tree-node").droppable({
                    accept: "no-accept"
                });
                var _17 = $(this).find("span.tree-indent");
                if (_17.length) {
                    e.data.offsetWidth -= _17.length * _17.width();
                }
            },
            onStartDrag: function() {
                $(this).draggable("proxy").css({
                    left: -10000,
                    top: -10000
                });
                _14.onStartDrag.call(_12, _c(_12, this));
                var _18 = _c(_12, this);
                if (_18.id == undefined) {
                    _18.id = "easyui_tree_node_id_temp";
                    _56(_12, _18);
                }
                _13.draggingNodeId = _18.id;
            },
            onDrag: function(e) {
                var x1 = e.pageX,
                y1 = e.pageY,
                x2 = e.data.startX,
                y2 = e.data.startY;
                var d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                if (d > 3) {
                    $(this).draggable("proxy").show();
                }
                this.pageY = e.pageY;
            },
            onStopDrag: function() {
                $(this).next("ul").find("div.tree-node").droppable({
                    accept: "div.tree-node"
                });
                for (var i = 0; i < _13.disabledNodes.length; i++) {
                    $(_13.disabledNodes[i]).droppable("enable");
                }
                _13.disabledNodes = [];
                var _19 = _cd(_12, _13.draggingNodeId);
                if (_19 && _19.id == "easyui_tree_node_id_temp") {
                    _19.id = "";
                    _56(_12, _19);
                }
                _14.onStopDrag.call(_12, _19);
            }
        }).droppable({
            accept: "div.tree-node",
            onDragEnter: function(e, _1a) {
                if (_14.onDragEnter.call(_12, this, _1b(_1a)) == false) {
                    _1c(_1a, false);
                    $(this).removeClass("tree-node-append tree-node-top tree-node-bottom");
                    $(this).droppable("disable");
                    _13.disabledNodes.push(this);
                }
            },
            onDragOver: function(e, _1d) {
                if ($(this).droppable("options").disabled) {
                    return;
                }
                var _1e = _1d.pageY;
                var top = $(this).offset().top;
                var _1f = top + $(this).outerHeight();
                _1c(_1d, true);
                $(this).removeClass("tree-node-append tree-node-top tree-node-bottom");
                if (_1e > top + (_1f - top) / 2) {
                    if (_1f - _1e < 5) {
                        $(this).addClass("tree-node-bottom");
                    } else {
                        $(this).addClass("tree-node-append");
                    }
                } else {
                    if (_1e - top < 5) {
                        $(this).addClass("tree-node-top");
                    } else {
                        $(this).addClass("tree-node-append");
                    }
                }
                if (_14.onDragOver.call(_12, this, _1b(_1d)) == false) {
                    _1c(_1d, false);
                    $(this).removeClass("tree-node-append tree-node-top tree-node-bottom");
                    $(this).droppable("disable");
                    _13.disabledNodes.push(this);
                }
            },
            onDragLeave: function(e, _20) {
                _1c(_20, false);
                $(this).removeClass("tree-node-append tree-node-top tree-node-bottom");
                _14.onDragLeave.call(_12, this, _1b(_20));
            },
            onDrop: function(e, _21) {
                var _22 = this;
                var _23, _24;
                if ($(this).hasClass("tree-node-append")) {
                    _23 = _25;
                    _24 = "append";
                } else {
                    _23 = _26;
                    _24 = $(this).hasClass("tree-node-top") ? "top": "bottom";
                }
                if (_14.onBeforeDrop.call(_12, _22, _1b(_21), _24) == false) {
                    $(this).removeClass("tree-node-append tree-node-top tree-node-bottom");
                    return;
                }
                _23(_21, _22, _24);
                $(this).removeClass("tree-node-append tree-node-top tree-node-bottom");
            }
        });
        function _1b(_27, pop) {
            return $(_27).closest("ul.tree").tree(pop ? "pop": "getData", _27);
        };
        function _1c(_28, _29) {
            var _2a = $(_28).draggable("proxy").find("span.tree-dnd-icon");
            _2a.removeClass("tree-dnd-yes tree-dnd-no").addClass(_29 ? "tree-dnd-yes": "tree-dnd-no");
        };
        function _25(_2b, _2c) {
            if (_c(_12, _2c).state == "closed") {
                _75(_12, _2c,
                function() {
                    _2d();
                });
            } else {
                _2d();
            }
            function _2d() {
                var _2e = _1b(_2b, true);
                $(_12).tree("append", {
                    parent: _2c,
                    data: [_2e]
                });
                _14.onDrop.call(_12, _2c, _2e, "append");
            };
        };
        function _26(_2f, _30, _31) {
            var _32 = {};
            if (_31 == "top") {
                _32.before = _30;
            } else {
                _32.after = _30;
            }
            var _33 = _1b(_2f, true);
            _32.data = _33;
            $(_12).tree("insert", _32);
            _14.onDrop.call(_12, _30, _33, _31);
        };
    };
    function _34(_35, _36, _37) {
        var _38 = $.data(_35, "tree").options;
        if (!_38.checkbox) {
            return;
        }
        var _39 = _c(_35, _36);
        if (_38.onBeforeCheck.call(_35, _39, _37) == false) {
            return;
        }
        var _3a = $(_36);
        var ck = _3a.find(".tree-checkbox");
        ck.removeClass("tree-checkbox0 tree-checkbox1 tree-checkbox2");
        if (_37) {
            ck.addClass("tree-checkbox1");
        } else {
            ck.addClass("tree-checkbox0");
        }
        if (_38.cascadeCheck) {
            _3b(_3a);
            _3c(_3a);
        }
        _38.onCheck.call(_35, _39, _37);
        function _3c(_3d) {
            var _3e = _3d.next().find(".tree-checkbox");
            _3e.removeClass("tree-checkbox0 tree-checkbox1 tree-checkbox2");
            if (_3d.find(".tree-checkbox").hasClass("tree-checkbox1")) {
                _3e.addClass("tree-checkbox1");
            } else {
                _3e.addClass("tree-checkbox0");
            }
        };
        function _3b(_3f) {
            var _40 = _8c(_35, _3f[0]);
            if (_40) {
                var ck = $(_40.target).find(".tree-checkbox");
                ck.removeClass("tree-checkbox0 tree-checkbox1 tree-checkbox2");
                if (_41(_3f)) {
                    ck.addClass("tree-checkbox1");
                } else {
                    if (_42(_3f)) {
                        ck.addClass("tree-checkbox0");
                    } else {
                        ck.addClass("tree-checkbox2");
                    }
                }
                _3b($(_40.target));
            }
            function _41(n) {
                var ck = n.find(".tree-checkbox");
                if (ck.hasClass("tree-checkbox0") || ck.hasClass("tree-checkbox2")) {
                    return false;
                }
                var b = true;
                n.parent().siblings().each(function() {
                    if (!$(this).children("div.tree-node").children(".tree-checkbox").hasClass("tree-checkbox1")) {
                        b = false;
                    }
                });
                return b;
            };
            function _42(n) {
                var ck = n.find(".tree-checkbox");
                if (ck.hasClass("tree-checkbox1") || ck.hasClass("tree-checkbox2")) {
                    return false;
                }
                var b = true;
                n.parent().siblings().each(function() {
                    if (!$(this).children("div.tree-node").children(".tree-checkbox").hasClass("tree-checkbox0")) {
                        b = false;
                    }
                });
                return b;
            };
        };
    };
    function _43(_44, _45) {
        var _46 = $.data(_44, "tree").options;
        if (!_46.checkbox) {
            return;
        }
        var _47 = $(_45);
        if (_48(_44, _45)) {
            var ck = _47.find(".tree-checkbox");
            if (ck.length) {
                if (ck.hasClass("tree-checkbox1")) {
                    _34(_44, _45, true);
                } else {
                    _34(_44, _45, false);
                }
            } else {
                if (_46.onlyLeafCheck) {
                    $("<span class=\"tree-checkbox tree-checkbox0\"></span>").insertBefore(_47.find(".tree-title"));
                }
            }
        } else {
            var ck = _47.find(".tree-checkbox");
            if (_46.onlyLeafCheck) {
                ck.remove();
            } else {
                if (ck.hasClass("tree-checkbox1")) {
                    _34(_44, _45, true);
                } else {
                    if (ck.hasClass("tree-checkbox2")) {
                        var _49 = true;
                        var _4a = true;
                        var _4b = _4c(_44, _45);
                        for (var i = 0; i < _4b.length; i++) {
                            if (_4b[i].checked) {
                                _4a = false;
                            } else {
                                _49 = false;
                            }
                        }
                        if (_49) {
                            _34(_44, _45, true);
                        }
                        if (_4a) {
                            _34(_44, _45, false);
                        }
                    }
                }
            }
        }
    };
    function _4d(_4e, ul, _4f, _50) {
        var _51 = $.data(_4e, "tree");
        var _52 = _51.options;
        var _53 = $(ul).prevAll("div.tree-node:first");
        _4f = _52.loadFilter.call(_4e, _4f, _53[0]);
        var _54 = _55(_4e, "domId", _53.attr("id"));
        if (!_50) {
            _54 ? _54.children = _4f: _51.data = _4f;
            $(ul).empty();
        } else {
            if (_54) {
                _54.children ? _54.children = _54.children.concat(_4f) : _54.children = _4f;
            } else {
                _51.data = _51.data.concat(_4f);
            }
        }
        _52.view.render.call(_52.view, _4e, ul, _4f);
        if (_52.dnd) {
            _11(_4e);
        }
        if (_54) {
            _56(_4e, _54);
        }
        var _57 = [];
        var _58 = [];
        for (var i = 0; i < _4f.length; i++) {
            var _59 = _4f[i];
            if (!_59.checked) {
                _57.push(_59);
            }
        }
        _5a(_4f,
        function(_5b) {
            if (_5b.checked) {
                _58.push(_5b);
            }
        });
        var _5c = _52.onCheck;
        _52.onCheck = function() {};
        if (_57.length) {
            _34(_4e, $("#" + _57[0].domId)[0], false);
        }
        for (var i = 0; i < _58.length; i++) {
            _34(_4e, $("#" + _58[i].domId)[0], true);
        }
        _52.onCheck = _5c;
        setTimeout(function() {
            _5d(_4e, _4e);
        },
        0);
        _52.onLoadSuccess.call(_4e, _54, _4f);
    };
    function _5d(_5e, ul, _5f) {
        var _60 = $.data(_5e, "tree").options;
        if (_60.lines) {
            $(_5e).addClass("tree-lines");
        } else {
            $(_5e).removeClass("tree-lines");
            return;
        }
        if (!_5f) {
            _5f = true;
            $(_5e).find("span.tree-indent").removeClass("tree-line tree-join tree-joinbottom");
            $(_5e).find("div.tree-node").removeClass("tree-node-last tree-root-first tree-root-one");
            var _61 = $(_5e).tree("getRoots");
            if (_61.length > 1) {
                $(_61[0].target).addClass("tree-root-first");
            } else {
                if (_61.length == 1) {
                    $(_61[0].target).addClass("tree-root-one");
                }
            }
        }
        $(ul).children("li").each(function() {
            var _62 = $(this).children("div.tree-node");
            var ul = _62.next("ul");
            if (ul.length) {
                if ($(this).next().length) {
                    _63(_62);
                }
                _5d(_5e, ul, _5f);
            } else {
                _64(_62);
            }
        });
        var _65 = $(ul).children("li:last").children("div.tree-node").addClass("tree-node-last");
        _65.children("span.tree-join").removeClass("tree-join").addClass("tree-joinbottom");
        function _64(_66, _67) {
            var _68 = _66.find("span.tree-icon");
            _68.prev("span.tree-indent").addClass("tree-join");
        };
        function _63(_69) {
            var _6a = _69.find("span.tree-indent, span.tree-hit").length;
            _69.next().find("div.tree-node").each(function() {
                $(this).children("span:eq(" + (_6a - 1) + ")").addClass("tree-line");
            });
        };
    };
    function _6b(_6c, ul, _6d, _6e) {
        var _6f = $.data(_6c, "tree").options;
        _6d = _6d || {};
        var _70 = null;
        if (_6c != ul) {
            var _71 = $(ul).prev();
            _70 = _c(_6c, _71[0]);
        }
        if (_6f.onBeforeLoad.call(_6c, _70, _6d) == false) {
            return;
        }
        var _72 = $(ul).prev().children("span.tree-folder");
        _72.addClass("tree-loading");
        var _73 = _6f.loader.call(_6c, _6d,
        function(_74) {
            _72.removeClass("tree-loading");
            _4d(_6c, ul, _74);
            if (_6e) {
                _6e();
            }
        },
        function() {
            _72.removeClass("tree-loading");
            _6f.onLoadError.apply(_6c, arguments);
            if (_6e) {
                _6e();
            }
        });
        if (_73 == false) {
            _72.removeClass("tree-loading");
        }
    };
    function _75(_76, _77, _78) {
        var _79 = $.data(_76, "tree").options;
        var hit = $(_77).children("span.tree-hit");
        if (hit.length == 0) {
            return;
        }
        if (hit.hasClass("tree-expanded")) {
            return;
        }
        var _7a = _c(_76, _77);
        if (_79.onBeforeExpand.call(_76, _7a) == false) {
            return;
        }
        hit.removeClass("tree-collapsed tree-collapsed-hover").addClass("tree-expanded");
        hit.next().addClass("tree-folder-open");
        var ul = $(_77).next();
        if (ul.length) {
            if (_79.animate) {
                ul.slideDown("normal",
                function() {
                    _7a.state = "open";
                    _79.onExpand.call(_76, _7a);
                    if (_78) {
                        _78();
                    }
                });
            } else {
                ul.css("display", "block");
                _7a.state = "open";
                _79.onExpand.call(_76, _7a);
                if (_78) {
                    _78();
                }
            }
        } else {
            var _7b = $("<ul style=\"display:none\"></ul>").insertAfter(_77);
            _6b(_76, _7b[0], {
                id: _7a.id
            },
            function() {
                if (_7b.is(":empty")) {
                    _7b.remove();
                }
                if (_79.animate) {
                    _7b.slideDown("normal",
                    function() {
                        _7a.state = "open";
                        _79.onExpand.call(_76, _7a);
                        if (_78) {
                            _78();
                        }
                    });
                } else {
                    _7b.css("display", "block");
                    _7a.state = "open";
                    _79.onExpand.call(_76, _7a);
                    if (_78) {
                        _78();
                    }
                }
            });
        }
    };
    function _7c(_7d, _7e) {
        var _7f = $.data(_7d, "tree").options;
        var hit = $(_7e).children("span.tree-hit");
        if (hit.length == 0) {
            return;
        }
        if (hit.hasClass("tree-collapsed")) {
            return;
        }
        var _80 = _c(_7d, _7e);
        if (_7f.onBeforeCollapse.call(_7d, _80) == false) {
            return;
        }
        hit.removeClass("tree-expanded tree-expanded-hover").addClass("tree-collapsed");
        hit.next().removeClass("tree-folder-open");
        var ul = $(_7e).next();
        if (_7f.animate) {
            ul.slideUp("normal",
            function() {
                _80.state = "closed";
                _7f.onCollapse.call(_7d, _80);
            });
        } else {
            ul.css("display", "none");
            _80.state = "closed";
            _7f.onCollapse.call(_7d, _80);
        }
    };
    function _81(_82, _83) {
        var hit = $(_83).children("span.tree-hit");
        if (hit.length == 0) {
            return;
        }
        if (hit.hasClass("tree-expanded")) {
            _7c(_82, _83);
        } else {
            _75(_82, _83);
        }
    };
    function _84(_85, _86) {
        var _87 = _4c(_85, _86);
        if (_86) {
            _87.unshift(_c(_85, _86));
        }
        for (var i = 0; i < _87.length; i++) {
            _75(_85, _87[i].target);
        }
    };
    function _88(_89, _8a) {
        var _8b = [];
        var p = _8c(_89, _8a);
        while (p) {
            _8b.unshift(p);
            p = _8c(_89, p.target);
        }
        for (var i = 0; i < _8b.length; i++) {
            _75(_89, _8b[i].target);
        }
    };
    function _8d(_8e, _8f) {
        var c = $(_8e).parent();
        while (c[0].tagName != "BODY" && c.css("overflow-y") != "auto") {
            c = c.parent();
        }
        var n = $(_8f);
        var _90 = n.offset().top;
        if (c[0].tagName != "BODY") {
            var _91 = c.offset().top;
            if (_90 < _91) {
                c.scrollTop(c.scrollTop() + _90 - _91);
            } else {
                if (_90 + n.outerHeight() > _91 + c.outerHeight() - 18) {
                    c.scrollTop(c.scrollTop() + _90 + n.outerHeight() - _91 - c.outerHeight() + 18);
                }
            }
        } else {
            c.scrollTop(_90);
        }
    };
    function _92(_93, _94) {
        var _95 = _4c(_93, _94);
        if (_94) {
            _95.unshift(_c(_93, _94));
        }
        for (var i = 0; i < _95.length; i++) {
            _7c(_93, _95[i].target);
        }
    };
    function _96(_97, _98) {
        var _99 = $(_98.parent);
        var _9a = _98.data;
        if (!_9a) {
            return;
        }
        _9a = $.isArray(_9a) ? _9a: [_9a];
        if (!_9a.length) {
            return;
        }
        var ul;
        if (_99.length == 0) {
            ul = $(_97);
        } else {
            if (_48(_97, _99[0])) {
                var _9b = _99.find("span.tree-icon");
                _9b.removeClass("tree-file").addClass("tree-folder tree-folder-open");
                var hit = $("<span class=\"tree-hit tree-expanded\"></span>").insertBefore(_9b);
                if (hit.prev().length) {
                    hit.prev().remove();
                }
            }
            ul = _99.next();
            if (!ul.length) {
                ul = $("<ul></ul>").insertAfter(_99);
            }
        }
        _4d(_97, ul[0], _9a, true);
        _43(_97, ul.prev());
    };
    function _9c(_9d, _9e) {
        var ref = _9e.before || _9e.after;
        var _9f = _8c(_9d, ref);
        var _a0 = _9e.data;
        if (!_a0) {
            return;
        }
        _a0 = $.isArray(_a0) ? _a0: [_a0];
        if (!_a0.length) {
            return;
        }
        _96(_9d, {
            parent: (_9f ? _9f.target: null),
            data: _a0
        });
        var _a1 = _9f ? _9f.children: $(_9d).tree("getRoots");
        for (var i = 0; i < _a1.length; i++) {
            if (_a1[i].domId == $(ref).attr("id")) {
                for (var j = _a0.length - 1; j >= 0; j--) {
                    _a1.splice((_9e.before ? i: (i + 1)), 0, _a0[j]);
                }
                _a1.splice(_a1.length - _a0.length, _a0.length);
                break;
            }
        }
        var li = $();
        for (var i = 0; i < _a0.length; i++) {
            li = li.add($("#" + _a0[i].domId).parent());
        }
        if (_9e.before) {
            li.insertBefore($(ref).parent());
        } else {
            li.insertAfter($(ref).parent());
        }
    };
    function _a2(_a3, _a4) {
        var _a5 = del(_a4);
        $(_a4).parent().remove();
        if (_a5) {
            if (!_a5.children || !_a5.children.length) {
                var _a6 = $(_a5.target);
                _a6.find(".tree-icon").removeClass("tree-folder").addClass("tree-file");
                _a6.find(".tree-hit").remove();
                $("<span class=\"tree-indent\"></span>").prependTo(_a6);
                _a6.next().remove();
            }
            _56(_a3, _a5);
            _43(_a3, _a5.target);
        }
        _5d(_a3, _a3);
        function del(_a7) {
            var id = $(_a7).attr("id");
            var _a8 = _8c(_a3, _a7);
            var cc = _a8 ? _a8.children: $.data(_a3, "tree").data;
            for (var i = 0; i < cc.length; i++) {
                if (cc[i].domId == id) {
                    cc.splice(i, 1);
                    break;
                }
            }
            return _a8;
        };
    };
    function _56(_a9, _aa) {
        var _ab = $.data(_a9, "tree").options;
        var _ac = $(_aa.target);
        var _ad = _c(_a9, _aa.target);
        var _ae = _ad.checked;
        if (_ad.iconCls) {
            _ac.find(".tree-icon").removeClass(_ad.iconCls);
        }
        $.extend(_ad, _aa);
        var tmp = $.extend({}, _ad);
        tmp.text = FW.specialchars(tmp.text);
        _ac.find(".tree-title").html(_ab.formatter.call(_a9, tmp));
        if (_ad.iconCls) {
            _ac.find(".tree-icon").addClass(_ad.iconCls);
        }
        if (_ae != _ad.checked) {
            _34(_a9, _aa.target, _ad.checked);
        }
    };
    function _af(_b0) {
        var _b1 = _b2(_b0);
        return _b1.length ? _b1[0] : null;
    };
    function _b2(_b3) {
        var _b4 = $.data(_b3, "tree").data;
        for (var i = 0; i < _b4.length; i++) {
            _b5(_b4[i]);
        }
        return _b4;
    };
    function _4c(_b6, _b7) {
        var _b8 = [];
        var n = _c(_b6, _b7);
        var _b9 = n ? n.children: $.data(_b6, "tree").data;
        _5a(_b9,
        function(_ba) {
            _b8.push(_b5(_ba));
        });
        return _b8;
    };
    function _8c(_bb, _bc) {
        var p = $(_bc).closest("ul").prevAll("div.tree-node:first");
        return _c(_bb, p[0]);
    };
    function _bd(_be, _bf) {
        _bf = _bf || "checked";
        if (!$.isArray(_bf)) {
            _bf = [_bf];
        }
        var _c0 = [];
        for (var i = 0; i < _bf.length; i++) {
            var s = _bf[i];
            if (s == "checked") {
                _c0.push("span.tree-checkbox1");
            } else {
                if (s == "unchecked") {
                    _c0.push("span.tree-checkbox0");
                } else {
                    if (s == "indeterminate") {
                        _c0.push("span.tree-checkbox2");
                    }
                }
            }
        }
        var _c1 = [];
        $(_be).find(_c0.join(",")).each(function() {
            var _c2 = $(this).parent();
            _c1.push(_c(_be, _c2[0]));
        });
        return _c1;
    };
    function _c3(_c4) {
        var _c5 = $(_c4).find("div.tree-node-selected");
        return _c5.length ? _c(_c4, _c5[0]) : null;
    };
    function _c6(_c7, _c8) {
        var _c9 = _c(_c7, _c8);
        if (_c9 && _c9.children) {
            _5a(_c9.children,
            function(_ca) {
                _b5(_ca);
            });
        }
        return _c9;
    };
    function _c(_cb, _cc) {
        return _55(_cb, "domId", $(_cc).attr("id"));
    };
    function _cd(_ce, id) {
        return _55(_ce, "id", id);
    };
    function _55(_cf, _d0, _d1) {
        var _d2 = $.data(_cf, "tree").data;
        var _d3 = null;
        _5a(_d2,
        function(_d4) {
            if (_d4[_d0] == _d1) {
                _d3 = _b5(_d4);
                return false;
            }
        });
        return _d3;
    };
    function _b5(_d5) {
        var d = $("#" + _d5.domId);
        _d5.target = d[0];
        _d5.checked = d.find(".tree-checkbox").hasClass("tree-checkbox1");
        return _d5;
    };
    function _5a(_d6, _d7) {
        var _d8 = [];
        for (var i = 0; i < _d6.length; i++) {
            _d8.push(_d6[i]);
        }
        while (_d8.length) {
            var _d9 = _d8.shift();
            if (_d7(_d9) == false) {
                return;
            }
            if (_d9.children) {
                for (var i = _d9.children.length - 1; i >= 0; i--) {
                    _d8.unshift(_d9.children[i]);
                }
            }
        }
    };
    function _da(_db, _dc) {
        var _dd = $.data(_db, "tree").options;
        var _de = _c(_db, _dc);
        
        //防止双击事件触发两次选择节点 by murmur
        var ts = new Date().getTime();
        var id = _de.id;        
        if(_itc_tlstclktime && ts-_itc_tlstclktime<300){
        	if(id == _itc_tlstclknode){
        		return;
        	}
        }
        _itc_tlstclknode = id;
        _itc_tlstclktime = ts;

        if (_dd.onBeforeSelect.call(_db, _de) == false) {
            return;
        }
        $(_db).find("div.tree-node-selected").removeClass("tree-node-selected");
        $(_dc).addClass("tree-node-selected");
        setTimeout(function(){
        	if(!_itc_treedblclick){
        		_dd.onSelect.call(_db, _de);
        	}
        	_itc_treedblclick = false;
        },300);
    };
    function _48(_df, _e0) {
        return $(_e0).children("span.tree-hit").length == 0;
    };
    function _e1(_e2, _e3) {
        var _e4 = $.data(_e2, "tree").options;
        var _e5 = _c(_e2, _e3);
        if (_e4.onBeforeEdit.call(_e2, _e5) == false) {
            return;
        }
        $(_e3).css("position", "relative");
        var nt = $(_e3).find(".tree-title");
        var _e6 = nt.outerWidth();
        nt.empty();
        var _e7 = $("<input class=\"tree-editor\">").appendTo(nt);
        _e7.val(_e5.text).focus();
        _e7.width(_e6 + 20);
        _e7.height(document.compatMode == "CSS1Compat" ? (18 - (_e7.outerHeight() - _e7.height())) : 18);
        _e7.bind("click",
        function(e) {
            return false;
        }).bind("mousedown",
        function(e) {
            e.stopPropagation();
        }).bind("mousemove",
        function(e) {
            e.stopPropagation();
        }).bind("keydown",
        function(e) {
            if (e.keyCode == 13) {
                _e8(_e2, _e3);
                return false;
            } else {
                if (e.keyCode == 27) {
                    _ee(_e2, _e3);
                    return false;
                }
            }
        }).bind("blur",
        function(e) {
            e.stopPropagation();
            _e8(_e2, _e3);
        });
    };
    function _e8(_e9, _ea) {
        var _eb = $.data(_e9, "tree").options;
        $(_ea).css("position", "");
        var _ec = $(_ea).find("input.tree-editor");
        var val = _ec.val();
        _ec.remove();
        var _ed = _c(_e9, _ea);
        _ed.text = val;
        _56(_e9, _ed);
        _eb.onAfterEdit.call(_e9, _ed);
    };
    function _ee(_ef, _f0) {
        var _f1 = $.data(_ef, "tree").options;
        $(_f0).css("position", "");
        $(_f0).find("input.tree-editor").remove();
        var _f2 = _c(_ef, _f0);
        _56(_ef, _f2);
        _f1.onCancelEdit.call(_ef, _f2);
    };
    $.fn.tree = function(_f3, _f4) {
        if (typeof _f3 == "string") {
            return $.fn.tree.methods[_f3](this, _f4);
        }
        var _f3 = _f3 || {};
        return this.each(function() {
            var _f5 = $.data(this, "tree");
            var _f6;
            if (_f5) {
                _f6 = $.extend(_f5.options, _f3);
                _f5.options = _f6;
            } else {
                _f6 = $.extend({},
                $.fn.tree.defaults, $.fn.tree.parseOptions(this), _f3);
                $.data(this, "tree", {
                    options: _f6,
                    tree: _1(this),
                    data: []
                });
                var _f7 = $.fn.tree.parseData(this);
                if (_f7.length) {
                    _4d(this, this, _f7);
                }
            }
            _4(this);
            if (_f6.data) {
                _4d(this, this, $.extend(true, [], _f6.data));
            }
            _6b(this, this);
        });
    };
    $.fn.tree.methods = {
        options: function(jq) {
            return $.data(jq[0], "tree").options;
        },
        loadData: function(jq, _f8) {
            return jq.each(function() {
                _4d(this, this, _f8);
            });
        },
        getNode: function(jq, _f9) {
            return _c(jq[0], _f9);
        },
        getData: function(jq, _fa) {
            return _c6(jq[0], _fa);
        },
        reload: function(jq, _fb) {
            return jq.each(function() {
                if (_fb) {
                    var _fc = $(_fb);
                    var hit = _fc.children("span.tree-hit");
                    hit.removeClass("tree-expanded tree-expanded-hover").addClass("tree-collapsed");
                    _fc.next().remove();
                    _75(this, _fb);
                } else {
                    $(this).empty();
                    _6b(this, this);
                }
            });
        },
        getRoot: function(jq) {
            return _af(jq[0]);
        },
        getRoots: function(jq) {
            return _b2(jq[0]);
        },
        getParent: function(jq, _fd) {
            return _8c(jq[0], _fd);
        },
        getChildren: function(jq, _fe) {
            return _4c(jq[0], _fe);
        },
        getChecked: function(jq, _ff) {
            return _bd(jq[0], _ff);
        },
        getSelected: function(jq) {
            return _c3(jq[0]);
        },
        isLeaf: function(jq, _100) {
            return _48(jq[0], _100);
        },
        find: function(jq, id) {
            return _cd(jq[0], id);
        },
        select: function(jq, _101) {
            return jq.each(function() {
                _da(this, _101);
            });
        },
        check: function(jq, _102) {
            return jq.each(function() {
                _34(this, _102, true);
            });
        },
        uncheck: function(jq, _103) {
            return jq.each(function() {
                _34(this, _103, false);
            });
        },
        collapse: function(jq, _104) {
            return jq.each(function() {
                _7c(this, _104);
            });
        },
        expand: function(jq, _105) {
            return jq.each(function() {
                _75(this, _105);
            });
        },
        collapseAll: function(jq, _106) {
            return jq.each(function() {
                _92(this, _106);
            });
        },
        expandAll: function(jq, _107) {
            return jq.each(function() {
                _84(this, _107);
            });
        },
        expandTo: function(jq, _108) {
            return jq.each(function() {
                _88(this, _108);
            });
        },
        scrollTo: function(jq, _109) {
            return jq.each(function() {
                _8d(this, _109);
            });
        },
        toggle: function(jq, _10a) {
            return jq.each(function() {
                _81(this, _10a);
            });
        },
        append: function(jq, _10b) {
            return jq.each(function() {
                _96(this, _10b);
            });
        },
        insert: function(jq, _10c) {
            return jq.each(function() {
                _9c(this, _10c);
            });
        },
        remove: function(jq, _10d) {
            return jq.each(function() {
                _a2(this, _10d);
            });
        },
        pop: function(jq, _10e) {
            var node = jq.tree("getData", _10e);
            jq.tree("remove", _10e);
            return node;
        },
        update: function(jq, _10f) {
            return jq.each(function() {
                _56(this, _10f);
            });
        },
        enableDnd: function(jq) {
            return jq.each(function() {
                _11(this);
            });
        },
        disableDnd: function(jq) {
            return jq.each(function() {
                _d(this);
            });
        },
        beginEdit: function(jq, _110) {
            return jq.each(function() {
                _e1(this, _110);
            });
        },
        endEdit: function(jq, _111) {
            return jq.each(function() {
                _e8(this, _111);
            });
        },
        cancelEdit: function(jq, _112) {
            return jq.each(function() {
                _ee(this, _112);
            });
        }
    };
    $.fn.tree.parseOptions = function(_113) {
        var t = $(_113);
        return $.extend({},
        $.parser.parseOptions(_113, ["url", "method", {
            checkbox: "boolean",
            cascadeCheck: "boolean",
            onlyLeafCheck: "boolean"
        },
        {
            animate: "boolean",
            lines: "boolean",
            dnd: "boolean"
        }]));
    };
    $.fn.tree.parseData = function(_114) {
        var data = [];
        _115(data, $(_114));
        return data;
        function _115(aa, tree) {
            tree.children("li").each(function() {
                var node = $(this);
                var item = $.extend({},
                $.parser.parseOptions(this, ["id", "iconCls", "state"]), {
                    checked: (node.attr("checked") ? true: undefined)
                });
                item.text = node.children("span").html();
                if (!item.text) {
                    item.text = node.html();
                }
                var _116 = node.children("ul");
                if (_116.length) {
                    item.children = [];
                    _115(item.children, _116);
                }
                aa.push(item);
            });
        };
    };
    var _117 = 1;
    var _118 = {
        render: function(_119, ul, data) {
            var opts = $.data(_119, "tree").options;
            var _11a = $(ul).prev("div.tree-node").find("span.tree-indent, span.tree-hit").length;
            var cc = _11b(_11a, data);
            $(ul).append(cc.join(""));
            function _11b(_11c, _11d) {
                var cc = [];
                for (var i = 0; i < _11d.length; i++) {
                    var item = _11d[i];
                    if (item.state != "open" && item.state != "closed") {
                        item.state = "open";
                    }
                    item.domId = "_easyui_tree_" + _117++;
                    cc.push("<li>");
                    //初始选择支持 by murmur
                    var selStr = item.selected?" tree-node-selected":"";
                    cc.push("<div id=\"" + item.domId + "\" class=\"tree-node" + selStr + "\">");
                    for (var j = 0; j < _11c; j++) {
                        cc.push("<span class=\"tree-indent\"></span>");
                    }
                    var _11e = false;
                    if (item.state == "closed") {
                        cc.push("<span class=\"tree-hit tree-collapsed\"></span>");
                        cc.push("<span class=\"tree-icon tree-folder " + (item.iconCls ? item.iconCls: "") + "\"></span>");
                    } else {
                        if (item.children && item.children.length) {
                            cc.push("<span class=\"tree-hit tree-expanded\"></span>");
                            cc.push("<span class=\"tree-icon tree-folder tree-folder-open " + (item.iconCls ? item.iconCls: "") + "\"></span>");
                        } else {
                            cc.push("<span class=\"tree-indent\"></span>");
                            cc.push("<span class=\"tree-icon tree-file " + (item.iconCls ? item.iconCls: "") + "\"></span>");
                            _11e = true;
                        }
                    }
                    if (opts.checkbox) {
                        if ((!opts.onlyLeafCheck) || _11e) {
                            cc.push("<span class=\"tree-checkbox tree-checkbox0\"></span>");
                        }
                    }
                    var _item = $.extend({}, item);
                    _item.text = FW.specialchars(_item.text);
                    cc.push("<span class=\"tree-title\">" + opts.formatter.call(_119, _item) + "</span>");
                    cc.push("</div>");
                    if (item.children && item.children.length) {
                        var tmp = _11b(_11c + 1, item.children);
                        cc.push("<ul style=\"display:" + (item.state == "closed" ? "none": "block") + "\">");
                        cc = cc.concat(tmp);
                        cc.push("</ul>");
                    }
                    cc.push("</li>");
                }
                return cc;
            };
        }
    };
    $.fn.tree.defaults = {
        url: null,
        method: "post",
        animate: false,
        checkbox: false,
        cascadeCheck: true,
        onlyLeafCheck: false,
        lines: false,
        dnd: false,
        data: null,
        formatter: function(node) {
            return node.text;
        },
        loader: function(_11f, _120, _121) {
            var opts = $(this).tree("options");
            if (!opts.url) {
                return false;
            }
            $.ajax({
                type: opts.method,
                url: opts.url,
                data: _11f,
                dataType: "json",
                success: function(data) {
                    _120(data);
                },
                error: function() {
                    _121.apply(this, arguments);
                }
            });
        },
        loadFilter: function(data, _122) {
            return data;
        },
        view: _118,
        onBeforeLoad: function(node, _123) {},
        onLoadSuccess: function(node, data) {},
        onLoadError: function() {},
        onClick: function(node) {},
        onDblClick: function(node) {},
        onBeforeExpand: function(node) {},
        onExpand: function(node) {},
        onBeforeCollapse: function(node) {},
        onCollapse: function(node) {},
        onBeforeCheck: function(node, _124) {},
        onCheck: function(node, _125) {},
        onBeforeSelect: function(node) {},
        onSelect: function(node) {},
        onContextMenu: function(e, node) {},
        onBeforeDrag: function(node) {},
        onStartDrag: function(node) {},
        onStopDrag: function(node) {},
        onDragEnter: function(_126, _127) {},
        onDragOver: function(_128, _129) {},
        onDragLeave: function(_12a, _12b) {},
        onBeforeDrop: function(_12c, _12d, _12e) {},
        onDrop: function(_12f, _130, _131) {},
        onBeforeEdit: function(node) {},
        onAfterEdit: function(node) {},
        onCancelEdit: function(node) {},
        onMouseEnter : function(e,node) {},
        onMouseOut : function(e,node) {}
    };
})(jQuery);

$.extend($.fn.tree.methods, {
    unselect: function(jq, target) {
        return jq.each(function() {
            var opts = $(this).tree('options');
            $(target).removeClass('tree-node-selected');
            if (opts.onUnselect) {
                opts.onUnselect.call(this, $(this).tree('getNode', target));
            }
        });
    }
});
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.draggable.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
function _1(e){
var _2=$.data(e.data.target,"draggable");
var _3=_2.options;
var _4=_2.proxy;
var _5=e.data;
var _6=_5.startLeft+e.pageX-_5.startX;
var _7=_5.startTop+e.pageY-_5.startY;
if(_4){
if(_4.parent()[0]==document.body){
if(_3.deltaX!=null&&_3.deltaX!=undefined){
_6=e.pageX+_3.deltaX;
}else{
_6=e.pageX-e.data.offsetWidth;
}
if(_3.deltaY!=null&&_3.deltaY!=undefined){
_7=e.pageY+_3.deltaY;
}else{
_7=e.pageY-e.data.offsetHeight;
}
}else{
if(_3.deltaX!=null&&_3.deltaX!=undefined){
_6+=e.data.offsetWidth+_3.deltaX;
}
if(_3.deltaY!=null&&_3.deltaY!=undefined){
_7+=e.data.offsetHeight+_3.deltaY;
}
}
}
if(e.data.parent!=document.body){
_6+=$(e.data.parent).scrollLeft();
_7+=$(e.data.parent).scrollTop();
}
if(_3.axis=="h"){
_5.left=_6;
}else{
if(_3.axis=="v"){
_5.top=_7;
}else{
_5.left=_6;
_5.top=_7;
}
}
};
function _8(e){
var _9=$.data(e.data.target,"draggable");
var _a=_9.options;
var _b=_9.proxy;
if(!_b){
_b=$(e.data.target);
}
_b.css({left:e.data.left,top:e.data.top});
$("body").css("cursor",_a.cursor);
};
function _c(e){
$.fn.draggable.isDragging=true;
var _d=$.data(e.data.target,"draggable");
var _e=_d.options;
var _f=$(".droppable").filter(function(){
return e.data.target!=this;
}).filter(function(){
var _10=$.data(this,"droppable").options.accept;
if(_10){
return $(_10).filter(function(){
return this==e.data.target;
}).length>0;
}else{
return true;
}
});
_d.droppables=_f;
var _11=_d.proxy;
if(!_11){
if(_e.proxy){
if(_e.proxy=="clone"){
_11=$(e.data.target).clone().insertAfter(e.data.target);
}else{
_11=_e.proxy.call(e.data.target,e.data.target);
}
_d.proxy=_11;
}else{
_11=$(e.data.target);
}
}
_11.css("position","absolute");
_1(e);
_8(e);
_e.onStartDrag.call(e.data.target,e);
return false;
};
function _12(e){
var _13=$.data(e.data.target,"draggable");
_1(e);
if(_13.options.onDrag.call(e.data.target,e)!=false){
_8(e);
}
var _14=e.data.target;
_13.droppables.each(function(){
var _15=$(this);
if(_15.droppable("options").disabled){
return;
}
var p2=_15.offset();
if(e.pageX>p2.left&&e.pageX<p2.left+_15.outerWidth()&&e.pageY>p2.top&&e.pageY<p2.top+_15.outerHeight()){
if(!this.entered){
$(this).trigger("_dragenter",[_14]);
this.entered=true;
}
$(this).trigger("_dragover",[_14]);
}else{
if(this.entered){
$(this).trigger("_dragleave",[_14]);
this.entered=false;
}
}
});
return false;
};
function _16(e){
$.fn.draggable.isDragging=false;
_12(e);
var _17=$.data(e.data.target,"draggable");
var _18=_17.proxy;
var _19=_17.options;
if(_19.revert){
if(_1a()==true){
$(e.data.target).css({position:e.data.startPosition,left:e.data.startLeft,top:e.data.startTop});
}else{
if(_18){
var _1b,top;
if(_18.parent()[0]==document.body){
_1b=e.data.startX-e.data.offsetWidth;
top=e.data.startY-e.data.offsetHeight;
}else{
_1b=e.data.startLeft;
top=e.data.startTop;
}
_18.animate({left:_1b,top:top},function(){
_1c();
});
}else{
$(e.data.target).animate({left:e.data.startLeft,top:e.data.startTop},function(){
$(e.data.target).css("position",e.data.startPosition);
});
}
}
}else{
$(e.data.target).css({position:"absolute",left:e.data.left,top:e.data.top});
_1a();
}
_19.onStopDrag.call(e.data.target,e);
$(document).unbind(".draggable");
setTimeout(function(){
$("body").css("cursor","");
},100);
function _1c(){
if(_18){
_18.remove();
}
_17.proxy=null;
};
function _1a(){
var _1d=false;
_17.droppables.each(function(){
var _1e=$(this);
if(_1e.droppable("options").disabled){
return;
}
var p2=_1e.offset();
if(e.pageX>p2.left&&e.pageX<p2.left+_1e.outerWidth()&&e.pageY>p2.top&&e.pageY<p2.top+_1e.outerHeight()){
if(_19.revert){
$(e.data.target).css({position:e.data.startPosition,left:e.data.startLeft,top:e.data.startTop});
}
$(this).trigger("_drop",[e.data.target]);
_1c();
_1d=true;
this.entered=false;
return false;
}
});
if(!_1d&&!_19.revert){
_1c();
}
return _1d;
};
return false;
};
$.fn.draggable=function(_1f,_20){
if(typeof _1f=="string"){
return $.fn.draggable.methods[_1f](this,_20);
}
return this.each(function(){
var _21;
var _22=$.data(this,"draggable");
if(_22){
_22.handle.unbind(".draggable");
_21=$.extend(_22.options,_1f);
}else{
_21=$.extend({},$.fn.draggable.defaults,$.fn.draggable.parseOptions(this),_1f||{});
}
var _23=_21.handle?(typeof _21.handle=="string"?$(_21.handle,this):_21.handle):$(this);
$.data(this,"draggable",{options:_21,handle:_23});
if(_21.disabled){
$(this).css("cursor","");
return;
}
_23.unbind(".draggable").bind("mousemove.draggable",{target:this},function(e){
if($.fn.draggable.isDragging){
return;
}
var _24=$.data(e.data.target,"draggable").options;
if(_25(e)){
$(this).css("cursor",_24.cursor);
}else{
$(this).css("cursor","");
}
}).bind("mouseleave.draggable",{target:this},function(e){
$(this).css("cursor","");
}).bind("mousedown.draggable",{target:this},function(e){
if(_25(e)==false){
return;
}
$(this).css("cursor","");
var _26=$(e.data.target).position();
var _27=$(e.data.target).offset();
var _28={startPosition:$(e.data.target).css("position"),startLeft:_26.left,startTop:_26.top,left:_26.left,top:_26.top,startX:e.pageX,startY:e.pageY,offsetWidth:(e.pageX-_27.left),offsetHeight:(e.pageY-_27.top),target:e.data.target,parent:$(e.data.target).parent()[0]};
$.extend(e.data,_28);
var _29=$.data(e.data.target,"draggable").options;
if(_29.onBeforeDrag.call(e.data.target,e)==false){
return;
}
$(document).bind("mousedown.draggable",e.data,_c);
$(document).bind("mousemove.draggable",e.data,_12);
$(document).bind("mouseup.draggable",e.data,_16);
});
function _25(e){
var _2a=$.data(e.data.target,"draggable");
var _2b=_2a.handle;
var _2c=$(_2b).offset();
var _2d=$(_2b).outerWidth();
var _2e=$(_2b).outerHeight();
var t=e.pageY-_2c.top;
var r=_2c.left+_2d-e.pageX;
var b=_2c.top+_2e-e.pageY;
var l=e.pageX-_2c.left;
return Math.min(t,r,b,l)>_2a.options.edge;
};
});
};
$.fn.draggable.methods={options:function(jq){
return $.data(jq[0],"draggable").options;
},proxy:function(jq){
return $.data(jq[0],"draggable").proxy;
},enable:function(jq){
return jq.each(function(){
$(this).draggable({disabled:false});
});
},disable:function(jq){
return jq.each(function(){
$(this).draggable({disabled:true});
});
}};
$.fn.draggable.parseOptions=function(_2f){
var t=$(_2f);
return $.extend({},$.parser.parseOptions(_2f,["cursor","handle","axis",{"revert":"boolean","deltaX":"number","deltaY":"number","edge":"number"}]),{disabled:(t.attr("disabled")?true:undefined)});
};
$.fn.draggable.defaults={proxy:null,revert:false,cursor:"move",deltaX:null,deltaY:null,handle:null,disabled:false,edge:0,axis:null,onBeforeDrag:function(e){
},onStartDrag:function(e){
},onDrag:function(e){
},onStopDrag:function(e){
}};
$.fn.draggable.isDragging=false;
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.window.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
function _1(_2,_3){
var _4=$.data(_2,"window").options;
if(_3){
$.extend(_4,_3);
}
$(_2).panel("resize",_4);
};
function _5(_6,_7){
var _8=$.data(_6,"window");
if(_7){
if(_7.left!=null){
_8.options.left=_7.left;
}
if(_7.top!=null){
_8.options.top=_7.top;
}
}
$(_6).panel("move",_8.options);
if(_8.shadow){
_8.shadow.css({left:_8.options.left,top:_8.options.top});
}
};
function _9(_a,_b){
var _c=$.data(_a,"window");
var _d=_c.options;
var _e=_d.width;
if(isNaN(_e)){
_e=_c.window._outerWidth();
}
if(_d.inline){
var _f=_c.window.parent();
_d.left=(_f.width()-_e)/2+_f.scrollLeft();
}else{
_d.left=($(window)._outerWidth()-_e)/2+$(document).scrollLeft();
}
if(_b){
_5(_a);
}
};
function _10(_11,_12){
var _13=$.data(_11,"window");
var _14=_13.options;
var _15=_14.height;
if(isNaN(_15)){
_15=_13.window._outerHeight();
}
if(_14.inline){
var _16=_13.window.parent();
_14.top=(_16.height()-_15)/2+_16.scrollTop();
}else{
_14.top=($(window)._outerHeight()-_15)/2+$(document).scrollTop();
}
if(_12){
_5(_11);
}
};
function _17(_18){
var _19=$.data(_18,"window");
var win=$(_18).panel($.extend({},_19.options,{border:false,doSize:true,closed:true,cls:"window",headerCls:"window-header",bodyCls:"window-body "+(_19.options.noheader?"window-body-noheader":""),onBeforeDestroy:function(){
if(_19.options.onBeforeDestroy.call(_18)==false){
return false;
}
if(_19.shadow){
_19.shadow.remove();
}
if(_19.mask){
_19.mask.remove();
}
},onClose:function(){
if(_19.shadow){
_19.shadow.hide();
}
if(_19.mask){
_19.mask.hide();
}
_19.options.onClose.call(_18);
},onOpen:function(){
if(_19.mask){
_19.mask.css({display:"block",zIndex:$.fn.window.defaults.zIndex++});
}
if(_19.shadow){
_19.shadow.css({display:"block",zIndex:$.fn.window.defaults.zIndex++,left:_19.options.left,top:_19.options.top,width:_19.window._outerWidth(),height:_19.window._outerHeight()});
}
_19.window.css("z-index",$.fn.window.defaults.zIndex++);
_19.options.onOpen.call(_18);
},onResize:function(_1a,_1b){
var _1c=$(this).panel("options");
$.extend(_19.options,{width:_1c.width,height:_1c.height,left:_1c.left,top:_1c.top});
if(_19.shadow){
_19.shadow.css({left:_19.options.left,top:_19.options.top,width:_19.window._outerWidth(),height:_19.window._outerHeight()});
}
_19.options.onResize.call(_18,_1a,_1b);
},onMinimize:function(){
if(_19.shadow){
_19.shadow.hide();
}
if(_19.mask){
_19.mask.hide();
}
_19.options.onMinimize.call(_18);
},onBeforeCollapse:function(){
if(_19.options.onBeforeCollapse.call(_18)==false){
return false;
}
if(_19.shadow){
_19.shadow.hide();
}
},onExpand:function(){
if(_19.shadow){
_19.shadow.show();
}
_19.options.onExpand.call(_18);
}}));
_19.window=win.panel("panel");
if(_19.mask){
_19.mask.remove();
}
if(_19.options.modal==true){
_19.mask=$("<div class=\"window-mask\"></div>").insertAfter(_19.window);
_19.mask.css({width:(_19.options.inline?_19.mask.parent().width():_1d().width),height:(_19.options.inline?_19.mask.parent().height():_1d().height),display:"none"});
}
if(_19.shadow){
_19.shadow.remove();
}
if(_19.options.shadow==true){
_19.shadow=$("<div class=\"window-shadow\"></div>").insertAfter(_19.window);
_19.shadow.css({display:"none"});
}
if(_19.options.left==null){
_9(_18);
}
if(_19.options.top==null){
_10(_18);
}
_5(_18);
if(_19.options.closed==false){
win.window("open");
}
};
function _1e(_1f){
var _20=$.data(_1f,"window");
_20.window.draggable({handle:">div.panel-header>div.panel-title",disabled:_20.options.draggable==false,onStartDrag:function(e){
if(_20.mask){
_20.mask.css("z-index",$.fn.window.defaults.zIndex++);
}
if(_20.shadow){
_20.shadow.css("z-index",$.fn.window.defaults.zIndex++);
}
_20.window.css("z-index",$.fn.window.defaults.zIndex++);
if(!_20.proxy){
_20.proxy=$("<div class=\"window-proxy\"></div>").insertAfter(_20.window);
}
_20.proxy.css({display:"none",zIndex:$.fn.window.defaults.zIndex++,left:e.data.left,top:e.data.top});
_20.proxy._outerWidth(_20.window._outerWidth());
_20.proxy._outerHeight(_20.window._outerHeight());
setTimeout(function(){
if(_20.proxy){
_20.proxy.show();
}
},500);
},onDrag:function(e){
_20.proxy.css({display:"block",left:e.data.left,top:e.data.top});
return false;
},onStopDrag:function(e){
_20.options.left=e.data.left;
_20.options.top=e.data.top;
$(_1f).window("move");
_20.proxy.remove();
_20.proxy=null;
}});
_20.window.resizable({disabled:_20.options.resizable==false,onStartResize:function(e){
_20.pmask=$("<div class=\"window-proxy-mask\"></div>").insertAfter(_20.window);
_20.pmask.css({zIndex:$.fn.window.defaults.zIndex++,left:e.data.left,top:e.data.top,width:_20.window._outerWidth(),height:_20.window._outerHeight()});
if(!_20.proxy){
_20.proxy=$("<div class=\"window-proxy\"></div>").insertAfter(_20.window);
}
_20.proxy.css({zIndex:$.fn.window.defaults.zIndex++,left:e.data.left,top:e.data.top});
_20.proxy._outerWidth(e.data.width);
_20.proxy._outerHeight(e.data.height);
},onResize:function(e){
_20.proxy.css({left:e.data.left,top:e.data.top});
_20.proxy._outerWidth(e.data.width);
_20.proxy._outerHeight(e.data.height);
return false;
},onStopResize:function(e){
$.extend(_20.options,{left:e.data.left,top:e.data.top,width:e.data.width,height:e.data.height});
_1(_1f);
_20.pmask.remove();
_20.pmask=null;
_20.proxy.remove();
_20.proxy=null;
}});
};
function _1d(){
if(document.compatMode=="BackCompat"){
return {width:Math.max(document.body.scrollWidth,document.body.clientWidth),height:Math.max(document.body.scrollHeight,document.body.clientHeight)};
}else{
return {width:Math.max(document.documentElement.scrollWidth,document.documentElement.clientWidth),height:Math.max(document.documentElement.scrollHeight,document.documentElement.clientHeight)};
}
};
$(window).resize(function(){
$("body>div.window-mask").css({width:$(window)._outerWidth(),height:$(window)._outerHeight()});
setTimeout(function(){
$("body>div.window-mask").css({width:_1d().width,height:_1d().height});
},50);
});
$.fn.window=function(_21,_22){
if(typeof _21=="string"){
var _23=$.fn.window.methods[_21];
if(_23){
return _23(this,_22);
}else{
return this.panel(_21,_22);
}
}
_21=_21||{};
return this.each(function(){
var _24=$.data(this,"window");
if(_24){
$.extend(_24.options,_21);
}else{
_24=$.data(this,"window",{options:$.extend({},$.fn.window.defaults,$.fn.window.parseOptions(this),_21)});
if(!_24.options.inline){
document.body.appendChild(this);
}
}
_17(this);
_1e(this);
});
};
$.fn.window.methods={options:function(jq){
var _25=jq.panel("options");
var _26=$.data(jq[0],"window").options;
return $.extend(_26,{closed:_25.closed,collapsed:_25.collapsed,minimized:_25.minimized,maximized:_25.maximized});
},window:function(jq){
return $.data(jq[0],"window").window;
},resize:function(jq,_27){
return jq.each(function(){
_1(this,_27);
});
},move:function(jq,_28){
return jq.each(function(){
_5(this,_28);
});
},hcenter:function(jq){
return jq.each(function(){
_9(this,true);
});
},vcenter:function(jq){
return jq.each(function(){
_10(this,true);
});
},center:function(jq){
return jq.each(function(){
_9(this);
_10(this);
_5(this);
});
}};
$.fn.window.parseOptions=function(_29){
return $.extend({},$.fn.panel.parseOptions(_29),$.parser.parseOptions(_29,[{draggable:"boolean",resizable:"boolean",shadow:"boolean",modal:"boolean",inline:"boolean"}]));
};
$.fn.window.defaults=$.extend({},$.fn.panel.defaults,{zIndex:9000,draggable:true,resizable:true,shadow:true,modal:false,inline:false,title:"New Window",collapsible:true,minimizable:true,maximizable:true,closable:true,closed:false});
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.dialog.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($) {
    function _1(_2) {
        var cp = document.createElement("div");
        while (_2.firstChild) {
            cp.appendChild(_2.firstChild);
        }
        _2.appendChild(cp);
        var _3 = $(cp);
        _3.attr("style", $(_2).attr("style"));
        $(_2).removeAttr("style").css("overflow", "hidden");
        _3.panel({
            border: false,
            doSize: false,
            bodyCls: "dialog-content"
        });
        return _3;
    };
    function _4(_5) {
        var _6 = $.data(_5, "dialog").options;
        var _7 = $.data(_5, "dialog").contentPanel;
        if (_6.toolbar) {
            if ($.isArray(_6.toolbar)) {
                $(_5).find("div.dialog-toolbar").remove();
                var _8 = $("<div class=\"dialog-toolbar\"><table cellspacing=\"0\" cellpadding=\"0\"><tr></tr></table></div>").prependTo(_5);
                var tr = _8.find("tr");
                for (var i = 0; i < _6.toolbar.length; i++) {
                    var _9 = _6.toolbar[i];
                    if (_9 == "-") {
                        $("<td><div class=\"dialog-tool-separator\"></div></td>").appendTo(tr);
                    } else {
                        var td = $("<td></td>").appendTo(tr);
                        var _a = $("<a href=\"javascript:void(0)\"></a>").appendTo(td);
                        _a[0].onclick = eval(_9.handler ||
                        function() {});
                    }
                }
            } else {
                $(_6.toolbar).addClass("dialog-toolbar").prependTo(_5);
                $(_6.toolbar).show();
            }
        } else {
            $(_5).find("div.dialog-toolbar").remove();
        }
        if (_6.buttons) {
            if ($.isArray(_6.buttons)) {
                $(_5).find("div.dialog-button").remove();
                var _b = $("<div class=\"dialog-button\"></div>").appendTo(_5);
                for (var i = 0; i < _6.buttons.length; i++) {
                    var p = _6.buttons[i];
                    var _c = $("<a href=\"javascript:void(0)\"></a>").appendTo(_b);
                    if (p.handler) {
                        _c[0].onclick = p.handler;
                    }
                }
            } else {
                $(_6.buttons).addClass("dialog-button").appendTo(_5);
                $(_6.buttons).show();
            }
        } else {
            $(_5).find("div.dialog-button").remove();
        }
        var _d = _6.href;
        var _e = _6.content;
        _6.href = null;
        _6.content = null;
        _7.panel({
            closed: _6.closed,
            cache: _6.cache,
            href: _d,
            content: _e,
            onLoad: function() {
                if (_6.height == "auto") {
                    $(_5).window("resize");
                }
                _6.onLoad.apply(_5, arguments);
            }
        });
        $(_5).window($.extend({},
        _6, {
            onOpen: function() {
                if (_7.panel("options").closed) {
                    _7.panel("open");
                }
                if (_6.onOpen) {
                    _6.onOpen.call(_5);
                }
            },
            onResize: function(_f, _10) {
                var _11 = $(_5);
                _7.panel("panel").show();
                _7.panel("resize", {
                    width: _11.width(),
                    height: (_10 == "auto") ? "auto": _11.height() - _11.children("div.dialog-toolbar")._outerHeight() - _11.children("div.dialog-button")._outerHeight()
                });
                if (_6.onResize) {
                    _6.onResize.call(_5, _f, _10);
                }
            }
        }));
        _6.href = _d;
        _6.content = _e;
    };
    function _12(_13, _14) {
        var _15 = $.data(_13, "dialog").contentPanel;
        _15.panel("refresh", _14);
    };
    $.fn.dialog = function(_16, _17) {
        if (typeof _16 == "string") {
            var _18 = $.fn.dialog.methods[_16];
            if (_18) {
                return _18(this, _17);
            } else {
                return this.window(_16, _17);
            }
        }
        _16 = _16 || {};
        return this.each(function() {
            var _19 = $.data(this, "dialog");
            if (_19) {
                $.extend(_19.options, _16);
            } else {
                $.data(this, "dialog", {
                    options: $.extend({},
                    $.fn.dialog.defaults, $.fn.dialog.parseOptions(this), _16),
                    contentPanel: _1(this)
                });
            }
            _4(this);
        });
    };
    $.fn.dialog.methods = {
        options: function(jq) {
            var _1a = $.data(jq[0], "dialog").options;
            var _1b = jq.panel("options");
            $.extend(_1a, {
                closed: _1b.closed,
                collapsed: _1b.collapsed,
                minimized: _1b.minimized,
                maximized: _1b.maximized
            });
            var _1c = $.data(jq[0], "dialog").contentPanel;
            return _1a;
        },
        dialog: function(jq) {
            return jq.window("window");
        },
        refresh: function(jq, _1d) {
            return jq.each(function() {
                _12(this, _1d);
            });
        }
    };
    $.fn.dialog.parseOptions = function(_1e) {
        return $.extend({},
        $.fn.window.parseOptions(_1e), $.parser.parseOptions(_1e, ["toolbar", "buttons"]));
    };
    $.fn.dialog.defaults = $.extend({},
    $.fn.window.defaults, {
        title: "New Dialog",
        collapsible: false,
        minimizable: false,
        maximizable: false,
        resizable: false,
        toolbar: null,
        buttons: null
    });
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_foldable.js
*/
(function($){
	$.fn.extend({
		ITCUI_Foldable:function(action,opts){
			var title = "无标题栏目"
			var _this = $(this);
			var _t = this;

			_t.initFoldable = function(){
				opts = opts || {};
				opts.onExpand = opts.onExpand || function(){};
				opts.onFold = opts.onFold || function(){};
				opts.hideOnEmpty = opts.hideOnEmpty===false?true:false;
				_this.data("opts",opts);
				if(_this.attr("grouptitle")){
					title = _this.attr("grouptitle");
				}
				if(opts){
					if(opts["grouptitle"]){
						title = opts["grouptitle"];
					}
				}
				var fStr = "";
				if(_this.css("float")){
					fStr = "float:" + _this.css("float");
				}
				var prepHtml = '<div class="itcui_frm_grp_title" style="width:100%;' + fStr + '">';
				prepHtml += '<span class="itcui_frm_grp_title_txt">' + title + '</span>';
				prepHtml += '<span class="title-arrow'
				if(_this.css("display")!="none"){
					prepHtml += ' expand';
				}
				prepHtml += '"></span>';				
				prepHtml += '</div>';
				_this.before(prepHtml);
				_t.hideOnEmpty();
			}

			_t.hideOnEmpty = function(){
				var objects = _this.children();
				var canHide = true;
				for(var i=0;i<objects.length;i++){
					if($(objects[i]).css("display")!="none"){
						canHide = false;
						break;
					}
				}
				if(canHide){
					_t.hideAll();
				}
			};

			_t.hideAll = function(){
				_this.hide();
				_this.prev(".itcui_frm_grp_title").hide();
			};

			_t.showAll = function(){
				_this.show();
				_this.prev(".itcui_frm_grp_title").show();	
			};

			_t.fold = function(){
				var grpTitle = _this.prev(".itcui_frm_grp_title");
				_t.foldOrExpand(grpTitle,"fold");
			};

			_t.expand = function(){
				var grpTitle = _this.prev(".itcui_frm_grp_title");
				_t.foldOrExpand(grpTitle,"expand");
			};

			_t.toggle = function(){
				var grpTitle = _this.prev(".itcui_frm_grp_title");
				_t.foldOrExpand(grpTitle);
			}

			_t.foldOrExpand = function(grouptitle,action){
				var mainPart = $(grouptitle).next("div");
				var opts = mainPart.data("opts");
				if((mainPart.css('display')=="none" || action=="expand") && action!="fold"){
					grouptitle.children(".title-arrow").addClass("expand");
					mainPart.slideDown("normal",opts.onExpand);
				}
				else if(action!="expand"){
					mainPart.slideUp("normal",opts.onFold);	
					grouptitle.children(".title-arrow").removeClass("expand");
				}
			}

			_t.addEvents = function(){
				var grpTitle = _this.prev(".itcui_frm_grp_title");
				if(_this.attr("allowexpand")){		
					grpTitle.click(function(e){
						_t.foldOrExpand(grpTitle);
					});
				}else{
					grpTitle.addClass("no-expand");
				}
			}

			if(!action){
				_t.initFoldable();
				_t.addEvents();
			}
			else if(action=="show"){
				_t.showAll();
			}
			else if(action=="hide"){
				_t.hideAll();
			}
			else if(action=="fold"){
				_t.fold();
			}
			else if(action=="expand"){
				_t.expand();
			}
			else if(action=="toggle"){
				_t.toggle();
			}
		}
	});
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\bootstrap\js\bs_tab.js
*/
/* ========================================================================
 * Bootstrap: tab.js v3.0.3
 * http://getbootstrap.com/javascript/#tabs
 * ========================================================================
 * Copyright 2013 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ======================================================================== */


+function ($) { "use strict";

  // TAB CLASS DEFINITION
  // ====================

  var Tab = function (element) {
    this.element = $(element)
  }

  Tab.prototype.show = function () {
    var $this    = this.element
    var $ul      = $this.closest('ul:not(.dropdown-menu)')
    var selector = $this.data('target')

    if (!selector) {
      selector = $this.attr('href')
      selector = selector && selector.replace(/.*(?=#[^\s]*$)/, '') //strip for ie7
    }

    if ($this.parent('li').hasClass('active')) return

    var previous = $ul.find('.active:last a')[0]
    var e        = $.Event('show.bs.tab', {
      relatedTarget: previous
    })

    $this.trigger(e)

    if (e.isDefaultPrevented()) return

    var $target = $(selector)

    this.activate($this.parent('li'), $ul)
    this.activate($target, $target.parent(), function () {
      $this.trigger({
        type: 'shown.bs.tab'
      , relatedTarget: previous
      })
    })
  }

  Tab.prototype.activate = function (element, container, callback) {
    var $active    = container.find('> .active')
    var transition = callback
      && $.support.transition
      && $active.hasClass('fade')

    function next() {
      $active
        .removeClass('active')
        .find('> .dropdown-menu > .active')
        .removeClass('active')

      element.addClass('active')

      if (transition) {
        element[0].offsetWidth // reflow for transition
        element.addClass('in')
      } else {
        element.removeClass('fade')
      }

      if (element.parent('.dropdown-menu')) {
        element.closest('li.dropdown').addClass('active')
      }

      callback && callback()
    }

    transition ?
      $active
        .one($.support.transition.end, next)
        .emulateTransitionEnd(150) :
      next()

    $active.removeClass('in')
  }


  // TAB PLUGIN DEFINITION
  // =====================

  var old = $.fn.tab

  $.fn.tab = function ( option ) {
    return this.each(function () {
      var $this = $(this)
      var data  = $this.data('bs.tab')

      if (!data) $this.data('bs.tab', (data = new Tab(this)))
      if (typeof option == 'string') data[option]()
    })
  }

  $.fn.tab.Constructor = Tab


  // TAB NO CONFLICT
  // ===============

  $.fn.tab.noConflict = function () {
    $.fn.tab = old
    return this
  }


  // TAB DATA-API
  // ============

  $(document).on('click.bs.tab.data-api', '[data-toggle="tab"], [data-toggle="pill"]', function (e) {
    e.preventDefault()
    $(this).tab('show')
  })

}(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_combotree.js
*/
(function($){
	$.fn.extend({
		ITCUI_ComboTree : function(action,opt,args){
			var width = 150;
			var _this = $(this);
			var _t = this;

			_t.initComboBox = function(){
				UiCommon.makeWrap(_t);
				width = parseInt(_this.css("width"));
				var wrapWidth = _this.attr("treewidth");
				var wrapHeight = _this.attr("treeheight");
				_this.data("wrapWidth",wrapWidth);
				_this.data("wrapHeight",wrapHeight);
				_this.data("opt",opt);
				//覆盖onSelect和onCheck事件
				var onSelect = opt.onSelect || function(){};
				var onCheck = opt.onCheck || function(){};
				var labelMode = _this.attr("deftext")?"deftext" : "tree";//初始化文字是来自树还是来自deftext属性				
				_this.data("_onSelect",onSelect);
				_this.data("_onCheck",onCheck);
				delete(opt.onCheck);
				delete(opt.onSelect);
				//获取初始化已选择的内容
				var comboLabel = _this.attr("deftext") || "";
				multiSelect = opt["checkbox"]?true:false;
				_this.data("multiSelect", multiSelect);
				var fStr = _this.css("float");
				if(fStr){
					fStr = ";float:" + fStr;
				}
				else{
					fStr = "";
				}
				var comboHtml = "<div class='itcui_combo bbox' style='position:relative;width:100%;'>";
				comboHtml += UiCommon.getComboInputStr(_this);
				comboHtml += "<span class='itcui_combo_text'>" + comboLabel + "</span><span class='itcui_combo_arrow_wrap'><b class='itcui_combo_arrow'></b></span>";
				comboHtml += "</div>";
				_this.css("display","none");
				_this.before(comboHtml);
				_this.prev(".itcui_combo").click(function(e){
					e.stopPropagation();
					var __this = $(this).next("select");
					_t.popUp(__this);
				});
				if(labelMode=="tree"){
					_t.setInitText(_this);
				}
			};

			_t.popUp = function(__this){
				var wrap = __this.next("div");
				if(wrap.hasClass("itcui_dropdown_menu")){
					wrap.css("display","block");
				}
				else{
					var wrapWidth = __this.data("wrapWidth");
					var wrapHeight = __this.data("wrapHeight");
					var opt = __this.data("opt");
					var fix = __this.attr("fix");
					var wrapHtml = "<div class='itcui_dropdown_menu' style='padding-top:6px;padding-bottom:6px;height:" + wrapHeight + ";width:" + wrapWidth + "'><div class='itcui_combotree' style='width:100%'></div>";
					wrapHtml += "</div>";
					__this.after(wrapHtml);
					opt["onSelect"] = function(node){
						var wrap = $(this).parents(".itcui_dropdown_menu");
						var p = wrap.prev("select");
						//单选时点击一项就隐藏树
						var isMultiSelect = p.data("multiSelect");
						var realEvent = p.data("_onSelect");
						if(!isMultiSelect && !p.data("doNotHide")){
							/*##2014-6-4只有单选树在单击节点时才更改文字*/
							wrap.hide();
							var tree = wrap.find(".itcui_combotree");
							var selectNode = tree.tree("getSelected");
							wrap.prev().prev().find(".itcui_combo_text").html(selectNode.text);
							__this.data("val",selectNode.id);
						}
						if(p.data("doNotHide")){
							p.data('doNotHide',false);
						}
						realEvent(node);
					};
					opt["onCheck"] = function(node, checked){
						var wrap = $(this).parents(".itcui_dropdown_menu");
						var p = wrap.prev("select");
						var realEvent = p.data("_onCheck");
						p.prev(".itcui_combo").find(".itcui_combo_text").html(_t.getSelectedText(p));
						realEvent(node, checked);
					};
					//创建树
					var tree = __this.next('.itcui_dropdown_menu').find('.itcui_combotree').tree(opt);
					_t.bindEvent(__this);					
					//初始选择 仅用于单选树
					var initId = __this.data("initSelected");
					if(initId){
						__this.data("doNotHide",true);//因为下一次的默认选择会触发隐藏事件 所以这里要打标机
						var targetNode = tree.tree("find",initId);
						tree.tree("select",targetNode.target);
					}
				}

			};

			_t.bindEvent = function(__this){
				var tree = __this.next('.itcui_dropdown_menu').find('.itcui_combotree');
				var multiSelect = __this.data("multiSelect");
				$("body").click(function(e){
					var wrap = $(".itcui_dropdown_menu");
					wrap.hide();
				});
				//多选时点击树时不消失			
			};

			/*
				遍历树节点，获取满足某条件的节点列表
				parent - 父节点
				resultList - 返回的结果列表
				property - 要获取的属性
				needCondition - 满足true的条件，如checked 				
			*/
			_t.visitNode = function(parent,resultList,property,needCondition){
				if(!parent){
					return;
				}
				for(var i=0;i<parent.length;i++){
					node = parent[i];
					if(node[needCondition]){
						resultList.push(node[property]);
					}
					_t.visitNode(node["children"],resultList,property,needCondition);
				}
				
			};		

			_t.setInitText = function(__this){
				var multiSelect = __this.data("multiSelect");
				var opt = __this.data("opt");
				if(!opt["data"]){
					return;
				}
				var resultList = [];
				if(multiSelect){
					_t.visitNode(opt["data"],resultList,"text","checked");
				}
				else{
					var resultIdList = [];
					_t.visitNode(opt["data"],resultList,"text","selected");	
					_t.visitNode(opt["data"],resultIdList,"id","selected");	
					//如果找到，还要标记一下，因为单选树没有默认选择的功能
					if(resultList.length>0){
						__this.data("initSelected",resultIdList[0]);
					}
				}
				__this.prev(".itcui_combo").find(".itcui_combo_text").html(resultList.join(" "));
			};

			
			_t.getSelectedText = function(__this){
				var multiSelect = __this.data("multiSelect");
				var tree = __this.next('.itcui_dropdown_menu').find('.itcui_combotree');
				if(multiSelect){
					var nodes = tree.tree("getChecked");
					var nodesName = [];
					for(var i=0;i<nodes.length;i++){
						var node = nodes[i];
						nodesName.push(node.text);
					}
					return nodesName.join(" ");
				}
				else{
					var node = tree.tree("getSelected");
					if(node){
						return node.text;
					}
				}
			};

			_t.setText = function(val){
				_this.prev(".itcui_combo").find(".itcui_combo_text").html(val);
			};

			_t.setVal = function(val){
				_this.prev().children("input").val(val);
			};

			_t.getVal = function(){
				var iptVal = _this.prev().children("input").val();
				return iptVal || _this.data("initSelected") || _this.attr("defval");
			}

			if(action=="create"){
				_t.initComboBox();
			}
			else if(action=="getValue"){
				return _t.getVal();
			}
			else if(action=="setValue"){
				_t.setVal(opt);
			}
			else{
				return _this.next(".itcui_dropdown_menu").children(".itcui_combotree").tree(action,opt);
			}
		}
	});
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\easyui\js\jquery.layout.js
*/
﻿/**
 * jQuery EasyUI 1.3.5
 * 
 * Copyright (c) 2009-2013 www.jeasyui.com. All rights reserved.
 *
 * Licensed under the GPL or commercial licenses
 * To use it on other terms please contact us: info@jeasyui.com
 * http://www.gnu.org/licenses/gpl.txt
 * http://www.jeasyui.com/license_commercial.php
 *
 */
(function($){
var _1=false;
function _2(_3){
var _4=$.data(_3,"layout");
var _5=_4.options;
var _6=_4.panels;
var cc=$(_3);
if(_3.tagName=="BODY"){
cc._fit();
}else{
_5.fit?cc.css(cc._fit()):cc._fit(false);
}
var _7={top:0,left:0,width:cc.width(),height:cc.height()};
_8(_9(_6.expandNorth)?_6.expandNorth:_6.north,"n");
_8(_9(_6.expandSouth)?_6.expandSouth:_6.south,"s");
_a(_9(_6.expandEast)?_6.expandEast:_6.east,"e");
_a(_9(_6.expandWest)?_6.expandWest:_6.west,"w");
_6.center.panel("resize",_7);
function _b(pp){
var _c=pp.panel("options");
return Math.min(Math.max(_c.height,_c.minHeight),_c.maxHeight);
};
function _d(pp){
var _e=pp.panel("options");
return Math.min(Math.max(_e.width,_e.minWidth),_e.maxWidth);
};
function _8(pp,_f){
if(!pp.length){
return;
}
var _10=pp.panel("options");
var _11=_b(pp);
pp.panel("resize",{width:cc.width(),height:_11,left:0,top:(_f=="n"?0:cc.height()-_11)});
_7.height-=_11;
if(_f=="n"){
_7.top+=_11;
if(!_10.split&&_10.border){
_7.top--;
}
}
if(!_10.split&&_10.border){
_7.height++;
}
};
function _a(pp,_12){
if(!pp.length){
return;
}
var _13=pp.panel("options");
var _14=_d(pp);
pp.panel("resize",{width:_14,height:_7.height,left:(_12=="e"?cc.width()-_14:0),top:_7.top});
_7.width-=_14;
if(_12=="w"){
_7.left+=_14;
if(!_13.split&&_13.border){
_7.left--;
}
}
if(!_13.split&&_13.border){
_7.width++;
}
};
};
function _15(_16){
var cc=$(_16);
cc.addClass("layout");
function _17(cc){
cc.children("div").each(function(){
var _18=$.fn.layout.parsePanelOptions(this);
if("north,south,east,west,center".indexOf(_18.region)>=0){
_1b(_16,_18,this);
}
});
};
cc.children("form").length?_17(cc.children("form")):_17(cc);
cc.append("<div class=\"layout-split-proxy-h\"></div><div class=\"layout-split-proxy-v\"></div>");
cc.bind("_resize",function(e,_19){
var _1a=$.data(_16,"layout").options;
if(_1a.fit==true||_19){
_2(_16);
}
return false;
});
};
function _1b(_1c,_1d,el){
_1d.region=_1d.region||"center";
var _1e=$.data(_1c,"layout").panels;
var cc=$(_1c);
var dir=_1d.region;
if(_1e[dir].length){
return;
}
var pp=$(el);
if(!pp.length){
pp=$("<div></div>").appendTo(cc);
}
var _1f=$.extend({},$.fn.layout.paneldefaults,{width:(pp.length?parseInt(pp[0].style.width)||pp.outerWidth():"auto"),height:(pp.length?parseInt(pp[0].style.height)||pp.outerHeight():"auto"),doSize:false,collapsible:true,cls:("layout-panel layout-panel-"+dir),bodyCls:"layout-body",onOpen:function(){
var _20=$(this).panel("header").children("div.panel-tool");
_20.children("a.panel-tool-collapse").hide();
var _21={north:"up",south:"down",east:"right",west:"left"};
if(!_21[dir]){
return;
}
var _22="layout-button-"+_21[dir];
var t=_20.children("a."+_22);
if(!t.length){
t=$("<a href=\"javascript:void(0)\"></a>").addClass(_22).appendTo(_20);
t.bind("click",{dir:dir},function(e){
_2f(_1c,e.data.dir);
return false;
});
}
$(this).panel("options").collapsible?t.show():t.hide();
}},_1d);
pp.panel(_1f);
_1e[dir]=pp;
if(pp.panel("options").split){
var _23=pp.panel("panel");
_23.addClass("layout-split-"+dir);
var _24="";
if(dir=="north"){
_24="s";
}
if(dir=="south"){
_24="n";
}
if(dir=="east"){
_24="w";
}
if(dir=="west"){
_24="e";
}
_23.resizable($.extend({},{handles:_24,onStartResize:function(e){
_1=true;
if(dir=="north"||dir=="south"){
var _25=$(">div.layout-split-proxy-v",_1c);
}else{
var _25=$(">div.layout-split-proxy-h",_1c);
}
var top=0,_26=0,_27=0,_28=0;
var pos={display:"block"};
if(dir=="north"){
pos.top=parseInt(_23.css("top"))+_23.outerHeight()-_25.height();
pos.left=parseInt(_23.css("left"));
pos.width=_23.outerWidth();
pos.height=_25.height();
}else{
if(dir=="south"){
pos.top=parseInt(_23.css("top"));
pos.left=parseInt(_23.css("left"));
pos.width=_23.outerWidth();
pos.height=_25.height();
}else{
if(dir=="east"){
pos.top=parseInt(_23.css("top"))||0;
pos.left=parseInt(_23.css("left"))||0;
pos.width=_25.width();
pos.height=_23.outerHeight();
}else{
if(dir=="west"){
pos.top=parseInt(_23.css("top"))||0;
pos.left=_23.outerWidth()-_25.width();
pos.width=_25.width();
pos.height=_23.outerHeight();
}
}
}
}
_25.css(pos);
$("<div class=\"layout-mask\"></div>").css({left:0,top:0,width:cc.width(),height:cc.height()}).appendTo(cc);
},onResize:function(e){
if(dir=="north"||dir=="south"){
var _29=$(">div.layout-split-proxy-v",_1c);
_29.css("top",e.pageY-$(_1c).offset().top-_29.height()/2);
}else{
var _29=$(">div.layout-split-proxy-h",_1c);
_29.css("left",e.pageX-$(_1c).offset().left-_29.width()/2);
}
return false;
},onStopResize:function(e){
cc.children("div.layout-split-proxy-v,div.layout-split-proxy-h").hide();
pp.panel("resize",e.data);
_2(_1c);
_1=false;
cc.find(">div.layout-mask").remove();
}},_1d));
}
};
function _2a(_2b,_2c){
var _2d=$.data(_2b,"layout").panels;
if(_2d[_2c].length){
_2d[_2c].panel("destroy");
_2d[_2c]=$();
var _2e="expand"+_2c.substring(0,1).toUpperCase()+_2c.substring(1);
if(_2d[_2e]){
_2d[_2e].panel("destroy");
_2d[_2e]=undefined;
}
}
};
function _2f(_30,_31,_32){
if(_32==undefined){
_32="normal";
}
var _33=$.data(_30,"layout").panels;
var p=_33[_31];
var _34=p.panel("options");
if(_34.onBeforeCollapse.call(p)==false){
return;
}
var _35="expand"+_31.substring(0,1).toUpperCase()+_31.substring(1);
if(!_33[_35]){
_33[_35]=_36(_31);
_33[_35].panel("panel").bind("click",function(){
var _37=_38();
p.panel("expand",false).panel("open").panel("resize",_37.collapse);
p.panel("panel").animate(_37.expand,function(){
$(this).unbind(".layout").bind("mouseleave.layout",{region:_31},function(e){
if(_1==true){
return;
}
_2f(_30,e.data.region);
});
});
return false;
});
}
var _39=_38();
if(!_9(_33[_35])){
_33.center.panel("resize",_39.resizeC);
}
p.panel("panel").animate(_39.collapse,_32,function(){
p.panel("collapse",false).panel("close");
_33[_35].panel("open").panel("resize",_39.expandP);
$(this).unbind(".layout");
});
function _36(dir){
var _3a;
if(dir=="east"){
_3a="layout-button-left";
}else{
if(dir=="west"){
_3a="layout-button-right";
}else{
if(dir=="north"){
_3a="layout-button-down";
}else{
if(dir=="south"){
_3a="layout-button-up";
}
}
}
}
var p=$("<div></div>").appendTo(_30);
p.panel($.extend({},$.fn.layout.paneldefaults,{cls:("layout-expand layout-expand-"+dir),title:"&nbsp;",closed:true,doSize:false,tools:[{iconCls:_3a,handler:function(){
_3c(_30,_31);
return false;
}}]}));
p.panel("panel").hover(function(){
$(this).addClass("layout-expand-over");
},function(){
$(this).removeClass("layout-expand-over");
});
return p;
};
function _38(){
var cc=$(_30);
var _3b=_33.center.panel("options");
if(_31=="east"){
var ww=_3b.width+_34.width-28;
if(_34.split||!_34.border){
ww++;
}
return {resizeC:{width:ww},expand:{left:cc.width()-_34.width},expandP:{top:_3b.top,left:cc.width()-28,width:28,height:_3b.height},collapse:{left:cc.width(),top:_3b.top,height:_3b.height}};
}else{
if(_31=="west"){
var ww=_3b.width+_34.width-28;
if(_34.split||!_34.border){
ww++;
}
return {resizeC:{width:ww,left:28-1},expand:{left:0},expandP:{left:0,top:_3b.top,width:28,height:_3b.height},collapse:{left:-_34.width,top:_3b.top,height:_3b.height}};
}else{
if(_31=="north"){
var hh=_3b.height;
if(!_9(_33.expandNorth)){
hh+=_34.height-28+((_34.split||!_34.border)?1:0);
}
_33.east.add(_33.west).add(_33.expandEast).add(_33.expandWest).panel("resize",{top:28-1,height:hh});
return {resizeC:{top:28-1,height:hh},expand:{top:0},expandP:{top:0,left:0,width:cc.width(),height:28},collapse:{top:-_34.height,width:cc.width()}};
}else{
if(_31=="south"){
var hh=_3b.height;
if(!_9(_33.expandSouth)){
hh+=_34.height-28+((_34.split||!_34.border)?1:0);
}
_33.east.add(_33.west).add(_33.expandEast).add(_33.expandWest).panel("resize",{height:hh});
return {resizeC:{height:hh},expand:{top:cc.height()-_34.height},expandP:{top:cc.height()-28,left:0,width:cc.width(),height:28},collapse:{top:cc.height(),width:cc.width()}};
}
}
}
}
};
};
function _3c(_3d,_3e){
var _3f=$.data(_3d,"layout").panels;
var p=_3f[_3e];
var _40=p.panel("options");
if(_40.onBeforeExpand.call(p)==false){
return;
}
var _41=_42();
var _43="expand"+_3e.substring(0,1).toUpperCase()+_3e.substring(1);
if(_3f[_43]){
_3f[_43].panel("close");
p.panel("panel").stop(true,true);
p.panel("expand",false).panel("open").panel("resize",_41.collapse);
p.panel("panel").animate(_41.expand,function(){
_2(_3d);
});
}
function _42(){
var cc=$(_3d);
var _44=_3f.center.panel("options");
if(_3e=="east"&&_3f.expandEast){
return {collapse:{left:cc.width(),top:_44.top,height:_44.height},expand:{left:cc.width()-_3f["east"].panel("options").width}};
}else{
if(_3e=="west"&&_3f.expandWest){
return {collapse:{left:-_3f["west"].panel("options").width,top:_44.top,height:_44.height},expand:{left:0}};
}else{
if(_3e=="north"&&_3f.expandNorth){
return {collapse:{top:-_3f["north"].panel("options").height,width:cc.width()},expand:{top:0}};
}else{
if(_3e=="south"&&_3f.expandSouth){
return {collapse:{top:cc.height(),width:cc.width()},expand:{top:cc.height()-_3f["south"].panel("options").height}};
}
}
}
}
};
};
function _9(pp){
if(!pp){
return false;
}
if(pp.length){
return pp.panel("panel").is(":visible");
}else{
return false;
}
};
function _45(_46){
var _47=$.data(_46,"layout").panels;
if(_47.east.length&&_47.east.panel("options").collapsed){
_2f(_46,"east",0);
}
if(_47.west.length&&_47.west.panel("options").collapsed){
_2f(_46,"west",0);
}
if(_47.north.length&&_47.north.panel("options").collapsed){
_2f(_46,"north",0);
}
if(_47.south.length&&_47.south.panel("options").collapsed){
_2f(_46,"south",0);
}
};
$.fn.layout=function(_48,_49){
if(typeof _48=="string"){
return $.fn.layout.methods[_48](this,_49);
}
_48=_48||{};
return this.each(function(){
var _4a=$.data(this,"layout");
if(_4a){
$.extend(_4a.options,_48);
}else{
var _4b=$.extend({},$.fn.layout.defaults,$.fn.layout.parseOptions(this),_48);
$.data(this,"layout",{options:_4b,panels:{center:$(),north:$(),south:$(),east:$(),west:$()}});
_15(this);
}
_2(this);
_45(this);
});
};
$.fn.layout.methods={resize:function(jq){
return jq.each(function(){
_2(this);
});
},panel:function(jq,_4c){
return $.data(jq[0],"layout").panels[_4c];
},collapse:function(jq,_4d){
return jq.each(function(){
_2f(this,_4d);
});
},expand:function(jq,_4e){
return jq.each(function(){
_3c(this,_4e);
});
},add:function(jq,_4f){
return jq.each(function(){
_1b(this,_4f);
_2(this);
if($(this).layout("panel",_4f.region).panel("options").collapsed){
_2f(this,_4f.region,0);
}
});
},remove:function(jq,_50){
return jq.each(function(){
_2a(this,_50);
_2(this);
});
}};
$.fn.layout.parseOptions=function(_51){
return $.extend({},$.parser.parseOptions(_51,[{fit:"boolean"}]));
};
$.fn.layout.defaults={fit:false};
$.fn.layout.parsePanelOptions=function(_52){
var t=$(_52);
return $.extend({},$.fn.panel.parseOptions(_52),$.parser.parseOptions(_52,["region",{split:"boolean",minWidth:"number",minHeight:"number",maxWidth:"number",maxHeight:"number"}]));
};
$.fn.layout.paneldefaults=$.extend({},$.fn.panel.defaults,{region:null,split:false,minWidth:10,minHeight:10,maxWidth:10000,maxHeight:10000});
})(jQuery);


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_datagrid_fix.js
*/
var _itc_gridsearches = {};//记录有多少个表头搜索 resize的时候有用
_itc_togglecheck = function(o,isAdd){
    for(var i=0;i<o.length;i++){
        var t = $(o[i]).parent().next(".checkbox-fix").children(".icheckbox_flat-blue");
        if(t.length==0){
        	continue;
        }
        if(isAdd){
            t.addClass("checked");
            t.children("input").iCheck("check");
        }
        else{
            t.removeClass("checked");
            t.children("input").iCheck("uncheck");
        }
    }
};

(function($){
	$.fn.extend({
		iFixCheckbox : function(){
			var t = $(this).prev(".datagrid-view2");
            var _t = this;

            _t.fixCheckBox = function(o){
                var op = o.parent();
                var chkstr = o[0].checked == true?" checked":"";
                op.after("<div class='checkbox-fix'><input type='checkbox' " + chkstr + "/></div>");
                op.hide();                
                var chk = op.next("div").children("input");
                chk.iCheck({
                    checkboxClass: 'icheckbox_flat-blue',
                    radioClass: 'iradio_flat-blue'
                }).on("ifClicked",function(){
                    var ipt = $(this).parents(".checkbox-fix").prev("div").children("input");
                    ipt.trigger("click");
                });                
            };

			//表头部分
			t.children(".datagrid-header").find(".datagrid-header-check > input").each(function(){
				if($(this).parents("td").children(".checkbox-fix").length>0){
					return;
				}
                _t.fixCheckBox($(this));
            });
			//数据行部分
			t.children(".datagrid-body").find(".datagrid-cell-check > input").each(function(){
				if($(this).parents("td").children(".checkbox-fix").length>0){
					return;
				}
                _t.fixCheckBox($(this));
            });
		}
	});
})(jQuery);

(function($){
    $.fn.extend({
        ITCUI_GridSearch : function(action,opts){
            var that = $(this);
            var outWrap = that.parents(".panel");
            var _t = this;
            _t.beginSearch = function(){
            	//选项处理
            	var tableId = that.attr("id");
            	opts = opts || {};
            	opts.remoteSearch = opts.remoteSearch || false;
                opts.onParseArgs = opts.onParseArgs || function(arg){
                    return arg;
                };
                opts.noSearchColumns = opts.noSearchColumns || {};
            	that.data("searchOpts",opts);
                var dgOpts = that.datagrid("options");
                if(!dgOpts){
                    FW.debug("无法找到表格参数，是否$操作符指向错误？");
                    return;
                }                
                var isFrozen = dgOpts.frozenColumns && dgOpts.frozenColumns.length > 0;//是否有冻结列
                var thead = outWrap.find(isFrozen ? ".datagrid-view1" : ".datagrid-view2").children(".datagrid-header").find(".datagrid-header-row");
                var hasRownum = dgOpts.rownumbers;
                var fields = thead.children("td");
                sHtml = "";
                var rowcheck = false;
                var delta = 0;//累积列偏移
                var columns = isFrozen ? dgOpts.frozenColumns : dgOpts.columns;//有冻结列时只处理冻结的部分
                //如果有行序号 则加一个初始的偏移
                if(dgOpts.rownumbers){
                	delta += 25;
                }
                for(var i=0;i<fields.length;i++){
                    var field = $(fields[i]);
                    var wrapId = "gs_" + _t.attr("id") + "_" + field.attr("field");
                    /*#2014-6-11对隐藏列的处理#*/
                    if(field.css("display")=="none"){
                        continue;
                    }
                    /*#2015-3-30跳过行序号*/
                    if(field.children(".datagrid-header-rownumber").length){
                    	continue;
                    }
                    //行选框要更大的偏移
                    if(field.children(".datagrid-header-check").length>0){
                        delta += 28;
                        continue;
                    }
                    field.attr("id",wrapId);                    
                    var width = field.width();
                    //非搜索列
                    if(opts.noSearchColumns[i]){
                    	delta += width;
                    	continue;
                    }
                    width -= 8;
                    //因为checkbox在的时候会算错 这里最后一个框的宽度要小一些
                    if(i==fields.length-1){
                        width -= 10;
                    }
                    var mlStr = delta ? ("margin-left:" + delta + "px") : "";
                    delta = 0;
                    //判断是不是特殊类型的表头搜索
                    var fType = "input";
                    var col = columns[0][hasRownum ? (i-1) : i];
                    if(col){
                        if(col.editor && col.editor.type=="combobox"){
                            var cType = col.editor.type;
                            fType = "combobox";
                        }
                    }

                    if(fType=="input"){
                        sHtml += '<div fieldid="' + wrapId + '" class="itc-gs-wrap input-roup-sm pull-left" style="width:' + width + 'px;' + mlStr + '">' +
        								'<input type="text" class="enter-allowed" icon="itcui_btn_mag" field="' + field.attr("field") + '">' + 
        						 '</div>';
                    }
                    else if(fType=="combobox"){
                        sHtml += '<div fieldid="' + wrapId + '" class="itc-gs-wrap pull-left" style="width:' + width + 'px;' + mlStr + '">' +
                                     '<select style="width:100%" field="' + field.attr("field") + '" colindex="' + i + '">' + 
                                     '</select>' +
                                '</div>';
                    }
                }
                var gsId = "itc_gs_" + that.attr("id");
                $("<div class='bbox itc_gridsearch' style='height:28px;clear:both'></div>").prependTo(outWrap).html(sHtml).attr("id",gsId);
                _itc_gridsearches[gsId] = true;
                //搜索框回车事件
                outWrap.children(".itc_gridsearch").find("input").each(function(){
                	$(this).keypress(function(e) {
					    if(e.which == 13) {
					        _t.doSearch(this);
					    }
					}).ITCUI_Input();
                });
                //下拉框初始化
                outWrap.children(".itc_gridsearch").find("select").each(function(){
                    var me = $(this);
                    var colIndex = me.attr("colindex");
                    var opts = columns[0][colIndex].editor.options;
                    var od = opts.data;
                    if(od && !opts.multiSelect && isArray(od)){
                    	if(od[0] && od[0][1]!="全部"){
	                        //去掉第一项的默认选择 改为“全部”
	                        if(od[0].length==3){
	                            od[0][2] = false;
	                        }
	                        od.unshift([null,"全部",true]);
                        }
                    }
                    opts.onChange = function(val,ptr){
                        _t.doSearch(ptr);
                    };
                    opts.initOnChange = false;
                    me.iCombo("init",opts);
                });
                outWrap.find(".itcui_btn_mag").click(function(){
                	_t.doSearch($(this).prev("input"));
                });
            };


            _t.endSearch = function(){
                that.parents(".panel").children(".itc_gridsearch").remove();
                var gsId = "itc_gs_" + that.attr("id");                
                if(that.data("parsedArgs")){
                    //恢复没有条件的列表
                    var origArgs = that.datagrid("options").queryParams;
                    for(var k in that.data("parsedArgs")){
                        delete(origArgs[k]);
                    }
                    //删除表格初始化标记 让分页器也能初始化
                    delete(_itc_grids[that.attr("id")]);
                    that.datagrid("getPager").pagination("select",1);
                    that.data("parsedArgs",null);
                }
                delete(_itc_gridsearches[gsId]);
            };

            /*搜索的入口函数 必须从某个文本框开始*/
            _t.doSearch = function(target){
            	var searchWrap = $(target).parents(".itc_gridsearch");
            	var that = searchWrap.parent(".datagrid")
				            		 .children(".datagrid-wrap")
				            		 .children(".datagrid-view")
				            		 .children("table");
				var sOpts = that.data("searchOpts");
				//合成搜索参数
				var inputs = searchWrap.find("input");
                var sArg = {};
				for(var i=0;i<inputs.length;i++){
                    var ipt = $(inputs[i]);
                    if(ipt.attr("type")!="hidden"){
                        sArg[ipt.attr("field")] = ipt.val();
                    }
                }
                //combobox搜索支持
                var selects = searchWrap.find("select");
                for(var i=0;i<selects.length;i++){
                    var sl = $(selects[i]);
                    sArg["_" + sl.attr("field")] = sl.iCombo("getVal");
                }
				if(!sOpts.remoteSearch){
					that.ITCUI_Pagination("search",sArg);
				}
				else{
					parsedArgs = sOpts.onParseArgs(sArg);
					//扩展参数
					var origArgs = that.datagrid("options").queryParams;
					for(var k in parsedArgs){
						origArgs[k] = parsedArgs[k];						
					}
					//将处理过的参数保存 以便停止搜索时可以还原列表
					that.data("parsedArgs",parsedArgs);
                    /*##2014-6-24 修正远程搜索时分页器不能正确初始化的bug*/
                    if(typeof(_itc_grids)!="undefined"){
                        delete(_itc_grids[that.attr("id")]);
                    }
					that.datagrid("getPager").pagination("select",1);
				}	
            };

            if(action=="init"){
            	_t.beginSearch();
            }
            else if(action=="end"){
            	_t.endSearch();
            }
        }   
     });
})(jQuery);

function _itc_bindvalidation(obj,opts){
	if(!opts || !opts.rules){
		return;
	}
	var datagrid = obj.parents(".datagrid-view2").next("table");
    //反查该列field对应的title
    var fields = datagrid.data("datagrid").options.columns[0];
    var name = obj.attr("name");
    if(name){
        name = name.split("_")[0];
    }
    for(var i=0;i<fields.length;i++){
        var field = fields[i];
        if(field.field === name){
            opts.title = field.title;
        }
    }
	//动态添加规则必须先空初始化一次验证器
	var validatorInited = datagrid.data("validatorInited");
	if(!validatorInited){
		var form = datagrid.parents("form");
		if(form.length==0){
			FW.debug("行编辑校验初始化失败，表格外层必须是form");
			return;
		}
		form.validate({
			"errorPlacement":ITC_ValidStyle1,
			"success":ITC_ValidSucc1,
			"ignore":".ignore"
		});
		datagrid.data("validatorInited",true);
	}
	var rules = $.extend({},opts.rules);
	rules.messages = rules.messages || {};
	for(var k in rules){
		if(k!="messages"){
            var msg = rules.messages[k] || defValidMsg[k] || "输入错误";
            msg = UiCommon.replaceValidMsgTable(msg, opts, opts.rules);
			rules.messages[k] = msg; 
		}
	}
	obj.rules("add",rules)
}

//覆盖原EasyUI的编辑器
$.extend($.fn.datagrid.defaults.editors, {
	//文字框
    text: {
        init: function(container,options,field,lineNum){
            var name = field + '_' + lineNum;
            //2014-09-09这里加了个6px的间距
            var input = $('<div class="input-group-sm bbox" style="position:relative;padding-right:6px"><input type="text" name="' + name + '" class="form-control validatebox-text valid-field" /></div>').appendTo(container);
            var obj = input.children("input");
            if(options && typeof(options)=="object"){
                for(var k in options){
                    if(/^on.+/.test(k)){
                        if(typeof(options[k])=="function"){
                            var evt = k.substring(2).toLowerCase();
                            obj.on(evt,options[k]);
                        }
                    }
                }
                if(options.dataType){
                    UiCommon.onlyNumKeyDown(options,obj);
                }
                if(options.align=="right"){
                	obj.addClass("dg-align-right");
                }
            }
            _itc_bindvalidation(obj,options);
            return input;
        },
        destroy: function(target){
            $(target).remove();
        },
        getValue: function(target){
            return $(target).children("input").val();
        },
        setValue: function(target, value){
            $(target).children("input").val(value);
        },
        resize: function(target, width){
            //$(target)._outerWidth(width);
        }
    },
    //单选/复选框 限定样式icheckbox_flat-blue
    checkbox: {
        init: function(container, options){
            var input = $('<input type="checkbox">').appendTo(container);
            input.iCheck({
			    checkboxClass: 'icheckbox_flat-blue',
			    radioClass: 'iradio_flat-blue'
			});
            return input;
        },
        destroy: function(target){
            $(target).parent().remove();
        },
        getValue: function(target){
            return $(target).parent().hasClass("checked");
        },
        setValue: function(target, value){
        	var tp = $(target);
        	if(value){
        		tp.iCheck('check');
        	}
        	else{
        		tp.iCheck('uncheck');
        	}
            $(target).children("input").val(value);
        },
        resize: function(target, width){
            
        }
    },
    //日期/时间选择器
    datebox : {
        init: function(container, options,field,lineNum){
            var name = field + "_" + lineNum;
            var o = $('<div class="itc_lazypicker bbox" style="width:100%"></div>').appendTo(container);
            container.css("padding-right","6px");
            options = options || {};
            if(options.dataType){
            	o.data("dataType",options.dataType);
            	if(options.dataType=="date"){
            		options.format = options.format || "yyyy-mm-dd";
            		options.minView = options.minView || 2;
            	}
            	else if(options.dataType=="datetime"){
            		options.format = options.format || "yyyy-mm-dd hh:ii";
            	}
            }
            o.ITCUI_LazyLoadPicker({inputName:name},options);
            var obj = o.children("input");
            _itc_bindvalidation(obj,options);
            if(options && typeof(options)=="object"){
                for(var k in options){
                    if(/^on.+/.test(k)){
                        if(typeof(options[k])=="function"){
                            var evt = k.substring(2).toLowerCase();
                            obj.on(evt,options[k]);
                        }
                    }
                }
            }
            return o;
        },
        destroy: function(target){
            $(target).remove();
        },
        getValue: function(target){
        	var val = $(target).children("input").val();
        	if(target.data("dataType")=="date"){
        		val = FW.date2long(val);
        	}
        	else if(target.data("dataType")=="datetime"){
        		val = FW.time2long(val);	
        	}
            return val;
        },        
        setValue: function(target, value){
        	if(target.data("dataType")=="date"){
        		value = FW.long2date(value);
        	}
        	else if(target.data("dataType")=="datetime"){
        		value = FW.long2time(value);
        	}
            $(target).children("input").val(value);
        },
        resize: function(target, width){
            //$(target)._outerWidth(width);
        }
    },
    //单选框
    combobox : {
        init: function(container, options,field,lineNum){
            maxlength = options.maxlength || 18;
            var name = field + "_" + lineNum;
            var o = $('<select style="width:100%" maxlength=' + maxlength + ' fixposition=1 _name="' + name + '">').appendTo(container);
            o.iCombo("init",options);
            _itc_bindvalidation(o.prev("div").children("input"),options);
            return o;
        },
        destroy: function(target){
            $(target).prev(".itcui_combo").remove();
            $(target).remove();
        },
        getValue: function(target){
            return $(target).iCombo("getVal");
        },
        setValue: function(target, value){
            $(target).iCombo("setVal",value);   
        },
        resize: function(target, width){
            //$(target)._outerWidth(width);
        }
    }
});

//覆盖validatebox方法
(function($){
	$.fn.extend({
		validatebox : function(arg){
			return true;
		}
	});
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\thirdparty\js\bootstrap-datetimepicker.js
*/
﻿/* =========================================================
 * bootstrap-datetimepicker.js
 * =========================================================
 * Copyright 2012 Stefan Petre
 * Improvements by Andrew Rowls
 * Improvements by Sébastien Malot
 * Improvements by Yun Lai
 * Project URL : http://www.malot.fr/bootstrap-datetimepicker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================= */

/*
 * Improvement by CuGBabyBeaR @ 2013-09-12
 * 
 * Make it work in bootstrap v3
 */

!function ($) {

	function UTCDate() {
		return new Date(Date.UTC.apply(Date, arguments));
	}

	function UTCToday() {
		var today = new Date();
		return UTCDate(today.getUTCFullYear(), today.getUTCMonth(), today.getUTCDate(), today.getUTCHours(), today.getUTCMinutes(), today.getUTCSeconds(), 0);
	}

	// Picker object

	var Datetimepicker = function (element, options) {
		var that = this;

		this.element = $(element);

		this.language = options.language || this.element.data('date-language') || "en";
		this.language = this.language in dates ? this.language : "en";
		this.isRTL = dates[this.language].rtl || false;
		this.formatType = options.formatType || this.element.data('format-type') || 'standard';
		this.format = DPGlobal.parseFormat(options.format || this.element.data('date-format') || dates[this.language].format || DPGlobal.getDefaultFormat(this.formatType, 'input'), this.formatType);
		this.isInline = false;
		this.isVisible = false;
		this.isInput = this.element.is('input');

		this.bootcssVer = this.isInput ? (this.element.is('.form-control') ? 3 : 2) : ( this.bootcssVer = this.element.is('.input-group') ? 3 : 2 );

		this.component = this.element.is('.date') ? ( this.bootcssVer == 3 ? this.element.find('.input-group-addon .glyphicon-th, .input-group-addon .glyphicon-time, .input-group-addon .glyphicon-calendar').parent() : this.element.find('.add-on .icon-th, .add-on .icon-time, .add-on .icon-calendar').parent()) : false;
		this.componentReset = this.element.is('.date') ? ( this.bootcssVer == 3 ? this.element.find('.input-group-addon .glyphicon-remove').parent() : this.element.find('.add-on .icon-remove').parent()) : false;
		this.hasInput = this.component && this.element.find('input').length;
		if (this.component && this.component.length === 0) {
			this.component = false;
		}
		this.linkField = options.linkField || this.element.data('link-field') || false;
		this.linkFormat = DPGlobal.parseFormat(options.linkFormat || this.element.data('link-format') || DPGlobal.getDefaultFormat(this.formatType, 'link'), this.formatType);
		this.minuteStep = options.minuteStep || this.element.data('minute-step') || 5;
		this.pickerPosition = options.pickerPosition || this.element.data('picker-position') || 'bottom-right';
		this.showMeridian = options.showMeridian || this.element.data('show-meridian') || false;
		this.initialDate = options.initialDate || new Date();
		//by murmur 2014.1.14
		this.beforeShowDay = options.beforeShowDay || function(){};
		this._attachEvents();

		this.formatViewType = "datetime";
		if ('formatViewType' in options) {
			this.formatViewType = options.formatViewType;
		} else if ('formatViewType' in this.element.data()) {
			this.formatViewType = this.element.data('formatViewType');
		}

		this.minView = 0;
		if ('minView' in options) {
			this.minView = options.minView;
		} else if ('minView' in this.element.data()) {
			this.minView = this.element.data('min-view');
		}
		this.minView = DPGlobal.convertViewMode(this.minView);

		this.maxView = DPGlobal.modes.length - 1;
		if ('maxView' in options) {
			this.maxView = options.maxView;
		} else if ('maxView' in this.element.data()) {
			this.maxView = this.element.data('max-view');
		}
		this.maxView = DPGlobal.convertViewMode(this.maxView);

		this.wheelViewModeNavigation = false;
		if ('wheelViewModeNavigation' in options) {
			this.wheelViewModeNavigation = options.wheelViewModeNavigation;
		} else if ('wheelViewModeNavigation' in this.element.data()) {
			this.wheelViewModeNavigation = this.element.data('view-mode-wheel-navigation');
		}

		this.wheelViewModeNavigationInverseDirection = false;

		if ('wheelViewModeNavigationInverseDirection' in options) {
			this.wheelViewModeNavigationInverseDirection = options.wheelViewModeNavigationInverseDirection;
		} else if ('wheelViewModeNavigationInverseDirection' in this.element.data()) {
			this.wheelViewModeNavigationInverseDirection = this.element.data('view-mode-wheel-navigation-inverse-dir');
		}

		this.wheelViewModeNavigationDelay = 100;
		if ('wheelViewModeNavigationDelay' in options) {
			this.wheelViewModeNavigationDelay = options.wheelViewModeNavigationDelay;
		} else if ('wheelViewModeNavigationDelay' in this.element.data()) {
			this.wheelViewModeNavigationDelay = this.element.data('view-mode-wheel-navigation-delay');
		}

		this.startViewMode = 2;
		if ('startView' in options) {
			this.startViewMode = options.startView;
		} else if ('startView' in this.element.data()) {
			this.startViewMode = this.element.data('start-view');
		}
		this.startViewMode = DPGlobal.convertViewMode(this.startViewMode);
		this.viewMode = this.startViewMode;

		this.viewSelect = this.minView;
		if ('viewSelect' in options) {
			this.viewSelect = options.viewSelect;
		} else if ('viewSelect' in this.element.data()) {
			this.viewSelect = this.element.data('view-select');
		}
		this.viewSelect = DPGlobal.convertViewMode(this.viewSelect);

		this.forceParse = true;
		if ('forceParse' in options) {
			this.forceParse = options.forceParse;
		} else if ('dateForceParse' in this.element.data()) {
			this.forceParse = this.element.data('date-force-parse');
		}

		var apdTarget = window._dialogEmmbed?_parent().$("body"):(this.isInline ? this.element : 'body');
		this.picker = $((this.bootcssVer == 3) ? DPGlobal.templateV3 : DPGlobal.template)
			.appendTo(apdTarget)
			.on({
				click:     $.proxy(this.click, this),
				mousedown: $.proxy(this.mousedown, this)
			});

		if (this.wheelViewModeNavigation) {
			if ($.fn.mousewheel) {
				this.picker.on({mousewheel: $.proxy(this.mousewheel, this)});
			} else {
				console.log("Mouse Wheel event is not supported. Please include the jQuery Mouse Wheel plugin before enabling this option");
			}
		}

		if (this.isInline) {
			this.picker.addClass('datetimepicker-inline');
		} else {
			this.picker.addClass('datetimepicker-dropdown-' + this.pickerPosition + ' dropdown-menu');
		}
		if (this.isRTL) {
			this.picker.addClass('datetimepicker-rtl');
			if (this.bootcssVer == 3) {
				this.picker.find('.prev span, .next span')
					.toggleClass('glyphicon-arrow-left glyphicon-arrow-right');
			} else {
				this.picker.find('.prev i, .next i')
					.toggleClass('icon-arrow-left icon-arrow-right');
			}
			;

		}
		$(document).on('mousedown', function (e) {
			// Clicked outside the datetimepicker, hide it
			if ($(e.target).closest('.datetimepicker').length === 0) {
				that.hide();
			}
		});

		if(window._dialogEmmbed){
			_parent().$(".window-mask").on('mousedown', function (e) {
				that.hide();
			});
		}

		this.autoclose = false;
		if ('autoclose' in options) {
			this.autoclose = options.autoclose;
		} else if ('dateAutoclose' in this.element.data()) {
			this.autoclose = this.element.data('date-autoclose');
		}

		this.keyboardNavigation = true;
		if ('keyboardNavigation' in options) {
			this.keyboardNavigation = options.keyboardNavigation;
		} else if ('dateKeyboardNavigation' in this.element.data()) {
			this.keyboardNavigation = this.element.data('date-keyboard-navigation');
		}

		this.todayBtn = (options.todayBtn || this.element.data('date-today-btn') || false);
		this.todayHighlight = (options.todayHighlight || this.element.data('date-today-highlight') || false);

		this.weekStart = ((options.weekStart || this.element.data('date-weekstart') || dates[this.language].weekStart || 0) % 7);
		this.weekEnd = ((this.weekStart + 6) % 7);
		this.startDate = -Infinity;
		this.endDate = Infinity;
		this.daysOfWeekDisabled = [];
		this.setStartDate(options.startDate || this.element.data('date-startdate'));
		this.setEndDate(options.endDate || this.element.data('date-enddate'));
		this.setDaysOfWeekDisabled(options.daysOfWeekDisabled || this.element.data('date-days-of-week-disabled'));
		this.fillDow();
		this.fillMonths();
		this.update();
		this.showMode();

		if (this.isInline) {
			this.show();
		}
	};

	Datetimepicker.prototype = {
		constructor: Datetimepicker,

		_events:       [],
		_attachEvents: function () {
			this._detachEvents();
			if (this.isInput) { // single input
				this._events = [
					[this.element, {
						focus:   $.proxy(this.show, this),
						keyup:   $.proxy(this.update, this),
						keydown: $.proxy(this.keydown, this)
					}]
				];
			}
			else if (this.component && this.hasInput) { // component: input + button
				this._events = [
					// For components that are not readonly, allow keyboard nav
					[this.element.find('input'), {
						focus:   $.proxy(this.show, this),
						keyup:   $.proxy(this.update, this),
						keydown: $.proxy(this.keydown, this)
					}],
					[this.component, {
						click: $.proxy(this.show, this)
					}]
				];
				if (this.componentReset) {
					this._events.push([
						this.componentReset,
						{click: $.proxy(this.reset, this)}
					]);
				}
			}
			else if (this.element.is('div')) {  // inline datetimepicker
				this.isInline = true;
			}
			else {
				this._events = [
					[this.element, {
						click: $.proxy(this.show, this)
					}]
				];
			}
			for (var i = 0, el, ev; i < this._events.length; i++) {
				el = this._events[i][0];
				ev = this._events[i][1];
				el.on(ev);
			}
		},

		_detachEvents: function () {
			for (var i = 0, el, ev; i < this._events.length; i++) {
				el = this._events[i][0];
				ev = this._events[i][1];
				el.off(ev);
			}
			this._events = [];
		},

		show: function (e) {
			this.picker.show();
			this.height = this.component ? this.component.outerHeight() : this.element.outerHeight();
			if (this.forceParse) {
				this.update();
			}
			this.place();
			$(window).on('resize', $.proxy(this.place, this));
			if (e) {
				e.stopPropagation();
				e.preventDefault();
			}
			this.isVisible = true;
			this.element.trigger({
				type: 'show',
				date: this.date
			});
		},

		hide: function (e) {
			if (!this.isVisible) return;
			if (this.isInline) return;
			this.picker.hide();
			$(window).off('resize', this.place);
			this.viewMode = this.startViewMode;
			this.showMode();
			if (!this.isInput) {
				$(document).off('mousedown', this.hide);
			}

			if (
				this.forceParse &&
					(
						this.isInput && this.element.val() ||
							this.hasInput && this.element.find('input').val()
						)
				)
				this.setValue();
			this.isVisible = false;
			this.element.trigger({
				type: 'hide',
				date: this.date
			});
			//by murmur 2014-11-18
			var inputObj = this.isInput && this.element || this.hasInput && this.element.find('input');
			if(inputObj.hasClass("valid-field")){
				inputObj.valid();
			}
		},

		remove: function () {
			this._detachEvents();
			this.picker.remove();
			delete this.picker;
			delete this.element.data().datetimepicker;
		},

		getDate: function () {
			var d = this.getUTCDate();
			return new Date(d.getTime() + (d.getTimezoneOffset() * 60000));
		},

		getUTCDate: function () {
			return this.date;
		},

		setDate: function (d) {
			this.setUTCDate(new Date(d.getTime() - (d.getTimezoneOffset() * 60000)));
		},

		setUTCDate: function (d) {
			if (d >= this.startDate && d <= this.endDate) {
				this.date = d;
				this.setValue();
				this.viewDate = this.date;
				this.fill();
			} else {
				this.element.trigger({
					type:      'outOfRange',
					date:      d,
					startDate: this.startDate,
					endDate:   this.endDate
				});
			}
		},

		setFormat: function (format) {
			this.format = DPGlobal.parseFormat(format, this.formatType);
			var element;
			if (this.isInput) {
				element = this.element;
			} else if (this.component) {
				element = this.element.find('input');
			}
			if (element && element.val()) {
				this.setValue();
			}
		},

		setValue: function () {
			var formatted = this.getFormattedDate();
			if (!this.isInput) {
				if (this.component) {
					this.element.find('input').val(formatted);
				}
				this.element.data('date', formatted);
			} else {
				this.element.val(formatted);
			}
			if (this.linkField) {
				$('#' + this.linkField).val(this.getFormattedDate(this.linkFormat));
			}
		},

		getFormattedDate: function (format) {
			if (format == undefined) format = this.format;
			return DPGlobal.formatDate(this.date, format, this.language, this.formatType);
		},

		setStartDate: function (startDate) {
			this.startDate = startDate || -Infinity;
			if (this.startDate !== -Infinity) {
				this.startDate = DPGlobal.parseDate(this.startDate, this.format, this.language, this.formatType);
			}
			this.update();
			this.updateNavArrows();
		},

		setEndDate: function (endDate) {
			this.endDate = endDate || Infinity;
			if (this.endDate !== Infinity) {
				this.endDate = DPGlobal.parseDate(this.endDate, this.format, this.language, this.formatType);
			}
			this.update();
			this.updateNavArrows();
		},

		setDaysOfWeekDisabled: function (daysOfWeekDisabled) {
			this.daysOfWeekDisabled = daysOfWeekDisabled || [];
			if (!$.isArray(this.daysOfWeekDisabled)) {
				this.daysOfWeekDisabled = this.daysOfWeekDisabled.split(/,\s*/);
			}
			this.daysOfWeekDisabled = $.map(this.daysOfWeekDisabled, function (d) {
				return parseInt(d, 10);
			});
			this.update();
			this.updateNavArrows();
		},

		place: function () {
			if (this.isInline) return;

			var index_highest = 0;
			$('div').each(function () {
				var index_current = parseInt($(this).css("zIndex"), 10);
				if (index_current > index_highest) {
					index_highest = index_current;
				}
			});
			var zIndex = window._dialogEmmbed?(UiCommon.getMaskIndex() + 10):(index_highest + 10);

			var offset, top, left;
			if (this.component) {
				offset = this.component.offset();
				left = offset.left;
				if (this.pickerPosition == 'bottom-left' || this.pickerPosition == 'top-left') {
					left += this.component.outerWidth() - this.picker.outerWidth();
				}
			} else {
				offset = this.element.offset();
				left = offset.left;
			}
			if (this.pickerPosition == 'top-left' || this.pickerPosition == 'top-right') {
				top = offset.top - this.picker.outerHeight();
			} else {
				top = offset.top + this.height;
			}
			if(window._dialogEmmbed){
				var pbody = $("body");
				var offDialog = UiCommon.getDialogOffset();
				top += offDialog.top + 26 - pbody.scrollTop();
				left += offDialog.left - pbody.scrollLeft();
			}
			this.picker.css({
				top:    top,
				left:   left,
				zIndex: zIndex
			});
		},

		update: function () {
			var date, fromArgs = false;
			if (arguments && arguments.length && (typeof arguments[0] === 'string' || arguments[0] instanceof Date)) {
				date = arguments[0];
				fromArgs = true;
			} else {
				date = this.element.data('date') || (this.isInput ? this.element.val() : this.element.find('input').val()) || this.initialDate;
				if (typeof date == 'string' || date instanceof String) {
				  date = date.replace(/^\s+|\s+$/g,'');
				}
			}

			if (!date) {
				date = new Date();
				fromArgs = false;
			}

			this.date = DPGlobal.parseDate(date, this.format, this.language, this.formatType);

			if (fromArgs) this.setValue();

			if (this.date < this.startDate) {
				this.viewDate = new Date(this.startDate);
			} else if (this.date > this.endDate) {
				this.viewDate = new Date(this.endDate);
			} else {
				this.viewDate = new Date(this.date);
			}
			this.fill();
		},

		fillDow: function () {
			var dowCnt = this.weekStart,
				html = '<tr>';
			while (dowCnt < this.weekStart + 7) {
				html += '<th class="dow">' + dates[this.language].daysMin[(dowCnt++) % 7] + '</th>';
			}
			html += '</tr>';
			this.picker.find('.datetimepicker-days thead').append(html);
		},

		fillMonths: function () {
			var html = '',
				i = 0;
			while (i < 12) {
				html += '<span class="month">' + dates[this.language].monthsShort[i++] + '月</span>';
			}
			this.picker.find('.datetimepicker-months td').html(html);
		},

		fill: function () {
			if (this.date == null || this.viewDate == null) {
				return;
			}
			var d = new Date(this.viewDate),
				year = d.getUTCFullYear(),
				month = d.getUTCMonth(),
				dayMonth = d.getUTCDate(),
				hours = d.getUTCHours(),
				minutes = d.getUTCMinutes(),
				startYear = this.startDate !== -Infinity ? this.startDate.getUTCFullYear() : -Infinity,
				startMonth = this.startDate !== -Infinity ? this.startDate.getUTCMonth() : -Infinity,
				endYear = this.endDate !== Infinity ? this.endDate.getUTCFullYear() : Infinity,
				endMonth = this.endDate !== Infinity ? this.endDate.getUTCMonth() : Infinity,
				currentDate = (new UTCDate(this.date.getUTCFullYear(), this.date.getUTCMonth(), this.date.getUTCDate())).valueOf(),
				today = new Date();
			/*	
			this.picker.find('.datetimepicker-days thead th:eq(1)')
				.text(dates[this.language].months[month] + ' ' + year);
			*/
			this.picker.find('.datetimepicker-days thead th:eq(1)')
				.text(year + "年" + (month+1) + "月");
			if (this.formatViewType == "time") {
				var hourConverted = hours % 12 ? hours % 12 : 12;
				var hoursDisplay = (hourConverted < 10 ? '0' : '') + hourConverted;
				var minutesDisplay = (minutes < 10 ? '0' : '') + minutes;
				var meridianDisplay = dates[this.language].meridiem[hours < 12 ? 0 : 1];
				this.picker.find('.datetimepicker-hours thead th:eq(1)')
					.text(hoursDisplay + ':' + minutesDisplay + ' ' + meridianDisplay.toUpperCase());
				this.picker.find('.datetimepicker-minutes thead th:eq(1)')
					.text(hoursDisplay + ':' + minutesDisplay + ' ' + meridianDisplay.toUpperCase());
			} else {
				/*
				this.picker.find('.datetimepicker-hours thead th:eq(1)')
					.text(dayMonth + ' ' + dates[this.language].months[month] + ' ' + year);
				this.picker.find('.datetimepicker-minutes thead th:eq(1)')
					.text(dayMonth + ' ' + dates[this.language].months[month] + ' ' + year);
				*/
				this.picker.find('.datetimepicker-hours thead th:eq(1)')
					.text(year + "年" + (month+1) + "月" + dayMonth + "日");
				this.picker.find('.datetimepicker-minutes thead th:eq(1)')
					.text(year + "年" + (month+1) + "月" + dayMonth + "日");
			}
			this.picker.find('tfoot th.today')
				.text(dates[this.language].today)
				.toggle(this.todayBtn !== false);
			this.updateNavArrows();
			this.fillMonths();
			/*var prevMonth = UTCDate(year, month, 0,0,0,0,0);
			 prevMonth.setUTCDate(prevMonth.getDate() - (prevMonth.getUTCDay() - this.weekStart + 7)%7);*/
			var prevMonth = UTCDate(year, month - 1, 28, 0, 0, 0, 0),
				day = DPGlobal.getDaysInMonth(prevMonth.getUTCFullYear(), prevMonth.getUTCMonth());
			prevMonth.setUTCDate(day);
			prevMonth.setUTCDate(day - (prevMonth.getUTCDay() - this.weekStart + 7) % 7);
			var nextMonth = new Date(prevMonth);
			nextMonth.setUTCDate(nextMonth.getUTCDate() + 42);
			nextMonth = nextMonth.valueOf();
			var html = [];
			var clsName;
			while (prevMonth.valueOf() < nextMonth) {
				if (prevMonth.getUTCDay() == this.weekStart) {
					html.push('<tr>');
				}
				clsName = '';
				if (prevMonth.getUTCFullYear() < year || (prevMonth.getUTCFullYear() == year && prevMonth.getUTCMonth() < month)) {
					clsName += ' old';
				} else if (prevMonth.getUTCFullYear() > year || (prevMonth.getUTCFullYear() == year && prevMonth.getUTCMonth() > month)) {
					clsName += ' new';
				}
				// Compare internal UTC date with local today, not UTC today
				if (this.todayHighlight &&
					prevMonth.getUTCFullYear() == today.getFullYear() &&
					prevMonth.getUTCMonth() == today.getMonth() &&
					prevMonth.getUTCDate() == today.getDate()) {
					clsName += ' today';
				}
				if (prevMonth.valueOf() == currentDate) {
					clsName += ' active';
				}
				if ((prevMonth.valueOf() + 86400000) <= this.startDate || prevMonth.valueOf() > this.endDate ||
					$.inArray(prevMonth.getUTCDay(), this.daysOfWeekDisabled) !== -1) {
					clsName += ' disabled';
				}
				//by murmur 2014.1.14
				if (this.beforeShowDay !== $.noop){
					var before = this.beforeShowDay(prevMonth);
					if (before === undefined)
						before = {};
					else if (typeof(before) === 'boolean')
						before = {enabled: before};
					else if (typeof(before) === 'string')
						before = {classes: before};
					if (before.enabled === false)
						clsName += 'disabled';
					if (before.classes)
						clsName += " " + before.classes;
					if (before.tooltip)
						tooltip = before.tooltip;
				}
				html.push('<td class="day' + clsName + '">' + prevMonth.getUTCDate() + '</td>');
				if (prevMonth.getUTCDay() == this.weekEnd) {
					html.push('</tr>');
				}
				prevMonth.setUTCDate(prevMonth.getUTCDate() + 1);
			}
			this.picker.find('.datetimepicker-days tbody').empty().append(html.join(''));

			html = [];
			var txt = '', meridian = '', meridianOld = '';
			for (var i = 0; i < 24; i++) {
				var actual = UTCDate(year, month, dayMonth, i);
				clsName = '';
				// We want the previous hour for the startDate
				if ((actual.valueOf() + 3600000) <= this.startDate || actual.valueOf() > this.endDate) {
					clsName += ' disabled';
				} else if (hours == i) {
					clsName += ' active';
				}
				if (this.showMeridian && dates[this.language].meridiem.length == 2) {
					meridian = (i < 12 ? dates[this.language].meridiem[0] : dates[this.language].meridiem[1]);
					if (meridian != meridianOld) {
						if (meridianOld != '') {
							html.push('</fieldset>');
						}
						html.push('<fieldset class="hour"><legend>' + meridian.toUpperCase() + '</legend>');
					}
					meridianOld = meridian;
					txt = (i % 12 ? i % 12 : 12);
					html.push('<span class="hour' + clsName + ' hour_' + (i < 12 ? 'am' : 'pm') + '">' + txt + '</span>');
					if (i == 23) {
						html.push('</fieldset>');
					}
				} else {
					txt = i + ':00';
					html.push('<span class="hour' + clsName + '">' + txt + '</span>');
				}
			}
			this.picker.find('.datetimepicker-hours td').html(html.join(''));

			html = [];
			txt = '', meridian = '', meridianOld = '';
			for (var i = 0; i < 60; i += this.minuteStep) {
				var actual = UTCDate(year, month, dayMonth, hours, i, 0);
				clsName = '';
				if (actual.valueOf() < this.startDate || actual.valueOf() > this.endDate) {
					clsName += ' disabled';
				} else if (Math.floor(minutes / this.minuteStep) == Math.floor(i / this.minuteStep)) {
					clsName += ' active';
				}
				if (this.showMeridian && dates[this.language].meridiem.length == 2) {
					meridian = (hours < 12 ? dates[this.language].meridiem[0] : dates[this.language].meridiem[1]);
					if (meridian != meridianOld) {
						if (meridianOld != '') {
							html.push('</fieldset>');
						}
						html.push('<fieldset class="minute"><legend>' + meridian.toUpperCase() + '</legend>');
					}
					meridianOld = meridian;
					txt = (hours % 12 ? hours % 12 : 12);
					//html.push('<span class="minute'+clsName+' minute_'+(hours<12?'am':'pm')+'">'+txt+'</span>');
					html.push('<span class="minute' + clsName + '">' + txt + ':' + (i < 10 ? '0' + i : i) + '</span>');
					if (i == 59) {
						html.push('</fieldset>');
					}
				} else {
					txt = i + ':00';
					//html.push('<span class="hour'+clsName+'">'+txt+'</span>');
					html.push('<span class="minute' + clsName + '">' + hours + ':' + (i < 10 ? '0' + i : i) + '</span>');
				}
			}
			this.picker.find('.datetimepicker-minutes td').html(html.join(''));

			var currentYear = this.date.getUTCFullYear();
			var months = this.picker.find('.datetimepicker-months')
				.find('th:eq(1)')
				.text(year)
				.end()
				.find('span').removeClass('active');
			if (currentYear == year) {
				months.eq(this.date.getUTCMonth()).addClass('active');
			}
			if (year < startYear || year > endYear) {
				months.addClass('disabled');
			}
			if (year == startYear) {
				months.slice(0, startMonth).addClass('disabled');
			}
			if (year == endYear) {
				months.slice(endMonth + 1).addClass('disabled');
			}

			html = '';
			year = parseInt(year / 10, 10) * 10;
			var yearCont = this.picker.find('.datetimepicker-years')
				.find('th:eq(1)')
				.text(year + '-' + (year + 9))
				.end()
				.find('td');
			year -= 1;
			for (var i = -1; i < 11; i++) {
				html += '<span class="year' + (i == -1 || i == 10 ? ' old' : '') + (currentYear == year ? ' active' : '') + (year < startYear || year > endYear ? ' disabled' : '') + '">' + year + '</span>';
				year += 1;
			}
			yearCont.html(html);
			this.place();
			this.element.trigger({type:"dateFill","date":d});
		},

		updateNavArrows: function () {
			var d = new Date(this.viewDate),
				year = d.getUTCFullYear(),
				month = d.getUTCMonth(),
				day = d.getUTCDate(),
				hour = d.getUTCHours();
			switch (this.viewMode) {
				case 0:
					if (this.startDate !== -Infinity && year <= this.startDate.getUTCFullYear()
						&& month <= this.startDate.getUTCMonth()
						&& day <= this.startDate.getUTCDate()
						&& hour <= this.startDate.getUTCHours()) {
						this.picker.find('.prev').css({visibility: 'hidden'});
					} else {
						this.picker.find('.prev').css({visibility: 'visible'});
					}
					if (this.endDate !== Infinity && year >= this.endDate.getUTCFullYear()
						&& month >= this.endDate.getUTCMonth()
						&& day >= this.endDate.getUTCDate()
						&& hour >= this.endDate.getUTCHours()) {
						this.picker.find('.next').css({visibility: 'hidden'});
					} else {
						this.picker.find('.next').css({visibility: 'visible'});
					}
					break;
				case 1:
					if (this.startDate !== -Infinity && year <= this.startDate.getUTCFullYear()
						&& month <= this.startDate.getUTCMonth()
						&& day <= this.startDate.getUTCDate()) {
						this.picker.find('.prev').css({visibility: 'hidden'});
					} else {
						this.picker.find('.prev').css({visibility: 'visible'});
					}
					if (this.endDate !== Infinity && year >= this.endDate.getUTCFullYear()
						&& month >= this.endDate.getUTCMonth()
						&& day >= this.endDate.getUTCDate()) {
						this.picker.find('.next').css({visibility: 'hidden'});
					} else {
						this.picker.find('.next').css({visibility: 'visible'});
					}
					break;
				case 2:
					if (this.startDate !== -Infinity && year <= this.startDate.getUTCFullYear()
						&& month <= this.startDate.getUTCMonth()) {
						this.picker.find('.prev').css({visibility: 'hidden'});
					} else {
						this.picker.find('.prev').css({visibility: 'visible'});
					}
					if (this.endDate !== Infinity && year >= this.endDate.getUTCFullYear()
						&& month >= this.endDate.getUTCMonth()) {
						this.picker.find('.next').css({visibility: 'hidden'});
					} else {
						this.picker.find('.next').css({visibility: 'visible'});
					}
					break;
				case 3:
				case 4:
					if (this.startDate !== -Infinity && year <= this.startDate.getUTCFullYear()) {
						this.picker.find('.prev').css({visibility: 'hidden'});
					} else {
						this.picker.find('.prev').css({visibility: 'visible'});
					}
					if (this.endDate !== Infinity && year >= this.endDate.getUTCFullYear()) {
						this.picker.find('.next').css({visibility: 'hidden'});
					} else {
						this.picker.find('.next').css({visibility: 'visible'});
					}
					break;
			}
		},

		mousewheel: function (e) {

			e.preventDefault();
			e.stopPropagation();

			if (this.wheelPause) {
				return;
			}

			this.wheelPause = true;

			var originalEvent = e.originalEvent;

			var delta = originalEvent.wheelDelta;

			var mode = delta > 0 ? 1 : (delta === 0) ? 0 : -1;

			if (this.wheelViewModeNavigationInverseDirection) {
				mode = -mode;
			}

			this.showMode(mode);

			setTimeout($.proxy(function () {

				this.wheelPause = false

			}, this), this.wheelViewModeNavigationDelay);

		},

		click: function (e) {
			e.stopPropagation();
			e.preventDefault();
			var target = $(e.target).closest('span, td, th, legend');
			if (target.length == 1) {
				if (target.is('.disabled')) {
					this.element.trigger({
						type:      'outOfRange',
						date:      this.viewDate,
						startDate: this.startDate,
						endDate:   this.endDate
					});
					return;
				}
				switch (target[0].nodeName.toLowerCase()) {
					case 'th':
						switch (target[0].className) {
							case 'switch':
								this.showMode(1);
								break;
							case 'prev':
							case 'next':
								var dir = DPGlobal.modes[this.viewMode].navStep * (target[0].className == 'prev' ? -1 : 1);
								switch (this.viewMode) {
									case 0:
										this.viewDate = this.moveHour(this.viewDate, dir);
										break;
									case 1:
										this.viewDate = this.moveDate(this.viewDate, dir);
										break;
									case 2:
										this.viewDate = this.moveMonth(this.viewDate, dir);
										break;
									case 3:
									case 4:
										this.viewDate = this.moveYear(this.viewDate, dir);
										break;
								}
								this.fill();
								break;
							case 'today':
								var date = new Date();
								date = UTCDate(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds(), 0);

								// Respect startDate and endDate.
								if (date < this.startDate) date = this.startDate;
								else if (date > this.endDate) date = this.endDate;

								this.viewMode = this.startViewMode;
								this.showMode(0);
								this._setDate(date);
								this.fill();
								if (this.autoclose) {
									this.hide();
								}
								break;
						}
						break;
					case 'span':
						if (!target.is('.disabled')) {
							var year = this.viewDate.getUTCFullYear(),
								month = this.viewDate.getUTCMonth(),
								day = this.viewDate.getUTCDate(),
								hours = this.viewDate.getUTCHours(),
								minutes = this.viewDate.getUTCMinutes(),
								seconds = this.viewDate.getUTCSeconds();

							if (target.is('.month')) {
								this.viewDate.setUTCDate(1);
								month = target.parent().find('span').index(target);
								day = this.viewDate.getUTCDate();
								this.viewDate.setUTCMonth(month);
								this.element.trigger({
									type: 'changeMonth',
									date: this.viewDate
								});
								if (this.viewSelect >= 3) {
									this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
								}
							} else if (target.is('.year')) {
								this.viewDate.setUTCDate(1);
								year = parseInt(target.text(), 10) || 0;
								this.viewDate.setUTCFullYear(year);
								this.element.trigger({
									type: 'changeYear',
									date: this.viewDate
								});
								if (this.viewSelect >= 4) {
									this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
								}
							} else if (target.is('.hour')) {
								hours = parseInt(target.text(), 10) || 0;
								if (target.hasClass('hour_am') || target.hasClass('hour_pm')) {
									if (hours == 12 && target.hasClass('hour_am')) {
										hours = 0;
									} else if (hours != 12 && target.hasClass('hour_pm')) {
										hours += 12;
									}
								}
								this.viewDate.setUTCHours(hours);
								this.element.trigger({
									type: 'changeHour',
									date: this.viewDate
								});
								if (this.viewSelect >= 1) {
									this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
								}
							} else if (target.is('.minute')) {
								minutes = parseInt(target.text().substr(target.text().indexOf(':') + 1), 10) || 0;
								this.viewDate.setUTCMinutes(minutes);
								this.element.trigger({
									type: 'changeMinute',
									date: this.viewDate
								});
								if (this.viewSelect >= 0) {
									this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
								}
							}
							if (this.viewMode != 0) {
								var oldViewMode = this.viewMode;
								this.showMode(-1);
								this.fill();
								if (oldViewMode == this.viewMode && this.autoclose) {
									this.hide();
								}
							} else {
								this.fill();
								if (this.autoclose) {
									this.hide();
								}
							}
						}
						break;
					case 'td':
						if (target.is('.day') && !target.is('.disabled')) {
							var day = parseInt(target.text(), 10) || 1;
							var year = this.viewDate.getUTCFullYear(),
								month = this.viewDate.getUTCMonth(),
								hours = this.viewDate.getUTCHours(),
								minutes = this.viewDate.getUTCMinutes(),
								seconds = this.viewDate.getUTCSeconds();
							if (target.is('.old')) {
								if (month === 0) {
									month = 11;
									year -= 1;
								} else {
									month -= 1;
								}
							} else if (target.is('.new')) {
								if (month == 11) {
									month = 0;
									year += 1;
								} else {
									month += 1;
								}
							}
							this.viewDate.setUTCFullYear(year);
							this.viewDate.setUTCMonth(month, day);
							this.element.trigger({
								type: 'changeDay',
								date: this.viewDate
							});
							if (this.viewSelect >= 2) {
								this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
							}
						}
						var oldViewMode = this.viewMode;
						this.showMode(-1);
						this.fill();
						if (oldViewMode == this.viewMode && this.autoclose) {
							this.hide();
						}
						break;
				}
			}
		},

		_setDate: function (date, which) {
			if (!which || which == 'date')
				this.date = date;
			if (!which || which == 'view')
				this.viewDate = date;
			this.fill();
			this.setValue();
			var element;
			if (this.isInput) {
				element = this.element;
			} else if (this.component) {
				element = this.element.find('input');
			}
			if (element) {
				element.change();
				if (this.autoclose && (!which || which == 'date')) {
					//this.hide();
				}
			}
			this.element.trigger({
				type: 'changeDate',
				date: this.date
			});
		},

		moveMinute: function (date, dir) {
			if (!dir) return date;
			var new_date = new Date(date.valueOf());
			//dir = dir > 0 ? 1 : -1;
			new_date.setUTCMinutes(new_date.getUTCMinutes() + (dir * this.minuteStep));
			return new_date;
		},

		moveHour: function (date, dir) {
			if (!dir) return date;
			var new_date = new Date(date.valueOf());
			//dir = dir > 0 ? 1 : -1;
			new_date.setUTCHours(new_date.getUTCHours() + dir);
			return new_date;
		},

		moveDate: function (date, dir) {
			if (!dir) return date;
			var new_date = new Date(date.valueOf());
			//dir = dir > 0 ? 1 : -1;
			new_date.setUTCDate(new_date.getUTCDate() + dir);
			return new_date;
		},

		moveMonth: function (date, dir) {
			if (!dir) return date;
			var new_date = new Date(date.valueOf()),
				day = new_date.getUTCDate(),
				month = new_date.getUTCMonth(),
				mag = Math.abs(dir),
				new_month, test;
			dir = dir > 0 ? 1 : -1;
			if (mag == 1) {
				test = dir == -1
					// If going back one month, make sure month is not current month
					// (eg, Mar 31 -> Feb 31 == Feb 28, not Mar 02)
					? function () {
					return new_date.getUTCMonth() == month;
				}
					// If going forward one month, make sure month is as expected
					// (eg, Jan 31 -> Feb 31 == Feb 28, not Mar 02)
					: function () {
					return new_date.getUTCMonth() != new_month;
				};
				new_month = month + dir;
				new_date.setUTCMonth(new_month);
				// Dec -> Jan (12) or Jan -> Dec (-1) -- limit expected date to 0-11
				if (new_month < 0 || new_month > 11)
					new_month = (new_month + 12) % 12;
			} else {
				// For magnitudes >1, move one month at a time...
				for (var i = 0; i < mag; i++)
					// ...which might decrease the day (eg, Jan 31 to Feb 28, etc)...
					new_date = this.moveMonth(new_date, dir);
				// ...then reset the day, keeping it in the new month
				new_month = new_date.getUTCMonth();
				new_date.setUTCDate(day);
				test = function () {
					return new_month != new_date.getUTCMonth();
				};
			}
			// Common date-resetting loop -- if date is beyond end of month, make it
			// end of month
			while (test()) {
				new_date.setUTCDate(--day);
				new_date.setUTCMonth(new_month);
			}
			return new_date;
		},

		moveYear: function (date, dir) {
			return this.moveMonth(date, dir * 12);
		},

		dateWithinRange: function (date) {
			return date >= this.startDate && date <= this.endDate;
		},

		keydown: function (e) {
			if (this.picker.is(':not(:visible)')) {
				if (e.keyCode == 27) // allow escape to hide and re-show picker
					this.show();
				return;
			}
			var dateChanged = false,
				dir, day, month,
				newDate, newViewDate;
			switch (e.keyCode) {
				case 27: // escape
					this.hide();
					e.preventDefault();
					break;
				case 37: // left
				case 39: // right
					if (!this.keyboardNavigation) break;
					dir = e.keyCode == 37 ? -1 : 1;
					viewMode = this.viewMode;
					if (e.ctrlKey) {
						viewMode += 2;
					} else if (e.shiftKey) {
						viewMode += 1;
					}
					if (viewMode == 4) {
						newDate = this.moveYear(this.date, dir);
						newViewDate = this.moveYear(this.viewDate, dir);
					} else if (viewMode == 3) {
						newDate = this.moveMonth(this.date, dir);
						newViewDate = this.moveMonth(this.viewDate, dir);
					} else if (viewMode == 2) {
						newDate = this.moveDate(this.date, dir);
						newViewDate = this.moveDate(this.viewDate, dir);
					} else if (viewMode == 1) {
						newDate = this.moveHour(this.date, dir);
						newViewDate = this.moveHour(this.viewDate, dir);
					} else if (viewMode == 0) {
						newDate = this.moveMinute(this.date, dir);
						newViewDate = this.moveMinute(this.viewDate, dir);
					}
					if (this.dateWithinRange(newDate)) {
						this.date = newDate;
						this.viewDate = newViewDate;
						this.setValue();
						this.update();
						e.preventDefault();
						dateChanged = true;
					}
					break;
				case 38: // up
				case 40: // down
					if (!this.keyboardNavigation) break;
					dir = e.keyCode == 38 ? -1 : 1;
					viewMode = this.viewMode;
					if (e.ctrlKey) {
						viewMode += 2;
					} else if (e.shiftKey) {
						viewMode += 1;
					}
					if (viewMode == 4) {
						newDate = this.moveYear(this.date, dir);
						newViewDate = this.moveYear(this.viewDate, dir);
					} else if (viewMode == 3) {
						newDate = this.moveMonth(this.date, dir);
						newViewDate = this.moveMonth(this.viewDate, dir);
					} else if (viewMode == 2) {
						newDate = this.moveDate(this.date, dir * 7);
						newViewDate = this.moveDate(this.viewDate, dir * 7);
					} else if (viewMode == 1) {
						if (this.showMeridian) {
							newDate = this.moveHour(this.date, dir * 6);
							newViewDate = this.moveHour(this.viewDate, dir * 6);
						} else {
							newDate = this.moveHour(this.date, dir * 4);
							newViewDate = this.moveHour(this.viewDate, dir * 4);
						}
					} else if (viewMode == 0) {
						newDate = this.moveMinute(this.date, dir * 4);
						newViewDate = this.moveMinute(this.viewDate, dir * 4);
					}
					if (this.dateWithinRange(newDate)) {
						this.date = newDate;
						this.viewDate = newViewDate;
						this.setValue();
						this.update();
						e.preventDefault();
						dateChanged = true;
					}
					break;
				case 13: // enter
					if (this.viewMode != 0) {
						var oldViewMode = this.viewMode;
						this.showMode(-1);
						this.fill();
						if (oldViewMode == this.viewMode && this.autoclose) {
							this.hide();
						}
					} else {
						this.fill();
						if (this.autoclose) {
							this.hide();
						}
					}
					e.preventDefault();
					break;
				case 9: // tab
					this.hide();
					break;
			}
			if (dateChanged) {
				var element;
				if (this.isInput) {
					element = this.element;
				} else if (this.component) {
					element = this.element.find('input');
				}
				if (element) {
					element.change();
				}
				this.element.trigger({
					type: 'changeDate',
					date: this.date
				});
			}
		},

		showMode: function (dir) {
			if (dir) {
				var newViewMode = Math.max(0, Math.min(DPGlobal.modes.length - 1, this.viewMode + dir));
				if (newViewMode >= this.minView && newViewMode <= this.maxView) {
					this.element.trigger({
						type:        'changeMode',
						date:        this.viewDate,
						oldViewMode: this.viewMode,
						newViewMode: newViewMode
					});

					this.viewMode = newViewMode;
				}
			}
			/*
			 vitalets: fixing bug of very special conditions:
			 jquery 1.7.1 + webkit + show inline datetimepicker in bootstrap popover.
			 Method show() does not set display css correctly and datetimepicker is not shown.
			 Changed to .css('display', 'block') solve the problem.
			 See https://github.com/vitalets/x-editable/issues/37

			 In jquery 1.7.2+ everything works fine.
			 */
			//this.picker.find('>div').hide().filter('.datetimepicker-'+DPGlobal.modes[this.viewMode].clsName).show();
			this.picker.find('>div').hide().filter('.datetimepicker-' + DPGlobal.modes[this.viewMode].clsName).css('display', 'block');
			this.updateNavArrows();
		},

		reset: function (e) {
			this._setDate(null, 'date');
		}
	};

	$.fn.datetimepicker = function (option) {
		var args = Array.apply(null, arguments);
		args.shift();
		return this.each(function () {
			var $this = $(this),
				data = $this.data('datetimepicker'),
				options = typeof option == 'object' && option;
			if (!data) {
				$this.data('datetimepicker', (data = new Datetimepicker(this, $.extend({}, $.fn.datetimepicker.defaults, options))));
			}
			if (typeof option == 'string' && typeof data[option] == 'function') {
				data[option].apply(data, args);
			}
		});
	};

	$.fn.datetimepicker.defaults = {
	};
	$.fn.datetimepicker.Constructor = Datetimepicker;
	var dates = $.fn.datetimepicker.dates = {
		en: {
			days:        ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
			daysShort:   ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
			daysMin:     ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"],
			months:      ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
			monthsShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
			meridiem:    ["am", "pm"],
			suffix:      ["st", "nd", "rd", "th"],
			today:       "Today"
		},
		ch: {
			days:        ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"],
			daysShort:   ["日", "一", "二", "三", "四", "五", "六", "日"],
			daysMin:     ["日", "一", "二", "三", "四", "五", "六", "日"],
			months:      ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
			monthsShort: ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],
			meridiem:    ["上午", "下午"],
			suffix:      ["st", "nd", "rd", "th"],
			today:       "今天"
		}
	};

	var DPGlobal = {
		modes:            [
			{
				clsName: 'minutes',
				navFnc:  'Hours',
				navStep: 1
			},
			{
				clsName: 'hours',
				navFnc:  'Date',
				navStep: 1
			},
			{
				clsName: 'days',
				navFnc:  'Month',
				navStep: 1
			},
			{
				clsName: 'months',
				navFnc:  'FullYear',
				navStep: 1
			},
			{
				clsName: 'years',
				navFnc:  'FullYear',
				navStep: 10
			}
		],
		isLeapYear:       function (year) {
			return (((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0))
		},
		getDaysInMonth:   function (year, month) {
			return [31, (DPGlobal.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month]
		},
		getDefaultFormat: function (type, field) {
			if (type == "standard") {
				if (field == 'input')
					return 'yyyy-mm-dd hh:ii';
				else
					return 'yyyy-mm-dd hh:ii:ss';
			} else if (type == "php") {
				if (field == 'input')
					return 'Y-m-d H:i';
				else
					return 'Y-m-d H:i:s';
			} else {
				throw new Error("Invalid format type.");
			}
		},
		validParts:       function (type) {
			if (type == "standard") {
				return /hh?|HH?|p|P|ii?|ss?|dd?|DD?|mm?|MM?|yy(?:yy)?/g;
			} else if (type == "php") {
				return /[dDjlNwzFmMnStyYaABgGhHis]/g;
			} else {
				throw new Error("Invalid format type.");
			}
		},
		nonpunctuation:   /[^ -\/:-@\[-`{-~\t\n\rTZ]+/g,
		parseFormat:      function (format, type) {
			// IE treats \0 as a string end in inputs (truncating the value),
			// so it's a bad format delimiter, anyway
			var separators = format.replace(this.validParts(type), '\0').split('\0'),
				parts = format.match(this.validParts(type));
			if (!separators || !separators.length || !parts || parts.length == 0) {
				throw new Error("Invalid date format.");
			}
			return {separators: separators, parts: parts};
		},
		parseDate:        function (date, format, language, type) {
			if (date instanceof Date) {
				var dateUTC = new Date(date.valueOf() - date.getTimezoneOffset() * 60000);
				dateUTC.setMilliseconds(0);
				return dateUTC;
			}
			if (/^\d{4}\-\d{1,2}\-\d{1,2}$/.test(date)) {
				format = this.parseFormat('yyyy-mm-dd', type);
			}
			if (/^\d{4}\-\d{1,2}\-\d{1,2}[T ]\d{1,2}\:\d{1,2}$/.test(date)) {
				format = this.parseFormat('yyyy-mm-dd hh:ii', type);
			}
			if (/^\d{4}\-\d{1,2}\-\d{1,2}[T ]\d{1,2}\:\d{1,2}\:\d{1,2}[Z]{0,1}$/.test(date)) {
				format = this.parseFormat('yyyy-mm-dd hh:ii:ss', type);
			}
			if (/^[-+]\d+[dmwy]([\s,]+[-+]\d+[dmwy])*$/.test(date)) {
				var part_re = /([-+]\d+)([dmwy])/,
					parts = date.match(/([-+]\d+)([dmwy])/g),
					part, dir;
				date = new Date();
				for (var i = 0; i < parts.length; i++) {
					part = part_re.exec(parts[i]);
					dir = parseInt(part[1]);
					switch (part[2]) {
						case 'd':
							date.setUTCDate(date.getUTCDate() + dir);
							break;
						case 'm':
							date = Datetimepicker.prototype.moveMonth.call(Datetimepicker.prototype, date, dir);
							break;
						case 'w':
							date.setUTCDate(date.getUTCDate() + dir * 7);
							break;
						case 'y':
							date = Datetimepicker.prototype.moveYear.call(Datetimepicker.prototype, date, dir);
							break;
					}
				}
				return UTCDate(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds(), 0);
			}
			var parts = date && date.match(this.nonpunctuation) || [],
				date = new Date(0, 0, 0, 0, 0, 0, 0),
				parsed = {},
				setters_order = ['hh', 'h', 'ii', 'i', 'ss', 's', 'yyyy', 'yy', 'M', 'MM', 'm', 'mm', 'D', 'DD', 'd', 'dd', 'H', 'HH', 'p', 'P'],
				setters_map = {
					hh:   function (d, v) {
						return d.setUTCHours(v);
					},
					h:    function (d, v) {
						return d.setUTCHours(v);
					},
					HH:   function (d, v) {
						return d.setUTCHours(v == 12 ? 0 : v);
					},
					H:    function (d, v) {
						return d.setUTCHours(v == 12 ? 0 : v);
					},
					ii:   function (d, v) {
						return d.setUTCMinutes(v);
					},
					i:    function (d, v) {
						return d.setUTCMinutes(v);
					},
					ss:   function (d, v) {
						return d.setUTCSeconds(v);
					},
					s:    function (d, v) {
						return d.setUTCSeconds(v);
					},
					yyyy: function (d, v) {
						return d.setUTCFullYear(v);
					},
					yy:   function (d, v) {
						return d.setUTCFullYear(2000 + v);
					},
					m:    function (d, v) {
						v -= 1;
						while (v < 0) v += 12;
						v %= 12;
						d.setUTCMonth(v);
						while (d.getUTCMonth() != v)
							d.setUTCDate(d.getUTCDate() - 1);
						return d;
					},
					d:    function (d, v) {
						return d.setUTCDate(v);
					},
					p:    function (d, v) {
						return d.setUTCHours(v == 1 ? d.getUTCHours() + 12 : d.getUTCHours());
					}
				},
				val, filtered, part;
			setters_map['M'] = setters_map['MM'] = setters_map['mm'] = setters_map['m'];
			setters_map['dd'] = setters_map['d'];
			setters_map['P'] = setters_map['p'];
			date = UTCDate(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
			if (parts.length == format.parts.length) {
				for (var i = 0, cnt = format.parts.length; i < cnt; i++) {
					val = parseInt(parts[i], 10);
					part = format.parts[i];
					if (isNaN(val)) {
						switch (part) {
							case 'MM':
								filtered = $(dates[language].months).filter(function () {
									var m = this.slice(0, parts[i].length),
										p = parts[i].slice(0, m.length);
									return m == p;
								});
								val = $.inArray(filtered[0], dates[language].months) + 1;
								break;
							case 'M':
								filtered = $(dates[language].monthsShort).filter(function () {
									var m = this.slice(0, parts[i].length),
										p = parts[i].slice(0, m.length);
									return m == p;
								});
								val = $.inArray(filtered[0], dates[language].monthsShort) + 1;
								break;
							case 'p':
							case 'P':
								val = $.inArray(parts[i].toLowerCase(), dates[language].meridiem);
								break;
						}
					}
					parsed[part] = val;
				}
				for (var i = 0, s; i < setters_order.length; i++) {
					s = setters_order[i];
					if (s in parsed && !isNaN(parsed[s]))
						setters_map[s](date, parsed[s])
				}
			}
			return date;
		},
		formatDate:       function (date, format, language, type) {
			if (date == null) {
				return '';
			}
			var val;
			if (type == 'standard') {
				val = {
					// year
					yy:   date.getUTCFullYear().toString().substring(2),
					yyyy: date.getUTCFullYear(),
					// month
					m:    date.getUTCMonth() + 1,
					M:    dates[language].monthsShort[date.getUTCMonth()],
					MM:   dates[language].months[date.getUTCMonth()],
					// day
					d:    date.getUTCDate(),
					D:    dates[language].daysShort[date.getUTCDay()],
					DD:   dates[language].days[date.getUTCDay()],
					p:    (dates[language].meridiem.length == 2 ? dates[language].meridiem[date.getUTCHours() < 12 ? 0 : 1] : ''),
					// hour
					h:    date.getUTCHours(),
					// minute
					i:    date.getUTCMinutes(),
					// second
					s:    date.getUTCSeconds()
				};

				if (dates[language].meridiem.length == 2) {
					val.H = (val.h % 12 == 0 ? 12 : val.h % 12);
				}
				else {
					val.H = val.h;
				}
				val.HH = (val.H < 10 ? '0' : '') + val.H;
				val.P = val.p.toUpperCase();
				val.hh = (val.h < 10 ? '0' : '') + val.h;
				val.ii = (val.i < 10 ? '0' : '') + val.i;
				val.ss = (val.s < 10 ? '0' : '') + val.s;
				val.dd = (val.d < 10 ? '0' : '') + val.d;
				val.mm = (val.m < 10 ? '0' : '') + val.m;
			} else if (type == 'php') {
				// php format
				val = {
					// year
					y: date.getUTCFullYear().toString().substring(2),
					Y: date.getUTCFullYear(),
					// month
					F: dates[language].months[date.getUTCMonth()],
					M: dates[language].monthsShort[date.getUTCMonth()],
					n: date.getUTCMonth() + 1,
					t: DPGlobal.getDaysInMonth(date.getUTCFullYear(), date.getUTCMonth()),
					// day
					j: date.getUTCDate(),
					l: dates[language].days[date.getUTCDay()],
					D: dates[language].daysShort[date.getUTCDay()],
					w: date.getUTCDay(), // 0 -> 6
					N: (date.getUTCDay() == 0 ? 7 : date.getUTCDay()),       // 1 -> 7
					S: (date.getUTCDate() % 10 <= dates[language].suffix.length ? dates[language].suffix[date.getUTCDate() % 10 - 1] : ''),
					// hour
					a: (dates[language].meridiem.length == 2 ? dates[language].meridiem[date.getUTCHours() < 12 ? 0 : 1] : ''),
					g: (date.getUTCHours() % 12 == 0 ? 12 : date.getUTCHours() % 12),
					G: date.getUTCHours(),
					// minute
					i: date.getUTCMinutes(),
					// second
					s: date.getUTCSeconds()
				};
				val.m = (val.n < 10 ? '0' : '') + val.n;
				val.d = (val.j < 10 ? '0' : '') + val.j;
				val.A = val.a.toString().toUpperCase();
				val.h = (val.g < 10 ? '0' : '') + val.g;
				val.H = (val.G < 10 ? '0' : '') + val.G;
				val.i = (val.i < 10 ? '0' : '') + val.i;
				val.s = (val.s < 10 ? '0' : '') + val.s;
			} else {
				throw new Error("Invalid format type.");
			}
			var date = [],
				seps = $.extend([], format.separators);
			for (var i = 0, cnt = format.parts.length; i < cnt; i++) {
				if (seps.length) {
					date.push(seps.shift());
				}
				date.push(val[format.parts[i]]);
			}
			if (seps.length) {
				date.push(seps.shift());
			}
			return date.join('');
		},
		convertViewMode:  function (viewMode) {
			switch (viewMode) {
				case 4:
				case 'decade':
					viewMode = 4;
					break;
				case 3:
				case 'year':
					viewMode = 3;
					break;
				case 2:
				case 'month':
					viewMode = 2;
					break;
				case 1:
				case 'day':
					viewMode = 1;
					break;
				case 0:
				case 'hour':
					viewMode = 0;
					break;
			}

			return viewMode;
		},
		headTemplate:     '<thead>' +
							  '<tr class="lx_th1">' +
							  '<th class="prev">&lt;&lt;</th>' +
							  '<th colspan="5" class="switch"></th>' +
							  '<th class="next">&gt;&gt;</th>' +
							  '</tr>' +
			'</thead>',
		headTemplateV3:   '<thead>' +
							  '<tr class="lx_th1">' +
							  '<th class="prev">&lt;&lt; </th>' +
							  '<th colspan="5" class="switch"></th>' +
							  '<th class="next">&gt;&gt;</th>' +
							  '</tr>' +
			'</thead>',
		contTemplate:     '<tbody><tr><td colspan="7"></td></tr></tbody>',
		footTemplate:     '<tfoot><tr><th colspan="7" class="today"></th></tr></tfoot>'
	};
	DPGlobal.template = '<div class="datetimepicker">' +
		'<div class="datetimepicker-minutes">' +
		'<table class=" table-condensed">' +
		DPGlobal.headTemplate +
		DPGlobal.contTemplate +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'<div class="datetimepicker-hours">' +
		'<table class=" table-condensed">' +
		DPGlobal.headTemplate +
		DPGlobal.contTemplate +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'<div class="datetimepicker-days">' +
		'<table class=" table-condensed">' +
		DPGlobal.headTemplate +
		'<tbody></tbody>' +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'<div class="datetimepicker-months">' +
		'<table class="table-condensed">' +
		DPGlobal.headTemplate +
		DPGlobal.contTemplate +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'<div class="datetimepicker-years">' +
		'<table class="table-condensed">' +
		DPGlobal.headTemplate +
		DPGlobal.contTemplate +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'</div>';
	DPGlobal.templateV3 = '<div class="datetimepicker">' +
		'<div class="datetimepicker-minutes">' +
		'<table class=" table-condensed">' +
		DPGlobal.headTemplateV3 +
		DPGlobal.contTemplate +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'<div class="datetimepicker-hours">' +
		'<table class=" table-condensed">' +
		DPGlobal.headTemplateV3 +
		DPGlobal.contTemplate +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'<div class="datetimepicker-days">' +
		'<table class=" table-condensed">' +
		DPGlobal.headTemplateV3 +
		'<tbody></tbody>' +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'<div class="datetimepicker-months">' +
		'<table class="table-condensed">' +
		DPGlobal.headTemplateV3 +
		DPGlobal.contTemplate +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'<div class="datetimepicker-years">' +
		'<table class="table-condensed">' +
		DPGlobal.headTemplateV3 +
		DPGlobal.contTemplate +
		DPGlobal.footTemplate +
		'</table>' +
		'</div>' +
		'</div>';
	$.fn.datetimepicker.DPGlobal = DPGlobal;

	/* DATETIMEPICKER NO CONFLICT
	 * =================== */

	$.fn.datetimepicker.noConflict = function () {
		$.fn.datetimepicker = old;
		return this;
	};

	/* DATETIMEPICKER DATA-API
	 * ================== */

	$(document).on(
		'focus.datetimepicker.data-api click.datetimepicker.data-api',
		'[data-provide="datetimepicker"]',
		function (e) {
			var $this = $(this);
			if ($this.data('datetimepicker')) return;
			e.preventDefault();
			// component click requires us to explicitly show it
			$this.datetimepicker('show');
		}
	);
	$(function () {
		$('[data-provide="datetimepicker-inline"]').datetimepicker();
	});

}(window.jQuery);

/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_dgfix.js
*/
(function($){
	$.fn.extend({		
		/**
		 * 具有延迟加载功能的日期选择器
		 * @param optTextBox 控制文本框的选项，包括图标等，用于初始化ITCUI_Input
		 * @param optDatePicker 控制日期选择器的选项
		 * 
		 * 这两个选项都可以为空
		 */
		ITCUI_LazyLoadPicker : function(optTextBox,optDatePicker){
			var _this = $(this);
			var optText = optTextBox || {};
			var optPicker = optDatePicker || {};
			optPicker.language = 'ch';
			optPicker.autoclose = optPicker.autoclose || true;
			optPicker.forceParse = true;
			var placeHolder = optText.placeholder || "";
			optText.inputId = optText.inputId || _this.attr("input-id") || ("itc_dp" + $(".itcui_btn_calander").length);
			var nameAttr = optText.inputName || _this.attr("input-name") || null;
			var nameStr = nameAttr?" name='" + nameAttr + "'":"";
			_this.addClass("input-group input-group-sm");
			//valid-field样式会触发onblur的自动验证 这个目前由自动表单加的外层样式控制
			var clsStr = _this.hasClass("valid-field-wrap")?"valid-field":"";
			$("<input" + nameStr + " id='" + optText.inputId + "' type='text' icon='itcui_btn_calander' class='" + clsStr + " ' style='width:10px'></input>").attr("placeholder",placeHolder).appendTo(_this);
			var ipt = $(_this.children(":input"));
			ipt.ITCUI_Input();
			var icon = ipt.next("span");
			_this.children("input").data("opts",optPicker);
			_this.children("input").data("inited",false);
			_this.children("input").click(function(){
				var __this = $(this);
				var inited = __this.data("inited");
				var opts = __this.data("opts");
				if(!inited){
					__this.data("inited",true);
					__this.datetimepicker(opts).datetimepicker("show");
				}
			});
			//保证点图标也能弹出来
			icon.click(function(e){
				var __this = $(this);
				var ipt = __this.prev("input");
				var inited = ipt.data("inited");
				if(!inited){
					var opts = ipt.data("opts");
					ipt.datetimepicker(opts).datetimepicker("show");
					ipt.data("inited",true);
				}
				else{
					ipt.datetimepicker("show");
				}
			});
		}
	});
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_notify.js
*/
var Notice = {};
/**
*	弹出提示（兼容乐学的参数顺序）
*	msg - 显示的信息
*	funcs - 点击确认后需要执行的函数
*	arg - 对funcs传入的参数
*	icon - 图标，默认为info，可以选alert,error
*/
Notice.confirm = function(msg,opts,arg,icon){
	var funcs = typeof(opts)=="function"?opts:opts.onConfirm;
	var cancelFunc = (typeof(opts)=="object" && opts.onCancel)?opts.onCancel:function(){};
	var p = _parent();
	icon = icon || "info"
	if(p.$("#confirmDlg").length==0){
		var cHtml = '<div id="confirmDlg" style="padding-left:20px;padding-top:20px">' +
		'<span class="cfmicon itcui_' + icon + 'mid" style="float:left"></span><span style="width:340px;float:left;font-size:14px;color:#555555;font-weight:bold;line-height:20px;padding-left:10px;" id="confirmMsg"></span><span style="width:340px;float:left;font-size:12px;color:#555555;margin-top:5px;height:40px;line-height:20px;padding-left:10px;" id="confirmMsg2"></span>' +
		'</div>' +
		'<div id="confirmDlgBtn" style="display:none;" class="bbox itcdlg-btns">' +		
		'<div class="btn-group btn-group-sm pull-right">'+
			'<button type="button" class="btn btn-default" id="confirmCancel">取消</button>'+
		'</div>' +
		'<div class="btn-group btn-group-sm pull-right">' +
		'<button type="button" class="btn btn-success" id="confirmOK" style="margin-right:10px">确定</button>'+
		'</div>' +
		'</div>';
		p.$("body").append(cHtml);
	}
	else{
		p.$("#confirmDlg").find('.cfmicon').removeClass("itcui_infomid")
					   .removeClass("itcui_okmid")
					   .removeClass("itcui_errormid")
					   .addClass("itcui_" + icon + "mid");
	}
	if(msg.indexOf("|")<0){
		p.$('#confirmMsg').html(msg);	
		p.$("#confirmMsg2").hide();
	}
	else{
		msgSplit = msg.split("|");
		p.$('#confirmMsg').html(msgSplit[0]);
		p.$("#confirmMsg2").html(msgSplit[1]).show();	
	}
	p.$('#confirmOK').unbind("click").click(function(){
		p.$("#confirmDlg").dialog("close");
		if(funcs){
			funcs(arg);//执行的函数
		}
	});
	p.$('#confirmCancel').unbind("click").click(function(){
		p.$("#confirmDlg").dialog("close");
		if(cancelFunc){
			cancelFunc();
		}
	});
	p.$("#confirmDlg").dialog({
		closed : false,
		closable : false,
		buttons:"#confirmDlgBtn",
		width:450,
		title:' ',
		height:180,	
		modal:true
	});
};


/**
 * 输入框
 * msg  - 提示信息
 * toDo - 确定后执行的函数，参数为输入的值
 */
Notice.input = function(msg,toDo){
	var p = _parent();
	if(p.$("#inputDlg").length==0){
		var cHtml = '<div id="inputDlg" style="padding-left:20px;padding-top:20px">' + 
						'<span style="width:350px;font-size:14px;color:#555555;font-weight:bold;line-height:20px;" id="inputMsg"></span>' +
						'<input style="width:400px" id="inputdlg_val"/>' + 
					'</div>' + 
					'<div id="inputDlgBtn" style="display:none" class="bbox itcdlg-btns">' + 
						'<div class="btn-group btn-group-sm pull-right">'+
							'<button type="button" class="btn btn-default" id="inputCancel">取消</button>'+
						'</div>' +
						'<div class="btn-group btn-group-sm pull-right">' +
						'<button type="button" class="btn btn-success" id="inputOK" style="margin-right:10px">确定</button>'+
						'</div>' +
					'</div>';
		p.$("body").append(cHtml);
	}
	p.$('#inputMsg').html(msg);
	p.$('#inputOK').unbind("click").click(function(){
		p.$("#inputDlg").dialog("close");
		toDo(p.$("#inputdlg_val").val());//执行的函数
	});
	p.$('#inputCancel').unbind("click").click(function(){
		p.$("#inputDlg").dialog("close");
	});
	p.$("#inputDlg").dialog({
		closed : false,
		buttons:"#inputDlgBtn",
		width:450,
		title:' ',
		height:180,	
		modal:true
	});
};

Notice.remark = function(func,opts){
	opts = opts || {};
	title = opts.title || "备注信息";
	opts.value = opts.value || "";
	func = func || function(){};
	var p = _parent();
	if(p.$("#inputDlg").length==0){
		var cHtml = '<div id="remarkDlg" style="overflow:hidden">' + 
						'<textarea style="width:100%;height:100%" id="remarkdlg_val"/>' + 
					'</div>' + 
					'<div id="remarkDlgBtn" style="display:none;" class="bbox itcdlg-btns">' + 
						'<div class="btn-group btn-group-sm pull-right">'+
							'<button type="button" class="btn btn-default" id="remarkCancel">取消</button>'+
						'</div>' +
						'<div class="btn-group btn-group-sm pull-right">' +
						'<button type="button" class="btn btn-success" id="remarkOK" style="margin-right:10px">确定</button>'+
						'</div>' +
					'</div>';
		p.$("body").append(cHtml);
	}
	p.$("#remarkdlg_val").val(opts.value);
	p.$('#remarkOK').unbind("click").click(function(){		
		//执行的函数
		if(!func(p.$("#remarkdlg_val").val())){
			p.$("#remarkDlg").dialog("close");
		}
	});
	p.$('#remarkCancel').unbind("click").click(function(){
		p.$("#remarkDlg").dialog("close");
	});
	p.$("#remarkDlg").dialog({
		closed : false,
		buttons:"#remarkDlgBtn",
		width:495,
		title:title,
		height:180,	
		modal:true
	});
}

Notice.dialog = function(src,_dlgOpts,btnOpts){
	//这里必须深拷一次 否则下面10px的像素修正会多次累加
	_dlgOpts = _dlgOpts || {};	
	var dlgOpts = $.extend({},_dlgOpts);
	dlgOpts.closed = false;
	var p = _parent();
	var suffix = dlgOpts.idSuffix || "";
	if(!dlgOpts.noButtons){
		dlgOpts.buttons = "#itcDlg" + suffix + "Btn";		
	}
	else{
		dlgOpts.buttons = null;
	}
	if(p.$("#itcDlg" + suffix).length==0){
		var dHtml = '<div id="itcDlg' + suffix + '" style="overflow:hidden">' +
						'<iframe style="width:100%;height:100%;" frameborder="no" border="0" id="itcDlg' + suffix + 'Content"></iframe>' + 
					'</div>' + 
					'<div id="itcDlg' + suffix + 'Btn" style="display:none;" class="bbox itcdlg-btns">' +
						'<div id="itcDlg' + suffix + 'BtnWrap" style="height:100%">' + 
						'</div>' + 
					'</div>';
		p.$("body").append(dHtml);
	}else{
		//清理掉默认绑定在div上的函数
		var data = p.$("#itcDlg" + suffix).data();
		for(var k in data){
			for(var kk in data[k].options){
				var f = data[k].options[kk];
				if(kk.indexOf("on") === 0 && typeof(f) == "function"){
					data[k].options[kk] = function(){};
				}
			}
		}
	}
	btnOpts = btnOpts || [];
	FW.set("currentDialog","itcDlg" + suffix + "Content");
	//默认情况下至少得有一个关闭按钮
	if(btnOpts.length == 0){
		btnOpts.push({
			"name" : "关闭",
			"float" : "right",
			"style" : "btn-default",
			"onclick" : function(){
				_parent().$("#itcDlg").dialog("close");
			}
		});
	}
	p.$("#itcDlg" + suffix + "BtnWrap").html("");
	var btnHtml = "";
	//按钮html
	var firstRight = true;
	var firstLeft = true;
	var hasMiddle = false;
	if(!dlgOpts.noButtons){
		for(var i=0;i<btnOpts.length;i++){		
			var opt = btnOpts[i];
			opt["float"] = opt["float"] || "right";
			opt.style = opt.style || "btn-default";
			//根据按钮在左/右侧的编号判断是否需要加间距
			var styleStr = "";
			if(opt["float"]=="left"){
				if(firstLeft==true){
					firstLeft = false;				
				}
				else{
					styleStr = "margin-left:8px;";
				}
			}
			else if(opt["float"]=="right"){
				if(firstRight==true){
					firstRight = false;				
				}
				else{
					styleStr = "margin-right:8px;";
				}
			}
			else{
				opt["float"] = "middle";
				hasMiddle = true;
			}
			var widthStr = "";
			if(opt.width){
				widthStr = " style=\"width:" + opt.width + "px\" ";
			}
			btnHtml += '<div class="btn-group btn-group-sm pull-' + opt["float"] + '" id="itcDlg' + suffix + 'Btn_' + i + '" style="' + styleStr + '">';
			btnHtml += '<button type="button" class="btn ' + opt.style + '" ' + widthStr + '>' + opt.name +'</button>';
			btnHtml += '</div>';		
		}
		p.$("#itcDlg" + suffix + "BtnWrap").append(btnHtml);
		if(hasMiddle){
			//必须有这句才算居中 光pull-middle不够
			p.$("#itcDlg" + suffix + "BtnWrap").css("text-align","center");
		}
	}
	//事件绑定 2016/4/28后改为闭包的形式并将this指针绑定到用户的回调中
	for(var i=0;i<btnOpts.length;i++){		
		var opt = btnOpts[i];        
        var handler = (function(){
            var _onClick = opt.onclick;
            return function(){
                var self = $(this);
                var _self = this;
                if(_onClick.apply(_self)){
                    var id = self.parent().attr('id');
                    var sfx = id.substring(6,id.length-5);
                    _parent().$("#itcDlg" + sfx).dialog("close");
                }
            }  
        })();
		p.$("#itcDlg" + suffix + "Btn_" + i).on("click", "button", handler);
	}
	//在对话框移动和关闭时删除掉combo的层
	dlgOpts._onClose = dlgOpts.onClose || function(){};
	dlgOpts.onClose = function(){
		p.$("#itcDlg" + suffix + "Content").attr("src","about:blank");
		p.$("#itc_combo_wrap").remove();
		p.$(".datetimepicker").remove();
		dlgOpts._onClose();
	};
	dlgOpts.onMove = function(){
		p.$("#itc_combo_wrap").remove();			
		p.$(".datetimepicker").hide();
	};
	p.$("#itcDlg" + suffix).dialog(dlgOpts);
	if(src){
		p.$("#itcDlg" + suffix + "Content").attr("src",src);
	}
};

Notice.screenTopMsg = function(msg,msgtype,parent){
	Notice.screenMsgId = Notice.screenMsgId || null; 
	var p = parent?_parent():window;
	p.$("#itcui_screen_top_msg").remove();
	var scn_width = parseInt(document.documentElement.clientWidth);
	var msg_html = "";
	if(msgtype=="success"){
		msg_html = "<div class='itcui_tips_success";
	}
	else if(msgtype=="error"){
		msg_html = "<div class='itcui_tips_error";
	}
	else if(msgtype=="loading")	{
		msg_html = "<div class='itcui_tips_loading";
	}
	else{
		return;
	}
	msg_html += " itcui_tips_top' style='width:auto' id='itcui_screen_top_msg'>";
	msg_html += msg + "</div>";
	p.$("body").append(msg_html);
	var tmp = p.$("#itcui_screen_top_msg");
	tmp.css("left",(scn_width-tmp.outerWidth())/2 + "px");
	tmp.hide();
	tmp.slideDown();
	if(Notice.screenMsgId){
		clearTimeout(Notice.screenMsgId);
	}
	if(parent){
		Notice.screenMsgId=setTimeout("_parent().$('#itcui_screen_top_msg').slideUp()",5000);
	}
	else{
		Notice.screenMsgId=setTimeout("$('#itcui_screen_top_msg').slideUp()",5000);
	}
};

Notice.successTopNotice = function(msg){
	Notice.screenTopMsg(msg,"success",true);
};

Notice.errorTopNotice = function(msg){
	Notice.screenTopMsg(msg,"error",true);
};

Notice.successNotice = function(msg){
	Notice.screenTopMsg(msg,"success",false);
};

Notice.errorNotice = function(msg){
	Notice.screenTopMsg(msg,"error",false);
};
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_templates.js
*/
var _templates = {};
_templates.pureTable = '<table class="itcui_table" cellspacing="0">\
<tr style="height:24px">\
{{each fields as value}}\
{{if !value.hidden}}\
<th {{if value.width}}width="{{value.width}}"{{/if}}>{{if value.fixed}}<div width="{{value.width}}">{{/if}}{{value.title}}{{if value.fixed}}</div>{{/if}}</th>\
{{/if}}\
{{/each}}\
</tr>\
{{each rows as row}}\
<tr>\
   {{each row as col}}\
   <td>{{if col.width}}<div width="{{col.width}}">{{/if}}{{col.val}}{{if col.width}}</div>{{/if}}</td>\
   {{/each}}\
</tr>\
{{/each}}\
</table>';

_templates.formField = '{{each fields as field}}{{if field}}\
{{if field.lineStart}}<div class="row">{{/if}}\
{{if !field.nouse}}<div class="form-fixfield col-xs-{{field.wrapXsWidth}} col-md-{{field.wrapMdWidth}} type-{{field.type}}" fieldid="{{field.id}}" style="padding-left:{{field.labelFixWidth}}">\
<div style="width:{{field.labelFixWidth}}" class="ctrl-label">\
{{if field.rules && field.rules.required && field.type!=\'label\'}}<span class="form-required">*</span>{{/if}}\
{{field.title}}{{if opts.labelColon}}：{{/if}}</div>\
<div>\
{{#field.el}}\
</div>\
</div>\
{{/if}}\
{{if field.lineEnd}}</div>{{/if}}\
{{/if}}{{/each}}';

_templates.formElement = '<div class="element-wrap col-xs-{{field.inputXsWidth}} col-md-{{field.inputMdWidth}}" {{if field.type==\'switch\'}}style="padding-top:2px"{{/if}}>\
    {{if field.type==\'text\'}}\
       <div class="input-group-sm">\
           <input type="{{if field.dataType==\'password\'}}password{{else}}text{{/if}}" class="form-control" id="{{field._id}}" name="{{field._name}}"/>\
       </div>\
    {{else if field.type==\'textarea\'}}\
       <textarea class="form-control" style="width:100%;height:{{if field.height}}{{field.height}}{{else}}80{{/if}}px" id="{{field._id}}" name="{{field._name}}"></textarea>\
    {{else if field.type==\'checkbox\' || field.type==\'radio\'}}\
       {{each field.data as row}}\
       <div class="pull-left">\
       <input class="autoform_cb" type="{{field.type}}" id="{{field._id}}_{{row[0]}}" name="{{field._name}}" value="{{row[1]}}"{{if row[2]}} checked{{/if}}/>\
       <label class="pure-label" style="margin-right:6px">{{row[1]}}</label>\
       </div>\
       {{/each}}\
    {{else if field.type==\'combobox\'}}\
       <select style="width:100%" id="{{field._id}}" _name="{{field._name}}"></select>\
    {{else if field.type==\'label\'}}\
       <span class="pure-label {{if field.breakAll}}pl-breakall{{else}}pl-ellipsis{{/if}}" id="{{field._id}}"></span>\
    {{else if field.type==\'date\' || field.type==\'datetime\'}}\
       <div class="itc_dtp_wrap" style="width:100%" input-name="{{field._name}}" input-id="{{field._id}}" dtptype="{{field.type}}"></div>\
    {{else if field.type==\'fileupload\'}}\
       <div class="itc_form_upload" style="width:100%" id="{{field._id}}"></div>\
    {{else if field.type ==\'combotree\'}}\
       <select id="{{field._id}}" style="width:100%" treewidth="{{field.options.treeWidth}}" treeheight="{{field.options.treeHeight}}"></select>\
    {{else if field.type ==\'switch\'}}\
       <input type="checkbox" id="{{field._id}}" {{if field.value===field.valueMapping[0]}}checked{{/if}} data-size="mini" data-on-text="{{field.valueMapping[1]}}" data-off-text="{{field.valueMapping[3]}}"/>\
    {{else if field.type ==\'hidden\'}}\
       <input class="ignore"  id="{{field._id}}" name="{{field._name}}" type="hidden"/>\
    {{/if}}\
</div>\
{{if field.type!=\'fileupload\'}}<div class="readonly-wrap col-xs-12 col-md-12">\
<span id="readonly_{{field._id}}" class="pure-label {{if field.breakAll}}pl-breakall{{else}}pl-ellipsis{{/if}}" title="{{field.value}}">{{field.value}}</span>\
</div>{{/if}}';

_templates.comboDropDown = '<div id="itc_combo_wrap" class="itcui_dropdown_menu bbox" style="padding-top:6px;padding-bottom:6px;max-height:250px;overflow-y:auto;width:{{cbWidth}}px;" >\
{{if opts.allowSearch}}\
<div style="width:100%;overflow:hidden;padding:2px">\
<div class="input-group input-group-sm" style="width:100%">\
<input type="text" id="itc_combo_search" placeholder="{{opts.sbPlaceHolder}}" style="width:100%"/>\
</div>\
</div>\
{{/if}}\
{{each dataMapping as item index}}\
{{if !opts.multiselect}}\
<div class="itcui_dropdown_item" style="width:100%" data-value="{{item.value}}" data-index="{{index}}" {{if item.clearSelect}}data-clear=1{{/if}}>\
<span class="itcui_dropdown_text">{{if item._title}}{{#item._title}}{{else}}{{item.title}}{{/if}}</span>\
</div>\
{{else}}\
<div id="itcui_combo_item_{{index}}" class="itcui_dropdown_item multi" style="width:100%" data-value="{{item.value}}" data-index="{{index}}">\
<input id="itcui_combo_chkbox_{{index}}" class="itcui_dropdown_checkbox" type="checkbox" {{if item.checked}}checked{{/if}}>\
<label for="itcui_combo_chkbox_{{index}}">{{if item._title}}{{item._title}}{{else}}{{item.title}}{{/if}}</label>\
</div>\
{{/if}}\
{{/each}}\
</div>'; 

/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_form_v2.js
*/
var defValidMsg = {
	"minlength" : "%f至少需要%l个字符",
	"maxlength" : "%f不能超过%l个字符",
	"maxEnLength" : "%f不能超过%l个字符",
	"maxChLength" : "%f不能超过%l个字符（中文算作2个字符）",
	"email" : "请输入合法的邮件地址",
	"alphanumeric" : "%f只能由字母和数字组成",
	"regex" : "%f格式错误",
	"required" : "%f不能为空",
	"greaterThan" : "%f不能早于%f2",
	"equalTo" : "两次密码输入必须一致",
	"digits" : "%f只能由0-9组成",
	"number" : "%f不是合法的数字",
	"remote" : "%f已经存在",
	"range" : "%f应该介于%l1和%l2之间",
	"rangelength" : "%f的长度应该介于%l1和%l2之间",
	"enLength" : "%f的长度应该是%l个字符",
	"chLength" : "%f的长度应该是%l个字符",
	"greaterEqualThan" : "%f必须晚于或者和%f2在同一天",
	"min" : "%f不能小于%l",
	"max" : "%f不能大于%l"
};

var _toFixed = function(num,fractionDigits){
	if(num=="0" || !num){
		return num;
	}
	return new Number(num).toFixed(fractionDigits);
};

(function($){
	$.fn.extend({
		iFormField : function(field, opts){
			//用于在只读模式为combo/checkbox/radio赋值的函数
			//该函数只会调用一次，调用后因为data已经有值了，就可以走原始流程了
			function ajaxLoad(field, callback){
				if(field.data){
					callback();
				}else if(field.dataType == "enum" && field.enumCat){
					field.data = FW.parseEnumData(field.enumCat,_enum);
				}else if(field.options && field.options.url){
					var opts = field.options;
					$.ajax({
						url : opts.url,
						dataType : "json",
						success : function(v){
							field.data = v;
							callback();
						}
					});
				}
			}
			
			function parseDatetime(_field, val){
				if(_field.dataType=="date" || (_field.type=="date" && typeof(val)!="string")){
					val = FW.long2date(val);
					_field.dataType = "date";
				}
				else if(_field.dataType=="datetime" || (_field.type=="datetime" && typeof(val)!="string")){
					val = FW.long2time(val);
					_field.dataType = "datetime";
				}
				return val;
			}

			function getComboFilter(val){
				var filter = {};
				var selArr = FW.split(val,",");
				for(var i=0;i<selArr.length;i++){
					filter[selArr[i]] = true;
				}
				return filter;
			}

			function parseSelectData(data,filter){
				var selTextArr = [];
				if(isArray(data)){
					for(var i=0;i<data.length;i++){
						if(filter[data[i][0]]){
							selTextArr.push(data[i][1]);
						}
					}
				}
				else{
					for(var k in data){
						if(filter[k]){
							selTextArr.push(data[k]);
						}
					}
				}
				return selTextArr.join(",");
			}
			
		    var isEmpty = function(val){
				return val || (val === 0);
			};

			var self = this;
			self.text = function(field, opts){
				var _field = field;
				var inited = false;
				return {
					init: function(){
						if(inited){
							return;
						}
						if(field.options){
							$("#" + _field._id).iInput("init", field.options)
						}						
						UiCommon.onlyNumKeyDown(field, $("#" + _field._id));
						inited = true;
					},

					getVal: function(isReadonly){
						return $("#" + _field._id).val();
					},

					setVal: function(val){
						$("#" + _field._id).val(isEmpty(val) ? val : "");
					},

					getText: function(){
						return FW.specialchars($("#" + _field._id).val());
					}
				};
			};

			self.textarea = function(){
				var _field = field;
				return {
					getVal: function(isReadonly){
						return $("#" + _field._id).val();
					},

					setVal: function(val){
						$("#" + _field._id).val(isEmpty(val) ? val : "");
					},

					getText: function(){
						var val = $("#" + _field._id).val();
						if(val){
							val = FW.specialchars(val).replace(/\n/g, "<br/>");
						}
						return val;
					}
				}
			};

			self.date = function(field, opts){
				var _opts = opts;
				var _field = field;
				var jqField = $(self);
				var inited = false;
				var tmpVal = null;
				var ret = {
					init: function(){
						if(inited){
							return;
						}
						var wrap = jqField.find(".itc_dtp_wrap");
						var dtpType = wrap.attr("dtptype");
						var dtpOpts = _field.options || {};
						dtpOpts.language = "ch";
						if(!_opts.format){
							if(dtpType=="date"){
								dtpOpts.format = "yyyy-mm-dd";
								dtpOpts.minView = 2;
							}
							else{
								dtpOpts.format = "yyyy-mm-dd hh:ii";
							}
						}
						if(field.rules){
							wrap.addClass("valid-field-wrap");
						}
						wrap.ITCUI_LazyLoadPicker(null, dtpOpts);
						inited = true;
						if(tmpVal){
							$("#" + _field._id).val(FW.specialchars(tmpVal));
						}
					},

					setVal: function(val){
						tmpVal = parseDatetime(_field, val);
						if(inited){
							$("#" + _field._id).val(FW.specialchars(tmpVal));
						}
					},

					getText: function(){
						return inited ? $("#" + _field._id).val() : tmpVal;
					},

					getVal: function(){
						var str =  inited ? $("#" + _field._id).val() : tmpVal;
						if(_field.dataType=="date"){
							return FW.date2long(str);
						}else if(_field.dataType=="datetime"){
							return FW.time2long(str);
						}
						return str;
					},

					isInit: function(){
						return inited;
					},

					onBeginEdit : function(){
						ret.init();
					}
				}
				return ret;
			};

			self.datetime = self.date;

			self.combobox = function(field, opts){
				var _field = field;
				var sel = $("#" + _field._id);
				var inited = false;
				var _opts = _field.options || {};
				var _this =  {
					init: function(){
						if(inited){
							return;
						}
						_opts.data = _field.data || _opts.data;
						sel.on("onComboChange", function(e, obj){
							var text = obj.text;
							if(_field.userFormatter){
								text = _field.formatter(_this.getVal(), text);
							}
							$("#readonly_" + _field._id).html(text);
						});
						sel.ITCUI_ComboBox(null, _opts);
						if(_field.dataType=="enum" && _field.enumCat){
							FW.loadComboEnum(_field._id,_field.enumCat,_field.value);
						}
						inited = true;
					},

					getVal: function(){
						return sel.iCombo("getVal");
					},

					setVal: function(val, opts){
						sel.iCombo("setVal",val);
					},

					getText: function(){
						return sel.iCombo("getTxt");
					},

					isInit: function(){
						return inited;
					},

					onBeginEdit: function(){

					}
				};
				return _this;
			}

			self.checkbox = function(field, opts){
				var _field = field;
				var jqField = $(self);
				var inited = false;
				var tmpValue = null;
				
				var _this = {
					init: function(){
						jqField.find(".autoform_cb").iCheck({
							checkboxClass: 'icheckbox_flat-blue',
							radioClass: 'iradio_flat-blue'
						});
						inited = true;
					},
					
					onBeginEdit: function(){
						if(inited){
							return;
						}
						_this.init();
						if(tmpValue !== null){
							_this.setVal(tmpValue);
						}
					},

					setVal: function(val, opts){
						if(inited){
							jqField.find("input[name='" + _field._name + "']").iCheck('uncheck');
							var selList = (typeof(val)=="string")?val.split(/,|，/):[val];
							for(var i=0;i<selList.length;i++){
								$("#" + field._id + "_" + selList[i]).iCheck('check');
							}
						}else{
							tmpValue = val;
						}
					},

					getVal: function(){
						return _this.getValOrText(true);
					},

					getText: function(){
						return _this.getValOrText();
					},

					getValOrText: function(isVal){
						var data = _field.data || _field.options.data;
						if(!isArray(data)){
							data = UiCommon.objData2Array(data);
						}
						var selList = [];
						var selMap = {};
						if(!inited){
							if(tmpValue){
                                tmpValue += "";
								var tmpArr = tmpValue.split(/,|，/);
								for(var i=0;i<tmpArr.length;i++){
									selMap[tmpArr[i]] = true;
								}
							}
						}
						for(var i=0;i<data.length;i++){
							if(inited){
								if($("#" + _field._id + "_" + data[i][0]).parent().hasClass("checked")){
									selList.push(data[i][isVal ? 0 : 1]);
								}
							}else{
								if(selMap[data[i][0]]){
									selList.push(data[i][isVal ? 0 : 1]);
								}
							}
						}
					    return selList.join(',');
					},

					isInit: function(){
						return inited;
					}
				};
				return _this;
			};

			self.radio = self.checkbox;

			self.label = function(field, opts){
				var _field = field;
				var jqField = $(self);
				var fieldValue = null;
				return {
					init: function(){
						if(_field.value){
							fieldValue = _field.value;
						}
					},
					setVal: function(val){
						fieldValue = val;
						val = parseDatetime(_field, val);
						if(!_field.noEscape){
							val = FW.deepSpecialchars(val);
						}
						val = _field.formatter(val);
						val += "";
						if(!val.length){
							//在编辑模式补一个空格保证label永远能显示出来
							val += " ";
						}
						$("#" + _field._id).html((val + "").replace(/\n/g, '<br/>'));
					},
					getVal: function(){
						return fieldValue;
					},
					getText: function(){
						return $.trim($("#" + _field._id).html());
					}
				};
			};

			self.hidden = function(field, opts){
				var _field = field;
				return {
					setVal: function(val){
						$("#" + _field._id).val(val);
					},

					getVal: function(){
						return $("#" + _field._id).val();
					}

				};
			};

			self.fileupload = function(field, opts){
				var _field = field;
				var sel = $("#" + _field._id);
				var inited = false;
				return {
					init: function(){
						sel.iUpload("init", _field.options);
						inited = true;
					},
					setVal: function(val){
						sel.ITCUI_FileUpload("setInitList",val);
					},
					getVal: function(){
						return sel.iUpload("getVal");
					},
					isInit: function(){
						return inited;
					}
				};
			};

			self.userdefine = function(field, opts){
				var _field = field;
				return {
					init: function(){
						if(_field.render && typeof(_field.render) == "function"){
							_field.render(_field._id);
						}
					},
					setVal: function(val){
						if(_field.setValue && typeof(_field.setValue)=="function"){
							_field.setValue(val,_field._id);
						}
					},
					getVal: function(){
						var val = null;
						if(_field.getValue && typeof(_field.getValue)=="function"){
							val = _field.getValue(_field._id);
						}
						return val
					},
					getText: function(){

					}
				};
			};

			self["switch"] = function(field, opts){
				var _field = field;
				var sel = $("#" + _field._id);
				return {
					init: function(){
						sel.bootstrapSwitch();
					},

					setVal: function(val){
						if(val === _field.valueMapping[0]){
							sel.bootstrapSwitch("state",true);
						}
					},

					getVal: function(){
						var mapping = _field.valueMapping;
						return $("#" + _field._id).bootstrapSwitch("state")?mapping[0]:mapping[2];
					},

					getText: function(){

					}
				};
			};

			//------loader部分----------
			var func = self[field.type];
			if(func){
				return func(field, opts);
			}
		}
	});
	$.fn.extend({
		iForm : function(act, args){
			var self = $(this);
			var _t = this;
			var defOpts = {
				xsWidth: 6,
				mdWidth: 4,
				labelWidth: 3,
				labelFixWidth: "120px",
				container: false,
				validate: false,
				fieldPrefix: "f_",
				namePrefix: "",
                addNamePrefixOn: "get",
				fixLabelWidth: true,
				labelColon: true,
				hideEmpty: true,
				initAsReadonly: false,
				autoPrecision: true,
				defValidMsg: {},
				hideZeroValue: false,
				inputXsWidth: 12,
				inputMdWidth: 12
			};

			function arr2obj(arr){
				var o = {};
				if(arr && isArray(arr)){
					for(var i=0;i<arr.length;i++){
						o[arr[i]] = true;
					}
					return o;
				}else if(typeof(arr) == "string"){
					o[arr] = true;
					return o;
				}
				return arr;
			}
			
			_t.init = function(opts, fields){
				var i,k,field;
				var _fields = [];
				if(fields){
					for(var i=0;i<fields.length;i++){
						_fields.push($.extend({}, fields[i]));
					}
					fields = _fields;
				}
				opts = opts || {};
				for(k in opts){
					defOpts[k] = opts[k];
				}
				for(k in defValidMsg){
					defOpts.defValidMsg[k] = defOpts.defValidMsg[k] || defValidMsg[k];
				}
				var elTmpl = template.compile(_templates.formElement);
				var mapping = {};
				if(!fields || fields.length == 0){
					FW.debug("error:empty fields when init form");
					return;
				}
				for(i=0;i<fields.length;i++){
					field = $.extend({}, fields[i]);
					if(!field || field.nouse){
						continue;
					}
					//为字段应用默认参数
					field.userFormatter = !!field.formatter;
					fields[i] = $.extend({
						wrapXsWidth: defOpts.xsWidth,
						wrapMdWidth: defOpts.mdWidth,
						inputXsWidth: defOpts.inputXsWidth,
						inputMdWidth: defOpts.inputMdWidth,
						labelFixWidth: defOpts.labelFixWidth,
						_name: field.name || field.id,
						_id: defOpts.fieldPrefix + field.id,
						type: "text",
						formatter : function(val){return val;},
						hideZeroValue: /(combobox|radio|checkbox)/.test(field.type) ? false : defOpts.hideZeroValue,
						hideEmpty: defOpts.hideEmpty
					}, field);
					field = fields[i];
					if(field.type == "switch"){
						field.valueMapping = field.valueMapping || ["Y","开启","N","关闭"]
					}
					//默认多行文本框强制折行
					if(field.type == "textarea" && field.breakAll === undefined){
						field.breakAll = true;	
					}
					var w = field.labelFixWidth;
					if(typeof(w) != "string" || w.indexOf("px") < 0){
						field.labelFixWidth += "px";
					}
					//金额自动小数点
					if(/\(元\)/.test(field.title) && defOpts.autoPrecision && !field.precision){
						fields[i].precision = 2;
					}
					//处理转行
					if(i == 0){
						field.lineStart = true;
					}else{
						if(field.linebreak){
							field.lineStart = true;
							fields[i - 1].lineEnd = true;
						}
					}
					//渲染基础html
					if(field.type == "userdefine"){
						if(field.getHtml && typeof(field.getHtml) == "function"){
							field.el = '<div class="element-wrap col-xs-12 col-md-12">' + field.getHtml(field._id) + '</div>';
						}
					}else{
						field.el = elTmpl({field: field, opts: defOpts});
					}
					mapping[field.id] = field;
				}
				fields[fields.length - 1].lineEnd = true;
				self.data("options", defOpts);
				//渲染出一个空白的表单
				var htm = _t.render(defOpts, fields);
				self.html(htm).addClass("bbox").addClass("autoform");
				//渲染字段
				var fieldObjs = {};
				self.data("fieldObjs", fieldObjs);
				var fieldObj;
				for(i=0;i<fields.length;i++){
					field = fields[i];
					if(field.nouse){
						continue;
					}
					var fieldWrap = self.find("div[fieldid='" + field.id + "']");
					var target = fieldWrap.find(".element-wrap");
					fieldWrap.addClass(defOpts.initAsReadonly ? "readonly" : "edit");
					fieldObj = $(target).iFormField(field, defOpts);
					fieldObj.fieldWrap = fieldWrap;
					fieldObj.fieldOption = field;
					fieldObjs[field.id] = fieldObj;
					if(fieldObj && !defOpts.initAsReadonly){
						if(fieldObj.init){
							fieldObj.init();
						}
						if(field.render && typeof(field.render)=="function"){
							field.render(field._id);
						}
					}else if(field.type == "combobox"){
						fieldObj.init();
					}
				}
				self.data("mapping", mapping);
				//初值 如果在选项中提供value的话
				for(i=0;i<fields.length;i++){
					field = fields[i];
					if(field.nouse){
						continue;
					}
					if(field.value !== undefined){
						var valObj = {};
						valObj[field.id] = field.value;
						_t.setVal(valObj);
					}else{
						fieldObjs[field.id].fieldWrap.addClass("empty-hide");
					}
					if(defOpts.initAsReadonly){
						fieldObjs[field.id].fieldWrap.find("input,textarea").addClass("ignore");
					}
				}
				//表单验证
				if(defOpts.validate){
					var rules = {};
					var messages = {};
					for(i=0;i<fields.length;i++){
						field = fields[i];
						if(field.rules){
							if(!field.type || (/(text|textarea|date|datetime|combobox)/.test(field.type))){
								rules[field._name] = {};
								messages[field._name] = {};
								for(var k in field.rules){
									var r = field.rules;
									rules[field._name][k] = r[k];
									messages[field._name][k] = UiCommon.replaceValidMsgForm(k,field,defOpts,mapping);
								}
							}
						}
					}
					var validator = self.validate({
						"rules":rules,
						"messages":messages,
						"errorPlacement":defOpts.errorPlacement || UiCommon.addValidStyle,
						"success":defOpts.validSuccess || ITC_ValidSucc1,
						"onkeyup":false,
						"ignore":".ignore"
					});
				}
				return self;
			};

			_t.render = function(opts, fields){
				var render = template.compile(_templates.formField);
				var renderArg = {
					fields : fields,
					opts: opts
				};
				return render(renderArg);
			};

			//获取表单的最原始数据，只支持获取单个字段或者所有
			_t.getVal = function(args){
				var fieldObjs = self.data("fieldObjs");
				var fields = {};
				if(typeof(args) == "string"){
					fields[args] = true;
				}else{
					fields = fieldObjs;
				}
				var ret = {};
                var formOpts = self.data("options")
				for(var k in fields){
					var fieldObj = fieldObjs[k];
					if(!fieldObj){
						continue;
					}
					var val = fieldObj.getVal();
                    var field = fieldObj.fieldOption;
                    var _k = field._name;                
                    if(formOpts.addNamePrefixOn === "get" || formOpts.addNamePrefixOn === "both"){
                        _k = formOpts.namePrefix + _k;
                    }                    
					ret[_k] = val;
				}
				return typeof(args) == "string" ? ret[args] : ret;
			};

			_t.setVal = function(args){
				var fieldObjs = self.data("fieldObjs");
				for(var k in args){
					var val = args[k];
					var fieldObj = fieldObjs[k];
					if(!fieldObj){
						continue;
					}
					var field = fieldObj.fieldOption;
					//老版本兼容
					if(val && typeof(val) == "string"){
						val = val.replace("BRBR", "\n");
					}
					//自动格式化数字
					if(field.precision){
						val = _toFixed(val, field.precision);
					}
					//在赋值完成后还要更新只读部分的显示
					fieldObj.setVal(val);
					if(!/(fileupload|hidden|combobox)/.test(field.type)){
						_t.setFieldText(fieldObj);
					}
					//处理空值的隐藏
					_t.doEmptyHide(fieldObj, val);
				}
				return self;
			};
			
			_t.doEmptyHide = function(fieldObj, val){
				var field = fieldObj.fieldOption;
				if(val || !field.hideEmpty || (typeof(val)=="number" && !field.hideZeroValue)){
					fieldObj.fieldWrap.removeClass('empty-hide');
				}else{
					fieldObj.fieldWrap.addClass('empty-hide');
				}
			};

			_t.setFieldText = function(fieldObj){
				var field = fieldObj.fieldOption;
				var type = field.type;
				var s = fieldObj.getText();
				var target = $("#readonly_" + field._id);
				if(type != "label"){
					if(field.userFormatter){
						s = field.formatter(FW.deepSpecialchars(fieldObj.getVal()), s);
					}else{
					}					
				}
				target.html(s);
			};

			_t.beginEdit = function(args){
				var fieldObjs = self.data("fieldObjs");
				args = arr2obj(args || fieldObjs);
				for(var k in args){
					var fieldObj = fieldObjs[k];
					if(!fieldObj){
						continue;
					}
					var field = fieldObj.fieldOption;
					var wrap = fieldObj.fieldWrap;
					if(field.type == "hidden"){
						continue;
					}
					if(fieldObj.onBeginEdit){
						fieldObj.onBeginEdit();
					}
					if(field.type != "fileupload"){
						wrap.addClass("edit").removeClass("readonly");
					}else{
						wrap.addClass("upload-edit").removeClass("upload-readonly");
					}
					if(!wrap.hasClass("user-hide")){
						wrap.find("input,textarea").removeClass("ignore");
					}
				}
				return self;
			};

			_t.endEdit = function(args){
				var fieldObjs = self.data("fieldObjs");
				args = arr2obj(args || fieldObjs);
				for(var k in args){
					var fieldObj = fieldObjs[k];
					if(!fieldObj){
						continue;
					}
					var field = fieldObj.fieldOption;
					var wrap = fieldObj.fieldWrap;
					if(field.type == "hidden"){
						continue;
					}
					if(fieldObj.onEndEdit){
						fieldObj.onEndEdit();
					}
					if(field.type != "fileupload"){
						wrap.removeClass("edit").addClass("readonly");
						$("#readonly_" + field._id).html(fieldObj.getText());
					}else{
						wrap.removeClass("upload-edit").addClass("upload-readonly");
					}
					_t.doEmptyHide(fieldObj, fieldObj.getVal());
					wrap.find("input,textarea").addClass("ignore");
				}
				return self;
			};

			_t.setFullVal = function(args){
				var fields = self.data("fieldObjs");
				for(var k in fields){
					if(args[k] === undefined){
						args[k] = null;
					}
				}
				return _t.setVal(args);
			};

			_t.hide = function(args){
				_t.hideOrShow(args, false);
				return self;
			};

			_t.show = function(args){
				_t.hideOrShow(args, true);
				return self;
			};

			_t.hideOrShow = function(args, isShow){
				args = arr2obj(args || fieldObjs);
				var fieldObjs = self.data("fieldObjs");
				for(var k in args){
					var fieldObj = fieldObjs[k];
					if(!fieldObj){
						continue;
					}
					var wrap = fieldObj.fieldWrap;
					if(isShow){
						wrap.removeClass("user-hide");
						if(wrap.hasClass("edit")){
							//只有编辑模式的字段在显示时才可以验证
							wrap.find("input,textarea").removeClass("ignore");
						}
					}else{
						fieldObj.fieldWrap.addClass("user-hide");
						wrap.find("input,textarea").addClass("ignore");
					}
				}
			};

			//-------------------loader部分-------------------
			var oldArgs = {
				getdata : "getVal",
				loaddata : "setVal",
				beginedit : "beginEdit",
				readonly : "endEdit"
			}
			if(typeof(act) == "object"){
				return _t.init(act, args);
			}else if(typeof(oldArgs[act]) == "string"){
				act = oldArgs[act];
			}
			if(act == "init"){
				return _t.init(args.options, args.fields);
			}
			//除了init方法都要校验表单是否已经初始化
			if(!self.data("fieldObjs")){
				if(act == "getVal"){
					return {};
				}
				return self;
			}
			return _t[act](args);
		}
	});
})(jQuery);

function ITC_ValidStyle1(label, element){
	var e = $(element);
	var objWidth = parseInt(e.css("width"));
	var offL = parseInt(element.offset().left);
	var _css = {
		"top": "2px"
	};
	if(e.parent().hasClass("itcui_combo")){
		_css["right"] = "20px";
	}
	else{
		_css["left"] = (objWidth - 22) + "px";
	}
	label.addClass('itcui_icon_warn_mid').css(_css).attr('title',$(label).text());
	label.insertAfter(element);
}

function ITC_ValidSucc1(label){
	$(label).remove();
}
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_common.js
*/
/*UI组件基础公用函数*/
var UiCommon = {};

//替换验证失败时显示的文字（iForm专用）
UiCommon.replaceValidMsgForm = function(rule,field,opts,mapping){
	var r = field.rules;
	//生成错误信息 可以是用户定义的也可以是默认的
	var msg = (field.messages && field.messages[rule])?field.messages[rule]:null;
	msg = msg || opts.defValidMsg[rule] || "字段填写错误";	
	msg = msg.replace("%f",field.title);
	//替换单一个参数的
	var arg1 = r[rule];
	if((arg1 || arg1 === 0) && !isArray(arg1)){
		msg = msg.replace("%l",arg1);
	}
	return UiCommon._replaceValidMsg(msg,r,opts,mapping);
};

//替换验证失败时显示的文字（datagrid专用）
UiCommon.replaceValidMsgTable = function(msg,field,r,opts,mapping){
	msg = msg.replace("%f",field.title);

	var len = r.maxlength || r.minlength || r.enLength || r.chLength || r.maxChLength || r.maxEnLength || r.min || r.max;
	if(len){
		msg = msg.replace("%l",len);
	}
	return UiCommon._replaceValidMsg(msg,r,opts,mapping);
};
//根据规则替换验证失败时显示的文字
UiCommon._replaceValidMsg = function(msg,r,opts,mapping){	
	var range = r.range || r.rangelength;
	if(range && isArray(range)){
		msg = msg.replace("%l1",range[0]);
		msg = msg.replace("%l2",range[1]);
	}

	var f2 = r.greaterThan || r.greaterEqualThan;

	if(typeof(f2)=="string" && opts && f2 && (f2 + "").indexOf("#")==0){
		var pfxLen = ("#" + opts.fieldPrefix).length;
		var f2Id = f2.substring(pfxLen);
		if(mapping && mapping[f2Id]){
			msg = msg.replace("%f2",mapping[f2Id].title);
		}
		else{
			FW.debug("gt/gte的参数错误，在双输入框验证时，需要传入一个正确的选择器，如#f_endTime")
		}
	}
	else{
		msg = msg.replace("%f2",f2);
	}
	return msg;
};

//获取目标对象的style宽度
UiCommon.getObjWidth = function(jqObj){
	//注意这里需要转换成小写 否则IE8下CSS大写识别不出来
	var _style = jqObj.attr("style");
	if(!_style){
		return null;
	}
	_style = _style.toLowerCase();
	var stSplit = _style.split(";");
	for(var i=0;i<stSplit.length;i++){
		//切割如width:100%为width和100%
		var kv = stSplit[i].split(":");
		if(kv.length==2){
			var k = $.trim(kv[0]);
			var v = $.trim(kv[1]);
			if(k=="width"){
				return v;
			}
		}
	}
	return null;
};

//为表单原始组件做一个外框
UiCommon.makeWrap = function(obj){
	var that = $(obj);
	if(that.parent(".itc_object_wrap").length>0){
		return;
	}
	var wrap = $("<div class='itc_object_wrap'></div>").insertBefore(that);
	var attributes = ["float","position","display","width","height","margin-left","margin-top","left","top"];
	var _width = UiCommon.getObjWidth(that) || "150px";
	var _css = {};
	for(var i=0;i<wrap.length;i++){
		var v = that.css(attributes[i]);
		if(v){
			_css[attributes[i]] = v;
		}
	}
	wrap.css(_css);
	wrap.append(that);
	that.attr("style","");
	that.css("width",_width);
	//这里注意需要把select的width复制到wrap上来 resize会出错
	wrap.css("width",_width);
};

//处理表单中某字段只允许(范围)数字输入的函数
UiCommon.onlyNumKeyDown = function(options,obj){
	var dt = options.dataType;
    if(/^number/.test(dt) || /^digits/.test(dt)){
        var n1 = dt.indexOf("[");
        var n2 = dt.indexOf("]");
        var upper = null;
        var lower = null;
        //分析如number[5,99]中的参数
        if(n1>0 && n2>0){
            var par = dt.substring(n1+1,n2).split(",");
            if(par.length==2){
                lower = par[0];
                upper = par[1];
            }
            dt = dt.substring(0,n1);            
        }
        var minv = options.minValue;
        var maxv = options.maxValue;
        if(minv && maxv){
            upper = typeof(maxv)=="function"?maxv(obj):maxv;
            lower = typeof(lower)=="function"?minv(obj):minv;
        }
        if(lower!==null && upper!==null){
            obj.attr("minval",lower);
            obj.attr("maxval",upper);
            obj.bind("keyup",function(){
                var me = $(this);
                var max = parseInt(me.attr("maxval"),10);
                var min = parseInt(me.attr("minval"),10);
                var val = parseInt(me.val(),10);
                if(val>max){
                    me.val(max);
                }
                else if(val<min){
                    me.val(min);
                }
            });
        }
        var args = ["event","'" + dt + "'",lower + "",upper + ""];
        obj.attr("onkeydown","UiCommon.validNum(" + args.join(",") + ")");
    }	
};

//一个封装的的事件，用于在文本框中只接受介于min-max之间的数字输入
UiCommon.validNum = function(evt,type,max,min){
	var e = evt || window.event;
    var key = e.keyCode || e.which;
    var valid = true;

    if (!e.shiftKey && !e.altKey && !e.ctrlKey &&
    // 数字键   
    key >= 48 && key <= 57 ||
    // 小键盘数字键
    key >= 96 && key <= 105 ||
    // Backspace Tab Enter
    key == 8 || key == 9 || key == 13 ||
    // Home End
    key == 35 || key == 36 ||
    // 左右方向键
    key == 37 || key == 39 ||
    // Del Ins
    key == 46 || key == 45) {
        
    }
    else {
        // 主键盘和小键盘上的小数点
        if(type=="number" && (key==110 || key==190)){

        }
        // 主键盘和小键盘上的减号
        else if((min==null || min<0) && (key==109 || key==189)){

        }
        else{
        	valid = false;
        }
    }

    if(!valid){
    	e.returnValue = false;
        if (e.preventDefault) e.preventDefault();
    }
};

/*
	将一个jQ Object移动到parent层中
	target - 被移动的元素，jQ Object
	relObj - 作为参考位置的原页内元素，移动后的被移动过的元素会显示在参考元素下
*/
UiCommon.moveToParent = function(target,relObj){
	if(window._xssEmbbed){
		return;
	}
	if(!window._dialogEmmbed){
		return;
	}
	var pbody = _parent().$("body");
	target.appendTo(pbody);
	target.data("relObj",relObj);
	target.data("parentWindow",$(window));
	UiCommon.adjustObjPosition(target);
};

/*
   根据元素的相对位置计算元素的位置（全框架）
*/
UiCommon.adjustObjPosition = function(target){
	relObj = target.data("relObj");
	if(!relObj){
		return;
	}
	//修复某些兼容模式的ie8无法第一次正确获取offset的bug
	var _offset = relObj.offset();
	setTimeout(function(){
		var offInner = relObj.offset();
		var relHeight = relObj.height();
		var zindexMax = UiCommon.getMaskIndex();	
		var offDialog = UiCommon.getDialogOffset();
		if(!offDialog){
			FW.debug("无法找到一个打开的对话框，请注意只有在对话框里的页面才能声明_dialogEmbbed=true");
			window._dialogEmmbed = false;
			return;
		}
		//IE中不能取body的scrollTop
		var scrollTop = $("body").scrollTop() || document.documentElement.scrollTop;
		var scrollLeft = $("body").scrollLeft() || document.documentElement.scrollLeft;
		target.css({
			"display" : "block",
			"z-index" : zindexMax + 1,
			"top" : offInner.top + offDialog.top + 28 + relHeight - scrollTop,
			"left" : offInner.left + offDialog.left - scrollLeft
		});	
	},50);
};

//获取最顶层对话框的参数 0取偏移1取zindex
UiCommon.getTopMost = function(type){
	var offDialog = null;
	var windows = _parent().$(".window");
	var zindexMax = 0;
	for(var i=0;i<windows.length;i++){
		var w = $(windows[i]);
		var idx = w.css("z-index");
		if(w.css("display")=="block"){
			if(idx>zindexMax){
				zindexMax = idx;
				offDialog = w.offset();
			}
		}
	}
	return type===1?zindexMax:offDialog;
};

UiCommon.addValidStyle = function(label, element){
	var e = $(element);
	var objWidth = parseInt(e.css("width"));
	var _css = {
		"top": "2px"
	};
	if(e.parent().hasClass("itcui_combo")){
		_css["right"] = "20px";
	}
	else{
		_css["left"] = (objWidth - 22) + "px";
	}
	label.addClass('itcui_icon_warn_mid').css(_css).attr('title', $(label).text());
	label.insertAfter(element);	
};

UiCommon.joinKeys = function(obj, delimiter){
	delimiter = delimiter || ",";
	var tmp = [];
	for(var k in obj){
		if(obj[k]){
			tmp.push(k);
		}
	}
	return tmp.join(delimiter);
};

UiCommon.removeComboWrap = function(){
	if(window._dialogEmmbed){
		_parent().$("#itc_combo_wrap").remove();
	}
	else{
		$("#itc_combo_wrap").remove();	
	}
};

UiCommon.getMaskIndex = function(){
	return UiCommon.getTopMost(1);
};

UiCommon.getDialogOffset = function(){
	return UiCommon.getTopMost(0);	
};

UiCommon.objData2Array = function(obj){
	var lst = [];
	for(var k in obj){
		lst.push([k, obj[k]]);
	}
	return lst;	
};

UiCommon.getExtName = function(fileName){
    var n = fileName.lastIndexOf(".");
    if(n < 0){
        return "";
    }
    return fileName.substring(n + 1);
};

UiCommon.isPreviewableImg = function(fileName){
    var extName = UiCommon.getExtName(fileName);
    if(!FW.get("canPreviewAttach")){
        return false;
    }
    return /(jpg|jpeg|png|gif)/.test(extName);
};

UiCommon.isPreviewableFile = function(fileName){
    var extName = UiCommon.getExtName(fileName);
    if(!FW.get("canPreviewAttach")){
        return false;
    }
    return /(doc|docx|xls|xlsx|ppt|pptx|pdf|txt|zip|rar|7z|tif|tiff)/.test(extName);
};
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_adapter.js
*/
var FW = {};
var _itc_grids = {};
var _itc_resize_grids = {};
var _enum_map = {};
/*-------------函数别名--------------*/
//主框架专属API
FW.addTab = function(arg1,arg2){
	_parent()._ITC.addTab(arg1,arg2);
}
FW.initFrame = function(arg){
	_parent()._ITC.init(arg);
}
FW.switchDefaultTab = function(){
	_parent()._ITC.switchDefaultTab();
}
FW.adjustFrame = function(){
	_parent()._ITC.adjustFrame();
}
FW.navigate =  function(id){
	_parent()._ITC.container.navigate(id);
};
FW.deleteTab = function(id){
	_parent()._ITC.deleteTab(id);
}
FW.activeTabById = function(id){
	_parent()._ITC.navTab.activeById(id);
};
FW.modifyTabId = function(newId, oldId){
	_parent()._ITC.navTab.modifyTabId(newId, oldId);
};
FW.deleteTabById = function(id){
	if(_parent()._eipMode && FW.getCurrentTabId()=="eipWindow"){
		_parent()._ITC.closeWindow();
	}
	else{
		_parent()._ITC.navTab.removeById(id);
	}
};
FW.activeTreeItem = function(id){
	_parent()._ITC.navTree.ITCUI_NavTree("switchto",id);
};
FW.getCurrentTabId = function(){
	return _parent()._ITC.navTab.getCurrentTab();
};
FW.addTabWithTree = function(arg){
	_parent()._ITC.addTabWithTree(arg);
};
FW.getCurrentNavTreeId = function(){
	return _parent()._ITC.navTree.ITCUI_NavTree("getactive");
};
FW.setTreeStat = function(state){
	_parent()._ITC.toggleNavTree(state);
	FW.adjustMask();
};
FW.toggleSideTree = function(isHide){
	_parent()._ITC.toggleSideTree(isHide);
}
FW.addSideFrame = function(opts){
	_parent()._ITC.addSideFrame(opts);
}
FW.registerEvent = function(arg1,arg2){
	return _event_handler.registerEvent(arg1,arg2);
};
FW.unregisterEvent = function(arg){
	_event_handler.unregisterEvent(arg);
};
FW.get = function(arg){
	return _event_handler.get(arg);
};
FW.set = function(arg1,arg2){
	_event_handler.set(arg1,arg2);
};
FW.triggerEvent = function(arg1,arg2){
	return _event_handler.triggerEvent(arg1,arg2);
};
FW.confirm = function(arg1,arg2){
	Notice.confirm(arg1,arg2);
};
FW.input = function(arg1,arg2){
	Notice.input(arg1,arg2);
};
FW.remark = function(arg1,arg2){
	Notice.remark(arg1,arg2);
};
FW.dialog = function(method,opts){
	if(!opts && typeof(opts)!="object"){
		return;
	}
	if(!opts.src){
		FW.debug("dialog方法以iframe方式显示页面，必须指定opts.src属性");
	}
	opts.dlgOpts = opts.dlgOpts || {};
	opts.btnOpts = opts.btnOpts || [{
            "name" : "取消",
            "float" : "right",
            "style" : "btn-default",
            "onclick" : function(){
                return true;
            }
        },{
            "name" : "确定",
            "float" : "right",
            "style" : "btn-success",
            "onclick" : function(){
                //请特别注意这里的写法 其中getRegForm是定义在feditprof方法对应页面下的函数，用于返回表单信息
                var p = _parent().window.document.getElementById("itcDlgContent").contentWindow;
                if(p.onOkBtnClick){
                	if(p.onOkBtnClick()){
                		return true;
                	}
                }
            }
        }
    ];
	var dlgOpts = opts.dlgOpts;
	dlgOpts.title = dlgOpts.title || "无标题对话框";
	dlgOpts.width = dlgOpts.width || 650;
	dlgOpts.height = dlgOpts.height  || 450;	
	//百分比大小支持 EasyUI源生对话框不支持百分比大小
	if((dlgOpts.width + "").indexOf("%")>0){
		var scnW = _parent().$("body").width();
		dlgOpts.width = dlgOpts.width.replace("%","") * scnW /100;
	}else{
        if(typeof(dlgOpts.width) === "string"){
            dlgOpts.width = parseInt(dlgOpts.width);
        }
    }
	if((dlgOpts.height + "").indexOf("%")>0){
		var scnH = _parent().$("body").height();
		dlgOpts.height = dlgOpts.height.replace("%","") * scnH /100;
	}
	else{
        if(typeof(dlgOpts.height) === "string"){
            dlgOpts.height = parseInt(dlgOpts.height);
        }
		//dlgOpts.height += 10;//补偿由于按钮栏高度调整导致整个对话框可用高度变少
	}
	dlgOpts.closed = false;
	dlgOpts.modal = (dlgOpts.modal==false)?false:true;
	Notice.dialog(opts.src,opts.dlgOpts,opts.btnOpts);
};
FW.success = _parent().Notice.successTopNotice;
FW.error = _parent().Notice.errorTopNotice;
FW.len = ITC_Len;
FW.substr = ITC_Substr;
FW.debug = function(msg){
	try{
		if(window.console&&window.console.log){
			console.log(msg);
		}
	}
	catch(e){
		
	}
};

FW.loadCss = function(url){	
	//加载字体大小样式
	if(/itc_dg/.test(url)){
		_loadgridcss(url);				
		//反查出url对应的主题
		var theme = null;
		if(window.cssMapping){
			for(var k in cssMapping){
				if(cssMapping[k]==url){
					theme = k;
					break;
				}
			}
		}
		else{
			FW.debug("无法找到cssMapping变量，这是一个在timss2中的全局约定变量");
		}
		if(theme){
			styleOpt.defStyle = theme;
			FW.debug("[dbg]setting theme:" + theme);
			//调整子页面里的表格样式
			_doeachframe(_setgridfontsize,{"url" : url,"theme":theme});
			FW.success("显示设置已更新");
		}
	}
};

FW.setAllPageSize = function(size){
	FW.debug("[dbg]setting pagesize:" + size);
	_doeachframe(function(frame,arg){
		//这里注意datagrid涉及data操作 函数必须在原本的frame执行包括$
		if(frame._setgridpgsize){
			frame._setgridpgsize(frame,arg);
		}
	},{"pageSize":size});
};

function _loadgridcss(url){
	$("head").find("link[itc_tbl]").remove();
	$('<link />').attr('rel', 'stylesheet').attr('href', url).attr('itc_tbl',true).appendTo("head");
}

function _doeachframe(func,arg){
	var frames = _topmost().FW.getAllFrames();
	var currTab = "ITCUI_Iframe_" + _topmost()._ITC.navTab.getCurrentTab();
	for(var i=0;i<frames.length;i++){
		var jqO = $(frames[i]);
		//当前页面不需要再调整了
		if(jqO.hasClass(currTab)){
			continue;
		}
		var frame = frames[i].contentWindow;
		FW.debug("[dbg]do batch action in:" + jqO.attr("id"));
		func(frame,arg);
	}
}

function _setgridfontsize(frame,opts){
	if(frame._realFrame){
		frame = frame[frame._realFrame].contentWindow || frame[frame._realFrame];
	}
	var jqO = $(frame);
	if(!frame._loadgridcss){
		return;
	}
	frame._loadgridcss(opts.url);	
	frame.FW.refreshGrids();
	//修改其他页面的菜单默认值
	if(opts.theme){
		frame.styleOpt.defStyle = opts.theme;
		var styleMnu = frame.$(".itc_pagination_style");
		if(styleMnu.length>0){
			styleMnu.find(".dropdown-selected").removeClass("dropdown-selected").addClass("dropdown-unselected");
			styleMnu.find("li[tblstyle='" + opts.theme + "']").children("a").children("span").addClass("dropdown-selected");
		}
	}
}

function _setgridpgsize(frame,opts){
	if(frame._realFrame){
		frame = frame[frame._realFrame].contentWindow || frame[frame._realFrame];
		frame._setgridpgsize(frame,opts);
		return;
	}
	var grids = $(".eu-datagrid");
	for(var i=0;i<grids.length;i++){
		var grid = $(grids[i]);
		if(grid.parent().hasClass("datagrid-view")){
			var dopts = grid.datagrid("options");
			if(dopts.pagination){
				var pager = grid.datagrid("getPager");
				var id = grid.attr("id");
				//重建分页器 这里的代码和itc_pagination.js里的一样 只不过不触发onChangePageSize事件
				$(".itc_pagination_container").each(function(){
					var container = $(this);
					var targetPager = container.data("targetPager");
					targetPager.pagination({"pageSize":opts.pageSize}).pagination("select",1);
					var _this = container.data('ptrGrid');
					var targetDiv = container.data('targetDiv');
					_this.ITCUI_Pagination("create",targetDiv,container.data("opts"));
				});				
			}
		}
	}
}

FW.setGridsPgSize = function(){
	_doeachframe(_setgridpgsize);
};

FW.getAllFrames = function(){
	var frameId = _topmost()._ITC.opts.contentId;
	return _topmost().$("#" + frameId).children(".ITCUI_ContainerFrame");
};

FW.refreshGrids = function(){
	$(".eu-datagrid").each(function(){
        var _this = $(this);
        if(_this.parent().hasClass("datagrid-view")){
        	_this.datagrid("resize");
        }
    });
};

FW.date2long = function(d){
	if(!d){
		return null;
	}
	s1 = d.split("-");
	return new Date(s1[0],s1[1]-1,s1[2]).getTime();
};

FW.time2long = function(d){
	if(!d){
		return null;
	}
	var _s = d.split(" ");
	var s1 = _s[0].split("-");
	var s2 = _s[1].split(":");
	return new Date(s1[0],s1[1]-1,s1[2],s2[0],s2[1]).getTime();
};

FW.long2time = function(t){
	if(t===null || typeof(t)=="undefined"){
		return "";
	}
	return new Date(t).format("yyyy-MM-dd hh:mm");
};

FW.long2date = function(t){
	if(t===null || typeof(t)=="undefined"){
		return "";
	}
	return new Date(t).format("yyyy-MM-dd");	
};

FW.getFrame = function(id){
	var o = _parent().$(".ITCUI_Iframe_" + id);
	if(o.length>0){
		return o[0].contentWindow;
	}
};

FW.split = function(str,sep){
	if(str==null){
		return [];
	}
	if(typeof(str)=="string"){
		return str.split(sep);
	}
	else{
		return [str];
	}
};

FW.getEnumMap = function(enumCode){
	if(enumCode){
		enumCode = enumCode.toLowerCase();
	}
	if(_enum_map[enumCode]){
		return _enum_map[enumCode];
	}
	if(window._enum){
		for(var k in _enum){
			_k = k.toLowerCase();
			if(_k == enumCode){
				var enums = _enum[k];
				var mp = {};
				for(var i=0;i<enums.length;i++){
					var e = enums[i];
					mp[e.code] = e.label;
				}
				_enum_map[enumCode] = mp;
				return mp;	
			}
		}		
		FW.debug("无法找到枚举数据" + enumCode + "，请检查配置是否正确");
		return {};
	}
	FW.debug("无法找到全局变量_enum，getEnumMap函数只能和mvc组件库一起使用");
	return {};
};

FW.stringify = function(obj){
	var t = typeof (obj);
	if (t != "object" || obj === null) {
		if (t == "string"){
			obj = obj.replace(/\\/g,"\\\\");
			obj = obj.replace(/\"/g,"\\\"");
			obj = obj.replace(/\t/g,"    ");
			obj = obj.replace(/\r\n/g,"\\n");
			obj = obj.replace(/\n/g,"\\n");						
			obj = '"'+ obj + '"';
		}
		return String(obj);
	}
	else {
		var n, v, json = [], arr = (obj && isArray(obj));
		for (n in obj) {
			v = obj[n]; t = typeof(v);
			if (t == "string"){
				v = v.replace(/\\/g,"\\\\");
				v = v.replace(/\"/g,"\\\"");
				v = v.replace(/\t/g,"    ");
				v = v.replace(/\r\n/g,"\\n");
				v = v.replace(/\n/g,"\\n");				
				v = '"'+v+'"';	
			}else if(t == "undefined"){
				continue;
			}
			else if (t == "object" && v !== null)v = FW.stringify(v);
			json.push((arr ? "" : '"' + n + '":') + String(v));
		}
		return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
	}
};

FW.parse = function(str){
	return eval("(" + str + ")");
};

FW.getScnWidth = function(){
	return parseInt(document.documentElement.clientWidth);
};

FW.escape = function(val){
	return escape(val);
};

FW.specialchars = function(val){
	if(!val){
		val = "";
	}
	else if(typeof(val) != "string"){
		return val + "";
	}
	return val.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/, '&#039');
}

FW.unspecialchars = function(val){
	if(typeof(val) != "string"){
		return val + "";
	}
	return val.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&').replace(/&quot;/, '"').replace(/&#039/, "'");
}

FW.deepSpecialchars = function(obj){
	if(!obj){
		return obj;
	}else if(typeof(obj) == "string"){
		return FW.specialchars(obj);
	}else if(isArray(obj) || typeof(obj) == "object"){
		for(var k in obj){
			if(typeof(obj[k]) == "string"){
				obj[k] = FW.specialchars(obj[k]);
			}else if(isArray(obj[k]) || typeof(obj[k]) == "object"){
				FW.deepSpecialchars(obj[k]);
			}
		}
		return obj;
	}
	return obj;
};

FW.parseObjSpecialChars = function(obj,isEscape){
	if(obj && typeof(obj) === "object"){
		for(var k in obj){
			if(obj[k] && typeof(obj[k])==="string"){
				obj[k] = FW[isEscape?"escape" : "unescape"](obj[k]);
			}
			else{
				obj[k] = FW.parseObjSpecialChars(obj[k],isEscape);
			}
		}
	}
	return obj;
}

FW.unescapeObj = function(obj){
	return FW.parseObjSpecialChars(obj,false);
};

FW.escapeObj = function(obj){
	return FW.parseObjSpecialChars(obj,true);
};

FW.unescape = function(val){
	return unescape(val);
};

//将BRBR转换为空格 因为表单中提交\n会挂 所以回车提交的是brbr
FW.convertBRBRToBlankSpace=function(value){
	return value;
}

/*--------废弃函数-------*/
FW.fixRoundButtons = function(bar){
	var sel = $(bar);
	if(sel.length==0 || !sel.hasClass("btn-toolbar")){
		return;		
	}
	sel.find(".left-button-corner").removeClass("left-button-corner");
	sel.find(".right-button-corner").removeClass("right-button-corner");
	sel.children(".button-mg0").removeClass("button-mg0");
	var groups = sel.children(".btn-group");
	//第一个可见的组修正间距问题
	sel.children(".btn-group:visible:first").addClass("button-mg0");
	for(var j=0;j<groups.length;j++){
		var group = $(groups[j]);
		var visButtons = group.children("button:visible");
		//如果组中所有按钮均不可见则直接隐藏外层
		if(visButtons.length==0){
			group.hide();
		}
		else{
			group.show();
		}
		visButtons.first().addClass("left-button-corner");
		visButtons.last().addClass("right-button-corner");
	}
};

FW.fixToolbar = function(bar){
	var sel = $(bar);
	if(sel.length==0 || !sel.hasClass("btn-toolbar")){
		return;		
	}
	sel.find(".left-button-corner").removeClass("left-button-corner");
	sel.find(".right-button-corner").removeClass("right-button-corner");
	sel.children(".button-mg0").removeClass("button-mg0");
	var groups = sel.children(".btn-group");	
	for(var j=0;j<groups.length;j++){
		var group = $(groups[j]);
		var hasVisibleButton = false;
		//找到按钮组中有无可以显示的按钮 这里不能用:visible 因为会继承父类的可视性
		var buttons = group.children("button");
		var btn = null;
		var firstVisBtn = null;
		var lastVisBtn = null;
		var visBtn = null;
		for(var i=0;i<buttons.length;i++){
			btn = $(buttons[i]);
			if(btn.css("display")!=="none" && btn.css("visibility")!=="hidden"){
				visBtn = btn;
				if(!firstVisBtn){
					firstVisBtn = btn;
				}
			}
		}
		lastVisBtn = visBtn;
		//如果组中所有按钮均不可见则直接隐藏外层
		if(!firstVisBtn){
			group.hide();
		}
		else{
			group.show();	
		}
		//在可视按钮的第一个和最后一个加上圆角效果
		if(firstVisBtn){
			firstVisBtn.addClass("left-button-corner");
		}
		if(lastVisBtn){
			lastVisBtn.addClass("right-button-corner");
		}
	}
	//第一个可见的组修正间距问题
	sel.children(".btn-group:visible:first").addClass("button-mg0");
}

FW.updateTreeTag = function(opts){
	_parent()._ITC.updateTreeTag(opts);
};

FW.initPrintButton=function(buttonId,url,title,extParams){
	$(buttonId).click(function(){
		FW.dialog("init",{
			src: url,
			btnOpts:[
					{
					    "name" : "关闭",
					    "float" : "right",
					    "style" : "btn-default",
					    "onclick" : function(){
					        _parent().$("#itcDlg").dialog("close");
					    }
					}
		        ],
			dlgOpts:{ width:800, height:650, closed:false, title:title, modal:true }
		});
	});
};

//用于渲染展示用表格的函数，第一个参数为easyui样式的列描述，后面是easyui格式的数据
FW.renderTable = function(fields, data){
	if(!fields || !data || !isArray(fields)){
		return;
	}
	var fieldMap = {};
	var _data = {
		fields : fields
	};
	for(var i=0;i<fields.length;i++){
		var field = fields[i];
		fieldMap[field.field] = field;	
	}	
	if(data.rows){
		data = data.rows;
	}
	var rows = [];
	//将object格式的数据重新排列成array
	for(var i=0;i<data.length;i++){
		var _row = data[i];
		var row = [];
		for(var j=0;j<fields.length;j++){
			var field = fields[j];
			if(field.hidden){
				continue;
			}
            var val = _row[field.field] || "";
            if(field.formatter){
                val = field.formatter(val, _row, i);
            }
			var cell = {
				val : val 
			};
			if(field.fixed){
				cell.width = field.width;
			}
			row.push(cell);
		}
		rows.push(row);
	}
	_data.rows = rows;
	var render = template.compile(_templates.pureTable);
	return render(_data);
};

//--------------两段下载封装------------
var _download_tokens = {};
function _itc_download(url,token){	
	$("#frame_download").remove();
	$("<iframe id='frame_download' style='display:none'></iframe").appendTo("body");
	$("#frame_download").attr("src",url);
}

function _download_show(me){
	//这里注意tagName不处理是大写的
	var tagName = me.prop("tagName").toLowerCase();
	if(tagName=="button"){
		me.button("reset");
	}
	else{
		me.css("visibility","visible");
	}
}

function _download_hide(me){
	var tagName = me.prop("tagName").toLowerCase();
	if(tagName=="button"){
		if(!me.attr("data-loading-text")){
			me.attr("data-loading-text",me.text())
		}
		me.button("loading");
	}
	else{
		me.css("visibility","hidden")
	}
}

var tokenTimer = null;

function _download_check_token(me,token,timeout){
	_download_tokens[token] = new Date().getTime();
	tokenTimer = setInterval(function(){
		var cookie = document.cookie;
		var clear = false;
		if(cookie.indexOf(token)>=0){
			clear = true;
		}
		else{
			var t1 = _download_tokens[token];
			var t2 = new Date().getTime();
			if(t2 - t1 > timeout*1000){
				$("#frame_download").remove();
				FW.error("文件下载超时");
				clear = true;
			}
		}
		if(clear){
			clearInterval(tokenTimer);
			tokenTimer = null;
			_download_show(me);
			delete _download_tokens[token];
		}
	},200);
}

(function($){
	$.fn.extend({
		//绑定下载
		bindDownload:function(opts){
			var obj = $(this);
			if(obj.length == 0){
				return;
			}
			if(opts==null || opts.url ==null){
				FW.log("bindDownload函数参数错误，必须指定opts.url")
				return;
			}
			opts.twoSteps = opts.twoSteps || false;
			opts.useCookie = opts.useCookie || false;
			opts.prepareTimeout = opts.prepareTimeout || 20;
			opts.downloadTimeout = opts.downloadTimeout || 5;
			obj.data("downloadOptions",opts);
			obj.click(function(){
				//停止以前的下载
				if(tokenTimer){
					clearInterval(tokenTimer);
				}
				var randKey = "token_" + Math.floor(Math.random() *1000000);
				var me = $(this);
				var opt = me.data("downloadOptions");
				//随机令牌
				var url = opt.url;
				if(url.indexOf("?")<0){
					url += "?";
				}
				url += "&rand=" + randKey;				
				//如果是cookie控制或者两部下载 点击后禁用对应动作
				if(opt.useCookie || opt.twoSteps){
					_download_hide(me);					
				}
				//记录随机token以便下载后核对
				if(opts.useCookie){
					_download_tokens[randKey] = true;
				}
				if(!opt.twoSteps){
					_itc_download(url);
					if(opts.useCookie){
						_download_check_token(me,randKey,opt.downloadTimeout);
					}
				}
				else{
					$.ajax({
						url : url + "&prepare=1",
						timeout : opt.prepareTimeout * 1000,
						success : function(data){
							if(data===true || data==="true"){
								if(!opts.useCookie){
									_download_show(me);									
								}
								_itc_download(url);
								if(opts.useCookie){
									_download_check_token(me,randKey,opt.downloadTimeout);
								}
							}
						},
						error : function(){
							FW.error("服务器暂时无法响应下载请求，请稍后再试");
							_download_show(me);
							delete _download_tokens[randKey];
						}
					});
				}
			});
		}
	});
})(jQuery);
/*-------------适配器部分--------------*/

function _changeArg(obj,from,to){
	if(!obj){
		return;
	}
	if(obj[from]){
		obj[to] = obj[from];
		delete(obj[from]);
	}
}
(function($){
	$.fn.extend({
		//分页器
		iPager:function(action,args){
			if(action=="init"){
				action="create";
			}
			if(action=="create"||action=="createPg"){
				if(typeof(args)!="object"){
					return;
				}
				$(this).ITCUI_Pagination(action,args.target,args.styleOpts);
			}
			return $(this);
		},
		//下拉框
		/*iCombo:function(action,args){
			if(action=="init"){
				_changeArg(args,"multiSelect","multiselect");
				if(args && args.enumCat && args.dataType=="enum"){
					if(window._enum){
						if(_enum[args.enumCat]){
							args.data = FW.parseEnumData(args.enumCat,_enum);
							delete(args.enumCat);
							delete(args.dataType);
						}
					}
				}
				$(this).ITCUI_ComboBox(null,args);
			}
			if(action=="getVal"){
				return $(this).ITCUI_ComboBox("getSelected");
			}
			else if(action=="setVal"){
				$(this).ITCUI_ComboBox("select",args);
			}
			else if(action=="getTxt"){
				return $(this).ITCUI_ComboBox("getSelectedText");
			}
			else if(action=="setTxt"){
				$(this).ITCUI_ComboBox("setText",args);	
			}
			else if(action=="setDefaultVal"){
				$(this).ITCUI_ComboBox("setValue",args);
			}
			else if(action=="loadData"){
				$(this).ITCUI_ComboBox("loadData",args);
			}
			return $(this);
		},*/
		ITCUI_ComboBox : function(act, arg){
			return $(this).iCombo(act, arg);	
		},
		//下拉树
		iCombotree:function(action,args){
			if(action=="init"){
				$(this).ITCUI_ComboTree("create",args);
			}
			else if(action=="getVal"){
				return $(this).ITCUI_ComboTree("getValue",args);
			}
			else{
				return $(this).ITCUI_ComboTree(action,args);
			}
			return $(this);
		},
		//自动表单
		ITC_Form:function(action,args){			
			return $(this).iForm(action, args);
		},
		//折叠区域
		iFold:function(action,args){
			if(action=="init"){
				if(typeof(args)=="object"){
					if(args["title"]){
						$(this).attr("grouptitle",args["title"]);
					}
				}
				$(this).ITCUI_Foldable();
			}
			else{
				$(this).ITCUI_Foldable(action,args);
			}
			return $(this);
		},
		//输入框扩展
		iInput:function(action,args){
			if(action=="init"){
				if(typeof(args)=="object"){
					if(args["icon"]){
						$(this).attr("icon",args["icon"]);
					}
					if(args["onlylabel"]){
						$(this).attr("onlylabel",true);	
					}
				}
				$(this).removeClass("form-control").ITCUI_Input(args);
			}
			return $(this);
		},
		//下拉框搜索
		iHint:function(action,args){
			var _this = $(this);
			_changeArg(args,"onGetPosition","getAbsPosFunc");			
			if(action=="init"){
				if(_this.hasClass("form-control")){
					//_this.removeClass("form-control");
					//_this.attr("icon","itcui_btn_mag");
					//_this.iInput("init");
					args.getAbsPosFunc = args.getAbsPosFunc || function(){
						return {left:0,top:1};
					}
				}
				_this.ITCUI_HintList(args);
			}
			return _this;
		},
		//日期选择器
		iDate:function(action,args){
			if(action=="init"){
				var inputOpts = null;
				var datepickerOpts = null;
				if(typeof(args)=="object"){
					inputOpts = args.inputOpts;
					datepickerOpts = args.datepickerOpts;
				}
				$(this).ITCUI_LazyLoadPicker(inputOpts,datepickerOpts);
			}
			return $(this);
		},
		//文件上传
		iUpload:function(action,args){
			if(action=="init"){
				$(this).ITCUI_FileUpload(args);
			}
			else{
				return $(this).ITCUI_FileUpload(action);
			}
			return $(this);
		},
		//数据表格扩展
		iDatagrid:function(action,args){
			var _this = $(this);
			if(action=="beginSearch"){
				_this.ITCUI_GridSearch("init",args);
			}
			else if(action=="endSearch"){
				_this.ITCUI_GridSearch("end");
			}
			else if(action=="init"){
				if(args.data && args.pageSize){
					args._data = args.data;
					args.data = {total:args._data.total,rows:args._data.rows.slice(0,args.pageSize)};
				}
				if(args.url){
					args.remoteSort = true;
				}
				var dgId = _this.attr('id');
				delete(_itc_grids[dgId]);
				args.fitColumns = (args.fitColumns==false)?false:true;
				args.pagination = true;
				args.pageList = args.pageList || [10,15,20,30,100];
                args._onRenderFinish = args.onRenderFinish || function(){};
				args.onRenderFinish = function(){
					var that = $(this);
                    var options = that.data("datagrid").options;
					/*##2014-6-18 b5无数据时不初始化分页器*/
					var data = that.datagrid("getData");					
					var id = that.attr("id");
					var pager = that.attr('pager');
					if(!pager){
						return;
					}					
					if(!_itc_grids[id]){
		                //flag标记可以保证分页器只初始化一次 表头搜索会自动重置分页器
		                var sOpt = typeof(styleOpt)=="undefined"?null:styleOpt;
		                /*##2014-6-18 b5修复datagrid指向错误*/
		                _this.iPager("init",{target:pager,"styleOpts":sOpt});
		                _itc_grids[id] = true;
		            }
		            that.iFixCheckbox();
                    if(options._onRenderFinish){
                        options._onRenderFinish.apply(this);
                    }
				}
				var styleStr = _this.attr("style");
				if(!styleStr || styleStr.toLowerCase().indexOf("height")<0){
					args.scrollbarSize = args.scrollbarSize || 0;
				}
				var dg = _this.datagrid(args);
				return dg;
			}
		},iMenu:function(action,args){
			var id = $(this).attr("id");
			if(action=="init"){
				if(!args || typeof(args)!="object"){
					return;
				}				
				ITCUtil.createMenu("#" + id,args["menu"],args);
			}
			else if(action=="getVal"){
				var o  = $("#" + args);
				if(o.length>0 && o.find(".dropdown-unchecked").length>0){
					return ITCUtil.getMenuCheckVal(args);
				}
				else{
					return ITCUtil.getMenuSelectVal("#" + id,args);
				}
			}
		}
	});
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itcui_loading.js
*/
/*
	ITC组件库 自动加载mask模块
*/
var ajaxCounter = 0;
var lastAjaxTs = -1;
var chkAjaxTime = 0;
var ajaxTimerId = -1;
var firstAjaxStart = -1;

FW.showMask = function(){
	_parent().$("#itc_msk").remove();
	var mc = _parent().$("#mainframe_content");
	var mskHtml = "<div id='itc_msk'><span style='vertical-align:middle'><span class='loading-juhua'></span></span></div>";
	mc.append(mskHtml);
	//2014-8-14 修正当有嵌入树时遮罩层位置没有正确偏移的bug
	FW.adjustMask();
};

FW.removeMask = function(){
    //datagrid对latebind无效
    $("body").trigger("loadingMaskRemoved");        
	_parent().$("#itc_msk").fadeOut("normal",function(){
		_parent().$("#itc_msk").remove();
	});
};

FW.adjustMask = function(){
	var mc = _parent().$("#mainframe_content");
	var pl = mc.css("padding-left");
	if(pl){
		var msk = _parent().$("#itc_msk");
		if(msk.length > 0){
			var cWidth = msk.parent().width();
			msk.css({"left":pl,"width":cWidth});
		}
	}
};

if(window._useLoadingMask){
	FW.showMask();
	ajaxTimerId = setInterval(function(){
		var ts = new Date().getTime();
		if(firstAjaxStart!=-1 && (ts-firstAjaxStart>10000)){
			clearInterval(ajaxTimerId);
				FW.removeMask();
				FW.debug("[dbg]AJAX请求10秒达到最大等待时间（是不是卡了或者报错了？），掩模层移除");
		}
		if(ajaxCounter==0){
			if(chkAjaxTime==3){
				clearInterval(ajaxTimerId);
				FW.removeMask();
				FW.debug("[dbg]所有ajax请求结束于" + ts +  "，掩模层移除");
			}
			else{
				chkAjaxTime += 1;
			}
		}
	},150);
}
else{
	_parent().$("#itc_msk").remove();
}

$(document).ajaxStart(function(){
	var ts = new Date().getTime();
	if(ts-lastAjaxTs>3000 && lastAjaxTs!=-1){
		return;
	}
	if(firstAjaxStart==-1){
		firstAjaxStart = ts;
	}
	lastAjaxTs = ts;
	ajaxCounter += 1;
	chkAjaxTime = 0;
});

$(document).ajaxStop(function(){
	var ts = new Date().getTime();
	if(ajaxCounter>0){
		ajaxCounter -= 1;
	}
});

/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_util.js
*/
/**
* ITC实用工具集
*/
var ITCUtil = ITCUtil || {};
ITCUtil._currMenu = null;

/**
	创建菜单的主入口函数
*/
ITCUtil.createMenu = function(target,menu,opts){
	opts = opts || {};
	opts.direction = opts.direction || "right";
	opts.menuAlign = opts.menuAlign || "left";
	opts.offsetFunc = opts.offsetFunc || function(target){
		return $(target).offset();
	};
	var me = $(target);
	//bootstrap模式
	var menuHtml = ITCUtil._createUlHtml(menu,opts);
	if(me.hasClass("dropdown") || me.find(".dropdown-toggle").length>0){
		me.append(menuHtml);
		ITCUtil._bindCheckEvent(target);
	}
	//经典模式
	else{
		if(!opts.id){
			return;
		}
		me.data("targetmenu",opts.id);
		me.data("offsetfunc",opts.offsetFunc);
		$("body").append(menuHtml);
		ITCUtil._bindCheckEvent("#" + opts.id);
		me.click(function(e){
			e.stopPropagation();
			var mid = "#" + $(this).data("targetmenu");
			var menu = $(mid);
			if(menu.css("display")=="none"){
				menu.css("display","block");
				var offset = $(this).data("offsetfunc")(this);
				menu.css({
					left : offset.left,
					top : offset.top + $(this).height()
				});
				ITCUtil._currMenu = mid;
			}
			else{
				menu.css("display","none");	
			}			
		});

		$("body").click(function(){
			$(ITCUtil._currMenu).hide();
		});
	}
};

ITCUtil.getMenuCheckVal = function(id){
	return $("#" + id).find(".dropdown-checked").length>0
};

ITCUtil.getMenuSelectVal = function(target,group){
	return $(target).find(".dropdown-selected[group='" + group + "']").parents("li").attr("id");
};

ITCUtil._bindCheckEvent = function(target){
	$(target).find(".dropdown-selected,.dropdown-unselected").parent("a").click(function(e){
		var _this = $(this);
		_this.parents("ul").find(".dropdown-selected").removeClass("dropdown-selected");
		_this.find(".dropdown-unselected").addClass("dropdown-selected");
	});

	$(target).find(".dropdown-checked,.dropdown-unchecked").parent("a").click(function(e){
		$(this).find(".dropdown-unchecked").toggleClass("dropdown-checked");
	});
};

ITCUtil._createUlHtml = function(menu,opts){
	if(!isArray(menu)){
		return;
	}
	var idStr = opts.id?" id='" + opts.id + "'":"";
	var h = "<ul class='dropdown-menu' role='menu' " + idStr + ">";
	//遍历一圈看有没有需要加图标或者加勾勾的
	var enableCheck = false;
	var enableIcon = false;
	for(var i=0;i<menu.length;i++){
		var o = menu[i];
		if(o.select||o.check){
			enableCheck = true;
		}
		if(o.iconCls){
			enableIcon = true;
		}
	}
	for(var i=0;i<menu.length;i++){
		var o = menu[i];
		var oid = o.id?' id="' + o.id + '"':'';
		if(o.title=="-"){
			h += '<li class="divider"></li>';
			continue;
		}
		//这里注意pull-right不是乱用的
		var pdStr = opts.direction=="left"?"pull-left":"";
		h += o.submenu?'<li class="dropdown-submenu ' + pdStr + '"' + oid :'<li ' + oid;
		h += '>';
		var chkStr = "";
		if(enableCheck){
			if(o.select){
				var selStr = o.selected?"dropdown-unselected dropdown-selected":"dropdown-unselected";
				var grpStr = o.group?' group="' + o.group + '"':'';
				chkStr = '<span class="' + selStr + '" ' + grpStr + '></span>';				
			}
			else if(o.check){
				var selStr = o.checked?"dropdown-unchecked dropdown-checked":"dropdown-unchecked";
				chkStr = '<span class="' + selStr + '"></span>';
			}
			else{
				chkStr = '<span class="dropdown-holder"></span>';
			}
		}
		if(enableIcon){
			
		}
		var clkStr = o.onclick?'onclick="' + o.onclick + '"':'';
		h += '<a ' + clkStr + '>' + chkStr + o.title + '</a>';
		if(o.submenu){
			h += ITCUtil._createUlHtml(o.submenu,opts);
		}
		h += '</li>';
	}
	h += "</ul>";
	return h;
};

ITCUtil.createBtn = function(target,opts){
	if(!opts){
		return;
	}
	var btnHtm = ITCUtil._cBasebtnHtm(opts);
	if(target){
		$(target).addClass("btn-group-sm").html(btnHtm);
	}
	else{
		return btnHtm;
	}
};

ITCUtil._cBasebtnHtm = function(opts){
	opts.style = opts.style || "btn-default";
	var wStr = opts.width?"width:" + opts.width + "px;":"";
	wStr += opts.height?"height:" + opts.height + "px;":"";
	var tglStr = opts.toggle?' data-toggle="button"':'';
	var mnuTglStyle = (opts.menu && !opts.split)?'dropdown-toggle':'';
	var mnuTglStr = (opts.menu && !opts.split)?'data-toggle="dropdown"':'';
	var cartStr = (opts.menu && !opts.split)?'<span class="caret" style="margin-left:4px"></span>':'';
	var onclickStr = opts.onclick?' onclick="' + opts.onclick + '" ':'';
	return '<button onmouseup="javascript:this.blur();"" ' +  onclickStr + ' type="button" class="' + mnuTglStyle + ' btn ' + opts.style + '" style="' + wStr + '" ' + tglStr + mnuTglStr + '>'+
               opts.title + 
               cartStr + 
           '</button>';
};

ITCUtil.createBtnToolBar = function(target,opts){
	if(opts==null || !isArray(opts)){
		return;
	}
	var btnHtm = '<div class="btn-group btn-group-sm">';
	for(var i=0;i<opts.length;i++){
		if (privMapping && opts.privilege){
			if(!privMapping[opts.privilege]){
				continue;
			}
		}
		var opt = opts[i];
		if(opt["break"] && i>0){
			btnHtm += '</div>';
			btnHtm += '<div class="btn-group btn-group-sm">';
		}
		if(!opt.menu){
			btnHtm += ITCUtil._cBasebtnHtm(opt);
		}
		else{
			
			if(opt.split){				
				btnHtm += ITCUtil.createSpltMnuBtn(null,opt)				
			}
			else{
				btnHtm += '<div class="btn-group btn-group-sm">';
				btnHtm += ITCUtil.createMnuBtn(null,opt)	
				btnHtm += '</div>';
			}
			
		}
	}
	btnHtm += '</div>';
	var _target = $(target);
	_target.addClass("btn-toolbar").html(btnHtm);
	_target.children().each(function(){
		var _this = $(this);
		if(_this.children().length==1){
			_this.children().first().removeClass('btn-group');
		}
	});
	//事件绑定
	_target.find(".dropdown-menu").each(function(){
		ITCUtil._bindCheckEvent($(this));
	});
};

ITCUtil.createSpltMnuBtn = function(target,opts){
	opts.split = true;
	var mOpts = opts.menu.length==1?{}:opts.menu[1];
	var btnHtm = ITCUtil._cBasebtnHtm(opts);
	btnHtm += '<div class="btn-group btn-group-sm">';
	btnHtm += '<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" style="height:28px;padding-left:6px;padding-right:6px">' +
		 	       '<span class="caret"></span>' + 
	     	   '</button>';
	btnHtm += ITCUtil._createUlHtml(opts.menu[0],mOpts);
	btnHtm += '</div>';
	if(target){
		var _target = $(target);
		if(_target.css("position")){
			_target.css({"position":"relative"});
		}
		_target.addClass("btn-group-sm btn-group").html(btnHtm);
		ITCUtil._bindCheckEvent(_target);
	}
	else{
		return btnHtm;
	}
};

ITCUtil.createMnuBtn = function(target,opts){
	var mOpts = opts.menu.length==1?{}:opts.menu[1];
	var btnHtm = ITCUtil._cBasebtnHtm(opts) + ITCUtil._createUlHtml(opts.menu[0],mOpts);
	if(target){
		var _target = $(target);
		if(_target.css("position")){
			_target.css({"position":"relative"});
		}
		_target.addClass("btn-group-sm").html(btnHtm);
		ITCUtil._bindCheckEvent(_target);
	}
	else{
		return btnHtm;

	}
};


/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_timssext.js
*/
var _timss_eventqueue = _parent()._timss_eventqueue || [];

FW.pushEvent = function(event,target,content){
	if(_timss_eventqueue.length==20){
		_timss_eventqueue.shift();
	}
	content = content || "无";
	if(typeof(content)=="object"){
		var objArr = [];
		for(var k in content){
			var s = content[k];
			if(s){
				if(typeof(s)=="string"){
					if(s.length>10) {
						s = s.substring(0,10);
					}
					objArr.push(k + "= " + s);
				}
			}
		}
		content = objArr.join(" ");
	}
	content = content + "";
	if(content.indexOf(",")>0){
		content = content.replaceAll(","," ");
	}
	_timss_eventqueue.push(new Date().getTime() + "," + event + "," + target + "," + content);
};

FW.loadEnumAsync = function(enumCat){
		
};

FW.loadComboEnum = function(comboId,enumCat,defaultVal){
	if(!window.TimssService && !window.ItcMvcService){
		FW.debug("无法找到TimssService，FW.loadComboEnum是一个专用于TIMSS2的适配方法。");
		return;
	}
	var v = "ENUM_" + enumCat;
	var svc = window.ItcMvcService || window.TimssService;
	var data = FW.get(v);
	/*## 2014-07-21 枚举后端预读*/
	if(!data && window._enum){
		data = _enum;
	}
	if(data){
		FW._parseEnumData(comboId,enumCat,data,defaultVal);
	}
	else{
		svc.getEnumParams([enumCat], function(data){
			_parent().FW.set(v,data,true);
			FW._parseEnumData(comboId,enumCat,data,defaultVal);
		});
	}
};

FW.parseEnumData = function(enumCat,data,defaultVal){
	var d = data[enumCat];
	if(!d){
		FW.debug("无法找到枚举数据" + enumCat + "，请检查配置是否正确");
		return [];
	}
	var comboData = [];
	for(var i=0;i<d.length;i++){
		var enumObj = d[i];
		if(defaultVal==enumObj.code){
			comboData.push([enumObj.code,enumObj.label,true]);
		}
		else{
			comboData.push([enumObj.code,enumObj.label]);
		}
	}
	return comboData;
};

FW._parseEnumData = function(comboId,enumCat,data,defaultVal){
	if(data && data[enumCat]){
		var comboData = FW.parseEnumData(enumCat,data,defaultVal);
		$("#" + comboId).iCombo("loadData",comboData);
	}
	else{
		FW.debug("枚举数据获取失败，请检查枚举类型code是否填写正确，以及TimssService是否有在干活");
	}
};


FW.addFormCloseEvent = function(){
	var title = $("body").children(".inner-title");
	if(title.length!=1){
		return;
	}
	if(!/(新建|添加|新增|创建)/.test(title.text())){
		return;
	}
	if(!_parent()._ITC){
		return;
	}
	var tabId = FW.getCurrentTabId();	
    var tab = _parent()._ITC.navTab.findTabItem(tabId);
	tab.beforeClose = tab.beforeClose || function(){
		FW.confirm("确定关闭|当前选项卡中未保存的内容将丢失，是否继续？",function(){
			delete(evtMap[tabId].beforeClose);
			FW.deleteTabById(tabId);
		});
		return false;
	};
};


(function($){
	$.fn.extend({
		//下拉列表选人
		initHintPersonList:function(opts){
			var basePath = window.basePath || "";
			opts = opts || {};
			opts.scope = opts.scope || "site";
			opts.showOn = opts.showOn || "input";
			opts.datasource = basePath + "user?method=hint"
			opts.getDataOnKeyPress = true;
			opts.highlight = true;
			opts.formatter = function(id,name){
				return name + " / " + id.split("_")[0];
			};
			if(opts.scope=="all"){
				opts.datasource += "&ignoresite=1";
			}
			$(this).iHint("init",opts);
		}
	});
})(jQuery);
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_privilege.js
*/
//--------------权限部分--------------
var Priv = {
	secUser : (_parent()._ItcMvc_ApplicationGlobal!=null ? _parent()._ItcMvc_ApplicationGlobal.userInfo : null) || window._userInfo || null,
	privMap : {}, //虚拟权限
	objCache : [], //jQuery选择器缓存
	repFunc : ["hasRole","hasGroup","hasPrivilege","siteIs","inOrg","idIs"],
	vPriv : window._vPrivilege || {}//虚拟权限
};

Priv.hasRole = function(v){
	if(!Priv.secUser){
		return false;
	}
	var r = Priv.secUser.roles;
	for(var i=0;i<r.length;i++){
		var role = r[i];
		if(role.id == v){
			return true;
		}
	}
	return false;
};

Priv.hasGroup = function(v){
	if(!Priv.secUser){
		return false;
	}
	var g = Priv.secUser.groups;
	for(var i=0;i<g.length;i++){
		var group = r[i];
		if(group.id == v){
			return true;
		}
	}
	return false;
};

Priv.hasPrivilege = function(priv){
	var pm = _parent().privMapping || window.privMapping || {};
	if(pm[priv]){
		return true;
	}
	if(Priv.vPriv[priv]){
		return true;
	}
	return false;
};

Priv.idIs = function(id){
	if(!Priv.secUser){
		return false;
	}
	if(Priv.secUser.userId == id){
		return true;
	}
	return false;
};

Priv.siteIs = function(site){
	if(!Priv.secUser){
		return false;
	}
	if(Priv.secUser.siteId == site){
		return true;
	}
	return false;
};

Priv.inOrg = function(v){
	if(!Priv.secUser){
		return false;
	}
	var o = Priv.secUser.orgs;
	for(var i=0;i<o.length;i++){
		var org = o[i];
		if(org.code==v || org.parentCode==v){
			return true;
		}
	}
	return false;
};

Priv.runExpr = function(expr){
	//非表达式 一般情况
	if(!expr.indexOf("(")<0){
		return Priv.hasPrivilege(expr);
	}
	//将缩写函数映射到Priv命名空间上
	var pf = Priv.repFunc;
	for(var i=0;i<pf.length;i++){
		var fn = pf[i];
		expr = expr.replaceAll(fn,"Priv." + fn);
	}
	try{
		return eval(expr);
	}
	catch(e){
		return false;
	}
};

Priv.map = function(expr,vPriv){
	if(expr){
		Priv.privMap[vPriv] = expr;
	}
	else{
		delete Priv.vPriv[vPriv];
		delete Priv.privMap[vPriv];
	}
};

Priv.applyPrivOnObj = function(obj){
	var _this = $(obj);
	var priv = _this.attr("privilege"); 			
	if(priv){
		Priv.objCache.push([_this,priv]);
		var privList = priv.split(" ");
		var hasPriv = true;
		for(var i=0;i<privList.length;i++){
			var p = privList[i];
			if(!Priv.hasPrivilege(p)){
				_this.hide();
				hasPriv = false;
				break;
			}
		}
		if(hasPriv){
			if(!_this.hasClass("only-hide")){
				_this.show();
			}
		}
	}
};

Priv.applyPrivOnForms = function(){
	$("form").each(function(){
		var f = $(this);
		var mapping = f.data("fieldObjs");
		if(mapping){
			for(var k in mapping){
				var field = mapping[k].fieldOption;
				var priv = field.privilege; 
				if(priv){
					if(Priv.hasPrivilege(priv)){
						f.iForm("show",k);
					}
					else{
						f.iForm("hide",k);
					}
				}
			}
		}
	});
};

Priv.applyPrivOnTables = function(){
	$("table").each(function(){
		var t = $(this);
		var dg = t.data("datagrid"); 
		if(dg){
			var columns = dg.options.columns[0];
			if(columns){
				for(var i=0; i<columns.length; i++){
					var col = columns[i];
					var priv = col.privilege;
					if(priv){
						if(Priv.hasPrivilege(priv)){
							t.datagrid("showColumn",col.field);
						}
						else{
							t.datagrid("hideColumn",col.field);
						}
					}
				}
			}
		}
	});
};

Priv.apply = function(withCache){
	withCache = /*typeof(withCache) == "undefined" ? true: withCache*/false;
	//计算表达式
	for(var k in Priv.privMap){
		var expr = Priv.privMap[k];
		if(Priv.runExpr(expr)){
			Priv.vPriv[k] = true;
		}
		else{
			delete Priv.vPriv[k];
		}
	}
	var objCache = Priv.objCache;
	var ocl = objCache.length;
	if(withCache && ocl>0){
		for(var i=0;i<ocl;i++){
			var item = objCache[i];
			Priv.applyPrivOnObj(item[0]);
		}
	}
	else{
		objCache = [];
		$(".priv").each(function(){
			Priv.applyPrivOnObj(this);
		});
	}
	//自动表单权限应用
	Priv.applyPrivOnForms();
	//datagrid应用权限
	Priv.applyPrivOnTables();
};

(function($){
	$.fn.extend({
		authShow : function(){
			var me = $(this);
			var privilege = me.attr("privilege");
			if(privilege){
				if(Priv.hasPrivilege(privilege)){
					me.show();
				}
			}
		}
	});
})(jQuery);

/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\bootstrap\js\bs_popover.js
*/
/* ========================================================================
 * Bootstrap: popover.js v3.2.0
 * http://getbootstrap.com/javascript/#popovers
 * ========================================================================
 * Copyright 2011-2014 Twitter, Inc.
 * Licensed under MIT (https://github.com/twbs/bootstrap/blob/master/LICENSE)
 * ======================================================================== */


+function ($) {
  'use strict';

  // POPOVER PUBLIC CLASS DEFINITION
  // ===============================

  var Popover = function (element, options) {
    this.init('popover', element, options)
  }

  if (!$.fn.tooltip) throw new Error('Popover requires tooltip.js')

  Popover.VERSION  = '3.2.0'

  Popover.DEFAULTS = $.extend({}, $.fn.tooltip.Constructor.DEFAULTS, {
    placement: 'right',
    trigger: 'click',
    content: '',
    template: '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>'
  })


  // NOTE: POPOVER EXTENDS tooltip.js
  // ================================

  Popover.prototype = $.extend({}, $.fn.tooltip.Constructor.prototype)

  Popover.prototype.constructor = Popover

  Popover.prototype.getDefaults = function () {
    return Popover.DEFAULTS
  }

  Popover.prototype.setContent = function () {
    var $tip    = this.tip()
    var title   = this.getTitle()
    var content = this.getContent()

    $tip.find('.popover-title')[this.options.html ? 'html' : 'text'](title)
    $tip.find('.popover-content').empty()[ // we use append for html objects to maintain js events
      this.options.html ? (typeof content == 'string' ? 'html' : 'append') : 'text'
    ](content)

    $tip.removeClass('fade top bottom left right in')

    // IE8 doesn't accept hiding via the `:empty` pseudo selector, we have to do
    // this manually by checking the contents.
    if (!$tip.find('.popover-title').html()) $tip.find('.popover-title').hide()
  }

  Popover.prototype.hasContent = function () {
    return this.getTitle() || this.getContent()
  }

  Popover.prototype.getContent = function () {
    var $e = this.$element
    var o  = this.options

    return $e.attr('data-content')
      || (typeof o.content == 'function' ?
            o.content.call($e[0]) :
            o.content)
  }

  Popover.prototype.arrow = function () {
    return (this.$arrow = this.$arrow || this.tip().find('.arrow'))
  }

  Popover.prototype.tip = function () {
    if (!this.$tip) this.$tip = $(this.options.template)
    return this.$tip
  }


  // POPOVER PLUGIN DEFINITION
  // =========================

  function Plugin(option) {
    return this.each(function () {
      var $this   = $(this)
      var data    = $this.data('bs.popover')
      var options = typeof option == 'object' && option

      if (!data && option == 'destroy') return
      if (!data) $this.data('bs.popover', (data = new Popover(this, options)))
      if (typeof option == 'string') data[option]()
    })
  }

  var old = $.fn.popover

  $.fn.popover             = Plugin
  $.fn.popover.Constructor = Popover


  // POPOVER NO CONFLICT
  // ===================

  $.fn.popover.noConflict = function () {
    $.fn.popover = old
    return this
  }

}(jQuery);

/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\itcui\js\itc_helper.js
*/
var _helper = new iHelper();

/*帮助模块 可以远程保存设置的*/
function iHelper(){
	var runMode = "local";//不检查和保存设置
	var showedTips = {};
	var basePath = "/"
	var helpData = {TIPS:[],SPLASH:[],FORM:[],POPUP:[]};
	var placeScnTopTipAt = "body";

	var showPageTopTip = function(tip){
		if(showedTips[tip.id]){
			return;
		}
		showedTips[tip.id] = true;
		var s = "<div class='scntop-tip-wrap bbox'><span>" + tip.msg + "<span><div class='scntop-close-wrap'><b class='icon-close'></b></div></div>";
		$(placeScnTopTipAt).prepend(s);		
		markTipRead(tip);
	};

	var showSplashTip = function(){

	};

	var showPopupTip = function(){

	};

	var markTipRead = function(tip){
		$.ajax({
			url : basePath + "tips?method=markRead",
			data : {
				id : tip.id
			},
			type : "GET"
		});
	};

	var showOnloadTips = function(){
		//页面顶端提示只显示一条
		var tips = helpData.TIPS;
		if(tips.length>0){
			showPageTopTip(tips[0]);
		}
	};

	var parseHelpData = function(data){
		for(var i=0;i<data.length;i++){
			var item = data[i];
			helpData[item.type].push(item);
		}
	};

	this.init = function(module,options){
		opts = options || {};
		placeScnTopTipAt = opts.placeScnTopTipAt || "body";
		var basePath = window.basePath || basePath;
		$.ajax({
			url : basePath + "tips?method=getTips",
			data : {
				modules : module
			},
			type : "GET",
			dataType : "JSON",
			success : function(data){
				if(data && isArray(data)){
					parseHelpData(data);
					showOnloadTips();
				}
			}
		});
	};


};
/*
File:D:\itc_uispec\trunk\prototype_ui\nightly\source\thirdparty\js\bootstrap-switch.js
*/
/* ========================================================================
 * bootstrap-switch - v3.2.1
 * http://www.bootstrap-switch.org
 * ========================================================================
 * Copyright 2012-2013 Mattia Larentis
 *
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================================
 */

(function() {
  var __slice = [].slice;

  (function($, window) {
    "use strict";
    var BootstrapSwitch;
    BootstrapSwitch = (function() {
      function BootstrapSwitch(element, options) {
        if (options == null) {
          options = {};
        }
        this.$element = $(element);
        this.options = $.extend({}, $.fn.bootstrapSwitch.defaults, {
          state: this.$element.is(":checked"),
          size: this.$element.data("size"),
          animate: this.$element.data("animate"),
          disabled: this.$element.is(":disabled"),
          readonly: this.$element.is("[readonly]"),
          indeterminate: this.$element.data("indeterminate"),
          inverse: this.$element.data("inverse"),
          radioAllOff: this.$element.data("radio-all-off"),
          onColor: this.$element.data("on-color"),
          offColor: this.$element.data("off-color"),
          onText: this.$element.data("on-text"),
          offText: this.$element.data("off-text"),
          labelText: this.$element.data("label-text"),
          handleWidth: this.$element.data("handle-width"),
          labelWidth: this.$element.data("label-width"),
          baseClass: this.$element.data("base-class"),
          wrapperClass: this.$element.data("wrapper-class")
        }, options);
        this.$wrapper = $("<div>", {
          "class": (function(_this) {
            return function() {
              var classes;
              classes = ["" + _this.options.baseClass].concat(_this._getClasses(_this.options.wrapperClass));
              classes.push(_this.options.state ? "" + _this.options.baseClass + "-on" : "" + _this.options.baseClass + "-off");
              if (_this.options.size != null) {
                classes.push("" + _this.options.baseClass + "-" + _this.options.size);
              }
              if (_this.options.disabled) {
                classes.push("" + _this.options.baseClass + "-disabled");
              }
              if (_this.options.readonly) {
                classes.push("" + _this.options.baseClass + "-readonly");
              }
              if (_this.options.indeterminate) {
                classes.push("" + _this.options.baseClass + "-indeterminate");
              }
              if (_this.options.inverse) {
                classes.push("" + _this.options.baseClass + "-inverse");
              }
              if (_this.$element.attr("id")) {
                classes.push("" + _this.options.baseClass + "-id-" + (_this.$element.attr("id")));
              }
              return classes.join(" ");
            };
          })(this)()
        });
        this.$container = $("<div>", {
          "class": "" + this.options.baseClass + "-container"
        });
        this.$on = $("<span>", {
          html: this.options.onText,
          "class": "" + this.options.baseClass + "-handle-on " + this.options.baseClass + "-" + this.options.onColor
        });
        this.$off = $("<span>", {
          html: this.options.offText,
          "class": "" + this.options.baseClass + "-handle-off " + this.options.baseClass + "-" + this.options.offColor
        });
        this.$label = $("<span>", {
          html: this.options.labelText,
          "class": "" + this.options.baseClass + "-label"
        });
        this.$element.on("init.bootstrapSwitch", (function(_this) {
          return function() {
            return _this.options.onInit.apply(element, arguments);
          };
        })(this));
        this.$element.on("switchChange.bootstrapSwitch", (function(_this) {
          return function() {
            return _this.options.onSwitchChange.apply(element, arguments);
          };
        })(this));
        this.$container = this.$element.wrap(this.$container).parent();
        this.$wrapper = this.$container.wrap(this.$wrapper).parent();
        this.$element.before(this.options.inverse ? this.$off : this.$on).before(this.$label).before(this.options.inverse ? this.$on : this.$off);
        if (this.options.indeterminate) {
          this.$element.prop("indeterminate", true);
        }
        this._width();
        this._containerPosition(this.options.state, (function(_this) {
          return function() {
            if (_this.options.animate) {
              return _this.$wrapper.addClass("" + _this.options.baseClass + "-animate");
            }
          };
        })(this));
        this._elementHandlers();
        this._handleHandlers();
        this._labelHandlers();
        this._formHandler();
        this._externalLabelHandler();
        this.$element.trigger("init.bootstrapSwitch");
      }

      BootstrapSwitch.prototype._constructor = BootstrapSwitch;

      BootstrapSwitch.prototype.state = function(value, skip) {
        if (typeof value === "undefined") {
          return this.options.state;
        }
        if (this.options.disabled || this.options.readonly) {
          return this.$element;
        }
        if (this.options.state && !this.options.radioAllOff && this.$element.is(':radio')) {
          return this.$element;
        }
        if (this.options.indeterminate) {
          this.indeterminate(false);
          value = true;
        } else {
          value = !!value;
        }
        this.$element.prop("checked", value).trigger("change.bootstrapSwitch", skip);
        return this.$element;
      };

      BootstrapSwitch.prototype.toggleState = function(skip) {
        if (this.options.disabled || this.options.readonly) {
          return this.$element;
        }
        if (this.options.indeterminate) {
          this.indeterminate(false);
          return this.state(true);
        } else {
          return this.$element.prop("checked", !this.options.state).trigger("change.bootstrapSwitch", skip);
        }
      };

      BootstrapSwitch.prototype.size = function(value) {
        if (typeof value === "undefined") {
          return this.options.size;
        }
        if (this.options.size != null) {
          this.$wrapper.removeClass("" + this.options.baseClass + "-" + this.options.size);
        }
        if (value) {
          this.$wrapper.addClass("" + this.options.baseClass + "-" + value);
        }
        this._width();
        this.options.size = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.animate = function(value) {
        if (typeof value === "undefined") {
          return this.options.animate;
        }
        value = !!value;
        this.options.animate = value;
        this.$wrapper[value ? "addClass" : "removeClass"]("" + this.options.baseClass + "-animate");
        return this.$element;
      };

      BootstrapSwitch.prototype.toggleAnimate = function() {
        this.options.animate = !this.options.animate;
        this.$wrapper.toggleClass("" + this.options.baseClass + "-animate");
        return this.$element;
      };

      BootstrapSwitch.prototype.disabled = function(value) {
        if (typeof value === "undefined") {
          return this.options.disabled;
        }
        value = !!value;
        this.options.disabled = value;
        this.$element.prop("disabled", value);
        this.$wrapper[value ? "addClass" : "removeClass"]("" + this.options.baseClass + "-disabled");
        return this.$element;
      };

      BootstrapSwitch.prototype.toggleDisabled = function() {
        this.options.disabled = !this.options.disabled;
        this.$element.prop("disabled", this.options.disabled);
        this.$wrapper.toggleClass("" + this.options.baseClass + "-disabled");
        return this.$element;
      };

      BootstrapSwitch.prototype.readonly = function(value) {
        if (typeof value === "undefined") {
          return this.options.readonly;
        }
        value = !!value;
        this.options.readonly = value;
        this.$element.prop("readonly", value);
        this.$wrapper[value ? "addClass" : "removeClass"]("" + this.options.baseClass + "-readonly");
        return this.$element;
      };

      BootstrapSwitch.prototype.toggleReadonly = function() {
        this.options.readonly = !this.options.readonly;
        this.$element.prop("readonly", this.options.readonly);
        this.$wrapper.toggleClass("" + this.options.baseClass + "-readonly");
        return this.$element;
      };

      BootstrapSwitch.prototype.indeterminate = function(value) {
        if (typeof value === "undefined") {
          return this.options.indeterminate;
        }
        value = !!value;
        this.options.indeterminate = value;
        this.$element.prop("indeterminate", value);
        this.$wrapper[value ? "addClass" : "removeClass"]("" + this.options.baseClass + "-indeterminate");
        this._containerPosition();
        return this.$element;
      };

      BootstrapSwitch.prototype.toggleIndeterminate = function() {
        this.options.indeterminate = !this.options.indeterminate;
        this.$element.prop("indeterminate", !this.options.indeterminate);
        this.$wrapper.toggleClass("" + this.options.baseClass + "-indeterminate");
        this._containerPosition();
        return this.$element;
      };

      BootstrapSwitch.prototype.inverse = function(value) {
        var $off, $on;
        if (typeof value === "undefined") {
          return this.options.inverse;
        }
        value = !!value;
        this.$wrapper[value ? "addClass" : "removeClass"]("" + this.options.baseClass + "-inverse");
        $on = this.$on.clone(true);
        $off = this.$off.clone(true);
        this.$on.replaceWith($off);
        this.$off.replaceWith($on);
        this.$on = $off;
        this.$off = $on;
        this.options.inverse = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.toggleInverse = function() {
        var $off, $on;
        this.$wrapper.toggleClass("" + this.options.baseClass + "-inverse");
        $on = this.$on.clone(true);
        $off = this.$off.clone(true);
        this.$on.replaceWith($off);
        this.$off.replaceWith($on);
        this.$on = $off;
        this.$off = $on;
        this.options.inverse = !this.options.inverse;
        return this.$element;
      };

      BootstrapSwitch.prototype.onColor = function(value) {
        var color;
        color = this.options.onColor;
        if (typeof value === "undefined") {
          return color;
        }
        if (color != null) {
          this.$on.removeClass("" + this.options.baseClass + "-" + color);
        }
        this.$on.addClass("" + this.options.baseClass + "-" + value);
        this.options.onColor = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.offColor = function(value) {
        var color;
        color = this.options.offColor;
        if (typeof value === "undefined") {
          return color;
        }
        if (color != null) {
          this.$off.removeClass("" + this.options.baseClass + "-" + color);
        }
        this.$off.addClass("" + this.options.baseClass + "-" + value);
        this.options.offColor = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.onText = function(value) {
        if (typeof value === "undefined") {
          return this.options.onText;
        }
        this.$on.html(value);
        this._width();
        this._containerPosition();
        this.options.onText = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.offText = function(value) {
        if (typeof value === "undefined") {
          return this.options.offText;
        }
        this.$off.html(value);
        this._width();
        this._containerPosition();
        this.options.offText = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.labelText = function(value) {
        if (typeof value === "undefined") {
          return this.options.labelText;
        }
        this.$label.html(value);
        this._width();
        this.options.labelText = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.handleWidth = function(value) {
        if (typeof value === "undefined") {
          return this.options.handleWidth;
        }
        this.options.handleWidth = value;
        this._width();
        this._containerPosition();
        return this.$element;
      };

      BootstrapSwitch.prototype.labelWidth = function(value) {
        if (typeof value === "undefined") {
          return this.options.labelWidth;
        }
        this.options.labelWidth = value;
        this._width();
        this._containerPosition();
        return this.$element;
      };

      BootstrapSwitch.prototype.baseClass = function(value) {
        return this.options.baseClass;
      };

      BootstrapSwitch.prototype.wrapperClass = function(value) {
        if (typeof value === "undefined") {
          return this.options.wrapperClass;
        }
        if (!value) {
          value = $.fn.bootstrapSwitch.defaults.wrapperClass;
        }
        this.$wrapper.removeClass(this._getClasses(this.options.wrapperClass).join(" "));
        this.$wrapper.addClass(this._getClasses(value).join(" "));
        this.options.wrapperClass = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.radioAllOff = function(value) {
        if (typeof value === "undefined") {
          return this.options.radioAllOff;
        }
        this.options.radioAllOff = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.onInit = function(value) {
        if (typeof value === "undefined") {
          return this.options.onInit;
        }
        if (!value) {
          value = $.fn.bootstrapSwitch.defaults.onInit;
        }
        this.options.onInit = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.onSwitchChange = function(value) {
        if (typeof value === "undefined") {
          return this.options.onSwitchChange;
        }
        if (!value) {
          value = $.fn.bootstrapSwitch.defaults.onSwitchChange;
        }
        this.options.onSwitchChange = value;
        return this.$element;
      };

      BootstrapSwitch.prototype.destroy = function() {
        var $form;
        $form = this.$element.closest("form");
        if ($form.length) {
          $form.off("reset.bootstrapSwitch").removeData("bootstrap-switch");
        }
        this.$container.children().not(this.$element).remove();
        this.$element.unwrap().unwrap().off(".bootstrapSwitch").removeData("bootstrap-switch");
        return this.$element;
      };

      BootstrapSwitch.prototype._width = function() {
        var $handles, handleWidth;
        $handles = this.$on.add(this.$off);
        $handles.add(this.$label).css("width", "");
        handleWidth = this.options.handleWidth === "auto" ? Math.max(this.$on.width(), this.$off.width()) : this.options.handleWidth;
        $handles.width(handleWidth);
        this.$label.width((function(_this) {
          return function(index, width) {
            if (_this.options.labelWidth !== "auto") {
              return _this.options.labelWidth;
            }
            if (width < handleWidth) {
              return handleWidth;
            } else {
              return width;
            }
          };
        })(this));
        this._handleWidth = this.$on.outerWidth();
        this._labelWidth = this.$label.outerWidth();
        this.$container.width((this._handleWidth * 2) + this._labelWidth);
        return this.$wrapper.width(this._handleWidth + this._labelWidth);
      };

      BootstrapSwitch.prototype._containerPosition = function(state, callback) {
        if (state == null) {
          state = this.options.state;
        }
        this.$container.css("margin-left", (function(_this) {
          return function() {
            var values;
            values = [0, "-" + _this._handleWidth + "px"];
            if (_this.options.indeterminate) {
              return "-" + (_this._handleWidth / 2) + "px";
            }
            if (state) {
              if (_this.options.inverse) {
                return values[1];
              } else {
                return values[0];
              }
            } else {
              if (_this.options.inverse) {
                return values[0];
              } else {
                return values[1];
              }
            }
          };
        })(this));
        if (!callback) {
          return;
        }
        if ($.support.transition) {
          return this.$container.one("bsTransitionEnd", callback).emulateTransitionEnd(500);
        } else {
          return callback();
        }
      };

      BootstrapSwitch.prototype._elementHandlers = function() {
        return this.$element.on({
          "change.bootstrapSwitch": (function(_this) {
            return function(e, skip) {
              var state;
              e.preventDefault();
              e.stopImmediatePropagation();
              state = _this.$element.is(":checked");
              _this._containerPosition(state);
              if (state === _this.options.state) {
                return;
              }
              _this.options.state = state;
              _this.$wrapper.toggleClass("" + _this.options.baseClass + "-off").toggleClass("" + _this.options.baseClass + "-on");
              if (!skip) {
                if (_this.$element.is(":radio")) {
                  $("[name='" + (_this.$element.attr('name')) + "']").not(_this.$element).prop("checked", false).trigger("change.bootstrapSwitch", true);
                }
                return _this.$element.trigger("switchChange.bootstrapSwitch", [state]);
              }
            };
          })(this),
          "focus.bootstrapSwitch": (function(_this) {
            return function(e) {
              e.preventDefault();
              return _this.$wrapper.addClass("" + _this.options.baseClass + "-focused");
            };
          })(this),
          "blur.bootstrapSwitch": (function(_this) {
            return function(e) {
              e.preventDefault();
              return _this.$wrapper.removeClass("" + _this.options.baseClass + "-focused");
            };
          })(this),
          "keydown.bootstrapSwitch": (function(_this) {
            return function(e) {
              if (!e.which || _this.options.disabled || _this.options.readonly) {
                return;
              }
              switch (e.which) {
                case 37:
                  e.preventDefault();
                  e.stopImmediatePropagation();
                  return _this.state(false);
                case 39:
                  e.preventDefault();
                  e.stopImmediatePropagation();
                  return _this.state(true);
              }
            };
          })(this)
        });
      };

      BootstrapSwitch.prototype._handleHandlers = function() {
        this.$on.on("click.bootstrapSwitch", (function(_this) {
          return function(e) {
            _this.state(false);
            return _this.$element.trigger("focus.bootstrapSwitch");
          };
        })(this));
        return this.$off.on("click.bootstrapSwitch", (function(_this) {
          return function(e) {
            _this.state(true);
            return _this.$element.trigger("focus.bootstrapSwitch");
          };
        })(this));
      };

      BootstrapSwitch.prototype._labelHandlers = function() {
        return this.$label.on({
          "mousedown.bootstrapSwitch touchstart.bootstrapSwitch": (function(_this) {
            return function(e) {
              if (_this._dragStart || _this.options.disabled || _this.options.readonly) {
                return;
              }
              e.preventDefault();
              _this._dragStart = (e.pageX || e.originalEvent.touches[0].pageX) - parseInt(_this.$container.css("margin-left"), 10);
              if (_this.options.animate) {
                _this.$wrapper.removeClass("" + _this.options.baseClass + "-animate");
              }
              return _this.$element.trigger("focus.bootstrapSwitch");
            };
          })(this),
          "mousemove.bootstrapSwitch touchmove.bootstrapSwitch": (function(_this) {
            return function(e) {
              var difference;
              if (_this._dragStart == null) {
                return;
              }
              e.preventDefault();
              difference = (e.pageX || e.originalEvent.touches[0].pageX) - _this._dragStart;
              if (difference < -_this._handleWidth || difference > 0) {
                return;
              }
              _this._dragEnd = difference;
              return _this.$container.css("margin-left", "" + _this._dragEnd + "px");
            };
          })(this),
          "mouseup.bootstrapSwitch touchend.bootstrapSwitch": (function(_this) {
            return function(e) {
              var state;
              if (!_this._dragStart) {
                return;
              }
              e.preventDefault();
              if (_this.options.animate) {
                _this.$wrapper.addClass("" + _this.options.baseClass + "-animate");
              }
              if (_this._dragEnd) {
                state = _this._dragEnd > -(_this._handleWidth / 2);
                _this._dragEnd = false;
                _this.state(_this.options.inverse ? !state : state);
              } else {
                _this.state(!_this.options.state);
              }
              return _this._dragStart = false;
            };
          })(this),
          "mouseleave.bootstrapSwitch": (function(_this) {
            return function(e) {
              return _this.$label.trigger("mouseup.bootstrapSwitch");
            };
          })(this)
        });
      };

      BootstrapSwitch.prototype._externalLabelHandler = function() {
        var $externalLabel;
        $externalLabel = this.$element.closest("label");
        return $externalLabel.on("click", (function(_this) {
          return function(event) {
            event.preventDefault();
            event.stopImmediatePropagation();
            if (event.target === $externalLabel[0]) {
              return _this.toggleState();
            }
          };
        })(this));
      };

      BootstrapSwitch.prototype._formHandler = function() {
        var $form;
        $form = this.$element.closest("form");
        if ($form.data("bootstrap-switch")) {
          return;
        }
        return $form.on("reset.bootstrapSwitch", function() {
          return window.setTimeout(function() {
            return $form.find("input").filter(function() {
              return $(this).data("bootstrap-switch");
            }).each(function() {
              return $(this).bootstrapSwitch("state", this.checked);
            });
          }, 1);
        }).data("bootstrap-switch", true);
      };

      BootstrapSwitch.prototype._getClasses = function(classes) {
        var c, cls, _i, _len;
        if (!$.isArray(classes)) {
          return ["" + this.options.baseClass + "-" + classes];
        }
        cls = [];
        for (_i = 0, _len = classes.length; _i < _len; _i++) {
          c = classes[_i];
          cls.push("" + this.options.baseClass + "-" + c);
        }
        return cls;
      };

      return BootstrapSwitch;

    })();
    $.fn.bootstrapSwitch = function() {
      var args, option, ret;
      option = arguments[0], args = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
      ret = this;
      this.each(function() {
        var $this, data;
        $this = $(this);
        data = $this.data("bootstrap-switch");
        if (!data) {
          $this.data("bootstrap-switch", data = new BootstrapSwitch(this, option));
        }
        if (typeof option === "string") {
          return ret = data[option].apply(data, args);
        }
      });
      return ret;
    };
    $.fn.bootstrapSwitch.Constructor = BootstrapSwitch;
    return $.fn.bootstrapSwitch.defaults = {
      state: true,
      size: null,
      animate: true,
      disabled: false,
      readonly: false,
      indeterminate: false,
      inverse: false,
      radioAllOff: false,
      onColor: "primary",
      offColor: "default",
      onText: "ON",
      offText: "OFF",
      labelText: "&nbsp;",
      handleWidth: "auto",
      labelWidth: "auto",
      baseClass: "bootstrap-switch",
      wrapperClass: "wrapper",
      onInit: function() {},
      onSwitchChange: function() {}
    };
  })(window.jQuery, window);

}).call(this);
