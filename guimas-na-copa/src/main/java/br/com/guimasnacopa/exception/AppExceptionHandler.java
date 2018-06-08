package br.com.guimasnacopa.exception;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.guimasnacopa.messages.AppMessages;

@ControllerAdvice
public class AppExceptionHandler {
	
	@Autowired
	AppMessages appMessages;
	
	@ExceptionHandler(value = Exception.class)
    public String generalExceptionHandler(Exception e, Model model){
 		appMessages.getErrorList().add("Que é isso companheiro? O Programador comenteu uma falta durísima, "
				+ "e agora esta operação está impossibilitada de seguir jogando :( "
				+ "" + e.getMessage());
		e.printStackTrace();
		model.addAttribute(appMessages);
        return "pages/login";
    }
	
	@ExceptionHandler(value = AppException.class)
    public String appErrorHandler(AppException e, Model model){
		appMessages.getErrorList().add("Que é isso companheiro? O Programador comenteu uma falta durísima, "
				+ "e agora esta operação está impossibilitada de seguir jogando :( "
				+ "" + e.getMessage());
		model.addAttribute(appMessages);
        return null;
    }
	
	@ExceptionHandler(value = LoginException.class)
    public String loginErrorHandler(LoginException e, Model model){
		model.addAttribute("erro",e);
		appMessages.getErrorList().add(e.getMessage());
		model.addAttribute(appMessages);
        return "pages/login";
    }
	
	@ExceptionHandler(value = ValidacaoException.class)
    public String validacaoHandler(ValidacaoException e, Model model){
		model.addAttribute("erro",e);
		appMessages.getErrorList().addAll(e.msgs );
		model.addAttribute(appMessages);
        return "pages/singup";
    }
}
