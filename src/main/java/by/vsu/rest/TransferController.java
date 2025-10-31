package by.vsu.rest;

import by.vsu.di.ServiceFactory;
import by.vsu.domain.Transfer;
import by.vsu.exception.ApplicationException;
import by.vsu.exception.InsufficientFundsException;
import by.vsu.exception.NotActiveException;
import by.vsu.exception.NotFoundException;
import by.vsu.model.service.TransferService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/transfer")
public class TransferController extends HttpServlet {
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			ServiceFactory.init();
		} catch(ClassNotFoundException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		TransferDto transferDto = mapper.readValue(req.getInputStream(), TransferDto.class);
		try(ServiceFactory factory = new ServiceFactory()) {
			TransferService transferService = factory.getTransferService();
			Transfer transfer = transferService.transfer(transferDto.getSenderAccountNumber(), transferDto.getReceiverAccountNumber(), transferDto.getAmount());
			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.setContentType("application/json");
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			mapper.writeValue(resp.getOutputStream(), transfer);
		} catch(NotFoundException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.setContentType("application/json");
			mapper.writeValue(resp.getOutputStream(), e.getMessage());
		} catch(NotActiveException | InsufficientFundsException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.setContentType("application/json");
			mapper.writeValue(resp.getOutputStream(), e.getMessage());
		} catch(ApplicationException e) {
			throw new ServletException(e);
		}
	}

	private static class TransferDto {
		private String senderAccountNumber;
		private String receiverAccountNumber;
		private Long amount;

		public String getSenderAccountNumber() {
			return senderAccountNumber;
		}

		public void setSenderAccountNumber(String senderAccountNumber) {
			this.senderAccountNumber = senderAccountNumber;
		}

		public String getReceiverAccountNumber() {
			return receiverAccountNumber;
		}

		public void setReceiverAccountNumber(String receiverAccountNumber) {
			this.receiverAccountNumber = receiverAccountNumber;
		}

		public Long getAmount() {
			return amount;
		}

		public void setAmount(Long amount) {
			this.amount = amount;
		}
	}
}
