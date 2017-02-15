package com.yudean.mvc.scheduler.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dao.support.AttachmentMapper;
import com.yudean.itc.dto.sec.SecureUser;
import com.yudean.itc.dto.support.Attachment;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.manager.support.IAttachmentManager;
import com.yudean.itc.manager.support.IConfigurationManager;
import com.yudean.itc.util.ApplicationConfig;
import com.yudean.itc.util.Constant;

/**
 * 附件清理计划任务
 * @author 890157
 */
@Component
@Lazy(false)
public class AttachmentCleaningScheduler {
	private static Logger logger = Logger.getLogger(AttachmentCleaningScheduler.class);
	private static final int MARK_DEL_STEP = 5;
	
	@Autowired
	private AttachmentMapper attachMapper;
	
	@Autowired
	private IAttachmentManager attachManager;
	
	@Autowired
	private IConfigurationManager confManager;
	
	private static String recyclePath = null;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	
	static{
		recyclePath = ApplicationConfig.getConfig("upload.recycle.dir");
	}
	
	//@Scheduled(cron="0 0 23 5 * ?")
	public void cleanAttachment() {
		cleanUnbindedAttachments();
		cleanUnusedAttachments();
	}
	
	
	/**
	 * 清理未绑定的附件
	 */
	public void cleanUnbindedAttachments(){
		logger.info("正在清除未绑定的附件");
		List<Attachment> attachments = attachMapper.selectUnbindedAttachments();
		StringBuilder sb = new StringBuilder("");
		sb.append("未被绑定的附件：\r\n");
		sb.append("---------------------------------------\r\n");
		logger.info("共找到未绑定的附件：" + attachments.size() + "个");
		List<String> unBindedIds = new ArrayList<String>();
		for(Attachment attach:attachments){
			Attachment realAttach = attachManager.retrieveAttachment(Constant.basePath, attach.getId());
			if(null != realAttach){
				File file = realAttach.getFile();
				moveFileToRecyleBin(file);
				unBindedIds.add(attach.getId());
				sb.append(file.getAbsolutePath() + "\r\n");
			}	
		}
		setAttachmentFlagDelete(unBindedIds);		
		writeCleaningResult(sb);
		logger.info("未绑定的附件清理完毕");
	}
	
	/**
	 * 分组将附件id设置为删除标记 每MARK_DEL_STEP个id一组
	 * @param ids
	 */
	private void setAttachmentFlagDelete(List<String> ids){
		int lSize = ids.size();
		int rCnt = lSize/MARK_DEL_STEP;
		for(int i=0;i<=rCnt;i++){
			int remainSize = lSize - i * MARK_DEL_STEP; 
			int arrSize = remainSize>=MARK_DEL_STEP?MARK_DEL_STEP:remainSize;
			String[] sArr = new String[arrSize];
			for(int j=0;j<arrSize;j++){
				sArr[j] = ids.get(i*MARK_DEL_STEP + j);
			}
			attachMapper.setAttachmentsBinded(sArr, -1);
		}
		
	}
	
	/**
	 * 扫描整个数据表 找到未使用的附件
	 */
	public void cleanUnusedAttachments(){
		//扫描所有业务表，找到已被绑定的附件ID
		Configuration conf = new Configuration();
		conf.setConf("attachment.scan.conf");
		SecureUser operator = new SecureUser();
		operator.setId("CLEANING-SCHEDULE");
		operator.setActive(StatusCode.YES);		
		List<Configuration> configList = confManager.query(conf, operator);
		if(null == configList || configList.size() == 0){
			logger.error("没有有效的表配置attachment.scan.conf，附件清理取消");
			return;
		}		
		Set<String> bindedAttach = new HashSet<String>(); 
		//扫描所有的表 获取已经用于业务的附件ID 每个配置项的格式为列名@表名 多个配置项逗号分隔
		String val = configList.get(0).getVal();
		String[] valSplit = val.split(",");
		for(String s : valSplit){
			logger.info("正在扫描" + s);
			String[] sSplit = s.split("@");
			if(sSplit.length != 2){
				continue;
			}
			List<String> idList = null;
			try{
				idList = attachMapper.selectBindings(sSplit[0], sSplit[1]);
			}
			catch(Exception ex){
				logger.error("配置项" + s + "不正确，无法获取附件ID信息");
				continue;
			}
			for(String id : idList){
				if(!bindedAttach.contains(id)){
					bindedAttach.add(id);
				}
			}			
		}
		logger.info("共找到" + bindedAttach.size() + "个已经绑定的附件");
		//获取附件表中所有的id
		List<String> allAttachIds = attachMapper.selectAllAttachmentId();
		logger.info("附件表b_attachment中共有附件" + allAttachIds.size() + "个");
		//将已绑定id和附件表id对比
		List<String> unusedIds = new ArrayList<String>();
		for(String id : allAttachIds){
			if(!bindedAttach.contains(id)){
				unusedIds.add(id);
			}
		}
		logger.info("共找到" + unusedIds.size() + "个未被使用的附件");
		setAttachmentFlagDelete(unusedIds);
		for(String s : unusedIds){
			Attachment realAttach = attachManager.retrieveAttachment(Constant.basePath, s);
			if(null != realAttach){
				moveFileToRecyleBin(realAttach.getFile());
			}
		}
		logger.info("未使用附件清理完毕");
	}
	
	/**
	 * 扫描整个目录 找到数据表中已经删除但是磁盘上依然存在的文件
	 * 这是一种维护操作 一般不需要执行该计划任务
	 */
	public void cleanUnlinkedAttachments(String uploadFolder){
		logger.info("正在扫描附件目录......");
		List<File> files = new ArrayList<File>();
		File baseDir = new File(uploadFolder);
		walkDir(baseDir, files);
		logger.info("附件目录扫描完毕，共发现" + files.size() + "个文件");
		StringBuilder sb = new StringBuilder("");
		sb.append("在目录中存在但是不在b_attachment中的文件：\r\n");
		sb.append("----------------------------------------\r\n");
		for(File file : files){
			Attachment attach = attachMapper.selectById(file.getName());
			if(null == attach){
				sb.append(file.getAbsolutePath() + "\r\n");
				moveFileToRecyleBin(file);
			}
		}
		writeCleaningResult(sb);
	}
	
	private void moveFileToRecyleBin(File srcFile){
		try{
			if(srcFile.exists()){
				FileUtils.moveToDirectory(srcFile, new File(recyclePath), true);
				srcFile.delete();
			}
		}
		catch(Exception ex){
			logger.error("无法将文件" + srcFile.getName() + "移动到回收站，请确定附件回收站所在目录未满，以及当前用户有磁盘的写权限");
			return;
		}
	}
	
	private void writeCleaningResult(StringBuilder result){
		String logPath = recyclePath;
		if(!logPath.endsWith("\\") || !logPath.endsWith("/")){
			logPath += File.separator + "cleaninglog_" + sdf.format(new Date());
			try{
				FileUtils.write(new File(logPath), result.toString());
			}
			catch(Exception ex){
				logger.error("清理日志" + logPath + "无法写入磁盘，请确定日志空间是否已满，以及当前用户有磁盘的写权限");
			}
		}
	}
	
	private void walkDir(File dir,List<File> files){
		File[] subFiles = dir.listFiles();
		if(subFiles == null){
			return;
		}
		for(File subFile : subFiles){
			if(subFile.isDirectory()){
				walkDir(subFile, files);
			}
			else{
				String fn = subFile.getName();
				if(!fn.endsWith("log") && !fn.endsWith("del")){
					files.add(subFile);
				}
			}
		}
	}
}
