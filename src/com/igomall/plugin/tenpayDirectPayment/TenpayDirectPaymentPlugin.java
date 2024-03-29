
package com.igomall.plugin.tenpayDirectPayment;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.igomall.entity.PaymentTransaction;
import com.igomall.entity.PluginConfig;
import com.igomall.plugin.PaymentPlugin;

/**
 * Plugin - 财付通(即时交易)
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Component("tenpayDirectPaymentPlugin")
public class TenpayDirectPaymentPlugin extends PaymentPlugin {

	@Override
	public String getName() {
		return "财付通(即时交易)";
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
		return "tenpay_direct_payment/install";
	}

	@Override
	public String getUninstallUrl() {
		return "tenpay_direct_payment/uninstall";
	}

	@Override
	public String getSettingUrl() {
		return "tenpay_direct_payment/setting";
	}

	@Override
	public void payHandle(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) throws Exception {
		PluginConfig pluginConfig = getPluginConfig();
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("trade_mode", "1");
		parameterMap.put("partner", pluginConfig.getAttribute("partner"));
		parameterMap.put("input_charset", "utf-8");
		parameterMap.put("sign_type", "MD5");
		parameterMap.put("return_url", getPostPayUrl(paymentPlugin, paymentTransaction));
		parameterMap.put("notify_url", getPostPayUrl(paymentPlugin, paymentTransaction, "notify"));
		parameterMap.put("out_trade_no", paymentTransaction.getSn());
		parameterMap.put("subject", StringUtils.abbreviate(paymentDescription.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 30));
		parameterMap.put("body", StringUtils.abbreviate(paymentDescription.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 30));
		parameterMap.put("bank_type", "DEFAULT");
		parameterMap.put("seller_id", pluginConfig.getAttribute("partner"));
		parameterMap.put("total_fee", paymentTransaction.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		parameterMap.put("fee_type", "1");
		parameterMap.put("spbill_create_ip", request.getLocalAddr());
		parameterMap.put("attach", "shopxx");
		parameterMap.put("sign", generateSign(parameterMap));

		modelAndView.addObject("requestUrl", "https://gw.tenpay.com/gateway/pay.htm");
		modelAndView.addObject("parameterMap", parameterMap);
		modelAndView.setViewName(PaymentPlugin.DEFAULT_PAY_VIEW_NAME);

	}

	@Override
	public void postPayHandle(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, boolean isPaySuccess, HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) throws Exception {
		super.postPayHandle(paymentPlugin, paymentTransaction, paymentDescription, extra, isPaySuccess, request, response, modelAndView);

		if (StringUtils.equals(extra, "notify")) {
			modelAndView.addObject("message", "Success");
		}
	}

	@Override
	public boolean isPaySuccess(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PluginConfig pluginConfig = getPluginConfig();
		if (StringUtils.equals(generateSign(request.getParameterMap()), request.getParameter("sign")) && StringUtils.equals(pluginConfig.getAttribute("partner"), request.getParameter("partner")) && StringUtils.equals(request.getParameter("trade_state"), "0")
				&& paymentTransaction.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(request.getParameter("total_fee"))) == 0) {
			try {
				Map<String, Object> parameterMap = new HashMap<>();
				parameterMap.put("input_charset", "utf-8");
				parameterMap.put("sign_type", "MD5");
				parameterMap.put("partner", pluginConfig.getAttribute("partner"));
				parameterMap.put("notify_id", request.getParameter("notify_id"));
				String verifyUrl = "https://gw.tenpay.com/gateway/simpleverifynotifyid.xml?input_charset=utf-8&sign_type=MD5&partner=" + pluginConfig.getAttribute("partner") + "&notify_id=" + request.getParameter("notify_id") + "&sign=" + generateSign(parameterMap);
				Document document = new SAXReader().read(new URL(verifyUrl));
				Node node = document.selectSingleNode("/root/retcode");
				if (StringUtils.equals(node.getText().trim(), "0")) {
					return true;
				}
			} catch (DocumentException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e.getMessage(), e);
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
		return DigestUtils.md5Hex(joinKeyValue(new TreeMap<>(parameterMap), null, "&key=" + pluginConfig.getAttribute("key"), "&", true, "sign")).toUpperCase();
	}

}