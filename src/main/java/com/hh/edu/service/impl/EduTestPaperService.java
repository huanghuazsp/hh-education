package com.hh.edu.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hh.edu.bean.EduSubject;
import com.hh.edu.bean.EduTestPaper;
import com.hh.system.service.impl.BaseService;
import com.hh.system.util.Check;
import com.hh.system.util.Convert;
import com.hh.system.util.Json;
import com.hh.system.util.MessageException;
import com.hh.system.util.Random;
import com.hh.system.util.dto.PageRange;
import com.hh.system.util.dto.PagingData;
import com.hh.system.util.dto.ParamFactory;
import com.hh.system.util.dto.ParamInf;

@Service
public class EduTestPaperService extends BaseService<EduTestPaper> {

	@Autowired
	private EduSubjectService eduSubjectService;

	@Override
	public PagingData<EduTestPaper> queryPagingData(EduTestPaper entity, PageRange pageRange) {
		ParamInf paramInf = ParamFactory.getParamHb();
		if (Check.isNoEmpty(entity.getType())) {
			paramInf.is("type", entity.getType());
		}
		if (Check.isNoEmpty(entity.getText())) {
			paramInf.like("text", entity.getText());
		}
		return super.queryPagingData(entity, pageRange, paramInf);
	}

	@Transactional
	public void generate(EduTestPaper object, String titleType) {

		object.setHead("<div style=\"text-align: center;\"><span style=\"font-size:36px;\">" + object.getText()
				+ "</span></div>");
		List<Map<String, Object>> mapList = Json.toMapList(object.getDataitems());
		List<Map<String, Object>> newMapList = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map : mapList) {
			String type = Convert.toString(map.get("type"));
			int subjectcount = eduSubjectService
					.findCount(ParamFactory.getParamHb().is("type", titleType).is("titleType", type));

			int subjectCount = Convert.toInt(map.get("subjectCount"));
			int score = Convert.toInt(map.get("score"));

			int[] randoms = Random.randomCommon(1, subjectcount+1, subjectCount);

			if(randoms==null){
				throw new MessageException("您所选的题目类型题目不足，请到题目管理中添加题目！");
			}
			StringBuffer subjectStrs = new StringBuffer("");

			for (int i : randoms) {
				List<EduSubject> eduSubjects = eduSubjectService.queryList(
						ParamFactory.getParamHb().is("type", titleType).is("titleType", type), new PageRange(i-1, 1));
				subjectStrs.append(eduSubjects.get(0).getId() + ",");

			}
			String str = subjectStrs.toString();
			if (Check.isNoEmpty(str)) {
				str = str.substring(0, str.length() - 1);
			}

			String title = "";

			if ("radio".equals(type)) {
				title = "单选题";
			} else if ("check".equals(type)) {
				title = "多选题";
			} else if ("fillEmpty".equals(type)) {
				title = "填空题";
			} else if ("shortAnswer".equals(type)) {
				title = "简答题";
			}
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.put("score", score);
			newMap.put("subjects", str);
			newMap.put("title", title);
			newMapList.add(newMap);
		}
		
		object.setDataitems(Json.toStr(newMapList));
		save(object);
	}
}