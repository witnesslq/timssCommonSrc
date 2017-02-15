package com.yudean.mvc.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class MvcCharacterEncodingFilter implements Filter {

	class Count {
		Long lastActiveTimestamp;// 最后活跃时间
		private Map<String, Long> pathTimestampMap = null;// 活跃请求

		Count() {
			pathTimestampMap = new HashMap<String, Long>();
			lastActiveTimestamp = 0L;
		}

		boolean compTimestamp(String path) {
			lastActiveTimestamp = System.currentTimeMillis();
			Long time = pathTimestampMap.get(path);
			boolean ret = true;
			if (null == time) {
				pathTimestampMap.put(path, lastActiveTimestamp);
			} else {
				ret = lastActiveTimestamp - time > 1000;
			}
			return ret;
		}
	}

	private static final Logger LOG = Logger.getLogger(MvcCharacterEncodingFilter.class);

	private static Map<String, Count> lockSession;// 锁定SESSION标记

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		lockSession = new HashMap<String, Count>();
	}

	// 拒绝请求，用于防止重复提交。
	private boolean abortCommit(HttpServletRequest request) {
		String path = request.getRequestURI();
		String id = request.getSession().getId();
		Count count = lockSession.get(id);
		if (null == count) {
			count = new Count();
			lockSession.put(id, count);
		}
		boolean ret = true;
		synchronized (count) {
			ret = count.compTimestamp(path);
		}
		return ret;
	}
}
