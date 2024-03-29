
package com.igomall.controller.admin;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.igomall.Message;
import com.igomall.Pageable;
import com.igomall.entity.FriendLink;
import com.igomall.service.FriendLinkService;

/**
 * Controller - 友情链接
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Controller("adminFriendLinkController")
@RequestMapping("/admin/friend_link")
public class FriendLinkController extends BaseController {

	@Inject
	private FriendLinkService friendLinkService;

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		model.addAttribute("types", FriendLink.Type.values());
		return "admin/friend_link/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(FriendLink friendLink, RedirectAttributes redirectAttributes) {
		if (!isValid(friendLink)) {
			return ERROR_VIEW;
		}
		if (FriendLink.Type.text.equals(friendLink.getType())) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			return ERROR_VIEW;
		}
		friendLinkService.save(friendLink);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("types", FriendLink.Type.values());
		model.addAttribute("friendLink", friendLinkService.find(id));
		return "admin/friend_link/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(FriendLink friendLink, RedirectAttributes redirectAttributes) {
		if (!isValid(friendLink)) {
			return ERROR_VIEW;
		}
		if (FriendLink.Type.text.equals(friendLink.getType())) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			return ERROR_VIEW;
		}
		friendLinkService.update(friendLink);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", friendLinkService.findPage(pageable));
		return "admin/friend_link/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		friendLinkService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}