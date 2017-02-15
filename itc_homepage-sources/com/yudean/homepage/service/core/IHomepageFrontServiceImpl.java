package com.yudean.homepage.service.core;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.homepage.bean.DeleteDraftParam;
import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.bean.WorktaskBean.WorkTaskClass;
import com.yudean.homepage.bean.WorktaskUserBean;
import com.yudean.homepage.bean.WorktaskUserBean.WorkTaskUserFlag;
import com.yudean.homepage.dao.HomepageWorktaskDao;
import com.yudean.homepage.dao.HomepageWorktaskListDao;
import com.yudean.homepage.dao.HomepageWorktaskUserDao;
import com.yudean.homepage.service.HomepageAnnotService;
import com.yudean.homepage.service.HomepageFrontService;
import com.yudean.homepage.vo.WorktaskFlowViewObj;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.itc.dto.Page;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.mvc.service.ItcMsgService;
import com.yudean.mvc.service.ItcMvcService;

@Service
@Lazy(false)
public class IHomepageFrontServiceImpl implements HomepageFrontService {
	private static final Logger LOG = Logger.getLogger(IHomepageFrontServiceImpl.class);

	@Autowired
	HomepageWorktaskListDao worktaskListDao;

	@Autowired
	HomepageWorktaskDao taskDao;

	@Autowired
	HomepageAnnotService homepageAnnotService;

	@Autowired
	HomepageWorktaskUserDao userDao;

	@Autowired
	ItcMsgService itcMsgService;

	@Autowired
	ItcMvcService itcMvcService;

	@Override
	public Page<WorktaskViewObj> getDoingTaskList(Page<WorktaskViewObj> page, UserInfo userInfo)
			throws RuntimeException {
		List<WorktaskViewObj> list = worktaskListDao.queryDoingWorkTask(page);
		if (null != list && 0 < list.size()) {
			page.setResults(list);
		}
		return page;
	}

	@Override
	public Page<WorktaskViewObj> getTaskList(Page<WorktaskViewObj> page, UserInfo userInfo) throws RuntimeException {
		List<WorktaskViewObj> list = worktaskListDao.queryWorkTask(page);
		if (null != list && 0 < list.size()) {
			page.setResults(list);
		}
		return page;
	}
	
	@Override
	public Page<WorktaskViewObj> getCompleteTaskList(Page<WorktaskViewObj> page, UserInfo userInfo) throws RuntimeException {
		List<WorktaskViewObj> list = worktaskListDao.queryCompleteWorkTask(page);
		if (null != list && 0 < list.size()) {
			page.setResults(list);
		}
		return page;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public Integer deleteTaskList(List<WorktaskViewObj> taskVoList, UserInfo userInfo) throws RuntimeException {
		WorktaskBean task = new WorktaskBean();
		WorktaskUserBean user = new WorktaskUserBean();
		Date curDate = new Date();
		Integer Count = 0;
		for (WorktaskViewObj vo : taskVoList) {
			String flowno = null;
			try {
				vo.getFlowno();
				task.setGroupid(vo.getGroupid());
				task.setSiteid(vo.getSiteid());
				task.setStatusdate(curDate);
				task.setModifydate(curDate);
				task.setModifyuser(userInfo.getUserId());
				task.setModifyusername(userInfo.getUserName());
				task.setClasstype(WorktaskBean.WorkTaskClass.Delete);
				task.setActive(WorktaskBean.ACTIVEFLAG.NO);
				taskDao.updateWorktask(task);
				user.setModifydate(curDate);
				user.setModifyuser(userInfo.getUserId());
				user.setId(vo.getId());
				user.setFlag(WorktaskUserBean.WorkTaskUserFlag.His);
				flowno = notifyModuleWhenDeleteDraft(vo);
				userDao.updateWorktaskUser(user);
				Count++;
			} catch (Exception e) {
				LOG.error("草稿" + flowno + "删除异常!,当前成功数" + Count, e);
			}
		}
		return Count;
	}

	/**
	 * 通知流程对应的业务类删除对应业务数据
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-11-20
	 * @param vo
	 *            :
	 */
	private String notifyModuleWhenDeleteDraft(WorktaskViewObj vo) throws Exception {
		DeleteDraftParam deleteParam = new DeleteDraftParam();
		String flowno = vo.getFlowno();
		deleteParam.setFlowId(flowno);
		deleteParam.setProcessInsId(vo.getExtcode());
		deleteParam.setName(vo.getName());
		deleteParam.setSiteid(vo.getSiteid());
		homepageAnnotService.deleteDraftNotify(deleteParam);
		return flowno;
	}

	@Override
	public void deleteBusinessTask(List<WorktaskViewObj> taskVoList) {
		LOG.error("method deleteBusinessTask has been deprecated");
	}

	@Override
	public WorktaskFlowViewObj getWorkTaskInfoByFlowNo(String flowNo) throws RuntimeException {
		WorktaskFlowViewObj viewObj = new WorktaskFlowViewObj();
		viewObj.setFlowno(flowNo);
		return worktaskListDao.queryWorkTaskFlow(viewObj);
	}

	@Override
	public int getUserProcessTaskCount(String userId, String siteId, UserInfo userInfo) throws RuntimeException {
		return worktaskListDao.queryWorkTakskCount(userId, siteId, WorkTaskClass.Processed, WorkTaskUserFlag.Cur);
	}
	
	@Override
        public int getUserTaskStaticCount(String userId, String siteId, WorkTaskClass type ,WorkTaskUserFlag flag) throws RuntimeException {
	    return worktaskListDao.queryWorkTakskCount(userId, siteId, type, flag);
        }
}
