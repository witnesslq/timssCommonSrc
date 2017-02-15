package com.yudean.homepage.dao;

import java.util.List;

import com.yudean.homepage.bean.WorktaskUserBean;

public interface HomepageWorktaskUserDao {
    /**
     * 增加一条记录
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param work
     * @return:
     */
    int insertWorktaskUser(WorktaskUserBean task);

    /**
     * 根据条件查询工作任务
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param work
     * @return:
     */
    List<WorktaskUserBean> queryWorktaksUser(WorktaskUserBean task);

    /**
     * 修改一条记录
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param work
     * @return:
     */
    int updateWorktaskUser(WorktaskUserBean task);

    /**
     * 删除一条记录
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param id
     * @return:
     */
    int deleteWorktaskUser(Integer id);

    /**
     * 批量提交信息
     * 
     * @param wubList
     * @author: kChen
     * @return
     */
    int updateAndSaveUserBatch(List<WorktaskUserBean> wubList);
}
