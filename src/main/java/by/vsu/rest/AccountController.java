package by.vsu.rest;

import by.vsu.di.ServiceFactory;
import by.vsu.domain.Account;
import by.vsu.exception.ApplicationException;
import by.vsu.model.service.AccountService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/account")
public class AccountController extends HttpServlet {
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			ServiceFactory.init();
		} catch(ClassNotFoundException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			AccountService accountService = ServiceFactory.getAccountService();
			List<Account> accounts = accountService.showAll();
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.writeValue(resp.getWriter(), accounts);
		} catch(ApplicationException e) {
			throw new ServletException(e);
		}
	}
}
