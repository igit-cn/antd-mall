
package com.igomall.controller.admin;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.igomall.TemplateConfig;
import com.igomall.service.TemplateService;
import com.igomall.util.SystemUtils;

/**
 * Controller - 模板
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Controller("adminTemplateController")
@RequestMapping("/admin/template")
public class TemplateController extends BaseController {

	@Inject
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Inject
	private TemplateService templateService;

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(String id, ModelMap model) {
		if (StringUtils.isEmpty(id)) {
			return ERROR_VIEW;
		}
		model.addAttribute("templateConfig", SystemUtils.getTemplateConfig(id));
		model.addAttribute("content", templateService.read(id));
		return "admin/template/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(String id, String content, RedirectAttributes redirectAttributes) {
		if (StringUtils.isEmpty(id) || content == null) {
			return ERROR_VIEW;
		}
		templateService.write(id, content);
		freeMarkerConfigurer.getConfiguration().clearTemplateCache();
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(TemplateConfig.Type type, ModelMap model) {
		model.addAttribute("type", type);
		model.addAttribute("types", TemplateConfig.Type.values());
		model.addAttribute("templateConfigs", SystemUtils.getTemplateConfigs(type));
		return "admin/template/list";
	}

}