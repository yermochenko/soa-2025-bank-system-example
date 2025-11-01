package by.vsu.rest;

import by.vsu.di.ServiceFactory;
import by.vsu.domain.Account;
import by.vsu.exception.ApplicationException;
import by.vsu.exception.NonZeroBalanceException;
import by.vsu.exception.NotActiveException;
import by.vsu.exception.NotFoundException;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

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
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		try(ServiceFactory factory = new ServiceFactory()) {
			AccountService accountService = factory.getAccountService();
			String number = req.getParameter("number");
			if(number != null) {
				Optional<Account> account = accountService.showOne(number);
				if(account.isPresent()) {
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("application/json");
					mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss"));
					mapper.writeValue(resp.getOutputStream(), account.get());
				} else {
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					resp.setContentType("application/json");
					mapper.writeValue(resp.getOutputStream(), "Nothing found");
				}
			} else {
				List<Account> accounts = accountService.showAll();
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setContentType("application/json");
				mapper.writeValue(resp.getOutputStream(), accounts);
			}
		} catch(ApplicationException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String client = mapper.readValue(req.getInputStream(), String.class);
		Account account = new Account();
		account.setClient(client);
		try(ServiceFactory factory = new ServiceFactory()) {
			AccountService accountService = factory.getAccountService();
			accountService.create(account);
			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.setContentType("application/json");
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.writeValue(resp.getWriter(), account);
		} catch(ApplicationException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Account account = mapper.readValue(req.getInputStream(), Account.class);
		try(ServiceFactory factory = new ServiceFactory()) {
			AccountService accountService = factory.getAccountService();
			if(accountService.update(account)) {
				resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch(ApplicationException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		try(ServiceFactory factory = new ServiceFactory()) {
			AccountService accountService = factory.getAccountService();
			String number = req.getParameter("number");
			if(number != null) {
				accountService.delete(number);
				resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.setContentType("application/json");
				mapper.writeValue(resp.getOutputStream(), "Parameter id is required");
			}
		} catch(NotActiveException | NonZeroBalanceException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.setContentType("application/json");
			mapper.writeValue(resp.getOutputStream(), e.getMessage());
		} catch(NotFoundException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.setContentType("application/json");
			mapper.writeValue(resp.getOutputStream(), e.getMessage());
		} catch(ApplicationException e) {
			throw new ServletException(e);
		}
	}
}
