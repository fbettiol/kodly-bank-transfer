package com.dws.challenge.web;

import com.dws.challenge.domain.Transfer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
public class TransfersController {

	@PostMapping()
	public ResponseEntity<Object> transferBalance(@RequestBody @Valid Transfer transferDetails) {
		log.info("Transferring {} Euros from account {} to account account {}", transferDetails.getAmount(),
				transferDetails.getSourceAccount(), transferDetails.getTargetAccount());
		return ResponseEntity.ok().body(null);
	}

}
