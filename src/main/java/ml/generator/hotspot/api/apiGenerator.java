package ml.generator.hotspot.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ml.generator.hotspot.helper.CodeQrHelper;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/generator")
public class apiGenerator {

	@GetMapping(value = "/qr-code/{qrCode}/{temp}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> genererEtiquetteProduitByCodeBarre(@PathVariable(value = "qrCode") String qrCode,@PathVariable(value = "temp") String temp) throws Exception {

		byte[] data = CodeQrHelper.generateBatchQrCode(qrCode,temp);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(data.length);
		headers.setContentType(MediaType.APPLICATION_PDF);
		return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
	}

}
