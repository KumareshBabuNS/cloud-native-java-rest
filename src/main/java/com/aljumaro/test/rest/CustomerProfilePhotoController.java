package com.aljumaro.test.rest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aljumaro.test.repository.Customer;
import com.aljumaro.test.service.CustomerService;
import com.aljumaro.test.service.exception.CustomerNotFoundException;

@RestController
@RequestMapping("/v1/customer/{id}/photo")
public class CustomerProfilePhotoController {

	private final Log log = LogFactory.getLog(getClass());

	private File photo;
	private CustomerService customerService;

	public CustomerProfilePhotoController(@Value("${upload.dir:${user.home}/images}") String uploadDir,
			CustomerService customerService) {
		super();
		this.photo = new File(uploadDir);
		this.customerService = customerService;
	}

	@GetMapping
	ResponseEntity<Resource> read(@PathVariable int id) {
		return this.customerService.findById(id).map(customer -> {

			Resource fileSystemResource = new ClassPathResource("/images/ignatius.jpg");

			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(fileSystemResource);
		}).orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT })
	Callable<ResponseEntity<?>> write(@PathVariable int id, @RequestParam MultipartFile file) throws Exception {

		log.info(String.format("upload-start /customers/%s/photo (%s bytes)", id, file.getSize()));

		return () -> this.customerService.findById(id).map(customer -> {
			try {
				File fileForCustomer = fileFor(customer);
				InputStream in = file.getInputStream();
				OutputStream out = new FileOutputStream(fileForCustomer);
				FileCopyUtils.copy(in, out);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			URI location = fromCurrentRequest().buildAndExpand(id).toUri();
			log.info(String.format("upload-finish /customers/%s/photo (%s)", id, location));
			return ResponseEntity.created(location).build();
		}).orElseThrow(() -> new CustomerNotFoundException(id));
	}

	private File fileFor(Customer customer) throws IOException {
		return File.createTempFile("TempFile_" + customer.getId(), ".jpg");
	}
}
