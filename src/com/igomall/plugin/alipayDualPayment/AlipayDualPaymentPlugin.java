
package com.igomall.plugin.alipayDualPayment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.igomall.Setting;
import com.igomall.entity.PaymentTransaction;
import com.igomall.entity.PluginConfig;
import com.igomall.plugin.PaymentPlugin;
import com.igomall.util.SystemUtils;
import com.igomall.util.WebUtils;

/**
 * Plugin - 支付宝(双接口)
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Component("alipayDualPaymentPlugin")
public class AlipayDualPaymentPlugin extends PaymentPlugin {

	@Override
	public String getName() {
		return "支付宝(双接口)";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "SHOP++";
	}

	@Override
	public String getSiteUrl() {
		return "";
	}

	@Override
	public String getInstallUrl() {
		return "alipay_dual_payment/install";
	}

	@Override
	public String getUninstallUrl() {
		return "alipay_dual_payment/uninstall";
	}

	@Override
	public String getSettingUrl() {
		return "alipay_dual_payment/setting";
	}

	@Override
	public void payHandle(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) throws Exception {
		Setting setting = SystemUtils.getSetting();
		PluginConfig pluginConfig = getPluginConfig();
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("service", "trade_create_by_buyer");
		parameterMap.put("partner", pluginConfig.getAttribute("partner"));
		parameterMap.put("_input_charset", "utf-8");
		parameterMap.put("sign_type", "MD5");
		parameterMap.put("return_url", getPostPayUrl(paymentPlugin, paymentTransaction));
		parameterMap.put("notify_url", getPostPayUrl(paymentPlugin, paymentTransaction, "notify"));
		parameterMap.put("out_trade_no", paymentTransaction.getSn());
		parameterMap.put("subject", StringUtils.abbreviate(paymentDescription.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 60));
		parameterMap.put("body", StringUtils.abbreviate(paymentDescription.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 600));
		parameterMap.put("payment_type", "1");
		parameterMap.put("logistics_type", "EXPRESS");
		parameterMap.put("logistics_fee", "0");
		parameterMap.put("logistics_payment", "SELLER_PAY");
		parameterMap.put("price", paymentTransaction.getAmount().setScale(2).toString());
		parameterMap.put("quantity", "1");
		parameterMap.put("seller_id", pluginConfig.getAttribute("partner"));
		parameterMap.put("total_fee", paymentTransaction.getAmount().setScale(2).toString());
		parameterMap.put("show_url", setting.getSiteUrl());
		parameterMap.put("paymethod", "directPay");
		parameterMap.put("extend_param", "isv^1860648a1");
		parameterMap.put("exter_invoke_ip", request.getLocalAddr());
		parameterMap.put("extra_common_param", "shopxx");
		parameterMap.put("sign", generateSign(parameterMap));

		modelAndView.addObject("requestUrl", "https://mapi.alipay.com/gateway.do");
		modelAndView.addObject("parameterMap", parameterMap);
		modelAndView.setViewName(PaymentPlugin.DEFAULT_PAY_VIEW_NAME);
	}

	@Override
	public void postPayHandle(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, boolean isPaySuccess, HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) throws Exception {
		super.postPayHandle(paymentPlugin, paymentTransaction, paymentDescription, extra, isPaySuccess, request, response, modelAndView);

		if (StringUtils.equals(extra, "notify")) {
			modelAndView.addObject("message", "success");
		}
	}

	@Override
	public boolean isPaySuccess(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PluginConfig pluginConfig = getPluginConfig();
		if (StringUtils.equals(generateSign(request.getParameterMap()), request.getParameter("sign")) && StringUtils.equals(pluginConfig.getAttribute("partner"), request.getParameter("seller_id"))
				&& (StringUtils.equals(request.getParameter("trade_status"), "WAIT_SELLER_SEND_GOODS") || StringUtils.equals(request.getParameter("trade_status"), "TRADE_SUCCESS") || StringUtils.equals(request.getParameter("trade_status"), "TRADE_FINISHED"))
				&& paymentTransaction.getAmount().compareTo(new BigDecimal(request.getParameter("total_fee"))) == 0) {
			Map<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("service", "notify_verify");
			parameterMap.put("partner", pluginConfig.getAttribute("partner"));
			parameterMap.put("notify_id", request.getParameter("notify_id"));

			String result = WebUtils.post("https://mapi.alipay.com/gateway.do", parameterMap);
			if (StringUtils.equals(result, "true")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 生成签名
	 * 
	 * @param parameterMap
	 *            参数
	 * @return 签名
	 */
	private String generateSign(Map<String, ?> parameterMap) {
		PluginConfig pluginConfig = getPluginConfig();
		return DigestUtils.md5Hex(joinKeyValue(new TreeMap<>(parameterMap), null, pluginConfig.getAttribute("key"), "&", true, "sign_type", "sign"));
	}

}