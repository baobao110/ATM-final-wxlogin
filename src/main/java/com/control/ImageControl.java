package com.control;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.captcha.Captcha;
import com.captcha.GifCaptcha;

@Controller
public class ImageControl {
	
	@RequestMapping("/picture")
	public void getCaptcha(HttpServletResponse response, HttpSession session) {
		try {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/gif");

			// gif格式动画验证码 宽，高，位数。
			Captcha captcha = new GifCaptcha(146, 33, 6);

			captcha.out(response.getOutputStream());

			session.setAttribute("code", captcha.text().toLowerCase());
		} catch (Exception e) {
		}
	}

}
