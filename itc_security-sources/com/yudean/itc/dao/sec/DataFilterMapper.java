package com.yudean.itc.dao.sec;

import java.util.List;

import com.yudean.itc.dto.sec.DataFilterRule;

public interface DataFilterMapper {

	List<DataFilterRule> selectFilterByFunction(String functionId);
}
