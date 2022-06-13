package com.api.config.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErroCadastro {
	
	private String campo;
	private String erroo;
	
}
