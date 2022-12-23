package com.dtnsbike.rest.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dtnsbike.dao.interfaces.ContactsDAO;
import com.dtnsbike.dao.service.AccountsService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Contacts;
import com.dtnsbike.model.ContactModel;
import com.dtnsbike.model.UpdateProfileForm;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@CrossOrigin("*")
@RestController
public class UpdateProfileRestController {

	@Autowired
	AccountsService account;

	@Autowired
	ContactsDAO contactDAO;

	@Autowired
	SessionService session;

	@SuppressWarnings("deprecation")
	@GetMapping("/rest/update_profile/account")
	public ResponseEntity<UpdateProfileForm> getOne() {
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		UpdateProfileForm form = new UpdateProfileForm();
		String fullname = acc.getLastname() + " " + acc.getMiddlename() + " " + acc.getFirstname();
		form.setUsername(acc.getUsername());
		form.setFullname(fullname);
		form.setLastname(acc.getLastname());
		form.setMiddlename(acc.getMiddlename());
		form.setFirstname(acc.getFirstname());
		form.setEmail(acc.getEmail());
		form.setPhone(acc.getPhone());
		form.setGender(acc.getGender());
		form.setMonth(acc.getBirthday().getMonth() + "");
		form.setYear(acc.getBirthday().getYear() + 1900 + "");
		form.setDay(acc.getBirthday().getDate() + "");
		form.setPhoto(acc.getPhoto());
		return ResponseEntity.ok(form);
	}

	@SuppressWarnings("deprecation")
	@PostMapping("/rest/update_profile/account")
	public ResponseEntity<UpdateProfileForm> PostOne(@RequestBody UpdateProfileForm form) throws ParseException {
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		List<Accounts> list = account.findAll();
		String birthPost = form.getYear() + "/" + (Integer.parseInt(form.getMonth()) + 1) + "/" + form.getDay();
		Date birthDay = new Date(birthPost);
		String gender = form.getGender();
		if (form.getLastname()==null || form.getFirstname()==null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getEmail().equals(form.getEmail()) && !list.get(i).getEmail().equals(acc.getEmail())) {
				form.setMessage("Email đã trùng! Vui lòng đổi 1 email khác");
				return ResponseEntity.status(HttpStatus.FOUND).body(form);
			}
			if (list.get(i).getPhone().equals(form.getPhone()) && !list.get(i).getPhone().equals(acc.getPhone())) {
				form.setMessage("Số điện thoại đã trùng! Vui lòng nhập số điện thoại khác");
				return ResponseEntity.status(HttpStatus.FOUND).body(form);
			}
		}
		String regNum = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
		String regEmail = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		if (!form.getPhone().matches(regNum)) {
			form.setMessage("Số điện thoại không đúng định dạng");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		if (!form.getEmail().matches(regEmail)) {
			form.setMessage("Email không đúng định dạng");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		LocalDate curDate = LocalDate.now();
		LocalDate yearO = birthDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (yearO.isAfter(curDate)) {
			form.setMessage("Sinh nhật không hợp lệ hay bạn đến từ tương lai");
			return ResponseEntity.status(HttpStatus.FOUND).body(form);
		} else if (Period.between(yearO, curDate).getYears() < 18) {
			form.setMessage("Waooo!!! Em chưa 18?");
			return ResponseEntity.status(HttpStatus.FOUND).body(form);
		}
		acc.setLastname(form.getLastname());
		acc.setMiddlename(form.getMiddlename());
		acc.setFirstname(form.getFirstname());
		acc.setEmail(form.getEmail());
		if (form.getPhoto() == null) {
			acc.setPhoto("");
		} else {
			acc.setPhoto(form.getPhoto());
		}
		acc.setPhone(form.getPhone());
		if (gender.equals("1") || gender.equals("2")) {
			acc.setGender(gender);
		} else {
			acc.setGender("3");
		}
		acc.setBirthday(birthDay);
		account.update(acc);
		return ResponseEntity.ok(form);
	}

	@GetMapping("/rest/update_profile/address")
	public ResponseEntity<List<Contacts>> getAddress() {
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		List<Contacts> list = contactDAO.findAllByUserAr(acc.getUsername());
		for (int i = 0; i < list.size(); i++) {
			list.get(i).getUserAr().setPassword(null);
		}
		return ResponseEntity.ok(list);
	}

	@PostMapping("/rest/update_profile/address")
	public ResponseEntity<ContactModel> PostOne(@RequestBody ContactModel form) throws ParseException {
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		String regNum = "^(0)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
		if (!form.getPhone().matches(regNum)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		Contacts contacts = new Contacts();
		contacts.setUserAr(acc);
		contacts.setAddress(form.getAdr());
		contacts.setFullname(form.getFullname());
		contacts.setPhone(form.getPhone());
		contactDAO.save(contacts);

		return ResponseEntity.ok(form);
	}

	@SuppressWarnings("unused")
	@PutMapping("/rest/update_profile/address")
	public ResponseEntity<ContactModel> PutOne(@RequestBody ContactModel form, @RequestParam("id") String id)
			throws ParseException {
		Accounts acc = session.get("account");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		if (acc == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		String regNum = "^(0)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
		if (!form.getPhone().matches(regNum)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		if (!NumberUtils.isParsable(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Contacts> contacts = contactDAO.findContactIdByUserAr(Integer.parseInt(id), acc.getUsername());
		if (!contacts.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Contacts updateC = contacts.get();
		updateC.setUserAr(acc);
		updateC.setAddress(form.getAdr());
		updateC.setFullname(form.getFullname());
		updateC.setPhone(form.getPhone());
		contactDAO.save(updateC);
		return ResponseEntity.ok(form);
	}

	@DeleteMapping("/rest/update_profile/deleteAddress/{id}")
	public ResponseEntity<JsonNode> deleteCartList(@PathVariable("id") String id) {
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.status(404).build();
		}
		List<Contacts> listAdr = contactDAO.findAllByUserAr(acc.getUsername());
		if (listAdr.size() <= 1) {
			return ResponseEntity.status(404).build();
		}
		if (!NumberUtils.isParsable(id)) {
			return ResponseEntity.status(404).build();
		}
		Optional<Contacts> contacts = contactDAO.findById(Integer.parseInt(id));
		if (!contacts.isPresent()) {
			return ResponseEntity.status(404).build();
		} else if (!contacts.get().getUserAr().getUsername().equals(acc.getUsername())) {
			return ResponseEntity.status(404).build();
		} else {
			contactDAO.delete(contacts.get());
		}
		return ResponseEntity.noContent().build();
	}

}
