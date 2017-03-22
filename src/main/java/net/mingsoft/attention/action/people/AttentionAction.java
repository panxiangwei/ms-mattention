/**
 * 
 */
package net.mingsoft.attention.action.people;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mingsoft.basic.entity.BasicEntity;
import com.mingsoft.people.action.BaseAction;
import com.mingsoft.people.entity.PeopleEntity;
import com.mingsoft.util.StringUtil;

import net.mingsoft.attention.biz.IBasicAttentionBiz;
import net.mingsoft.attention.constant.ModelCode;
import net.mingsoft.attention.entity.BasicAttentionEntity;
import net.mingsoft.basic.util.BasicUtil;

/**
 * 
 * 铭飞MS平台－关注插件
 * 
 * @author 铭飞开发团队
 * @version 版本号：100-000-000<br/>
 *          创建日期：2015年3月20日<br/>
 *          历史修订：<br/>
 */
@Controller("peopleAttention")
@RequestMapping("/people/attention")
public class AttentionAction extends BaseAction {

	/**
	 * 注入关注业务层
	 */
	@Autowired
	private IBasicAttentionBiz basicAttentionBiz;

	/**
	 * 新增关注
	 * 
	 * @param basicAttention
	 *            <i>basicAttention参数包含字段信息参考：</i><br/>
	 *            basicAttentionBasicId 信息编号<br/>
	 *            basicAttentionType 关注类型 具体平台也可以根据自身的规则定义
	 *            <dt><span class="strong">返回</span></dt><br/>
	 *            {code:"错误编码",<br/>
	 *            result:"true｜false",<br/>
	 *            resultMsg:"错误信息", <br/>
	 *            }
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public void save(@ModelAttribute BasicAttentionEntity basicAttention, HttpServletRequest request,
			HttpServletResponse response) {
		if (basicAttention == null || basicAttention.getBasicAttentionBasicId() == 0
				|| basicAttention.getBasicAttentionType() == 0) {
			this.outJson(response, ModelCode.ATTENTION, false);
			return;
		}
		// 获取用户session
		PeopleEntity people = (PeopleEntity) this.getPeopleBySession(request);
		basicAttention.setBasicAttentionPeopleId(people.getPeopleId());
		// 获取APPID
		basicAttention.setBasicAttentionAppId(BasicUtil.getAppId());
		basicAttention.setBasicAttentionType(basicAttention.getBasicAttentionType());

		// 检查是否已经存在
		BasicAttentionEntity basicAttentionEntity = this.basicAttentionBiz.getEntityByPeopleAttention(basicAttention);
		if (basicAttentionEntity != null) {
			this.outJson(response, ModelCode.ATTENTION, false);
			return;
		}

		this.basicAttentionBiz.saveEntity(basicAttention);
		this.outJson(response, ModelCode.ATTENTION, true);
	}

	/**
	 * 判断用户是否关注过信息 <br/>
	 * <i>basicAttention参数包含字段信息参考：</i><br/>
	 * basicAttentionBasicId 信息编号<br/>
	 * basicAttentionType 关注类型 具体平台也可以根据自身的规则定义 ，
	 * 
	 * <dt><span class="strong">返回</span></dt><br/>
	 * {code:"错误编码",<br/>
	 * result:"true存在｜false不存在",<br/>
	 * resultMsg:"错误信息", <br/>
	 * }
	 */
	@RequestMapping("/isExists")
	@ResponseBody
	public void isExists(@ModelAttribute BasicAttentionEntity basicAttention, HttpServletRequest request,
			HttpServletResponse response) {
		if (basicAttention == null || basicAttention.getBasicAttentionBasicId() == 0
				|| basicAttention.getBasicAttentionType() == 0) {
			this.outJson(response, ModelCode.ATTENTION, false);
			return;
		}
		// 获取用户session
		PeopleEntity people = (PeopleEntity) this.getPeopleBySession(request);
		basicAttention.setBasicAttentionPeopleId(people.getPeopleId());
		// 获取APPID
		basicAttention.setBasicAttentionAppId(BasicUtil.getAppId());
		basicAttention.setBasicAttentionType(basicAttention.getBasicAttentionType());

		BasicAttentionEntity basicAttentionEntity = this.basicAttentionBiz.getEntityByPeopleAttention(basicAttention);
		if (basicAttentionEntity == null || basicAttentionEntity.getBasicAttentionId() == 0) {
			this.outJson(response, ModelCode.ATTENTION, false);
		} else {
			this.outJson(response, ModelCode.ATTENTION, true);
		}
	}

	/**
	 * 删除关注
	 * 
	 * @param basic
	 *            <i>basic参数包含字段信息参考：</i><br/>
	 *            basicId 信息编号集合，多个编号用逗号隔开,例如 1,2,3,4
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public void delete( HttpServletRequest request, HttpServletResponse response) {
		// 否则执行多选删除方法
		int[] ids = BasicUtil.getInts("basicId",",");
		// 删除多条评论
		this.basicAttentionBiz.delete(ids, this.getPeopleBySession(request).getPeopleId());
	}
}