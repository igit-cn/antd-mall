
package com.igomall.controller.shop;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.igomall.Pageable;
import com.igomall.entity.Brand;
import com.igomall.exception.ResourceNotFoundException;
import com.igomall.service.BrandService;

/**
 * Controller - 品牌
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Controller("shopBrandController")
@RequestMapping("/brand")
public class BrandController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 40;

	@Inject
	private BrandService brandService;

	/**
	 * 列表
	 */
	@GetMapping("/list/{pageNumber}")
	public String list(@PathVariable Integer pageNumber, ModelMap model) {
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", brandService.findPage(pageable));
		return "shop/brand/list";
	}

	/**
	 * 详情
	 */
	@GetMapping("/detail/{brandId}")
	public String detail(@PathVariable Long brandId, ModelMap model) {
		Brand brand = brandService.find(brandId);
		if (brand == null) {
			throw new ResourceNotFoundException();
		}
		model.addAttribute("brand", brand);
		return "shop/brand/detail";
	}

}