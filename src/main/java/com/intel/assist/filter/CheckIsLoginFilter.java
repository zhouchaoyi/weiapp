package com.intel.assist.filter;

import com.intel.assist.model.entity.LoginUser;
import com.intel.assist.web.services.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * kunpeng
 * 20150427
 */
public class CheckIsLoginFilter implements Filter {
    private static Logger log = Logger.getLogger(CheckIsLoginFilter.class);

    public void destroy() {
        log.debug("destroy CheckIsLoginFilter");
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String strContextPath = request.getContextPath();
        String uri = request.getRequestURI();



        uri = uri.substring(strContextPath.length());

        String sessionId = request.getParameter("sessionId");
        if(StringUtils.isBlank(sessionId)){
                if (!(uri.startsWith("/login") || StringUtils.equalsIgnoreCase("/",uri))) {
                        HttpSession session = request.getSession();
                        if (null != session) {
                            LoginUser u = (LoginUser) session.getAttribute("loginUSer");
                            if (null == u) {
                                response.sendRedirect(strContextPath + "/");
                                log.debug("Check User Faild! This request will redirect to login.jsp");
                                return;
                            }
                        } else {
                            response.sendRedirect(strContextPath + "/");
                        }
                }
        }else{
            if(!new LoginService().isExistSession(sessionId)){
                response.sendRedirect(strContextPath + "/");
                log.debug("Check User Faild! This request will redirect to login.jsp");
                return;
            }
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
        log.debug("init CheckIsLoginFilter. nothing need to do.");
    }

}
