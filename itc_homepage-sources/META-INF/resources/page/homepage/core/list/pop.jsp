<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<ul class="dropdown-menu">
	<li>
		<a href="javascript:homepageServiceImpl.newProcessTab('purchase/purapply/purApplyForm.do?type=new_single&sheetId=', 'PA', '采购申请');">采购申请</a>
	</li>
	<li style="border-bottom: 1px solid rgba(0, 0, 0, 0.15);">
		<a href="javascript:homepageServiceImpl.newProcessTab('purchase/purorder/purOrderForm.do?type=new&sheetId=', 'PO', '采购单');">采购单</a>
	</li>
	<li style="border-bottom: 1px solid rgba(0, 0, 0, 0.15);">
		<a href="javascript:homepageServiceImpl.newProcessTab('inventory/invmatapply/invMatApplyForm.do?imaid=', 'IMS', '领料');">领料</a>
	</li>
	<li >
		<a href="javascript:homepageServiceImpl.newProcessTab('itsm/workorder/homepageOpenNewWOPage.do', 'ITMS', 'IT服务工单');">IT服务工单</a>
	</li>
</ul>