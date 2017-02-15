/**
 * 工作任务模块，前端接口
 */
var homepageService = {
	/**
	 * 刷新工作任务列表
	 */
	refresh : null
};
$(document).ready(function() {
	homepageService.refresh = function() {
		var _iframejq = $(window.parent.document).find("#navtab_homepage");
		for ( var i = 0; i < _iframejq.length; i++) {
			var _iframeNodeDom = _iframejq.get(i).contentWindow;
			var _impl = _iframeNodeDom.homepageServiceImpl;
			if (null != _impl && "undefined" != typeof _impl) {
				_impl.refresh();
			}
		}
	};
});