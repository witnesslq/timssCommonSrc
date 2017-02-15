package com.yudean.itc.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yudean.itc.SecurityBeanHelper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.manager.sec.IAuthorizationManager;
import com.yudean.itc.manager.sec.ISecurityMaintenanceManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class BaseServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(BaseServlet.class);
	private static HashMap<String,String> contentTypes;
	static{
		contentTypes = new HashMap<String, String>();
		contentTypes.put("ppt", "application/ms-powerpoint");
		contentTypes.put("pptx", "application/ms-powerpoint");
		contentTypes.put("jpg", "image/jpeg");
		contentTypes.put("jpeg", "image/jpeg");
		contentTypes.put("gif", "image/gif");
		contentTypes.put("png", "image/png");
		contentTypes.put("pdf", "application/pdf");
		contentTypes.put("doc", "application/ms-word");
		contentTypes.put("docx", "application/ms-word");
	}
	/**
	 * 从post/get中获取int参数
	 * 
	 * @param request
	 * @param pname
	 *            参数名
	 * @param defVal
	 *            无参数时的默认值
	 * @return
	 */
	public Integer getIntegerParam(HttpServletRequest request, String pname, Integer defVal) {
		String s = request.getParameter(pname);
		try {
			return Integer.parseInt(s);
		} catch (Exception ex) {
			return defVal;
		}
	}

	/**
	 * 输出一个有文字描述但是没有具体数据的结果，如删除、新增、登陆结果
	 * 
	 * @param response
	 */
	public void outputStatus(HttpServletResponse response, int code, String msg) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("status", code);
		hm.put("msg", msg);
		outputJson(response, hm);
	}

	/**
	 * 输出一个有文字描述和附加数据的JSON
	 * 
	 * @param response
	 */
	public void outputStatusData(HttpServletResponse response, int code, String msg, Object data) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("status", code);
		hm.put("msg", msg);
		hm.put("data", data);
		outputJson(response, hm);
	}

	/**
	 * 输出一个空表格json数据，用于查询失败或者无数据情况
	 * 
	 * @param response
	 */
	public void outputEmptyTable(HttpServletResponse response) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("totalCount", 0);
		hm.put("currPage", 0);
		hm.put("data", new ArrayList<String>());
		outputJson(response, hm);
	}

	/**
	 * JSON输出（该输出在浏览器端需要parse）
	 * 
	 * @param response
	 * @param data
	 */
	public void outputJson(HttpServletResponse response, Object data) {

		response.setContentType("text/html;contentType=utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			JSONObject jst = JSONObject.fromObject(data);
			out.print(jst.toString().replaceAll("clazz", "class"));
		} catch (Exception ex) {
			out.print("{'result':'-1'}");
		}
		out.close();
	}

	/**
	 * 将json对象转换为字符串
	 * 
	 * @param o
	 * @return
	 */
	public String obj2Str(Object o) {
		if (o instanceof List) {
			JSONArray arr = JSONArray.fromObject(o);
			return arr.toString();
		}
		JSONObject obj = JSONObject.fromObject(o);
		return obj.toString();
	}

	/**
	 * 将a b c d e之类的字符串转为{a:true,b:true....}形式的Hash表，用于过滤
	 * 
	 * @param request
	 * @return
	 */
	public HashMap<String, Boolean> buildOwndMap(HttpServletRequest request) {
		String owned = request.getParameter("owned");
		if (owned != null) {
			owned = owned.trim();
		}
		String[] ownedSplit = owned.split(" ");
		HashMap<String, Boolean> hm = new HashMap<String, Boolean>();
		for (String k : ownedSplit) {
			hm.put(k, true);
		}
		return hm;
	}

	public <T extends Object> Page<T> getPager(HttpServletRequest request) {
		Integer pgNum = getIntegerParam(request, "page", 1);
		Integer pgCnt = getIntegerParam(request, "rows", 15);
		Page<T> page = new Page<T>();
		page.setPageNo(pgNum);
		page.setPageSize(pgCnt);
		return page;
	}

	public ISecurityMaintenanceManager getMtManager() {
		SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
		ISecurityMaintenanceManager manager = helper.getBean(ISecurityMaintenanceManager.class);
		return manager;
	}

	public IAuthorizationManager getAuzManager() {
		SecurityBeanHelper helper = SecurityBeanHelper.getInstance();
		IAuthorizationManager manager = helper.getBean(IAuthorizationManager.class);
		return manager;
	}

	public <T extends Object> HashMap<String, Object> wrapResultWithPage(Page<T> p) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("total", p.getTotalRecord());
		return result;
	}

	public void outputMsg(HttpServletResponse response, String msg) {
		response.setContentType("text/html;contentType=utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			out.close();
		}
	}

	public void printRequest(HttpServletRequest request) {
		@SuppressWarnings("rawtypes")
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			// String s = (String) e.nextElement();
			// System.out.println(s + " => " + request.getParameter(s));
		}
	}

	public String trimStrToNull(String s) {
		if (s == null) {
			return null;
		} else {
			s = s.trim();
			if (s.length() == 0) {
				return null;
			} else {
				return s;
			}
		}
	}

	public void goDownload(File file,String fileName,boolean isOpen,HttpServletRequest request,HttpServletResponse response){
		FileInputStream inStream = null;
		OutputStream outStream = null;
		if (file.exists()) {
			try{
				inStream = new FileInputStream(file);
				String extFn = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
				String cDispotion = isOpen?"inline":"attachment";

				//这里注意不改文件名编码会乱码
				String agent = request.getHeader("User-Agent");
				if(agent!=null){
					agent = agent.toLowerCase();
				}
				//liek gecko是ie11的头
				if(agent==null || agent.indexOf("msie")>0){
					response.addHeader("Content-Disposition", cDispotion + ";filename=\"" + URLEncoder.encode(fileName, "UTF8").replace("+", "%20") + "\"");
				}
				else{
					response.addHeader("Content-Disposition", cDispotion + ";filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF8").replace("+", "%20"));
				}

		        response.addHeader("Content-Length" ,  "" + file.length());
		        if(isOpen==true){
		        	String cType = contentTypes.get(extFn);
					if(cType==null){
						cType = "application/octet-stream";
					}
					response.setContentType(cType);
		        }
		        else{
		        	response.setContentType("application/octet-stream");
		        }
		        outStream = response.getOutputStream();
		        byte[] buffer = new byte[4096];
		        int bytesRead = -1;		        
		        while ((bytesRead = inStream.read(buffer)) != -1) {
		            outStream.write(buffer, 0, bytesRead);
		        }		        
			}
			catch(Exception ex){
				ex.printStackTrace();				
			}
			finally{
		        try {
		        	if(inStream!=null){
		        		inStream.close();
		        	}
					if(outStream!=null){
						outStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}		        
			}
		}
		else{
			outputMsg(response, "这个文件在数据库中存在，但是在物理磁盘被删除，请联系系统管理员处理此问题");
		}
	}

	public void outputImage(File file, HttpServletResponse response){
		FileInputStream inStream = null;
		OutputStream outStream = null;
		if(!file.exists()){
			return;
		}
		String fn = file.getName();
		String extFn = fn.substring(fn.lastIndexOf(".")+1).toLowerCase();
		String cType = "";
		if(extFn.equals("png")){
			cType = contentTypes.get("png");
		}
		response.setContentType(cType);
		try{
			inStream = new FileInputStream(file);
			outStream = response.getOutputStream();
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
		}catch (Exception ex){
			logger.error("Error when output image:", ex);
		}
	}
}
