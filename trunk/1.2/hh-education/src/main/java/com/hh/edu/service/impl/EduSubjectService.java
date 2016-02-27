 package com.hh.edu.service.impl;
import com.hh.system.service.impl.BaseService;
import com.hh.system.util.Check;
import com.hh.system.util.dto.PageRange;
import com.hh.system.util.dto.PagingData;
import com.hh.system.util.dto.ParamFactory;
import com.hh.system.util.dto.ParamInf;

import org.springframework.stereotype.Service;

import com.hh.edu.bean.EduSubject;

@Service
public class EduSubjectService extends BaseService<EduSubject> {

	@Override
	public PagingData<EduSubject> queryPagingData(EduSubject entity,
			PageRange pageRange) {
		ParamInf paramInf = ParamFactory.getParamHb();
		if (Check.isNoEmpty(entity.getType())) {
			paramInf.is("type", entity.getType());
		}
		if (Check.isNoEmpty(entity.getText())) {
			paramInf.like("text", entity.getText());
		}
		return super.queryPagingData(entity, pageRange,paramInf);
	}
	
}
 